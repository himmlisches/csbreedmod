import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.URLDecoder;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;

public class Project extends JFrame {
	
	final static long million = 1000000;
	final static long billion = 1000000000L;
	final static long trillion = 1000*billion;
	final static long quadrillion = 1000*trillion;
	final static long quintillion = 1000*quadrillion;
	final static int scenesThisVersion = 48;
	final static int vignettesThisVersion = 15;
	
	public static JFrame window = new JFrame("Project");
	public static Container nestedcp = new Container();
	public static Container portraits = new Container();
	public static JTextPane textPane = new JTextPane();
	public static JScrollPane scrollPane = new JScrollPane(textPane);
	public static JScrollPane portraitPane = new JScrollPane(portraits);
	public static Emotion[] displayedEmotions = new Emotion[5];
	public static String[] displayedNames = new String[5];
	public static Chosen.Species[] displayedType = new Chosen.Species[5];
	public static Boolean[] displayedCivilians = new Boolean[]{false, false, false, false, false};
	public static Boolean[] displayedFallen = new Boolean[]{false, false, false, false, false};
	public static Forsaken.Gender[] displayedGender = new Forsaken.Gender[]{Forsaken.Gender.FEMALE, Forsaken.Gender.FEMALE, Forsaken.Gender.FEMALE, Forsaken.Gender.FEMALE, Forsaken.Gender.FEMALE};
	
	public static enum Emotion {
		ANGER,
		FEAR,
		FOCUS,
		JOY,
		LEWD,
		NEUTRAL,
		SHAME,
		STRUGGLE,
		SWOON
	}

	public Project() {
		
		Container cp = window.getContentPane();
		window.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
		
		//nestedcp.setLayout(new BoxLayout(nestedcp, BoxLayout.X_AXIS));
		nestedcp.setLayout(new GridBagLayout());
		cp.add(nestedcp);
		//portraits.setLayout(new GridLayout(4,1));
		portraits.setLayout(new BoxLayout(portraits, BoxLayout.Y_AXIS));
		
		textPane.setEditable(false);
		//scrollPane.setPreferredSize(new Dimension(1300, 760));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		nestedcp.add(scrollPane, c);
		
		
		JPanel controlPanel = new JPanel(new FlowLayout());
		controlPanel.setMaximumSize(new Dimension(5000,40));
		cp.add(controlPanel);
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setTitle("Corrupted Saviors");
		window.setSize(new Dimension(1300, 800));
		window.setVisible(true);
		
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		
		WorldState ThisState = new WorldState();
		ThisState.toggleColors(textPane);
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
		File saveLocation = new File(path + java.io.File.separator + "saves.sav");
		SaveData saves = null;
		if (saveLocation.exists()) {
			ReadObject robj = new ReadObject();
			saves = robj.deserializeSaveData(path + java.io.File.separator + "saves.sav");
		} else {
			saves = new SaveData();
		}
		final SaveData saveFile = saves;
		if (saveFile.getSaves().length > 0) {
			ThisState.copySettings(textPane, saveFile.getSaves()[0]);
			ThisState.copyToggles(saveFile.getSaves()[0]);
			ThisState.setGenders(saveFile.getSaves()[0].getGenderBalance());
			if (ThisState.hardMode == false) {
				ThisState.eventOffset = 0;
				ThisState.clampPercent = 100;
				ThisState.clampStart = 11;
				ThisState.downtimeMultiplier = 100;
				ThisState.types = new Chosen.Species[3];
			}
		}
		if (saves.harem == null) {
			saves.harem = new Forsaken[0];
		}
		saves.fillIDs();
		if (saves.harem != null) {
			if (saves.harem.length > 0) {
				if (saves.harem[0].hateExp == 0) {
					for (int i = 0; i < saves.harem.length; i++) {
						saves.harem[i].hateExp = 20000;
						saves.harem[i].pleaExp = 20000;
						saves.harem[i].injuExp = 20000;
						saves.harem[i].expoExp = 20000;
						saves.harem[i].chooseCombatStyle();
						saves.harem[i].motivation = 1000;
						saves.harem[i].stamina = 1000;
						if (saves.harem[i].innocence > 66) {
							saves.harem[i].textColor = new Color(255, 0, 150);
							saves.harem[i].darkColor = new Color(255, 0, 150);
						} else if (saves.harem[i].innocence > 33) {
							saves.harem[i].textColor = new Color(120, 50, 180);
							saves.harem[i].darkColor = new Color(150, 100, 200);
						} else {
							saves.harem[i].textColor = new Color(200, 100, 100);
							saves.harem[i].darkColor = new Color(255, 130, 220);
						}
						saves.harem[i].others = null;
					}
					WriteObject wobj = new WriteObject();
					wobj.serializeSaveData(saves);
				}
				for (int i = 0; i < saves.harem.length; i++) {
					if (saves.harem[i].forsakenID == 0) {
						saves.harem[i].forsakenID = saves.assignID();
						WriteObject wobj = new WriteObject();
						wobj.serializeSaveData(saves);
					}
					if (saves.harem[i].forsakenRelations == null) {
						if (saves.harem[i].chosenRelations == null) {
							saves.harem[i].otherChosen = new Chosen[]{saves.harem[i].firstFormerPartner, saves.harem[i].secondFormerPartner};
							saves.harem[i].chosenRelations = new Forsaken.Relationship[]{Forsaken.Relationship.PARTNER, Forsaken.Relationship.PARTNER};
							if (saves.harem[i].others != null) {
								saves.harem[i].forsakenRelations = new Forsaken.Relationship[saves.harem[i].others.length];
								for (int j = 0; j < saves.harem[i].others.length; j++) {
									if (saves.harem[i].others[j].equals(saves.harem[i].firstPartner) || saves.harem[i].others[j].equals(saves.harem[i].secondPartner)) {
										saves.harem[i].forsakenRelations[j] = Forsaken.Relationship.PARTNER;
									}
								}
							} else {
								saves.harem[i].others = new Forsaken[0];
								saves.harem[i].forsakenRelations = new Forsaken.Relationship[0];
								saves.harem[i].troublemaker = new int[0];
							}
							Forsaken[] checkedForsaken = saves.harem;
							for (int j = 0; j < checkedForsaken.length; j++) {
								if (checkedForsaken[j].equals(saves.harem[i].firstPartner) || checkedForsaken[j].equals(saves.harem[i].secondPartner)) {
									Boolean alreadyThere = false;
									for (int k = 0; k < saves.harem[i].others.length; k++) {
										if (saves.harem[i].others[k].equals(checkedForsaken[j])) {
											alreadyThere = true;
										}
									}
									if (alreadyThere == false) {
										Forsaken[] newOthers = new Forsaken[saves.harem[i].others.length+1];
										Forsaken.Relationship[] newRelationships = new Forsaken.Relationship[saves.harem[i].forsakenRelations.length+1];
										int[] newTroubles = new int[saves.harem[i].troublemaker.length+1];
										for (int k = 0; k < saves.harem[i].others.length; k++) {
											newOthers[k] = saves.harem[i].others[k];
											newRelationships[k] = saves.harem[i].forsakenRelations[k];
											newTroubles[k] = saves.harem[i].troublemaker[k];
										}
										newOthers[saves.harem[i].others.length] = checkedForsaken[j];
										newRelationships[saves.harem[i].forsakenRelations.length] = Forsaken.Relationship.PARTNER;
										saves.harem[i].others = newOthers;
										saves.harem[i].forsakenRelations = newRelationships;
										saves.harem[i].troublemaker = newTroubles;
									}
								}
							}
						}
					}
					saves.harem[i].save = saves;
				}
			}
		}
		if (saves.sceneText == null) {
			saves.organizeScenes(scenesThisVersion);
		} else if (saves.sceneText.length < scenesThisVersion) {
			saves.organizeScenes(scenesThisVersion);
		}
		if (saves.harem == null) {
			saves.harem = new Forsaken[0];
			WriteObject wobj = new WriteObject();
			wobj.serializeSaveData(saves);
		}
		saves.currentText = new String[0];
		saves.currentColor = new Color[0];
		saves.currentUnderline = new Boolean[0];
		ThisState.save = saves;
		IntroOne(textPane, controlPanel, window, ThisState);
	}
	
	public static String getFilePath() {
		String result = "";
		result = Project.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String fileName = "";
		for (int i = result.length()-1; i >= 0; i--) {
			if (result.charAt(i) != '/') {
				fileName = result.charAt(i) + fileName;
			} else {
				i = -1;
			}
		}
		result = result.substring(0, result.length() - fileName.length() - 1);
		try {
			result = URLDecoder.decode(result,"UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		result = result.replaceAll("file:/", "");
		result = result.replaceAll(java.io.File.separator + "u0020", java.io.File.separator + " ");
		return result;
	}
	
	public static void clearPortraits() {
		nestedcp.remove(portraitPane);
		portraits.removeAll();
		displayedNames = new String[5];
		nestedcp.validate();
		nestedcp.repaint();
	}
	
	public static void changePortrait(Forsaken.Gender gender, Chosen.Species spec, Boolean civilian, Boolean fallen, WorldState w, String[] names, int number, Emotion first, Emotion backup) {
		if (w.portraits) {
			int displayed = 5;
			if (names[3] == null) {
				displayed = 3;
				displayedEmotions[3] = null;
				displayedNames[3] = null;
				displayedEmotions[4] = null;
				displayedNames[4] = null;
			} else if (names[4] == null) {
				displayed = 4;
				displayedEmotions[4] = null;
				displayedNames[4] = null;
			}
			displayedType[number] = spec;
			displayedCivilians[number] = civilian;
			displayedFallen[number] = fallen;
			displayedGender[number] = gender;
			nestedcp.remove(portraitPane);
			portraits.removeAll();
			portraitPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			portraitPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			int imageSize = scrollPane.getHeight()/displayed;
			BufferedImage image = null;
			for (int i = 0; i < displayed; i++) {
				displayedNames[i] = names[i];
				if (i == number) {
					if (displayedEmotions[i] == first) {
						displayedEmotions[i] = backup;
					} else {
						displayedEmotions[i] = first;
					}
				}
				String path = getFilePath() + java.io.File.separator + "portraits" + java.io.File.separator + "empty";
				if (names[i] != null) {
					path = getFilePath() + java.io.File.separator + "portraits" + java.io.File.separator + names[i] + java.io.File.separator;
				}
				String[] folders = new String[]{"", "", "", ""};
				if (displayedGender[i] == Forsaken.Gender.MALE) {
					folders[0] = "male" + java.io.File.separator;
				}
				if (displayedType[i] == Chosen.Species.SUPERIOR) {
					folders[1] = "superior" + java.io.File.separator;
				}
				if (displayedCivilians[i]) {
					folders[2] = "civilian" + java.io.File.separator;
				}
				if (displayedFallen[i]) {
					folders[3] = "forsaken" + java.io.File.separator;
				}
				String type = "neutral";
				if (displayedEmotions[i] == Emotion.ANGER) {
					type = "anger";
				} else if (displayedEmotions[i] == Emotion.FEAR) {
					type = "fear";
				} else if (displayedEmotions[i] == Emotion.FOCUS) {
					type = "focus";
				} else if (displayedEmotions[i] == Emotion.JOY) {
					type = "joy";
				} else if (displayedEmotions[i] == Emotion.LEWD) {
					type = "lewd";
				} else if (displayedEmotions[i] == Emotion.NEUTRAL) {
					type = "neutral";
				} else if (displayedEmotions[i] == Emotion.SHAME) {
					type = "sadness";
				} else if (displayedEmotions[i] == Emotion.STRUGGLE) {
					type = "struggle";
				} else if (displayedEmotions[i] == Emotion.SWOON) {
					type = "swoon";
				}
				for (int j = 0; j < 16 && image == null && displayedNames[i] != null; j++) {
					String nav = "";
					if (folders[0].length() > 0 && j < 8) {
						nav = nav + folders[0];
					}
					if (folders[1].length() > 0 && j % 8 < 4) {
						nav = nav + folders[1];
					}
					if (folders[2].length() > 0 && j % 4 < 2) {
						nav = nav + folders[2];
					}
					if (folders[3].length() > 0 && j % 2 == 0) {
						nav = nav + folders[3];
					}
					try {
						image = ImageIO.read(new File(path + nav + type + ".png"));
					} catch (IOException ie) {
						try {
							image = ImageIO.read(new File(path + nav + type + ".jpg"));
						} catch (IOException ig) {
							try {
								image = ImageIO.read(new File(path + nav + type + ".gif"));
							} catch (IOException ih) {
								try {
									image = ImageIO.read(new File(path + nav + type + ".jpeg"));
								} catch (IOException ii) {
									
								}
							}
						}
					}
				}
				if (image == null) {
					try {
						image = ImageIO.read(new File(getFilePath() + java.io.File.separator + "portraits" + java.io.File.separator + "empty.png"));
					} catch (IOException ie) {
						w.portraits = false;
						clearPortraits();
					}
				}
				if (image != null) {
					Image resized = image.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
					JLabel picLabel = new JLabel(new ImageIcon(resized));
					portraits.add(picLabel);
				}
				image = null;
			}
			//int imageSize = (scrollPane.getHeight() - ((Integer)UIManager.get("ScrollBar.width")).intValue())/4;
			//portraitPane.setPreferredSize(new Dimension(imageSize, imageSize*4));
			//portraitPane.setPreferredSize(new Dimension(imageSize, imageSize*4));
			//scrollPane.setMaximumSize(new Dimension(window.getWidth()-imageSize, window.getHeight()-40));
			GridBagConstraints c = new GridBagConstraints();
			c.weighty = 0;
			c.weightx = 0;
			c.fill = GridBagConstraints.VERTICAL;
			c.ipadx = imageSize;
			nestedcp.add(portraitPane, c);
			nestedcp.validate();
			nestedcp.repaint();
		}
	}
	
	public static void IntroOne (JTextPane t, JPanel p, JFrame f, WorldState w) {
		w.setGenders(w.getGenderBalance());
		p.getInputMap().clear();
		p.getActionMap().clear();
		clearPortraits();
		if (w.portraits) {
			BufferedImage image = null;
			try {
				image = ImageIO.read(new File(getFilePath() + java.io.File.separator + "portraits" + java.io.File.separator + "empty.png"));
			} catch (IOException ie) {
				w.portraits = false;
				clearPortraits();
			}
		}
		if (t.getBackground().equals(w.BACKGROUND) == false) {
			w.toggleColors(t);
		}
		w.append(t, "Corrupted Saviors R24: Genetics: \"Enhancement\"\n\nThis game contains content of an adult nature and should not be played by the underaged or by those unable to distinguish fantasy from reality.\n\n" + w.getSeparator() + "\n\nJapan, mid-21st century.  The psychic energies of humanity have finally begun to coalesce into physical form.  The resulting beings are known as Demons.  Born from the base desires suppressed deep within the human mind, these creatures spread across the planet, leaving chaos and depravity in their wake.\n\nBut Demons do not represent the entirety of the human condition.  The hopes and determination of humanity have also risen up, gathering in the bodies of a few Chosen warriors in order to grant them the power to fight the Demons.  Although each of them was once an ordinary person, their new abilities place them at the center of the struggle for the soul of humanity.\n\nYou are a Demon Lord, the highest form of Demon, with your own mind and will, focused on the corruption of all that is good in the world.  The Chosen are the keystone of humanity's resistance to your goal, but to simply kill them would be meaningless.  Instead, shatter their notions of right and wrong, showing them the true darkness that hides within!");
		if (w.getCast()[0] == null) {
			Chosen newChosen = new Chosen();
			newChosen.setNumber(0);
			w.initialize();
			newChosen.generate(w);
			w.addChosen(newChosen);
		} else if (w.getCast()[0].getGender().equals(w.getGenders()[0]) == false) {
			w.getCast()[0] = null;
			Chosen newChosen = new Chosen();
			newChosen.setNumber(0);
			w.initialize();
			newChosen.generate(w);
			w.addChosen(newChosen);
		}
		p.removeAll();
		JButton NewGame = new JButton("Single Play");
		NewGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (w.getEarlyCheat()) {
					Shop(t, p, f, w);
				} else {
					w.active = true;
					IntroTwo(t, p, f, w);
				}
			}
		});
		p.add(NewGame);
		JButton Campaign = new JButton("Campaign");
		Campaign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldState x = new WorldState();
				x.copySettings(t, w);
				x.copyToggles(w);
				x.save = w.save;
				x.setGenders(x.getGenderBalance());
				x.active = true;
				x.campaign = true;
				x.cityName = x.getCityName(0);
				x.campaignRand = new Random();
				x.earlyCheat = false;
				x.hardMode = false;
				x.clampStart = 11;
				x.clampPercent = 100;
				x.eventOffset = 0;
				x.downtimeMultiplier = 100;
				Chosen newChosen = new Chosen();
				newChosen.setNumber(0);
				x.initialize();
				newChosen.generate(x);
				x.addChosen(newChosen);
				IntroTwo(t, p, f, x);
			}
		});
		p.add(Campaign);
		JButton LoadGame = new JButton("Load Game");
		LoadGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Data(t, p, f, w, "load", 0, false);
			}
		});
		p.add(LoadGame);
		JButton Import = new JButton("Import");
		Import.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Data(t, p, f, w, "import", 0, false);
			}
		});
		p.add(Import);
		JButton Tutorial = new JButton("Tutorial");
		Tutorial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WorldState x = new WorldState();
				x.copySettings(t, w);
				x.copyToggles(w);
				x.tutorialInit();
				x.save = w.save;
				BeginBattle(t, p, f, x, x.getCast()[0]);
				x.grayAppend(t, "\n\n(Welcome to the tutorial!  This feature is intended to demonstrate some useful techniques for corrupting the Chosen.  It uses a mid-game save file with several upgrades already purchased.  When playing from the start, it makes more sense to use the first several days experimenting to find the strengths and weaknesses of the Chosen and accumulating Evil Energy before aiming to break a vulnerability.  Read the guide.txt file included with the game for a more basic overview of the mechanics.\n\nFor now, let's start by using Examine to figure out how best to deal with Miracle.)");
			}
		});
		p.add(Tutorial);
		JButton Options = new JButton("Options");
		Options.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OptionsMenu(t, p, f, w, null);
			}
		});
		p.add(Options);
		JButton Customize = new JButton("Customize");
		Customize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.removeAll();
				WorldState x = new WorldState();
				x.copySettings(t, w);
				x.copyToggles(w);
				x.setGenders(w.getGenderBalance());
				x.save = w.save;
				x.active = true;
				x.freshCustom(t, p, f);
			}
		});
		p.add(Customize);
		if (w.save.harem == null) {
			w.save.harem = new Forsaken[0];
		}
		if (w.save.harem.length > 0 || w.getEarlyCheat()) {
			final SaveData fileUsed = w.save;
			JButton Forsaken = new JButton("Forsaken");
			Forsaken.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ForsakenMenu(t, p, f, w, fileUsed, 0);
				}
			});
			p.add(Forsaken);
		}
		if (w.save.sceneText == null) {
			w.save.organizeScenes(scenesThisVersion);
		}
		for (int i = 0; i < w.save.sceneText.length; i++) {
			if (w.save.sceneText[i].length > 0) {
				i = w.save.sceneText.length;
				JButton Scenes = new JButton("Scenes");
				Scenes.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SceneCompletion(t, p, f, w, w.save);
						SceneViewer(t, p, f, w, w.save, 0);
					}
				});
				p.add(Scenes);
			}
		}
		JButton About = new JButton("About");
		About.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\nCopyright 2019-2021 by CSdev. Corrupted Saviors is licensed under the Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/.\n\nDefault portrait set created by CSdev with the assistance of Artbreeder and dedicated to the public domain (CC0).  For more information, see https://creativecommons.org/publicdomain/zero/1.0/.\n\nIf you like this game, please share it and discuss it so that it can be further enjoyed and improved!  There is a good chance that the developer reads whatever forum you found it on.  Direct feedback can also be sent to corruptedsaviors@gmail.com\n\nNew versions are first posted to corruptedsaviors.blogspot.com\nThe developer's tip jar can be found at subscribestar.adult/csdev");
			}
		});
		p.add(About);
		p.validate();
		//f.pack();
		p.repaint();
	}
	
	public static void OptionsDisplay(JTextPane t, JPanel p, JFrame f, WorldState w, Boolean earlyCheatVisible) {
		t.setText("");
		if (earlyCheatVisible) {
			w.append(t, "Difficulty: ");
			if (w.getEarlyCheat()) {
				w.append(t, "EASY (cheats available from the start)");
			} else if (w.hardMode) {
				w.append(t, "HARD (shorter deadlines, Chosen take less damage as damage level goes up, cannot use Forsaken)");
			} else {
				w.append(t, "NORMAL");
			}
			w.append(t, "\n\n");
		}
		w.append(t, "Current background: ");
		if (t.getBackground().equals(Color.WHITE)) {
			w.append(t, "white");
		} else {
			w.append(t, "black");
		}
		w.append(t, "\n\nCommentary mode: ");
		if (w.getCommentaryRead()) {
			if (w.getCommentaryWrite()) {
				w.append(t, "Read/Write");
			} else {
				w.append(t, "Read");
			}
		} else {
			if (w.getCommentaryWrite()) {
				w.append(t, "Write");
			} else {
				w.append(t, "None");
			}
		}
		w.append(t, "\n\nText size: " + w.getTextSize());
		w.append(t, "\n\nEnemy composition: ");
		if (w.getGenderBalance()[0] == 0) {
			Boolean listed = false;
			if (w.getGenderBalance()[1] > 0) {
				listed = true;
				w.append(t, w.getGenderBalance()[1] + " female");
				if (w.getGenderBalance()[1] > 1) {
					w.append(t, "s");
				}
			}
			if (w.getGenderBalance()[2] > 0) {
				if (listed) {
					w.append(t, ", ");
				}
				w.append(t, w.getGenderBalance()[2] + " male");
				if (w.getGenderBalance()[2] > 1) {
					w.append(t, "s");
				}
				listed = true;
			}
			if (w.getGenderBalance()[3] > 0) {
				if (listed) {
					w.append(t, ", ");
				}
				w.append(t, w.getGenderBalance()[3] + " futanari");
				listed = true;
			}
			if (listed == false) {
				w.append(t, "none set");
			}
		} else {
			Boolean listed = false;
			int divisor = w.getGenderBalance()[1]+w.getGenderBalance()[2]+w.getGenderBalance()[3];
			if (divisor == 0) {
				divisor = 1;
			}
			int count = 0;
			for (int i = 1; i < 4; i++) {
				if (w.getGenderBalance()[i] > 0) {
					count++;
				}
			}
			int multiplier = 10000/divisor;
			if (w.getGenderBalance()[1] > 0) {
				listed = true;
				if (count > 1) {
					w.append(t, (multiplier*w.getGenderBalance()[1])/100 + "% female");
				} else {
					w.append(t, "100% female");
				}
			}
			if (w.getGenderBalance()[2] > 0) {
				if (listed) {
					w.append(t, ", ");
				}
				if (count > 1) {
					w.append(t, (multiplier*w.getGenderBalance()[2])/100 + "% male");
				} else {
					w.append(t, "100% male");
				}
				listed = true;
			}
			if (w.getGenderBalance()[3] > 0) {
				if (listed) {
					w.append(t, ", ");
				}
				if (count > 1) {
					w.append(t, (multiplier*w.getGenderBalance()[3])/100 + "% futanari");
				} else {
					w.append(t, "100% futanari");
				}
			}
		}
		if (w.getGenderBalance()[2] > 0) {
			w.append(t, "\n\nMales shift: ");
			if (w.getMaleShift() == 0) {
				w.append(t, "never");
			} else if (w.getMaleShift() == 1) {
				w.append(t, "to female when first inseminated");
			} else if (w.getMaleShift() == 2) {
				w.append(t, "to futanari when first inseminated");
			}
		}
		if (w.getGenderBalance()[1] > 0 || (w.getGenderBalance()[2] > 0 && w.getMaleShift() == 1)) {
			w.append(t, "\n\nFemales shift: ");
			if (w.getFemaleShift() == 0) {
				w.append(t, "never");
			} else {
				w.append(t, "to futanari when first using Fantasize");
			}
		}
		if (w.getMaleShift() > 0 || w.getFemaleShift() > 0) {
			w.append(t, "\n\nShifted Chosen can shift again: ");
			if (w.getRepeatShift()) {
				w.append(t, "yes");
			} else {
				w.append(t, "no");
			}
		}
		w.append(t, "\n\nGraphic violence: ");
		if (w.tickle()) {
			w.append(t, "OFF (replaced by tickling)");
		} else {
			w.append(t, "ON");
		}
		w.append(t, "\n\nPortraits: ");
		if (w.portraits) {
			w.append(t, "ON");
		} else {
			w.append(t, "OFF");
		}
		w.append(t, "\n\nPassage separator:\n" + w.getSeparator());
	}
	
	public static void OptionsMenu(JTextPane t, JPanel p, JFrame f, WorldState w, Boolean earlyCheatVisible) {
		p.removeAll();
		if (earlyCheatVisible == null) {
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
			File saveLocation = new File(path + java.io.File.separator + "saves.sav");
			SaveData saves = null;
			if (saveLocation.exists()) {
				ReadObject robj = new ReadObject();
				saves = robj.deserializeSaveData(path + java.io.File.separator + "saves.sav");
			} else {
				saves = new SaveData();
			}
			final SaveData saveFile = saves;
			for (int i = 0; i < saveFile.getSaves().length; i++) {
				if (saveFile.getSaves()[i].getDay() > 50 - saveFile.getSaves()[i].eventOffset * 3) {
					earlyCheatVisible = true;
				}
			}
			if (w.getEarlyCheat()) {
				earlyCheatVisible = true;
			}
			if (w.hardMode) {
				earlyCheatVisible = true;
			}
			if (saves.harem != null) {
				if (saves.harem.length > 0) {
					earlyCheatVisible = true;
				}
			}
			if (earlyCheatVisible == null) {
				earlyCheatVisible = false;
			}
		}
		final Boolean CheatVisibility = earlyCheatVisible;
		OptionsDisplay(t, p, f, w, earlyCheatVisible);
		JButton EarlyCheat = new JButton("Change Difficulty");
		EarlyCheat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (w.getEarlyCheat()) {
					w.setEarlyCheat(false);
					w.hardMode = true;
					w.clampStart = 1;
					w.clampPercent = 80;
					w.eventOffset = 5;
				} else if (w.hardMode) {
					w.hardMode = false;
					w.clampStart = 11;
					w.clampPercent = 100;
					w.eventOffset = 0;
				} else {
					w.setEarlyCheat(true);
				}
				OptionsMenu(t, p, f, w, null);
			}
		});
		if (earlyCheatVisible) {
			p.add(EarlyCheat);
		}
		class EarlyCheatAction extends AbstractAction {
			public void actionPerformed(ActionEvent e) {
				w.setEarlyCheat(true);
				OptionsMenu(t, p, f, w, true);
			}
		}
		Action EarlyCheatAssignment = new EarlyCheatAction();
		p.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0),"pressed");
		p.getActionMap().put("pressed",EarlyCheatAssignment);
		/*f.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				System.out.println("test");
				if (e.getKeyCode() == KeyEvent.VK_C) {
					w.setEarlyCheat(true);
					OptionsMenu(t, p, f, w);
				}
			}
			public void keyTyped(KeyEvent e) {
				
			}
			public void keyReleased(KeyEvent e) {
			
			}
		});*/
		JButton Invert = new JButton("Change Background");
		Invert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.toggleColors(t);
				OptionsMenu(t, p, f, w, CheatVisibility);
			}
		});
		p.add(Invert);
		JButton Commentary = new JButton("Change Commentary Mode");
		Commentary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (w.getCommentaryRead()) {
					if (w.getCommentaryWrite()) {
						w.setCommentaryWrite(false);
					} else {
						w.setCommentaryRead(false);
						w.setCommentaryWrite(true);
					}
				} else {
					if (w.getCommentaryWrite()) {
						w.setCommentaryWrite(false);
					} else {
						w.setCommentaryRead(true);
						w.setCommentaryWrite(true);
					}
				}
				OptionsMenu(t, p, f, w, CheatVisibility);
			}
		});
		p.add(Commentary);
		JButton TextSize = new JButton("Change Text Size");
		TextSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.switchTextSize();
				OptionsMenu(t, p, f, w, CheatVisibility);
			}
		});
		p.add(TextSize);
		JButton Content = new JButton("Content Options");
		Content.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ContentMenu(t, p, f, w, CheatVisibility);
			}
		});
		p.add(Content);
		JButton Portraits = new JButton("Toggle Portraits");
		Portraits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.portraits = (w.portraits == false);
				OptionsMenu(t, p, f, w, CheatVisibility);
			}
		});
		p.add(Portraits);
		JButton ChangeSeparator = new JButton("Change Separator");
		ChangeSeparator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter the text that will be used to separate passages.  Leave blank to use the default, '---'.");
				if (input == null) {
					w.setSeparator("---");
				} else if (input.length() == 0) {
					w.setSeparator("---");
				} else {
					w.setSeparator(input);
				}
				OptionsMenu(t, p, f, w, CheatVisibility);
			}
		});
		p.add(ChangeSeparator);
		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				t.setText("");
				IntroOne(t, p, f, w);
			}
		});
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void GenderMenu (JTextPane t, JPanel p, JFrame f, WorldState w, Boolean earlyCheatVisible) {
		p.removeAll();
		p.getInputMap().clear();
		p.getActionMap().clear();
		OptionsDisplay(t, p, f, w, earlyCheatVisible);
		JButton ToggleRandomness = new JButton("Randomize Composition");
		if (w.getGenderBalance()[0] == 1) {
			ToggleRandomness.setText("Fix Composition");
		}
		ToggleRandomness.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.toggleGenderRandomness();
				GenderMenu(t, p, f, w, earlyCheatVisible);
			}
		});
		p.add(ToggleRandomness);
		JButton FewerFemales = new JButton("Fewer Females");
		if (w.getGenderBalance()[1] > 0 && (w.getGenderBalance()[0] == 0 || w.getGenderBalance()[2] > 0 || w.getGenderBalance()[3] > 0)) {
			FewerFemales.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.decreaseGender(1);
					GenderMenu(t, p, f, w, earlyCheatVisible);
				}
			});
		} else {
			FewerFemales.setForeground(Color.GRAY);
		}
		p.add(FewerFemales);
		JButton MoreFemales = new JButton("More Females");
		if ((w.getGenderBalance()[0] == 1 && (w.getGenderBalance()[2] > 0 || w.getGenderBalance()[3] > 0)) || (w.getGenderBalance()[0] == 0 && w.getGenderBalance()[1] + w.getGenderBalance()[2] + w.getGenderBalance()[3] < 3)) {
			MoreFemales.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.increaseGender(1);
					GenderMenu(t, p, f, w, earlyCheatVisible);
				}
			});
		} else {
			MoreFemales.setForeground(Color.GRAY);
		}
		p.add(MoreFemales);
		JButton FewerMales = new JButton("Fewer Males");
		if (w.getGenderBalance()[2] > 0 && (w.getGenderBalance()[0] == 0 || w.getGenderBalance()[1] > 0 || w.getGenderBalance()[3] > 0)) {
			FewerMales.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.decreaseGender(2);
					GenderMenu(t, p, f, w, earlyCheatVisible);
				}
			});
		} else {
			FewerMales.setForeground(Color.GRAY);
		}
		p.add(FewerMales);
		JButton MoreMales = new JButton("More Males");
		if ((w.getGenderBalance()[0] == 1 && (w.getGenderBalance()[1] > 0 || w.getGenderBalance()[3] > 0)) || (w.getGenderBalance()[0] == 0 && w.getGenderBalance()[1] + w.getGenderBalance()[2] + w.getGenderBalance()[3] < 3)) {
			MoreMales.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.increaseGender(2);
					GenderMenu(t, p, f, w, earlyCheatVisible);
				}
			});
		} else {
			MoreMales.setForeground(Color.GRAY);
		}
		p.add(MoreMales);
		JButton FewerFuta = new JButton("Fewer Futanari");
		if (w.getGenderBalance()[3] > 0 && (w.getGenderBalance()[0] == 0 || w.getGenderBalance()[1] > 0 || w.getGenderBalance()[2] > 0)) {
			FewerFuta.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.decreaseGender(3);
					GenderMenu(t, p, f, w, earlyCheatVisible);
				}
			});
		} else {
			FewerFuta.setForeground(Color.GRAY);
		}
		p.add(FewerFuta);
		JButton MoreFuta = new JButton("More Futanari");
		if ((w.getGenderBalance()[0] == 1 && (w.getGenderBalance()[2] > 0 || w.getGenderBalance()[1] > 0)) || (w.getGenderBalance()[0] == 0 && w.getGenderBalance()[1] + w.getGenderBalance()[2] + w.getGenderBalance()[3] < 3)) {
			MoreFuta.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.increaseGender(3);
					GenderMenu(t, p, f, w, earlyCheatVisible);
				}
			});
		} else {
			MoreFuta.setForeground(Color.GRAY);
		}
		p.add(MoreFuta);
		JButton Back = new JButton("Back");
		if (w.getGenderBalance()[0] == 1 || w.getGenderBalance()[1] + w.getGenderBalance()[2] + w.getGenderBalance()[3] == 3) {
			Back.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ContentMenu(t, p, f, w, earlyCheatVisible);
				}
			});
		} else {
			Back.setForeground(Color.GRAY);
		}
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void ContentMenu(JTextPane t, JPanel p, JFrame f, WorldState w, Boolean earlyCheatVisible) {
		p.removeAll();
		p.getInputMap().clear();
		p.getActionMap().clear();
		OptionsDisplay(t, p, f, w, earlyCheatVisible);
		JButton Violence = new JButton("Toggle Violence");
		Violence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.toggleTickle();
				ContentMenu(t, p, f, w, earlyCheatVisible);
			}
		});
		p.add(Violence);
		JButton Genders = new JButton("Change Composition");
		Genders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GenderMenu(t, p, f, w, earlyCheatVisible);
			}
		});
		p.add(Genders);
		if (w.getGenderBalance()[2] > 0) {
			JButton MaleShift = new JButton("Toggle Male Shifting");
			MaleShift.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.changeMaleShift();
					ContentMenu(t, p, f, w, earlyCheatVisible);
				}
			});
			p.add(MaleShift);
		}
		if (w.getGenderBalance()[1] > 0 || (w.getGenderBalance()[2] > 0 && w.getMaleShift() == 1)) {
			JButton FemaleShift = new JButton("Toggle Female Shifting");
			FemaleShift.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.changeFemaleShift();
					ContentMenu(t, p, f, w, earlyCheatVisible);
				}
			});
			p.add(FemaleShift);
		}
		if (w.getMaleShift() > 0 || w.getFemaleShift() > 0) {
			JButton RepeatShift = new JButton("Toggle Repeated Shifting");
			RepeatShift.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.changeRepeatShift();
					ContentMenu(t, p, f, w, earlyCheatVisible);
				}
			});
			p.add(RepeatShift);
		}
		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OptionsMenu(t, p, f, w, earlyCheatVisible);
			}
		});
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void ForsakenMenu(JTextPane t, JPanel p, JFrame f, WorldState w, SaveData s, int page) {
		p.removeAll();
		if (page == 0) {
			if (w.active) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				if (w.usedForsaken != null) {
					w.append(t, w.usedForsaken.mainName + " is currently prepared to lead your forces into battle.  ");
				}
				if (w.loopComplete) {
					if (w.day <= 50 - 3*w.eventOffset) {
						w.append(t, "Select one of the Forsaken to see more information and management options, or select 'Pass Time' to move to the next day without doing any training.");
					} else {
						w.append(t, "Select one of the Forsaken to see more information and management options.");
					}
				} else {
					w.append(t, "Select one of the Forsaken to see more information and management options, or select 'Deploy' to use one as your Commander.");
				}
			} else {
				w.append(t, "\n\n" + w.getSeparator() + "\n\nWith which of your Forsaken would you like to interact?");
			}
		} else {
			Project.clearPortraits();
			JButton PreviousPage = new JButton("<");
			PreviousPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ForsakenMenu(t, p, f, w, s, page-1);
				}
			});
			p.add(PreviousPage);
		}
		clearPortraits();
		String[] nameDisplay = new String[5];
		for (int i = 0; i < 5; i++) {
			if (w.getHarem() != null) {
				if (w.getHarem().length > i + page*5) {
					Forsaken subject = w.getHarem()[i+(page*5)];
					subject.textSize = w.textSize;
					JButton ThisOne = new JButton(subject.mainName);
					ThisOne.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							ForsakenInteraction(t, p, f, w, s, subject);
						}
					});
					p.add(ThisOne);
					nameDisplay[i] = subject.mainName;
					if (subject.flavorObedience() < 20) {
						changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Project.Emotion.ANGER, Project.Emotion.NEUTRAL);
					} else if (subject.flavorObedience() < 40) {
						changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Project.Emotion.ANGER, Project.Emotion.SHAME);
					} else if (subject.flavorObedience() < 61) {
						changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Project.Emotion.FEAR, Project.Emotion.SHAME);
					} else if (subject.flavorObedience() < 81) {
						changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Project.Emotion.FOCUS, Project.Emotion.NEUTRAL);
					} else {
						changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Project.Emotion.JOY, Project.Emotion.FOCUS);
					}
				}
			}
		}
		if (w.getHarem().length > 5*(page+1)) {
			JButton NextPage = new JButton(">");
			NextPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ForsakenMenu(t, p, f, w, s, page+1);
				}
			});
			p.add(NextPage);
		}
		JButton NewForsaken = new JButton("(Generate Forsaken)");
		NewForsaken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WriteObject wobj = new WriteObject();
				WorldState dummy = new WorldState();
				dummy.copyToggles(w);
				dummy.copySettings(t, w);
				dummy.setGenders(w.getGenderBalance());
				Chosen newChosen = new Chosen();
				newChosen.setNumber(0);
				dummy.initialize();
				newChosen.generate(dummy);
				w.corruptColors(newChosen);
				int index = 0;
				if (s.harem == null) {
					s.harem = new Forsaken[1];
				} else {
					index = s.harem.length;
				}
				int lastPage = s.harem.length/5;
				Forsaken[] newHarem = new Forsaken[index+1];
				for (int j = 0; j < index; j++) {
					newHarem[j] = s.harem[j];
				}
				Forsaken newForsaken = new Forsaken();
				newForsaken.initialize(w, newChosen);
				newHarem[index] = newForsaken;
				newForsaken.forsakenID = s.assignID();
				s.harem = newHarem;
				wobj.serializeSaveData(s);
				ForsakenMenu(t, p, f, w, s, lastPage);
			}
		});
		if (w.campaign == false) {
			p.add(NewForsaken);
		}
		JButton PassTime = new JButton("Pass Time");
		PassTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (w.loopComplete) {
					p.removeAll();
					w.append(t, "\n\n" + w.getSeparator() + "\n\nDo you want to give your Forsaken a break and just move on to the next day?");
					JButton Confirm = new JButton("Confirm");
					Confirm.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							PostBattle(t, p, f, w);
						}
					});
					p.add(Confirm);
					JButton Cancel = new JButton("Cancel");
					Cancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							ForsakenMenu(t, p, f, w, s, page);
						}
					});
					p.add(Cancel);
					p.validate();
					p.repaint();
				} else {
					ForsakenChoice(t, p, f, w, s, 0);
				}
			}
		});
		if (w.active && w.loopComplete == false) {
			PassTime.setText("Deploy");
		}
		if (w.campaign == false || w.day <= 50 - w.eventOffset*3) {
			p.add(PassTime);
		}
		if (w.active && w.loopComplete == false) {
			JButton UseDemon = new JButton("Use Demon");
			UseDemon.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (w.usedForsaken != null) {
						w.evilEnergy += w.usedForsaken.EECost();
					}
					w.usedForsaken = null;
					Customize(t, p, f, w);
				}
			});
			p.add(UseDemon);
		}
		JButton Back = new JButton("Done");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (w.active) {
					Shop(t, p, f, w);
				} else {
					t.setText("");
					IntroOne(t, p, f, w);
				}
			}
		});
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void ForsakenChoice(JTextPane t, JPanel p, JFrame f, WorldState w, SaveData s, int page) {
		p.removeAll();
		if (page == 0) {
			if (w.active) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich of the Forsaken would you like to send into battle?");
			} else {
				w.append(t, "\n\n" + w.getSeparator() + "\n\nYou can spend one of the Forsaken's Stamina and Motivation (as would normally happen when sending them into battle), or you can simply pass time without spending anything by selecting 'None'.");
			}
		} else {
			JButton PreviousPage = new JButton("<");
			PreviousPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.append(t, "\n\n" + w.getSeparator());
					ForsakenChoice(t, p, f, w, s, page-1);
				}
			});
			p.add(PreviousPage);
		}
		clearPortraits();
		String[] nameDisplay = new String[5];
		for (int i = page*5; i < page*5+5; i++) {
			if (i < w.getHarem().length) {
				w.append(t, "\n\n" + w.getHarem()[i].mainName + "\nStamina: " + w.getHarem()[i].stamina/10 + "." + w.getHarem()[i].stamina%10 + "%\nMotivation: " + w.getHarem()[i].motivation/10 + "." + w.getHarem()[i].motivation%10 + "%\nCost: 20% Stamina, " + w.getHarem()[i].motivationCost()/10 + "." + w.getHarem()[i].motivationCost()%10 + "% Motivation, " + w.getHarem()[i].EECost() + " EE\n" + w.getHarem()[i].describeCombatStyle(w, false) + "\nReputation Strength: " + (200-w.getHarem()[i].disgrace*2) + "%\nTarget Compatibilities:");
				if (w.active) {
					for (int j = 0; j < 3; j++) {
						if (w.getCast()[j] != null) {
							w.append(t, "\n" + w.getCast()[j].getMainName() + " - ");
							int compatibility = w.getHarem()[i].compatibility(w.getCast()[j]);
							//Personal (8 rounds, 150% damage)
							if (compatibility >= 8) {
								w.append(t, "Excellent (8 rounds)");
							} else if (compatibility == 7) {
								w.append(t, "Good (7 rounds)");
							} else if (compatibility == 6) {
								w.append(t, "Average (6 rounds)");
							} else if (compatibility == 5) {
								w.append(t, "Poor (5 rounds)");
							} else {
								w.append(t, "Terrible (4 rounds)");
							}
						}
					}
				} else {
					w.append(t, "N/A");
				}
				JButton Choice = new JButton(w.getHarem()[i].mainName);
				Forsaken Spent = w.getHarem()[i];
				final int index = i;
				Choice.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (w.active) {
							if (w.usedForsaken != null) {
								w.evilEnergy += w.usedForsaken.EECost();
							}
							w.evilEnergy -= Spent.EECost();
							w.usedForsaken = Spent;
							w.usedForsakenIndex = index;
							ForsakenMenu(t, p, f, w, s, 0);
						} else {
							p.removeAll();
							Spent.stamina -= 200;
							Spent.motivation -= Spent.motivationCost();
							ForsakenDowntime(t, p, f, w, s, new Forsaken[]{Spent});
							JButton Continue = new JButton("Continue");
							Continue.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									ForsakenMenu(t, p, f, w, s, 0);
								}
							});
							p.add(Continue);
							p.validate();
							p.repaint();
						}
					}
				});
				int EEAvailable = w.evilEnergy;
				if (w.usedForsaken != null) {
					EEAvailable += w.usedForsaken.EECost();
				}
				if (Spent.stamina >= 200 && Spent.motivation >= Spent.motivationCost() && (w.active == false || EEAvailable >= Spent.EECost())) {
					p.add(Choice);
				}
				nameDisplay[i-page*5] = Spent.mainName;
				if (Spent.flavorObedience() < 20) {
					changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i-page*5, Project.Emotion.ANGER, Project.Emotion.NEUTRAL);
				} else if (Spent.flavorObedience() < 40) {
					changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i-page*5, Project.Emotion.ANGER, Project.Emotion.SHAME);
				} else if (Spent.flavorObedience() < 61) {
					changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i-page*5, Project.Emotion.FEAR, Project.Emotion.SHAME);
				} else if (Spent.flavorObedience() < 81) {
					changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i-page*5, Project.Emotion.FOCUS, Project.Emotion.NEUTRAL);
				} else {
					changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i-page*5, Project.Emotion.JOY, Project.Emotion.FOCUS);
				}
			}
		}
		if (w.getHarem().length > (page+1)*5) {
			JButton NextPage = new JButton(">");
			NextPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.append(t, "\n\n" + w.getSeparator());
					ForsakenChoice(t, p, f, w, s, page+1);
				}
			});
			p.add(NextPage);
		}
		JButton None = new JButton("None");
		None.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.removeAll();
				ForsakenDowntime(t, p, f, w, s, null);
				JButton Continue = new JButton("Continue");
				Continue.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ForsakenMenu(t, p, f, w, s, 0);
					}
				});
				p.add(Continue);
				p.validate();
				p.repaint();
			}
		});
		if (w.active == false) {
			p.add(None);
		}
		JButton Cancel = new JButton("Cancel");
		Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ForsakenMenu(t, p, f, w, s, 0);
			}
		});
		p.add(Cancel);
		p.validate();
		p.repaint();
	}
	
	public static void ForsakenInteraction(JTextPane t, JPanel p, JFrame f, WorldState w, SaveData s, Forsaken x) {
		p.removeAll();
		clearPortraits();
		String[] nameDisplay = new String[5];
		nameDisplay[0] = x.mainName;
		if (x.flavorObedience() < 20) {
			changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Project.Emotion.ANGER, Project.Emotion.NEUTRAL);
		} else if (x.flavorObedience() < 40) {
			changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Project.Emotion.ANGER, Project.Emotion.SHAME);
		} else if (x.flavorObedience() < 61) {
			changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Project.Emotion.FEAR, Project.Emotion.SHAME);
		} else if (x.flavorObedience() < 81) {
			changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Project.Emotion.FOCUS, Project.Emotion.NEUTRAL);
		} else {
			changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Project.Emotion.JOY, Project.Emotion.FOCUS);
		}
		w.append(t, "\n\n" + w.getSeparator() + "\n\n" + x.mainName);
		if (x.mainName.equals(x.originalName) == false) {
			w.append(t, " (formerly known as");
			if (x.adjectiveName.equals("none") == false) {
				w.append(t, " " + x.adjectiveName);
				if (x.nounName.length() > 0) {
					w.append(t, " " + x.nounName);
				}
			}
			w.append(t, " " + x.originalName + ")");
		}
		if (x.familyName.length() > 0) {
			if (x.mainName.equals(x.familyName) || x.mainName.equals(x.givenName) || x.originalName.equals(x.familyName) || x.originalName.equals(x.givenName)) {
				w.append(t, "\nFull name: ");
			} else {
				w.append(t, "\nReal name: ");
			}
			if (x.filthyGaijin) {
				w.append(t, x.givenName + " " + x.familyName);
			} else {
				w.append(t, x.familyName + " " + x.givenName);
			}
		} else if (x.givenName.equals(x.mainName) == false && x.givenName.equals(x.originalName) == false) {
			w.append(t, "\nReal name: " + x.givenName);
		}
		w.append(t, "\n\nStamina: " + x.stamina/10 + "." + x.stamina % 10 + "%\nMotivation: " + x.motivation/10 + "." + x.motivation % 10 + "%");
		w.append(t, "\n\nExpertise\nHATE: " + x.condensedFormat(x.hateExp) + " (x" + x.expMultiplierDisplay(x.hateExp) + " dmg)\nPLEA: " + x.condensedFormat(x.pleaExp) + " (x" + x.expMultiplierDisplay(x.pleaExp) + " dmg)");
		if (w.tickleOn) {
			w.append(t, "\nANTI: ");
		} else {
			w.append(t, "\nINJU: ");
		}
		w.append(t, x.condensedFormat(x.injuExp) + " (x" + x.expMultiplierDisplay(x.injuExp) + " dmg)\nEXPO: " + x.condensedFormat(x.expoExp) + " (x" + x.expMultiplierDisplay(x.expoExp) + " dmg)\n" + x.describeCombatStyle(w, true));
		if (x.defeatType == 5 && x.obedience < 40) {
			w.append(t, "\n\nTrait: Eager Partner\nWhile Obedience remains below 40%, 1/4 Motivation cost to deploy and +50% PLEA and EXPO damage");
		}
		if (x.type == Chosen.Species.SUPERIOR) {
			w.append(t, "\n\nTrait: Superior Forsaken\nx2 Motivation cost to deploy, +50% damage");
		}
		w.append(t, "\n\nOrgasms given: ");
		if (x.orgasmsGiven == 0) {
			w.append(t, "none");
		} else {
			w.append(t, x.orgasmsGiven + "");
		}
		w.append(t, "\nOrgasms experienced: ");
		if (x.timesOrgasmed == 0) {
			w.append(t, "none");
		} else {
			w.append(t, x.timesOrgasmed + "");
		}
		w.append(t, "\nLongest continuous orgasm: ");
		if (x.timesOrgasmed == 0) {
			w.append(t, "N/A");
		} else {
			if (x.strongestOrgasm < 600) {
				w.append(t, (x.strongestOrgasm / 10) + "." + (x.strongestOrgasm % 10) + " seconds");
			} else if (x.strongestOrgasm < 36000) {
				w.append(t, (x.strongestOrgasm / 600) + " minutes " + ((x.strongestOrgasm % 600)/10) + " seconds");
			} else {
				w.append(t, (x.strongestOrgasm / 36000) + " hours " + ((x.strongestOrgasm % 36000)/600) + " minutes");
			}
		}
		w.append(t, "\nSeen naked by: " + x.timesExposed + " people (" + x.timesExposedSelf + " with permission)");
		if (x.gender != Forsaken.Gender.MALE) {
			w.append(t, "\nTimes vaginally penetrated: " + x.timesHadSex);
			if (x.timesHadSex == 0) {
				w.append(t, " (virgin)");
			}
		}
		w.append(t, "\nTimes anally penetrated: ");
		int analCount = x.enjoyedAnal;
		if (x.gender == Forsaken.Gender.MALE) {
			analCount += x.timesHadSex;
		} else {
			analCount += x.timesTortured;
		}
		w.append(t, analCount + "");
		if (analCount == 0) {
			w.append(t, " (anal virgin)");
		}
		if (x.demonicBirths > 0) {
			w.append(t, "\nDemonic births: " + x.demonicBirths);
		} else {
			w.append(t, "\nDemonic births: 0");
		}
		w.append(t, "\n\nPeople injured: " + x.peopleInjured + "\nPeople killed: " + x.timesKilled + "\nSelf-harm incidents: " + x.timesHarmedSelf + "\n\nHostility: " + x.hostility + "% (");
		if (x.hostility < 20) {
			w.append(t, "Optimistic about humanity");
		} else if (x.hostility < 40) {
			w.append(t, "Ambivalent about humanity");
		} else if (x.hostility < 61) {
			w.append(t, "Pessimistic about humanity");
		} else if (x.hostility < 81) {
			w.append(t, "Hateful toward humanity itself");
		} else {
			w.append(t, "Desires the destruction of humanity");
		}
		w.append(t, ")\nDeviancy: " + x.deviancy + "% (");
		if (x.deviancy < 20) {
			w.append(t, "Little interest in sexuality");
		} else if (x.deviancy < 40) {
			w.append(t, "Elaborate sexual fantasies");
		} else if (x.deviancy < 61) {
			w.append(t, "Twisted sexual desires");
		} else if (x.deviancy < 81) {
			w.append(t, "Fetishizes aberrant actions");
		} else {
			w.append(t, "Seeks sexual pleasure regardless of situation");
		}
		w.append(t, ")\nObedience: " + x.obedience + "% (");
		if (x.defeatType == 5 && x.obedience < 40) {
			w.append(t, "Obeys due to expectation of rewards");
		} else if (x.obedience < 20) {
			w.append(t, "Reflexively disobeys");
		} else if (x.obedience < 40) {
			w.append(t, "Obeys when convenient");
		} else if (x.obedience < 61) {
			w.append(t, "Obeys out of fear");
		} else if (x.obedience < 81) {
			w.append(t, "Eagerly obeys");
		} else {
			w.append(t, "Unthinkingly obeys");
		}
		w.append(t, ")\nDisgrace: " + x.disgrace + "% (");
		if (x.disgrace < 20) {
			w.append(t, "Still somewhat respected");
		} else if (x.disgrace < 40) {
			w.append(t, "Humiliated");
		} else if (x.disgrace < 61) {
			w.append(t, "Object of base lust");
		} else if (x.disgrace < 81) {
			w.append(t, "Viewed with contempt");
		} else {
			w.append(t, "Considered powerless and worthless");
		}
		w.append(t, ")\n\nWhat would you like to speak to " + x.mainName + " about?");
		if (w.active && w.loopComplete == false) {
			w.append(t, "  Note that training " + x.himHer() + " will take the entire day.");
		}
		JButton Self = new JButton("Herself");
		if (x.gender == Forsaken.Gender.MALE) {
			Self.setText("Himself");
		}
		Self.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				x.selfTalk(t);
			}
		});
		p.add(Self);
		JButton Philosophy = new JButton("Philosophy");
		Philosophy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				x.philosophyTalk(t);
			}
		});
		p.add(Philosophy);
		JButton Training = new JButton("Training");
		Training.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				x.trainingTalk(t);
			}
		});
		//p.add(Training);
		JButton Life = new JButton("Everyday Life");
		Life.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				x.lifeTalk(t);
			}
		});
		//p.add(Life);
		WriteObject wobj = new WriteObject();
		JButton Others = new JButton("Other Forsaken");
		Others.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				x.othersTalk(w, t, s);
			}
		});
		if (w.getHarem().length > 1) {
			p.add(Others);
		}
		JButton ChangeName = new JButton("Change Name");
		ChangeName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("What alias will you give " + x.himHer() + "?");
				if (input != null) {
					if (input.length() > 0) {
						x.mainName = input;
						wobj.serializeSaveData(s);
					}
				}
				ForsakenInteraction(t, p, f, w, s, x);
			}
		});
		p.add(ChangeName);
		JButton ChangeTextColor = new JButton("Change Text Color");
		ChangeTextColor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int firstColor = -1;
				int secondColor = -1;
				int thirdColor = -1;
				firstColor = Integer.parseInt(JOptionPane.showInputDialog("Enter a value for red (0-255)."));
				secondColor = Integer.parseInt(JOptionPane.showInputDialog("Enter a value for green (0-255)."));
				thirdColor = Integer.parseInt(JOptionPane.showInputDialog("Enter a value for blue (0-255)."));
				if (firstColor >= 0 && firstColor <= 255 && secondColor >= 0 && secondColor <= 255 && thirdColor >= 0 && thirdColor <= 255) {
					p.removeAll();
					Color firstStorage = x.textColor;
					Color secondStorage = x.darkColor;
					x.textColor = new Color(firstColor, secondColor, thirdColor);
					x.darkColor = new Color(firstColor, secondColor, thirdColor);
					w.append(t, "\n\n" + w.getSeparator() + "\n\n");
					x.say(t, "\"" + x.mainName + " will now talk like this.\"");
					JButton Confirm = new JButton("Confirm");
					Confirm.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							wobj.serializeSaveData(s);
							ForsakenInteraction(t, p, f, w, s, x);
						}
					});
					p.add(Confirm);
					JButton Cancel = new JButton("Cancel");
					Cancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							x.textColor = firstStorage;
							x.darkColor = secondStorage;
							ForsakenInteraction(t, p, f, w, s, x);
						}
					});
					p.add(Cancel);
					p.validate();
					p.repaint();
				} else {
					w.append(t, "\n\n" + w.getSeparator() + "\n\nError: one or more invalid values.");
				}
			}
		});
		p.add(ChangeTextColor);
		JButton FreeTraining = new JButton("Free Training");
		FreeTraining.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Boolean[] newTraining = new Boolean[18];
				for (int i = 0; i < newTraining.length; i++) {
					newTraining[i] = false;
				}
				x.trainingMenu(t, p, f, w, s, newTraining, 0, true);
			}
		});
		if (w.active) {
			FreeTraining.setText("Training");
		}
		if (w.active == false || ((w.day != 50 - w.eventOffset*3 || w.loopComplete) && (w.day != 51 - w.eventOffset*3 || w.campaign == false))) {
			p.add(FreeTraining);
		}
		JButton Delete = new JButton("Delete");
		if (w.campaign) {
			Delete.setText("Sacrifice");
		}
		Delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.removeAll();
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				if (x.gender == Forsaken.Gender.MALE) {
					w.append(t, x.mainName + " will have " + x.hisHer() + " body modified into single-purpose breeding stock for the Demons, and you will never interact directly with " + x.himHer() + " again.  The terror of facing a similar fate will motivate any other Forsaken to obey you much more faithfully in the short-term.  Is this okay?");
				} else {
					w.append(t, x.mainName + " will spend the rest of " + x.hisHer() + " life as single-purpose breeding stock for the Demons, and you will never interact directly with " + x.himHer() + " again.  The terror of facing a similar fate will motivate any other Forsaken to obey you much more faithfully in the short-term.  Is this okay?");
				}
				JButton Confirm = new JButton("Confirm");
				Confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.removeAll();
						Forsaken[] newHarem = new Forsaken[w.getHarem().length-1];
						int removal = 0;
						for (int i = 0; i < w.getHarem().length; i++) {
							if (w.getHarem()[i] == x) {
								removal = i;
							} else {
								w.getHarem()[i].motivation = 1000;
							}
						}
						for (int i = 0; i < newHarem.length; i++) {
							if (i < removal) {
								newHarem[i] = w.getHarem()[i];
							} else {
								newHarem[i] = w.getHarem()[i+1];
							}
						}
						if (w.campaign) {
							Forsaken[] newSacrificed = new Forsaken[w.sacrificed.length+1];
							for (int i = 0; i < w.sacrificed.length; i++) {
								newSacrificed[i] = w.sacrificed[i];
							}
							newSacrificed[newSacrificed.length-1] = w.conquered[removal];
							w.conquered = newHarem;
							w.sacrificed = newSacrificed;
						} else {
							s.harem = newHarem;
						}
						wobj.serializeSaveData(s);
						x.describeSacrifice(t, w);
						JButton Continue = new JButton("Continue");
						Continue.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								ForsakenMenu(t, p, f, w, s, 0);
							}
						});
						p.add(Continue);
						p.validate();
						p.repaint();
					}
				});
				p.add(Confirm);
				JButton Cancel = new JButton("Cancel");
				Cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ForsakenInteraction(t, p, f, w, s, x);
					}
				});
				p.add(Cancel);
				p.validate();
				p.repaint();
			}
		});
		p.add(Delete);
		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ForsakenMenu(t, p, f, w, s, 0);
			}
		});
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void SceneCompletion(JTextPane t, JPanel p, JFrame f, WorldState w, SaveData s) {
		w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		w.underlineAppend(t, "Scenes Recorded");
		w.append(t, "\n");
		int types = 0;
		for (int i = 5; i < 21; i++) {
			if (s.sceneText[i].length > 0) {
				types++;
			}
		}
		w.append(t, "Core Vulnerability Break: " + types + "/20\n");
		w.append(t, "Core Vulnerability Distortions: ");
		if (s.sceneText[21].length > 0) {
			w.append(t, "1");
		} else {
			w.append(t, "0");
		}
		w.append(t, "/1\n");
		types = 0;
		for (int i = 33; i < scenesThisVersion; i++) {
			if (s.sceneText[i].length > 0) {
				types++;
			}
		}
		w.append(t, "Daily Vignettes: " + types + "/" + vignettesThisVersion);
		w.append(t, "\n\nWhich type of scene would you like to view?");
	}
	
	public static void SceneViewer (JTextPane t, JPanel p, JFrame f, WorldState w, SaveData s, int starting) {
		p.removeAll();
		int found = 0;
		int highest = 0;
		for (int i = starting - 1; i >= 0 && found < 5; i--) {
			if (s.sceneText[i].length > 0) {
				found++;
				highest = i;
			}
		}
		if (found > 0) {
			int newStartPoint = highest;
			JButton Previous = new JButton("<");
			Previous.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SceneViewer(t, p, f, w, s, newStartPoint);
				}
			});
			p.add(Previous);
		}
		found = 0;
		highest = 0;
		for (int i = starting; i < s.sceneText.length && found < 5; i++) {
			if (s.sceneText[i].length > 0) {
				String sceneName = "";
				found++;
				highest = i;
				if (i == 0) {
					sceneName = "First Meeting";
				} else if (i == 1) {
					sceneName = "Interview";
				} else if (i == 2) {
					sceneName = "Vacation";
				} else if (i == 3) {
					sceneName = "Final Preparation";
				} else if (i == 4) {
					sceneName = "Epilogue";
				} else if (i == 5) {
					sceneName = "First 'Violence'";
				} else if (i == 6) {
					sceneName = "First 'Service'";
				} else if (i == 7) {
					sceneName = "First 'Begging'";
				} else if (i == 8) {
					sceneName = "First 'Covering'";
				} else if (i == 9) {
					sceneName = "First 'Insemination'";
				} else if (i == 10) {
					sceneName = "First 'Force Orgasm'";
				} else if (i == 11) {
					sceneName = "First 'Sodomize/Torture/Force Laughter'";
				} else if (i == 12) {
					sceneName = "First 'Broadcast'";
				} else if (i == 13) {
					sceneName = "First 'Slaughter'";
				} else if (i == 14) {
					sceneName = "First 'Fantasize'";
				} else if (i == 15) {
					sceneName = "First 'Detonate'";
				} else if (i == 16) {
					sceneName = "First 'Striptease'";
				} else if (i == 17) {
					sceneName = "First 'Impregnation'";
				} else if (i == 18) {
					sceneName = "First 'Hypnotism'";
				} else if (i == 19) {
					sceneName = "First 'Drain'";
				} else if (i == 20) {
					sceneName = "First 'Parasitism'";
				} else if (i == 21) {
					sceneName = "First 'Tempt'";
				} else if (i == 33) {
					sceneName = "Perverted Donor";
				} else if (i == 34) {
					sceneName = "Sexual Technique Training";
				} else if (i == 35) {
					sceneName = "Blackmailed";
				} else if (i == 36) {
					sceneName = "Bodypaint Experiment";
				} else if (i == 37) {
					sceneName = "Photoshoot";
				} else if (i == 38) {
					sceneName = "Stripped in Public";
				} else if (i == 39) {
					sceneName = "Movie Date";
				} else if (i == 40) {
					sceneName = "Petplay";
				} else if (i == 41) {
					sceneName = "Train Molester";
				} else if (i == 42) {
					sceneName = "Sexual Combat Training";
				} else if (i == 43) {
					sceneName = "Guilty Service";
				} else if (i == 44) {
					sceneName = "Sleep Molester";
				} else if (i == 45) {
					sceneName = "Saving One's Rival";
				} else if (i == 46) {
					sceneName = "Service Competition";
				} else if (i == 47) {
					sceneName = "Relief Through Abuse";
				}
				JButton PickScene = new JButton(sceneName);
				int sceneType = i;
				PickScene.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SceneChoice(t, p, f, w, s, sceneType, starting, 0);
					}
				});
				p.add(PickScene);
			}
		}
		for (int i = highest+1; i < s.sceneText.length; i++) {
			if (s.sceneText[i].length > 0) {
				JButton Next = new JButton(">");
				int newStartPoint = i;
				Next.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SceneViewer(t, p, f, w, s, newStartPoint);
					}
				});
				p.add(Next);
				i = s.sceneText.length;
			}
		}
		JButton Back = new JButton("Done");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				t.setText("");
				IntroOne(t, p, f, w);
			}
		});
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void SceneChoice(JTextPane t, JPanel p, JFrame f, WorldState w, SaveData s, int type, int starting, int page) {
		p.removeAll();
		w.append(t, "\n\n" + w.getSeparator() + "\n\nWhose scene would you like to replay?\n");
		if (page > 0) {
			JButton Previous = new JButton("<");
			Previous.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int newPage = page-5;
					if (newPage < 0) {
						newPage = 0;
					}
					SceneChoice(t, p, f, w, s, type, starting, newPage);
				}
			});
			p.add(Previous);
		}
		for (int i = page; i < page+5 && i < s.sceneText[type].length; i++) {
			w.append(t, "\n" + s.sceneButtons[type][i] + ": " + s.sceneSummaries[type][i]);
			JButton PickScene = new JButton(s.sceneButtons[type][i]);
			int sceneID = i;
			PickScene.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ReplayScene(t, p, f, w, s, type, starting, page, sceneID);
				}
			});
			p.add(PickScene);
		}
		if (s.sceneText[type].length > page+5) {
			JButton Next = new JButton(">");
			Next.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SceneChoice(t, p, f, w, s, type, starting, page+5);
				}
			});
			p.add(Next);
		}
		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SceneCompletion(t, p, f, w, s);
				SceneViewer(t, p, f, w, s, starting);
			}
		});
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void ReplayScene(JTextPane t, JPanel p, JFrame f, WorldState w, SaveData s, int type, int starting, int page, int entry) {
		p.removeAll();
		w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		String[] shownFaces = new String[5];
		clearPortraits();
		for (int i = 0; i < 5; i++) {
			if (s.sceneEmotions[type][entry][i] != null) {
				shownFaces[i] = s.sceneFaces[type][entry][i];
				changePortrait(s.sceneGenders[type][entry][i], s.sceneSpecs[type][entry][i], s.sceneCivs[type][entry][i], s.sceneFallen[type][entry][i], w, shownFaces, i, s.sceneEmotions[type][entry][i], s.sceneEmotions[type][entry][i]);
			}
		}
		for (int i = 0; i < s.sceneText[type][entry].length; i++) {
			w.flexibleAppend(t, s.sceneText[type][entry][i], s.sceneColor[type][entry][i], s.sceneUnderline[type][entry][i]);
		}
		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SceneChoice(t, p, f, w, s, type, starting, page);
			}
		});
		p.add(Back);
		JButton Delete = new JButton("Delete");
		Delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.removeAll();
				w.append(t, "\n\n" + w.getSeparator() + "\n\nReally delete this scene?");
				JButton ReallyDelete = new JButton("Delete");
				ReallyDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String[][] newSceneText = new String[s.sceneText[type].length-1][0];
						Color[][] newSceneColor = new Color[s.sceneColor[type].length-1][0];
						Boolean[][] newSceneUnderline = new Boolean[s.sceneUnderline[type].length-1][0];
						String[] newSceneButtons = new String[s.sceneButtons[type].length-1];
						String[] newSceneSummaries = new String[s.sceneSummaries[type].length-1];
						Emotion[][] newSceneEmotions = new Emotion[s.sceneEmotions[type].length-1][5];
						String[][] newSceneFaces = new String[s.sceneFaces[type].length-1][5];
						Chosen.Species[][] newSceneSpecs = new Chosen.Species[s.sceneSpecs[type].length-1][5];
						Boolean[][] newSceneCivs = new Boolean[s.sceneCivs[type].length-1][5];
						Boolean[][] newSceneFallen = new Boolean[s.sceneFallen[type].length-1][5];
						Forsaken.Gender[][] newSceneGenders = new Forsaken.Gender[s.sceneGenders[type].length-1][5];
						for (int i = 0; i < s.sceneText[type].length-1; i++) {
							int editedEntry = i;
							if (i >= entry) {
								editedEntry++;
							}
							newSceneText[i] = s.sceneText[type][editedEntry];
							newSceneColor[i] = s.sceneColor[type][editedEntry];
							newSceneUnderline[i] = s.sceneUnderline[type][editedEntry];
							newSceneButtons[i] = s.sceneButtons[type][editedEntry];
							newSceneSummaries[i] = s.sceneSummaries[type][editedEntry];
							newSceneEmotions[i] = s.sceneEmotions[type][editedEntry];
							newSceneFaces[i] = s.sceneFaces[type][editedEntry];
							newSceneSpecs[i] = s.sceneSpecs[type][editedEntry];
							newSceneCivs[i] = s.sceneCivs[type][editedEntry];
							newSceneFallen[i] = s.sceneFallen[type][editedEntry];
							newSceneGenders[i] = s.sceneGenders[type][editedEntry];
						}
						s.sceneText[type] = newSceneText;
						s.sceneColor[type] = newSceneColor;
						s.sceneUnderline[type] = newSceneUnderline;
						s.sceneButtons[type] = newSceneButtons;
						s.sceneSummaries[type] = newSceneSummaries;
						s.sceneEmotions[type] = newSceneEmotions;
						s.sceneFaces[type] = newSceneFaces;
						s.sceneSpecs[type] = newSceneSpecs;
						s.sceneCivs[type] = newSceneCivs;
						s.sceneFallen[type] = newSceneFallen;
						s.sceneGenders[type] = newSceneGenders;
						WriteObject wobj = new WriteObject();
						wobj.serializeSaveData(s);
						if (s.sceneText[type].length > 0) {
							SceneChoice(t, p, f, w, s, type, starting, 0);
						} else {
							SceneCompletion(t, p, f, w, s);
							SceneViewer(t, p, f, w, s, 0);
						}
					}
				});
				p.add(ReallyDelete);
				JButton Return = new JButton("Return to Scene Choice");
				Return.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						SceneChoice(t, p, f, w, s, type, starting, page);
					}
				});
				p.add(Return);
				p.validate();
				p.repaint();
			}
		});
		p.add(Delete);
		p.validate();
		p.repaint();
	}
	
	public static void IntroTwo (JTextPane t, JPanel p, JFrame f, WorldState w) {
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
		File saveLocation = new File(path + java.io.File.separator + "saves.sav");
		if (saveLocation.exists()) {
			ReadObject robj = new ReadObject();
			w.save = robj.deserializeSaveData(path + java.io.File.separator + "saves.sav");
			if (w.save.sceneText == null) {
				w.save.organizeScenes(scenesThisVersion);
			} else if (w.save.sceneText.length < scenesThisVersion) {
				w.save.organizeScenes(scenesThisVersion);
			}
		} else {
			w.save = new SaveData();
			if (w.save.sceneText == null) {
				w.save.organizeScenes(scenesThisVersion);
			} else if (w.save.sceneText.length < scenesThisVersion) {
				w.save.organizeScenes(scenesThisVersion);
			}
		}
		//^^^seems to prevent pointer fuckery by ensuring that each save file refers to saved version rather than current one
		w.getCast()[0].world = w;
		String city = "the capital city";
		if (w.campaign) {
			city = w.cityName;
		}
		w.append(t, "\n\n" + w.getSeparator() + "\n\nThe peaceful everyday routine of " + city + " is instantly shattered as a horde of Demons and their dominated human Thralls spills out onto the street!  Screams and alarms fill the air, chaos descending on the scene in an instant.  Already, innocents are being mobbed and dragged away towards a terrible fate.\n\nJust then, a sound like a thunderclap cuts through the panic, and a voice calls out a challenge to the Demons below!\n\n");
		w.getCast()[0].say(t, "\"" + w.getCast()[0].announcement() + "\"\n\n");
		w.getCast()[0].transform(t, w);
		w.newCombat(w, w.getCast());
		w.append(t, "\n");
		PickTarget(t, p, f, w);
	}
	
	public static void PickTarget (JTextPane t, JPanel p, JFrame f, WorldState w) {
		Color YELLOWISH = new Color(255,225,125);
		Color PURPLISH = new Color(225, 125, 255);
		int inseminated = 0;
		int orgasming = 0;
		int sodomized = 0;
		int broadcasted = 0;
		p.removeAll();
		Chosen[] initiative = new Chosen[3];
		if (w.getCombatants()[2] != null) {
			if (w.getCombatants()[0].getConfidence() > w.getCombatants()[1].getConfidence()) {
				if (w.getCombatants()[0].getConfidence() > w.getCombatants()[2].getConfidence()) {
					if (w.getCombatants()[1].getConfidence() > w.getCombatants()[2].getConfidence()) {
						initiative[0] = w.getCombatants()[0];
						initiative[1] = w.getCombatants()[1];
						initiative[2] = w.getCombatants()[2];
					} else {
						initiative[0] = w.getCombatants()[0];
						initiative[1] = w.getCombatants()[2];
						initiative[2] = w.getCombatants()[1];
					}
				} else {
					initiative[0] = w.getCombatants()[2];
					initiative[1] = w.getCombatants()[0];
					initiative[2] = w.getCombatants()[1];
				}
			} else {
				if (w.getCombatants()[0].getConfidence() > w.getCombatants()[2].getConfidence()) {
					initiative[0] = w.getCombatants()[1];
					initiative[1] = w.getCombatants()[0];
					initiative[2] = w.getCombatants()[2];
				} else {
					if (w.getCombatants()[1].getConfidence() > w.getCombatants()[2].getConfidence()) {
						initiative[0] = w.getCombatants()[1];
						initiative[1] = w.getCombatants()[2];
						initiative[2] = w.getCombatants()[0];
					} else {
						initiative[0] = w.getCombatants()[2];
						initiative[1] = w.getCombatants()[1];
						initiative[2] = w.getCombatants()[0];
					}
				}
			}
		} else if (w.getCombatants()[1] != null) {
			if (w.getCombatants()[0].getConfidence() > w.getCombatants()[1].getConfidence()) {
				initiative[0] = w.getCombatants()[0];
				initiative[1] = w.getCombatants()[1];
			} else {
				initiative[0] = w.getCombatants()[1];
				initiative[1] = w.getCombatants()[0];
			}
		} else {
			initiative[0] = w.getCombatants()[0];
		}
		for (int i = 0; i < w.getCombatants().length; i++) {
			if (w.getCombatants()[i] != null) {
				if (w.getCombatants()[i].isInseminated()) {
					inseminated++;
				} else if (w.getCombatants()[i].isOrgasming()) {
					orgasming++;
				} else if (w.getCombatants()[i].isSodomized()) {
					sodomized++;
				} else if (w.getCombatants()[i].isBroadcasted()) {
					broadcasted++;
				}
			}
		}
		w.append(t, "\nRound " + w.battleRound + "\n");
		if (w.evacNotice) {
			w.append(t, "Evacuation complete!\n");
			w.evacNotice = false;
		} else {
			w.append(t, "Evacuation: " + w.getEvacStatus(true) + "\n");
		}
		Chosen trappedChosen = null;
		for (int i = 0; i < 3; i++) {
			if (w.getCombatants()[i] != null) {
				w.getCombatants()[i].updateSurround();
				if (w.getCombatants()[i].isSurrounded() == false && w.getCombatants()[i].isCaptured() == false) {
					
				} else {
					trappedChosen = w.getCombatants()[i];
				}
			}
		}
		w.append(t, "Extermination: " + w.getExterminationStatus(true) + "\n\n");
		if (w.evacuationProgress < w.evacuationComplete) {
			w.append(t, "The desperate battle continues...\n");
		} else {
			Chosen c = null;
			Boolean allGrabbed = true;
			if (w.getCombatants()[0] != null) {
				if (w.getCombatants()[0].isSurrounded() || w.getCombatants()[0].isCaptured() || (w.finalBattle && (w.getCombatants()[0].alive == false || w.getCombatants()[0].resolve <= 0))) {
					if (w.getCombatants()[1] != null) {
						if (w.getCombatants()[1].isSurrounded() || w.getCombatants()[1].isCaptured() || (w.finalBattle && (w.getCombatants()[1].alive == false || w.getCombatants()[1].resolve <= 0))) {
							if (w.getCombatants()[2] != null) {
								if (w.getCombatants()[2].isSurrounded() == false && w.getCombatants()[2].isCaptured() == false && (w.finalBattle == false || (w.getCombatants()[2].alive && w.getCombatants()[2].resolve > 0))) {
									allGrabbed = false;
								}
							}
						} else {
							allGrabbed = false;
						}
					}
				} else {
					allGrabbed = false;
				}
			}
			if (allGrabbed) {
				w.append(t, "The Demons have the Chosen at their mercy!\n");
			} else if (w.exterminationProgress >= w.exterminationComplete) {
				Boolean allFree = true;
				if (w.getCombatants()[0].isSurrounded() || w.getCombatants()[0].isCaptured()) {
					allFree = false;
				} else if (w.getCombatants()[1] != null) {
					if (w.getCombatants()[1].isSurrounded() || w.getCombatants()[1].isCaptured()) {
						allFree = false;
					} else if (w.getCombatants()[2] != null) {
						if (w.getCombatants()[2].isSurrounded() || w.getCombatants()[2].isCaptured()) {
							allFree = false;
						}
					}
				}
				if (allFree) {
					int defeated = 0;
					Chosen survivor = null;
					for (int i = 0; i < 3; i++) {
						if (w.finalBattle) {
							if (w.getCast()[i].alive == false || w.getCast()[i].resolve <= 0) {
								defeated++;
							} else {
								survivor = w.getCast()[i];
							}
						}
					}
					if (defeated == 2 && w.finalBattle) {
						w.append(t, "With " + survivor.hisHer() + " allies defeated and no hope of winning on " + survivor.hisHer() + " own, " + survivor.getMainName() + " is preparing to make use of the hole in the Demons' formation to escape!  " + survivor.HeShe() + "'ll get away next turn unless " + survivor.heShe() + "'s surrounded or captured.\n");
					} else {
						w.append(t, "The reanimated Demons are fighting their last stand!  Combat will end next turn unless one of the Chosen is surrounded or captured.\n");
					}
				} else if (w.finalBattle) {
					Chosen killer1 = null;
					Chosen killer2 = null;
					Chosen victim1 = null;
					Chosen victim2 = null;
					for (int i = 0; i < 3; i++) {
						if (w.getCast()[i].isSurrounded() || w.getCast()[i].isCaptured()) {
							if (victim1 == null) {
								victim1 = w.getCast()[i];
							} else {
								victim2 = w.getCast()[i];
							}
						} else if (w.getCast()[i].alive && w.getCast()[i].resolve > 0) {
							if (killer1 == null) {
								killer1 = w.getCast()[i];
							} else {
								killer2 = w.getCast()[i];
							}
						}
					}
					int duration1 = 0;
					if (victim1.isSurrounded()) {
						duration1 = victim1.getSurroundDuration();
					} else {
						duration1 = w.captureDuration - victim1.captureProgression;
						if (victim1.timesDetonated() > 0) {
							duration1 -= victim1.getINJULevel();
						}
					}
					int duration2 = 0;
					if (victim2 != null) {
						if (victim2.isSurrounded()) {
							duration2 = victim2.getSurroundDuration();
						} else {
							duration2 = w.captureDuration - victim1.captureProgression;
							if (victim2.timesDetonated() > 0) {
								duration2 -= victim2.getINJULevel();
							}
						}
					}
					if (duration1 < 2 && duration2 < 2) {
						if (victim2 == null) {
							if (killer2 == null) {
								w.append(t, killer1.getMainName() + " waits for " + victim1.getMainName() + "'s imminent escape so that the two of them can work together to end this.\n");
							} else {
								w.append(t, killer1.getMainName() + " and " + killer2.getMainName() + " wait for " + victim1.getMainName() + " to escape and rejoin their formation.\n");
							}
						} else {
							w.append(t, killer1.getMainName() + " waits for " + victim1.getMainName() + " and " + victim2.getMainName() + " to escape and form up so they can all work together to end this.\n");
						}
					} else {
						if (victim2 != null) {
							if (duration2 < 2) {
								victim2 = null;
							}
						}
						if (victim2 != null) {
							if (duration1 < 2) {
								victim1 = victim2;
								duration1 = duration2;
								victim2 = null;
							}
						}
						if (victim2 != null) {
							if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == -4 || victim1.isImpregnated() || victim1.isHypnotized() || victim1.isDrained() || victim1.isParasitized() || victim1.temptReq < 100000 || victim1.resolve < 50) {
								if (w.getRelationship(killer1.getNumber(), victim2.getNumber()) == -4 || victim2.isImpregnated() || victim2.isHypnotized() || victim2.isDrained() || victim2.isParasitized() || victim2.temptReq < 100000 || victim2.resolve < 50) {
									if (w.getTechs()[40].isOwned() && killer1.hesitated == false && (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4 || w.getRelationship(killer1.getNumber(), victim2.getNumber()) == 4)) {
										if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4) {
											if (w.getRelationship(killer1.getNumber(), victim2.getNumber()) == 4) {
												w.append(t, killer1.getMainName() + " calls out to the other Chosen, urging them to escape before they get caught up in " + killer1.hisHer() + " final attack.");
											} else {
												w.append(t, killer1.getMainName() + " prepares to launch a devastating attack in order to finish the battle, even though " + victim2.getMainName() + " is in the way.");
											}
										} else {
											w.append(t, killer1.getMainName() + " prepares to launch a devastating attack in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
										}
									} else {
										w.append(t, killer1.getMainName() + " prepares to launch a devastating attack in order to finish the battle, even though " + victim1.getMainName() + " and " + victim2.getMainName() + " are in the way.");
									}
								} else if (duration2 > duration1) {
									w.append(t, killer1.getMainName() + " buys time for " + victim2.getMainName() + " to escape so that the two of them can work together to end this.");
								} else {
									if (w.getTechs()[40].isOwned() && killer1.hesitated == false && w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4) {
										w.append(t, killer1.getMainName() + " calls out to " + victim1.getMainName() + ", urging " + victim1.himHer() + " to escape before " + victim1.heShe() + " gets caught up in " + killer1.getMainName() + "'s final attack.");
									} else {
										w.append(t, killer1.getMainName() + " prepares to launch a devastating attack so that the battle can be finished after " + victim2.getMainName() + " escapes, even though " + victim1.getMainName() + " is in the way.");
									}
								}
							} else if (w.getRelationship(killer1.getNumber(), victim2.getNumber()) == -4 || victim2.isImpregnated() || victim2.isHypnotized() || victim2.isDrained() || victim2.isParasitized() || victim2.temptReq < 100000 || victim2.resolve < 50) {
								if (duration1 > duration2) {
									w.append(t, killer1.getMainName() + " buys time for " + victim1.getMainName() + " to escape so that the two of them can work together to end this.");
								} else if (w.getTechs()[40].isOwned() && killer1.hesitated == false && w.getRelationship(killer1.getNumber(), victim2.getNumber()) == 4) {
									w.append(t, killer1.getMainName() + " calls out to " + victim2.getMainName() + ", urging " + victim2.himHer() + " to escape before " + victim2.heShe() + " gets caught up in " + killer1.getMainName() + "'s final attack.");
								} else {
									w.append(t, killer1.getMainName() + " prepares to launch a devastating attack in order to finish the battle, even though " + victim2.getMainName() + " is in the way.");
								}
							} else {
								w.append(t, killer1.getMainName() + " buys time for the other two Chosen to escape so that they all can work together to end this.");
							}
						} else if (killer2 != null) {
							if (victim1.isImpregnated() || victim1.isHypnotized() || victim1.isDrained() || victim1.isParasitized() || victim1.temptReq < 100000 || victim1.resolve < 50) {
								if (w.getTechs()[40].isOwned()) {
									if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4 && killer1.hesitated == false) {
										if (w.getRelationship(killer2.getNumber(), victim1.getNumber()) == 4 && killer2.hesitated == false) {
											w.append(t, killer1.getMainName() + " and " + killer2.getMainName() + " call out to " + victim1.getMainName() + ", urging " + victim1.himHer() + " to escape before " + victim1.heShe() + " gets caught up in their final attack.");
										} else {
											w.append(t, victim1.getMainName() + "'s captivity is preventing the other Chosen from ending the battle, and while " + killer1.getMainName() + " looks conflicted, " + killer2.getMainName() + " is preparing to attack anyway.");
										}
									} else if (w.getRelationship(killer2.getNumber(), victim1.getNumber()) == 4 && killer2.hesitated == false) {
										w.append(t, victim1.getMainName() + "'s captivity is preventing the other Chosen from ending the battle, and while " + killer2.getMainName() + " looks conflicted, " + killer1.getMainName() + " is preparing to attack anyway.");
									} else {
										w.append(t, killer1.getMainName() + " and " + killer2.getMainName() + " prepare to launch their most devastating attacks in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
									}
								} else {
									w.append(t, killer1.getMainName() + " and " + killer2.getMainName() + " prepare to launch their most devastating attacks in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
								}
							} else if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == -4) {
								if (w.getRelationship(killer2.getNumber(), victim1.getNumber()) == -4) {
									w.append(t, killer1.getMainName() + " and " + killer2.getMainName() + " prepare to launch their most devastating attacks in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
								} else {
									w.append(t, victim1.getMainName() + "'s captivity is preventing the other Chosen from ending the battle, and while " + killer2.getMainName() + " isn't willing to sacrifice " + victim1.getMainName() + "'s life in order to finish things sooner, " + killer1.getMainName() + " is.");
								}
							} else if (w.getRelationship(killer2.getNumber(), victim1.getNumber()) == -4) {
								w.append(t, victim1.getMainName() + "'s captivity is preventing the other Chosen from ending the battle, and while " + killer1.getMainName() + " isn't willing to sacrifice " + victim1.getMainName() + "'s life in order to finish things sooner, " + killer2.getMainName() + " is.");
							} else {
								w.append(t, victim1.getMainName() + "'s captivity is preventing the other Chosen from ending the battle, but " + killer1.getMainName() + " and " + killer2.getMainName() + " aren't willing to sacrifice " + victim1.hisHer() + " life just to finish things a little bit sooner.");
							}
						} else {
							if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == -4 || victim1.isImpregnated() || victim1.isHypnotized() || victim1.isDrained() || victim1.isParasitized() || victim1.temptReq < 100000 || victim1.resolve < 50) {
								if (w.getTechs()[40].isOwned() && killer1.hesitated == false && w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4) {
									w.append(t, killer1.getMainName() + " calls out to " + victim1.getMainName() + ", urging " + victim1.himHer() + " to escape before " + victim1.heShe() + " gets caught up in " + killer1.getMainName() + "'s final attack.");
								} else {
									w.append(t, killer1.getMainName() + " prepares to launch a devastating attack in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
								}
							} else {
								w.append(t, killer1.getMainName() + " buys time for " + victim1.getMainName() + " to escape so that the two of them can work together to finish this.");
							}
						}
						w.append(t, "\n");
					}
				} else {
					while (c == null) {
						c = w.getCombatants()[(int)(Math.random()*3)];
						if (c != null) {
							if (c.isSurrounded() || c.isCaptured()) {
								c = null;
							}
						}
					}
					w.append(t, c.getMainName() + " can't finish clearing out the Demons due to the risk of hitting the trapped " + trappedChosen.getMainName() + " with friendly fire!\n");
				}
			} else {
				while (c == null) {
					c = w.getCombatants()[(int)(Math.random()*3)];
					if (c != null) {
						if (c.isSurrounded() || c.isCaptured()) {
							c = null;
						}
					}
				}
				Boolean plural = false;
				if (w.getCombatants()[1] != null) {
					plural = true;
				}
				if (w.exterminationMultiplier == 100) {
					w.append(t, "With the civilians evacuated, " + c.getMainName());
					if (plural) {
						w.append(t, " and the other Chosen can start drawing on their full power!");
					} else {
						w.append(t, " can start drawing on " + c.hisHer() + " full power!");
					}
				} else if (w.exterminationMultiplier == 150) {
					w.append(t, c.getMainName() + "'s attacks grow stronger and stronger, shattering windows and setting off alarms!");
				} else if (w.exterminationMultiplier == 225) {
					w.append(t, c.getMainName() + " moves like a blur, taking down a wide swath of Demons!");
				} else if (w.exterminationMultiplier == 337) {
					w.append(t, "A blast of energy from " + c.getMainName() + " brings down a small building in a cloud of rubble!");
				} else if (w.exterminationMultiplier == 505) {
					w.append(t, "The area is riddled with craters caused by the power of " + c.getMainName() + "'s attacks!");
				} else if (w.exterminationMultiplier == 757) {
					w.append(t, "The district is consumed by an enormous explosion as " + c.getMainName() + " blasts away the Demons!");
				}
				w.append(t, "\n(Extermination power");
				if (w.cast[1] != null) {
					w.append(t, " per Chosen");
				}
				w.append(t, ": " + (w.exterminationPerChosen*w.exterminationMultiplier/100) + ")");
				w.append(t, "\n");
			}
		}
		for (int i = 0; i < w.getCombatants().length; i++) {
			if (w.getCombatants()[i] != null) {
				w.append(t, "\n");
				if (w.getCombatants()[i].type == Chosen.Species.SUPERIOR) {
					w.append(t, "[SUPERIOR] ");
				}
				if (w.getCombatants()[i].isSurrounded() && (w.getCombatants()[i].resolve > 0 || w.finalBattle == false)) {
					w.orangeAppend(t, w.getCombatants()[i].getMainName() + ": ");
					if (inseminated == 3 || orgasming == 3 || sodomized == 3 || broadcasted == 3) {
						w.orangeAppend(t, "In Orgy");
					} else if (w.getCombatants()[i].isInseminated()) {
						w.orangeAppend(t, "Inseminated");
					} else if (w.getCombatants()[i].isOrgasming()) {
						w.orangeAppend(t, "Orgasming");
					} else if (w.getCombatants()[i].isSodomized()) {
						if (w.tickle()) {
							w.orangeAppend(t, "Laughing");
						} else if (w.getCombatants()[i].getGender().equals("male")) {
							w.orangeAppend(t, "Tortured");
						} else {
							w.orangeAppend(t, "Sodomized");
						}
					} else if (w.getCombatants()[i].isBroadcasted()) {
						w.orangeAppend(t, "Broadcasted");
					} else if (w.getCombatants()[i].tempted) {
						w.orangeAppend(t, "Tempted");
					} else {
						w.orangeAppend(t, "Surrounded");
					}
					if (w.getCombatants()[i].getSurroundDuration() > 1) {
						w.orangeAppend(t, " for " + w.getCombatants()[i].getSurroundDuration() + " more turns");
					} else {
						w.orangeAppend(t, " until next turn");
					}
				} else if (w.getCombatants()[i].isCaptured()) {
					w.orangeAppend(t, w.getCombatants()[i].getMainName() + ": ");
					if (w.getCombatants()[i].timesDetonated() > 0 && w.adaptationsDisabled() == false) {
						if (w.getCombatants()[i].getCaptureProgression() + w.getCombatants()[i].getINJULevel() + 1 >= w.getCaptureDuration()) {
							w.orangeAppend(t, "Detonating next turn");
						} else if (w.getCombatants()[i].getCaptureProgression() + w.getCombatants()[i].getINJULevel() + 2 == w.getCaptureDuration()) {
							w.orangeAppend(t, "Detonating in 2 more turns");
						} else if (w.getBodyStatus()[5] || w.getBodyStatus()[12] || w.getBodyStatus()[13] || w.getBodyStatus()[21] || w.usedForsaken != null) {
							w.orangeAppend(t, "Detonating in up to " + (w.getCaptureDuration() - w.getCombatants()[i].getCaptureProgression() - w.getCombatants()[i].getINJULevel()) + " more turns");
						} else {
							w.orangeAppend(t, "Detonating in " + (w.getCaptureDuration() - w.getCombatants()[i].getCaptureProgression() - w.getCombatants()[i].getINJULevel()) + " more turns");
						}
					} else {
						if (w.getCombatants()[i].getCaptureProgression() < w.getCaptureDuration()) {
							w.orangeAppend(t, "Captured for " + (w.getCaptureDuration() - w.getCombatants()[i].getCaptureProgression() + 1) + " more turns");
						} else {
							w.orangeAppend(t, "Captured until next turn");
						}
					}
				} else if (w.getCombatants()[i].alive == false) {
					w.redAppend(t, w.getCombatants()[i].getMainName() + ": Killed in Action");
				} else if (w.finalBattle && w.getCombatants()[i].resolve <= 0) {
					w.greenAppend(t, w.getCombatants()[i].getMainName() + ": Resolve Broken!");
				} else if (w.getCombatants()[i].surroundPossible(w)) {
					w.purpleAppend(t, w.getCombatants()[i].getMainName() + ": Opening Level " + (w.getCombatants()[i].getFEAROpening(w) + w.getCombatants()[i].getPAINOpening() + w.getCombatants()[i].getDISGOpening() + w.getCombatants()[i].getSHAMOpening(w)) + " vs. Defense Level " + w.getCombatants()[i].getDefenseLevel());
				} else if (w.getCombatants()[i].getDefenseLevel() > 9000) {
					w.append(t, w.getCombatants()[i].getMainName() + ": Flying Above Battlefield");
				} else {
					w.append(t, w.getCombatants()[i].getMainName() + ": Opening Level " + (w.getCombatants()[i].getFEAROpening(w) + w.getCombatants()[i].getPAINOpening() + w.getCombatants()[i].getDISGOpening() + w.getCombatants()[i].getSHAMOpening(w)) + " vs. Defense Level " + w.getCombatants()[i].getDefenseLevel());
				}
				if (w.finalBattle && w.getCombatants()[i].resolve > 0 && w.getCombatants()[i].alive) {
					w.append(t, " [Resolve at " + w.getCombatants()[i].resolve + "%]");
				}
			}
		}
		if (w.usedForsaken != null) {
			w.append(t, "\n\n" + w.usedForsaken.mainName + ": ");
			int occupied = -1;
			for (int i = 0; i < w.getCombatants().length; i++) {
				if (w.getCombatants()[i] != null) {
					if (w.getCombatants()[i].captured) {
						occupied = i;
					}
				}
			}
			if (occupied >= 0) {
				w.purpleAppend(t, "Busy with " + w.getCombatants()[occupied].mainName);
			} else if (w.usedForsaken.injured > 1) {
				w.redAppend(t, "Stunned for " + w.usedForsaken.injured + " turns");
				changePortrait(w.usedForsaken.gender, w.usedForsaken.type, displayedCivilians[3], true, w, w.nameCombatants(), 3, Project.Emotion.SWOON, Project.Emotion.SWOON);
			} else if (w.usedForsaken.injured == 1) {
				w.redAppend(t, "Stunned until next turn");
				changePortrait(w.usedForsaken.gender, w.usedForsaken.type, displayedCivilians[3], true, w, w.nameCombatants(), 3, Project.Emotion.SWOON, Project.Emotion.SWOON);
			} else {
				w.greenAppend(t, "Ready to capture target");
				if (w.usedForsaken.flavorObedience() < 20) {
					changePortrait(w.usedForsaken.gender, w.usedForsaken.type, displayedCivilians[3], true, w, w.nameCombatants(), 3, Project.Emotion.ANGER, Project.Emotion.NEUTRAL);
				} else if (w.usedForsaken.flavorObedience() < 40) {
					changePortrait(w.usedForsaken.gender, w.usedForsaken.type, displayedCivilians[3], true, w, w.nameCombatants(), 3, Project.Emotion.ANGER, Project.Emotion.SHAME);
				} else if (w.usedForsaken.flavorObedience() < 61) {
					changePortrait(w.usedForsaken.gender, w.usedForsaken.type, displayedCivilians[3], true, w, w.nameCombatants(), 3, Project.Emotion.SHAME, Project.Emotion.STRUGGLE);
				} else if (w.usedForsaken.flavorObedience() < 81) {
					changePortrait(w.usedForsaken.gender, w.usedForsaken.type, displayedCivilians[3], true, w, w.nameCombatants(), 3, Project.Emotion.FOCUS, Project.Emotion.ANGER);
				} else {
					changePortrait(w.usedForsaken.gender, w.usedForsaken.type, displayedCivilians[3], true, w, w.nameCombatants(), 3, Project.Emotion.FOCUS, Project.Emotion.JOY);
				}
			}
		}
		if (w.getRallyBonus() > 0) {
			w.append(t, "\n\nMorale bonus: incoming trauma decreased by " + (w.getRallyBonus()/6) + "%");
		}
		if (w.getDistractBonus() > 0) {
			w.append(t, "\n\nThralls distracted: damage to surrounded Chosen decreased by " + (w.getDistractBonus()/3) + "%");
		}
		if (w.getBarrierMulti() > 10000) {
			w.append(t, "\n\nDemonic barrier: all damage increased by " + (w.getBarrierMulti()/100 - 100) + "%");
		}
		int targets = 0;
		int targetFound = 0;
		int defeated = 0;
		int trapped = 0;
		for (int i = 0; i < 3; i++) {
			if (w.getCombatants()[i] != null) {
				if (w.finalBattle == false) {
					targets++;
					targetFound = i;
				} else if (w.getCombatants()[i].isCaptured() || (w.getCombatants()[i].isSurrounded() && (w.getCombatants()[i].isDefiled() || (w.getCombatants()[i].getHATELevel() < 3 && w.getCombatants()[i].getPLEALevel() < 3 && w.getCombatants()[i].getINJULevel() < 3 && w.getCombatants()[i].getEXPOLevel() < 3 && w.getCombatants()[i].grind && w.getCombatants()[i].caress && w.getCombatants()[i].pummel && w.getCombatants()[i].humiliate)))) {
					trapped++;
				} else if (w.getCombatants()[i].alive && w.getCombatants()[i].resolve > 0) {
					targets++;
					targetFound = i;
				} else {
					defeated++;
				}
			}
		}
		if (targets == 1) {
			if (w.getCast()[1] != null) {
				if (w.finalBattle && defeated > 0) {
					w.append(t, "\n\n" + w.getCombatants()[targetFound].getMainName() + " is still resisting!");
				} else if (w.getCombatants()[1] != null) {
					w.append(t, "\n\n" + w.getCombatants()[targetFound].getMainName() + " is trying to stall until the team can fight at full strength!");
				} else {
					w.append(t, "\n\n" + w.getCombatants()[targetFound].getMainName() + " is fighting alone!");
				}
			}
		} else if (targets == 0) {
			if (w.getCombatants()[1] == null) {
				w.append(t, "\n\n" + w.getCombatants()[0].getMainName() + "'s allies haven't shown up yet!");
			} else {
				w.append(t, "\n\nThe Chosen are struggling to escape the Demons' clutches!");
			}
		} else {
			w.chatter(t);
			w.append(t, "\n\nWho will you target?");
		}
		if (targets == 1 && (w.getCombatants()[1] == null || defeated == 2)) {
			PickAction(t, p, f, w, w.getCombatants()[targetFound], initiative);
		} else {
			p.removeAll();
			for (int i = 0; i < 3; i++) {
				if (w.getCombatants()[i] != null) {
					if (w.finalBattle == false || (w.getCombatants()[i].resolve > 0 && w.getCombatants()[i].alive)) {
						final int thisChosen = i;
						class TargetButton extends AbstractAction {
							public TargetButton(String text, String desc) {
								super(text);
								putValue(SHORT_DESCRIPTION, desc);
							}
							public void actionPerformed(ActionEvent e) {
								w.append(t, "\n\n" + w.getSeparator());
								PickAction(t, p, f, w, w.getCombatants()[thisChosen], initiative);
							}
						}
						Action TargetAction = new TargetButton(w.getCombatants()[i].getMainName(), "Hotkey:");
						JButton Target = new JButton(TargetAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -30);
						      }
						};
						if ((w.getCombatants()[i].getCurrentHATE() >= 10000 || w.getCombatants()[i].getCurrentPLEA() >= 10000 || w.getCombatants()[i].getCurrentINJU() >= 10000 || w.getCombatants()[i].getCurrentEXPO() >= 10000) && w.getCombatants()[i].isSurrounded() && w.getCombatants()[i].isDefiled() == false) {
							if ((w.getCombatants()[i].getCurrentHATE() >= 10000 && inseminated > 0) || (w.getCombatants()[i].getCurrentPLEA() >= 10000 && orgasming > 0) || (w.getCombatants()[i].getCurrentINJU() >= 10000 && sodomized > 0) || (w.getCombatants()[i].getCurrentEXPO() >= 10000 && broadcasted > 0)) {
								Target.setBackground(PURPLISH);
							} else {
								Target.setBackground(YELLOWISH);
							}
						}
						Target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("" + (thisChosen + 1)),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && (w.getActions()[w.getCurrentAction()]-1) / 14 == w.getCombatants()[thisChosen].getNumber()) {
							Target.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Target.getActionMap().put("pressed",TargetAction);
						p.add(Target);
					}
				}
			}
			class PassButton extends AbstractAction {
				public PassButton(String text, String desc) {
					super(text);
					putValue(SHORT_DESCRIPTION, desc);
				}
				public void actionPerformed(ActionEvent e) {
					advanceAction(p, w, 0);
					if (w.getTechs()[30].isOwned() && w.progressExtermination(0) == false) {
						p.removeAll();
						w.increaseBarrier(t);
						class ContinueButton extends AbstractAction {
							public ContinueButton(String text, String desc) {
								super(text);
								putValue(SHORT_DESCRIPTION, desc);
							}
							public void actionPerformed(ActionEvent e) {
								EnemyTurn(t, p, f, w, initiative, 0);
							}
						}
						Action ContinueAction = new ContinueButton("Continue", "Hotkey:");
						JButton Continue = new JButton(ContinueAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -30);
						      }
						};
						Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						Continue.getActionMap().put("pressed",ContinueAction);
						p.add(Continue);
						p.validate();
						p.repaint();
					} else {
						EnemyTurn(t, p, f, w, initiative, 0);
					}
				}
			}
			Action PassAction = new PassButton("Do Nothing", "Hotkey:");
			JButton Pass = new JButton(PassAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			Pass.setForeground(Color.GRAY);
			Pass.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0),"pressed");
			if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == 0) {
				Pass.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
			}
			Pass.getActionMap().put("pressed",PassAction);
			if (w.getTechs()[30].isOwned() && w.progressExtermination(0) == false) {
				Pass.setText("Barrier");
				Pass.setToolTipText("+5% damage for rest of battle");
			}
			p.add(Pass);
			int occupied = 0;
			for (int i = 0; i < 3; i++) {
				if (w.getCombatants()[i] != null) {
					if (w.getCombatants()[i].isSurrounded()) {
						if (w.getCombatants()[i].getSurroundDuration() > 0) {
							occupied += w.getCombatants()[i].getSurroundDuration();
						} else {
							occupied++;
						}
					} else if (w.getCombatants()[i].isCaptured()) {
						occupied += w.getCaptureDuration() - w.getCombatants()[i].getCaptureProgression() + 1;
					}
				}
			}
			final int occupiedBonus = occupied/5;
			class RetreatButton extends AbstractAction {
				public RetreatButton(String text, String desc) {
					super(text);
					putValue(SHORT_DESCRIPTION, desc);
				}
				public void actionPerformed(ActionEvent e) {
					p.removeAll();
					w.append(t, "\n\n" + w.getSeparator() + "\n\n");
					if (occupiedBonus > 0) {
						w.append(t, "Retreat and end the battle immediately for +" + occupiedBonus + " Evil Energy?");
					} else {
						w.append(t, "Really retreat?  You will not gain any bonus Evil Energy!");
					}
					JButton Confirm = new JButton("Confirm");
					Confirm.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							p.removeAll();
							w.append(t, "\n\n" + w.getSeparator() + "\n\n");
							String[] trapped = new String[]{null, null, null};
							String[] free = new String[]{null, null, null};
							int trappedNumber = 0;
							for (int i = 0; i < 3; i++) {
								if (w.getCombatants()[i] != null) {
									if (w.getCombatants()[i].isSurrounded() || w.getCombatants()[i].isCaptured()) {
										for (int j = 0; j < 3; j++) {
											if (trapped[j] == null) {
												trapped[j] = w.getCombatants()[i].getMainName();
												trappedNumber++;
												j = 3;
											}
										}
									} else {
										for (int j = 0; j < 3; j++) {
											if (free[j] == null) {
												free[j] = w.getCombatants()[i].getMainName();
												j = 3;
											}
										}
									}
								}
							}
							if (w.getCombatants()[1] == null) {
								for (int i = 0; i < 3; i++) {
									if (w.getCast()[i] != null && w.getCast()[i].equals(w.getCombatants()[0]) == false) {
										if (free[0] == null) {
											free[0] = w.getCast()[i].mainName;
										} else if (free[1] == null) {
											free[1] = w.getCast()[i].mainName;
										} else {
											free[2] = w.getCast()[i].mainName;
										}
									}
								}
							} else if (w.getCombatants()[2] == null) {
								for (int i = 0; i < 3; i++) {
									if (w.getCast()[i] != null && w.getCast()[i].equals(w.getCombatants()[0]) == false && w.getCast()[i].equals(w.getCombatants()[1]) == false) {
										if (free[0] == null) {
											free[0] = w.getCast()[i].mainName;
										} else if (free[1] == null) {
											free[1] = w.getCast()[i].mainName;
										} else {
											free[2] = w.getCast()[i].mainName;
										}
									}
								}
							}
							w.append(t, "You order your Demons to flee back into the tunnels beneath the city along with their captive victims.  ");
							if (w.getCast()[1] == null) {
								if (trappedNumber == 0) {
									w.append(t, "However, " + free[0] + " is quick to pursue, cutting your forces down from behind and stopping them from taking any significant number of civilians back to the hive.");
								} else {
									w.append(t, trapped[0] + " is unable to follow until plenty of civilians are already on their way to the hive.");
								}
							} else if (w.getCast()[2] == null) {
								if (trappedNumber == 0) {
									w.append(t, "However, the two Chosen cut your forces down from behind, freeing most of the civilians before they can be brought to the hive.");
								} else if (trappedNumber == 1) {
									w.append(t, "With " + trapped[0] + " unable to give chase, the risk of splitting the team forces " + free[0] + " to give up and let you take the civilians to the hive.");
								} else {
									w.append(t, "The Chosen have to finish dealing with their own problems before they can try to stop you, and by then, plenty of your forces have escaped.");
								}
							} else if (trappedNumber == 0 || occupiedBonus == 0) {
								w.append(t, "However, the three Chosen cut your forces down from behind, freeing most of the civilians before they can be brought to the hive.");
							} else if (trappedNumber == 1) {
								w.append(t, free[0] + " and " + free[1] + " try to give chase, but with " + trapped[0] + " unable to follow, they're forced to give up due to the risk of splitting the team.");
							} else if (trappedNumber == 2) {
								w.append(t, free[0] + " tries to stop them, but with " + trapped[0] + " and " + trapped[1] + " unable to help, you're able to get plenty of victims to the hive.");
							} else {
								w.append(t, "The Chosen have to finish dealing with their own problems before they can try to stop you, and by then, plenty of your forces have escaped.");
							}
							if (occupiedBonus > 0) {
								w.append(t, "\n\n+" + occupiedBonus + " Evil Energy");
							}
							advanceAction(p, w, 43);
							w.addEnergy(occupiedBonus);
							JButton Continue = new JButton("Continue");
							Continue.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									PostBattle(t, p, f, w);
								}
							});
							p.add(Continue);
							p.validate();
							p.repaint();
						}
					});
					p.add(Confirm);
					JButton Cancel = new JButton("Cancel");
					Cancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							w.append(t, "\n\n" + w.getSeparator() + "\n");
							PickTarget(t, p, f, w);
						}
					});
					p.add(Cancel);
					p.validate();
					p.repaint();
				}
			}
			Action RetreatAction = new RetreatButton("Retreat (" + occupiedBonus + ")", "End battle immediately for +" + occupiedBonus + " EE");
			JButton Retreat = new JButton(RetreatAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			if (w.getTechs()[19].isOwned() && w.finalBattle == false) {
				p.add(Retreat);
			}
			if (w.writePossible()) {
				addWriteButton(p, w);
			}
			p.validate();
			//f.pack();
			p.repaint();
		}
		w.readCommentary(t);
	}
	
	public static void advanceAction(JPanel p, WorldState w, int action) {
		Boolean actionMatches = true;
		if (w.getActions().length > w.getCurrentAction()) {
			if (w.getActions()[w.getCurrentAction()] != action) {
				actionMatches = false;
				w.truncateCommentary(w.getCurrentAction());
			}
		}
		if (w.writePossible()) {
			if (w.getCurrentComment().length() > 0) {
				w.writeCommentary(w.getCurrentComment());
			} else if (w.getCommentary().length <= w.getCurrentAction() || actionMatches == false) {
				String generated = "";
				if (action == 0) {
					if (w.getTechs()[30].isOwned() && w.progressExtermination(0) == false) {
						generated = "Deepen your barrier.";
					} else {
						generated = "Do nothing.";
					}
				} else if (action == 43) {
					generated = "Retreat from the battle.";
				} else if (action > 43) {
					int type = (action-44)/3;
					int target = (action-44)%3;
					String targetedChosen = w.getCast()[target].getMainName();
					if (type == 0) {
						generated = "Tempt ";
					}
					generated = generated + targetedChosen + ".";
				} else {
					int target = (action-1)/14;
					int type = ((action-1)%14)+1;
					String targetedChosen = w.getCast()[target].getMainName();
					if (type == 1) {
						generated = "Surround ";
					} else if (type == 2) {
						generated = "Capture ";
						/*if (w.upgradedCommander() == false && w.getTechs()[31].isOwned()) {
							generated = generated + " and then ";
						}
						type = ((w.getActions()[w.getCurrentAction()+1]-1)%14)+1;
						if (type == 7) {
							generated = generated + "Grind against ";
						} else if (type == 8) {
							generated = generated + "Caress ";
						} else if (type == 9) {
							generated = generated + "Pummel ";
						} else if (type == 10) {
							generated = generated + "Humiliate ";
						} else if (type == 11) {
							generated = generated + "Inseminate ";
						} else if (type == 12) {
							generated = generated + "Force Orgasm on ";
						} else if (type == 13) {
							if(w.getCast()[target].getGender().equals("male")) {
								generated = generated + "Torture ";
							} else {
								generated = generated + "Sodomize ";
							}
						} else if (type == 14) {
							generated = generated + "Broadcast ";
						}
						generated = generated + targetedChosen + ".";*/
					} else if (type == 3) {
						generated = "Threaten ";
					} else if (type == 4) {
						generated = "Slime ";
					} else if (type == 5) {
						if (w.tickle()) {
							generated = "Poke ";
						} else {
							generated = "Attack ";
						}
					} else if (type == 6) {
						generated = "Taunt ";
					} else {
						if (w.getTechs()[31].isOwned() && w.getCast()[target].isSurrounded() == false) {
							if (w.getCast()[target].surroundPossible(w) == false) {
								generated = generated + "Capture and then ";
							} else {
								generated = generated + "Surround and then ";
							}
						}
						if (type == 7) {
							generated = generated + "Grind against ";
						} else if (type == 8) {
							generated = generated + "Caress ";
						} else if (type == 9) {
							if (w.tickle()) {
								generated = generated + "Tickle ";
							} else {
								generated = generated + "Pummel ";
							}
						} else if (type == 10) {
							generated = generated + "Humiliate ";
						} else if (type == 11) {
							generated = generated + "Inseminate ";
						} else if (type == 12) {
							generated = generated + "Force Orgasm on ";
						} else if (type == 13) {
							if (w.tickle()) {
								generated = generated + "Force Laughter from ";
							} else if(w.getCast()[target].getGender().equals("male")) {
								generated = generated + "Torture ";
							} else {
								generated = generated + "Sodomize ";
							}
						} else if (type == 14) {
							generated = generated + "Broadcast ";
						}
					}
					generated = generated + targetedChosen + ".";
				}
				w.writeCommentary(generated);
			}
		}
		w.nextAction(action);
	}
	
	public static void addWriteButton(JPanel p, WorldState w) {
		JButton Comment = new JButton("Comment");
		Comment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String prompt = "Enter your comment here.  Leave blank to ";
				if (w.getCurrentComment().length() > 0) {
					prompt = prompt + "keep the comment you already wrote.";
				} else if (w.getCommentary().length > w.getCurrentAction()) {
					prompt = prompt + "keep the previous playthrough's comment.";
				} else {
					prompt = prompt + "generate a default comment describing your action.";
				}
				String input = JOptionPane.showInputDialog(prompt);
				if (input != null) {
					if (input.length() > 0) {
						w.setCurrentComment(input);
						Comment.setToolTipText("\"" + input + "\"");
					}
				}
			}
		});
		Comment.setForeground(Color.GRAY);
		Comment.setToolTipText("No comment currently stored.");
		p.add(Comment);
	}
	
	public static void PickAction (JTextPane t, JPanel p, JFrame f, WorldState w, Chosen c, Chosen[] initiative) {
		Color YELLOWISH = new Color(255,225,125);
		Color PURPLISH = new Color(225, 125, 255);
		Color REDDISH = new Color(255, 145, 145);
		int inseminated = 0;
		int orgasming = 0;
		int sodomized = 0;
		int broadcasted = 0;
		for (int i = 0; i < 3; i++) {
			if (w.getCombatants()[i] != null) {
				if (w.getCombatants()[i].isInseminated()) {
					inseminated++;
				} else if (w.getCombatants()[i].isOrgasming()) {
					orgasming++;
				} else if (w.getCombatants()[i].isSodomized()) {
					sodomized++;
				} else if (w.getCombatants()[i].isBroadcasted()) {
					broadcasted++;
				}
			}
		}
		class ContinueButton extends AbstractAction {
			public ContinueButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				EnemyTurn(t, p, f, w, initiative, 0);
			}
		}
		Action ContinueAction = new ContinueButton("Continue", "Hotkey:");
		class AttackButton extends AbstractAction {
			public AttackButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				if (w.finalBattle && w.getTechs()[44].isOwned()) {
					w.finalAttack(t, w, c);
				} else {
					c.Attack(t, p, f, w);
				}
				advanceAction(p, w, c.getNumber()*14+5);
				p.removeAll();
				JButton Continue = new JButton(ContinueAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -30);
				      }
				};
				Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				Continue.getActionMap().put("pressed",ContinueAction);
				p.add(Continue);
				p.validate();
				//f.pack();
				p.repaint();
			}
		}
		String attackName = "Attack";
		if (w.tickle()) {
			attackName = "Poke";
		}
		Action AttackAction = new AttackButton(attackName, "Use " + attackName);
		class SlimeButton extends AbstractAction {
			public SlimeButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				if (w.finalBattle && w.getTechs()[43].isOwned() && c.isHypnotized()) {
					w.finalSlime(t, w, c);
				} else {
					c.Slime(t, p, f, w);
				}
				advanceAction(p, w, c.getNumber()*14+4);
				p.removeAll();
				JButton Continue = new JButton(ContinueAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -30);
				      }
				};
				Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				Continue.getActionMap().put("pressed",ContinueAction);
				p.add(Continue);
				p.validate();
				//f.pack();
				p.repaint();
			}
		}
		Action SlimeAction = new SlimeButton("Slime", "Use Slime");
		class TauntButton extends AbstractAction {
			public TauntButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				if (w.finalBattle && w.getTechs()[45].isOwned() && c.isParasitized() && c.surroundPossible(w)) {
					w.finalTaunt(t, w, c);
				} else {
					c.Taunt(t, p, f, w);
				}
				advanceAction(p, w, c.getNumber()*14+6);
				p.removeAll();
				JButton Continue = new JButton(ContinueAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -30);
				      }
				};
				Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				Continue.getActionMap().put("pressed",ContinueAction);
				p.add(Continue);
				p.validate();
				//f.pack();
				p.repaint();
			}
		}
		Action TauntAction = new TauntButton("Taunt", "Use Taunt");
		class ThreatenButton extends AbstractAction {
			public ThreatenButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				Boolean impregnatedAlly = false;
				for (int i = 0; i < 3; i++) {
					if (i != c.getNumber() && w.getCast()[i] != null) {
						if (w.getCast()[i].isImpregnated() && w.getCast()[i].alive) {
							impregnatedAlly = true;
						}
					}
				}
				if (w.finalBattle && w.getTechs()[42].isOwned() && impregnatedAlly) {
					w.finalThreaten(t, w, c);
				} else {
					c.Threaten(t, p, f, w);
				}
				advanceAction(p, w, c.getNumber()*14+3);
				p.removeAll();
				JButton Continue = new JButton(ContinueAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -30);
				      }
				};
				Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				Continue.getActionMap().put("pressed",ContinueAction);
				p.add(Continue);
				p.validate();
				//f.pack();
				p.repaint();
			}
		}
		Action ThreatenAction = new ThreatenButton("Threaten", "Use Threaten");
		class GrindButton extends AbstractAction {
			public GrindButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				c.beginGrind();
				advanceAction(p, w, c.getNumber()*14+7);
				EnemyTurn(t, p, f, w, initiative, 0);
			}
		}
		Action GrindAction = new GrindButton("Grind", "Use Grind");
		class CaressButton extends AbstractAction {
			public CaressButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				c.beginCaress();
				advanceAction(p, w, c.getNumber()*14+8);
				EnemyTurn(t, p, f, w, initiative, 0);
			}
		}
		Action CaressAction = new CaressButton("Caress", "Use Caress");
		class PummelButton extends AbstractAction {
			public PummelButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				c.beginPummel();
				advanceAction(p, w, c.getNumber()*14+9);
				EnemyTurn(t, p, f, w, initiative, 0);
			}
		}
		String pummelName = "Pummel";
		if (w.tickle()) {
			pummelName = "Tickle";
		}
		Action PummelAction = new PummelButton(pummelName, "Use " + pummelName);
		class HumiliateButton extends AbstractAction {
			public HumiliateButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				c.beginHumiliate();
				advanceAction(p, w, c.getNumber()*14+10);
				EnemyTurn(t, p, f, w, initiative, 0);
			}
		}
		Action HumiliateAction = new HumiliateButton("Humiliate", "Use Humiliate");
		class InseminateButton extends AbstractAction {
			public InseminateButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				c.beginInseminate();
				advanceAction(p, w, c.getNumber()*14+11);
				EnemyTurn(t, p, f, w, initiative, 0);
			}
		}
		Action InseminateAction = new InseminateButton("Inseminate", "Use Inseminate");
		class ForceOrgasmButton extends AbstractAction {
			public ForceOrgasmButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				c.beginOrgasm();
				advanceAction(p, w, c.getNumber()*14+12);
				EnemyTurn(t, p, f, w, initiative, 0);
			}
		}
		Action ForceOrgasmAction = new ForceOrgasmButton("Force Orgasm", "Use Force Orgasm");
		class SodomizeButton extends AbstractAction {
			public SodomizeButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				c.beginSodomize();
				advanceAction(p, w, c.getNumber()*14+13);
				EnemyTurn(t, p, f, w, initiative, 0);
			}
		}
		String SodomizeName = "Sodomize";
		if (w.tickle()) {
			SodomizeName = "Force Laughter";
		} else if (c.getGender().equals("male")) {
			SodomizeName = "Torture";
		}
		Action SodomizeAction = new SodomizeButton(SodomizeName, "Use " + SodomizeName);
		class BroadcastButton extends AbstractAction {
			public BroadcastButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				c.beginBroadcast();
				advanceAction(p, w, c.getNumber()*14+14);
				EnemyTurn(t, p, f, w, initiative, 0);
			}
		}
		Action BroadcastAction = new BroadcastButton("Broadcast", "Use Broadcast");
		class TemptButton extends AbstractAction{
			public TemptButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				if (w.finalBattle) {
					w.finalTempt(t, c);
				} else {
					c.beginTempt();
				}
				advanceAction(p, w, c.getNumber()+44);
				if (w.finalBattle) {
					p.removeAll();
					JButton Continue = new JButton(ContinueAction){
						public Point getToolTipLocation(MouseEvent e) {
					        return new Point(0, -30);
					      }
					};
					Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
					Continue.getActionMap().put("pressed",ContinueAction);
					p.add(Continue);
					p.validate();
					//f.pack();
					p.repaint();
				} else {
					EnemyTurn(t, p, f, w, initiative, 0);
				}
			}
		}
		Action TemptAction = new TemptButton("Tempt", "Use Tempt");
		final int finalInseminated = inseminated;
		final int finalOrgasming = orgasming;
		final int finalSodomized = sodomized;
		final int finalBroadcasted = broadcasted;
		class SurroundButton extends AbstractAction {
			public SurroundButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				w.setSurroundTarget(c);
				if (w.getTechs()[31].isOwned()) {
					p.removeAll();
					int defilers = 0;
					Boolean plusPossible = false;
					Boolean orgyPossible = false;
					String PAINname = "PAIN";
					String INJUname = "INJU";
					if (w.tickle()) {
						PAINname = "TICK";
						INJUname = "ANTI";
					}
					if (c.getGrind() == false) {
						JButton Grind = new JButton(GrindAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						Grind.setToolTipText("<html><center>Inflicts HATE along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Can cause tier-1 Morality or Dignity Break</center></html>");
						p.add(Grind);
						Grind.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("1"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 7) {
							Grind.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Grind.getActionMap().put("pressed",GrindAction);
					}
					if (c.getCaress() == false) {
						JButton Caress = new JButton(CaressAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						Caress.setToolTipText("<html><center>Inflicts PLEA along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Can cause tier-1 Innocence or Confidence Break</center></html>");
						p.add(Caress);
						Caress.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("2"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 8) {
							Caress.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Caress.getActionMap().put("pressed",CaressAction);
					}
					if (c.getPummel() == false) {
						JButton Pummel = new JButton(PummelAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						Pummel.setToolTipText("<html><center>Inflicts " + INJUname + " along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Can cause tier-1 Morality or Confidence Break</center></html>");
						p.add(Pummel);
						Pummel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("3"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 9) {
							Pummel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Pummel.getActionMap().put("pressed",PummelAction);
					}
					if (c.getHumiliate() == false) {
						JButton Humiliate = new JButton(HumiliateAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						Humiliate.setToolTipText("<html><center>Inflicts EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Can cause tier-1 Innocence or Dignity Break</center></html>");
						p.add(Humiliate);
						Humiliate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("4"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 10) {
							Humiliate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Humiliate.getActionMap().put("pressed",HumiliateAction);
					}
					if (c.getCurrentHATE() >= 10000) {
						defilers++;
						JButton Inseminate = new JButton(InseminateAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						if (finalInseminated > 0) {
							Inseminate.setBackground(PURPLISH);
							if (finalInseminated == 1) {
								Inseminate.setText("Inseminate+");
								plusPossible = true;
							} else {
								Inseminate.setText("Orgy");
								orgyPossible = true;
							}
						} else {
							Inseminate.setBackground(YELLOWISH);
						}
						if (c.temptReq < 100000 && finalInseminated != 2) {
							Inseminate.setBackground(REDDISH);
						}
						Inseminate.setToolTipText("<html><center>Inflicts HATE and PLEA along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
						if (finalInseminated == 1) {
							Inseminate.setToolTipText("<html><center>Inflicts HATE, PLEA and " + INJUname + " along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
						} else if (finalInseminated == 2) {
							Inseminate.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
						}
						p.add(Inseminate);
						Inseminate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("5"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 11) {
							Inseminate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Inseminate.getActionMap().put("pressed",InseminateAction);
					}
					if (c.getCurrentPLEA() >= 10000) {
						defilers++;
						JButton ForceOrgasm = new JButton(ForceOrgasmAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						if (finalOrgasming > 0) {
							ForceOrgasm.setBackground(PURPLISH);
							if (finalOrgasming == 1) {
								ForceOrgasm.setText("Force Orgasm+");
								plusPossible = true;
							} else {
								ForceOrgasm.setText("Orgy");
								orgyPossible = true;
							}
						} else {
							ForceOrgasm.setBackground(YELLOWISH);
						}
						ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA and " + INJUname + " along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
						if (finalOrgasming == 1) {
							ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA," + INJUname + ", and EXPO along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
						} else if (finalOrgasming == 2) {
							ForceOrgasm.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
						}
						p.add(ForceOrgasm);
						ForceOrgasm.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("6"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 12) {
							ForceOrgasm.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						ForceOrgasm.getActionMap().put("pressed",ForceOrgasmAction);
					}
					if (c.getCurrentINJU() >= 10000) {
						defilers++;
						JButton Sodomize = new JButton(SodomizeAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						if (finalSodomized > 0) {
							Sodomize.setBackground(PURPLISH);
							if (finalSodomized == 1) {
								if (w.tickle()) {
									Sodomize.setText("Force Laughter+");
								} else if (c.getGender().equals("male")) {
									Sodomize.setText("Torture+");
								} else {
									Sodomize.setText("Sodomize+");
								}
								plusPossible = true;
							} else {
								Sodomize.setText("Orgy");
								orgyPossible = true;
							}
						} else {
							Sodomize.setBackground(YELLOWISH);
						}
						if (c.temptReq < 100000 && finalSodomized != 2) {
							Sodomize.setBackground(REDDISH);
						}
						Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + " and EXPO along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
						if (finalSodomized == 1) {
							Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + ", EXPO, and HATE along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
						} else if (finalSodomized == 2) {
							Sodomize.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
						}
						p.add(Sodomize);
						Sodomize.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("7"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 13) {
							Sodomize.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Sodomize.getActionMap().put("pressed",SodomizeAction);
					}
					if (c.getCurrentEXPO() >= 10000) {
						defilers++;
						JButton Broadcast = new JButton(BroadcastAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						if (finalBroadcasted > 0) {
							Broadcast.setBackground(PURPLISH);
							if (finalBroadcasted == 1) {
								Broadcast.setText("Broadcast+");
								plusPossible = true;
							} else {
								Broadcast.setText("Orgy");
								orgyPossible = true;
							}
						} else {
							Broadcast.setBackground(YELLOWISH);
						}
						Broadcast.setToolTipText("<html><center>Inflicts EXPO and HATE along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
						if (finalBroadcasted == 1) {
							Broadcast.setToolTipText("<html><center>Inflicts EXPO, HATE, and PLEA along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
						} else if (finalBroadcasted == 2) {
							Broadcast.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
						}
						p.add(Broadcast);
						Broadcast.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("8"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 14) {
							Broadcast.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Broadcast.getActionMap().put("pressed",BroadcastAction);
					}
					long currentTemptReq = c.temptReq;
					if (w.finalBattle) {
						currentTemptReq = currentTemptReq*10;
					}
					if (c.getCurrentPLEA() >= currentTemptReq && c.vVirg && c.aVirg && c.cVirg == false && c.modest == false && c.ruthless == false && c.usingSlaughter == false && c.usingDetonate == false && (c.temptReq < 100000 || w.finalBattle == false)) {
						JButton Tempt = new JButton(TemptAction) {
							public Point getToolTipLocation(MouseEvent e) {
								return new Point(0, -60);
							}
						};
						Tempt.setToolTipText("<html><center>Inflicts extremely high PLEA and EXPO<br>but decreases other circumstances to zero and does not inflict trauma<br>Causes and intensifies Morality/Confidence Distortion</center></html>");
						Tempt.setBackground(PURPLISH);
						p.add(Tempt);
						Tempt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("9"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 14) {
							Tempt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Tempt.getActionMap().put("pressed",TemptAction);
					}
					w.append(t, "\n\n" + w.getSeparator() + "\n\nWhat should the Thralls do after surrounding " + c.getMainName() + "?");
					if (defilers > 1) {
						w.append(t, "  " + defilers + " defiler actions possible.");
					} else if (defilers == 1) {
						w.append(t, "  1 defiler action possible.");
					}
					int difference = 0;
					if (orgyPossible) {
						String firstName = "";
						String secondName = "";
						int duration = 0;
						int opening = c.getFEAROpening(w) + c.getDISGOpening() + c.getPAINOpening() + c.getSHAMOpening(w) + 1;
						for (int i = 0; i < 3; i++) {
							if (w.getCombatants()[i] != null && w.getCombatants()[i] != c) {
								if (firstName.length() == 0) {
									firstName = w.getCombatants()[i].getMainName();
									duration = w.getCombatants()[i].getSurroundDuration();
								} else {
									secondName = w.getCombatants()[i].getMainName();
								}
							}
						}
						w.append(t, "  Orgy with " + firstName + " and " + secondName);
						if (duration > opening) {
							difference = duration - opening;
							w.append(t, " will cause them");
						} else if (opening > duration) {
							difference = opening - duration;
							w.append(t, " will allow " + c.getMainName());
						}
						if (difference > 1) {
							w.append(t, " to escape " + difference + " turns early.");
						} else if (difference == 1) {
							w.append(t, " to escape 1 turn early.");
						} else {
							w.append(t, " does not allow any of them to escape early.");
						}
					} else if (plusPossible) {
						int opening = c.getFEAROpening(w) + c.getDISGOpening() + c.getPAINOpening() + c.getSHAMOpening(w) + 1;
						for (int i = 0; i < 3; i++) {
							if (w.getCombatants()[i] != null && w.getCombatants()[i] != c) {
								String defilementType = "";
								if (w.getCombatants()[i].isInseminated() && c.getHATELevel() >= 3) {
									defilementType = "Inseminate";
								} else if (w.getCombatants()[i].isOrgasming() && c.getPLEALevel() >= 3) {
									defilementType = "Force Orgasm";
								} else if (w.getCombatants()[i].isSodomized() && c.getINJULevel() >= 3) {
									if (w.tickle()) {
										defilementType = "Force Laughter";
									} else if (c.getGender().equals("male")) {
										defilementType = "Torture";
									} else {
										defilementType = "Sodomize";
									}
								} else if (w.getCombatants()[i].isBroadcasted() && c.getEXPOLevel() >= 3) {
									defilementType = "Broadcast";
								}
								if (defilementType.length() > 0) {
									w.append(t, "  " + defilementType + " with " + w.getCombatants()[i].getMainName());
									if (opening > w.getCombatants()[i].getSurroundDuration()) {
										w.append(t, " will allow " + c.getMainName());
										difference = opening - w.getCombatants()[i].getSurroundDuration();
									} else if (w.getCombatants()[i].getSurroundDuration() > opening) {
										w.append(t, " will allow " + w.getCombatants()[i].getMainName());
										difference = w.getCombatants()[i].getSurroundDuration() - opening;
									}
									if (difference > 1) {
										w.append(t, " to escape " + difference + " turns early.");
									} else if (difference == 1) {
										w.append(t, " to escape 1 turn early.");
									} else {
										w.append(t, " does not allow either of them to escape early.");
									}
									difference = 0;
								}
							}
						}
					}
					JButton Back = new JButton("Cancel");
					Back.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							w.setSurroundTarget(null);
							PickAction(t, p, f, w, c, initiative);
						}
					});
					p.add(Back);
					p.validate();
					p.repaint();
				} else {
					advanceAction(p, w, c.getNumber()*14+1);
					EnemyTurn(t, p, f, w, initiative, 0);
				}
			}
		}
		Action SurroundAction = new SurroundButton("Surround", "Hotkey:");
		class CaptureButton extends AbstractAction {
			public CaptureButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				Boolean directlyAdvance = true;
				if (w.upgradedCommander()) {
					w.setCaptureTarget(c);
				} else {
					w.setSurroundTarget(c);
					if (w.getTechs()[31].isOwned()) {
						directlyAdvance = false;
					}
				}
				if (directlyAdvance) {
					advanceAction(p, w, c.getNumber()*14+2);
					EnemyTurn(t, p, f, w, initiative, 0);
				} else {
					p.removeAll();
					String PAINname = "PAIN";
					String INJUname = "INJU";
					if (w.tickle()) {
						PAINname = "TICK";
						INJUname = "ANTI";
					}
					int defilers = 0;
					Boolean plusPossible = false;
					Boolean orgyPossible = false;
					if (c.getGrind() == false) {
						JButton Grind = new JButton(GrindAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						Grind.setToolTipText("<html><center>Inflicts HATE along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Can cause tier-1 Morality or Dignity Break</center></html>");
						p.add(Grind);
						Grind.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("1"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 7) {
							Grind.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Grind.getActionMap().put("pressed",GrindAction);
					}
					if (c.getCaress() == false) {
						JButton Caress = new JButton(CaressAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						Caress.setToolTipText("<html><center>Inflicts PLEA along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Can cause tier-1 Innocence or Confidence Break</center></html>");
						p.add(Caress);
						Caress.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("2"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 8) {
							Caress.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Caress.getActionMap().put("pressed",CaressAction);
					}
					if (c.getPummel() == false) {
						JButton Pummel = new JButton(PummelAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						Pummel.setToolTipText("<html><center>Inflicts " + INJUname + " along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Can cause tier-1 Morality or Confidence Break</center></html>");
						p.add(Pummel);
						Pummel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("3"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 9) {
							Pummel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Pummel.getActionMap().put("pressed",PummelAction);
					}
					if (c.getHumiliate() == false) {
						JButton Humiliate = new JButton(HumiliateAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						Humiliate.setToolTipText("<html><center>Inflicts EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Can cause tier-1 Innocence or Dignity Break</center></html>");
						p.add(Humiliate);
						Humiliate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("4"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 10) {
							Humiliate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Humiliate.getActionMap().put("pressed",HumiliateAction);
					}
					if (c.getCurrentHATE() >= 10000) {
						defilers++;
						JButton Inseminate = new JButton(InseminateAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						if (finalInseminated > 0) {
							Inseminate.setBackground(PURPLISH);
							if (finalInseminated == 1) {
								Inseminate.setText("Inseminate+");
								plusPossible = true;
							} else {
								Inseminate.setText("Orgy");
								orgyPossible = true;
							}
						} else {
							Inseminate.setBackground(YELLOWISH);
						}
						if (c.temptReq < 100000 && finalInseminated != 2) {
							Inseminate.setBackground(REDDISH);
						}
						Inseminate.setToolTipText("<html><center>Inflicts HATE and PLEA along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
						if (finalInseminated == 1) {
							Inseminate.setToolTipText("<html><center>Inflicts HATE, PLEA and " + INJUname + " along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
						} else if (finalInseminated == 2) {
							Inseminate.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
						}
						p.add(Inseminate);
						Inseminate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("5"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 11) {
							Inseminate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Inseminate.getActionMap().put("pressed",InseminateAction);
					}
					if (c.getCurrentPLEA() >= 10000) {
						defilers++;
						JButton ForceOrgasm = new JButton(ForceOrgasmAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						if (finalOrgasming > 0) {
							ForceOrgasm.setBackground(PURPLISH);
							if (finalOrgasming == 1) {
								ForceOrgasm.setText("Force Orgasm+");
								plusPossible = true;
							} else {
								ForceOrgasm.setText("Orgy");
								orgyPossible = true;
							}
						} else {
							ForceOrgasm.setBackground(YELLOWISH);
						}
						ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA and " + INJUname + " along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
						if (finalOrgasming == 1) {
							ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA," + INJUname + ", and EXPO along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
						} else if (finalOrgasming == 2) {
							ForceOrgasm.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
						}
						p.add(ForceOrgasm);
						ForceOrgasm.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("6"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 12) {
							ForceOrgasm.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						ForceOrgasm.getActionMap().put("pressed",ForceOrgasmAction);
					}
					if (c.getCurrentINJU() >= 10000) {
						defilers++;
						JButton Sodomize = new JButton(SodomizeAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						if (finalSodomized > 0) {
							Sodomize.setBackground(PURPLISH);
							if (finalSodomized == 1) {
								if (w.tickle()) {
									Sodomize.setText("Force Laughter+");
								} else if (c.getGender().equals("male")) {
									Sodomize.setText("Torture+");
								} else {
									Sodomize.setText("Sodomize+");
								}
								plusPossible = true;
							} else {
								Sodomize.setText("Orgy");
								orgyPossible = true;
							}
						} else {
							Sodomize.setBackground(YELLOWISH);
						}
						if (c.temptReq < 100000 && finalSodomized != 2) {
							Sodomize.setBackground(REDDISH);
						}
						Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + " and EXPO along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
						if (finalSodomized == 1) {
							Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + ", EXPO, and HATE along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
						} else if (finalSodomized == 2) {
							Sodomize.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
						}
						p.add(Sodomize);
						Sodomize.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("7"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 13) {
							Sodomize.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Sodomize.getActionMap().put("pressed",SodomizeAction);
					}
					if (c.getCurrentEXPO() >= 10000) {
						defilers++;
						JButton Broadcast = new JButton(BroadcastAction){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -60);
						      }
						};
						if (finalBroadcasted > 0) {
							Broadcast.setBackground(PURPLISH);
							if (finalBroadcasted == 1) {
								Broadcast.setText("Broadcast+");
								plusPossible = true;
							} else {
								Broadcast.setText("Orgy");
								orgyPossible = true;
							}
						} else {
							Broadcast.setBackground(YELLOWISH);
						}
						Broadcast.setToolTipText("<html><center>Inflicts EXPO and HATE along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
						if (finalBroadcasted == 1) {
							Broadcast.setToolTipText("<html><center>Inflicts EXPO, HATE, and PLEA along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
						} else if (finalBroadcasted == 2) {
							Broadcast.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
						}
						p.add(Broadcast);
						Broadcast.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("8"),"pressed");
						if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 14) {
							Broadcast.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						}
						Broadcast.getActionMap().put("pressed",BroadcastAction);
					}
					w.append(t, "\n\n" + w.getSeparator() + "\n\nWhat should the Thralls do after surrounding " + c.getMainName() + "?");
					if (defilers > 1) {
						w.append(t, "  " + defilers + " defiler actions possible.");
					} else if (defilers == 1) {
						w.append(t, "  1 defiler action possible.");
					}
					int difference = 0;
					if (orgyPossible) {
						String firstName = "";
						String secondName = "";
						int duration = 0;
						int opening = w.getCaptureDuration() + 1;
						for (int i = 0; i < 3; i++) {
							if (w.getCombatants()[i] != null && w.getCombatants()[i] != c) {
								if (firstName.length() == 0) {
									firstName = w.getCombatants()[i].getMainName();
									duration = w.getCombatants()[i].getSurroundDuration();
								} else {
									secondName = w.getCombatants()[i].getMainName();
								}
							}
						}
						w.append(t, "  Orgy with " + firstName + " and " + secondName);
						if (duration > opening) {
							difference = duration - opening;
							w.append(t, " will cause them");
						} else if (opening > duration) {
							difference = opening - duration;
							w.append(t, " will allow " + c.getMainName());
						}
						if (difference > 1) {
							w.append(t, " to escape " + difference + " turns early.");
						} else if (difference == 1) {
							w.append(t, " to escape 1 turn early.");
						} else {
							w.append(t, " does not allow any of them to escape early.");
						}
					} else if (plusPossible) {
						int opening = w.getCaptureDuration() + 1;
						for (int i = 0; i < 3; i++) {
							if (w.getCombatants()[i] != null && w.getCombatants()[i] != c) {
								String defilementType = "";
								if (w.getCombatants()[i].isInseminated() && c.getHATELevel() >= 3) {
									defilementType = "Inseminate";
								} else if (w.getCombatants()[i].isOrgasming() && c.getPLEALevel() >= 3) {
									defilementType = "Force Orgasm";
								} else if (w.getCombatants()[i].isSodomized() && c.getINJULevel() >= 3) {
									if (w.tickle()) {
										defilementType = "Force Laughter";
									} else if (c.getGender().equals("male")) {
										defilementType = "Torture";
									} else {
										defilementType = "Sodomize";
									}
								} else if (w.getCombatants()[i].isBroadcasted() && c.getEXPOLevel() >= 3) {
									defilementType = "Broadcast";
								}
								if (defilementType.length() > 0) {
									w.append(t, "  " + defilementType + " with " + w.getCombatants()[i].getMainName());
									if (opening > w.getCombatants()[i].getSurroundDuration()) {
										w.append(t, " will allow " + c.getMainName());
										difference = opening - w.getCombatants()[i].getSurroundDuration();
									} else if (w.getCombatants()[i].getSurroundDuration() > opening) {
										w.append(t, " will allow " + w.getCombatants()[i].getMainName());
										difference = w.getCombatants()[i].getSurroundDuration() - opening;
									}
									if (difference > 1) {
										w.append(t, " to escape " + difference + " turns early.");
									} else if (difference == 1) {
										w.append(t, " to escape 1 turn early.");
									} else {
										w.append(t, " does not allow either of them to escape early.");
									}
									difference = 0;
								}
							}
						}
					}
					JButton Back = new JButton("Cancel");
					Back.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							w.setSurroundTarget(null);
							PickAction(t, p, f, w, c, initiative);
						}
					});
					p.add(Back);
					p.validate();
					p.repaint();
				}
			}
		}
		Action CaptureAction = new CaptureButton("Capture", "Use Capture");
		class ExamineButton extends AbstractAction {
			public ExamineButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				c.Examine(t, p, f, w);
			}
		}
		Action ExamineAction = new ExamineButton("Examine", "Hotkey:");
		p.removeAll();
		w.append(t, "\n\n");
		c.printStatus(t, w);
		if (c.isCaptured() == false && c.isDefiled() == false) {
			w.append(t, "\n\nChoose your action.");
			if (w.usedForsaken != null && c.defenseLevel < 9000) {
				if (w.usedForsaken.injured == 0 && w.commanderFree()) {
					w.append(t, "  " + w.usedForsaken.mainName + " can ");
				} else {
					w.append(t, "  Once " + w.usedForsaken.mainName + " is ready, " + w.usedForsaken.heShe() + " will be able to ");
				}
				w.append(t, "capture " + c.mainName + " for " + w.usedForsaken.compatibility(c) + " rounds.");
			}
		}
		JButton Examine = new JButton(ExamineAction){
			public Point getToolTipLocation(MouseEvent e) {
		        return new Point(0, -30);
		      }
		};
		Examine.setForeground(Color.GRAY);
		p.add(Examine);
		Examine.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0),"pressed");
		Examine.getActionMap().put("pressed",ExamineAction);
		class PassButton extends AbstractAction {
			public PassButton(String text, String desc) {
				super(text);
				putValue(SHORT_DESCRIPTION, desc);
			}
			public void actionPerformed(ActionEvent e) {
				advanceAction(p, w, 0);
				if (w.getTechs()[30].isOwned() && w.progressExtermination(0) == false) {
					p.removeAll();
					w.increaseBarrier(t);
					class ContinueButton extends AbstractAction {
						public ContinueButton(String text, String desc) {
							super(text);
							putValue(SHORT_DESCRIPTION, desc);
						}
						public void actionPerformed(ActionEvent e) {
							EnemyTurn(t, p, f, w, initiative, 0);
						}
					}
					Action ContinueAction = new ContinueButton("Continue", "Hotkey:");
					JButton Continue = new JButton(ContinueAction){
						public Point getToolTipLocation(MouseEvent e) {
					        return new Point(0, -30);
					      }
					};
					Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
					Continue.getActionMap().put("pressed",ContinueAction);
					p.add(Continue);
					p.validate();
					p.repaint();
				} else {
					EnemyTurn(t, p, f, w, initiative, 0);
				}
			}
		}
		Action PassAction = new PassButton("Do Nothing", "Hotkey:");
		JButton Pass = new JButton(PassAction){
			public Point getToolTipLocation(MouseEvent e) {
		        return new Point(0, -30);
		      }
		};
		Pass.setForeground(Color.GRAY);
		Pass.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0),"pressed");
		if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == 0) {
			Pass.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
		}
		Pass.getActionMap().put("pressed",PassAction);
		if (w.getTechs()[30].isOwned() && w.progressExtermination(0) == false) {
			Pass.setText("Barrier");
			Pass.setToolTipText("+5% damage for rest of battle");
		}
		if (c.isDefiled()) {
			w.append(t, "\n\nThe Thralls have been driven into a frenzy ");
			if (c.isInseminated()) {
				w.append(t, "inseminating " + c.getMainName());
			} else if (c.isOrgasming()) {
				w.append(t, "forcing " + c.getMainName() + " to orgasm");
			} else if (c.isSodomized()) {
				if (w.tickle()) {
					w.append(t, "forcing " + c.getMainName() + " to laugh");
				} else if (c.getGender().equals("male")) {
					w.append(t, "torturing " + c.getMainName());
				} else {
					w.append(t, "sodomizing " + c.getMainName());
				}
			} else if (c.isBroadcasted()) {
				w.append(t, "broadcasting " + c.getMainName() + "'s humiliation");
			} else if (c.tempted) {
				w.append(t, "giving " + c.mainName + " all the pleasure " + c.heShe() + " wants");
			}
			w.append(t, ".  Any additional orders would simply confuse them right now.");
		} else if (c.isSurrounded()) {
			int defilers = 0;
			String PAINname = "PAIN";
			String INJUname = "INJU";
			if (w.tickle()) {
				PAINname = "TICK";
				INJUname = "ANTI";
			}
			Boolean plusPossible = false;
			Boolean orgyPossible = false;
			if (c.getGrind() == false) {
				JButton Grind = new JButton(GrindAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -60);
				      }
				};
				Grind.setToolTipText("<html><center>Inflicts HATE along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Can cause tier-1 Morality or Dignity Break</center></html>");
				p.add(Grind);
				Grind.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("1"),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 7) {
					Grind.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Grind.getActionMap().put("pressed",GrindAction);
			}
			if (c.getCaress() == false) {
				JButton Caress = new JButton(CaressAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -60);
				      }
				};
				Caress.setToolTipText("<html><center>Inflicts PLEA along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Can cause tier-1 Innocence or Confidence Break</center></html>");
				p.add(Caress);
				Caress.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("2"),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 8) {
					Caress.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Caress.getActionMap().put("pressed",CaressAction);
			}
			if (c.getPummel() == false) {
				JButton Pummel = new JButton(PummelAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -60);
				      }
				};
				Pummel.setToolTipText("<html><center>Inflicts " + INJUname + " along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Can cause tier-1 Morality or Confidence Break</center></html>");
				p.add(Pummel);
				Pummel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("3"),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 9) {
					Pummel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Pummel.getActionMap().put("pressed",PummelAction);
			}
			if (c.getHumiliate() == false) {
				JButton Humiliate = new JButton(HumiliateAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -60);
				      }
				};
				Humiliate.setToolTipText("<html><center>Inflicts EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Can cause tier-1 Innocence or Dignity Break</center></html>");
				p.add(Humiliate);
				Humiliate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("4"),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 10) {
					Humiliate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Humiliate.getActionMap().put("pressed",HumiliateAction);
			}
			if (c.getCurrentHATE() >= 10000) {
				defilers++;
				JButton Inseminate = new JButton(InseminateAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -60);
				      }
				};
				if (inseminated  > 0) {
					Inseminate.setBackground(PURPLISH);
					if (inseminated == 1) {
						Inseminate.setText("Inseminate+");
						plusPossible = true;
					} else {
						Inseminate.setText("Orgy");
						orgyPossible = true;
					}
				} else {
					Inseminate.setBackground(YELLOWISH);
				}
				if (c.temptReq < 100000 && inseminated != 2) {
					Inseminate.setBackground(REDDISH);
				}
				Inseminate.setToolTipText("<html><center>Inflicts HATE and PLEA along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
				if (inseminated == 1) {
					Inseminate.setToolTipText("<html><center>Inflicts HATE, PLEA and " + INJUname + " along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
				} else if (inseminated == 2) {
					Inseminate.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
				}
				p.add(Inseminate);
				Inseminate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("5"),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 11) {
					Inseminate.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Inseminate.getActionMap().put("pressed",InseminateAction);
			}
			if (c.getCurrentPLEA() >= 10000) {
				defilers++;
				JButton ForceOrgasm = new JButton(ForceOrgasmAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -60);
				      }
				};
				if (orgasming > 0) {
					ForceOrgasm.setBackground(PURPLISH);
					if (orgasming == 1) {
						ForceOrgasm.setText("Force Orgasm+");
						plusPossible = true;
					} else {
						ForceOrgasm.setText("Orgy");
						orgyPossible = true;
					}
				} else {
					ForceOrgasm.setBackground(YELLOWISH);
				}
				ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA and " + INJUname + " along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
				if (orgasming == 1) {
					ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA," + INJUname + ", and EXPO along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
				} else if (orgasming == 2) {
					ForceOrgasm.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
				}
				p.add(ForceOrgasm);
				ForceOrgasm.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("6"),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 12) {
					ForceOrgasm.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				ForceOrgasm.getActionMap().put("pressed",ForceOrgasmAction);
			}
			if (c.getCurrentINJU() >= 10000) {
				defilers++;
				JButton Sodomize = new JButton(SodomizeAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -60);
				      }
				};
				if (sodomized > 0) {
					Sodomize.setBackground(PURPLISH);
					if (sodomized == 1) {
						if (w.tickle()) {
							Sodomize.setText("Force Laughter+");
						} else if (c.getGender().equals("male")) {
							Sodomize.setText("Torture+");
						} else {
							Sodomize.setText("Sodomize+");
						}
						plusPossible = true;
					} else {
						Sodomize.setText("Orgy");
						orgyPossible = true;
					}
				} else {
					Sodomize.setBackground(YELLOWISH);
				}
				if (c.temptReq < 100000 && sodomized != 2) {
					Sodomize.setBackground(REDDISH);
				}
				Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + " and EXPO along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
				if (sodomized == 1) {
					Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + ", EXPO, and HATE along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
				} else if (sodomized == 2) {
					Sodomize.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
				}
				p.add(Sodomize);
				Sodomize.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("7"),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 13) {
					Sodomize.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Sodomize.getActionMap().put("pressed",SodomizeAction);
			}
			if (c.getCurrentEXPO() >= 10000) {
				defilers++;
				JButton Broadcast = new JButton(BroadcastAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -60);
				      }
				};
				if (broadcasted > 0) {
					Broadcast.setBackground(PURPLISH);
					if (broadcasted == 1) {
						Broadcast.setText("Broadcast+");
						plusPossible = true;
					} else {
						Broadcast.setText("Orgy");
						orgyPossible = true;
					}
				} else {
					Broadcast.setBackground(YELLOWISH);
				}
				Broadcast.setToolTipText("<html><center>Inflicts EXPO and HATE along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
				if (broadcasted == 1) {
					Broadcast.setToolTipText("<html><center>Inflicts EXPO, HATE, and PLEA along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
				} else if (broadcasted == 2) {
					Broadcast.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
				}
				p.add(Broadcast);
				Broadcast.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("8"),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 14) {
					Broadcast.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Broadcast.getActionMap().put("pressed",BroadcastAction);
			}
			long currentTemptReq = c.temptReq;
			if (w.finalBattle) {
				currentTemptReq = currentTemptReq*10;
			}
			if (c.getCurrentPLEA() >= currentTemptReq && c.vVirg && c.aVirg && c.cVirg == false && c.modest == false && c.ruthless == false && c.usingSlaughter == false && c.usingDetonate == false && (c.temptReq < 100000 || w.finalBattle == false)) {
				JButton Tempt = new JButton(TemptAction) {
					public Point getToolTipLocation(MouseEvent e) {
						return new Point(0, -60);
					}
				};
				Tempt.setToolTipText("<html><center>Inflicts extremely high PLEA and EXPO<br>but decreases other circumstances to zero and does not inflict trauma<br>Causes and intensifies Morality/Confidence Distortion</center></html>");
				Tempt.setBackground(PURPLISH);
				p.add(Tempt);
				Tempt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("9"),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 14) {
					Tempt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Tempt.getActionMap().put("pressed",TemptAction);
			}
			if (defilers > 0) {
				if (defilers > 1) {
					w.append(t, "  " + defilers + " defiler actions possible.");
				} else if (defilers == 1) {
					w.append(t, "  1 defiler action possible.");
				}
				int difference = 0;
				if (orgyPossible) {
					String firstName = "";
					String secondName = "";
					int duration = 0;
					int opening = c.getSurroundDuration();
					for (int i = 0; i < 3; i++) {
						if (w.getCombatants()[i] != null && w.getCombatants()[i] != c) {
							if (firstName.length() == 0) {
								firstName = w.getCombatants()[i].getMainName();
								duration = w.getCombatants()[i].getSurroundDuration();
							} else {
								secondName = w.getCombatants()[i].getMainName();
							}
						}
					}
					w.append(t, "  Orgy with " + firstName + " and " + secondName);
					if (duration > opening) {
						difference = duration - opening;
						w.append(t, " will cause them");
					} else if (opening > duration) {
						difference = opening - duration;
						w.append(t, " will allow " + c.getMainName());
					}
					if (difference > 1) {
						w.append(t, " to escape " + difference + " turns early.");
					} else if (difference == 1) {
						w.append(t, " to escape 1 turn early.");
					} else {
						w.append(t, " does not allow any of them to escape early.");
					}
				} else if (plusPossible) {
					int opening = c.getSurroundDuration();
					for (int i = 0; i < 3; i++) {
						if (w.getCombatants()[i] != null && w.getCombatants()[i] != c) {
							String defilementType = "";
							if (w.getCombatants()[i].isInseminated() && c.getHATELevel() >= 3) {
								defilementType = "Inseminate";
							} else if (w.getCombatants()[i].isOrgasming() && c.getPLEALevel() >= 3) {
								defilementType = "Force Orgasm";
							} else if (w.getCombatants()[i].isSodomized() && c.getINJULevel() >= 3) {
								if (w.tickle()) {
									defilementType = "Force Laughter";
								} else if (c.getGender().equals("male")) {
									defilementType = "Torture";
								} else {
									defilementType = "Sodomize";
								}
							} else if (w.getCombatants()[i].isBroadcasted() && c.getEXPOLevel() >= 3) {
								defilementType = "Broadcast";
							}
							if (defilementType.length() > 0) {
								w.append(t, "  " + defilementType + " with " + w.getCombatants()[i].getMainName());
								if (opening > w.getCombatants()[i].getSurroundDuration()) {
									w.append(t, " will allow " + c.getMainName());
									difference = opening - w.getCombatants()[i].getSurroundDuration();
								} else if (w.getCombatants()[i].getSurroundDuration() > opening) {
									w.append(t, " will allow " + w.getCombatants()[i].getMainName());
									difference = w.getCombatants()[i].getSurroundDuration() - opening;
								}
								if (difference > 1) {
									w.append(t, " to escape " + difference + " turns early.");
								} else if (difference == 1) {
									w.append(t, " to escape 1 turn early.");
								} else {
									w.append(t, " does not allow either of them to escape early.");
								}
								difference = 0;
							}
						}
					}
				}
			}
		} else if (c.isCaptured()) {
			if (w.usedForsaken == null) {
				w.append(t, "\n\n" + c.getMainName() + " is captured by your Commander.  Any attempts to help by other Demons would simply get in the way.");
			} else {
				w.append(t, "\n\n" + c.getMainName() + " is engaged in combat with " + w.usedForsaken.mainName + ".  There's no room for the Demons to get involved.");
			}
		} else {
			String PAINname = "PAIN";
			String INJUname = "INJU";
			if (w.tickle()) {
				PAINname = "TICK";
				INJUname = "ANTI";
			}
			JButton Attack = new JButton(AttackAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			Attack.setToolTipText("Inflicts " + PAINname);
			Attack.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("3"),"pressed");
			if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 5) {
				Attack.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
			}
			Attack.getActionMap().put("pressed",AttackAction);
			if (w.finalBattle && w.getTechs()[44].isOwned()) {
				Attack.setBackground(YELLOWISH);
			}
			JButton Slime = new JButton(SlimeAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			Slime.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("2"),"pressed");
			if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 4) {
				Slime.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
			}
			Slime.getActionMap().put("pressed",SlimeAction);
			Slime.setToolTipText("Inflicts DISG");
			if (w.finalBattle && w.getTechs()[43].isOwned() && c.isHypnotized()) {
				Slime.setBackground(YELLOWISH);
			}
			JButton Taunt = new JButton(TauntAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			Taunt.setToolTipText("Inflicts SHAM");
			Taunt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("4"),"pressed");
			if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 6) {
				Taunt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
			}
			Taunt.getActionMap().put("pressed",TauntAction);
			if (w.finalBattle && w.getTechs()[45].isOwned() && c.isParasitized() && c.surroundPossible(w)) {
				Taunt.setBackground(YELLOWISH);
			}
			JButton Threaten = new JButton(ThreatenAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			Threaten.setToolTipText("Inflicts FEAR");
			Threaten.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("1"),"pressed");
			if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 3) {
				Threaten.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
			}
			Threaten.getActionMap().put("pressed",ThreatenAction);
			Boolean impregnatedAlly = false;
			for (int i = 0; i < 3; i++) {
				if (i != c.getNumber() && w.getCast()[i] != null) {
					if (w.getCast()[i].isImpregnated() && w.getCast()[i].alive) {
						impregnatedAlly = true;
					}
				}
			}
			if (w.finalBattle && w.getTechs()[42].isOwned() && impregnatedAlly) {
				Threaten.setBackground(YELLOWISH);
			}
			p.add(Threaten);
			p.add(Slime);
			p.add(Attack);
			p.add(Taunt);
			if (c.surroundPossible(w)) {
				JButton Surround = new JButton(SurroundAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -60);
				      }
				};
				Surround.setBackground(YELLOWISH);
				Surround.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && (w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 1 || (w.getTechs()[31].isOwned() && c.isSurrounded() == false && w.getActions()[w.getCurrentAction()] >= c.getNumber()*14+7 && w.getActions()[w.getCurrentAction()] <= c.getNumber()*14+14))) {
					Surround.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Surround.getActionMap().put("pressed",SurroundAction);
				p.add(Surround);
			}
			if ((c.surroundPossible(w) == false || w.upgradedCommander()) && w.getCapturesPossible() > 0 && (c.getDefenseLevel() < 9000 || w.getBodyStatus()[24]) && w.commanderFree()) {
				JButton Capture = new JButton(CaptureAction){
					public Point getToolTipLocation(MouseEvent e) {
				        return new Point(0, -90);
				      }
				};
				Capture.setBackground(PURPLISH);
				String description = "<html><center>Constantly inflicts ";
				if (w.getBodyStatus()[26]) {
					int types = 2;
					String[] damages = new String[4];
					if (w.getBodyStatus()[19]) {
						damages[0] = "HATE";
					} else if (w.getBodyStatus()[20]) {
						damages[0] = "PLEA";
					} else if (w.getBodyStatus()[21]) {
						if (w.tickle()) {
							damages[0] = "ANTI";
						} else {
							damages[0] = "INJU";
						}
					} else if (w.getBodyStatus()[22]) {
						damages[0] = "EXPO";
					}
					if (w.getBodyStatus()[11]) {
						damages[1] = "HATE";
						damages[2] = "PLEA";
					} else if (w.getBodyStatus()[12]) {
						damages[1] = "PLEA";
						if (w.tickle()) {
							damages[2] = "ANTI";
						} else {
							damages[2] = "INJU";
						}
					} else if (w.getBodyStatus()[13]) {
						if (w.tickle()) {
							damages[1] = "ANTI";
						} else {
							damages[1] = "INJU";
						}
						damages[2] = "EXPO";
					} else if (w.getBodyStatus()[14]) {
						damages[1] = "EXPO";
						damages[2] = "HATE";
					}
					if (w.getBodyStatus()[3]) {
						damages[3] = "HATE";
					} else if (w.getBodyStatus()[4]) {
						damages[3] = "PLEA";
					} else if (w.getBodyStatus()[5]) {
						if (w.tickle()) {
							damages[3] = "ANTI";
						} else {
							damages[3] = "INJU";
						}
					} else if (w.getBodyStatus()[6]) {
						damages[3] = "EXPO";
					}
					if (damages[1].equals(damages[0]) == false && damages[2].equals(damages[0]) == false) {
						types++;
					}
					if (damages[3].equals(damages[0]) == false && damages[3].equals(damages[1]) == false && damages[3].equals(damages[2]) == false) {
						types++;
					}
					if (types == 2) {
						description = description + damages[0] + " and ";
						if (damages[0].equals(damages[1])) {
							description = description + damages[2];
						} else {
							description = description + damages[1];
						}
					} else if (types == 3) {
						description = description + damages[0] + ", ";
						if (damages[0].equals(damages[1])) {
							description = description + damages[3] + ", and " + damages[2];
						} else if (damages[0].equals(damages[2])) {
							description = description + damages[1] + ", and " + damages[3];
						} else if (damages[0].equals(damages[3]) || damages[1].equals(damages[3])) {
							description = description + damages[1] + ", and " + damages[2];
						} else {
							description = description + damages[2] + ", and " + damages[1];
						}
					} else {
						description = description + damages[0] + ", " + damages[1] + ", " + damages[3] + ", and " + damages[2];
					}
					description = description + " along with<br>all four traumas";
				} else if (w.getBodyStatus()[19]) {
					description = description + "HATE along with<br>FEAR, DISG, " + PAINname + ", and SHAM";
				} else if (w.getBodyStatus()[20]) {
					description = description + "PLEA along with<br>DISG, " + PAINname + ", SHAM, and FEAR";
				} else if (w.getBodyStatus()[21]) {
					description = description + INJUname + " along with<br>" + PAINname + ", SHAM, FEAR, and DISG";
				} else if (w.getBodyStatus()[22]) {
					description = description + "EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname;
				} else if (w.getBodyStatus()[18]) {
					String[] damages = new String[3];
					if (w.getBodyStatus()[3]) {
						damages[1] = "HATE";
					} else if (w.getBodyStatus()[4]) {
						damages[1] = "PLEA";
					} else if (w.getBodyStatus()[5]) {
						damages[1] = INJUname;
					} else if (w.getBodyStatus()[6]) {
						damages[1] = "EXPO";
					}
					if (w.getBodyStatus()[11]) {
						damages[0] = "HATE";
						damages[2] = "PLEA";
					} else if (w.getBodyStatus()[12]) {
						damages[0] = "PLEA";
						damages[2] = INJUname;
					} else if (w.getBodyStatus()[13]) {
						damages[0] = INJUname;
						damages[2] = "EXPO";
					} else if (w.getBodyStatus()[14]) {
						damages[0] = "EXPO";
						damages[2] = "HATE";
					}
					if (damages[0].equals(damages[2])) {
						description = description + damages[0] + " and " + damages[1];
					} else if (damages[1].equals(damages[2])) {
						description = description + damages[1] + " and " + damages[0];
					} else {
						description = description + damages[0] + ", " + damages[1] + ", and " + damages[2];
					}
					description = description + " along with<br>all four traumas";
				} else if (w.getBodyStatus()[11]) {
					description = description + "HATE and PLEA along with<br>FEAR, DISG, " + PAINname + ", and SHAM";
				} else if (w.getBodyStatus()[12]) {
					description = description + "PLEA and " + INJUname + " along with<br>DISG, " + PAINname + ", SHAM, and FEAR";
				} else if (w.getBodyStatus()[13]) {
					description = description + INJUname + " and EXPO along with<br>" + PAINname + ", SHAM, FEAR, and DISG";
				} else if (w.getBodyStatus()[14]) {
					description = description + "EXPO and HATE along with<br>SHAM, FEAR, DISG, and " + PAINname;
				} else if (w.getBodyStatus()[10]) {
					Boolean firstFound = false;
					if (w.getBodyStatus()[3]) {
						description = description + "HATE";
						firstFound = true;
					}
					if (w.getBodyStatus()[4]) {
						if (firstFound) {
							description = description + " and ";
						}
						description = description + "PLEA";
						firstFound = true;
					}
					if (w.getBodyStatus()[5]) {
						if (firstFound) {
							description = description + " and ";
						}
						description = description + INJUname;
						firstFound = true;
					}
					if (w.getBodyStatus()[6]) {
						description = " and EXPO";
					}
					description = description + " along with<br>all four traumas";
				} else if (w.getBodyStatus()[3]) {
					description = description + "HATE along with<br>FEAR, DISG, " + PAINname + ", and SHAM";
				} else if (w.getBodyStatus()[4]) {
					description = description + "PLEA along with<br>DISG, " + PAINname + ", SHAM, and FEAR";
				} else if (w.getBodyStatus()[5]) {
					description = description + INJUname + " along with<br>" + PAINname + ", SHAM, FEAR, and DISG";
				} else if (w.getBodyStatus()[6]) {
					description = description + "EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname;
				} else {
					description = "<html><center>Surrounds the target";
				}
				description = description + "<br>for ";
				if (w.getBodyStatus()[25]) {
					description = description + "eight";
				} else if (w.getBodyStatus()[15]) {
					description = description + "six";
				} else if (w.getBodyStatus()[9]) {
					description = description + "five";
				} else if (w.getBodyStatus()[7]) {
					description = description + "four";
				} else if (w.getBodyStatus()[1]) {
					description = description + "three";
				} else {
					description = description + "two";
				}
				description = description + " rounds";
				if (w.getBodyStatus()[8]) {
					description = description + " (";
					if (w.getCapturesPossible() == 4) {
						description = description + "four";
					} else if (w.getCapturesPossible() == 3) {
						description = description + "three";
					} else if (w.getCapturesPossible() == 2) {
						description = description + "two";
					} else if (w.getCapturesPossible() == 1) {
						description = description + "one";
					}
					description = description + " left)";
				}
				if (w.getBodyStatus()[11]) {
					description = description + "<br>Above 10k HATE, causes tier-2 Morality Break";
				} else if (w.getBodyStatus()[12]) {
					description = description + "<br>Above 10k PLEA, causes tier-2 Innocence Break";
				} else if (w.getBodyStatus()[13]) {
					description = description + "<br>Above 10k " + INJUname + ", causes tier-2 Confidence Break";
				} else if (w.getBodyStatus()[14]) {
					description = description + "<br>Above 10k EXPO, causes tier-2 Dignity Break";
				}
				if (w.getBodyStatus()[19]) {
					description = description + "<br>Above 1000% Impregnation effectiveness, causes Total Morality Break";
				} else if (w.getBodyStatus()[20]) {
					description = description + "<br>Above 1000% Hypnosis effectiveness, causes Total Innocence Break";
				} else if (w.getBodyStatus()[21]) {
					description = description + "<br>Above 1000% Drain effectiveness, causes Total Confidence Break";
				} else if (w.getBodyStatus()[22]) {
					description = description + "<br>Above 1000% Parasitism effectiveness, causes Total Dignity Break";
				}
				if (w.usedForsaken != null) {
					description = "<html><center>Grab with " + w.usedForsaken.mainName + " for " + w.usedForsaken.compatibility(c) + " rounds<br>" + w.usedForsaken.describeCombatStyle(w, false);
				}
				description = description + "</center></html>";
				Capture.setToolTipText(description);
				Capture.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, 0),"pressed");
				if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber()*14 + 2) {
					Capture.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
				}
				Capture.getActionMap().put("pressed",CaptureAction);
				p.add(Capture);
			}
		}
		int defeated = 0;
		int targets = 0;
		for (int i = 0; i < 3; i++) {
			if (w.getCombatants()[i] != null) {
				if (w.finalBattle && (w.getCombatants()[i].alive == false || w.getCombatants()[i].resolve <= 0)) {
					defeated++;
				} else if (w.getCombatants()[i].isCaptured() == false && (w.getCombatants()[i].isSurrounded() == false || (w.getCombatants()[i].isDefiled() == false && (w.getCombatants()[i].getHATELevel() >= 3 || w.getCombatants()[i].getPLEALevel() >= 3 || w.getCombatants()[i].getINJULevel() >= 3 || w.getCombatants()[i].getEXPOLevel() >= 3 || w.getCombatants()[i].grind == false || w.getCombatants()[i].caress == false || w.getCombatants()[i].pummel == false || w.getCombatants()[i].humiliate == false)))) {
					if (w.getCombatants()[i] != c) {
						targets++;
					}
				}
			}
		}
		if (w.getCombatants()[1] != null && defeated < 2) {
			class BackButton extends AbstractAction {
				public BackButton(String text, String desc) {
					super(text);
					putValue(SHORT_DESCRIPTION, desc);
				}
				public void actionPerformed(ActionEvent e) {
					w.append(t, "\n\n" + w.getSeparator() + "\n");
					PickTarget(t, p, f, w);
					if (w.tutorialResponse()) {
						if (w.getBattleRound() == 6 && c == w.getCast()[2]) {
							w.grayAppend(t, "\n\n(We created another opening last turn, but because we've already grabbed Miracle once, her defense level has gone up.  We'll need at least three opening levels to grab her again.  Fortunately, she's taken enough FEAR and SHAM damage now that it should be easy to push her over 100 in both.  Target Miracle and then Threaten her.)");
						}
					}
				}
			}
			Action BackAction = new BackButton("Back", "Hotkey:");
			JButton Back = new JButton(BackAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			Back.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0),"pressed");
			Back.getActionMap().put("pressed",BackAction);
			p.add(Back);
		} else {
			p.add(Pass);
			int occupied = 0;
			for (int i = 0; i < 3; i++) {
				if (w.getCombatants()[i] != null) {
					if (w.getCombatants()[i].isSurrounded() && w.getCombatants()[i].getSurroundDuration() > 0) {
						if (w.getCombatants()[i].getSurroundDuration() > 0) {
							occupied += w.getCombatants()[i].getSurroundDuration();
						} else {
							occupied++;
						}
					} else if (w.getCombatants()[i].isCaptured()) {
						occupied += w.getCaptureDuration() - w.getCombatants()[i].getCaptureProgression() + 1;
					}
				}
			}
			final int occupiedBonus = occupied/5;
			class RetreatButton extends AbstractAction {
				public RetreatButton(String text, String desc) {
					super(text);
					putValue(SHORT_DESCRIPTION, desc);
				}
				public void actionPerformed(ActionEvent e) {
					p.removeAll();
					w.append(t, "\n\n" + w.getSeparator() + "\n\n");
					if (occupiedBonus > 0) {
						w.append(t, "Retreat and end the battle immediately for +" + occupiedBonus + " Evil Energy?");
					} else {
						w.append(t, "Really retreat?  You will not gain any bonus Evil Energy!");
					}
					JButton Confirm = new JButton("Confirm");
					Confirm.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							p.removeAll();
							w.append(t, "\n\n" + w.getSeparator() + "\n\n");
							String[] trapped = new String[]{null, null, null};
							String[] free = new String[]{null, null, null};
							int trappedNumber = 0;
							for (int i = 0; i < 3; i++) {
								if (w.getCombatants()[i] != null) {
									if (w.getCombatants()[i].isSurrounded() || w.getCombatants()[i].isCaptured()) {
										for (int j = 0; j < 3; j++) {
											if (trapped[j] == null) {
												trapped[j] = w.getCombatants()[i].getMainName();
												trappedNumber++;
												j = 3;
											}
										}
									} else {
										for (int j = 0; j < 3; j++) {
											if (free[j] == null) {
												free[j] = w.getCombatants()[i].getMainName();
												j = 3;
											}
										}
									}
								}
							}
							if (w.getCombatants()[1] == null) {
								for (int i = 0; i < 3; i++) {
									if (w.getCast()[i] != null && w.getCast()[i].equals(w.getCombatants()[0]) == false) {
										if (free[0] == null) {
											free[0] = w.getCast()[i].mainName;
										} else if (free[1] == null) {
											free[1] = w.getCast()[i].mainName;
										} else {
											free[2] = w.getCast()[i].mainName;
										}
									}
								}
							} else if (w.getCombatants()[2] == null) {
								for (int i = 0; i < 3; i++) {
									if (w.getCast()[i] != null && w.getCast()[i].equals(w.getCombatants()[0]) == false && w.getCast()[i].equals(w.getCombatants()[1]) == false) {
										if (free[0] == null) {
											free[0] = w.getCast()[i].mainName;
										} else if (free[1] == null) {
											free[1] = w.getCast()[i].mainName;
										} else {
											free[2] = w.getCast()[i].mainName;
										}
									}
								}
							}
							w.append(t, "You order your Demons to flee back into the tunnels beneath the city along with their captive victims.  ");
							if (w.getCast()[1] == null) {
								if (trappedNumber == 0) {
									w.append(t, "However, " + free[0] + " is quick to pursue, cutting your forces down from behind and stopping them from taking any significant number of civilians back to the hive.");
								} else {
									w.append(t, trapped[0] + " is unable to follow until plenty of civilians are already on their way to the hive.");
								}
							} else if (w.getCast()[2] == null) {
								if (trappedNumber == 0) {
									w.append(t, "However, the two Chosen cut your forces down from behind, freeing most of the civilians before they can be brought to the hive.");
								} else if (trappedNumber == 1) {
									w.append(t, "With " + trapped[0] + " unable to give chase, the risk of splitting the team forces " + free[0] + " to give up and let you take the civilians to the hive.");
								} else {
									w.append(t, "The Chosen have to finish dealing with their own problems before they can try to stop you, and by then, plenty of your forces have escaped.");
								}
							} else if (trappedNumber == 0 || occupiedBonus == 0) {
								w.append(t, "However, the three Chosen cut your forces down from behind, freeing most of the civilians before they can be brought to the hive.");
							} else if (trappedNumber == 1) {
								w.append(t, free[0] + " and " + free[1] + " try to give chase, but with " + trapped[0] + " unable to follow, they're forced to give up due to the risk of splitting the team.");
							} else if (trappedNumber == 2) {
								w.append(t, free[0] + " tries to stop them, but with " + trapped[0] + " and " + trapped[1] + " unable to help, you're able to get plenty of victims to the hive.");
							} else {
								w.append(t, "The Chosen have to finish dealing with their own problems before they can try to stop you, and by then, plenty of your forces have escaped.");
							}
							if (occupiedBonus > 0) {
								w.append(t, "\n\n+" + occupiedBonus + " Evil Energy");
							}
							advanceAction(p, w, 43);
							w.addEnergy(occupiedBonus);
							JButton Continue = new JButton("Continue");
							Continue.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									PostBattle(t, p, f, w);
								}
							});
							p.add(Continue);
							p.validate();
							p.repaint();
						}
					});
					p.add(Confirm);
					JButton Cancel = new JButton("Cancel");
					Cancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							w.append(t, "\n\n" + w.getSeparator() + "\n");
							PickTarget(t, p, f, w);
						}
					});
					p.add(Cancel);
					p.validate();
					p.repaint();
				}
			}
			Action RetreatAction = new RetreatButton("Retreat (" + occupiedBonus + ")", "End battle immediately for +" + occupiedBonus + " EE");
			JButton Retreat = new JButton(RetreatAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			if (w.getTechs()[19].isOwned() && w.finalBattle == false) {
				p.add(Retreat);
			}
		}
		if (w.writePossible()) {
			addWriteButton(p, w);
		}
		p.validate();
		//f.pack();
		p.repaint();
	}
	
	public static void EnemyTurn(JTextPane t, JPanel p, JFrame f, WorldState w, Chosen[] initiative, int progress) {
		Boolean endgame = w.finalBattle;
		Boolean actorFound = false;
		while (actorFound == false && progress < initiative.length && initiative[progress] != null) {
			w.clearBonus(progress);
			actorFound = true;
			if (w.finalBattle) {
				if (initiative[progress].alive == false || initiative[progress].resolve <= 0) {
					actorFound = false;
					progress++;
				}
			}
		}
		if (actorFound) {
			if (w.getCaptureTarget() == initiative[progress] || initiative[progress].isCaptured()) {
				if (w.usedForsaken == null) {
					w.BeCaptured(t, p, f, w, initiative[progress]);
				} else {
					w.usedForsaken.captureChosen(t, p, f, w, initiative[progress]);
				}
			} else if (w.getSurroundTarget() == initiative[progress] || initiative[progress].isSurrounded()) {
				initiative[progress].BeSurrounded(t, p, f, w);
			} else {
				initiative[progress].TakeTurn(t, p, f, w);
			}
			progress++;
		}
		p.removeAll();
		final int currentProgress = progress;
		Boolean moreTurns = true;
		if (progress > 2) {
			moreTurns = false;
		} else if (initiative[progress] == null) {
			moreTurns = false;
		} else if (w.finalBattle && (initiative[progress].alive == false || initiative[progress].resolve <= 0)) {
			progress++;
			if (progress > 2) {
				moreTurns = false;
			} else if (initiative[progress] == null) {
				moreTurns = false;
			} else if (w.finalBattle && (initiative[progress].alive == false || initiative[progress].resolve <= 0)) {
				progress++;
				if (progress > 2) {
					moreTurns = false;
				} else if (initiative[progress] == null) {
					moreTurns = false;
				} else if (w.finalBattle && (initiative[progress].alive == false || initiative[progress].resolve <= 0)) {
					moreTurns = false;
				}
			}
		}
		if (actorFound == false) {
			int defeated = 0;
			for (int i = 0; i < 3; i++) {
				if (initiative[i] != null) {
					if (initiative[i].alive == false || initiative[i].resolve <= 0) {
						defeated++;
					}
				}
			}
			if (defeated < 3) {
				endgame = false;
				w.append(t, "\n\n" + w.getSeparator() + "\n\nThe Demons swarm across the city unopposed!");
			}
		} else {
			endgame = false;
		}
		if (endgame) {
			int captured = 0;
			int dead = 0;
			Chosen[] survivors = new Chosen[3];
			Chosen[] killed = new Chosen[3];
			for (int i = 0; i < 3; i++) {
				if (w.getCast()[i].alive) {
					survivors[captured] = w.getCast()[i];
					captured++;
				} else {
					killed[dead] = w.getCast()[i];
					dead++;
				}
			}
			w.append(t, "\n\n" + w.getSeparator() + "\n\n");
			if (captured == 3) {
				w.append(t, "Finally, all three of the Chosen have surrendered to your forces.  This is a flawless victory - you couldn't have hoped for a better result.  By the time the reinforcements from other cities arrive, your Demonic Barrier has already reached full strength, and no more Chosen can enter without immediately losing their powers and joining the ranks of your captives.\n\nThe Demons escort " + w.getCast()[0].getMainName() + ", " + w.getCast()[1].getMainName() + ", and " + w.getCast()[2].getMainName() + " to your throne room, where you will begin to train them into your own loyal servants...");
			} else if (captured == 2) {
				w.append(t, "With both " + survivors[0].getMainName() + " and " + survivors[1].getMainName() + " broken, your victory is complete.  By the time the reinforcements from other cities arrive, your Demonic Barrier has already reached full strength, and no more Chosen can enter without immediately losing their powers and joining the ranks of your captives.\n\n" + killed[0].getMainName() + "'s death was unfortunate - " + killed[0].heShe() + " would have made an excellent servant.  But you still have " + survivors[0].getMainName() + " and " + survivors[1].getMainName() + ".  The Demons escort them to your throne room so that their training can begin...");
			} else {
				w.append(t, "With " + survivors[0].getMainName() + " defeated, your takeover of the city is complete.  By the time the reinforcements from other cities arrive, your Demonic Barrier has already reached full strength, and no more Chosen can enter without immediately losing their powers and joining the ranks of your captives.\n\nThe deaths of " + killed[0].getMainName() + " and " + killed[1].getMainName() + " were very unfortunate - they would have made excellent servants.  But you still managed to hold onto one prize.  The Demons escort " + survivors[0].getMainName() + " into your throne room so that " + survivors[0].hisHer() + " training can begin...");
			}
			EndFinalBattle(t, p, f, w);
		} else if (moreTurns) {
			class ContinueButton extends AbstractAction {
				public ContinueButton(String text, String desc) {
					super(text);
					putValue(SHORT_DESCRIPTION, desc);
				}
				public void actionPerformed(ActionEvent e) {
					EnemyTurn(t, p, f, w, initiative, currentProgress);
				}
			}
			Action ContinueAction = new ContinueButton("Continue", "Hotkey:");
			JButton Continue = new JButton(ContinueAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
			Continue.getActionMap().put("pressed",ContinueAction);
			p.add(Continue);
			p.validate();
			//f.pack();
			p.repaint();
		} else {
			Chosen[] synch = new Chosen[0];
			for (int i = 0; i < 3; i++) {
				if (w.getCombatants()[i] != null) {
					int type = 0;
					if (w.getCombatants()[i].isInseminated()) {
						type = 1;
					} else if (w.getCombatants()[i].isOrgasming()) {
						type = 2;
					} else if (w.getCombatants()[i].isSodomized()) {
						type = 3;
					} else if (w.getCombatants()[i].isBroadcasted()) {
						type = 4;
					}
					if (type > 0) {
						for (int j = i+1; j < 3; j++) {
							if (w.getCombatants()[j] != null) {
								int otherType = 0;
								if (w.getCombatants()[j].isInseminated()) {
									otherType = 1;
								} else if (w.getCombatants()[j].isOrgasming()) {
									otherType = 2;
								} else if (w.getCombatants()[j].isSodomized()) {
									otherType = 3;
								} else if (w.getCombatants()[j].isBroadcasted()) {
									otherType = 4;
								}
								if (type == otherType) {
									if (synch.length == 0) {
										synch = new Chosen[]{w.getCombatants()[i], w.getCombatants()[j]};
									} else {
										synch = w.getCombatants();
									}
								}
							}
						}
					}
				}
			}
			if (synch.length > 1) {
				w.synchSurroundDurations(synch);
			}
			class ContinueButton extends AbstractAction {
				public ContinueButton(String text, String desc) {
					super(text);
					putValue(SHORT_DESCRIPTION, desc);
				}
				public void actionPerformed(ActionEvent e) {
					w.append(t, "\n\n" + w.getSeparator() + "\n\n");
					Boolean newChosen = false;
					if (w.getCast()[1] == null) {
						if (w.getTotalRounds() >= 18*(15-w.eventOffset)/15 || w.day > 20) {
							newChosen = true;
						}
					} else if (w.getCast()[2] == null) {
						if (w.getTotalRounds() >= 60*(15-w.eventOffset)/15 || w.day > 30) {
							newChosen = true;
						}
					}
					if ((w.evacComplete() || w.getBattleRound() < 4) && ((w.getCast()[1] != null && w.getTotalRounds() < 80*(15-w.eventOffset)/15 && w.day <= 30) || (w.getCast()[1] == null && w.getTotalRounds() < 28*(15-w.eventOffset)/15 && w.day <= 20))) {
						newChosen = false;
					}
					/*if (w.getCombatants()[0].isSurrounded() || w.getCombatants()[0].isCaptured()) {
						if (w.getCombatants()[1] == null) {
							newChosen = false;
						} else if (w.getCombatants()[1].isSurrounded() || w.getCombatants()[1].isCaptured()) {
							newChosen = false;
						}
					}*/
					int arrival = -1;
					int timerStandard = 10000;
					for (int i = 0; i < 3; i++) {
						if (w.decrementArrival(i)) {
							Chosen thisChosen = w.getCast()[i];
							Boolean successfulArrival = true;
							if (thisChosen == w.getCombatants()[0]) {
								successfulArrival = false;
							} else if (w.getCombatants()[1] != null) {
								if (w.getCombatants()[1] == thisChosen) {
									successfulArrival = false;
								}
							}
							if (w.getCast()[i] == null) {
								successfulArrival = false;
							}
							if (successfulArrival) {
								if (w.getArrivalTimer()[i] < timerStandard) {
									arrival = i;
									timerStandard = w.getArrivalTimer()[i];
								}
							}
						}
					}
					if (w.getCombatants()[2] != null) {
						arrival = -1;
					}
					if (newChosen) {
						p.removeAll();
						Chosen arrivingChosen = new Chosen();
						if (w.getCast()[1] == null) {
							arrivingChosen.setNumber(1);
						} else {
							arrivingChosen.setNumber(2);
						}
						arrivingChosen.generate(w);
						w.addChosen(arrivingChosen);
						w.addToCombat(arrivingChosen);
						if (arrivingChosen.type == Chosen.Species.SUPERIOR) {
							w.append(t, "A sudden ripple passes through your army, a psychic shockwave carrying an emotion rarely felt by Demons: fear.  Those with voices let loose roars of dismay, and the rest slither backward to gain some distance from the newcomer on the rooftop on the edge of the battlefield.\n\nRendered indistinct by the sun at its back, the newcomer's silhouette could belong to anyone at all.  But even though there's nothing special about the flash of light and the thunderclap of the Chosen's transformation, the Demons recognize the voice that rings out, and they know that one of their most dangerous foes has arrived.\n\n");
						} else {
							w.append(t, "As the battle rages below, an unfamiliar figure arrives on the nearby rooftops.  After watching for a moment, " + arrivingChosen.heShe() + " makes a fateful decision.  A loud crack rings through the air, light shining from above!\n\n");
						}
						arrivingChosen.say(t, "\"" + arrivingChosen.announcement() + "\"\n\n");
						arrivingChosen.transform(t, w);
						w.append(t, "\n\n");
						arrivingChosen.printGreeting(t, w);
						Chosen responding = w.getCombatants()[1];
						if (w.getCombatants()[1] == arrivingChosen) {
							responding = w.getCombatants()[0];
						} else if ((w.getCombatants()[1].isSurrounded() || w.getCombatants()[1].isCaptured()) && w.getCombatants()[0].isSurrounded() == false && w.getCombatants()[0].isCaptured() == false) {
							responding = w.getCombatants()[0];
						}
						if (responding.isSurrounded() == false && responding.isCaptured() == false) {
							w.append(t, "\n\n");
							responding.printResponse(t, w);
						}
						class ContinueButtonTwo extends AbstractAction {
							public ContinueButtonTwo(String text, String desc) {
								super(text);
								putValue(SHORT_DESCRIPTION, desc);
							}
							public void actionPerformed(ActionEvent e) {
								w.append(t, "\n\n" + w.getSeparator() + "\n");
								w.endTurn(t);
								w.append(t, "\n");
								PickTarget(t, p, f, w);
							}
						}
						Action ContinueActionTwo = new ContinueButtonTwo("Continue", "Hotkey:");
						JButton Continue = new JButton(ContinueActionTwo){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -30);
						      }
						};
						Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						Continue.getActionMap().put("pressed",ContinueActionTwo);
						p.add(Continue);
						p.validate();
						//f.pack();
						p.repaint();
					} else if (arrival >= 0) {
						p.removeAll();
						Chosen arrivingChosen = w.getCast()[arrival];
						arrivingChosen.say(t, "\"" + arrivingChosen.announcement() + "\"\n\n");
						arrivingChosen.transform(t, w);
						if (w.finalBattle) {
							w.addToCombat(arrivingChosen);
							w.append(t, "\n\n");
							w.finalBattleIntro(t, arrivingChosen);
						} else {
							Chosen responder = null;
							Boolean response = false;
							while (response == false) {
								if (Math.random() < 0.5) {
									if (w.getCombatants()[0].isSurrounded() == false && w.getCombatants()[0].isCaptured() == false && w.getRelationship(w.getCombatants()[0].getNumber(), arrivingChosen.getNumber()) != 0) {
										responder = w.getCombatants()[0];
										response = true;
									}
								} else {
									if (w.getCombatants()[1] != null) {
										if (w.getCombatants()[1].isSurrounded() == false && w.getCombatants()[1].isCaptured() == false && w.getRelationship(w.getCombatants()[1].getNumber(), arrivingChosen.getNumber()) != 0) {
											responder = w.getCombatants()[1];
											response = true;
										}
									}
								}
								if (w.getCombatants()[0].isSurrounded() || w.getCombatants()[0].isCaptured() || w.getRelationship(w.getCombatants()[0].getNumber(), arrivingChosen.getNumber()) == 0) {
									if (w.getCombatants()[1] == null) {
										response = true;
									} else {
										if (w.getCombatants()[1].isSurrounded() || w.getCombatants()[1].isCaptured() || w.getRelationship(w.getCombatants()[1].getNumber(), arrivingChosen.getNumber()) == 0) {
											response = true;
										}
									}
								}
							}
							w.addToCombat(arrivingChosen);
							if (responder != null) {
								w.append(t, "\n\n");
								responder.printGreetingAgain(t, w, arrivingChosen);
							}
						}
						class ContinueButtonTwo extends AbstractAction {
							public ContinueButtonTwo(String text, String desc) {
								super(text);
								putValue(SHORT_DESCRIPTION, desc);
							}
							public void actionPerformed(ActionEvent e) {
								w.append(t, "\n\n" + w.getSeparator() + "\n");
								if (w.endTurn(t)) {
									w.append(t, "\n");
									if (w.finalBattle) {
										DefeatScene(t, p, f, w);
									} else {
										p.removeAll();
										w.append(t, "The Demonic forces have been routed, and the stragglers flee back into their underground tunnels.  Crisis workers arrive to round up the remaining Thralls for purification.  Meanwhile, ");
										Chosen c = null;
										while (c == null) {
											c = w.getCombatants()[(int)(Math.random()*3)];
											if (c != null) {
												if (c.isSurrounded()) {
													c = null;
												}
											}
										}
										if (w.getCombatants()[1] == null) {
											w.append(t, c.getMainName() + " returns");
										} else {
											w.append(t, "the Chosen return");
										}
										w.append(t, " home to prepare for tomorrow's fight.\n\n");
										c.VictoryLine(t, p, f, w);
										JButton ContinueTwo = new JButton("Continue");
										ContinueTwo.addActionListener(new ActionListener() {
											@Override
											public void actionPerformed(ActionEvent e) {
												PostBattle(t, p, f, w);
											}
										});
										p.add(ContinueTwo);
										p.validate();
										//f.pack();
										p.repaint();
									}
								} else {
									w.append(t, "\n");
									PickTarget(t, p, f, w);
									if (w.tutorialResponse()) {
										if (w.getBattleRound() == 6) {
											if (w.getCast()[0].currentPAIN == 108) {
												w.grayAppend(t, "\n\n(The factors that determine when reinforcements show up are the personalities and relationships of the initially-targeted Chosen and the arriving Chosen.  This means that as long as their relationship doesn't change, Axiom will always show up on Round 6 when we go after Miracle.\n\nLet's target Axiom and then use Examine to see what she's like.)");
											} else {
												w.endTutorial();
											}
										} else if (w.getBattleRound() == 7) {
											if (w.getCast()[0].getCurrentFEAR() == 133) {
												w.grayAppend(t, "\n\n(We put another FEAR level on Miracle, but FEAR only provides an opening when one of the other Chosen is already surrounded.  Fortunately, another possible target has just arrived.  Target Spice and use Examine to see what we can expect from her.)");
											} else {
												w.endTutorial();
											}
										}
									}
								}
							}
						}
						Action ContinueActionTwo = new ContinueButtonTwo("Continue", "Hotkey:");
						JButton Continue = new JButton(ContinueActionTwo){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -30);
						      }
						};
						Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
						Continue.getActionMap().put("pressed",ContinueActionTwo);
						p.add(Continue);
						p.validate();
						//f.pack();
						p.repaint();
					} else if (w.endTurn(t)) {
						p.removeAll();
						if (w.finalBattle) {
							DefeatScene(t, p, f, w);
						} else {
							w.append(t, "The Demonic forces have been routed, and the stragglers flee back into their underground tunnels.  Crisis workers arrive to round up the remaining Thralls for purification.  Meanwhile, ");
							Chosen c = null;
							while (c == null) {
								c = w.getCombatants()[(int)(Math.random()*3)];
								if (c != null) {
									if (c.isSurrounded()) {
										c = null;
									}
								}
							}
							if (w.getCombatants()[1] == null) {
								w.append(t, c.getMainName() + " returns");
							} else {
								w.append(t, "the Chosen return");
							}
							w.append(t, " home to prepare for tomorrow's fight.\n\n");
							c.VictoryLine(t, p, f, w);
							JButton ContinueTwo = new JButton("Continue");
							ContinueTwo.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									PostBattle(t, p, f, w);
								}
							});
							p.add(ContinueTwo);
							p.validate();
							//f.pack();
							p.repaint();
						}
					} else {
						w.append(t, "\n");
						PickTarget(t, p, f, w);
						if (w.tutorialResponse()) {
							if (w.getBattleRound() == 2) {
								if (w.getCast()[0].getCurrentDISG() == 70) {
									w.grayAppend(t, "\n\n(With the right upgrades, high ANGST, or naturally high vulnerabilities, it's possible to reliably deal 100 or more trauma in a single turn, setting up openings very quickly.  But those don't apply here, so let's use Slime again.)");
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 3) {
								if (w.getCast()[0].getCurrentDISG() == 140) {
									if (w.tickle()) {
										w.grayAppend(t, "\n\n(Surrounding Miracle right now will only give us one turn to torment her.  In other situations, it might be a good idea to create another opening to increase the duration.  But since she's pretty weak to ANTI, and since we have the upgrade that increases circumstance damage, one turn should be plenty.  Surround her!)");
									} else {
										w.grayAppend(t, "\n\n(Surrounding Miracle right now will only give us one turn to torment her.  In other situations, it might be a good idea to create another opening to increase the duration.  But since she's pretty weak to INJU, and since we have the upgrade that increases circumstance damage, one turn should be plenty.  Surround her!)");
									}
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 4) {
								if (w.getCast()[0].isSurrounded()) {
									if (w.tickle()) {
										w.grayAppend(t, "\n\n(Tickle deals ANTIcipation damage, whose main effect is to multiply other circumstance damage, so it's often a good idea to start there.  Try Tickling her.)");
									} else {
										w.grayAppend(t, "\n\n(Pummel deals INJU damage, whose main effect is to multiply other circumstance damage, so it's often a good idea to start there.  Try Pummeling her.)");
									}
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 5) {
								if (w.getCast()[0].getCurrentINJU() == 126) {
									if (w.tickle()) {
										w.grayAppend(t, "\n\n(She escaped quickly, but not before getting level 1 ANTI.  All circumstances, in addition to their other effects, also apply an extra x2 multiplier to their associated trauma.  For ANTIcipation, that's TICKle.  Even though Miracle doesn't usually take much TICK damage, that extra multiplier will let us use it to create another opening.  Tickle her!");
									} else {
										w.grayAppend(t, "\n\n(She escaped quickly, but not before getting level 1 INJU.  All circumstances, in addition to their other effects, also apply an extra x2 multiplier to their associated trauma.  For INJU, that's PAIN.  Even though Miracle doesn't usually take much PAIN damage, that extra multiplier will let us use it to create another opening.  Attack her!");
									}
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 8) {
								if (w.getCast()[1].captured) {
									w.grayAppend(t, "\n\n(The EXPO damage on Spice will increase the circumstance damage (but not the trauma damage) taken by the other two Chosen (but not by Spice herself).  From the extermination bar, we can see that the Chosen won't finish killing the Demons until next turn, so we can still spend another turn setting up.  Taunt Miracle in order to create another opening on her.)");
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 9) {
								if (w.getCast()[0].getCurrentSHAM() == 105) {
									w.grayAppend(t, "\n\n(As long as one of the Chosen is surrounded or captured, the battle won't immediately end when extermination is completed, but any Chosen surrounded or captured after that point will take off into the sky afterward and become ungrabbable.  That means that it's a very good idea to set up situations like this where you can grab Chosen immediately before extermination is completed.  Take this chance to surround Miracle!)");
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 10) {
								if (w.getCast()[0].isSurrounded()) {
									if (w.tickle()) {
										w.grayAppend(t, "\n\n(This surround duration isn't long enough to actually break Miracle, so we want to focus on dealing lots of trauma damage to create some even higher openings.  This is especially important because using a TICK opening causes it to stop providing further openings until it levels up again.  HATE (from Grind) and PLEA (from Caress) both increase the trauma multiplier.  PLEA increases it by more, but HATE also increases the circumstance multiplier.  Because we're planning to apply both, let's start with Grind.)");
									} else {
										w.grayAppend(t, "\n\n(This surround duration isn't long enough to actually break Miracle, so we want to focus on dealing lots of trauma damage to create some even higher openings.  This is especially important because using a PAIN opening causes it to stop providing further openings until it levels up again.  HATE (from Grind) and PLEA (from Caress) both increase the trauma multiplier.  PLEA increases it by more, but HATE also increases the circumstance multiplier.  Because we're planning to apply both, let's start with Grind.)");
									}
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 11) {
								if (w.getCast()[0].getCurrentHATE() == 153) {
									w.grayAppend(t, "\n\n(And now Caress Miracle.)");
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 12) {
								if (w.getCast()[0].getCurrentPLEA() == 531) {
									w.grayAppend(t, "\n\n(At this rate, the battle will end once Miracle escapes, because at that point, none of the Chosen will be surrounded.  We can stop that from happening by surrounding one of the others, but that isn't possible yet.  We have to set up a surround this turn, then use it next turn.  Attack Spice.)");
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 13) {
								if (w.getCast()[1].currentPAIN == 109) {
									w.grayAppend(t, "\n\n(And now Surround Spice.)");
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 14) {
								if (w.getCast()[1].isSurrounded()) {
									w.grayAppend(t, "\n\n(It's tempting to try to do something with Spice, but because extermination was already completed, we won't be able to grab her again after this.  We don't have a way to increase her EXPO by another level, so it wouldn't help us break Miracle, either.  Just leave Spice alone and surround Miracle again.)");
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 15) {
								if (w.getCast()[0].isSurrounded()) {
									w.grayAppend(t, "\n\n(Miracle's multipliers don't look too impressive, but because we've built up such a long surround duration, we have time to improve them.  HATE is already almost at the next level, so start with Grind on Miracle.)");
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 16) {
								if (w.getCast()[0].getCurrentHATE() == 958) {
									if (w.tickle()) {
										w.grayAppend(t, "\n\n(Next, Tickle Miracle again.)");
									} else {
										w.grayAppend(t, "\n\n(Next, Pummel Miracle again.)");
									}
								} else {
									w.endTutorial();
								}
							} else if (w.getBattleRound() == 17) {
								if (w.getCast()[0].getCurrentINJU() == 415) {
									w.grayAppend(t, "\n\n(We can see that HATE and PLEA are being penalized because their associated traumas (FEAR and DISG) are pulling ahead of the other two.  Keeping the traumas balanced is an important part of keeping the Chosen off-guard so that they can't muster resistance against any one circumstance.  ");
									if (w.tickle()) {
										w.grayAppend(t, "TICK should catch up since we're tickling her");
									} else {
										w.grayAppend(t, "PAIN should catch up since we're pummeling her");
									}
									w.grayAppend(t, ", but SHAM might have trouble.  Therefore, even though we aren't trying to deal any damage to the other two Chosen, we should still Humiliate Miracle here.");
								}
							} else if (w.getBattleRound() == 18) {
								if (w.getCast()[0].getCurrentEXPO() == 372) {
									w.grayAppend(t, "\n\n(That did it!  Miracle saw that after ");
									if (w.tickle()) {
										w.grayAppend(t, "TICK, SHAM, and EXPO");
									} else {
										w.grayAppend(t, "PAIN, SHAM, and INJU");
									}
									w.grayAppend(t, " leveled up, she'd be facing a massive x18 multiplier to her circumstances.  With the long surround duration, that would put her at risk of reaching 10k damage and unlocking a new level of torments.  In order to mitigate the damage and avoid that, she threw her pride away.");
									w.grayAppend(t, "\n\nThe bonus Evil Energy you get from breaking the vulnerability is enough to pay for the Commander you used, and now that Miracle has a broken vulnerability, she can be induced to commit greater sins during downtime and increase your Evil Energy income even more, allowing you to buy more upgrades and Commanders to crack the harder vulnerabilities!\n\nThis concludes the tutorial for Corrupted Saviors.  You can finish the battle however you like, or even go back to the start of the tutorial to try a completely different strategy.  Good luck!)");
								}
								w.endTutorial();
							}
						}
					}
				}
			}
			Action ContinueAction = new ContinueButton("Continue", "Hotkey:");
			JButton Continue = new JButton(ContinueAction){
				public Point getToolTipLocation(MouseEvent e) {
			        return new Point(0, -30);
			      }
			};
			Continue.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"),"pressed");
			Continue.getActionMap().put("pressed",ContinueAction);
			p.add(Continue);
			p.validate();
			//f.pack();
			p.repaint();
		}
	}
	
	public static void PostBattle(JTextPane t, JPanel p, JFrame f, WorldState w) {
		Boolean justContinue = true;
		Boolean postScene = false;
		int vignette = -1;
		if (w.isTutorial() == false && w.loopComplete == false) {
			vignette = w.chooseVignette();
		}
		if (w.loopComplete) {
			w.pendingBreaks = new int[0];
		}
		int chosenVignette = vignette;
		for (int i = 0; i < 3; i++) {
			if (w.getCast()[i] != null) {
				if (w.getCast()[i].morality < 34 && w.getCast()[i].impregnated) {
					for (int j = 0; j < 3; j++) {
						if (w.getCast()[j] != null) {
							if (w.getCast()[j].morality > 66 && w.getCast()[j].temptReq < 100000) {
								//w.addBreak(12);
							}
						}
					}
				}
				if (w.getCast()[i].confidence < 34 && w.getCast()[i].drained) {
					for (int j = 0; j < 3; j++) {
						if (w.getCast()[j] != null) {
							if (w.getCast()[j].confidence > 66 && w.getCast()[j].temptReq < 100000) {
								//w.addBreak(14);
							}
						}
					}
				}
			}
		}
		if (w.isTutorial()) {
			long totalTrauma = w.getCast()[0].getCurrentFEAR() + w.getCast()[0].getCurrentDISG() + w.getCast()[0].getCurrentPAIN() + w.getCast()[0].getCurrentSHAM() + w.getCast()[1].getCurrentFEAR() + w.getCast()[1].getCurrentDISG() + w.getCast()[1].getCurrentPAIN() + w.getCast()[1].getCurrentSHAM() + w.getCast()[2].getCurrentFEAR() + w.getCast()[2].getCurrentDISG() + w.getCast()[2].getCurrentPAIN() + w.getCast()[2].getCurrentSHAM();
			w.append(t, "\n\n" + w.getSeparator() + "\n\nTotal trauma: " + w.getCast()[0].condensedFormat(totalTrauma) + "\nVulnerabilities broken:");
			int cores = 0;
			int sigs = 0;
			int minors = 0;
			for (int i = 0; i < 3; i++) {
				if (w.getCast()[i].isRuthless()) {
					if (w.getCast()[i].getMorality() > 66) {
						cores++;
					} else if (w.getCast()[i].getMorality() > 33) {
						sigs++;
					}
				}
				if (w.getCast()[i].isVVirg() == false) {
					if (w.getCast()[i].getMorality() > 66) {
						cores++;
					} else if (w.getCast()[i].getMorality() > 33) {
						sigs++;
					}
				}
				if (w.getCast()[i].isLustful()) {
					if (w.getCast()[i].getInnocence() > 66) {
						cores++;
					} else if (w.getCast()[i].getInnocence() > 33) {
						sigs++;
					} else {
						minors++;
					}
				}
				if (w.getCast()[i].isCVirg() == false) {
					if (w.getCast()[i].getInnocence() > 66) {
						cores++;
					} else if (w.getCast()[i].getInnocence() > 33) {
						sigs++;
					} else {
						minors++;
					}
				}
				if (w.getCast()[i].isMeek()) {
					if (w.getCast()[i].getConfidence() > 66) {
						cores++;
					} else if (w.getCast()[i].getConfidence() > 33) {
						sigs++;
					} else {
						minors++;
					}
				}
				if (w.getCast()[i].isAVirg() == false) {
					if (w.getCast()[i].getConfidence() > 66) {
						cores++;
					} else if (w.getCast()[i].getConfidence() > 33) {
						sigs++;
					} else {
						minors++;
					}
				}
				if (w.getCast()[i].isDebased()) {
					if (w.getCast()[i].getDignity() > 66) {
						cores++;
					}  else if (w.getCast()[i].getDignity() > 33) {
						sigs++;
					}
				}
				if (w.getCast()[i].isModest() == false) {
					if (w.getCast()[i].getDignity() > 66) {
						cores++;
					}  else if (w.getCast()[i].getDignity() > 33) {
						sigs++;
					}
				}
			}
			if (cores == 0 && sigs == 0 && minors == 0) {
				w.append(t, " none");
			}
			if (cores > 0) {
				w.append(t, " " + cores + " Core");
			}
			if (sigs > 0) {
				w.append(t, " " + sigs + " Significant");
			}
			if (minors > 0) {
				w.append(t, " " + minors + " Minor");
			}
		} else if (w.getCast()[0].isIntroduced() == false) {
			justContinue = false;
			w.append(t, "\n\n" + w.getSeparator() + "\n\n");
			w.getCast()[0].printIntro(t, w);
		} else if (w.getCast()[1] != null) {
			if (w.getCast()[1].isIntroduced() == false) {
				justContinue = false;
				postScene = true;
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				w.getCast()[0].firstMeeting(t, w, w.getCast()[1]);
				p.removeAll();
				JButton Continue = new JButton("Continue");
				Continue.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p.removeAll();
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						w.getCast()[1].printIntro(t, w);
						JButton Continue2 = new JButton("Continue");
						Continue2.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Downtime(t, p, f, w);
							}
						});
						p.add(Continue2);
						p.validate();
						//f.pack();
						p.repaint();
					}
				});
				p.add(Continue);
				p.validate();
				//f.pack();
				p.repaint();
			} else if (w.getCast()[2] != null) {
				if (w.getCast()[2].isIntroduced() == false) {
					justContinue = false;
					postScene = true;
					w.append(t, "\n\n" + w.getSeparator() + "\n\n");
					w.getCast()[2].firstTrio(t, w, w.getCast()[0], w.getCast()[1]);
					p.removeAll();
					JButton Continue = new JButton("Continue");
					Continue.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							p.removeAll();
							w.append(t, "\n\n" + w.getSeparator() + "\n\n");
							w.getCast()[2].printIntro(t, w);
							JButton ContinueTwo = new JButton("Continue");
							ContinueTwo.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									Downtime(t, p, f, w);
								}
							});
							p.add(ContinueTwo);
							p.validate();
							//f.pack();
							p.repaint();
						}
					});
					p.add(Continue);
					p.validate();
					//f.pack();
					p.repaint();
				} else {
					JButton lastContinue = new JButton("Continue");
					if (w.getDay() == 15 - w.eventOffset && w.loopComplete == false) {
						justContinue = false;
						postScene = true;
						if (w.getBreaks().length == 0) {
							InterviewChain(t, p, f, w);
						} else {
							lastContinue.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									InterviewChain(t, p, f, w);
								}
							});
						}
					} else if (w.getDay() == 30 - w.eventOffset*2 && w.loopComplete == false) {
						justContinue = false;
						postScene = true;
						if (w.getBreaks().length == 0) {
							VacationChain(t, p, f, w);
						} else {
							lastContinue.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									VacationChain(t, p, f, w);
								}
							});
						}
					} else if (w.getDay() == 45 - w.eventOffset*3 && w.loopComplete == false) {
						justContinue = false;
						postScene = true;
						if (w.getBreaks().length == 0) {
							DeploymentChain(t, p, f, w);
						} else {
							lastContinue.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									DeploymentChain(t, p, f, w);
								}
							});
						}
					} else if (chosenVignette >= 0 && w.getBreaks().length == 0) {
						justContinue = false;
						if (w.getBreaks().length == 0) {
							w.showVignette(t, chosenVignette);
						} else {
							lastContinue.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.showVignette(t, chosenVignette);
								}
							});
						}
					} else {
						lastContinue.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								Downtime(t, p, f, w);
							}
						});
					}
					if (w.getBreaks().length > 0) {
						if (w.getBreaks().length > 1) {
							postScene = true;
						}
						justContinue = false;
						SortBreaks(w);
						HandleBreaks(t, p, f, w, lastContinue);
					}
				}
			} else if (chosenVignette >= 0) {
				justContinue = false;
				w.showVignette(t, chosenVignette);
			}
		} else if (chosenVignette >= 0) {
			justContinue = false;
			w.showVignette(t, chosenVignette);
		}
		if (w.isTutorial()) {
			p.removeAll();
			JButton Continue = new JButton("Continue");
			Continue.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					w.append(t, "\n\n" + w.getSeparator() + "\n\n");
					WorldState x = new WorldState();
					x.copySettings(t, w);
					x.copyToggles(w);
					x.save = w.save;
					IntroOne(t, p, f, x);
				}
			});
			p.add(Continue);
			p.validate();
			p.repaint();
		} else if (justContinue) {
			Downtime(t, p, f, w);
		} else if (postScene == false) {
			p.removeAll();
			JButton Continue = new JButton("Continue");
			Continue.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Downtime(t, p, f, w);
				}
			});
			p.add(Continue);
			p.validate();
			//f.pack();
			p.repaint();
		}
	}
	
	public static void SortBreaks(WorldState w) {
		/*int[] idealOrder = new int[]{1, 0, 4, 10, 9, 2, 5, 7, 3, 6, 11, 8};
		for (int i = 0; i < w.getBreaks().length-1; i++) {
			for (int j = 0; j < idealOrder.length; j++) {
				if (w.getBreaks()[i] == idealOrder[j]) {
					j = idealOrder.length;
				} else if (w.getBreaks()[i+1] == idealOrder[j]) {
					int storage = w.getBreaks()[i];
					w.getBreaks()[i] = w.getBreaks()[i+1];
					w.getBreaks()[i+1] = storage;
					j = idealOrder.length;
					i = -1;
				}
			}
		}*/
	}
	
	public static void DeploymentChain(JTextPane t, JPanel p, JFrame f, WorldState w) {
		w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		Chosen moral = null;
		Chosen innocent = null;
		Chosen confident = null;
		Chosen dignified = null;
		Chosen neitherOne = null;
		Chosen neitherTwo = null;
		Chosen neitherThree = null;
		Chosen neitherFour = null;
		Chosen immoral = null;
		Chosen nocent = null;
		Chosen unconfident = null;
		Chosen undignified = null;
		for (int i = 0; i < 3; i++) {
			if (w.getCast()[i].getMorality() > 66) {
				moral = w.getCast()[i];
			} else if (w.getCast()[i].getMorality() > 33) {
				neitherOne = w.getCast()[i];
			} else {
				immoral = w.getCast()[i];
			}
			if (w.getCast()[i].getInnocence() > 66) {
				innocent = w.getCast()[i];
			} else if (w.getCast()[i].getInnocence() > 33) {
				neitherTwo = w.getCast()[i];
			} else {
				nocent = w.getCast()[i];
			}
			if (w.getCast()[i].getConfidence() > 66) {
				confident = w.getCast()[i];
			} else if (w.getCast()[i].getConfidence() > 33) {
				neitherThree = w.getCast()[i];
			} else {
				unconfident = w.getCast()[i];
			}
			if (w.getCast()[i].getDignity() > 66) {
				dignified = w.getCast()[i];
			} else if (w.getCast()[i].getDignity() > 33) {
				neitherFour = w.getCast()[i];
			} else {
				undignified = w.getCast()[i];
			}
		}
		Chosen first = dignified;
		Chosen second = neitherFour;
		Chosen third = undignified;
		Chosen fourth = null;
		Chosen fifth = null;
		Chosen sixth = null;
		Chosen seventh = null;
		Chosen eighth = null;
		Chosen ninth = null;
		if (moral == dignified) {
			fourth = innocent;
			fifth = neitherTwo;
			sixth = nocent;
		} else {
			fourth = moral;
			fifth = neitherOne;
			sixth = immoral;
		}
		if (dignified == confident || confident == moral) {
			seventh = innocent;
			eighth = neitherTwo;
			ninth = nocent;
		} else {
			seventh = confident;
			eighth = neitherThree;
			ninth = unconfident;
		}
		final Chosen[] order = new Chosen[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth};
		first.deploymentOne(t, w, second, third);
		p.removeAll();
		JButton Continue = new JButton("Continue");
		Continue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				p.removeAll();
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				order[3].deploymentTwo(t, w, order[4], order[5]);
				JButton ContinueTwo = new JButton("Continue");
				ContinueTwo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p.removeAll();
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						order[6].deploymentThree(t, w, order[7], order[8]);
						JButton ContinueThree = new JButton("Continue");
						ContinueThree.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								p.removeAll();
								w.append(t, "\n\n" + w.getSeparator() + "\n\n");
								Downtime(t, p, f, w);
							}
						});
						p.add(ContinueThree);
						p.validate();
						p.repaint();
					}
				});
				p.add(ContinueTwo);
				p.validate();
				p.repaint();
			}
		});
		p.add(Continue);
		p.validate();
		p.repaint();
	}
	
	public static void VacationChain(JTextPane t, JPanel p, JFrame f, WorldState w) {
		w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		Chosen moral = null;
		Chosen innocent = null;
		Chosen confident = null;
		Chosen dignified = null;
		Chosen neitherOne = null;
		Chosen neitherTwo = null;
		Chosen neitherThree = null;
		Chosen neitherFour = null;
		Chosen immoral = null;
		Chosen nocent = null;
		Chosen unconfident = null;
		Chosen undignified = null;
		for (int i = 0; i < 3; i++) {
			if (w.getCast()[i].getMorality() > 66) {
				moral = w.getCast()[i];
			} else if (w.getCast()[i].getMorality() > 33) {
				neitherOne = w.getCast()[i];
			} else {
				immoral = w.getCast()[i];
			}
			if (w.getCast()[i].getInnocence() > 66) {
				innocent = w.getCast()[i];
			} else if (w.getCast()[i].getInnocence() > 33) {
				neitherTwo = w.getCast()[i];
			} else {
				nocent = w.getCast()[i];
			}
			if (w.getCast()[i].getConfidence() > 66) {
				confident = w.getCast()[i];
			} else if (w.getCast()[i].getConfidence() > 33) {
				neitherThree = w.getCast()[i];
			} else {
				unconfident = w.getCast()[i];
			}
			if (w.getCast()[i].getDignity() > 66) {
				dignified = w.getCast()[i];
			} else if (w.getCast()[i].getDignity() > 33) {
				neitherFour = w.getCast()[i];
			} else {
				undignified = w.getCast()[i];
			}
		}
		Chosen first = confident;
		Chosen second = neitherThree;
		Chosen third = unconfident;
		Chosen fourth = null;
		Chosen fifth = null;
		Chosen sixth = null;
		Chosen seventh = null;
		Chosen eighth = null;
		Chosen ninth = null;
		if (confident == dignified) {
			fourth = moral;
			fifth = neitherOne;
			sixth = immoral;
		} else {
			fourth = dignified;
			fifth = neitherFour;
			sixth = undignified;
		}
		if (confident == innocent || dignified == innocent) {
			seventh = moral;
			eighth = neitherOne;
			ninth = immoral;
		} else {
			seventh = innocent;
			eighth = neitherTwo;
			ninth = nocent;
		}
		final Chosen[] order = new Chosen[]{first, second, third, fourth, fifth, sixth, seventh, eighth, ninth};
		first.vacationOne(t, w, second, third);
		p.removeAll();
		JButton Continue = new JButton("Continue");
		Continue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				p.removeAll();
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				order[3].vacationTwo(t, w, order[4], order[5]);
				JButton ContinueTwo = new JButton("Continue");
				ContinueTwo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p.removeAll();
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						order[6].vacationThree(t, w, order[7], order[8]);
						JButton ContinueThree = new JButton("Continue");
						ContinueThree.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								p.removeAll();
								w.append(t, "\n\n" + w.getSeparator() + "\n\n");
								Downtime(t, p, f, w);
							}
						});
						p.add(ContinueThree);
						p.validate();
						p.repaint();
					}
				});
				p.add(ContinueTwo);
				p.validate();
				p.repaint();
			}
		});
		p.add(Continue);
		p.validate();
		p.repaint();
	}
	
	public static void InterviewChain(JTextPane t, JPanel p, JFrame f, WorldState w) {
		w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		Chosen first = null;
		Chosen second = null;
		Chosen third = null;
		Chosen innocents = null;
		Chosen morals = null;
		Chosen confidents = null;
		Chosen dignifieds = null;
		for (int i = 0; i < 3; i++) {
			if (w.getCast()[i].getInnocence() > 66) {
				innocents = w.getCast()[i];
			}
			if (w.getCast()[i].getMorality() > 66) {
				morals = w.getCast()[i];
			}
			if (w.getCast()[i].getConfidence() > 66) {
				confidents = w.getCast()[i];
			}
			if (w.getCast()[i].getDignity() > 66) {
				dignifieds = w.getCast()[i];
			}
		}
		final Chosen moral = morals;
		final Chosen innocent = innocents;
		final Chosen confident = confidents;
		final Chosen dignified = dignifieds;
		first = innocent;
		if (innocent == moral) {
			second = confident;
			third = dignified;
		} else {
			second = moral;
			if (innocent == dignified || moral == dignified) {
				third = confident;
			} else {
				third = dignified;
			}
		}
		first.interviewOne(t, w, moral, innocent, confident, dignified);
		final Chosen chosenTwo = second;
		final Chosen chosenThree = third;
		p.removeAll();
		JButton Continue = new JButton("Continue");
		Continue.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				p.removeAll();
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				chosenTwo.interviewTwo(t, w, moral, innocent, confident, dignified);
				JButton ContinueTwo = new JButton("Continue");
				ContinueTwo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p.removeAll();
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						chosenThree.interviewThree(t, w, moral, innocent, confident, dignified);
						JButton ContinueThree = new JButton("Continue");
						ContinueThree.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Downtime(t, p, f, w);
							}
						});
						p.add(ContinueThree);
						p.validate();
						p.repaint();
					}
				});
				p.add(ContinueTwo);
				p.validate();
				p.repaint();
			}
		});
		p.add(Continue);
		p.validate();
		p.repaint();
	}
	
	public static void HandleBreaks(JTextPane t, JPanel p, JFrame f, WorldState w, JButton proceed) {
		p.removeAll();
		w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		int sceneType = w.getBreaks()[0];
		Chosen broken = null;
		Chosen c = null;
		Chosen d = null;
		if (sceneType % 4 == 0) {
			for (int i = 0; i < 3; i++) {
				if (w.getCast()[i].getMorality() > 66) {
					broken = w.getCast()[i];
				} else if (w.getCast()[i].getMorality() < 34) {
					c = w.getCast()[i];
				}
			}
		} else if (sceneType % 4 == 1) {
			for (int i = 0; i < 3; i++) {
				if (w.getCast()[i].getInnocence() > 66) {
					broken = w.getCast()[i];
				} else if (w.getCast()[i].getInnocence() < 34) {
					c = w.getCast()[i];
				}
			}
		} else if (sceneType % 4 == 2) {
			for (int i = 0; i < 3; i++) {
				if (w.getCast()[i].getConfidence() > 66) {
					broken = w.getCast()[i];
				} else if (w.getCast()[i].getConfidence() < 34) {
					c = w.getCast()[i];
				}
			}
		} else if (sceneType % 4 == 3) {
			for (int i = 0; i < 3; i++) {
				if (w.getCast()[i].getDignity() > 66) {
					broken = w.getCast()[i];
				} else if (w.getCast()[i].getDignity() < 34) {
					c = w.getCast()[i];
				}
			}
		}
		if (sceneType < 16) {
			broken.breakScene(t, w, c, sceneType);
		} else {
			c = null;
			d = null;
			if (sceneType == 16) {
				for (int i = 0; i < 3; i++) {
					if (w.getCast()[i].temptReq < 100000 && w.getCast()[i].pastTempted == false && (w.getCast()[i].morality > 66 || w.getCast()[i].confidence > 66)) {
						broken = w.getCast()[i];
					}
				}
				if (broken == null) {
					w.append(t, "One of the Chosen began to trust the Thralls, but that trust was betrayed before it had time to take root.");
					w.discardBreak();
				} else {
					if (broken.morality > 66) {
						for (int i = 0; i < 3; i++) {
							if (w.getCast()[i].morality < 34) {
								c = w.getCast()[i];
							}
						}
					}
					if (broken.confidence > 66) {
						for (int i = 0; i < 3; i++) {
							if (w.getCast()[i].confidence < 34) {
								if (c == null) {
									c = w.getCast()[i];
								} else if (c != w.getCast()[i]) {
									d = w.getCast()[i];
								}
							}
						}
					}
					w.distortionScene(t, broken, c, d, sceneType);
				}
			}
		}
		if (w.getBreaks().length > 0) {
			JButton Continue = new JButton("Continue");
			Continue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					HandleBreaks(t, p, f, w, proceed);
				}
			});
			p.add(Continue);
		} else {
			p.add(proceed);
		}
		p.validate();
		p.repaint();
	}
	
	public static void Downtime(JTextPane t, JPanel p, JFrame f, WorldState w) {
		for (int i = 0; i < 3; i++) {
			if (w.getCast()[i] != null) {
				if (w.getCast()[i].temptReq < 100000) {
					w.getCast()[i].pastTempted = true;
				}
			}
		}
		Forsaken[] exhaustedTest = new Forsaken[0];
		if (w.usedForsaken != null) {
			w.usedForsaken.motivation -= w.usedForsaken.motivationCost();
			w.usedForsaken.stamina -= 200;
			exhaustedTest = new Forsaken[]{w.usedForsaken};
		}
		Forsaken[] exhausted = exhaustedTest;
		t.setText("");
		w.incrementDay();
		w.clearCommander();
		int lastChosen = 0;
		int totalActions = 22;
		Long[][] actionWeights = new Long[3][totalActions];
		int[] chosenAction = new int[]{-1, -1, -1};
		if (w.getCast()[2] != null) {
			lastChosen = 2;
		} else if (w.getCast()[1] != null) {
			lastChosen = 1;
		}
		for (int i = 0; i <= lastChosen; i++) {
			w.getCast()[i].addTrauma();
		}
		long divisor = 1;
		long highest = 0;
		for (int i = 0; i <= lastChosen; i++) {
			if (w.getCast()[i].getANGST() > highest) {
				highest = w.getCast()[i].getANGST();
			}
			if (w.getCast()[i].getTotalFEAR() > highest) {
				highest = w.getCast()[i].getTotalFEAR();
			}
			if (w.getCast()[i].getTotalDISG() > highest) {
				highest = w.getCast()[i].getTotalDISG();
			}
			if (w.getCast()[i].getTotalPAIN() > highest) {
				highest = w.getCast()[i].getTotalPAIN();
			}
			if (w.getCast()[i].getTotalSHAM() > highest) {
				highest = w.getCast()[i].getTotalSHAM();
			}
		}
		while (highest > 10*trillion) {
			highest = highest/10;
			divisor = divisor*10;
		}
		for (int i = 0; i <= lastChosen; i++) {
			long fear = w.getCast()[i].getTotalFEAR()/divisor;
			long disg = w.getCast()[i].getTotalDISG()/divisor;
			long pain = w.getCast()[i].getTotalPAIN()/divisor;
			long sham = w.getCast()[i].getTotalSHAM()/divisor;
			long angst = w.getCast()[i].getANGST()/divisor;
			Boolean divided = true;
			if (fear == 0 && disg == 0 && pain == 0 && sham == 0) {
				fear = w.getCast()[i].getTotalFEAR();
				disg = w.getCast()[i].getTotalDISG();
				pain = w.getCast()[i].getTotalPAIN();
				sham = w.getCast()[i].getTotalSHAM();
				angst = angst*divisor;
				divided = false;
			}
			actionWeights[i][0] = 150L; //Patrol
			actionWeights[i][1] = 50 + fear*100/(100+w.getCast()[i].getMorality()); //Socialize
			actionWeights[i][2] = 50 + disg*100/(100+w.getCast()[i].getMorality()); //Play
			actionWeights[i][3] = 50 + pain*100/(100+w.getCast()[i].getMorality()); //Train
			actionWeights[i][4] = 50 + sham*100/(100+w.getCast()[i].getMorality()); //Interview
			long inhibition = 20*1000*w.downtimeMultiplier/100;
			if (divided) {
				inhibition = inhibition/divisor;
			}
			if (w.getCast()[i].isRuthless()) {
				actionWeights[i][5] = (fear*200 + pain*100 + angst*20)/(100+w.getCast()[i].getMorality()) - inhibition; //Vigilantism; FEAR, PAIN
			} else {
				actionWeights[i][5] = 0L;
			}
			if (w.getCast()[i].isLustful()) {
				actionWeights[i][6] = (disg*200 + fear*100 + angst*20)/(100+w.getCast()[i].getMorality()) - inhibition; //Fooling around; DISG, FEAR
			} else {
				actionWeights[i][6] = 0L;
			}
			if (w.getCast()[i].isMeek()) {
				actionWeights[i][7] = (pain*200 + sham*100 + angst*20)/(100+w.getCast()[i].getMorality()) - inhibition; //Transform in public; PAIN, SHAM
			} else {
				actionWeights[i][7] = 0L;
			}
			if (w.getCast()[i].isDebased()) {
				actionWeights[i][8] = (sham*200 + disg*100 + angst*20)/(100+w.getCast()[i].getMorality()) - inhibition; //Watch disturbing videos; SHAM, DISG
			} else {
				actionWeights[i][8] = 0L;
			}
			inhibition = 4*Project.million*w.downtimeMultiplier/100;
			if (divided) {
				inhibition = inhibition/divisor;
			}
			if (w.getCast()[i].isVVirg() == false) {
				actionWeights[i][9] = (fear*400 + disg*200 + angst*40)/(100+w.getCast()[i].getMorality()) - inhibition; //Promiscuity; FEAR, DISG
			} else {
				actionWeights[i][9] = 0L;
			}
			if (w.getCast()[i].isCVirg() == false) {
				actionWeights[i][10] = (disg*400 + pain*200 + angst*40)/(100+w.getCast()[i].getMorality()) - inhibition; //Fake defeat; DISG, PAIN
			} else {
				actionWeights[i][10] = 0L;
			}
			if (w.getCast()[i].isAVirg() == false) {
				actionWeights[i][11] = (pain*400 + sham*200 + angst*40)/(100+w.getCast()[i].getMorality()) - inhibition; //Devastation; PAIN, SHAM
			} else {
				actionWeights[i][11] = 0L;
			}
			if (w.getCast()[i].isModest() == false) {
				actionWeights[i][12] = (sham*400 + fear*200 + angst*40)/(100+w.getCast()[i].getMorality()) - inhibition; //Pornography; SHAM, FEAR
			} else {
				actionWeights[i][12] = 0L;
			}
			inhibition = 10*Project.billion*w.downtimeMultiplier/100;
			if (divided) {
				inhibition = inhibition/divisor;
			}
			if (w.getCast()[i].timesSlaughtered() > 0) {
				actionWeights[i][13] = (fear*1000 + pain*500 + disg*250 + angst*100)/(100+w.getCast()[i].getMorality()) - inhibition; //murder; FEAR, PAIN, DISG
			} else {
				actionWeights[i][13] = 0L;
			}
			if (w.getCast()[i].timesFantasized() > 0) {
				actionWeights[i][14] = (disg*1000 + sham*500 + fear*250 + angst*100)/(100+w.getCast()[i].getMorality()) - inhibition; //surrender; DISG, SHAM, FEAR
			} else {
				actionWeights[i][14] = 0L;
			}
			if (w.getCast()[i].timesDetonated() > 0) {
				actionWeights[i][15] = (pain*1000 + disg*500 + sham*250 + angst*100)/(100+w.getCast()[i].getMorality()) - inhibition; //self-harm; PAIN, DISG, SHAM
			} else {
				actionWeights[i][15] = 0L;
			}
			if (w.getCast()[i].timesStripped() > 0) {
				actionWeights[i][16] = (sham*1000 + fear*500 + pain*250 + angst*100)/(100+w.getCast()[i].getMorality()) - inhibition; //incitement; SHAM, FEAR, PAIN
			} else {
				actionWeights[i][16] = 0L;
			}
			inhibition = 200*Project.trillion*w.downtimeMultiplier/100;
			if (divided) {
				inhibition = inhibition/divisor;
			}
			if (w.getCast()[i].isImpregnated()) {
				actionWeights[i][17] = (fear*2000 + pain*1000 + sham*500 + angst*250)/(100+w.getCast()[i].getMorality()) - inhibition; //sabotage; FEAR, PAIN, SHAM
			} else {
				actionWeights[i][17] = 0L;
			}
			if (w.getCast()[i].isHypnotized()) {
				actionWeights[i][18] = (disg*2000 + fear*1000 + pain*500 + angst*250)/(100+w.getCast()[i].getMorality()) - inhibition; //puppetry; DISG, FEAR, PAIN
			} else {
				actionWeights[i][18] = 0L;
			}
			if (w.getCast()[i].isDrained()) {
				actionWeights[i][19] = (pain*2000 + sham*1000 + disg*500 + angst*250)/(100+w.getCast()[i].getMorality()) - inhibition; //surrender; PAIN, SHAM, DISG
			} else {
				actionWeights[i][19] = 0L;
			}
			if (w.getCast()[i].isParasitized()) {
				actionWeights[i][20] = (sham*2000 + disg*1000 + fear*500 + angst*250)/(100+w.getCast()[i].getMorality()) - inhibition; //cooperation; SHAM, DISG, FEAR
			} else {
				actionWeights[i][20] = 0L;
			}
			if (w.getCast()[i].betraying && w.getCast()[i].temptReq < 100000) {
				actionWeights[i][21] = fear*5 + disg*5 + pain*5 + sham*5 + angst/2;
				if (divided) {
					actionWeights[i][21] = actionWeights[i][21]/divisor;
				}
			} else {
				actionWeights[i][21] = 0L;
			}
			w.getCast()[i].betraying = false;
			long highestWeight = 0;
			for (int j = 0; j < actionWeights[i].length; j++) {
				if (actionWeights[i][j].compareTo(highestWeight) > 0) {
					highestWeight = actionWeights[i][j];
					chosenAction[i] = j;
				} else if (actionWeights[i][j] < 0L) {
					actionWeights[i][j] = 0L;
				}
			}
		}
		long combinedWeights[][] = new long[3][totalActions];
		for (int i = 0; i <= lastChosen; i++) {
			for (int j = 0; j < totalActions; j++) {
				combinedWeights[i][j] = actionWeights[i][j];
				for (int k = 0; k <= lastChosen; k++) {
					if (i != k) {
						combinedWeights[i][j] = combinedWeights[i][j]*(200+w.getCast()[i].getInnocence())/200;
						combinedWeights[i][j] = ((actionWeights[k][chosenAction[k]]+actionWeights[k][j])*100/actionWeights[k][chosenAction[k]])*combinedWeights[i][j]/100;
						combinedWeights[i][j] = combinedWeights[i][j]*(8+w.getRelationship(i, k))/8;
						long addedWeight = combinedWeights[i][j]-actionWeights[i][j];
						if (addedWeight > 0 && w.getCast()[i].getANGST() > w.getCast()[k].getANGST()) {
							addedWeight = (w.getCast()[k].getANGST()*100/w.getCast()[i].getANGST())*addedWeight/100;
							combinedWeights[i][j] = actionWeights[i][j] + addedWeight;
						}
					}
				}
			}
		}
		long[] totalWeights = new long[totalActions];
		int[] testOrder = new int[totalActions];
		for (int i = 0; i < totalActions; i++) {
			totalWeights[i] = 0;
			testOrder[i] = i;
			for (int j = 0; j <= lastChosen; j++) {
				if (combinedWeights[j][i] >= actionWeights[j][chosenAction[j]]) {
					totalWeights[i] += combinedWeights[j][i];
				}
			}
		}
		Boolean sorted = false;
		while (sorted == false) {
			sorted = true;
			for (int i = 0; i < totalActions-1; i++) {
				if (totalWeights[i] < totalWeights[i+1]) {
					long storage = totalWeights[i];
					totalWeights[i] = totalWeights[i+1];
					totalWeights[i+1] = storage;
					int storageTwo = testOrder[i];
					testOrder[i] = testOrder[i+1];
					testOrder[i+1] = storageTwo;
					sorted = false;
				}
			}
		}
		Boolean doubleFound = false;
		for (int i = 0; i < totalActions; i++) {
			int matches = 0;
			Boolean[] matching = new Boolean[]{false, false, false};
			if (w.getCast()[0] != null) {
				if (combinedWeights[0][testOrder[i]] > actionWeights[0][chosenAction[0]]) {
					matches++;
					matching[0] = true;
				}
			}
			if (w.getCast()[1] != null) {
				if (combinedWeights[1][testOrder[i]] > actionWeights[1][chosenAction[1]]) {
					matches++;
					matching[1] = true;
				}
			}
			if (w.getCast()[2] != null) {
				if (combinedWeights[2][testOrder[i]] > actionWeights[2][chosenAction[2]]) {
					matches++;
					matching[2] = true;
				}
			}
			if (matches > 2) {
				chosenAction[0] = testOrder[i];
				chosenAction[1] = testOrder[i];
				chosenAction[2] = testOrder[i];
				i = totalActions;
			} else if (doubleFound == false && matches > 1) {
				for (int j = 0; j < 3; j++) {
					if (matching[j]) {
						chosenAction[j] = testOrder[i];
					}
				}
				doubleFound = true;
			}
		}
		/*for (int i = 0; i < 3; i++) {
			if (w.getCast()[i] != null) {
				w.append(t, "\n\n" + w.getCast()[i].getMainName());
				for (int j = 0; j < totalActions; j++) {
					w.append(t, "\n" + j + ": " + w.getCast()[i].condensedFormat(combinedWeights[i][j]) + " > " + w.getCast()[i].condensedFormat(actionWeights[i][j]));
				}
			}
		}
		w.append(t, "\n\nTest Order:\n");
		for (int i = 0; i < totalActions; i++) {
			w.append(t, "\n" + i + ": " + testOrder[i]);
		}
		w.append(t, "\n\n");*/
		Boolean singleAction = true;
		if (w.loopComplete) {
			singleAction = false;
			if (w.getHarem().length > 0) {
				ForsakenDowntime(t, p, f, w, w.save, exhausted);
			} else {
				Shop(t, p, f, w);
			}
		} else if (chosenAction[0] == chosenAction[1] && chosenAction[0] == chosenAction[2]) {
			w.getCast()[0].TripleDowntime(t, p, f, w, w.getCast()[1], w.getCast()[2], chosenAction[0]);
		} else if (chosenAction[0] == chosenAction[1]) {
			w.getCast()[0].DoubleDowntime(t, p, f, w, w.getCast()[1], chosenAction[0]);
			if (w.getCast()[2] != null) {
				singleAction = false;
				p.removeAll();
				JButton Continue = new JButton("Continue");
				Continue.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						w.getCast()[2].SingleDowntime(t, p, f, w, chosenAction[2]);
						p.removeAll();
						JButton Continue2 = new JButton("Continue");
						Continue2.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
									ForsakenDowntime(t, p, f, w, w.save, exhausted);
								} else {
									Shop(t, p, f, w);
								}
							}
						});
						p.add(Continue2);
						p.validate();
						//f.pack();
						p.repaint();
					}
				});
				p.add(Continue);
				p.validate();
				//f.pack();
				p.repaint();
			}
		} else if (chosenAction[0] == chosenAction[2]) {
			w.getCast()[0].DoubleDowntime(t, p, f, w, w.getCast()[2], chosenAction[0]);
			singleAction = false;
			p.removeAll();
			JButton Continue = new JButton("Continue");
			Continue.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					w.append(t, "\n\n" + w.getSeparator() + "\n\n");
					w.getCast()[1].SingleDowntime(t, p, f, w, chosenAction[1]);
					p.removeAll();
					JButton Continue2 = new JButton("Continue");
					Continue2.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
								ForsakenDowntime(t, p, f, w, w.save, exhausted);
							} else {
								Shop(t, p, f, w);
							}
						}
					});
					p.add(Continue2);
					p.validate();
					//f.pack();
					p.repaint();
				}
			});
			p.add(Continue);
			p.validate();
			//f.pack();
			p.repaint();
		} else if (chosenAction[1] == chosenAction[2] && chosenAction[1] >= 0) {
			w.getCast()[0].SingleDowntime(t, p, f, w, chosenAction[0]);
			singleAction = false;
			p.removeAll();
			JButton Continue = new JButton("Continue");
			Continue.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					w.append(t, "\n\n" + w.getSeparator() + "\n\n");
					w.getCast()[1].DoubleDowntime(t, p, f, w, w.getCast()[2], chosenAction[1]);
					p.removeAll();
					JButton Continue2 = new JButton("Continue");
					Continue2.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
								ForsakenDowntime(t, p, f, w, w.save, exhausted);
							} else {
								Shop(t, p, f, w);
							}
						}
					});
					p.add(Continue2);
					p.validate();
					//f.pack();
					p.repaint();
				}
			});
			p.add(Continue);
			p.validate();
			//f.pack();
			p.repaint();
		} else {
			w.getCast()[0].SingleDowntime(t, p, f, w, chosenAction[0]);
			if (w.getCast()[1] != null) {
				singleAction = false;
				p.removeAll();
				JButton Continue = new JButton("Continue");
				Continue.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						w.getCast()[1].SingleDowntime(t, p, f, w, chosenAction[1]);
						if (w.getCast()[2] != null) {
							p.removeAll();
							JButton Continue2 = new JButton("Continue");
							Continue2.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									w.append(t, "\n\n" + w.getSeparator() + "\n\n");
									w.getCast()[2].SingleDowntime(t, p, f, w, chosenAction[2]);
									p.removeAll();
									JButton Continue3 = new JButton("Continue");
									Continue3.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
											if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
												ForsakenDowntime(t, p, f, w, w.save, exhausted);
											} else {
												Shop(t, p, f, w);
											}
										}
									});
									p.add(Continue3);
									p.validate();
									//f.pack();
									p.repaint();
								}
							});
							p.add(Continue2);
							p.validate();
							//f.pack();
							p.repaint();
						} else {
							p.removeAll();
							JButton Continue4 = new JButton("Continue");
							Continue4.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
										ForsakenDowntime(t, p, f, w, w.save, exhausted);
									} else {
										Shop(t, p, f, w);
									}
								}
							});
							p.add(Continue4);
							p.validate();
							//f.pack();
							p.repaint();
						}
					}
				});
				p.add(Continue);
				p.validate();
				//f.pack();
				p.repaint();
			}
		}
		if (singleAction) {
			p.removeAll();
			JButton Continue = new JButton("Continue");
			Continue.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (w.hardMode == false && w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
						ForsakenDowntime(t, p, f, w, w.save, exhausted);
					} else {
						Shop(t, p, f, w);
					}
				}
			});
			p.add(Continue);
			p.validate();
			//f.pack();
			p.repaint();
		}
	}
	
	public static void Shop(JTextPane t, JPanel p, JFrame f, WorldState w) {
		p.removeAll();
		w.active = true;
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
		File saveLocation = new File(path + java.io.File.separator + "saves.sav");
		if (saveLocation.exists()) {
			ReadObject robj = new ReadObject();
			w.save = robj.deserializeSaveData(path + java.io.File.separator + "saves.sav");
			if (w.save.sceneText == null) {
				w.save.organizeScenes(scenesThisVersion);
			} else if (w.save.sceneText.length < scenesThisVersion) {
				w.save.organizeScenes(scenesThisVersion);
			}
		} else {
			w.save = new SaveData();
			if (w.save.sceneText == null) {
				w.save.organizeScenes(scenesThisVersion);
			} else if (w.save.sceneText.length < scenesThisVersion) {
				w.save.organizeScenes(scenesThisVersion);
			}
		}
		if (w.save.harem == null) {
			w.save.harem = new Forsaken[0];
		}
		//^^^seems to prevent pointer fuckery by ensuring that each save file refers to saved version rather than current one
		if (t.getText().length() > 0) {
			w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		}
		for (int i = 0; i < 3; i++) {
			if (w.getCast()[i] != null) {
				w.getCast()[i].setTextSize(w.getTextSize());
				w.getCast()[i].world = w;
			}
		}
		if (w.getTextSize() == 0) {
			w.switchTextSize();
		}
		if (w.usedForsaken != null && w.save != null && w.getHarem() != null && w.getHarem().length > w.usedForsakenIndex && w.getHarem()[w.usedForsakenIndex].EECost() == w.usedForsaken.EECost()) {
			w.usedForsaken = w.getHarem()[w.usedForsakenIndex];
		} else if (w.usedForsaken != null) {
			w.evilEnergy += w.usedForsaken.EECost();
			w.usedForsaken = null;
		}
		if (w.campaign) {
			w.append(t, w.cityName + " - ");
		}
		clearPortraits();
		if (w.usedForsaken != null) {
			String[] nameDisplay = new String[5];
			nameDisplay[3] = w.usedForsaken.mainName;
			if (w.usedForsaken.flavorObedience() < 20) {
				changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Project.Emotion.ANGER, Project.Emotion.NEUTRAL);
			} else if (w.usedForsaken.flavorObedience() < 40) {
				changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Project.Emotion.ANGER, Project.Emotion.SHAME);
			} else if (w.usedForsaken.flavorObedience() < 61) {
				changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Project.Emotion.FEAR, Project.Emotion.SHAME);
			} else if (w.usedForsaken.flavorObedience() < 81) {
				changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Project.Emotion.FOCUS, Project.Emotion.NEUTRAL);
			} else {
				changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Project.Emotion.JOY, Project.Emotion.FOCUS);
			}
		}
		w.append(t, "Day " + w.getDay());
		if (w.clampPercent != 100) {
			w.append(t, "\nDamage Mitigation: " + (100-w.clampPercent) + "% per level");
		}
		if (w.eventOffset != 0) {
			w.append(t, "\nPreparedness: Final Battle on Day " + (50 - w.eventOffset*3));
		}
		if (w.downtimeMultiplier != 100) {
			w.append(t, "\nLuxuries: " + w.downtimeMultiplier + "% Trauma resolution speed");
		}
		if (w.types[2] != null) {
			int superior = 0;
			for (int j = 0; j < 3; j++) {
				if (w.types[j] == Chosen.Species.SUPERIOR) {
					superior++;
				}
			}
			w.append(t, "\nElites: " + superior + " Superior Chosen");
		}
		w.printShopTutorial(t);
		if (w.getCast()[1] != null) {
			w.printGroupTutorial(t);
		}
		if ((w.getDay() > 50 - w.eventOffset*3 || w.getEarlyCheat() || w.cheater) && w.campaign == false) {
			JButton Cheat = new JButton("Cheat");
			Cheat.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (w.isCheater()) {
						Cheat(t, p, f, w);
					} else {
						p.removeAll();
						w.append(t, "\n\n" + w.getSeparator() + "\n\nActivating Cheat Mode will give you unlimited Evil Energy as well as other benefits");
						if (w.getDay() <= 35 && w.hardMode) {
							w.append(t, ", but you will not receive a score for the playthrough");
						}
						w.append(t, ".  Activate Cheat Mode?");
						JButton Activate = new JButton("Activate Cheat Mode");
						Activate.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								w.setCheater();
								Cheat(t, p, f, w);
							}
						});
						p.add(Activate);
						JButton Cancel = new JButton("Cancel");
						Cancel.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Shop(t, p, f, w);
							}
						});
						p.add(Cancel);
						p.validate();
						p.repaint();
					}
				}
			});
			p.add(Cheat);
		}
		for (int i = 0; i < w.getTechs().length && w.loopComplete == false; i++) {
			if (w.getTechs()[i].isOwned() == false) {
				Boolean shown = false;
				for (int j = 0; j < w.getTechs()[i].getPrereqs().length; j++) {
					if (w.getTechs()[i].getPrereqs()[j].isOwned()) {
						shown = true;
					}
				}
				if (w.getTechs()[i].getPrereqs().length == 0) {
					shown = true;
				}
				if (shown) {
					w.append(t, "\n\n");
					w.getTechs()[i].printSummary(w, t);
					int ownedPrereqs = 0;
					for (int j = 0; j < w.getTechs()[i].getPrereqs().length; j++) {
						if (w.getTechs()[i].getPrereqs()[j].isOwned()) {
							ownedPrereqs++;
						}
					}
					final int thisTech = i;
					if (w.getEvilEnergy() >= w.getTechs()[i].getCost() && ownedPrereqs >= w.getTechs()[i].getPrereqReqs()) {
						JButton Buy = new JButton(w.getTechs()[i].getName()){
							public Point getToolTipLocation(MouseEvent e) {
						        return new Point(0, -30);
						      }
						};;
						Buy.setToolTipText(w.getTechs()[i].getTooltip());
						Buy.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								p.removeAll();
								w.append(t, "\n\n" + w.getSeparator() + "\n\n" + w.getTechs()[thisTech].getName() + " costs " + w.getTechs()[thisTech].getCost() + " Evil Energy.  Will you develop it now?");
								JButton Confirm = new JButton("Confirm");
								Confirm.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										w.getTechs()[thisTech].buy(w);
										advanceDowntimeAction(p, w, thisTech);
										Shop(t, p, f, w);
									}
								});
								if (thisTech != 48 || w.getCast()[2] != null) {
									p.add(Confirm);
								} else if (thisTech == 48) {
									w.append(t, "  (Forbidden until all three Chosen have been encountered.)");
								}
								JButton Cancel = new JButton("Cancel");
								Cancel.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										Shop(t, p, f, w);
									}
								});
								p.add(Cancel);
								p.validate();
								//f.pack();
								p.repaint();
							}
						});
						p.add(Buy);
					}
				}
			}
		}
		if (w.loopComplete == false) {
			w.append(t, "\n\nYou have " + w.getEvilEnergy() + " Evil Energy.");
		} else if (w.day <= 50 - 3*w.eventOffset) {
			w.append(t, "\n\n" + (51 - w.day - 3*w.eventOffset) + " day");
			if (51 - w.day - 3*w.eventOffset != 1) {
				w.append(t, "s remain ");
			} else {
				w.append(t, " remains ");
			}
			w.append(t, "before your attack on the next city.");
		} else {
			w.append(t, "\n\nIt is time to choose your next destination.");
		}
		if (w.newAchievement()) {
			w.greenAppend(t, "\n\nYou have obtained a new Achievement!  See the Info menu for more details.");
		} else {
			w.printTip(t);
		}
		JButton Profiles = new JButton("Info");
		Profiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowInformation(t, p, f, w);
			}
		});
		p.add(Profiles);
		if (w.getTechs()[3].isOwned()) {
			JButton CustomBody = new JButton("Commander");
			CustomBody.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (w.getBodyStatus()[0] == false && w.hardMode == false && w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
						ForsakenMenu(t, p, f, w, w.save, 0);
					} else {
						Customize(t, p, f, w);
					}
				}
			});
			if (w.loopComplete) {
				CustomBody.setText("Forsaken");
			}
			if (w.getHarem().length > 0 || w.loopComplete == false) {
				p.add(CustomBody);
			} else if (w.day != 51 - w.eventOffset*3 || w.campaign == false) {
				JButton Pass = new JButton("Pass Time");
				Pass.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						PostBattle(t, p, f, w);
					}
				});
				p.add(Pass);
			}
			if ((w.getBodyStatus()[0] == false && w.usedForsaken == null) && w.getEvilEnergy() > 0 && w.loopComplete == false) {
				CustomBody.setBackground(Color.YELLOW);
			}
		}
		JButton Data = new JButton("Data");
		Data.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\nSelect an option.");
				p.removeAll();
				JButton NewSave = new JButton("New Save File");
				NewSave.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Data(t, p, f, w, "newsave", 0, true);
					}
				});
				p.add(NewSave);
				JButton Overwrite = new JButton("Overwrite Save");
				Overwrite.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Data(t, p, f, w, "overwrite", 0, true);
					}
				});
				p.add(Overwrite);
				JButton Load = new JButton("Load");
				Load.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Data(t, p, f, w, "load", 0, true);
					}
				});
				p.add(Load);
				JButton Delete = new JButton("Delete");
				Delete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Data(t, p, f, w, "delete", 0, true);
					}
				});
				p.add(Delete);
				JButton Import = new JButton("Import");
				Import.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Data(t, p, f, w, "import", 0, true);
					}
				});
				p.add(Import);
				JButton Export = new JButton("Export");
				Export.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Data(t, p, f, w, "export", 0, true);
					}
				});
				p.add(Export);
				JButton Back = new JButton("Back");
				Back.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Shop(t, p, f, w);
					}
				});
				p.add(Back);
				p.validate();
				//f.pack();
				p.repaint();
			}
		});
		p.add(Data);
		JButton Quit = new JButton("Quit");
		Quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\nReally quit?  Current progress will not be saved.");
				p.removeAll();
				JButton ReallyQuit = new JButton("Quit to main menu");
				ReallyQuit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						WorldState x = new WorldState();
						x.copySettings(t, w);
						x.copyToggles(w);
						x.save = w.save;
						IntroOne(t, p, f, x);
					}
				});
				p.add(ReallyQuit);
				JButton Back = new JButton("Back");
				Back.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Shop(t, p, f, w);
					}
				});
				p.add(Back);
				p.validate();
				//f.pack();
				p.repaint();
			}
		});
		p.add(Quit);
		JButton NextBattle = new JButton("Next Battle");
		NextBattle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (w.getCast()[1] == null) {
					ConfirmBattle(t, p, f, w, w.getCast()[0]);
				} else {
					pickStartingTarget(t, p, f, w);
				}
			}
		});
		if (w.loopComplete == false) {
			p.add(NextBattle);
		} else if (w.day > 50 - w.eventOffset*3) {
			JButton NextCity = new JButton("Next City");
			NextCity.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					PickNextCity(t, p, f, w);
				}
			});
			p.add(NextCity);
		}
		if (w.writePossible()) {
			addWriteButton(p, w);
		}
		p.validate();
		//f.pack();
		p.repaint();
		w.readCommentary(t);
	}
	
	public static void PickNextCity(JTextPane t, JPanel p, JFrame f, WorldState w) {
		p.removeAll();
		w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich city will you attack next?");
		if (w.nextCities == null || w.nextCities.length == 0) {
			w.nextCities = new WorldState[2];
			for (int i = 0; i < w.nextCities.length; i++) {
				w.nextCities[i] = new WorldState();
				w.nextCities[i].campaignRand = w.campaignRand;
				w.nextCities[i].save = w.save;
				w.nextCities[i].copySettings(t, w);
				w.nextCities[i].copyToggles(w);
				w.nextCities[i].setGenders(w.nextCities[i].getGenderBalance());
				w.nextCities[i].active = true;
				w.nextCities[i].campaign = true;
				w.nextCities[i].loops = w.loops+1;
				w.nextCities[i].cityName = w.nextCities[i].getCityName(w.loops*2+i+1);
				w.nextCities[i].earlyCheat = false;
				w.nextCities[i].hardMode = false;
				w.nextCities[i].eventOffset = 0;
				w.nextCities[i].clampStart = 11;
				w.nextCities[i].clampPercent = 100;
				w.nextCities[i].downtimeMultiplier = 100;
				int difficultyScore = 0;
				int clampRemoval = 0;
				while (difficultyScore < w.nextCities[i].loops*10) {
					double difficultyType = w.nextCities[i].campaignRand.nextDouble();
					if (difficultyType < 11.0/36.0 && clampRemoval < 1660) {
						w.nextCities[i].clampStart = 1;
						int increase = (int)(w.nextCities[i].campaignRand.nextDouble()*5)+1;
						if (increase > w.nextCities[i].loops*10 - difficultyScore) {
							increase = w.nextCities[i].loops*10 - difficultyScore;
						}
						clampRemoval += increase*15;
						difficultyScore += increase;
					} else if (difficultyType < 22.0/36.0) {
						if (difficultyScore <= w.nextCities[i].loops*10 - 3 && w.nextCities[i].eventOffset < 10) {
							w.nextCities[i].eventOffset++;
							difficultyScore += 3;
						}
					} else if (difficultyType < 33.0/36.0) {
						int increase = (int)(w.nextCities[i].campaignRand.nextDouble()*5)+1;
						if (increase > w.nextCities[i].loops*10 - difficultyScore) {
							increase = w.nextCities[i].loops*10 - difficultyScore;
						}
						difficultyScore += increase;
						while (increase > 0) {
							w.nextCities[i].downtimeMultiplier = w.nextCities[i].downtimeMultiplier*11/10;
							increase--;
						}
					} else if (difficultyType < 36.0/36.0 && w.nextCities[i].loops*10 - difficultyScore >= 11 && w.nextCities[i].types[0] == null) {
						if (w.nextCities[i].types[2] == null) {
							w.nextCities[i].types[2] = Chosen.Species.SUPERIOR;
						} else if (w.nextCities[i].types[1] == null) {
							w.nextCities[i].types[1] = Chosen.Species.SUPERIOR;
						} else {
							w.nextCities[i].types[0] = Chosen.Species.SUPERIOR;
						}
						difficultyScore += 11;
					}
				}
				while (clampRemoval > 0) {
					w.nextCities[i].clampPercent--;
					clampRemoval -= (10 + (100-w.clampPercent)/5);
				}
				Chosen newChosen = new Chosen();
				newChosen.setNumber(0);
				w.nextCities[i].conquered = w.conquered;
				w.nextCities[i].sacrificed = w.sacrificed;
				w.nextCities[i].returning = w.returning;
				w.nextCities[i].deceased = w.deceased;
				w.nextCities[i].formerChosen = w.formerChosen;
				w.nextCities[i].initialize();
				newChosen.generate(w.nextCities[i]);
				w.nextCities[i].addChosen(newChosen);
				if (i == 1) {
					int differences = 0;
					int requirement = (int)(Math.sqrt((double)(w.nextCities[0].loops*5)));
					differences += Math.abs(w.nextCities[0].clampPercent - w.nextCities[1].clampPercent);
					differences += Math.abs((w.nextCities[0].eventOffset - w.nextCities[1].eventOffset)*3);
					int higher = w.nextCities[0].downtimeMultiplier;
					int lower = w.nextCities[1].downtimeMultiplier;
					if (lower > higher) {
						int storage = lower;
						lower = higher;
						higher = storage;
					}
					while (higher > lower) {
						higher = higher*10/11;
						differences++;
					}
					Boolean sameElites = true;
					Boolean noElites = true;
					for (int j = 0; j < 3; j++) {
						if (w.nextCities[0].types[j] != w.nextCities[1].types[j]) {
							if (w.nextCities[0].types[j] == null || w.nextCities[1].types[j] == null) {
								sameElites = false;
								differences = differences+11;
							}
						}
						if (w.nextCities[0].types[j] != null || w.nextCities[1].types[j] != null) {
							noElites = false;
						}
					}
					if (differences < requirement || (sameElites && noElites == false)) {
						i = -1;
					}
				}
			}
		}
		for (int i = 0; i < w.nextCities.length; i++) {
			w.append(t, "\n\n");
			w.underlineAppend(t, w.nextCities[i].cityName);
			if (w.nextCities[i].clampPercent != 100) {
				w.append(t, "\nDamage Mitigation: " + (100-w.nextCities[i].clampPercent) + "% per level");
			}
			if (w.nextCities[i].eventOffset != 0) {
				w.append(t, "\nPreparedness: Final Battle on Day " + (50 - w.nextCities[i].eventOffset*3));
			}
			if (w.nextCities[i].downtimeMultiplier != 100) {
				w.append(t, "\nLuxuries: " + w.nextCities[i].downtimeMultiplier + "% Trauma resolution speed");
			}
			if (w.nextCities[i].types[2] != null) {
				int superior = 0;
				for (int j = 0; j < 3; j++) {
					if (w.nextCities[i].types[j] == Chosen.Species.SUPERIOR) {
						superior++;
					}
				}
				w.append(t, "\nElites: " + superior + " Superior Chosen");
			}
			JButton ThisOne = new JButton(w.nextCities[i].cityName);
			WorldState pickedWorld = w.nextCities[i];
			ThisOne.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					p.removeAll();
					w.append(t, "\n\n" + w.getSeparator() + "\n\n" + pickedWorld.cityName + " will be targeted.  Are you sure?");
					JButton Confirm = new JButton("Confirm");
					Confirm.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							pickedWorld.achievementSeen = w.achievementSeen;
							pickedWorld.evilEnergy = w.achievementHeld(0)[0];
							Shop(t, p, f, pickedWorld);
						}
					});
					p.add(Confirm);
					JButton Cancel = new JButton("Cancel");
					Cancel.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							PickNextCity(t, p, f, w);
						}
					});
					p.add(Cancel);
					p.validate();
					p.repaint();
				}
			});
			p.add(ThisOne);
		}
		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Shop(t, p, f, w);
			}
		});
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void ShowInformation(JTextPane t, JPanel p, JFrame f, WorldState w) {
		p.removeAll();
		w.append(t, "\n\n" + w.getSeparator());
		if (w.getTechs()[0].isOwned() && w.loopComplete == false) {
			w.append(t, "\n\nOverall corruption progress:");
			int longest = 3;
			for (int i = 0; i < 3; i++) {
				if (w.getCast()[i] != null) {
					if (w.getCast()[i].getMainName().length() > longest) {
						longest = w.getCast()[i].getMainName().length();
					}
				}
			}
			for (int i = 0; i < 3; i++) {
				if (w.getCast()[i] != null) {
					w.append(t, "\n\n" + w.getCast()[i].getMainName());
					for (int j = w.getCast()[i].getMainName().length(); j < longest; j++) {
						w.append(t, " ");
					}
					String gap = "";
					for (int j = 0; j < longest - 3; j++) {
						gap = gap + " ";
					}
					w.append(t, "  +2 T1 T2 T3 T4\nMOR" + gap + " ");
					if (w.getCast()[i].getMorality() > 66) {
						w.append(t, "[");
						if (w.getCast()[i].bonusHATE == false) {
							w.append(t, "X");
						} else {
							w.append(t, " ");
						}
						w.append(t, "][");
					} else {
						w.append(t, "   [");
					}
					if (w.getCast()[i].temptReq == 100000) {
						if (w.getCast()[i].isRuthless()) {
							w.append(t, "X");
						} else {
							w.append(t, " ");
						}
						w.append(t, "][");
						if (w.getCast()[i].isVVirg() == false) {
							w.append(t, "X");
						} else {
							w.append(t, " ");
						}
						w.append(t, "][");
						if (w.getCast()[i].timesSlaughtered() > 0) {
							w.append(t, "X");
						} else if (w.getCast()[i].usingSlaughter) {
							w.append(t, "/");
						} else {
							w.append(t, " ");
						}
						w.append(t, "][");
						if (w.getCast()[i].isImpregnated()) {
							w.append(t, "X");
						} else if (w.getCast()[i].getImpregnationEffectiveness() >= w.getCast()[i].impregnationReq()) {
							w.append(t, "/");
						} else {
							w.append(t, " ");
						}
					} else {
						w.append(t, "~][~][~][~");
					}
					w.append(t, "]");
					if (w.getCast()[i].getImpregnationEffectiveness() > 100) {
						if (w.getCast()[i].getImpregnationEffectiveness() < 1000) {
							w.append(t, " ");
						}
						w.append(t, " " + w.getCast()[i].getImpregnationEffectiveness() + "%");
					}
					w.append(t, "\nINN" + gap + " ");
					if (w.getCast()[i].getInnocence() > 66) {
						w.append(t, "[");
						if (w.getCast()[i].bonusPLEA == false) {
							w.append(t, "X");
						} else {
							w.append(t, " ");
						}
						w.append(t, "][");
					} else {
						w.append(t, "   [");
					}
					if (w.getCast()[i].isLustful()) {
						w.append(t, "X");
					} else {
						w.append(t, " ");
					}
					w.append(t, "][");
					if (w.getCast()[i].isCVirg() == false) {
						w.append(t, "X");
					} else {
						w.append(t, " ");
					}
					w.append(t, "][");
					if (w.getCast()[i].timesFantasized() > 0) {
						w.append(t, "X");
					} else if (w.getCast()[i].usingFantasize) {
						w.append(t, "/");
					} else {
						w.append(t, " ");
					}
					w.append(t, "][");
					if (w.getCast()[i].isHypnotized()) {
						w.append(t, "X");
					} else if (w.getCast()[i].getHypnosisEffectiveness() >= w.getCast()[i].hypnosisReq()) {
						w.append(t, "/");
					} else {
						w.append(t, " ");
					}
					w.append(t, "]");
					if (w.getCast()[i].getHypnosisEffectiveness() > 100) {
						if (w.getCast()[i].getHypnosisEffectiveness() < 1000) {
							w.append(t, " ");
						}
						w.append(t, " " + w.getCast()[i].getHypnosisEffectiveness() + "%");
					}
					w.append(t, "\nCON" + gap + " ");
					if (w.getCast()[i].getConfidence() > 66) {
						w.append(t, "[");
						if (w.getCast()[i].bonusINJU == false) {
							w.append(t, "X");
						} else {
							w.append(t, " ");
						}
						w.append(t, "][");
					} else {
						w.append(t, "   [");
					}
					if (w.getCast()[i].isMeek()) {
						w.append(t, "X");
					} else {
						w.append(t, " ");
					}
					w.append(t, "][");
					if (w.getCast()[i].temptReq == 100000) {
						if (w.getCast()[i].isAVirg() == false) {
							w.append(t, "X");
						} else {
							w.append(t, " ");
						}
						w.append(t, "][");
						if (w.getCast()[i].timesDetonated() > 0) {
							w.append(t, "X");
						} else if (w.getCast()[i].usingDetonate) {
							w.append(t, "/");
						} else {
							w.append(t, " ");
						}
						w.append(t, "][");
						if (w.getCast()[i].isDrained()) {
							w.append(t, "X");
						} else if (w.getCast()[i].getDrainEffectiveness() >= w.getCast()[i].drainReq()) {
							w.append(t, "/");
						} else {
							w.append(t, " ");
						}
					} else {
						w.append(t, "~][~][~");
					}
					w.append(t, "]");
					if (w.getCast()[i].getDrainEffectiveness() > 100) {
						if (w.getCast()[i].getDrainEffectiveness() < 1000) {
							w.append(t, " ");
						}
						w.append(t, " " + w.getCast()[i].getDrainEffectiveness() + "%");
					}
					w.append(t, "\nDIG" + gap + " ");
					if (w.getCast()[i].getDignity() > 66) {
						w.append(t, "[");
						if (w.getCast()[i].bonusEXPO == false) {
							w.append(t, "X");
						} else {
							w.append(t, " ");
						}
						w.append(t, "][");
					} else {
						w.append(t, "   [");
					}
					if (w.getCast()[i].isDebased()) {
						w.append(t, "X");
					} else {
						w.append(t, " ");
					}
					w.append(t, "][");
					if (w.getCast()[i].isModest() == false) {
						w.append(t, "X");
					} else {
						w.append(t, " ");
					}
					w.append(t, "][");
					if (w.getCast()[i].timesStripped() > 0) {
						w.append(t, "X");
					} else if (w.getCast()[i].usingStrip) {
						w.append(t, "/");
					} else {
						w.append(t, " ");
					}
					w.append(t, "][");
					if (w.getCast()[i].isParasitized()) {
						w.append(t, "X");
					} else if (w.getCast()[i].getParasitismEffectiveness() >= w.getCast()[i].parasitismReq()) {
						w.append(t, "/");
					} else {
						w.append(t, " ");
					}
					w.append(t, "]");
					if (w.getCast()[i].getParasitismEffectiveness() > 100) {
						if (w.getCast()[i].getParasitismEffectiveness() < 1000) {
							w.append(t, " ");
						}
						w.append(t, " " + w.getCast()[i].getParasitismEffectiveness() + "%");
					}

					//BREEDMOD START
					//w.append(t, "/n");

					
					//w.append(t, (w.getCast()[i].chosenGenetics.printNameValuePair()));

					//BREEDMOD END
				}
			}
			for (int i = 0; i < 3 && w.loopComplete == false; i++) {
				if (w.getCast()[i] != null) {
					final int thisChosen = i;
					JButton openProfile = new JButton(w.getCast()[i].getMainName() + "'s Profile");
					openProfile.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							p.removeAll();
							clearPortraits();
							changePortrait(w.getCast()[thisChosen].convertGender(), w.getCast()[thisChosen].type, false, false, w, new String[]{w.getCast()[thisChosen].mainName, null, null, null, null}, 0, Project.Emotion.NEUTRAL, Project.Emotion.NEUTRAL);
							w.append(t, "\n\n" + w.getSeparator() + "\n\n");
							w.getCast()[thisChosen].printIntro(t, w);
							w.getCast()[thisChosen].printProfile(t, p, f, w);
							JButton Continue = new JButton("Continue");
							Continue.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									ShowInformation(t, p, f, w);
								}
							});
							p.add(Continue);
							p.validate();
							//f.pack();
							p.repaint();
						}
					});
					p.add(openProfile);
				}
			}
		}
		w.append(t, "\n\nWhich information do you want to view?");
		JButton Statistics = new JButton("Statistics");
		Statistics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int highest = w.getCast()[0].getMainName().length();
				if (highest < 3) {
					highest = 3;
				}
				if (w.getCast()[1] != null) {
					if (w.getCast()[1].getMainName().length() > highest) {
						highest = w.getCast()[1].getMainName().length();
					}
				}
				if (w.getCast()[2] != null) {
					if (w.getCast()[2].getMainName().length() > highest) {
						highest = w.getCast()[2].getMainName().length();
					}
				}
				String[] names = new String[]{"", "", ""};
				names[0] = w.getCast()[0].getMainName();
				while (names[0].length() < highest) {
					names[0] = names[0] + " ";
				}
				if (w.getCast()[1] != null) {
					names[1] = w.getCast()[1].getMainName();
					while (names[1].length() < highest) {
						names[1] = names[1] + " ";
					}
				}
				if (w.getCast()[2] != null) {
					names[2] = w.getCast()[2].getMainName();
					while (names[2].length() < highest) {
						names[2] = names[2] + " ";
					}
				}
				String totals = "All";
				while (totals.length() < highest) {
					totals = totals + " ";
				}
				w.append(t, "\n\n" + w.getSeparator() + "\n\nOpening Levels Taken:\n\n");
				int spaces = highest;
				while (spaces > 0) {
					w.append(t, " ");
					spaces--;
				}
				if (w.tickle()) {
					w.append(t, "  FEAR  DISG  TICK  SHAM Total");
				} else {
					w.append(t, "  FEAR  DISG  PAIN  SHAM Total");
				}
				int totalFEAR = 0;
				int totalDISG = 0;
				int totalPAIN = 0;
				int totalSHAM = 0;
				for (int i = 0; i < 3; i++) {
					if (w.getCast()[i] != null) {
						w.append(t, "\n" + names[i] + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getFEARopenings()) + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getDISGopenings()) + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getPAINopenings()) + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getSHAMopenings()) + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getFEARopenings()+w.getCast()[i].getDISGopenings()+w.getCast()[i].getPAINopenings()+w.getCast()[i].getSHAMopenings()));
						totalFEAR += w.getCast()[i].getFEARopenings();
						totalDISG += w.getCast()[i].getDISGopenings();
						totalPAIN += w.getCast()[i].getPAINopenings();
						totalSHAM += w.getCast()[i].getSHAMopenings();
					}
				}
				w.append(t, "\n" + totals + " " + w.getCast()[0].fixedFormat(totalFEAR) + " " + w.getCast()[0].fixedFormat(totalDISG) + " " + w.getCast()[0].fixedFormat(totalPAIN) + " " + w.getCast()[0].fixedFormat(totalSHAM) + " " + w.getCast()[0].fixedFormat(totalFEAR+totalDISG+totalPAIN+totalSHAM));
			}
		});
		p.add(Statistics);
		JButton Upgrades = new JButton("View All Upgrades");
		Upgrades.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\nSelect an upgrade to view.");
				ViewUpgrades(t, p, f, w, 0);
			}
		});
		p.add(Upgrades);
		JButton Achievements = new JButton("Achievements");
		Achievements.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ViewAchievements(t, p, f, w, 0);
			}
		});
		if (w.campaign) {
			p.add(Achievements);
		}
		if (w.newAchievement()) {
			Achievements.setBackground(new Color(255,225,125));
		}
		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Shop(t, p, f, w);
			}
		});
		p.add(Back);
		p.validate();
		//f.pack();
		p.repaint();
	}
	
	public static void ViewAchievements(JTextPane t, JPanel p, JFrame f, WorldState w, int page) {
		p.removeAll();
		w.append(t, "\n\n" + w.getSeparator());
		if (page > 0) {
			JButton PreviousPage = new JButton("Previous Page");
			PreviousPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewAchievements(t, p, f, w, page-1);
				}
			});
			p.add(PreviousPage);
		}
		for (int i = page*5; i < page*5+5 && i < w.achievementSeen.length; i++) {
			w.append(t, "\n\n");
			String description = "";
			if (i == 0) {
				w.underlineAppend(t, "Residual Energy");
				description += "Forsaken Sacrificed: " + w.achievementHeld(0)[1] + "\n";
				description += "Level: " + w.achievementHeld(0)[0];
				if (w.achievementHeld(0)[0] == 0) {
					description += " (Next: 1 sacrifice)\nBonus: N/A";
				} else if (w.achievementHeld(0)[0] == 1) {
					description += " (Next: 3 sacrifices)\nBonus: +1 Starting EE";
				} else if (w.achievementHeld(0)[0] == 2) {
					description += " (Next: 6 sacrifices)\nBonus: +2 Starting EE";
				} else if (w.achievementHeld(0)[0] == 3) {
					description += " (Next: 15 sacrifices)\nBonus: +3 Starting EE";
				} else if (w.achievementHeld(0)[0] == 4) {
					description += " (Next: 60 sacrifices)\nBonus: +4 Starting EE";
				} else {
					description += "\nBonus: +5 Starting EE";
				}
				description += "\nThe supernaturally-enhanced bodies of former Chosen make for excellent breeding stock.  This role prevents them from fighting in battle, but it can give you a head start in establishing new bases of operations.  And they tend to start enjoying it before too long.";
			} else if (i == 1) {
				w.underlineAppend(t, "Impregnation Specialty");
				description += "Chosen Impregnated: " + w.achievementHeld(i)[1] + "\n";
				description += "Level: " + w.achievementHeld(i)[0];
				if (w.achievementHeld(i)[0] == 0) {
					description += " (Next: 4 impregnated)\nBonus: N/A";
				} else if (w.achievementHeld(i)[0] == 1) {
					description += " (Next: 10 impregnated)\nBonus: -200% Impregnation Threshold";
				} else if (w.achievementHeld(i)[0] == 2) {
					description += " (Next: 25 impregnated)\nBonus: -400% Impregnation Threshold";
				} else if (w.achievementHeld(i)[0] == 3) {
					description += " (Next: 60 impregnated)\nBonus: -600% Impregnation Threshold";
				} else if (w.achievementHeld(i)[0] == 4) {
					description += " (Next: 160 impregnated)\nBonus: -700% Impregnation Threshold";
				} else {
					description += "\nBonus: -750% Impregnation Threshold";
				}
				description += "\nAs the Chosen hear rumors that you're able to impregnate even them, their lack of faith in their own protections will cause it to become even easier to do so.";
			} else if (i == 2) {
				w.underlineAppend(t, "Hypnosis Specialty");
				description += "Chosen Hypnotized: " + w.achievementHeld(i)[1] + "\n";
				description += "Level: " + w.achievementHeld(i)[0];
				if (w.achievementHeld(i)[0] == 0) {
					description += " (Next: 4 hypnotized)\nBonus: N/A";
				} else if (w.achievementHeld(i)[0] == 1) {
					description += " (Next: 10 hypnotized)\nBonus: -200% Hypnosis Threshold";
				} else if (w.achievementHeld(i)[0] == 2) {
					description += " (Next: 25 hypnotized)\nBonus: -400% Hypnosis Threshold";
				} else if (w.achievementHeld(i)[0] == 3) {
					description += " (Next: 60 hypnotized)\nBonus: -600% Hypnosis Threshold";
				} else if (w.achievementHeld(i)[0] == 4) {
					description += " (Next: 160 hypnotized)\nBonus: -700% Hypnosis Threshold";
				} else {
					description += "\nBonus: -750% Hypnosis Threshold";
				}
				description += "\nMuch of the difficulty in Demonic Hypnosis comes from finding exploitable weaknesses in the target's thought process.  But all human minds share some similarities, and the more you break, the more tricks you figure out.";
			} else if (i == 3) {
				w.underlineAppend(t, "Drain Specialty");
				description += "Chosen Drained: " + w.achievementHeld(i)[1] + "\n";
				description += "Level: " + w.achievementHeld(i)[0];
				if (w.achievementHeld(i)[0] == 0) {
					description += " (Next: 4 drained)\nBonus: N/A";
				} else if (w.achievementHeld(i)[0] == 1) {
					description += " (Next: 10 drained)\nBonus: -200% Drain Threshold";
				} else if (w.achievementHeld(i)[0] == 2) {
					description += " (Next: 25 drained)\nBonus: -400% Drain Threshold";
				} else if (w.achievementHeld(i)[0] == 3) {
					description += " (Next: 60 drained)\nBonus: -600% Drain Threshold\n";
				} else if (w.achievementHeld(i)[0] == 4) {
					description += " (Next: 160 drained)\nBonus: -700% Drain Threshold";
				} else {
					description += "\nBonus: -750% Drain Threshold";
				}
				description += "\nThe Holy Energy which empowers the Chosen is inherently difficult for a Demon to absorb, but whenever you do successfully begin draining energy from one of the Chosen, her aura mingles with your own, and you find it easier to draw more of their energy into yourself.";
			} else if (i == 4) {
				w.underlineAppend(t, "Parasitism Specialty");
				description += "Chosen Parasitized: " + w.achievementHeld(i)[1] + "\n";
				description += "Level: " + w.achievementHeld(i)[0];
				if (w.achievementHeld(i)[0] == 0) {
					description += " (Next: 4 parasitized)\nBonus: N/A";
				} else if (w.achievementHeld(i)[0] == 1) {
					description += " (Next: 10 parasitized)\nBonus: -200% Parasitism Threshold";
				} else if (w.achievementHeld(i)[0] == 2) {
					description += " (Next: 25 parasitized)\nBonus: -400% Parasitism Threshold";
				} else if (w.achievementHeld(i)[0] == 3) {
					description += " (Next: 60 parasitized)\nBonus: -600% Parasitism Threshold";
				} else if (w.achievementHeld(i)[0] == 4) {
					description += " (Next: 160 parasitized)\nBonus: -700% Parasitism Threshold";
				} else {
					description += "\nBonus: -750% Parasitism Threshold";
				}
				description += "\nThe public loves to see the Chosen humiliated, and as it becomes more common for their transformations to become corrupted by you, everyone's anticipation for the next such corruption will do much of the work for you.";
			} else if (i == 5) {
				w.underlineAppend(t, "Temptation Specialty");
				description += "Chosen Tempted: " + w.achievementHeld(i)[1] + "\n";
				description += "Level: " + w.achievementHeld(i)[0];
				if (w.achievementHeld(i)[0] == 0) {
					description += " (Next: 2 Tempted)\nBonus: N/A";
				} else if (w.achievementHeld(i)[0] == 1) {
					description += " (Next: 5 Tempted)\nBonus: Tempt requirement decreases 15% per use";
				} else if (w.achievementHeld(i)[0] == 2) {
					description += " (Next: 12 Tempted)\nBonus: Tempt requirement decreases 20% per use";
				} else if (w.achievementHeld(i)[0] == 3) {
					description += " (Next: 30 Tempted)\nBonus: Tempt requirement decreases 25% per use";
				} else if (w.achievementHeld(i)[0] == 4) {
					description += " (Next: 80 Tempted)\nBonus: Tempt requirement decreases 30% per use";
				} else {
					description += "\nBonus: Tempt requirement decreases 35% per use";
				}
				description += "\nThe Chosen are carefully guided by their handlers and by society at large so that they don't even consider the possibility of turning to the side of the Demons.  But the more they see other Chosen being treated kindly by the Thralls, the more willing they'll be to think of you as a potential ally.";
			} else if (i == 6) {
				w.underlineAppend(t, "Heroine Hunter");
				description += "Superior Chosen Broken: " + w.achievementHeld(i)[1] + "\n";
				description += "Level: " + w.achievementHeld(i)[0];
				if (w.achievementHeld(i)[0] == 0) {
					description += " (Next: 1 Broken)\nBonus: N/A";
				} else if (w.achievementHeld(i)[0] == 1) {
					description += " (Next: 3 Broken)\nBonus: Slight increase to Resolve damage";
				} else if (w.achievementHeld(i)[0] == 2) {
					description += " (Next: 6 Broken)\nBonus: Notable increase to Resolve damage";
				} else if (w.achievementHeld(i)[0] == 3) {
					description += " (Next: 15 Broken)\nBonus: Moderate increase to Resolve damage";
				} else if (w.achievementHeld(i)[0] == 4) {
					description += " (Next: 40 Broken)\nBonus: Large increase to Resolve damage";
				} else {
					description += "\nBonus: Extreme increase to Resolve damage";
				}
				description += "\nThe public may not know the difference, but the Chosen themselves are keenly aware that some of their number are far more competent than others.  As you prove that you can convert even the best of them to the Demonic cause, they'll all lose hope of ever winning against you.";
			}
			if (w.achievementHeld(i)[0] > w.achievementSeen[i]) {
				w.achievementSeen[i] = w.achievementHeld(i)[0];
				w.greenAppend(t, "\n" + description);
			} else if (w.achievementHeld(i)[0] > 0) {
				w.append(t, "\n" + description);
			} else {
				w.grayAppend(t, "\n" + description);
			}
		}
		if (page < (w.achievementSeen.length-1)/5) {
			JButton NextPage = new JButton("Next Page");
			NextPage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewAchievements(t, p, f, w, page+1);
				}
			});
			p.add(NextPage);
		}
		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowInformation(t, p, f, w);
			}
		});
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void ViewUpgrades(JTextPane t, JPanel p, JFrame f, WorldState w, int page) {
		p.removeAll();
		if (page > 0) {
			JButton Previous = new JButton("<");
			Previous.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewUpgrades(t, p, f, w, page-1);
				}
			});
			p.add(Previous);
		}
		for (int i = page*5; i < w.getTechs().length && i < page*5+5; i++) {
			int id = i;
			JButton Upgrade = new JButton(w.getTechs()[i].name);
			Upgrade.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.append(t, "\n\n" + w.getSeparator() + "\n\n");
					w.getTechs()[id].printSummary(w, t);
				}
			});
			p.add(Upgrade);
			if (w.getTechs()[i].isOwned() == false) {
				Upgrade.setForeground(Color.GRAY);
			}
		}
		if (page < (w.getTechs().length-1)/5) {
			JButton Next = new JButton(">");
			Next.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ViewUpgrades(t, p, f, w, page+1);
				}
			});
			p.add(Next);
		}
		JButton Back = new JButton("Back");
		Back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowInformation(t, p, f, w);
			}
		});
		p.add(Back);
		p.validate();
		p.repaint();
	}
	
	public static void Cheat(JTextPane t, JPanel p, JFrame f, WorldState w) {
		p.removeAll();
		w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich cheat would you like to use?\n\n+100 Evil Energy: Increases your Evil Energy by 100.\n\nChange Day: Allows you to skip closer to future events or revisit past ones with the team in its current state.  Range is limited to 1-50, and because events require all three Chosen to be present, this cheat cannot be activated until the full team has been encountered.\n\nDisable/Enable Adaptations: Prevents/Allows Chosen use of Slaughter, Fantasize, Detonate, and Striptease.  Note that use of these actions is required to reach later corruption stages.\n\nUnlock All Upgrades: Purchases every upgrade aside from Imago Quickening at no Evil Energy cost.");
		JButton AddEnergy = new JButton("+100 Evil Energy");
		AddEnergy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				w.addEnergy(100);
				Shop(t, p, f, w);
			}
		});
		p.add(AddEnergy);
		JButton ChangeDay = new JButton ("Change Day");
		ChangeDay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = JOptionPane.showInputDialog("Enter the number of the day you wish to move to.");
				try {
					int newDay = Integer.valueOf(input);
					if (newDay < 2) {
						newDay = 2;
					} else if (newDay > 50) {
						newDay = 50;
					}
					w.setDay(newDay);
					Shop(t, p, f, w);
				} catch (NumberFormatException n) {
					w.append(t, "\n\n" + w.getSeparator() + "\n\nError: unable to recognize input as a number.");
					Cheat(t, p, f, w);
				}
			}
		});
		if (w.getCast()[2] != null) {
			p.add(ChangeDay);
		}
		JButton ToggleAdaptations = new JButton("Disable Adaptations");
		if (w.adaptationsDisabled()) {
			ToggleAdaptations.setText("Enable Adaptations");
		}
		ToggleAdaptations.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				w.toggleAdaptations();
				Cheat(t, p, f, w);
			}
		});
		p.add(ToggleAdaptations);
		JButton AllUpgrades = new JButton("Unlock All Upgrades");
		AllUpgrades.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < w.getTechs().length-1; i++) {
					w.getTechs()[i].owned = true;
				}
				Cheat(t, p, f, w);
			}
		});
		p.add(AllUpgrades);
		JButton Cancel = new JButton("Back");
		Cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Shop(t, p, f, w);
			}
		});
		p.add(Cancel);
		p.validate();
		p.repaint();
	}
	
	public static void Data(JTextPane t, JPanel p, JFrame f, WorldState w, String function, int page, Boolean toShop) {
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
		File saveLocation = new File(path + java.io.File.separator + "saves.sav");
		SaveData saves = null;
		if (saveLocation.exists()) {
			ReadObject robj = new ReadObject();
			saves = robj.deserializeSaveData(path + java.io.File.separator + "saves.sav");
		} else {
			saves = new SaveData();
		}
		for (int i = 0; i < saves.getSaves().length; i++) {
			saves.getSaves()[i].repairSave();
		}
		WriteObject wobj = new WriteObject();
		final SaveData saveFile = saves;
		if (function.equals("newsave")) {
			Boolean aborted = false;
			String newSaveName = JOptionPane.showInputDialog("What would you like to name this save?");
			if (newSaveName == null) {
				aborted = true;
			} else if (newSaveName.length() == 0) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				newSaveName = "Save of " + dateFormat.format(date);
			}
			if (aborted) {
				w.append(t, "  Aborted.");
			} else {
				w.append(t, "  \"" + newSaveName + "\" saved");
				saves.newSave(w, newSaveName);
				wobj.serializeSaveData(saves);
				w.save = saves;
			}
			Shop(t, p, f, w);
		} else if (function.equals("overwrite")) {
			if (saves.getSaves().length == 0) {
				Data(t, p, f, w, "newsave", 0, toShop);
			} else {
				String fullSaveName = saves.getNames()[0] + " - Day " + saves.getSaves()[0].getDay() + " versus ";
				if (saves.getSaves()[0].getCast()[1] == null) {
					fullSaveName = fullSaveName + saves.getSaves()[0].getCast()[0].getMainName();
				} else if (saves.getSaves()[0].getCast()[2] == null) {
					fullSaveName = fullSaveName + saves.getSaves()[0].getCast()[0].getMainName() + " and " + saves.getSaves()[0].getCast()[1].getMainName();
				} else {
					fullSaveName = fullSaveName + saves.getSaves()[0].getCast()[0].getMainName() + ", " + saves.getSaves()[0].getCast()[1].getMainName() + ", and " + saves.getSaves()[0].getCast()[2].getMainName();
				}
				w.append(t, "\n\n" + w.getSeparator() + "\n\nReally overwrite \"" + fullSaveName + "\"?");
				p.removeAll();
				JButton Confirm = new JButton("Confirm");
				Confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveFile.overwriteSave(w);
						wobj.serializeSaveData(saveFile);
						w.append(t, "  Done.");
						w.save = saveFile;
						Shop(t, p, f, w);
					}
				});
				p.add(Confirm);
				JButton Cancel = new JButton("Cancel");
				Cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						w.append(t, "  Cancelled.");
						Shop(t, p, f, w);
					}
				});
				p.add(Cancel);
				p.validate();
				//f.pack();
				p.repaint();
			}
		} else if (function.equals("export")) {
			WorldState newWorld = new WorldState();
			newWorld.copyInitial(w);
			Chosen newChosen = new Chosen();
			newChosen.setNumber(0);
			newChosen.generate(newWorld);
			newWorld.addChosen(newChosen);
			String newSaveName = JOptionPane.showInputDialog("What would you like to name the exported file?");
			Boolean blankName = false;
			if (newSaveName == null) {
				blankName = true;
			} else if (newSaveName.length() == 0) {
				blankName = true;
			}
			if (blankName) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				newSaveName = "Team of " + dateFormat.format(date);
			}
			String editedName = "";
			for (int i = 0; i < newSaveName.length(); i++) {
				if (newSaveName.charAt(i) == '/' || newSaveName.charAt(i) == ':') {
					editedName = editedName + "-";
				} else {
					editedName = editedName + newSaveName.charAt(i);
				}
			}
			if (w.getHighScore() > 0) {
				newWorld.setParScore(w.getHighScore());
			}
			if (w.getParScore() > newWorld.getParScore()) {
				newWorld.setParScore(w.getParScore());
			}
			newWorld.copySettings(t, w);
			newWorld.copyToggles(w);
			wobj.exportFile(newWorld, editedName);
			w.append(t, "\n\n" + w.getSeparator() + "\n\nDay 1 start against this team saved to '" + editedName + ".par'.");
		} else if (function.equals("import")) {
			p.removeAll();
			int i = page*4;
			int j = 0;
			WorldState[] foundWorlds = new WorldState[0];
			ReadObject robj = new ReadObject();
			foundWorlds = robj.importFiles();
			if (foundWorlds.length == 0) {
				w.append(t, "\n\n" + w.getSeparator() + "\n\nNo importable files found in directory.");
			} else {
				w.append(t, "\n\n" + w.getSeparator() + "\n\nFound the following importable files in directory.  Which would you like to import?");
				if (page > 0) {
					JButton LastPage = new JButton("Previous Page");
					LastPage.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Data(t, p, f, w, function, page - 1, toShop);
						}
					});
					p.add(LastPage);
				}
				while (i < foundWorlds.length && j < 4) {
					w.append(t, "\n\nFile " + (i+1) + ": " + foundWorlds[i].getSaveTitle());
					if (foundWorlds[i].getParScore() > 0) {
						w.append(t, " (Par " + foundWorlds[i].getCast()[0].condensedFormat(foundWorlds[i].getParScore()) + ")");
					}
					final int worldSelected = i;
					final WorldState[] worldList = foundWorlds;
					JButton Access = new JButton("" + (i+1));
					Access.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							String path = Project.class.getProtectionDomain().getCodeSource().getLocation().getPath();
							String fileName = "";
							for (int i = path.length()-1; i >= 0; i--) {
								if (path.charAt(i) != '/') {
									fileName = path.charAt(i) + fileName;
								} else {
									i = -1;
								}
							}
							try {
								path = path.substring(0, path.length() - fileName.length() - 1);
								path = URLDecoder.decode(path,"UTF-8");
								path = path.replaceAll("file:/", "");
								path = path.replaceAll(java.io.File.separator + "u0020", java.io.File.separator + " ");
								File saveLocation = new File(path + java.io.File.separator + "saves.sav");
								SaveData saves = null;
								if (saveLocation.exists()) {
									ReadObject robj = new ReadObject();
									saves = robj.deserializeSaveData(path + java.io.File.separator + "saves.sav");
								} else {
									saves = new SaveData();
									
								}
								WriteObject wobj = new WriteObject();
								saves.endSave(worldList[worldSelected], worldList[worldSelected].getSaveTitle());
								for (int i = 0; i < 3; i++) {
									if (worldList[worldSelected].getCast()[i] != null) {
										worldList[worldSelected].getCast()[i].globalID = saves.assignChosenID();
									}
								}
								wobj.serializeSaveData(saves);
								w.append(t, "\n\n" + w.getSeparator() + "\n\nImported file saved to slot " + saves.getSaves().length + ".");
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					});
					p.add(Access);
					i++;
					j++;
				}
				if (page*4 + 4 < foundWorlds.length) {
					JButton NextPage = new JButton("Next Page");
					NextPage.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							Data(t, p, f, w, function, page + 1, toShop);
						}
					});
					p.add(NextPage);
				}
			}
			JButton Back = new JButton("Back");
			Back.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (toShop) {
						Shop(t, p, f, w);
					} else {
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						IntroOne(t, p, f, w);
					}
				}
			});
			p.add(Back);
			p.validate();
			p.repaint();
		} else {
			int i = page*4;
			int j = 0;
			w.append(t, "\n\n" + w.getSeparator() + "\n\n");
			if (function.equals("load")) {
				w.append(t, "Load which slot?");
			} else {
				w.append(t, "Delete which slot?");
			}
			p.removeAll();
			if (page > 0) {
				JButton LastPage = new JButton("Previous Page");
				LastPage.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Data(t, p, f, w, function, page - 1, toShop);
					}
				});
				p.add(LastPage);
			}
			while (i < saves.getSaves().length && j < 4) {
				String fullSaveName = saves.getNames()[i] + " - Day " + saves.getSaves()[i].getDay() + " versus ";
				if (saves.getSaves()[i].getCast()[1] == null) {
					fullSaveName = fullSaveName + saves.getSaves()[i].getCast()[0].getMainName();
				} else if (saves.getSaves()[i].getCast()[2] == null) {
					fullSaveName = fullSaveName + saves.getSaves()[i].getCast()[0].getMainName() + " and " + saves.getSaves()[i].getCast()[1].getMainName();
				} else {
					fullSaveName = fullSaveName + saves.getSaves()[i].getCast()[0].getMainName() + ", " + saves.getSaves()[i].getCast()[1].getMainName() + ", and " + saves.getSaves()[i].getCast()[2].getMainName();
				}
				if (saves.getSaves()[i].getHighScore() > 0) {
					fullSaveName = fullSaveName + " (HS " + saves.getSaves()[i].getCast()[0].condensedFormat(saves.getSaves()[i].getHighScore());
					if (saves.getSaves()[i].getParScore() > 0) {
						fullSaveName = fullSaveName + " | Par " + saves.getSaves()[i].getCast()[0].condensedFormat(saves.getSaves()[i].getParScore()) + ")";
					} else {
						fullSaveName = fullSaveName + ")";
					}
				} else if (saves.getSaves()[i].getParScore() > 0) {
					fullSaveName = fullSaveName + " (Par " + saves.getSaves()[i].getCast()[0].condensedFormat(saves.getSaves()[i].getParScore()) + ")";
				}
				String displayedName = "\n\nSlot " + (i+1);
				if (i == 0) {
					displayedName = displayedName + " (most recent)";
				} else if (i == saves.getSaves().length - 1) {
					displayedName = displayedName + " (oldest)";
				}
				displayedName = displayedName + ", " + fullSaveName;
				if (saves.getSaves()[i].campaign) {
					w.blueAppend(t, displayedName + " [Loop " + (saves.getSaves()[i].loops+1) + ": " + saves.getSaves()[i].cityName);
					if (saves.getSaves()[i].loopComplete) {
						w.blueAppend(t, "] [Loop Complete]");
					} else {
						w.blueAppend(t, "]");
					}
				} else if (saves.getSaves()[i].hardMode) {
					w.redAppend(t, displayedName);
				} else if (saves.getSaves()[i].earlyCheat) {
					w.greenAppend(t, displayedName);
				} else {
					w.append(t, displayedName);
				}
				final int fileSelected = i;
				final String thisSaveName = fullSaveName;
				JButton Access = new JButton("" + (i+1));
				Access.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (function.equals("load")) {
							if (toShop) {
								p.removeAll();
								w.append(t, "\n\n" + w.getSeparator() + "\n\nReally load \"" + thisSaveName + "\"?  Your current progress won't be saved.");
								JButton Confirm = new JButton("Confirm");
								Confirm.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										WorldState savedWorld = new WorldState();
										savedWorld = saveFile.getSaves()[fileSelected];
										wobj.serializeSaveData(saveFile);
										saveFile.moveToFront(fileSelected);
										wobj.serializeSaveData(saveFile);
										savedWorld.copySettings(t, w);
										savedWorld.setCommentaryRead(w.getCommentaryRead());
										savedWorld.setCommentaryWrite(w.getCommentaryWrite());
										saveFile.getSaves()[0].save = saveFile;
										if (savedWorld.getDay() == 1 && savedWorld.evilEnergy == 0 && savedWorld.getTechs()[0].owned == false && savedWorld.getTechs()[1].owned == false && savedWorld.getTechs()[2].owned == false && savedWorld.getTechs()[3].owned == false && savedWorld.campaign == false) {
											savedWorld.earlyCheat = w.earlyCheat;
											savedWorld.earlyCheat = w.earlyCheat;
											savedWorld.hardMode = w.hardMode;
											savedWorld.eventOffset = w.eventOffset;
											savedWorld.clampStart = w.clampStart;
											savedWorld.clampPercent = w.clampPercent;
											if (savedWorld.earlyCheat) {
												Shop(t, p, f, saveFile.getSaves()[0]);
											} else {
												IntroTwo(t, p, f, saveFile.getSaves()[0]);
											}
										} else {
											Shop(t, p, f, saveFile.getSaves()[0]);
										}
									}
								});
								p.add(Confirm);
								JButton Cancel = new JButton("Cancel");
								Cancel.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {
										Shop(t, p, f, w);
									}
								});
								p.add(Cancel);
								p.validate();
								//f.pack();
								p.repaint();
							} else {
								WorldState savedWorld = new WorldState();
								savedWorld = saveFile.getSaves()[fileSelected];
								saveFile.moveToFront(fileSelected);
								wobj.serializeSaveData(saveFile);
								savedWorld.copySettings(t, w);
								savedWorld.setCommentaryRead(w.getCommentaryRead());
								savedWorld.setCommentaryWrite(w.getCommentaryWrite());
								savedWorld.save = saveFile;
								if (savedWorld.getDay() == 1 && savedWorld.evilEnergy == 0 && savedWorld.getTechs()[0].owned == false && savedWorld.getTechs()[1].owned == false && savedWorld.getTechs()[2].owned == false && savedWorld.getTechs()[3].owned == false) {
									savedWorld.earlyCheat = w.earlyCheat;
									savedWorld.hardMode = w.hardMode;
									savedWorld.eventOffset = w.eventOffset;
									savedWorld.clampStart = w.clampStart;
									savedWorld.clampPercent = w.clampPercent;
									if (savedWorld.earlyCheat) {
										Shop(t, p, f, savedWorld);
									} else {
										IntroTwo(t, p, f, savedWorld);
									}
								} else {
									Shop(t, p, f, savedWorld);
								}
							}
						} else {
							p.removeAll();
							w.append(t, "\n\n" + w.getSeparator() + "\n\nReally delete \"" + thisSaveName + "\"?");
							JButton Confirm = new JButton("Confirm");
							Confirm.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									saveFile.deleteSave(fileSelected);
									wobj.serializeSaveData(saveFile);
									w.append(t, "  Done.");
									w.save = saveFile;
									Shop(t, p, f, w);
								}
							});
							p.add(Confirm);
							JButton Cancel = new JButton("Cancel");
							Cancel.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.append(t, "  Cancelled.");
									Shop(t, p, f, w);
								}
							});
							p.add(Cancel);
							p.validate();
							//f.pack();
							p.repaint();
						}
					}
				});
				p.add(Access);
				i++;
				j++;
			}
			if (page*4 + 4 < saves.getSaves().length) {
				JButton NextPage = new JButton("Next Page");
				NextPage.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Data(t, p, f, w, function, page + 1, toShop);
					}
				});
				p.add(NextPage);
			}
			JButton Cancel = new JButton("Cancel");
			Cancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (toShop) {
						Shop(t, p, f, w);
					} else {
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						IntroOne(t, p, f, w);
					}
				}
			});
			p.add(Cancel);
			p.validate();
			//f.pack();
			p.repaint();
		}
	}
	
	public static void Customize(JTextPane t, JPanel p, JFrame f, WorldState w) {
		p.removeAll();
		w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		if (w.getBodyStatus()[0] == false) {
			w.append(t, "You aren't currently sending a Commander body to the battlefield.  Creating one costs 1 Evil Energy.");
			if (w.getEvilEnergy() >= 1) {
				JButton Create = new JButton("Create");
				Create.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.newCommander();
						w.addEnergy(-1);
						Customize(t, p, f, w);
					}
				});
				p.add(Create);
			}
			if (w.hardMode == false && w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
				JButton UseForsaken = new JButton("Use Forsaken");
				UseForsaken.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ForsakenMenu(t, p, f, w, w.save, 0);
					}
				});
				p.add(UseForsaken);
			}
			JButton Back = new JButton("Back");
			Back.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Shop(t, p, f, w);
				}
			});
			p.add(Back);
		} else {
			w.printCommanderSummary(t, null);
			int suppressorsKnown = 0;
			if (w.getTechs()[10].isOwned()) {
				suppressorsKnown++;
			}
			if (w.getTechs()[11].isOwned()) {
				suppressorsKnown++;
			}
			if (w.getTechs()[12].isOwned()) {
				suppressorsKnown++;
			}
			if (w.getTechs()[13].isOwned()) {
				suppressorsKnown++;
			}
			int suppressorsUsed = 0;
			if (w.getBodyStatus()[3]) {
				suppressorsUsed++;
			}
			if (w.getBodyStatus()[4]) {
				suppressorsUsed++;
			}
			if (w.getBodyStatus()[5]) {
				suppressorsUsed++;
			}
			if (w.getBodyStatus()[6]) {
				suppressorsUsed++;
			}
			final Boolean defilerUsed = w.getBodyStatus()[11] || w.getBodyStatus()[12] || w.getBodyStatus()[13] || w.getBodyStatus()[14];
			Boolean defilerKnown = false;
			if (w.getTechs()[22].isOwned() || w.getTechs()[23].isOwned() || w.getTechs()[24].isOwned() || w.getTechs()[25].isOwned()) {
				defilerKnown = true;
			}
			final int suppressorsUsedFinal = suppressorsUsed;
			final Boolean punisherUsed = w.getBodyStatus()[19] || w.getBodyStatus()[20] || w.getBodyStatus()[21] || w.getBodyStatus()[22];
			Boolean punisherKnown = false;
			if (w.getTechs()[34].isOwned() || w.getTechs()[35].isOwned() || w.getTechs()[36].isOwned() || w.getTechs()[37].isOwned()) {
				punisherKnown = true;
			}
			if ((punisherUsed == false || (w.getTechs()[47].isOwned() && (w.getEvilEnergy() >= 66 || defilerUsed))) && ((suppressorsKnown > 0 && suppressorsUsed == 0 && (defilerUsed == false || (w.getTechs()[33].isOwned() && w.getEvilEnergy() >= 10) || punisherUsed)) || (suppressorsKnown > 1 && suppressorsUsed == 1 && w.getEvilEnergy() >= 5 && w.getTechs()[21].isOwned() && defilerUsed == false && punisherUsed == false))) {
				JButton Suppressor = new JButton("Suppressor Upgrades");
				Suppressor.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						p.removeAll();
						if (w.getTechs()[10].isOwned() && w.getBodyStatus()[3] == false) {
							JButton Hunger = new JButton("Hunger [HATE]");
							Hunger.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									if (punisherUsed) {
										w.applyCompletion();
									} else if (defilerUsed) {
										w.applySynthesis();
									} else if (suppressorsUsedFinal == 1) {
										w.applyVersatility();
									}
									w.applyHunger();
									Customize(t, p, f, w);
								}
							});
							p.add(Hunger);
						}
						if (w.getTechs()[11].isOwned() && w.getBodyStatus()[4] == false) {
							JButton Lust = new JButton("Lust [PLEA]");
							Lust.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									if (punisherUsed) {
										w.applyCompletion();
									} else if (defilerUsed) {
										w.applySynthesis();
									} else if (suppressorsUsedFinal == 1) {
										w.applyVersatility();
									}
									w.applyLust();
									Customize(t, p, f, w);
								}
							});
							p.add(Lust);
						}
						if (w.getTechs()[12].isOwned() && w.getBodyStatus()[5] == false) {
							JButton Anger = new JButton("Anger [INJU]");
							Anger.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									if (punisherUsed) {
										w.applyCompletion();
									} else if (defilerUsed) {
										w.applySynthesis();
									} else if (suppressorsUsedFinal == 1) {
										w.applyVersatility();
									}
									w.applyAnger();
									Customize(t, p, f, w);
								}
							});
							p.add(Anger);
						}
						if (w.getTechs()[13].isOwned() && w.getBodyStatus()[6] == false) {
							JButton Mania = new JButton("Mania [EXPO]");
							Mania.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									if (punisherUsed) {
										w.applyCompletion();
									} else if (defilerUsed) {
										w.applySynthesis();
									} else if (suppressorsUsedFinal == 1) {
										w.applyVersatility();
									}
									w.applyMania();
									Customize(t, p, f, w);
								}
							});
							p.add(Mania);
						}
						JButton Cancel = new JButton("Cancel");
						Cancel.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Customize(t, p, f, w);
							}
						});
						p.add(Cancel);
						p.validate();
						p.repaint();
						w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich upgrade will you apply?");
					}
				});
				p.add(Suppressor);
			}
			if ((punisherUsed == false || (w.getTechs()[47].isOwned() && w.getEvilEnergy() >= 66) || (w.getTechs()[47].isOwned() && suppressorsUsed == 1)) && defilerKnown == true && defilerUsed == false && (suppressorsUsed == 0 || (suppressorsUsed == 1 && w.getTechs()[33].isOwned() && w.getEvilEnergy() >= 16)) && w.getEvilEnergy() >= 6) {
				JButton Defiler = new JButton("Defiler Upgrades");
				Defiler.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.removeAll();
						if (w.getTechs()[22].isOwned()) {
							JButton Ambition = new JButton("Ambition [HATE/PLEA]");
							Ambition.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.applyAmbition();
									if (punisherUsed) {
										w.applyCompletion();
									} else if (suppressorsUsedFinal == 1) {
										w.applySynthesis();
									}
									Customize(t, p, f, w);
								}
							});
							p.add(Ambition);
						}
						if (w.getTechs()[23].isOwned()) {
							JButton Dominance = new JButton("Dominance [PLEA/INJU]");
							if (w.tickle()) {
								Dominance.setText("Dominance [PLEA/ANTI]");
							}
							Dominance.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.applyDominance();
									if (punisherUsed) {
										w.applyCompletion();
									} else if (suppressorsUsedFinal == 1) {
										w.applySynthesis();
									}
									Customize(t, p, f, w);
								}
							});
							p.add(Dominance);
						}
						if (w.getTechs()[24].isOwned()) {
							JButton Spite = new JButton("Spite [INJU/EXPO]");
							if (w.tickle()) {
								Spite.setText("Spite [ANTI/EXPO]");
							}
							Spite.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.applySpite();
									if (punisherUsed) {
										w.applyCompletion();
									} else if (suppressorsUsedFinal == 1) {
										w.applySynthesis();
									}
									Customize(t, p, f, w);
								}
							});
							p.add(Spite);
						}
						if (w.getTechs()[25].isOwned()) {
							JButton Vanity = new JButton("Vanity [EXPO/HATE]");
							Vanity.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.applyVanity();
									if (punisherUsed) {
										w.applyCompletion();
									} else if (suppressorsUsedFinal == 1) {
										w.applySynthesis();
									}
									Customize(t, p, f, w);
								}
							});
							p.add(Vanity);
						}
						JButton Cancel = new JButton("Cancel");
						Cancel.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Customize(t, p, f, w);
							}
						});
						p.add(Cancel);
						p.validate();
						p.repaint();
						w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich upgrade will you apply?");
					}
				});
				p.add(Defiler);
			}
			if (punisherUsed == false && punisherKnown && ((defilerUsed == false && suppressorsUsed == 0) || (w.getTechs()[47].isOwned() && suppressorsUsed < 2 && (w.getEvilEnergy() >= 66 || (defilerUsed && w.getEvilEnergy() >= 60) || (defilerUsed && suppressorsUsed == 1 && w.getEvilEnergy() >= 50))))) {
				JButton Punisher = new JButton("Punisher Upgrades");
				Punisher.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.removeAll();
						if (w.getTechs()[34].isOwned()) {
							JButton Impregnation = new JButton("Impregnation [HATE]");
							Impregnation.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.applyImpregnation();
									if (defilerUsed || suppressorsUsedFinal == 1) {
										w.applyCompletion();
									}
									Customize(t, p, f, w);
								}
							});
							p.add(Impregnation);
						}
						if (w.getTechs()[35].isOwned()) {
							JButton Hypnosis = new JButton("Hypnosis [PLEA]");
							Hypnosis.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.applyHypnosis();
									if (defilerUsed || suppressorsUsedFinal == 1) {
										w.applyCompletion();
									}
									Customize(t, p, f, w);
								}
							});
							p.add(Hypnosis);
						}
						if (w.getTechs()[36].isOwned()) {
							JButton Drain = new JButton("Drain [INJU]");
							if (w.tickle()) {
								Drain.setText("Drain [ANTI]");
							}
							Drain.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.applyDrain();
									if (defilerUsed || suppressorsUsedFinal == 1) {
										w.applyCompletion();
									}
									Customize(t, p, f, w);
								}
							});
							p.add(Drain);
						}
						if (w.getTechs()[37].isOwned()) {
							JButton Parasitism = new JButton("Parasitism [EXPO]");
							Parasitism.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									w.applyParasitism();
									if (defilerUsed || suppressorsUsedFinal == 1) {
										w.applyCompletion();
									}
									Customize(t, p, f, w);
								}
							});
							p.add(Parasitism);
						}
						JButton Cancel = new JButton("Cancel");
						Cancel.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Customize(t, p, f, w);
							}
						});
						p.add(Cancel);
						p.validate();
						p.repaint();
						w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich upgrade will you apply?");
					}
				});
				p.add(Punisher);
			}
			if (w.getTechs()[8].isOwned() && w.getBodyStatus()[1] == false && w.getEvilEnergy() >= 1) {
				JButton Enhance = new JButton("Enhance Duration (1)");
				Enhance.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.enhanceOne();
						Customize(t, p, f, w);
					}
				});
				p.add(Enhance);
			}
			if (w.getTechs()[14].isOwned() && w.getBodyStatus()[1] && w.getBodyStatus()[7] == false && w.getEvilEnergy() >= 1) {
				JButton Enhance = new JButton("Enhance Duration (2)");
				Enhance.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.enhanceTwo();
						Customize(t, p, f, w);
					}
				});
				p.add(Enhance);
			}
			if (w.getTechs()[20].isOwned() && w.getBodyStatus()[7] && w.getBodyStatus()[9] == false && w.getEvilEnergy() >= 2) {
				JButton Enhance = new JButton("Enhance Duration (3)");
				Enhance.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.enhanceThree();
						Customize(t, p, f, w);
					}
				});
				p.add(Enhance);
			}
			if (w.getTechs()[26].isOwned() && w.getBodyStatus()[9] && w.getBodyStatus()[15] == false && w.getEvilEnergy() >= 2) {
				JButton Enhance = new JButton("Enhance Duration (4)");
				Enhance.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.enhanceFour();
						Customize(t, p, f, w);
					}
				});
				p.add(Enhance);
			}
			if (w.getTechs()[46].isOwned() && w.getBodyStatus()[15] && w.getBodyStatus()[25] == false && w.getEvilEnergy() >= 30) {
				JButton Enhance = new JButton("Enhance Duration (5)");
				Enhance.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						w.enhanceFive();
						Customize(t, p, f, w);
					}
				});
				p.add(Enhance);
			}
			if (w.getTechs()[15].isOwned() && w.getBodyStatus()[8] == false && w.getEvilEnergy() >= 2) {
				JButton AddCapture = new JButton("Extra Capture (1)");
				AddCapture.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.addCaptureOne();
						Customize(t, p, f, w);
					}
				});
				p.add(AddCapture);
			}
			if (w.getTechs()[27].isOwned() && w.getBodyStatus()[8] && w.getBodyStatus()[16] == false && w.getEvilEnergy() >= 5) {
				JButton AddCapture = new JButton("Extra Capture (2)");
				AddCapture.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.addCaptureTwo();
						Customize(t, p, f, w);
					}
				});
				p.add(AddCapture);
			}
			if (w.getTechs()[32].isOwned() && w.getBodyStatus()[16] && w.getBodyStatus()[17] == false && w.getEvilEnergy() >= 10) {
				JButton AddCapture = new JButton("Extra Capture (3)");
				AddCapture.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						w.addCaptureThree();
						Customize(t, p, f, w);
					}
				});
				p.add(AddCapture);
			}
			if (w.getTechs()[38].isOwned() && w.getBodyStatus()[17] && w.getBodyStatus()[23] == false && w.getEvilEnergy() >= 20) {
				JButton AddCapture = new JButton("Extra Capture (4)");
				AddCapture.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						w.addCaptureFour();
						Customize(t, p, f, w);
					}
				});
				p.add(AddCapture);
			}
			if (w.getTechs()[39].isOwned() && w.getBodyStatus()[24] == false && w.getEvilEnergy() >= 10) {
				JButton Flight = new JButton("Flight");
				Flight.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						w.applyRelentlessness();
						Customize(t, p, f, w);
					}
				});
				p.add(Flight);
			}
			if (w.getTechs()[9].isOwned()) {
				JButton Toggle = new JButton("Toggle Ambush");
				Toggle.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						w.toggleAmbush();
						Customize(t, p, f, w);
					}
				});
				p.add(Toggle);
			}
			JButton Refund = new JButton("Refund");
			Refund.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					w.addEnergy(w.getCommanderValue());
					w.clearCommander();
					Customize(t, p, f, w);
				}
			});
			p.add(Refund);
			if (punisherUsed == false || (defilerUsed == false && suppressorsUsed == 0) || (defilerUsed && suppressorsUsed == 1)) {
				JButton Done = new JButton("Done");
				Done.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Shop(t, p, f, w);
					}
				});
				p.add(Done);
			}
		}
		p.validate();
		//f.pack();
		p.repaint();
	}
	
	public static void advanceDowntimeAction(JPanel p, WorldState w, int action) {
		Boolean actionMatches = true;
		if (w.getActions().length > w.getCurrentAction()) {
			if (w.getActions()[w.getCurrentAction()] != action) {
				actionMatches = false;
				w.truncateCommentary(w.getCurrentAction());
			}
		}
		if (w.writePossible()) {
			if (w.getCurrentComment().length() > 0) {
				w.writeCommentary(w.getCurrentComment());
			} else if (w.getCommentary().length <= w.getCurrentAction() || actionMatches == false) {
				String generated = "";
				if (action < w.getTechs().length) {
					generated = "Buy " + w.getTechs()[action].getName() + ".";
				} else if (w.usedForsaken != null) {
					generated = generated + "(This playthrough used one of the Forsaken here, so it may not be possible to reliably follow it.)";
				} else {
					int index = action - w.getTechs().length;
					if (w.getBodyStatus()[0]) {
						generated = "Buy a Commander with a ";
						if (w.getBodyStatus()[1]) {
							if (w.getBodyStatus()[7]) {
								if (w.getBodyStatus()[9]) {
									if (w.getBodyStatus()[15]) {
										if (w.getBodyStatus()[25]) {
											generated = generated + "eight";
										} else {
											generated = generated + "six";
										}
									} else {
										generated = generated + "five";
									}
								} else {
									generated = generated + "four";
								}
							} else {
								generated = generated + "three";
							}
						} else {
							generated = generated + "two";
						}
						generated = generated + "-turn duration";
						if (w.getBodyStatus()[17]) {
							generated = generated + " and three extra captures.  ";
						} else if (w.getBodyStatus()[16]) {
							generated = generated + " and two extra captures.  ";
						} else if (w.getBodyStatus()[8]) {
							generated = generated + " and an extra capture.  ";
						} else {
							generated = generated + ".  ";
						}
						if (w.getBodyStatus()[26]) {
							String suppressor = "";
							String defiler = "";
							String punisher = "";
							if (w.getBodyStatus()[19]) {
								punisher = "Impregnation";
							} else if (w.getBodyStatus()[20]) {
								punisher = "Hypnosis";
							} else if (w.getBodyStatus()[21]) {
								punisher = "Drain";
							} else if (w.getBodyStatus()[22]) {
								punisher = "Parasitism";
							}
							if (w.getBodyStatus()[11]) {
								defiler = "Ambition";
							} else if (w.getBodyStatus()[12]) {
								defiler = "Dominance";
							} else if (w.getBodyStatus()[13]) {
								defiler = "Spite";
							} else if (w.getBodyStatus()[14]) {
								defiler = "Vanity";
							}
							if (w.getBodyStatus()[3]) {
								suppressor = "Hunger";
							} else if (w.getBodyStatus()[4]) {
								suppressor = "Lust";
							} else if (w.getBodyStatus()[5]) {
								suppressor = "Anger";
							} else if (w.getBodyStatus()[6]) {
								suppressor = "Mania";
							}
							generated = generated + "Equip it with the Suppressor " + suppressor + ", the Defiler " + defiler + ", and the Punisher " + punisher + ".  ";
						} else if (w.getBodyStatus()[19]) {
							generated = generated + "Equip it with the Punisher Impregnation.  ";
						} else if (w.getBodyStatus()[20]) {
							generated = generated + "Equip it with the Punisher Hypnosis.  ";
						} else if (w.getBodyStatus()[21]) {
							generated = generated + "Equip it with the Punisher Drain.  ";
						} else if (w.getBodyStatus()[22]) {
							generated = generated + "Equip it with the Punisher Parasitism.  ";
						} else if (w.getBodyStatus()[18]) {
							generated = generated + "Equip it with the Defiler ";
							if (w.getBodyStatus()[11]) {
								generated = generated + "Ambition";
							} else if (w.getBodyStatus()[12]) {
								generated = generated + "Dominance";
							} else if (w.getBodyStatus()[13]) {
								generated = generated + "Spite";
							} else if (w.getBodyStatus()[14]) {
								generated = generated + "Vanity";
							}
							generated = generated + " and the Suppressor ";
							if (w.getBodyStatus()[3]) {
								generated = generated + "Hunger";
							} else if (w.getBodyStatus()[4]) {
								generated = generated + "Lust";
							} else if (w.getBodyStatus()[5]) {
								generated = generated + "Anger";
							} else if (w.getBodyStatus()[6]) {
								generated = generated + "Mania";
							}
							generated = generated + ".  ";
						} else if (w.getBodyStatus()[11]) {
							generated = generated + "Equip it with the Defiler Ambition.  ";
						} else if (w.getBodyStatus()[12]) {
							generated = generated + "Equip it with the Defiler Dominance.  ";
						} else if (w.getBodyStatus()[13]) {
							generated = generated + "Equip it with the Defiler Spite.  ";
						} else if (w.getBodyStatus()[14]) {
							generated = generated + "Equip it with the Defiler Vanity.  ";
						} else if (w.getBodyStatus()[10]) {
							generated = generated + "Equip it with the Suppressors ";
							Boolean first = false;
							if (w.getBodyStatus()[3]) {
								generated = generated + "Hunger";
								first = true;
							}
							if (w.getBodyStatus()[4]) {
								if (first) {
									generated = generated + " and ";
								}
								generated = generated + "Lust";
								first = true;
							}
							if (w.getBodyStatus()[5]) {
								if (first) {
									generated = generated + " and ";
								}
								generated = generated + "Anger";
								first = true;
							}
							if (w.getBodyStatus()[6]) {
								generated = generated + " and Mania";
							}
							generated = generated + ".  ";
						} else if (w.getBodyStatus()[3]) {
							generated = generated + "Equip it with the Suppressor Hunger.  ";
						} else if (w.getBodyStatus()[4]) {
							generated = generated + "Equip it with the Suppressor Lust.  ";
						} else if (w.getBodyStatus()[5]) {
							generated = generated + "Equip it with the Suppressor Anger.  ";
						} else if (w.getBodyStatus()[6]) {
							generated = generated + "Equip it with the Suppressor Mania.  ";
						}
						if (w.getBodyStatus()[2]) {
							generated = generated + "Turn off its ambush and send ";
						} else if (w.getTechs()[9].isOwned()) {
							generated = generated + "Leave its ambush on and send ";
						} else {
							generated = generated + "Send ";
						}
						if (w.getTechs()[31].isOwned() && w.upgradedCommander() == false) {
							Chosen target = w.getCast()[(index-3)/4];
							index = (index-3)%4;
							generated = generated + "it after " + target.getMainName() + ".  Have the Thralls start by using ";
							if (index == 0) {
								generated = generated + "Grind.";
							} else if (index == 1) {
								generated = generated + "Caress.";
							} else if (index == 2) {
								if (w.tickle()) {
									generated = generated + "Tickle.";
								} else {
									generated = generated + "Pummel.";
								}
							} else if (index == 3) {
								generated = generated + "Humiliate.";
							}
						} else {
							generated = generated + "it after " + w.getCast()[index].getMainName() + ".";
						}
					} else {
						generated = "Target " + w.getCast()[index].getMainName();
						if (w.getTechs()[3].isOwned()) {
							generated = generated + " without bringing along a Commander.";
						} else {
							generated = generated + ".";
						}
					}
				}
				w.writeCommentary(generated);
			}
		}
		w.nextAction(action);
	}
	
	public static void pickStartingTarget(JTextPane t, JPanel p, JFrame f, WorldState w) {
		p.removeAll();
		w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich of the Chosen will you target?");
		for (int i = 0; i < w.getCast().length; i++) {
			if (w.getCast()[i] != null) {
				final int thisChosen = i;
				JButton thisOne = new JButton(w.getCast()[i].getMainName());
				thisOne.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						ConfirmBattle(t, p, f, w, w.getCast()[thisChosen]);
					}
				});
				p.add(thisOne);
			}
		}
		JButton Cancel = new JButton("Cancel");
		Cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Shop(t, p, f, w);
			}
		});
		p.add(Cancel);
		p.validate();
		//f.pack();
		p.repaint();
	}
	
	public static void ConfirmBattle(JTextPane t, JPanel p, JFrame f, WorldState w, Chosen c) {
		p.removeAll();
		Boolean immediateAction = false;
		w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		if (w.getTechs()[0].isOwned()) {
			w.append(t, c.mainName + "'s ");
			c.printVulnerabilities(t, p, f, w);
			w.append(t, "\n\n");
		}
		if (w.getDay() == (50 - w.eventOffset*3) || w.getTechs()[48].isOwned()) {
			w.append(t, "This will be the final battle.  When extermination is completed, instead of waiting for surrounded and captured allies to escape, the Chosen may sacrifice each other's lives in order to defeat you.  Victory requires neutralizing at least two of the three Chosen.\n\n");
		}
		if (w.getBodyStatus()[0]) {
			if (w.upgradedCommander() || w.getTechs()[31].isOwned() == false || w.getBodyStatus()[2]) {
				w.printCommanderSummary(t, c);
			} else {
				immediateAction = true;
			}
		} else if (w.usedForsaken != null) {
			w.append(t, "Commanding Forsaken: " + w.usedForsaken.mainName + "\nStamina: " + w.usedForsaken.stamina/10 + "." + w.usedForsaken.stamina%10 + "%\nMotivation: " + w.usedForsaken.motivation/10 + "." + w.usedForsaken.motivation%10 + "%\nCost: 20% Stamina, " + w.usedForsaken.motivationCost()/10 + "." + w.usedForsaken.motivationCost()%10 + "% Motivation, " + w.usedForsaken.EECost() + " EE\n" + w.usedForsaken.describeCombatStyle(w, false) + "\nReputation Strength: " + (200-w.usedForsaken.disgrace*2) + "%\nTarget Compatibilities:");
			for (int j = 0; j < 3; j++) {
				if (w.getCast()[j] != null) {
					w.append(t, "\n" + w.getCast()[j].getMainName() + " - ");
					int compatibility = w.usedForsaken.compatibility(w.getCast()[j]);
					//Perfect (8 rounds, 150% damage)
					if (compatibility >= 8) {
						w.append(t, "Excellent (8 rounds)");
					} else if (compatibility == 7) {
						w.append(t, "Good (7 rounds)");
					} else if (compatibility == 6) {
						w.append(t, "Average (6 rounds)");
					} else if (compatibility == 5) {
						w.append(t, "Poor (5 rounds)");
					} else {
						w.append(t, "Terrible (4 rounds)");
					}
				}
			}
			w.append(t, "\n\nYou will invade a neighborhood along " + c.getMainName() + "'s patrol path in order to lure " + c.himHer() + " in an attack " + c.himHer() + ".  " + w.usedForsaken.mainName + " will lie in wait, ready to engage " + c.himHer() + " in battle upon your order.");
		} else {
			w.append(t, "You will invade a neighborhood along " + c.getMainName() + "'s patrol path in order to lure " + c.himHer() + " in and attack " + c.himHer() + ".");
		}
		if (immediateAction) {
			w.append(t, "Which action do you want the Thralls to perform once they grab " + c.himHer() + "?");
			for (int i = 0; i < 4; i++) {
				final int type = i;
				String torment = "ERROR";
				if (i == 0) {
					torment = "Grind";
				} else if (i == 1) {
					torment = "Caress";
				} else if (i == 2) {
					if (w.tickle()) {
						torment = "Tickle";
					} else {
						torment = "Pummel";
					}
				} else {
					torment = "Humiliate";
				}
				final String finalTorment = torment;
				JButton Action = new JButton(torment);
				Action.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						p.removeAll();
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						w.printCommanderSummary(t, c);
						w.append(t, "  The Thralls will begin by using " + finalTorment + " on " + c.himHer() + ".  Is this okay?");
						JButton Confirm = new JButton("Confirm");
						Confirm.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								if (w.usedForsaken != null || w.recordedCommanders.length < w.day - 1) {
									w.commentaryRead = false;
									w.commentaryWrite= false;
								}
								if (w.getDay() > 1 && w.isCheater() == false && (w.commentaryWrite || w.commentaryRead)) {
									w.archiveCommander(w.getDay());
								}
								advanceDowntimeAction(p, w, w.getTechs().length + w.getCast().length + c.getNumber()*4 + type);
								if (type == 0) {
									c.beginGrind();
								} else if (type == 1) {
									c.beginCaress();
								} else if (type == 2) {
									c.beginPummel();
								} else if (type == 3) {
									c.beginHumiliate();
								}
								if (w.getDay() == 50 - w.eventOffset*3 || w.getTechs()[48].isOwned()) {
									BeginFinalBattle(t, p, f, w, c);
								} else {
									BeginBattle(t, p, f, w, c);
								}
							}
						});
						p.add(Confirm);
						JButton Cancel = new JButton("Cancel");
						Cancel.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								Shop(t, p, f, w);
							}
						});
						p.add(Cancel);
						p.validate();
						p.repaint();
					}
				});
				p.add(Action);
			}
		} else {
			w.append(t, "  Is this okay?");
			JButton Confirm = new JButton("Confirm");
			Confirm.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (w.usedForsaken != null || w.recordedCommanders.length < w.day - 1) {
						w.commentaryRead = false;
						w.commentaryWrite= false;
					}
					if (w.getDay() > 1 && w.isCheater() == false && (w.commentaryRead || w.commentaryWrite)) {
						w.archiveCommander(w.getDay());
					}
					advanceDowntimeAction(p, w, w.getTechs().length + c.getNumber());
					if (w.getDay() == 50 - w.eventOffset*3 || w.getTechs()[48].isOwned()) {
						BeginFinalBattle(t, p, f, w, c);
					} else {
						BeginBattle(t, p, f, w, c);
					}
				}
			});
			p.add(Confirm);
		}
		JButton Cancel = new JButton("Cancel");
		Cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Shop(t, p, f, w);
			}
		});
		p.add(Cancel);
		p.validate();
		//f.pack();
		p.repaint();
	}
	
	public static void BeginBattle(JTextPane t, JPanel p, JFrame f, WorldState w, Chosen c) {
		w.incrementTotalRounds();
		Chosen[] newCombatants = new Chosen[3];
		newCombatants[0] = c;
		w.newCombat(w, newCombatants);
		if (w.getBodyStatus()[0] && w.getBodyStatus()[2] == false) {
			if (w.getTechs()[31].isOwned() && w.upgradedCommander() == false) {
				w.setBattleRound(0);
				c.BeSurrounded(t, p, f, w);
			} else {
				w.append(t, "\n\n" + w.getSeparator() + "\n\n");
				w.append(t, "You lure " + c.getMainName() + " into battle with an attack on a neighborhood along " + c.hisHer() + " patrol route.  ");
				if (w.upgradedCommander()) {
					w.append(t, "Then, you spring your ambush.  ");
					c.startCaptured(t, w);
				} else {
					w.append(t, "Then, with your Commander body on the battlefield, you set up an ambush.  ");
					if (w.getCapturesPossible() > 0) {
						w.append(t, "In the chaos, your body takes a serious injury, but with " + c.getMainName() + " surrounded, the battle has already progressed in the Demons' favor.");
					} else {
						w.append(t, "In the chaos, your body is destroyed, but with " + c.getMainName() + " surrounded, the battle has already progressed in the Demons' favor.");
					}
					c.setSurrounded(w);
					c.printSurroundedLine(t, w, c.getThisAttack());
				}
			}
		} else {
			w.append(t, "\n\n" + w.getSeparator() + "\n\n");
			w.append(t, "In the middle of a busy street, a horde of Demons suddenly erupts from underground");
			if (w.getBodyStatus()[0]) {
				w.append(t, ", led by the Commander body you're remotely controlling.");
			} else {
				w.append(t, "!");
			}
			w.append(t, "  They begin attacking civilians and dragging them away for conversion to the Demonic cause, but before they can get too far, a thundering burst of light shines down on the scene!\n\n");
			c.say(t, "\"" + c.announcement() + "\"\n\n");
			c.transform(t, w);
		}
		w.append(t, "\n");
		PickTarget(t, p, f, w);
	}
	
	public static void BeginFinalBattle(JTextPane t, JPanel p, JFrame f, WorldState w, Chosen c) {
		w.incrementTotalRounds();
		Chosen[] newCombatants = new Chosen[3];
		newCombatants[0] = c;
		w.newCombat(w, newCombatants);
		if (w.getDay() == 50 - w.eventOffset*3) {
			w.append(t, "\n\n" + w.getSeparator() + "\n\nThe city's streets are devoid of life.  In preparation for the coming battle, the residents have been evacuated to temporary housing in the surrounding countryside.  The only ones who remain are the stubborn, the thrillseekers, some entrepreneurial journalists, and of course your minions.  They all know what's coming, and they're waiting for you to make your move.\n\nFinally, the silence is broken by the sound of shattering pavement.  An enormous, dark shape rises out of the ground, toppling buildings and sending tons of rubble spilling in all directions as it grows.  It's an enormous pillar whose surface shimmers like an oil slick, and it continues upward until it dwarfs the skyscrapers below, penetrating the heavens themselves.  All throughout the city, space begins to warp and shift as you corrupt the fabric of reality and bend it to your will.\n\n" + c.getMainName() + " is the closest of the Chosen to the epicenter.  Although " + c.hisHer() + " instincts are telling " + c.himHer() + " to immediately begin drawing on as much energy as " + c.heShe() + " can, " + c.heShe() + " recalls from the strategy briefing that it will still take some time to evacuate the last few VIPs who had to stay until the last moment.  The neighboring cities will also need a chance to prepare for the destructive electromagnetic pulses that are likely to be released as the Chosen fight at full power.\n\n");
		} else {
			w.append(t, "\n\n" + w.getSeparator() + "\n\nThe city's streets are bustling as if this were a day like any other.  Its citizens have no idea how close your plans are to completion.\n\nWithout warning, the pavement of one of the main streets shatters and opens up.  An enormous, dark shape rises out of the ground, toppling buildings and sending tons of rubble spilling in all directions as it grows.  It's an enormous pillar whose surface shimmers like an oil slick, and it continues upward until it dwarfs the skyscrapers below, penetrating the heavens themselves.  All throughout the city, space begins to warp and shift as you corrupt the fabric of reality and bend it to your will.\n\n" + c.getMainName() + " is the closest of the Chosen to the epicenter.  Although " + c.hisHer() + " instincts are telling " + c.himHer() + " to immediately begin drawing on as much energy as " + c.heShe() + " can, " + c.heShe() + " has orders to restrain " + c.himHer() + "self until " + c.heShe() + "'s given clearance to go all-out.  Loudspeakers across the city broadcast instructions to the Chosen as they all hurry toward the tower, warning them that this will be the final battle and that they may not survive.  They're told to hold back at least until the most important VIPs can get a safe distance from the city.  It goes unsaid that the rest of the populace is considered an acceptable sacrifice.\n\n");
		}
		w.finalBattleIntro(t, c);
		if (w.getBodyStatus()[0] && w.getBodyStatus()[2] == false) {
			if (w.getTechs()[31].isOwned() && w.upgradedCommander() == false) {
				w.setBattleRound(0);
				c.BeSurrounded(t, p, f, w);
			} else {
				w.append(t, "\n\nBefore the Chosen can meet up with each other, you spring your ambush.  ");
				if (w.upgradedCommander()) {
					c.startCaptured(t, w);
				} else {
					w.append(t, "Led by your Commander body, your minions emerge from their hiding places and rush in from all directions.  ");
					if (w.getCapturesPossible() > 0) {
						w.append(t, "In the chaos, your body takes a serious injury, but with " + c.getMainName() + " surrounded, the battle has already progressed in the Demons' favor.");
					} else {
						w.append(t, "In the chaos, your body is destroyed, but with " + c.getMainName() + " surrounded, the battle has already progressed in the Demons' favor.");
					}
					c.setSurrounded(w);
					c.printSurroundedLine(t, w, c.getThisAttack());
				}
			}
		}
		w.append(t, "\n");
		PickTarget(t, p, f, w);
	}
	
	public static void DefeatScene(JTextPane t, JPanel p, JFrame f, WorldState w) {
		Chosen[] killed = new Chosen[2];
		Chosen[] fallen = new Chosen[2];
		Chosen[] escaped = new Chosen[3];
		for (int i = 0; i < 3; i++) {
			if (w.getCast()[i].alive == false) {
				if (killed[0] == null) {
					killed[0] = w.getCast()[i];
				} else {
					killed[1] = w.getCast()[i];
				}
			} else if (w.getCast()[i].resolve <= 0) {
				if (fallen[0] == null) {
					fallen[0] = w.getCast()[i];
				} else {
					fallen[1] = w.getCast()[i];
				}
			} else {
				if (escaped[0] == null) {
					escaped[0] = w.getCast()[i];
				} else if (escaped[1] == null) {
					escaped[1] = w.getCast()[i];
				} else {
					escaped[2] = w.getCast()[i];
				}
			}
		}
		if (escaped[2] != null) {
			w.append(t, "With the Demons all exterminated, there's nothing to stop the Chosen from launching their final maneuver.  They split up, flying in different directions across the city until they can barely see each other as tiny glowing specks against the darkening sky.  Then, they extend their arms toward each other, forming the points of an enormous triangle - with the Demonic spire at its center.\n\n");
			Chosen high = null;
			Chosen mid = null;
			Chosen low = null;
			for (int i = 0; i < 3; i++) {
				if (w.getCast()[i].getConfidence() > 66) {
					high = w.getCast()[i];
				} else if (w.getCast()[i].getConfidence() > 33) {
					mid = w.getCast()[i];
				} else {
					low = w.getCast()[i];
				}
			}
			high.say(t, "\"");
			if (w.getRelationship(high.getNumber(), mid.getNumber()) >= 0) {
				if (w.getRelationship(high.getNumber(), low.getNumber()) >= 0) {
					high.say(t, "Let's do it!  Just like we practiced!\"\n\n");
					mid.say(t, "\"");
					if (w.getRelationship(mid.getNumber(), low.getNumber()) >= 0) {
						mid.say(t, "I'm ready!  You okay, " + low.getMainName() + "!?\"\n\n");
						low.say(t, "\"R-Ready here too!");
					} else {
						mid.say(t, "I'm ready!  You'd better not screw it up for us, " + low.getMainName() + "!\"\n\n");
						low.say(t, "\"I-I won't let you down, " + high.getMainName() + "!");
					}
				} else {
					high.say(t, "You'd better not screw this up, " + low.getMainName() + "!\"\n\n");
					mid.say(t, "\"");
					if (w.getRelationship(mid.getNumber(), low.getNumber()) >= 0) {
						mid.say(t, "Don't worry, " + low.getMainName() + "!  I believe in you!\"\n\n");
						low.say(t, "\"R-Right!");
					} else {
						mid.say(t, "Don't worry, we can finish this without " + low.himHer() + " if we have to!\"\n\n");
						low.say(t, "\"I-I'm fine, just worry about yourselves!");
					}
				}
			} else {
				if (w.getRelationship(high.getNumber(), low.getNumber()) >= 0) {
					high.say(t, "You'd better not screw this up, " + mid.getMainName() + "!\"\n\n");
					mid.say(t, "\"");
					if (w.getRelationship(mid.getNumber(), low.getNumber()) >= 0) {
						mid.say(t, "Worry about yourself, " + high.getMainName() + "!\"\n\n");
						low.say(t, "\"P-Please, you two, we shouldn't be fighting each other now of all times!");
					} else {
						mid.say(t, "You know that if anyone's going to screw up here, it's " + low.getMainName() + "!\"\n\n");
						low.say(t, "\"I-I won't!  I'm ready, " + high.getMainName() + "!");
					}
				} else {
					high.say(t, "You two had better not screw this up!\"\n\n");
					mid.say(t, "\"");
					if (w.getRelationship(mid.getNumber(), low.getNumber()) >= 0) {
						mid.say(t, "We definitely won't!  Right, " + mid.getMainName() + "!?\"\n\n");
						low.say(t, "\"R-Right!");
					} else {
						mid.say(t, "I definitely won't, though I'm not sure about " + mid.getMainName() + "...\"\n\n");
						low.say(t, "\"I-I'm fine!  So let's do this!");
					}
				}
			}
			low.say(t, "\"\n\n");
			w.append(t, "The three Chosen concentrate, and the space between their outstretched hands briefly shimmers like a pane of glass.  A sharp glint cuts across the city, and suddenly the black tower is severed at its base.  It topples downward, striking the ground with a great rumble and a cloud of dust.  As quickly as that, the Demonic presence over the city lifts.\n\nThe battle is over.  But even though this Demon Lord has been defeated, the scars left on the hearts of the Chosen won't heal so easily.  Their troubles may be just beginning...");
		} else if (escaped[1] != null) {
			Chosen first = escaped[0];
			Chosen second = escaped[1];
			if (second.getConfidence() > first.getConfidence()) {
				first = escaped[1];
				second = escaped[0];
			}
			if (fallen[0] != null) {
				w.append(t, "With " + fallen[0].getMainName() + "'s defeat, a powerful evil energy is gathering on the battlefield.  The tip of the Demonic spire begins to glow, preparing to release one final pulse of corruption that will cement your domination over this region of reality.  However, at the same time, " + escaped[0].getMainName() + " and " + escaped[1].getMainName() + " have cleared out the last of your Demonic forces, and they're ready to launch their counterattack.");
			} else {
				w.append(t, "With every moment that passes, the Demonic spire grows upward, gathering power and deepening your domination over this region of reality.  However, by sacrificing " + killed[0].getMainName() + "'s life, " + escaped[0].getMainName() + " and " + escaped[1].getMainName() + " have exterminated all the surrounding Demons, and they're ready to launch their counterattack.");
			}
			if (w.getRelationship(first.getNumber(), second.getNumber()) >= 0) {
				first.say(t, "\n\n\"If we work together, I think we can still stop it!  Back me up!\"\n\n");
				second.say(t, "\"Got it!  I'm right behind you!\"\n\n");
				w.append(t, first.getMainName() + " charges forward, blazing brighter than the sun as " + first.heShe() + " draws on as much psychic energy as " + first.heShe() + " can.  " + second.getMainName() + " has a hand on " + first.hisHer() + " shoulder, pushing " + first.himHer() + " forward as they fly together.  The two of them blast through the base of the tower, leaving an enormous hole behind them.  And with its lower structure compromised, the shaft begins to topple to one side.  It lands on the city with a deafening crash, kicking up a huge cloud of debris.  As quickly as that, the Demonic presence over the city lifts.\n\nThe battle is over.  But even though this Demon Lord has been defeated, the scars left on the hearts of the Chosen won't heal so easily.  ");
			} else {
				first.say(t, "\n\n\"If we're going to take that thing down, we need to go all-out!  Don't hold back, or you'll die!\"\n\n");
				second.say(t, "\"Huh?  Gaaah!  Ergh... you're... crazy...!\"\n\n");
				w.append(t, first.getMainName() + " holds out one palm to shoot a beam of crackling destructive energy directly at " + second.getMainName() + ".  For " + second.hisHer() + " part, " + second.getMainName() + " barely reacts in time to intercept the beam with " + second.hisHer() + " own blast.  The glowing line between " + first.getMainName() + "'s hand and " + second.getMainName() + "'s annihilates everything it touches as the two of them run toward the Demonic spire.  When it cuts into the base of the tower, the opposing energies cause a huge explosion that throws the two Chosen in different directions.  When they come to their senses, they see the structure beginning to tilt to one side.  It finally topples, throwing up a huge cloud of debris as it lands on the city below.  As quickly as that, the Demonic presence over the city lifts.\n\nThe battle is over.  But even though this Demon Lord has been defeated, the scars left on the hearts of the Chosen won't heal so easily.  ");
			}
			if (fallen[0] != null) {
				w.append(t, first.getMainName() + " and " + second.getMainName() + " have their own troubles to deal with, and " + fallen[0].getMainName() + " is nowhere to be found...");
			} else {
				w.append(t, "Their troubles may be just beginning...");
			}
		} else {
			w.append(t, "After exterminating the intervening Demons, " + escaped[0].getMainName() + " attacks the Demonic spire, drawing on as much power as " + escaped[0].heShe() + " can in an attempt to destroy it.  ");
			if (fallen[1] != null) {
				w.append(t, "But with the other two Chosen having succumbed to the Demons, " + escaped[0].heShe() + "'s finding that " + escaped[0].heShe() + " isn't able to make a dent in it on " + escaped[0].hisHer() + " own.");
			} else if (killed[1] != null) {
				w.append(t, "But as the only survivor among the three Chosen, " + escaped[0].heShe() + "'s finding that " + escaped[0].heShe() + " isn't able to make a dent in it on " + escaped[0].hisHer() + " own.");
			} else {
				w.append(t, "But with " + killed[0].getMainName() + " dead and " + fallen[0].getMainName() + " having succumbed to the Demons, " + escaped[0].getMainName() + " is finding that " + escaped[0].heShe() + " isn't able to make a dent in it on " + escaped[0].hisHer() + " own.");
			}
			escaped[0].say(t, "\n\n\"");
			if (escaped[0].getConfidence() > 66) {
				escaped[0].say(t, "No!  I... I should be strong enough...!");
			} else if (escaped[0].getConfidence() > 33) {
				escaped[0].say(t, "Ugh...  We were so close...");
			} else {
				escaped[0].say(t, "I'm... I'm too weak after all...");
			}
			escaped[0].say(t, "\"\n\n");
			if (escaped[0].isDrained()) {
				w.append(t, escaped[0].getMainName() + " falls to " + escaped[0].hisHer() + " knees, overwhelmed by despair.  " + escaped[0].HeShe() + " spots a nearby knife discarded by a Thrall and takes it in " + escaped[0].hisHer() + " hands.  Before the fight, " + escaped[0].heShe() + " was worried about hurting " + escaped[0].himHer() + "self too badly to fight, but not badly enough to actually be fatal.  But now, " + escaped[0].heShe() + " has nothing left to lose...");
			} else if (escaped[0].isParasitized()) {
				w.append(t, escaped[0].getMainName() + " turns and flies away, fleeing the battle.  However, " + escaped[0].hisHer() + " clothes begin to flicker and fade, and " + escaped[0].heShe() + " has a harder and harder time staying airborne.  Combined with " + escaped[0].hisHer() + " damaged reputation, this last failure has proven fatal for " + escaped[0].hisHer() + " connection to the public's psychic energy.  By the time " + escaped[0].heShe() + " reaches the neighboring city, " + escaped[0].heShe() + "'ll return to being nothing more than ");
				if (escaped[0].getMainName().equals(escaped[0].getGivenName()) == false) {
					w.append(t, escaped[0].getGivenName() + ", ");
				}
				w.append(t, "a typical powerless human.");
			} else if (escaped[0].isImpregnated()) {
				w.append(t, escaped[0].getMainName() + " turns and flies away, fleeing the battle.  " + escaped[0].HeShe() + " has no intention of returning to the military and reporting " + escaped[0].hisHer() + " failure, because " + escaped[0].heShe() + " knows that soon, " + escaped[0].hisHer() + " Demonic pregnancy will begin to show.  " + escaped[0].HisHer() + " life as one of the Chosen is over, and " + escaped[0].hisHer() + " life as a fugitive begins...");
			} else if (escaped[0].isHypnotized()) {
				w.append(t, "As much as it pains " + escaped[0].himHer() + " to do so, " + escaped[0].getMainName() + " turns and flees.  This battle may be lost, but " + escaped[0].heShe() + "'s determined to escape and survive to fight another day.  However, even after " + escaped[0].heShe() + " escapes the range of your influence, your post-hypnotic commands continue to linger in " + escaped[0].hisHer() + " subconscious.  It remains to be seen what sort of depravity " + escaped[0].heShe() + "'ll get into...");
			} else {
				w.append(t, "With no other options remaining, " + escaped[0].getMainName() + " turns to flee.");
			}
		}
		EndFinalBattle(t, p, f, w);
	}
	
	public static void EndFinalBattle(JTextPane t, JPanel p, JFrame f, WorldState w) {
		Chosen escaped = null;
		Chosen[] defeated = new Chosen[3];
		int numberDefeated = 0;
		int numberKilled = 0;
		for (int i = 0; i < 3; i++) {
			if (w.getCast()[i].alive == false) {
				numberKilled++;
			} else if (w.getCast()[i].resolve <= 0) {
				defeated[numberDefeated] = w.getCast()[i];
				numberDefeated++;
			} else {
				escaped = w.getCast()[i];
			}
		}
		if (numberKilled + numberDefeated == 2) {
			int type = 0;
			if (escaped.isDrained()) {
				type = 3;
			} else if (escaped.isImpregnated()) {
				type = 1;
			} else if (escaped.isParasitized()) {
				type = 4;
			} else if (escaped.isHypnotized()) {
				type = 2;
			}
			final int sceneShown = type;
			final Chosen sceneSubject = escaped;
			if (type != 0) {
				p.removeAll();
				JButton Continue = new JButton("Continue");
				Continue.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						w.append(t, "\n\n" + w.getSeparator() + "\n\n");
						w.ending(t, sceneShown, sceneSubject, null, null);
						ReportScore(t, p, f, w);
					}
				});
				p.add(Continue);
				p.validate();
				p.repaint();
			} else {
				ReportScore(t, p, f, w);
			}
		} else {
			ReportScore(t, p, f, w);
		}
	}
	
	public static void ReportScore(JTextPane t, JPanel p, JFrame f, WorldState w) {
		p.removeAll();
		JButton Continue = new JButton("Continue");
		Continue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.removeAll();
				int forsaken = 0;
				int casualties = 0;
				int returning = 0;
				Chosen[] corrupted = new Chosen[3];
				Chosen[] killed = new Chosen[3];
				Chosen[] escaped = new Chosen[3];
				for (int i = 0; i < 3; i++) {
					if (w.getCast()[i].alive && w.getCast()[i].resolve <= 0) {
						corrupted[forsaken] = w.getCast()[i];
						forsaken++;
					} else if (w.getCast()[i].alive) {
						escaped[returning] = w.getCast()[i];
						returning++;
					} else {
						killed[casualties] = w.getCast()[i];
						casualties++;
					}
				}
				if (w.isCheater() == false && w.hardMode) {
					w.scoreSummary(t);
				}
				w.finalBattle = false;
				w.getTechs()[48].owned = false;
				for (int i = 0; i < 3; i++) {
					w.getCast()[i].alive = true;
					w.getCast()[i].resolve = 100;
				}
				w.incrementDay();
				w.clearCommander();
				for (int i = 0; i < 3; i++) {
					w.getCast()[i].addTrauma();
					w.getCast()[i].surrounded = false;
					w.getCast()[i].captured = false;
					w.getCast()[i].removeSurround = false;
				}
				if (forsaken+casualties >= 2 || w.campaign == false) {
					JButton ContinueFour = new JButton("Continue Playing");
					ContinueFour.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							Shop(t, p, f, w);
						}
					});
					if (w.campaign) {
						ContinueFour.setText("Continue");
					}
					p.add(ContinueFour);
				}
				if ((w.isCheater() || w.hardMode == false) && w.campaign == false) {
					w.append(t, "\n\n" + w.getSeparator() + "\n\nI hope you enjoyed this playthrough of Corrupted Saviors!  For an even greater challenge, consider trying Hard Mode or Campaign Mode!");
				}
				if (w.campaign) {
					if (forsaken+casualties >= 2) {
						w.append(t, "\n\n" + w.getSeparator() + "\n\nThe city of " + w.cityName + " has fallen under your control.  ");
						if (w.day < 50 - w.eventOffset) {
							w.append(t, "Your followers won't be able to establish a foothold in another city right away, so you can take some time to consolidate power here and enjoy your conquest.");
						} else {
							w.append(t, "Now all that remains is to decide where to strike next.");
						}
						w.loopComplete = true;
					} else {
						w.append(t, "\n\n" + w.getSeparator() + "\n\nWith your defeat in " + w.cityName + ", the momentum of your advance across the globe has been halted.  However, so long as there is darkness within the human heart, a Demon Lord cannot be truly killed.  Before long, you will rise again.");
						if (w.conquered.length > 0 || w.sacrificed.length > 0) {
							w.append(t, "  And in the meantime, there's much enjoyment to be had from your conquests...");
						}
					}
				}
				if (forsaken > 0) {
					w.append(t, "\n\n");
					if (forsaken == 1) {
						w.append(t, corrupted[0].getMainName() + " has ");
					} else if (forsaken == 2) {
						w.append(t, corrupted[0].getMainName() + " and " + corrupted[1].getMainName() + " have ");
					} else {
						w.append(t, corrupted[0].getMainName() + ", " + corrupted[1].getMainName() + ", and " + corrupted[2].getMainName() + " have ");
					}
					if (w.campaign == false) {
						w.append(t, "been added to the ranks of the Forsaken!  You can interact with them from the Main Menu, and you may also use them to help corrupt new Chosen in future playthroughs!");
					} else if (forsaken+casualties >= 2) {
						w.append(t, "been added to the ranks of the Forsaken!  This will surely prove useful against the Chosen you've yet to face.");
					} else {
						w.append(t, "been added to the ranks of the Forsaken!  This makes for a fine consolation prize.");
					}
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
					File saveLocation = new File(path + java.io.File.separator + "saves.sav");
					SaveData saves = null;
					if (saveLocation.exists()) {
						ReadObject robj = new ReadObject();
						saves = robj.deserializeSaveData(path + java.io.File.separator + "saves.sav");
					} else {
						saves = new SaveData();
					}
					for (int i = 0; i < saves.getSaves().length; i++) {
						saves.getSaves()[i].repairSave();
					}
					WriteObject wobj = new WriteObject();
					final SaveData saveFile = saves;
					for (int i = 0; i < 3; i++) {
						if (forsaken > i) {
							int index = 0;
							if (w.campaign == false && saves.harem == null) {
								saves.harem = new Forsaken[1];
							} else {
								index = w.getHarem().length;
							}
							Forsaken[] newHarem = new Forsaken[index+1];
							for (int j = 0; j < index; j++) {
								newHarem[j] = w.getHarem()[j];
							}
							Forsaken newForsaken = new Forsaken();
							newForsaken.initialize(w, corrupted[i]);
							newHarem[index] = newForsaken;
							newHarem[index].forsakenID = saves.assignID();
							newHarem[index].otherChosen = new Chosen[2];
							newHarem[index].chosenRelations = new Forsaken.Relationship[2];
							for (int j = 0; j < 3; j++) {
								if (j < i) {
									newHarem[index].otherChosen[j] = w.getCast()[j];
									newHarem[index].chosenRelations[j] = Forsaken.Relationship.PARTNER;
								} else if (j > i) {
									newHarem[index].otherChosen[j-1] = w.getCast()[j];
									newHarem[index].chosenRelations[j-1] = Forsaken.Relationship.PARTNER;
								}
							}
							if (i == 1) {
								int originalRelationship = w.getRelationship(corrupted[0].number, corrupted[i].number);
								newForsaken.firstPartner = newHarem[index-1];
								newForsaken.firstFormerPartner = corrupted[0];
								newHarem[index-1].firstPartner = newForsaken;
								newHarem[index-1].firstFormerPartner = corrupted[1];
								newForsaken.firstOriginalRelationship = originalRelationship;
								newHarem[index-1].firstOriginalRelationship = originalRelationship;
								if (casualties > 0) {
									newForsaken.secondFormerPartner = killed[0];
									newForsaken.secondOriginalRelationship = w.getRelationship(corrupted[1].number, killed[0].number);
									newHarem[index-1].secondFormerPartner = killed[0];
									newHarem[index-1].secondOriginalRelationship = w.getRelationship(corrupted[0].number, killed[0].number);
								} else if (returning > 0) {
									newForsaken.secondFormerPartner = escaped[0];
									newForsaken.secondOriginalRelationship = w.getRelationship(corrupted[1].number, escaped[0].number);
									newHarem[index-1].secondFormerPartner = escaped[0];
									newHarem[index-1].secondOriginalRelationship = w.getRelationship(corrupted[0].number, escaped[0].number);
								}
								newForsaken.others = new Forsaken[]{newHarem[index-1]};
								newForsaken.forsakenRelations = new Forsaken.Relationship[]{Forsaken.Relationship.PARTNER};
								newForsaken.troublemaker = new int[1];
								newHarem[index-1].others = new Forsaken[]{newForsaken};
								newHarem[index-1].forsakenRelations = new Forsaken.Relationship[]{Forsaken.Relationship.PARTNER};
								newHarem[index-1].troublemaker = new int[1];
							} else if (i == 2) {
								int firstOriginalRelationship = w.getRelationship(corrupted[0].number, corrupted[2].number);
								int secondOriginalRelationship = w.getRelationship(corrupted[1].number, corrupted[2].number);
								newForsaken.firstPartner = newHarem[index-2];
								newForsaken.firstFormerPartner = corrupted[0];
								newForsaken.firstOriginalRelationship = firstOriginalRelationship;
								newHarem[index-2].secondPartner = newForsaken;
								newHarem[index-2].secondFormerPartner = corrupted[2];
								newHarem[index-2].secondOriginalRelationship = firstOriginalRelationship;
								newForsaken.secondPartner = newHarem[index-1];
								newForsaken.secondFormerPartner = corrupted[1];
								newForsaken.secondOriginalRelationship = secondOriginalRelationship;
								newHarem[index-1].secondPartner = newForsaken;
								newHarem[index-1].secondFormerPartner = corrupted[2];
								newHarem[index-1].secondOriginalRelationship = secondOriginalRelationship;
								newForsaken.others = new Forsaken[]{newHarem[index-1], newHarem[index-2]};
								newForsaken.forsakenRelations = new Forsaken.Relationship[]{Forsaken.Relationship.PARTNER, Forsaken.Relationship.PARTNER};
								newForsaken.troublemaker = new int[2];
								newHarem[index-1].others = new Forsaken[]{newForsaken, newHarem[index-2]};
								newHarem[index-1].forsakenRelations = new Forsaken.Relationship[]{Forsaken.Relationship.PARTNER, Forsaken.Relationship.PARTNER};
								newHarem[index-1].troublemaker = new int[2];
								newHarem[index-2].others = new Forsaken[]{newForsaken, newHarem[index-1]};
								newHarem[index-2].forsakenRelations = new Forsaken.Relationship[]{Forsaken.Relationship.PARTNER, Forsaken.Relationship.PARTNER};
								newHarem[index-2].troublemaker = new int[2];
							}
							if (w.campaign) {
								w.conquered = newHarem;
							} else {
								saves.harem = newHarem;
								w.save = saves;
							}
						}
					}
					if (forsaken == 1) {
						Forsaken addition = w.getHarem()[w.getHarem().length-1];
						if (casualties > 0) {
							addition.firstFormerPartner = killed[0];
							addition.firstOriginalRelationship = w.getRelationship(corrupted[0].number, killed[0].number);
							if (returning > 0) {
								addition.secondFormerPartner = escaped[0];
								addition.secondOriginalRelationship = w.getRelationship(corrupted[0].number, escaped[0].number);
							} else {
								addition.secondFormerPartner = killed[1];
								addition.secondOriginalRelationship = w.getRelationship(corrupted[0].number, killed[1].number);
							}
						} else {
							addition.firstFormerPartner = escaped[0];
							addition.firstOriginalRelationship = w.getRelationship(corrupted[0].number, escaped[0].number);
							addition.secondFormerPartner = escaped[1];
							addition.secondOriginalRelationship = w.getRelationship(corrupted[0].number, escaped[1].number);
						}
					}
					wobj.serializeSaveData(saves);
					final SaveData fileUsed = saves;
					if (w.campaign == false) {
						JButton ForsakenMenu = new JButton("Forsaken Menu");
						ForsakenMenu.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								WorldState x = new WorldState();
								x.initialize();
								x.copySettings(t, w);
								x.copyToggles(w);
								x.setGenders(w.getGenderBalance());
								x.save = w.save;
								ForsakenMenu(t, p, f, x, fileUsed, 0);
							}
						});
						p.add(ForsakenMenu);
					}
				}
				if (w.campaign) {
					int numberDiscarded = 0;
					int numberKept = 0;
					Chosen[] discarded = new Chosen[3];
					Chosen[] kept = new Chosen[3];
					for (int i = 0; i < 3; i++) {
						if (w.getCast()[i].resolve > 0) {
							if (w.getCast()[i].alive == false || w.getCast()[i].impregnated || w.getCast()[i].hypnotized || w.getCast()[i].drained || w.getCast()[i].parasitized) {
								discarded[numberDiscarded] = w.getCast()[i];
							} else {
								kept[numberDiscarded] = w.getCast()[i];
							}
						}
					}
					if (numberKept > 0) {
						Chosen[] newReturning = new Chosen[w.returning.length+numberKept];
						for (int i = 0; i < w.returning.length; i++) {
							newReturning[i] = w.returning[i];
						}
						for (int i = 0; i < numberKept; i++) {
							newReturning[w.returning.length+i] = kept[i];
						}
						w.returning = newReturning;
					}
					if (numberDiscarded > 0) {
						Chosen[] newDeceased = new Chosen[w.deceased.length+numberDiscarded];
						for (int i = 0; i < w.deceased.length; i++) {
							newDeceased[i] = w.deceased[i];
						}
						for (int i = 0; i < numberDiscarded; i++) {
							newDeceased[w.deceased.length+i] = discarded[i];
						}
						w.deceased = newDeceased;
					}
					if (forsaken > 0) {
						Chosen[] newFormerChosen = new Chosen[w.formerChosen.length+forsaken];
						for (int i = 0; i < w.formerChosen.length; i++) {
							newFormerChosen[i] = w.formerChosen[i];
						}
						for (int i = 0; i < forsaken; i++) {
							newFormerChosen[w.formerChosen.length+i] = corrupted[i];
						}
						w.formerChosen = newFormerChosen;
					}
				}
				if (w.campaign && forsaken+casualties < 2) {
					if (w.conquered.length > 0 || w.sacrificed.length > 0) {
						w.append(t, "\n\n");
						WrapUpCampaign(t, p, f, w, null, null);
					} else {
						w.append(t, "\n\nThank you for playing the campaign mode of Corrupted Saviors!  If you're having trouble with the game, consider trying out Single Play mode, which allows the use of cheats after the final battle regardless of the result.");
						JButton Continue = new JButton("Main Menu");
						Continue.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								WorldState x = new WorldState();
								x.initialize();
								x.copySettings(t, w);
								x.copyToggles(w);
								x.setGenders(w.getGenderBalance());
								x.save = w.save;
								IntroOne(t, p, f, x);
							}
						});
						p.add(Continue);
					}
				}
				p.validate();
				p.repaint();
			}
		});
		p.add(Continue);
		p.validate();
		p.repaint();
	}
	
	public static void WrapUpCampaign(JTextPane t, JPanel p, JFrame f, WorldState w, Boolean[] broughtConquered, Boolean[] broughtSacrificed) {
		p.removeAll();
		int conquered = w.conquered.length;
		int sacrificed = w.sacrificed.length;
		if (broughtConquered == null || broughtSacrificed == null) {
			broughtConquered = new Boolean[conquered];
			for (int i = 0; i < conquered; i++) {
				broughtConquered[i] = true;
			}
			broughtSacrificed = new Boolean[sacrificed];
			for (int i = 0; i < sacrificed; i++) {
				broughtSacrificed[i] = true;
			}
		} else {
			w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		}
		if (conquered > 0 && sacrificed > 0) {
			w.append(t, "You can bring your Forsaken with you to use in Single Play mode, including the Forsaken who were disposed of during play.");
		} else if (sacrificed > 0) {
			w.append(t, "Even though you didn't end the game with any Forsaken, you can still bring with you the ones you disposed of during play.");
		} else {
			w.append(t, "You can bring your Forsaken with you to use in Single Play mode.");
		}
		Boolean saveAllConquered = false;
		Boolean deleteAllConquered = false;
		Boolean saveAllSacrificed = false;
		Boolean deleteAllSacrificed = false;
		if (conquered > 0) {
			w.append(t, "\n\nCurrent Forsaken\n");
			for (int i = 0; i < conquered; i++) {
				w.append(t, "\n" + w.conquered[i].mainName + ": ");
				if (broughtConquered[i]) {
					w.greenAppend(t, "SAVE");
					deleteAllConquered = true;
				} else {
					w.redAppend(t, "DELETE");
					saveAllConquered = true;
				}
			}
		}
		if (sacrificed > 0) {
			w.append(t, "\n\nFormer Forsaken\n");
			for (int i = 0; i < sacrificed; i++) {
				w.append(t, "\n" + w.sacrificed[i].mainName + ": ");
				if (broughtSacrificed[i]) {
					w.greenAppend(t, "SAVE");
					deleteAllSacrificed = true;
				} else {
					w.redAppend(t, "DELETE");
					saveAllSacrificed = true;
				}
			}
		}
		final Boolean[] conqueredSetting = broughtConquered;
		final Boolean[] sacrificedSetting = broughtSacrificed;
		if (saveAllConquered) {
			JButton Save = new JButton("Save All Current");
			Save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Boolean[] newSaved = new Boolean[conquered];
					for (int i = 0; i < conquered; i++) {
						newSaved[i] = true;
					}
					WrapUpCampaign(t, p, f, w, newSaved, sacrificedSetting);
				}
			});
			p.add(Save);
		}
		if (deleteAllConquered) {
			JButton Delete = new JButton("Delete All Current");
			Delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Boolean[] newSaved = new Boolean[conquered];
					for (int i = 0; i < conquered; i++) {
						newSaved[i] = false;
					}
					WrapUpCampaign(t, p, f, w, newSaved, sacrificedSetting);
				}
			});
			p.add(Delete);
		}
		if (saveAllSacrificed) {
			JButton Save = new JButton("Save All Former");
			Save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Boolean[] newSaved = new Boolean[sacrificed];
					for (int i = 0; i < sacrificed; i++) {
						newSaved[i] = true;
					}
					WrapUpCampaign(t, p, f, w, conqueredSetting, newSaved);
				}
			});
			p.add(Save);
		}
		if (deleteAllSacrificed) {
			JButton Save = new JButton("Delete All Former");
			Save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Boolean[] newSaved = new Boolean[sacrificed];
					for (int i = 0; i < sacrificed; i++) {
						newSaved[i] = false;
					}
					WrapUpCampaign(t, p, f, w, conqueredSetting, newSaved);
				}
			});
			p.add(Save);
		}
		JButton Decide = new JButton("Decide Individually");
		Decide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DecideKeptForsaken(t, p, f, w, conqueredSetting, sacrificedSetting, 0);
			}
		});
		p.add(Decide);
		JButton Done = new JButton("Done");
		Done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				p.removeAll();
				w.append(t, "\n\n" + w.getSeparator() + "\n\nAre you sure?  ");
				int brought = 0;
				int deleted = 0;
				for (int i = 0; i < conquered; i++) {
					if (conqueredSetting[i]) {
						brought++;
					} else {
						deleted++;
					}
				}
				for (int i = 0; i < sacrificed; i++) {
					if (sacrificedSetting[i]) {
						brought++;
					} else {
						deleted++;
					}
				}
				if (deleted == 0) {
					w.append(t, "All Forsaken from this playthrough will be added to the save file.");
				} else if (brought == 0) {
					w.append(t, "All Forsaken from this playthrough will be deleted and can only be recovered by loading an old campaign save.");
				} else {
					w.append(t, brought + " Forsaken from this playthrough will be added to the save file and the other " + deleted + " will be deleted.  Deleted Forsaken can only be recovered by loading an old campaign save.");
				}
				final int totalBrought = brought;
				JButton Confirm = new JButton("Confirm");
				Confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (totalBrought > 0) {
							if (w.save.harem == null) {
								w.save.harem = new Forsaken[0];
							}
							Forsaken[] newHarem = new Forsaken[w.save.harem.length+totalBrought];
							for (int i = 0; i < w.save.harem.length; i++) {
								newHarem[i] = w.save.harem[i];
							}
							int additional = 0;
							for (int i = 0; i < conquered; i++) {
								if (conqueredSetting[i]) {
									newHarem[w.save.harem.length+additional] = w.conquered[i];
									additional++;
								}
							}
							for (int i = 0; i < sacrificed; i++) {
								if (sacrificedSetting[i]) {
									newHarem[w.save.harem.length+additional] = w.sacrificed[i];
									additional++;
								}
							}
							w.save.harem = newHarem;
						}
						t.setText("");
						WorldState x = new WorldState();
						x.initialize();
						x.copySettings(t, w);
						x.copyToggles(w);
						x.setGenders(w.getGenderBalance());
						x.save = w.save;
						IntroOne(t, p, f, x);
					}
				});
				p.add(Confirm);
				JButton Cancel = new JButton("Cancel");
				Cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						WrapUpCampaign(t, p, f, w, conqueredSetting, sacrificedSetting);
					}
				});
				p.add(Cancel);
				p.validate();
				p.repaint();
			}
		});
		p.add(Done);
		p.validate();
		p.repaint();
	}
	
	public static void DecideKeptForsaken(JTextPane t, JPanel p, JFrame f, WorldState w, Boolean[] broughtConquered, Boolean[] broughtSacrificed, int position) {
		p.removeAll();
		if (position < broughtConquered.length) {
			w.append(t, "\n\n" + w.getSeparator() + "\n\nWhat will you do with " + w.conquered[position].mainName + "?");
			JButton Save = new JButton("Save");
			Save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					broughtConquered[position] = true;
					DecideKeptForsaken(t, p, f, w, broughtConquered, broughtSacrificed, position+1);
				}
			});
			p.add(Save);
			JButton Delete = new JButton("Delete");
			Delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					broughtConquered[position] = false;
					DecideKeptForsaken(t, p, f, w, broughtConquered, broughtSacrificed, position+1);
				}
			});
			p.add(Delete);
		} else if (position < broughtConquered.length + broughtSacrificed.length) {
			w.append(t, "\n\n" + w.getSeparator() + "\n\nWhat will you do with " + w.sacrificed[position-broughtConquered.length].mainName + "?");
			JButton Save = new JButton("Save");
			Save.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					broughtSacrificed[position-broughtConquered.length] = true;
					DecideKeptForsaken(t, p, f, w, broughtConquered, broughtSacrificed, position+1);
				}
			});
			p.add(Save);
			JButton Delete = new JButton("Delete");
			Delete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					broughtSacrificed[position-broughtConquered.length] = false;
					DecideKeptForsaken(t, p, f, w, broughtConquered, broughtSacrificed, position+1);
				}
			});
			p.add(Delete);
		} else {
			WrapUpCampaign(t, p, f, w, broughtConquered, broughtSacrificed);
		}
		p.validate();
		p.repaint();
	}
	
	public static void ForsakenDowntime(JTextPane t, JPanel p, JFrame f, WorldState w, SaveData s, Forsaken[] exhausted) {
		if (w.loopComplete == false) {
			w.append(t, "\n\n" + w.getSeparator() + "\n\n");
		}
		for (int i = 0; i < w.getHarem().length; i++) {
			if (w.getHarem()[i].others != null) {
				for (int j = 0; j < w.getHarem()[i].others.length; j++) {
					Boolean present = false;
					for (int k = 0; k < w.getHarem().length; k++) {
						if (w.getHarem()[k].equals(w.getHarem()[i].others[j])) {
							present = true;
						}
					}
					if (present) {
						w.getHarem()[i].troublemaker[j] = w.getHarem()[i].troublemaker[j]*9/10;
					}
				}
			}
		}
		Forsaken[] included = w.getHarem();
		if (exhausted != null) {
			if (exhausted.length == 0) {
				exhausted = w.trainedForsaken;
			}
		}
		if (exhausted != null && exhausted.length > 0) {
			Forsaken[] newIncluded = new Forsaken[w.getHarem().length - exhausted.length];
			int numberFound = 0;
			for (int i = 0; i < w.getHarem().length; i++) {
				Boolean notExhausted = true;
				for (int j = 0; j < exhausted.length; j++) {
					if (w.getHarem()[i].equals(exhausted[j])) {
						notExhausted = false;
					}
				}
				if (notExhausted) {
					newIncluded[numberFound] = w.getHarem()[i];
					numberFound++;
				}
			}
			included = newIncluded;
		}
		Forsaken tantruming = null;
		int highest = 0;
		for (int i = 0; i < included.length; i++) {
			if (included[i].hostility*10 - included[i].motivation > highest) {
				highest = included[i].hostility*10 - included[i].motivation;
				tantruming = included[i];
			}
		}
		int[] damages = new int[3];
		if (tantruming != null) {
			damages = tantruming.motivationDamage();
			for (int i = 0; i < w.getHarem().length; i++) {
				if (w.getHarem()[i] != tantruming) {
					int offense = 100;
					int damage = damages[1];
					if (tantruming.opinion(w.getHarem()[i]) > 100) {
						offense = 70;
						damage = damages[0];
					} else if (tantruming.opinion(w.getHarem()[i]) < -100) {
						offense = 150;
						damage = damages[2];
					}
					w.getHarem()[i].motivation -= damage;
					if (w.getHarem()[i].motivation < 0) {
						w.getHarem()[i].motivation = 0;
					}
					if (w.getHarem()[i].others != null) {
						Boolean found = false;
						for (int j = 0; j < w.getHarem()[i].others.length; j++) {
							if (w.getHarem()[i].others[j].equals(tantruming)) {
								found = true;
								w.getHarem()[i].troublemaker[j] += offense;
							}
						}
						if (found == false) {
							Forsaken[] newOthers = new Forsaken[w.getHarem()[i].others.length+1];
							int[] newTroublemaker = new int[w.getHarem()[i].troublemaker.length+1];
							for (int j = 0; j < w.getHarem()[i].others.length; j++) {
								newOthers[j] = w.getHarem()[i].others[j];
								newTroublemaker[j] = w.getHarem()[i].troublemaker[j];
							}
							newOthers[newOthers.length-1] = tantruming;
							newTroublemaker[newTroublemaker.length-1] = offense;
							w.getHarem()[i].others = newOthers;
							w.getHarem()[i].troublemaker = newTroublemaker;
						}
					} else {
						w.getHarem()[i].others = new Forsaken[]{tantruming};
						w.getHarem()[i].troublemaker = new int[offense];
					}
				}
			}
			if (w.getHarem().length > 1) {
				if (tantruming.hostility < 20 && tantruming.defeatType != 5) {
					w.append(t, tantruming.mainName + " tries to organize a resistance against you, ");
					if (tantruming.confidence > 66) {
						w.append(t, "demanding that your other minions join " + tantruming.himHer() + ".  ");
					} else if (tantruming.confidence > 33) {
						w.append(t, "appealing to your other minions' sense of morality.  ");
					} else {
						w.append(t, "begging and pleading for your other minions to find their conscience.  ");
					}
					w.append(t, "Even for those who are inclined to listen to such arguments, the effort is more annoying than persuasive.");
				} else if (tantruming.hostility < 40) {
					w.append(t, tantruming.mainName + " lets out " + tantruming.hisHer() + " frustrations on your other minions, ");
					if (tantruming.confidence > 66) {
						w.append(t, "aggressively asserting " + tantruming.hisHer() + " dominance over anyone who can't or won't stand up to " + tantruming.himHer() + ".  ");
					} else if (tantruming.confidence > 33) {
						w.append(t, "spitting insults at anyone who so much as looks at " + tantruming.himHer() + " funny.  ");
					} else {
						w.append(t, "passive-aggressively insulting anyone who makes the mistake of spending too much time around " + tantruming.himHer() + ".  ");
					}
					w.append(t, "Even for everyone else, " + tantruming.hisHer() + " acting out is a constant annoyance.");
				} else if (tantruming.hostility < 61) {
					w.append(t, tantruming.mainName + " makes a scene in the middle of your base of operations, ");
					if (tantruming.confidence > 66) {
						w.append(t, "ranting, raving, and blaming everyone else for all " + tantruming.hisHer() + " problems.  ");
					} else if (tantruming.confidence > 33) {
						w.append(t, "shouting about how " + tantruming.heShe() + " hates being one of the Forsaken.  ");
					} else {
						w.append(t, "wailing in despair and whining about how unfair the world is.  ");
					}
					w.append(t, "The disruptive behavior is bad for your other minions' morale.");
				} else if (tantruming.hostility < 81) {
					w.append(t, tantruming.mainName + " gets violent with your other minions, ");
					if (tantruming.confidence > 66) {
						w.append(t, "challenging them to fight " + tantruming.himHer() + " head-on, and outright attacking those who try to flee.  ");
					} else if (tantruming.confidence > 33) {
						w.append(t, "picking fights and getting into several scuffles over the course of the night.  ");
					} else {
						w.append(t, "abruptingly attacking them from behind and then fleeing before they can retaliate.  ");
					}
					w.append(t, "The anger and resentment directed at " + tantruming.himHer() + " grows.");
				} else {
					w.append(t, tantruming.mainName + " goes on a murderous rampage, ");
					if (tantruming.confidence > 66) {
						w.append(t, "carving a wide and indiscriminate swath of destruction through your base of operations.  ");
					} else if (tantruming.confidence > 33) {
						w.append(t, "hunting down and attacking anyone " + tantruming.heShe() + " feels has wronged " + tantruming.himHer() + ".  ");
					} else {
						w.append(t, "slipping poison into the meals of countless Thralls and others " + tantruming.heShe() + " has a grudge against before " + tantruming.heShe() + "'s caught.  ");
					}
					w.append(t, "The resulting chaos affects your other minions as well.");
				}
				w.append(t, "  (+" + tantruming.staminaRegen()/10 + "." + tantruming.staminaRegen()%10 + "% Stamina, restores own Motivation at expense of everyone else)");
			} else {
				w.append(t, tantruming.mainName + " is too stressed to relax, but there aren't any other Forsaken around for " + tantruming.himHer() + " to release " + tantruming.hisHer() + " tension on. (+" + tantruming.staminaRegen() + " Stamina)");
			}
		}
		for (int i = 0; i < included.length; i++) {
			if (included[i] != tantruming) {
				if (tantruming != null || i > 0) {
					w.append(t, "\n\n");
				}
				int flavor = (int)(4*Math.random());
				if (included[i].demonicBirths < 0) {
					w.append(t, "Now that " + included[i].mainName + " is no longer one of the Chosen, the child in " + included[i].hisHer() + " belly is just a regular Demon, and " + included[i].heShe() + " quickly goes into labor.  The resulting abomination ");
					if (included[i].gender.equals(Forsaken.Gender.MALE)) {
						w.append(t, "forces its way out of " + included[i].mainName + "'s asshole");
					} else {
						w.append(t, "slides out of " + included[i].mainName + "'s distended vagina");
					}
					w.append(t, " while " + included[i].heShe() + " ");
					if (included[i].confidence > 66) {
						w.append(t, "grunts and strains");
					} else if (included[i].confidence > 33) {
						w.append(t, "stares down in horror");
					} else {
						w.append(t, "whimpers and whines");
					}
					w.append(t, ", then scuttles off immediately in search of its first victim.");
					included[i].demonicBirths = 1;
				} else if (flavor == 0) {
					if (included[i].demonicBirths > 0 && (int)(Math.random()*2) == 0) {
						included[i].demonicBirths++;
						w.append(t, "Due to " + included[i].hisHer() + " nighttime activities, " + included[i].mainName + " has been impregnated with another fast-growing Demonic child.  " + included[i].HeShe() + " gives birth to a small tentacled creature, ");
						if (included[i].innocence > 66) {
							w.append(t, "then happily waves goodbye as it slithers away.");
						} else if (included[i].innocence > 33) {
							w.append(t, "which leaves " + included[i].himHer() + " gasping for breath.");
						} else {
							w.append(t, "then mentally collects " + included[i].himHer() + "self and continues about " + included[i].hisHer() + " business.");
						}
					} else if (included[i].timesKilled > 2 && (int)(Math.random()*2) == 0) {
						included[i].timesKilled++;
						w.append(t, "A particularly bold Thrall ambushes " + included[i].mainName + " while " + included[i].heShe() + "'s alone and tries to rape " + included[i].himHer());
						if (included[i].morality > 66) {
							w.append(t, ", and " + included[i].mainName + " is happy afterwards to note that " + included[i].heShe() + " doesn't feel guilty in the slightest about killing him.");
						} else if (included[i].morality > 33) {
							w.append(t, ", but the Forsaken has no trouble overpowering and killing " + included[i].hisHer() + " attacker.");
						} else {
							w.append(t, ", and " + included[i].mainName + " enjoys giving him an especially slow and painful death.");
						}
					} else if (included[i].timesHadSex > 0 && ((int)(Math.random()*2) == 0 || included[i].peopleInjured == 0)) {
						included[i].timesHadSex += 3 + (int)(Math.random()*3);
						included[i].orgasmsGiven += 5 + (int)(Math.random()*5);
						if (included[i].timesOrgasmed > 0) {
							included[i].timesOrgasmed++;
						}
						w.append(t, included[i].mainName + " attends a wild party and ends up participating in an orgy, ");
						if (included[i].confidence > 66) {
							w.append(t, "gleefully dominating several partners at once.");
						} else if (included[i].confidence > 33) {
							w.append(t, "enjoying " + included[i].himHer() + "self greatly.");
						} else {
							w.append(t, "surrendering " + included[i].himHer() + "self to the lustful crowd.");
						}
					} else if (included[i].peopleInjured > 0) {
						included[i].peopleInjured++;
						w.append(t, "A particularly bold Thrall ambushes " + included[i].mainName + " while " + included[i].heShe() + "'s alone and tries to rape " + included[i].himHer());
						if (included[i].morality > 66) {
							w.append(t, ", but " + included[i].heShe() + " has no trouble fending him off.");
						} else if (included[i].morality > 33) {
							w.append(t, ", only to receive a sound beating.");
						} else {
							w.append(t, ", only to be left with some very painful injuries in " + included[i].hisHer() + " wake.");
						}
					} else {
						if (included[i].morality > 66) {
							w.append(t, included[i].mainName + " spends " + included[i].hisHer() + " time helping out your weaker minions, protecting them from danger and boosting their spirits.");
						} else if (included[i].morality > 33) {
							w.append(t, included[i].mainName + " hangs out with some of the friends " + included[i].heShe() + "'s made among your minions.");
						} else {
							w.append(t, included[i].mainName + " spends some time trying to bargain with you for better accommomdations, but to no avail.");
						}
					}
				} else if (flavor == 1) {
					if (included[i].hypnotized && (int)(Math.random()*2) == 0) {
						w.append(t, included[i].mainName + " sleeps through most of the day, having vivid dreams as you reach directly into " + included[i].hisHer());
						if (included[i].innocence > 66) {
							w.append(t, " simple mind and rearrange " + included[i].hisHer() + " instinctive impulses to your liking.");
						} else if (included[i].innocence > 33) {
							w.append(t, " subconscious in order to reinforce " + included[i].hisHer() + " hypnotic conditioning.");
						} else {
							w.append(t, " mind and carefully influence " + included[i].hisHer() + " thought process in order to prevent " + included[i].himHer() + " from finding a way to break your hypnotism.");
						}
					} else if (included[i].strongestOrgasm >= 1000 && (int)(Math.random()*2) == 0) {
						w.append(t, included[i].mainName + " spends the day enjoying the company of several tentacled Demons");
						if (included[i].dignity > 66) {
							w.append(t, ", but while " + included[i].heShe() + " tries to pretend that " + included[i].heShe() + "'s just inspecting " + included[i].hisHer() + " forces, the truth is that " + included[i].heShe() + "'s having them make " + included[i].himHer() + " cum over and over again.");
						} else if (included[i].dignity > 33) {
							w.append(t, ", allowing them to ravage " + included[i].himHer() + " with their many appendages.");
						} else {
							w.append(t, ", and soon " + included[i].heShe() + "'s screaming at the top of " + included[i].hisHer() + " lungs as " + included[i].heShe() + "'s gripped by a long, continuous climax.");
						}
						included[i].timesOrgasmed += 10 + (int)(Math.random()*10);
					} else if (included[i].strongestOrgasm >= 200 && ((int)(Math.random()*2) == 0 || included[i].orgasmsGiven < 1000)) {
						included[i].timesOrgasmed += 4 + (int)(Math.random()*4);
						if (included[i].confidence > 66) {
							w.append(t, included[i].mainName + " decides that " + included[i].heShe() + " needs a day to relax.  " + included[i].HeShe() + " spends much of it masturbating.");
						} else if (included[i].confidence > 33) {
							w.append(t, included[i].mainName + " tries to manage " + included[i].hisHer() + " lust by spending some time masturbating.  " + included[i].HeShe() + " ends up doing it for most of the day.");
						} else {
							w.append(t, "Overcome by the Demonic influence in the air, " + included[i].mainName + " hides in " + included[i].hisHer() + " room and starts to quietly masturbate, jumping in alarm whenever " + included[i].heShe() + " hears movement outside.");
						}
					} else if (included[i].orgasmsGiven >= 1000) {
						if (included[i].timesOrgasmed > 0) {
							included[i].timesOrgasmed += 2 + (int)(Math.random()*2);
						}
						if (included[i].innocence > 66) {
							w.append(t, included[i].mainName + " reads pornographic comics all day, marvelling at what " + included[i].heShe() + " sees.");
						} else if (included[i].innocence > 33) {
							w.append(t, included[i].mainName + " spends the day playing pornographic computer games.");
						} else {
							w.append(t, included[i].mainName + " spends the day studying and theorizing about methods to more efficiently force an unwilling target to orgasm.");
						}
					} else {
						if (included[i].innocence > 66) {
							w.append(t, included[i].mainName + " plays video games all day, forgetting for awhile where " + included[i].heShe() + " is.");
						} else if (included[i].innocence > 33) {
							w.append(t, included[i].mainName + " relaxes and spends " + included[i].hisHer() + " evening watching DVDs smuggled in from the outside world.");
						} else {
							w.append(t, included[i].mainName + " spends most of the day reading scholarly articles on psychography.");
						}
					}
				} else if (flavor == 2) {
					if (included[i].drained && (int)(Math.random()*2) == 0) {
						if (included[i].confidence > 66) {
							included[i].timesHarmedSelf++;
							w.append(t, included[i].mainName + " whips " + included[i].himHer() + "self until " + included[i].hisHer() + " back begins to show the marks, stubbornly enduring the pain to remind " + included[i].himHer() + "self not to oppose you.");
						} else if (included[i].confidence > 33) {
							w.append(t, included[i].mainName + " asks to be drained of what little residual psychic energy remains inside " + included[i].himHer() + ", submitting " + included[i].himHer() + "self to you completely.");
						} else {
							w.append(t, included[i].mainName + " begs you to punish " + included[i].himHer() + " for ever daring to oppose you, and after you use a spare Demonic body to lightly moleset " + included[i].himHer() + ", " + included[i].heShe() + " seems grateful and satisfied.");
						}
					} else if (included[i].timesHarmedSelf > 0 && (int)(Math.random()*2) == 0) {
						w.append(t, included[i].mainName + " isolates " + included[i].himHer() + "self and spends the day in silent contemplation of your greatness, ");
						if (included[i].innocence > 66) {
							w.append(t, "though it doesn't amount to much more than mentally repeating 'The Demon Lord is Really Strong' over and over again.");
						} else if (included[i].innocence > 33) {
							w.append(t, "reminding " + included[i].himHer() + "self that your will is absolute.");
						} else {
							w.append(t, "attempting to understand the true nature of a Demon Lord.");
						}
					} else if (included[i].timesTortured > 0 && ((int)(Math.random()*2) == 0 || included[i].meek == false)) {
						if (included[i].confidence > 66) {
							w.append(t, included[i].mainName + " humbles " + included[i].himHer() + "self by doing manual labor alongside your lesser minions in an attempt to show you " + included[i].hisHer() + " willingness to serve.");
						} else if (included[i].confidence > 33) {
							w.append(t, included[i].mainName + " keeps " + included[i].himHer() + "self busy by doing manual labor with the Thralls at your base of operations, hopeful that you'll notice " + included[i].hisHer() + " efforts.");
						} else {
							w.append(t, included[i].mainName + " presents " + included[i].himHer() + "self to the Thrall in charge of constructing your base of operations, offering to help out in a show of submission.");
						}
					} else if (included[i].meek) {
						if (included[i].confidence > 66) {
							w.append(t, included[i].mainName + " is suffering from flashbacks to " + included[i].hisHer() + " past abuses, but " + included[i].heShe() + " forces " + included[i].himHer() + "self to go outside and do " + included[i].hisHer() + " daily routine anyway, and " + included[i].heShe() + " feels satisfied about it once " + included[i].heShe() + " returns to " + included[i].hisHer() + " room for the night.");
						} else if (included[i].confidence > 33) {
							w.append(t, included[i].mainName + " feels worried about going outside, so " + included[i].heShe() + " just spends the day in " + included[i].hisHer() + " room.");
						} else {
							w.append(t, included[i].mainName + " locks " + included[i].himHer() + "self in " + included[i].hisHer() + " room, resting there until " + included[i].heShe() + " can overcome " + included[i].hisHer() + " old fears of being abused by the Thralls.");
						}
					} else {
						if (included[i].confidence > 66) {
							w.append(t, included[i].mainName + " has a good day, and " + included[i].heShe() + " goes to bed in high spirits.");
						} else if (included[i].confidence > 33) {
							w.append(t, included[i].mainName + " spends a leisurely day doing nothing in particular.");
						} else {
							w.append(t, included[i].mainName + " lifts weights in " + included[i].hisHer() + " room all day, desperate to become stronger.");
						}
					}
				} else {
					if (included[i].parasitized && (int)(Math.random()*2) == 0) {
						w.append(t, included[i].mainName + " spends the day with what's left of " + included[i].hisHer() + " fans, ");
						if (included[i].innocence > 66) {
							w.append(t, "not really even noticing that there are far fewer than before.");
						} else if (included[i].innocence > 33) {
							w.append(t, "and even though there clearly aren't as many as before, " + included[i].heShe() + " still enjoys " + included[i].himHer() + "self.");
						} else {
							w.append(t, "but " + included[i].heShe() + " can't help but dwell on the fact that most of them have moved on to newer Chosen and Forsaken.");
						}
						included[i].timesExposed += 10 + (int)(Math.random()*10);
						included[i].timesExposedSelf += 10 + (int)(Math.random()*10);
					} else if (included[i].timesExposedSelf > 100 && (int)(Math.random()*2) == 0) {
						w.append(t, included[i].mainName + " goes outside in the nude");
						if (included[i].dignity > 66) {
							w.append(t, ", greatly enjoying the extra attention it gets " + included[i].himHer() + ".");
						} else if (included[i].dignity > 33) {
							w.append(t, ", letting a few of your minions catch glimpses of " + included[i].himHer() + " before returning home.");
						} else {
							w.append(t, " as if it isn't any big deal.");
						}
						included[i].timesExposed += 1;
						included[i].timesExposedSelf += 1;
					} else if (included[i].timesExposed > 100000 && ((int)(Math.random()*2) == 0) || included[i].debased == false) {
						w.append(t, included[i].mainName + " goes outside in ");
						if (included[i].dignity > 66) {
							w.append(t, "a dress that's practically transparent, not quite showing the details of " + included[i].hisHer() + " private parts, but leaving very little to the imagination.");
						} else if (included[i].dignity > 33) {
							w.append(t, "a long shirt with nothing underneath, teasing your minions with the promise of catching a glimpse of " + included[i].hisHer() + " most intimate places.");
						} else {
							w.append(t, "a tiny miniskirt with no panties, and " + included[i].heShe() + " makes no effort whatsoever to avoid flashing people whenever " + included[i].heShe() + " stretches or bends over.");
						}
					} else if (included[i].debased) {
						w.append(t, "During " + included[i].hisHer() + " daily routine, " + included[i].mainName + " is confronted by a Thrall with a recording of " + included[i].himHer() + " being humiliated, ");
						if (included[i].dignity > 66) {
							w.append(t, "but " + included[i].mainName + " is pleasantly surprised to see that the Thrall is just an enthusiastic fan.");
						} else if (included[i].dignity > 33) {
							w.append(t, "but " + included[i].mainName + " doesn't let it get to " + included[i].himHer() + ".");
						} else {
							w.append(t, "but " + included[i].mainName + " is past the point of caring, and " + included[i].heShe() + " doesn't let it ruin " + included[i].hisHer() + " day.");
						}
					} else {
						w.append(t, included[i].mainName + " spends the day talking to a gathering of " + included[i].hisHer() + " fans, ");
						if (included[i].confidence > 66) {
							w.append(t, "happily regaling them with stories of " + included[i].hisHer() + " time as one of the Chosen.");
						} else if (included[i].confidence > 33) {
							w.append(t, "chatting about what life is like under the Demon Lord.");
						} else {
							w.append(t, "blushing and stammering when " + included[i].heShe() + " hears how much they still love " + included[i].himHer() + ".");
						}
					}
				}
				w.append(t, "  (+" + included[i].staminaRegen()/10 + "." + included[i].staminaRegen()%10 + "% Stamina");
				if (tantruming != null) {
					int lost = damages[1];
					if (tantruming.opinion(included[i]) > 100) {
						lost = damages[0];
					} else if (tantruming.opinion(included[i]) < -100) {
						lost = damages[2];
					}
					w.append(t, ", ");
					if (included[i].motivation/10 < included[i].hostility) {
						w.redAppend(t, "-" + lost/10 + "." + lost%10 + "% Motivation");
					} else {
						w.append(t, "-" + lost/10 + "." + lost%10 + "% Motivation");
					}
				}
				w.append(t, ")");
			}
		}
		if (exhausted != null && (tantruming != null || included.length == 0)) {
			for (int i = 0; i < exhausted.length; i++) {
				if (tantruming != null) {
					w.append(t, "\n\n" + exhausted[i].mainName + " finds it difficult to rest due to " + tantruming.mainName + "'s disturbance.  (");
					int lost = damages[1];
					if (tantruming.opinion(exhausted[i]) > 100) {
						lost = damages[0];
					} else if (tantruming.opinion(exhausted[i]) < -100) {
						lost = damages[2];
					}
					if (exhausted[i].motivation/10 < exhausted[i].hostility) {
						w.redAppend(t, "-" + lost/10 + "." + lost%10 + "% Motivation");
					} else {
						w.append(t, "-" + lost/10 + "." + lost%10 + "% Motivation");
					}
					w.append(t, ")");
				} else {
					if (i != 0) {
						w.append(t, "\n\n");
					}
					w.append(t, exhausted[i].mainName + " is tired due to the day's activities.");
				}
			}
		}
		for (int i = 0; i < included.length; i++) {
			included[i].stamina += included[i].staminaRegen();
			if (included[i].stamina > 1000) {
				included[i].stamina = 1000;
			}
		}
		if (tantruming != null && w.getHarem().length > 1) {
			tantruming.motivation = 1000;
		}
		WriteObject wobj = new WriteObject();
		wobj.serializeSaveData(s);
		w.trainedForsaken = null;
		if (w.active) {
			p.removeAll();
			JButton Continue = new JButton("Continue");
			Continue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					w.usedForsaken = null;
					Shop(t, p, f, w);
				}
			});
			p.add(Continue);
			p.validate();
			p.repaint();
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable () {
			@Override
			public void run() {
				new Project();
			}
		});
	}

}
