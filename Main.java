import java.util.TreeMap;
import java.util.Vector;


public class Main {
	public static void main(String[] args) throws Exception{
		//testGeneticAlgo();
		//printRunTimes();
		//testConvergence();
		//testPossiblePossitions();
		
		
		
		String fileLayout;
		//String fileLayout =  "complicatedLayouts/design32.txt";
		//fileLayout = "complicatedLayouts/layout1.txt"
		
		fileLayout = "/Users/arunprasathshankar/Desktop/LAYOUT/lay";
		
		
		
		for(int i = 0;i<5;i++){
			Object[] ans = testGeneticAlgo(fileLayout+(i+1)+".txt");
			Library.appendToFile("GAruntime.log", ""+ans[2]+"\n");
			Library.appendToFile("GAsoln.log",""+ ans[1]+"\n");
			Library.appendToFile("GAvalue.log",""+ ans[0]+"\n");
			}
			
			
		
		for(int i = 0;i<5;i++){
		Object[] ans = generateBruteForceSolution(fileLayout+(i+1)+".txt");
		Library.appendToFile("BFruntime.log", ""+ans[2]+"\n");
		Library.appendToFile("BFsoln.log",""+ ans[1]+"\n");
		Library.appendToFile("BFvalue.log",""+ ans[0]+"\n");
		}
		
		
		//testGeneticAlgo(fileLayout);
	}

	/*
	 * test for the possible position generation
	 */
	public static void testPossiblePossitions() throws Exception{
		String filename = "/home/dhrsaha/workspace/Surveillance/RunTimeTest/papa_layout_sizes/8x8.txt";//"layoutTest.dhr";
		double[][] layout = MatrixParser.readMatrix(filename);
		Library.printLayoutToConsole(layout);
		//Library.printLayoutToConsole(MatrixParser.generatePossiblePositions(layout));
		Library.printLayoutToFile(MatrixParser.generatePossiblePositions(layout), "testing.dhr");
	}

	/*
	 * test the manhattan blockage
	 */
	public static void testManhattanBlockage() throws Exception{
		String filename = "layoutTest.dhr";
		double[][] layout = MatrixParser.readMatrix(filename);

		double[][] possilbe  = (MatrixParser.generatePossiblePositions(layout));

		TreeMap<Integer,GridPosition> map = MatrixParser.createTreeMap(possilbe);

		char[] carr = new char[map.keySet().size()];
		for(int i = 0;i<carr.length;i++){
			carr[i] = '1';
		}
		String gene = new String(carr);


		long start = System.currentTimeMillis();
		double[][] visible = CostUpdater.getVisibilityMatrix(possilbe,gene , map);
		System.out.println("prinitng the visibility matrix");
		Library.printLayoutToConsole(visible);
		long stop = System.currentTimeMillis();
		System.out.println("total time = "+(stop -start));
		System.out.println("gene size = "+gene.length());
	}

	/*
	 * test alternate memoization approach
	 * create the table with gene sequence of all 1s
	 * then iterate over all genes and check the time it takes to evaluate each gene
	 */
	public static void testMemoization() throws Exception{
		String filename = "layoutTest.dhr";
		double[][] layout = MatrixParser.readMatrix(filename);
		double[][] possilbe  = (MatrixParser.generatePossiblePositions(layout));
		TreeMap<Integer,GridPosition> map  =MatrixParser.createTreeMap(possilbe); 
		double n = map.keySet().size();
		double[][] table = Table.createTable(layout);

		System.out.println("size of each gene = "+n);
		long start = System.currentTimeMillis();
		for (int i = 0;i<Math.pow(2, 10);i++){//TODO:set the upper lt to n
			System.out.println("evaluating "+i);
			Table.getVisibilityMatrix(table, Library.createBinaryString(i), layout.length, layout[0].length);
		}
		long end = System.currentTimeMillis();

		System.out.println("------- total time reqd  =  "+(end  - start));

	}
	/*
	 * test genetic algorithm
	 */
	public static Object[] testGeneticAlgo(String layoutFileName) throws Exception{
		long start = System.currentTimeMillis();
		String filename = layoutFileName;
		double[][] layout = MatrixParser.readMatrix(filename);
		double[][] possilbe  = (MatrixParser.generatePossiblePositions(layout));
		TreeMap<Integer,GridPosition> map  =MatrixParser.createTreeMap(possilbe); 
		double n = map.keySet().size();
		double[][] table = Table.createTable(layout);
		Object[] ans = GeneticAlgo.run(table, (int)n, layout.length, layout[0].length,"./results/");
		long stop = System.currentTimeMillis();
		System.out.println("run time = "+(stop - start));
		long runtime = stop-start;
		return new Object[]{ans[0],ans[1],runtime};
	}
	
	
	/*
	 * 
	 */
	public static void printRunTimes() throws Exception{
	String baseFolderOP = "RunTimeTest/";
	String baseFolderIP = baseFolderOP+"papa_layout_sizes/";
	
	/*
	 * create a data structrure to store teh data
	 */
	int tableTIME = 0,avgTimeBrute = 1,totalProjTimeBrute = 2,GATime = 3;
	TreeMap<Double,Vector<Long>> runTimeData = new TreeMap<Double,Vector<Long>>(); 

	
	
	for(int testCaseId =64;testCaseId<=64;testCaseId*=2){//TODO: change this to the 128 upperBound
		
		System.out.println("running test case "+testCaseId);
		
		
		//init the data structure
		runTimeData.put((double)testCaseId, new Vector<Long>());
		for (int j =0;j<4;j++){
			Vector<Long> v = runTimeData.get((double)testCaseId);
			v.add(new Long(-1));
		}
		
		/*
		 * create the table and save the time
		 */
		String filename = baseFolderIP+testCaseId+"x"+testCaseId+".txt";
		double[][] layout = MatrixParser.readMatrix(filename);
		double[][] possilbe  = (MatrixParser.generatePossiblePositions(layout));
		TreeMap<Integer,GridPosition> map  =MatrixParser.createTreeMap(possilbe); 
		//find the length of the GA string
		double n = map.keySet().size();
		
		long startTable = System.currentTimeMillis();
		double[][] table = Table.createTable(layout);
		long endTable = System.currentTimeMillis();
		
		//print the total time required in the file 
		runTimeData.get((double)testCaseId).set(tableTIME, endTable - startTable);
		
		/*
		 * iterate 100 times for different search strings and get the average
		 */
		
		long val = 0;
		
		int k =0;
		long start = System.currentTimeMillis();
		for (;k<Math.min(100, Math.pow(2, n));k++){//TODO:set the upper lt to n
			
			
			//Table.getVisibilityMatrix(table, Library.createBinaryString(k), layout.length, layout[0].length);
			double v = CostUpdater.Eval(table, Library.createBinaryString(k), layout.length, layout[0].length);
			
			
			
			
		}
		long stop = System.currentTimeMillis();
		val+=(stop - start);
		double avg = ((double)val) / (k+1);

		runTimeData.get((double)testCaseId).set(avgTimeBrute, (long)avg);
		runTimeData.get((double)testCaseId).set(totalProjTimeBrute, (long)((Math.pow(2, n))*avg));

		/*
		 * run the GA on this and try to get the data 
		 */
		long startGA = System.currentTimeMillis();
		GeneticAlgo.run(table, (int)n, layout.length, layout[0].length,baseFolderOP+"/"+testCaseId);
		long stopGA = System.currentTimeMillis();
		runTimeData.get((double)testCaseId).set(GATime, (long)(((double)(stopGA - startGA)) / Library.ITERATION_LMT));
		
		
		
		
		/*
		 * append the data
		 */
		String s   = "";
			double d = (double)testCaseId;
			s+= d+"\t";
			for(long l:runTimeData.get(d)){
				s+=l+"\t";
			}
			s+="\n";
			
		
		String opFile = baseFolderOP+"/runTimeComparisons.txt";
		Library.appendToFile(opFile,s);
		
	}
	
	
	}
	
	/*
	 *create 3 different policies and then evaluate the convergence for each
	 *use 1000 starting population
	 * 
	 */
	public static void testConvergence() throws Exception{
		String baseFolderOP = "RunTimeTest/";
		String baseFolderIP = baseFolderOP+"papa_layout_sizes/";
		
		
		for(int testCaseId =8;testCaseId<=8;testCaseId*=2){//TODO: change this to the 128 upperBound
			
			System.out.println("running test case "+testCaseId);
			
			
		
			
			/*
			 * create the table and save the time
			 */
			String filename = baseFolderIP+testCaseId+"x"+testCaseId+".txt";
			double[][] layout = MatrixParser.readMatrix(filename);
			double[][] possilbe  = (MatrixParser.generatePossiblePositions(layout));
			TreeMap<Integer,GridPosition> map  =MatrixParser.createTreeMap(possilbe); 
			//find the length of the GA string
			double n = map.keySet().size();
			
			long startTable = System.currentTimeMillis();
			double[][] table = Table.createTable(layout);
			long endTable = System.currentTimeMillis();
			
			
			/*
			 * iterate 100 times for different search strings and get the average
			 */
			
			long val = 0;
			
			int k =0;
			long start = System.currentTimeMillis();
			double max = -Double.MAX_VALUE;
			String chosenString = null;
			for (;k<Math.pow(2, n);k++){//TODO:set the upper lt to n
				
				
				//Table.getVisibilityMatrix(table, Library.createBinaryString(k), layout.length, layout[0].length);
				double v = CostUpdater.Eval(table, Library.createBinaryString(k), layout.length, layout[0].length);
				System.out.println("brute force "+v);
				
				if(v>max){
					max = v;
					chosenString = Library.createBinaryString(k);
				}
				
				
				
			}
			
			
			long stop = System.currentTimeMillis();
			
			
			double timeBrute = stop - start;

			
			/*
			 * run the GA on this and try to get the data 
			 */
			long startGA = System.currentTimeMillis();
			String gene = GeneticAlgo.run(table, (int)n, layout.length, layout[0].length,baseFolderOP+"/"+testCaseId);
			long stopGA = System.currentTimeMillis();
			
			
			double maxGA = CostUpdater.Eval(table, gene, layout.length, layout[0].length);
			double timeGA = stopGA - startGA;
			
			/*
			 * append the data
			 */
				
			
				//in the op file we should write down the following:
				//total time to converge
				//total time for brute force
				//soln for brute force 
				//soln for GA
			
			String data = "";
			data+=max+"\t"+maxGA+"\t"+timeBrute+"\t"+timeGA+"\n";
			String opFile = baseFolderOP+"/policy3_additional.txt";
			Library.appendToFile(opFile,data);
			
		}
		
		
		}
		

	/*
	 * take a custom layout 
	 * get the brute force soln
	 */
	public static Object[] generateBruteForceSolution(String layoutFileName) throws Exception{
		long start = System.currentTimeMillis();
		String baseFolderOP = "complicatedLayouts/";
		String baseFolderIP = "complicatedLayouts/";
		
		
		
			
			
			
			
		
			
			/*
			 * create the table and save the time
			 */
			String filename = layoutFileName;
			double[][] layout = MatrixParser.readMatrix(filename);
			double[][] possilbe  = (MatrixParser.generatePossiblePositions(layout));
			TreeMap<Integer,GridPosition> map  =MatrixParser.createTreeMap(possilbe); 
			//find the length of the GA string
			double n = map.keySet().size();
			
			long startTable = System.currentTimeMillis();
			double[][] table = Table.createTable(layout);
			long endTable = System.currentTimeMillis();
			
			
			/*
			 * iterate 100 times for different search strings and get the average
			 */
			
			 double max  = - Double.MAX_VALUE;
			 String chosenString = "";
			double maxPossible = Math.pow(2, n);
			for (int k =0;k<maxPossible;k++){//TODO:set the upper lt to n
				
				String currentString = Library.createBinaryString(k,n);
				
				System.out.println("evaluating "+k+"/"+maxPossible +"  --string = "+currentString);
				double v = CostUpdater.Eval(table, currentString, layout.length, layout[0].length);
				System.out.println("brute force "+v);
				
				if(v>max){
					max = v;
					chosenString = currentString;
					System.out.println("new selection = "+chosenString);
					
				}
				
				long inter = System.currentTimeMillis();
				long delta = inter - start;
				if(inter - start >28000){
					System.out.println("iterations = "+k);
					//break;
				}
				if(k % 1000 == 0){
					System.out.println("projected time = "+delta*maxPossible / k / 60 / 1000 +" n= "+n+" using layout "+layoutFileName);
				}
				
			}
			long end = System.currentTimeMillis();
			long runtime = end-start;
			
			System.out.println("cost of solution = "+max);
			System.out.println("solution   = "+chosenString);
			System.out.println("run time = "+runtime);
					
			//soln is of the form soln string value run time
			return new Object[]{chosenString,max,runtime};
	}
}
 