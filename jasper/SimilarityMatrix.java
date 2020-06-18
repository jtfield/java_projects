package jasper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SimilarityMatrix {

	ArrayList<String> lines = new ArrayList<String>();
	
	Set<String> nameSet = new HashSet<String>();
	
	
	public SimilarityMatrix(String inputFile) throws FileNotFoundException, IOException {
		
		//Take file name as input for building tree of related nodes
		String[] split=inputFile.split("=");
		String a=split[0].toLowerCase();
		String b=split.length>1 ? split[1] : null;
		if(b!=null && b.equalsIgnoreCase("null")){b=null;}
		in = b;
		
		//Read in file, add header line and add to header variable
	    try (BufferedReader br = new BufferedReader(new FileReader(in))) {
	        String line;
	        
	        while ((line = br.readLine()) != null) {
	        	
	        	//if line is the header line, split and assign to variable.
	        	if(line.startsWith("#")) {header=line.split("\t");
	        	} else {
	        		String[] data = line.split("\t");
	        		
	        		//make sure the data in column 1 isnt in the header line
		        	//column 1 should be query names
	        		if(!Arrays.asList(header).contains(data[0])) {nameSet.add(data[0]);}
	        		lines.add(line);
	        	
	        	}
	        	
	        }
	    }
		
	    double[][] matrix = new double[nameSet.size() + 1 ][nameSet.size() + 1];
	    
	    //loop over lines and fill in matrix
	    for(int i=0; i<lines.size(); i++) {
	    	
	    	fillMatrix(matrix, nameSet, lines.toArray()[i]);
	    }
	    
	    //return matrix;
	}
	
	/**
	 * Fill matrix with relationship information of organisms output from sketch comparison.
	 * 
	 * @param matrix Matrix of comparison percentage values.
	 * @param setNames Set of names of included organisms.
	 * @param object Line of sketch comparison output file.
	 */
	void fillMatrix(double[][] matrix, Set<String> setNames, Object object) {
		//System.out.println(object);
		//cast line as string
		String stringLine = (String) object;
		
		//split line
		String[] lineData = stringLine.split("\t");
		
		//place both organism names in variables
		//qName is the query, column 1
		String qName = lineData[0];
		String mName = lineData[1];
		double similarity = Double.parseDouble(lineData[2]);
		
		//set positions variables
		int qPos = -1;
		int mPos = -1;
		
		//convert setNames to array that can be iterated over
		String[] nameArray = setNames.toArray(new String[setNames.size()]);
		
		//loop over setNames and get each organisms positions within the matrix
		for(int i = 0; i<nameArray.length; i++) {
			if(nameArray[i].contentEquals(qName)) {qPos = i;}
			else if(nameArray[i].contentEquals(mName)) {mPos = i;}
			
			
			//after finding both name positions, add similarity value to matrix
			if(qPos!=-1 && mPos!=-1) {matrix[qPos][mPos] = similarity;}
		}
		
		//displays matrix
		/*
		for (int i = 0; i < matrix.length; i++) {
		    for (int j = 0; j < matrix[i].length; j++) {
		        System.out.print(matrix[i][j] + " ");
		    }
		    System.out.println();
		}*/
		
	}
	
	/*
	public void showMatrix() {
		for (int i = 0; i < matrix.length; i++) {
		    for (int j = 0; j < matrix[i].length; j++) {
		        System.out.print(matrix[i][j] + " ");
		    }
		    System.out.println();
		}
	}
	*/
	
	private String[] header;
	private String in=null;
	private long linesProcessed=0;
}
