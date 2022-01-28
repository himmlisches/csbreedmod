import java.net.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.text.*;
import javax.swing.*;



/*
Breedmod Bugfixes
Line 5065 and removed "Hotkey" in continue constructor
commented out duplicate at 7626, 7787
commented out 5056, 6543, 7687
8090 changed Project.ContinueButton("Continue", "Hotkey:"); to ContinueButton("Continue", "Hotkey:")
5065, 6552, 7799, 7854 removed "Hotkey" from ContinueButtonTwo("Continue", "Hotkey:");

*/

public class Project extends JFrame
{
    static final long million = 1000000L;
    static final long billion = 1000000000L;
    static final long trillion = 1000000000000L;
    static final long quadrillion = 1000000000000000L;
    static final long quintillion = 1000000000000000000L;
    static final int scenesThisVersion = 48;
    static final int vignettesThisVersion = 15;
    public static JFrame window;
    public static Container nestedcp;
    public static Container portraits;
    public static JTextPane textPane;
    public static JScrollPane scrollPane;
    public static JScrollPane portraitPane;
    public static Emotion[] displayedEmotions;
    public static String[] displayedNames;
    public static Chosen.Species[] displayedType;
    public static Boolean[] displayedCivilians;
    public static Boolean[] displayedFallen;
    public static Forsaken.Gender[] displayedGender;
    
    static {
        Project.window = new JFrame("Project");
        Project.nestedcp = new Container();
        Project.portraits = new Container();
        Project.textPane = new JTextPane();
        Project.scrollPane = new JScrollPane(Project.textPane);
        Project.portraitPane = new JScrollPane(Project.portraits);
        Project.displayedEmotions = new Emotion[5];
        Project.displayedNames = new String[5];
        Project.displayedType = new Chosen.Species[5];
        Project.displayedCivilians = new Boolean[] { false, false, false, false, false };
        Project.displayedFallen = new Boolean[] { false, false, false, false, false };
        Project.displayedGender = new Forsaken.Gender[] { Forsaken.Gender.FEMALE, Forsaken.Gender.FEMALE, Forsaken.Gender.FEMALE, Forsaken.Gender.FEMALE, Forsaken.Gender.FEMALE };
    }
    
    public Project() {
        final Container cp = Project.window.getContentPane();
        Project.window.setLayout(new BoxLayout(cp, 1));
        Project.nestedcp.setLayout(new GridBagLayout());
        cp.add(Project.nestedcp);
        Project.portraits.setLayout(new BoxLayout(Project.portraits, 1));
        Project.textPane.setEditable(false);
        Project.scrollPane.setVerticalScrollBarPolicy(22);
        final GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = 1;
        Project.nestedcp.add(Project.scrollPane, c);
        final JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setMaximumSize(new Dimension(5000, 40));
        cp.add(controlPanel);
        Project.window.setDefaultCloseOperation(3);
        Project.window.setTitle("Corrupted Saviors");
        Project.window.setSize(new Dimension(1300, 800));
        Project.window.setVisible(true);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        final WorldState ThisState = new WorldState();
        ThisState.toggleColors(Project.textPane);
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
        final File saveLocation = new File(String.valueOf(path) + File.separator + "saves.sav");
        SaveData saves = null;
        if (saveLocation.exists()) {
            final ReadObject robj = new ReadObject();
            saves = robj.deserializeSaveData(String.valueOf(path) + File.separator + "saves.sav");
        }
        else {
            saves = new SaveData();
        }
        final SaveData saveFile = saves;
        if (saveFile.getSaves().length > 0) {
            ThisState.copySettings(Project.textPane, saveFile.getSaves()[0]);
            ThisState.copyToggles(saveFile.getSaves()[0]);
            ThisState.setGenders(saveFile.getSaves()[0].getGenderBalance());
            if (!ThisState.hardMode) {
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
        if (saves.harem != null && saves.harem.length > 0) {
            if (saves.harem[0].hateExp == 0L) {
                for (int j = 0; j < saves.harem.length; ++j) {
                    saves.harem[j].hateExp = 20000L;
                    saves.harem[j].pleaExp = 20000L;
                    saves.harem[j].injuExp = 20000L;
                    saves.harem[j].expoExp = 20000L;
                    saves.harem[j].chooseCombatStyle();
                    saves.harem[j].motivation = 1000;
                    saves.harem[j].stamina = 1000;
                    if (saves.harem[j].innocence > 66) {
                        saves.harem[j].textColor = new Color(255, 0, 150);
                        saves.harem[j].darkColor = new Color(255, 0, 150);
                    }
                    else if (saves.harem[j].innocence > 33) {
                        saves.harem[j].textColor = new Color(120, 50, 180);
                        saves.harem[j].darkColor = new Color(150, 100, 200);
                    }
                    else {
                        saves.harem[j].textColor = new Color(200, 100, 100);
                        saves.harem[j].darkColor = new Color(255, 130, 220);
                    }
                    saves.harem[j].others = null;
                }
                final WriteObject wobj = new WriteObject();
                wobj.serializeSaveData(saves);
            }
            for (int j = 0; j < saves.harem.length; ++j) {
                if (saves.harem[j].forsakenID == 0) {
                    saves.harem[j].forsakenID = saves.assignID();
                    final WriteObject wobj2 = new WriteObject();
                    wobj2.serializeSaveData(saves);
                }
                if (saves.harem[j].forsakenRelations == null && saves.harem[j].chosenRelations == null) {
                    saves.harem[j].otherChosen = new Chosen[] { saves.harem[j].firstFormerPartner, saves.harem[j].secondFormerPartner };
                    saves.harem[j].chosenRelations = new Forsaken.Relationship[] { Forsaken.Relationship.PARTNER, Forsaken.Relationship.PARTNER };
                    if (saves.harem[j].others != null) {
                        saves.harem[j].forsakenRelations = new Forsaken.Relationship[saves.harem[j].others.length];
                        for (int k = 0; k < saves.harem[j].others.length; ++k) {
                            if (saves.harem[j].others[k].equals(saves.harem[j].firstPartner) || saves.harem[j].others[k].equals(saves.harem[j].secondPartner)) {
                                saves.harem[j].forsakenRelations[k] = Forsaken.Relationship.PARTNER;
                            }
                        }
                    }
                    else {
                        saves.harem[j].others = new Forsaken[0];
                        saves.harem[j].forsakenRelations = new Forsaken.Relationship[0];
                        saves.harem[j].troublemaker = new int[0];
                    }
                    final Forsaken[] checkedForsaken = saves.harem;
                    for (int l = 0; l < checkedForsaken.length; ++l) {
                        if (checkedForsaken[l].equals(saves.harem[j].firstPartner) || checkedForsaken[l].equals(saves.harem[j].secondPartner)) {
                            Boolean alreadyThere = false;
                            for (int m = 0; m < saves.harem[j].others.length; ++m) {
                                if (saves.harem[j].others[m].equals(checkedForsaken[l])) {
                                    alreadyThere = true;
                                }
                            }
                            if (!alreadyThere) {
                                final Forsaken[] newOthers = new Forsaken[saves.harem[j].others.length + 1];
                                final Forsaken.Relationship[] newRelationships = new Forsaken.Relationship[saves.harem[j].forsakenRelations.length + 1];
                                final int[] newTroubles = new int[saves.harem[j].troublemaker.length + 1];
                                for (int k2 = 0; k2 < saves.harem[j].others.length; ++k2) {
                                    newOthers[k2] = saves.harem[j].others[k2];
                                    newRelationships[k2] = saves.harem[j].forsakenRelations[k2];
                                    newTroubles[k2] = saves.harem[j].troublemaker[k2];
                                }
                                newOthers[saves.harem[j].others.length] = checkedForsaken[l];
                                newRelationships[saves.harem[j].forsakenRelations.length] = Forsaken.Relationship.PARTNER;
                                saves.harem[j].others = newOthers;
                                saves.harem[j].forsakenRelations = newRelationships;
                                saves.harem[j].troublemaker = newTroubles;
                            }
                        }
                    }
                }
                saves.harem[j].save = saves;
            }
        }
        if (saves.customRoster == null) {
            saves.customRoster = new Chosen[0];
        }
        if (saves.sceneText == null) {
            saves.organizeScenes(48);
        }
        else if (saves.sceneText.length < 48) {
            saves.organizeScenes(48);
        }
        if (saves.harem == null) {
            saves.harem = new Forsaken[0];
            final WriteObject wobj = new WriteObject();
            wobj.serializeSaveData(saves);
        }
        saves.currentText = new String[0];
        saves.currentColor = new Color[0];
        saves.currentUnderline = new Boolean[0];
        ThisState.save = saves;
        IntroOne(Project.textPane, controlPanel, Project.window, ThisState);
    }
    
    public static String getFilePath() {
        String result = "";
        result = Project.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String fileName = "";
        for (int i = result.length() - 1; i >= 0; --i) {
            if (result.charAt(i) != '/') {
                fileName = String.valueOf(result.charAt(i)) + fileName;
            }
            else {
                i = -1;
            }
        }
        result = result.substring(0, result.length() - fileName.length() - 1);
        try {
            result = URLDecoder.decode(result, "UTF-8");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        result = result.replaceAll("file:/", "");
        result = result.replaceAll(String.valueOf(File.separator) + "u0020", String.valueOf(File.separator) + " ");
        return result;
    }
    
    public static void clearPortraits() {
        Project.nestedcp.remove(Project.portraitPane);
        Project.portraits.removeAll();
        Project.displayedNames = new String[5];
        Project.nestedcp.validate();
        Project.nestedcp.repaint();
    }
    
    public static void changePortrait(final Forsaken.Gender gender, final Chosen.Species spec, final Boolean civilian, final Boolean fallen, final WorldState w, final String[] names, final int number, final Emotion first, final Emotion backup) {
        if (w.portraits) {
            int displayed = 5;
            if (names[3] == null) {
                displayed = 3;
                Project.displayedEmotions[3] = null;
                Project.displayedNames[3] = null;
                Project.displayedEmotions[4] = null;
                Project.displayedNames[4] = null;
            }
            else if (names[4] == null) {
                displayed = 4;
                Project.displayedEmotions[4] = null;
                Project.displayedNames[4] = null;
            }
            Project.displayedType[number] = spec;
            Project.displayedCivilians[number] = civilian;
            Project.displayedFallen[number] = fallen;
            Project.displayedGender[number] = gender;
            Project.nestedcp.remove(Project.portraitPane);
            Project.portraits.removeAll();
            Project.portraitPane.setHorizontalScrollBarPolicy(31);
            Project.portraitPane.setVerticalScrollBarPolicy(21);
            final int imageSize = Project.scrollPane.getHeight() / displayed;
            BufferedImage image = null;
            for (int i = 0; i < displayed; ++i) {
                Project.displayedNames[i] = names[i];
                if (i == number) {
                    if (Project.displayedEmotions[i] == first) {
                        Project.displayedEmotions[i] = backup;
                    }
                    else {
                        Project.displayedEmotions[i] = first;
                    }
                }
                String path = String.valueOf(getFilePath()) + File.separator + "portraits" + File.separator + "empty";
                if (names[i] != null) {
                    path = String.valueOf(getFilePath()) + File.separator + "portraits" + File.separator + names[i] + File.separator;
                }
                final String[] folders = { "", "", "", "" };
                if (Project.displayedGender[i] == Forsaken.Gender.MALE) {
                    folders[0] = "male" + File.separator;
                }
                if (Project.displayedType[i] == Chosen.Species.SUPERIOR) {
                    folders[1] = "superior" + File.separator;
                }
                if (Project.displayedCivilians[i]) {
                    folders[2] = "civilian" + File.separator;
                }
                if (Project.displayedFallen[i]) {
                    folders[3] = "forsaken" + File.separator;
                }
                String type = "neutral";
                if (Project.displayedEmotions[i] == Emotion.ANGER) {
                    type = "anger";
                }
                else if (Project.displayedEmotions[i] == Emotion.FEAR) {
                    type = "fear";
                }
                else if (Project.displayedEmotions[i] == Emotion.FOCUS) {
                    type = "focus";
                }
                else if (Project.displayedEmotions[i] == Emotion.JOY) {
                    type = "joy";
                }
                else if (Project.displayedEmotions[i] == Emotion.LEWD) {
                    type = "lewd";
                }
                else if (Project.displayedEmotions[i] == Emotion.NEUTRAL) {
                    type = "neutral";
                }
                else if (Project.displayedEmotions[i] == Emotion.SHAME) {
                    type = "sadness";
                }
                else if (Project.displayedEmotions[i] == Emotion.STRUGGLE) {
                    type = "struggle";
                }
                else if (Project.displayedEmotions[i] == Emotion.SWOON) {
                    type = "swoon";
                }
                for (int j = 0; j < 16 && image == null && Project.displayedNames[i] != null; ++j) {
                    String nav = "";
                    if (folders[0].length() > 0 && j < 8) {
                        nav = String.valueOf(nav) + folders[0];
                    }
                    if (folders[1].length() > 0 && j % 8 < 4) {
                        nav = String.valueOf(nav) + folders[1];
                    }
                    if (folders[2].length() > 0 && j % 4 < 2) {
                        nav = String.valueOf(nav) + folders[2];
                    }
                    if (folders[3].length() > 0 && j % 2 == 0) {
                        nav = String.valueOf(nav) + folders[3];
                    }
                    try {
                        image = ImageIO.read(new File(String.valueOf(path) + nav + type + ".png"));
                    }
                    catch (IOException ie) {
                        try {
                            image = ImageIO.read(new File(String.valueOf(path) + nav + type + ".jpg"));
                        }
                        catch (IOException ig) {
                            try {
                                image = ImageIO.read(new File(String.valueOf(path) + nav + type + ".gif"));
                            }
                            catch (IOException ih) {
                                try {
                                    image = ImageIO.read(new File(String.valueOf(path) + nav + type + ".jpeg"));
                                }
                                catch (IOException ex) {}
                            }
                        }
                    }
                }
                if (image == null) {
                    try {
                        image = ImageIO.read(new File(String.valueOf(getFilePath()) + File.separator + "portraits" + File.separator + "empty.png"));
                    }
                    catch (IOException ie2) {
                        w.portraits = false;
                        clearPortraits();
                    }
                }
                if (image != null) {
                    final Image resized = image.getScaledInstance(imageSize, imageSize, 4);
                    final JLabel picLabel = new JLabel(new ImageIcon(resized));
                    Project.portraits.add(picLabel);
                }
                image = null;
            }
            final GridBagConstraints c = new GridBagConstraints();
            c.weighty = 0.0;
            c.weightx = 0.0;
            c.fill = 3;
            c.ipadx = imageSize;
            Project.nestedcp.add(Project.portraitPane, c);
            Project.nestedcp.validate();
            Project.nestedcp.repaint();
        }
    }
    
    public static void IntroOne(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        w.setGenders(w.getGenderBalance());
        p.getInputMap().clear();
        p.getActionMap().clear();
        clearPortraits();
        if (w.portraits) {
            BufferedImage image = null;
            try {
                image = ImageIO.read(new File(String.valueOf(getFilePath()) + File.separator + "portraits" + File.separator + "empty.png"));
            }
            catch (IOException ie) {
                w.portraits = false;
                clearPortraits();
            }
        }
        if (!t.getBackground().equals(w.BACKGROUND)) {
            w.toggleColors(t);
        }
        w.append(t, "Corrupted Saviors, Release 25d: \"Reunion\"\n\nThis game contains content of an adult nature and should not be played by the underaged or by those unable to distinguish fantasy from reality.\n\n" + w.getSeparator() + "\n\nJapan, mid-21st century.  The psychic energies of humanity have finally begun to coalesce into physical form.  The resulting beings are known as Demons.  Born from the base desires suppressed deep within the human mind, these creatures spread across the planet, leaving chaos and depravity in their wake.\n\nBut Demons do not represent the entirety of the human condition.  The hopes and determination of humanity have also risen up, gathering in the bodies of a few Chosen warriors in order to grant them the power to fight the Demons.  Although each of them was once an ordinary person, their new abilities place them at the center of the struggle for the soul of humanity.\n\nYou are a Demon Lord, the highest form of Demon, with your own mind and will, focused on the corruption of all that is good in the world.  The Chosen are the keystone of humanity's resistance to your goal, but to simply kill them would be meaningless.  Instead, shatter their notions of right and wrong, showing them the true darkness that hides within!");
        if (w.getCast()[0] == null) {
            final Chosen newChosen = new Chosen();
            newChosen.setNumber(0);
            w.initialize();
            newChosen.generate(w);
            w.addChosen(newChosen);
        }
        else if (!w.getCast()[0].getGender().equals(w.getGenders()[0])) {
            w.getCast()[0] = null;
            final Chosen newChosen = new Chosen();
            newChosen.setNumber(0);
            w.initialize();
            newChosen.generate(w);
            w.addChosen(newChosen);
        }
        p.removeAll();
        final JButton NewGame = new JButton("Single Play");
        NewGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.getEarlyCheat()) {
                    Project.Shop(t, p, f, w);
                }
                else {
                    w.active = true;
                    Project.IntroTwo(t, p, f, w);
                }
            }
        });
        p.add(NewGame);
        final JButton Campaign = new JButton("Campaign");
        Campaign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Boolean[] enabled = new Boolean[w.save.customRoster.length];
                for (int i = 0; i < enabled.length; ++i) {
                    enabled[i] = true;
                }
                Project.CampaignMenu(t, p, f, w, enabled);
            }
        });
        p.add(Campaign);
        final JButton LoadGame = new JButton("Load Game");
        LoadGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.Data(t, p, f, w, "load", 0, false);
            }
        });
        p.add(LoadGame);
        final JButton Import = new JButton("Import");
        Import.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.Data(t, p, f, w, "import", 0, false);
            }
        });
        p.add(Import);
        final JButton Tutorial = new JButton("Tutorial");
        Tutorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final WorldState x = new WorldState();
                x.copySettings(t, w);
                x.copyToggles(w);
                x.tutorialInit();
                x.save = w.save;
                Project.BeginBattle(t, p, f, x, x.getCast()[0]);
                x.grayAppend(t, "\n\n(Welcome to the tutorial!  This feature is intended to demonstrate some useful techniques for corrupting the Chosen.  It uses a mid-game save file with several upgrades already purchased.  When playing from the start, it makes more sense to use the first several days experimenting to find the strengths and weaknesses of the Chosen and accumulating Evil Energy before aiming to break a vulnerability.  Read the guide.txt file included with the game for a more basic overview of the mechanics.\n\nFor now, let's start by using Examine to figure out how best to deal with Miracle.)");
            }
        });
        p.add(Tutorial);
        final JButton Options = new JButton("Options");
        Options.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.OptionsMenu(t, p, f, w, null);
            }
        });
        p.add(Options);
        final JButton Customize = new JButton("Customize");
        Customize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                final WorldState x = new WorldState();
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
            final JButton Forsaken = new JButton("Forsaken");
            Forsaken.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.ForsakenMenu(t, p, f, w, fileUsed, 0);
                }
            });
            p.add(Forsaken);
        }
        if (w.save.sceneText == null) {
            w.save.organizeScenes(48);
        }
        for (int i = 0; i < w.save.sceneText.length; ++i) {
            if (w.save.sceneText[i].length > 0) {
                i = w.save.sceneText.length;
                final JButton Scenes = new JButton("Scenes");
                Scenes.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.SceneCompletion(t, p, f, w, w.save);
                        Project.SceneViewer(t, p, f, w, w.save, 0);
                    }
                });
                p.add(Scenes);
            }
        }
        final JButton About = new JButton("About");
        About.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\nCopyright 2019-2021 by CSdev. Corrupted Saviors is licensed under the Creative Commons Attribution-ShareAlike 4.0 International License. To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/4.0/.\n\nDefault portrait set created by CSdev with the assistance of Artbreeder and dedicated to the public domain (CC0).  For more information, see https://creativecommons.org/publicdomain/zero/1.0/.\n\nIf you like this game, please share it and discuss it so that it can be further enjoyed and improved!  There is a good chance that the developer reads whatever forum you found it on.  Direct feedback can also be sent to corruptedsaviors@gmail.com\n\nNew versions are first posted to corruptedsaviors.blogspot.com\nThe developer's tip jar can be found at subscribestar.adult/csdev");
            }
        });
        p.add(About);
        p.validate();
        p.repaint();
    }
    
    public static void CampaignMenu(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Boolean[] enabled) {
        p.removeAll();
        clearPortraits();
        final JButton Begin = new JButton("Begin");
        Begin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final WorldState x = new WorldState();
                for (int i = 0; i < enabled.length; ++i) {
                    if (enabled[i]) {
                        final Chosen[] newCustom = new Chosen[x.campaignCustom.length + 1];
                        for (int j = 0; j < x.campaignCustom.length; ++j) {
                            newCustom[j] = x.campaignCustom[j];
                        }
                        newCustom[x.campaignCustom.length] = w.save.customRoster[i];
                        x.campaignCustom = newCustom;
                    }
                }
                x.copySettings(t, w);
                x.copyToggles(w);
                x.save = w.save;
                x.setGenders(x.getGenderBalance());
                x.active = true;
                x.campaign = true;
                x.cityName = x.getCityName(0);
                x.campaignRand = new Random();
                x.hardMode = false;
                x.clampStart = 11;
                x.clampPercent = 100;
                x.eventOffset = 0;
                x.downtimeMultiplier = 100;
                final Chosen newChosen = new Chosen();
                newChosen.setNumber(0);
                x.initialize();
                newChosen.generate(x);
                x.addChosen(newChosen);
                if (w.getEarlyCheat()) {
                    Project.Shop(t, p, f, x);
                }
                else {
                    w.active = true;
                    Project.IntroTwo(t, p, f, x);
                }
            }
        });
        p.add(Begin);
        w.append(t, "\n\n" + w.getSeparator());
        if (w.save.customRoster == null) {
            w.save.customRoster = new Chosen[0];
        }
        if (w.save.customRoster.length == 0) {
            w.append(t, "\n\nBefore you start, you can generate custom Chosen who will eventually appear to face you.  You can also import a full team of Chosen from a save, in which case they'll face you in their Day 1 (i.e. uncorrupted) state.");
        }
        else {
            ReportCustomInclusion(t, w, enabled);
            final JButton Toggle = new JButton("Toggle Inclusion");
            Toggle.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.CampaignCustomToggle(t, p, f, w, enabled, 0, false);
                }
            });
            p.add(Toggle);
            final JButton DeleteChosen = new JButton("Delete Chosen");
            DeleteChosen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.CampaignCustomToggle(t, p, f, w, enabled, 0, true);
                }
            });
            p.add(DeleteChosen);
        }
        if (w.earlyCheat) {
            w.append(t, "\n\nEasy Mode is turned on.  It will be possible to use cheats.  Aside from the presence of Elite Chosen, there will be no increases in the difficulty of later loops.");
        }
        final JButton LoadTeam = new JButton("Load Team");
        LoadTeam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.Data(t, p, f, w, "teamload", 0, false);
            }
        });
        p.add(LoadTeam);
        final JButton SoloGen = new JButton("Single Custom");
        SoloGen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Chosen c = new Chosen();
                final int[] array = new int[6];
                array[0] = (int)(Math.random() * 26.0);
                array[1] = (int)(Math.random() * 26.0);
                final int[] nameSeed = array;
                final WorldState dummy = new WorldState();
                for (int i = 0; i < 3; ++i) {
                    dummy.genders[i] = w.genders[i];
                }
                final String[] names = c.genName(dummy, nameSeed);
                c.givenName = names[0];
                c.familyName = names[1];
                c.gender = dummy.genders[0];
                c.originalGender = c.gender;
                c.textSize = w.getCast()[0].textSize;
                Project.SingleCustom(t, p, f, w, c, null);
            }
        });
        p.add(SoloGen);
        final JButton ExportIncluded = new JButton("Export Included");
        ExportIncluded.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final WriteObject wobj = new WriteObject();
                Chosen[] exportedRoster = new Chosen[0];
                for (int i = 0; i < enabled.length; ++i) {
                    if (enabled[i]) {
                        final Chosen[] newRoster = new Chosen[exportedRoster.length + 1];
                        for (int j = 0; j < exportedRoster.length; ++j) {
                            newRoster[j] = exportedRoster[j];
                        }
                        newRoster[exportedRoster.length] = w.save.customRoster[i];
                        exportedRoster = newRoster;
                    }
                }
                String newRosterName = JOptionPane.showInputDialog("What would you like to name the exported roster?");
                Boolean blankName = false;
                if (newRosterName == null) {
                    blankName = true;
                }
                else if (newRosterName.length() == 0) {
                    blankName = true;
                }
                if (blankName) {
                    newRosterName = String.valueOf(exportedRoster[0].mainName) + "'s Roster";
                }
                exportedRoster[0].rosterName = newRosterName;
                String editedName = "";
                for (int k = 0; k < newRosterName.length(); ++k) {
                    if (newRosterName.charAt(k) == '/' || newRosterName.charAt(k) == ':') {
                        editedName = String.valueOf(editedName) + "-";
                    }
                    else {
                        editedName = String.valueOf(editedName) + newRosterName.charAt(k);
                    }
                }
                wobj.exportRoster(exportedRoster, editedName);
                w.append(t, "\n\n" + w.getSeparator() + "\n\nNew roster saved to '" + editedName + ".ros'.");
            }
        });
        if (enabled.length > 0) {
            p.add(ExportIncluded);
        }
        final JButton ImportRoster = new JButton("Import Roster");
        ImportRoster.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.ImportMenu(t, p, f, w, enabled, 0);
            }
        });
        p.add(ImportRoster);
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.IntroOne(t, p, f, w);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
        final WriteObject wobj = new WriteObject();
        wobj.serializeSaveData(w.save);
    }
    
    public static void ImportMenu(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Boolean[] enabled, final int page) {
        p.removeAll();
        int i = page * 4;
        int j = 0;
        Chosen[][] foundRosters = new Chosen[0][0];
        final ReadObject robj = new ReadObject();
        foundRosters = robj.importRoster();
        if (foundRosters.length == 0) {
            w.append(t, "\n\n" + w.getSeparator() + "\n\nNo importable rosters found in directory.");
        }
        else {
            w.append(t, "\n\n" + w.getSeparator() + "\n\nFound the following importable rosters in directory.  Which would you like to import?");
            if (page > 0) {
                final JButton LastPage = new JButton("<");
                LastPage.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.ImportMenu(t, p, f, w, enabled, page - 1);
                    }
                });
                p.add(LastPage);
            }
            while (i < foundRosters.length && j < 4) {
                w.append(t, "\n\nFile " + (i + 1) + ": " + foundRosters[i][0].rosterName);
                final int rosterSelected = i;
                final Chosen[][] rosterList = foundRosters;
                final JButton Access = new JButton(new StringBuilder().append(i + 1).toString());
                Access.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final WriteObject wobj = new WriteObject();
                        final Chosen[] newRoster = new Chosen[w.save.customRoster.length + rosterList[rosterSelected].length];
                        final Boolean[] newEnabled = new Boolean[w.save.customRoster.length + rosterList[rosterSelected].length];
                        for (int k = 0; k < newRoster.length; ++k) {
                            if (k < w.save.customRoster.length) {
                                newRoster[k] = w.save.customRoster[k];
                                newEnabled[k] = enabled[k];
                            }
                            else {
                                newRoster[k] = rosterList[rosterSelected][k - w.save.customRoster.length];
                                newRoster[k].globalID = w.save.assignChosenID();
                                newEnabled[k] = true;
                            }
                        }
                        w.save.customRoster = newRoster;
                        wobj.serializeSaveData(w.save);
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n" + rosterList[rosterSelected].length + " new Chosen added to custom roster.");
                        Project.CampaignMenu(t, p, f, w, newEnabled);
                    }
                });
                p.add(Access);
                ++i;
                ++j;
            }
            if (page * 4 + 4 < foundRosters.length) {
                final JButton NextPage = new JButton(">");
                NextPage.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.ImportMenu(t, p, f, w, enabled, page + 1);
                    }
                });
                p.add(NextPage);
            }
        }
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.CampaignMenu(t, p, f, w, enabled);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void SingleCustom(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c, final int[] answers) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\n" + c.customSummary() + "\n\nSet the Chosen's basic characteristics.");
        final JButton FamilyName = new JButton("Family Name");
        FamilyName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String input = JOptionPane.showInputDialog("What is " + c.hisHer() + " family surname?  Leave blank to have " + c.himHer() + " lack a surname.");
                if (input == null) {
                    c.familyName = "";
                }
                else {
                    c.familyName = input;
                }
                Project.SingleCustom(t, p, f, w, c, answers);
            }
        });
        p.add(FamilyName);
        final JButton GivenName = new JButton("Given Name");
        GivenName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String input = JOptionPane.showInputDialog("What name was " + c.heShe() + " given at birth?  Leave blank to keep the current choice (" + c.givenName + ").");
                if (input != null && input.length() > 0) {
                    c.givenName = input;
                    Project.SingleCustom(t, p, f, w, c, answers);
                }
            }
        });
        p.add(GivenName);
        final JButton NameOrder = new JButton("Name Order");
        NameOrder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.filthyGaijin = !c.filthyGaijin;
                Project.SingleCustom(t, p, f, w, c, answers);
            }
        });
        p.add(NameOrder);
        final JButton Sex = new JButton("Sex");
        Sex.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (c.gender.equals("female")) {
                    c.gender = "male";
                }
                else if (c.gender.equals("male")) {
                    c.gender = "futanari";
                }
                else {
                    c.gender = "female";
                }
                Project.SingleCustom(t, p, f, w, c, answers);
            }
        });
        p.add(Sex);
        final JButton Species = new JButton("Species");
        Species.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (c.type == null) {
                    c.type = Chosen.Species.SUPERIOR;
                }
                else {
                    c.type = null;
                }
                Project.SingleCustom(t, p, f, w, c, answers);
            }
        });
        p.add(Species);
        final JButton Continue = new JButton("Continue (Quiz)");
        Continue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (answers == null) {
                    Project.CustomQuiz(t, p, f, w, c, 0, new int[32]);
                }
                else {
                    Project.CustomQuiz(t, p, f, w, c, 0, answers);
                }
            }
        });
        p.add(Continue);
        final JButton DirectInput = new JButton("Continue (Direct Input)");
        DirectInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String input = "";
                input = JOptionPane.showInputDialog("Enter " + c.givenName + "'s Morality (0 to 100).");
                int upperLimit = 100;
                int lowerLimit = 0;
                int value = 0;
                try {
                    value = Integer.parseInt(input);
                }
                catch (Exception ex) {
                    w.append(t, "\n\n" + w.getSeparator() + "\n\nInvalid value.");
                    Project.SingleCustom(t, p, f, w, c, answers);
                    ex.printStackTrace();
                }
                if (value >= 0 && value <= 100) {
                    c.morality = Integer.parseInt(input);
                    input = JOptionPane.showInputDialog("Enter " + c.givenName + "'s Innocence (0 to 100).");
                    try {
                        value = Integer.parseInt(input);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        w.append(t, "\n\n" + w.getSeparator() + "\n\nInvalid value.");
                        Project.SingleCustom(t, p, f, w, c, answers);
                    }
                    if (value >= 0 && value <= 100) {
                        c.innocence = value;
                        if (c.morality > 66 && c.innocence > 66) {
                            upperLimit = 66;
                        }
                        else if (c.morality < 34 && c.innocence < 34) {
                            lowerLimit = 34;
                        }
                        else if (c.morality > 33 && c.innocence > 33 && c.morality < 67 && c.innocence < 67) {
                            upperLimit = 33;
                            lowerLimit = 67;
                        }
                        if (upperLimit > lowerLimit) {
                            input = JOptionPane.showInputDialog("Enter " + c.givenName + "'s Confidence (" + lowerLimit + " to " + upperLimit + ").");
                        }
                        else {
                            input = JOptionPane.showInputDialog("Enter " + c.givenName + "'s Confidence (0 to 33 or 67 to 100).");
                        }
                        try {
                            value = Integer.parseInt(input);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            w.append(t, "\n\n" + w.getSeparator() + "\n\nInvalid value.");
                            Project.SingleCustom(t, p, f, w, c, answers);
                        }
                        if (value >= 0 && value <= 100 && ((value >= lowerLimit && value <= upperLimit) || (upperLimit < lowerLimit && (value > 66 || value < 34)))) {
                            c.confidence = value;
                            Boolean highFound = false;
                            Boolean midFound = false;
                            Boolean lowFound = false;
                            if (c.morality > 66) {
                                highFound = true;
                            }
                            else if (c.morality > 34) {
                                midFound = true;
                            }
                            else {
                                lowFound = true;
                            }
                            if (c.innocence > 66) {
                                highFound = true;
                            }
                            else if (c.innocence > 33) {
                                midFound = true;
                            }
                            else {
                                lowFound = true;
                            }
                            if (c.confidence > 66) {
                                highFound = true;
                            }
                            else if (c.confidence > 33) {
                                midFound = true;
                            }
                            else {
                                lowFound = true;
                            }
                            if (!highFound) {
                                upperLimit = 100;
                                lowerLimit = 67;
                            }
                            else if (!midFound) {
                                upperLimit = 66;
                                lowerLimit = 34;
                            }
                            else if (!lowFound) {
                                upperLimit = 33;
                                lowerLimit = 0;
                            }
                            else {
                                upperLimit = 100;
                                lowerLimit = 0;
                            }
                            input = JOptionPane.showInputDialog("Enter " + c.givenName + "'s Dignity (" + lowerLimit + " to " + upperLimit + ").");
                            try {
                                value = Integer.parseInt(input);
                            }
                            catch (Exception ex2) {
                                w.append(t, "\n\n" + w.getSeparator() + "\n\nInvalid value.");
                                Project.SingleCustom(t, p, f, w, c, answers);
                                ex2.printStackTrace();
                            }
                            if (value >= lowerLimit && value <= upperLimit) {
                                c.dignity = value;
                                if (!w.determineVVirg(c.morality, c.innocence, c.confidence, c.dignity)) {
                                    c.ruthless = true;
                                    c.vVirg = false;
                                    c.vStart = false;
                                    c.vTaker = 0;
                                }
                                else {
                                    c.ruthless = false;
                                    c.vVirg = true;
                                    c.vStart = true;
                                    c.vTaker = -1;
                                }
                                if (!w.determineCVirg(c.morality, c.innocence, c.confidence, c.dignity)) {
                                    c.lustful = true;
                                    c.cVirg = false;
                                    c.cStart = false;
                                    c.cTaker = 0;
                                }
                                else {
                                    c.lustful = false;
                                    c.cVirg = true;
                                    c.cStart = true;
                                    c.cTaker = -1;
                                }
                                if (!w.determineAVirg(c.morality, c.innocence, c.confidence, c.dignity)) {
                                    c.meek = true;
                                    c.aVirg = false;
                                    c.aStart = false;
                                    c.aTaker = 0;
                                }
                                else {
                                    c.meek = false;
                                    c.aVirg = true;
                                    c.aStart = true;
                                    c.aTaker = -1;
                                }
                                if (!w.determineModest(c.morality, c.innocence, c.confidence, c.dignity)) {
                                    c.debased = true;
                                    c.modest = false;
                                    c.mStart = false;
                                    c.mTaker = 0;
                                }
                                else {
                                    c.debased = false;
                                    c.modest = true;
                                    c.mStart = true;
                                    c.mTaker = -1;
                                }
                                Project.SingleCosmetics(t, p, f, w, c, null);
                            }
                            else {
                                w.append(t, "\n\n" + w.getSeparator() + "\n\nInvalid value.");
                                Project.SingleCustom(t, p, f, w, c, answers);
                            }
                        }
                        else {
                            w.append(t, "\n\n" + w.getSeparator() + "\n\nInvalid value.");
                            Project.SingleCustom(t, p, f, w, c, answers);
                        }
                    }
                    else {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\nInvalid value.");
                        Project.SingleCustom(t, p, f, w, c, answers);
                    }
                }
                else {
                    w.append(t, "\n\n" + w.getSeparator() + "\n\nInvalid value.");
                    Project.SingleCustom(t, p, f, w, c, answers);
                }
            }
        });
        p.add(DirectInput);
        final JButton RandomGen = new JButton("Continue (Random)");
        RandomGen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Boolean valid = false;
                final int[] stats = new int[4];
                int highs = 0;
                int mids = 0;
                int lows = 0;
                while (!valid) {
                    highs = 0;
                    mids = 0;
                    lows = 0;
                    valid = true;
                    for (int i = 0; i < 4; ++i) {
                        stats[i] = (int)(Math.random() * 101.0);
                        if (stats[i] > 66) {
                            ++highs;
                        }
                        else if (stats[i] > 33) {
                            ++mids;
                        }
                        else {
                            ++lows;
                        }
                    }
                    if (highs == 0 || mids == 0 || lows == 0) {
                        valid = false;
                    }
                }
                c.morality = stats[0];
                c.innocence = stats[1];
                c.confidence = stats[2];
                c.dignity = stats[3];
                if (!w.determineVVirg(c.morality, c.innocence, c.confidence, c.dignity)) {
                    c.ruthless = true;
                    c.vVirg = false;
                    c.vStart = false;
                    c.vTaker = 0;
                }
                else {
                    c.ruthless = false;
                    c.vVirg = true;
                    c.vStart = true;
                    c.vTaker = -1;
                }
                if (!w.determineCVirg(c.morality, c.innocence, c.confidence, c.dignity)) {
                    c.lustful = true;
                    c.cVirg = false;
                    c.cStart = false;
                    c.cTaker = 0;
                }
                else {
                    c.lustful = false;
                    c.cVirg = true;
                    c.cStart = true;
                    c.cTaker = -1;
                }
                if (!w.determineAVirg(c.morality, c.innocence, c.confidence, c.dignity)) {
                    c.meek = true;
                    c.aVirg = false;
                    c.aStart = false;
                    c.aTaker = 0;
                }
                else {
                    c.meek = false;
                    c.aVirg = true;
                    c.aStart = true;
                    c.aTaker = -1;
                }
                if (!w.determineModest(c.morality, c.innocence, c.confidence, c.dignity)) {
                    c.debased = true;
                    c.modest = false;
                    c.mStart = false;
                    c.mTaker = 0;
                }
                else {
                    c.debased = false;
                    c.modest = true;
                    c.mStart = true;
                    c.mTaker = -1;
                }
                Project.SingleCosmetics(t, p, f, w, c, null);
            }
        });
        p.add(RandomGen);
        final JButton Quit = new JButton("Quit");
        Quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                w.append(t, "\n\n" + w.getSeparator() + "\n\nReally quit?  All customization of this Chosen will be lost!");
                final JButton MainMenu = new JButton("Quit");
                MainMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        Project.IntroOne(t, p, f, w);
                    }
                });
                p.add(MainMenu);
                final JButton Cancel = new JButton("Cancel");
                Cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.SingleCustom(t, p, f, w, c, answers);
                    }
                });
                p.add(Cancel);
                p.validate();
                p.repaint();
            }
        });
        p.add(Quit);
        p.validate();
        p.repaint();
    }
    
    public static void CustomQuiz(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c, final int progress, final int[] answers) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        if (progress == 0) {
            w.append(t, "This segment takes the form of a series of questions about how " + c.givenName + " thinks and behaves.  Pick the option that comes closest to how " + c.heShe() + " really is.\n\nQuestion 1: In what way does " + c.givenName + " try to act?");
        }
        else if (progress == 1) {
            w.append(t, "Question 2: Does " + c.givenName + " try to earn others' fear, or does " + c.heShe() + " try to earn their love?");
        }
        else if (progress == 2) {
            w.append(t, "Question 3: What does " + c.givenName + " do after making a mistake that hurts someone else?");
        }
        else if (progress == 3) {
            w.append(t, "Question 4: What does " + c.givenName + " tend to do when someone disagrees with " + c.himHer() + " about what they should do?");
        }
        else if (progress == 4) {
            w.append(t, "Question 5: Which enemies does " + c.givenName + " prefer to target?");
        }
        else if (progress == 5) {
            w.append(t, "Question 6: How does " + c.givenName + " treat people " + c.heShe() + " dislikes?");
        }
        else if (progress == 6) {
            w.append(t, "Question 7: What does " + c.givenName + " do when someone hurts " + c.himHer() + "?");
        }
        else if (progress == 7) {
            w.append(t, "Question 8: Which social role describes " + c.givenName + "?");
        }
        else if (progress == 8) {
            w.append(t, "Question 9: How does " + c.givenName + " feel after being forced to run from a fight?");
        }
        else if (progress == 9) {
            w.append(t, "Question 10: When is it possible for " + c.givenName + "'s friends to convince " + c.himHer() + " to commit a crime?");
        }
        else if (progress == 10) {
            w.append(t, "Question 11: How can one pick a fight with " + c.givenName + "?");
        }
        else if (progress == 11) {
            w.append(t, "Question 12: How does " + c.givenName + " fight against a stronger enemy?");
        }
        else if (progress == 12) {
            w.append(t, "Question 13: How does " + c.givenName + " respond when civilians are taken hostage?");
        }
        else if (progress == 13) {
            w.append(t, "Question 14: Will " + c.givenName + " lie to protect someone else?");
        }
        else if (progress == 14) {
            w.append(t, "Question 15: How does " + c.givenName + " react when stripped while protecting civilians?");
        }
        else if (progress == 15) {
            w.append(t, "Question 16: How does " + c.givenName + " respond to being praised?");
        }
        else if (progress == 16) {
            w.append(t, "Question 17: When does " + c.givenName + " put " + c.himHer() + "self in harm's way?");
        }
        else if (progress == 17) {
            w.append(t, "Question 18: What does " + c.givenName + " think of " + c.hisHer() + " fanbase?");
        }
        else if (progress == 18) {
            w.append(t, "Question 19: What does " + c.givenName + " do when interviewed by reporters after a major defeat?");
        }
        else if (progress == 19) {
            w.append(t, "Question 20: How does " + c.givenName + " fight against a weaker enemy?");
        }
        else if (progress == 20) {
            w.append(t, "Question 21: How does " + c.givenName + " handle it when " + c.hisHer() + " friends get into an argument?");
        }
        else if (progress == 21) {
            w.append(t, "Question 22: What does " + c.givenName + " do about " + c.hisHer() + " friends' personality flaws?");
        }
        else if (progress == 22) {
            w.append(t, "Question 23: Does " + c.givenName + " claim to be a good person?");
        }
        else if (progress == 23) {
            w.append(t, "Question 24: Does " + c.givenName + " pretend to be stronger than " + c.heShe() + " actually is?");
        }
        else if (progress == 24) {
            w.append(t, "Question 25: When is " + c.givenName + " willing to abandon innocent lives?");
        }
        else if (progress == 25) {
            w.append(t, "Question 26: How does " + c.givenName + " handle fans who try to get too close to " + c.himHer() + "?");
        }
        else if (progress == 26) {
            w.append(t, "Question 27: How does " + c.givenName + " respond to being given orders?");
        }
        else if (progress == 27) {
            w.append(t, "Question 28: How does " + c.givenName + " prefer to get help from people?");
        }
        else if (progress == 28) {
            w.append(t, "Question 29: What does " + c.givenName + " do with the vast wealth paid to the Chosen?");
        }
        else if (progress == 29) {
            w.append(t, "Question 30: What does " + c.givenName + " tell " + c.hisHer() + " fans to do?");
        }
        else if (progress == 30) {
            w.append(t, "Question 31: Does " + c.givenName + " keep " + c.hisHer() + " promises?");
        }
        else if (progress == 31) {
            w.append(t, "Question 32: What does " + c.givenName + " do when " + c.heShe() + " notices " + c.heShe() + "'s being filmed by spectators?");
        }
        if (answers[progress] != 0) {
            w.append(t, "  (Previous answer: " + answers[progress] + ")");
        }
        w.append(t, "\n\n");
        if (progress == 0) {
            w.append(t, "1: A way that makes others happy.\n2: A way that makes everyone happy.\n3: A way that makes " + c.givenName + " " + c.himHer() + "self happy.");
        }
        else if (progress == 1) {
            w.append(t, "1: Love.\n2: Both fear and love.\n3: Fear.");
        }
        else if (progress == 2) {
            w.append(t, "1: Apologize.\n2: Ignore them.\n3: Try to cover up " + c.hisHer() + " involvement.");
        }
        else if (progress == 3) {
            w.append(t, "1: Give in.\n2: Stubbornly hold " + c.hisHer() + " position.\n3: Try to reason with the other person.");
        }
        else if (progress == 4) {
            w.append(t, "1: The ones threatening innocents.\n2: Whichever ones cross " + c.hisHer() + " path.\n3: Whichever ones stand between " + c.himHer() + " and safety.");
        }
        else if (progress == 5) {
            w.append(t, "1: Try to find a way to befriend them.\n2: Spread gossip about them.\n3: Constantly bring up their weaknesses and insecurities.");
        }
        else if (progress == 6) {
            w.append(t, "1: Hold " + c.hisHer() + " pain inside.\n2: Hurt them back, by any means necessary.\n3: Whine to anyone who will listen.");
        }
        else if (progress == 7) {
            w.append(t, "1: Follower.\n2: Leader.\n3: Outcast.");
        }
        else if (progress == 8) {
            w.append(t, "1: Angry at " + c.himHer() + "self for being too weak.\n2: Perfectly content to have safely escaped.\n3: Angry at " + c.hisHer() + " allies for putting " + c.himHer() + " in that position.");
        }
        else if (progress == 9) {
            w.append(t, "1: When " + c.heShe() + " thinks it won't hurt anybody.\n2: When " + c.heShe() + "'s sure " + c.heShe() + " won't be punished for it.\n3: Pretty much always.");
        }
        else if (progress == 10) {
            w.append(t, "1: By hurting others in front of " + c.himHer() + ".\n2: It isn't possible.\n3: By insulting " + c.hisHer() + " pride.");
        }
        else if (progress == 11) {
            w.append(t, "1: Charge straight in.\n2: Call for help.\n3: Find a way to even the odds.");
        }
        else if (progress == 12) {
            w.append(t, "1: " + c.HeShe() + " attacks anyway.\n2: " + c.HeShe() + " surrenders immediately.\n3: " + c.HeShe() + " cooperates only until " + c.heShe() + " can find a chance to free them.");
        }
        else if (progress == 13) {
            w.append(t, "1: Only if they make it worth " + c.hisHer() + " while.\n2: " + c.HeShe() + " thinks lying is wrong.\n3: " + c.HeShe() + " will, if " + c.heShe() + " thinks they deserve it.");
        }
        else if (progress == 14) {
            w.append(t, "1: Flee, leaving the civilians to their fate.\n2: Laugh it off.\n3: Try to pretend it doesn't bother " + c.himHer() + ".");
        }
        else if (progress == 15) {
            w.append(t, "1: " + c.HeShe() + " assumes that the other person is trying to get something from " + c.himHer() + ".\n2: " + c.HeShe() + " accepts it as what " + c.heShe() + " deserves.\n3: " + c.HeShe() + " feels surprised and happy.");
        }
        else if (progress == 16) {
            w.append(t, "1: Whenever it protects others.\n2: Whenever there's an appropriate reward.\n3: Almost never.");
        }
        else if (progress == 17) {
            w.append(t, "1: " + c.HeShe() + " wants to serve them.\n2: " + c.HeShe() + " wants them to serve " + c.himHer() + ".\n3: " + c.HeShe() + " doesn't care about them.");
        }
        else if (progress == 18) {
            w.append(t, "1: Reassure everyone.\n2: Blame someone else.\n3: Ignore them.");
        }
        else if (progress == 19) {
            w.append(t, "1: Let " + c.hisHer() + " guard down and show off.\n2: Remain slow, steady, and cautious.\n3: Take them out quickly and move on.");
        }
        else if (progress == 20) {
            w.append(t, "1: Take the side of whoever is more useful to " + c.himHer() + ".\n2: Take the side of whoever " + c.heShe() + " agrees with.\n3: Try to help them resolve their differences with each other.");
        }
        else if (progress == 21) {
            w.append(t, "1: Exploit them for " + c.hisHer() + " own benefit.\n2: Tolerate them patiently.\n3: Try to help them overcome their flaws.");
        }
        else if (progress == 22) {
            w.append(t, "1: Yes, but " + c.heShe() + " knows " + c.heShe() + "'s not.\n2: Yes, and " + c.heShe() + " thinks it's true.\n3: No, " + c.heShe() + " doesn't.");
        }
        else if (progress == 23) {
            w.append(t, "1: Yes, but not by so much that it can be disproven.\n2: Yes, to an impossible degree.\n3: No, never.");
        }
        else if (progress == 24) {
            w.append(t, "1: Whenever " + c.heShe() + " feels like it would be even slightly dangerous for " + c.himHer() + ".\n2: Whenever " + c.heShe() + " judges it unlikely that " + c.heShe() + "'d be able to successfully save them.\n3: Never.");
        }
        else if (progress == 25) {
            w.append(t, "1: Being mean to them.\n2: Firmly but gently turning them away.\n3: Trying to make them happy without going too far beyond " + c.hisHer() + " comfort zone.");
        }
        else if (progress == 26) {
            w.append(t, "1: Spitefully do the opposite.\n2: Obey quickly.\n3: Obey only if " + c.heShe() + " agrees with them.");
        }
        else if (progress == 27) {
            w.append(t, "1: Bargain for something of equal value.\n2: Whine and beg.\n3: Act cute and sweet-talk them.");
        }
        else if (progress == 28) {
            w.append(t, "1: Spend it all on luxuries for " + c.himHer() + "self.\n2: Personally direct it into projects which benefit society as " + c.heShe() + " sees it.\n3: Give most of it away to people who know better than " + c.himHer() + " how it should be spent.");
        }
        else if (progress == 29) {
            w.append(t, "1: Worship " + c.himHer() + ".\n2: Try to make the world a better place.\n3: Do whatever makes them happy.");
        }
        else if (progress == 30) {
            w.append(t, "1: Only when " + c.heShe() + " feels like it.\n2: " + c.HeShe() + " tries, but often promises more than " + c.heShe() + " can do.\n3: Yes, " + c.heShe() + "'s trustworthy.");
        }
        else if (progress == 31) {
            w.append(t, "1: Focus even more on not making any mistakes.\n2: Get flustered.\n3: Ignore them.");
        }
        for (int i = 0; i < 3; ++i) {
            final int picked = i + 1;
            final JButton ThisAnswer = new JButton(new StringBuilder().append(picked).toString());
            ThisAnswer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    answers[progress] = picked;
                    if (progress < 31) {
                        Project.CustomQuiz(t, p, f, w, c, progress + 1, answers);
                    }
                    else {
                        Project.SinglePersonality(t, p, f, w, c, answers);
                    }
                }
            });
            p.add(ThisAnswer);
        }
        final JButton Pass = new JButton("Pass");
        Pass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                answers[progress] = 0;
                if (progress < 31) {
                    Project.CustomQuiz(t, p, f, w, c, progress + 1, answers);
                }
                else {
                    Project.SinglePersonality(t, p, f, w, c, answers);
                }
            }
        });
        p.add(Pass);
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (progress > 0) {
                    Project.CustomQuiz(t, p, f, w, c, progress - 1, answers);
                }
                else {
                    Project.SingleCustom(t, p, f, w, c, answers);
                }
            }
        });
        p.add(Back);
        final JButton Quit = new JButton("Quit");
        Quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                w.append(t, "\n\n" + w.getSeparator() + "\n\nReally quit?  All customization of this Chosen will be lost!");
                final JButton MainMenu = new JButton("Quit");
                MainMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        Project.IntroOne(t, p, f, w);
                    }
                });
                p.add(MainMenu);
                final JButton Cancel = new JButton("Cancel");
                Cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.CustomQuiz(t, p, f, w, c, progress, answers);
                    }
                });
                p.validate();
                p.repaint();
            }
        });
        p.add(Quit);
        p.validate();
        p.repaint();
    }
    
    public static void SinglePersonality(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c, final int[] answers) {
        final int[] totals = new int[4];
        final int[] certainties = new int[4];
        for (int i = 0; i < 32; ++i) {
            final int skipped = 3 - i % 4;
            final int[] signs = new int[3];
            final int first = 0;
            final int second = 0;
            final int third = 0;
            if (i / 4 == 0) {
                signs[0] = 1;
                signs[2] = (signs[1] = 1);
            }
            else if (i / 4 == 1) {
                signs[signs[0] = 1] = 1;
                signs[2] = -1;
            }
            else if (i / 4 == 2) {
                signs[signs[0] = 1] = -1;
                signs[2] = 1;
            }
            else if (i / 4 == 3) {
                signs[0] = -1;
                signs[2] = (signs[1] = 1);
            }
            else if (i / 4 == 4) {
                signs[0] = 1;
                signs[2] = (signs[1] = -1);
            }
            else if (i / 4 == 5) {
                signs[0] = -1;
                signs[1] = 1;
                signs[2] = -1;
            }
            else if (i / 4 == 6) {
                signs[1] = (signs[0] = -1);
                signs[2] = 1;
            }
            else {
                signs[0] = -1;
                signs[2] = (signs[1] = -1);
            }
            for (int j = 0; j < 3 && answers[i] != 0; ++j) {
                int index;
                if (skipped <= (index = j)) {
                    ++index;
                }
                if (answers[i] - 1 == j) {
                    totals[index] += 2 * signs[j];
                    certainties[index] += 2;
                }
                else {
                    totals[index] -= signs[j];
                    ++certainties[index];
                }
            }
        }
        final int[] stats = new int[4];
        int highs = 0;
        int mids = 0;
        int lows = 0;
        for (int k = 0; k < 4; ++k) {
            if (totals[k] < -14) {
                stats[k] = totals[k] + 36;
            }
            else if (totals[k] > 14) {
                stats[k] = totals[k] + 64;
            }
            else {
                stats[k] = totals[k] * 2 + 50;
            }
            if (certainties[k] < 30) {
                if (stats[k] > 50) {
                    stats[k] = stats[k] + 30 - certainties[k];
                }
                else {
                    stats[k] = stats[k] - 30 + certainties[k];
                }
            }
            if (stats[k] > 66) {
                ++highs;
            }
            else if (stats[k] > 33) {
                ++mids;
            }
            else {
                ++lows;
            }
        }
        while (highs == 0 || mids == 0 || lows == 0) {
            highs = 0;
            mids = 0;
            lows = 0;
            for (int k = 0; k < 4; ++k) {
                if (stats[k] > 66) {
                    ++highs;
                }
                else if (stats[k] > 33) {
                    ++mids;
                }
                else {
                    ++lows;
                }
            }
            if (highs == 0) {
                int greatestFlexibility = 0;
                int flexibleStat = 0;
                for (int l = 0; l < 4; ++l) {
                    if (stats[l] * (36 - certainties[l]) > greatestFlexibility) {
                        greatestFlexibility = stats[l] * (36 - certainties[l]);
                        flexibleStat = l;
                    }
                }
                stats[flexibleStat] = 67 + (36 - certainties[flexibleStat]) / 2;
            }
            else if (lows == 0) {
                int greatestFlexibility = 0;
                int flexibleStat = 0;
                for (int l = 0; l < 4; ++l) {
                    if ((100 - stats[l]) * (36 - certainties[l]) > greatestFlexibility) {
                        greatestFlexibility = (100 - stats[l]) * (36 - certainties[l]);
                        flexibleStat = l;
                    }
                }
                stats[flexibleStat] = 33 - (36 - certainties[flexibleStat]) / 2;
            }
            else {
                int greatestFlexibility = 0;
                int flexibleStat = 0;
                for (int l = 0; l < 4; ++l) {
                    if (((stats[l] > 66 && highs > 1) || (stats[l] < 34 && lows > 1)) && Math.abs(stats[l] - 50) * (36 - certainties[l]) > greatestFlexibility) {
                        greatestFlexibility = (100 - stats[l]) * (36 - certainties[l]);
                        flexibleStat = l;
                    }
                }
                if (stats[flexibleStat] > 50) {
                    stats[flexibleStat] = 66 - (36 - certainties[flexibleStat]) / 2;
                }
                else {
                    stats[flexibleStat] = 33 + (36 - certainties[flexibleStat]) / 2;
                }
            }
            for (int k = 0; k < 4; ++k) {
                if (stats[k] > 66) {
                    ++highs;
                }
                else if (stats[k] > 33) {
                    ++mids;
                }
                else {
                    ++lows;
                }
            }
        }
        c.morality = stats[0];
        c.innocence = stats[1];
        c.confidence = stats[2];
        c.dignity = stats[3];
        if (!w.determineVVirg(c.morality, c.innocence, c.confidence, c.dignity)) {
            c.ruthless = true;
            c.vVirg = false;
            c.vStart = false;
            c.vTaker = 0;
        }
        else {
            c.ruthless = false;
            c.vVirg = true;
            c.vStart = true;
            c.vTaker = -1;
        }
        if (!w.determineCVirg(c.morality, c.innocence, c.confidence, c.dignity)) {
            c.lustful = true;
            c.cVirg = false;
            c.cStart = false;
            c.cTaker = 0;
        }
        else {
            c.lustful = false;
            c.cVirg = true;
            c.cStart = true;
            c.cTaker = -1;
        }
        if (!w.determineAVirg(c.morality, c.innocence, c.confidence, c.dignity)) {
            c.meek = true;
            c.aVirg = false;
            c.aStart = false;
            c.aTaker = 0;
        }
        else {
            c.meek = false;
            c.aVirg = true;
            c.aStart = true;
            c.aTaker = -1;
        }
        if (!w.determineModest(c.morality, c.innocence, c.confidence, c.dignity)) {
            c.debased = true;
            c.modest = false;
            c.mStart = false;
            c.mTaker = 0;
        }
        else {
            c.debased = false;
            c.modest = true;
            c.mStart = true;
            c.mTaker = -1;
        }
        SingleCosmetics(t, p, f, w, c, answers);
    }
    
    public static void SingleCosmetics(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c, final int[] answers) {
        p.removeAll();
        if (c.mainName == null) {
            c.world = w;
            if (c.globalID == 0) {
                c.globalID = w.save.assignChosenID();
            }
            c.textSize = w.textSize;
            if (c.morality > 66) {
                c.textColor = new Color(0, 0, 230);
                c.darkColor = new Color(100, 100, 255);
            }
            else if (c.morality > 33) {
                c.textColor = new Color(0, 110, 0);
                c.darkColor = new Color(70, 170, 70);
            }
            else {
                c.textColor = new Color(180, 0, 0);
                c.darkColor = new Color(220, 90, 90);
            }
            c.incantation = c.genIncantation(c.morality, c.dignity);
            c.adjectiveName = c.genAdjectiveName(c.innocence, c.confidence);
            c.nounName = c.genNounName(c.morality, c.innocence);
            c.mainName = c.genMainName(c.morality, c.innocence, c.confidence, c.dignity);
            final String[] cosmetics = w.pickCosmetics(c.morality, c.innocence, c.confidence, c.dignity);
            c.topCover = cosmetics[0];
            c.topAccess = cosmetics[1];
            c.bottomCover = cosmetics[2];
            c.bottomAccess = cosmetics[3];
            c.feetType = cosmetics[9];
            c.underType = cosmetics[4];
            c.color = cosmetics[5];
            c.accessory = cosmetics[6];
            c.weapon = cosmetics[7];
            c.customWeaponType = cosmetics[8];
            if (c.morality > 66) {
                c.bonusHATE = true;
            }
            else {
                c.bonusHATE = false;
            }
            if (c.innocence > 66) {
                c.bonusPLEA = true;
            }
            else {
                c.bonusPLEA = false;
            }
            if (c.confidence > 66) {
                c.bonusINJU = true;
            }
            else {
                c.bonusINJU = false;
            }
            if (c.dignity > 66) {
                c.bonusEXPO = true;
            }
            else {
                c.bonusEXPO = false;
            }
        }
        w.append(t, "\n\n" + w.getSeparator() + "\n\n" + c.customSummary());
        int highs = 0;
        int mids = 0;
        int lows = 0;
        if (c.morality > 66) {
            w.blueAppend(t, "\nMorality " + c.morality + ": Core");
            ++highs;
        }
        else if (c.morality > 33) {
            w.greenAppend(t, "\nMorality " + c.morality + ": Significant");
            ++mids;
        }
        else {
            w.redAppend(t, "\nMorality " + c.morality + ": Minor");
            ++lows;
        }
        if (!c.vVirg) {
            w.redAppend(t, " (BROKEN)");
        }
        if (c.innocence > 66) {
            w.blueAppend(t, "\nInnocence " + c.innocence + ": Core");
            ++highs;
        }
        else if (c.innocence > 33) {
            w.greenAppend(t, "\nInnocence " + c.innocence + ": Significant");
            ++mids;
        }
        else {
            w.redAppend(t, "\nInnocence " + c.innocence + ": Minor");
            ++lows;
        }
        if (!c.cVirg) {
            w.redAppend(t, " (BROKEN)");
        }
        if (c.confidence > 66) {
            w.blueAppend(t, "\nConfidence " + c.confidence + ": Core");
            ++highs;
        }
        else if (c.confidence > 33) {
            w.greenAppend(t, "\nConfidence " + c.confidence + ": Significant");
            ++mids;
        }
        else {
            w.redAppend(t, "\nConfidence " + c.confidence + ": Minor");
            ++lows;
        }
        if (!c.aVirg) {
            w.redAppend(t, " (BROKEN)");
        }
        if (c.dignity > 66) {
            w.blueAppend(t, "\nDignity " + c.dignity + ": Core");
            ++highs;
        }
        else if (c.dignity > 33) {
            w.greenAppend(t, "\nDignity " + c.dignity + ": Significant");
            ++mids;
        }
        else {
            w.redAppend(t, "\nDignity " + c.dignity + ": Minor");
            ++lows;
        }
        if (!c.modest) {
            w.redAppend(t, " (BROKEN)");
        }
        w.append(t, "\nValid Custom Partners:");
        int found = 0;
        for (int i = 0; i < w.save.customRoster.length; ++i) {
            int foundHighs = 0;
            int foundMids = 0;
            int foundLows = 0;
            Boolean compatible = true;
            final Chosen subject = w.save.customRoster[i];
            if (subject.morality > 66) {
                ++foundHighs;
                if (c.morality > 66) {
                    compatible = false;
                }
            }
            else if (subject.morality > 33) {
                ++foundMids;
                if (c.morality > 33 && c.morality < 67) {
                    compatible = false;
                }
            }
            else {
                ++foundLows;
                if (c.morality < 34) {
                    compatible = false;
                }
            }
            if (subject.innocence > 66) {
                ++foundHighs;
                if (c.innocence > 66) {
                    compatible = false;
                }
            }
            else if (subject.innocence > 33) {
                ++foundMids;
                if (c.innocence > 33 && c.innocence < 67) {
                    compatible = false;
                }
            }
            else {
                ++foundLows;
                if (c.innocence < 34) {
                    compatible = false;
                }
            }
            if (subject.confidence > 66) {
                ++foundHighs;
                if (c.confidence > 66) {
                    compatible = false;
                }
            }
            else if (subject.confidence > 33) {
                ++foundMids;
                if (c.confidence > 33 && c.confidence < 67) {
                    compatible = false;
                }
            }
            else {
                ++foundLows;
                if (c.confidence < 34) {
                    compatible = false;
                }
            }
            if (subject.dignity > 66) {
                ++foundHighs;
                if (c.dignity > 66) {
                    compatible = false;
                }
            }
            else if (subject.dignity > 33) {
                ++foundMids;
                if (c.dignity > 33 && c.dignity < 67) {
                    compatible = false;
                }
            }
            else {
                ++foundLows;
                if (c.dignity < 34) {
                    compatible = false;
                }
            }
            if ((highs == 2 && foundHighs == 2) || (mids == 2 && foundMids == 2) || (lows == 2 && foundLows == 2)) {
                compatible = false;
            }
            if (compatible) {
                ++found;
                w.append(t, "\n " + subject.mainName);
            }
        }
        if (found == 0) {
            w.append(t, "\nNone");
        }
        w.append(t, "\n\nYou can adjust Vulnerability Breaks and cosmetic details here.  Note that going back to the previous screen will reset these to their default values.");
        final JButton Breaks = new JButton("Vulnerability Breaks");
        Breaks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.SingleVulnerabilities(t, p, f, w, c, answers, 0);
            }
        });
        p.add(Breaks);
        final JButton Cosmetics = new JButton("Cosmetics");
        Cosmetics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.SingleNameAndClothes(t, p, f, w, c, answers, 0);
            }
        });
        p.add(Cosmetics);
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.mainName = null;
                if (answers == null) {
                    Project.SingleCustom(t, p, f, w, c, null);
                }
                else {
                    Project.CustomQuiz(t, p, f, w, c, 31, answers);
                }
            }
        });
        p.add(Back);
        final JButton Finish = new JButton("Finish");
        Finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                w.append(t, "\n\n" + w.getSeparator() + "\n\nThis will add the Chosen to your save file's custom roster, and you will not be able to make further adjustments.");
                final JButton Cancel = new JButton("Back");
                Cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.SingleCosmetics(t, p, f, w, c, answers);
                    }
                });
                p.add(Cancel);
                final JButton Done = new JButton("Done");
                Done.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final Chosen[] newRoster = new Chosen[w.save.customRoster.length + 1];
                        for (int i = 0; i < w.save.customRoster.length; ++i) {
                            newRoster[i] = w.save.customRoster[i];
                        }
                        newRoster[w.save.customRoster.length] = c;
                        w.save.customRoster = newRoster;
                        final WriteObject wobj = new WriteObject();
                        wobj.serializeSaveData(w.save);
                        final Boolean[] included = new Boolean[w.save.customRoster.length];
                        for (int j = 0; j < included.length; ++j) {
                            included[j] = true;
                        }
                        Project.CampaignMenu(t, p, f, w, included);
                    }
                });
                p.add(Done);
                p.validate();
                p.repaint();
            }
        });
        p.add(Finish);
        p.validate();
        p.repaint();
    }
    
    public static void SingleNameAndClothes(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c, final int[] answers, final int progress) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        if (progress == 0) {
            if (c.mainName.equals(c.givenName) || c.mainName.equals(c.familyName)) {
                w.append(t, "The first step is to decide what " + c.givenName + " will call " + c.himHer() + "self.  " + c.HeShe() + "'s currently just going by '" + c.mainName + "', but most Chosen pick an alias for themselves.  Which should " + c.heShe() + " choose?");
            }
            else {
                w.append(t, "The first step is to decide what " + c.givenName + " will call " + c.himHer() + "self.  " + c.HeShe() + " likes the sound of '" + c.mainName + "', but the civilian identities of the Chosen are a matter of public record, so it wouldn't be too strange for " + c.himHer() + " to go by " + c.hisHer() + " real name.  Which should " + c.heShe() + " choose?");
            }
            if (!c.mainName.equals(c.givenName)) {
                final JButton CurrentName = new JButton(c.mainName);
                CurrentName.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 1);
                    }
                });
                p.add(CurrentName);
            }
            final JButton GivenName = new JButton(c.givenName);
            GivenName.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    c.mainName = c.givenName;
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 1);
                }
            });
            p.add(GivenName);
            if (!c.mainName.equals(c.familyName) && c.familyName.length() > 0) {
                final JButton FamilyName = new JButton(c.familyName);
                FamilyName.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        c.mainName = c.familyName;
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 1);
                    }
                });
                p.add(FamilyName);
            }
            final JButton SomethingElse = new JButton("Something Else");
            SomethingElse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final String input = JOptionPane.showInputDialog("Type the name to be used here.  Leave blank to continue using '" + c.mainName + "'.");
                    if (input != null && input.length() > 0) {
                        c.mainName = input;
                    }
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 1);
                }
            });
            p.add(SomethingElse);
        }
        else if (progress == 1) {
            if (c.nounName.length() > 0) {
                w.append(t, "Most Chosen also use a descriptive title that defines how they see themselves.  " + c.mainName + "'s first idea is to use '" + c.adjectiveName + " " + c.nounName + "', so that " + c.heShe() + "'d be '" + c.adjectiveName + " " + c.nounName + " " + c.mainName + "'.  Should " + c.heShe() + " use something different?");
            }
            else if (c.adjectiveName.equals("none")) {
                w.append(t, "Most Chosen also use a descriptive title that defines how they see themselves.  However, none immediately comes to " + c.mainName + ".  Should " + c.heShe() + " use one at all?");
            }
            else {
                w.append(t, "Most Chosen also use a descriptive title that defines how they see themselves.  " + c.mainName + "'s first idea is to use '" + c.adjectiveName + "', so that " + c.heShe() + "'d be '" + c.adjectiveName + " " + c.mainName + "'.  Should " + c.heShe() + " use something different?");
            }
            final JButton CurrentTitle = new JButton(c.adjectiveName);
            if (c.nounName.length() > 0) {
                CurrentTitle.setText(String.valueOf(c.adjectiveName) + " " + c.nounName);
            }
            CurrentTitle.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 2);
                }
            });
            p.add(CurrentTitle);
            final JButton SomethingElse = new JButton("Something Else");
            SomethingElse.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    c.nounName = "";
                    final String input = JOptionPane.showInputDialog("Type the title to be used here.  Leave blank to just go by '" + c.mainName + "'.");
                    if (input != null) {
                        if (input.length() > 0) {
                            c.adjectiveName = input;
                        }
                        else {
                            c.adjectiveName = "none";
                        }
                    }
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 2);
                }
            });
            p.add(SomethingElse);
        }
        else if (progress == 2) {
            w.append(t, "In order to transform into ");
            if (c.adjectiveName.equals("none")) {
                if (c.mainName.equals(c.givenName) || c.mainName.equals(c.familyName)) {
                    w.append(t, String.valueOf(c.hisHer()) + " Chosen form");
                }
                else {
                    w.append(t, c.mainName);
                }
            }
            else if (c.nounName.length() > 0) {
                w.append(t, String.valueOf(c.adjectiveName) + " " + c.nounName + " " + c.mainName);
            }
            else {
                w.append(t, String.valueOf(c.adjectiveName) + " " + c.mainName);
            }
            w.append(t, ", " + c.givenName + " needs to speak an incantation of " + c.hisHer() + " choice.  The first that comes to " + c.hisHer() + " mind is '" + c.incantation + "'.");
            final JButton Keep = new JButton("Keep");
            Keep.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 3);
                }
            });
            p.add(Keep);
            final JButton Change = new JButton("Change");
            Change.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final String input = JOptionPane.showInputDialog("Type the new incantation here.  Leave blank to leave it unchanged.");
                    if (input != null && input.length() > 0) {
                        c.incantation = input;
                    }
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 3);
                }
            });
            p.add(Change);
        }
        else if (progress == 3) {
            String result = c.mainName;
            if (c.nounName.length() > 0) {
                result = String.valueOf(c.nounName) + " " + result;
            }
            if (!c.adjectiveName.equals("none")) {
                result = String.valueOf(c.adjectiveName) + " " + result;
            }
            result = String.valueOf(c.incantation) + "  " + result + ", transform!";
            w.append(t, String.valueOf(c.givenName) + "'s civilian clothes will disintegrate when " + c.heShe() + " says '" + result + "'  In their place, garments and equipment woven of psychic energy representing " + c.hisHer() + " true nature will materialize.  Click 'Change' to give " + c.himHer() + " something different, or click the button for the current item to keep it.\n\nFirst off, what does " + c.heShe() + " wear to cover " + c.hisHer() + " chest?");
            final JButton Current = new JButton(c.topDesc());
            Current.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 5);
                }
            });
            p.add(Current);
            final JButton Change2 = new JButton("Change");
            Change2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final String input = JOptionPane.showInputDialog("Type the name of the garment here.  Leave blank to use '" + c.topDesc() + "'.");
                    Boolean changed = false;
                    if (input != null && !input.equals(c.topDesc()) && input.length() > 0) {
                        changed = true;
                    }
                    if (changed) {
                        c.topCover = input;
                        c.accessory = "none";
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 4);
                    }
                    else {
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 5);
                    }
                }
            });
            p.add(Change2);
        }
        else if (progress == 4) {
            if (c.gender.equals("male")) {
                w.append(t, "And in order to get at " + c.hisHer() + " nipples, does one go up " + c.hisHer() + " " + c.topDesc() + ", into " + c.hisHer() + " " + c.topDesc() + ", down " + c.hisHer() + " " + c.topDesc() + ", or around " + c.hisHer() + " " + c.topDesc() + "?");
            }
            else {
                w.append(t, "And in order to get at " + c.hisHer() + " breasts, does one go up " + c.hisHer() + " " + c.topDesc() + ", into " + c.hisHer() + " " + c.topDesc() + ", down " + c.hisHer() + " " + c.topDesc() + ", or around " + c.hisHer() + " " + c.topDesc() + "?");
            }
            for (int j = 0; j < 4; ++j) {
                String method = "";
                if (j == 0) {
                    method = "up";
                }
                else if (j == 1) {
                    method = "into";
                }
                else if (j == 2) {
                    method = "down";
                }
                else if (j == 3) {
                    method = "around";
                }
                final String finalMethod = method;
                final JButton ThisOne = new JButton(method);
                ThisOne.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        c.topAccess = finalMethod;
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 5);
                    }
                });
                p.add(ThisOne);
            }
        }
        else if (progress == 5) {
            w.append(t, "Next, what does " + c.heShe() + " wear to cover " + c.hisHer() + " hips?");
            final JButton Current2 = new JButton(c.bottomDesc());
            Current2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 7);
                }
            });
            p.add(Current2);
            final JButton Change = new JButton("Change");
            Change.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final String input = JOptionPane.showInputDialog("Type the name of the garment here.  Leave blank to use '" + c.bottomDesc() + "'.");
                    Boolean changed = false;
                    if (input != null && !input.equals(c.bottomDesc()) && input.length() > 0) {
                        changed = true;
                    }
                    if (changed) {
                        c.bottomCover = input;
                        c.accessory = "none";
                        if (!c.underType.equals("none")) {
                            c.underType = "panties";
                        }
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 6);
                    }
                    else {
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 7);
                    }
                }
            });
            p.add(Change);
        }
        else if (progress == 6) {
            if (c.gender.equals("female")) {
                w.append(t, "And in order to get at " + c.hisHer() + " pussy, does one go up " + c.hisHer() + " " + c.bottomDesc() + ", into " + c.hisHer() + " " + c.bottomDesc() + ", down " + c.hisHer() + " " + c.bottomDesc() + ", or around " + c.hisHer() + " " + c.bottomDesc() + "?");
            }
            else {
                w.append(t, "And in order to get at " + c.hisHer() + " penis, does one go up " + c.hisHer() + " " + c.bottomDesc() + ", into " + c.hisHer() + " " + c.bottomDesc() + ", down " + c.hisHer() + " " + c.bottomDesc() + ", or around " + c.hisHer() + " " + c.bottomDesc() + "?");
            }
            for (int j = 0; j < 4; ++j) {
                String method = "";
                if (j == 0) {
                    method = "up";
                }
                else if (j == 1) {
                    method = "into";
                }
                else if (j == 2) {
                    method = "down";
                }
                else if (j == 3) {
                    method = "around";
                }
                final String finalMethod = method;
                final JButton ThisOne = new JButton(method);
                ThisOne.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        c.bottomAccess = finalMethod;
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 7);
                    }
                });
                p.add(ThisOne);
            }
        }
        else if (progress == 7) {
            w.append(t, "What footwear does " + c.mainName + "'s transformation give " + c.himHer() + "?  Enter 'none' (without the quotes) to have " + c.himHer() + " go barefoot.");
            final JButton Current2 = new JButton(c.feetType);
            Current2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 8);
                }
            });
            p.add(Current2);
            final JButton Change = new JButton("Change");
            Change.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final String input = JOptionPane.showInputDialog("Type the name of the garment here.  Leave blank to use '" + c.feetType + "'.");
                    if (input != null && input.length() > 0) {
                        if (!c.feetType.equals(input)) {
                            c.accessory = "none";
                        }
                        c.feetType = input;
                    }
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 8);
                }
            });
            p.add(Change);
        }
        else if (progress == 8) {
            w.append(t, "When " + c.heShe() + "'s transformed, " + c.mainName + " is surrounded by '" + c.color + "' light.  Is this alright?");
            final JButton Keep = new JButton("Yes");
            Keep.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 9);
                }
            });
            p.add(Keep);
            final JButton Change = new JButton("Change");
            Change.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final String input = JOptionPane.showInputDialog("Type the light description to be used.  Leave blank to use '" + c.color + "'.");
                    if (input != null && input.length() > 0) {
                        if (!c.color.equals(input)) {
                            c.accessory = "none";
                        }
                        c.color = input;
                    }
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 9);
                }
            });
            p.add(Change);
        }
        else if (progress == 9) {
            w.append(t, "Currently, " + c.mainName + " is set to fight using " + c.hisHer() + " " + c.weapon + ".  Is this okay?");
            final JButton Keep = new JButton("Yes");
            Keep.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, 11);
                }
            });
            p.add(Keep);
            final JButton Change = new JButton("Change");
            Change.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Boolean changed = false;
                    final String input = JOptionPane.showInputDialog("Type the name of the weapon to be used.  Leave blank to use '" + c.weapon + "'.");
                    if (input != null && input.length() > 0) {
                        if (!c.weapon.equals(input)) {
                            c.accessory = "none";
                            changed = true;
                        }
                        c.weapon = input;
                    }
                    if (changed) {
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 10);
                    }
                    else {
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 11);
                    }
                }
            });
            p.add(Change);
        }
        else if (progress == 10) {
            w.append(t, "Does " + c.mainName + " swing " + c.hisHer() + " " + c.weapon + ", shoot " + c.hisHer() + " " + c.weapon + ", command " + c.hisHer() + " " + c.weapon + ", or is " + c.hisHer() + " weapon a part of " + c.himHer() + "?");
            for (int j = 0; j < 4; ++j) {
                String method = "";
                if (j == 0) {
                    method = "swing";
                }
                else if (j == 1) {
                    method = "shoot";
                }
                else if (j == 2) {
                    method = "command";
                }
                else if (j == 3) {
                    method = "part of " + c.himHer();
                }
                final JButton ThisOne2 = new JButton(method);
                if (method.contains("part")) {
                    method = "part";
                }
                final String finalMethod2 = method;
                ThisOne2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        c.customWeaponType = finalMethod2;
                        Project.SingleNameAndClothes(t, p, f, w, c, answers, 11);
                    }
                });
                p.add(ThisOne2);
            }
        }
        else if (progress == 11) {
            w.append(t, "There's one final important question.  ");
            if (c.underType.equals("none")) {
                w.append(t, String.valueOf(c.mainName) + "'s outfit doesn't currently include panties.  Should that be changed?");
                final JButton Change3 = new JButton("Wear panties");
                Change3.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        c.underType = "panties";
                        c.accessory = "none";
                        Project.SingleCosmetics(t, p, f, w, c, answers);
                    }
                });
                p.add(Change3);
            }
            else {
                w.append(t, "Would you prefer for " + c.mainName + " to stop wearing anything under " + c.hisHer() + " " + c.bottomDesc() + "?");
                final JButton Change3 = new JButton("Wear nothing");
                Change3.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        c.underType = "none";
                        c.accessory = "none";
                        Project.SingleCosmetics(t, p, f, w, c, answers);
                    }
                });
                p.add(Change3);
            }
            final JButton Keep = new JButton("Leave it as is");
            Keep.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SingleCosmetics(t, p, f, w, c, answers);
                }
            });
            p.add(Keep);
        }
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (progress - 1 == 4 || progress - 1 == 6 || progress - 1 == 10) {
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, progress - 2);
                }
                else if (progress == 0) {
                    Project.SingleCosmetics(t, p, f, w, c, answers);
                }
                else {
                    Project.SingleNameAndClothes(t, p, f, w, c, answers, progress - 1);
                }
            }
        });
        if (progress != 4 && progress != 6 && progress != 10) {
            p.add(Back);
        }
        p.validate();
        p.repaint();
    }
    
    public static void SingleVulnerabilities(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c, final int[] answers, final int progress) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        if (progress == 0) {
            if (c.morality > 66) {
                w.append(t, String.valueOf(c.givenName) + "'s sense of Morality is a ");
                w.blueAppend(t, "Core");
                w.append(t, " part of " + c.hisHer() + " identity");
            }
            else if (c.morality > 33) {
                w.append(t, String.valueOf(c.givenName) + "'s Morality is a ");
                w.greenAppend(t, "Significant");
                w.append(t, " component of " + c.hisHer() + " personality");
            }
            if (c.morality > 33) {
                if (c.gender.equals("male")) {
                    w.append(t, ", so " + c.heShe() + " wouldn't normally engage in 'immoral' activities like violence and sex with other men.  ");
                }
                else {
                    w.append(t, ", so " + c.heShe() + " wouldn't normally engage in 'immoral' activities like violence and promiscuity.  ");
                }
                if (c.vVirg) {
                    w.append(t, "If this Vulnerability is broken, it means " + c.heShe() + " has already been raped by the time the game starts.");
                }
                else {
                    w.append(t, "However, " + c.heShe() + " is set to have already been raped before the game starts.");
                }
            }
            else {
                w.append(t, "Morality is only a ");
                w.redAppend(t, "Minor");
                w.append(t, " concern for " + c.givenName + ", ");
                if (c.vVirg) {
                    if (c.gender.equals("male")) {
                        w.append(t, "but " + c.heShe() + " hasn't gotten curious enough to try having sex with other men yet.  Break this Vulnerability to change that.");
                    }
                    else {
                        w.append(t, "but " + c.heShe() + " hasn't gotten around to having sex yet.  Break this Vulnerability to change that.");
                    }
                }
                else if (c.gender.equals("male")) {
                    w.append(t, "so " + c.heShe() + " has seen no reason to avoid having sex with other men.  Restore this Vulnerability to have " + c.himHer() + " start as an anal virgin instead.");
                }
                else {
                    w.append(t, "so " + c.heShe() + " has seen no reason to avoid having sex.  Restore this Vulnerability to have " + c.himHer() + " start as a virgin instead.");
                }
            }
        }
        else if (progress == 1) {
            if (c.innocence > 66) {
                w.append(t, String.valueOf(c.givenName) + "'s Innocence is a ");
                w.blueAppend(t, "Core");
                w.append(t, " part of " + c.hisHer() + " identity");
            }
            else if (c.innocence > 33) {
                w.append(t, String.valueOf(c.givenName) + " has retained ");
                w.greenAppend(t, "Significant");
                w.append(t, " Innocence regarding sexual matters");
            }
            if (c.innocence > 33) {
                w.append(t, ", so " + c.heShe() + " wouldn't normally have any idea how good it can feel to be forced to cum during battle.  ");
                if (c.cVirg) {
                    w.append(t, "If this Vulnerability is broken, it means " + c.heShe() + " has already become addicted to this feeling.");
                }
                else {
                    w.append(t, "However, " + c.heShe() + " is set to have already become addicted to this feeling when the game starts.");
                }
            }
            else {
                w.append(t, String.valueOf(c.givenName) + " has retained only a ");
                w.redAppend(t, "Minor");
                w.append(t, " amount of Innocence");
                if (c.cVirg) {
                    w.append(t, ", but " + c.heShe() + "'s still sane enough to hold back from cumming during battle.  Break this Vulnerability to change that.");
                }
                else {
                    w.append(t, ", so " + c.heShe() + " happily allows " + c.himHer() + "self to cum during battle.  Restore this Vulnerability to have " + c.himHer() + " start out with some restraint.");
                }
            }
        }
        else if (progress == 2) {
            if (c.confidence > 66) {
                w.append(t, String.valueOf(c.givenName) + "'s Confidence is a ");
                w.blueAppend(t, "Core");
                w.append(t, " part of " + c.hisHer() + " identity");
            }
            else if (c.confidence > 33) {
                w.append(t, String.valueOf(c.givenName) + " has a ");
                w.greenAppend(t, "Significant");
                w.append(t, " amount of Confidence");
            }
            if (c.confidence > 33) {
                w.append(t, " because of " + c.hisHer() + " past victories against the Demons.  ");
                if (c.aVirg) {
                    w.append(t, "If this Vulnerability is broken, it means " + c.heShe() + " has suffered a crushing defeat and been tortured before.");
                }
                else {
                    w.append(t, "However, " + c.heShe() + " is set to have already had " + c.hisHer() + " self-image shaken by being defeated and tortured recently.");
                }
            }
            else {
                w.append(t, String.valueOf(c.givenName) + " has only a ");
                w.redAppend(t, "Minor");
                w.append(t, " amount of Confidence left");
                if (c.aVirg) {
                    w.append(t, ", but this is due purely to " + c.hisHer() + " weak personality.  Break this Vulnerability to have " + c.hisHer() + " self-esteem damaged by a recent capture and torture.");
                }
                else {
                    w.append(t, ", largely because of " + c.hisHer() + " recent defeat and torture at the hands of the Demons.  Restore this Vulnerability to erase this event and let " + c.himHer() + " start out with at least a little bit of hope.");
                }
            }
        }
        else if (progress == 3) {
            if (c.dignity > 66) {
                w.append(t, String.valueOf(c.givenName) + "'s need for Dignity is a ");
                w.blueAppend(t, "Core");
                w.append(t, " part of " + c.hisHer() + " identity");
            }
            else if (c.dignity > 33) {
                w.append(t, String.valueOf(c.givenName) + " maintains a ");
                w.greenAppend(t, "Significant");
                w.append(t, " amount of Dignity");
            }
            if (c.dignity > 33) {
                w.append(t, ", wanting to be viewed as a mighty, unassailable warrior.  ");
                if (c.modest) {
                    w.append(t, "If this Vulnerability is broken, it means " + c.heShe() + " has been stripped and had " + c.hisHer() + " humiliation broadcast to the world.");
                }
                else {
                    w.append(t, "However, " + c.heShe() + " is set to have already had " + c.hisHer() + " public image damaged by being stripped during battle and having the footage broadcast to the world.");
                }
            }
            else {
                w.append(t, String.valueOf(c.givenName) + " has only a ");
                w.redAppend(t, "Minor");
                w.append(t, " interest in retaining " + c.hisHer() + " dignity");
                if (c.modest) {
                    w.append(t, ", but " + c.heShe() + " has managed to avoid any public humiliation so far, mostly through pure luck.  Break this Vulnerability to have footage of " + c.hisHer() + " defeat and stripping be broadcast to the world.");
                }
                else {
                    w.append(t, ", and as a result, " + c.heShe() + " hasn't managed to stop footage of " + c.hisHer() + " defeat and stripping from being spread across the world.  Restore this Vulnerability to make it so that " + c.heShe() + " hasn't yet been captured in such a shameful state.");
                }
            }
        }
        if ((progress == 0 && c.vVirg) || (progress == 1 && c.cVirg) || (progress == 2 && c.aVirg) || (progress == 3 && c.modest)) {
            final JButton Break = new JButton("Break");
            Break.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (progress == 0) {
                        c.vVirg = false;
                        c.ruthless = true;
                        c.vTaker = 0;
                        c.vStart = false;
                    }
                    else if (progress == 1) {
                        c.cVirg = false;
                        c.lustful = true;
                        c.cTaker = 0;
                        c.cStart = false;
                    }
                    else if (progress == 2) {
                        c.aVirg = false;
                        c.meek = true;
                        c.aTaker = 0;
                        c.aStart = false;
                    }
                    else {
                        c.modest = false;
                        c.debased = true;
                        c.mTaker = 0;
                        c.mStart = false;
                    }
                    if (progress < 3) {
                        Project.SingleVulnerabilities(t, p, f, w, c, answers, progress + 1);
                    }
                    else {
                        Project.SingleCosmetics(t, p, f, w, c, answers);
                    }
                }
            });
            p.add(Break);
        }
        else {
            final JButton Restore = new JButton("Restore");
            Restore.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (progress == 0) {
                        c.vVirg = true;
                        c.ruthless = false;
                        c.vTaker = -1;
                        c.vStart = true;
                    }
                    else if (progress == 1) {
                        c.cVirg = true;
                        c.lustful = false;
                        c.cTaker = -1;
                        c.cStart = true;
                    }
                    else if (progress == 2) {
                        c.aVirg = true;
                        c.meek = false;
                        c.aTaker = -1;
                        c.aStart = true;
                    }
                    else {
                        c.modest = true;
                        c.debased = false;
                        c.mTaker = -1;
                        c.mStart = true;
                    }
                    if (progress < 3) {
                        Project.SingleVulnerabilities(t, p, f, w, c, answers, progress + 1);
                    }
                    else {
                        Project.SingleCosmetics(t, p, f, w, c, answers);
                    }
                }
            });
            p.add(Restore);
        }
        final JButton Continue = new JButton("Continue");
        Continue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (progress < 3) {
                    Project.SingleVulnerabilities(t, p, f, w, c, answers, progress + 1);
                }
                else {
                    Project.SingleCosmetics(t, p, f, w, c, answers);
                }
            }
        });
        p.add(Continue);
        p.validate();
        p.repaint();
    }
    
    public static void ReportCustomInclusion(final JTextPane t, final WorldState w, final Boolean[] enabled) {
        w.append(t, "\n\nCustom Chosen:");
        for (int i = 0; i < w.save.customRoster.length; ++i) {
            if (enabled[i]) {
                w.append(t, "\n" + w.save.customRoster[i].mainName + ": ");
                w.greenAppend(t, "INCLUDED");
            }
            else {
                w.grayAppend(t, "\n" + w.save.customRoster[i].mainName + ": ");
                w.redAppend(t, "EXCLUDED");
            }
        }
    }
    
    public static void CampaignCustomToggle(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Boolean[] enabled, final int page, final Boolean deleting) {
        p.removeAll();
        clearPortraits();
        final String[] shownNames = new String[5];
        if (page > 0) {
            final JButton Previous = new JButton("<");
            Previous.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.CampaignCustomToggle(t, p, f, w, enabled, page - 1, deleting);
                }
            });
            p.add(Previous);
        }
        for (int i = 0; i < 5; ++i) {
            if (i + page * 5 < enabled.length) {
                final int index = i + page * 5;
                shownNames[i] = w.save.customRoster[index].mainName;
                changePortrait(w.save.customRoster[index].convertGender(), w.save.customRoster[index].type, false, false, w, shownNames, i, Emotion.NEUTRAL, Emotion.NEUTRAL);
                final JButton ThisOne = new JButton(w.save.customRoster[i + page * 5].mainName);
                ThisOne.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (deleting) {
                            p.removeAll();
                            w.append(t, "\n\n" + w.getSeparator() + "\n\nReally delete " + w.save.customRoster[index].mainName + "?");
                            final JButton Delete = new JButton("Delete");
                            Delete.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    final Boolean[] newEnabled = new Boolean[enabled.length - 1];
                                    final Chosen[] newRoster = new Chosen[w.save.customRoster.length - 1];
                                    for (int j = 0; j < newRoster.length; ++j) {
                                        if (j < index) {
                                            newEnabled[j] = enabled[j];
                                            newRoster[j] = w.save.customRoster[j];
                                        }
                                        else {
                                            newEnabled[j] = enabled[j + 1];
                                            newRoster[j] = w.save.customRoster[j + 1];
                                        }
                                    }
                                    w.save.customRoster = newRoster;
                                    if (index == newEnabled.length && newEnabled.length % 5 == 0 && page != 0) {
                                        Project.CampaignCustomToggle(t, p, f, w, newEnabled, page - 1, true);
                                    }
                                    else {
                                        Project.CampaignCustomToggle(t, p, f, w, newEnabled, page, true);
                                    }
                                    w.append(t, "\n\n" + w.getSeparator());
                                    Project.ReportCustomInclusion(t, w, newEnabled);
                                }
                            });
                            p.add(Delete);
                            final JButton Back = new JButton("Back");
                            Back.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    Project.CampaignCustomToggle(t, p, f, w, enabled, page, true);
                                }
                            });
                            p.add(Back);
                            p.validate();
                            p.repaint();
                        }
                        else {
                            enabled[index] = !enabled[index];
                            w.append(t, "\n\n" + w.getSeparator());
                            Project.ReportCustomInclusion(t, w, enabled);
                        }
                    }
                });
                p.add(ThisOne);
            }
        }
        if ((page + 1) * 5 < enabled.length) {
            final JButton Next = new JButton(">");
            Next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.CampaignCustomToggle(t, p, f, w, enabled, page + 1, deleting);
                }
            });
            p.add(Next);
        }
        final JButton Done = new JButton("Done");
        Done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.CampaignMenu(t, p, f, w, enabled);
            }
        });
        p.add(Done);
        p.validate();
        p.repaint();
        t.setCaretPosition(t.getDocument().getLength() - 1);
    }
    
    public static void OptionsDisplay(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Boolean earlyCheatVisible) {
        t.setText("");
        if (earlyCheatVisible) {
            w.append(t, "Difficulty: ");
            if (w.getEarlyCheat()) {
                w.append(t, "EASY (cheats available from the start)");
            }
            else if (w.hardMode) {
                w.append(t, "HARD (shorter deadlines, Chosen take less damage as damage level goes up, cannot use Forsaken)");
            }
            else {
                w.append(t, "NORMAL");
            }
            w.append(t, "\n\n");
        }
        w.append(t, "Current background: ");
        if (t.getBackground().equals(Color.WHITE)) {
            w.append(t, "white");
        }
        else {
            w.append(t, "black");
        }
        w.append(t, "\n\nCommentary mode: ");
        if (w.getCommentaryRead()) {
            if (w.getCommentaryWrite()) {
                w.append(t, "Read/Write");
            }
            else {
                w.append(t, "Read");
            }
        }
        else if (w.getCommentaryWrite()) {
            w.append(t, "Write");
        }
        else {
            w.append(t, "None");
        }
        w.append(t, "\n\nText size: " + w.getTextSize());
        w.append(t, "\n\nEnemy composition: ");
        if (w.getGenderBalance()[0] == 0) {
            Boolean listed = false;
            if (w.getGenderBalance()[1] > 0) {
                listed = true;
                w.append(t, String.valueOf(w.getGenderBalance()[1]) + " female");
                if (w.getGenderBalance()[1] > 1) {
                    w.append(t, "s");
                }
            }
            if (w.getGenderBalance()[2] > 0) {
                if (listed) {
                    w.append(t, ", ");
                }
                w.append(t, String.valueOf(w.getGenderBalance()[2]) + " male");
                if (w.getGenderBalance()[2] > 1) {
                    w.append(t, "s");
                }
                listed = true;
            }
            if (w.getGenderBalance()[3] > 0) {
                if (listed) {
                    w.append(t, ", ");
                }
                w.append(t, String.valueOf(w.getGenderBalance()[3]) + " futanari");
                listed = true;
            }
            if (!listed) {
                w.append(t, "none set");
            }
        }
        else {
            Boolean listed = false;
            int divisor = w.getGenderBalance()[1] + w.getGenderBalance()[2] + w.getGenderBalance()[3];
            if (divisor == 0) {
                divisor = 1;
            }
            int count = 0;
            for (int i = 1; i < 4; ++i) {
                if (w.getGenderBalance()[i] > 0) {
                    ++count;
                }
            }
            final int multiplier = 10000 / divisor;
            if (w.getGenderBalance()[1] > 0) {
                listed = true;
                if (count > 1) {
                    w.append(t, String.valueOf(multiplier * w.getGenderBalance()[1] / 100) + "% female");
                }
                else {
                    w.append(t, "100% female");
                }
            }
            if (w.getGenderBalance()[2] > 0) {
                if (listed) {
                    w.append(t, ", ");
                }
                if (count > 1) {
                    w.append(t, String.valueOf(multiplier * w.getGenderBalance()[2] / 100) + "% male");
                }
                else {
                    w.append(t, "100% male");
                }
                listed = true;
            }
            if (w.getGenderBalance()[3] > 0) {
                if (listed) {
                    w.append(t, ", ");
                }
                if (count > 1) {
                    w.append(t, String.valueOf(multiplier * w.getGenderBalance()[3] / 100) + "% futanari");
                }
                else {
                    w.append(t, "100% futanari");
                }
            }
        }
        if (w.getGenderBalance()[2] > 0) {
            w.append(t, "\n\nMales shift: ");
            if (w.getMaleShift() == 0) {
                w.append(t, "never");
            }
            else if (w.getMaleShift() == 1) {
                w.append(t, "to female when first inseminated");
            }
            else if (w.getMaleShift() == 2) {
                w.append(t, "to futanari when first inseminated");
            }
        }
        if (w.getGenderBalance()[1] > 0 || (w.getGenderBalance()[2] > 0 && w.getMaleShift() == 1)) {
            w.append(t, "\n\nFemales shift: ");
            if (w.getFemaleShift() == 0) {
                w.append(t, "never");
            }
            else {
                w.append(t, "to futanari when first using Fantasize");
            }
        }
        if (w.getMaleShift() > 0 || w.getFemaleShift() > 0) {
            w.append(t, "\n\nShifted Chosen can shift again: ");
            if (w.getRepeatShift()) {
                w.append(t, "yes");
            }
            else {
                w.append(t, "no");
            }
        }
        w.append(t, "\n\nGraphic violence: ");
        if (w.tickle()) {
            w.append(t, "OFF (replaced by tickling)");
        }
        else {
            w.append(t, "ON");
        }
        w.append(t, "\n\nPortraits: ");
        if (w.portraits) {
            w.append(t, "ON");
        }
        else {
            w.append(t, "OFF");
        }
        w.append(t, "\n\nPassage separator:\n" + w.getSeparator());
    }
    
    public static void OptionsMenu(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, Boolean earlyCheatVisible) {
        p.removeAll();
        if (earlyCheatVisible == null) {
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
            final File saveLocation = new File(String.valueOf(path) + File.separator + "saves.sav");
            SaveData saves = null;
            if (saveLocation.exists()) {
                final ReadObject robj = new ReadObject();
                saves = robj.deserializeSaveData(String.valueOf(path) + File.separator + "saves.sav");
            }
            else {
                saves = new SaveData();
            }
            final SaveData saveFile = saves;
            for (int j = 0; j < saveFile.getSaves().length; ++j) {
                if (saveFile.getSaves()[j].getDay() > 50 - saveFile.getSaves()[j].eventOffset * 3) {
                    earlyCheatVisible = true;
                }
            }
            if (w.getEarlyCheat()) {
                earlyCheatVisible = true;
            }
            if (w.hardMode) {
                earlyCheatVisible = true;
            }
            if (saves.harem != null && saves.harem.length > 0) {
                earlyCheatVisible = true;
            }
            if (earlyCheatVisible == null) {
                earlyCheatVisible = false;
            }
        }
        final Boolean CheatVisibility = earlyCheatVisible;
        OptionsDisplay(t, p, f, w, earlyCheatVisible);
        final JButton EarlyCheat = new JButton("Change Difficulty");
        EarlyCheat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.getEarlyCheat()) {
                    w.setEarlyCheat(false);
                    w.hardMode = true;
                    w.clampStart = 1;
                    w.clampPercent = 80;
                    w.eventOffset = 5;
                }
                else if (w.hardMode) {
                    w.hardMode = false;
                    w.clampStart = 11;
                    w.clampPercent = 100;
                    w.eventOffset = 0;
                }
                else {
                    w.setEarlyCheat(true);
                }
                Project.OptionsMenu(t, p, f, w, null);
            }
        });
        if (earlyCheatVisible) {
            p.add(EarlyCheat);
        }
        class EarlyCheatAction extends AbstractAction
        {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.setEarlyCheat(true);
                Project.OptionsMenu(t, p, f, w, true);
            }
        }
        final Action EarlyCheatAssignment = new EarlyCheatAction();
        p.getInputMap(2).put(KeyStroke.getKeyStroke(67, 0), "pressed");
        p.getActionMap().put("pressed", EarlyCheatAssignment);
        final JButton Invert = new JButton("Change Background");
        Invert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.toggleColors(t);
                Project.OptionsMenu(t, p, f, w, CheatVisibility);
            }
        });
        p.add(Invert);
        final JButton Commentary = new JButton("Change Commentary Mode");
        Commentary.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.getCommentaryRead()) {
                    if (w.getCommentaryWrite()) {
                        w.setCommentaryWrite(false);
                    }
                    else {
                        w.setCommentaryRead(false);
                        w.setCommentaryWrite(true);
                    }
                }
                else if (w.getCommentaryWrite()) {
                    w.setCommentaryWrite(false);
                }
                else {
                    w.setCommentaryRead(true);
                    w.setCommentaryWrite(true);
                }
                Project.OptionsMenu(t, p, f, w, CheatVisibility);
            }
        });
        p.add(Commentary);
        final JButton TextSize = new JButton("Change Text Size");
        TextSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.switchTextSize();
                Project.OptionsMenu(t, p, f, w, CheatVisibility);
            }
        });
        p.add(TextSize);
        final JButton Content = new JButton("Content Options");
        Content.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.ContentMenu(t, p, f, w, CheatVisibility);
            }
        });
        p.add(Content);
        final JButton Portraits = new JButton("Toggle Portraits");
        Portraits.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.portraits = !w.portraits;
                Project.OptionsMenu(t, p, f, w, CheatVisibility);
            }
        });
        p.add(Portraits);
        final JButton ChangeSeparator = new JButton("Change Separator");
        ChangeSeparator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String input = JOptionPane.showInputDialog("Enter the text that will be used to separate passages.  Leave blank to use the default, '---'.");
                if (input == null) {
                    w.setSeparator("---");
                }
                else if (input.length() == 0) {
                    w.setSeparator("---");
                }
                else {
                    w.setSeparator(input);
                }
                Project.OptionsMenu(t, p, f, w, CheatVisibility);
            }
        });
        p.add(ChangeSeparator);
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                t.setText("");
                Project.IntroOne(t, p, f, w);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void GenderMenu(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Boolean earlyCheatVisible) {
        p.removeAll();
        p.getInputMap().clear();
        p.getActionMap().clear();
        OptionsDisplay(t, p, f, w, earlyCheatVisible);
        final JButton ToggleRandomness = new JButton("Randomize Composition");
        if (w.getGenderBalance()[0] == 1) {
            ToggleRandomness.setText("Fix Composition");
        }
        ToggleRandomness.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.toggleGenderRandomness();
                Project.GenderMenu(t, p, f, w, earlyCheatVisible);
            }
        });
        p.add(ToggleRandomness);
        final JButton FewerFemales = new JButton("Fewer Females");
        if (w.getGenderBalance()[1] > 0 && (w.getGenderBalance()[0] == 0 || w.getGenderBalance()[2] > 0 || w.getGenderBalance()[3] > 0)) {
            FewerFemales.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.decreaseGender(1);
                    Project.GenderMenu(t, p, f, w, earlyCheatVisible);
                }
            });
        }
        else {
            FewerFemales.setForeground(Color.GRAY);
        }
        p.add(FewerFemales);
        final JButton MoreFemales = new JButton("More Females");
        if ((w.getGenderBalance()[0] == 1 && (w.getGenderBalance()[2] > 0 || w.getGenderBalance()[3] > 0)) || (w.getGenderBalance()[0] == 0 && w.getGenderBalance()[1] + w.getGenderBalance()[2] + w.getGenderBalance()[3] < 3)) {
            MoreFemales.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.increaseGender(1);
                    Project.GenderMenu(t, p, f, w, earlyCheatVisible);
                }
            });
        }
        else {
            MoreFemales.setForeground(Color.GRAY);
        }
        p.add(MoreFemales);
        final JButton FewerMales = new JButton("Fewer Males");
        if (w.getGenderBalance()[2] > 0 && (w.getGenderBalance()[0] == 0 || w.getGenderBalance()[1] > 0 || w.getGenderBalance()[3] > 0)) {
            FewerMales.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.decreaseGender(2);
                    Project.GenderMenu(t, p, f, w, earlyCheatVisible);
                }
            });
        }
        else {
            FewerMales.setForeground(Color.GRAY);
        }
        p.add(FewerMales);
        final JButton MoreMales = new JButton("More Males");
        if ((w.getGenderBalance()[0] == 1 && (w.getGenderBalance()[1] > 0 || w.getGenderBalance()[3] > 0)) || (w.getGenderBalance()[0] == 0 && w.getGenderBalance()[1] + w.getGenderBalance()[2] + w.getGenderBalance()[3] < 3)) {
            MoreMales.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.increaseGender(2);
                    Project.GenderMenu(t, p, f, w, earlyCheatVisible);
                }
            });
        }
        else {
            MoreMales.setForeground(Color.GRAY);
        }
        p.add(MoreMales);
        final JButton FewerFuta = new JButton("Fewer Futanari");
        if (w.getGenderBalance()[3] > 0 && (w.getGenderBalance()[0] == 0 || w.getGenderBalance()[1] > 0 || w.getGenderBalance()[2] > 0)) {
            FewerFuta.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.decreaseGender(3);
                    Project.GenderMenu(t, p, f, w, earlyCheatVisible);
                }
            });
        }
        else {
            FewerFuta.setForeground(Color.GRAY);
        }
        p.add(FewerFuta);
        final JButton MoreFuta = new JButton("More Futanari");
        if ((w.getGenderBalance()[0] == 1 && (w.getGenderBalance()[2] > 0 || w.getGenderBalance()[1] > 0)) || (w.getGenderBalance()[0] == 0 && w.getGenderBalance()[1] + w.getGenderBalance()[2] + w.getGenderBalance()[3] < 3)) {
            MoreFuta.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.increaseGender(3);
                    Project.GenderMenu(t, p, f, w, earlyCheatVisible);
                }
            });
        }
        else {
            MoreFuta.setForeground(Color.GRAY);
        }
        p.add(MoreFuta);
        final JButton Back = new JButton("Back");
        if (w.getGenderBalance()[0] == 1 || w.getGenderBalance()[1] + w.getGenderBalance()[2] + w.getGenderBalance()[3] == 3) {
            Back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.ContentMenu(t, p, f, w, earlyCheatVisible);
                }
            });
        }
        else {
            Back.setForeground(Color.GRAY);
        }
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void ContentMenu(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Boolean earlyCheatVisible) {
        p.removeAll();
        p.getInputMap().clear();
        p.getActionMap().clear();
        OptionsDisplay(t, p, f, w, earlyCheatVisible);
        final JButton Violence = new JButton("Toggle Violence");
        Violence.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.toggleTickle();
                Project.ContentMenu(t, p, f, w, earlyCheatVisible);
            }
        });
        p.add(Violence);
        final JButton Genders = new JButton("Change Composition");
        Genders.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.GenderMenu(t, p, f, w, earlyCheatVisible);
            }
        });
        p.add(Genders);
        if (w.getGenderBalance()[2] > 0) {
            final JButton MaleShift = new JButton("Toggle Male Shifting");
            MaleShift.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.changeMaleShift();
                    Project.ContentMenu(t, p, f, w, earlyCheatVisible);
                }
            });
            p.add(MaleShift);
        }
        if (w.getGenderBalance()[1] > 0 || (w.getGenderBalance()[2] > 0 && w.getMaleShift() == 1)) {
            final JButton FemaleShift = new JButton("Toggle Female Shifting");
            FemaleShift.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.changeFemaleShift();
                    Project.ContentMenu(t, p, f, w, earlyCheatVisible);
                }
            });
            p.add(FemaleShift);
        }
        if (w.getMaleShift() > 0 || w.getFemaleShift() > 0) {
            final JButton RepeatShift = new JButton("Toggle Repeated Shifting");
            RepeatShift.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.changeRepeatShift();
                    Project.ContentMenu(t, p, f, w, earlyCheatVisible);
                }
            });
            p.add(RepeatShift);
        }
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.OptionsMenu(t, p, f, w, earlyCheatVisible);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void ForsakenMenu(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final SaveData s, final int page) {
        p.removeAll();
        if (page == 0) {
            if (w.active) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                if (w.usedForsaken != null) {
                    w.append(t, String.valueOf(w.usedForsaken.mainName) + " is currently prepared to lead your forces into battle.  ");
                }
                if (w.loopComplete) {
                    if (w.day <= 50 - 3 * w.eventOffset) {
                        w.append(t, "Select one of the Forsaken to see more information and management options, or select 'Pass Time' to move to the next day without doing any training.");
                    }
                    else {
                        w.append(t, "Select one of the Forsaken to see more information and management options.");
                    }
                }
                else {
                    w.append(t, "Select one of the Forsaken to see more information and management options, or select 'Deploy' to use one as your Commander.");
                }
            }
            else {
                w.append(t, "\n\n" + w.getSeparator() + "\n\nWith which of your Forsaken would you like to interact?");
            }
        }
        else {
            clearPortraits();
            final JButton PreviousPage = new JButton("<");
            PreviousPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.ForsakenMenu(t, p, f, w, s, page - 1);
                }
            });
            p.add(PreviousPage);
        }
        clearPortraits();
        final String[] nameDisplay = new String[5];
        for (int i = 0; i < 5; ++i) {
            if (w.getHarem() != null && w.getHarem().length > i + page * 5) {
                final Forsaken subject = w.getHarem()[i + page * 5];
                subject.textSize = w.textSize;
                final JButton ThisOne = new JButton(subject.mainName);
                ThisOne.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.ForsakenInteraction(t, p, f, w, s, subject);
                    }
                });
                p.add(ThisOne);
                nameDisplay[i] = subject.mainName;
                if (subject.flavorObedience() < 20) {
                    changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Emotion.ANGER, Emotion.NEUTRAL);
                }
                else if (subject.flavorObedience() < 40) {
                    changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Emotion.ANGER, Emotion.SHAME);
                }
                else if (subject.flavorObedience() < 61) {
                    changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Emotion.FEAR, Emotion.SHAME);
                }
                else if (subject.flavorObedience() < 81) {
                    changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Emotion.FOCUS, Emotion.NEUTRAL);
                }
                else {
                    changePortrait(subject.gender, subject.type, true, true, w, nameDisplay, i, Emotion.JOY, Emotion.FOCUS);
                }
            }
        }
        if (w.getHarem().length > 5 * (page + 1)) {
            final JButton NextPage = new JButton(">");
            NextPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.ForsakenMenu(t, p, f, w, s, page + 1);
                }
            });
            p.add(NextPage);
        }
        final JButton NewForsaken = new JButton("(Generate Forsaken)");
        NewForsaken.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final WriteObject wobj = new WriteObject();
                final WorldState dummy = new WorldState();
                dummy.copyToggles(w);
                dummy.copySettings(t, w);
                dummy.setGenders(w.getGenderBalance());
                final Chosen newChosen = new Chosen();
                newChosen.setNumber(0);
                dummy.initialize();
                newChosen.generate(dummy);
                w.corruptColors(newChosen);
                int index = 0;
                if (s.harem == null) {
                    s.harem = new Forsaken[1];
                }
                else {
                    index = s.harem.length;
                }
                final int lastPage = s.harem.length / 5;
                final Forsaken[] newHarem = new Forsaken[index + 1];
                for (int j = 0; j < index; ++j) {
                    newHarem[j] = s.harem[j];
                }
                final Forsaken newForsaken = new Forsaken();
                newForsaken.initialize(w, newChosen);
                newHarem[index] = newForsaken;
                newForsaken.forsakenID = s.assignID();
                s.harem = newHarem;
                wobj.serializeSaveData(s);
                Project.ForsakenMenu(t, p, f, w, s, lastPage);
            }
        });
        if (!w.campaign) {
            p.add(NewForsaken);
        }
        final JButton PassTime = new JButton("Pass Time");
        PassTime.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.loopComplete) {
                    p.removeAll();
                    w.append(t, "\n\n" + w.getSeparator() + "\n\nDo you want to give your Forsaken a break and just move on to the next day?");
                    final JButton Confirm = new JButton("Confirm");
                    Confirm.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            Project.PostBattle(t, p, f, w);
                        }
                    });
                    p.add(Confirm);
                    final JButton Cancel = new JButton("Cancel");
                    Cancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            Project.ForsakenMenu(t, p, f, w, s, page);
                        }
                    });
                    p.add(Cancel);
                    p.validate();
                    p.repaint();
                }
                else {
                    Project.ForsakenChoice(t, p, f, w, s, 0);
                }
            }
        });
        if (w.active && !w.loopComplete) {
            PassTime.setText("Deploy");
        }
        if (!w.campaign || w.day <= 50 - w.eventOffset * 3) {
            p.add(PassTime);
        }
        if (w.active && !w.loopComplete) {
            final JButton UseDemon = new JButton("Use Demon");
            UseDemon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (w.usedForsaken != null) {
                        final WorldState val$w = w;
                        val$w.evilEnergy += w.usedForsaken.EECost();
                    }
                    w.usedForsaken = null;
                    Project.Customize(t, p, f, w);
                }
            });
            p.add(UseDemon);
        }
        final JButton Back = new JButton("Done");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.active) {
                    Project.Shop(t, p, f, w);
                }
                else {
                    t.setText("");
                    Project.IntroOne(t, p, f, w);
                }
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void ForsakenChoice(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final SaveData s, final int page) {
        p.removeAll();
        if (page == 0) {
            if (w.active) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich of the Forsaken would you like to send into battle?");
            }
            else {
                w.append(t, "\n\n" + w.getSeparator() + "\n\nYou can spend one of the Forsaken's Stamina and Motivation (as would normally happen when sending them into battle), or you can simply pass time without spending anything by selecting 'None'.");
            }
        }
        else {
            final JButton PreviousPage = new JButton("<");
            PreviousPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.append(t, "\n\n" + w.getSeparator());
                    Project.ForsakenChoice(t, p, f, w, s, page - 1);
                }
            });
            p.add(PreviousPage);
        }
        clearPortraits();
        final String[] nameDisplay = new String[5];
        for (int i = page * 5; i < page * 5 + 5; ++i) {
            if (i < w.getHarem().length) {
                int actualCost = w.getHarem()[i].motivationCost();
                if (w.getHarem()[i].isFormerFriend(w.getCast()[0]) || w.getHarem()[i].isFormerFriend(w.getCast()[1]) || w.getHarem()[i].isFormerFriend(w.getCast()[2])) {
                    actualCost *= 2;
                }
                w.append(t, "\n\n" + w.getHarem()[i].mainName + "\nStamina: " + w.getHarem()[i].stamina / 10 + "." + w.getHarem()[i].stamina % 10 + "%\nMotivation: " + w.getHarem()[i].motivation / 10 + "." + w.getHarem()[i].motivation % 10 + "%\nCost: 20% Stamina, " + actualCost / 10 + "." + actualCost % 10 + "% Motivation, " + w.getHarem()[i].EECost() + " EE\n" + w.getHarem()[i].describeCombatStyle(w, false) + "\nReputation Strength: " + (200 - w.getHarem()[i].disgrace * 2) + "%\nTarget Compatibilities:");
                if (w.active) {
                    for (int j = 0; j < 3; ++j) {
                        if (w.getCast()[j] != null) {
                            w.append(t, "\n" + w.getCast()[j].getMainName() + " - ");
                            final int compatibility = w.getHarem()[i].compatibility(w.getCast()[j]);
                            if (w.getHarem()[i].knowsPersonally(w.getCast()[j])) {
                                w.append(t, "Personal (8 rounds, +25% damage)");
                            }
                            else if (compatibility >= 8) {
                                w.append(t, "Excellent (8 rounds)");
                            }
                            else if (compatibility == 7) {
                                w.append(t, "Good (7 rounds)");
                            }
                            else if (compatibility == 6) {
                                w.append(t, "Average (6 rounds)");
                            }
                            else if (compatibility == 5) {
                                w.append(t, "Poor (5 rounds)");
                            }
                            else {
                                w.append(t, "Terrible (4 rounds)");
                            }
                        }
                    }
                }
                else {
                    w.append(t, "N/A");
                }
                final JButton Choice = new JButton(w.getHarem()[i].mainName);
                final Forsaken Spent = w.getHarem()[i];
                final int index = i;
                Choice.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (w.active) {
                            if (w.usedForsaken != null) {
                                final WorldState val$w = w;
                                val$w.evilEnergy += w.usedForsaken.EECost();
                            }
                            final WorldState val$w2 = w;
                            val$w2.evilEnergy -= Spent.EECost();
                            w.usedForsaken = Spent;
                            w.usedForsakenIndex = index;
                            Project.ForsakenMenu(t, p, f, w, s, 0);
                        }
                        else {
                            p.removeAll();
                            final Forsaken val$Spent = Spent;
                            val$Spent.stamina -= 200;
                            final Forsaken val$Spent2 = Spent;
                            val$Spent2.motivation -= Spent.motivationCost();
                            Project.ForsakenDowntime(t, p, f, w, s, new Forsaken[] { Spent });
                            final JButton Continue = new JButton("Continue");
                            Continue.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    Project.ForsakenMenu(t, p, f, w, s, 0);
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
                if (Spent.stamina >= 200 && Spent.motivation >= actualCost && (!w.active || EEAvailable >= Spent.EECost())) {
                    p.add(Choice);
                }
                nameDisplay[i - page * 5] = Spent.mainName;
                if (Spent.flavorObedience() < 20) {
                    changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i - page * 5, Emotion.ANGER, Emotion.NEUTRAL);
                }
                else if (Spent.flavorObedience() < 40) {
                    changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i - page * 5, Emotion.ANGER, Emotion.SHAME);
                }
                else if (Spent.flavorObedience() < 61) {
                    changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i - page * 5, Emotion.FEAR, Emotion.SHAME);
                }
                else if (Spent.flavorObedience() < 81) {
                    changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i - page * 5, Emotion.FOCUS, Emotion.NEUTRAL);
                }
                else {
                    changePortrait(Spent.gender, Spent.type, true, true, w, nameDisplay, i - page * 5, Emotion.JOY, Emotion.FOCUS);
                }
            }
        }
        if (w.getHarem().length > (page + 1) * 5) {
            final JButton NextPage = new JButton(">");
            NextPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.append(t, "\n\n" + w.getSeparator());
                    Project.ForsakenChoice(t, p, f, w, s, page + 1);
                }
            });
            p.add(NextPage);
        }
        final JButton None = new JButton("None");
        None.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                Project.ForsakenDowntime(t, p, f, w, s, null);
                final JButton Continue = new JButton("Continue");
                Continue.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.ForsakenMenu(t, p, f, w, s, 0);
                    }
                });
                p.add(Continue);
                p.validate();
                p.repaint();
            }
        });
        if (!w.active) {
            p.add(None);
        }
        final JButton Cancel = new JButton("Cancel");
        Cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.ForsakenMenu(t, p, f, w, s, 0);
            }
        });
        p.add(Cancel);
        p.validate();
        p.repaint();
    }
    
    public static void ForsakenInteraction(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final SaveData s, final Forsaken x) {
        p.removeAll();
        clearPortraits();
        final String[] nameDisplay = new String[5];
        nameDisplay[0] = x.mainName;
        if (x.flavorObedience() < 20) {
            changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Emotion.ANGER, Emotion.NEUTRAL);
        }
        else if (x.flavorObedience() < 40) {
            changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Emotion.ANGER, Emotion.SHAME);
        }
        else if (x.flavorObedience() < 61) {
            changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Emotion.FEAR, Emotion.SHAME);
        }
        else if (x.flavorObedience() < 81) {
            changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Emotion.FOCUS, Emotion.NEUTRAL);
        }
        else {
            changePortrait(x.gender, x.type, true, true, w, nameDisplay, 0, Emotion.JOY, Emotion.FOCUS);
        }
        w.append(t, "\n\n" + w.getSeparator() + "\n\n" + x.mainName);
        if (!x.mainName.equals(x.originalName)) {
            w.append(t, " (formerly known as");
            if (!x.adjectiveName.equals("none")) {
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
            }
            else {
                w.append(t, "\nReal name: ");
            }
            if (x.filthyGaijin) {
                w.append(t, String.valueOf(x.givenName) + " " + x.familyName);
            }
            else {
                w.append(t, String.valueOf(x.familyName) + " " + x.givenName);
            }
        }
        else if (!x.givenName.equals(x.mainName) && !x.givenName.equals(x.originalName)) {
            w.append(t, "\nReal name: " + x.givenName);
        }
        w.append(t, "\n\nStamina: " + x.stamina / 10 + "." + x.stamina % 10 + "%\nMotivation: " + x.motivation / 10 + "." + x.motivation % 10 + "%");
        w.append(t, "\n\nExpertise\nHATE: " + x.condensedFormat(x.hateExp) + " (x" + x.expMultiplierDisplay(x.hateExp) + " dmg)\nPLEA: " + x.condensedFormat(x.pleaExp) + " (x" + x.expMultiplierDisplay(x.pleaExp) + " dmg)");
        if (w.tickleOn) {
            w.append(t, "\nANTI: ");
        }
        else {
            w.append(t, "\nINJU: ");
        }
        w.append(t, String.valueOf(x.condensedFormat(x.injuExp)) + " (x" + x.expMultiplierDisplay(x.injuExp) + " dmg)\nEXPO: " + x.condensedFormat(x.expoExp) + " (x" + x.expMultiplierDisplay(x.expoExp) + " dmg)\n" + x.describeCombatStyle(w, true));
        if (x.defeatType == 5 && x.obedience < 40) {
            w.append(t, "\n\nTrait: Eager Partner\nWhile Obedience remains below 40%, 1/4 Motivation cost to deploy and +50% PLEA and EXPO damage");
        }
        if (x.type == Chosen.Species.SUPERIOR) {
            w.append(t, "\n\nTrait: Superior Forsaken\nx2 Motivation cost to deploy, +50% damage");
        }
        w.append(t, "\n\nOrgasms given: ");
        if (x.orgasmsGiven == 0) {
            w.append(t, "none");
        }
        else {
            w.append(t, new StringBuilder(String.valueOf(x.orgasmsGiven)).toString());
        }
        w.append(t, "\nOrgasms experienced: ");
        if (x.timesOrgasmed == 0) {
            w.append(t, "none");
        }
        else {
            w.append(t, new StringBuilder(String.valueOf(x.timesOrgasmed)).toString());
        }
        w.append(t, "\nLongest continuous orgasm: ");
        if (x.timesOrgasmed == 0) {
            w.append(t, "N/A");
        }
        else if (x.strongestOrgasm < 600) {
            w.append(t, String.valueOf(x.strongestOrgasm / 10) + "." + x.strongestOrgasm % 10 + " seconds");
        }
        else if (x.strongestOrgasm < 36000) {
            w.append(t, String.valueOf(x.strongestOrgasm / 600) + " minutes " + x.strongestOrgasm % 600 / 10 + " seconds");
        }
        else {
            w.append(t, String.valueOf(x.strongestOrgasm / 36000) + " hours " + x.strongestOrgasm % 36000 / 600 + " minutes");
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
        }
        else {
            analCount += x.timesTortured;
        }
        w.append(t, new StringBuilder(String.valueOf(analCount)).toString());
        if (analCount == 0) {
            w.append(t, " (anal virgin)");
        }
        if (x.demonicBirths > 0) {
            w.append(t, "\nDemonic births: " + x.demonicBirths);
        }
        else {
            w.append(t, "\nDemonic births: 0");
        }
        w.append(t, "\n\nPeople injured: " + x.peopleInjured + "\nPeople killed: " + x.timesKilled + "\nSelf-harm incidents: " + x.timesHarmedSelf + "\n\nHostility: " + x.hostility + "% (");
        if (x.hostility < 20) {
            w.append(t, "Optimistic about humanity");
        }
        else if (x.hostility < 40) {
            w.append(t, "Ambivalent about humanity");
        }
        else if (x.hostility < 61) {
            w.append(t, "Pessimistic about humanity");
        }
        else if (x.hostility < 81) {
            w.append(t, "Hateful toward humanity itself");
        }
        else {
            w.append(t, "Desires the destruction of humanity");
        }
        w.append(t, ")\nDeviancy: " + x.deviancy + "% (");
        if (x.deviancy < 20) {
            w.append(t, "Little interest in sexuality");
        }
        else if (x.deviancy < 40) {
            w.append(t, "Elaborate sexual fantasies");
        }
        else if (x.deviancy < 61) {
            w.append(t, "Twisted sexual desires");
        }
        else if (x.deviancy < 81) {
            w.append(t, "Fetishizes aberrant actions");
        }
        else {
            w.append(t, "Seeks sexual pleasure regardless of situation");
        }
        w.append(t, ")\nObedience: " + x.obedience + "% (");
        if (x.defeatType == 5 && x.obedience < 40) {
            w.append(t, "Obeys due to expectation of rewards");
        }
        else if (x.obedience < 20) {
            w.append(t, "Reflexively disobeys");
        }
        else if (x.obedience < 40) {
            w.append(t, "Obeys when convenient");
        }
        else if (x.obedience < 61) {
            w.append(t, "Obeys out of fear");
        }
        else if (x.obedience < 81) {
            w.append(t, "Eagerly obeys");
        }
        else {
            w.append(t, "Unthinkingly obeys");
        }
        w.append(t, ")\nDisgrace: " + x.disgrace + "% (");
        if (x.disgrace < 20) {
            w.append(t, "Still somewhat respected");
        }
        else if (x.disgrace < 40) {
            w.append(t, "Humiliated");
        }
        else if (x.disgrace < 61) {
            w.append(t, "Object of base lust");
        }
        else if (x.disgrace < 81) {
            w.append(t, "Viewed with contempt");
        }
        else {
            w.append(t, "Considered powerless and worthless");
        }
        w.append(t, ")\n\nWhat would you like to speak to " + x.mainName + " about?");
        if (w.active && !w.loopComplete) {
            w.append(t, "  Note that training " + x.himHer() + " will take the entire day.");
        }
        final JButton Self = new JButton("Herself");
        if (x.gender == Forsaken.Gender.MALE) {
            Self.setText("Himself");
        }
        Self.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                x.selfTalk(t);
            }
        });
        p.add(Self);
        final JButton Philosophy = new JButton("Philosophy");
        Philosophy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                x.philosophyTalk(t);
            }
        });
        p.add(Philosophy);
        final JButton Training = new JButton("Training");
        Training.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                x.trainingTalk(t);
            }
        });
        final JButton Life = new JButton("Everyday Life");
        Life.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                x.lifeTalk(t);
            }
        });
        final WriteObject wobj = new WriteObject();
        final JButton Others = new JButton("Other Forsaken");
        Others.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                x.othersTalk(w, t, s);
            }
        });
        if (w.getHarem().length > 1) {
            p.add(Others);
        }
        final JButton ChangeName = new JButton("Change Name");
        ChangeName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String input = JOptionPane.showInputDialog("What alias will you give " + x.himHer() + "?");
                if (input != null && input.length() > 0) {
                    x.mainName = input;
                    wobj.serializeSaveData(s);
                }
                Project.ForsakenInteraction(t, p, f, w, s, x);
            }
        });
        p.add(ChangeName);
        final JButton ChangeTextColor = new JButton("Change Text Color");
        ChangeTextColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                int firstColor = -1;
                int secondColor = -1;
                int thirdColor = -1;
                firstColor = Integer.parseInt(JOptionPane.showInputDialog("Enter a value for red (0-255)."));
                secondColor = Integer.parseInt(JOptionPane.showInputDialog("Enter a value for green (0-255)."));
                thirdColor = Integer.parseInt(JOptionPane.showInputDialog("Enter a value for blue (0-255)."));
                if (firstColor >= 0 && firstColor <= 255 && secondColor >= 0 && secondColor <= 255 && thirdColor >= 0 && thirdColor <= 255) {
                    p.removeAll();
                    final Color firstStorage = x.textColor;
                    final Color secondStorage = x.darkColor;
                    x.textColor = new Color(firstColor, secondColor, thirdColor);
                    x.darkColor = new Color(firstColor, secondColor, thirdColor);
                    w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                    x.say(t, "\"" + x.mainName + " will now talk like this.\"");
                    final JButton Confirm = new JButton("Confirm");
                    Confirm.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            wobj.serializeSaveData(s);
                            Project.ForsakenInteraction(t, p, f, w, s, x);
                        }
                    });
                    p.add(Confirm);
                    final JButton Cancel = new JButton("Cancel");
                    Cancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            x.textColor = firstStorage;
                            x.darkColor = secondStorage;
                            Project.ForsakenInteraction(t, p, f, w, s, x);
                        }
                    });
                    p.add(Cancel);
                    p.validate();
                    p.repaint();
                }
                else {
                    w.append(t, "\n\n" + w.getSeparator() + "\n\nError: one or more invalid values.");
                }
            }
        });
        p.add(ChangeTextColor);
        final JButton FreeTraining = new JButton("Free Training");
        FreeTraining.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final Boolean[] newTraining = new Boolean[18];
                for (int i = 0; i < newTraining.length; ++i) {
                    newTraining[i] = false;
                }
                x.trainingMenu(t, p, f, w, s, newTraining, 0, true);
            }
        });
        if (w.active) {
            FreeTraining.setText("Training");
        }
        if (!w.active || ((w.day != 50 - w.eventOffset * 3 || w.loopComplete) && (w.day != 51 - w.eventOffset * 3 || !w.campaign))) {
            p.add(FreeTraining);
        }
        final JButton Delete = new JButton("Delete");
        if (w.campaign) {
            Delete.setText("Sacrifice");
        }
        Delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                if (x.gender == Forsaken.Gender.MALE) {
                    w.append(t, String.valueOf(x.mainName) + " will have " + x.hisHer() + " body modified into single-purpose breeding stock for the Demons, and you will never interact directly with " + x.himHer() + " again.  The terror of facing a similar fate will motivate any other Forsaken to obey you much more faithfully in the short-term.  Is this okay?");
                }
                else {
                    w.append(t, String.valueOf(x.mainName) + " will spend the rest of " + x.hisHer() + " life as single-purpose breeding stock for the Demons, and you will never interact directly with " + x.himHer() + " again.  The terror of facing a similar fate will motivate any other Forsaken to obey you much more faithfully in the short-term.  Is this okay?");
                }
                final JButton Confirm = new JButton("Confirm");
                Confirm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        p.removeAll();
                        final Forsaken[] newHarem = new Forsaken[w.getHarem().length - 1];
                        int removal = 0;
                        for (int i = 0; i < w.getHarem().length; ++i) {
                            if (w.getHarem()[i] == x) {
                                removal = i;
                            }
                            else {
                                w.getHarem()[i].motivation = 1000;
                            }
                        }
                        for (int i = 0; i < newHarem.length; ++i) {
                            if (i < removal) {
                                newHarem[i] = w.getHarem()[i];
                            }
                            else {
                                newHarem[i] = w.getHarem()[i + 1];
                            }
                        }
                        if (w.campaign) {
                            final Forsaken[] newSacrificed = new Forsaken[w.sacrificed.length + 1];
                            for (int j = 0; j < w.sacrificed.length; ++j) {
                                newSacrificed[j] = w.sacrificed[j];
                            }
                            newSacrificed[newSacrificed.length - 1] = w.conquered[removal];
                            w.conquered = newHarem;
                            w.sacrificed = newSacrificed;
                        }
                        else {
                            s.harem = newHarem;
                        }
                        wobj.serializeSaveData(s);
                        x.describeSacrifice(t, w);
                        final JButton Continue = new JButton("Continue");
                        Continue.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.ForsakenMenu(t, p, f, w, s, 0);
                            }
                        });
                        p.add(Continue);
                        p.validate();
                        p.repaint();
                    }
                });
                p.add(Confirm);
                final JButton Cancel = new JButton("Cancel");
                Cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.ForsakenInteraction(t, p, f, w, s, x);
                    }
                });
                p.add(Cancel);
                p.validate();
                p.repaint();
            }
        });
        p.add(Delete);
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.ForsakenMenu(t, p, f, w, s, 0);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void SceneCompletion(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final SaveData s) {
        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        w.underlineAppend(t, "Scenes Recorded");
        w.append(t, "\n");
        int types = 0;
        for (int i = 5; i < 21; ++i) {
            if (s.sceneText[i].length > 0) {
                ++types;
            }
        }
        w.append(t, "Core Vulnerability Break: " + types + "/20\n");
        w.append(t, "Core Vulnerability Distortions: ");
        if (s.sceneText[21].length > 0) {
            w.append(t, "1");
        }
        else {
            w.append(t, "0");
        }
        w.append(t, "/1\n");
        types = 0;
        for (int i = 33; i < 48; ++i) {
            if (s.sceneText[i].length > 0) {
                ++types;
            }
        }
        w.append(t, "Daily Vignettes: " + types + "/" + 15);
        w.append(t, "\n\nWhich type of scene would you like to view?");
    }
    
    public static void SceneViewer(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final SaveData s, final int starting) {
        p.removeAll();
        int found = 0;
        int highest = 0;
        for (int i = starting - 1; i >= 0 && found < 5; --i) {
            if (s.sceneText[i].length > 0) {
                ++found;
                highest = i;
            }
        }
        if (found > 0) {
            final int newStartPoint = highest;
            final JButton Previous = new JButton("<");
            Previous.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SceneViewer(t, p, f, w, s, newStartPoint);
                }
            });
            p.add(Previous);
        }
        found = 0;
        highest = 0;
        for (int i = starting; i < s.sceneText.length && found < 5; ++i) {
            if (s.sceneText[i].length > 0) {
                String sceneName = "";
                ++found;
                if ((highest = i) == 0) {
                    sceneName = "First Meeting";
                }
                else if (i == 1) {
                    sceneName = "Interview";
                }
                else if (i == 2) {
                    sceneName = "Vacation";
                }
                else if (i == 3) {
                    sceneName = "Final Preparation";
                }
                else if (i == 4) {
                    sceneName = "Epilogue";
                }
                else if (i == 5) {
                    sceneName = "First 'Violence'";
                }
                else if (i == 6) {
                    sceneName = "First 'Service'";
                }
                else if (i == 7) {
                    sceneName = "First 'Begging'";
                }
                else if (i == 8) {
                    sceneName = "First 'Covering'";
                }
                else if (i == 9) {
                    sceneName = "First 'Insemination'";
                }
                else if (i == 10) {
                    sceneName = "First 'Force Orgasm'";
                }
                else if (i == 11) {
                    sceneName = "First 'Sodomize/Torture/Force Laughter'";
                }
                else if (i == 12) {
                    sceneName = "First 'Broadcast'";
                }
                else if (i == 13) {
                    sceneName = "First 'Slaughter'";
                }
                else if (i == 14) {
                    sceneName = "First 'Fantasize'";
                }
                else if (i == 15) {
                    sceneName = "First 'Detonate'";
                }
                else if (i == 16) {
                    sceneName = "First 'Striptease'";
                }
                else if (i == 17) {
                    sceneName = "First 'Impregnation'";
                }
                else if (i == 18) {
                    sceneName = "First 'Hypnotism'";
                }
                else if (i == 19) {
                    sceneName = "First 'Drain'";
                }
                else if (i == 20) {
                    sceneName = "First 'Parasitism'";
                }
                else if (i == 21) {
                    sceneName = "First 'Tempt'";
                }
                else if (i == 33) {
                    sceneName = "Perverted Donor";
                }
                else if (i == 34) {
                    sceneName = "Sexual Technique Training";
                }
                else if (i == 35) {
                    sceneName = "Blackmailed";
                }
                else if (i == 36) {
                    sceneName = "Bodypaint Experiment";
                }
                else if (i == 37) {
                    sceneName = "Photoshoot";
                }
                else if (i == 38) {
                    sceneName = "Stripped in Public";
                }
                else if (i == 39) {
                    sceneName = "Movie Date";
                }
                else if (i == 40) {
                    sceneName = "Petplay";
                }
                else if (i == 41) {
                    sceneName = "Train Molester";
                }
                else if (i == 42) {
                    sceneName = "Sexual Combat Training";
                }
                else if (i == 43) {
                    sceneName = "Guilty Service";
                }
                else if (i == 44) {
                    sceneName = "Sleep Molester";
                }
                else if (i == 45) {
                    sceneName = "Saving One's Rival";
                }
                else if (i == 46) {
                    sceneName = "Service Competition";
                }
                else if (i == 47) {
                    sceneName = "Relief Through Abuse";
                }
                final JButton PickScene = new JButton(sceneName);
                final int sceneType = i;
                PickScene.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.SceneChoice(t, p, f, w, s, sceneType, starting, 0);
                    }
                });
                p.add(PickScene);
            }
        }
        for (int i = highest + 1; i < s.sceneText.length; ++i) {
            if (s.sceneText[i].length > 0) {
                final JButton Next = new JButton(">");
                final int newStartPoint2 = i;
                Next.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.SceneViewer(t, p, f, w, s, newStartPoint2);
                    }
                });
                p.add(Next);
                i = s.sceneText.length;
            }
        }
        final JButton Back = new JButton("Done");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                t.setText("");
                Project.IntroOne(t, p, f, w);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void SceneChoice(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final SaveData s, final int type, final int starting, final int page) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\nWhose scene would you like to replay?\n");
        if (page > 0) {
            final JButton Previous = new JButton("<");
            Previous.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    int newPage = page - 5;
                    if (newPage < 0) {
                        newPage = 0;
                    }
                    Project.SceneChoice(t, p, f, w, s, type, starting, newPage);
                }
            });
            p.add(Previous);
        }
        for (int i = page; i < page + 5 && i < s.sceneText[type].length; ++i) {
            w.append(t, "\n" + s.sceneButtons[type][i] + ": " + s.sceneSummaries[type][i]);
            final JButton PickScene = new JButton(s.sceneButtons[type][i]);
            final int sceneID = i;
            PickScene.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.ReplayScene(t, p, f, w, s, type, starting, page, sceneID);
                }
            });
            p.add(PickScene);
        }
        if (s.sceneText[type].length > page + 5) {
            final JButton Next = new JButton(">");
            Next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.SceneChoice(t, p, f, w, s, type, starting, page + 5);
                }
            });
            p.add(Next);
        }
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.SceneCompletion(t, p, f, w, s);
                Project.SceneViewer(t, p, f, w, s, starting);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void ReplayScene(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final SaveData s, final int type, final int starting, final int page, final int entry) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        final String[] shownFaces = new String[5];
        clearPortraits();
        for (int i = 0; i < 5; ++i) {
            if (s.sceneEmotions[type][entry][i] != null) {
                shownFaces[i] = s.sceneFaces[type][entry][i];
                changePortrait(s.sceneGenders[type][entry][i], s.sceneSpecs[type][entry][i], s.sceneCivs[type][entry][i], s.sceneFallen[type][entry][i], w, shownFaces, i, s.sceneEmotions[type][entry][i], s.sceneEmotions[type][entry][i]);
            }
        }
        for (int i = 0; i < s.sceneText[type][entry].length; ++i) {
            w.flexibleAppend(t, s.sceneText[type][entry][i], s.sceneColor[type][entry][i], s.sceneUnderline[type][entry][i]);
        }
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.SceneChoice(t, p, f, w, s, type, starting, page);
            }
        });
        p.add(Back);
        final JButton Delete = new JButton("Delete");
        Delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                w.append(t, "\n\n" + w.getSeparator() + "\n\nReally delete this scene?");
                final JButton ReallyDelete = new JButton("Delete");
                ReallyDelete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        final String[][] newSceneText = new String[s.sceneText[type].length - 1][0];
                        final Color[][] newSceneColor = new Color[s.sceneColor[type].length - 1][0];
                        final Boolean[][] newSceneUnderline = new Boolean[s.sceneUnderline[type].length - 1][0];
                        final String[] newSceneButtons = new String[s.sceneButtons[type].length - 1];
                        final String[] newSceneSummaries = new String[s.sceneSummaries[type].length - 1];
                        final Emotion[][] newSceneEmotions = new Emotion[s.sceneEmotions[type].length - 1][5];
                        final String[][] newSceneFaces = new String[s.sceneFaces[type].length - 1][5];
                        final Chosen.Species[][] newSceneSpecs = new Chosen.Species[s.sceneSpecs[type].length - 1][5];
                        final Boolean[][] newSceneCivs = new Boolean[s.sceneCivs[type].length - 1][5];
                        final Boolean[][] newSceneFallen = new Boolean[s.sceneFallen[type].length - 1][5];
                        final Forsaken.Gender[][] newSceneGenders = new Forsaken.Gender[s.sceneGenders[type].length - 1][5];
                        for (int i = 0; i < s.sceneText[type].length - 1; ++i) {
                            int editedEntry;
                            if ((editedEntry = i) >= entry) {
                                ++editedEntry;
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
                        final WriteObject wobj = new WriteObject();
                        wobj.serializeSaveData(s);
                        if (s.sceneText[type].length > 0) {
                            Project.SceneChoice(t, p, f, w, s, type, starting, 0);
                        }
                        else {
                            Project.SceneCompletion(t, p, f, w, s);
                            Project.SceneViewer(t, p, f, w, s, 0);
                        }
                    }
                });
                p.add(ReallyDelete);
                final JButton Return = new JButton("Return to Scene Choice");
                Return.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.SceneChoice(t, p, f, w, s, type, starting, page);
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
    
    public static void IntroTwo(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
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
        final File saveLocation = new File(String.valueOf(path) + File.separator + "saves.sav");
        if (saveLocation.exists()) {
            final ReadObject robj = new ReadObject();
            w.save = robj.deserializeSaveData(String.valueOf(path) + File.separator + "saves.sav");
            if (w.save.sceneText == null) {
                w.save.organizeScenes(48);
            }
            else if (w.save.sceneText.length < 48) {
                w.save.organizeScenes(48);
            }
        }
        else {
            w.save = new SaveData();
            if (w.save.sceneText == null) {
                w.save.organizeScenes(48);
            }
            else if (w.save.sceneText.length < 48) {
                w.save.organizeScenes(48);
            }
        }
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
    
    public static void PickTarget(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        final Color YELLOWISH = new Color(255, 225, 125);
        final Color PURPLISH = new Color(225, 125, 255);
        int inseminated = 0;
        int orgasming = 0;
        int sodomized = 0;
        int broadcasted = 0;
        p.removeAll();
        final Chosen[] initiative = new Chosen[3];
        if (w.getCombatants()[2] != null) {
            if (w.getCombatants()[0].getConfidence() > w.getCombatants()[1].getConfidence()) {
                if (w.getCombatants()[0].getConfidence() > w.getCombatants()[2].getConfidence()) {
                    if (w.getCombatants()[1].getConfidence() > w.getCombatants()[2].getConfidence()) {
                        initiative[0] = w.getCombatants()[0];
                        initiative[1] = w.getCombatants()[1];
                        initiative[2] = w.getCombatants()[2];
                    }
                    else {
                        initiative[0] = w.getCombatants()[0];
                        initiative[1] = w.getCombatants()[2];
                        initiative[2] = w.getCombatants()[1];
                    }
                }
                else {
                    initiative[0] = w.getCombatants()[2];
                    initiative[1] = w.getCombatants()[0];
                    initiative[2] = w.getCombatants()[1];
                }
            }
            else if (w.getCombatants()[0].getConfidence() > w.getCombatants()[2].getConfidence()) {
                initiative[0] = w.getCombatants()[1];
                initiative[1] = w.getCombatants()[0];
                initiative[2] = w.getCombatants()[2];
            }
            else if (w.getCombatants()[1].getConfidence() > w.getCombatants()[2].getConfidence()) {
                initiative[0] = w.getCombatants()[1];
                initiative[1] = w.getCombatants()[2];
                initiative[2] = w.getCombatants()[0];
            }
            else {
                initiative[0] = w.getCombatants()[2];
                initiative[1] = w.getCombatants()[1];
                initiative[2] = w.getCombatants()[0];
            }
        }
        else if (w.getCombatants()[1] != null) {
            if (w.getCombatants()[0].getConfidence() > w.getCombatants()[1].getConfidence()) {
                initiative[0] = w.getCombatants()[0];
                initiative[1] = w.getCombatants()[1];
            }
            else {
                initiative[0] = w.getCombatants()[1];
                initiative[1] = w.getCombatants()[0];
            }
        }
        else {
            initiative[0] = w.getCombatants()[0];
        }
        for (int i = 0; i < w.getCombatants().length; ++i) {
            if (w.getCombatants()[i] != null) {
                if (w.getCombatants()[i].isInseminated()) {
                    ++inseminated;
                }
                else if (w.getCombatants()[i].isOrgasming()) {
                    ++orgasming;
                }
                else if (w.getCombatants()[i].isSodomized()) {
                    ++sodomized;
                }
                else if (w.getCombatants()[i].isBroadcasted()) {
                    ++broadcasted;
                }
            }
        }
        w.append(t, "\nRound " + w.battleRound + "\n");
        if (w.evacNotice) {
            w.append(t, "Evacuation complete!\n");
            w.evacNotice = false;
        }
        else {
            w.append(t, "Evacuation: " + w.getEvacStatus(true) + "\n");
        }
        Chosen trappedChosen = null;
        for (int j = 0; j < 3; ++j) {
            if (w.getCombatants()[j] != null) {
                w.getCombatants()[j].updateSurround();
                if (w.getCombatants()[j].isSurrounded() || w.getCombatants()[j].isCaptured()) {
                    trappedChosen = w.getCombatants()[j];
                }
            }
        }
        w.append(t, "Extermination: " + w.getExterminationStatus(true) + "\n\n");
        if (w.evacuationProgress < w.evacuationComplete) {
            w.append(t, "The desperate battle continues...\n");
        }
        else {
            Chosen c = null;
            Boolean allGrabbed = true;
            if (w.getCombatants()[0] != null) {
                if (w.getCombatants()[0].isSurrounded() || w.getCombatants()[0].isCaptured() || (w.finalBattle && (!w.getCombatants()[0].alive || w.getCombatants()[0].resolve <= 0))) {
                    if (w.getCombatants()[1] != null) {
                        if (w.getCombatants()[1].isSurrounded() || w.getCombatants()[1].isCaptured() || (w.finalBattle && (!w.getCombatants()[1].alive || w.getCombatants()[1].resolve <= 0))) {
                            if (w.getCombatants()[2] != null && !w.getCombatants()[2].isSurrounded() && !w.getCombatants()[2].isCaptured() && (!w.finalBattle || (w.getCombatants()[2].alive && w.getCombatants()[2].resolve > 0))) {
                                allGrabbed = false;
                            }
                        }
                        else {
                            allGrabbed = false;
                        }
                    }
                }
                else {
                    allGrabbed = false;
                }
            }
            if (allGrabbed) {
                w.append(t, "The Demons have the Chosen at their mercy!\n");
            }
            else if (w.exterminationProgress >= w.exterminationComplete) {
                Boolean allFree = true;
                if (w.getCombatants()[0].isSurrounded() || w.getCombatants()[0].isCaptured()) {
                    allFree = false;
                }
                else if (w.getCombatants()[1] != null) {
                    if (w.getCombatants()[1].isSurrounded() || w.getCombatants()[1].isCaptured()) {
                        allFree = false;
                    }
                    else if (w.getCombatants()[2] != null && (w.getCombatants()[2].isSurrounded() || w.getCombatants()[2].isCaptured())) {
                        allFree = false;
                    }
                }
                if (allFree) {
                    int defeated = 0;
                    Chosen survivor = null;
                    for (int k = 0; k < 3; ++k) {
                        if (w.finalBattle) {
                            if (!w.getCast()[k].alive || w.getCast()[k].resolve <= 0) {
                                ++defeated;
                            }
                            else {
                                survivor = w.getCast()[k];
                            }
                        }
                    }
                    if (defeated == 2 && w.finalBattle) {
                        w.append(t, "With " + survivor.hisHer() + " allies defeated and no hope of winning on " + survivor.hisHer() + " own, " + survivor.getMainName() + " is preparing to make use of the hole in the Demons' formation to escape!  " + survivor.HeShe() + "'ll get away next turn unless " + survivor.heShe() + "'s surrounded or captured.\n");
                    }
                    else {
                        w.append(t, "The reanimated Demons are fighting their last stand!  Combat will end next turn unless one of the Chosen is surrounded or captured.\n");
                    }
                }
                else if (w.finalBattle) {
                    Chosen killer1 = null;
                    Chosen killer2 = null;
                    Chosen victim1 = null;
                    Chosen victim2 = null;
                    for (int l = 0; l < 3; ++l) {
                        if (w.getCast()[l].isSurrounded() || w.getCast()[l].isCaptured()) {
                            if (victim1 == null) {
                                victim1 = w.getCast()[l];
                            }
                            else {
                                victim2 = w.getCast()[l];
                            }
                        }
                        else if (w.getCast()[l].alive && w.getCast()[l].resolve > 0) {
                            if (killer1 == null) {
                                killer1 = w.getCast()[l];
                            }
                            else {
                                killer2 = w.getCast()[l];
                            }
                        }
                    }
                    int duration1 = 0;
                    if (victim1.isSurrounded()) {
                        duration1 = victim1.getSurroundDuration();
                    }
                    else {
                        duration1 = w.captureDuration - victim1.captureProgression;
                        if (victim1.timesDetonated() > 0) {
                            duration1 -= victim1.getINJULevel();
                        }
                    }
                    int duration2 = 0;
                    if (victim2 != null) {
                        if (victim2.isSurrounded()) {
                            duration2 = victim2.getSurroundDuration();
                        }
                        else {
                            duration2 = w.captureDuration - victim1.captureProgression;
                            if (victim2.timesDetonated() > 0) {
                                duration2 -= victim2.getINJULevel();
                            }
                        }
                    }
                    if (duration1 < 2 && duration2 < 2) {
                        if (victim2 == null) {
                            if (killer2 == null) {
                                w.append(t, String.valueOf(killer1.getMainName()) + " waits for " + victim1.getMainName() + "'s imminent escape so that the two of them can work together to end this.\n");
                            }
                            else {
                                w.append(t, String.valueOf(killer1.getMainName()) + " and " + killer2.getMainName() + " wait for " + victim1.getMainName() + " to escape and rejoin their formation.\n");
                            }
                        }
                        else {
                            w.append(t, String.valueOf(killer1.getMainName()) + " waits for " + victim1.getMainName() + " and " + victim2.getMainName() + " to escape and form up so they can all work together to end this.\n");
                        }
                    }
                    else {
                        if (victim2 != null && duration2 < 2) {
                            victim2 = null;
                        }
                        if (victim2 != null && duration1 < 2) {
                            victim1 = victim2;
                            duration1 = duration2;
                            victim2 = null;
                        }
                        if (victim2 != null) {
                            if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == -4 || victim1.isImpregnated() || victim1.isHypnotized() || victim1.isDrained() || victim1.isParasitized() || victim1.temptReq < 100000L || victim1.resolve < 50) {
                                if (w.getRelationship(killer1.getNumber(), victim2.getNumber()) == -4 || victim2.isImpregnated() || victim2.isHypnotized() || victim2.isDrained() || victim2.isParasitized() || victim2.temptReq < 100000L || victim2.resolve < 50) {
                                    if (w.getTechs()[40].isOwned() && !killer1.hesitated && (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4 || w.getRelationship(killer1.getNumber(), victim2.getNumber()) == 4)) {
                                        if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4) {
                                            if (w.getRelationship(killer1.getNumber(), victim2.getNumber()) == 4) {
                                                w.append(t, String.valueOf(killer1.getMainName()) + " calls out to the other Chosen, urging them to escape before they get caught up in " + killer1.hisHer() + " final attack.");
                                            }
                                            else {
                                                w.append(t, String.valueOf(killer1.getMainName()) + " prepares to launch a devastating attack in order to finish the battle, even though " + victim2.getMainName() + " is in the way.");
                                            }
                                        }
                                        else {
                                            w.append(t, String.valueOf(killer1.getMainName()) + " prepares to launch a devastating attack in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
                                        }
                                    }
                                    else {
                                        w.append(t, String.valueOf(killer1.getMainName()) + " prepares to launch a devastating attack in order to finish the battle, even though " + victim1.getMainName() + " and " + victim2.getMainName() + " are in the way.");
                                    }
                                }
                                else if (duration2 > duration1) {
                                    w.append(t, String.valueOf(killer1.getMainName()) + " buys time for " + victim2.getMainName() + " to escape so that the two of them can work together to end this.");
                                }
                                else if (w.getTechs()[40].isOwned() && !killer1.hesitated && w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4) {
                                    w.append(t, String.valueOf(killer1.getMainName()) + " calls out to " + victim1.getMainName() + ", urging " + victim1.himHer() + " to escape before " + victim1.heShe() + " gets caught up in " + killer1.getMainName() + "'s final attack.");
                                }
                                else {
                                    w.append(t, String.valueOf(killer1.getMainName()) + " prepares to launch a devastating attack so that the battle can be finished after " + victim2.getMainName() + " escapes, even though " + victim1.getMainName() + " is in the way.");
                                }
                            }
                            else if (w.getRelationship(killer1.getNumber(), victim2.getNumber()) == -4 || victim2.isImpregnated() || victim2.isHypnotized() || victim2.isDrained() || victim2.isParasitized() || victim2.temptReq < 100000L || victim2.resolve < 50) {
                                if (duration1 > duration2) {
                                    w.append(t, String.valueOf(killer1.getMainName()) + " buys time for " + victim1.getMainName() + " to escape so that the two of them can work together to end this.");
                                }
                                else if (w.getTechs()[40].isOwned() && !killer1.hesitated && w.getRelationship(killer1.getNumber(), victim2.getNumber()) == 4) {
                                    w.append(t, String.valueOf(killer1.getMainName()) + " calls out to " + victim2.getMainName() + ", urging " + victim2.himHer() + " to escape before " + victim2.heShe() + " gets caught up in " + killer1.getMainName() + "'s final attack.");
                                }
                                else {
                                    w.append(t, String.valueOf(killer1.getMainName()) + " prepares to launch a devastating attack in order to finish the battle, even though " + victim2.getMainName() + " is in the way.");
                                }
                            }
                            else {
                                w.append(t, String.valueOf(killer1.getMainName()) + " buys time for the other two Chosen to escape so that they all can work together to end this.");
                            }
                        }
                        else if (killer2 != null) {
                            if (victim1.isImpregnated() || victim1.isHypnotized() || victim1.isDrained() || victim1.isParasitized() || victim1.temptReq < 100000L || victim1.resolve < 50) {
                                if (w.getTechs()[40].isOwned()) {
                                    if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4 && !killer1.hesitated) {
                                        if (w.getRelationship(killer2.getNumber(), victim1.getNumber()) == 4 && !killer2.hesitated) {
                                            w.append(t, String.valueOf(killer1.getMainName()) + " and " + killer2.getMainName() + " call out to " + victim1.getMainName() + ", urging " + victim1.himHer() + " to escape before " + victim1.heShe() + " gets caught up in their final attack.");
                                        }
                                        else {
                                            w.append(t, String.valueOf(victim1.getMainName()) + "'s captivity is preventing the other Chosen from ending the battle, and while " + killer1.getMainName() + " looks conflicted, " + killer2.getMainName() + " is preparing to attack anyway.");
                                        }
                                    }
                                    else if (w.getRelationship(killer2.getNumber(), victim1.getNumber()) == 4 && !killer2.hesitated) {
                                        w.append(t, String.valueOf(victim1.getMainName()) + "'s captivity is preventing the other Chosen from ending the battle, and while " + killer2.getMainName() + " looks conflicted, " + killer1.getMainName() + " is preparing to attack anyway.");
                                    }
                                    else {
                                        w.append(t, String.valueOf(killer1.getMainName()) + " and " + killer2.getMainName() + " prepare to launch their most devastating attacks in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
                                    }
                                }
                                else {
                                    w.append(t, String.valueOf(killer1.getMainName()) + " and " + killer2.getMainName() + " prepare to launch their most devastating attacks in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
                                }
                            }
                            else if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == -4) {
                                if (w.getRelationship(killer2.getNumber(), victim1.getNumber()) == -4) {
                                    w.append(t, String.valueOf(killer1.getMainName()) + " and " + killer2.getMainName() + " prepare to launch their most devastating attacks in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
                                }
                                else {
                                    w.append(t, String.valueOf(victim1.getMainName()) + "'s captivity is preventing the other Chosen from ending the battle, and while " + killer2.getMainName() + " isn't willing to sacrifice " + victim1.getMainName() + "'s life in order to finish things sooner, " + killer1.getMainName() + " is.");
                                }
                            }
                            else if (w.getRelationship(killer2.getNumber(), victim1.getNumber()) == -4) {
                                w.append(t, String.valueOf(victim1.getMainName()) + "'s captivity is preventing the other Chosen from ending the battle, and while " + killer1.getMainName() + " isn't willing to sacrifice " + victim1.getMainName() + "'s life in order to finish things sooner, " + killer2.getMainName() + " is.");
                            }
                            else {
                                w.append(t, String.valueOf(victim1.getMainName()) + "'s captivity is preventing the other Chosen from ending the battle, but " + killer1.getMainName() + " and " + killer2.getMainName() + " aren't willing to sacrifice " + victim1.hisHer() + " life just to finish things a little bit sooner.");
                            }
                        }
                        else if (w.getRelationship(killer1.getNumber(), victim1.getNumber()) == -4 || victim1.isImpregnated() || victim1.isHypnotized() || victim1.isDrained() || victim1.isParasitized() || victim1.temptReq < 100000L || victim1.resolve < 50) {
                            if (w.getTechs()[40].isOwned() && !killer1.hesitated && w.getRelationship(killer1.getNumber(), victim1.getNumber()) == 4) {
                                w.append(t, String.valueOf(killer1.getMainName()) + " calls out to " + victim1.getMainName() + ", urging " + victim1.himHer() + " to escape before " + victim1.heShe() + " gets caught up in " + killer1.getMainName() + "'s final attack.");
                            }
                            else {
                                w.append(t, String.valueOf(killer1.getMainName()) + " prepares to launch a devastating attack in order to finish the battle, even though " + victim1.getMainName() + " is in the way.");
                            }
                        }
                        else {
                            w.append(t, String.valueOf(killer1.getMainName()) + " buys time for " + victim1.getMainName() + " to escape so that the two of them can work together to finish this.");
                        }
                        w.append(t, "\n");
                    }
                }
                else {
                    while (c == null) {
                        c = w.getCombatants()[(int)(Math.random() * 3.0)];
                        if (c != null && (c.isSurrounded() || c.isCaptured())) {
                            c = null;
                        }
                    }
                    w.append(t, String.valueOf(c.getMainName()) + " can't finish clearing out the Demons due to the risk of hitting the trapped " + trappedChosen.getMainName() + " with friendly fire!\n");
                }
            }
            else {
                while (c == null) {
                    c = w.getCombatants()[(int)(Math.random() * 3.0)];
                    if (c != null && (c.isSurrounded() || c.isCaptured())) {
                        c = null;
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
                    }
                    else {
                        w.append(t, " can start drawing on " + c.hisHer() + " full power!");
                    }
                }
                else if (w.exterminationMultiplier == 150) {
                    w.append(t, String.valueOf(c.getMainName()) + "'s attacks grow stronger and stronger, shattering windows and setting off alarms!");
                }
                else if (w.exterminationMultiplier == 225) {
                    w.append(t, String.valueOf(c.getMainName()) + " moves like a blur, taking down a wide swath of Demons!");
                }
                else if (w.exterminationMultiplier == 337) {
                    w.append(t, "A blast of energy from " + c.getMainName() + " brings down a small building in a cloud of rubble!");
                }
                else if (w.exterminationMultiplier == 505) {
                    w.append(t, "The area is riddled with craters caused by the power of " + c.getMainName() + "'s attacks!");
                }
                else if (w.exterminationMultiplier == 757) {
                    w.append(t, "The district is consumed by an enormous explosion as " + c.getMainName() + " blasts away the Demons!");
                }
                w.append(t, "\n(Extermination power");
                if (w.cast[1] != null) {
                    w.append(t, " per Chosen");
                }
                w.append(t, ": " + w.exterminationPerChosen * w.exterminationMultiplier / 100 + ")");
                w.append(t, "\n");
            }
        }
        for (int j = 0; j < w.getCombatants().length; ++j) {
            if (w.getCombatants()[j] != null) {
                w.append(t, "\n");
                if (w.getCombatants()[j].type == Chosen.Species.SUPERIOR) {
                    w.append(t, "[SUPERIOR] ");
                }
                if (w.getCombatants()[j].isSurrounded() && (w.getCombatants()[j].resolve > 0 || !w.finalBattle)) {
                    w.orangeAppend(t, String.valueOf(w.getCombatants()[j].getMainName()) + ": ");
                    if (inseminated == 3 || orgasming == 3 || sodomized == 3 || broadcasted == 3) {
                        w.orangeAppend(t, "In Orgy");
                    }
                    else if (w.getCombatants()[j].isInseminated()) {
                        w.orangeAppend(t, "Inseminated");
                    }
                    else if (w.getCombatants()[j].isOrgasming()) {
                        w.orangeAppend(t, "Orgasming");
                    }
                    else if (w.getCombatants()[j].isSodomized()) {
                        if (w.tickle()) {
                            w.orangeAppend(t, "Laughing");
                        }
                        else if (w.getCombatants()[j].getGender().equals("male")) {
                            w.orangeAppend(t, "Tortured");
                        }
                        else {
                            w.orangeAppend(t, "Sodomized");
                        }
                    }
                    else if (w.getCombatants()[j].isBroadcasted()) {
                        w.orangeAppend(t, "Broadcasted");
                    }
                    else if (w.getCombatants()[j].tempted) {
                        w.orangeAppend(t, "Tempted");
                    }
                    else {
                        w.orangeAppend(t, "Surrounded");
                    }
                    if (w.getCombatants()[j].getSurroundDuration() > 1) {
                        w.orangeAppend(t, " for " + w.getCombatants()[j].getSurroundDuration() + " more turns");
                    }
                    else {
                        w.orangeAppend(t, " until next turn");
                    }
                }
                else if (w.getCombatants()[j].isCaptured()) {
                    w.orangeAppend(t, String.valueOf(w.getCombatants()[j].getMainName()) + ": ");
                    if (w.getCombatants()[j].timesDetonated() > 0 && !w.adaptationsDisabled()) {
                        if (w.getCombatants()[j].getCaptureProgression() + w.getCombatants()[j].getINJULevel() + 1 >= w.getCaptureDuration()) {
                            w.orangeAppend(t, "Detonating next turn");
                        }
                        else if (w.getCombatants()[j].getCaptureProgression() + w.getCombatants()[j].getINJULevel() + 2 == w.getCaptureDuration()) {
                            w.orangeAppend(t, "Detonating in 2 more turns");
                        }
                        else if (w.getBodyStatus()[5] || w.getBodyStatus()[12] || w.getBodyStatus()[13] || w.getBodyStatus()[21] || w.usedForsaken != null) {
                            w.orangeAppend(t, "Detonating in up to " + (w.getCaptureDuration() - w.getCombatants()[j].getCaptureProgression() - w.getCombatants()[j].getINJULevel()) + " more turns");
                        }
                        else {
                            w.orangeAppend(t, "Detonating in " + (w.getCaptureDuration() - w.getCombatants()[j].getCaptureProgression() - w.getCombatants()[j].getINJULevel()) + " more turns");
                        }
                    }
                    else if (w.getCombatants()[j].getCaptureProgression() < w.getCaptureDuration()) {
                        w.orangeAppend(t, "Captured for " + (w.getCaptureDuration() - w.getCombatants()[j].getCaptureProgression() + 1) + " more turns");
                    }
                    else {
                        w.orangeAppend(t, "Captured until next turn");
                    }
                }
                else if (!w.getCombatants()[j].alive) {
                    w.redAppend(t, String.valueOf(w.getCombatants()[j].getMainName()) + ": Killed in Action");
                }
                else if (w.finalBattle && w.getCombatants()[j].resolve <= 0) {
                    w.greenAppend(t, String.valueOf(w.getCombatants()[j].getMainName()) + ": Resolve Broken!");
                }
                else if (w.getCombatants()[j].surroundPossible(w)) {
                    w.purpleAppend(t, String.valueOf(w.getCombatants()[j].getMainName()) + ": Opening Level " + (w.getCombatants()[j].getFEAROpening(w) + w.getCombatants()[j].getPAINOpening() + w.getCombatants()[j].getDISGOpening() + w.getCombatants()[j].getSHAMOpening(w)) + " vs. Defense Level " + w.getCombatants()[j].getDefenseLevel());
                }
                else if (w.getCombatants()[j].getDefenseLevel() > 9000) {
                    w.append(t, String.valueOf(w.getCombatants()[j].getMainName()) + ": Flying Above Battlefield");
                }
                else {
                    w.append(t, String.valueOf(w.getCombatants()[j].getMainName()) + ": Opening Level " + (w.getCombatants()[j].getFEAROpening(w) + w.getCombatants()[j].getPAINOpening() + w.getCombatants()[j].getDISGOpening() + w.getCombatants()[j].getSHAMOpening(w)) + " vs. Defense Level " + w.getCombatants()[j].getDefenseLevel());
                }
                if (w.finalBattle && w.getCombatants()[j].resolve > 0 && w.getCombatants()[j].alive) {
                    w.append(t, " [Resolve at " + w.getCombatants()[j].resolve + "%]");
                }
            }
        }
        if (w.usedForsaken != null) {
            w.append(t, "\n\n" + w.usedForsaken.mainName + ": ");
            int occupied = -1;
            for (int m = 0; m < w.getCombatants().length; ++m) {
                if (w.getCombatants()[m] != null && w.getCombatants()[m].captured) {
                    occupied = m;
                }
            }
            if (occupied >= 0) {
                w.purpleAppend(t, "Busy with " + w.getCombatants()[occupied].mainName);
            }
            else if (w.usedForsaken.injured > 1) {
                w.redAppend(t, "Stunned for " + w.usedForsaken.injured + " turns");
                changePortrait(w.usedForsaken.gender, w.usedForsaken.type, Project.displayedCivilians[3], true, w, w.nameCombatants(), 3, Emotion.SWOON, Emotion.SWOON);
            }
            else if (w.usedForsaken.injured == 1) {
                w.redAppend(t, "Stunned until next turn");
                changePortrait(w.usedForsaken.gender, w.usedForsaken.type, Project.displayedCivilians[3], true, w, w.nameCombatants(), 3, Emotion.SWOON, Emotion.SWOON);
            }
            else {
                w.greenAppend(t, "Ready to capture target");
                if (w.usedForsaken.flavorObedience() < 20) {
                    changePortrait(w.usedForsaken.gender, w.usedForsaken.type, Project.displayedCivilians[3], true, w, w.nameCombatants(), 3, Emotion.ANGER, Emotion.NEUTRAL);
                }
                else if (w.usedForsaken.flavorObedience() < 40) {
                    changePortrait(w.usedForsaken.gender, w.usedForsaken.type, Project.displayedCivilians[3], true, w, w.nameCombatants(), 3, Emotion.ANGER, Emotion.SHAME);
                }
                else if (w.usedForsaken.flavorObedience() < 61) {
                    changePortrait(w.usedForsaken.gender, w.usedForsaken.type, Project.displayedCivilians[3], true, w, w.nameCombatants(), 3, Emotion.SHAME, Emotion.STRUGGLE);
                }
                else if (w.usedForsaken.flavorObedience() < 81) {
                    changePortrait(w.usedForsaken.gender, w.usedForsaken.type, Project.displayedCivilians[3], true, w, w.nameCombatants(), 3, Emotion.FOCUS, Emotion.ANGER);
                }
                else {
                    changePortrait(w.usedForsaken.gender, w.usedForsaken.type, Project.displayedCivilians[3], true, w, w.nameCombatants(), 3, Emotion.FOCUS, Emotion.JOY);
                }
            }
        }
        if (w.getRallyBonus() > 0) {
            w.append(t, "\n\nMorale bonus: incoming trauma decreased by " + w.getRallyBonus() / 6 + "%");
        }
        if (w.getDistractBonus() > 0) {
            w.append(t, "\n\nThralls distracted: damage to surrounded Chosen decreased by " + w.getDistractBonus() / 3 + "%");
        }
        if (w.getBarrierMulti() > 10000L) {
            w.append(t, "\n\nDemonic barrier: all damage increased by " + (w.getBarrierMulti() / 100L - 100L) + "%");
        }
        int targets = 0;
        int targetFound = 0;
        int defeated2 = 0;
        int trapped = 0;
        for (int i2 = 0; i2 < 3; ++i2) {
            if (w.getCombatants()[i2] != null) {
                if (!w.finalBattle) {
                    ++targets;
                    targetFound = i2;
                }
                else if (w.getCombatants()[i2].isCaptured() || (w.getCombatants()[i2].isSurrounded() && (w.getCombatants()[i2].isDefiled() || (w.getCombatants()[i2].getHATELevel() < 3 && w.getCombatants()[i2].getPLEALevel() < 3 && w.getCombatants()[i2].getINJULevel() < 3 && w.getCombatants()[i2].getEXPOLevel() < 3 && w.getCombatants()[i2].grind && w.getCombatants()[i2].caress && w.getCombatants()[i2].pummel && w.getCombatants()[i2].humiliate)))) {
                    ++trapped;
                }
                else if (w.getCombatants()[i2].alive && w.getCombatants()[i2].resolve > 0) {
                    ++targets;
                    targetFound = i2;
                }
                else {
                    ++defeated2;
                }
            }
        }
        if (targets == 1) {
            if (w.getCast()[1] != null) {
                if (w.finalBattle && defeated2 > 0) {
                    w.append(t, "\n\n" + w.getCombatants()[targetFound].getMainName() + " is still resisting!");
                }
                else if (w.getCombatants()[1] != null) {
                    w.append(t, "\n\n" + w.getCombatants()[targetFound].getMainName() + " is trying to stall until the team can fight at full strength!");
                }
                else {
                    w.append(t, "\n\n" + w.getCombatants()[targetFound].getMainName() + " is fighting alone!");
                }
            }
        }
        else if (targets == 0) {
            if (w.getCombatants()[1] == null) {
                w.append(t, "\n\n" + w.getCombatants()[0].getMainName() + "'s allies haven't shown up yet!");
            }
            else {
                w.append(t, "\n\nThe Chosen are struggling to escape the Demons' clutches!");
            }
        }
        else {
            w.chatter(t);
            w.append(t, "\n\nWho will you target?");
        }
        if (targets == 1 && (w.getCombatants()[1] == null || defeated2 == 2)) {
            PickAction(t, p, f, w, w.getCombatants()[targetFound], initiative);
        }
        else {
            p.removeAll();
            for (int i2 = 0; i2 < 3; ++i2) {
                if (w.getCombatants()[i2] != null && (!w.finalBattle || (w.getCombatants()[i2].resolve > 0 && w.getCombatants()[i2].alive))) {
                    final int thisChosen = i2;
                    class TargetButton extends AbstractAction
                    {
                        public TargetButton(final String text, final String desc) {
                            super(text);
                            this.putValue("ShortDescription", desc);
                        }
                        
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            w.append(t, "\n\n" + w.getSeparator());
                            Project.PickAction(t, p, f, w, w.getCombatants()[thisChosen], initiative);
                        }
                    }
                    final Action TargetAction = new TargetButton(w.getCombatants()[i2].getMainName(), "Hotkey:");
                    final JButton Target = new JButton(TargetAction) {
                        @Override
                        public Point getToolTipLocation(final MouseEvent e) {
                            return new Point(0, -30);
                        }
                    };
                    if ((w.getCombatants()[i2].getCurrentHATE() >= 10000L || w.getCombatants()[i2].getCurrentPLEA() >= 10000L || w.getCombatants()[i2].getCurrentINJU() >= 10000L || w.getCombatants()[i2].getCurrentEXPO() >= 10000L) && w.getCombatants()[i2].isSurrounded() && !w.getCombatants()[i2].isDefiled()) {
                        if ((w.getCombatants()[i2].getCurrentHATE() >= 10000L && inseminated > 0) || (w.getCombatants()[i2].getCurrentPLEA() >= 10000L && orgasming > 0) || (w.getCombatants()[i2].getCurrentINJU() >= 10000L && sodomized > 0) || (w.getCombatants()[i2].getCurrentEXPO() >= 10000L && broadcasted > 0)) {
                            Target.setBackground(PURPLISH);
                        }
                        else {
                            Target.setBackground(YELLOWISH);
                        }
                    }
                    Target.getInputMap(2).put(KeyStroke.getKeyStroke(new StringBuilder().append(thisChosen + 1).toString()), "pressed");
                    if (w.onTrack && w.getActions().length > w.getCurrentAction() && (w.getActions()[w.getCurrentAction()] - 1) / 14 == w.getCombatants()[thisChosen].getNumber()) {
                        Target.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                    }
                    Target.getActionMap().put("pressed", TargetAction);
                    p.add(Target);
                }
            }
            class PassButton extends AbstractAction
            {
                public PassButton(final String text, final String desc) {
                    super(text);
                    this.putValue("ShortDescription", desc);
                }
                
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.advanceAction(p, w, 0);
                    if (w.getTechs()[30].isOwned() && !w.progressExtermination(0)) {
                        p.removeAll();
                        w.increaseBarrier(t);
                        class ContinueButton extends AbstractAction
                        {
                            public ContinueButton(final String desc) {
                                //super(text);
                                this.putValue("ShortDescription", desc);
                            }
                            
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.EnemyTurn(t, p, f, w, initiative, 0);
                            }
                        }
                        final Action ContinueAction = new ContinueButton("Continue");
                        final JButton Continue = new JButton(ContinueAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -30);
                            }
                        };
                        Continue.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        Continue.getActionMap().put("pressed", ContinueAction);
                        p.add(Continue);
                        p.validate();
                        p.repaint();
                    }
                    else {
                        Project.EnemyTurn(t, p, f, w, initiative, 0);
                    }
                }
            }
            final Action PassAction = new PassButton("Do Nothing", "Hotkey:");
            final JButton Pass = new JButton(PassAction) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            Pass.setForeground(Color.GRAY);
            Pass.getInputMap(2).put(KeyStroke.getKeyStroke(70, 0), "pressed");
            if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == 0) {
                Pass.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            }
            Pass.getActionMap().put("pressed", PassAction);
            if (w.getTechs()[30].isOwned() && !w.progressExtermination(0)) {
                Pass.setText("Barrier");
                Pass.setToolTipText("+5% damage for rest of battle");
            }
            p.add(Pass);
            int occupied2 = 0;
            for (int l = 0; l < 3; ++l) {
                if (w.getCombatants()[l] != null) {
                    if (w.getCombatants()[l].isSurrounded()) {
                        if (w.getCombatants()[l].getSurroundDuration() > 0) {
                            occupied2 += w.getCombatants()[l].getSurroundDuration();
                        }
                        else {
                            ++occupied2;
                        }
                    }
                    else if (w.getCombatants()[l].isCaptured()) {
                        occupied2 += w.getCaptureDuration() - w.getCombatants()[l].getCaptureProgression() + 1;
                    }
                }
            }
            final int occupiedBonus = occupied2 / 5;
            class RetreatButton extends AbstractAction
            {
                public RetreatButton(final String text, final String desc) {
                    super(text);
                    this.putValue("ShortDescription", desc);
                }
                
                @Override
                public void actionPerformed(final ActionEvent e) {
                    p.removeAll();
                    w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                    if (occupiedBonus > 0) {
                        w.append(t, "Retreat and end the battle immediately for +" + occupiedBonus + " Evil Energy?");
                    }
                    else {
                        w.append(t, "Really retreat?  You will not gain any bonus Evil Energy!");
                    }
                    final JButton Confirm = new JButton("Confirm");
                    Confirm.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            p.removeAll();
                            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                            final String[] trapped = new String[3];
                            final String[] free = new String[3];
                            int trappedNumber = 0;
                            for (int i = 0; i < 3; ++i) {
                                if (w.getCombatants()[i] != null) {
                                    if (w.getCombatants()[i].isSurrounded() || w.getCombatants()[i].isCaptured()) {
                                        for (int j = 0; j < 3; ++j) {
                                            if (trapped[j] == null) {
                                                trapped[j] = w.getCombatants()[i].getMainName();
                                                ++trappedNumber;
                                                j = 3;
                                            }
                                        }
                                    }
                                    else {
                                        for (int j = 0; j < 3; ++j) {
                                            if (free[j] == null) {
                                                free[j] = w.getCombatants()[i].getMainName();
                                                j = 3;
                                            }
                                        }
                                    }
                                }
                            }
                            if (w.getCombatants()[1] == null) {
                                for (int i = 0; i < 3; ++i) {
                                    if (w.getCast()[i] != null && !w.getCast()[i].equals(w.getCombatants()[0])) {
                                        if (free[0] == null) {
                                            free[0] = w.getCast()[i].mainName;
                                        }
                                        else if (free[1] == null) {
                                            free[1] = w.getCast()[i].mainName;
                                        }
                                        else {
                                            free[2] = w.getCast()[i].mainName;
                                        }
                                    }
                                }
                            }
                            else if (w.getCombatants()[2] == null) {
                                for (int i = 0; i < 3; ++i) {
                                    if (w.getCast()[i] != null && !w.getCast()[i].equals(w.getCombatants()[0]) && !w.getCast()[i].equals(w.getCombatants()[1])) {
                                        if (free[0] == null) {
                                            free[0] = w.getCast()[i].mainName;
                                        }
                                        else if (free[1] == null) {
                                            free[1] = w.getCast()[i].mainName;
                                        }
                                        else {
                                            free[2] = w.getCast()[i].mainName;
                                        }
                                    }
                                }
                            }
                            w.append(t, "You order your Demons to flee back into the tunnels beneath the city along with their captive victims.  ");
                            if (w.getCast()[1] == null) {
                                if (trappedNumber == 0) {
                                    w.append(t, "However, " + free[0] + " is quick to pursue, cutting your forces down from behind and stopping them from taking any significant number of civilians back to the hive.");
                                }
                                else {
                                    w.append(t, String.valueOf(trapped[0]) + " is unable to follow until plenty of civilians are already on their way to the hive.");
                                }
                            }
                            else if (w.getCast()[2] == null) {
                                if (trappedNumber == 0) {
                                    w.append(t, "However, the two Chosen cut your forces down from behind, freeing most of the civilians before they can be brought to the hive.");
                                }
                                else if (trappedNumber == 1) {
                                    w.append(t, "With " + trapped[0] + " unable to give chase, the risk of splitting the team forces " + free[0] + " to give up and let you take the civilians to the hive.");
                                }
                                else {
                                    w.append(t, "The Chosen have to finish dealing with their own problems before they can try to stop you, and by then, plenty of your forces have escaped.");
                                }
                            }
                            else if (trappedNumber == 0 || occupiedBonus == 0) {
                                w.append(t, "However, the three Chosen cut your forces down from behind, freeing most of the civilians before they can be brought to the hive.");
                            }
                            else if (trappedNumber == 1) {
                                w.append(t, String.valueOf(free[0]) + " and " + free[1] + " try to give chase, but with " + trapped[0] + " unable to follow, they're forced to give up due to the risk of splitting the team.");
                            }
                            else if (trappedNumber == 2) {
                                w.append(t, String.valueOf(free[0]) + " tries to stop them, but with " + trapped[0] + " and " + trapped[1] + " unable to help, you're able to get plenty of victims to the hive.");
                            }
                            else {
                                w.append(t, "The Chosen have to finish dealing with their own problems before they can try to stop you, and by then, plenty of your forces have escaped.");
                            }
                            if (occupiedBonus > 0) {
                                w.append(t, "\n\n+" + occupiedBonus + " Evil Energy");
                            }
                            Project.advanceAction(p, w, 43);
                            w.addEnergy(occupiedBonus);
                            final JButton Continue = new JButton("Continue");
                            Continue.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    Project.PostBattle(t, p, f, w);
                                }
                            });
                            p.add(Continue);
                            p.validate();
                            p.repaint();
                        }
                    });
                    p.add(Confirm);
                    final JButton Cancel = new JButton("Cancel");
                    Cancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            w.append(t, "\n\n" + w.getSeparator() + "\n");
                            Project.PickTarget(t, p, f, w);
                        }
                    });
                    p.add(Cancel);
                    p.validate();
                    p.repaint();
                }
            }
            final Action RetreatAction = new RetreatButton("Retreat (" + occupiedBonus + ")", "End battle immediately for +" + occupiedBonus + " EE");
            final JButton Retreat = new JButton(RetreatAction) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            if (w.getTechs()[19].isOwned() && !w.finalBattle) {
                p.add(Retreat);
            }
            if (w.writePossible()) {
                addWriteButton(p, w);
            }
            p.validate();
            p.repaint();
        }
        w.readCommentary(t);
    }
    
    public static void advanceAction(final JPanel p, final WorldState w, final int action) {
        Boolean actionMatches = true;
        if (w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] != action) {
            actionMatches = false;
            w.truncateCommentary(w.getCurrentAction());
        }
        if (w.writePossible()) {
            if (w.getCurrentComment().length() > 0) {
                w.writeCommentary(w.getCurrentComment());
            }
            else if (w.getCommentary().length <= w.getCurrentAction() || !actionMatches) {
                String generated = "";
                if (action == 0) {
                    if (w.getTechs()[30].isOwned() && !w.progressExtermination(0)) {
                        generated = "Deepen your barrier.";
                    }
                    else {
                        generated = "Do nothing.";
                    }
                }
                else if (action == 43) {
                    generated = "Retreat from the battle.";
                }
                else if (action > 43) {
                    final int type = (action - 44) / 3;
                    final int target = (action - 44) % 3;
                    final String targetedChosen = w.getCast()[target].getMainName();
                    if (type == 0) {
                        generated = "Tempt ";
                    }
                    generated = String.valueOf(generated) + targetedChosen + ".";
                }
                else {
                    final int target2 = (action - 1) / 14;
                    final int type2 = (action - 1) % 14 + 1;
                    final String targetedChosen = w.getCast()[target2].getMainName();
                    if (type2 == 1) {
                        generated = "Surround ";
                    }
                    else if (type2 == 2) {
                        generated = "Capture ";
                    }
                    else if (type2 == 3) {
                        generated = "Threaten ";
                    }
                    else if (type2 == 4) {
                        generated = "Slime ";
                    }
                    else if (type2 == 5) {
                        if (w.tickle()) {
                            generated = "Poke ";
                        }
                        else {
                            generated = "Attack ";
                        }
                    }
                    else if (type2 == 6) {
                        generated = "Taunt ";
                    }
                    else {
                        if (w.getTechs()[31].isOwned() && !w.getCast()[target2].isSurrounded()) {
                            if (!w.getCast()[target2].surroundPossible(w)) {
                                generated = String.valueOf(generated) + "Capture and then ";
                            }
                            else {
                                generated = String.valueOf(generated) + "Surround and then ";
                            }
                        }
                        if (type2 == 7) {
                            generated = String.valueOf(generated) + "Grind against ";
                        }
                        else if (type2 == 8) {
                            generated = String.valueOf(generated) + "Caress ";
                        }
                        else if (type2 == 9) {
                            if (w.tickle()) {
                                generated = String.valueOf(generated) + "Tickle ";
                            }
                            else {
                                generated = String.valueOf(generated) + "Pummel ";
                            }
                        }
                        else if (type2 == 10) {
                            generated = String.valueOf(generated) + "Humiliate ";
                        }
                        else if (type2 == 11) {
                            generated = String.valueOf(generated) + "Inseminate ";
                        }
                        else if (type2 == 12) {
                            generated = String.valueOf(generated) + "Force Orgasm on ";
                        }
                        else if (type2 == 13) {
                            if (w.tickle()) {
                                generated = String.valueOf(generated) + "Force Laughter from ";
                            }
                            else if (w.getCast()[target2].getGender().equals("male")) {
                                generated = String.valueOf(generated) + "Torture ";
                            }
                            else {
                                generated = String.valueOf(generated) + "Sodomize ";
                            }
                        }
                        else if (type2 == 14) {
                            generated = String.valueOf(generated) + "Broadcast ";
                        }
                    }
                    generated = String.valueOf(generated) + targetedChosen + ".";
                }
                w.writeCommentary(generated);
            }
        }
        w.nextAction(action);
    }
    
    public static void addWriteButton(final JPanel p, final WorldState w) {
        final JButton Comment = new JButton("Comment");
        Comment.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                String prompt = "Enter your comment here.  Leave blank to ";
                if (w.getCurrentComment().length() > 0) {
                    prompt = String.valueOf(prompt) + "keep the comment you already wrote.";
                }
                else if (w.getCommentary().length > w.getCurrentAction()) {
                    prompt = String.valueOf(prompt) + "keep the previous playthrough's comment.";
                }
                else {
                    prompt = String.valueOf(prompt) + "generate a default comment describing your action.";
                }
                final String input = JOptionPane.showInputDialog(prompt);
                if (input != null && input.length() > 0) {
                    w.setCurrentComment(input);
                    Comment.setToolTipText("\"" + input + "\"");
                }
            }
        });
        Comment.setForeground(Color.GRAY);
        Comment.setToolTipText("No comment currently stored.");
        p.add(Comment);
    }
    
    public static void PickAction(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c, final Chosen[] initiative) {
        final Color YELLOWISH = new Color(255, 225, 125);
        final Color PURPLISH = new Color(225, 125, 255);
        final Color REDDISH = new Color(255, 145, 145);
        int inseminated = 0;
        int orgasming = 0;
        int sodomized = 0;
        int broadcasted = 0;
        for (int i = 0; i < 3; ++i) {
            if (w.getCombatants()[i] != null) {
                if (w.getCombatants()[i].isInseminated()) {
                    ++inseminated;
                }
                else if (w.getCombatants()[i].isOrgasming()) {
                    ++orgasming;
                }
                else if (w.getCombatants()[i].isSodomized()) {
                    ++sodomized;
                }
                else if (w.getCombatants()[i].isBroadcasted()) {
                    ++broadcasted;
                }
            }
        }
        class ContinueButton extends AbstractAction
        {
            public ContinueButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.EnemyTurn(t, p, f, w, initiative, 0);
            }
        }
        final Action ContinueAction = new ContinueButton("Continue", "Hotkey:");
        String attackName = "Attack";
        if (w.tickle()) {
            attackName = "Poke";
        }
        class AttackButton extends AbstractAction
        {
            public AttackButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.finalBattle && w.getTechs()[44].isOwned()) {
                    w.finalAttack(t, w, c);
                }
                else {
                    c.Attack(t, p, f, w);
                }
                Project.advanceAction(p, w, c.getNumber() * 14 + 5);
                p.removeAll();
                final JButton Continue = new JButton(ContinueAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -30);
                    }
                };
                Continue.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                Continue.getActionMap().put("pressed", ContinueAction);
                p.add(Continue);
                p.validate();
                p.repaint();
            }
        }
        final Action AttackAction = new AttackButton(attackName, "Use " + attackName);
        class SlimeButton extends AbstractAction
        {
            public SlimeButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.finalBattle && w.getTechs()[43].isOwned() && c.isHypnotized()) {
                    w.finalSlime(t, w, c);
                }
                else {
                    c.Slime(t, p, f, w);
                }
                Project.advanceAction(p, w, c.getNumber() * 14 + 4);
                p.removeAll();
                final JButton Continue = new JButton(ContinueAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -30);
                    }
                };
                Continue.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                Continue.getActionMap().put("pressed", ContinueAction);
                p.add(Continue);
                p.validate();
                p.repaint();
            }
        }
        final Action SlimeAction = new SlimeButton("Slime", "Use Slime");
        class TauntButton extends AbstractAction
        {
            public TauntButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.finalBattle && w.getTechs()[45].isOwned() && c.isParasitized() && c.surroundPossible(w)) {
                    w.finalTaunt(t, w, c);
                }
                else {
                    c.Taunt(t, p, f, w);
                }
                Project.advanceAction(p, w, c.getNumber() * 14 + 6);
                p.removeAll();
                final JButton Continue = new JButton(ContinueAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -30);
                    }
                };
                Continue.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                Continue.getActionMap().put("pressed", ContinueAction);
                p.add(Continue);
                p.validate();
                p.repaint();
            }
        }
        final Action TauntAction = new TauntButton("Taunt", "Use Taunt");
        class ThreatenButton extends AbstractAction
        {
            public ThreatenButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                Boolean impregnatedAlly = false;
                for (int i = 0; i < 3; ++i) {
                    if (i != c.getNumber() && w.getCast()[i] != null && w.getCast()[i].isImpregnated() && w.getCast()[i].alive) {
                        impregnatedAlly = true;
                    }
                }
                if (w.finalBattle && w.getTechs()[42].isOwned() && impregnatedAlly) {
                    w.finalThreaten(t, w, c);
                }
                else {
                    c.Threaten(t, p, f, w);
                }
                Project.advanceAction(p, w, c.getNumber() * 14 + 3);
                p.removeAll();
                final JButton Continue = new JButton(ContinueAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -30);
                    }
                };
                Continue.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                Continue.getActionMap().put("pressed", ContinueAction);
                p.add(Continue);
                p.validate();
                p.repaint();
            }
        }
        final Action ThreatenAction = new ThreatenButton("Threaten", "Use Threaten");
        class GrindButton extends AbstractAction
        {
            public GrindButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.beginGrind();
                Project.advanceAction(p, w, c.getNumber() * 14 + 7);
                Project.EnemyTurn(t, p, f, w, initiative, 0);
            }
        }
        final Action GrindAction = new GrindButton("Grind", "Use Grind");
        class CaressButton extends AbstractAction
        {
            public CaressButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.beginCaress();
                Project.advanceAction(p, w, c.getNumber() * 14 + 8);
                Project.EnemyTurn(t, p, f, w, initiative, 0);
            }
        }
        final Action CaressAction = new CaressButton("Caress", "Use Caress");
        String pummelName = "Pummel";
        if (w.tickle()) {
            pummelName = "Tickle";
        }
        class PummelButton extends AbstractAction
        {
            public PummelButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.beginPummel();
                Project.advanceAction(p, w, c.getNumber() * 14 + 9);
                Project.EnemyTurn(t, p, f, w, initiative, 0);
            }
        }
        final Action PummelAction = new PummelButton(pummelName, "Use " + pummelName);
        class HumiliateButton extends AbstractAction
        {
            public HumiliateButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.beginHumiliate();
                Project.advanceAction(p, w, c.getNumber() * 14 + 10);
                Project.EnemyTurn(t, p, f, w, initiative, 0);
            }
        }
        final Action HumiliateAction = new HumiliateButton("Humiliate", "Use Humiliate");
        class InseminateButton extends AbstractAction
        {
            public InseminateButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.beginInseminate();
                Project.advanceAction(p, w, c.getNumber() * 14 + 11);
                Project.EnemyTurn(t, p, f, w, initiative, 0);
            }
        }
        final Action InseminateAction = new InseminateButton("Inseminate", "Use Inseminate");
        class ForceOrgasmButton extends AbstractAction
        {
            public ForceOrgasmButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.beginOrgasm();
                Project.advanceAction(p, w, c.getNumber() * 14 + 12);
                Project.EnemyTurn(t, p, f, w, initiative, 0);
            }
        }
        final Action ForceOrgasmAction = new ForceOrgasmButton("Force Orgasm", "Use Force Orgasm");
        String SodomizeName = "Sodomize";
        if (w.tickle()) {
            SodomizeName = "Force Laughter";
        }
        else if (c.getGender().equals("male")) {
            SodomizeName = "Torture";
        }
        class SodomizeButton extends AbstractAction
        {
            public SodomizeButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.beginSodomize();
                Project.advanceAction(p, w, c.getNumber() * 14 + 13);
                Project.EnemyTurn(t, p, f, w, initiative, 0);
            }
        }
        final Action SodomizeAction = new SodomizeButton(SodomizeName, "Use " + SodomizeName);
        class BroadcastButton extends AbstractAction
        {
            public BroadcastButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                c.beginBroadcast();
                Project.advanceAction(p, w, c.getNumber() * 14 + 14);
                Project.EnemyTurn(t, p, f, w, initiative, 0);
            }
        }
        final Action BroadcastAction = new BroadcastButton("Broadcast", "Use Broadcast");
        class TemptButton extends AbstractAction
        {
            public TemptButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.finalBattle) {
                    w.finalTempt(t, c);
                }
                else {
                    c.beginTempt();
                }
                Project.advanceAction(p, w, c.getNumber() + 44);
                if (w.finalBattle) {
                    p.removeAll();
                    final JButton Continue = new JButton(ContinueAction) {
                        @Override
                        public Point getToolTipLocation(final MouseEvent e) {
                            return new Point(0, -30);
                        }
                    };
                    Continue.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                    Continue.getActionMap().put("pressed", ContinueAction);
                    p.add(Continue);
                    p.validate();
                    p.repaint();
                }
                else {
                    Project.EnemyTurn(t, p, f, w, initiative, 0);
                }
            }
        }
        final Action TemptAction = new TemptButton("Tempt", "Use Tempt");
        final int finalInseminated = inseminated;
        final int finalOrgasming = orgasming;
        final int finalSodomized = sodomized;
        final int finalBroadcasted = broadcasted;
        class SurroundButton extends AbstractAction
        {
            public SurroundButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
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
                    if (!c.getGrind()) {
                        final JButton Grind = new JButton(GrindAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        Grind.setToolTipText("<html><center>Inflicts HATE along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Can cause tier-1 Morality or Dignity Break</center></html>");
                        p.add(Grind);
                        Grind.getInputMap(2).put(KeyStroke.getKeyStroke("1"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 7) {
                            Grind.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Grind.getActionMap().put("pressed", GrindAction);
                    }
                    if (!c.getCaress()) {
                        final JButton Caress = new JButton(CaressAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        Caress.setToolTipText("<html><center>Inflicts PLEA along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Can cause tier-1 Innocence or Confidence Break</center></html>");
                        p.add(Caress);
                        Caress.getInputMap(2).put(KeyStroke.getKeyStroke("2"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 8) {
                            Caress.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Caress.getActionMap().put("pressed", CaressAction);
                    }
                    if (!c.getPummel()) {
                        final JButton Pummel = new JButton(PummelAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        Pummel.setToolTipText("<html><center>Inflicts " + INJUname + " along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Can cause tier-1 Morality or Confidence Break</center></html>");
                        p.add(Pummel);
                        Pummel.getInputMap(2).put(KeyStroke.getKeyStroke("3"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 9) {
                            Pummel.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Pummel.getActionMap().put("pressed", PummelAction);
                    }
                    if (!c.getHumiliate()) {
                        final JButton Humiliate = new JButton(HumiliateAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        Humiliate.setToolTipText("<html><center>Inflicts EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Can cause tier-1 Innocence or Dignity Break</center></html>");
                        p.add(Humiliate);
                        Humiliate.getInputMap(2).put(KeyStroke.getKeyStroke("4"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 10) {
                            Humiliate.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Humiliate.getActionMap().put("pressed", HumiliateAction);
                    }
                    if (c.getCurrentHATE() >= 10000L) {
                        ++defilers;
                        final JButton Inseminate = new JButton(InseminateAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        if (finalInseminated > 0) {
                            Inseminate.setBackground(PURPLISH);
                            if (finalInseminated == 1) {
                                Inseminate.setText("Inseminate+");
                                plusPossible = true;
                            }
                            else {
                                Inseminate.setText("Orgy");
                                orgyPossible = true;
                            }
                        }
                        else {
                            Inseminate.setBackground(YELLOWISH);
                        }
                        if (c.temptReq < 100000L && finalInseminated != 2) {
                            Inseminate.setBackground(REDDISH);
                        }
                        Inseminate.setToolTipText("<html><center>Inflicts HATE and PLEA along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
                        if (finalInseminated == 1) {
                            Inseminate.setToolTipText("<html><center>Inflicts HATE, PLEA and " + INJUname + " along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
                        }
                        else if (finalInseminated == 2) {
                            Inseminate.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                        }
                        p.add(Inseminate);
                        Inseminate.getInputMap(2).put(KeyStroke.getKeyStroke("5"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 11) {
                            Inseminate.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Inseminate.getActionMap().put("pressed", InseminateAction);
                    }
                    if (c.getCurrentPLEA() >= 10000L) {
                        ++defilers;
                        final JButton ForceOrgasm = new JButton(ForceOrgasmAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        if (finalOrgasming > 0) {
                            ForceOrgasm.setBackground(PURPLISH);
                            if (finalOrgasming == 1) {
                                ForceOrgasm.setText("Force Orgasm+");
                                plusPossible = true;
                            }
                            else {
                                ForceOrgasm.setText("Orgy");
                                orgyPossible = true;
                            }
                        }
                        else {
                            ForceOrgasm.setBackground(YELLOWISH);
                        }
                        ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA and " + INJUname + " along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
                        if (finalOrgasming == 1) {
                            ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA," + INJUname + ", and EXPO along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
                        }
                        else if (finalOrgasming == 2) {
                            ForceOrgasm.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                        }
                        p.add(ForceOrgasm);
                        ForceOrgasm.getInputMap(2).put(KeyStroke.getKeyStroke("6"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 12) {
                            ForceOrgasm.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        ForceOrgasm.getActionMap().put("pressed", ForceOrgasmAction);
                    }
                    if (c.getCurrentINJU() >= 10000L) {
                        ++defilers;
                        final JButton Sodomize = new JButton(SodomizeAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        if (finalSodomized > 0) {
                            Sodomize.setBackground(PURPLISH);
                            if (finalSodomized == 1) {
                                if (w.tickle()) {
                                    Sodomize.setText("Force Laughter+");
                                }
                                else if (c.getGender().equals("male")) {
                                    Sodomize.setText("Torture+");
                                }
                                else {
                                    Sodomize.setText("Sodomize+");
                                }
                                plusPossible = true;
                            }
                            else {
                                Sodomize.setText("Orgy");
                                orgyPossible = true;
                            }
                        }
                        else {
                            Sodomize.setBackground(YELLOWISH);
                        }
                        if (c.temptReq < 100000L && finalSodomized != 2) {
                            Sodomize.setBackground(REDDISH);
                        }
                        Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + " and EXPO along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
                        if (finalSodomized == 1) {
                            Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + ", EXPO, and HATE along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
                        }
                        else if (finalSodomized == 2) {
                            Sodomize.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                        }
                        p.add(Sodomize);
                        Sodomize.getInputMap(2).put(KeyStroke.getKeyStroke("7"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 13) {
                            Sodomize.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Sodomize.getActionMap().put("pressed", SodomizeAction);
                    }
                    if (c.getCurrentEXPO() >= 10000L) {
                        ++defilers;
                        final JButton Broadcast = new JButton(BroadcastAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        if (finalBroadcasted > 0) {
                            Broadcast.setBackground(PURPLISH);
                            if (finalBroadcasted == 1) {
                                Broadcast.setText("Broadcast+");
                                plusPossible = true;
                            }
                            else {
                                Broadcast.setText("Orgy");
                                orgyPossible = true;
                            }
                        }
                        else {
                            Broadcast.setBackground(YELLOWISH);
                        }
                        Broadcast.setToolTipText("<html><center>Inflicts EXPO and HATE along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
                        if (finalBroadcasted == 1) {
                            Broadcast.setToolTipText("<html><center>Inflicts EXPO, HATE, and PLEA along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
                        }
                        else if (finalBroadcasted == 2) {
                            Broadcast.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                        }
                        p.add(Broadcast);
                        Broadcast.getInputMap(2).put(KeyStroke.getKeyStroke("8"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 14) {
                            Broadcast.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Broadcast.getActionMap().put("pressed", BroadcastAction);
                    }
                    long currentTemptReq = c.temptReq;
                    if (w.finalBattle) {
                        currentTemptReq *= 10L;
                    }
                    if (c.getCurrentPLEA() >= currentTemptReq && c.vVirg && c.aVirg && !c.cVirg && !c.modest && !c.ruthless && !c.usingSlaughter && !c.usingDetonate && (c.temptReq < 100000L || !w.finalBattle)) {
                        final JButton Tempt = new JButton(TemptAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        Tempt.setToolTipText("<html><center>Inflicts extremely high PLEA and EXPO<br>but decreases other circumstances to zero and does not inflict trauma<br>Causes and intensifies Morality/Confidence Distortion</center></html>");
                        Tempt.setBackground(PURPLISH);
                        p.add(Tempt);
                        Tempt.getInputMap(2).put(KeyStroke.getKeyStroke("9"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 14) {
                            Tempt.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Tempt.getActionMap().put("pressed", TemptAction);
                    }
                    w.append(t, "\n\n" + w.getSeparator() + "\n\nWhat should the Thralls do after surrounding " + c.getMainName() + "?");
                    if (defilers > 1) {
                        w.append(t, "  " + defilers + " defiler actions possible.");
                    }
                    else if (defilers == 1) {
                        w.append(t, "  1 defiler action possible.");
                    }
                    int difference = 0;
                    if (orgyPossible) {
                        String firstName = "";
                        String secondName = "";
                        int duration = 0;
                        final int opening = c.getFEAROpening(w) + c.getDISGOpening() + c.getPAINOpening() + c.getSHAMOpening(w) + 1;
                        for (int i = 0; i < 3; ++i) {
                            if (w.getCombatants()[i] != null && w.getCombatants()[i] != c) {
                                if (firstName.length() == 0) {
                                    firstName = w.getCombatants()[i].getMainName();
                                    duration = w.getCombatants()[i].getSurroundDuration();
                                }
                                else {
                                    secondName = w.getCombatants()[i].getMainName();
                                }
                            }
                        }
                        w.append(t, "  Orgy with " + firstName + " and " + secondName);
                        if (duration > opening) {
                            difference = duration - opening;
                            w.append(t, " will cause them");
                        }
                        else if (opening > duration) {
                            difference = opening - duration;
                            w.append(t, " will allow " + c.getMainName());
                        }
                        if (difference > 1) {
                            w.append(t, " to escape " + difference + " turns early.");
                        }
                        else if (difference == 1) {
                            w.append(t, " to escape 1 turn early.");
                        }
                        else {
                            w.append(t, " does not allow any of them to escape early.");
                        }
                    }
                    else if (plusPossible) {
                        final int opening2 = c.getFEAROpening(w) + c.getDISGOpening() + c.getPAINOpening() + c.getSHAMOpening(w) + 1;
                        for (int j = 0; j < 3; ++j) {
                            if (w.getCombatants()[j] != null && w.getCombatants()[j] != c) {
                                String defilementType = "";
                                if (w.getCombatants()[j].isInseminated() && c.getHATELevel() >= 3) {
                                    defilementType = "Inseminate";
                                }
                                else if (w.getCombatants()[j].isOrgasming() && c.getPLEALevel() >= 3) {
                                    defilementType = "Force Orgasm";
                                }
                                else if (w.getCombatants()[j].isSodomized() && c.getINJULevel() >= 3) {
                                    if (w.tickle()) {
                                        defilementType = "Force Laughter";
                                    }
                                    else if (c.getGender().equals("male")) {
                                        defilementType = "Torture";
                                    }
                                    else {
                                        defilementType = "Sodomize";
                                    }
                                }
                                else if (w.getCombatants()[j].isBroadcasted() && c.getEXPOLevel() >= 3) {
                                    defilementType = "Broadcast";
                                }
                                if (defilementType.length() > 0) {
                                    w.append(t, "  " + defilementType + " with " + w.getCombatants()[j].getMainName());
                                    if (opening2 > w.getCombatants()[j].getSurroundDuration()) {
                                        w.append(t, " will allow " + c.getMainName());
                                        difference = opening2 - w.getCombatants()[j].getSurroundDuration();
                                    }
                                    else if (w.getCombatants()[j].getSurroundDuration() > opening2) {
                                        w.append(t, " will allow " + w.getCombatants()[j].getMainName());
                                        difference = w.getCombatants()[j].getSurroundDuration() - opening2;
                                    }
                                    if (difference > 1) {
                                        w.append(t, " to escape " + difference + " turns early.");
                                    }
                                    else if (difference == 1) {
                                        w.append(t, " to escape 1 turn early.");
                                    }
                                    else {
                                        w.append(t, " does not allow either of them to escape early.");
                                    }
                                    difference = 0;
                                }
                            }
                        }
                    }
                    final JButton Back = new JButton("Cancel");
                    Back.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            w.setSurroundTarget(null);
                            Project.PickAction(t, p, f, w, c, initiative);
                        }
                    });
                    p.add(Back);
                    p.validate();
                    p.repaint();
                }
                else {
                    Project.advanceAction(p, w, c.getNumber() * 14 + 1);
                    Project.EnemyTurn(t, p, f, w, initiative, 0);
                }
            }
        }
        final Action SurroundAction = new SurroundButton("Surround", "Hotkey:");
        class CaptureButton extends AbstractAction
        {
            public CaptureButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                Boolean directlyAdvance = true;
                if (w.upgradedCommander()) {
                    w.setCaptureTarget(c);
                }
                else {
                    w.setSurroundTarget(c);
                    if (w.getTechs()[31].isOwned()) {
                        directlyAdvance = false;
                    }
                }
                if (directlyAdvance) {
                    Project.advanceAction(p, w, c.getNumber() * 14 + 2);
                    Project.EnemyTurn(t, p, f, w, initiative, 0);
                }
                else {
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
                    if (!c.getGrind()) {
                        final JButton Grind = new JButton(GrindAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        Grind.setToolTipText("<html><center>Inflicts HATE along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Can cause tier-1 Morality or Dignity Break</center></html>");
                        p.add(Grind);
                        Grind.getInputMap(2).put(KeyStroke.getKeyStroke("1"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 7) {
                            Grind.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Grind.getActionMap().put("pressed", GrindAction);
                    }
                    if (!c.getCaress()) {
                        final JButton Caress = new JButton(CaressAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        Caress.setToolTipText("<html><center>Inflicts PLEA along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Can cause tier-1 Innocence or Confidence Break</center></html>");
                        p.add(Caress);
                        Caress.getInputMap(2).put(KeyStroke.getKeyStroke("2"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 8) {
                            Caress.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Caress.getActionMap().put("pressed", CaressAction);
                    }
                    if (!c.getPummel()) {
                        final JButton Pummel = new JButton(PummelAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        Pummel.setToolTipText("<html><center>Inflicts " + INJUname + " along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Can cause tier-1 Morality or Confidence Break</center></html>");
                        p.add(Pummel);
                        Pummel.getInputMap(2).put(KeyStroke.getKeyStroke("3"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 9) {
                            Pummel.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Pummel.getActionMap().put("pressed", PummelAction);
                    }
                    if (!c.getHumiliate()) {
                        final JButton Humiliate = new JButton(HumiliateAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        Humiliate.setToolTipText("<html><center>Inflicts EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Can cause tier-1 Innocence or Dignity Break</center></html>");
                        p.add(Humiliate);
                        Humiliate.getInputMap(2).put(KeyStroke.getKeyStroke("4"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 10) {
                            Humiliate.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Humiliate.getActionMap().put("pressed", HumiliateAction);
                    }
                    if (c.getCurrentHATE() >= 10000L) {
                        ++defilers;
                        final JButton Inseminate = new JButton(InseminateAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        if (finalInseminated > 0) {
                            Inseminate.setBackground(PURPLISH);
                            if (finalInseminated == 1) {
                                Inseminate.setText("Inseminate+");
                                plusPossible = true;
                            }
                            else {
                                Inseminate.setText("Orgy");
                                orgyPossible = true;
                            }
                        }
                        else {
                            Inseminate.setBackground(YELLOWISH);
                        }
                        if (c.temptReq < 100000L && finalInseminated != 2) {
                            Inseminate.setBackground(REDDISH);
                        }
                        Inseminate.setToolTipText("<html><center>Inflicts HATE and PLEA along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
                        if (finalInseminated == 1) {
                            Inseminate.setToolTipText("<html><center>Inflicts HATE, PLEA and " + INJUname + " along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
                        }
                        else if (finalInseminated == 2) {
                            Inseminate.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                        }
                        p.add(Inseminate);
                        Inseminate.getInputMap(2).put(KeyStroke.getKeyStroke("5"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 11) {
                            Inseminate.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Inseminate.getActionMap().put("pressed", InseminateAction);
                    }
                    if (c.getCurrentPLEA() >= 10000L) {
                        ++defilers;
                        final JButton ForceOrgasm = new JButton(ForceOrgasmAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        if (finalOrgasming > 0) {
                            ForceOrgasm.setBackground(PURPLISH);
                            if (finalOrgasming == 1) {
                                ForceOrgasm.setText("Force Orgasm+");
                                plusPossible = true;
                            }
                            else {
                                ForceOrgasm.setText("Orgy");
                                orgyPossible = true;
                            }
                        }
                        else {
                            ForceOrgasm.setBackground(YELLOWISH);
                        }
                        ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA and " + INJUname + " along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
                        if (finalOrgasming == 1) {
                            ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA," + INJUname + ", and EXPO along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
                        }
                        else if (finalOrgasming == 2) {
                            ForceOrgasm.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                        }
                        p.add(ForceOrgasm);
                        ForceOrgasm.getInputMap(2).put(KeyStroke.getKeyStroke("6"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 12) {
                            ForceOrgasm.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        ForceOrgasm.getActionMap().put("pressed", ForceOrgasmAction);
                    }
                    if (c.getCurrentINJU() >= 10000L) {
                        ++defilers;
                        final JButton Sodomize = new JButton(SodomizeAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        if (finalSodomized > 0) {
                            Sodomize.setBackground(PURPLISH);
                            if (finalSodomized == 1) {
                                if (w.tickle()) {
                                    Sodomize.setText("Force Laughter+");
                                }
                                else if (c.getGender().equals("male")) {
                                    Sodomize.setText("Torture+");
                                }
                                else {
                                    Sodomize.setText("Sodomize+");
                                }
                                plusPossible = true;
                            }
                            else {
                                Sodomize.setText("Orgy");
                                orgyPossible = true;
                            }
                        }
                        else {
                            Sodomize.setBackground(YELLOWISH);
                        }
                        if (c.temptReq < 100000L && finalSodomized != 2) {
                            Sodomize.setBackground(REDDISH);
                        }
                        Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + " and EXPO along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
                        if (finalSodomized == 1) {
                            Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + ", EXPO, and HATE along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
                        }
                        else if (finalSodomized == 2) {
                            Sodomize.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                        }
                        p.add(Sodomize);
                        Sodomize.getInputMap(2).put(KeyStroke.getKeyStroke("7"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 13) {
                            Sodomize.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Sodomize.getActionMap().put("pressed", SodomizeAction);
                    }
                    if (c.getCurrentEXPO() >= 10000L) {
                        ++defilers;
                        final JButton Broadcast = new JButton(BroadcastAction) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -60);
                            }
                        };
                        if (finalBroadcasted > 0) {
                            Broadcast.setBackground(PURPLISH);
                            if (finalBroadcasted == 1) {
                                Broadcast.setText("Broadcast+");
                                plusPossible = true;
                            }
                            else {
                                Broadcast.setText("Orgy");
                                orgyPossible = true;
                            }
                        }
                        else {
                            Broadcast.setBackground(YELLOWISH);
                        }
                        Broadcast.setToolTipText("<html><center>Inflicts EXPO and HATE along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
                        if (finalBroadcasted == 1) {
                            Broadcast.setToolTipText("<html><center>Inflicts EXPO, HATE, and PLEA along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
                        }
                        else if (finalBroadcasted == 2) {
                            Broadcast.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                        }
                        p.add(Broadcast);
                        Broadcast.getInputMap(2).put(KeyStroke.getKeyStroke("8"), "pressed");
                        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 14) {
                            Broadcast.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                        }
                        Broadcast.getActionMap().put("pressed", BroadcastAction);
                    }
                    w.append(t, "\n\n" + w.getSeparator() + "\n\nWhat should the Thralls do after surrounding " + c.getMainName() + "?");
                    if (defilers > 1) {
                        w.append(t, "  " + defilers + " defiler actions possible.");
                    }
                    else if (defilers == 1) {
                        w.append(t, "  1 defiler action possible.");
                    }
                    int difference = 0;
                    if (orgyPossible) {
                        String firstName = "";
                        String secondName = "";
                        int duration = 0;
                        final int opening = w.getCaptureDuration() + 1;
                        for (int i = 0; i < 3; ++i) {
                            if (w.getCombatants()[i] != null && w.getCombatants()[i] != c) {
                                if (firstName.length() == 0) {
                                    firstName = w.getCombatants()[i].getMainName();
                                    duration = w.getCombatants()[i].getSurroundDuration();
                                }
                                else {
                                    secondName = w.getCombatants()[i].getMainName();
                                }
                            }
                        }
                        w.append(t, "  Orgy with " + firstName + " and " + secondName);
                        if (duration > opening) {
                            difference = duration - opening;
                            w.append(t, " will cause them");
                        }
                        else if (opening > duration) {
                            difference = opening - duration;
                            w.append(t, " will allow " + c.getMainName());
                        }
                        if (difference > 1) {
                            w.append(t, " to escape " + difference + " turns early.");
                        }
                        else if (difference == 1) {
                            w.append(t, " to escape 1 turn early.");
                        }
                        else {
                            w.append(t, " does not allow any of them to escape early.");
                        }
                    }
                    else if (plusPossible) {
                        final int opening2 = w.getCaptureDuration() + 1;
                        for (int j = 0; j < 3; ++j) {
                            if (w.getCombatants()[j] != null && w.getCombatants()[j] != c) {
                                String defilementType = "";
                                if (w.getCombatants()[j].isInseminated() && c.getHATELevel() >= 3) {
                                    defilementType = "Inseminate";
                                }
                                else if (w.getCombatants()[j].isOrgasming() && c.getPLEALevel() >= 3) {
                                    defilementType = "Force Orgasm";
                                }
                                else if (w.getCombatants()[j].isSodomized() && c.getINJULevel() >= 3) {
                                    if (w.tickle()) {
                                        defilementType = "Force Laughter";
                                    }
                                    else if (c.getGender().equals("male")) {
                                        defilementType = "Torture";
                                    }
                                    else {
                                        defilementType = "Sodomize";
                                    }
                                }
                                else if (w.getCombatants()[j].isBroadcasted() && c.getEXPOLevel() >= 3) {
                                    defilementType = "Broadcast";
                                }
                                if (defilementType.length() > 0) {
                                    w.append(t, "  " + defilementType + " with " + w.getCombatants()[j].getMainName());
                                    if (opening2 > w.getCombatants()[j].getSurroundDuration()) {
                                        w.append(t, " will allow " + c.getMainName());
                                        difference = opening2 - w.getCombatants()[j].getSurroundDuration();
                                    }
                                    else if (w.getCombatants()[j].getSurroundDuration() > opening2) {
                                        w.append(t, " will allow " + w.getCombatants()[j].getMainName());
                                        difference = w.getCombatants()[j].getSurroundDuration() - opening2;
                                    }
                                    if (difference > 1) {
                                        w.append(t, " to escape " + difference + " turns early.");
                                    }
                                    else if (difference == 1) {
                                        w.append(t, " to escape 1 turn early.");
                                    }
                                    else {
                                        w.append(t, " does not allow either of them to escape early.");
                                    }
                                    difference = 0;
                                }
                            }
                        }
                    }
                    final JButton Back = new JButton("Cancel");
                    Back.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            w.setSurroundTarget(null);
                            Project.PickAction(t, p, f, w, c, initiative);
                        }
                    });
                    p.add(Back);
                    p.validate();
                    p.repaint();
                }
            }
        }
        final Action CaptureAction = new CaptureButton("Capture", "Use Capture");
        class ExamineButton extends AbstractAction
        {
            public ExamineButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.Examine(t, p, f, c);
            }
        }
        final Action ExamineAction = new ExamineButton("Examine", "Hotkey:");
        p.removeAll();
        w.append(t, "\n\n");
        c.printStatus(t, w);
        if (!c.isCaptured() && !c.isDefiled()) {
            w.append(t, "\n\nChoose your action.");
            if (w.usedForsaken != null && c.defenseLevel < 9000) {
                if (w.usedForsaken.injured == 0 && w.commanderFree()) {
                    w.append(t, "  " + w.usedForsaken.mainName + " can ");
                }
                else {
                    w.append(t, "  Once " + w.usedForsaken.mainName + " is ready, " + w.usedForsaken.heShe() + " will be able to ");
                }
                w.append(t, "capture " + c.mainName + " for " + w.usedForsaken.compatibility(c) + " rounds.");
            }
        }
        final JButton Examine = new JButton(ExamineAction) {
            @Override
            public Point getToolTipLocation(final MouseEvent e) {
                return new Point(0, -30);
            }
        };
        Examine.setForeground(Color.GRAY);
        p.add(Examine);
        Examine.getInputMap(2).put(KeyStroke.getKeyStroke(88, 0), "pressed");
        Examine.getActionMap().put("pressed", ExamineAction);
        class PassButton extends AbstractAction
        {
            public PassButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.advanceAction(p, w, 0);
                if (w.getTechs()[30].isOwned() && !w.progressExtermination(0)) {
                    p.removeAll();
                    w.increaseBarrier(t);
                    class ContinueButton extends AbstractAction
                    {
                        public ContinueButton(final String desc) {
                            //super(text);
                            this.putValue("ShortDescription", desc);
                        }
                        
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            Project.EnemyTurn(t, p, f, w, initiative, 0);
                        }
                    }
                    final Action ContinueAction = new ContinueButton("Continue");
                    final JButton Continue = new JButton(ContinueAction) {
                        @Override
                        public Point getToolTipLocation(final MouseEvent e) {
                            return new Point(0, -30);
                        }
                    };
                    Continue.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                    Continue.getActionMap().put("pressed", ContinueAction);
                    p.add(Continue);
                    p.validate();
                    p.repaint();
                }
                else {
                    Project.EnemyTurn(t, p, f, w, initiative, 0);
                }
            }
        }
        final Action PassAction = new PassButton("Do Nothing", "Hotkey:");
        final JButton Pass = new JButton(PassAction) {
            @Override
            public Point getToolTipLocation(final MouseEvent e) {
                return new Point(0, -30);
            }
        };
        Pass.setForeground(Color.GRAY);
        Pass.getInputMap(2).put(KeyStroke.getKeyStroke(70, 0), "pressed");
        if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == 0) {
            Pass.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
        }
        Pass.getActionMap().put("pressed", PassAction);
        if (w.getTechs()[30].isOwned() && !w.progressExtermination(0)) {
            Pass.setText("Barrier");
            Pass.setToolTipText("+5% damage for rest of battle");
        }
        if (c.isDefiled()) {
            w.append(t, "\n\nThe Thralls have been driven into a frenzy ");
            if (c.isInseminated()) {
                w.append(t, "inseminating " + c.getMainName());
            }
            else if (c.isOrgasming()) {
                w.append(t, "forcing " + c.getMainName() + " to orgasm");
            }
            else if (c.isSodomized()) {
                if (w.tickle()) {
                    w.append(t, "forcing " + c.getMainName() + " to laugh");
                }
                else if (c.getGender().equals("male")) {
                    w.append(t, "torturing " + c.getMainName());
                }
                else {
                    w.append(t, "sodomizing " + c.getMainName());
                }
            }
            else if (c.isBroadcasted()) {
                w.append(t, "broadcasting " + c.getMainName() + "'s humiliation");
            }
            else if (c.tempted) {
                w.append(t, "giving " + c.mainName + " all the pleasure " + c.heShe() + " wants");
            }
            w.append(t, ".  Any additional orders would simply confuse them right now.");
        }
        else if (c.isSurrounded()) {
            int defilers = 0;
            String PAINname = "PAIN";
            String INJUname = "INJU";
            if (w.tickle()) {
                PAINname = "TICK";
                INJUname = "ANTI";
            }
            Boolean plusPossible = false;
            Boolean orgyPossible = false;
            if (!c.getGrind()) {
                final JButton Grind = new JButton(GrindAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                Grind.setToolTipText("<html><center>Inflicts HATE along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Can cause tier-1 Morality or Dignity Break</center></html>");
                p.add(Grind);
                Grind.getInputMap(2).put(KeyStroke.getKeyStroke("1"), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 7) {
                    Grind.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Grind.getActionMap().put("pressed", GrindAction);
            }
            if (!c.getCaress()) {
                final JButton Caress = new JButton(CaressAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                Caress.setToolTipText("<html><center>Inflicts PLEA along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Can cause tier-1 Innocence or Confidence Break</center></html>");
                p.add(Caress);
                Caress.getInputMap(2).put(KeyStroke.getKeyStroke("2"), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 8) {
                    Caress.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Caress.getActionMap().put("pressed", CaressAction);
            }
            if (!c.getPummel()) {
                final JButton Pummel = new JButton(PummelAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                Pummel.setToolTipText("<html><center>Inflicts " + INJUname + " along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Can cause tier-1 Morality or Confidence Break</center></html>");
                p.add(Pummel);
                Pummel.getInputMap(2).put(KeyStroke.getKeyStroke("3"), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 9) {
                    Pummel.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Pummel.getActionMap().put("pressed", PummelAction);
            }
            if (!c.getHumiliate()) {
                final JButton Humiliate = new JButton(HumiliateAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                Humiliate.setToolTipText("<html><center>Inflicts EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Can cause tier-1 Innocence or Dignity Break</center></html>");
                p.add(Humiliate);
                Humiliate.getInputMap(2).put(KeyStroke.getKeyStroke("4"), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 10) {
                    Humiliate.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Humiliate.getActionMap().put("pressed", HumiliateAction);
            }
            if (c.getCurrentHATE() >= 10000L) {
                ++defilers;
                final JButton Inseminate = new JButton(InseminateAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                if (inseminated > 0) {
                    Inseminate.setBackground(PURPLISH);
                    if (inseminated == 1) {
                        Inseminate.setText("Inseminate+");
                        plusPossible = true;
                    }
                    else {
                        Inseminate.setText("Orgy");
                        orgyPossible = true;
                    }
                }
                else {
                    Inseminate.setBackground(YELLOWISH);
                }
                if (c.temptReq < 100000L && inseminated != 2) {
                    Inseminate.setBackground(REDDISH);
                }
                Inseminate.setToolTipText("<html><center>Inflicts HATE and PLEA along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
                if (inseminated == 1) {
                    Inseminate.setToolTipText("<html><center>Inflicts HATE, PLEA and " + INJUname + " along with<br>FEAR, DISG, " + PAINname + ", and SHAM<br>Causes tier-2 Morality Break</center></html>");
                }
                else if (inseminated == 2) {
                    Inseminate.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                }
                p.add(Inseminate);
                Inseminate.getInputMap(2).put(KeyStroke.getKeyStroke("5"), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 11) {
                    Inseminate.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Inseminate.getActionMap().put("pressed", InseminateAction);
            }
            if (c.getCurrentPLEA() >= 10000L) {
                ++defilers;
                final JButton ForceOrgasm = new JButton(ForceOrgasmAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                if (orgasming > 0) {
                    ForceOrgasm.setBackground(PURPLISH);
                    if (orgasming == 1) {
                        ForceOrgasm.setText("Force Orgasm+");
                        plusPossible = true;
                    }
                    else {
                        ForceOrgasm.setText("Orgy");
                        orgyPossible = true;
                    }
                }
                else {
                    ForceOrgasm.setBackground(YELLOWISH);
                }
                ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA and " + INJUname + " along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
                if (orgasming == 1) {
                    ForceOrgasm.setToolTipText("<html><center>Inflicts PLEA," + INJUname + ", and EXPO along with<br>DISG, " + PAINname + ", SHAM, and FEAR<br>Causes tier-2 Innocence Break</center></html>");
                }
                else if (orgasming == 2) {
                    ForceOrgasm.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                }
                p.add(ForceOrgasm);
                ForceOrgasm.getInputMap(2).put(KeyStroke.getKeyStroke("6"), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 12) {
                    ForceOrgasm.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                ForceOrgasm.getActionMap().put("pressed", ForceOrgasmAction);
            }
            if (c.getCurrentINJU() >= 10000L) {
                ++defilers;
                final JButton Sodomize = new JButton(SodomizeAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                if (sodomized > 0) {
                    Sodomize.setBackground(PURPLISH);
                    if (sodomized == 1) {
                        if (w.tickle()) {
                            Sodomize.setText("Force Laughter+");
                        }
                        else if (c.getGender().equals("male")) {
                            Sodomize.setText("Torture+");
                        }
                        else {
                            Sodomize.setText("Sodomize+");
                        }
                        plusPossible = true;
                    }
                    else {
                        Sodomize.setText("Orgy");
                        orgyPossible = true;
                    }
                }
                else {
                    Sodomize.setBackground(YELLOWISH);
                }
                if (c.temptReq < 100000L && sodomized != 2) {
                    Sodomize.setBackground(REDDISH);
                }
                Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + " and EXPO along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
                if (sodomized == 1) {
                    Sodomize.setToolTipText("<html><center>Inflicts " + INJUname + ", EXPO, and HATE along with<br>" + PAINname + ", SHAM, FEAR, and DISG<br>Causes tier-2 Confidence Break</center></html>");
                }
                else if (sodomized == 2) {
                    Sodomize.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                }
                p.add(Sodomize);
                Sodomize.getInputMap(2).put(KeyStroke.getKeyStroke("7"), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 13) {
                    Sodomize.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Sodomize.getActionMap().put("pressed", SodomizeAction);
            }
            if (c.getCurrentEXPO() >= 10000L) {
                ++defilers;
                final JButton Broadcast = new JButton(BroadcastAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                if (broadcasted > 0) {
                    Broadcast.setBackground(PURPLISH);
                    if (broadcasted == 1) {
                        Broadcast.setText("Broadcast+");
                        plusPossible = true;
                    }
                    else {
                        Broadcast.setText("Orgy");
                        orgyPossible = true;
                    }
                }
                else {
                    Broadcast.setBackground(YELLOWISH);
                }
                Broadcast.setToolTipText("<html><center>Inflicts EXPO and HATE along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
                if (broadcasted == 1) {
                    Broadcast.setToolTipText("<html><center>Inflicts EXPO, HATE, and PLEA along with<br>SHAM, FEAR, DISG, and " + PAINname + "<br>Causes tier-2 Dignity Break</center></html>");
                }
                else if (broadcasted == 2) {
                    Broadcast.setToolTipText("<html><center>Inflicts a high degree of all damage types</html>");
                }
                p.add(Broadcast);
                Broadcast.getInputMap(2).put(KeyStroke.getKeyStroke("8"), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 14) {
                    Broadcast.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Broadcast.getActionMap().put("pressed", BroadcastAction);
            }
            long currentTemptReq = c.temptReq;
            if (w.finalBattle) {
                currentTemptReq *= 10L;
            }
            if (c.getCurrentPLEA() >= currentTemptReq && c.vVirg && c.aVirg && !c.cVirg && !c.modest && !c.ruthless && !c.usingSlaughter && !c.usingDetonate && (c.temptReq < 100000L || !w.finalBattle)) {
                final JButton Tempt = new JButton(TemptAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                Tempt.setToolTipText("<html><center>Inflicts extremely high PLEA and EXPO<br>but decreases other circumstances to zero and does not inflict trauma<br>Causes and intensifies Morality/Confidence Distortion</center></html>");
                Tempt.setBackground(PURPLISH);
                p.add(Tempt);
                Tempt.getInputMap(2).put(KeyStroke.getKeyStroke("9"), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 14) {
                    Tempt.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Tempt.getActionMap().put("pressed", TemptAction);
            }
            if (defilers > 0) {
                if (defilers > 1) {
                    w.append(t, "  " + defilers + " defiler actions possible.");
                }
                else if (defilers == 1) {
                    w.append(t, "  1 defiler action possible.");
                }
                int difference = 0;
                if (orgyPossible) {
                    String firstName = "";
                    String secondName = "";
                    int duration = 0;
                    final int opening = c.getSurroundDuration();
                    for (int j = 0; j < 3; ++j) {
                        if (w.getCombatants()[j] != null && w.getCombatants()[j] != c) {
                            if (firstName.length() == 0) {
                                firstName = w.getCombatants()[j].getMainName();
                                duration = w.getCombatants()[j].getSurroundDuration();
                            }
                            else {
                                secondName = w.getCombatants()[j].getMainName();
                            }
                        }
                    }
                    w.append(t, "  Orgy with " + firstName + " and " + secondName);
                    if (duration > opening) {
                        difference = duration - opening;
                        w.append(t, " will cause them");
                    }
                    else if (opening > duration) {
                        difference = opening - duration;
                        w.append(t, " will allow " + c.getMainName());
                    }
                    if (difference > 1) {
                        w.append(t, " to escape " + difference + " turns early.");
                    }
                    else if (difference == 1) {
                        w.append(t, " to escape 1 turn early.");
                    }
                    else {
                        w.append(t, " does not allow any of them to escape early.");
                    }
                }
                else if (plusPossible) {
                    final int opening2 = c.getSurroundDuration();
                    for (int k = 0; k < 3; ++k) {
                        if (w.getCombatants()[k] != null && w.getCombatants()[k] != c) {
                            String defilementType = "";
                            if (w.getCombatants()[k].isInseminated() && c.getHATELevel() >= 3) {
                                defilementType = "Inseminate";
                            }
                            else if (w.getCombatants()[k].isOrgasming() && c.getPLEALevel() >= 3) {
                                defilementType = "Force Orgasm";
                            }
                            else if (w.getCombatants()[k].isSodomized() && c.getINJULevel() >= 3) {
                                if (w.tickle()) {
                                    defilementType = "Force Laughter";
                                }
                                else if (c.getGender().equals("male")) {
                                    defilementType = "Torture";
                                }
                                else {
                                    defilementType = "Sodomize";
                                }
                            }
                            else if (w.getCombatants()[k].isBroadcasted() && c.getEXPOLevel() >= 3) {
                                defilementType = "Broadcast";
                            }
                            if (defilementType.length() > 0) {
                                w.append(t, "  " + defilementType + " with " + w.getCombatants()[k].getMainName());
                                if (opening2 > w.getCombatants()[k].getSurroundDuration()) {
                                    w.append(t, " will allow " + c.getMainName());
                                    difference = opening2 - w.getCombatants()[k].getSurroundDuration();
                                }
                                else if (w.getCombatants()[k].getSurroundDuration() > opening2) {
                                    w.append(t, " will allow " + w.getCombatants()[k].getMainName());
                                    difference = w.getCombatants()[k].getSurroundDuration() - opening2;
                                }
                                if (difference > 1) {
                                    w.append(t, " to escape " + difference + " turns early.");
                                }
                                else if (difference == 1) {
                                    w.append(t, " to escape 1 turn early.");
                                }
                                else {
                                    w.append(t, " does not allow either of them to escape early.");
                                }
                                difference = 0;
                            }
                        }
                    }
                }
            }
        }
        else if (c.isCaptured()) {
            if (w.usedForsaken == null) {
                w.append(t, "\n\n" + c.getMainName() + " is captured by your Commander.  Any attempts to help by other Demons would simply get in the way.");
            }
            else {
                w.append(t, "\n\n" + c.getMainName() + " is engaged in combat with " + w.usedForsaken.mainName + ".  There's no room for the Demons to get involved.");
            }
        }
        else {
            String PAINname2 = "PAIN";
            String INJUname2 = "INJU";
            if (w.tickle()) {
                PAINname2 = "TICK";
                INJUname2 = "ANTI";
            }
            final JButton Attack = new JButton(AttackAction) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            Attack.setToolTipText("Inflicts " + PAINname2);
            Attack.getInputMap(2).put(KeyStroke.getKeyStroke("3"), "pressed");
            if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 5) {
                Attack.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            }
            Attack.getActionMap().put("pressed", AttackAction);
            if (w.finalBattle && w.getTechs()[44].isOwned()) {
                Attack.setBackground(YELLOWISH);
            }
            final JButton Slime = new JButton(SlimeAction) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            Slime.getInputMap(2).put(KeyStroke.getKeyStroke("2"), "pressed");
            if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 4) {
                Slime.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            }
            Slime.getActionMap().put("pressed", SlimeAction);
            Slime.setToolTipText("Inflicts DISG");
            if (w.finalBattle && w.getTechs()[43].isOwned() && c.isHypnotized()) {
                Slime.setBackground(YELLOWISH);
            }
            final JButton Taunt = new JButton(TauntAction) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            Taunt.setToolTipText("Inflicts SHAM");
            Taunt.getInputMap(2).put(KeyStroke.getKeyStroke("4"), "pressed");
            if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 6) {
                Taunt.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            }
            Taunt.getActionMap().put("pressed", TauntAction);
            if (w.finalBattle && w.getTechs()[45].isOwned() && c.isParasitized() && c.surroundPossible(w)) {
                Taunt.setBackground(YELLOWISH);
            }
            final JButton Threaten = new JButton(ThreatenAction) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            Threaten.setToolTipText("Inflicts FEAR");
            Threaten.getInputMap(2).put(KeyStroke.getKeyStroke("1"), "pressed");
            if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 3) {
                Threaten.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            }
            Threaten.getActionMap().put("pressed", ThreatenAction);
            Boolean impregnatedAlly = false;
            for (int l = 0; l < 3; ++l) {
                if (l != c.getNumber() && w.getCast()[l] != null && w.getCast()[l].isImpregnated() && w.getCast()[l].alive) {
                    impregnatedAlly = true;
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
                final JButton Surround = new JButton(SurroundAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -60);
                    }
                };
                Surround.setBackground(YELLOWISH);
                Surround.getInputMap(2).put(KeyStroke.getKeyStroke(90, 0), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && (w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 1 || (w.getTechs()[31].isOwned() && !c.isSurrounded() && w.getActions()[w.getCurrentAction()] >= c.getNumber() * 14 + 7 && w.getActions()[w.getCurrentAction()] <= c.getNumber() * 14 + 14))) {
                    Surround.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Surround.getActionMap().put("pressed", SurroundAction);
                p.add(Surround);
            }
            if ((!c.surroundPossible(w) || w.upgradedCommander()) && w.getCapturesPossible() > 0 && (c.getDefenseLevel() < 9000 || w.getBodyStatus()[24]) && w.commanderFree()) {
                final JButton Capture = new JButton(CaptureAction) {
                    @Override
                    public Point getToolTipLocation(final MouseEvent e) {
                        return new Point(0, -90);
                    }
                };
                Capture.setBackground(PURPLISH);
                String description = "<html><center>Constantly inflicts ";
                if (w.getBodyStatus()[26]) {
                    int types = 2;
                    final String[] damages = new String[4];
                    if (w.getBodyStatus()[19]) {
                        damages[0] = "HATE";
                    }
                    else if (w.getBodyStatus()[20]) {
                        damages[0] = "PLEA";
                    }
                    else if (w.getBodyStatus()[21]) {
                        if (w.tickle()) {
                            damages[0] = "ANTI";
                        }
                        else {
                            damages[0] = "INJU";
                        }
                    }
                    else if (w.getBodyStatus()[22]) {
                        damages[0] = "EXPO";
                    }
                    if (w.getBodyStatus()[11]) {
                        damages[1] = "HATE";
                        damages[2] = "PLEA";
                    }
                    else if (w.getBodyStatus()[12]) {
                        damages[1] = "PLEA";
                        if (w.tickle()) {
                            damages[2] = "ANTI";
                        }
                        else {
                            damages[2] = "INJU";
                        }
                    }
                    else if (w.getBodyStatus()[13]) {
                        if (w.tickle()) {
                            damages[1] = "ANTI";
                        }
                        else {
                            damages[1] = "INJU";
                        }
                        damages[2] = "EXPO";
                    }
                    else if (w.getBodyStatus()[14]) {
                        damages[1] = "EXPO";
                        damages[2] = "HATE";
                    }
                    if (w.getBodyStatus()[3]) {
                        damages[3] = "HATE";
                    }
                    else if (w.getBodyStatus()[4]) {
                        damages[3] = "PLEA";
                    }
                    else if (w.getBodyStatus()[5]) {
                        if (w.tickle()) {
                            damages[3] = "ANTI";
                        }
                        else {
                            damages[3] = "INJU";
                        }
                    }
                    else if (w.getBodyStatus()[6]) {
                        damages[3] = "EXPO";
                    }
                    if (!damages[1].equals(damages[0]) && !damages[2].equals(damages[0])) {
                        ++types;
                    }
                    if (!damages[3].equals(damages[0]) && !damages[3].equals(damages[1]) && !damages[3].equals(damages[2])) {
                        ++types;
                    }
                    if (types == 2) {
                        description = String.valueOf(description) + damages[0] + " and ";
                        if (damages[0].equals(damages[1])) {
                            description = String.valueOf(description) + damages[2];
                        }
                        else {
                            description = String.valueOf(description) + damages[1];
                        }
                    }
                    else if (types == 3) {
                        description = String.valueOf(description) + damages[0] + ", ";
                        if (damages[0].equals(damages[1])) {
                            description = String.valueOf(description) + damages[3] + ", and " + damages[2];
                        }
                        else if (damages[0].equals(damages[2])) {
                            description = String.valueOf(description) + damages[1] + ", and " + damages[3];
                        }
                        else if (damages[0].equals(damages[3]) || damages[1].equals(damages[3])) {
                            description = String.valueOf(description) + damages[1] + ", and " + damages[2];
                        }
                        else {
                            description = String.valueOf(description) + damages[2] + ", and " + damages[1];
                        }
                    }
                    else {
                        description = String.valueOf(description) + damages[0] + ", " + damages[1] + ", " + damages[3] + ", and " + damages[2];
                    }
                    description = String.valueOf(description) + " along with<br>all four traumas";
                }
                else if (w.getBodyStatus()[19]) {
                    description = String.valueOf(description) + "HATE along with<br>FEAR, DISG, " + PAINname2 + ", and SHAM";
                }
                else if (w.getBodyStatus()[20]) {
                    description = String.valueOf(description) + "PLEA along with<br>DISG, " + PAINname2 + ", SHAM, and FEAR";
                }
                else if (w.getBodyStatus()[21]) {
                    description = String.valueOf(description) + INJUname2 + " along with<br>" + PAINname2 + ", SHAM, FEAR, and DISG";
                }
                else if (w.getBodyStatus()[22]) {
                    description = String.valueOf(description) + "EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname2;
                }
                else if (w.getBodyStatus()[18]) {
                    final String[] damages2 = new String[3];
                    if (w.getBodyStatus()[3]) {
                        damages2[1] = "HATE";
                    }
                    else if (w.getBodyStatus()[4]) {
                        damages2[1] = "PLEA";
                    }
                    else if (w.getBodyStatus()[5]) {
                        damages2[1] = INJUname2;
                    }
                    else if (w.getBodyStatus()[6]) {
                        damages2[1] = "EXPO";
                    }
                    if (w.getBodyStatus()[11]) {
                        damages2[0] = "HATE";
                        damages2[2] = "PLEA";
                    }
                    else if (w.getBodyStatus()[12]) {
                        damages2[0] = "PLEA";
                        damages2[2] = INJUname2;
                    }
                    else if (w.getBodyStatus()[13]) {
                        damages2[0] = INJUname2;
                        damages2[2] = "EXPO";
                    }
                    else if (w.getBodyStatus()[14]) {
                        damages2[0] = "EXPO";
                        damages2[2] = "HATE";
                    }
                    if (damages2[0].equals(damages2[2])) {
                        description = String.valueOf(description) + damages2[0] + " and " + damages2[1];
                    }
                    else if (damages2[1].equals(damages2[2])) {
                        description = String.valueOf(description) + damages2[1] + " and " + damages2[0];
                    }
                    else {
                        description = String.valueOf(description) + damages2[0] + ", " + damages2[1] + ", and " + damages2[2];
                    }
                    description = String.valueOf(description) + " along with<br>all four traumas";
                }
                else if (w.getBodyStatus()[11]) {
                    description = String.valueOf(description) + "HATE and PLEA along with<br>FEAR, DISG, " + PAINname2 + ", and SHAM";
                }
                else if (w.getBodyStatus()[12]) {
                    description = String.valueOf(description) + "PLEA and " + INJUname2 + " along with<br>DISG, " + PAINname2 + ", SHAM, and FEAR";
                }
                else if (w.getBodyStatus()[13]) {
                    description = String.valueOf(description) + INJUname2 + " and EXPO along with<br>" + PAINname2 + ", SHAM, FEAR, and DISG";
                }
                else if (w.getBodyStatus()[14]) {
                    description = String.valueOf(description) + "EXPO and HATE along with<br>SHAM, FEAR, DISG, and " + PAINname2;
                }
                else if (w.getBodyStatus()[10]) {
                    Boolean firstFound = false;
                    if (w.getBodyStatus()[3]) {
                        description = String.valueOf(description) + "HATE";
                        firstFound = true;
                    }
                    if (w.getBodyStatus()[4]) {
                        if (firstFound) {
                            description = String.valueOf(description) + " and ";
                        }
                        description = String.valueOf(description) + "PLEA";
                        firstFound = true;
                    }
                    if (w.getBodyStatus()[5]) {
                        if (firstFound) {
                            description = String.valueOf(description) + " and ";
                        }
                        description = String.valueOf(description) + INJUname2;
                        firstFound = true;
                    }
                    if (w.getBodyStatus()[6]) {
                        description = " and EXPO";
                    }
                    description = String.valueOf(description) + " along with<br>all four traumas";
                }
                else if (w.getBodyStatus()[3]) {
                    description = String.valueOf(description) + "HATE along with<br>FEAR, DISG, " + PAINname2 + ", and SHAM";
                }
                else if (w.getBodyStatus()[4]) {
                    description = String.valueOf(description) + "PLEA along with<br>DISG, " + PAINname2 + ", SHAM, and FEAR";
                }
                else if (w.getBodyStatus()[5]) {
                    description = String.valueOf(description) + INJUname2 + " along with<br>" + PAINname2 + ", SHAM, FEAR, and DISG";
                }
                else if (w.getBodyStatus()[6]) {
                    description = String.valueOf(description) + "EXPO along with<br>SHAM, FEAR, DISG, and " + PAINname2;
                }
                else {
                    description = "<html><center>Surrounds the target";
                }
                description = String.valueOf(description) + "<br>for ";
                if (w.getBodyStatus()[25]) {
                    description = String.valueOf(description) + "eight";
                }
                else if (w.getBodyStatus()[15]) {
                    description = String.valueOf(description) + "six";
                }
                else if (w.getBodyStatus()[9]) {
                    description = String.valueOf(description) + "five";
                }
                else if (w.getBodyStatus()[7]) {
                    description = String.valueOf(description) + "four";
                }
                else if (w.getBodyStatus()[1]) {
                    description = String.valueOf(description) + "three";
                }
                else {
                    description = String.valueOf(description) + "two";
                }
                description = String.valueOf(description) + " rounds";
                if (w.getBodyStatus()[8]) {
                    description = String.valueOf(description) + " (";
                    if (w.getCapturesPossible() == 4) {
                        description = String.valueOf(description) + "four";
                    }
                    else if (w.getCapturesPossible() == 3) {
                        description = String.valueOf(description) + "three";
                    }
                    else if (w.getCapturesPossible() == 2) {
                        description = String.valueOf(description) + "two";
                    }
                    else if (w.getCapturesPossible() == 1) {
                        description = String.valueOf(description) + "one";
                    }
                    description = String.valueOf(description) + " left)";
                }
                if (w.getBodyStatus()[11]) {
                    description = String.valueOf(description) + "<br>Above 10k HATE, causes tier-2 Morality Break";
                }
                else if (w.getBodyStatus()[12]) {
                    description = String.valueOf(description) + "<br>Above 10k PLEA, causes tier-2 Innocence Break";
                }
                else if (w.getBodyStatus()[13]) {
                    description = String.valueOf(description) + "<br>Above 10k " + INJUname2 + ", causes tier-2 Confidence Break";
                }
                else if (w.getBodyStatus()[14]) {
                    description = String.valueOf(description) + "<br>Above 10k EXPO, causes tier-2 Dignity Break";
                }
                if (w.getBodyStatus()[19]) {
                    description = String.valueOf(description) + "<br>Above 1000% Impregnation effectiveness, causes Total Morality Break";
                }
                else if (w.getBodyStatus()[20]) {
                    description = String.valueOf(description) + "<br>Above 1000% Hypnosis effectiveness, causes Total Innocence Break";
                }
                else if (w.getBodyStatus()[21]) {
                    description = String.valueOf(description) + "<br>Above 1000% Drain effectiveness, causes Total Confidence Break";
                }
                else if (w.getBodyStatus()[22]) {
                    description = String.valueOf(description) + "<br>Above 1000% Parasitism effectiveness, causes Total Dignity Break";
                }
                if (w.usedForsaken != null) {
                    description = "<html><center>Grab with " + w.usedForsaken.mainName + " for " + w.usedForsaken.compatibility(c) + " rounds<br>" + w.usedForsaken.describeCombatStyle(w, false);
                }
                description = String.valueOf(description) + "</center></html>";
                Capture.setToolTipText(description);
                Capture.getInputMap(2).put(KeyStroke.getKeyStroke(67, 0), "pressed");
                if (w.onTrack && w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] == c.getNumber() * 14 + 2) {
                    Capture.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                }
                Capture.getActionMap().put("pressed", CaptureAction);
                p.add(Capture);
            }
        }
        int defeated = 0;
        int targets = 0;
        for (int m = 0; m < 3; ++m) {
            if (w.getCombatants()[m] != null) {
                if (w.finalBattle && (!w.getCombatants()[m].alive || w.getCombatants()[m].resolve <= 0)) {
                    ++defeated;
                }
                else if (!w.getCombatants()[m].isCaptured() && (!w.getCombatants()[m].isSurrounded() || (!w.getCombatants()[m].isDefiled() && (w.getCombatants()[m].getHATELevel() >= 3 || w.getCombatants()[m].getPLEALevel() >= 3 || w.getCombatants()[m].getINJULevel() >= 3 || w.getCombatants()[m].getEXPOLevel() >= 3 || !w.getCombatants()[m].grind || !w.getCombatants()[m].caress || !w.getCombatants()[m].pummel || !w.getCombatants()[m].humiliate))) && w.getCombatants()[m] != c) {
                    ++targets;
                }
            }
        }
        if (w.getCombatants()[1] != null && defeated < 2) {
            class BackButton extends AbstractAction
            {
                public BackButton(final String text, final String desc) {
                    super(text);
                    this.putValue("ShortDescription", desc);
                }
                
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.append(t, "\n\n" + w.getSeparator() + "\n");
                    Project.PickTarget(t, p, f, w);
                    if (w.tutorialResponse() && w.getBattleRound() == 6 && c == w.getCast()[2]) {
                        w.grayAppend(t, "\n\n(We created another opening last turn, but because we've already grabbed Miracle once, her defense level has gone up.  We'll need at least three opening levels to grab her again.  Fortunately, she's taken enough FEAR and SHAM damage now that it should be easy to push her over 100 in both.  Target Miracle and then Threaten her.)");
                    }
                }
            }
            final Action BackAction = new BackButton("Back", "Hotkey:");
            final JButton Back = new JButton(BackAction) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            Back.getInputMap(2).put(KeyStroke.getKeyStroke(66, 0), "pressed");
            Back.getActionMap().put("pressed", BackAction);
            p.add(Back);
        }
        else {
            p.add(Pass);
            int occupied = 0;
            for (int i2 = 0; i2 < 3; ++i2) {
                if (w.getCombatants()[i2] != null) {
                    if (w.getCombatants()[i2].isSurrounded() && w.getCombatants()[i2].getSurroundDuration() > 0) {
                        if (w.getCombatants()[i2].getSurroundDuration() > 0) {
                            occupied += w.getCombatants()[i2].getSurroundDuration();
                        }
                        else {
                            ++occupied;
                        }
                    }
                    else if (w.getCombatants()[i2].isCaptured()) {
                        occupied += w.getCaptureDuration() - w.getCombatants()[i2].getCaptureProgression() + 1;
                    }
                }
            }
            final int occupiedBonus = occupied / 5;
            class RetreatButton extends AbstractAction
            {
                public RetreatButton(final String text, final String desc) {
                    super(text);
                    this.putValue("ShortDescription", desc);
                }
                
                @Override
                public void actionPerformed(final ActionEvent e) {
                    p.removeAll();
                    w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                    if (occupiedBonus > 0) {
                        w.append(t, "Retreat and end the battle immediately for +" + occupiedBonus + " Evil Energy?");
                    }
                    else {
                        w.append(t, "Really retreat?  You will not gain any bonus Evil Energy!");
                    }
                    final JButton Confirm = new JButton("Confirm");
                    Confirm.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            p.removeAll();
                            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                            final String[] trapped = new String[3];
                            final String[] free = new String[3];
                            int trappedNumber = 0;
                            for (int i = 0; i < 3; ++i) {
                                if (w.getCombatants()[i] != null) {
                                    if (w.getCombatants()[i].isSurrounded() || w.getCombatants()[i].isCaptured()) {
                                        for (int j = 0; j < 3; ++j) {
                                            if (trapped[j] == null) {
                                                trapped[j] = w.getCombatants()[i].getMainName();
                                                ++trappedNumber;
                                                j = 3;
                                            }
                                        }
                                    }
                                    else {
                                        for (int j = 0; j < 3; ++j) {
                                            if (free[j] == null) {
                                                free[j] = w.getCombatants()[i].getMainName();
                                                j = 3;
                                            }
                                        }
                                    }
                                }
                            }
                            if (w.getCombatants()[1] == null) {
                                for (int i = 0; i < 3; ++i) {
                                    if (w.getCast()[i] != null && !w.getCast()[i].equals(w.getCombatants()[0])) {
                                        if (free[0] == null) {
                                            free[0] = w.getCast()[i].mainName;
                                        }
                                        else if (free[1] == null) {
                                            free[1] = w.getCast()[i].mainName;
                                        }
                                        else {
                                            free[2] = w.getCast()[i].mainName;
                                        }
                                    }
                                }
                            }
                            else if (w.getCombatants()[2] == null) {
                                for (int i = 0; i < 3; ++i) {
                                    if (w.getCast()[i] != null && !w.getCast()[i].equals(w.getCombatants()[0]) && !w.getCast()[i].equals(w.getCombatants()[1])) {
                                        if (free[0] == null) {
                                            free[0] = w.getCast()[i].mainName;
                                        }
                                        else if (free[1] == null) {
                                            free[1] = w.getCast()[i].mainName;
                                        }
                                        else {
                                            free[2] = w.getCast()[i].mainName;
                                        }
                                    }
                                }
                            }
                            w.append(t, "You order your Demons to flee back into the tunnels beneath the city along with their captive victims.  ");
                            if (w.getCast()[1] == null) {
                                if (trappedNumber == 0) {
                                    w.append(t, "However, " + free[0] + " is quick to pursue, cutting your forces down from behind and stopping them from taking any significant number of civilians back to the hive.");
                                }
                                else {
                                    w.append(t, String.valueOf(trapped[0]) + " is unable to follow until plenty of civilians are already on their way to the hive.");
                                }
                            }
                            else if (w.getCast()[2] == null) {
                                if (trappedNumber == 0) {
                                    w.append(t, "However, the two Chosen cut your forces down from behind, freeing most of the civilians before they can be brought to the hive.");
                                }
                                else if (trappedNumber == 1) {
                                    w.append(t, "With " + trapped[0] + " unable to give chase, the risk of splitting the team forces " + free[0] + " to give up and let you take the civilians to the hive.");
                                }
                                else {
                                    w.append(t, "The Chosen have to finish dealing with their own problems before they can try to stop you, and by then, plenty of your forces have escaped.");
                                }
                            }
                            else if (trappedNumber == 0 || occupiedBonus == 0) {
                                w.append(t, "However, the three Chosen cut your forces down from behind, freeing most of the civilians before they can be brought to the hive.");
                            }
                            else if (trappedNumber == 1) {
                                w.append(t, String.valueOf(free[0]) + " and " + free[1] + " try to give chase, but with " + trapped[0] + " unable to follow, they're forced to give up due to the risk of splitting the team.");
                            }
                            else if (trappedNumber == 2) {
                                w.append(t, String.valueOf(free[0]) + " tries to stop them, but with " + trapped[0] + " and " + trapped[1] + " unable to help, you're able to get plenty of victims to the hive.");
                            }
                            else {
                                w.append(t, "The Chosen have to finish dealing with their own problems before they can try to stop you, and by then, plenty of your forces have escaped.");
                            }
                            if (occupiedBonus > 0) {
                                w.append(t, "\n\n+" + occupiedBonus + " Evil Energy");
                            }
                            Project.advanceAction(p, w, 43);
                            w.addEnergy(occupiedBonus);
                            final JButton Continue = new JButton("Continue");
                            Continue.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    Project.PostBattle(t, p, f, w);
                                }
                            });
                            p.add(Continue);
                            p.validate();
                            p.repaint();
                        }
                    });
                    p.add(Confirm);
                    final JButton Cancel = new JButton("Cancel");
                    Cancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            w.append(t, "\n\n" + w.getSeparator() + "\n");
                            Project.PickTarget(t, p, f, w);
                        }
                    });
                    p.add(Cancel);
                    p.validate();
                    p.repaint();
                }
            }
            final Action RetreatAction = new RetreatButton("Retreat (" + occupiedBonus + ")", "End battle immediately for +" + occupiedBonus + " EE");
            final JButton Retreat = new JButton(RetreatAction) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            if (w.getTechs()[19].isOwned() && !w.finalBattle) {
                p.add(Retreat);
            }
        }
        if (w.writePossible()) {
            addWriteButton(p, w);
        }
        p.validate();
        p.repaint();
    }
    
    public static void EnemyTurn(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen[] initiative, int progress) {
        Boolean endgame = w.finalBattle;
        Boolean actorFound;
        for (actorFound = false; !actorFound && progress < initiative.length && initiative[progress] != null; actorFound = false, ++progress) {
            w.clearBonus(progress);
            actorFound = true;
            if (w.finalBattle && (!initiative[progress].alive || initiative[progress].resolve <= 0)) {}
        }
        if (actorFound) {
            if (w.getCaptureTarget() == initiative[progress] || initiative[progress].isCaptured()) {
                if (w.usedForsaken == null) {
                    w.BeCaptured(t, p, f, w, initiative[progress]);
                }
                else {
                    w.usedForsaken.captureChosen(t, p, f, w, initiative[progress]);
                }
            }
            else if (w.getSurroundTarget() == initiative[progress] || initiative[progress].isSurrounded()) {
                initiative[progress].BeSurrounded(t, p, f, w);
            }
            else {
                initiative[progress].TakeTurn(t, p, f, w);
            }
            ++progress;
        }
        p.removeAll();
        final int currentProgress = progress;
        Boolean moreTurns = true;
        if (progress > 2) {
            moreTurns = false;
        }
        else if (initiative[progress] == null) {
            moreTurns = false;
        }
        else if (w.finalBattle && (!initiative[progress].alive || initiative[progress].resolve <= 0)) {
            if (++progress > 2) {
                moreTurns = false;
            }
            else if (initiative[progress] == null) {
                moreTurns = false;
            }
            else if (w.finalBattle && (!initiative[progress].alive || initiative[progress].resolve <= 0)) {
                if (++progress > 2) {
                    moreTurns = false;
                }
                else if (initiative[progress] == null) {
                    moreTurns = false;
                }
                else if (w.finalBattle && (!initiative[progress].alive || initiative[progress].resolve <= 0)) {
                    moreTurns = false;
                }
            }
        }
        if (!actorFound) {
            int defeated = 0;
            for (int i = 0; i < 3; ++i) {
                if (initiative[i] != null && (!initiative[i].alive || initiative[i].resolve <= 0)) {
                    ++defeated;
                }
            }
            if (defeated < 3) {
                endgame = false;
                w.append(t, "\n\n" + w.getSeparator() + "\n\nThe Demons swarm across the city unopposed!");
            }
        }
        else {
            endgame = false;
        }
        /*class ContinueButton extends AbstractAction
        {
            public ContinueButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.EnemyTurn(t, p, f, w, initiative, currentProgress);
            }
        }*/
        class ContinueButton extends AbstractAction
        {
            public ContinueButton(final String text, final String desc) {
                super(text);
                this.putValue("ShortDescription", desc);
            }
            
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                Boolean newChosen = false;
                if (w.getCast()[1] == null) {
                    if (w.getTotalRounds() >= 18 * (15 - w.eventOffset) / 15 || w.day > 20) {
                        newChosen = true;
                    }
                }
                else if (w.getCast()[2] == null && (w.getTotalRounds() >= 60 * (15 - w.eventOffset) / 15 || w.day > 30)) {
                    newChosen = true;
                }
                if ((w.evacComplete() || w.getBattleRound() < 4) && ((w.getCast()[1] != null && w.getTotalRounds() < 80 * (15 - w.eventOffset) / 15 && w.day <= 30) || (w.getCast()[1] == null && w.getTotalRounds() < 28 * (15 - w.eventOffset) / 15 && w.day <= 20))) {
                    newChosen = false;
                }
                int arrival = -1;
                int timerStandard = 10000;
                for (int i = 0; i < 3; ++i) {
                    if (w.decrementArrival(i)) {
                        final Chosen thisChosen = w.getCast()[i];
                        Boolean successfulArrival = true;
                        if (thisChosen == w.getCombatants()[0]) {
                            successfulArrival = false;
                        }
                        else if (w.getCombatants()[1] != null && w.getCombatants()[1] == thisChosen) {
                            successfulArrival = false;
                        }
                        if (w.getCast()[i] == null) {
                            successfulArrival = false;
                        }
                        if (successfulArrival && w.getArrivalTimer()[i] < timerStandard) {
                            arrival = i;
                            timerStandard = w.getArrivalTimer()[i];
                        }
                    }
                }
                if (w.getCombatants()[2] != null) {
                    arrival = -1;
                }
                class ContinueButtonTwo extends AbstractAction
                {
                    public ContinueButtonTwo(final String desc) {
                        //super(text);
                        this.putValue("ShortDescription", desc);
                    }
                    
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.append(t, "\n\n" + w.getSeparator() + "\n");
                        if (w.endTurn(t)) {
                            w.append(t, "\n");
                            if (w.finalBattle) {
                                Project.DefeatScene(t, p, f, w);
                            }
                            else {
                                p.removeAll();
                                w.append(t, "The Demonic forces have been routed, and the stragglers flee back into their underground tunnels.  Crisis workers arrive to round up the remaining Thralls for purification.  Meanwhile, ");
                                Chosen c;
                                for (c = null; c == null; c = null) {
                                    c = w.getCombatants()[(int)(Math.random() * 3.0)];
                                    if (c != null && c.isSurrounded()) {}
                                }
                                if (w.getCombatants()[1] == null) {
                                    w.append(t, String.valueOf(c.getMainName()) + " returns");
                                }
                                else {
                                    w.append(t, "the Chosen return");
                                }
                                w.append(t, " home to prepare for tomorrow's fight.\n\n");
                                c.VictoryLine(t, p, f, w);
                                final JButton ContinueTwo = new JButton("Continue");
                                ContinueTwo.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(final ActionEvent e) {
                                        Project.PostBattle(t, p, f, w);
                                    }
                                });
                                p.add(ContinueTwo);
                                p.validate();
                                p.repaint();
                            }
                        }
                        else {
                            w.append(t, "\n");
                            Project.PickTarget(t, p, f, w);
                            if (w.tutorialResponse()) {
                                if (w.getBattleRound() == 6) {
                                    if (w.getCast()[0].currentPAIN == 108L) {
                                        w.grayAppend(t, "\n\n(The factors that determine when reinforcements show up are the personalities and relationships of the initially-targeted Chosen and the arriving Chosen.  This means that as long as their relationship doesn't change, Axiom will always show up on Round 6 when we go after Miracle.\n\nLet's target Axiom and then use Examine to see what she's like.)");
                                    }
                                    else {
                                        w.endTutorial();
                                    }
                                }
                                else if (w.getBattleRound() == 7) {
                                    if (w.getCast()[0].getCurrentFEAR() == 133L) {
                                        w.grayAppend(t, "\n\n(We put another FEAR level on Miracle, but FEAR only provides an opening when one of the other Chosen is already surrounded.  Fortunately, another possible target has just arrived.  Target Spice and use Examine to see what we can expect from her.)");
                                    }
                                    else {
                                        w.endTutorial();
                                    }
                                }
                            }
                        }
                    }
                }
                if (newChosen) {
                    p.removeAll();
                    final Chosen arrivingChosen = new Chosen();
                    if (w.getCast()[1] == null) {
                        arrivingChosen.setNumber(1);
                    }
                    else {
                        arrivingChosen.setNumber(2);
                    }
                    arrivingChosen.generate(w);
                    w.addChosen(arrivingChosen);
                    w.addToCombat(arrivingChosen);
                    if (arrivingChosen.type == Chosen.Species.SUPERIOR) {
                        w.append(t, "A sudden ripple passes through your army, a psychic shockwave carrying an emotion rarely felt by Demons: fear.  Those with voices let loose roars of dismay, and the rest slither backward to gain some distance from the newcomer on the rooftop on the edge of the battlefield.\n\nRendered indistinct by the sun at its back, the newcomer's silhouette could belong to anyone at all.  But even though there's nothing special about the flash of light and the thunderclap of the Chosen's transformation, the Demons recognize the voice that rings out, and they know that one of their most dangerous foes has arrived.\n\n");
                    }
                    else {
                        w.append(t, "As the battle rages below, an unfamiliar figure arrives on the nearby rooftops.  After watching for a moment, " + arrivingChosen.heShe() + " makes a fateful decision.  A loud crack rings through the air, light shining from above!\n\n");
                    }
                    arrivingChosen.say(t, "\"" + arrivingChosen.announcement() + "\"\n\n");
                    arrivingChosen.transform(t, w);
                    w.append(t, "\n\n");
                    arrivingChosen.printGreeting(t, w);
                    Chosen responding = w.getCombatants()[1];
                    if (w.getCombatants()[1] == arrivingChosen) {
                        responding = w.getCombatants()[0];
                    }
                    else if ((w.getCombatants()[1].isSurrounded() || w.getCombatants()[1].isCaptured()) && !w.getCombatants()[0].isSurrounded() && !w.getCombatants()[0].isCaptured()) {
                        responding = w.getCombatants()[0];
                    }
                    if (!responding.isSurrounded() && !responding.isCaptured()) {
                        w.append(t, "\n\n");
                        responding.printResponse(t, w);
                    }
                    /*class ContinueButtonTwo extends AbstractAction
                    {
                        public ContinueButtonTwo(final String desc) {
                            super(text);
                            this.putValue("ShortDescription", desc);
                        }
                        
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            w.append(t, "\n\n" + w.getSeparator() + "\n");
                            w.endTurn(t);
                            w.append(t, "\n");
                            Project.PickTarget(t, p, f, w);
                        }
                    }*/
                    final Action ContinueActionTwo = new ContinueButtonTwo("Continue");
                    final JButton Continue = new JButton(ContinueActionTwo) {
                        @Override
                        public Point getToolTipLocation(final MouseEvent e) {
                            return new Point(0, -30);
                        }
                    };
                    Continue.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                    Continue.getActionMap().put("pressed", ContinueActionTwo);
                    p.add(Continue);
                    p.validate();
                    p.repaint();
                }
                else if (arrival >= 0) {
                    p.removeAll();
                    final Chosen arrivingChosen = w.getCast()[arrival];
                    arrivingChosen.say(t, "\"" + arrivingChosen.announcement() + "\"\n\n");
                    arrivingChosen.transform(t, w);
                    if (w.finalBattle) {
                        w.addToCombat(arrivingChosen);
                        w.append(t, "\n\n");
                        w.finalBattleIntro(t, arrivingChosen);
                    }
                    else {
                        Chosen responder = null;
                        Boolean response = false;
                        while (!response) {
                            if (Math.random() < 0.5) {
                                if (!w.getCombatants()[0].isSurrounded() && !w.getCombatants()[0].isCaptured() && w.getRelationship(w.getCombatants()[0].getNumber(), arrivingChosen.getNumber()) != 0) {
                                    responder = w.getCombatants()[0];
                                    response = true;
                                }
                            }
                            else if (w.getCombatants()[1] != null && !w.getCombatants()[1].isSurrounded() && !w.getCombatants()[1].isCaptured() && w.getRelationship(w.getCombatants()[1].getNumber(), arrivingChosen.getNumber()) != 0) {
                                responder = w.getCombatants()[1];
                                response = true;
                            }
                            if (w.getCombatants()[0].isSurrounded() || w.getCombatants()[0].isCaptured() || w.getRelationship(w.getCombatants()[0].getNumber(), arrivingChosen.getNumber()) == 0) {
                                if (w.getCombatants()[1] == null) {
                                    response = true;
                                }
                                else {
                                    if (!w.getCombatants()[1].isSurrounded() && !w.getCombatants()[1].isCaptured() && w.getRelationship(w.getCombatants()[1].getNumber(), arrivingChosen.getNumber()) != 0) {
                                        continue;
                                    }
                                    response = true;
                                }
                            }
                        }
                        w.addToCombat(arrivingChosen);
                        if (responder != null) {
                            w.append(t, "\n\n");
                            responder.printGreetingAgain(t, w, arrivingChosen);
                        }
                    }
                    final Action ContinueActionTwo2 = new ContinueButtonTwo("Continue");
                    final JButton Continue2 = new JButton(ContinueActionTwo2) {
                        @Override
                        public Point getToolTipLocation(final MouseEvent e) {
                            return new Point(0, -30);
                        }
                    };
                    Continue2.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
                    Continue2.getActionMap().put("pressed", ContinueActionTwo2);
                    p.add(Continue2);
                    p.validate();
                    p.repaint();
                }
                else if (w.endTurn(t)) {
                    p.removeAll();
                    if (w.finalBattle) {
                        Project.DefeatScene(t, p, f, w);
                    }
                    else {
                        w.append(t, "The Demonic forces have been routed, and the stragglers flee back into their underground tunnels.  Crisis workers arrive to round up the remaining Thralls for purification.  Meanwhile, ");
                        Chosen c;
                        for (c = null; c == null; c = null) {
                            c = w.getCombatants()[(int)(Math.random() * 3.0)];
                            if (c != null && c.isSurrounded()) {}
                        }
                        if (w.getCombatants()[1] == null) {
                            w.append(t, String.valueOf(c.getMainName()) + " returns");
                        }
                        else {
                            w.append(t, "the Chosen return");
                        }
                        w.append(t, " home to prepare for tomorrow's fight.\n\n");
                        c.VictoryLine(t, p, f, w);
                        final JButton ContinueTwo = new JButton("Continue");
                        ContinueTwo.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.PostBattle(t, p, f, w);
                            }
                        });
                        p.add(ContinueTwo);
                        p.validate();
                        p.repaint();
                    }
                }
                else {
                    w.append(t, "\n");
                    Project.PickTarget(t, p, f, w);
                    if (w.tutorialResponse()) {
                        if (w.getBattleRound() == 2) {
                            if (w.getCast()[0].getCurrentDISG() == 70L) {
                                w.grayAppend(t, "\n\n(With the right upgrades, high ANGST, or naturally high vulnerabilities, it's possible to reliably deal 100 or more trauma in a single turn, setting up openings very quickly.  But those don't apply here, so let's use Slime again.)");
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 3) {
                            if (w.getCast()[0].getCurrentDISG() == 140L) {
                                if (w.tickle()) {
                                    w.grayAppend(t, "\n\n(Surrounding Miracle right now will only give us one turn to torment her.  In other situations, it might be a good idea to create another opening to increase the duration.  But since she's pretty weak to ANTI, and since we have the upgrade that increases circumstance damage, one turn should be plenty.  Surround her!)");
                                }
                                else {
                                    w.grayAppend(t, "\n\n(Surrounding Miracle right now will only give us one turn to torment her.  In other situations, it might be a good idea to create another opening to increase the duration.  But since she's pretty weak to INJU, and since we have the upgrade that increases circumstance damage, one turn should be plenty.  Surround her!)");
                                }
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 4) {
                            if (w.getCast()[0].isSurrounded()) {
                                if (w.tickle()) {
                                    w.grayAppend(t, "\n\n(Tickle deals ANTIcipation damage, whose main effect is to multiply other circumstance damage, so it's often a good idea to start there.  Try Tickling her.)");
                                }
                                else {
                                    w.grayAppend(t, "\n\n(Pummel deals INJU damage, whose main effect is to multiply other circumstance damage, so it's often a good idea to start there.  Try Pummeling her.)");
                                }
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 5) {
                            if (w.getCast()[0].getCurrentINJU() == 126L) {
                                if (w.tickle()) {
                                    w.grayAppend(t, "\n\n(She escaped quickly, but not before getting level 1 ANTI.  All circumstances, in addition to their other effects, also apply an extra x2 multiplier to their associated trauma.  For ANTIcipation, that's TICKle.  Even though Miracle doesn't usually take much TICK damage, that extra multiplier will let us use it to create another opening.  Tickle her!");
                                }
                                else {
                                    w.grayAppend(t, "\n\n(She escaped quickly, but not before getting level 1 INJU.  All circumstances, in addition to their other effects, also apply an extra x2 multiplier to their associated trauma.  For INJU, that's PAIN.  Even though Miracle doesn't usually take much PAIN damage, that extra multiplier will let us use it to create another opening.  Attack her!");
                                }
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 8) {
                            if (w.getCast()[1].captured) {
                                w.grayAppend(t, "\n\n(The EXPO damage on Spice will increase the circumstance damage (but not the trauma damage) taken by the other two Chosen (but not by Spice herself).  From the extermination bar, we can see that the Chosen won't finish killing the Demons until next turn, so we can still spend another turn setting up.  Taunt Miracle in order to create another opening on her.)");
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 9) {
                            if (w.getCast()[0].getCurrentSHAM() == 105L) {
                                w.grayAppend(t, "\n\n(As long as one of the Chosen is surrounded or captured, the battle won't immediately end when extermination is completed, but any Chosen surrounded or captured after that point will take off into the sky afterward and become ungrabbable.  That means that it's a very good idea to set up situations like this where you can grab Chosen immediately before extermination is completed.  Take this chance to surround Miracle!)");
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 10) {
                            if (w.getCast()[0].isSurrounded()) {
                                if (w.tickle()) {
                                    w.grayAppend(t, "\n\n(This surround duration isn't long enough to actually break Miracle, so we want to focus on dealing lots of trauma damage to create some even higher openings.  This is especially important because using a TICK opening causes it to stop providing further openings until it levels up again.  HATE (from Grind) and PLEA (from Caress) both increase the trauma multiplier.  PLEA increases it by more, but HATE also increases the circumstance multiplier.  Because we're planning to apply both, let's start with Grind.)");
                                }
                                else {
                                    w.grayAppend(t, "\n\n(This surround duration isn't long enough to actually break Miracle, so we want to focus on dealing lots of trauma damage to create some even higher openings.  This is especially important because using a PAIN opening causes it to stop providing further openings until it levels up again.  HATE (from Grind) and PLEA (from Caress) both increase the trauma multiplier.  PLEA increases it by more, but HATE also increases the circumstance multiplier.  Because we're planning to apply both, let's start with Grind.)");
                                }
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 11) {
                            if (w.getCast()[0].getCurrentHATE() == 153L) {
                                w.grayAppend(t, "\n\n(And now Caress Miracle.)");
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 12) {
                            if (w.getCast()[0].getCurrentPLEA() == 531L) {
                                w.grayAppend(t, "\n\n(At this rate, the battle will end once Miracle escapes, because at that point, none of the Chosen will be surrounded.  We can stop that from happening by surrounding one of the others, but that isn't possible yet.  We have to set up a surround this turn, then use it next turn.  Attack Spice.)");
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 13) {
                            if (w.getCast()[1].currentPAIN == 109L) {
                                w.grayAppend(t, "\n\n(And now Surround Spice.)");
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 14) {
                            if (w.getCast()[1].isSurrounded()) {
                                w.grayAppend(t, "\n\n(It's tempting to try to do something with Spice, but because extermination was already completed, we won't be able to grab her again after this.  We don't have a way to increase her EXPO by another level, so it wouldn't help us break Miracle, either.  Just leave Spice alone and surround Miracle again.)");
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 15) {
                            if (w.getCast()[0].isSurrounded()) {
                                w.grayAppend(t, "\n\n(Miracle's multipliers don't look too impressive, but because we've built up such a long surround duration, we have time to improve them.  HATE is already almost at the next level, so start with Grind on Miracle.)");
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 16) {
                            if (w.getCast()[0].getCurrentHATE() == 958L) {
                                if (w.tickle()) {
                                    w.grayAppend(t, "\n\n(Next, Tickle Miracle again.)");
                                }
                                else {
                                    w.grayAppend(t, "\n\n(Next, Pummel Miracle again.)");
                                }
                            }
                            else {
                                w.endTutorial();
                            }
                        }
                        else if (w.getBattleRound() == 17) {
                            if (w.getCast()[0].getCurrentINJU() == 415L) {
                                w.grayAppend(t, "\n\n(We can see that HATE and PLEA are being penalized because their associated traumas (FEAR and DISG) are pulling ahead of the other two.  Keeping the traumas balanced is an important part of keeping the Chosen off-guard so that they can't muster resistance against any one circumstance.  ");
                                if (w.tickle()) {
                                    w.grayAppend(t, "TICK should catch up since we're tickling her");
                                }
                                else {
                                    w.grayAppend(t, "PAIN should catch up since we're pummeling her");
                                }
                                w.grayAppend(t, ", but SHAM might have trouble.  Therefore, even though we aren't trying to deal any damage to the other two Chosen, we should still Humiliate Miracle here.");
                            }
                        }
                        else if (w.getBattleRound() == 18) {
                            if (w.getCast()[0].getCurrentEXPO() == 372L) {
                                w.grayAppend(t, "\n\n(That did it!  Miracle saw that after ");
                                if (w.tickle()) {
                                    w.grayAppend(t, "TICK, SHAM, and EXPO");
                                }
                                else {
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
        if (endgame) {
            int captured = 0;
            int dead = 0;
            final Chosen[] survivors = new Chosen[3];
            final Chosen[] killed = new Chosen[3];
            for (int j = 0; j < 3; ++j) {
                if (w.getCast()[j].alive) {
                    survivors[captured] = w.getCast()[j];
                    ++captured;
                }
                else {
                    killed[dead] = w.getCast()[j];
                    ++dead;
                }
            }
            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
            if (captured == 3) {
                w.append(t, "Finally, all three of the Chosen have surrendered to your forces.  This is a flawless victory - you couldn't have hoped for a better result.  By the time the reinforcements from other cities arrive, your Demonic Barrier has already reached full strength, and no more Chosen can enter without immediately losing their powers and joining the ranks of your captives.\n\nThe Demons escort " + w.getCast()[0].getMainName() + ", " + w.getCast()[1].getMainName() + ", and " + w.getCast()[2].getMainName() + " to your throne room, where you will begin to train them into your own loyal servants...");
            }
            else if (captured == 2) {
                w.append(t, "With both " + survivors[0].getMainName() + " and " + survivors[1].getMainName() + " broken, your victory is complete.  By the time the reinforcements from other cities arrive, your Demonic Barrier has already reached full strength, and no more Chosen can enter without immediately losing their powers and joining the ranks of your captives.\n\n" + killed[0].getMainName() + "'s death was unfortunate - " + killed[0].heShe() + " would have made an excellent servant.  But you still have " + survivors[0].getMainName() + " and " + survivors[1].getMainName() + ".  The Demons escort them to your throne room so that their training can begin...");
            }
            else {
                w.append(t, "With " + survivors[0].getMainName() + " defeated, your takeover of the city is complete.  By the time the reinforcements from other cities arrive, your Demonic Barrier has already reached full strength, and no more Chosen can enter without immediately losing their powers and joining the ranks of your captives.\n\nThe deaths of " + killed[0].getMainName() + " and " + killed[1].getMainName() + " were very unfortunate - they would have made excellent servants.  But you still managed to hold onto one prize.  The Demons escort " + survivors[0].getMainName() + " into your throne room so that " + survivors[0].hisHer() + " training can begin...");
            }
            EndFinalBattle(t, p, f, w);
        }
        else if (moreTurns) {
            final Action ContinueAction = new ContinueButton("Continue", "Hotkey:");
            final JButton Continue = new JButton(ContinueAction) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            Continue.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            Continue.getActionMap().put("pressed", ContinueAction);
            p.add(Continue);
            p.validate();
            p.repaint();
        }
        else {
            Chosen[] synch = new Chosen[0];
            for (int i = 0; i < 3; ++i) {
                if (w.getCombatants()[i] != null) {
                    int type = 0;
                    if (w.getCombatants()[i].isInseminated()) {
                        type = 1;
                    }
                    else if (w.getCombatants()[i].isOrgasming()) {
                        type = 2;
                    }
                    else if (w.getCombatants()[i].isSodomized()) {
                        type = 3;
                    }
                    else if (w.getCombatants()[i].isBroadcasted()) {
                        type = 4;
                    }
                    if (type > 0) {
                        for (int k = i + 1; k < 3; ++k) {
                            if (w.getCombatants()[k] != null) {
                                int otherType = 0;
                                if (w.getCombatants()[k].isInseminated()) {
                                    otherType = 1;
                                }
                                else if (w.getCombatants()[k].isOrgasming()) {
                                    otherType = 2;
                                }
                                else if (w.getCombatants()[k].isSodomized()) {
                                    otherType = 3;
                                }
                                else if (w.getCombatants()[k].isBroadcasted()) {
                                    otherType = 4;
                                }
                                if (type == otherType) {
                                    if (synch.length == 0) {
                                        synch = new Chosen[] { w.getCombatants()[i], w.getCombatants()[k] };
                                    }
                                    else {
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
            final Action ContinueAction2 = new ContinueButton("Continue", "Hotkey:");
            final JButton Continue2 = new JButton(ContinueAction2) {
                @Override
                public Point getToolTipLocation(final MouseEvent e) {
                    return new Point(0, -30);
                }
            };
            Continue2.getInputMap(2).put(KeyStroke.getKeyStroke("SPACE"), "pressed");
            Continue2.getActionMap().put("pressed", ContinueAction2);
            p.add(Continue2);
            p.validate();
            p.repaint();
        }
    }
    
    public static void PostBattle(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        Boolean justContinue = true;
        Boolean postScene = false;
        int vignette = -1;
        if (!w.isTutorial() && !w.loopComplete) {
            vignette = w.chooseVignette();
        }
        if (w.loopComplete) {
            w.pendingBreaks = new int[0];
        }
        final int chosenVignette = vignette;
        for (int i = 0; i < 3; ++i) {
            if (w.getCast()[i] != null) {
                if (w.getCast()[i].morality < 34 && w.getCast()[i].impregnated) {
                    for (int j = 0; j < 3; ++j) {
                        if (w.getCast()[j] != null && w.getCast()[j].morality > 66) {
                            final long temptReq = w.getCast()[j].temptReq;
                        }
                    }
                }
                if (w.getCast()[i].confidence < 34 && w.getCast()[i].drained) {
                    for (int j = 0; j < 3; ++j) {
                        if (w.getCast()[j] != null && w.getCast()[j].confidence > 66) {
                            final long temptReq2 = w.getCast()[j].temptReq;
                        }
                    }
                }
            }
        }
        if (w.isTutorial()) {
            final long totalTrauma = w.getCast()[0].getCurrentFEAR() + w.getCast()[0].getCurrentDISG() + w.getCast()[0].getCurrentPAIN() + w.getCast()[0].getCurrentSHAM() + w.getCast()[1].getCurrentFEAR() + w.getCast()[1].getCurrentDISG() + w.getCast()[1].getCurrentPAIN() + w.getCast()[1].getCurrentSHAM() + w.getCast()[2].getCurrentFEAR() + w.getCast()[2].getCurrentDISG() + w.getCast()[2].getCurrentPAIN() + w.getCast()[2].getCurrentSHAM();
            w.append(t, "\n\n" + w.getSeparator() + "\n\nTotal trauma: " + w.getCast()[0].condensedFormat(totalTrauma) + "\nVulnerabilities broken:");
            int cores = 0;
            int sigs = 0;
            int minors = 0;
            for (int k = 0; k < 3; ++k) {
                if (w.getCast()[k].isRuthless()) {
                    if (w.getCast()[k].getMorality() > 66) {
                        ++cores;
                    }
                    else if (w.getCast()[k].getMorality() > 33) {
                        ++sigs;
                    }
                }
                if (!w.getCast()[k].isVVirg()) {
                    if (w.getCast()[k].getMorality() > 66) {
                        ++cores;
                    }
                    else if (w.getCast()[k].getMorality() > 33) {
                        ++sigs;
                    }
                }
                if (w.getCast()[k].isLustful()) {
                    if (w.getCast()[k].getInnocence() > 66) {
                        ++cores;
                    }
                    else if (w.getCast()[k].getInnocence() > 33) {
                        ++sigs;
                    }
                    else {
                        ++minors;
                    }
                }
                if (!w.getCast()[k].isCVirg()) {
                    if (w.getCast()[k].getInnocence() > 66) {
                        ++cores;
                    }
                    else if (w.getCast()[k].getInnocence() > 33) {
                        ++sigs;
                    }
                    else {
                        ++minors;
                    }
                }
                if (w.getCast()[k].isMeek()) {
                    if (w.getCast()[k].getConfidence() > 66) {
                        ++cores;
                    }
                    else if (w.getCast()[k].getConfidence() > 33) {
                        ++sigs;
                    }
                    else {
                        ++minors;
                    }
                }
                if (!w.getCast()[k].isAVirg()) {
                    if (w.getCast()[k].getConfidence() > 66) {
                        ++cores;
                    }
                    else if (w.getCast()[k].getConfidence() > 33) {
                        ++sigs;
                    }
                    else {
                        ++minors;
                    }
                }
                if (w.getCast()[k].isDebased()) {
                    if (w.getCast()[k].getDignity() > 66) {
                        ++cores;
                    }
                    else if (w.getCast()[k].getDignity() > 33) {
                        ++sigs;
                    }
                }
                if (!w.getCast()[k].isModest()) {
                    if (w.getCast()[k].getDignity() > 66) {
                        ++cores;
                    }
                    else if (w.getCast()[k].getDignity() > 33) {
                        ++sigs;
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
        }
        else if (!w.getCast()[0].isIntroduced()) {
            justContinue = false;
            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
            w.getCast()[0].printIntro(t, w);
        }
        else if (w.getCast()[1] != null) {
            if (!w.getCast()[1].isIntroduced()) {
                justContinue = false;
                postScene = true;
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                w.getCast()[0].firstMeeting(t, w, w.getCast()[1]);
                p.removeAll();
                final JButton Continue = new JButton("Continue");
                Continue.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        p.removeAll();
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        w.getCast()[1].printIntro(t, w);
                        final JButton Continue2 = new JButton("Continue");
                        Continue2.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.Downtime(t, p, f, w);
                            }
                        });
                        p.add(Continue2);
                        p.validate();
                        p.repaint();
                    }
                });
                p.add(Continue);
                p.validate();
                p.repaint();
            }
            else if (w.getCast()[2] != null) {
                if (!w.getCast()[2].isIntroduced()) {
                    justContinue = false;
                    postScene = true;
                    w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                    w.getCast()[2].firstTrio(t, w, w.getCast()[0], w.getCast()[1]);
                    p.removeAll();
                    final JButton Continue = new JButton("Continue");
                    Continue.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            p.removeAll();
                            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                            w.getCast()[2].printIntro(t, w);
                            final JButton ContinueTwo = new JButton("Continue");
                            ContinueTwo.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    Project.Downtime(t, p, f, w);
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
                else {
                    final JButton lastContinue = new JButton("Continue");
                    if (w.getDay() == 15 - w.eventOffset && !w.loopComplete) {
                        justContinue = false;
                        postScene = true;
                        if (w.getBreaks().length == 0) {
                            InterviewChain(t, p, f, w);
                        }
                        else {
                            lastContinue.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    Project.InterviewChain(t, p, f, w);
                                }
                            });
                        }
                    }
                    else if (w.getDay() == 30 - w.eventOffset * 2 && !w.loopComplete) {
                        justContinue = false;
                        postScene = true;
                        if (w.getBreaks().length == 0) {
                            VacationChain(t, p, f, w);
                        }
                        else {
                            lastContinue.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    Project.VacationChain(t, p, f, w);
                                }
                            });
                        }
                    }
                    else if (w.getDay() == 45 - w.eventOffset * 3 && !w.loopComplete) {
                        justContinue = false;
                        postScene = true;
                        if (w.getBreaks().length == 0) {
                            DeploymentChain(t, p, f, w);
                        }
                        else {
                            lastContinue.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    Project.DeploymentChain(t, p, f, w);
                                }
                            });
                        }
                    }
                    else if (chosenVignette >= 0 && w.getBreaks().length == 0) {
                        justContinue = false;
                        if (w.getBreaks().length == 0) {
                            w.showVignette(t, chosenVignette);
                        }
                        else {
                            lastContinue.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.showVignette(t, chosenVignette);
                                }
                            });
                        }
                    }
                    else {
                        lastContinue.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.Downtime(t, p, f, w);
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
            }
            else if (chosenVignette >= 0) {
                justContinue = false;
                w.showVignette(t, chosenVignette);
            }
        }
        else if (chosenVignette >= 0) {
            justContinue = false;
            w.showVignette(t, chosenVignette);
        }
        if (w.isTutorial()) {
            p.removeAll();
            final JButton Continue = new JButton("Continue");
            Continue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                    final WorldState x = new WorldState();
                    x.copySettings(t, w);
                    x.copyToggles(w);
                    x.save = w.save;
                    Project.IntroOne(t, p, f, x);
                }
            });
            p.add(Continue);
            p.validate();
            p.repaint();
        }
        else if (justContinue) {
            Downtime(t, p, f, w);
        }
        else if (!postScene) {
            p.removeAll();
            final JButton Continue = new JButton("Continue");
            Continue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.Downtime(t, p, f, w);
                }
            });
            p.add(Continue);
            p.validate();
            p.repaint();
        }
    }
    
    public static void SortBreaks(final WorldState w) {
    }
    
    public static void DeploymentChain(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
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
        for (int i = 0; i < 3; ++i) {
            if (w.getCast()[i].getMorality() > 66) {
                moral = w.getCast()[i];
            }
            else if (w.getCast()[i].getMorality() > 33) {
                neitherOne = w.getCast()[i];
            }
            else {
                immoral = w.getCast()[i];
            }
            if (w.getCast()[i].getInnocence() > 66) {
                innocent = w.getCast()[i];
            }
            else if (w.getCast()[i].getInnocence() > 33) {
                neitherTwo = w.getCast()[i];
            }
            else {
                nocent = w.getCast()[i];
            }
            if (w.getCast()[i].getConfidence() > 66) {
                confident = w.getCast()[i];
            }
            else if (w.getCast()[i].getConfidence() > 33) {
                neitherThree = w.getCast()[i];
            }
            else {
                unconfident = w.getCast()[i];
            }
            if (w.getCast()[i].getDignity() > 66) {
                dignified = w.getCast()[i];
            }
            else if (w.getCast()[i].getDignity() > 33) {
                neitherFour = w.getCast()[i];
            }
            else {
                undignified = w.getCast()[i];
            }
        }
        final Chosen first = dignified;
        final Chosen second = neitherFour;
        final Chosen third = undignified;
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
        }
        else {
            fourth = moral;
            fifth = neitherOne;
            sixth = immoral;
        }
        if (dignified == confident || confident == moral) {
            seventh = innocent;
            eighth = neitherTwo;
            ninth = nocent;
        }
        else {
            seventh = confident;
            eighth = neitherThree;
            ninth = unconfident;
        }
        final Chosen[] order = { first, second, third, fourth, fifth, sixth, seventh, eighth, ninth };
        first.deploymentOne(t, w, second, third);
        p.removeAll();
        final JButton Continue = new JButton("Continue");
        Continue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                order[3].deploymentTwo(t, w, order[4], order[5]);
                final JButton ContinueTwo = new JButton("Continue");
                ContinueTwo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        p.removeAll();
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        order[6].deploymentThree(t, w, order[7], order[8]);
                        final JButton ContinueThree = new JButton("Continue");
                        ContinueThree.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                p.removeAll();
                                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                                Project.Downtime(t, p, f, w);
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
    
    public static void VacationChain(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
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
        for (int i = 0; i < 3; ++i) {
            if (w.getCast()[i].getMorality() > 66) {
                moral = w.getCast()[i];
            }
            else if (w.getCast()[i].getMorality() > 33) {
                neitherOne = w.getCast()[i];
            }
            else {
                immoral = w.getCast()[i];
            }
            if (w.getCast()[i].getInnocence() > 66) {
                innocent = w.getCast()[i];
            }
            else if (w.getCast()[i].getInnocence() > 33) {
                neitherTwo = w.getCast()[i];
            }
            else {
                nocent = w.getCast()[i];
            }
            if (w.getCast()[i].getConfidence() > 66) {
                confident = w.getCast()[i];
            }
            else if (w.getCast()[i].getConfidence() > 33) {
                neitherThree = w.getCast()[i];
            }
            else {
                unconfident = w.getCast()[i];
            }
            if (w.getCast()[i].getDignity() > 66) {
                dignified = w.getCast()[i];
            }
            else if (w.getCast()[i].getDignity() > 33) {
                neitherFour = w.getCast()[i];
            }
            else {
                undignified = w.getCast()[i];
            }
        }
        final Chosen first = confident;
        final Chosen second = neitherThree;
        final Chosen third = unconfident;
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
        }
        else {
            fourth = dignified;
            fifth = neitherFour;
            sixth = undignified;
        }
        if (confident == innocent || dignified == innocent) {
            seventh = moral;
            eighth = neitherOne;
            ninth = immoral;
        }
        else {
            seventh = innocent;
            eighth = neitherTwo;
            ninth = nocent;
        }
        final Chosen[] order = { first, second, third, fourth, fifth, sixth, seventh, eighth, ninth };
        first.vacationOne(t, w, second, third);
        p.removeAll();
        final JButton Continue = new JButton("Continue");
        Continue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                order[3].vacationTwo(t, w, order[4], order[5]);
                final JButton ContinueTwo = new JButton("Continue");
                ContinueTwo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        p.removeAll();
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        order[6].vacationThree(t, w, order[7], order[8]);
                        final JButton ContinueThree = new JButton("Continue");
                        ContinueThree.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                p.removeAll();
                                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                                Project.Downtime(t, p, f, w);
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
    
    public static void InterviewChain(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        Chosen first = null;
        Chosen second = null;
        Chosen third = null;
        Chosen innocents = null;
        Chosen morals = null;
        Chosen confidents = null;
        Chosen dignifieds = null;
        for (int i = 0; i < 3; ++i) {
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
        if ((first = innocent) == moral) {
            second = confident;
            third = dignified;
        }
        else {
            second = moral;
            if (innocent == dignified || moral == dignified) {
                third = confident;
            }
            else {
                third = dignified;
            }
        }
        first.interviewOne(t, w, moral, innocent, confident, dignified);
        final Chosen chosenTwo = second;
        final Chosen chosenThree = third;
        p.removeAll();
        final JButton Continue = new JButton("Continue");
        Continue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                chosenTwo.interviewTwo(t, w, moral, innocent, confident, dignified);
                final JButton ContinueTwo = new JButton("Continue");
                ContinueTwo.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        p.removeAll();
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        chosenThree.interviewThree(t, w, moral, innocent, confident, dignified);
                        final JButton ContinueThree = new JButton("Continue");
                        ContinueThree.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.Downtime(t, p, f, w);
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
    
    public static void HandleBreaks(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final JButton proceed) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        final int sceneType = w.getBreaks()[0];
        Chosen broken = null;
        Chosen c = null;
        Chosen d = null;
        if (sceneType % 4 == 0) {
            for (int i = 0; i < 3; ++i) {
                if (w.getCast()[i].getMorality() > 66) {
                    broken = w.getCast()[i];
                }
                else if (w.getCast()[i].getMorality() < 34) {
                    c = w.getCast()[i];
                }
            }
        }
        else if (sceneType % 4 == 1) {
            for (int i = 0; i < 3; ++i) {
                if (w.getCast()[i].getInnocence() > 66) {
                    broken = w.getCast()[i];
                }
                else if (w.getCast()[i].getInnocence() < 34) {
                    c = w.getCast()[i];
                }
            }
        }
        else if (sceneType % 4 == 2) {
            for (int i = 0; i < 3; ++i) {
                if (w.getCast()[i].getConfidence() > 66) {
                    broken = w.getCast()[i];
                }
                else if (w.getCast()[i].getConfidence() < 34) {
                    c = w.getCast()[i];
                }
            }
        }
        else if (sceneType % 4 == 3) {
            for (int i = 0; i < 3; ++i) {
                if (w.getCast()[i].getDignity() > 66) {
                    broken = w.getCast()[i];
                }
                else if (w.getCast()[i].getDignity() < 34) {
                    c = w.getCast()[i];
                }
            }
        }
        if (sceneType < 16) {
            broken.breakScene(t, w, c, sceneType);
        }
        else {
            c = null;
            d = null;
            if (sceneType == 16) {
                for (int i = 0; i < 3; ++i) {
                    if (w.getCast()[i].temptReq < 100000L && !w.getCast()[i].pastTempted && (w.getCast()[i].morality > 66 || w.getCast()[i].confidence > 66)) {
                        broken = w.getCast()[i];
                    }
                }
                if (broken == null) {
                    w.append(t, "One of the Chosen began to trust the Thralls, but that trust was betrayed before it had time to take root.");
                    w.discardBreak();
                }
                else {
                    if (broken.morality > 66) {
                        for (int i = 0; i < 3; ++i) {
                            if (w.getCast()[i].morality < 34) {
                                c = w.getCast()[i];
                            }
                        }
                    }
                    if (broken.confidence > 66) {
                        for (int i = 0; i < 3; ++i) {
                            if (w.getCast()[i].confidence < 34) {
                                if (c == null) {
                                    c = w.getCast()[i];
                                }
                                else if (c != w.getCast()[i]) {
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
            final JButton Continue = new JButton("Continue");
            Continue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.HandleBreaks(t, p, f, w, proceed);
                }
            });
            p.add(Continue);
        }
        else {
            p.add(proceed);
        }
        p.validate();
        p.repaint();
    }
    
    public static void Downtime(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        for (int i = 0; i < 3; ++i) {
            if (w.getCast()[i] != null && w.getCast()[i].temptReq < 100000L) {
                w.getCast()[i].pastTempted = true;
            }
        }
        Forsaken[] exhaustedTest = new Forsaken[0];
        if (w.usedForsaken != null) {
            int actualCost = w.usedForsaken.motivationCost();
            if (w.usedForsaken.isFormerFriend(w.getCast()[0]) || w.usedForsaken.isFormerFriend(w.getCast()[1]) || w.usedForsaken.isFormerFriend(w.getCast()[2])) {
                actualCost *= 2;
            }
            final Forsaken usedForsaken = w.usedForsaken;
            usedForsaken.motivation -= actualCost;
            final Forsaken usedForsaken2 = w.usedForsaken;
            usedForsaken2.stamina -= 200;
            exhaustedTest = new Forsaken[] { w.usedForsaken };
        }
        final Forsaken[] exhausted = exhaustedTest;
        t.setText("");
        w.incrementDay();
        w.clearCommander();
        int lastChosen = 0;
        final int totalActions = 22;
        final Long[][] actionWeights = new Long[3][totalActions];
        final int[] chosenAction = { -1, -1, -1 };
        if (w.getCast()[2] != null) {
            lastChosen = 2;
        }
        else if (w.getCast()[1] != null) {
            lastChosen = 1;
        }
        for (int j = 0; j <= lastChosen; ++j) {
            w.getCast()[j].addTrauma();
        }
        long divisor = 1L;
        long highest = 0L;
        for (int k = 0; k <= lastChosen; ++k) {
            if (w.getCast()[k].getANGST() > highest) {
                highest = w.getCast()[k].getANGST();
            }
            if (w.getCast()[k].getTotalFEAR() > highest) {
                highest = w.getCast()[k].getTotalFEAR();
            }
            if (w.getCast()[k].getTotalDISG() > highest) {
                highest = w.getCast()[k].getTotalDISG();
            }
            if (w.getCast()[k].getTotalPAIN() > highest) {
                highest = w.getCast()[k].getTotalPAIN();
            }
            if (w.getCast()[k].getTotalSHAM() > highest) {
                highest = w.getCast()[k].getTotalSHAM();
            }
        }
        while (highest > 10000000000000L) {
            highest /= 10L;
            divisor *= 10L;
        }
        for (int k = 0; k <= lastChosen; ++k) {
            long fear = w.getCast()[k].getTotalFEAR() / divisor;
            long disg = w.getCast()[k].getTotalDISG() / divisor;
            long pain = w.getCast()[k].getTotalPAIN() / divisor;
            long sham = w.getCast()[k].getTotalSHAM() / divisor;
            long angst = w.getCast()[k].getANGST() / divisor;
            Boolean divided = true;
            if (fear == 0L && disg == 0L && pain == 0L && sham == 0L) {
                fear = w.getCast()[k].getTotalFEAR();
                disg = w.getCast()[k].getTotalDISG();
                pain = w.getCast()[k].getTotalPAIN();
                sham = w.getCast()[k].getTotalSHAM();
                angst *= divisor;
                divided = false;
            }
            actionWeights[k][0] = 150L;
            actionWeights[k][1] = 50L + fear * 100L / (100 + w.getCast()[k].getMorality());
            actionWeights[k][2] = 50L + disg * 100L / (100 + w.getCast()[k].getMorality());
            actionWeights[k][3] = 50L + pain * 100L / (100 + w.getCast()[k].getMorality());
            actionWeights[k][4] = 50L + sham * 100L / (100 + w.getCast()[k].getMorality());
            long inhibition = 20000 * w.downtimeMultiplier / 100;
            if (divided) {
                inhibition /= divisor;
            }
            if (w.getCast()[k].isRuthless()) {
                actionWeights[k][5] = (fear * 200L + pain * 100L + angst * 20L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][5] = 0L;
            }
            if (w.getCast()[k].isLustful()) {
                actionWeights[k][6] = (disg * 200L + fear * 100L + angst * 20L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][6] = 0L;
            }
            if (w.getCast()[k].isMeek()) {
                actionWeights[k][7] = (pain * 200L + sham * 100L + angst * 20L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][7] = 0L;
            }
            if (w.getCast()[k].isDebased()) {
                actionWeights[k][8] = (sham * 200L + disg * 100L + angst * 20L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][8] = 0L;
            }
            inhibition = 4000000L * w.downtimeMultiplier / 100L;
            if (divided) {
                inhibition /= divisor;
            }
            if (!w.getCast()[k].isVVirg()) {
                actionWeights[k][9] = (fear * 400L + disg * 200L + angst * 40L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][9] = 0L;
            }
            if (!w.getCast()[k].isCVirg()) {
                actionWeights[k][10] = (disg * 400L + pain * 200L + angst * 40L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][10] = 0L;
            }
            if (!w.getCast()[k].isAVirg()) {
                actionWeights[k][11] = (pain * 400L + sham * 200L + angst * 40L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][11] = 0L;
            }
            if (!w.getCast()[k].isModest()) {
                actionWeights[k][12] = (sham * 400L + fear * 200L + angst * 40L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][12] = 0L;
            }
            inhibition = 10000000000L * w.downtimeMultiplier / 100L;
            if (divided) {
                inhibition /= divisor;
            }
            if (w.getCast()[k].timesSlaughtered() > 0) {
                actionWeights[k][13] = (fear * 1000L + pain * 500L + disg * 250L + angst * 100L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][13] = 0L;
            }
            if (w.getCast()[k].timesFantasized() > 0) {
                actionWeights[k][14] = (disg * 1000L + sham * 500L + fear * 250L + angst * 100L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][14] = 0L;
            }
            if (w.getCast()[k].timesDetonated() > 0) {
                actionWeights[k][15] = (pain * 1000L + disg * 500L + sham * 250L + angst * 100L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][15] = 0L;
            }
            if (w.getCast()[k].timesStripped() > 0) {
                actionWeights[k][16] = (sham * 1000L + fear * 500L + pain * 250L + angst * 100L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][16] = 0L;
            }
            inhibition = 200000000000000L * w.downtimeMultiplier / 100L;
            if (divided) {
                inhibition /= divisor;
            }
            if (w.getCast()[k].isImpregnated()) {
                actionWeights[k][17] = (fear * 2000L + pain * 1000L + sham * 500L + angst * 250L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][17] = 0L;
            }
            if (w.getCast()[k].isHypnotized()) {
                actionWeights[k][18] = (disg * 2000L + fear * 1000L + pain * 500L + angst * 250L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][18] = 0L;
            }
            if (w.getCast()[k].isDrained()) {
                actionWeights[k][19] = (pain * 2000L + sham * 1000L + disg * 500L + angst * 250L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][19] = 0L;
            }
            if (w.getCast()[k].isParasitized()) {
                actionWeights[k][20] = (sham * 2000L + disg * 1000L + fear * 500L + angst * 250L) / (100 + w.getCast()[k].getMorality()) - inhibition;
            }
            else {
                actionWeights[k][20] = 0L;
            }
            if (w.getCast()[k].betraying && w.getCast()[k].temptReq < 100000L) {
                actionWeights[k][21] = fear * 5L + disg * 5L + pain * 5L + sham * 5L + angst / 2L;
                if (divided) {
                    actionWeights[k][21] /= divisor;
                }
            }
            else {
                actionWeights[k][21] = 0L;
            }
            w.getCast()[k].betraying = false;
            long highestWeight = 0L;
            for (int l = 0; l < actionWeights[k].length; ++l) {
                if (actionWeights[k][l].compareTo(highestWeight) > 0) {
                    highestWeight = actionWeights[k][l];
                    chosenAction[k] = l;
                }
                else if (actionWeights[k][l] < 0L) {
                    actionWeights[k][l] = 0L;
                }
            }
        }
        final long[][] combinedWeights = new long[3][totalActions];
        for (int m = 0; m <= lastChosen; ++m) {
            for (int j2 = 0; j2 < totalActions; ++j2) {
                combinedWeights[m][j2] = actionWeights[m][j2];
                for (int k2 = 0; k2 <= lastChosen; ++k2) {
                    if (m != k2) {
                        combinedWeights[m][j2] = combinedWeights[m][j2] * (200 + w.getCast()[m].getInnocence()) / 200L;
                        combinedWeights[m][j2] = (actionWeights[k2][chosenAction[k2]] + actionWeights[k2][j2]) * 100L / actionWeights[k2][chosenAction[k2]] * combinedWeights[m][j2] / 100L;
                        combinedWeights[m][j2] = combinedWeights[m][j2] * (8 + w.getRelationship(m, k2)) / 8L;
                        long addedWeight = combinedWeights[m][j2] - actionWeights[m][j2];
                        if (addedWeight > 0L && w.getCast()[m].getANGST() > w.getCast()[k2].getANGST()) {
                            addedWeight = w.getCast()[k2].getANGST() * 100L / w.getCast()[m].getANGST() * addedWeight / 100L;
                            combinedWeights[m][j2] = actionWeights[m][j2] + addedWeight;
                        }
                    }
                }
            }
        }
        final long[] totalWeights = new long[totalActions];
        final int[] testOrder = new int[totalActions];
        for (int i2 = 0; i2 < totalActions; ++i2) {
            totalWeights[i2] = 0L;
            testOrder[i2] = i2;
            for (int j3 = 0; j3 <= lastChosen; ++j3) {
                if (combinedWeights[j3][i2] >= actionWeights[j3][chosenAction[j3]]) {
                    final long[] array = totalWeights;
                    final int n = i2;
                    array[n] += combinedWeights[j3][i2];
                }
            }
        }
        Boolean sorted = false;
        while (!sorted) {
            sorted = true;
            for (int i3 = 0; i3 < totalActions - 1; ++i3) {
                if (totalWeights[i3] < totalWeights[i3 + 1]) {
                    final long storage = totalWeights[i3];
                    totalWeights[i3] = totalWeights[i3 + 1];
                    totalWeights[i3 + 1] = storage;
                    final int storageTwo = testOrder[i3];
                    testOrder[i3] = testOrder[i3 + 1];
                    testOrder[i3 + 1] = storageTwo;
                    sorted = false;
                }
            }
        }
        Boolean doubleFound = false;
        for (int i4 = 0; i4 < totalActions; ++i4) {
            int matches = 0;
            final Boolean[] matching = { false, false, false };
            if (w.getCast()[0] != null && combinedWeights[0][testOrder[i4]] > actionWeights[0][chosenAction[0]]) {
                ++matches;
                matching[0] = true;
            }
            if (w.getCast()[1] != null && combinedWeights[1][testOrder[i4]] > actionWeights[1][chosenAction[1]]) {
                ++matches;
                matching[1] = true;
            }
            if (w.getCast()[2] != null && combinedWeights[2][testOrder[i4]] > actionWeights[2][chosenAction[2]]) {
                ++matches;
                matching[2] = true;
            }
            if (matches > 2) {
                chosenAction[0] = testOrder[i4];
                chosenAction[1] = testOrder[i4];
                chosenAction[2] = testOrder[i4];
                i4 = totalActions;
            }
            else if (!doubleFound && matches > 1) {
                for (int j4 = 0; j4 < 3; ++j4) {
                    if (matching[j4]) {
                        chosenAction[j4] = testOrder[i4];
                    }
                }
                doubleFound = true;
            }
        }
        Boolean singleAction = true;
        if (w.loopComplete) {
            singleAction = false;
            if (w.getHarem().length > 0) {
                ForsakenDowntime(t, p, f, w, w.save, exhausted);
            }
            else {
                Shop(t, p, f, w);
            }
        }
        else if (chosenAction[0] == chosenAction[1] && chosenAction[0] == chosenAction[2]) {
            w.getCast()[0].TripleDowntime(t, p, f, w, w.getCast()[1], w.getCast()[2], chosenAction[0]);
        }
        else if (chosenAction[0] == chosenAction[1]) {
            w.getCast()[0].DoubleDowntime(t, p, f, w, w.getCast()[1], chosenAction[0]);
            if (w.getCast()[2] != null) {
                singleAction = false;
                p.removeAll();
                final JButton Continue = new JButton("Continue");
                Continue.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        w.getCast()[2].SingleDowntime(t, p, f, w, chosenAction[2]);
                        p.removeAll();
                        final JButton Continue2 = new JButton("Continue");
                        Continue2.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
                                    Project.ForsakenDowntime(t, p, f, w, w.save, exhausted);
                                }
                                else {
                                    Project.Shop(t, p, f, w);
                                }
                            }
                        });
                        p.add(Continue2);
                        p.validate();
                        p.repaint();
                    }
                });
                p.add(Continue);
                p.validate();
                p.repaint();
            }
        }
        else if (chosenAction[0] == chosenAction[2]) {
            w.getCast()[0].DoubleDowntime(t, p, f, w, w.getCast()[2], chosenAction[0]);
            singleAction = false;
            p.removeAll();
            final JButton Continue = new JButton("Continue");
            Continue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                    w.getCast()[1].SingleDowntime(t, p, f, w, chosenAction[1]);
                    p.removeAll();
                    final JButton Continue2 = new JButton("Continue");
                    Continue2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
                                Project.ForsakenDowntime(t, p, f, w, w.save, exhausted);
                            }
                            else {
                                Project.Shop(t, p, f, w);
                            }
                        }
                    });
                    p.add(Continue2);
                    p.validate();
                    p.repaint();
                }
            });
            p.add(Continue);
            p.validate();
            p.repaint();
        }
        else if (chosenAction[1] == chosenAction[2] && chosenAction[1] >= 0) {
            w.getCast()[0].SingleDowntime(t, p, f, w, chosenAction[0]);
            singleAction = false;
            p.removeAll();
            final JButton Continue = new JButton("Continue");
            Continue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                    w.getCast()[1].DoubleDowntime(t, p, f, w, w.getCast()[2], chosenAction[1]);
                    p.removeAll();
                    final JButton Continue2 = new JButton("Continue");
                    Continue2.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
                                Project.ForsakenDowntime(t, p, f, w, w.save, exhausted);
                            }
                            else {
                                Project.Shop(t, p, f, w);
                            }
                        }
                    });
                    p.add(Continue2);
                    p.validate();
                    p.repaint();
                }
            });
            p.add(Continue);
            p.validate();
            p.repaint();
        }
        else {
            w.getCast()[0].SingleDowntime(t, p, f, w, chosenAction[0]);
            if (w.getCast()[1] != null) {
                singleAction = false;
                p.removeAll();
                final JButton Continue = new JButton("Continue");
                Continue.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        w.getCast()[1].SingleDowntime(t, p, f, w, chosenAction[1]);
                        if (w.getCast()[2] != null) {
                            p.removeAll();
                            final JButton Continue2 = new JButton("Continue");
                            Continue2.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                                    w.getCast()[2].SingleDowntime(t, p, f, w, chosenAction[2]);
                                    p.removeAll();
                                    final JButton Continue3 = new JButton("Continue");
                                    Continue3.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(final ActionEvent e) {
                                            if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
                                                Project.ForsakenDowntime(t, p, f, w, w.save, exhausted);
                                            }
                                            else {
                                                Project.Shop(t, p, f, w);
                                            }
                                        }
                                    });
                                    p.add(Continue3);
                                    p.validate();
                                    p.repaint();
                                }
                            });
                            p.add(Continue2);
                            p.validate();
                            p.repaint();
                        }
                        else {
                            p.removeAll();
                            final JButton Continue3 = new JButton("Continue");
                            Continue3.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    if (w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
                                        Project.ForsakenDowntime(t, p, f, w, w.save, exhausted);
                                    }
                                    else {
                                        Project.Shop(t, p, f, w);
                                    }
                                }
                            });
                            p.add(Continue3);
                            p.validate();
                            p.repaint();
                        }
                    }
                });
                p.add(Continue);
                p.validate();
                p.repaint();
            }
        }
        if (singleAction) {
            p.removeAll();
            final JButton Continue = new JButton("Continue");
            Continue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!w.hardMode && w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
                        Project.ForsakenDowntime(t, p, f, w, w.save, exhausted);
                    }
                    else {
                        Project.Shop(t, p, f, w);
                    }
                }
            });
            p.add(Continue);
            p.validate();
            p.repaint();
        }
    }
    
    public static void Shop(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        p.removeAll();
        w.active = true;
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
        final File saveLocation = new File(String.valueOf(path) + File.separator + "saves.sav");
        if (saveLocation.exists()) {
            final ReadObject robj = new ReadObject();
            w.save = robj.deserializeSaveData(String.valueOf(path) + File.separator + "saves.sav");
            if (w.save.sceneText == null) {
                w.save.organizeScenes(48);
            }
            else if (w.save.sceneText.length < 48) {
                w.save.organizeScenes(48);
            }
        }
        else {
            w.save = new SaveData();
            if (w.save.sceneText == null) {
                w.save.organizeScenes(48);
            }
            else if (w.save.sceneText.length < 48) {
                w.save.organizeScenes(48);
            }
        }
        if (w.save.harem == null) {
            w.save.harem = new Forsaken[0];
        }
        if (t.getText().length() > 0) {
            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        }
        for (int j = 0; j < 3; ++j) {
            if (w.getCast()[j] != null) {
                w.getCast()[j].setTextSize(w.getTextSize());
                w.getCast()[j].world = w;
            }
        }
        if (w.getTextSize() == 0) {
            w.switchTextSize();
        }
        if (w.usedForsaken != null && w.save != null && w.getHarem() != null && w.getHarem().length > w.usedForsakenIndex && w.getHarem()[w.usedForsakenIndex].EECost() == w.usedForsaken.EECost()) {
            w.usedForsaken = w.getHarem()[w.usedForsakenIndex];
        }
        else if (w.usedForsaken != null) {
            w.evilEnergy += w.usedForsaken.EECost();
            w.usedForsaken = null;
        }
        if (w.campaign) {
            w.append(t, String.valueOf(w.cityName) + " - ");
        }
        clearPortraits();
        if (w.usedForsaken != null) {
            final String[] nameDisplay = { null, null, null, w.usedForsaken.mainName, null };
            if (w.usedForsaken.flavorObedience() < 20) {
                changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Emotion.ANGER, Emotion.NEUTRAL);
            }
            else if (w.usedForsaken.flavorObedience() < 40) {
                changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Emotion.ANGER, Emotion.SHAME);
            }
            else if (w.usedForsaken.flavorObedience() < 61) {
                changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Emotion.FEAR, Emotion.SHAME);
            }
            else if (w.usedForsaken.flavorObedience() < 81) {
                changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Emotion.FOCUS, Emotion.NEUTRAL);
            }
            else {
                changePortrait(w.usedForsaken.gender, w.usedForsaken.type, true, true, w, nameDisplay, 3, Emotion.JOY, Emotion.FOCUS);
            }
        }
        w.append(t, "Day " + w.getDay());
        if (w.clampPercent != 100) {
            w.append(t, "\nDamage Mitigation: " + (100 - w.clampPercent) + "% per level");
        }
        if (w.eventOffset != 0) {
            w.append(t, "\nPreparedness: Final Battle on Day " + (50 - w.eventOffset * 3));
        }
        if (w.downtimeMultiplier != 100) {
            w.append(t, "\nLuxuries: " + w.downtimeMultiplier + "% Trauma resolution speed");
        }
        if (w.types[2] != null) {
            int superior = 0;
            for (int k = 0; k < 3; ++k) {
                if (w.types[k] == Chosen.Species.SUPERIOR) {
                    ++superior;
                }
            }
            w.append(t, "\nElites: " + superior + " Superior Chosen");
        }
        w.printShopTutorial(t);
        if (w.getCast()[1] != null) {
            w.printGroupTutorial(t);
        }
        if ((w.getDay() > 50 - w.eventOffset * 3 || w.getEarlyCheat() || w.cheater) && (!w.campaign || w.getEarlyCheat())) {
            final JButton Cheat = new JButton("Cheat");
            Cheat.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (w.isCheater()) {
                        Project.Cheat(t, p, f, w);
                    }
                    else {
                        p.removeAll();
                        w.append(t, "\n\n" + w.getSeparator() + "\n\nActivating Cheat Mode will give you unlimited Evil Energy as well as other benefits");
                        if (w.getDay() <= 35 && w.hardMode) {
                            w.append(t, ", but you will not receive a score for the playthrough");
                        }
                        w.append(t, ".  Activate Cheat Mode?");
                        final JButton Activate = new JButton("Activate Cheat Mode");
                        Activate.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                w.setCheater();
                                Project.Cheat(t, p, f, w);
                            }
                        });
                        p.add(Activate);
                        final JButton Cancel = new JButton("Cancel");
                        Cancel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.Shop(t, p, f, w);
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
        for (int j = 0; j < w.getTechs().length && !w.loopComplete; ++j) {
            if (!w.getTechs()[j].isOwned()) {
                Boolean shown = false;
                for (int l = 0; l < w.getTechs()[j].getPrereqs().length; ++l) {
                    if (w.getTechs()[j].getPrereqs()[l].isOwned()) {
                        shown = true;
                    }
                }
                if (w.getTechs()[j].getPrereqs().length == 0) {
                    shown = true;
                }
                if (shown) {
                    w.append(t, "\n\n");
                    w.getTechs()[j].printSummary(w, t);
                    int ownedPrereqs = 0;
                    for (int m = 0; m < w.getTechs()[j].getPrereqs().length; ++m) {
                        if (w.getTechs()[j].getPrereqs()[m].isOwned()) {
                            ++ownedPrereqs;
                        }
                    }
                    final int thisTech = j;
                    if (w.getEvilEnergy() >= w.getTechs()[j].getCost() && ownedPrereqs >= w.getTechs()[j].getPrereqReqs()) {
                        final JButton Buy = new JButton(w.getTechs()[j].getName()) {
                            @Override
                            public Point getToolTipLocation(final MouseEvent e) {
                                return new Point(0, -30);
                            }
                        };
                        Buy.setToolTipText(w.getTechs()[j].getTooltip());
                        Buy.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                p.removeAll();
                                w.append(t, "\n\n" + w.getSeparator() + "\n\n" + w.getTechs()[thisTech].getName() + " costs " + w.getTechs()[thisTech].getCost() + " Evil Energy.  Will you develop it now?");
                                final JButton Confirm = new JButton("Confirm");
                                Confirm.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(final ActionEvent e) {
                                        w.getTechs()[thisTech].buy(w);
                                        Project.advanceDowntimeAction(p, w, thisTech);
                                        Project.Shop(t, p, f, w);
                                    }
                                });
                                if (thisTech != 48 || w.getCast()[2] != null) {
                                    p.add(Confirm);
                                }
                                else if (thisTech == 48) {
                                    w.append(t, "  (Forbidden until all three Chosen have been encountered.)");
                                }
                                final JButton Cancel = new JButton("Cancel");
                                Cancel.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(final ActionEvent e) {
                                        Project.Shop(t, p, f, w);
                                    }
                                });
                                p.add(Cancel);
                                p.validate();
                                p.repaint();
                            }
                        });
                        p.add(Buy);
                    }
                }
            }
        }
        if (!w.loopComplete) {
            w.append(t, "\n\nYou have " + w.getEvilEnergy() + " Evil Energy.");
        }
        else if (w.day <= 50 - 3 * w.eventOffset) {
            w.append(t, "\n\n" + (51 - w.day - 3 * w.eventOffset) + " day");
            if (51 - w.day - 3 * w.eventOffset != 1) {
                w.append(t, "s remain ");
            }
            else {
                w.append(t, " remains ");
            }
            w.append(t, "before your attack on the next city.");
        }
        else {
            w.append(t, "\n\nIt is time to choose your next destination.");
        }
        if (w.newAchievement()) {
            w.greenAppend(t, "\n\nYou have obtained a new Achievement!  See the Info menu for more details.");
        }
        else {
            w.printTip(t);
        }
        final JButton Profiles = new JButton("Info");
        Profiles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.ShowInformation(t, p, f, w);
            }
        });
        p.add(Profiles);
        if (w.getTechs()[3].isOwned()) {
            final JButton CustomBody = new JButton("Commander");
            CustomBody.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (!w.getBodyStatus()[0] && !w.hardMode && w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
                        Project.ForsakenMenu(t, p, f, w, w.save, 0);
                    }
                    else {
                        Project.Customize(t, p, f, w);
                    }
                }
            });
            if (w.loopComplete) {
                CustomBody.setText("Forsaken");
            }
            if (w.getHarem().length > 0 || !w.loopComplete) {
                p.add(CustomBody);
            }
            else if (w.day != 51 - w.eventOffset * 3 || !w.campaign) {
                final JButton Pass = new JButton("Pass Time");
                Pass.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.PostBattle(t, p, f, w);
                    }
                });
                p.add(Pass);
            }
            if (!w.getBodyStatus()[0] && w.usedForsaken == null && w.getEvilEnergy() > 0 && !w.loopComplete) {
                CustomBody.setBackground(Color.YELLOW);
            }
        }
        final JButton Data = new JButton("Data");
        Data.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\nSelect an option.");
                p.removeAll();
                final JButton NewSave = new JButton("New Save File");
                NewSave.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Data(t, p, f, w, "newsave", 0, true);
                    }
                });
                p.add(NewSave);
                final JButton Overwrite = new JButton("Overwrite Save");
                Overwrite.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Data(t, p, f, w, "overwrite", 0, true);
                    }
                });
                p.add(Overwrite);
                final JButton Load = new JButton("Load");
                Load.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Data(t, p, f, w, "load", 0, true);
                    }
                });
                p.add(Load);
                final JButton Delete = new JButton("Delete");
                Delete.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Data(t, p, f, w, "delete", 0, true);
                    }
                });
                p.add(Delete);
                final JButton Import = new JButton("Import");
                Import.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Data(t, p, f, w, "import", 0, true);
                    }
                });
                p.add(Import);
                final JButton Export = new JButton("Export");
                Export.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Data(t, p, f, w, "export", 0, true);
                    }
                });
                p.add(Export);
                final JButton Back = new JButton("Back");
                Back.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Shop(t, p, f, w);
                    }
                });
                p.add(Back);
                p.validate();
                p.repaint();
            }
        });
        p.add(Data);
        final JButton Quit = new JButton("Quit");
        Quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\nReally quit?  Current progress will not be saved.");
                p.removeAll();
                final JButton ReallyQuit = new JButton("Quit to main menu");
                ReallyQuit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        final WorldState x = new WorldState();
                        x.copySettings(t, w);
                        x.copyToggles(w);
                        x.save = w.save;
                        Project.IntroOne(t, p, f, x);
                    }
                });
                p.add(ReallyQuit);
                final JButton Back = new JButton("Back");
                Back.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Shop(t, p, f, w);
                    }
                });
                p.add(Back);
                p.validate();
                p.repaint();
            }
        });
        p.add(Quit);
        final JButton NextBattle = new JButton("Next Battle");
        NextBattle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (w.getCast()[1] == null) {
                    Project.ConfirmBattle(t, p, f, w, w.getCast()[0]);
                }
                else {
                    Project.pickStartingTarget(t, p, f, w);
                }
            }
        });
        if (!w.loopComplete) {
            p.add(NextBattle);
        }
        else if (w.day > 50 - w.eventOffset * 3) {
            final JButton NextCity = new JButton("Next City");
            NextCity.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.PickNextCity(t, p, f, w);
                }
            });
            p.add(NextCity);
        }
        if (w.writePossible()) {
            addWriteButton(p, w);
        }
        p.validate();
        p.repaint();
        w.readCommentary(t);
    }
    
    public static void PickNextCity(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich city will you attack next?");
        if (w.nextCities == null || w.nextCities.length == 0) {
            w.nextCities = new WorldState[2];
            for (int i = 0; i < w.nextCities.length; ++i) {
                w.nextCities[i] = new WorldState();
                w.nextCities[i].campaignRand = w.campaignRand;
                w.nextCities[i].save = w.save;
                w.nextCities[i].copySettings(t, w);
                w.nextCities[i].copyToggles(w);
                w.nextCities[i].setGenders(w.nextCities[i].getGenderBalance());
                w.nextCities[i].active = true;
                w.nextCities[i].campaign = true;
                w.nextCities[i].loops = w.loops + 1;
                w.nextCities[i].cityName = w.nextCities[i].getCityName(w.loops * 2 + i + 1);
                w.nextCities[i].earlyCheat = w.earlyCheat;
                w.nextCities[i].hardMode = false;
                w.nextCities[i].eventOffset = 0;
                w.nextCities[i].clampStart = 11;
                w.nextCities[i].clampPercent = 100;
                w.nextCities[i].downtimeMultiplier = 100;
                int difficultyScore = 0;
                int clampRemoval = 0;
                while (difficultyScore < w.nextCities[i].loops * 10) {
                    final double difficultyType = w.nextCities[i].campaignRand.nextDouble();
                    if (difficultyType < 0.3055555555555556 && clampRemoval < 1660) {
                        w.nextCities[i].clampStart = 1;
                        int increase = (int)(w.nextCities[i].campaignRand.nextDouble() * 5.0) + 1;
                        if (increase > w.nextCities[i].loops * 10 - difficultyScore) {
                            increase = w.nextCities[i].loops * 10 - difficultyScore;
                        }
                        if (!w.earlyCheat) {
                            clampRemoval += increase * 15;
                        }
                        difficultyScore += increase;
                    }
                    else if (difficultyType < 0.6111111111111112) {
                        if (difficultyScore > w.nextCities[i].loops * 10 - 3 || w.nextCities[i].eventOffset >= 10) {
                            continue;
                        }
                        if (!w.earlyCheat) {
                            final WorldState worldState = w.nextCities[i];
                            ++worldState.eventOffset;
                        }
                        difficultyScore += 3;
                    }
                    else if (difficultyType < 0.9166666666666666) {
                        int increase = (int)(w.nextCities[i].campaignRand.nextDouble() * 5.0) + 1;
                        if (increase > w.nextCities[i].loops * 10 - difficultyScore) {
                            increase = w.nextCities[i].loops * 10 - difficultyScore;
                        }
                        difficultyScore += increase;
                        while (increase > 0) {
                            if (w.earlyCheat) {
                                break;
                            }
                            w.nextCities[i].downtimeMultiplier = w.nextCities[i].downtimeMultiplier * 11 / 10;
                            --increase;
                        }
                    }
                    else {
                        if (difficultyType >= 1.0 || w.nextCities[i].loops * 10 - difficultyScore < 11 || w.nextCities[i].types[0] != null) {
                            continue;
                        }
                        if (w.nextCities[i].types[2] == null) {
                            w.nextCities[i].types[2] = Chosen.Species.SUPERIOR;
                        }
                        else if (w.nextCities[i].types[1] == null) {
                            w.nextCities[i].types[1] = Chosen.Species.SUPERIOR;
                        }
                        else {
                            w.nextCities[i].types[0] = Chosen.Species.SUPERIOR;
                        }
                        difficultyScore += 11;
                    }
                }
                while (clampRemoval > 0) {
                    final WorldState worldState2 = w.nextCities[i];
                    --worldState2.clampPercent;
                    clampRemoval -= 10 + (100 - w.clampPercent) / 5;
                }
                w.nextCities[i].conquered = w.conquered;
                w.nextCities[i].sacrificed = w.sacrificed;
                w.nextCities[i].returning = w.returning;
                w.nextCities[i].deceased = w.deceased;
                w.nextCities[i].formerChosen = w.formerChosen;
                w.nextCities[i].campaignCustom = w.campaignCustom;
                if (i == 1 && !w.earlyCheat) {
                    int differences = 0;
                    final int requirement = (int)Math.sqrt(w.nextCities[0].loops * 5);
                    differences += Math.abs(w.nextCities[0].clampPercent - w.nextCities[1].clampPercent);
                    differences += Math.abs((w.nextCities[0].eventOffset - w.nextCities[1].eventOffset) * 3);
                    int higher = w.nextCities[0].downtimeMultiplier;
                    int lower = w.nextCities[1].downtimeMultiplier;
                    if (lower > higher) {
                        final int storage = lower;
                        lower = higher;
                        higher = storage;
                    }
                    while (higher > lower) {
                        higher = higher * 10 / 11;
                        ++differences;
                    }
                    Boolean sameElites = true;
                    Boolean noElites = true;
                    for (int j = 0; j < 3; ++j) {
                        if (w.nextCities[0].types[j] != w.nextCities[1].types[j] && (w.nextCities[0].types[j] == null || w.nextCities[1].types[j] == null)) {
                            sameElites = false;
                            differences += 11;
                        }
                        if (w.nextCities[0].types[j] != null || w.nextCities[1].types[j] != null) {
                            noElites = false;
                        }
                    }
                    if (differences < requirement || (sameElites && !noElites)) {
                        i = -1;
                    }
                }
            }
        }
        for (int i = 0; i < w.nextCities.length; ++i) {
            w.append(t, "\n\n");
            w.underlineAppend(t, w.nextCities[i].cityName);
            if (w.nextCities[i].clampPercent != 100) {
                w.append(t, "\nDamage Mitigation: " + (100 - w.nextCities[i].clampPercent) + "% per level");
            }
            if (w.nextCities[i].eventOffset != 0) {
                w.append(t, "\nPreparedness: Final Battle on Day " + (50 - w.nextCities[i].eventOffset * 3));
            }
            if (w.nextCities[i].downtimeMultiplier != 100) {
                w.append(t, "\nLuxuries: " + w.nextCities[i].downtimeMultiplier + "% Trauma resolution speed");
            }
            if (w.nextCities[i].types[2] != null) {
                int superior = 0;
                for (int k = 0; k < 3; ++k) {
                    if (w.nextCities[i].types[k] == Chosen.Species.SUPERIOR) {
                        ++superior;
                    }
                }
                w.append(t, "\nElites: " + superior + " Superior Chosen");
            }
            final JButton ThisOne = new JButton(w.nextCities[i].cityName);
            final WorldState pickedWorld = w.nextCities[i];
            ThisOne.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    p.removeAll();
                    w.append(t, "\n\n" + w.getSeparator() + "\n\n" + pickedWorld.cityName + " will be targeted.  Are you sure?");
                    final JButton Confirm = new JButton("Confirm");
                    Confirm.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            pickedWorld.initialize();
                            final Chosen newChosen = new Chosen();
                            newChosen.setNumber(0);
                            newChosen.generate(pickedWorld);
                            pickedWorld.addChosen(newChosen);
                            pickedWorld.achievementSeen = w.achievementSeen;
                            pickedWorld.evilEnergy = w.achievementHeld(0)[0];
                            Project.Shop(t, p, f, pickedWorld);
                        }
                    });
                    p.add(Confirm);
                    final JButton Cancel = new JButton("Cancel");
                    Cancel.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            Project.PickNextCity(t, p, f, w);
                        }
                    });
                    p.add(Cancel);
                    p.validate();
                    p.repaint();
                }
            });
            p.add(ThisOne);
        }
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.Shop(t, p, f, w);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void ShowInformation(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator());
        if (w.getTechs()[0].isOwned() && !w.loopComplete) {
            w.append(t, "\n\nOverall corruption progress:");
            int longest = 3;
            for (int i = 0; i < 3; ++i) {
                if (w.getCast()[i] != null && w.getCast()[i].getMainName().length() > longest) {
                    longest = w.getCast()[i].getMainName().length();
                }
            }
            for (int i = 0; i < 3; ++i) {
                if (w.getCast()[i] != null) {
                    w.append(t, "\n\n" + w.getCast()[i].getMainName());
                    for (int j = w.getCast()[i].getMainName().length(); j < longest; ++j) {
                        w.append(t, " ");
                    }
                    String gap = "";
                    for (int k = 0; k < longest - 3; ++k) {
                        gap = String.valueOf(gap) + " ";
                    }
                    w.append(t, "  +2 T1 T2 T3 T4\nMOR" + gap + " ");
                    if (w.getCast()[i].getMorality() > 66) {
                        w.append(t, "[");
                        if (!w.getCast()[i].bonusHATE) {
                            w.append(t, "X");
                        }
                        else {
                            w.append(t, " ");
                        }
                        w.append(t, "][");
                    }
                    else {
                        w.append(t, "   [");
                    }
                    if (w.getCast()[i].temptReq == 100000L) {
                        if (w.getCast()[i].isRuthless()) {
                            w.append(t, "X");
                        }
                        else {
                            w.append(t, " ");
                        }
                        w.append(t, "][");
                        if (!w.getCast()[i].isVVirg()) {
                            w.append(t, "X");
                        }
                        else {
                            w.append(t, " ");
                        }
                        w.append(t, "][");
                        if (w.getCast()[i].timesSlaughtered() > 0) {
                            w.append(t, "X");
                        }
                        else if (w.getCast()[i].usingSlaughter) {
                            w.append(t, "/");
                        }
                        else {
                            w.append(t, " ");
                        }
                        w.append(t, "][");
                        if (w.getCast()[i].isImpregnated()) {
                            w.append(t, "X");
                        }
                        else if (w.getCast()[i].getImpregnationEffectiveness() >= w.getCast()[i].impregnationReq()) {
                            w.append(t, "/");
                        }
                        else {
                            w.append(t, " ");
                        }
                    }
                    else {
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
                        if (!w.getCast()[i].bonusPLEA) {
                            w.append(t, "X");
                        }
                        else {
                            w.append(t, " ");
                        }
                        w.append(t, "][");
                    }
                    else {
                        w.append(t, "   [");
                    }
                    if (w.getCast()[i].isLustful()) {
                        w.append(t, "X");
                    }
                    else {
                        w.append(t, " ");
                    }
                    w.append(t, "][");
                    if (!w.getCast()[i].isCVirg()) {
                        w.append(t, "X");
                    }
                    else {
                        w.append(t, " ");
                    }
                    w.append(t, "][");
                    if (w.getCast()[i].timesFantasized() > 0) {
                        w.append(t, "X");
                    }
                    else if (w.getCast()[i].usingFantasize) {
                        w.append(t, "/");
                    }
                    else {
                        w.append(t, " ");
                    }
                    w.append(t, "][");
                    if (w.getCast()[i].isHypnotized()) {
                        w.append(t, "X");
                    }
                    else if (w.getCast()[i].getHypnosisEffectiveness() >= w.getCast()[i].hypnosisReq()) {
                        w.append(t, "/");
                    }
                    else {
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
                        if (!w.getCast()[i].bonusINJU) {
                            w.append(t, "X");
                        }
                        else {
                            w.append(t, " ");
                        }
                        w.append(t, "][");
                    }
                    else {
                        w.append(t, "   [");
                    }
                    if (w.getCast()[i].isMeek()) {
                        w.append(t, "X");
                    }
                    else {
                        w.append(t, " ");
                    }
                    w.append(t, "][");
                    if (w.getCast()[i].temptReq == 100000L) {
                        if (!w.getCast()[i].isAVirg()) {
                            w.append(t, "X");
                        }
                        else {
                            w.append(t, " ");
                        }
                        w.append(t, "][");
                        if (w.getCast()[i].timesDetonated() > 0) {
                            w.append(t, "X");
                        }
                        else if (w.getCast()[i].usingDetonate) {
                            w.append(t, "/");
                        }
                        else {
                            w.append(t, " ");
                        }
                        w.append(t, "][");
                        if (w.getCast()[i].isDrained()) {
                            w.append(t, "X");
                        }
                        else if (w.getCast()[i].getDrainEffectiveness() >= w.getCast()[i].drainReq()) {
                            w.append(t, "/");
                        }
                        else {
                            w.append(t, " ");
                        }
                    }
                    else {
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
                        if (!w.getCast()[i].bonusEXPO) {
                            w.append(t, "X");
                        }
                        else {
                            w.append(t, " ");
                        }
                        w.append(t, "][");
                    }
                    else {
                        w.append(t, "   [");
                    }
                    if (w.getCast()[i].isDebased()) {
                        w.append(t, "X");
                    }
                    else {
                        w.append(t, " ");
                    }
                    w.append(t, "][");
                    if (!w.getCast()[i].isModest()) {
                        w.append(t, "X");
                    }
                    else {
                        w.append(t, " ");
                    }
                    w.append(t, "][");
                    if (w.getCast()[i].timesStripped() > 0) {
                        w.append(t, "X");
                    }
                    else if (w.getCast()[i].usingStrip) {
                        w.append(t, "/");
                    }
                    else {
                        w.append(t, " ");
                    }
                    w.append(t, "][");
                    if (w.getCast()[i].isParasitized()) {
                        w.append(t, "X");
                    }
                    else if (w.getCast()[i].getParasitismEffectiveness() >= w.getCast()[i].parasitismReq()) {
                        w.append(t, "/");
                    }
                    else {
                        w.append(t, " ");
                    }
                    w.append(t, "]");
                    if (w.getCast()[i].getParasitismEffectiveness() > 100) {
                        if (w.getCast()[i].getParasitismEffectiveness() < 1000) {
                            w.append(t, " ");
                        }
                        w.append(t, " " + w.getCast()[i].getParasitismEffectiveness() + "%");
                    }
                }
            }
            for (int i = 0; i < 3 && !w.loopComplete; ++i) {
                if (w.getCast()[i] != null) {
                    final int thisChosen = i;
                    final JButton openProfile = new JButton(String.valueOf(w.getCast()[i].getMainName()) + "'s Profile");
                    openProfile.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            p.removeAll();
                            Project.clearPortraits();
                            final Forsaken.Gender convertGender = w.getCast()[thisChosen].convertGender();
                            final Chosen.Species type = w.getCast()[thisChosen].type;
                            final Boolean value = false;
                            final Boolean value2 = false;
                            final WorldState val$w = w;
                            final String[] names = new String[5];
                            names[0] = w.getCast()[thisChosen].mainName;
                            Project.changePortrait(convertGender, type, value, value2, val$w, names, 0, Emotion.NEUTRAL, Emotion.NEUTRAL);
                            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                            w.getCast()[thisChosen].printIntro(t, w);
                            w.getCast()[thisChosen].printProfile(t, p, f, w);
                            final JButton Continue = new JButton("Continue");
                            Continue.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    Project.ShowInformation(t, p, f, w);
                                }
                            });
                            p.add(Continue);
                            p.validate();
                            p.repaint();
                        }
                    });
                    p.add(openProfile);
                }
            }
        }
        w.append(t, "\n\nWhich information do you want to view?");
        final JButton Statistics = new JButton("Statistics");
        Statistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                int highest = w.getCast()[0].getMainName().length();
                if (highest < 3) {
                    highest = 3;
                }
                if (w.getCast()[1] != null && w.getCast()[1].getMainName().length() > highest) {
                    highest = w.getCast()[1].getMainName().length();
                }
                if (w.getCast()[2] != null && w.getCast()[2].getMainName().length() > highest) {
                    highest = w.getCast()[2].getMainName().length();
                }
                final String[] names = { "", "", "" };
                names[0] = w.getCast()[0].getMainName();
                while (names[0].length() < highest) {
                    names[0] = String.valueOf(names[0]) + " ";
                }
                if (w.getCast()[1] != null) {
                    names[1] = w.getCast()[1].getMainName();
                    while (names[1].length() < highest) {
                        names[1] = String.valueOf(names[1]) + " ";
                    }
                }
                if (w.getCast()[2] != null) {
                    names[2] = w.getCast()[2].getMainName();
                    while (names[2].length() < highest) {
                        names[2] = String.valueOf(names[2]) + " ";
                    }
                }
                String totals;
                for (totals = "All"; totals.length() < highest; totals = String.valueOf(totals) + " ") {}
                w.append(t, "\n\n" + w.getSeparator() + "\n\nOpening Levels Taken:\n\n");
                for (int spaces = highest; spaces > 0; --spaces) {
                    w.append(t, " ");
                }
                if (w.tickle()) {
                    w.append(t, "  FEAR  DISG  TICK  SHAM Total");
                }
                else {
                    w.append(t, "  FEAR  DISG  PAIN  SHAM Total");
                }
                int totalFEAR = 0;
                int totalDISG = 0;
                int totalPAIN = 0;
                int totalSHAM = 0;
                for (int i = 0; i < 3; ++i) {
                    if (w.getCast()[i] != null) {
                        w.append(t, "\n" + names[i] + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getFEARopenings()) + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getDISGopenings()) + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getPAINopenings()) + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getSHAMopenings()) + " " + w.getCast()[i].fixedFormat(w.getCast()[i].getFEARopenings() + w.getCast()[i].getDISGopenings() + w.getCast()[i].getPAINopenings() + w.getCast()[i].getSHAMopenings()));
                        totalFEAR += w.getCast()[i].getFEARopenings();
                        totalDISG += w.getCast()[i].getDISGopenings();
                        totalPAIN += w.getCast()[i].getPAINopenings();
                        totalSHAM += w.getCast()[i].getSHAMopenings();
                    }
                }
                w.append(t, "\n" + totals + " " + w.getCast()[0].fixedFormat(totalFEAR) + " " + w.getCast()[0].fixedFormat(totalDISG) + " " + w.getCast()[0].fixedFormat(totalPAIN) + " " + w.getCast()[0].fixedFormat(totalSHAM) + " " + w.getCast()[0].fixedFormat(totalFEAR + totalDISG + totalPAIN + totalSHAM));
            }
        });
        p.add(Statistics);
        final JButton Upgrades = new JButton("View All Upgrades");
        Upgrades.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\nSelect an upgrade to view.");
                Project.ViewUpgrades(t, p, f, w, 0);
            }
        });
        p.add(Upgrades);
        final JButton Achievements = new JButton("Achievements");
        Achievements.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.ViewAchievements(t, p, f, w, 0);
            }
        });
        if (w.campaign) {
            p.add(Achievements);
        }
        if (w.newAchievement()) {
            Achievements.setBackground(new Color(255, 225, 125));
        }
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.Shop(t, p, f, w);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void ViewAchievements(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final int page) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator());
        if (page > 0) {
            final JButton PreviousPage = new JButton("Previous Page");
            PreviousPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.ViewAchievements(t, p, f, w, page - 1);
                }
            });
            p.add(PreviousPage);
        }
        for (int i = page * 5; i < page * 5 + 5 && i < w.achievementSeen.length; ++i) {
            w.append(t, "\n\n");
            String description = "";
            if (i == 0) {
                w.underlineAppend(t, "Residual Energy");
                description = String.valueOf(description) + "Forsaken Sacrificed: " + w.achievementHeld(0)[1] + "\n";
                description = String.valueOf(description) + "Level: " + w.achievementHeld(0)[0];
                if (w.achievementHeld(0)[0] == 0) {
                    description = String.valueOf(description) + " (Next: 1 sacrifice)\nBonus: N/A";
                }
                else if (w.achievementHeld(0)[0] == 1) {
                    description = String.valueOf(description) + " (Next: 3 sacrifices)\nBonus: +1 Starting EE";
                }
                else if (w.achievementHeld(0)[0] == 2) {
                    description = String.valueOf(description) + " (Next: 6 sacrifices)\nBonus: +2 Starting EE";
                }
                else if (w.achievementHeld(0)[0] == 3) {
                    description = String.valueOf(description) + " (Next: 15 sacrifices)\nBonus: +3 Starting EE";
                }
                else if (w.achievementHeld(0)[0] == 4) {
                    description = String.valueOf(description) + " (Next: 60 sacrifices)\nBonus: +4 Starting EE";
                }
                else {
                    description = String.valueOf(description) + "\nBonus: +5 Starting EE";
                }
                description = String.valueOf(description) + "\nThe supernaturally-enhanced bodies of former Chosen make for excellent breeding stock.  This role prevents them from fighting in battle, but it can give you a head start in establishing new bases of operations.  And they tend to start enjoying it before too long.";
            }
            else if (i == 1) {
                w.underlineAppend(t, "Impregnation Specialty");
                description = String.valueOf(description) + "Chosen Impregnated: " + w.achievementHeld(i)[1] + "\n";
                description = String.valueOf(description) + "Level: " + w.achievementHeld(i)[0];
                if (w.achievementHeld(i)[0] == 0) {
                    description = String.valueOf(description) + " (Next: 4 impregnated)\nBonus: N/A";
                }
                else if (w.achievementHeld(i)[0] == 1) {
                    description = String.valueOf(description) + " (Next: 10 impregnated)\nBonus: -200% Impregnation Threshold";
                }
                else if (w.achievementHeld(i)[0] == 2) {
                    description = String.valueOf(description) + " (Next: 25 impregnated)\nBonus: -400% Impregnation Threshold";
                }
                else if (w.achievementHeld(i)[0] == 3) {
                    description = String.valueOf(description) + " (Next: 60 impregnated)\nBonus: -600% Impregnation Threshold";
                }
                else if (w.achievementHeld(i)[0] == 4) {
                    description = String.valueOf(description) + " (Next: 160 impregnated)\nBonus: -700% Impregnation Threshold";
                }
                else {
                    description = String.valueOf(description) + "\nBonus: -750% Impregnation Threshold";
                }
                description = String.valueOf(description) + "\nAs the Chosen hear rumors that you're able to impregnate even them, their lack of faith in their own protections will cause it to become even easier to do so.";
            }
            else if (i == 2) {
                w.underlineAppend(t, "Hypnosis Specialty");
                description = String.valueOf(description) + "Chosen Hypnotized: " + w.achievementHeld(i)[1] + "\n";
                description = String.valueOf(description) + "Level: " + w.achievementHeld(i)[0];
                if (w.achievementHeld(i)[0] == 0) {
                    description = String.valueOf(description) + " (Next: 4 hypnotized)\nBonus: N/A";
                }
                else if (w.achievementHeld(i)[0] == 1) {
                    description = String.valueOf(description) + " (Next: 10 hypnotized)\nBonus: -200% Hypnosis Threshold";
                }
                else if (w.achievementHeld(i)[0] == 2) {
                    description = String.valueOf(description) + " (Next: 25 hypnotized)\nBonus: -400% Hypnosis Threshold";
                }
                else if (w.achievementHeld(i)[0] == 3) {
                    description = String.valueOf(description) + " (Next: 60 hypnotized)\nBonus: -600% Hypnosis Threshold";
                }
                else if (w.achievementHeld(i)[0] == 4) {
                    description = String.valueOf(description) + " (Next: 160 hypnotized)\nBonus: -700% Hypnosis Threshold";
                }
                else {
                    description = String.valueOf(description) + "\nBonus: -750% Hypnosis Threshold";
                }
                description = String.valueOf(description) + "\nMuch of the difficulty in Demonic Hypnosis comes from finding exploitable weaknesses in the target's thought process.  But all human minds share some similarities, and the more you break, the more tricks you figure out.";
            }
            else if (i == 3) {
                w.underlineAppend(t, "Drain Specialty");
                description = String.valueOf(description) + "Chosen Drained: " + w.achievementHeld(i)[1] + "\n";
                description = String.valueOf(description) + "Level: " + w.achievementHeld(i)[0];
                if (w.achievementHeld(i)[0] == 0) {
                    description = String.valueOf(description) + " (Next: 4 drained)\nBonus: N/A";
                }
                else if (w.achievementHeld(i)[0] == 1) {
                    description = String.valueOf(description) + " (Next: 10 drained)\nBonus: -200% Drain Threshold";
                }
                else if (w.achievementHeld(i)[0] == 2) {
                    description = String.valueOf(description) + " (Next: 25 drained)\nBonus: -400% Drain Threshold";
                }
                else if (w.achievementHeld(i)[0] == 3) {
                    description = String.valueOf(description) + " (Next: 60 drained)\nBonus: -600% Drain Threshold\n";
                }
                else if (w.achievementHeld(i)[0] == 4) {
                    description = String.valueOf(description) + " (Next: 160 drained)\nBonus: -700% Drain Threshold";
                }
                else {
                    description = String.valueOf(description) + "\nBonus: -750% Drain Threshold";
                }
                description = String.valueOf(description) + "\nThe Holy Energy which empowers the Chosen is inherently difficult for a Demon to absorb, but whenever you do successfully begin draining energy from one of the Chosen, her aura mingles with your own, and you find it easier to draw more of their energy into yourself.";
            }
            else if (i == 4) {
                w.underlineAppend(t, "Parasitism Specialty");
                description = String.valueOf(description) + "Chosen Parasitized: " + w.achievementHeld(i)[1] + "\n";
                description = String.valueOf(description) + "Level: " + w.achievementHeld(i)[0];
                if (w.achievementHeld(i)[0] == 0) {
                    description = String.valueOf(description) + " (Next: 4 parasitized)\nBonus: N/A";
                }
                else if (w.achievementHeld(i)[0] == 1) {
                    description = String.valueOf(description) + " (Next: 10 parasitized)\nBonus: -200% Parasitism Threshold";
                }
                else if (w.achievementHeld(i)[0] == 2) {
                    description = String.valueOf(description) + " (Next: 25 parasitized)\nBonus: -400% Parasitism Threshold";
                }
                else if (w.achievementHeld(i)[0] == 3) {
                    description = String.valueOf(description) + " (Next: 60 parasitized)\nBonus: -600% Parasitism Threshold";
                }
                else if (w.achievementHeld(i)[0] == 4) {
                    description = String.valueOf(description) + " (Next: 160 parasitized)\nBonus: -700% Parasitism Threshold";
                }
                else {
                    description = String.valueOf(description) + "\nBonus: -750% Parasitism Threshold";
                }
                description = String.valueOf(description) + "\nThe public loves to see the Chosen humiliated, and as it becomes more common for their transformations to become corrupted by you, everyone's anticipation for the next such corruption will do much of the work for you.";
            }
            else if (i == 5) {
                w.underlineAppend(t, "Temptation Specialty");
                description = String.valueOf(description) + "Chosen Tempted: " + w.achievementHeld(i)[1] + "\n";
                description = String.valueOf(description) + "Level: " + w.achievementHeld(i)[0];
                if (w.achievementHeld(i)[0] == 0) {
                    description = String.valueOf(description) + " (Next: 2 Tempted)\nBonus: N/A";
                }
                else if (w.achievementHeld(i)[0] == 1) {
                    description = String.valueOf(description) + " (Next: 5 Tempted)\nBonus: Tempt requirement decreases 15% per use";
                }
                else if (w.achievementHeld(i)[0] == 2) {
                    description = String.valueOf(description) + " (Next: 12 Tempted)\nBonus: Tempt requirement decreases 20% per use";
                }
                else if (w.achievementHeld(i)[0] == 3) {
                    description = String.valueOf(description) + " (Next: 30 Tempted)\nBonus: Tempt requirement decreases 25% per use";
                }
                else if (w.achievementHeld(i)[0] == 4) {
                    description = String.valueOf(description) + " (Next: 80 Tempted)\nBonus: Tempt requirement decreases 30% per use";
                }
                else {
                    description = String.valueOf(description) + "\nBonus: Tempt requirement decreases 35% per use";
                }
                description = String.valueOf(description) + "\nThe Chosen are carefully guided by their handlers and by society at large so that they don't even consider the possibility of turning to the side of the Demons.  But the more they see other Chosen being treated kindly by the Thralls, the more willing they'll be to think of you as a potential ally.";
            }
            else if (i == 6) {
                w.underlineAppend(t, "Heroine Hunter");
                description = String.valueOf(description) + "Superior Chosen Broken: " + w.achievementHeld(i)[1] + "\n";
                description = String.valueOf(description) + "Level: " + w.achievementHeld(i)[0];
                if (w.achievementHeld(i)[0] == 0) {
                    description = String.valueOf(description) + " (Next: 1 Broken)\nBonus: N/A";
                }
                else if (w.achievementHeld(i)[0] == 1) {
                    description = String.valueOf(description) + " (Next: 3 Broken)\nBonus: Slight increase to Resolve damage";
                }
                else if (w.achievementHeld(i)[0] == 2) {
                    description = String.valueOf(description) + " (Next: 6 Broken)\nBonus: Notable increase to Resolve damage";
                }
                else if (w.achievementHeld(i)[0] == 3) {
                    description = String.valueOf(description) + " (Next: 15 Broken)\nBonus: Moderate increase to Resolve damage";
                }
                else if (w.achievementHeld(i)[0] == 4) {
                    description = String.valueOf(description) + " (Next: 40 Broken)\nBonus: Large increase to Resolve damage";
                }
                else {
                    description = String.valueOf(description) + "\nBonus: Extreme increase to Resolve damage";
                }
                description = String.valueOf(description) + "\nThe public may not know the difference, but the Chosen themselves are keenly aware that some of their number are far more competent than others.  As you prove that you can convert even the best of them to the Demonic cause, they'll all lose hope of ever winning against you.";
            }
            if (w.achievementHeld(i)[0] > w.achievementSeen[i]) {
                w.achievementSeen[i] = w.achievementHeld(i)[0];
                w.greenAppend(t, "\n" + description);
            }
            else if (w.achievementHeld(i)[0] > 0) {
                w.append(t, "\n" + description);
            }
            else {
                w.grayAppend(t, "\n" + description);
            }
        }
        if (page < (w.achievementSeen.length - 1) / 5) {
            final JButton NextPage = new JButton("Next Page");
            NextPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.ViewAchievements(t, p, f, w, page + 1);
                }
            });
            p.add(NextPage);
        }
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.ShowInformation(t, p, f, w);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void ViewUpgrades(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final int page) {
        p.removeAll();
        if (page > 0) {
            final JButton Previous = new JButton("<");
            Previous.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.ViewUpgrades(t, p, f, w, page - 1);
                }
            });
            p.add(Previous);
        }
        for (int i = page * 5; i < w.getTechs().length && i < page * 5 + 5; ++i) {
            final int id = i;
            final JButton Upgrade = new JButton(w.getTechs()[i].name);
            Upgrade.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                    w.getTechs()[id].printSummary(w, t);
                }
            });
            p.add(Upgrade);
            if (!w.getTechs()[i].isOwned()) {
                Upgrade.setForeground(Color.GRAY);
            }
        }
        if (page < (w.getTechs().length - 1) / 5) {
            final JButton Next = new JButton(">");
            Next.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.ViewUpgrades(t, p, f, w, page + 1);
                }
            });
            p.add(Next);
        }
        final JButton Back = new JButton("Back");
        Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.ShowInformation(t, p, f, w);
            }
        });
        p.add(Back);
        p.validate();
        p.repaint();
    }
    
    public static void Cheat(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich cheat would you like to use?\n\n+100 Evil Energy: Increases your Evil Energy by 100.\n\nChange Day: Allows you to skip closer to future events or revisit past ones with the team in its current state.  Range is limited to 1-50, and because events require all three Chosen to be present, this cheat cannot be activated until the full team has been encountered.\n\nDisable/Enable Adaptations: Prevents/Allows Chosen use of Slaughter, Fantasize, Detonate, and Striptease.  Note that use of these actions is required to reach later corruption stages.\n\nUnlock All Upgrades: Purchases every upgrade aside from Imago Quickening at no Evil Energy cost.");
        final JButton AddEnergy = new JButton("+100 Evil Energy");
        AddEnergy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.addEnergy(100);
                Project.Shop(t, p, f, w);
            }
        });
        p.add(AddEnergy);
        final JButton ChangeDay = new JButton("Change Day");
        ChangeDay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final String input = JOptionPane.showInputDialog("Enter the number of the day you wish to move to.");
                try {
                    int newDay = Integer.valueOf(input);
                    if (newDay < 2) {
                        newDay = 2;
                    }
                    else if (newDay > 50 - w.eventOffset * 3) {
                        newDay = 50;
                    }
                    w.setDay(newDay);
                    Project.Shop(t, p, f, w);
                }
                catch (NumberFormatException n) {
                    w.append(t, "\n\n" + w.getSeparator() + "\n\nError: unable to recognize input as a number.");
                    Project.Cheat(t, p, f, w);
                }
            }
        });
        if (w.getCast()[2] != null) {
            p.add(ChangeDay);
        }
        final JButton ToggleAdaptations = new JButton("Disable Adaptations");
        if (w.adaptationsDisabled()) {
            ToggleAdaptations.setText("Enable Adaptations");
        }
        ToggleAdaptations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                w.toggleAdaptations();
                Project.Cheat(t, p, f, w);
            }
        });
        p.add(ToggleAdaptations);
        final JButton AllUpgrades = new JButton("Unlock All Upgrades");
        AllUpgrades.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                for (int i = 0; i < w.getTechs().length - 1; ++i) {
                    w.getTechs()[i].owned = true;
                }
                Project.Cheat(t, p, f, w);
            }
        });
        p.add(AllUpgrades);
        final JButton Cancel = new JButton("Back");
        Cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.Shop(t, p, f, w);
            }
        });
        p.add(Cancel);
        p.validate();
        p.repaint();
    }
    
    public static void Data(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final String function, final int page, final Boolean toShop) {
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
        final File saveLocation = new File(String.valueOf(path) + File.separator + "saves.sav");
        SaveData saves = null;
        if (saveLocation.exists()) {
            final ReadObject robj = new ReadObject();
            saves = robj.deserializeSaveData(String.valueOf(path) + File.separator + "saves.sav");
        }
        else {
            saves = new SaveData();
        }
        for (int j = 0; j < saves.getSaves().length; ++j) {
            saves.getSaves()[j].repairSave();
        }
        final WriteObject wobj = new WriteObject();
        final SaveData saveFile = saves;
        if (function.equals("newsave")) {
            Boolean aborted = false;
            String newSaveName = JOptionPane.showInputDialog("What would you like to name this save?");
            if (newSaveName == null) {
                aborted = true;
            }
            else if (newSaveName.length() == 0) {
                final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                final Date date = new Date();
                newSaveName = "Save of " + dateFormat.format(date);
            }
            if (aborted) {
                w.append(t, "  Aborted.");
            }
            else {
                w.append(t, "  \"" + newSaveName + "\" saved");
                saves.newSave(w, newSaveName);
                wobj.serializeSaveData(saves);
                w.save = saves;
            }
            Shop(t, p, f, w);
        }
        else if (function.equals("overwrite")) {
            if (saves.getSaves().length == 0) {
                Data(t, p, f, w, "newsave", 0, toShop);
            }
            else {
                String fullSaveName = String.valueOf(saves.getNames()[0]) + " - Day " + saves.getSaves()[0].getDay() + " versus ";
                if (saves.getSaves()[0].getCast()[1] == null) {
                    fullSaveName = String.valueOf(fullSaveName) + saves.getSaves()[0].getCast()[0].getMainName();
                }
                else if (saves.getSaves()[0].getCast()[2] == null) {
                    fullSaveName = String.valueOf(fullSaveName) + saves.getSaves()[0].getCast()[0].getMainName() + " and " + saves.getSaves()[0].getCast()[1].getMainName();
                }
                else {
                    fullSaveName = String.valueOf(fullSaveName) + saves.getSaves()[0].getCast()[0].getMainName() + ", " + saves.getSaves()[0].getCast()[1].getMainName() + ", and " + saves.getSaves()[0].getCast()[2].getMainName();
                }
                w.append(t, "\n\n" + w.getSeparator() + "\n\nReally overwrite \"" + fullSaveName + "\"?");
                p.removeAll();
                final JButton Confirm = new JButton("Confirm");
                Confirm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        saveFile.overwriteSave(w);
                        wobj.serializeSaveData(saveFile);
                        w.append(t, "  Done.");
                        w.save = saveFile;
                        Project.Shop(t, p, f, w);
                    }
                });
                p.add(Confirm);
                final JButton Cancel = new JButton("Cancel");
                Cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.append(t, "  Cancelled.");
                        Project.Shop(t, p, f, w);
                    }
                });
                p.add(Cancel);
                p.validate();
                p.repaint();
            }
        }
        else if (function.equals("export")) {
            final WorldState newWorld = new WorldState();
            newWorld.copyInitial(w);
            final Chosen newChosen = new Chosen();
            newChosen.setNumber(0);
            newChosen.generate(newWorld);
            newWorld.addChosen(newChosen);
            String newSaveName2 = JOptionPane.showInputDialog("What would you like to name the exported file?");
            Boolean blankName = false;
            if (newSaveName2 == null) {
                blankName = true;
            }
            else if (newSaveName2.length() == 0) {
                blankName = true;
            }
            if (blankName) {
                final DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                final Date date2 = new Date();
                newSaveName2 = "Team of " + dateFormat2.format(date2);
            }
            String editedName = "";
            for (int k = 0; k < newSaveName2.length(); ++k) {
                if (newSaveName2.charAt(k) == '/' || newSaveName2.charAt(k) == ':') {
                    editedName = String.valueOf(editedName) + "-";
                }
                else {
                    editedName = String.valueOf(editedName) + newSaveName2.charAt(k);
                }
            }
            if (w.getHighScore() > 0L) {
                newWorld.setParScore(w.getHighScore());
            }
            if (w.getParScore() > newWorld.getParScore()) {
                newWorld.setParScore(w.getParScore());
            }
            newWorld.copySettings(t, w);
            newWorld.copyToggles(w);
            wobj.exportFile(newWorld, editedName);
            w.append(t, "\n\n" + w.getSeparator() + "\n\nDay 1 start against this team saved to '" + editedName + ".par'.");
        }
        else if (function.equals("import")) {
            p.removeAll();
            int l = page * 4;
            int m = 0;
            WorldState[] foundWorlds = new WorldState[0];
            final ReadObject robj2 = new ReadObject();
            foundWorlds = robj2.importFiles();
            if (foundWorlds.length == 0) {
                w.append(t, "\n\n" + w.getSeparator() + "\n\nNo importable files found in directory.");
            }
            else {
                w.append(t, "\n\n" + w.getSeparator() + "\n\nFound the following importable files in directory.  Which would you like to import?");
                if (page > 0) {
                    final JButton LastPage = new JButton("Previous Page");
                    LastPage.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            Project.Data(t, p, f, w, function, page - 1, toShop);
                        }
                    });
                    p.add(LastPage);
                }
                while (l < foundWorlds.length && m < 4) {
                    w.append(t, "\n\nFile " + (l + 1) + ": " + foundWorlds[l].getSaveTitle());
                    if (foundWorlds[l].getParScore() > 0L) {
                        w.append(t, " (Par " + foundWorlds[l].getCast()[0].condensedFormat(foundWorlds[l].getParScore()) + ")");
                    }
                    final int worldSelected = l;
                    final WorldState[] worldList = foundWorlds;
                    final JButton Access = new JButton(new StringBuilder().append(l + 1).toString());
                    Access.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
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
                            try {
                                path = path.substring(0, path.length() - fileName.length() - 1);
                                path = URLDecoder.decode(path, "UTF-8");
                                path = path.replaceAll("file:/", "");
                                path = path.replaceAll(String.valueOf(File.separator) + "u0020", String.valueOf(File.separator) + " ");
                                final File saveLocation = new File(String.valueOf(path) + File.separator + "saves.sav");
                                SaveData saves = null;
                                if (saveLocation.exists()) {
                                    final ReadObject robj = new ReadObject();
                                    saves = robj.deserializeSaveData(String.valueOf(path) + File.separator + "saves.sav");
                                }
                                else {
                                    saves = new SaveData();
                                }
                                final WriteObject wobj = new WriteObject();
                                saves.endSave(worldList[worldSelected], worldList[worldSelected].getSaveTitle());
                                for (int j = 0; j < 3; ++j) {
                                    if (worldList[worldSelected].getCast()[j] != null) {
                                        worldList[worldSelected].getCast()[j].globalID = saves.assignChosenID();
                                    }
                                }
                                wobj.serializeSaveData(saves);
                                w.append(t, "\n\n" + w.getSeparator() + "\n\nImported file saved to slot " + saves.getSaves().length + ".");
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    p.add(Access);
                    ++l;
                    ++m;
                }
                if (page * 4 + 4 < foundWorlds.length) {
                    final JButton NextPage = new JButton("Next Page");
                    NextPage.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            Project.Data(t, p, f, w, function, page + 1, toShop);
                        }
                    });
                    p.add(NextPage);
                }
            }
            final JButton Back = new JButton("Back");
            Back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (toShop) {
                        Project.Shop(t, p, f, w);
                    }
                    else {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        Project.IntroOne(t, p, f, w);
                    }
                }
            });
            p.add(Back);
            p.validate();
            p.repaint();
        }
        else {
            int l = page * 4;
            int m = 0;
            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
            if (function.equals("load")) {
                w.append(t, "Load which slot?");
            }
            else if (function.equals("teamload")) {
                w.append(t, "Load which team?");
            }
            else {
                w.append(t, "Delete which slot?");
            }
            p.removeAll();
            if (page > 0) {
                final JButton LastPage2 = new JButton("Previous Page");
                LastPage2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Data(t, p, f, w, function, page - 1, toShop);
                    }
                });
                p.add(LastPage2);
            }
            while (l < saves.getSaves().length && m < 4) {
                String fullSaveName2 = String.valueOf(saves.getNames()[l]) + " - Day " + saves.getSaves()[l].getDay() + " versus ";
                if (saves.getSaves()[l].getCast()[1] == null) {
                    fullSaveName2 = String.valueOf(fullSaveName2) + saves.getSaves()[l].getCast()[0].getMainName();
                }
                else if (saves.getSaves()[l].getCast()[2] == null) {
                    fullSaveName2 = String.valueOf(fullSaveName2) + saves.getSaves()[l].getCast()[0].getMainName() + " and " + saves.getSaves()[l].getCast()[1].getMainName();
                }
                else {
                    fullSaveName2 = String.valueOf(fullSaveName2) + saves.getSaves()[l].getCast()[0].getMainName() + ", " + saves.getSaves()[l].getCast()[1].getMainName() + ", and " + saves.getSaves()[l].getCast()[2].getMainName();
                }
                if (saves.getSaves()[l].getHighScore() > 0L) {
                    fullSaveName2 = String.valueOf(fullSaveName2) + " (HS " + saves.getSaves()[l].getCast()[0].condensedFormat(saves.getSaves()[l].getHighScore());
                    if (saves.getSaves()[l].getParScore() > 0L) {
                        fullSaveName2 = String.valueOf(fullSaveName2) + " | Par " + saves.getSaves()[l].getCast()[0].condensedFormat(saves.getSaves()[l].getParScore()) + ")";
                    }
                    else {
                        fullSaveName2 = String.valueOf(fullSaveName2) + ")";
                    }
                }
                else if (saves.getSaves()[l].getParScore() > 0L) {
                    fullSaveName2 = String.valueOf(fullSaveName2) + " (Par " + saves.getSaves()[l].getCast()[0].condensedFormat(saves.getSaves()[l].getParScore()) + ")";
                }
                String displayedName = "\n\nSlot " + (l + 1);
                if (l == 0) {
                    displayedName = String.valueOf(displayedName) + " (most recent)";
                }
                else if (l == saves.getSaves().length - 1) {
                    displayedName = String.valueOf(displayedName) + " (oldest)";
                }
                displayedName = String.valueOf(displayedName) + ", " + fullSaveName2;
                if (saves.getSaves()[l].campaign) {
                    if (saves.getSaves()[l].earlyCheat) {
                        w.greenAppend(t, String.valueOf(displayedName) + " [Loop " + (saves.getSaves()[l].loops + 1) + ": " + saves.getSaves()[l].cityName);
                        if (saves.getSaves()[l].loopComplete) {
                            w.greenAppend(t, "] [Loop Complete]");
                        }
                        else {
                            w.greenAppend(t, "]");
                        }
                    }
                    else {
                        w.blueAppend(t, String.valueOf(displayedName) + " [Loop " + (saves.getSaves()[l].loops + 1) + ": " + saves.getSaves()[l].cityName);
                        if (saves.getSaves()[l].loopComplete) {
                            w.blueAppend(t, "] [Loop Complete]");
                        }
                        else {
                            w.blueAppend(t, "]");
                        }
                    }
                }
                else if (saves.getSaves()[l].hardMode) {
                    w.redAppend(t, displayedName);
                }
                else if (saves.getSaves()[l].earlyCheat) {
                    w.greenAppend(t, displayedName);
                }
                else {
                    w.append(t, displayedName);
                }
                final int fileSelected = l;
                final String thisSaveName = fullSaveName2;
                final JButton Access = new JButton(new StringBuilder().append(l + 1).toString());
                Access.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (function.equals("load")) {
                            if (toShop) {
                                p.removeAll();
                                w.append(t, "\n\n" + w.getSeparator() + "\n\nReally load \"" + thisSaveName + "\"?  Your current progress won't be saved.");
                                final JButton Confirm = new JButton("Confirm");
                                Confirm.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(final ActionEvent e) {
                                        WorldState savedWorld = new WorldState();
                                        savedWorld = saveFile.getSaves()[fileSelected];
                                        wobj.serializeSaveData(saveFile);
                                        saveFile.moveToFront(fileSelected);
                                        wobj.serializeSaveData(saveFile);
                                        savedWorld.copySettings(t, w);
                                        savedWorld.setCommentaryRead(w.getCommentaryRead());
                                        savedWorld.setCommentaryWrite(w.getCommentaryWrite());
                                        saveFile.getSaves()[0].save = saveFile;
                                        if (savedWorld.getDay() == 1 && savedWorld.evilEnergy == 0 && !savedWorld.getTechs()[0].owned && !savedWorld.getTechs()[1].owned && !savedWorld.getTechs()[2].owned && !savedWorld.getTechs()[3].owned && !savedWorld.campaign) {
                                            savedWorld.earlyCheat = w.earlyCheat;
                                            savedWorld.earlyCheat = w.earlyCheat;
                                            savedWorld.hardMode = w.hardMode;
                                            savedWorld.eventOffset = w.eventOffset;
                                            savedWorld.clampStart = w.clampStart;
                                            savedWorld.clampPercent = w.clampPercent;
                                            if (savedWorld.earlyCheat) {
                                                Project.Shop(t, p, f, saveFile.getSaves()[0]);
                                            }
                                            else {
                                                Project.IntroTwo(t, p, f, saveFile.getSaves()[0]);
                                            }
                                        }
                                        else {
                                            Project.Shop(t, p, f, saveFile.getSaves()[0]);
                                        }
                                    }
                                });
                                p.add(Confirm);
                                final JButton Cancel = new JButton("Cancel");
                                Cancel.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(final ActionEvent e) {
                                        Project.Shop(t, p, f, w);
                                    }
                                });
                                p.add(Cancel);
                                p.validate();
                                p.repaint();
                            }
                            else {
                                WorldState savedWorld = new WorldState();
                                savedWorld = saveFile.getSaves()[fileSelected];
                                saveFile.moveToFront(fileSelected);
                                wobj.serializeSaveData(saveFile);
                                savedWorld.copySettings(t, w);
                                savedWorld.setCommentaryRead(w.getCommentaryRead());
                                savedWorld.setCommentaryWrite(w.getCommentaryWrite());
                                savedWorld.save = saveFile;
                                if (savedWorld.getDay() == 1 && savedWorld.evilEnergy == 0 && !savedWorld.getTechs()[0].owned && !savedWorld.getTechs()[1].owned && !savedWorld.getTechs()[2].owned && !savedWorld.getTechs()[3].owned) {
                                    savedWorld.earlyCheat = w.earlyCheat;
                                    savedWorld.hardMode = w.hardMode;
                                    savedWorld.eventOffset = w.eventOffset;
                                    savedWorld.clampStart = w.clampStart;
                                    savedWorld.clampPercent = w.clampPercent;
                                    if (savedWorld.earlyCheat) {
                                        Project.Shop(t, p, f, savedWorld);
                                    }
                                    else {
                                        Project.IntroTwo(t, p, f, savedWorld);
                                    }
                                }
                                else {
                                    Project.Shop(t, p, f, savedWorld);
                                }
                            }
                        }
                        else if (function.equals("teamload")) {
                            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                            int found = 1;
                            final Chosen[] copies = new Chosen[3];
                            final WorldState x = new WorldState();
                            x.copySettings(t, saveFile.getSaves()[fileSelected]);
                            x.copyToggles(saveFile.getSaves()[fileSelected]);
                            x.copyInitial(saveFile.getSaves()[fileSelected]);
                            x.save = w.save;
                            x.campaign = saveFile.getSaves()[fileSelected].campaign;
                            for (int j = 0; j < 3; ++j) {
                                if (saveFile.getSaves()[fileSelected].getCast()[j] != null) {
                                    found = j + 1;
                                    if (x.loopChosen != null && x.loopChosen[j] != null) {
                                        (copies[j] = new Chosen()).campaignImport(x, x.loopChosen[j]);
                                        copies[j].globalID = w.save.assignChosenID();
                                        copies[j].originalCity = null;
                                        copies[j].lastLoop = 0;
                                        copies[j].originalGender = copies[j].gender;
                                        if (copies[j].morality > 66) {
                                            copies[j].bonusHATE = true;
                                        }
                                        if (copies[j].innocence > 66) {
                                            copies[j].bonusPLEA = true;
                                        }
                                        if (copies[j].confidence > 66) {
                                            copies[j].bonusINJU = true;
                                        }
                                        if (copies[j].dignity > 66) {
                                            copies[j].bonusEXPO = true;
                                        }
                                        copies[j].introduced = false;
                                        copies[j].resetDistortions();
                                        copies[j].kills = new Chosen[0];
                                        copies[j].killRelationships = new int[0];
                                        copies[j].formerPartners = new Chosen[0];
                                        copies[j].formerRelationships = new int[0];
                                    }
                                    else {
                                        (copies[j] = new Chosen()).setNumber(j);
                                        copies[j].generate(x);
                                        x.addChosen(copies[j]);
                                        copies[j].originalCity = null;
                                        copies[j].lastLoop = 0;
                                    }
                                }
                            }
                            final Chosen[] newRoster = new Chosen[w.save.customRoster.length + found];
                            for (int i = 0; i < newRoster.length; ++i) {
                                if (i < w.save.customRoster.length) {
                                    newRoster[i] = w.save.customRoster[i];
                                }
                                else {
                                    newRoster[i] = copies[i - w.save.customRoster.length];
                                }
                            }
                            w.save.customRoster = newRoster;
                            w.append(t, "Added " + saveFile.getSaves()[fileSelected].getCast()[0].mainName);
                            if (found == 3) {
                                w.append(t, ", " + saveFile.getSaves()[fileSelected].getCast()[1].mainName + ", and " + saveFile.getSaves()[fileSelected].getCast()[2].mainName);
                            }
                            else if (found == 2) {
                                w.append(t, " and " + saveFile.getSaves()[fileSelected].getCast()[1].mainName);
                            }
                            w.append(t, " to the custom roster.");
                        }
                        else {
                            p.removeAll();
                            w.append(t, "\n\n" + w.getSeparator() + "\n\nReally delete \"" + thisSaveName + "\"?");
                            final JButton Confirm = new JButton("Confirm");
                            Confirm.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    saveFile.deleteSave(fileSelected);
                                    wobj.serializeSaveData(saveFile);
                                    w.append(t, "  Done.");
                                    w.save = saveFile;
                                    Project.Shop(t, p, f, w);
                                }
                            });
                            p.add(Confirm);
                            final JButton Cancel = new JButton("Cancel");
                            Cancel.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.append(t, "  Cancelled.");
                                    Project.Shop(t, p, f, w);
                                }
                            });
                            p.add(Cancel);
                            p.validate();
                            p.repaint();
                        }
                    }
                });
                p.add(Access);
                ++l;
                ++m;
            }
            if (page * 4 + 4 < saves.getSaves().length) {
                final JButton NextPage2 = new JButton("Next Page");
                NextPage2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Data(t, p, f, w, function, page + 1, toShop);
                    }
                });
                p.add(NextPage2);
            }
            final JButton Cancel = new JButton("Cancel");
            if (function.equals("teamload")) {
                Cancel.setText("Done");
            }
            Cancel.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (function.equals("teamload")) {
                        final Boolean[] enabled = new Boolean[w.save.customRoster.length];
                        for (int i = 0; i < enabled.length; ++i) {
                            enabled[i] = true;
                        }
                        Project.CampaignMenu(t, p, f, w, enabled);
                    }
                    else if (toShop) {
                        Project.Shop(t, p, f, w);
                    }
                    else {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        Project.IntroOne(t, p, f, w);
                    }
                }
            });
            p.add(Cancel);
            p.validate();
            p.repaint();
        }
    }
    
    public static void Customize(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        if (!w.getBodyStatus()[0]) {
            w.append(t, "You aren't currently sending a Commander body to the battlefield.  Creating one costs 1 Evil Energy.");
            if (w.getEvilEnergy() >= 1) {
                final JButton Create = new JButton("Create");
                Create.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.newCommander();
                        w.addEnergy(-1);
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(Create);
            }
            if (!w.hardMode && w.save != null && w.getHarem() != null && w.getHarem().length > 0) {
                final JButton UseForsaken = new JButton("Use Forsaken");
                UseForsaken.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.ForsakenMenu(t, p, f, w, w.save, 0);
                    }
                });
                p.add(UseForsaken);
            }
            final JButton Back = new JButton("Back");
            Back.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    Project.Shop(t, p, f, w);
                }
            });
            p.add(Back);
        }
        else {
            w.printCommanderSummary(t, null);
            int suppressorsKnown = 0;
            if (w.getTechs()[10].isOwned()) {
                ++suppressorsKnown;
            }
            if (w.getTechs()[11].isOwned()) {
                ++suppressorsKnown;
            }
            if (w.getTechs()[12].isOwned()) {
                ++suppressorsKnown;
            }
            if (w.getTechs()[13].isOwned()) {
                ++suppressorsKnown;
            }
            int suppressorsUsed = 0;
            if (w.getBodyStatus()[3]) {
                ++suppressorsUsed;
            }
            if (w.getBodyStatus()[4]) {
                ++suppressorsUsed;
            }
            if (w.getBodyStatus()[5]) {
                ++suppressorsUsed;
            }
            if (w.getBodyStatus()[6]) {
                ++suppressorsUsed;
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
            if ((!punisherUsed || (w.getTechs()[47].isOwned() && (w.getEvilEnergy() >= 66 || defilerUsed))) && ((suppressorsKnown > 0 && suppressorsUsed == 0 && (!defilerUsed || (w.getTechs()[33].isOwned() && w.getEvilEnergy() >= 10) || punisherUsed)) || (suppressorsKnown > 1 && suppressorsUsed == 1 && w.getEvilEnergy() >= 5 && w.getTechs()[21].isOwned() && !defilerUsed && !punisherUsed))) {
                final JButton Suppressor = new JButton("Suppressor Upgrades");
                Suppressor.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        p.removeAll();
                        if (w.getTechs()[10].isOwned() && !w.getBodyStatus()[3]) {
                            final JButton Hunger = new JButton("Hunger [HATE]");
                            Hunger.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    if (punisherUsed) {
                                        w.applyCompletion();
                                    }
                                    else if (defilerUsed) {
                                        w.applySynthesis();
                                    }
                                    else if (suppressorsUsedFinal == 1) {
                                        w.applyVersatility();
                                    }
                                    w.applyHunger();
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Hunger);
                        }
                        if (w.getTechs()[11].isOwned() && !w.getBodyStatus()[4]) {
                            final JButton Lust = new JButton("Lust [PLEA]");
                            Lust.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    if (punisherUsed) {
                                        w.applyCompletion();
                                    }
                                    else if (defilerUsed) {
                                        w.applySynthesis();
                                    }
                                    else if (suppressorsUsedFinal == 1) {
                                        w.applyVersatility();
                                    }
                                    w.applyLust();
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Lust);
                        }
                        if (w.getTechs()[12].isOwned() && !w.getBodyStatus()[5]) {
                            final JButton Anger = new JButton("Anger [INJU]");
                            Anger.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    if (punisherUsed) {
                                        w.applyCompletion();
                                    }
                                    else if (defilerUsed) {
                                        w.applySynthesis();
                                    }
                                    else if (suppressorsUsedFinal == 1) {
                                        w.applyVersatility();
                                    }
                                    w.applyAnger();
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Anger);
                        }
                        if (w.getTechs()[13].isOwned() && !w.getBodyStatus()[6]) {
                            final JButton Mania = new JButton("Mania [EXPO]");
                            Mania.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    if (punisherUsed) {
                                        w.applyCompletion();
                                    }
                                    else if (defilerUsed) {
                                        w.applySynthesis();
                                    }
                                    else if (suppressorsUsedFinal == 1) {
                                        w.applyVersatility();
                                    }
                                    w.applyMania();
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Mania);
                        }
                        final JButton Cancel = new JButton("Cancel");
                        Cancel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.Customize(t, p, f, w);
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
            if ((!punisherUsed || (w.getTechs()[47].isOwned() && w.getEvilEnergy() >= 66) || (w.getTechs()[47].isOwned() && suppressorsUsed == 1)) && defilerKnown && !defilerUsed && (suppressorsUsed == 0 || (suppressorsUsed == 1 && w.getTechs()[33].isOwned() && w.getEvilEnergy() >= 16)) && w.getEvilEnergy() >= 6) {
                final JButton Defiler = new JButton("Defiler Upgrades");
                Defiler.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        p.removeAll();
                        if (w.getTechs()[22].isOwned()) {
                            final JButton Ambition = new JButton("Ambition [HATE/PLEA]");
                            Ambition.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.applyAmbition();
                                    if (punisherUsed) {
                                        w.applyCompletion();
                                    }
                                    else if (suppressorsUsedFinal == 1) {
                                        w.applySynthesis();
                                    }
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Ambition);
                        }
                        if (w.getTechs()[23].isOwned()) {
                            final JButton Dominance = new JButton("Dominance [PLEA/INJU]");
                            if (w.tickle()) {
                                Dominance.setText("Dominance [PLEA/ANTI]");
                            }
                            Dominance.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.applyDominance();
                                    if (punisherUsed) {
                                        w.applyCompletion();
                                    }
                                    else if (suppressorsUsedFinal == 1) {
                                        w.applySynthesis();
                                    }
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Dominance);
                        }
                        if (w.getTechs()[24].isOwned()) {
                            final JButton Spite = new JButton("Spite [INJU/EXPO]");
                            if (w.tickle()) {
                                Spite.setText("Spite [ANTI/EXPO]");
                            }
                            Spite.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.applySpite();
                                    if (punisherUsed) {
                                        w.applyCompletion();
                                    }
                                    else if (suppressorsUsedFinal == 1) {
                                        w.applySynthesis();
                                    }
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Spite);
                        }
                        if (w.getTechs()[25].isOwned()) {
                            final JButton Vanity = new JButton("Vanity [EXPO/HATE]");
                            Vanity.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.applyVanity();
                                    if (punisherUsed) {
                                        w.applyCompletion();
                                    }
                                    else if (suppressorsUsedFinal == 1) {
                                        w.applySynthesis();
                                    }
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Vanity);
                        }
                        final JButton Cancel = new JButton("Cancel");
                        Cancel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.Customize(t, p, f, w);
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
            if (!punisherUsed && punisherKnown && ((!defilerUsed && suppressorsUsed == 0) || (w.getTechs()[47].isOwned() && suppressorsUsed < 2 && (w.getEvilEnergy() >= 66 || (defilerUsed && w.getEvilEnergy() >= 60) || (defilerUsed && suppressorsUsed == 1 && w.getEvilEnergy() >= 50))))) {
                final JButton Punisher = new JButton("Punisher Upgrades");
                Punisher.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        p.removeAll();
                        if (w.getTechs()[34].isOwned()) {
                            final JButton Impregnation = new JButton("Impregnation [HATE]");
                            Impregnation.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.applyImpregnation();
                                    if (defilerUsed || suppressorsUsedFinal == 1) {
                                        w.applyCompletion();
                                    }
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Impregnation);
                        }
                        if (w.getTechs()[35].isOwned()) {
                            final JButton Hypnosis = new JButton("Hypnosis [PLEA]");
                            Hypnosis.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.applyHypnosis();
                                    if (defilerUsed || suppressorsUsedFinal == 1) {
                                        w.applyCompletion();
                                    }
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Hypnosis);
                        }
                        if (w.getTechs()[36].isOwned()) {
                            final JButton Drain = new JButton("Drain [INJU]");
                            if (w.tickle()) {
                                Drain.setText("Drain [ANTI]");
                            }
                            Drain.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.applyDrain();
                                    if (defilerUsed || suppressorsUsedFinal == 1) {
                                        w.applyCompletion();
                                    }
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Drain);
                        }
                        if (w.getTechs()[37].isOwned()) {
                            final JButton Parasitism = new JButton("Parasitism [EXPO]");
                            Parasitism.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(final ActionEvent e) {
                                    w.applyParasitism();
                                    if (defilerUsed || suppressorsUsedFinal == 1) {
                                        w.applyCompletion();
                                    }
                                    Project.Customize(t, p, f, w);
                                }
                            });
                            p.add(Parasitism);
                        }
                        final JButton Cancel = new JButton("Cancel");
                        Cancel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.Customize(t, p, f, w);
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
            if (w.getTechs()[8].isOwned() && !w.getBodyStatus()[1] && w.getEvilEnergy() >= 1) {
                final JButton Enhance = new JButton("Enhance Duration (1)");
                Enhance.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.enhanceOne();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(Enhance);
            }
            if (w.getTechs()[14].isOwned() && w.getBodyStatus()[1] && !w.getBodyStatus()[7] && w.getEvilEnergy() >= 1) {
                final JButton Enhance = new JButton("Enhance Duration (2)");
                Enhance.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.enhanceTwo();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(Enhance);
            }
            if (w.getTechs()[20].isOwned() && w.getBodyStatus()[7] && !w.getBodyStatus()[9] && w.getEvilEnergy() >= 2) {
                final JButton Enhance = new JButton("Enhance Duration (3)");
                Enhance.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.enhanceThree();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(Enhance);
            }
            if (w.getTechs()[26].isOwned() && w.getBodyStatus()[9] && !w.getBodyStatus()[15] && w.getEvilEnergy() >= 2) {
                final JButton Enhance = new JButton("Enhance Duration (4)");
                Enhance.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.enhanceFour();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(Enhance);
            }
            if (w.getTechs()[46].isOwned() && w.getBodyStatus()[15] && !w.getBodyStatus()[25] && w.getEvilEnergy() >= 30) {
                final JButton Enhance = new JButton("Enhance Duration (5)");
                Enhance.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.enhanceFive();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(Enhance);
            }
            if (w.getTechs()[15].isOwned() && !w.getBodyStatus()[8] && w.getEvilEnergy() >= 2) {
                final JButton AddCapture = new JButton("Extra Capture (1)");
                AddCapture.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.addCaptureOne();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(AddCapture);
            }
            if (w.getTechs()[27].isOwned() && w.getBodyStatus()[8] && !w.getBodyStatus()[16] && w.getEvilEnergy() >= 5) {
                final JButton AddCapture = new JButton("Extra Capture (2)");
                AddCapture.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.addCaptureTwo();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(AddCapture);
            }
            if (w.getTechs()[32].isOwned() && w.getBodyStatus()[16] && !w.getBodyStatus()[17] && w.getEvilEnergy() >= 10) {
                final JButton AddCapture = new JButton("Extra Capture (3)");
                AddCapture.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.addCaptureThree();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(AddCapture);
            }
            if (w.getTechs()[38].isOwned() && w.getBodyStatus()[17] && !w.getBodyStatus()[23] && w.getEvilEnergy() >= 20) {
                final JButton AddCapture = new JButton("Extra Capture (4)");
                AddCapture.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.addCaptureFour();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(AddCapture);
            }
            if (w.getTechs()[39].isOwned() && !w.getBodyStatus()[24] && w.getEvilEnergy() >= 10) {
                final JButton Flight = new JButton("Flight");
                Flight.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.applyRelentlessness();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(Flight);
            }
            if (w.getTechs()[9].isOwned()) {
                final JButton Toggle = new JButton("Toggle Ambush");
                Toggle.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.toggleAmbush();
                        Project.Customize(t, p, f, w);
                    }
                });
                p.add(Toggle);
            }
            final JButton Refund = new JButton("Refund");
            Refund.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.addEnergy(w.getCommanderValue());
                    w.clearCommander();
                    Project.Customize(t, p, f, w);
                }
            });
            p.add(Refund);
            if (!punisherUsed || (!defilerUsed && suppressorsUsed == 0) || (defilerUsed && suppressorsUsed == 1)) {
                final JButton Done = new JButton("Done");
                Done.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.Shop(t, p, f, w);
                    }
                });
                p.add(Done);
            }
        }
        p.validate();
        p.repaint();
    }
    
    public static void advanceDowntimeAction(final JPanel p, final WorldState w, final int action) {
        Boolean actionMatches = true;
        if (w.getActions().length > w.getCurrentAction() && w.getActions()[w.getCurrentAction()] != action) {
            actionMatches = false;
            w.truncateCommentary(w.getCurrentAction());
        }
        if (w.writePossible()) {
            if (w.getCurrentComment().length() > 0) {
                w.writeCommentary(w.getCurrentComment());
            }
            else if (w.getCommentary().length <= w.getCurrentAction() || !actionMatches) {
                String generated = "";
                if (action < w.getTechs().length) {
                    generated = "Buy " + w.getTechs()[action].getName() + ".";
                }
                else if (w.usedForsaken != null) {
                    generated = String.valueOf(generated) + "(This playthrough used one of the Forsaken here, so it may not be possible to reliably follow it.)";
                }
                else {
                    int index = action - w.getTechs().length;
                    if (w.getBodyStatus()[0]) {
                        generated = "Buy a Commander with a ";
                        if (w.getBodyStatus()[1]) {
                            if (w.getBodyStatus()[7]) {
                                if (w.getBodyStatus()[9]) {
                                    if (w.getBodyStatus()[15]) {
                                        if (w.getBodyStatus()[25]) {
                                            generated = String.valueOf(generated) + "eight";
                                        }
                                        else {
                                            generated = String.valueOf(generated) + "six";
                                        }
                                    }
                                    else {
                                        generated = String.valueOf(generated) + "five";
                                    }
                                }
                                else {
                                    generated = String.valueOf(generated) + "four";
                                }
                            }
                            else {
                                generated = String.valueOf(generated) + "three";
                            }
                        }
                        else {
                            generated = String.valueOf(generated) + "two";
                        }
                        generated = String.valueOf(generated) + "-turn duration";
                        if (w.getBodyStatus()[17]) {
                            generated = String.valueOf(generated) + " and three extra captures.  ";
                        }
                        else if (w.getBodyStatus()[16]) {
                            generated = String.valueOf(generated) + " and two extra captures.  ";
                        }
                        else if (w.getBodyStatus()[8]) {
                            generated = String.valueOf(generated) + " and an extra capture.  ";
                        }
                        else {
                            generated = String.valueOf(generated) + ".  ";
                        }
                        if (w.getBodyStatus()[26]) {
                            String suppressor = "";
                            String defiler = "";
                            String punisher = "";
                            if (w.getBodyStatus()[19]) {
                                punisher = "Impregnation";
                            }
                            else if (w.getBodyStatus()[20]) {
                                punisher = "Hypnosis";
                            }
                            else if (w.getBodyStatus()[21]) {
                                punisher = "Drain";
                            }
                            else if (w.getBodyStatus()[22]) {
                                punisher = "Parasitism";
                            }
                            if (w.getBodyStatus()[11]) {
                                defiler = "Ambition";
                            }
                            else if (w.getBodyStatus()[12]) {
                                defiler = "Dominance";
                            }
                            else if (w.getBodyStatus()[13]) {
                                defiler = "Spite";
                            }
                            else if (w.getBodyStatus()[14]) {
                                defiler = "Vanity";
                            }
                            if (w.getBodyStatus()[3]) {
                                suppressor = "Hunger";
                            }
                            else if (w.getBodyStatus()[4]) {
                                suppressor = "Lust";
                            }
                            else if (w.getBodyStatus()[5]) {
                                suppressor = "Anger";
                            }
                            else if (w.getBodyStatus()[6]) {
                                suppressor = "Mania";
                            }
                            generated = String.valueOf(generated) + "Equip it with the Suppressor " + suppressor + ", the Defiler " + defiler + ", and the Punisher " + punisher + ".  ";
                        }
                        else if (w.getBodyStatus()[19]) {
                            generated = String.valueOf(generated) + "Equip it with the Punisher Impregnation.  ";
                        }
                        else if (w.getBodyStatus()[20]) {
                            generated = String.valueOf(generated) + "Equip it with the Punisher Hypnosis.  ";
                        }
                        else if (w.getBodyStatus()[21]) {
                            generated = String.valueOf(generated) + "Equip it with the Punisher Drain.  ";
                        }
                        else if (w.getBodyStatus()[22]) {
                            generated = String.valueOf(generated) + "Equip it with the Punisher Parasitism.  ";
                        }
                        else if (w.getBodyStatus()[18]) {
                            generated = String.valueOf(generated) + "Equip it with the Defiler ";
                            if (w.getBodyStatus()[11]) {
                                generated = String.valueOf(generated) + "Ambition";
                            }
                            else if (w.getBodyStatus()[12]) {
                                generated = String.valueOf(generated) + "Dominance";
                            }
                            else if (w.getBodyStatus()[13]) {
                                generated = String.valueOf(generated) + "Spite";
                            }
                            else if (w.getBodyStatus()[14]) {
                                generated = String.valueOf(generated) + "Vanity";
                            }
                            generated = String.valueOf(generated) + " and the Suppressor ";
                            if (w.getBodyStatus()[3]) {
                                generated = String.valueOf(generated) + "Hunger";
                            }
                            else if (w.getBodyStatus()[4]) {
                                generated = String.valueOf(generated) + "Lust";
                            }
                            else if (w.getBodyStatus()[5]) {
                                generated = String.valueOf(generated) + "Anger";
                            }
                            else if (w.getBodyStatus()[6]) {
                                generated = String.valueOf(generated) + "Mania";
                            }
                            generated = String.valueOf(generated) + ".  ";
                        }
                        else if (w.getBodyStatus()[11]) {
                            generated = String.valueOf(generated) + "Equip it with the Defiler Ambition.  ";
                        }
                        else if (w.getBodyStatus()[12]) {
                            generated = String.valueOf(generated) + "Equip it with the Defiler Dominance.  ";
                        }
                        else if (w.getBodyStatus()[13]) {
                            generated = String.valueOf(generated) + "Equip it with the Defiler Spite.  ";
                        }
                        else if (w.getBodyStatus()[14]) {
                            generated = String.valueOf(generated) + "Equip it with the Defiler Vanity.  ";
                        }
                        else if (w.getBodyStatus()[10]) {
                            generated = String.valueOf(generated) + "Equip it with the Suppressors ";
                            Boolean first = false;
                            if (w.getBodyStatus()[3]) {
                                generated = String.valueOf(generated) + "Hunger";
                                first = true;
                            }
                            if (w.getBodyStatus()[4]) {
                                if (first) {
                                    generated = String.valueOf(generated) + " and ";
                                }
                                generated = String.valueOf(generated) + "Lust";
                                first = true;
                            }
                            if (w.getBodyStatus()[5]) {
                                if (first) {
                                    generated = String.valueOf(generated) + " and ";
                                }
                                generated = String.valueOf(generated) + "Anger";
                                first = true;
                            }
                            if (w.getBodyStatus()[6]) {
                                generated = String.valueOf(generated) + " and Mania";
                            }
                            generated = String.valueOf(generated) + ".  ";
                        }
                        else if (w.getBodyStatus()[3]) {
                            generated = String.valueOf(generated) + "Equip it with the Suppressor Hunger.  ";
                        }
                        else if (w.getBodyStatus()[4]) {
                            generated = String.valueOf(generated) + "Equip it with the Suppressor Lust.  ";
                        }
                        else if (w.getBodyStatus()[5]) {
                            generated = String.valueOf(generated) + "Equip it with the Suppressor Anger.  ";
                        }
                        else if (w.getBodyStatus()[6]) {
                            generated = String.valueOf(generated) + "Equip it with the Suppressor Mania.  ";
                        }
                        if (w.getBodyStatus()[2]) {
                            generated = String.valueOf(generated) + "Turn off its ambush and send ";
                        }
                        else if (w.getTechs()[9].isOwned()) {
                            generated = String.valueOf(generated) + "Leave its ambush on and send ";
                        }
                        else {
                            generated = String.valueOf(generated) + "Send ";
                        }
                        if (w.getTechs()[31].isOwned() && !w.upgradedCommander()) {
                            final Chosen target = w.getCast()[(index - 3) / 4];
                            index = (index - 3) % 4;
                            generated = String.valueOf(generated) + "it after " + target.getMainName() + ".  Have the Thralls start by using ";
                            if (index == 0) {
                                generated = String.valueOf(generated) + "Grind.";
                            }
                            else if (index == 1) {
                                generated = String.valueOf(generated) + "Caress.";
                            }
                            else if (index == 2) {
                                if (w.tickle()) {
                                    generated = String.valueOf(generated) + "Tickle.";
                                }
                                else {
                                    generated = String.valueOf(generated) + "Pummel.";
                                }
                            }
                            else if (index == 3) {
                                generated = String.valueOf(generated) + "Humiliate.";
                            }
                        }
                        else {
                            generated = String.valueOf(generated) + "it after " + w.getCast()[index].getMainName() + ".";
                        }
                    }
                    else {
                        generated = "Target " + w.getCast()[index].getMainName();
                        if (w.getTechs()[3].isOwned()) {
                            generated = String.valueOf(generated) + " without bringing along a Commander.";
                        }
                        else {
                            generated = String.valueOf(generated) + ".";
                        }
                    }
                }
                w.writeCommentary(generated);
            }
        }
        w.nextAction(action);
    }
    
    public static void pickStartingTarget(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        p.removeAll();
        w.append(t, "\n\n" + w.getSeparator() + "\n\nWhich of the Chosen will you target?");
        for (int i = 0; i < w.getCast().length; ++i) {
            if (w.getCast()[i] != null) {
                final int thisChosen = i;
                final JButton thisOne = new JButton(w.getCast()[i].getMainName());
                thisOne.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.ConfirmBattle(t, p, f, w, w.getCast()[thisChosen]);
                    }
                });
                p.add(thisOne);
            }
        }
        final JButton Cancel = new JButton("Cancel");
        Cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.Shop(t, p, f, w);
            }
        });
        p.add(Cancel);
        p.validate();
        p.repaint();
    }
    
    public static void ConfirmBattle(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c) {
        p.removeAll();
        Boolean immediateAction = false;
        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        if (w.getTechs()[0].isOwned()) {
            w.append(t, String.valueOf(c.mainName) + "'s ");
            c.printVulnerabilities(t, p, f, w);
            w.append(t, "\n\n");
        }
        if (w.getDay() == 50 - w.eventOffset * 3 || w.getTechs()[48].isOwned()) {
            w.append(t, "This will be the final battle.  When extermination is completed, instead of waiting for surrounded and captured allies to escape, the Chosen may sacrifice each other's lives in order to defeat you.  Victory requires neutralizing at least two of the three Chosen.\n\n");
        }
        if (w.getBodyStatus()[0]) {
            if (w.upgradedCommander() || !w.getTechs()[31].isOwned() || w.getBodyStatus()[2]) {
                w.printCommanderSummary(t, c);
            }
            else {
                immediateAction = true;
            }
        }
        else if (w.usedForsaken != null) {
            int actualCost = w.usedForsaken.motivationCost();
            if (w.usedForsaken.isFormerFriend(w.getCast()[0]) || w.usedForsaken.isFormerFriend(w.getCast()[1]) || w.usedForsaken.isFormerFriend(w.getCast()[2])) {
                actualCost *= 2;
            }
            w.append(t, "Commanding Forsaken: " + w.usedForsaken.mainName + "\nStamina: " + w.usedForsaken.stamina / 10 + "." + w.usedForsaken.stamina % 10 + "%\nMotivation: " + w.usedForsaken.motivation / 10 + "." + w.usedForsaken.motivation % 10 + "%\nCost: 20% Stamina, " + actualCost / 10 + "." + actualCost % 10 + "% Motivation, " + w.usedForsaken.EECost() + " EE\n" + w.usedForsaken.describeCombatStyle(w, false) + "\nReputation Strength: " + (200 - w.usedForsaken.disgrace * 2) + "%\nTarget Compatibilities:");
            for (int j = 0; j < 3; ++j) {
                if (w.getCast()[j] != null) {
                    w.append(t, "\n" + w.getCast()[j].getMainName() + " - ");
                    final int compatibility = w.usedForsaken.compatibility(w.getCast()[j]);
                    if (w.usedForsaken.knowsPersonally(w.getCast()[j])) {
                        w.append(t, "Personal (8 rounds, +25% damage)");
                    }
                    else if (compatibility >= 8) {
                        w.append(t, "Excellent (8 rounds)");
                    }
                    else if (compatibility == 7) {
                        w.append(t, "Good (7 rounds)");
                    }
                    else if (compatibility == 6) {
                        w.append(t, "Average (6 rounds)");
                    }
                    else if (compatibility == 5) {
                        w.append(t, "Poor (5 rounds)");
                    }
                    else {
                        w.append(t, "Terrible (4 rounds)");
                    }
                }
            }
            w.append(t, "\n\nYou will invade a neighborhood along " + c.getMainName() + "'s patrol path in order to lure " + c.himHer() + " in an attack " + c.himHer() + ".  " + w.usedForsaken.mainName + " will lie in wait, ready to engage " + c.himHer() + " in battle upon your order.");
        }
        else {
            w.append(t, "You will invade a neighborhood along " + c.getMainName() + "'s patrol path in order to lure " + c.himHer() + " in and attack " + c.himHer() + ".");
        }
        if (immediateAction) {
            w.append(t, "Which action do you want the Thralls to perform once they grab " + c.himHer() + "?");
            for (int i = 0; i < 4; ++i) {
                final int type = i;
                String torment = "ERROR";
                if (i == 0) {
                    torment = "Grind";
                }
                else if (i == 1) {
                    torment = "Caress";
                }
                else if (i == 2) {
                    if (w.tickle()) {
                        torment = "Tickle";
                    }
                    else {
                        torment = "Pummel";
                    }
                }
                else {
                    torment = "Humiliate";
                }
                final String finalTorment = torment;
                final JButton Action = new JButton(torment);
                Action.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        p.removeAll();
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        w.printCommanderSummary(t, c);
                        w.append(t, "  The Thralls will begin by using " + finalTorment + " on " + c.himHer() + ".  Is this okay?");
                        final JButton Confirm = new JButton("Confirm");
                        Confirm.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                if (w.usedForsaken != null || w.recordedCommanders.length < w.day - 1) {
                                    w.commentaryRead = false;
                                    w.commentaryWrite = false;
                                }
                                if (w.getDay() > 1 && !w.isCheater() && (w.commentaryWrite || w.commentaryRead)) {
                                    w.archiveCommander(w.getDay());
                                }
                                Project.advanceDowntimeAction(p, w, w.getTechs().length + w.getCast().length + c.getNumber() * 4 + type);
                                if (type == 0) {
                                    c.beginGrind();
                                }
                                else if (type == 1) {
                                    c.beginCaress();
                                }
                                else if (type == 2) {
                                    c.beginPummel();
                                }
                                else if (type == 3) {
                                    c.beginHumiliate();
                                }
                                if (w.getDay() == 50 - w.eventOffset * 3 || w.getTechs()[48].isOwned()) {
                                    Project.BeginFinalBattle(t, p, f, w, c);
                                }
                                else {
                                    Project.BeginBattle(t, p, f, w, c);
                                }
                            }
                        });
                        p.add(Confirm);
                        final JButton Cancel = new JButton("Cancel");
                        Cancel.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                Project.Shop(t, p, f, w);
                            }
                        });
                        p.add(Cancel);
                        p.validate();
                        p.repaint();
                    }
                });
                p.add(Action);
            }
        }
        else {
            w.append(t, "  Is this okay?");
            final JButton Confirm = new JButton("Confirm");
            Confirm.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    if (w.usedForsaken != null || w.recordedCommanders.length < w.day - 1) {
                        w.commentaryRead = false;
                        w.commentaryWrite = false;
                    }
                    if (w.getDay() > 1 && !w.isCheater() && (w.commentaryRead || w.commentaryWrite)) {
                        w.archiveCommander(w.getDay());
                    }
                    Project.advanceDowntimeAction(p, w, w.getTechs().length + c.getNumber());
                    if (w.getDay() == 50 - w.eventOffset * 3 || w.getTechs()[48].isOwned()) {
                        Project.BeginFinalBattle(t, p, f, w, c);
                    }
                    else {
                        Project.BeginBattle(t, p, f, w, c);
                    }
                }
            });
            p.add(Confirm);
        }
        final JButton Cancel = new JButton("Cancel");
        Cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.Shop(t, p, f, w);
            }
        });
        p.add(Cancel);
        p.validate();
        p.repaint();
    }
    
    public static void BeginBattle(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c) {
        w.incrementTotalRounds();
        final Chosen[] newCombatants = { c, null, null };
        w.newCombat(w, newCombatants);
        if (w.getBodyStatus()[0] && !w.getBodyStatus()[2]) {
            if (w.getTechs()[31].isOwned() && !w.upgradedCommander()) {
                w.setBattleRound(0);
                c.BeSurrounded(t, p, f, w);
            }
            else {
                w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                w.append(t, "You lure " + c.getMainName() + " into battle with an attack on a neighborhood along " + c.hisHer() + " patrol route.  ");
                if (w.upgradedCommander()) {
                    w.append(t, "Then, you spring your ambush.  ");
                    c.startCaptured(t, w);
                }
                else {
                    w.append(t, "Then, with your Commander body on the battlefield, you set up an ambush.  ");
                    if (w.getCapturesPossible() > 0) {
                        w.append(t, "In the chaos, your body takes a serious injury, but with " + c.getMainName() + " surrounded, the battle has already progressed in the Demons' favor.");
                    }
                    else {
                        w.append(t, "In the chaos, your body is destroyed, but with " + c.getMainName() + " surrounded, the battle has already progressed in the Demons' favor.");
                    }
                    c.setSurrounded(w);
                    c.printSurroundedLine(t, w, c.getThisAttack());
                }
            }
        }
        else {
            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
            w.append(t, "In the middle of a busy street, a horde of Demons suddenly erupts from underground");
            if (w.getBodyStatus()[0]) {
                w.append(t, ", led by the Commander body you're remotely controlling.");
            }
            else {
                w.append(t, "!");
            }
            w.append(t, "  They begin attacking civilians and dragging them away for conversion to the Demonic cause, but before they can get too far, a thundering burst of light shines down on the scene!\n\n");
            c.say(t, "\"" + c.announcement() + "\"\n\n");
            c.transform(t, w);
        }
        w.append(t, "\n");
        PickTarget(t, p, f, w);
    }
    
    public static void BeginFinalBattle(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Chosen c) {
        w.incrementTotalRounds();
        final Chosen[] newCombatants = { c, null, null };
        w.newCombat(w, newCombatants);
        if (w.getDay() == 50 - w.eventOffset * 3) {
            w.append(t, "\n\n" + w.getSeparator() + "\n\nThe city's streets are devoid of life.  In preparation for the coming battle, the residents have been evacuated to temporary housing in the surrounding countryside.  The only ones who remain are the stubborn, the thrillseekers, some entrepreneurial journalists, and of course your minions.  They all know what's coming, and they're waiting for you to make your move.\n\nFinally, the silence is broken by the sound of shattering pavement.  An enormous, dark shape rises out of the ground, toppling buildings and sending tons of rubble spilling in all directions as it grows.  It's a gigantic pillar whose surface shimmers like an oil slick, and it continues upward until it dwarfs the skyscrapers below, penetrating the heavens themselves.  All throughout the city, space begins to warp and shift as you corrupt the fabric of reality and bend it to your will.\n\n" + c.getMainName() + " is the closest of the Chosen to the epicenter.  Although " + c.hisHer() + " instincts are telling " + c.himHer() + " to immediately begin drawing on as much energy as " + c.heShe() + " can, " + c.heShe() + " recalls from the strategy briefing that it will still take some time to evacuate the last few VIPs who had to stay until the last moment.  The neighboring cities will also need a chance to prepare for the destructive electromagnetic pulses that are likely to be released as the Chosen fight at full power.\n\n");
        }
        else {
            w.append(t, "\n\n" + w.getSeparator() + "\n\nThe city's streets are bustling as if this were a day like any other.  Its citizens have no idea how close your plans are to completion.\n\nWithout warning, the pavement of one of the main streets shatters and opens up.  An enormous, dark shape rises out of the ground, toppling buildings and sending tons of rubble spilling in all directions as it grows.  It's an enormous pillar whose surface shimmers like an oil slick, and it continues upward until it dwarfs the skyscrapers below, penetrating the heavens themselves.  All throughout the city, space begins to warp and shift as you corrupt the fabric of reality and bend it to your will.\n\n" + c.getMainName() + " is the closest of the Chosen to the epicenter.  Although " + c.hisHer() + " instincts are telling " + c.himHer() + " to immediately begin drawing on as much energy as " + c.heShe() + " can, " + c.heShe() + " has orders to restrain " + c.himHer() + "self until " + c.heShe() + "'s given clearance to go all-out.  Loudspeakers across the city broadcast instructions to the Chosen as they all hurry toward the tower, warning them that this will be the final battle and that they may not survive.  They're told to hold back at least until the most important VIPs can get a safe distance from the city.  It goes unsaid that the rest of the populace is considered an acceptable sacrifice.\n\n");
        }
        w.finalBattleIntro(t, c);
        if (w.getBodyStatus()[0] && !w.getBodyStatus()[2]) {
            if (w.getTechs()[31].isOwned() && !w.upgradedCommander()) {
                w.setBattleRound(0);
                c.BeSurrounded(t, p, f, w);
            }
            else {
                w.append(t, "\n\nBefore the Chosen can meet up with each other, you spring your ambush.  ");
                if (w.upgradedCommander()) {
                    c.startCaptured(t, w);
                }
                else {
                    w.append(t, "Led by your Commander body, your minions emerge from their hiding places and rush in from all directions.  ");
                    if (w.getCapturesPossible() > 0) {
                        w.append(t, "In the chaos, your body takes a serious injury, but with " + c.getMainName() + " surrounded, the battle has already progressed in the Demons' favor.");
                    }
                    else {
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
    
    public static void DefeatScene(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        final Chosen[] killed = new Chosen[2];
        final Chosen[] fallen = new Chosen[2];
        final Chosen[] escaped = new Chosen[3];
        for (int i = 0; i < 3; ++i) {
            if (!w.getCast()[i].alive) {
                if (killed[0] == null) {
                    killed[0] = w.getCast()[i];
                }
                else {
                    killed[1] = w.getCast()[i];
                }
            }
            else if (w.getCast()[i].resolve <= 0) {
                if (fallen[0] == null) {
                    fallen[0] = w.getCast()[i];
                }
                else {
                    fallen[1] = w.getCast()[i];
                }
            }
            else if (escaped[0] == null) {
                escaped[0] = w.getCast()[i];
            }
            else if (escaped[1] == null) {
                escaped[1] = w.getCast()[i];
            }
            else {
                escaped[2] = w.getCast()[i];
            }
        }
        if (escaped[2] != null) {
            w.append(t, "With the Demons all exterminated, there's nothing to stop the Chosen from launching their final maneuver.  They split up, flying in different directions across the city until they can barely see each other as tiny glowing specks against the darkening sky.  Then, they extend their arms toward each other, forming the points of an enormous triangle - with the Demonic spire at its center.\n\n");
            Chosen high = null;
            Chosen mid = null;
            Chosen low = null;
            for (int j = 0; j < 3; ++j) {
                if (w.getCast()[j].getConfidence() > 66) {
                    high = w.getCast()[j];
                }
                else if (w.getCast()[j].getConfidence() > 33) {
                    mid = w.getCast()[j];
                }
                else {
                    low = w.getCast()[j];
                }
            }
            high.say(t, "\"");
            changePortrait(high.convertGender(), high.type, false, false, w, new String[] { high.mainName, mid.mainName, low.mainName, null, null }, 0, Emotion.FOCUS, Emotion.FOCUS);
            changePortrait(mid.convertGender(), mid.type, false, false, w, new String[] { high.mainName, mid.mainName, low.mainName, null, null }, 1, Emotion.FOCUS, Emotion.FOCUS);
            changePortrait(low.convertGender(), low.type, false, false, w, new String[] { high.mainName, mid.mainName, low.mainName, null, null }, 2, Emotion.FOCUS, Emotion.FOCUS);
            if (w.getRelationship(high.getNumber(), mid.getNumber()) >= 0) {
                if (w.getRelationship(high.getNumber(), low.getNumber()) >= 0) {
                    high.say(t, "Let's do it!  Just like we practiced!\"\n\n");
                    mid.say(t, "\"");
                    if (w.getRelationship(mid.getNumber(), low.getNumber()) >= 0) {
                        mid.say(t, "I'm ready!  You okay, " + low.getMainName() + "!?\"\n\n");
                        low.say(t, "\"R-Ready here too!");
                    }
                    else {
                        changePortrait(mid.convertGender(), mid.type, false, false, w, new String[] { high.mainName, mid.mainName, low.mainName, null, null }, 1, Emotion.ANGER, Emotion.ANGER);
                        mid.say(t, "I'm ready!  You'd better not screw it up for us, " + low.getMainName() + "!\"\n\n");
                        low.say(t, "\"I-I won't let you down, " + high.getMainName() + "!");
                    }
                }
                else {
                    changePortrait(high.convertGender(), high.type, false, false, w, new String[] { high.mainName, mid.mainName, low.mainName, null, null }, 0, Emotion.ANGER, Emotion.ANGER);
                    high.say(t, "You'd better not screw this up, " + low.getMainName() + "!\"\n\n");
                    mid.say(t, "\"");
                    if (w.getRelationship(mid.getNumber(), low.getNumber()) >= 0) {
                        mid.say(t, "Don't worry, " + low.getMainName() + "!  I believe in you!\"\n\n");
                        low.say(t, "\"R-Right!");
                    }
                    else {
                        changePortrait(low.convertGender(), low.type, false, false, w, new String[] { high.mainName, mid.mainName, low.mainName, null, null }, 2, Emotion.ANGER, Emotion.ANGER);
                        mid.say(t, "Don't worry, we can finish this without " + low.himHer() + " if we have to!\"\n\n");
                        low.say(t, "\"I-I'm fine, just worry about yourselves!");
                    }
                }
            }
            else {
                changePortrait(high.convertGender(), high.type, false, false, w, new String[] { high.mainName, mid.mainName, low.mainName, null, null }, 0, Emotion.ANGER, Emotion.ANGER);
                if (w.getRelationship(high.getNumber(), low.getNumber()) >= 0) {
                    high.say(t, "You'd better not screw this up, " + mid.getMainName() + "!\"\n\n");
                    changePortrait(mid.convertGender(), mid.type, false, false, w, new String[] { high.mainName, mid.mainName, low.mainName, null, null }, 1, Emotion.ANGER, Emotion.ANGER);
                    mid.say(t, "\"");
                    if (w.getRelationship(mid.getNumber(), low.getNumber()) >= 0) {
                        mid.say(t, "Worry about yourself, " + high.getMainName() + "!\"\n\n");
                        low.say(t, "\"P-Please, you two, we shouldn't be fighting each other now of all times!");
                        changePortrait(low.convertGender(), low.type, false, false, w, new String[] { high.mainName, mid.mainName, low.mainName, null, null }, 2, Emotion.FEAR, Emotion.FEAR);
                    }
                    else {
                        mid.say(t, "You know that if anyone's going to screw up here, it's " + low.getMainName() + "!\"\n\n");
                        low.say(t, "\"I-I won't!  I'm ready, " + high.getMainName() + "!");
                    }
                }
                else {
                    high.say(t, "You two had better not screw this up!\"\n\n");
                    mid.say(t, "\"");
                    if (w.getRelationship(mid.getNumber(), low.getNumber()) >= 0) {
                        mid.say(t, "We definitely won't!  Right, " + mid.getMainName() + "!?\"\n\n");
                        low.say(t, "\"R-Right!");
                    }
                    else {
                        mid.say(t, "I definitely won't, though I'm not sure about " + mid.getMainName() + "...\"\n\n");
                        low.say(t, "\"I-I'm fine!  So let's do this!");
                    }
                }
            }
            low.say(t, "\"\n\n");
            w.append(t, "The three Chosen concentrate, and the space between their outstretched hands briefly shimmers like a pane of glass.  A sharp glint cuts across the city, and suddenly the black tower is severed at its base.  It topples downward, striking the ground with a great rumble and a cloud of dust.  As quickly as that, the Demonic presence over the city lifts.\n\nThe battle is over.  But even though this Demon Lord has been defeated, the scars left on the hearts of the Chosen won't heal so easily.  Their troubles may be just beginning...");
        }
        else if (escaped[1] != null) {
            Chosen first = escaped[0];
            Chosen second = escaped[1];
            if (second.getConfidence() > first.getConfidence()) {
                first = escaped[1];
                second = escaped[0];
            }
            if (fallen[0] != null) {
                w.append(t, "With " + fallen[0].getMainName() + "'s defeat, a powerful evil energy is gathering on the battlefield.  The tip of the Demonic spire begins to glow, preparing to release one final pulse of corruption that will cement your domination over this region of reality.  However, at the same time, " + escaped[0].getMainName() + " and " + escaped[1].getMainName() + " have cleared out the last of your Demonic forces, and they're ready to launch their counterattack.");
            }
            else {
                w.append(t, "With every moment that passes, the Demonic spire grows upward, gathering power and deepening your domination over this region of reality.  However, by sacrificing " + killed[0].getMainName() + "'s life, " + escaped[0].getMainName() + " and " + escaped[1].getMainName() + " have exterminated all the surrounding Demons, and they're ready to launch their counterattack.");
            }
            changePortrait(first.convertGender(), first.type, false, false, w, new String[] { first.mainName, second.mainName, null, null, null }, 0, Emotion.FOCUS, Emotion.FOCUS);
            if (w.getRelationship(first.getNumber(), second.getNumber()) >= 0) {
                changePortrait(second.convertGender(), second.type, false, false, w, new String[] { first.mainName, second.mainName, null, null, null }, 1, Emotion.FOCUS, Emotion.FOCUS);
                first.say(t, "\n\n\"If we work together, I think we can still stop it!  Back me up!\"\n\n");
                second.say(t, "\"Got it!  I'm right behind you!\"\n\n");
                w.append(t, String.valueOf(first.getMainName()) + " charges forward, blazing brighter than the sun as " + first.heShe() + " draws on as much psychic energy as " + first.heShe() + " can.  " + second.getMainName() + " has a hand on " + first.hisHer() + " shoulder, pushing " + first.himHer() + " forward as they fly together.  The two of them blast through the base of the tower, leaving an enormous hole behind them.  And with its lower structure compromised, the shaft begins to topple to one side.  It lands on the city with a deafening crash, kicking up a huge cloud of debris.  As quickly as that, the Demonic presence over the city lifts.\n\nThe battle is over.  But even though this Demon Lord has been defeated, the scars left on the hearts of the Chosen won't heal so easily.  ");
            }
            else {
                changePortrait(second.convertGender(), second.type, false, false, w, new String[] { first.mainName, second.mainName, null, null, null }, 1, Emotion.FEAR, Emotion.FEAR);
                first.say(t, "\n\n\"If we're going to take that thing down, we need to go all-out!  Don't hold back, or you'll die!\"\n\n");
                second.say(t, "\"Huh?  Gaaah!  Ergh... you're... crazy...!\"\n\n");
                w.append(t, String.valueOf(first.getMainName()) + " holds out one palm to shoot a beam of crackling destructive energy directly at " + second.getMainName() + ".  For " + second.hisHer() + " part, " + second.getMainName() + " barely reacts in time to intercept the beam with " + second.hisHer() + " own blast.  The glowing line between " + first.getMainName() + "'s hand and " + second.getMainName() + "'s annihilates everything it touches as the two of them run toward the Demonic spire.  When it cuts into the base of the tower, the opposing energies cause a huge explosion that throws the two Chosen in different directions.  After they've come to their senses, they see the structure beginning to tilt to one side.  It finally topples, throwing up a huge cloud of debris as it lands on the city below.  As quickly as that, the Demonic presence over the city lifts.\n\nThe battle is over.  But even though this Demon Lord has been defeated, the scars left on the hearts of the Chosen won't heal so easily.  ");
            }
            if (fallen[0] != null) {
                w.append(t, String.valueOf(first.getMainName()) + " and " + second.getMainName() + " have their own troubles to deal with, and " + fallen[0].getMainName() + " is nowhere to be found...");
            }
            else {
                w.append(t, "Their troubles may be just beginning...");
            }
        }
        else {
            w.append(t, "After exterminating the intervening Demons, " + escaped[0].getMainName() + " attacks the Demonic spire, drawing on as much power as " + escaped[0].heShe() + " can in an attempt to destroy it.  ");
            if (fallen[1] != null) {
                w.append(t, "But with the other two Chosen having succumbed to the Demons, " + escaped[0].heShe() + "'s finding that " + escaped[0].heShe() + " isn't able to make a dent in it on " + escaped[0].hisHer() + " own.");
            }
            else if (killed[1] != null) {
                w.append(t, "But as the only survivor among the three Chosen, " + escaped[0].heShe() + "'s finding that " + escaped[0].heShe() + " isn't able to make a dent in it on " + escaped[0].hisHer() + " own.");
            }
            else {
                w.append(t, "But with " + killed[0].getMainName() + " dead and " + fallen[0].getMainName() + " having succumbed to the Demons, " + escaped[0].getMainName() + " is finding that " + escaped[0].heShe() + " isn't able to make a dent in it on " + escaped[0].hisHer() + " own.");
            }
            escaped[0].say(t, "\n\n\"");
            final Forsaken.Gender convertGender = escaped[0].convertGender();
            final Chosen.Species type = escaped[0].type;
            final Boolean value = false;
            final Boolean value2 = false;
            final String[] names = new String[5];
            names[0] = escaped[0].mainName;
            changePortrait(convertGender, type, value, value2, w, names, 0, Emotion.SHAME, Emotion.SHAME);
            if (escaped[0].getConfidence() > 66) {
                final Forsaken.Gender convertGender2 = escaped[0].convertGender();
                final Chosen.Species type2 = escaped[0].type;
                final Boolean value3 = false;
                final Boolean value4 = false;
                final String[] names2 = new String[5];
                names2[0] = escaped[0].mainName;
                changePortrait(convertGender2, type2, value3, value4, w, names2, 0, Emotion.STRUGGLE, Emotion.STRUGGLE);
                escaped[0].say(t, "No!  I... I should be strong enough...!");
            }
            else if (escaped[0].getConfidence() > 33) {
                escaped[0].say(t, "Ugh...  We were so close...");
            }
            else {
                escaped[0].say(t, "I'm... I'm too weak after all...");
            }
            escaped[0].say(t, "\"\n\n");
            if (escaped[0].isDrained()) {
                w.append(t, String.valueOf(escaped[0].getMainName()) + " falls to " + escaped[0].hisHer() + " knees, overwhelmed by despair.  " + escaped[0].HeShe() + " spots a nearby knife discarded by a Thrall and takes it in " + escaped[0].hisHer() + " hands.  Before the fight, " + escaped[0].heShe() + " was worried about hurting " + escaped[0].himHer() + "self too badly to fight, but not badly enough to actually be fatal.  But now, " + escaped[0].heShe() + " has nothing left to lose...");
            }
            else if (escaped[0].isParasitized()) {
                w.append(t, String.valueOf(escaped[0].getMainName()) + " turns and flies away, fleeing the battle.  However, " + escaped[0].hisHer() + " clothes begin to flicker and fade, and " + escaped[0].heShe() + " has a harder and harder time staying airborne.  Combined with " + escaped[0].hisHer() + " damaged reputation, this last failure has proven fatal for " + escaped[0].hisHer() + " connection to the public's psychic energy.  By the time " + escaped[0].heShe() + " reaches the neighboring city, " + escaped[0].heShe() + "'ll return to being nothing more than ");
                if (!escaped[0].getMainName().equals(escaped[0].getGivenName())) {
                    w.append(t, String.valueOf(escaped[0].getGivenName()) + ", ");
                }
                w.append(t, "a typical powerless human.");
            }
            else if (escaped[0].isImpregnated()) {
                w.append(t, String.valueOf(escaped[0].getMainName()) + " turns and flies away, fleeing the battle.  " + escaped[0].HeShe() + " has no intention of returning to the military and reporting " + escaped[0].hisHer() + " failure, because " + escaped[0].heShe() + " knows that soon, " + escaped[0].hisHer() + " Demonic pregnancy will begin to show.  " + escaped[0].HisHer() + " life as one of the Chosen is over, and " + escaped[0].hisHer() + " life as a fugitive begins...");
            }
            else if (escaped[0].isHypnotized()) {
                w.append(t, "As much as it pains " + escaped[0].himHer() + " to do so, " + escaped[0].getMainName() + " turns and flees.  This battle may be lost, but " + escaped[0].heShe() + "'s determined to escape and survive to fight another day.  However, even after " + escaped[0].heShe() + " escapes the range of your influence, your post-hypnotic commands continue to linger in " + escaped[0].hisHer() + " subconscious.  It remains to be seen what sort of depravity " + escaped[0].heShe() + "'ll get into...");
            }
            else {
                w.append(t, "With no other options remaining, " + escaped[0].getMainName() + " turns to flee.");
            }
        }
        EndFinalBattle(t, p, f, w);
    }
    
    public static void EndFinalBattle(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        Chosen escaped = null;
        final Chosen[] defeated = new Chosen[3];
        int numberDefeated = 0;
        int numberKilled = 0;
        for (int i = 0; i < 3; ++i) {
            if (!w.getCast()[i].alive) {
                ++numberKilled;
            }
            else if (w.getCast()[i].resolve <= 0) {
                defeated[numberDefeated] = w.getCast()[i];
                ++numberDefeated;
            }
            else {
                escaped = w.getCast()[i];
            }
        }
        if (numberKilled + numberDefeated == 2) {
            int type = 0;
            if (escaped.isDrained()) {
                type = 3;
            }
            else if (escaped.isImpregnated()) {
                type = 1;
            }
            else if (escaped.isParasitized()) {
                type = 4;
            }
            else if (escaped.isHypnotized()) {
                type = 2;
            }
            final int sceneShown = type;
            final Chosen sceneSubject = escaped;
            if (type != 0) {
                p.removeAll();
                final JButton Continue = new JButton("Continue");
                Continue.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\n");
                        w.ending(t, sceneShown, sceneSubject, null, null);
                        Project.ReportScore(t, p, f, w);
                    }
                });
                p.add(Continue);
                p.validate();
                p.repaint();
            }
            else {
                ReportScore(t, p, f, w);
            }
        }
        else {
            ReportScore(t, p, f, w);
        }
    }
    
    public static void ReportScore(final JTextPane t, final JPanel p, final JFrame f, final WorldState w) {
        p.removeAll();
        final JButton Continue = new JButton("Continue");
        Continue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                int forsaken = 0;
                int casualties = 0;
                int returning = 0;
                final Chosen[] corrupted = new Chosen[3];
                final Chosen[] killed = new Chosen[3];
                final Chosen[] escaped = new Chosen[3];
                for (int i = 0; i < 3; ++i) {
                    if (w.getCast()[i].alive && w.getCast()[i].resolve <= 0) {
                        corrupted[forsaken] = w.getCast()[i];
                        ++forsaken;
                    }
                    else if (w.getCast()[i].alive) {
                        escaped[returning] = w.getCast()[i];
                        ++returning;
                    }
                    else {
                        killed[casualties] = w.getCast()[i];
                        ++casualties;
                    }
                }
                if (!w.isCheater() && w.hardMode) {
                    w.scoreSummary(t);
                }
                w.finalBattle = false;
                w.getTechs()[48].owned = false;
                for (int i = 0; i < 3; ++i) {
                    w.getCast()[i].alive = true;
                    w.getCast()[i].resolve = 100;
                }
                w.incrementDay();
                w.clearCommander();
                for (int i = 0; i < 3; ++i) {
                    w.getCast()[i].addTrauma();
                    w.getCast()[i].surrounded = false;
                    w.getCast()[i].captured = false;
                    w.getCast()[i].removeSurround = false;
                }
                if (forsaken + casualties >= 2 || !w.campaign) {
                    final JButton ContinueFour = new JButton("Continue Playing");
                    ContinueFour.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(final ActionEvent e) {
                            Project.Shop(t, p, f, w);
                        }
                    });
                    if (w.campaign) {
                        ContinueFour.setText("Continue");
                    }
                    p.add(ContinueFour);
                }
                if ((w.isCheater() || !w.hardMode) && !w.campaign) {
                    w.append(t, "\n\n" + w.getSeparator() + "\n\nI hope you enjoyed this playthrough of Corrupted Saviors!  For an even greater challenge, consider trying Hard Mode or Campaign Mode!");
                }
                if (w.campaign) {
                    if (forsaken + casualties >= 2) {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\nThe city of " + w.cityName + " has fallen under your control.  ");
                        if (w.day < 50 - w.eventOffset) {
                            w.append(t, "Your followers won't be able to establish a foothold in another city right away, so you can take some time to consolidate power here and enjoy your conquest.");
                        }
                        else {
                            w.append(t, "Now all that remains is to decide where to strike next.");
                        }
                        w.loopComplete = true;
                    }
                    else {
                        w.append(t, "\n\n" + w.getSeparator() + "\n\nWith your defeat in " + w.cityName + ", the momentum of your advance across the globe has been halted.  However, so long as there is darkness within the human heart, a Demon Lord cannot be truly killed.  Before long, you will rise again.");
                        if (w.conquered.length > 0 || w.sacrificed.length > 0) {
                            w.append(t, "  And in the meantime, there's much enjoyment to be had from your conquests...");
                        }
                    }
                }
                if (forsaken > 0) {
                    w.append(t, "\n\n");
                    if (forsaken == 1) {
                        w.append(t, String.valueOf(corrupted[0].getMainName()) + " has ");
                    }
                    else if (forsaken == 2) {
                        w.append(t, String.valueOf(corrupted[0].getMainName()) + " and " + corrupted[1].getMainName() + " have ");
                    }
                    else {
                        w.append(t, String.valueOf(corrupted[0].getMainName()) + ", " + corrupted[1].getMainName() + ", and " + corrupted[2].getMainName() + " have ");
                    }
                    if (!w.campaign) {
                        w.append(t, "been added to the ranks of the Forsaken!  You can interact with them from the Main Menu, and you may also use them to help corrupt new Chosen in future playthroughs!");
                    }
                    else if (forsaken + casualties >= 2) {
                        w.append(t, "been added to the ranks of the Forsaken!  This will surely prove useful against the Chosen you've yet to face.");
                    }
                    else {
                        w.append(t, "been added to the ranks of the Forsaken!  This makes for a fine consolation prize.");
                    }
                    String path = Project.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                    String fileName = "";
                    for (int j = path.length() - 1; j >= 0; --j) {
                        if (path.charAt(j) != '/') {
                            fileName = String.valueOf(path.charAt(j)) + fileName;
                        }
                        else {
                            j = -1;
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
                    final File saveLocation = new File(String.valueOf(path) + File.separator + "saves.sav");
                    SaveData saves = null;
                    if (saveLocation.exists()) {
                        final ReadObject robj = new ReadObject();
                        saves = robj.deserializeSaveData(String.valueOf(path) + File.separator + "saves.sav");
                    }
                    else {
                        saves = new SaveData();
                    }
                    for (int k = 0; k < saves.getSaves().length; ++k) {
                        saves.getSaves()[k].repairSave();
                    }
                    final WriteObject wobj = new WriteObject();
                    final SaveData saveFile = saves;
                    for (int l = 0; l < 3; ++l) {
                        if (forsaken > l) {
                            int index = 0;
                            if (!w.campaign && saves.harem == null) {
                                saves.harem = new Forsaken[1];
                            }
                            else {
                                index = w.getHarem().length;
                            }
                            final Forsaken[] newHarem = new Forsaken[index + 1];
                            for (int m = 0; m < index; ++m) {
                                newHarem[m] = w.getHarem()[m];
                            }
                            final Forsaken newForsaken = new Forsaken();
                            newForsaken.initialize(w, corrupted[l]);
                            newHarem[index] = newForsaken;
                            newHarem[index].forsakenID = saves.assignID();
                            newHarem[index].otherChosen = new Chosen[2 + corrupted[l].formerPartners.length];
                            newHarem[index].chosenRelations = new Forsaken.Relationship[2 + corrupted[l].formerPartners.length];
                            for (int j2 = 0; j2 < 3 + corrupted[l].formerPartners.length; ++j2) {
                                if (j2 < 3) {
                                    if (j2 < corrupted[l].number) {
                                        newHarem[index].otherChosen[j2] = w.getCast()[j2];
                                        newHarem[index].chosenRelations[j2] = Forsaken.Relationship.PARTNER;
                                    }
                                    else if (j2 > corrupted[l].number) {
                                        newHarem[index].otherChosen[j2 - 1] = w.getCast()[j2];
                                        newHarem[index].chosenRelations[j2 - 1] = Forsaken.Relationship.PARTNER;
                                    }
                                }
                                else {
                                    newHarem[index].otherChosen[j2 - 1] = corrupted[l].formerPartners[j2 - 3];
                                    newHarem[index].chosenRelations[j2 - 1] = Forsaken.Relationship.PARTNER;
                                }
                            }
                            newHarem[index].formerPartners = new Chosen[corrupted[l].formerPartners.length + 2];
                            newHarem[index].formerFriendships = new int[corrupted[l].formerRelationships.length + 2];
                            for (int j2 = 0; j2 < newHarem[index].formerPartners.length + 1; ++j2) {
                                if (j2 < 3) {
                                    if (j2 < corrupted[l].number) {
                                        newHarem[index].formerPartners[j2] = w.getCast()[j2];
                                        newHarem[index].formerFriendships[j2] = w.getRelationship(corrupted[l].number, j2);
                                    }
                                    else if (j2 > corrupted[l].number) {
                                        newHarem[index].formerPartners[j2 - 1] = w.getCast()[j2];
                                        newHarem[index].formerFriendships[j2 - 1] = w.getRelationship(corrupted[l].number, j2);
                                    }
                                }
                                else {
                                    newHarem[index].formerPartners[j2 - 1] = corrupted[l].formerPartners[j2 - 3];
                                    newHarem[index].formerFriendships[j2 - 1] = corrupted[l].formerRelationships[j2 - 3];
                                }
                            }
                            newHarem[index].kills = corrupted[l].kills;
                            newHarem[index].killRelationships = corrupted[l].killRelationships;
                            if (l == 1) {
                                newForsaken.others = new Forsaken[] { newHarem[index - 1] };
                                newForsaken.forsakenRelations = new Forsaken.Relationship[] { Forsaken.Relationship.PARTNER };
                                newForsaken.troublemaker = new int[1];
                                newHarem[index - 1].others = new Forsaken[] { newForsaken };
                                newHarem[index - 1].forsakenRelations = new Forsaken.Relationship[] { Forsaken.Relationship.PARTNER };
                                newHarem[index - 1].troublemaker = new int[1];
                            }
                            else if (l == 2) {
                                newForsaken.others = new Forsaken[] { newHarem[index - 1], newHarem[index - 2] };
                                newForsaken.forsakenRelations = new Forsaken.Relationship[] { Forsaken.Relationship.PARTNER, Forsaken.Relationship.PARTNER };
                                newForsaken.troublemaker = new int[2];
                                newHarem[index - 1].others = new Forsaken[] { newForsaken, newHarem[index - 2] };
                                newHarem[index - 1].forsakenRelations = new Forsaken.Relationship[] { Forsaken.Relationship.PARTNER, Forsaken.Relationship.PARTNER };
                                newHarem[index - 1].troublemaker = new int[2];
                                newHarem[index - 2].others = new Forsaken[] { newForsaken, newHarem[index - 1] };
                                newHarem[index - 2].forsakenRelations = new Forsaken.Relationship[] { Forsaken.Relationship.PARTNER, Forsaken.Relationship.PARTNER };
                                newHarem[index - 2].troublemaker = new int[2];
                            }
                            if (w.campaign) {
                                w.conquered = newHarem;
                            }
                            else {
                                saves.harem = newHarem;
                                w.save = saves;
                            }
                        }
                    }
                    wobj.serializeSaveData(saves);
                    final SaveData fileUsed = saves;
                    if (!w.campaign) {
                        final JButton ForsakenMenu = new JButton("Forsaken Menu");
                        ForsakenMenu.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                final WorldState x = new WorldState();
                                x.initialize();
                                x.copySettings(t, w);
                                x.copyToggles(w);
                                x.setGenders(w.getGenderBalance());
                                x.save = w.save;
                                Project.ForsakenMenu(t, p, f, x, fileUsed, 0);
                            }
                        });
                        p.add(ForsakenMenu);
                    }
                }
                if (w.campaign) {
                    int numberDiscarded = 0;
                    int numberKept = 0;
                    final Chosen[] discarded = new Chosen[3];
                    final Chosen[] kept = new Chosen[3];
                    for (int k = 0; k < 3; ++k) {
                        if (escaped[k] != null) {
                            if (escaped[k].impregnated || escaped[k].hypnotized || escaped[k].drained || escaped[k].parasitized) {
                                discarded[numberDiscarded] = escaped[k];
                                ++numberDiscarded;
                            }
                            else {
                                kept[numberKept] = escaped[k];
                                ++numberKept;
                            }
                        }
                    }
                    if (numberKept > 0) {
                        final Chosen[] newReturning = new Chosen[w.returning.length + numberKept];
                        for (int i2 = 0; i2 < w.returning.length; ++i2) {
                            newReturning[i2] = w.returning[i2];
                        }
                        for (int i2 = 0; i2 < numberKept; ++i2) {
                            newReturning[w.returning.length + i2] = kept[i2];
                        }
                        w.returning = newReturning;
                    }
                    if (numberDiscarded > 0) {
                        final Chosen[] newDeceased = new Chosen[w.deceased.length + numberDiscarded];
                        for (int i2 = 0; i2 < w.deceased.length; ++i2) {
                            newDeceased[i2] = w.deceased[i2];
                        }
                        for (int i2 = 0; i2 < numberDiscarded; ++i2) {
                            newDeceased[w.deceased.length + i2] = discarded[i2];
                        }
                        w.deceased = newDeceased;
                    }
                    if (forsaken > 0) {
                        final Chosen[] newFormerChosen = new Chosen[w.formerChosen.length + forsaken];
                        for (int i2 = 0; i2 < w.formerChosen.length; ++i2) {
                            newFormerChosen[i2] = w.formerChosen[i2];
                        }
                        for (int i2 = 0; i2 < forsaken; ++i2) {
                            newFormerChosen[w.formerChosen.length + i2] = corrupted[i2];
                        }
                        w.formerChosen = newFormerChosen;
                    }
                }
                if (w.campaign && forsaken + casualties < 2) {
                    if (w.conquered.length > 0 || w.sacrificed.length > 0) {
                        w.append(t, "\n\n");
                        Project.WrapUpCampaign(t, p, f, w, null, null);
                    }
                    else {
                        w.append(t, "\n\nThank you for playing the campaign mode of Corrupted Saviors!  If you're having trouble with the game, consider trying out Single Play mode, which allows the use of cheats after the final battle regardless of the result.");
                        final JButton Continue = new JButton("Main Menu");
                        Continue.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(final ActionEvent e) {
                                final WorldState x = new WorldState();
                                x.initialize();
                                x.copySettings(t, w);
                                x.copyToggles(w);
                                x.setGenders(w.getGenderBalance());
                                x.save = w.save;
                                Project.IntroOne(t, p, f, x);
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
    
    public static void WrapUpCampaign(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, Boolean[] broughtConquered, Boolean[] broughtSacrificed) {
        p.removeAll();
        final int conquered = w.conquered.length;
        final int sacrificed = w.sacrificed.length;
        if (broughtConquered == null || broughtSacrificed == null) {
            broughtConquered = new Boolean[conquered];
            for (int i = 0; i < conquered; ++i) {
                broughtConquered[i] = true;
            }
            broughtSacrificed = new Boolean[sacrificed];
            for (int i = 0; i < sacrificed; ++i) {
                broughtSacrificed[i] = true;
            }
        }
        else {
            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        }
        if (conquered > 0 && sacrificed > 0) {
            w.append(t, "You can bring your Forsaken with you to use in Single Play mode, including the Forsaken who were disposed of during play.");
        }
        else if (sacrificed > 0) {
            w.append(t, "Even though you didn't end the game with any Forsaken, you can still bring with you the ones you disposed of during play.");
        }
        else {
            w.append(t, "You can bring your Forsaken with you to use in Single Play mode.");
        }
        Boolean saveAllConquered = false;
        Boolean deleteAllConquered = false;
        Boolean saveAllSacrificed = false;
        Boolean deleteAllSacrificed = false;
        if (conquered > 0) {
            w.append(t, "\n\nCurrent Forsaken\n");
            for (int j = 0; j < conquered; ++j) {
                w.append(t, "\n" + w.conquered[j].mainName + ": ");
                if (broughtConquered[j]) {
                    w.greenAppend(t, "SAVE");
                    deleteAllConquered = true;
                }
                else {
                    w.redAppend(t, "DELETE");
                    saveAllConquered = true;
                }
            }
        }
        if (sacrificed > 0) {
            w.append(t, "\n\nFormer Forsaken\n");
            for (int j = 0; j < sacrificed; ++j) {
                w.append(t, "\n" + w.sacrificed[j].mainName + ": ");
                if (broughtSacrificed[j]) {
                    w.greenAppend(t, "SAVE");
                    deleteAllSacrificed = true;
                }
                else {
                    w.redAppend(t, "DELETE");
                    saveAllSacrificed = true;
                }
            }
        }
        final Boolean[] conqueredSetting = broughtConquered;
        final Boolean[] sacrificedSetting = broughtSacrificed;
        if (saveAllConquered) {
            final JButton Save = new JButton("Save All Current");
            Save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final Boolean[] newSaved = new Boolean[conquered];
                    for (int i = 0; i < conquered; ++i) {
                        newSaved[i] = true;
                    }
                    Project.WrapUpCampaign(t, p, f, w, newSaved, sacrificedSetting);
                }
            });
            p.add(Save);
        }
        if (deleteAllConquered) {
            final JButton Delete = new JButton("Delete All Current");
            Delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final Boolean[] newSaved = new Boolean[conquered];
                    for (int i = 0; i < conquered; ++i) {
                        newSaved[i] = false;
                    }
                    Project.WrapUpCampaign(t, p, f, w, newSaved, sacrificedSetting);
                }
            });
            p.add(Delete);
        }
        if (saveAllSacrificed) {
            final JButton Save = new JButton("Save All Former");
            Save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final Boolean[] newSaved = new Boolean[sacrificed];
                    for (int i = 0; i < sacrificed; ++i) {
                        newSaved[i] = true;
                    }
                    Project.WrapUpCampaign(t, p, f, w, conqueredSetting, newSaved);
                }
            });
            p.add(Save);
        }
        if (deleteAllSacrificed) {
            final JButton Save = new JButton("Delete All Former");
            Save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    final Boolean[] newSaved = new Boolean[sacrificed];
                    for (int i = 0; i < sacrificed; ++i) {
                        newSaved[i] = false;
                    }
                    Project.WrapUpCampaign(t, p, f, w, conqueredSetting, newSaved);
                }
            });
            p.add(Save);
        }
        final JButton Decide = new JButton("Decide Individually");
        Decide.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Project.DecideKeptForsaken(t, p, f, w, conqueredSetting, sacrificedSetting, 0);
            }
        });
        p.add(Decide);
        final JButton Done = new JButton("Done");
        Done.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                p.removeAll();
                w.append(t, "\n\n" + w.getSeparator() + "\n\nAre you sure?  ");
                int brought = 0;
                int deleted = 0;
                for (int i = 0; i < conquered; ++i) {
                    if (conqueredSetting[i]) {
                        ++brought;
                    }
                    else {
                        ++deleted;
                    }
                }
                for (int i = 0; i < sacrificed; ++i) {
                    if (sacrificedSetting[i]) {
                        ++brought;
                    }
                    else {
                        ++deleted;
                    }
                }
                if (deleted == 0) {
                    w.append(t, "All Forsaken from this playthrough will be added to the save file.");
                }
                else if (brought == 0) {
                    w.append(t, "All Forsaken from this playthrough will be deleted and can only be recovered by loading an old campaign save.");
                }
                else {
                    w.append(t, String.valueOf(brought) + " Forsaken from this playthrough will be added to the save file and the other " + deleted + " will be deleted.  Deleted Forsaken can only be recovered by loading an old campaign save.");
                }
                final int totalBrought = brought;
                final JButton Confirm = new JButton("Confirm");
                Confirm.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        if (totalBrought > 0) {
                            if (w.save.harem == null) {
                                w.save.harem = new Forsaken[0];
                            }
                            final Forsaken[] newHarem = new Forsaken[w.save.harem.length + totalBrought];
                            for (int i = 0; i < w.save.harem.length; ++i) {
                                newHarem[i] = w.save.harem[i];
                            }
                            int additional = 0;
                            for (int j = 0; j < conquered; ++j) {
                                if (conqueredSetting[j]) {
                                    newHarem[w.save.harem.length + additional] = w.conquered[j];
                                    ++additional;
                                }
                            }
                            for (int j = 0; j < sacrificed; ++j) {
                                if (sacrificedSetting[j]) {
                                    newHarem[w.save.harem.length + additional] = w.sacrificed[j];
                                    ++additional;
                                }
                            }
                            w.save.harem = newHarem;
                        }
                        t.setText("");
                        final WorldState x = new WorldState();
                        x.initialize();
                        x.copySettings(t, w);
                        x.copyToggles(w);
                        x.setGenders(w.getGenderBalance());
                        x.save = w.save;
                        Project.IntroOne(t, p, f, x);
                    }
                });
                p.add(Confirm);
                final JButton Cancel = new JButton("Cancel");
                Cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        Project.WrapUpCampaign(t, p, f, w, conqueredSetting, sacrificedSetting);
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
    
    public static void DecideKeptForsaken(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final Boolean[] broughtConquered, final Boolean[] broughtSacrificed, final int position) {
        p.removeAll();
        if (position < broughtConquered.length) {
            w.append(t, "\n\n" + w.getSeparator() + "\n\nWhat will you do with " + w.conquered[position].mainName + "?");
            final JButton Save = new JButton("Save");
            Save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    broughtConquered[position] = true;
                    Project.DecideKeptForsaken(t, p, f, w, broughtConquered, broughtSacrificed, position + 1);
                }
            });
            p.add(Save);
            final JButton Delete = new JButton("Delete");
            Delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    broughtConquered[position] = false;
                    Project.DecideKeptForsaken(t, p, f, w, broughtConquered, broughtSacrificed, position + 1);
                }
            });
            p.add(Delete);
        }
        else if (position < broughtConquered.length + broughtSacrificed.length) {
            w.append(t, "\n\n" + w.getSeparator() + "\n\nWhat will you do with " + w.sacrificed[position - broughtConquered.length].mainName + "?");
            final JButton Save = new JButton("Save");
            Save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    broughtSacrificed[position - broughtConquered.length] = true;
                    Project.DecideKeptForsaken(t, p, f, w, broughtConquered, broughtSacrificed, position + 1);
                }
            });
            p.add(Save);
            final JButton Delete = new JButton("Delete");
            Delete.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    broughtSacrificed[position - broughtConquered.length] = false;
                    Project.DecideKeptForsaken(t, p, f, w, broughtConquered, broughtSacrificed, position + 1);
                }
            });
            p.add(Delete);
        }
        else {
            WrapUpCampaign(t, p, f, w, broughtConquered, broughtSacrificed);
        }
        p.validate();
        p.repaint();
    }
    
    public static void ForsakenDowntime(final JTextPane t, final JPanel p, final JFrame f, final WorldState w, final SaveData s, Forsaken[] exhausted) {
        if (!w.loopComplete) {
            w.append(t, "\n\n" + w.getSeparator() + "\n\n");
        }
        for (int i = 0; i < w.getHarem().length; ++i) {
            if (w.getHarem()[i].others != null) {
                for (int j = 0; j < w.getHarem()[i].others.length; ++j) {
                    Boolean present = false;
                    for (int k = 0; k < w.getHarem().length; ++k) {
                        if (w.getHarem()[k].equals(w.getHarem()[i].others[j])) {
                            present = true;
                        }
                    }
                    if (present) {
                        w.getHarem()[i].troublemaker[j] = w.getHarem()[i].troublemaker[j] * 9 / 10;
                    }
                }
            }
        }
        Forsaken[] included = w.getHarem();
        if (exhausted != null && exhausted.length == 0) {
            exhausted = w.trainedForsaken;
        }
        if (exhausted != null && exhausted.length > 0) {
            final Forsaken[] newIncluded = new Forsaken[w.getHarem().length - exhausted.length];
            int numberFound = 0;
            for (int l = 0; l < w.getHarem().length; ++l) {
                Boolean notExhausted = true;
                for (int m = 0; m < exhausted.length; ++m) {
                    if (w.getHarem()[l].equals(exhausted[m])) {
                        notExhausted = false;
                    }
                }
                if (notExhausted) {
                    newIncluded[numberFound] = w.getHarem()[l];
                    ++numberFound;
                }
            }
            included = newIncluded;
        }
        Forsaken tantruming = null;
        int highest = 0;
        for (int l = 0; l < included.length; ++l) {
            if (included[l].hostility * 10 - included[l].motivation > highest) {
                highest = included[l].hostility * 10 - included[l].motivation;
                tantruming = included[l];
            }
        }
        int[] damages = new int[3];
        if (tantruming != null) {
            damages = tantruming.motivationDamage();
            for (int i2 = 0; i2 < w.getHarem().length; ++i2) {
                if (w.getHarem()[i2] != tantruming) {
                    int offense = 100;
                    int damage = damages[1];
                    if (tantruming.opinion(w.getHarem()[i2]) > 100) {
                        offense = 70;
                        damage = damages[0];
                    }
                    else if (tantruming.opinion(w.getHarem()[i2]) < -100) {
                        offense = 150;
                        damage = damages[2];
                    }
                    final Forsaken forsaken = w.getHarem()[i2];
                    forsaken.motivation -= damage;
                    if (w.getHarem()[i2].motivation < 0) {
                        w.getHarem()[i2].motivation = 0;
                    }
                    if (w.getHarem()[i2].others != null) {
                        Boolean found = false;
                        for (int j2 = 0; j2 < w.getHarem()[i2].others.length; ++j2) {
                            if (w.getHarem()[i2].others[j2].equals(tantruming)) {
                                found = true;
                                final int[] troublemaker = w.getHarem()[i2].troublemaker;
                                final int n = j2;
                                troublemaker[n] += offense;
                            }
                        }
                        if (!found) {
                            final Forsaken[] newOthers = new Forsaken[w.getHarem()[i2].others.length + 1];
                            final int[] newTroublemaker = new int[w.getHarem()[i2].troublemaker.length + 1];
                            for (int j3 = 0; j3 < w.getHarem()[i2].others.length; ++j3) {
                                newOthers[j3] = w.getHarem()[i2].others[j3];
                                newTroublemaker[j3] = w.getHarem()[i2].troublemaker[j3];
                            }
                            newOthers[newOthers.length - 1] = tantruming;
                            newTroublemaker[newTroublemaker.length - 1] = offense;
                            w.getHarem()[i2].others = newOthers;
                            w.getHarem()[i2].troublemaker = newTroublemaker;
                        }
                    }
                    else {
                        w.getHarem()[i2].others = new Forsaken[] { tantruming };
                        w.getHarem()[i2].troublemaker = new int[offense];
                    }
                }
            }
            if (w.getHarem().length > 1) {
                if (tantruming.hostility < 20 && tantruming.defeatType != 5) {
                    w.append(t, String.valueOf(tantruming.mainName) + " tries to organize a resistance against you, ");
                    if (tantruming.confidence > 66) {
                        w.append(t, "demanding that your other minions join " + tantruming.himHer() + ".  ");
                    }
                    else if (tantruming.confidence > 33) {
                        w.append(t, "appealing to your other minions' sense of morality.  ");
                    }
                    else {
                        w.append(t, "begging and pleading for your other minions to find their conscience.  ");
                    }
                    w.append(t, "Even for those who are inclined to listen to such arguments, the effort is more annoying than persuasive.");
                }
                else if (tantruming.hostility < 40) {
                    w.append(t, String.valueOf(tantruming.mainName) + " lets out " + tantruming.hisHer() + " frustrations on your other minions, ");
                    if (tantruming.confidence > 66) {
                        w.append(t, "aggressively asserting " + tantruming.hisHer() + " dominance over anyone who can't or won't stand up to " + tantruming.himHer() + ".  ");
                    }
                    else if (tantruming.confidence > 33) {
                        w.append(t, "spitting insults at anyone who so much as looks at " + tantruming.himHer() + " funny.  ");
                    }
                    else {
                        w.append(t, "passive-aggressively insulting anyone who makes the mistake of spending too much time around " + tantruming.himHer() + ".  ");
                    }
                    w.append(t, "Even for everyone else, " + tantruming.hisHer() + " acting out is a constant annoyance.");
                }
                else if (tantruming.hostility < 61) {
                    w.append(t, String.valueOf(tantruming.mainName) + " makes a scene in the middle of your base of operations, ");
                    if (tantruming.confidence > 66) {
                        w.append(t, "ranting, raving, and blaming everyone else for all " + tantruming.hisHer() + " problems.  ");
                    }
                    else if (tantruming.confidence > 33) {
                        w.append(t, "shouting about how " + tantruming.heShe() + " hates being one of the Forsaken.  ");
                    }
                    else {
                        w.append(t, "wailing in despair and whining about how unfair the world is.  ");
                    }
                    w.append(t, "The disruptive behavior is bad for your other minions' morale.");
                }
                else if (tantruming.hostility < 81) {
                    w.append(t, String.valueOf(tantruming.mainName) + " gets violent with your other minions, ");
                    if (tantruming.confidence > 66) {
                        w.append(t, "challenging them to fight " + tantruming.himHer() + " head-on, and outright attacking those who try to flee.  ");
                    }
                    else if (tantruming.confidence > 33) {
                        w.append(t, "picking fights and getting into several scuffles over the course of the night.  ");
                    }
                    else {
                        w.append(t, "abruptingly attacking them from behind and then fleeing before they can retaliate.  ");
                    }
                    w.append(t, "The anger and resentment directed at " + tantruming.himHer() + " grows.");
                }
                else {
                    w.append(t, String.valueOf(tantruming.mainName) + " goes on a murderous rampage, ");
                    if (tantruming.confidence > 66) {
                        w.append(t, "carving a wide and indiscriminate swath of destruction through your base of operations.  ");
                    }
                    else if (tantruming.confidence > 33) {
                        w.append(t, "hunting down and attacking anyone " + tantruming.heShe() + " feels has wronged " + tantruming.himHer() + ".  ");
                    }
                    else {
                        w.append(t, "slipping poison into the meals of countless Thralls and others " + tantruming.heShe() + " has a grudge against before " + tantruming.heShe() + "'s caught.  ");
                    }
                    w.append(t, "The resulting chaos affects your other minions as well.");
                }
                w.append(t, "  (+" + tantruming.staminaRegen() / 10 + "." + tantruming.staminaRegen() % 10 + "% Stamina, restores own Motivation at expense of everyone else)");
            }
            else {
                w.append(t, String.valueOf(tantruming.mainName) + " is too stressed to relax, but there aren't any other Forsaken around for " + tantruming.himHer() + " to release " + tantruming.hisHer() + " tension on. (+" + tantruming.staminaRegen() + " Stamina)");
            }
        }
        for (int i2 = 0; i2 < included.length; ++i2) {
            if (included[i2] != tantruming) {
                if (tantruming != null || i2 > 0) {
                    w.append(t, "\n\n");
                }
                final int flavor = (int)(4.0 * Math.random());
                if (included[i2].demonicBirths < 0) {
                    w.append(t, "Now that " + included[i2].mainName + " is no longer one of the Chosen, the child in " + included[i2].hisHer() + " belly is just a regular Demon, and " + included[i2].heShe() + " quickly goes into labor.  The resulting abomination ");
                    if (included[i2].gender.equals(Forsaken.Gender.MALE)) {
                        w.append(t, "forces its way out of " + included[i2].mainName + "'s asshole");
                    }
                    else {
                        w.append(t, "slides out of " + included[i2].mainName + "'s distended vagina");
                    }
                    w.append(t, " while " + included[i2].heShe() + " ");
                    if (included[i2].confidence > 66) {
                        w.append(t, "grunts and strains");
                    }
                    else if (included[i2].confidence > 33) {
                        w.append(t, "stares down in horror");
                    }
                    else {
                        w.append(t, "whimpers and whines");
                    }
                    w.append(t, ", then scuttles off immediately in search of its first victim.");
                    included[i2].demonicBirths = 1;
                }
                else if (flavor == 0) {
                    if (included[i2].demonicBirths > 0 && (int)(Math.random() * 2.0) == 0) {
                        final Forsaken forsaken2 = included[i2];
                        ++forsaken2.demonicBirths;
                        w.append(t, "Due to " + included[i2].hisHer() + " nighttime activities, " + included[i2].mainName + " has been impregnated with another fast-growing Demonic child.  " + included[i2].HeShe() + " gives birth to a small tentacled creature, ");
                        if (included[i2].innocence > 66) {
                            w.append(t, "then happily waves goodbye as it slithers away.");
                        }
                        else if (included[i2].innocence > 33) {
                            w.append(t, "which leaves " + included[i2].himHer() + " gasping for breath.");
                        }
                        else {
                            w.append(t, "then mentally collects " + included[i2].himHer() + "self and continues about " + included[i2].hisHer() + " business.");
                        }
                    }
                    else if (included[i2].timesKilled > 2 && (int)(Math.random() * 2.0) == 0) {
                        final Forsaken forsaken3 = included[i2];
                        ++forsaken3.timesKilled;
                        w.append(t, "A particularly bold Thrall ambushes " + included[i2].mainName + " while " + included[i2].heShe() + "'s alone and tries to rape " + included[i2].himHer());
                        if (included[i2].morality > 66) {
                            w.append(t, ", and " + included[i2].mainName + " is happy afterwards to note that " + included[i2].heShe() + " doesn't feel guilty in the slightest about killing him.");
                        }
                        else if (included[i2].morality > 33) {
                            w.append(t, ", but the Forsaken has no trouble overpowering and killing " + included[i2].hisHer() + " attacker.");
                        }
                        else {
                            w.append(t, ", and " + included[i2].mainName + " enjoys giving him an especially slow and painful death.");
                        }
                    }
                    else if (included[i2].timesHadSex > 0 && ((int)(Math.random() * 2.0) == 0 || included[i2].peopleInjured == 0)) {
                        final Forsaken forsaken4 = included[i2];
                        forsaken4.timesHadSex += 3 + (int)(Math.random() * 3.0);
                        final Forsaken forsaken5 = included[i2];
                        forsaken5.orgasmsGiven += 5 + (int)(Math.random() * 5.0);
                        if (included[i2].timesOrgasmed > 0) {
                            final Forsaken forsaken6 = included[i2];
                            ++forsaken6.timesOrgasmed;
                        }
                        w.append(t, String.valueOf(included[i2].mainName) + " attends a wild party and ends up participating in an orgy, ");
                        if (included[i2].confidence > 66) {
                            w.append(t, "gleefully dominating several partners at once.");
                        }
                        else if (included[i2].confidence > 33) {
                            w.append(t, "enjoying " + included[i2].himHer() + "self greatly.");
                        }
                        else {
                            w.append(t, "surrendering " + included[i2].himHer() + "self to the lustful crowd.");
                        }
                    }
                    else if (included[i2].peopleInjured > 0) {
                        final Forsaken forsaken7 = included[i2];
                        ++forsaken7.peopleInjured;
                        w.append(t, "A particularly bold Thrall ambushes " + included[i2].mainName + " while " + included[i2].heShe() + "'s alone and tries to rape " + included[i2].himHer());
                        if (included[i2].morality > 66) {
                            w.append(t, ", but " + included[i2].heShe() + " has no trouble fending him off.");
                        }
                        else if (included[i2].morality > 33) {
                            w.append(t, ", only to receive a sound beating.");
                        }
                        else {
                            w.append(t, ", only to be left with some very painful injuries in " + included[i2].hisHer() + " wake.");
                        }
                    }
                    else if (included[i2].morality > 66) {
                        w.append(t, String.valueOf(included[i2].mainName) + " spends " + included[i2].hisHer() + " time helping out your weaker minions, protecting them from danger and boosting their spirits.");
                    }
                    else if (included[i2].morality > 33) {
                        w.append(t, String.valueOf(included[i2].mainName) + " hangs out with some of the friends " + included[i2].heShe() + "'s made among your minions.");
                    }
                    else {
                        w.append(t, String.valueOf(included[i2].mainName) + " spends some time trying to bargain with you for better accommomdations, but to no avail.");
                    }
                }
                else if (flavor == 1) {
                    if (included[i2].hypnotized && (int)(Math.random() * 2.0) == 0) {
                        w.append(t, String.valueOf(included[i2].mainName) + " sleeps through most of the day, having vivid dreams as you reach directly into " + included[i2].hisHer());
                        if (included[i2].innocence > 66) {
                            w.append(t, " simple mind and rearrange " + included[i2].hisHer() + " instinctive impulses to your liking.");
                        }
                        else if (included[i2].innocence > 33) {
                            w.append(t, " subconscious in order to reinforce " + included[i2].hisHer() + " hypnotic conditioning.");
                        }
                        else {
                            w.append(t, " mind and carefully influence " + included[i2].hisHer() + " thought process in order to prevent " + included[i2].himHer() + " from finding a way to break your hypnotism.");
                        }
                    }
                    else if (included[i2].strongestOrgasm >= 1000 && (int)(Math.random() * 2.0) == 0) {
                        w.append(t, String.valueOf(included[i2].mainName) + " spends the day enjoying the company of several tentacled Demons");
                        if (included[i2].dignity > 66) {
                            w.append(t, ", but while " + included[i2].heShe() + " tries to pretend that " + included[i2].heShe() + "'s just inspecting " + included[i2].hisHer() + " forces, the truth is that " + included[i2].heShe() + "'s having them make " + included[i2].himHer() + " cum over and over again.");
                        }
                        else if (included[i2].dignity > 33) {
                            w.append(t, ", allowing them to ravage " + included[i2].himHer() + " with their many appendages.");
                        }
                        else {
                            w.append(t, ", and soon " + included[i2].heShe() + "'s screaming at the top of " + included[i2].hisHer() + " lungs as " + included[i2].heShe() + "'s gripped by a long, continuous climax.");
                        }
                        final Forsaken forsaken8 = included[i2];
                        forsaken8.timesOrgasmed += 10 + (int)(Math.random() * 10.0);
                    }
                    else if (included[i2].strongestOrgasm >= 200 && ((int)(Math.random() * 2.0) == 0 || included[i2].orgasmsGiven < 1000)) {
                        final Forsaken forsaken9 = included[i2];
                        forsaken9.timesOrgasmed += 4 + (int)(Math.random() * 4.0);
                        if (included[i2].confidence > 66) {
                            w.append(t, String.valueOf(included[i2].mainName) + " decides that " + included[i2].heShe() + " needs a day to relax.  " + included[i2].HeShe() + " spends much of it masturbating.");
                        }
                        else if (included[i2].confidence > 33) {
                            w.append(t, String.valueOf(included[i2].mainName) + " tries to manage " + included[i2].hisHer() + " lust by spending some time masturbating.  " + included[i2].HeShe() + " ends up doing it for most of the day.");
                        }
                        else {
                            w.append(t, "Overcome by the Demonic influence in the air, " + included[i2].mainName + " hides in " + included[i2].hisHer() + " room and starts to quietly masturbate, jumping in alarm whenever " + included[i2].heShe() + " hears movement outside.");
                        }
                    }
                    else if (included[i2].orgasmsGiven >= 1000) {
                        if (included[i2].timesOrgasmed > 0) {
                            final Forsaken forsaken10 = included[i2];
                            forsaken10.timesOrgasmed += 2 + (int)(Math.random() * 2.0);
                        }
                        if (included[i2].innocence > 66) {
                            w.append(t, String.valueOf(included[i2].mainName) + " reads pornographic comics all day, marvelling at what " + included[i2].heShe() + " sees.");
                        }
                        else if (included[i2].innocence > 33) {
                            w.append(t, String.valueOf(included[i2].mainName) + " spends the day playing pornographic computer games.");
                        }
                        else {
                            w.append(t, String.valueOf(included[i2].mainName) + " spends the day studying and theorizing about methods to more efficiently force an unwilling target to orgasm.");
                        }
                    }
                    else if (included[i2].innocence > 66) {
                        w.append(t, String.valueOf(included[i2].mainName) + " plays video games all day, forgetting for awhile where " + included[i2].heShe() + " is.");
                    }
                    else if (included[i2].innocence > 33) {
                        w.append(t, String.valueOf(included[i2].mainName) + " relaxes and spends " + included[i2].hisHer() + " evening watching DVDs smuggled in from the outside world.");
                    }
                    else {
                        w.append(t, String.valueOf(included[i2].mainName) + " spends most of the day reading scholarly articles on psychography.");
                    }
                }
                else if (flavor == 2) {
                    if (included[i2].drained && (int)(Math.random() * 2.0) == 0) {
                        if (included[i2].confidence > 66) {
                            final Forsaken forsaken11 = included[i2];
                            ++forsaken11.timesHarmedSelf;
                            w.append(t, String.valueOf(included[i2].mainName) + " whips " + included[i2].himHer() + "self until " + included[i2].hisHer() + " back begins to show the marks, stubbornly enduring the pain to remind " + included[i2].himHer() + "self not to oppose you.");
                        }
                        else if (included[i2].confidence > 33) {
                            w.append(t, String.valueOf(included[i2].mainName) + " asks to be drained of what little residual psychic energy remains inside " + included[i2].himHer() + ", submitting " + included[i2].himHer() + "self to you completely.");
                        }
                        else {
                            w.append(t, String.valueOf(included[i2].mainName) + " begs you to punish " + included[i2].himHer() + " for ever daring to oppose you, and after you use a spare Demonic body to lightly moleset " + included[i2].himHer() + ", " + included[i2].heShe() + " seems grateful and satisfied.");
                        }
                    }
                    else if (included[i2].timesHarmedSelf > 0 && (int)(Math.random() * 2.0) == 0) {
                        w.append(t, String.valueOf(included[i2].mainName) + " isolates " + included[i2].himHer() + "self and spends the day in silent contemplation of your greatness, ");
                        if (included[i2].innocence > 66) {
                            w.append(t, "though it doesn't amount to much more than mentally repeating 'The Demon Lord is Really Strong' over and over again.");
                        }
                        else if (included[i2].innocence > 33) {
                            w.append(t, "reminding " + included[i2].himHer() + "self that your will is absolute.");
                        }
                        else {
                            w.append(t, "attempting to understand the true nature of a Demon Lord.");
                        }
                    }
                    else if (included[i2].timesTortured > 0 && ((int)(Math.random() * 2.0) == 0 || !included[i2].meek)) {
                        if (included[i2].confidence > 66) {
                            w.append(t, String.valueOf(included[i2].mainName) + " humbles " + included[i2].himHer() + "self by doing manual labor alongside your lesser minions in an attempt to show you " + included[i2].hisHer() + " willingness to serve.");
                        }
                        else if (included[i2].confidence > 33) {
                            w.append(t, String.valueOf(included[i2].mainName) + " keeps " + included[i2].himHer() + "self busy by doing manual labor with the Thralls at your base of operations, hopeful that you'll notice " + included[i2].hisHer() + " efforts.");
                        }
                        else {
                            w.append(t, String.valueOf(included[i2].mainName) + " presents " + included[i2].himHer() + "self to the Thrall in charge of constructing your base of operations, offering to help out in a show of submission.");
                        }
                    }
                    else if (included[i2].meek) {
                        if (included[i2].confidence > 66) {
                            w.append(t, String.valueOf(included[i2].mainName) + " is suffering from flashbacks to " + included[i2].hisHer() + " past abuses, but " + included[i2].heShe() + " forces " + included[i2].himHer() + "self to go outside and do " + included[i2].hisHer() + " daily routine anyway, and " + included[i2].heShe() + " feels satisfied about it once " + included[i2].heShe() + " returns to " + included[i2].hisHer() + " room for the night.");
                        }
                        else if (included[i2].confidence > 33) {
                            w.append(t, String.valueOf(included[i2].mainName) + " feels worried about going outside, so " + included[i2].heShe() + " just spends the day in " + included[i2].hisHer() + " room.");
                        }
                        else {
                            w.append(t, String.valueOf(included[i2].mainName) + " locks " + included[i2].himHer() + "self in " + included[i2].hisHer() + " room, resting there until " + included[i2].heShe() + " can overcome " + included[i2].hisHer() + " old fears of being abused by the Thralls.");
                        }
                    }
                    else if (included[i2].confidence > 66) {
                        w.append(t, String.valueOf(included[i2].mainName) + " has a good day, and " + included[i2].heShe() + " goes to bed in high spirits.");
                    }
                    else if (included[i2].confidence > 33) {
                        w.append(t, String.valueOf(included[i2].mainName) + " spends a leisurely day doing nothing in particular.");
                    }
                    else {
                        w.append(t, String.valueOf(included[i2].mainName) + " lifts weights in " + included[i2].hisHer() + " room all day, desperate to become stronger.");
                    }
                }
                else if (included[i2].parasitized && (int)(Math.random() * 2.0) == 0) {
                    w.append(t, String.valueOf(included[i2].mainName) + " spends the day with what's left of " + included[i2].hisHer() + " fans, ");
                    if (included[i2].innocence > 66) {
                        w.append(t, "not really even noticing that there are far fewer than before.");
                    }
                    else if (included[i2].innocence > 33) {
                        w.append(t, "and even though there clearly aren't as many as before, " + included[i2].heShe() + " still enjoys " + included[i2].himHer() + "self.");
                    }
                    else {
                        w.append(t, "but " + included[i2].heShe() + " can't help but dwell on the fact that most of them have moved on to newer Chosen and Forsaken.");
                    }
                    final Forsaken forsaken12 = included[i2];
                    forsaken12.timesExposed += 10 + (int)(Math.random() * 10.0);
                    final Forsaken forsaken13 = included[i2];
                    forsaken13.timesExposedSelf += 10 + (int)(Math.random() * 10.0);
                }
                else if (included[i2].timesExposedSelf > 100 && (int)(Math.random() * 2.0) == 0) {
                    w.append(t, String.valueOf(included[i2].mainName) + " goes outside in the nude");
                    if (included[i2].dignity > 66) {
                        w.append(t, ", greatly enjoying the extra attention it gets " + included[i2].himHer() + ".");
                    }
                    else if (included[i2].dignity > 33) {
                        w.append(t, ", letting a few of your minions catch glimpses of " + included[i2].himHer() + " before returning home.");
                    }
                    else {
                        w.append(t, " as if it isn't any big deal.");
                    }
                    final Forsaken forsaken14 = included[i2];
                    ++forsaken14.timesExposed;
                    final Forsaken forsaken15 = included[i2];
                    ++forsaken15.timesExposedSelf;
                }
                else if ((included[i2].timesExposed > 100000 && (int)(Math.random() * 2.0) == 0) || !included[i2].debased) {
                    w.append(t, String.valueOf(included[i2].mainName) + " goes outside in ");
                    if (included[i2].dignity > 66) {
                        w.append(t, "a dress that's practically transparent, not quite showing the details of " + included[i2].hisHer() + " private parts, but leaving very little to the imagination.");
                    }
                    else if (included[i2].dignity > 33) {
                        w.append(t, "a long shirt with nothing underneath, teasing your minions with the promise of catching a glimpse of " + included[i2].hisHer() + " most intimate places.");
                    }
                    else {
                        w.append(t, "a tiny miniskirt with no panties, and " + included[i2].heShe() + " makes no effort whatsoever to avoid flashing people whenever " + included[i2].heShe() + " stretches or bends over.");
                    }
                }
                else if (included[i2].debased) {
                    w.append(t, "During " + included[i2].hisHer() + " daily routine, " + included[i2].mainName + " is confronted by a Thrall with a recording of " + included[i2].himHer() + " being humiliated, ");
                    if (included[i2].dignity > 66) {
                        w.append(t, "but " + included[i2].mainName + " is pleasantly surprised to see that the Thrall is just an enthusiastic fan.");
                    }
                    else if (included[i2].dignity > 33) {
                        w.append(t, "but " + included[i2].mainName + " doesn't let it get to " + included[i2].himHer() + ".");
                    }
                    else {
                        w.append(t, "but " + included[i2].mainName + " is past the point of caring, and " + included[i2].heShe() + " doesn't let it ruin " + included[i2].hisHer() + " day.");
                    }
                }
                else {
                    w.append(t, String.valueOf(included[i2].mainName) + " spends the day talking to a gathering of " + included[i2].hisHer() + " fans, ");
                    if (included[i2].confidence > 66) {
                        w.append(t, "happily regaling them with stories of " + included[i2].hisHer() + " time as one of the Chosen.");
                    }
                    else if (included[i2].confidence > 33) {
                        w.append(t, "chatting about what life is like under the Demon Lord.");
                    }
                    else {
                        w.append(t, "blushing and stammering when " + included[i2].heShe() + " hears how much they still love " + included[i2].himHer() + ".");
                    }
                }
                w.append(t, "  (+" + included[i2].staminaRegen() / 10 + "." + included[i2].staminaRegen() % 10 + "% Stamina");
                if (tantruming != null) {
                    int lost = damages[1];
                    if (tantruming.opinion(included[i2]) > 100) {
                        lost = damages[0];
                    }
                    else if (tantruming.opinion(included[i2]) < -100) {
                        lost = damages[2];
                    }
                    w.append(t, ", ");
                    if (included[i2].motivation / 10 < included[i2].hostility) {
                        w.redAppend(t, "-" + lost / 10 + "." + lost % 10 + "% Motivation");
                    }
                    else {
                        w.append(t, "-" + lost / 10 + "." + lost % 10 + "% Motivation");
                    }
                }
                w.append(t, ")");
            }
        }
        if (exhausted != null && (tantruming != null || included.length == 0)) {
            for (int i2 = 0; i2 < exhausted.length; ++i2) {
                if (tantruming != null) {
                    w.append(t, "\n\n" + exhausted[i2].mainName + " finds it difficult to rest due to " + tantruming.mainName + "'s disturbance.  (");
                    int lost2 = damages[1];
                    if (tantruming.opinion(exhausted[i2]) > 100) {
                        lost2 = damages[0];
                    }
                    else if (tantruming.opinion(exhausted[i2]) < -100) {
                        lost2 = damages[2];
                    }
                    if (exhausted[i2].motivation / 10 < exhausted[i2].hostility) {
                        w.redAppend(t, "-" + lost2 / 10 + "." + lost2 % 10 + "% Motivation");
                    }
                    else {
                        w.append(t, "-" + lost2 / 10 + "." + lost2 % 10 + "% Motivation");
                    }
                    w.append(t, ")");
                }
                else {
                    if (i2 != 0) {
                        w.append(t, "\n\n");
                    }
                    w.append(t, String.valueOf(exhausted[i2].mainName) + " is tired due to the day's activities.");
                }
            }
        }
        for (int i2 = 0; i2 < included.length; ++i2) {
            final Forsaken forsaken16 = included[i2];
            forsaken16.stamina += included[i2].staminaRegen();
            if (included[i2].stamina > 1000) {
                included[i2].stamina = 1000;
            }
        }
        if (tantruming != null && w.getHarem().length > 1) {
            tantruming.motivation = 1000;
        }
        final WriteObject wobj = new WriteObject();
        wobj.serializeSaveData(s);
        w.trainedForsaken = null;
        if (w.active) {
            p.removeAll();
            final JButton Continue = new JButton("Continue");
            Continue.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    w.usedForsaken = null;
                    Project.Shop(t, p, f, w);
                }
            });
            p.add(Continue);
            p.validate();
            p.repaint();
        }
    }
    
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Project();
            }
        });
    }
    
    public enum Emotion
    {
        ANGER("ANGER", 0), 
        FEAR("FEAR", 1), 
        FOCUS("FOCUS", 2), 
        JOY("JOY", 3), 
        LEWD("LEWD", 4), 
        NEUTRAL("NEUTRAL", 5), 
        SHAME("SHAME", 6), 
        STRUGGLE("STRUGGLE", 7), 
        SWOON("SWOON", 8);
        
        private Emotion(final String s, final int n) {
        }
    }
}
