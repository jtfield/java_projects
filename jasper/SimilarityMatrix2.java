package jasper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SimilarityMatrix2 {
	
	/*--------------------------------------------------------------*/
	/*----------------        Initialization        ----------------*/
	/*--------------------------------------------------------------*/

	/**
	 * Takes in a file of sketch similarity percentages from SketchCompare.
	 * Returns an matrix object containing each percentage
	 * 
	 * @param inputFile The file containing pairwise comparisons of each sketch
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public SimilarityMatrix2(String inputFile) throws FileNotFoundException, IOException {

		//Take file name as input for building tree of related nodes
		in = inputFile;

		//Read in file, add header line and add to header variable
		try (BufferedReader br = new BufferedReader(new FileReader(in))) {
			String line;

			//while line isn't empty, process
			while ((line = br.readLine()) != null) {

				//if line is the header line, split and assign to variable.
				//may be used when header becomes more complex
				if(line.startsWith("#")) {header=line.split("\t");
				} else {
					
					//If not a header line, split on tab.
					String[] data = line.split("\t");
					
					//Query organism is column 0.
					String queryName = data[0];
					//String refName = data[1];
					
					//If query name isn't in the HashMap already, add it.
					if(orgPosMap.containsKey(queryName)==false) {
						orgPosMap.put(queryName, orgCount);
						
						//Update count of organisms in HashMap.
						//This is used for the size of the matrix.
						orgCount++;
					}
				}
			}
		}
		
		//Initialize the matrix with the appropriate sizes.
		matrix = new double[orgCount][orgCount];

		//Begin readding the file a second time.
		try (BufferedReader br = new BufferedReader(new FileReader(in))) {
			String line;

			//while line isn't empty, process
			while ((line = br.readLine()) != null) {

				//If line is the header line, split and assign to variable.
				//may be used when header becomes more complex
				if(line.startsWith("#")) {assert true;
				} else {
					
					//If not a header line, split on tab.
					String[] data = line.split("\t");
					
					//Column 0 is query name.
					String queryName = data[0];
					
					//Column 1 is reference name.
					String refName = data[1];
					
					//Column 2 is the similarity percentage.
					double similarity = Double.parseDouble(data[2]);
					
					//Check that both names are in the HashMap (too slow?)
					if(orgPosMap.containsKey(queryName)==true && orgPosMap.containsKey(refName)) {
						
						//Get the positions assigned to both organisms.
						int queryPos = orgPosMap.get(queryName);
						int refPos = orgPosMap.get(refName);
						
						//Add the similarity percentage to the appropriate matrix position.
						matrix[queryPos][refPos] = similarity;
					}
				}
			}
		}	
	}
	
	/**
	 * Prints out the entire matrix.
	 * Impractical in cases of large input datasets.
	 * 
	 */
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {
		    for (int j = 0; j < matrix[i].length; j++) {
		        sb.append(matrix[i][j] + " ");
		    }
		    sb.append('\n');
		}
		return sb.toString();
	}
	
	/**
	 * Returns the similarity of two specified organisms.
	 * Both organisms must have been compared using SketchCompare.
	 * 
	 * @param org1 The Name of an organism.
	 * @param org2 The name of a second organism.
	 * @return similarity The Double percentage similarity between the two sketches.
	 */
	public Double getSimilarity(String org1, String org2) {
		int orgName1 = orgPosMap.get(org1);
		int orgName2 = orgPosMap.get(org2);
		
		return matrix[orgName1][orgName2];
	}
	
	public String getNames() {
		
		StringBuilder sb=new StringBuilder();
		for(String key: orgPosMap.keySet()) {
			sb.append(key);
			sb.append('\t');
		}
		return sb.toString();
	}

	
	
	/**
	 * Returns the names of each organism in the dataset along with its position in the matrix.
	 * 
	 * @return names StringBuilder of names.
	 */
	public String orgAndPosition() {
		StringBuilder sb=new StringBuilder();
		Iterator it = orgPosMap.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry pair = (Map.Entry)it.next();
	    	sb.append(pair);
	    	sb.append('\n');
	    }
		return sb.toString();
	}
	
	public int getSize() {
		return orgCount;
	}
	
	/*--------------------------------------------------------------*/
	/*----------------            Fields            ----------------*/
	/*--------------------------------------------------------------*/
    
	//Matrix that will hold the percentages of sketch comparisons
	private double[][] matrix;
	
	//The number of sketches being analyzed
	private int orgCount;
	
	//ArrayList that will hold the lines of the input file
	ArrayList<String> lines = new ArrayList<String>();
		
	//Set that will hold the names of the organisms being compared in the input file
	Set<String> nameSet = new HashSet<String>();
	
	//HashMap containing the name of the organism and its position within the matrix
	HashMap<String, Integer> orgPosMap = new HashMap<>();
	
	//Header line of the comparison input file
	private String[] header;
	
	//Input file name
	private String in=null;
	
	//Number of lines processed from the sketch comparison file
	private long linesProcessed=0;

	public void showMatrix() {
		System.out.print(this);
		
	}
	
}
