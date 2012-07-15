import java.util.TreeMap;
import java.util.Vector;


public class CostUpdater {
	/*
	 * given a visibility matrix get the evaluation of the matrix
	 * that is get a single value based on the policy
	 * we will describe the policy elsewhere
	 */
	public static double Eval(double[][] table,String gene,int rows,int cols){
		double valVis = 0;
		double totalPixels = rows*cols;
		for(int i = 0;i<rows;i++){
			for(int j=0;j<cols;j++){
				
			
				
				
				double tmp = 0;
				for(int k = 0;k<gene.length();k++)
					if(gene.charAt(k) == '1'){
						/*
						tmp +=table[i*cols+j][k];
						*/
						
							tmp=1;
						
						
					}
				
				
				
				valVis+=Library.Cost_Visibility*tmp;
			}
		}
		
		
		double valCP = 0;
		for(int k =0;k<gene.length();k++)
		    valCP += Library.Cost_Price;  
		return valCP+valVis / totalPixels;
	}
	
	/*
	 * given a gene find the visibility of the layout 
	 * visibility is calculated as follows
	 * for each i,j 
	 * for each camera position specified by a 1 in the gene
	 * check to see if there is an obstacle between the manhattan path from i,j to the cam location
	 * if not then sum up the visibility
	 * else do not change the visibility 
	 */
	
	public static double[][] getVisibilityMatrix(double[][] layout ,String gene,TreeMap<Integer,GridPosition> map) throws Exception{
		int rows = layout.length;int cols = layout[0].length;
		double[][] visibilityMatrix = new double[rows][cols];
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
							//System.out.println("evaluating "+i+","+j+" with cam position "+camPosition);
							Vector<GridPosition> manhattanPath = getManhattanPath(new GridPosition(i,j),new GridPosition(camPosition.x,camPosition.y),rows,cols);
							if(!isBlockedFromView(manhattanPath,layout)){
								
								if(manhattanPath.size()!=0){
								visibilityMatrix[i][j] +=  Library.VISIBILITY_CONST / manhattanPath.size();
								}
								else{
									visibilityMatrix[i][j] +=  Library.VISIBILITY_CONST;
								}
							}
							
						}
					}
				}
			}
		}
		
		
		return visibilityMatrix;
	}

	/*
	 * given a set of grid points check if the manhattan path is blocked by a wall or warehouse or not
	 */
	public  static boolean isBlockedFromView(
			Vector<GridPosition> manhattanPath, double[][] layout) {
		
		for(GridPosition g:manhattanPath){
			double val = layout[(int)g.x][(int)g.y];
			if(val == Library.WALL || val ==Library.WAREHOUSE)
				return true;
		}
		return false;
	}

/*
	 * get the manhattan path from a given source coordinate to a destination coordinate
	 * 
	 * from the start point and the end point get the slope of the line
	 * from the start point get the 3 neighbors who are at <= dist from the target than the original starting point
	 * get the perpendicular distance from each
	 * choose the minimum 
	 * 
	 * 
	 * x,y is the destination point and i,j is the source
	 */
public static Vector<GridPosition> getManhattanPath(GridPosition source,GridPosition destination,int maxRows,int maxCols) throws Exception{
	Vector<GridPosition> result = new Vector<GridPosition>();
	GridPosition nextBlock = new GridPosition(source.x,source.y);
	if(source.isEqual(destination))
		return result;
	do{
		nextBlock = getNextManhattanBlock(nextBlock,destination,maxRows,maxCols);
		result.add(nextBlock);
	}while(!nextBlock.isEqual(destination));
	return result;
}
	
	
	
	/*
	 * getNextmanhattan block gives us the next block from the source to the destination in the manhattan path from source to dest
	 */
	public static GridPosition getNextManhattanBlock(GridPosition sourcePosition, GridPosition destPosition, int rows, int cols) {
		
		//System.out.println("next manhattan block called from "+sourcePosition);
		
		if(sourcePosition.isEqual(new GridPosition(5,7))){
			int k = 0;
		}
		
		double i = sourcePosition.x;
		double j = sourcePosition.y;
		double x = destPosition.x;
		double y = destPosition.y;
		
		
		//slope of the line
		// (y - j ) / (x  - i) = m;--> mx - y -(m*i-j);this is the equation of the line
		double m = ((double)(y - j)) /((double)(x - i));
		 
		//the original distance between the pts
		double origDist = findDist(x,y,i,j);
		
		
		
		double chosenx = -1;
		double choseny = -1;
		double chosenPerpLen = Double.MAX_VALUE;
		
		
		if(i-1 >= 0 && j-1>=0){
			double[] arr = returnChosenValues(i,j,x,y,i-1,j-1,m,new double[]{chosenx,choseny,chosenPerpLen});
			chosenx = arr[0];
			choseny = arr[1];
			chosenPerpLen = arr[2];
		}
		if(i+1<rows && j+1<cols){
			double[] arr = returnChosenValues(i,j,x,y,i+1,j+1,m,new double[]{chosenx,choseny,chosenPerpLen});
			chosenx = arr[0];
			choseny = arr[1];
			chosenPerpLen = arr[2];
		}
		if(i-1>=0 && j+1<cols){
			double[] arr = returnChosenValues(i,j,x,y,i-1,j+1,m,new double[]{chosenx,choseny,chosenPerpLen});
			chosenx = arr[0];
			choseny = arr[1];
			chosenPerpLen = arr[2];
		}
		if(i+1<rows && j-1>=0){
			double[] arr = returnChosenValues(i,j,x,y,i+1,j-1,m,new double[]{chosenx,choseny,chosenPerpLen});
			chosenx = arr[0];
			choseny = arr[1];
			chosenPerpLen = arr[2];
		}

		if(i-1>=0){
			double[] arr = returnChosenValues(i,j,x,y,i-1,j,m,new double[]{chosenx,choseny,chosenPerpLen});
			chosenx = arr[0];
			choseny = arr[1];
			chosenPerpLen = arr[2];
		}
		if(j-1>=0){
			double[] arr = returnChosenValues(i,j,x,y,i,j-1,m,new double[]{chosenx,choseny,chosenPerpLen});
			chosenx = arr[0];
			choseny = arr[1];
			chosenPerpLen = arr[2];
		}
		if(i+1<rows){
			double[] arr = returnChosenValues(i,j,x,y,i+1,j,m,new double[]{chosenx,choseny,chosenPerpLen});
			chosenx = arr[0];
			choseny = arr[1];
			chosenPerpLen = arr[2];
		}
		if(j+1<cols){
			double[] arr = returnChosenValues(i,j,x,y,i,j+1,m,new double[]{chosenx,choseny,chosenPerpLen});
			chosenx = arr[0];
			choseny = arr[1];
			chosenPerpLen = arr[2];
		}

		
		return new GridPosition(chosenx,choseny);
	}
	
		
	/*
	 * find distance
	 */
	public static double findDist(double x,double y,double i ,double j){
		return Math.sqrt(((double)(y-j))*((double)(y-j))+((double)(x-i))*((double)(x-i)));
	}
	/*
	 * return chosen values
	 * check if the euclid dist to the end is lesser than the original one
	 * if so 
	 * check if the perpendicular dist is lesser than the original one
	 * if so return [i][j][perpDist] as a double arr
	 * 
	 * 
	 */
	public static double[] returnChosenValues(double sourcex,double sourcey,double destx,double desty,double x_under_cons,double y_under_cons,double m ,
			double[] origianValues){
		
		double prevMinDist = origianValues[2]; 
		
		
		if(findDist(destx,desty,x_under_cons,y_under_cons) <= findDist(sourcex,sourcey,destx,desty))
		{
			//get the distance to the line
			double perpDist = Math.abs(m*(double)x_under_cons - (double)y_under_cons -(m*(double)sourcex-(double)sourcey));
			if(Double.isInfinite(m)){
				//perpendicular dist = diff in the i values
				perpDist = Math.abs(x_under_cons - sourcex);
				
			}
			
			if(perpDist < prevMinDist){
				double chosenx = x_under_cons;
				double choseny = y_under_cons;
				double chosenPerpLen = perpDist;
				return new double[]{chosenx,choseny,chosenPerpLen};
			}
		}
		
		return origianValues;
	}
}
