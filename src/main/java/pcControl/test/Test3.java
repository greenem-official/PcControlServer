package pcControl.test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Test3 {
	public static void main(String[] args) {
//		File f = new File("c:\\\\adobe\\\\/After effects");
//		System.out.println(f.exists());
		//String d = "C:\\\\adobe\\1";
		//System.out.println(s.substring(0,s.length()-5));
		//System.out.println(s.split("/").length);
		//System.out.println(s.replace("\\", "//"));
		//String s = d.replaceAll("\\\\+", "/").substring(0, d.length() - (d.split("/")[(d.split("/")).length - 1]).length());
		//System.out.println(d.replaceAll("\\\\+", "/")); //[\\\\]
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
		
		String s = "c:\\";
		System.out.println(new File(s).exists());
		
//		File f = new File("G:\\EclipceWorkspaces\\Win10\\Main\\Builds\\PcControl\\logs");
//		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("DD-MM-YYYY");  
//		LocalDateTime now = LocalDateTime.now();  
//		f.renameTo(new File("before_" + dtf.format(now) + ".log"));
	}
}
