package template;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import fileIO.ByteStreamWriter;
import fileIO.FileFormat;
import fileIO.ReadWrite;
import shared.Parser;
import shared.PreParser;
import shared.Shared;
import shared.Timer;
import stream.ConcurrentReadInputStream;
import stream.ConcurrentReadOutputStream;
import stream.Read;
import structures.ListNum;
import structures.LongList;

/**
 * Read in file of high-throughput reads and report the positions at which kmers from reference start
 * @author Jasper Toscani Field
 * @date Jun 4, 2020
 *
 */
public class KmerPosition {

	public static void main(String[] args){
		//Start a timer immediately upon code entrance.
		Timer t=new Timer();
		
		//Create an instance of this class
		KmerPosition x=new KmerPosition(args);
		
		//Run the object
		x.process(t);
		
		//Close the print stream if it was redirected
		Shared.closeStream(x.outstream);
	}
	
	public KmerPosition(String[] args){
		
		{//Preparse block for help, config files, and outstream
			PreParser pp=new PreParser(args, getClass(), false);
			args=pp.args;
			outstream=pp.outstream;
		}
		
		Parser parser=new Parser();
		for(int i=0; i<args.length; i++){
			String arg=args[i];
			String[] split=arg.split("=");
			String a=split[0].toLowerCase();
			String b=split.length>1 ? split[1] : null;
			if(b!=null && b.equalsIgnoreCase("null")){b=null;}

			if(a.equals("parse_flag_goes_here")){
				//Set a variable here
				
			}else if(a.equals("ref")){
				ref=b;
					
			}else if(a.equals("k")){
					k=Integer.parseInt(b);
			}else if(parser.parse(arg, a, b)){
				//do nothing
			}else{
				//				throw new RuntimeException("Unknown parameter "+args[i]);
				assert(false) : "Unknown parameter "+args[i];
				outstream.println("Unknown parameter "+args[i]);
			}
		}
		
		{//Process parser fields
			Parser.processQuality();
			
			maxReads=parser.maxReads;
			in1=parser.in1;
			out1=parser.out1;
		}
		
		ffout1=FileFormat.testOutput(out1, FileFormat.TXT, null, true, true, false, false);
		ffin1=FileFormat.testInput(in1, FileFormat.FASTQ, null, true, true);
		ffref=FileFormat.testInput(ref, FileFormat.FASTA, null, true, true);
	}
	
	void process(Timer t){
		HashSet<String> kr=kmerReturn();
		
		final ConcurrentReadInputStream cris;
		{
			cris=ConcurrentReadInputStream.getReadInputStream(maxReads, true, ffin1, null);
			cris.start();
		}
		boolean paired=cris.paired();
		
		long readsProcessed=0, basesProcessed=0;
		{
			
			ListNum<Read> ln=cris.nextList();
			ArrayList<Read> reads=(ln!=null ? ln.list : null);
			
			if(reads!=null && !reads.isEmpty()){
				Read r=reads.get(0);
				assert((ffin1==null || ffin1.samOrBam()) || (r.mate!=null)==cris.paired());
			}
			
			while(ln!=null && reads!=null && reads.size()>0){//ln!=null prevents a compiler potential null access warning
				if(verbose){outstream.println("Fetched "+reads.size()+" reads.");}
				
				for(int idx=0; idx<reads.size(); idx++){
					final Read r1=reads.get(idx);
					readsProcessed+=r1.pairCount();
					basesProcessed+=r1.pairLength();
					
					//  *********  Process reads here  *********
					processRead(r1, kr);
					
				}

				cris.returnList(ln);
				if(verbose){outstream.println("Returned a list.");}
				ln=cris.nextList();
				reads=(ln!=null ? ln.list : null);
			}
			if(ln!=null){
				cris.returnList(ln.id, ln.list==null || ln.list.isEmpty());
			}
		}
		errorState=ReadWrite.closeStreams(cris) | errorState;
		if(verbose){outstream.println("Finished reading data.");}
		
		outputResults();
		
		t.stop();
		outstream.println("Time:                         \t"+t);
		outstream.println("Reads Processed:    "+readsProcessed+" \t"+String.format(Locale.ROOT, "%.2fk reads/sec", (readsProcessed/(double)(t.elapsed))*1000000));
		assert(!errorState) : "An error was encountered.";
	}
	
	private void outputResults(){
		if(ffout1==null) {return;}
		ByteStreamWriter bsw=new ByteStreamWriter(ffout1);
		bsw.start();
		
		//Write stuff to the bsw
		bsw.println("Stuff");

		errorState=bsw.poisonAndWait() | errorState;
	}
	
	private HashSet<String> kmerReturn(){
		HashSet<String> hs=new HashSet<String>();
		ArrayList<Read> readArray=ConcurrentReadInputStream.getReads(maxReads, false, ffref, null, null, null);
		for(Read r : readArray) {
			addToSet(hs, r);
		}
		
		System.out.println(hs);
		
		return hs;
	}
	
	private int addToSet(HashSet<String> hs, Read r) {
		int countRead=0;
		for(int i=0, j=k; j<=r.length(); i++, j++) {
			//String(byte[] bytes, int offset, int length)
			String s=new String(r.bases, i, k);
			hs.add(s);
			countRead++;
		}
		return countRead;
	}
	
	private void processRead(Read r, HashSet<String> hs) {
		for(int i=0, j=k; j<=r.length(); i++, j++) {
			//String(byte[] bytes, int offset, int length)
			
			String s=new String(r.bases, i, k);
			totalEncounter.increment(i);
			if(hs.contains(s)) {
				counts.increment(i);
			}
			
		}
	}
	
	/*--------------------------------------------------------------*/
	
	/*--------------------------------------------------------------*/
	
	private String in1=null;
	private String out1=null;
	private String ref=null;
	
	private final FileFormat ffin1;
	private final FileFormat ffout1;
	private final FileFormat ffref;
	
	/*--------------------------------------------------------------*/

	private long maxReads=-1;
	private boolean errorState=false;
	private int k=6;
	private LongList counts=new LongList();
	private LongList totalEncounter=new LongList();
	
	/*--------------------------------------------------------------*/
	
	private java.io.PrintStream outstream=System.err;
	public static boolean verbose=false;
	
}
