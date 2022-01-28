import java.math.*;
import java.util.*;

public class GeneticsRoll { 
    public int newGene(){
        /**
         * Returns a random int between 0 and 100, based on a
         * normal logarithmic distribution centered at 50
         * 
         * 50% of population below 0
         * 25% of population from 0-10
         * 12.5% of population from 11-20
         * 6.25% of population from 21-30
         * 3.1% of population from 31-40
         * 1.6% of population from 41-50
         * 0.8% of population from 51-60
         * 0.4% of population from 61-70
         * 0.2% of population from 71-80
         * 0.1% of population from 81-90
         * 0.05% of population from 91-100
         */
        int newGene;
        int mean = 50;
        double deviation = 10.0;

        Random newRandom = new Random();
        
        newGene = Math.floor((newRandom.nextGaussian() * deviation) + mean);

        if (newGene < 0) {
             newGene = 0;
        } else if (newGene > 100) {
            newGene = 100;
        }

        return newGene;
    }
    
    public int childGene(int motherGene, int fatherGene, double geneticDeviation){
        /**
         * Returns an int based on a mother and father value
         * bounded 1-100
         */

        int childGene;
        int geneticDrift;
        double deviation = geneticDeviation;

        Random newRandom = new Random();

        geneticDrift = Math.floor(newRandom.nextGaussian() * deviation);

        childGene =  geneticDrift + (Math.floor(0.5*(motherGene + fatherGene)));
        
        if (childGene < 1) {
            childGene = 1;
       } else if (childGene > 100) {
           childGene = 100;
       }

       return childGene;
    }
}