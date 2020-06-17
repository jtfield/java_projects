package jasper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import shared.Timer;
import stream.Read;



public class TreeRead {
	
	/*--------------------------------------------------------------*/
	/*----------------        Initialization        ----------------*/
	/*--------------------------------------------------------------*/

	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		//Start a timer immediately upon code entrance.
		Timer t=new Timer();
		
		//Create an instance of this class
		TreeRead x=new TreeRead(args);
		
		//Run the object
		x.process(t);
		

	}

	public TreeRead(String[] args){
		
		String arg=args[0];
		String[] split=arg.split("=");
		String a=split[0].toLowerCase();
		String b=split.length>1 ? split[1] : null;
		if(b!=null && b.equalsIgnoreCase("null")){b=null;}
		in = b;
	}
	
	/*--------------------------------------------------------------*/
	/*----------------    Initialization Helpers    ----------------*/
	/*--------------------------------------------------------------*/
	
	
	
	/*--------------------------------------------------------------*/
	/*----------------         Outer Methods        ----------------*/
	/*--------------------------------------------------------------*/
	
	
	
	/*--------------------------------------------------------------*/
	/*----------------         Inner Methods        ----------------*/
	/*--------------------------------------------------------------*/
	
	void process(Timer t) throws FileNotFoundException, IOException{
		
		ArrayList<TreeNode> nodes=new ArrayList<TreeNode>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(in))) {
	        String line;
	        
	        while ((line = br.readLine()) != null) {
	        	
	        	
	        	//if line is the header line, split and assign to variable.
	        	if(line.startsWith("#")) {header=line.split("\t");
	        	} else {
	        		String[] data = line.split("\t");
	        		//System.out.println(data[0]);
	        		if(!Arrays.asList(header).contains(data[0])) {
	        			TreeNode orgName = new TreeNode(data[0], null, data[1]);
	        			nodes.add(orgName);
	        		}
	        	}
	        }
		}
		for(TreeNode node : nodes) {
			System.out.println(node.parent);
		}
		System.out.println(nodes);
	}
	
	
	/*--------------------------------------------------------------*/
	/*----------------            Fields            ----------------*/
	/*--------------------------------------------------------------*/
	
	private String in=null;
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
	
	
	
}
