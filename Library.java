import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


public class Library {
	/*
	 * some constants
	 */
	public static final int WALL = 1;
	public static final int WAREHOUSE = 2;
	public static final int BLANK = 0;
	public static final int POSSIBLE = 5;
	public static final double VISIBILITY_CONST = 1;
	public static final int INIT_POPULATION = 1000;
	public static final int ITERATION_LMT = 1000;
	public static final double Cost_Visibility = 5;
	public static final double Cost_Price = -1;
	public static final double P_cross = .50;
	public static final double P_mut = 0.001;
	/*
	 * read file as string
	 */
	public static String readFileAsString(String fileName) throws Exception{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(
				new FileReader(fileName));
		char[] buf = new char[1024];
		int numRead=0;
		while((numRead=reader.read(buf)) != -1){
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}
	
	/*
	 * print a layout to console
	 */
	public static void printLayoutToConsole(double[][] layout){
		for(double[] d:layout){
			{for(int i = 0;i<d.length;i++)
				System.out.print(d[i]+" ");
			}
			System.out.println();
		}
	}
	public static void printLayoutToFile(double[][] matrix,String fileName) throws Exception
	  {
	  
	  // Create file 
	  FileWriter fstream = new FileWriter(fileName,true);
	  BufferedWriter out = new BufferedWriter(fstream);
	  
	  
	  /*
	   * put our stuff in here
	   */
	  for(double[] d:matrix){
			{for(int i = 0;i<d.length;i++)
				out.write(d[i]+" ");
			}
			out.write("\n");
		}
	  
	  
	  //Close the output stream
	  out.close();
	  }
	
	/*
	 * create binary string given an integer value
	 */
	public static String createBinaryString(double val,double size) throws Exception{
		String s =  Integer.toBinaryString((int)val);
		//pad the rest up with 0s 
		if(s.length() == size)
			return s;
		else{
			char[] carr = new char[(int)(size - s.length())];
			for(int i = 0;i<carr.length;i++){
				carr[i] = '0';
			}
			return (new String(carr)+s);
		}
	}
	
	/*
	 * fix the layout data
	 */
	public static void main(String []args) throws Exception{
		String filename = "layoutTest.dhr";
		double[][] layout = MatrixParser.readMatrix(filename);
		Library.printLayoutToFile(layout, "layoutTestFixed.dhr");
	}

	public static void writeToFile(String fileName, String data) throws Exception{
		BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
		out.write(data);
		out.close();
	}

	public static void appendToFile(String opFile, String data) throws Exception{

		BufferedWriter writer = new BufferedWriter(
				new FileWriter(opFile,true)) ;
				writer.write(data) ;
				writer.close() ;
	}
}
