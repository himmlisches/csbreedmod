import java.io.*;
import java.net.URLDecoder;

public class ReadObject {
	
public WorldState[] importFiles() {
	WorldState[] worlds = {};
	
	FileInputStream fin = null;
	ObjectInputStream ois = null;
	
	String path = Project.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	String fileName = "";
	for (int i = path.length()-1; i >= 0; i--) {
		if (path.charAt(i) != '/') {
			fileName = path.charAt(i) + fileName;
		} else {
			i = -1;
		}
	}
	path = path.substring(0, path.length() - fileName.length() - 1);
	try {
		path = URLDecoder.decode(path,"UTF-8");
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	path = path.replaceAll("file:/", "");
	path = path.replaceAll(java.io.File.separator + "u0020", java.io.File.separator + " ");
	
	File f = new File(path);
	File[] matchingFiles = f.listFiles(new FilenameFilter() {
	    public boolean accept(File dir, String name) {
	        return name.endsWith("par");
	    }
	});
	
	try {
		WorldState[] newWorlds = new WorldState[matchingFiles.length];
		for (int i = 0; i < matchingFiles.length; i++) {
			fin = new FileInputStream(matchingFiles[i]);
			ois = new ObjectInputStream(fin);
			newWorlds[i] = (WorldState) ois.readObject();
		}
		worlds = newWorlds;

	} catch (Exception ex) {
		ex.printStackTrace();
	} finally {

		if (fin != null) {
			try {
				fin.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (ois != null) {
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	return worlds;
}

public SaveData deserializeSaveData(String filename) {
		
		SaveData s = null;
		
		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {

			fin = new FileInputStream(filename);
			ois = new ObjectInputStream(fin);
			s = (SaveData) ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {

			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		
		s.organizeScenes(Project.scenesThisVersion);
		
		return s;
		
	}

}
