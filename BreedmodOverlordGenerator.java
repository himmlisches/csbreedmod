
public class BreedmodOverlordGenerator {

    //variables here are set by player input
    Boolean keepChosenBody;
    BreedmodVariables overlordBreedmodVariables;

    Boolean overlordIsGaijin;
    String overlordGivenName;
    String overlordFamilyName;
    Gender overlordOriginalGender;
    Gender overlordGender;
    String overlordIncantation;
    String overlordAdjectiveName;
    String overlordNounName;
    String overlordMainName;
    String overlordOriginalName;
    String overlordTopCover;
    String overlordTopAccess;
    String overlordBottomCover;
    String overlordBottomAccess;
    String overlordUnderType;
    String overlordColor;
    String overlordAccessory;
    String overlordWeapon;
    String overlordCustomWeaponType;
    String overlordFeetType;
    Boolean overlordIsRuthless;
    Boolean overlordIsLustful;

    

    public Forsaken generateOverlord (Forsaken sacrifice) {

        Forsaken overlord = new Forsaken();

        overlord.textSize = sacrifice.textSize;
        overlord.givenName = this.overlordGivenName;
        overlord.familyName = this.overlordFamilyName;
        overlord.filthyGaijin = this.overlordIsGaijin;
        overlord.textColor = sacrifice.textColor;
        overlord.darkColor = sacrifice.textColor;
        overlord.originalGender = this.overlordOriginalGender;
        overlord.gender = this.overlordGender;
        overlord.incantation = this.overlordIncantation;
        overlord.adjectiveName = this.overlordAdjectiveName;
        overlord.nounName = this.overlordNounName;
        overlord.mainName = this.overlordMainName;
        overlord.originalName = this.overlordOriginalName;
        overlord.topCover = this.overlordTopCover;
        overlord.topAccess = this.overlordTopAccess;
        overlord.bottomCover = this.overlordBottomCover;
        overlord.bottomAccess = this.overlordBottomAccess;
        overlord.underType = this.overlordUnderType;
        overlord.color = this.overlordColor;
        overlord.accessory = this.overlordAccessory;
        overlord.weapon = this.overlordWeapon;
        overlord.customWeaponType = this.overlordCustomWeaponType;
        overlord.feetType = this.overlordFeetType;
        
        int number; //number of killed chosen
        Chosen[] kills;

        overlord.morality = 0; //how guilty they feel
        overlord.innocence = 0;  //perversity
        overlord.confidence = 100; //devotion? I think?
        overlord.dignity = 0; //amount they attack other forsaken?
        overlord.hostility = this.hostility;  //needless pain caused
        overlord.deviancy = this.deviancy;   //how kinky you are
        overlord.obedience = 100; //to your will
        overlord.disgrace = 0; //humility
        int stamina = 2000;
        int motivation = 10000;


        overlord.takers = {Taker.NONE, Taker.NONE, Taker.NONE, Taker.NONE};
        overlord.takerIDs = new int[4];

        int[] killRelationships;
        int defeatType;
        Chosen formerSelf;
        int[][] formerRelationships;
        Forsaken firstPartner;
        Forsaken secondPartner;
        Chosen firstFormerPartner = 0;
        Chosen secondFormerPartner = 0;
        int firstOriginalRelationship;
        int secondOriginalRelationship;
        Forsaken[] others;
        Chosen[] otherChosen;
        int[] troublemaker;
        Relationship[] forsakenRelations;
        Relationship[] chosenRelations;
        int forsakenID;
        Chosen[] formerPartners;
        int[] formerFriendships;
        long hateExp;
        long pleaExp;
        long injuExp;
        long expoExp;
        int combatStyle;
        int injured;


        overlord.ruthless = this.overlordIsRuthless;
        overlord.lustful = this.overlordIsLustful;

        overlord.peopleInjured = 0;
        overlord.timesHadSex = 0;
        overlord.timesKilled = 0;
        overlord.demonicBirths = 0;
        overlord.orgasmsGiven = 0;
        overlord.timesOrgasmed = 0;
        overlord.strongestOrgasm = 0;
        overlord.timesTortured = 0;
        overlord.timesHarmedSelf = 0;
        overlord.timesExposed = 0;
        overlord.timesExposedSelf = 0;
        overlord.enjoyedAnal = 0;
        overlord.hypnotized = false;
        overlord.meek = false;
        overlord.drained = false;
        overlord.debased = false;
        overlord.parasitized = false;



        return overlord;

    }






}