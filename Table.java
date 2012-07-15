import java.util.TreeMap;
import java.util.Vector;


public class Table {
	
	/*
	 * given a layout this will return a 2d table with
	 * (i,j) as rows and length(gene) cols
	 * this table will show the contribution of each possible cam on each possible
	 * coordinate
	 * 
	 * the rows are hashed as
	 * gridpoint(i,j) = row i*col_size+j;
	 */
public static double[][] createTable(double[][] layout) throws Exception{
	
	/*
	 * create the map
	 */
	TreeMap<Integer,GridPosition> map = MatrixParser.createTreeMap(layout);
	
	/*
	 * construct a gene with all ones
	 */
	char[] carr = new char[map.keySet().size()];
	for(int i=0;i<carr.length;i++)
		carr[i] = '1';
	String gene = new String(carr);
	
	int rows = layout.length;int cols = layout[0].length;
	
	
	/*
	 * create the matrix to return
	 */
	double[][] visibilityTable = new double[rows*cols][gene.length()];
	
	
	
	
	for(int i =0;i<rows;i++){
		for(int j = 0;j<cols;j++){
			if(layout[i][j] != Library.WALL && layout[i][j]!= Library.WAREHOUSE){
				for(int k = 0;k<gene.length();k++){
					if(gene.charAt(k) == '1'){
						/*
						 * there is a cam at this location 
						 * 
						 */
						
						GridPosition camPosition = map.get(k);
						System.out.println("evaluating "+i+","+j+" with cam position "+camPosition);
						Vector<GridPosition> manhattanPath = CostUpdater.getManhattanPath(new GridPosition(i,j),new GridPosition(camPosition.x,camPosition.y),rows,cols);
						if(!CostUpdater.isBlockedFromView(manhattanPath,layout)){
							
							if(manhattanPath.size()!=0){
							visibilityTable[i*cols+j][k] +=  Library.VISIBILITY_CONST / manhattanPath.size();
							}
							else{
								visibilityTable[i*cols+j][k] +=  Library.VISIBILITY_CONST;
							}
						}
						
					}
				}
			}
		}
	}
	
	
	return visibilityTable;
}

/*
 * get the visibility matrix given a table and a gene
 */
public static double[][] getVisibilityMatrix(double[][] table,String gene,int rows,int cols) throws Exception{
	double[][] result = new double[rows][cols];
	for(int i = 0;i<rows;i++){
		for(int j=0;j<cols;j++){
			for(int k = 0;k<gene.length();k++)
				if(gene.charAt(k) == '1'){
					result[i][j] +=table[i*cols+j][k];
				}
		}
	}
	return result;
}
}
