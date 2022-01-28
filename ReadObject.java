import java.net.*;
import java.io.*;

public class ReadObject
{
    public WorldState[] importFiles() {
        WorldState[] worlds = new WorldState[0];
        FileInputStream fin = null;
        ObjectInputStream ois = null;
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
        try {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        path = path.replaceAll("file:/", "");
        path = path.replaceAll(String.valueOf(File.separator) + "u0020", String.valueOf(File.separator) + " ");
        final File f = new File(path);
        final File[] matchingFiles = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith("par");
            }
        });
        try {
            final WorldState[] newWorlds = new WorldState[matchingFiles.length];
            for (int j = 0; j < matchingFiles.length; ++j) {
                fin = new FileInputStream(matchingFiles[j]);
                ois = new ObjectInputStream(fin);
                newWorlds[j] = (WorldState)ois.readObject();
            }
            worlds = newWorlds;
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
            if (fin != null) {
                try {
                    fin.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return worlds;
            }
            return worlds;
        }
        finally {
            if (fin != null) {
                try {
                    fin.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (fin != null) {
            try {
                fin.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ois != null) {
            try {
                ois.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return worlds;
    }
    
    public Chosen[][] importRoster() {
        Chosen[][] additions = new Chosen[0][0];
        FileInputStream fin = null;
        ObjectInputStream ois = null;
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
        try {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        path = path.replaceAll("file:/", "");
        path = path.replaceAll(String.valueOf(File.separator) + "u0020", String.valueOf(File.separator) + " ");
        final File f = new File(path);
        final File[] matchingFiles = f.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.endsWith("ros");
            }
        });
        try {
            final Chosen[][] newAdditions = new Chosen[matchingFiles.length][0];
            for (int j = 0; j < matchingFiles.length; ++j) {
                fin = new FileInputStream(matchingFiles[j]);
                ois = new ObjectInputStream(fin);
                newAdditions[j] = (Chosen[])ois.readObject();
            }
            additions = newAdditions;
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
            if (fin != null) {
                try {
                    fin.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                return additions;
            }
            return additions;
        }
        finally {
            if (fin != null) {
                try {
                    fin.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (fin != null) {
            try {
                fin.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ois != null) {
            try {
                ois.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return additions;
    }
    
    public SaveData deserializeSaveData(final String filename) {
        SaveData s = null;
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        Label_0167: {
            try {
                fin = new FileInputStream(filename);
                ois = new ObjectInputStream(fin);
                s = (SaveData)ois.readObject();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                if (fin != null) {
                    try {
                        fin.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (ois != null) {
                    try {
                        ois.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break Label_0167;
            }
            finally {
                if (fin != null) {
                    try {
                        fin.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (ois != null) {
                    try {
                        ois.close();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (fin != null) {
                try {
                    fin.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        s.organizeScenes(48);
        return s;
    }
}
