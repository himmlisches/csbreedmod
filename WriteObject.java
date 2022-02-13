import java.io.*;
import java.net.URLDecoder;

public class WriteObject {
	
public void exportFile(WorldState w, String saveName) {
	FileOutputStream fout = null;
	ObjectOutputStream oos = null;
	
	try {
		/*String path = Project.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String fileName = "";
		for (int i = path.length()-1; i >= 0; i--) {
			if (path.charAt(i) != java.io.File.separatorChar) {
				fileName = path.charAt(i) + fileName;
			} else {
				i = -1;
			}
		}
		path = path.substring(0, path.length() - fileName.length() - 1);
		path = URLDecoder.decode(path,"UTF-8");
		path = path.replaceAll("file:" + java.io.File.separator, "");
		path = path.replaceAll("\\u0020", java.io.File.separator + " ");
		
		w.setSaveTitle(saveName);
		
		fout = new FileOutputStream(path + java.io.File.separator + saveName + ".par");
		oos = new ObjectOutputStream(fout);
		oos.writeObject(w);*/
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
		path = URLDecoder.decode(path,"UTF-8");
		path = path.replaceAll("file:/", "");
		path = path.replaceAll(java.io.File.separator + "u0020", java.io.File.separator + " ");
		
		w.setSaveTitle(saveName);
		
		fout = new FileOutputStream(path + java.io.File.separator + saveName + ".par");
		oos = new ObjectOutputStream(fout);
		oos.writeObject(w);
	} catch (Exception ex) {
		
		

		ex.printStackTrace();

	} finally {

		if (fout != null) {
			try {
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (oos != null) {
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}

public void serializeSaveData(SaveData s) {
		
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;
		
		try {
			
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
			path = URLDecoder.decode(path,"UTF-8");
			path = path.replaceAll("file:/", "");
			path = path.replaceAll(java.io.File.separator + "u0020", java.io.File.separator + " ");
			
			fout = new FileOutputStream(path + java.io.File.separator + "saves.sav");
			oos = new ObjectOutputStream(fout);
			oos.writeObject(s);
			
		} catch (Exception ex) {
			
			

			ex.printStackTrace();

		} finally {

			if (fout != null) {
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		
	}

}
