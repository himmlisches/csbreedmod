import java.io.Serializable;
import java.awt.*;

public class SaveData implements Serializable {
	
	private static final long serialVersionUID = -3432656854627862248L;
	
	WorldState[] saves = new WorldState[0];
	String[] names = new String[0];
	Forsaken[] harem = new Forsaken[0];
	int forsakenMade = 0;
	int chosenMade = 0;
	String[][][] sceneText = new String[Project.scenesThisVersion][0][0];
	Color[][][] sceneColor = new Color[Project.scenesThisVersion][0][0];
	Boolean[][][] sceneUnderline = new Boolean[Project.scenesThisVersion][0][0];
	String[] currentText = new String[0];
	Color[] currentColor = new Color[0];
	Boolean[] currentUnderline = new Boolean[0];
	String[][] sceneButtons = new String[Project.scenesThisVersion][0];
	String[][] sceneSummaries = new String[Project.scenesThisVersion][0];
	Project.Emotion[][][] sceneEmotions = new Project.Emotion[Project.scenesThisVersion][0][5];
	String[][][] sceneFaces = new String[Project.scenesThisVersion][0][5];
	Chosen.Species[][][] sceneSpecs = new Chosen.Species[Project.scenesThisVersion][0][5];
	Boolean[][][] sceneCivs = new Boolean[Project.scenesThisVersion][0][5];
	Boolean[][][] sceneFallen = new Boolean[Project.scenesThisVersion][0][5];
	Forsaken.Gender[][][] sceneGenders = new Forsaken.Gender[Project.scenesThisVersion][0][5];
	
	public void organizeScenes(int scenesThisVersion) {
		//0: first meetings (pair and trio)
		//1: interviews (1, 2, and 3)
		//2: vacations (1, 2, and 3)
		//3: preparations (1, 2, and 3)
		//4: epilogues
		//5: first violences
		//6: first services
		//7: first beggings
		//8: first coverings
		//9: first rapes
		//10: first orgasms
		//11: first tortures
		//12: first broadcasts
		//13: first slaughters
		//14: first fantasizes
		//15: first detonations
		//16: first stripteases
		//17: first impregnations
		//18: first hypnotisms
		//19: first drains
		//20: first parasitisms
		//21: Morality/Confidence distortion 1
		//22: Morality/Confidence distortion 2
		//23: Innocence/Dignity distortion 1
		//24: Innocence/Dignity distortion 2
		//25: Innocence/Confidence distortion 1
		//26: Innocence/Confidence distortion 2
		//27: Morality/Dignity distortion 1
		//28: Morality/Dignity distortion 2
		//29: Morality/Innocence distortion 1
		//30: Morality/Innocence distortion 2
		//31: Confidence/Dignity distortion 1
		//32: Confidence/Dignity distortion 2
		//33: Service to secure donations (high Morality, T1 Innocence)
		//34: Sexual training (high Confidence, T1 Innocence)
		//35: Blackmailed into service (high Dignity, T1 Innocence, no more than T1 Dignity)
		//36: Bodypaint to render stripping irrelevant (high Innocence, T2 Dignity)
		//37: Public appearances in increasingly skimpy outfits [or photoshoot?] (high Confidence, T2 Dignity)
		//38: Berated by a former fan (high Morality, T2 Dignity)
		//39: Sex between two Chosen (mid Confidence, low Confidence, friendly, T2 Morality)
		//40: Sex between two Chosen (high Confidence, low Confidence, friendly)
		//41: Train molester (high Morality, low trauma)
		//42: Sex between two Chosen (high Confidence, mid Confidence, friendly)
		//43: Guilted by serviced Thrall (high Morality, T1 Innocence and T1 Confidence)
		//44: Sleep molester (mid Morality, low trauma)
		newScene();
		String[][][] newSceneText = new String[scenesThisVersion][0][0];
		Color[][][] newSceneColor = new Color[scenesThisVersion][0][0];
		Boolean[][][] newSceneUnderline = new Boolean[scenesThisVersion][0][0];
		String[][] newSceneButtons = new String[scenesThisVersion][0];
		String[][] newSceneSummaries = new String[scenesThisVersion][0];
		Project.Emotion[][][] newSceneEmotions = new Project.Emotion[scenesThisVersion][0][5];
		String[][][] newSceneFaces = new String[scenesThisVersion][0][5];
		Chosen.Species[][][] newSceneSpecs = new Chosen.Species[scenesThisVersion][0][5];
		Boolean[][][] newSceneCivs = new Boolean[scenesThisVersion][0][5];
		Boolean[][][] newSceneFallen = new Boolean[scenesThisVersion][0][5];
		Forsaken.Gender[][][] newSceneGenders = new Forsaken.Gender[scenesThisVersion][0][5];
		if (sceneText != null) {
			for (int i = 0; i < sceneText.length; i++) {
				newSceneText[i] = sceneText[i];
				newSceneColor[i] = sceneColor[i];
				newSceneUnderline[i] = sceneUnderline[i];
				newSceneButtons[i] = sceneButtons[i];
				newSceneSummaries[i] = sceneSummaries[i];
				if (sceneEmotions != null) {
					newSceneEmotions[i] = sceneEmotions[i];
					newSceneFaces[i] = sceneFaces[i];
					newSceneSpecs[i] = sceneSpecs[i];
					newSceneCivs[i] = sceneCivs[i];
					newSceneFallen[i] = sceneFallen[i];
				} else {
					newSceneEmotions[i] = new Project.Emotion[sceneText[i].length][5];
					newSceneFaces[i] = new String[sceneText[i].length][5];
					newSceneSpecs[i] = new Chosen.Species[sceneText[i].length][5];
					newSceneCivs[i] = new Boolean[sceneText[i].length][5];
					newSceneFallen[i] = new Boolean[sceneText[i].length][5];
				}
				if (sceneGenders != null) {
					newSceneGenders[i] = sceneGenders[i];
				} else {
					newSceneGenders[i] = new Forsaken.Gender[sceneText[i].length][5];
				}
			}
		}
		sceneText = newSceneText;
		sceneColor = newSceneColor;
		sceneUnderline = newSceneUnderline;
		sceneButtons = newSceneButtons;
		sceneSummaries = newSceneSummaries;
		sceneEmotions = newSceneEmotions;
		sceneFaces = newSceneFaces;
		sceneSpecs = newSceneSpecs;
		sceneCivs = newSceneCivs;
		sceneFallen = newSceneFallen;
		sceneGenders = newSceneGenders;
	}
	
	public void newScene() {
		currentText = new String[0];
		currentColor = new Color[0];
		currentUnderline = new Boolean[0];
	}
	
	public void addLine(String t, Color c, Boolean u) {
		String[] newText = new String[currentText.length+1];
		Color[] newColor = new Color[currentColor.length+1];
		Boolean[] newUnderline = new Boolean[currentUnderline.length+1];
		for (int i = 0; i < currentText.length; i++) {
			newText[i] = currentText[i];
			newColor[i] = currentColor[i];
			newUnderline[i] = currentUnderline[i];
		}
		newText[currentText.length] = t;
		newColor[currentColor.length] = c;
		newUnderline[currentUnderline.length] = u;
		currentText = newText;
		currentColor = newColor;
		currentUnderline = newUnderline;
	}
	
	public void saveScene(int type, String button, String summary) {
		Boolean unique = true;
		for (int i = 0; i < sceneText[type].length && unique; i++) {
			Boolean difference = false;
			for (int j = 0; j < sceneText[type][i].length && difference == false; j++) {
				if (sceneText[type][i].length != currentText.length) {
					difference = true;
				} else if (sceneText[type][i][j].contentEquals(currentText[j]) == false) {
					difference = true;
				}
			}
			unique = difference;
		}
		if (unique) {
			String[][] newText = new String[sceneText[type].length+1][0];
			Color[][] newColor = new Color[sceneColor[type].length+1][0];
			Boolean[][] newUnderline = new Boolean[sceneUnderline[type].length+1][0];
			String[] newButtons = new String[sceneButtons[type].length+1];
			String[] newSummaries = new String[sceneSummaries[type].length+1];
			Project.Emotion[][] newEmotions = new Project.Emotion[sceneEmotions[type].length+1][5];
			String[][] newFaces = new String[sceneFaces[type].length+1][5];
			Chosen.Species[][] newSpecs = new Chosen.Species[sceneSpecs[type].length+1][5];
			Boolean[][] newCivs = new Boolean[sceneCivs[type].length+1][5];
			Boolean[][] newFallen = new Boolean[sceneFallen[type].length+1][5];
			Forsaken.Gender[][] newGenders = new Forsaken.Gender[sceneGenders[type].length+1][5];
			for (int i = 0; i < sceneText[type].length; i++) {
				newText[i] = sceneText[type][i];
				newColor[i] = sceneColor[type][i];
				newUnderline[i] = sceneUnderline[type][i];
				newButtons[i] = sceneButtons[type][i];
				newSummaries[i] = sceneSummaries[type][i];
				newEmotions[i] = sceneEmotions[type][i];
				newFaces[i] = sceneFaces[type][i];
				newSpecs[i] = sceneSpecs[type][i];
				newCivs[i] = sceneCivs[type][i];
				newFallen[i] = sceneFallen[type][i];
				newGenders[i] = sceneGenders[type][i];
			}
			newText[sceneText[type].length] = currentText;
			newColor[sceneColor[type].length] = currentColor;
			newUnderline[sceneUnderline[type].length] = currentUnderline;
			newButtons[sceneButtons[type].length] = button;
			newSummaries[sceneSummaries[type].length] = summary;
			newEmotions[sceneEmotions[type].length] = Project.displayedEmotions;
			newFaces[sceneFaces[type].length] = Project.displayedNames;
			newSpecs[sceneSpecs[type].length] = Project.displayedType;
			newCivs[sceneCivs[type].length] = Project.displayedCivilians;
			newFallen[sceneFallen[type].length] = Project.displayedFallen;
			newGenders[sceneGenders[type].length] = Project.displayedGender;
			sceneText[type] = newText;
			sceneColor[type] = newColor;
			sceneUnderline[type] = newUnderline;
			sceneButtons[type] = newButtons;
			sceneSummaries[type] = newSummaries;
			sceneEmotions[type] = newEmotions;
			sceneFaces[type] = newFaces;
			sceneSpecs[type] = newSpecs;
			sceneCivs[type] = newCivs;
			sceneFallen[type] = newFallen;
			sceneGenders[type] = newGenders;
			WriteObject wobj = new WriteObject();
			wobj.serializeSaveData(this);
		}
		newScene();
	}
	
	public int assignID() {
		forsakenMade++;
		return forsakenMade;
	}
	
	public int assignChosenID() {
		chosenMade++;
		return chosenMade;
	}
	
	public void fillIDs() {
		for (int i = 0; i < harem.length; i++) {
			if (harem[i].forsakenID == 0) {
				harem[i].forsakenID = assignID();
			}
		}
		for (int i = 0; i < saves.length; i++) {
			if (saves[i].campaign == null) {
				saves[i].campaign = false;
			}
			if (saves[i].campaign) {
				for (int j = 0; j < saves[i].conquered.length; j++) {
					if (saves[i].conquered[j].forsakenID == 0) {
						saves[i].conquered[j].forsakenID = assignID();
					}
				}
				for (int j = 0; j < saves[i].sacrificed.length; j++) {
					if (saves[i].sacrificed[j].forsakenID == 0) {
						saves[i].sacrificed[j].forsakenID = assignID();
					}
				}
			}
			for (int j = 0; j < 3; j++) {
				if (saves[i].getCast()[j] != null) {
					if (saves[i].getCast()[j].globalID == 0) {
						saves[i].getCast()[j].globalID = assignChosenID();
					}
				}
			}
		}
	}
	
	public void checkIDs() {
		int highest = 0;
		int highestChosen = 0;
		for (int i = 0; i < harem.length; i++) {
			if (harem[i].forsakenID > highest) {
				highest = harem[i].forsakenID;
			}
		}
		for (int i = 0; i < saves.length; i++) {
			if (saves[i].campaign) {
				for (int j = 0; j < saves[i].conquered.length; j++) {
					if (saves[i].conquered[j].forsakenID > highest) {
						highest = saves[i].conquered[j].forsakenID;
					}
				}
				for (int j = 0; j < saves[i].sacrificed.length; j++) {
					if (saves[i].sacrificed[j].forsakenID > highest) {
						highest = saves[i].sacrificed[j].forsakenID;
					}
				}
			}
			for (int j = 0; j < 3; j++) {
				if (saves[i].getCast()[j] != null) {
					if (saves[i].getCast()[j].globalID > highestChosen) {
						highestChosen = saves[i].getCast()[j].globalID;
					}
				}
			}
		}
		forsakenMade = highest;
		chosenMade = highestChosen;
	}
	
	public WorldState[] getSaves() {
		return saves;
	}
	
	public String[] getNames() {
		return names;
	}
	
	public void newSave(WorldState w, String name) {
		WorldState[] newSaves = new WorldState[saves.length + 1];
		newSaves[0] = w;
		String[] newNames = new String[names.length + 1];
		newNames[0] = name;
		for (int i = 0; i < saves.length; i++) {
			newSaves[i+1] = saves[i];
			newNames[i+1] = names[i];
		}
		saves = newSaves;
		names = newNames;
	}
	
	public void endSave(WorldState w, String name) {
		WorldState[] newSaves = new WorldState[saves.length + 1];
		newSaves[saves.length] = w;
		w.save = this;
		String[] newNames = new String[names.length + 1];
		newNames[saves.length] = name;
		for (int i = 0; i < saves.length; i++) {
			newSaves[i] = saves[i];
			newNames[i] = names[i];
		}
		saves = newSaves;
		names = newNames;
	}
	
	public void overwriteSave(WorldState w) {
		saves[0] = w;
	}
	
	public void deleteSave(int file) {
		WorldState[] newSaves = new WorldState[saves.length - 1];
		String[] newNames = new String[names.length - 1];
		for (int i = 0; i < saves.length; i++) {
			if (i < file) {
				newSaves[i] = saves[i];
				newNames[i] = names[i];
			} else if (i > file) {
				newSaves[i-1] = saves[i];
				newNames[i-1] = names[i];
			}
		}
		saves = newSaves;
		names = newNames;
	}
	
	public void moveToFront(int file) {
		WorldState[] newSaves = new WorldState[saves.length];
		String[] newNames = new String[names.length];
		newSaves[0] = saves[file];
		newNames[0] = names[file];
		for (int i = 0; i < saves.length - 1; i++) {
			if (i < file) {
				newSaves[i+1] = saves[i];
				newNames[i+1] = names[i];
			} else {
				newSaves[i+1] = saves[i+1];
				newNames[i+1] = names[i+1];
			}
		}
		saves = newSaves;
		names = newNames;
	}
	
}