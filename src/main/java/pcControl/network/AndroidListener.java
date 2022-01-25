package pcControl.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.apache.logging.log4j.Logger;

import pcControl.PcControlMain;
import pcControl.data.References;
import pcControl.execution.GeneralStuff;
import pcControl.execution.Permissions;
import pcControl.execution.RunningProcesses;
import pcControl.logging.GeneralLogger;

public class AndroidListener implements Runnable {
	private static Logger log = References.log4j;
	
	private static volatile AndroidListener INSTANCE;
	
	public static AndroidListener getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new AndroidListener();
		}
		return INSTANCE;
	}

	private boolean firstRun = true;
	
	@Override
	public void run() {
		try {
			//ServerSocket serverSocket = new ServerSocket(References.getInstance().portServerSide);
			try {
				References.ArServerSocket = new ServerSocket(References.ArSocketPort);
			}
			catch (BindException e) {
				log.info("THE PORT HAS ALREADY BEED TAKEN, PLEASE CLOSE OTHER INSTANSES OR OTHER APPS USING THIS PORT!");
				PcControlMain.doExit();
			}
			//can be a loop here
			while (true) {
				if(!firstRun) {
					log.info("App disconnected");
					References.connected = false;
					GeneralStuff.cleanDisconnectedUserChanges();
				}
				else {
					firstRun = false;
				}
				if(References.STOPPING) {
					return;
				}
				References.ArSocket = References.ArServerSocket.accept();
				log.info("App connected");
				References.connected = true;
				GeneralStuff.reloadFiles();
				References.lastArInSocketActivity = Calendar.getInstance().getTimeInMillis();
				References.arLocation = new File("C:\\"); //config
				References.currentSocketVerified = false;
				//SocketClient sender = PcControlMain.getInstance().ArSender;
				ArSocketActivityKeeper keeper = new ArSocketActivityKeeper();
				keeper.init();
				Thread threadActivityKeeper = new Thread(keeper);
				threadActivityKeeper.start();
				//log.info(References.ArSocket + "\n");
	//			References.ArServerSocket.close();
				References.ArOutSocket = new PrintWriter(References.ArSocket.getOutputStream(), true);
				References.ArInSocket = new BufferedReader(new InputStreamReader(References.ArSocket.getInputStream(), "windows-1251"));
				
	//			Main.getInstance().ArSender.startConnection("128.72.175.213", 12345);
				References.sender.startConnection(References.ArInSocket, References.ArOutSocket);
				
				String inputLine = "";
				int times = 1;
				while (inputLine!=null && !References.ArSocket.isClosed() && References.ArSocket.isConnected()
						&& !References.ArSocket.isInputShutdown() && !References.ArSocket.isOutputShutdown()
						&& References.ArInSocket!=null && References.ArOutSocket!=null) {
					//log.info(References.ArSocket.isClosed());
					try {
						inputLine = References.ArInSocket.readLine();
					}
					catch(SocketException e) {
						References.ArSocket.close();
						log.info("SocketException, Socket was closed");
						break;
					}
					/*if(inputLine!=null && !inputLine.equals("$heartbeat.check")) {
						log.info(inputLine);
					}*/
					//log.info("ar");
					//if(inputLine != null && inputLine != ""){
					if(inputLine!=null && !inputLine.equals("")) {
						References.lastArInSocketActivity = Calendar.getInstance().getTimeInMillis();
	//					if (inputLine.equalsIgnoreCase("servermanager stop serversocket")) {
	//						References.getInstance().outSocket.println("Socket Closed");
	//						break;
	//					}
	//					References.outSocket.println("message back to PX");                                                //useful
						
						/*if(inputLine!=null && !inputLine.equals("$heartbeat.check")) {
							log.info("From android: " + inputLine);
						}*/
						if(inputLine.startsWith("$")) {
							String[] args = inputLine.substring(1).split("\\.");
							int len = args.length;
							//log.info(len);
							if(len>0) {
								if(args[0].equals("auth")) { // IS THERE A WAY TO AVOID SENDING THIS?
									if(len>1) {
										if(args[1].equals("request")) {
											if(References.currentSocketVerified==false) {
												if(len>2) {
													if(args[2].startsWith("password=")) {
														if(inputLine.substring(23).equals(References.password)) {
															References.currentSocketVerified = true;
															References.sender.sendMessage("$auth.result.accepted");
														}
														else {
															References.currentSocketVerified = false;
															References.sender.sendMessage("$auth.result.denied");
															try {
																Thread.currentThread().sleep(100);
															} catch (InterruptedException e) {
																e.printStackTrace();
															}
															References.ArSocket.close();
														}
													}
												}
											}
											else {
												References.sender.sendMessage("$auth.alreadyConnected");
											}
										}
									}
								}
								else if(args[0].equals("system")) {
									//log.info("system...");
									if(len>1) {
										if(args[1].equals("getinfo")) {
											if(len>2) {
												if(args[2].equals("tasklist")) {
													if(len>3) {
														if(args[3].equals("request")) {
																log.info("Sending the task list");
																References.sender.sendMessage("$system.getinfo.tasklist.result.text=" + printTaskList());
														}
													}
												}												
											}
										}
										else if(args[1].equals("management")) {
											if(len>2) {
												if(args[2].equals("shutdown")) {
													if(len>3) {
														if(args[3].equals("usual")) {
															if(len>4) {
																if(args[4].equals("request")) {
																	log.info("Shutting down the pc...");
																	References.sender.sendMessage("$system.management.shutdown.usual.accepted");
																	//ProcessBuilder pb = new ProcessBuilder("shutdown /t 0 /s");
																	//pb.start();
																	
																	//Runtime.getRuntime().exec("shutdown /t 0 /s"); // UNCOMMENT LATER
																	
																}
															}
														}
													}
												}												
											}
										}
										else if(args[1].equals("execution")) {
											//log.info("execution");
											if(len>2) {
												if(args[2].startsWith("input=")) {
													RunningProcesses.sendInput(inputLine.substring(24));
													log.info("Sending input to subprocess: \"" + inputLine.substring(24) + "\"");
												}
												if(args[2].equalsIgnoreCase("stop")) {
													if(len>3) {
														if(args[3].equalsIgnoreCase("request")) {
															try {
																RunningProcesses.stopProcess();
															} catch (ClassNotFoundException e) {
																e.printStackTrace();
															}
														}
													}
												}
											}
										}
										else if(args[1].equals("files")) {
											if(len>2) {
												//System.out.println(args[2]);
												if(args[2].equals("getpathseparator")) {
													if(len>3) {
														if(args[3].equals("request")) {
															String separator = FileSystems.getDefault().getSeparator();
															if(References.printFileDataSendingMessage) {
																log.info("Sending path separator = " + separator);
															}
															References.sender.sendMessage("$system.files.getpathseparator.result.pathseparator=" + separator);
														}
													}
												}
												if(args[2].equals("getlocation")) {
													if(len>3) {
														if(args[3].equals("request")) {
															if(References.printFileDataSendingMessage) {
																log.info("Sending location...");
															}
															References.sender.sendMessage("$system.files.getlocation.result.location=" + References.arLocation.getCanonicalPath());
														}
													}
												}
												else if(args[2].equals("fileslist")) {
													if (len > 3) {
														if (args[3].equals("request")) {
															boolean silent = false;
															if (len > 4) {
																if (args[4].equals("silent=true")) {
																	silent = true;
																}
															}
															sendFilesList(silent);
														}
													}
												}
												else if(args[2].equals("folderslist")) {
													if(len>3) {
														if(args[3].equals("request")) {
															boolean silent = false;
															if(len>4) {
																if(args[4].equals("silent=true")) {
																	silent = true;
																}
															}
															sendFoldersList(silent);
														}
													}
												}
												else if(args[2].equals("nonfolderslist")) {
													if(len>3) {
														if(args[3].equals("request")) {
															boolean silent = false;
															if(len>4) {
																if(args[4].equals("silent=true")) {
																	silent = true;
																}
															}
															sendNonFoldersList(silent);
														}
													}
												}
												else if(args[2].equals("changelocation")) {
													if(len>3) {
														//System.out.println("Requested path: " + inputLine.substring(41));
														if(args[3].equals("request")) {
															if(len>4) {
																boolean success = false;
																if(args[4].startsWith("new=")) {
																	String text = inputLine.substring(41);
																	if(!text.endsWith("/") && !text.endsWith("\\")) {
																		text += "/";
																	}
																	log.info("Requested path: " + text);
																	File f = new File(text);
																	
																	if(f.exists()) {
																		if(Permissions.hasFolderAccess(f.getCanonicalPath(), References.foldersAllowedToSee)) {
																			References.sender.sendMessage("$system.files.changelocation.result.accepted.path=" + f.getCanonicalPath());
																			References.arLocation = f;
																			success = true;
																		}
																	}
																	else {
																		if(!References.arLocation.getCanonicalPath().endsWith("/") && !References.arLocation.getCanonicalPath().endsWith("\\")) {
																			References.arLocation = new File(References.arLocation, "/");
																		}
																		File fullF = new File(References.arLocation + text);
																		if(fullF.exists()) {
																			try {
																				References.sender.sendMessage("$system.files.changelocation.result.accepted.path=" + fullF.getCanonicalPath());
																				References.arLocation = fullF;
																			} catch (IOException e) {
																				e.printStackTrace();
																			}
																			success = true;
																		}
																	}
																}
																if(args[4].equalsIgnoreCase("up")) {
																	File newFile = References.arLocation.getParentFile();
																	if(newFile!=null) {
																		if(Permissions.hasFolderAccess(newFile.getCanonicalPath(), References.foldersAllowedToSee)) {
																			References.sender.sendMessage("$system.files.changelocation.result.accepted.path=" + newFile.getCanonicalPath());
																			success = true;
																			References.arLocation = newFile;
																			sendFilesList(true);
																			sendFoldersList(true);
																			sendNonFoldersList(true);
																			log.info("Did \"cd ..\"");
																		}
																	}
																	else {
																		References.sender.sendMessage("$servermessage.text=You can't use this in the main directory!");
																	}
																}
																
																if(!success) {
																	References.sender.sendMessage("$system.files.changelocation.result.denied.old=" + References.arLocation.getCanonicalPath());
																}
															}
														}
													}
												}
												else if(args[2].equals("executefile")) {
													if(len>3) {
														if(args[3].equals("request")) {
															if(len>4) {
																if(args[4].startsWith("file=")) {
																	String file = inputLine.substring(39);
																	log.info("Request to execute file \"" + file + "\"");
																	RunningProcesses.checkExetutionPerms(References.sender, file);
																}
															}
														}
													}
												}
											}
										}
									}
								}
								else if(args[0].equals("rsccommand")) {
									if(len>1) {
										if(args[1].equals("normal")) {
											if(len>2) {
												if(args[2].startsWith("text=")) {
													String text = inputLine.substring(24);
													log.info("A command from Android to RSC: " + text);
													//if(text.startsWith("SOME_COMMAND")) {}
													References.sender.sendMessage("Unknown command!\n");
												}
											}
										}
									}
								}
								else if(args[0].equals("rscmessage")) { //special message like /say
									if(len>1) {
										if(args[1].equals("normal")) {
											if(len>2) {
												if(args[2].startsWith("text=")) {
													String text = inputLine.substring(24);
													log.info("A message from Android to RSC: " + text);
												}
											}
										}
									}
								}
								else if(args[0].equals("mcmessage")) {
									if(len>1) {
										if(args[1].equals("normal")) {
											if(len>2) {
												if(args[2].startsWith("text=")) {
													String text = inputLine.substring(23);
													log.info("MC message from Android to server: " + text);
												}
											}
										}
									}
								}
								else if(args[0].equals("mccommand")) {
									if(len>1) {
										if(args[1].equals("normal")) {
											if(len>2) {
												if(args[2].startsWith("text=")) {
													String text = inputLine.substring(23);
													log.info("MC Command from Android to server: " + text);
												}
											}
										}
									}
								}
								else if(args[0].equals("heartbeat")) {
									if(len>1) {
										if(args[1].equals("check")) {
											//ignore
											References.lastArInSocketActivity = Calendar.getInstance().getTimeInMillis();
											if(References.printHeartbeats) {
												log.info("Recieved HeartBeatInfo: " + inputLine);
											}
										}
									}
								}
								else {
									References.sender.sendMessage("$rsccommand.unknown");
								}
							}
							else {
								References.sender.sendMessage("$rcsmessage.error.general");
							}
						}
						else {
							/*if(true) {
								sender.sendMessage("$rcsmessage.unknown");
							}*/
						}
						//String s = "to android (" + times + " call); " + inputLine;
//						for (int i = 0; i < 20; i++) {
//							s += "\n";
//						}
						//s+="1237689";
						//Main.getInstance().ArSender.sendMessage(s);
//						Main.getInstance().ArSender.sendMessage(s);
//						Main.getInstance().ArSender.sendMessage(s);
//						Main.getInstance().ArSender.sendMessage(s);
//						Main.getInstance().ArSender.sendMessage(s);
//						Main.getInstance().ArSender.sendMessage(s);
						
						times++;
					}
	//				if(References.ArSocket.isClosed()) {
	//					log.info("closed");
	//					continue;
	//				}
				}
				//log.info("new");
				keeper.stop();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String printTaskList() {
		try {
			Runtime runtime = Runtime.getRuntime();
			Process proc = runtime.exec("tasklist -fo csv /nh");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = "";
			ArrayList<String> used = new ArrayList<>();
			String lastMain = "";
			while ((line = stdInput.readLine()) != null) {
				boolean repeating = false;
				String[] oldLine = line.split("\"");
				int position = 1;
				for (String column : oldLine) {
					if (!column.equals(",") && !column.equals("")) {
						if (position == 1) { // <4 for memory and full name
							lastMain = column;
							if(used.contains(column)) {
								repeating = true;
							}
							else {
								used.add(column);
							}
						}
						if (position == 3 && !repeating) {
							
						}
						position++;
						if (position > 5) {
							position = 1;
						}
					}
				}
			}
			
			Collections.sort(used);
			
			String result = "";
			
			int j = 0;
			for (String s : used) {
				if(!s.startsWith("0")) {
					result += s;
				}
				j++;
				if(j<used.size()) {
					if(!s.startsWith("0")) {
						result += "\n";
					}
					//System.out.println(result.replaceAll("&l&ine&", " "));
				}
			}
			
			return result.replaceAll("\n", "&l&ine&");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void sendFilesList(boolean silent) {
		if((References.printFileDataSendingMessage && !silent) || (References.printFileDataSendingSilent && silent)) {
			log.info("Sending files list...");
		}
		String text = "";
		File[] files = References.arLocation.listFiles();
		for (int i = 0; i < files.length; i++) {
			try {
				if(Permissions.hasFolderAccess(files[i].getCanonicalPath(), References.foldersAllowedToSee)) {
					text += files[i].getName() + "&&nex&t&";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (silent) {
			References.sender.sendMessage("$system.files.fileslist.silentresult.list=" + text);
		} else {
			References.sender.sendMessage("$system.files.fileslist.result.list=" + text);
		}
	}
	
	private void sendFoldersList(boolean silent) {
		if((References.printFileDataSendingMessage && !silent) || (References.printFileDataSendingSilent && silent)) {
			log.info("Sending folders only list...");
		}
		File[] files = References.arLocation.listFiles();
		String text = "";
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				try {
					if(Permissions.hasFolderAccess(files[i].getCanonicalPath(), References.foldersAllowedToSee)) {
						text += files[i].getName() + "&&nex&t&";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (silent) {
			References.sender.sendMessage("$system.files.folderslist.silentresult.list=" + text);
		} else {
			References.sender.sendMessage("$system.files.folderslist.result.list=" + text);
		}
	}
	
	private void sendNonFoldersList(boolean silent) {
		if((References.printFileDataSendingMessage && !silent) || (References.printFileDataSendingSilent && silent)) {
			log.info("Sending non-folders only list...");
		}
		File[] files = References.arLocation.listFiles();
		String text = "";
		for (int i = 0; i < files.length; i++) {
			if(!files[i].isDirectory()) {
				try {
					if(Permissions.hasFolderAccess(files[i].getCanonicalPath(), References.foldersAllowedToSee)) {
						text += files[i].getName() + "&&nex&t&";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println("");
		if (silent) {
			References.sender.sendMessage("$system.files.nonfolderslist.silentresult.list=" + text);
		} else {
			References.sender.sendMessage("$system.files.nonfolderslist.result.list=" + text);
		}
	}
}
