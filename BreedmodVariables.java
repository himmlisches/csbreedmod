
public class BreedmodVariables {
    
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


    public void childBreedmodVars(){



    }
}

