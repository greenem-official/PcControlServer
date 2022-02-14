package pcControl.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.apache.logging.log4j.Logger;

import pcControl.PcControlMain;
import pcControl.data.References;
import pcControl.execution.GeneralStuff;
import pcControl.execution.Permissions;
import pcControl.execution.RunningProcesses;

public class AndroidListener implements Runnable {
	private static Logger log = References.log4j;
	
	public static Thread sizeCalculations = null;
	
	private static volatile AndroidListener INSTANCE;
	
	public static AndroidListener getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new AndroidListener();
		}
		return INSTANCE;
	}
	
	private boolean chekedSockedIsOk = true;

	private boolean firstRun = true;
	private boolean wasDisconnect = false;
	private boolean gotNullOnLastConnect = false;
	
	public static ArrayList<Socket> connections = new ArrayList<>();
	
	@Override
	public void run() {
		try {
			//ServerSocket serverSocket = new ServerSocket(References.getInstance().portServerSide);
			try {
				References.serverSocket = new ServerSocket(References.serverSocketPort);
			}
			catch (BindException e) {
				log.info("THE PORT HAS ALREADY BEED TAKEN, PLEASE CLOSE OTHER INSTANSES OR OTHER APPS USING THIS PORT!");
				PcControlMain.doExit();
			}
			//can be a loop here
			while (true) {
				if(References.printMiscellaneousDebug) {
					log.info("loop start");
				}
				/*if(!firstRun) {
					log.info("App disconnected");
					References.connected = false;
					GeneralStuff.cleanDisconnectedUserChanges();
				}
				else {
					firstRun = false;
				}*/
				
				wasDisconnect = false;
				
				//log.info("1) " + firstRun + " " + chekedSockedIsOk + " " + gotNullOnLastConnect);
				
				
				if(!firstRun) {
					if(chekedSockedIsOk) {
						if(!gotNullOnLastConnect) {
							log.info("App disconnected");
							References.connected = false;
							GeneralStuff.cleanDisconnectedUserChanges();
						}
					}
					wasDisconnect = true;
				}
				else {
					firstRun = false;
				}
				
				chekedSockedIsOk = true;
				gotNullOnLastConnect = false;
				
				if(References.STOPPING) {
					return;
				}
				Socket socket = References.serverSocket.accept();
				
				connections.add(socket);
				
				ConnectionPerDevice newDeviceConnection = new ConnectionPerDevice() {
					@Override
					public void run() {
						try {
							if(printMiscellaneousDebug) {
								log.info("loop middle");
							}
							
							boolean firstRunC = firstRun;
							
							sender = new SocketClient();
							
							//log.info(socket.isClosed());
							//log.info(socket.isConnected());
							
							/*if(!socket.isClosed() && socket.isConnected()) {
								log.info("App connected");
							}
							else {
								continue;
							}*/
							connected = true;
							GeneralStuff.reloadFiles();
							lastArInSocketActivity = Calendar.getInstance().getTimeInMillis();
							arLocation = new File("C:\\"); //config
							currentSocketVerified = false;
							//SocketClient sender = PcControlMain.getInstance().ArSender;
							ArSocketActivityKeeper keeper = new ArSocketActivityKeeper();
							keeper.socket = socket;
							keeper.in = inSocket;
							keeper.out = outSocket;
//							keeper.init(); // now not
							Thread threadActivityKeeper = new Thread(keeper);
							threadActivityKeeper.start();
							//log.info(ArSocket + "\n");
//							ArServerSocket.close();
							OutputStream outputStream = socket.getOutputStream();
							outSocket = new PrintWriter(outputStream, true);
							
							inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream(), "windows-1251"));
							
//							Main.getInstance().ArSender.startConnection("128.72.175.213", 12345);
							sender.startConnection(inSocket, outSocket, outputStream);
							
							String inputLine = "";
							int times = 1;
							boolean chekedSockedIsOk = true;
							while (inputLine!=null && !socket.isClosed() && socket.isConnected()
									&& !socket.isInputShutdown() && !socket.isOutputShutdown()
									&& inSocket!=null && outSocket!=null) {
								//log.info(ArSocket.isClosed());
								try {
									inputLine = inSocket.readLine();
									if(printFirstConnectMessage) {
										log.info("Debugging inputline after connction: " + inputLine);
									}
									//log.info("2) " + chekedSockedIsOk + " " + (inputLine==null) + " " + wasDisconnect + " " + firstRunC + " " + firstRun);
									if(chekedSockedIsOk && inputLine==null) {
										socket.close();
										if(times==1) {
											chekedSockedIsOk = false;
											gotNullOnLastConnect = true;
										}
										break;
									}
									else {
										if(wasDisconnect || firstRunC) {
											log.info("App connected");
											firstRunC = false;
											wasDisconnect = false;
										}
									}
								}
								catch(SocketException e) {
									socket.close();
									if(printSocketException) {
										log.info("SocketException, Socket was closed " + e);
									}
									break;
								}
								/*if(inputLine!=null && !inputLine.equals("$heartbeat.check")) {
									log.info(inputLine);
								}*/
								//log.info("ar");
								//if(inputLine != null && inputLine != ""){
								if(inputLine!=null && !inputLine.equals("")) {
									//inputLine = new String(inputLine.getBytes(Charset.forName("windows-1251")));
									//inputLine = new String(inputLine.getBytes(Charset.forName("Cp1251")));
									//log.debug(inputLine);
									lastArInSocketActivity = Calendar.getInstance().getTimeInMillis();
//									if (inputLine.equalsIgnoreCase("servermanager stop serversocket")) {
//										getInstance().outSocket.println("Socket Closed");
//										break;
//									}
//									outSocket.println("message back to PX");                                                //useful
									
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
														if(currentSocketVerified==false) {
															if(len>2) {
																if(args[2].startsWith("password=")) {
																	if(inputLine.substring(23).equals(References.password)) {
																		currentSocketVerified = true;
																		sender.sendMessage("$auth.result.accepted");
																		//sendLocation();
																	}
																	else {
																		currentSocketVerified = false;
																		sender.sendMessage("$auth.result.denied");
																		try {
																			Thread.currentThread().sleep(100);
																		} catch (InterruptedException e) {
																			e.printStackTrace();
																		}
																		socket.close();
																	}
																}
															}
														}
														else {
															sender.sendMessage("$auth.alreadyConnected");
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
																			sender.sendMessage("$system.getinfo.tasklist.result.text=" + printTaskList());
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
																				sender.sendMessage("$system.management.shutdown.usual.accepted");
																				//ProcessBuilder pb = new ProcessBuilder("shutdown /t 0 /s");
																				//pb.start();
																				
																				if(realShutdown) {
																					Runtime.getRuntime().exec("shutdown /t 0 /s");
																				}
																				
																			}
																		}
																	}
																	else if(args[3].equals("restart")) {
																		if(len>4) {
																			if(args[4].equals("request")) {
																				log.info("Restarting the pc...");
																				sender.sendMessage("$system.management.shutdown.restart.accepted");
																				if(realShutdown) {
																					Runtime.getRuntime().exec("shutdown /t 0 /r"); // or /g?
																				}
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
															//log.debug(args[2]);
															//System.out.println(args[2]);
															if(args[2].equals("getpathseparator")) {
																if(len>3) {
																	if(args[3].equals("request")) {
																		String separator = FileSystems.getDefault().getSeparator();
																		if(printFileDataSendingMessage) {
																			log.info("Sending path separator = " + separator);
																		}
																		sender.sendMessage("$system.files.getpathseparator.result.pathseparator=" + separator);
																	}
																}
															}
															else if(args[2].equals("getlocation")) {
																if(len>3) {
																	if(args[3].equals("request")) {
																		sendLocation();
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
																						sender.sendMessage("$system.files.changelocation.result.accepted.path=" + f.getCanonicalPath());
																						arLocation = f;
																						success = true;
																					}
																				}
																				else {
																					if(!arLocation.getCanonicalPath().endsWith("/") && !arLocation.getCanonicalPath().endsWith("\\")) {
																						arLocation = new File(arLocation, "/");
																					}
																					File fullF = new File(arLocation + text);
																					if(fullF.exists()) {
																						try {
																							sender.sendMessage("$system.files.changelocation.result.accepted.path=" + fullF.getCanonicalPath());
																							arLocation = fullF;
																						} catch (IOException e) {
																							e.printStackTrace();
																						}
																						success = true;
																					}
																				}
																			}
																			if(args[4].equalsIgnoreCase("up")) {
																				File newFile = arLocation.getParentFile();
																				if(newFile!=null) {
																					if(Permissions.hasFolderAccess(newFile.getCanonicalPath(), References.foldersAllowedToSee)) {
																						sender.sendMessage("$system.files.changelocation.result.accepted.path=" + newFile.getCanonicalPath());
																						success = true;
																						arLocation = newFile;
																						sendFilesList(true);
																						sendFoldersList(true);
																						sendNonFoldersList(true);
																						log.info("Did \"cd ..\"");
																					}
																				}
																				else {
																					sender.sendMessage("$servermessage.text=You can't use this in the main directory!");
																				}
																			}
																			
																			if(!success) {
																				sender.sendMessage("$system.files.changelocation.result.denied.old=" + arLocation.getCanonicalPath());
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
																				RunningProcesses.checkExetutionPerms(sender, file);
																			}
																		}
																	}
																}
															}
															else if(args[2].equals("dirinfo")) {
																if(len>3) {
																	if(args[3].equals("request")) {
																		sendDirInfo();
																	}
																	if(args[3].equals("stopCalculatingSize")) {
																		GeneralStuff.sizeRequestId++;
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
																sender.sendMessage("Unknown command!\n");
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
														lastArInSocketActivity = Calendar.getInstance().getTimeInMillis();
														if(printHeartbeats) {
															log.info("Recieved HeartBeatInfo: " + inputLine);
														}
													}
												}
											}
											else {
												sender.sendMessage("$rsccommand.unknown");
											}
										}
										else {
											sender.sendMessage("$rcsmessage.error.general");
										}
									}
									else {
										/*if(true) {
											sender.sendMessage("$rcsmessage.unknown");
										}*/
									}
									//String s = "to android (" + times + " call); " + inputLine;
//									for (int i = 0; i < 20; i++) {
//										s += "\n";
//									}
									//s+="1237689";
									//Main.getInstance().ArSender.sendMessage(s);
//									Main.getInstance().ArSender.sendMessage(s);
//									Main.getInstance().ArSender.sendMessage(s);
//									Main.getInstance().ArSender.sendMessage(s);
//									Main.getInstance().ArSender.sendMessage(s);
//									Main.getInstance().ArSender.sendMessage(s);
									
									
								}
//								if(ArSocket.isClosed()) {
//									log.info("closed");
//									continue;
//								}
								times++;
							}
							
							//log.info("new");
							keeper.stop();
							
							if(printMiscellaneousDebug) {
								log.info("loop end");
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
						File[] files = arLocation.listFiles();
						if(files==null) {
							if(!silent) {
								sender.sendMessage("$servermessage.text=Cannot get list of files in there!");
							}
							return;
						}
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
							sender.sendMessage("$system.files.fileslist.silentresult.list=" + text);
							//log.info(text);
						} else {
							sender.sendMessage("$system.files.fileslist.result.list=" + text);
						}
					}
					
					private void sendFoldersList(boolean silent) {
						if((References.printFileDataSendingMessage && !silent) || (References.printFileDataSendingSilent && silent)) {
							log.info("Sending folders only list...");
						}
						File[] files = arLocation.listFiles();
						String text = "";
						if(files==null) {
							if(!silent) {
								sender.sendMessage("$servermessage.text=Cannot get list of folders in there!");
							}
							return;
						}
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
							sender.sendMessage("$system.files.folderslist.silentresult.list=" + text);
						} else {
							sender.sendMessage("$system.files.folderslist.result.list=" + text);
						}
					}
					
					private void sendNonFoldersList(boolean silent) {
						if((References.printFileDataSendingMessage && !silent) || (References.printFileDataSendingSilent && silent)) {
							log.info("Sending non-folders only list...");
						}
						File[] files = arLocation.listFiles();
						String text = "";
						if(files==null) {
							if(!silent) {
								sender.sendMessage("$servermessage.text=Cannot get list of non-folder files in there!");
							}
							return;
						}
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
							sender.sendMessage("$system.files.nonfolderslist.silentresult.list=" + text);
						} else {
							sender.sendMessage("$system.files.nonfolderslist.result.list=" + text);
						}
					}
					
					public static void sendLocation() {
						if(References.printFileDataSendingMessage) {
							log.info("Sending location...");
						}
						try {
							sender.sendMessage("$system.files.getlocation.result.location=" + arLocation.getCanonicalPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					public static void sendDirInfo() {
						log.info("Sending directory info...");
						File f = arLocation;
						Date date = new Date(f.lastModified());
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
						String result = "";
						String slN = "&l&ine&";
						result += "Name: " + f.getName() + slN;
						result += "Full path: " + f.getPath() + slN;
						result += "Last change: " + dateFormat.format(date) + slN;
						//result += "Size: " + GeneralStuff.getFormatedFolderSize(GeneralStuff.getFolderSize(f)) + slN;
						if(!f.canRead()) {
							result += "Can't be read" + slN;
						}
						if(!f.canWrite()) {
							result += "Can't be written" + slN;
						}
						if(f.isHidden()) {
							result += "Hidden directory" + slN;
						}
						//result += "Calculating folder size..." + slN; //later
						result += slN;
						sender.sendMessage("$system.files.dirinfo.result.info=" + result);
						
						/*sizeCalculations = new Thread(new Runnable() { // later
							@Override
							public void run() {
								int savedId = GeneralStuff.sizeRequestId;
								String size = GeneralStuff.getFormatedFolderSize(GeneralStuff.getFolderSize(f));
								if(savedId!=GeneralStuff.sizeRequestId) {
									return;	
								}
								String s = "$system.files.dirinfo.result.sizeinfo=";
								if(References.folderSizeGotLimit) {
									s+=">";
									References.folderSizeGotLimit = false;
									References.folderSizeHowManyChecked = 0;
								}
								s += size; 
								References.sender.sendMessage(s);
							}
						});
						sizeCalculations.start();*/
						
						//log.info("Sending directory info END");
					}
				};
				
				newDeviceConnection.socket = socket;
				
				new Thread(newDeviceConnection).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
