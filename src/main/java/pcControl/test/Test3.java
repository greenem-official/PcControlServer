package pcControl.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import pcControl.execution.GeneralStuff;

public class Test3 {
	public static void main(String[] args) {
//		File f = new File("c:\\\\adobe\\\\/After effects");
//		System.out.println(f.exists());
//		String d = "C:\\\\adobe\\1";
		//System.out.println(s.substring(0,s.length()-5));
		//System.out.println(s.split("/").length);
		//System.out.println(s.replace("\\", "//"));
		//String s = d.replaceAll("\\\\+", "/").substring(0, d.length() - (d.split("/")[(d.split("/")).length - 1]).length());
//		System.out.println(d.replaceAll("\\\\+", "/")); //[\\\\]
		//File f = new File("C:\\");
		//System.out.println(f.getParent());
//		String old = "C:\\";
//		String text = "C:\\adobe";
//		GeneralLogger.log("Requested path: " + text);
//		File f = new File(text);
//		
//		boolean success = false;
//		
//		if(f.exists()) {
//			System.out.println("$system.files.changelocation.result.accepted.path=" + text);
//			success = true;
//		}
//		else {
//			if(!old.endsWith("/") && !old.endsWith("\\")) {
//				old = old + "/";
//			}
//			File fullF = new File(old + text);
//			if(fullF.exists()) {
//				try {
//					System.out.println("$system.files.changelocation.result.accepted.path=" + fullF.getCanonicalPath());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				success = true;
//			}
//		}
//		if(!success) {
//			System.out.println("$system.files.changelocation.result.denied.old=" + References.arLocation);	
//		}
		
//		String s = "c:\\";
//		System.out.println(new File(s).exists());
		
//		File f = new File("G:\\EclipceWorkspaces\\Win10\\Main\\Builds\\PcControl\\logs");
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("DD-MM-YYYY");  
//		LocalDateTime now = LocalDateTime.now();  
//		f.renameTo(new File("before_" + dtf.format(now) + ".log"));
		
		//System.out.println(new File("C:\\").canRead());
		//File f = new File("G:\\EclipceWorkspaces\\Win10\\Main\\Builds\\PcControl");
		
		/*File f = new File("C:\\$AV_AVG");
		System.out.println("1:" + f.canRead());
		System.out.println("2:" + f.canWrite());
		System.out.println("3:" + (f.getTotalSpace()/1024/1024/1024));
		System.out.println("4:" + f.getFreeSpace()/1024/1024/1024);
		System.out.println("5:" + f.getUsableSpace()/1024/1024/1024);
		System.out.println("6:" + f.isHidden());
		System.out.println("7:" +  f.lastModified());
		System.out.println("8:" + f.getName());
		System.out.println("9:" + f.isFile());
		
//		DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("u-M-d hh:mm:ss a O");
		Date date = new Date(f.lastModified());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
		String result = "";
		result += "Name: " + f.getName() + "\n";
		result += "Full path: " + f.getPath() + "\n";
		result += "Last change: " + dateFormat.format(date) + "\n";
		result += "Size: " + GeneralStuff.getFormatedFolderSize(GeneralStuff.getFolderSize(f)) + "\n";
		if(!f.canRead()) {
			result += "Can't be read" + "\n";
		}
		if(!f.canWrite()) {
			result += "Can't be written" + "\n";
		}
		if(f.isHidden()) {
			result += "Hidden directory" + "\n";
		}
        System.out.println(result);*/
        
//		String s = "ssdfsdfdfstr\n";
//		s = s.substring(0, s.length()-1);
//		System.out.println(s + " w");
		
//		String value = "\"javaw -jar " + System.getProperty("user.dir") + "\\myJar.jar\"";
//		WinRegistry.writeStringValue(WinRegistry.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Run", "myJar autorun key", value);
		
//		Registry
		
		Path newLink = Paths.get("C:\\Users\\Ivan\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\" + "link.lnk"); // with .lnk, only for coppying!
		Path target = Paths.get("G:\\EclipceWorkspaces\\Win10\\Main\\Builds\\PcControl\\" + "run.bat");
		
//		createLink(newLink, target);
		copyLink(Paths.get("G:\\EclipceWorkspaces\\Win10\\Main\\Builds\\PcControl\\" + "RunAsAdmin.lnk"), newLink);
//		deleteLink(newLink);
//		System.out.println(System.getProperty("user.home"));
	}
	
	private static void createLink(Path newLink, Path target) {
		try {
		    Files.createSymbolicLink(newLink, target);
		} catch (FileAlreadyExistsException e) {
			// ?
		} catch (FileSystemException e) {
			System.out.println("A problem with deleting the file from autostart. Probably not enough permissions.");
		} catch (IOException x) {
		    System.err.println(x);
		} catch (UnsupportedOperationException x) {
		    // Some file systems do not support symbolic links.
		    System.err.println(x);
		}
	}
	
	private static void deleteLink(Path link) {
//		System.out.println(Files.isSymbolicLink(link));
//		if(Files.isSymbolicLink(link)) {
		link.toFile().delete();
//		}
	}
	
	private static void copyLink(Path from, Path to) {
		try {
			Files.copy(from, to, StandardCopyOption.COPY_ATTRIBUTES);
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
