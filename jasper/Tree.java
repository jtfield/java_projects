package jasper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Tree {
	
	/*--------------------------------------------------------------*/
	/*----------------        Initialization        ----------------*/
	/*--------------------------------------------------------------*/
	
	/**
	 * Takes in an input file with 2 columns (organism, parent organism) and adds these to TreeNodes
	 * TreeNodes are then added to a HashMap and children node values are added to each node if applicable
	 * 
	 * @param inputFile The input file you wish to have values added to the Tree object
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Tree(String inputFile) throws FileNotFoundException, IOException {
		
		in = inputFile;
		
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
	        			
	        			//Increment linesProcessed
	        			linesProcessed++;
	        		}
	        	}
	        }
		}
	
		//Run method to add children nodes to each node if applicable
		addChild(nodes, lines);
		
		//return nodes;
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
			
			//get the organism node and parent node
			TreeNode orgNode = treeNodeMap.get(org);
			TreeNode parNode = treeNodeMap.get(par);
			
			//Assert parent node isnt empty or parent node is the 0/life node.
			assert(parNode != null || par.equals("0"));
			
			//Assert the query orgnaism node isnt empty, if it is, return node name.
			assert(orgNode != null): org;
			
			//Add the child node name to the query node.
			parNode.addChildren(org);
			
		}
			
	}
	
	/**
	 * Returns a StringBuilder of names of organisms/nodes along with
	 * the parent node and the names of children nodes.
	 * 
	 * @return StringBuilder
	 */
	public String toString() {
		StringBuilder sb=new StringBuilder();
		Iterator it = nodes.entrySet().iterator();
	    while (it.hasNext()) {
	    	Map.Entry pair = (Map.Entry)it.next();
	    	sb.append(pair);
	    	sb.append('\n');
	    }
		return sb.toString();
	}
	
	/**
	 * Returns Set<String> of node keys for the tree.
	 * @return Set<String>
	 */
	public Set<String> keySet() {
		return nodes.keySet();
	}
	
	public TreeNode getNode(String nodeName) {
		return nodes.get(nodeName);
	}
	
	/*--------------------------------------------------------------*/
	/*----------------            Fields            ----------------*/
	/*--------------------------------------------------------------*/
	
	//HashMap holding the names of the organisms as keys and the organism node as values
	HashMap<String, TreeNode> nodes = new HashMap<>();
		
	//ArrayList of all lines in input file. need these later to fill in values for children nodes
	ArrayList<String> lines = new ArrayList<String>();
	
	//Header line of input file
	private String[] header;
	
	//Input file name
	private String in=null;
	
	//Number of lines processed for data from input file
	private long linesProcessed=0;
	
}
