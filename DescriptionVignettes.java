import java.io.*;
import java.util.*;


public class DescriptionVignettes {
    /**
     * VIGNETTES ARE LOADED ORDER DEPENDENT, ASSUMING ALL 
     * NON-BASE TEN SEVERITY DESCRIPTORS ARE MANUALLY LOADED INTO THE STRING 
     * ARRAY BEFORE ALL BASE TEN SEVERITIES
     * 
     * VIGNETTES ARE LOADED FROM vignettes.csv
     * 
     * VIGNETTE ORDER IN vignettes.csv MUST MATCH ORDER LISTED 
     * IN instantiateCharacteristicDescriptions(), 
     * OR THE DESCRIPTORS WILL NOT MATCH THEIR DESIGNATED ATTRIBUTE
     * 
     */
     
    
    //HASHMAP TO FIND WHICH POSITION IN THE DESCRIPTOR ARRAY
    //A CHARACTERISTICS CAN BE FOUND
    //get("boobshapedescriptions") should return "0"
     HashMap<String, Integer> characteristicsIndex;

    ArrayList<ArrayList<ArrayList<String>>> characteristicDescriptions;

    ArrayList<ArrayList<String>> analSensitivityDescriptions;
    ArrayList<ArrayList<String>> analCapacityDescriptions;
    ArrayList<ArrayList<String>> analTightnessDescriptions;
    ArrayList<ArrayList<String>> analLubricationDescriptions;
    ArrayList<ArrayList<String>> analHoldsInCumDescriptions;
    ArrayList<ArrayList<String>> vagSensitivityDescriptions; ArrayList<ArrayList<String>> vagCapacityDescriptions;
    ArrayList<ArrayList<String>> vagTightnessDescriptions;
    ArrayList<ArrayList<String>> vagLubricationDescriptions;
    ArrayList<ArrayList<String>> vagHoldsInCumDescriptions;
    ArrayList<ArrayList<String>> wombCapacityDescriptions;
    ArrayList<ArrayList<String>> cervixSensitivityDescriptions;
    ArrayList<ArrayList<String>> cervixPenetrationDescriptions;
    ArrayList<ArrayList<String>> girthQueenDescriptions;
    ArrayList<ArrayList<String>> lengthQueenDescriptions;
    ArrayList<ArrayList<String>> ovulationFrequencyDescriptions;
    ArrayList<ArrayList<String>> ovulationQuantityDescriptions;
    ArrayList<ArrayList<String>> pregnancyCravingDescriptions;
    ArrayList<ArrayList<String>> pregnancyRateDescriptions;
    ArrayList<ArrayList<String>> goesIntoHeatDescriptions;
    ArrayList<ArrayList<String>> pheremonesDescriptions;
    ArrayList<ArrayList<String>> incestResistanceDescriptions;
    ArrayList<ArrayList<String>> hipSizeDescriptions;
    ArrayList<ArrayList<String>> hipShapeDescriptions;
    ArrayList<ArrayList<String>> buttSizeDescriptions;
    ArrayList<ArrayList<String>> buttShapeDescriptions;
    ArrayList<ArrayList<String>> boobSizeDescriptions;
    ArrayList<ArrayList<String>> boobShapeDescriptions;
    ArrayList<ArrayList<String>> boobSensitivityDescriptions;
    ArrayList<ArrayList<String>> milkProductionDescriptions;
    ArrayList<ArrayList<String>> milkTypeDescriptions;
    ArrayList<ArrayList<String>> gagReflexDescriptions;
    ArrayList<ArrayList<String>> throatSensitivityDescriptions;
    ArrayList<ArrayList<String>> cumFlavorDesireDescriptions;
    ArrayList<ArrayList<String>> cumDependencyDescriptions;
    ArrayList<ArrayList<String>> facialBeautyDescriptions;
    ArrayList<ArrayList<String>> facialArchetypeDescriptions;
    ArrayList<ArrayList<String>> heightDescriptions;
    ArrayList<ArrayList<String>> waistWidthDescriptions;
    ArrayList<ArrayList<String>> musculatureDescriptions;
    ArrayList<ArrayList<String>> fatPercentageDescriptions;
    ArrayList<ArrayList<String>> skinColorDescriptions;
    ArrayList<ArrayList<String>> flexibilityDescriptions;
    ArrayList<ArrayList<String>> hairColorDescriptions;
    ArrayList<ArrayList<String>> hairLengthDescriptions;
    ArrayList<ArrayList<String>> growsHairBelowNeckDescriptions;
    ArrayList<ArrayList<String>> intelligenceDescriptions;
    ArrayList<ArrayList<String>> egoDescriptions;
    ArrayList<ArrayList<String>> submissivenessDescriptions;
    ArrayList<ArrayList<String>> naturalMasochismDescriptions;
    ArrayList<ArrayList<String>> libidoDescriptions;
    ArrayList<ArrayList<String>> cumProductionDescriptions;
    ArrayList<ArrayList<String>> cumPotencyDescriptions;
    ArrayList<ArrayList<String>> cumTasteDescriptions;
    ArrayList<ArrayList<String>> cumIntoxicationDescriptions;
    ArrayList<ArrayList<String>> cumKillerDescriptions;
    ArrayList<ArrayList<String>> dickLengthDescriptions;
    ArrayList<ArrayList<String>> dickGirthDescriptions;
    ArrayList<ArrayList<String>> testicleSizeDescriptions;
    
    public void instantiateCharacteristicDescriptions() throws FileNotFoundException{
        
       

        ArrayList<String> characteristicNames = new ArrayList<String>();
        characteristicNames.add("boobShape");
        characteristicNames.add("facialArchetype");
        characteristicNames.add("hairColor");
        characteristicNames.add("growsHairBelowNeck");
        characteristicNames.add("analSensitivity");
        characteristicNames.add("analCapacity");
        characteristicNames.add("analTightness");
        characteristicNames.add("analLubrications");
        characteristicNames.add("analHoldsInCum");
        characteristicNames.add("vagSensitivity");
        characteristicNames.add("vagCapacity");
        characteristicNames.add("vagTightness");
        characteristicNames.add("vagLubrication");
        characteristicNames.add("vagHoldsInCum");
        characteristicNames.add("wombCapacity");
        characteristicNames.add("cervixSensitivity");
        characteristicNames.add("cervixPenetration");
        characteristicNames.add("girthQueen");
        characteristicNames.add("lengthQueen");
        characteristicNames.add("ovulationFrequency");
        characteristicNames.add("ovulationQuantity");
        characteristicNames.add("pregnancyCraving");
        characteristicNames.add("pregnancyRate");
        characteristicNames.add("goesIntoHeat");
        characteristicNames.add("pheremones");
        characteristicNames.add("hipSize");
        characteristicNames.add("hipShape");
        characteristicNames.add("buttSize");
        characteristicNames.add("buttShape");
        characteristicNames.add("boobSize");
        characteristicNames.add("boobSensitivity");
        characteristicNames.add("milkProduction");
        characteristicNames.add("milkType");
        characteristicNames.add("gagReflex");
        characteristicNames.add("throatSensitivity");
        characteristicNames.add("cumFlavorDesire");
        characteristicNames.add("cumDependency");
        characteristicNames.add("facialBeauty");
        characteristicNames.add("height");
        characteristicNames.add("waistWidth");
        characteristicNames.add("musculature");
        characteristicNames.add("fatPercentage");
        characteristicNames.add("skinColor");
        characteristicNames.add("flexibility");
        characteristicNames.add("hairLength");
        characteristicNames.add("intelligence");
        characteristicNames.add("ego");
        characteristicNames.add("submissiveness");
        characteristicNames.add("naturalMasochism");
        characteristicNames.add("libido");
        characteristicNames.add("incestResistance");
        characteristicNames.add("cumProduction");
        characteristicNames.add("cumPotency");
        characteristicNames.add("cumTaste");
        characteristicNames.add("cumIntoxication");
        characteristicNames.add("cumKiller");
        characteristicNames.add("dickLength");
        characteristicNames.add("dickGirth");
        characteristicNames.add("testicleSize");

        //LOAD HASHMAP WITH VARIABLE NAMES AND INDEX
        int x = 0;
        for (String characteristicName : characteristicNames){
            this.characteristicsIndex.put(characteristicName, x);
            x++;

        }
       
        this.characteristicDescriptions = new ArrayList<ArrayList<ArrayList<String>>>();
        

        //place non-base 10 severities at top. These are probably easier to load semi manually.
        this.boobShapeDescriptions = new ArrayList<ArrayList<String>>(5);
        this.facialArchetypeDescriptions = new ArrayList<ArrayList<String>>(5);
        this.hairColorDescriptions = new ArrayList<ArrayList<String>>(11);   
        this.growsHairBelowNeckDescriptions= new ArrayList<ArrayList<String>>(4);

        //base 10 severities
        this.analSensitivityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.analCapacityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.analTightnessDescriptions = new ArrayList<ArrayList<String>>(10);
        this.analLubricationDescriptions = new ArrayList<ArrayList<String>>(10);
        this.analHoldsInCumDescriptions = new ArrayList<ArrayList<String>>(10);
        this.vagSensitivityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.vagCapacityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.vagTightnessDescriptions = new ArrayList<ArrayList<String>>(10);
        this.vagLubricationDescriptions = new ArrayList<ArrayList<String>>(10);
        this.vagHoldsInCumDescriptions = new ArrayList<ArrayList<String>>(10);
        this.wombCapacityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.cervixSensitivityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.cervixPenetrationDescriptions = new ArrayList<ArrayList<String>>(10);
        this.girthQueenDescriptions = new ArrayList<ArrayList<String>>(10);
        this.lengthQueenDescriptions = new ArrayList<ArrayList<String>>(10);
        this.ovulationFrequencyDescriptions= new ArrayList<ArrayList<String>>(10);
        this.ovulationQuantityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.pregnancyCravingDescriptions = new ArrayList<ArrayList<String>>(10);
        this.pregnancyRateDescriptions = new ArrayList<ArrayList<String>>(10);
        this.goesIntoHeatDescriptions = new ArrayList<ArrayList<String>>(10);
        this.pheremonesDescriptions = new ArrayList<ArrayList<String>>(10);
        this.hipSizeDescriptions = new ArrayList<ArrayList<String>>(10);
        this.hipShapeDescriptions = new ArrayList<ArrayList<String>>(10);
        this.buttSizeDescriptions = new ArrayList<ArrayList<String>>(10);
        this.buttShapeDescriptions = new ArrayList<ArrayList<String>>(10);
        this.boobSizeDescriptions = new ArrayList<ArrayList<String>>(10);
        this.boobSensitivityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.milkProductionDescriptions = new ArrayList<ArrayList<String>>(10);
        this.milkTypeDescriptions = new ArrayList<ArrayList<String>>(10);
        this.gagReflexDescriptions = new ArrayList<ArrayList<String>>(10);
        this.throatSensitivityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.cumFlavorDesireDescriptions = new ArrayList<ArrayList<String>>(10);
        this.cumDependencyDescriptions = new ArrayList<ArrayList<String>>(10);
        this.facialBeautyDescriptions = new ArrayList<ArrayList<String>>(10);
        this.heightDescriptions = new ArrayList<ArrayList<String>>(10);
        this.waistWidthDescriptions = new ArrayList<ArrayList<String>>(10);
        this.musculatureDescriptions = new ArrayList<ArrayList<String>>(10);
        this.fatPercentageDescriptions = new ArrayList<ArrayList<String>>(10);
        this.skinColorDescriptions = new ArrayList<ArrayList<String>>(10);
        this.flexibilityDescriptions = new ArrayList<ArrayList<String>>(10);
        this.hairLengthDescriptions = new ArrayList<ArrayList<String>>(10);
        this.intelligenceDescriptions = new ArrayList<ArrayList<String>>(10);
        this.egoDescriptions = new ArrayList<ArrayList<String>>(10);
        this.submissivenessDescriptions = new ArrayList<ArrayList<String>>(10);
        this.naturalMasochismDescriptions = new ArrayList<ArrayList<String>>(10);
        this.libidoDescriptions = new ArrayList<ArrayList<String>>(10);
        this.incestResistanceDescriptions = new ArrayList<ArrayList<String>>(10);
        this.cumProductionDescriptions = new ArrayList<ArrayList<String>>(10);
        this.cumPotencyDescriptions = new ArrayList<ArrayList<String>>(10);
        this.cumTasteDescriptions = new ArrayList<ArrayList<String>>(10);
        this.cumIntoxicationDescriptions = new ArrayList<ArrayList<String>>(10);
        this.cumKillerDescriptions = new ArrayList<ArrayList<String>>(10);
        this.dickLengthDescriptions = new ArrayList<ArrayList<String>>(10);
        this.dickGirthDescriptions = new ArrayList<ArrayList<String>>(10);
        this.testicleSizeDescriptions = new ArrayList<ArrayList<String>>(10);

        
        
        //place non-base 10 severities at top. These are probably easier to load semi manually.
        this.characteristicDescriptions.add(this.boobShapeDescriptions);
        this.characteristicDescriptions.add(this.facialArchetypeDescriptions);
        this.characteristicDescriptions.add(this.hairColorDescriptions);   
        this.characteristicDescriptions.add(this.growsHairBelowNeckDescriptions);

        //base 10 severities
        this.characteristicDescriptions.add(this.analSensitivityDescriptions);
        this.characteristicDescriptions.add(this.analCapacityDescriptions);
        this.characteristicDescriptions.add(this.analTightnessDescriptions);
        this.characteristicDescriptions.add(this.analLubricationDescriptions);
        this.characteristicDescriptions.add(this.analHoldsInCumDescriptions);
        this.characteristicDescriptions.add(this.vagSensitivityDescriptions);
        this.characteristicDescriptions.add(this.vagCapacityDescriptions);
        this.characteristicDescriptions.add(this.vagTightnessDescriptions);
        this.characteristicDescriptions.add(this.vagLubricationDescriptions);
        this.characteristicDescriptions.add(this.vagHoldsInCumDescriptions);
        this.characteristicDescriptions.add(this.wombCapacityDescriptions);
        this.characteristicDescriptions.add(this.cervixSensitivityDescriptions);
        this.characteristicDescriptions.add(this.cervixPenetrationDescriptions);
        this.characteristicDescriptions.add(this.girthQueenDescriptions);
        this.characteristicDescriptions.add(this.lengthQueenDescriptions);
        this.characteristicDescriptions.add(this.ovulationFrequencyDescriptions); 
        this.characteristicDescriptions.add(this.ovulationQuantityDescriptions);
        this.characteristicDescriptions.add(this.pregnancyCravingDescriptions); 
        this.characteristicDescriptions.add(this.pregnancyRateDescriptions);
        this.characteristicDescriptions.add(this.goesIntoHeatDescriptions);
        this.characteristicDescriptions.add(this.pheremonesDescriptions);
        this.characteristicDescriptions.add(this.hipSizeDescriptions);
        this.characteristicDescriptions.add(this.hipShapeDescriptions);
        this.characteristicDescriptions.add(this.buttSizeDescriptions);
        this.characteristicDescriptions.add(this.buttShapeDescriptions);
        this.characteristicDescriptions.add(this.boobSizeDescriptions);
        this.characteristicDescriptions.add(this.boobSensitivityDescriptions);
        this.characteristicDescriptions.add(this.milkProductionDescriptions);
        this.characteristicDescriptions.add(this.milkTypeDescriptions);
        this.characteristicDescriptions.add(this.gagReflexDescriptions);
        this.characteristicDescriptions.add(this.throatSensitivityDescriptions);
        this.characteristicDescriptions.add(this.cumFlavorDesireDescriptions);
        this.characteristicDescriptions.add(this.cumDependencyDescriptions);
        this.characteristicDescriptions.add(this.facialBeautyDescriptions);
        this.characteristicDescriptions.add(this.heightDescriptions);
        this.characteristicDescriptions.add(this.waistWidthDescriptions);
        this.characteristicDescriptions.add(this.musculatureDescriptions);
        this.characteristicDescriptions.add(this.fatPercentageDescriptions);
        this.characteristicDescriptions.add(this.skinColorDescriptions);
        this.characteristicDescriptions.add(this.flexibilityDescriptions);
        this.characteristicDescriptions.add(this.hairLengthDescriptions);
        this.characteristicDescriptions.add(this.intelligenceDescriptions); 
        this.characteristicDescriptions.add(this.egoDescriptions);
        this.characteristicDescriptions.add(this.submissivenessDescriptions);
        this.characteristicDescriptions.add(this.naturalMasochismDescriptions);
        this.characteristicDescriptions.add(this.libidoDescriptions);
        this.characteristicDescriptions.add(this.incestResistanceDescriptions);
        this.characteristicDescriptions.add(this.cumProductionDescriptions);
        this.characteristicDescriptions.add(this.cumPotencyDescriptions);
        this.characteristicDescriptions.add(this.cumTasteDescriptions);
        this.characteristicDescriptions.add(this.cumIntoxicationDescriptions);
        this.characteristicDescriptions.add(this.cumKillerDescriptions);
        this.characteristicDescriptions.add(this.dickLengthDescriptions);
        this.characteristicDescriptions.add(this.dickGirthDescriptions);
        this.characteristicDescriptions.add(this.testicleSizeDescriptions);

        
        
        Scanner vignetteLoader = new Scanner(new File("vignettes.csv"));

        // FOR EXPORTING GOOGLE SHEETS DOC: https://stackoverflow.com/questions/3315636/how-to-enclose-every-cell-with-double-quotes-in-google-docs-spreadsheet

        //.csv will output with """,""" between strings
        vignetteLoader.useDelimiter("\"\"\",\"\"\"");
        
        String currString;

        //which characteristic
        int i = 0;
        //which severity within characteristic
        int j = 0;
        
        while (vignetteLoader.hasNext()){

            currString = vignetteLoader.next();

            
            //CHARACTERISTIC LOADING
            //if string is empty, skip
            if ((currString.equals("")) | (currString.equals(""))){         
            
            } else if (characteristicsIndex.containsKey(currString)){
            //if string is a characteristic name, load at index of that name
                i = characteristicsIndex.get(currString);

            //if string matches an int, set severity index to int
            }else if (currString.equals("1")){
                j = 0;
            }else if (currString.equals("2")){
                j = 1;
            }else if (currString.equals("3")){
                j = 2;
            }else if (currString.equals("4")){
                j = 3;
            }else if (currString.equals("5")){
                j = 4;
            }else if (currString.equals("6")){
                j = 5;
            }else if (currString.equals("7")){
                j = 6;
            }else if (currString.equals("8")){
                j = 7;
            }else if (currString.equals("9")){
                j = 8;
            }else if (currString.equals("10")){
                j = 9;
            }else if (currString.equals("11")){
                j = 10;
            } else {
                
                //else, load string into the descriptor array

                //THIS PROBABLY DOESN'T WORK, BUT ITS WORTH A SHOT
                ((characteristicDescriptions.get(i)).get(j)).add(currString);
            }
        }

        vignetteLoader.close();

    }

}