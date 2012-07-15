import java.util.TreeMap;

import javax.swing.text.Position;


public class MatrixParser {
	/*
	 * given a file name read the file 
	 * and parse the data to get
	 * the matrix data
	 */
	public static double[][] readMatrix(String fileName) throws Exception{
		String data = Library.readFileAsString(fileName).trim();
		String lines []= data.split("\n");

		int cols = lines[0].trim().length();
		int rows = lines.length;

		double[][] layout = new double[rows][cols];


		for (int i = 0;i<rows;i++){
			String line = lines[i].trim();
			//System.out.println("line\t\t"+line);
			for (int j=0;j<cols;j++){
				//System.out.println("char " +line.charAt(j));
				layout[i][j] = Integer.parseInt(""+line.charAt(j));
			}
		}


		return layout;
	}

	/*
	 * generate possilbe positions
	 */
	public static double[][] generatePossiblePositions(double[][] layout){
		int rows = layout.length;
		int cols = layout[0].length;

		for(int i = 0;i<rows;i++)
			for(int j =0;j<cols;j++){
				if(layout[i][j] == Library.BLANK){
					/*
					 * check its surroundings to see if there is
					 * a warehouse nearby or not
					 * if not then it is a possilbe location
					 */
					if(i-1 >= 0 && j-1>=0){
						if(layout[i-1][j-1] == Library.WAREHOUSE)
							layout[i][j] = Library.POSSIBLE;
					}
					if(i+1<rows && j+1<cols){
						if(layout[i+1][j+1] == Library.WAREHOUSE)
							layout[i][j] = Library.POSSIBLE;
					}
					if(i-1>=0 && j+1<cols){
						if(layout[i-1][j+1] == Library.WAREHOUSE)
							layout[i][j] = Library.POSSIBLE;
					}
					if(i+1<rows && j-1>=0){
						if(layout[i+1][j-1] == Library.WAREHOUSE)
							layout[i][j] = Library.POSSIBLE;
					}

					if(i-1>=0){
						if(layout[i-1][j] == Library.WAREHOUSE)
							layout[i][j] = Library.POSSIBLE;
					}
					if(j-1>=0){
						if(layout[i][j-1] == Library.WAREHOUSE)
							layout[i][j] = Library.POSSIBLE;
					}
					if(i+1<rows){
						if(layout[i+1][j] == Library.WAREHOUSE)
							layout[i][j] = Library.POSSIBLE;
					}
					if(j+1<cols){
						if(layout[i][j+1] == Library.WAREHOUSE)
							layout[i][j] = Library.POSSIBLE;
					}


				}
			}
		return layout;
	}

	
	/*
	 * create a data structure of the form 
	 * HashTable<Integer,coordinates> from the layout file
	 * 
	 */
	public static TreeMap<Integer,GridPosition> createTreeMap(double[][] layout) throws Exception{
		int rows  = layout.length;int cols = layout[0].length;
		int count = -1;
		TreeMap<Integer,GridPosition> map = new TreeMap<Integer,GridPosition>();
		
		for(int i =0;i<rows;i++){
			for(int j=0;j<cols;j++){
				if(layout[i][j] == Library.POSSIBLE){
					count++;
					map.put(count, new GridPosition(i,j));
				}
			}
		}
		
		return map;
	}
	
	
}