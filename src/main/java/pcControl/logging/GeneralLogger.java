package pcControl.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import pcControl.data.References;

public class GeneralLogger {
	public static void log(Object s) {
		log(s.toString());
	}

	public static void log(char[] s) {
		log(String.valueOf(s));
	}
	
	public static void log(char s) {
		log(String.valueOf(s));
	}
	
	public static void log(long s) {
		log(String.valueOf(s));
	}
	
	public static void log(double s) {
		log(String.valueOf(s));
	}
	
	public static void log(float s) {
		log(String.valueOf(s));
	}
	
	public static void log(int s) {
		log(String.valueOf(s));
	}
	
	public static void log(boolean s) {
		log(String.valueOf(s));
	}
	
	public static void log(String s) {
		if(References.fixEmptyLines) {
    		s = s.replaceAll("\n\n", "\n");
    	}
		System.out.println("[" + getTime() + "] " + s);
	}
	
	public static String getTime() {
		return getTime("HH:mm:ss");
	}
	
	public static String getTime(String pattern) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);  
		   LocalDateTime now = LocalDateTime.now();  
		   return dtf.format(now);  
	}
}
