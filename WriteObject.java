import java.net.*;
import java.io.*;

public class WriteObject
{
    public void exportRoster(final Chosen[] c, final String saveName) {
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            String path = Project.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String fileName = "";
            for (int i = path.length() - 1; i >= 0; --i) {
                if (path.charAt(i) != '/') {
                    fileName = String.valueOf(path.charAt(i)) + fileName;
                }
                else {
                    i = -1;
                }
            }
            path = path.substring(0, path.length() - fileName.length() - 1);
            path = URLDecoder.decode(path, "UTF-8");
            path = path.replaceAll("file:/", "");
            path = path.replaceAll(String.valueOf(File.separator) + "u0020", String.valueOf(File.separator) + " ");
            fout = new FileOutputStream(String.valueOf(path) + File.separator + saveName + ".ros");
            oos = new ObjectOutputStream(fout);
            oos.writeObject(c);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        finally {
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (fout != null) {
            try {
                fout.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (oos != null) {
            try {
                oos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void exportFile(final WorldState w, final String saveName) {
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            String path = Project.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String fileName = "";
            for (int i = path.length() - 1; i >= 0; --i) {
                if (path.charAt(i) != '/') {
                    fileName = String.valueOf(path.charAt(i)) + fileName;
                }
                else {
                    i = -1;
                }
            }
            path = path.substring(0, path.length() - fileName.length() - 1);
            path = URLDecoder.decode(path, "UTF-8");
            path = path.replaceAll("file:/", "");
            path = path.replaceAll(String.valueOf(File.separator) + "u0020", String.valueOf(File.separator) + " ");
            w.setSaveTitle(saveName);
            fout = new FileOutputStream(String.valueOf(path) + File.separator + saveName + ".par");
            oos = new ObjectOutputStream(fout);
            oos.writeObject(w);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        finally {
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (fout != null) {
            try {
                fout.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (oos != null) {
            try {
                oos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void serializeSaveData(final SaveData s) {
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            String path = Project.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String fileName = "";
            for (int i = path.length() - 1; i >= 0; --i) {
                if (path.charAt(i) != '/') {
                    fileName = String.valueOf(path.charAt(i)) + fileName;
                }
                else {
                    i = -1;
                }
            }
            path = path.substring(0, path.length() - fileName.length() - 1);
            path = URLDecoder.decode(path, "UTF-8");
            path = path.replaceAll("file:/", "");
            path = path.replaceAll(String.valueOf(File.separator) + "u0020", String.valueOf(File.separator) + " ");
            fout = new FileOutputStream(String.valueOf(path) + File.separator + "saves.sav");
            oos = new ObjectOutputStream(fout);
            oos.writeObject(s);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        finally {
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (fout != null) {
            try {
                fout.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (oos != null) {
            try {
                oos.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
