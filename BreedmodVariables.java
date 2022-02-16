
public class BreedmodVariables {

    //defaults to 0, must be set during creation of chosen.
    int isSuperior;
    int isHighVariance;

    int analSensitivity;
    int analCapacity;
    int analTightness;
    int analLubrication;
    int analHoldsInCum;     //creampies stay in

    int vagSensitivity;
    int vagCapacity;
    int vagTightness;
    int vagLubrication;
    int vagHoldsInCum;      //creampies stay in

    int wombCapacity;
    int cervixSensitivity;
    int cervixPenetration;  //can the cervix be penetrated
    int girthQueen;         //at high level, cant get off on small dicks
    int lengthQueen;
    int ovulationFrequency;
    int ovulationQuantity;
    int pregnancyCraving;    //how much they want to get knocked up
    int pregnancyRate;
    int goesIntoHeat;
    int pheremones;

    int hipSize;
    int hipShape;           //Thighgap and Pelvic Girdle
    int buttSize;
    int buttShape;

    int boobSize;
    int boobShape;           //Athletic(Uniboob), Bell, Droopy, Round, Teardrop, Wideset
    int boobSensitivity;
    int milkProduction;
    int milkType;

    int gagReflex;
    int throatSensitivity;

    int cumFlavorDesire;    //loves/hates flavour of cum
    int cumDependency;      //gets high on cum

    int facialBeauty;
    int facialArchetype;    //Normal, Sensual, Cute, Exotic, Androgynous
    int height;
    int waistWidth;
    int musculature;
    int fatPercentage;
    int skinColor;
    int flexibility;

    int hairColor;
    int hairLength;         //not exactly genetic, but eh
    int growsHairBelowNeck;

    int intelligence;
    int ego;
    int submissiveness;
    int naturalMasochism;
    int libido;
    int incestResistance;

    //Recessive for girls
    int cumProduction;
    int cumPotency;
    int cumTaste;
    int cumIntoxication;
    int cumKiller;  //cum kills competitor sperm
    int dickLength;
    int dickGirth;
    int testicleSize;


    public void instantiateBreedmodVars(){
        BreedmodChosenGen breedmodVariables = new BreedmodChosenGen();

        this.isSuperior = 0;
        this.isHighVariance = 0;

        //(skew of distribution, max of value, min of value)
        //Many of these should be tied together, but this is fine for now
       this.analSensitivity = breedmodVariables.initializeGeneticsValue(-10, 30, 1);
       this.analCapacity = breedmodVariables.initializeGeneticsValue(0, 40, 1);
       this.analTightness = breedmodVariables.initializeGeneticsValue(30, 50, 20);
       this.analLubrication = breedmodVariables.initializeGeneticsValue(0, 10, 1);
       this.analHoldsInCum = breedmodVariables.initializeGeneticsValue(20, 35, 1);

       this.vagSensitivity = breedmodVariables.initializeGeneticsValue(5, 50, 1);
       this.vagCapacity = breedmodVariables.initializeGeneticsValue(25, 35, 15);
       this.vagTightness = breedmodVariables.initializeGeneticsValue(10, 40, 10);
       this.vagLubrication = breedmodVariables.initializeGeneticsValue(10, 40, 1);
       this.vagHoldsInCum = breedmodVariables.initializeGeneticsValue(0, 20, 1);

       this.wombCapacity = breedmodVariables.initializeGeneticsValue(0, 20, 1);
       this.cervixSensitivity = breedmodVariables.initializeGeneticsValue(-20, 20, 1);
       this.cervixPenetration = breedmodVariables.initializeGeneticsValue(-20, 10, 1);
       this.girthQueen = breedmodVariables.initializeGeneticsValue(20, 65, 10);
       this.lengthQueen = breedmodVariables.initializeGeneticsValue(10, 40, 1);
       this.ovulationFrequency = breedmodVariables.initializeGeneticsValue(20, 30, 1);
       this.ovulationQuantity = breedmodVariables.initializeGeneticsValue(0, 40, 1);
       this.pregnancyCraving = breedmodVariables.initializeGeneticsValue(0, 40, 1);
       this.pregnancyRate = breedmodVariables.initializeGeneticsValue(20, 30, 10);
       this.goesIntoHeat = breedmodVariables.initializeGeneticsValue(0, 30, 1);
       this.pheremones = breedmodVariables.initializeGeneticsValue(0, 20, 1);

       this.hipSize = breedmodVariables.initializeGeneticsValue(30, 60, 10);

       this.buttSize = breedmodVariables.initializeGeneticsValue(30, 60, 10);


       this.boobSize = breedmodVariables.initializeGeneticsValue(20, 50, 1);
       this.boobShape = breedmodVariables.initializeGeneticsValue(20, 50, 1);
       this.boobSensitivity = breedmodVariables.initializeGeneticsValue(10, 40, 10);
       this.milkProduction = breedmodVariables.initializeGeneticsValue(0, 20, 1);


       this.gagReflex = breedmodVariables.initializeGeneticsValue(20, 60, 1);
       this.throatSensitivity = breedmodVariables.initializeGeneticsValue(0, 10, 1);

       this.cumFlavorDesire = breedmodVariables.initializeGeneticsValue(0, 20, 1);
       this.cumDependency = breedmodVariables.initializeGeneticsValue(0, 10, 1);

       this.facialBeauty = breedmodVariables.initializeGeneticsValue(30, 70, 1);

       this.height = breedmodVariables.initializeGeneticsValue(50, 70, 30);
       this.waistWidth = breedmodVariables.initializeGeneticsValue(50, 60, 40);
       this.musculature = breedmodVariables.initializeGeneticsValue(50, 70, 20);
       this.fatPercentage = breedmodVariables.initializeGeneticsValue(40, 65, 20);
       this.skinColor = breedmodVariables.initializeGeneticsValue(20, 40, 1);
       this.flexibility = breedmodVariables.initializeGeneticsValue(40, 70, 10);

       this.hairColor = breedmodVariables.initializeGeneticsValue(10, 20, 1);
       this.hairLength = breedmodVariables.initializeGeneticsValue(50, 80, 1);
       this.growsHairBelowNeck = breedmodVariables.initializeGeneticsValue(0, 100, 1);

       this.intelligence = breedmodVariables.initializeGeneticsValue(40, 60, 30);
       this.ego = breedmodVariables.initializeGeneticsValue(50, 100, 20);
       this.submissiveness = breedmodVariables.initializeGeneticsValue(20, 30, 1);
       this.naturalMasochism = breedmodVariables.initializeGeneticsValue(0, 40, 1);
       this.libido = breedmodVariables.initializeGeneticsValue(30, 40, 1);

       this.cumProduction = breedmodVariables.initializeGeneticsValue(0, 20, 1);
       this.cumPotency = breedmodVariables.initializeGeneticsValue(0, 30, 1);
       this.cumTaste = breedmodVariables.initializeGeneticsValue(0, 40, 1);
       this.cumIntoxication = breedmodVariables.initializeGeneticsValue(0, 10, 1);
       this.cumKiller = breedmodVariables.initializeGeneticsValue(0, 10, 1);
       this.dickLength = breedmodVariables.initializeGeneticsValue(10, 40, 1);
       this.dickGirth = breedmodVariables.initializeGeneticsValue(10, 40, 1);
       this.testicleSize = breedmodVariables.initializeGeneticsValue(10, 30, 1);

       this.incestResistance = breedmodVariables.initializeGeneticsValue(1, 30, 1);


       //TYPE INSTANTIATIONS, NOT SURE HOW TO HANDLE THESE
       this.facialArchetype = breedmodVariables.initializeGeneticsValue(50, 100, 1); //for now, 1-20 "androgenous" 21-40 "normal" 41-60 "exotic" 61-80 "cute" 81-100 "sexy"
       //this.milkType = breedmodVariables.initializeGeneticsValue(0, 10, 1); save for future version, not relevant for long time.
       this.buttShape = breedmodVariables.initializeGeneticsValue(30, 75, 10);  //for now, over 60 is a "bubblebutt" flag
       this.hipShape = breedmodVariables.initializeGeneticsValue(30, 60, 10);

       //BREEDMOD VARIABLE INSTANTIATION END

    }


    public void childBreedmodVars(BreedmodVariables fatherGenetics, BreedmodVariables motherGenetics){

    BreedmodChosenGen newGeneticsRoller = new BreedmodChosenGen();
    newGeneticsRoller.fatherIsHighVariance = fatherGenetics.isHighVariance;
    newGeneticsRoller.motherIsHighVariance = motherGenetics.isHighVariance;
    newGeneticsRoller.motherIsSuperior = motherGenetics.isSuperior;
    newGeneticsRoller.fatherIsSuperior = fatherGenetics.isHighVariance;
    if (motherGenetics.ovulationFrequency > 85) {

    }

    this.analSensitivity = newGeneticsRoller.childGeneticsValue(fatherGenetics.analSensitivity, motherGenetics.analSensitivity);
    this.analCapacity = newGeneticsRoller.childGeneticsValue(fatherGenetics.analCapacity, motherGenetics.analCapacity);
    this.analTightness = newGeneticsRoller.childGeneticsValue(fatherGenetics.analTightness, motherGenetics.analTightness);
    this.analLubrication = newGeneticsRoller.childGeneticsValue(fatherGenetics.analLubrication, motherGenetics.analLubrication);
    this.analHoldsInCum = newGeneticsRoller.childGeneticsValue(fatherGenetics.analHoldsInCum, motherGenetics.analHoldsInCum);
    this.vagSensitivity = newGeneticsRoller.childGeneticsValue(fatherGenetics.vagSensitivity, motherGenetics.vagSensitivity);
    this.vagCapacity = newGeneticsRoller.childGeneticsValue(fatherGenetics.vagCapacity, motherGenetics.vagCapacity);
    this.vagTightness = newGeneticsRoller.childGeneticsValue(fatherGenetics.vagTightness, motherGenetics.vagTightness);
    this.vagLubrication = newGeneticsRoller.childGeneticsValue(fatherGenetics.vagLubrication, motherGenetics.vagLubrication);
    this.vagHoldsInCum = newGeneticsRoller.childGeneticsValue(fatherGenetics.vagHoldsInCum, motherGenetics.vagHoldsInCum);
    this.wombCapacity = newGeneticsRoller.childGeneticsValue(fatherGenetics.wombCapacity, motherGenetics.wombCapacity);
    this.cervixSensitivity = newGeneticsRoller.childGeneticsValue(fatherGenetics.cervixSensitivity, motherGenetics.cervixSensitivity);
    this.cervixPenetration = newGeneticsRoller.childGeneticsValue(fatherGenetics.cervixPenetration, motherGenetics.cervixPenetration);
    this.girthQueen = newGeneticsRoller.childGeneticsValue(fatherGenetics.girthQueen, motherGenetics.girthQueen);
    this.lengthQueen = newGeneticsRoller.childGeneticsValue(fatherGenetics.lengthQueen, motherGenetics.lengthQueen);
    this.ovulationFrequency = newGeneticsRoller.childGeneticsValue(fatherGenetics.ovulationFrequency, motherGenetics.ovulationFrequency);
    this.ovulationQuantity = newGeneticsRoller.childGeneticsValue(fatherGenetics.ovulationQuantity, motherGenetics.ovulationQuantity);
    this.pregnancyCraving = newGeneticsRoller.childGeneticsValue(fatherGenetics.pregnancyCraving, motherGenetics.pregnancyCraving);
    this.pregnancyRate = newGeneticsRoller.childGeneticsValue(fatherGenetics.pregnancyRate, motherGenetics.pregnancyRate);
    this.goesIntoHeat = newGeneticsRoller.childGeneticsValue(fatherGenetics.goesIntoHeat, motherGenetics.goesIntoHeat);
    this.pheremones = newGeneticsRoller.childGeneticsValue(fatherGenetics.pheremones, motherGenetics.pheremones);
    this.hipSize = newGeneticsRoller.childGeneticsValue(fatherGenetics.hipSize, motherGenetics.hipSize);
    this.hipShape = newGeneticsRoller.childGeneticsValue(fatherGenetics.hipShape, motherGenetics.hipShape);
    this.buttSize = newGeneticsRoller.childGeneticsValue(fatherGenetics.buttSize, motherGenetics.buttSize);
    this.buttShape = newGeneticsRoller.childGeneticsValue(fatherGenetics.buttShape, motherGenetics.buttShape);
    this.boobSize = newGeneticsRoller.childGeneticsValue(fatherGenetics.boobSize, motherGenetics.boobSize);
    this.boobShape = newGeneticsRoller.childGeneticsValue(fatherGenetics.boobShape, motherGenetics.boobShape);
    this.boobSensitivity = newGeneticsRoller.childGeneticsValue(fatherGenetics.boobSensitivity, motherGenetics.boobSensitivity);
    this.milkProduction = newGeneticsRoller.childGeneticsValue(fatherGenetics.milkProduction, motherGenetics.milkProduction);
    this.milkType = newGeneticsRoller.childGeneticsValue(fatherGenetics.milkType, motherGenetics.milkType);
    this.gagReflex = newGeneticsRoller.childGeneticsValue(fatherGenetics.gagReflex, motherGenetics.gagReflex);
    this.throatSensitivity = newGeneticsRoller.childGeneticsValue(fatherGenetics.throatSensitivity, motherGenetics.throatSensitivity);
    this.cumFlavorDesire = newGeneticsRoller.childGeneticsValue(fatherGenetics.cumFlavorDesire, motherGenetics.cumFlavorDesire);
    this.cumDependency = newGeneticsRoller.childGeneticsValue(fatherGenetics.cumDependency, motherGenetics.cumDependency);
    this.facialBeauty = newGeneticsRoller.childGeneticsValue(fatherGenetics.facialBeauty, motherGenetics.facialBeauty);
    this.facialArchetype = newGeneticsRoller.childGeneticsValue(fatherGenetics.facialArchetype, motherGenetics.facialArchetype);
    this.height = newGeneticsRoller.childGeneticsValue(fatherGenetics.height, motherGenetics.height);
    this.waistWidth = newGeneticsRoller.childGeneticsValue(fatherGenetics.waistWidth, motherGenetics.waistWidth);
    this.musculature = newGeneticsRoller.childGeneticsValue(fatherGenetics.musculature, motherGenetics.musculature);
    this.fatPercentage = newGeneticsRoller.childGeneticsValue(fatherGenetics.fatPercentage, motherGenetics.fatPercentage);
    this.skinColor = newGeneticsRoller.childGeneticsValue(fatherGenetics.skinColor, motherGenetics.skinColor);
    this.flexibility = newGeneticsRoller.childGeneticsValue(fatherGenetics.flexibility, motherGenetics.flexibility);
    this.hairColor = newGeneticsRoller.childGeneticsValue(fatherGenetics.hairColor, motherGenetics.hairColor);
    this.hairLength = newGeneticsRoller.childGeneticsValue(fatherGenetics.hairLength, motherGenetics.hairLength);
    this.growsHairBelowNeck = newGeneticsRoller.childGeneticsValue(fatherGenetics.growsHairBelowNeck, motherGenetics.growsHairBelowNeck);
    this.intelligence = newGeneticsRoller.childGeneticsValue(fatherGenetics.intelligence, motherGenetics.intelligence);
    this.ego = newGeneticsRoller.childGeneticsValue(fatherGenetics.ego, motherGenetics.ego);
    this.submissiveness = newGeneticsRoller.childGeneticsValue(fatherGenetics.submissiveness, motherGenetics.submissiveness);
    this.naturalMasochism = newGeneticsRoller.childGeneticsValue(fatherGenetics.naturalMasochism, motherGenetics.naturalMasochism);
    this.libido = newGeneticsRoller.childGeneticsValue(fatherGenetics.libido, motherGenetics.libido);
    this.incestResistance = newGeneticsRoller.childGeneticsValue(fatherGenetics.incestResistance, motherGenetics.incestResistance);
    this.cumProduction = newGeneticsRoller.childGeneticsValue(fatherGenetics.cumProduction, motherGenetics.cumProduction);
    this.cumPotency = newGeneticsRoller.childGeneticsValue(fatherGenetics.cumPotency, motherGenetics.cumPotency);
    this.cumTaste = newGeneticsRoller.childGeneticsValue(fatherGenetics.cumTaste, motherGenetics.cumTaste);
    this.cumIntoxication = newGeneticsRoller.childGeneticsValue(fatherGenetics.cumIntoxication, motherGenetics.cumIntoxication);
    this.cumKiller = newGeneticsRoller.childGeneticsValue(fatherGenetics.cumKiller, motherGenetics.cumKiller);
    this.dickLength = newGeneticsRoller.childGeneticsValue(fatherGenetics.dickLength, motherGenetics.dickLength);
    this.dickGirth = newGeneticsRoller.childGeneticsValue(fatherGenetics.dickGirth, motherGenetics.dickGirth);
    this.testicleSize = newGeneticsRoller.childGeneticsValue(fatherGenetics.testicleSize, motherGenetics.testicleSize);


    }

    public String printNameValuePair(){

        String output = "";

        output.concat("analSensitivity is " + String.valueOf(this.analSensitivity) + "\n");
        output.concat("analCapacity is " + String.valueOf(this.analCapacity) + "\n");
        output.concat("analTightness is " + String.valueOf(this.analTightness) + "\n");
        output.concat("analLubrication is " + String.valueOf(this.analLubrication) + "\n");
        output.concat("analHoldsInCum is " + String.valueOf(this.analHoldsInCum) + "\n");
        output.concat("vagSensitivity is " + String.valueOf(this.vagSensitivity) + "\n");
        output.concat("vagCapacity is " + String.valueOf(this.vagCapacity) + "\n");
        output.concat("vagTightness is " + String.valueOf(this.vagTightness) + "\n");
        output.concat("vagLubrication is " + String.valueOf(this.vagLubrication) + "\n");
        output.concat("vagHoldsInCum is " + String.valueOf(this.vagHoldsInCum) + "\n");
        output.concat("wombCapacity is " + String.valueOf(this.wombCapacity) + "\n");
        output.concat("cervixSensitivity is " + String.valueOf(this.cervixSensitivity) + "\n");
        output.concat("cervixPenetration is " + String.valueOf(this.cervixPenetration) + "\n");
        output.concat("girthQueen is " + String.valueOf(this.girthQueen) + "\n");
        output.concat("lengthQueen is " + String.valueOf(this.lengthQueen) + "\n");
        output.concat("ovulationFrequency is " + String.valueOf(this.ovulationFrequency) + "\n");
        output.concat("ovulationQuantity is " + String.valueOf(this.ovulationQuantity) + "\n");
        output.concat("pregnancyCraving is " + String.valueOf(this.pregnancyCraving) + "\n");
        output.concat("pregnancyRate is " + String.valueOf(this.pregnancyRate) + "\n");
        output.concat("goesIntoHeat is " + String.valueOf(this.goesIntoHeat) + "\n");
        output.concat("pheremones is " + String.valueOf(this.pheremones) + "\n");
        output.concat("hipSize is " + String.valueOf(this.hipSize) + "\n");
        output.concat("hipShape is " + String.valueOf(this.hipShape) + "\n");
        output.concat("buttSize is " + String.valueOf(this.buttSize) + "\n");
        output.concat("buttShape is " + String.valueOf(this.buttShape) + "\n");
        output.concat("boobSize is " + String.valueOf(this.boobSize) + "\n");
        output.concat("boobShape is " + String.valueOf(this.boobShape) + "\n");
        output.concat("boobSensitivity is " + String.valueOf(this.boobSensitivity) + "\n");
        output.concat("milkProduction is " + String.valueOf(this.milkProduction) + "\n");
        output.concat("milkType is " + String.valueOf(this.milkType) + "\n");
        output.concat("gagReflex is " + String.valueOf(this.gagReflex) + "\n");
        output.concat("throatSensitivity is " + String.valueOf(this.throatSensitivity) + "\n");
        output.concat("cumFlavorDesire is " + String.valueOf(this.cumFlavorDesire) + "\n");
        output.concat("cumDependency is " + String.valueOf(this.cumDependency) + "\n");
        output.concat("facialBeauty is " + String.valueOf(this.facialBeauty) + "\n");
        output.concat("facialArchetype is " + String.valueOf(this.facialArchetype) + "\n");
        output.concat("height is " + String.valueOf(this.height) + "\n");
        output.concat("waistWidth is " + String.valueOf(this.waistWidth) + "\n");
        output.concat("musculature is " + String.valueOf(this.musculature) + "\n");
        output.concat("fatPercentage is " + String.valueOf(this.fatPercentage) + "\n");
        output.concat("skinColor is " + String.valueOf(this.skinColor) + "\n");
        output.concat("flexibility is " + String.valueOf(this.flexibility) + "\n");
        output.concat("hairColor is " + String.valueOf(this.hairColor) + "\n");
        output.concat("hairLength is " + String.valueOf(this.hairLength) + "\n");
        output.concat("growsHairBelowNeck is " + String.valueOf(this.growsHairBelowNeck) + "\n");
        output.concat("intelligence is " + String.valueOf(this.intelligence) + "\n");
        output.concat("ego is " + String.valueOf(this.ego) + "\n");
        output.concat("submissiveness is " + String.valueOf(this.submissiveness) + "\n");
        output.concat("naturalMasochism is " + String.valueOf(this.naturalMasochism) + "\n");
        output.concat("libido is " + String.valueOf(this.libido) + "\n");
        output.concat("incestResistance is " + String.valueOf(this.incestResistance) + "\n");
        output.concat("cumProduction is " + String.valueOf(this.cumProduction) + "\n");
        output.concat("cumPotency is " + String.valueOf(this.cumPotency) + "\n");
        output.concat("cumTaste is " + String.valueOf(this.cumTaste) + "\n");
        output.concat("cumIntoxication is " + String.valueOf(this.cumIntoxication) + "\n");
        output.concat("cumKiller is " + String.valueOf(this.cumKiller) + "\n");
        output.concat("dickLength is " + String.valueOf(this.dickLength) + "\n");
        output.concat("dickGirth is " + String.valueOf(this.dickGirth) + "\n");
        output.concat("testicleSize is " + String.valueOf(this.testicleSize) + "\n");

        return output;
    }
}

