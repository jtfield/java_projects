package jasper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import shared.Parser;
import shared.PreParser;
import shared.Timer;
import stream.Read;



public class TreeValidate {
	
	/*--------------------------------------------------------------*/
	/*----------------        Initialization        ----------------*/
	/*--------------------------------------------------------------*/

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		//Start a timer immediately upon code entrance.
		Timer t=new Timer();
		
		//Create an instance of this class
		TreeValidate x=new TreeValidate(args);
		
		//Run the object
		x.process(t);
		
	
	
	}
	
	/**
	 * Handles pre-parsing and parsing of user flags.
	 * Reads in the sketch similarity file (sim) and organism-parent relationship file (tree).
	 * 
	 * @param args string of the arguments input at the commandline.
	 */
	public TreeValidate(String[] args) {
		
		{//Preparse block for help, config files, and outstream
			PreParser pp=new PreParser(args, getClass(), false);
			args=pp.args;
			outstream=pp.outstream;
		}
		
		//Primary parsing of standard arguments found in all bbmap programs (maxReads, parseSam, parseZip, etc).
		Parser parser=new Parser();
		
		//Loop through arguments up to the maximum number of arguments input.
		//process all remaining arguments. 
		for(int i=0; i<args.length; i++){
			
			//Grab argument string at index.
			String arg=args[i];
			
			//Split argument string on "=".
			String[] split=arg.split("=");
			
			//Convert the left side to lowercase.
			String a=split[0].toLowerCase();
			
			//Ternary conditional statement: is the length of the split greater than 1 (thus, an actual input)?
			//if so, the right side of the split is the b variable, if not, b is null.
			String b=split.length>1 ? split[1] : null;
			
			//If b isn't null but a string "null" was input, convert b to null.
			if(b!=null && b.equalsIgnoreCase("null")){b=null;}

			//Unused example statement. does nothing currently. start here for adding new flag parsing.
			if(a.equals("parse_flag_goes_here")){
				
			//Handle reference variable assignment.
			}else if(a.equals("sim")){
				sim=b;
			
			//Handle kmer variable assignment.
			}else if(a.equals("tree")){
				tree=b;
					
			//Parses in and out flags, handles all flags not recognized earlier in class.
			}else if(parser.parse(arg, a, b)){
				
			//If not one of the known parameters, let the user know they made a mistake.
			}else{
				assert(false) : "Unknown parameter "+args[i];
				outstream.println("Unknown parameter "+args[i]);
			}
		}
		
	}
	
	
	/*--------------------------------------------------------------*/
	/*----------------         Outer Methods        ----------------*/
	/*--------------------------------------------------------------*/
	
	
	/**
	 * Creates the similarity matrix and the relationship tree.
	 * Also passes these objects to the inner processing method for analysis.
	 * 
	 * @param t
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void process(Timer t) throws FileNotFoundException, IOException{
		
		//Pass input file to Tree class to create tree
		Tree relationshipTree=new Tree(tree);
		
		//Pass similarity file to create similarity matrix object
		SimilarityMatrix2 matrix=new SimilarityMatrix2(sim);
		
		//Add parent node similarity percentages to each node in the tree.
		addParentSimilarities(relationshipTree, matrix);
		
		//Check similarities
		checkSimilarities(relationshipTree, matrix);
		
		System.out.println(relationshipTree.getNode("sim_genome_1.fa").childSims);
		
		t.stop();
		outstream.println("Time:                         \t"+t);
		
	}
	
	/*--------------------------------------------------------------*/
	/*----------------         Inner Methods        ----------------*/
	/*--------------------------------------------------------------*/
	/**
	 * Iterate over nodes in the Tree and compare present relationships
	 * with values found in the similarity matrix.
	 * 
	 * @param tree Tree object containing TreeNode objects detailing the parent and children of each node.
	 * @param matrix SimilarityMatrix2 object containing percentage similarity of sketches.
	 */
	void addParentSimilarities(Tree tree, SimilarityMatrix2 matrix){
		
		//Iterate over organisms/nodes in the tree.
		for ( String keyOrg : tree.keySet() ) {
			
			//Similarity between keyOrg node and its parent.
			//double parSim;
			
			TreeNode keyNode = tree.getNode(keyOrg);
			
			//Identify parent node.
			String parentName = keyNode.getParent();
			
			//Get descendant nodes.
			HashSet<String> childNames = keyNode.getChildren();
			
			if(!parentName.equals("0")) {
				double parSim = matrix.getSimilarity(keyOrg, parentName);
				keyNode.addParSim(parSim);
			}
			
			for(String kid : childNames) {
				
				if(!tree.getNode(kid).parent.equals("0")) {
					
					double kidSim = matrix.getSimilarity(keyOrg, kid);
					
					keyNode.addChildSim(kid, kidSim);
				}
			}
			
			
			
			/*
			//Initialize HashMap to hold similarities between keyOrg and its descendants.
			HashMap<String, Double> childrenSimilarities = new HashMap<String, Double>();
			
			//Get similarity value between keyOrg and its parent.
			//double parSim = matrix.getSimilarity(keyOrg, parent);
			
			//Get the organism names present in the matrix.
			HashMap<String, Integer> matrixOrgs = matrix.getHashMap();
			
			//Make sure the parent of the keyOrg is in the matrix
			//This will be expanded to handle nodes like "0" and "life"
			if(matrixOrgs.containsKey(parent)==true) {
				
				//Get similarity value between keyOrg and its parent.
				parSim = matrix.getSimilarity(keyOrg, parent);
				
				//Loop over each child node.
				for(String kid : childNames) {
					
					//Place child node similarities into the HashMap.
					childrenSimilarities.put(kid, matrix.getSimilarity(keyOrg, kid));
				}
				
			}
			
			//Loop over organisms in the matrix.
			for(String checkOrg : matrix.getNames().split("\t")) {
				
				//If organism isn't a child node of keyOrg.
				if(childNames.contains(checkOrg)==false) {
					
					//Get similarity
					double thisSim = matrix.getSimilarity(keyOrg, checkOrg);
					
					//Check which similarity number is larger.
					//if(thisSim > parSim) {
						//flag
					//}
				}
			}
*/
		}
	}
	
	void checkSimilarities(Tree tree, SimilarityMatrix2 matrix) {
		//Iterate over organisms/nodes in the tree.
		for ( String keyOrg : tree.keySet() ) {

			//Similarity between keyOrg node and its parent.
			//double parSim;

			TreeNode keyNode = tree.getNode(keyOrg);

			//Identify parent node.
			String parentName = keyNode.getParent();
		}
	}
	
	
	
	/*--------------------------------------------------------------*/
	/*----------------            Fields            ----------------*/
	/*--------------------------------------------------------------*/
	
	private String sim=null;
	private String tree=null;
	private String out=null;
	
	/*--------------------------------------------------------------*/
	
	private long linesProcessed=0;
	private long linesOut=0;
	private long bytesProcessed=0;
	private long bytesOut=0;
	private long taxa=0;
	
	/*--------------------------------------------------------------*/
	/*----------------         Final Fields         ----------------*/
	/*--------------------------------------------------------------*/
	
	private String[] header;
	//private final FileFormat ffin;
	//private final FileFormat ffout;
	
	/*--------------------------------------------------------------*/
	/*----------------        Common Fields         ----------------*/
	/*--------------------------------------------------------------*/
	
	//private PrintStream outstream=System.err;
	public static boolean verbose=false;
	public boolean errorState=false;
	private boolean overwrite=false;
	private boolean append=false;
	/**Output stream that output statistics are piped through to the output file. */
	private java.io.PrintStream outstream=System.err;
	
	
}
