package jasper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Tree {

	HashMap<String, TreeNode> nodes = new HashMap<>();
	
	//ArrayList of all lines in input file. need these later to fill in values for children nodes
	ArrayList<String> lines = new ArrayList<String>();
	
	/**
	 * Takes in an input file with 2 columns (organism, parent organism) and adds these to TreeNodes
	 * TreeNodes are then added to a HashMap and children node values are added to each node if applicable
	 * 
	 * @param inputFile The input file you wish to have values added to the Tree object
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Tree(String inputFile) throws FileNotFoundException, IOException {
		
		//Take file name as input for building tree of related nodes
		String[] split=inputFile.split("=");
		String a=split[0].toLowerCase();
		String b=split.length>1 ? split[1] : null;
		if(b!=null && b.equalsIgnoreCase("null")){b=null;}
		in = b;
		
		//parse file. create each node and place in 
		try (BufferedReader br = new BufferedReader(new FileReader(in))) {
	        String line;
	        
	        while ((line = br.readLine()) != null) {
	        	
	        	
	        	//if line is the header line, split and assign to variable.
	        	if(line.startsWith("#")) {header=line.split("\t");
	        	} else {
	        		String[] data = line.split("\t");
	        		
	        		//Make sure you're not adding the header line to any data structure
	        		if(!Arrays.asList(header).contains(data[0])) {
	        			
	        			//Create a TreeNode containing the name of the organism and the parent node/organism
	        			//System.out.println(data[0]);
	        			TreeNode orgName = new TreeNode(data[0], data[1]);
	        			
	        			//Add node to HashMap nodes with the name of the organism as the key
	        			nodes.put(data[0], orgName);
	        			
	        			//Add line to lines list for further processing
	        			lines.add(line);
		
	        		}
	        	}
	        }
		}
	
		//Run method to add children nodes to each node if applicable
		addChild(nodes, lines);
		
		
	}
	
	
	/**
	 * Adds children node names to each node if applicable
	 * 
	 * @param treeNodeMap HashMap of TreeNode objects
	 * @param lineList ArrayList<String> of lines from the input file
	 */
	void addChild(HashMap<String, TreeNode> treeNodeMap, ArrayList<String> lineList) {
		String par;
		String org;
		
		//iterate over lines from the file and split into the organism and the parent node
		for(String line : lineList) {
			String[] split=line.split("\t");
			
			//isolate the organism and the parent from the split
			org = split[0];
			par = split[1];
			//System.out.println(org + " " + par);
			
			//get the organism node and parent node
			TreeNode orgNode = treeNodeMap.get(org);
			TreeNode parNode = treeNodeMap.get(par);
			//System.out.println(orgNode.orgName);
			//if(parNode != null) {System.out.println(parNode.orgName);}
			
			//if both nodes exist, do stuff
			//if(parNode != null && orgNode != null) {System.out.println(orgNode.orgName);}
			assert(parNode != null || par.equals("0"));
			assert(orgNode != null): org;
			parNode.addChildren(org);
			//if(parNode != null && orgNode != null) {parNode.setChildren(par);}
			assert(orgNode.orgName.equals("5")): orgNode;
			
		}
			
	}
	
	
	private String[] header;
	private String in=null;
	private long linesProcessed=0;
	
}
