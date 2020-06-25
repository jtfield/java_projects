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
		addRelationSims(relationshipTree, matrix);
		
		//Check similarities
		checkSimilarities(relationshipTree, matrix);
		
		//System.out.println(relationshipTree.getNode("sim_genome_2.fa").parSim);
		
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
	void addRelationSims(Tree tree, SimilarityMatrix2 matrix){
		
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
				//System.out.println(parSim);
				keyNode.addParSim(parSim);
			}
			
			for(String kid : childNames) {
				
				if(!tree.getNode(kid).parent.equals("0")) {
					
					double kidSim = matrix.getSimilarity(keyOrg, kid);
					
					keyNode.addChildSim(kid, kidSim);
				}
			}
		}
	}
	
	void checkSimilarities(Tree tree, SimilarityMatrix2 matrix) {

		//Iterate over organisms/nodes in the tree.
		for ( String keyOrg : tree.keySet() ) {

			//If the organism isn't the life/0 node.
			if(!keyOrg.equals("0")) {

				//Get the node from the tree
				TreeNode keyNode = tree.getNode(keyOrg);

				//Identify parent node.
				String parentName = keyNode.getParent();

				//Get the child node names.
				HashSet<String> childNameSet = keyNode.getChildren();

				//Get the organism names present in the matrix.
				HashMap<String, Integer> matrixOrgs = matrix.getHashMap();

				String minChildName = keyNode.minimumDescendantName();
				double minChildSim = keyNode.minimumDescendantSim();

				//HashMap<String, Double> minSimChild = keyNode.minimumDescendantSim();

				//Iterate over the organisms in the matrix.
				for(String matrixOrg : matrixOrgs.keySet()) {

					//if we aren't comparing similarities of the node to itself and
					//if we aren't examining a child node and
					//if we aren't examining a parent node
					if(!keyOrg.equals(matrixOrg) && !childNameSet.contains(matrixOrg) && !matrixOrg.equals(parentName) ) {

						double matrixOrgSim = matrix.getSimilarity(keyOrg, matrixOrg);


						if(matrixOrgSim > minChildSim || matrixOrgSim > keyNode.parSim) {

							System.out.println();
							System.out.println("problem");
							System.out.println("Base org " + keyOrg);
							System.out.println("kid name " + minChildName);
							System.out.println("par name " + parentName);
							System.out.println("other org " + matrixOrg);
							System.out.println("par sim " + keyNode.parSim);
							System.out.println("child sim " + minChildSim);
							System.out.println("matrix sim " + matrixOrgSim);
							
						}

					}
				}
			}
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
