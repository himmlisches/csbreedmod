public class BreedmodChosenGen {
	//see line 90750 of chosen.class for call

	int fatherIsHighVariance;
	int motherIsHighVariance;
	int motherIsSuperior;
	int fatherIsSuperior;

	public int initializeGeneticsValue(int skew, int max, int min) {
		/**
		 * returns an int from 1-100
		 * where 1 indicates anal has no pleasure
		 * and 100 indicates near instant orgasm from stimulation
		 * default assumed to be 1-30
		 * 
		 * Note: Description intended to be in brackets of 10, 
		 * e.g. 1-10 have a single description, 11-20, etc.
		 */
		
		int result;
		int localSkew = skew;
		int localMax = max;
		int localMin = min;

		if (localMin < 1){
			localMin = 1;
		}

		BreedmodGeneticsRoll genetics = new BreedmodGeneticsRoll();
		result = genetics.newGene() + localSkew;

		if (result > localMax) {
			result = localMax;
		} else if (result < localMin) {
			result = localMin;
		}

		return result;
	}

	public int childGeneticsValue(int fatherGene, int motherGene){
		
		double childVariance = (double) ((fatherIsHighVariance + motherIsHighVariance)*10 + 10);


		int result;

		int localMax = 100;
		int localMin = 1;

		if (localMin < 1){
			localMin = 1;
		}

		BreedmodGeneticsRoll genetics = new BreedmodGeneticsRoll();
		result = genetics.childGene(fatherGene, motherGene, childVariance);

		//if the parents are superior, give a better roll
		if (motherIsSuperior == 1){
			result = result + 7;
		}

		if (result > localMax) {
			result = localMax;
		} else if (result < localMin) {
			result = localMin;
		}

		return result;
		

	}
	
} 