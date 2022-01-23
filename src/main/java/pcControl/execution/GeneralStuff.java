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
		try {
			path = Paths.get(new File(References.configsDir, "mainconfig.yml").getCanonicalPath());
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
}