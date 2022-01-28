import java.io.*;
import java.awt.*;

public class SaveData implements Serializable
{
    private static final long serialVersionUID = -3432656854627862248L;
    WorldState[] saves;
    String[] names;
    Forsaken[] harem;
    int forsakenMade;
    int chosenMade;
    String[][][] sceneText;
    Color[][][] sceneColor;
    Boolean[][][] sceneUnderline;
    String[] currentText;
    Color[] currentColor;
    Boolean[] currentUnderline;
    String[][] sceneButtons;
    String[][] sceneSummaries;
    Project.Emotion[][][] sceneEmotions;
    String[][][] sceneFaces;
    Chosen.Species[][][] sceneSpecs;
    Boolean[][][] sceneCivs;
    Boolean[][][] sceneFallen;
    Forsaken.Gender[][][] sceneGenders;
    Chosen[] customRoster;
    
    public SaveData() {
        this.saves = new WorldState[0];
        this.names = new String[0];
        this.harem = new Forsaken[0];
        this.forsakenMade = 0;
        this.chosenMade = 0;
        this.sceneText = new String[48][0][0];
        this.sceneColor = new Color[48][0][0];
        this.sceneUnderline = new Boolean[48][0][0];
        this.currentText = new String[0];
        this.currentColor = new Color[0];
        this.currentUnderline = new Boolean[0];
        this.sceneButtons = new String[48][0];
        this.sceneSummaries = new String[48][0];
        this.sceneEmotions = new Project.Emotion[48][0][5];
        this.sceneFaces = new String[48][0][5];
        this.sceneSpecs = new Chosen.Species[48][0][5];
        this.sceneCivs = new Boolean[48][0][5];
        this.sceneFallen = new Boolean[48][0][5];
        this.sceneGenders = new Forsaken.Gender[48][0][5];
        this.customRoster = new Chosen[0];
    }
    
    public void organizeScenes(final int scenesThisVersion) {
        this.newScene();
        final String[][][] newSceneText = new String[scenesThisVersion][0][0];
        final Color[][][] newSceneColor = new Color[scenesThisVersion][0][0];
        final Boolean[][][] newSceneUnderline = new Boolean[scenesThisVersion][0][0];
        final String[][] newSceneButtons = new String[scenesThisVersion][0];
        final String[][] newSceneSummaries = new String[scenesThisVersion][0];
        final Project.Emotion[][][] newSceneEmotions = new Project.Emotion[scenesThisVersion][0][5];
        final String[][][] newSceneFaces = new String[scenesThisVersion][0][5];
        final Chosen.Species[][][] newSceneSpecs = new Chosen.Species[scenesThisVersion][0][5];
        final Boolean[][][] newSceneCivs = new Boolean[scenesThisVersion][0][5];
        final Boolean[][][] newSceneFallen = new Boolean[scenesThisVersion][0][5];
        final Forsaken.Gender[][][] newSceneGenders = new Forsaken.Gender[scenesThisVersion][0][5];
        if (this.sceneText != null) {
            for (int i = 0; i < this.sceneText.length; ++i) {
                newSceneText[i] = this.sceneText[i];
                newSceneColor[i] = this.sceneColor[i];
                newSceneUnderline[i] = this.sceneUnderline[i];
                newSceneButtons[i] = this.sceneButtons[i];
                newSceneSummaries[i] = this.sceneSummaries[i];
                if (this.sceneEmotions != null) {
                    newSceneEmotions[i] = this.sceneEmotions[i];
                    newSceneFaces[i] = this.sceneFaces[i];
                    newSceneSpecs[i] = this.sceneSpecs[i];
                    newSceneCivs[i] = this.sceneCivs[i];
                    newSceneFallen[i] = this.sceneFallen[i];
                }
                else {
                    newSceneEmotions[i] = new Project.Emotion[this.sceneText[i].length][5];
                    newSceneFaces[i] = new String[this.sceneText[i].length][5];
                    newSceneSpecs[i] = new Chosen.Species[this.sceneText[i].length][5];
                    newSceneCivs[i] = new Boolean[this.sceneText[i].length][5];
                    newSceneFallen[i] = new Boolean[this.sceneText[i].length][5];
                }
                if (this.sceneGenders != null) {
                    newSceneGenders[i] = this.sceneGenders[i];
                }
                else {
                    newSceneGenders[i] = new Forsaken.Gender[this.sceneText[i].length][5];
                }
            }
        }
        this.sceneText = newSceneText;
        this.sceneColor = newSceneColor;
        this.sceneUnderline = newSceneUnderline;
        this.sceneButtons = newSceneButtons;
        this.sceneSummaries = newSceneSummaries;
        this.sceneEmotions = newSceneEmotions;
        this.sceneFaces = newSceneFaces;
        this.sceneSpecs = newSceneSpecs;
        this.sceneCivs = newSceneCivs;
        this.sceneFallen = newSceneFallen;
        this.sceneGenders = newSceneGenders;
    }
    
    public void newScene() {
        this.currentText = new String[0];
        this.currentColor = new Color[0];
        this.currentUnderline = new Boolean[0];
    }
    
    public void addLine(final String t, final Color c, final Boolean u) {
        final String[] newText = new String[this.currentText.length + 1];
        final Color[] newColor = new Color[this.currentColor.length + 1];
        final Boolean[] newUnderline = new Boolean[this.currentUnderline.length + 1];
        for (int i = 0; i < this.currentText.length; ++i) {
            newText[i] = this.currentText[i];
            newColor[i] = this.currentColor[i];
            newUnderline[i] = this.currentUnderline[i];
        }
        newText[this.currentText.length] = t;
        newColor[this.currentColor.length] = c;
        newUnderline[this.currentUnderline.length] = u;
        this.currentText = newText;
        this.currentColor = newColor;
        this.currentUnderline = newUnderline;
    }
    
    public void saveScene(final int type, final String button, final String summary) {
        Boolean unique = true;
        Boolean difference;
        for (int i = 0; i < this.sceneText[type].length && unique; unique = difference, ++i) {
            difference = false;
            for (int j = 0; j < this.sceneText[type][i].length && !difference; ++j) {
                if (this.sceneText[type][i].length != this.currentText.length) {
                    difference = true;
                }
                else if (!this.sceneText[type][i][j].contentEquals(this.currentText[j])) {
                    difference = true;
                }
            }
        }
        if (unique) {
            final String[][] newText = new String[this.sceneText[type].length + 1][0];
            final Color[][] newColor = new Color[this.sceneColor[type].length + 1][0];
            final Boolean[][] newUnderline = new Boolean[this.sceneUnderline[type].length + 1][0];
            final String[] newButtons = new String[this.sceneButtons[type].length + 1];
            final String[] newSummaries = new String[this.sceneSummaries[type].length + 1];
            final Project.Emotion[][] newEmotions = new Project.Emotion[this.sceneEmotions[type].length + 1][5];
            final String[][] newFaces = new String[this.sceneFaces[type].length + 1][5];
            final Chosen.Species[][] newSpecs = new Chosen.Species[this.sceneSpecs[type].length + 1][5];
            final Boolean[][] newCivs = new Boolean[this.sceneCivs[type].length + 1][5];
            final Boolean[][] newFallen = new Boolean[this.sceneFallen[type].length + 1][5];
            final Forsaken.Gender[][] newGenders = new Forsaken.Gender[this.sceneGenders[type].length + 1][5];
            for (int k = 0; k < this.sceneText[type].length; ++k) {
                newText[k] = this.sceneText[type][k];
                newColor[k] = this.sceneColor[type][k];
                newUnderline[k] = this.sceneUnderline[type][k];
                newButtons[k] = this.sceneButtons[type][k];
                newSummaries[k] = this.sceneSummaries[type][k];
                newEmotions[k] = this.sceneEmotions[type][k];
                newFaces[k] = this.sceneFaces[type][k];
                newSpecs[k] = this.sceneSpecs[type][k];
                newCivs[k] = this.sceneCivs[type][k];
                newFallen[k] = this.sceneFallen[type][k];
                newGenders[k] = this.sceneGenders[type][k];
            }
            newText[this.sceneText[type].length] = this.currentText;
            newColor[this.sceneColor[type].length] = this.currentColor;
            newUnderline[this.sceneUnderline[type].length] = this.currentUnderline;
            newButtons[this.sceneButtons[type].length] = button;
            newSummaries[this.sceneSummaries[type].length] = summary;
            newEmotions[this.sceneEmotions[type].length] = Project.displayedEmotions;
            newFaces[this.sceneFaces[type].length] = Project.displayedNames;
            newSpecs[this.sceneSpecs[type].length] = Project.displayedType;
            newCivs[this.sceneCivs[type].length] = Project.displayedCivilians;
            newFallen[this.sceneFallen[type].length] = Project.displayedFallen;
            newGenders[this.sceneGenders[type].length] = Project.displayedGender;
            this.sceneText[type] = newText;
            this.sceneColor[type] = newColor;
            this.sceneUnderline[type] = newUnderline;
            this.sceneButtons[type] = newButtons;
            this.sceneSummaries[type] = newSummaries;
            this.sceneEmotions[type] = newEmotions;
            this.sceneFaces[type] = newFaces;
            this.sceneSpecs[type] = newSpecs;
            this.sceneCivs[type] = newCivs;
            this.sceneFallen[type] = newFallen;
            this.sceneGenders[type] = newGenders;
            final WriteObject wobj = new WriteObject();
            wobj.serializeSaveData(this);
        }
        this.newScene();
    }
    
    public int assignID() {
        return ++this.forsakenMade;
    }
    
    public int assignChosenID() {
        return ++this.chosenMade;
    }
    
    public void fillIDs() {
        for (int i = 0; i < this.harem.length; ++i) {
            if (this.harem[i].forsakenID == 0) {
                this.harem[i].forsakenID = this.assignID();
            }
        }
        for (int i = 0; i < this.saves.length; ++i) {
            if (this.saves[i].campaign == null) {
                this.saves[i].campaign = false;
            }
            if (this.saves[i].campaign) {
                for (int j = 0; j < this.saves[i].conquered.length; ++j) {
                    if (this.saves[i].conquered[j].forsakenID == 0) {
                        this.saves[i].conquered[j].forsakenID = this.assignID();
                    }
                }
                for (int j = 0; j < this.saves[i].sacrificed.length; ++j) {
                    if (this.saves[i].sacrificed[j].forsakenID == 0) {
                        this.saves[i].sacrificed[j].forsakenID = this.assignID();
                    }
                }
            }
            for (int j = 0; j < 3; ++j) {
                if (this.saves[i].getCast()[j] != null && this.saves[i].getCast()[j].globalID == 0) {
                    this.saves[i].getCast()[j].globalID = this.assignChosenID();
                }
            }
        }
    }
    
    public void checkIDs() {
        int highest = 0;
        int highestChosen = 0;
        for (int i = 0; i < this.harem.length; ++i) {
            if (this.harem[i].forsakenID > highest) {
                highest = this.harem[i].forsakenID;
            }
        }
        for (int i = 0; i < this.saves.length; ++i) {
            if (this.saves[i].campaign) {
                for (int j = 0; j < this.saves[i].conquered.length; ++j) {
                    if (this.saves[i].conquered[j].forsakenID > highest) {
                        highest = this.saves[i].conquered[j].forsakenID;
                    }
                }
                for (int j = 0; j < this.saves[i].sacrificed.length; ++j) {
                    if (this.saves[i].sacrificed[j].forsakenID > highest) {
                        highest = this.saves[i].sacrificed[j].forsakenID;
                    }
                }
            }
            for (int j = 0; j < 3; ++j) {
                if (this.saves[i].getCast()[j] != null && this.saves[i].getCast()[j].globalID > highestChosen) {
                    highestChosen = this.saves[i].getCast()[j].globalID;
                }
            }
        }
        this.forsakenMade = highest;
        this.chosenMade = highestChosen;
    }
    
    public WorldState[] getSaves() {
        return this.saves;
    }
    
    public String[] getNames() {
        return this.names;
    }
    
    public void newSave(final WorldState w, final String name) {
        final WorldState[] newSaves = new WorldState[this.saves.length + 1];
        newSaves[0] = w;
        final String[] newNames = new String[this.names.length + 1];
        newNames[0] = name;
        for (int i = 0; i < this.saves.length; ++i) {
            newSaves[i + 1] = this.saves[i];
            newNames[i + 1] = this.names[i];
        }
        this.saves = newSaves;
        this.names = newNames;
    }
    
    public void endSave(final WorldState w, final String name) {
        final WorldState[] newSaves = new WorldState[this.saves.length + 1];
        newSaves[this.saves.length] = w;
        w.save = this;
        final String[] newNames = new String[this.names.length + 1];
        newNames[this.saves.length] = name;
        for (int i = 0; i < this.saves.length; ++i) {
            newSaves[i] = this.saves[i];
            newNames[i] = this.names[i];
        }
        this.saves = newSaves;
        this.names = newNames;
    }
    
    public void overwriteSave(final WorldState w) {
        this.saves[0] = w;
    }
    
    public void deleteSave(final int file) {
        final WorldState[] newSaves = new WorldState[this.saves.length - 1];
        final String[] newNames = new String[this.names.length - 1];
        for (int i = 0; i < this.saves.length; ++i) {
            if (i < file) {
                newSaves[i] = this.saves[i];
                newNames[i] = this.names[i];
            }
            else if (i > file) {
                newSaves[i - 1] = this.saves[i];
                newNames[i - 1] = this.names[i];
            }
        }
        this.saves = newSaves;
        this.names = newNames;
    }
    
    public void moveToFront(final int file) {
        final WorldState[] newSaves = new WorldState[this.saves.length];
        final String[] newNames = new String[this.names.length];
        newSaves[0] = this.saves[file];
        newNames[0] = this.names[file];
        for (int i = 0; i < this.saves.length - 1; ++i) {
            if (i < file) {
                newSaves[i + 1] = this.saves[i];
                newNames[i + 1] = this.names[i];
            }
            else {
                newSaves[i + 1] = this.saves[i + 1];
                newNames[i + 1] = this.names[i + 1];
            }
        }
        this.saves = newSaves;
        this.names = newNames;
    }
}
