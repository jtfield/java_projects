package template;

import java.io.PrintStream;

import fileIO.ByteFile;
import fileIO.ByteFile1;
import fileIO.ByteFile2;
import fileIO.ByteStreamWriter;
import fileIO.FileFormat;
import fileIO.ReadWrite;
import shared.Parser;
import shared.PreParser;
import shared.Shared;
import shared.Timer;
import shared.Tools;
import structures.ByteBuilder;

/**
 * @author Brian Bushnell
 * @date April 9, 2020
 *
 */
public class A_SampleBasic {
	
	/*--------------------------------------------------------------*/
	/*----------------        Initialization        ----------------*/
	/*--------------------------------------------------------------*/
	
	/**
	 * Code entrance from the command line.
	 * @param args Command line arguments
	 */
	public static void main(String[] args){
		//Start a timer immediately upon code entrance.
		Timer t=new Timer();
		
		//Create an instance of this class
		A_SampleBasic x=new A_SampleBasic(args);
		
		//Run the object
		x.process(t);
		
		//Close the print stream if it was redirected
		Shared.closeStream(x.outstream);
	}
	
	/**
	 * Constructor.
	 * @param args Command line arguments
	 */
	public A_SampleBasic(String[] args){
		
		{//Preparse block for help, config files, and outstream
			PreParser pp=new PreParser(args, /*getClass()*/null, false);
			args=pp.args;
			outstream=pp.outstream;
		}
		
		//Set shared static variables prior to parsing
		
		{//Parse the arguments
			final Parser parser=parse(args);
			parser.out1="stdout.txt";
			overwrite=parser.overwrite;
			append=parser.append;
			
			in=parser.in1;

			out=parser.out1;
		}
		
		fixExtensions(); //Add or remove .gz or .bz2 as needed
		checkFileExistence(); //Ensure files can be read and written
		checkStatics(); //Adjust file-related static fields as needed for this program

		ffout=FileFormat.testOutput(out, FileFormat.TXT, null, true, overwrite, append, false);
		ffin=FileFormat.testInput(in, FileFormat.TXT, null, true, true);
	}
	
	/*--------------------------------------------------------------*/
	/*----------------    Initialization Helpers    ----------------*/
	/*--------------------------------------------------------------*/
	
	/** Parse arguments from the command line */
	private Parser parse(String[] args){
		
		Parser parser=new Parser();
		for(int i=0; i<args.length; i++){
			String arg=args[i];
			String[] split=arg.split("=");
			String a=split[0].toLowerCase();
			String b=split.length>1 ? split[1] : null;
			if(b!=null && b.equalsIgnoreCase("null")){b=null;}

			if(a.equals("verbose")){
				verbose=Tools.parseBoolean(b);
				ByteFile1.verbose=verbose;
				ByteFile2.verbose=verbose;
				ReadWrite.verbose=verbose;
			}else if(parser.parse(arg, a, b)){
				//do nothing
			}else{
				outstream.println("Unknown parameter "+args[i]);
				assert(false) : "Unknown parameter "+args[i];
				//				throw new RuntimeException("Unknown parameter "+args[i]);
			}
		}
		
		return parser;
	}
	
	/** Add or remove .gz or .bz2 as needed */
	private void fixExtensions(){
		in=Tools.fixExtension(in);
		if(in==null){throw new RuntimeException("Error - at least one input file is required.");}
	}
	
	/** Ensure files can be read and written */
	private void checkFileExistence(){
		//Ensure output files can be written
		if(!Tools.testOutputFiles(overwrite, append, false, out)){
			outstream.println((out==null)+", "+out);
			throw new RuntimeException("\n\noverwrite="+overwrite+"; Can't write to output file "+out+"\n");
		}
		
		//Ensure input files can be read
		if(!Tools.testInputFiles(false, true, in)){
			throw new RuntimeException("\nCan't read some input files.\n");  
		}
		
		//Ensure that no file was specified multiple times
		if(!Tools.testForDuplicateFiles(true, in, out)){
			throw new RuntimeException("\nSome file names were specified multiple times.\n");
		}
	}
	
	/** Adjust file-related static fields as needed for this program */
	private static void checkStatics(){
		//Adjust the number of threads for input file reading
		if(!ByteFile.FORCE_MODE_BF1 && !ByteFile.FORCE_MODE_BF2 && Shared.threads()>2){
			ByteFile.FORCE_MODE_BF2=true;
		}
	}
	
	/*--------------------------------------------------------------*/
	/*----------------         Outer Methods        ----------------*/
	/*--------------------------------------------------------------*/
	
	void process(Timer t){
		
		ByteFile bf=ByteFile.makeByteFile(ffin);
		ByteStreamWriter bsw=makeBSW(ffout);
		
//		assert(false) : "Header goes here.";
		if(bsw!=null){
//			assert(false) : "Header goes here.";
		}
		
		processInner(bf, bsw);
		
		errorState|=bf.close();
		if(bsw!=null){errorState|=bsw.poisonAndWait();}
		
		t.stop();
		
		outstream.println(Tools.timeLinesBytesProcessed(t, linesProcessed, bytesProcessed, 8));
		
		outstream.println();
		outstream.println("Valid Lines:       \t"+linesOut);
		outstream.println("Invalid Lines:     \t"+(linesProcessed-linesOut));
		
		if(errorState){
			throw new RuntimeException(getClass().getName()+" terminated in an error state; the output may be corrupt.");
		}
	}
	
	/*--------------------------------------------------------------*/
	/*----------------         Inner Methods        ----------------*/
	/*--------------------------------------------------------------*/
	
	private void processInner(ByteFile bf, ByteStreamWriter bsw){
		byte[] line=bf.nextLine();
		byte[] line2=bf.nextLine();
		byte[] line3=bf.nextLine();
		byte[] line4=bf.nextLine();
		ByteBuilder bb=new ByteBuilder();
		ByteBuilder bb_2=new ByteBuilder();
		
		
		while(line!=null && line2!=null && line3!=null && line4!=null){
			if(line.length>0){
				//linesProcessed++;
				linesProcessed++;
				//linesProcessed++;
				//linesProcessed++;
				//bytesProcessed+=(line.length+1);
				bytesProcessed+=(line2.length+1);
				//bytesProcessed+=(line3.length+1);
				//bytesProcessed+=(line4.length+1);

				if(true){
					linesOut++;
					//bytesOut+=(line.length+1);
					bytesOut+=(line2.length+1);
					//bytesOut+=(line3.length+1);
					//bytesOut+=(line4.length+1);
					//bb.append(line);
					//bb.append("+");
					bb.append(line2);
					//bb.append(line3);
					//bb.append(line4);
					//for(int i=0; i<line.length && line[i]!='\t'; i++){
					//	bb.append(line[i]);
					//}
					bb.nl();
					bsw.print(bb);
					byte[] b=bb.toBytes();
					int half_len = (b.length / 2);
					for(int i=0; i<half_len && b[i]!='\t'; i++) {
						if(b[i]==b[(b.length - 1) - i]){bsw.print(b[i]);bsw.print(i);}
						//bsw.print(b[(b.length - 1) - i]);
						
					}
					//if(b[0]==b[half_len]){bsw.print(b);} 
					
					
					//bsw.print(bb.toBytes());
					
					/*Get info from Brian about how to split combined line without converting back to string*/
					//String string_bb = bb.toString();
					
					//String[] parts_bb = string_bb.split("\\+");
					//String seq = parts_bb[1];
					//bb_2.append(seq);
					//int half_len = (seq.length() / 2);
					//bsw.print(half_len);
					//for(int i=0; i<half_len && seq[i]!='\t'; i++) {
					//	if(seq.charAt(i)==seq.charAt(seq.length()-i)){bsw.print(i);};
					//}
					bsw.print("\n\n\n");
					//if(seq.charAt(0)==seq.charAt(seq.length()-1)){bsw.print("waffle");};
					
					
					bb.clear();
				}
			}
			line=bf.nextLine();
			line2=bf.nextLine();
			line3=bf.nextLine();
			line4=bf.nextLine();
		}
	}
	
	private static ByteStreamWriter makeBSW(FileFormat ff){
		if(ff==null){return null;}
		ByteStreamWriter bsw=new ByteStreamWriter(ff);
		bsw.start();
		return bsw;
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
	
	private long seq_lines=0;
    private long seq_blocks=0;
    private long loop_block=0;
    private long first_seq=0;
	
	/*--------------------------------------------------------------*/
	/*----------------         Final Fields         ----------------*/
	/*--------------------------------------------------------------*/
	
	private final FileFormat ffin;
	private final FileFormat ffout;
	
	/*--------------------------------------------------------------*/
	/*----------------        Common Fields         ----------------*/
	/*--------------------------------------------------------------*/
	
	private PrintStream outstream=System.err;
	public static boolean verbose=false;
	public boolean errorState=false;
	private boolean overwrite=false;
	private boolean append=false;
	
}
