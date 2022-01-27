package pcControl.execution;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.Logger;

import pcControl.PcControlMain;
import pcControl.data.References;
import pcControl.logging.GeneralLogger;

public class GeneralStuff {
	private static Logger log = References.log4j;
	public static int sizeRequestId = 0;
	
	public static void cleanDisconnectedUserChanges() { //add more actions here
		try {
			RunningProcesses.stopProcess();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}
		catch (NoClassDefFoundError e) {
			
		}
	}
	
	public static void reloadFiles() {
		//System.out.println("Reload files");
		GeneralStuff.replaceAllBackslashesInConfig();
		Permissions.copyHelpFile();
		Permissions.copyMainConfigYamlFile();
		GeneralStuff.replaceAllBackslashesInConfig(); //turn off if too slow
		Permissions.reloadConfig();
		//References.appExecutionDir = new File(System.getProperty("user.dir"));
	}
	
	public static void setConstants() {
		//References.appExecutionDir = new File(System.getProperty("user.dir"));
//		Yaml yaml = new Yaml();
//		try {
//			yaml.load(References.appExecutionDir.getCanonicalPath() + "test.yaml");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		System.out.println("yaml");
	}
	
	public static void replaceAllBackslashesInConfig() {
		Path path = null;
		File f = new File(References.configsDir, "mainconfig.yml");
		if(!f.exists()) {
			return;
		}
		try {
			path = Paths.get(f.getCanonicalPath());
		} catch (IOException e1) {
			log.error("getCanonicalPath IOException");
			e1.printStackTrace();
		}
		Charset charset = Charset.forName("windows-1251");

		String content = null;
		try {
			content = new String(Files.readAllBytes(path), charset);
		} catch (IOException e) {
			log.error("readAllBytes IOException");
			e.printStackTrace();
		}
		content = content.replace("\\", "/");
		try {
			Files.write(path, content.getBytes(charset));
		} catch (IOException e) {
			log.error("write printStackTrace");
			e.printStackTrace();
		}
//		try {
//			Thread.currentThread().sleep(1000);
//		} catch (InterruptedException e) {
//			log.error("sleep InterruptedException");
//			e.printStackTrace();
//		}
	}
	
	public static String getFormatedFolderSize(long bytes) {
		if(bytes==-1L) {
			return "UNKNOWN!";
		}
		if(bytes<1024) {
			return bytes + " Bytes";
		}
		long kb = bytes/1024;
		if(kb<1024) {
			return kb + " KB";
		}
		long mb = kb/1024;
		if(mb<1024) {
			return mb + " MB";
		}
		long gb = mb/1024;
		if(gb<1024) {
			return gb + " GB";
		}
		long tb = gb/1024;
		if(tb<1024) {
			return tb + " TB";
		}
		long pb = tb/1024;
		if(pb<1024) {
			return pb + " PB";
		}
		long eb = pb/1024;
		if(eb<1024) {
			return eb + " EB";
		}
		long zb = eb/1024;
		if(zb<1024) {
			return zb + " ZB";
		}
		long yb = zb/1024;
		if(yb<1024) {
			return yb + " YB";
		}
		long bb = yb/1024;
		if(bb<1024) {
			return bb + " BB";
		}
		//GeopByte (real?) is also gb, why lol
		return bb/1024 + " GeopBytes";
	}
	
	public static long getFolderSize(File dir) {
		References.folderSizeGotLimit = false;
		References.folderSizeHowManyChecked = 0;
		sizeRequestId++;
		if(sizeRequestId>1000) {
			sizeRequestId = 0;
		}
		return getFolderSize(dir, 1, 0, false, sizeRequestId);
	}
	
	public static long getFolderSize(File dir, int recursionLevel, long previousSize, boolean gotLimit, int reqId) {
		if(reqId!=sizeRequestId) {
			return -4L; // already old request
		}
		if(previousSize < 23687091200L) { // 50gb)
			if(recursionLevel < 20) {
				if(References.folderSizeHowManyChecked<1000) {
				    long size = 0;
				    try {
					    for (File file : dir.listFiles()) {
					        if (file.isFile()) {
					            //System.out.println(file.getName() + " " + file.length());
					            size += file.length();
					        }
					        else {
					        	long subsize = getFolderSize(file, recursionLevel + 1, size, gotLimit, reqId);
					        	if(subsize<0) {
					        		gotLimit = true;
					        		References.folderSizeGotLimit = true;
					        	}
					        	else {
					        		size += subsize;
					        	}
					        	References.folderSizeHowManyChecked+=1;
					        }
					    }
	//				    try {
	//						System.out.println(dir.getCanonicalPath() + " - " + size);
	//					} catch (IOException e) {
	//						e.printStackTrace();
	//					}
					    return size;
				    } catch(NullPointerException e) {
				    	return -1L;
				    }
				} else {
					References.folderSizeGotLimit = true;
					gotLimit = true;
					return -3L;
				}
			} else {
				References.folderSizeGotLimit = true;
				gotLimit = true;
				return -2L;
			}
		} else {
			References.folderSizeGotLimit = true;
			gotLimit = true;
			return -1L;
		}
	}
}