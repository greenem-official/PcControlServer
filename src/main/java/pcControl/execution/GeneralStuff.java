package pcControl.execution;

import java.io.File;
import java.net.URISyntaxException;

import pcControl.PcControlMain;
import pcControl.data.References;
import pcControl.logging.GeneralLogger;

public class GeneralStuff {
	public static void cleanDisconnectedUserChanges() { //add more actions here
		RunningProcesses.stopProcess();
	}
	
	public static void reloadFiles() {
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