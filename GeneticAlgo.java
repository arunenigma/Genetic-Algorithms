import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;


public class GeneticAlgo {
	/*
	 * create an
	 */
	public static Object[] run(double[][] table,int geneLength,int rows,int cols,String opFolder) throws Exception{
		String[] population = getInitialPopulation(geneLength,Library.INIT_POPULATION);
		Random random = new Random();
		double lastVal = 0;
		
		
		for(int iteration = 0;iteration<Library.ITERATION_LMT;iteration++){

			
			//System.out.println("----------------- iteration = "+iteration);
			
			TreeMap<String,Double> evalMap = new TreeMap<String,Double>();
			//evaluate each gene
			for(String s:population){
				double val = CostUpdater.Eval(table, s, rows, cols);
				evalMap.put(s, val);
			}
			//normalize the gene value by the cumulative gene value
			double totalval = 0;
			for(String s : evalMap.keySet()){
				totalval+=evalMap.get(s);
			}
			for(String s : evalMap.keySet()){
				evalMap.put(s, evalMap.get(s) / totalval);
			}

			//for each normalized gene value try to see if they will be chosen to mate or not
			TreeMap<Double,String> roulleteMap = new TreeMap<Double,String>();
			Object[] populationArr = evalMap.keySet().toArray();
			for(int i = 0;i<evalMap.keySet().size();i++){
				String string =(String) populationArr[i];
				double value = 0;
				for(int j =0;j<=i;j++){
					value+=evalMap.get(populationArr[j]);
				}
				roulleteMap.put(value,string);
			}


			//generate a sequence of random numbers
			double[]randomArr = new double[(int)Library.INIT_POPULATION];
			for(int i = 0;i<randomArr.length;i++){
				randomArr[i] = random.nextDouble();
			}

			Vector<String> nextGen = new Vector<String>();
			for(int i =0;i<randomArr.length;i++){
				for(double val :roulleteMap.keySet()){
					if(val>randomArr[i]){
						//choose this
						nextGen.add(roulleteMap.get(val));
						break;
					}
				}
			}

			//after the next generation has been chosen 
			//generate another set of random numbers for the probability of 
			double[]randomArrCrossover = new double[(int)Library.INIT_POPULATION];
			Vector<String> bufferGen = new Vector<String>();
			for(String s : nextGen){
				bufferGen.add(s);
			}

			for(int i = 0;i<randomArr.length;i++){
				randomArrCrossover[i] = random.nextDouble();
			}

			//search for 2 consequtive crossover ones
			Vector<String> crossoverBuffer = new Vector<String>();
			for(int i = 0;i<randomArr.length;i++){
				if(crossoverBuffer.size() == 2){
					//apply crossover to evaluate new genes
					String[] offsprings = crossOver(crossoverBuffer.get(0),crossoverBuffer.get(1));
					bufferGen.add(offsprings[0]);
					bufferGen.add(offsprings[1]);

					//clear the buffer
					crossoverBuffer.clear();
				}
				if(randomArrCrossover[i] < Library.P_cross){
					//save the gene location and carry on
					crossoverBuffer.add(nextGen.get(i));
				}
				else{
					//add it back to the population
					bufferGen.add(nextGen.get(i));
				}
			}
			if(crossoverBuffer.size() == 2){
				//apply crossover to create new genes
				String[] offsprings = crossOver(crossoverBuffer.get(0),crossoverBuffer.get(1));
				bufferGen.add(offsprings[0]);
				bufferGen.add(offsprings[1]);

				//clear the buffer
				crossoverBuffer.clear();
			}
			else if(crossoverBuffer.size() ==1 ){
				// add it back to the population without any change
				bufferGen.add(crossoverBuffer.get(0));
			}



			//do the mutation on all the strings
			population = mutation(bufferGen);
			
			
			/*
			 * this part here is to generate some data for the output
			 */
			
			if(iteration%20 == 0){
				
				//System.out.println(iteration+" completed");
				/*
				 * TODO: has been deactivated to decrease the run time
				 * */
				//double[]bestgene = chooseBestGene(population,table,rows,cols);
				//double[][] visibmat = Table.getVisibilityMatrix(table, population[(int)bestgene[0]], rows, cols);
				
				//System.out.println(iteration+"\t"+bestgene[1]+"\t"+population[(int)bestgene[0]]);
				//evaluate the value matrix and get it to the 
				//Library.printLayoutToFile(visibmat, "policy3Results/"+iteration+".dhr");
				
			}
			System.out.println(iteration+" completed");
			
			/*
			 * to break out of the loop if it converges;
			*/
			double[]bestgene = new double[2];
			if(iteration>50)
				 bestgene = chooseBestGene(population,table,rows,cols);
			
			
			if(lastVal!=0 && iteration > 50){
				
					
					double changeDelta = Math.abs((bestgene[1] - lastVal) / lastVal);
					//System.out.println(changeDelta);
					
					if(changeDelta < 0.01) {
						//converge
						//System.out.println("converged after iteration = "+iteration);
					
						break;
					}
				
				
			}
			
			
			lastVal = bestgene[1]; 
			
			//System.out.println("value = "+bestgene[1]);
			
			
		}
		
		
		 int index= (int)chooseBestGene(population,table,rows,cols)[0];
		 double[]bestgene = chooseBestGene(population,table,rows,cols);
			//double[][] visibmat = Table.getVisibilityMatrix(table, population[(int)bestgene[0]], rows, cols);
			
			System.out.println("final"+"\t"+bestgene[1]+"\t"+population[(int)bestgene[0]]);
			//evaluate the value matrix and get it to the 
			//Library.printLayoutToFile(visibmat, "policy3Results/"+"final"+".dhr");
		return new Object[]{bestgene[1],population[index]};
		
	}
	
	/*
	 * choose best gene
	 */
	public static double[] chooseBestGene(String[] population,double[][] table,int rows,int cols) throws Exception{
		//choose the best gene from the final pool and return it
		int chosenIndex = -1;
		double chosenValue = -Double.MAX_VALUE;//note we are trying to maximize the value
		for(int i = 0;i<population.length;i++){
			double v = CostUpdater.Eval(table, population[i], rows, cols);
			if(v>chosenValue){
				chosenIndex = i;
				chosenValue = v;
			}
		}
		
		return new double[]{chosenIndex,chosenValue};
	}

	private static String[] crossOver(String string1, String string2) {
		Random r = new Random();
		int pos = r.nextInt(string1.length()-1);
		//System.out.println("position generated = "+pos);
		String alt1 = string1.substring(0, pos)+string2.substring(pos);
		String alt2 = string2.substring(0, pos)+string1.substring(pos);
		return new String[]{alt1,alt2};
	}

	/*
	 * apply mutation on a collection of genes
	 * iterate over each bit and flip it if it has a prob <= mutation prob
	 */
	private static String[] mutation(Vector<String> bufferGen) {
		Random r = new Random();
		String[] result = new String[bufferGen.size()];
		for(int i = 0;i<bufferGen.size();i++){
			char[] chars = new String(bufferGen.get(i)).toCharArray();
			for(int j = 0;j<bufferGen.get(i).length();j++){
				
				if(r.nextDouble() < Library.P_mut ){
					if(chars[j] == '1')
						chars[j] = '0';
					else
						chars[j] = '1';
				}
			}
			
			result[i] = new String(chars);
		}
		
		return result;
	}
	/*
	 * initialize the population
	 */
	public static String[] getInitialPopulation(int geneLength,int totalPopulation){
		//create a random number of binary strings
		String[] result = new String[totalPopulation];
		Random r = new Random();
		for(int i =0;i<totalPopulation;i++){
			char carr[] = new char[geneLength];
			for(int j = 0;j<carr.length;j++)
				if(r.nextBoolean() == true)
					carr[j] = '1';
				else
					carr[j] = '0';

			result[i] = new String(carr);
		}
		
		System.out.println("generated population");
		for(String s :result)
			System.out.println(s);
		return result; 
	}

	

}
