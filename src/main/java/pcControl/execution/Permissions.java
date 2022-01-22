package pcControl.execution;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

import pcControl.PcControlMain;
import pcControl.data.References;
import pcControl.logging.GeneralLogger;

public class Permissions {
	public static File configsDir = null;
	
	/***
	ONLY AFTER GeneralStuff.setConstants(), here copyHelpFile and copyMainConfigYamlFile
	***/
	
	public static void init() {
		configsDir = new File(References.appExecutionDir, "configs");
		configsDir.mkdirs();
	}
	
	public static void reloadConfig() {
		//System.out.println(configsDir);
		File f = new File(configsDir, "mainconfig.yml");
		f.getParentFile().mkdirs();
		
		Yaml yaml = new Yaml();
		HashMap<String, Object> yamlMap = null;
		try {
			yamlMap = yaml.load(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String currentKey = null;
		for (Map.Entry<String, Object> mainEntry : yamlMap.entrySet()) {
            //System.out.println(mainEntry.toString());
            if(mainEntry.getKey().equals("settings")) {
            	LinkedHashMap<String, Object> settings = (LinkedHashMap<String, Object>) mainEntry.getValue();
	            for (Map.Entry<String, Object> entry : ((HashMap<String, Object>) settings).entrySet()) {
		            currentKey = "folders-allowed-to-see";
		            if(entry.getKey().equals(currentKey)) {
		            	//System.out.println(entry.getValue());
		            	References.foldersAllowedToSee = (ArrayList<String>) entry.getValue();
		            }
		            currentKey = "folders-and-files-allowed-to-use-execution";
		            if(entry.getKey().equals(currentKey)) {
		            	//System.out.println(entry.getValue());
		            	References.foldersAndFilesAllowedToExecute = (ArrayList<String>) entry.getValue();
		            }
	            }
            }
        }
	}
	
	public static void copyHelpFile() {
		/*boolean same = false;
		try {
			if(filesCompareByByte(new FileInputStream(new File(configsDir, "confighelp.txt")), Permissions.class.getResourceAsStream("/confighelp.txt")) == 1L) {
				same = true;
			}
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}*/
		//GeneralLogger.log(same);
		//if(!same) {
		//GeneralLogger.log(configsDir);
			File f = new File(configsDir, "confighelp.txt");
			f.getParentFile().mkdirs();
			//if (!f.exists()) {
				InputStream is = null;
				try {
					//System.out.println(Permissions.class.getResource("/confighelp.txt").toURI());
					is = Permissions.class.getResource("/confighelp.txt").toURI().toURL().openStream(); //Permissions.class.getResourceAsStream
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				OutputStream os = null;
				try {
					os = new FileOutputStream(f);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				byte[] buffer = new byte[4096];
				int length;
				try {
					while ((length = is.read(buffer)) > 0) {
						os.write(buffer, 0, length);
					}
					os.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			//}
		//}
	}
	
	public static void copyMainConfigYamlFile() {
//		try {
//			Yaml old = new Yaml().load(configsDir.getCanonicalPath() + "mainconfig.yml");
//		} catch (IOException e4) {
//			e4.printStackTrace();
//		}
//		Yaml newYaml = new Yaml().load(Permissions.class.getResourceAsStream("/mainconfig.yml"));|
		/*boolean same = false;
		try {
			if(filesCompareByByte(new FileInputStream(new File(configsDir, "mainconfig.yml")), Permissions.class.getResourceAsStream("/mainconfig.yml")) == 1L) {
				same = true;
			}
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}*/
		//if(!same) {
		File f = new File(configsDir, "mainconfig.yml");
		f.getParentFile().mkdirs();
		if (!f.exists()) {
			doCopyOfTheConfig(f);
		}
		else {
			if(References.updateConfigOnNewVersion) {
				Yaml yaml = new Yaml();
				HashMap<String, Object> yamlMap = null;
				try {
					yamlMap = yaml.load(new FileInputStream(f));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				boolean otherversion = false;
				String[] parts = null;
				for (Map.Entry<String, Object> mainEntry : yamlMap.entrySet()) {
		            if(mainEntry.getKey().equals("config-version")) {
		            	String version = (String) mainEntry.getValue();
		            	parts = version.split("-");
		            	if(!parts[0].equals(References.mainVersion) || !parts[1].equals(References.patchVerion)) {
		            		otherversion = true;
		            	}
		            	break;
		            }
				}
				if(otherversion) {
					GeneralLogger.log("Old config version: " + parts[0] + "-" + parts[1]);
					GeneralLogger.log("New config version: " + References.mainVersion + "-" + References.patchVerion);
					doCopyOfTheConfig(f);
				}
			}
		}
	}
	
	private static void doCopyOfTheConfig(File f) {
		InputStream is = null;
		try {
			is = Permissions.class.getResourceAsStream("/mainconfig.yml");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		OutputStream os = null;
		try {
			os = new FileOutputStream(f);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		byte[] buffer = new byte[4096];
		int length;
		try {
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static long filesCompareByByte(FileInputStream fileInputStream1, InputStream fileInputStream2) throws IOException {
		
		
	    try (BufferedInputStream fis1 = new BufferedInputStream(fileInputStream1);
	         BufferedInputStream fis2 = new BufferedInputStream(fileInputStream2)) {
	        
	        int ch = 0;
	        long pos = 1;
	        while ((ch = fis1.read()) != -1) {
	            if (ch != fis2.read()) {
	                return pos;
	            }
	            pos++;
	        }
	        if (fis2.read() == -1) {
	            return -1;
	        }
	        else {
	            return pos;
	        }
	    }
	}
	
	public static File getResourceAsFile(String resourcePath) {
	    try {
	        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
	        if (in == null) {
	            return null;
	        }

	        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
	        tempFile.deleteOnExit();

	        try (FileOutputStream out = new FileOutputStream(tempFile)) {
	            //copy stream
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = in.read(buffer)) != -1) {
	                out.write(buffer, 0, bytesRead);
	            }
	        }
	        return tempFile;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	public static boolean hasFolderAccess(File f) {
		
		return false;
	}
}
