package pcControl.execution;

import java.io.File;
import java.net.URISyntaxException;

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
			e.printStackTrace();
		}
	}
	
	public static void reloadFiles() {
		//System.out.println("Reload files");
		Permissions.copyHelpFile();
		Permissions.copyMainConfigYamlFile();
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
}