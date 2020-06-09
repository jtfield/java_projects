package template;

import java.util.ArrayList;
import java.util.Locale;

import dna.AminoAcid;
import fileIO.ByteStreamWriter;
import fileIO.FileFormat;
import fileIO.ReadWrite;
import shared.Parser;
import shared.PreParser;
import shared.Shared;
import shared.Timer;
import shared.Tools;
import stream.ConcurrentReadInputStream;
import stream.Read;
import structures.ListNum;
import structures.LongHashSet;
import structures.LongList;

/**
 * Read in file of high-throughput reads sequences and a reference sequence file <br>
 * report the positions in the reads that are the start of a matching kmer sequence
 * between the read and the reference sequence. <br>
 * 
 * read = ACGTA <br>
 * reference = ATGTACC
 * kmer length = 3
 * match = GTA, beginning in the read at position 2 (zero indexed).
 * returned info = #positions, #number of kmers beginning at that position, #percentage of reads with kmers beginning at that positons
 * 
 * 
 * @author Jasper Toscani Field
 * @date Jun 4, 2020
 *
 */
public class KmerPosition3 {

	public static void main(String[] args){
		//Start a timer immediately upon code entrance.
		Timer t=new Timer();
		
		//Create an instance of this class
		KmerPosition3 x=new KmerPosition3(args);
		
		//Run the object
		x.process(t);
		
		//Close the print stream if it was redirected
		Shared.closeStream(x.outstream);
	}
	
	/**
	 * Handles pre-parsing and parsing of user flags
	 * Reads in the read file(s) and the reference files
	 * Sets the maximum number of reads to be processed
	 * 
	 * @param args: string of the arguments input at the commandline
	 * 
	 */
	public KmerPosition3(String[] args){
		
		{//Preparse block for help, config files, and outstream
			PreParser pp=new PreParser(args, getClass(), false);
			args=pp.args;
			outstream=pp.outstream;
		}
		
		//Primary parsing of arguments not handled by preparser
		Parser parser=new Parser();
		
		//loop through arguments up to the maximum number of arguments input
		for(int i=0; i<args.length; i++){
			//Grab argument string at index
			String arg=args[i];
			
			//split argument string on "="
			String[] split=arg.split("=");
			
			//convert the left side to lowercase
			String a=split[0].toLowerCase();
			
			//Ternary conditional statement: is the length of the split greater than 1 (thus, an actual input)?
			//if so, the right side of the split is the b variable, if not, b is null
			String b=split.length>1 ? split[1] : null;
			
			//if b isn't null but a string "null" was input, convert b to null
			if(b!=null && b.equalsIgnoreCase("null")){b=null;}

			//unused statement? does nothing currently. start here for adding new flag parsing
			if(a.equals("parse_flag_goes_here")){
				
			//handle reference variable assignment
			}else if(a.equals("ref")){
				ref=b;
			
			//handle kmer variable assignment
			}else if(a.equals("k")){
					k=Integer.parseInt(b);
					
			//Parses in and out flags, handles all flags not recognized earlier in class
			}else if(parser.parse(arg, a, b)){
				
			//if not one of the known parameters, let the user know they made a mistake
			}else{
				//				throw new RuntimeException("Unknown parameter "+args[i]);
				assert(false) : "Unknown parameter "+args[i];
				outstream.println("Unknown parameter "+args[i]);
			}
		}
		
		{//Handle quality scoring
			Parser.processQuality();
			
			//appropriate argument passing
			maxReads=parser.maxReads;
			in1=parser.in1;
			in2=parser.in2;
			out1=parser.out1;
		}
		
		//File format handling for each file
		ffout1=FileFormat.testOutput(out1, FileFormat.TXT, null, true, true, false, false);
		ffin1=FileFormat.testInput(in1, FileFormat.FASTQ, null, true, true);
		ffin2=FileFormat.testInput(in2, FileFormat.FASTQ, null, true, true);
		ffref=FileFormat.testInput(ref, FileFormat.FASTA, null, true, true);
	}
	
	/**
	 * Primary processing function
	 * 
	 * @param t: Timer object
	 */
	void process(Timer t){
		
		//Creates a hash set of kmers found in the reference of length k, advancing 1 position each step
		LongHashSet refKmerSet=kmerReturn();
		
		//instantiate the input stream
		final ConcurrentReadInputStream cris;
		{
			cris=ConcurrentReadInputStream.getReadInputStream(maxReads, true, ffin1, ffin2);
			cris.start();
		}
		boolean paired=cris.paired();
		//System.out.println(paired);
		
		long readsProcessed=0, basesProcessed=0;
		{
			//returns listNum of 200 reads,
			//minimize function calling to reduce inter-thread communication
			ListNum<Read> ln=cris.nextList();
			
			//prevents null pointer exception if ListNum is null
			//meaning file was empty
			ArrayList<Read> reads=(ln!=null ? ln.list : null);
			
			//pulls first read in list, and checks if paired input was selected and 
			//determines if this is correct
			if(reads!=null && !reads.isEmpty()){
				Read r=reads.get(0);
				
				assert((ffin1==null || ffin1.samOrBam()) || (r.mate!=null)==cris.paired());
			}
			
			//loop while more reads available
			while(ln!=null && reads!=null && reads.size()>0){//ln!=null prevents a compiler potential null access warning
				if(verbose){outstream.println("Fetched "+reads.size()+" reads.");}
				
				//loop through every read in list
				for(int idx=0; idx<reads.size(); idx++){
					final Read r1=reads.get(idx);
					final Read r2=r1.mate;
					//handles incrementing the number of reads processed if paired or unpaired
					readsProcessed+=r1.pairCount();
					basesProcessed+=r1.pairLength();
					
					//  *********  Process reads here  *********
					//Pass read 1 (and 2 if paired) and the reference kmer set, with the positional count list 
					processRead(r1, refKmerSet, counts1, totalEncounter1);
					if(r1.mate!=null) {
						processRead(r2, refKmerSet, counts2, totalEncounter2);
					}
				}

				//when done processing list, return to input stream to
				//notify we're ready for new list of reads
				cris.returnList(ln);
				
				if(verbose){outstream.println("Returned a list.");}
				
				//Grab next list
				ln=cris.nextList();
				reads=(ln!=null ? ln.list : null);
			}
			
			//if no list returned on line 188, return list now
			if(ln!=null){
				cris.returnList(ln.id, ln.list==null || ln.list.isEmpty());
			}
		}
		
		//close stream after processing file, collect error if encountered
		errorState=ReadWrite.closeStreams(cris) | errorState;
		if(verbose){outstream.println("Finished reading data.");}
		
		//writes output to file indicated by user
		outputResults(counts1, totalEncounter1, counts2, totalEncounter2);
		
		//stop timer after all processes have competed and before printing runtime
		t.stop();
		outstream.println("Time:                         \t"+t);
		outstream.println("Reads Processed:    "+readsProcessed+" \t"+String.format(Locale.ROOT, "%.2fk reads/sec", (readsProcessed/(double)(t.elapsed))*1000000));
		
		//If an error statement was encountered, report this and crash program (exit with 1, not 0)
		assert(!errorState) : "An error was encountered.";
	}
	
	/**
	 * This function converts the list of counts for kmers found at each position in both read sets and 
	 * the positions that have reads of length reaching that position, to arrays
	 * The statistics of the analyses are written to the output file
	 * 
	 * @param posCounts1 list of length of the reads, tracking the number of kmers found beginning at each position for readset 1
	 * @param readCounts1 list of max length of the reads, tracking the number of reads with nucleotides at that position for readset 1
	 * @param posCounts2 Same for readset 2
	 * @param readCounts2 Same for readset 2
	 */
	private void outputResults(LongList posCounts1, LongList readCounts1, LongList posCounts2, LongList readCounts2){
		//Makes sure a valid output file name exists
		if(ffout1==null) {return;}
		
		//begins output writer method
		ByteStreamWriter bsw=new ByteStreamWriter(ffout1);
		bsw.start();
		
		//converts count lists to arrays for indexing
		long[] readArray1 = readCounts1.toArray();
		long[] countArray1 = posCounts1.toArray();
		long[] readArray2 = readCounts2.toArray();
		long[] countArray2 = posCounts2.toArray();
		
		//writes the header line to the output file
		bsw.println("#pos\tread1_count\tread1_perc\tread2_count\tread2_perc");
		
		//finds the maximum of both read set lengths
		//this handles if one read set is longer than the other
		int maxLen = Tools.max(readArray1.length, readArray2.length);
		
		//iterate to the length of the longest read (if the value exists, otherwise report 0) and
		//write counts to output file
		for(int i=0; i<maxLen; i++) {
			
			bsw.print(i);
			bsw.print('\t');
			bsw.print(countArray1.length>i ? countArray1[i] : 0);
			bsw.print('\t');
			bsw.print(countArray1.length>i ? (countArray1[i] / (float) readArray1[i]) * 100 : 0, 3);
			bsw.print('\t');
			bsw.print(countArray2.length>i ? countArray2[i] : 0);
			bsw.print('\t');
			bsw.print(countArray2.length>i ? (countArray2[i] / (float) readArray2[i]) * 100 : 0, 3);
			bsw.println();
		}
		
		//stop the output stream by telling separate thread no more data is incoming
		//but to finish writing already passed data
		errorState=bsw.poisonAndWait() | errorState;
	}
	
	/**
	 * This method produces a LongHashSet containing all forward kmers of length k found in the reference file
	 * reference kmers are converted to bytes for faster, memory efficient comparison to read kmers
	 * 
	 * @return A set of reference kmers
	 */
	private LongHashSet kmerReturn(){
		
		//initialize empty LongHashSet to accept reference kmers
		LongHashSet hs=new LongHashSet();
		
		//get the reference sequences from the ref file
		ArrayList<Read> readArray=ConcurrentReadInputStream.getReads(maxReads, false, ffref, null, null, null);
		
		//iterate over sequences pulled from reference file and pass the sequence to the byte conversion method
		for(Read r : readArray) {
			addToSet(hs, r);
		}
		
		//System.out.println(hs);
		
		return hs;
	}
	
	/**
	 * This method separates the reference sequence into kmers before converting each kmer to bytes
	 * The converted kmers are added to the hashset and returned to the main method for final comparison
	 * 
	 * @param hs hashset that will hold the kmers after conversion to bytes
	 * @param r the reference sequence
	 * @return Number of kmers added to hashset
	 */
	private int addToSet(LongHashSet hs, Read r) {
		int proccessedKmers=0;
		/*for(int i=0, j=k; j<=r.length(); i++, j++) {
			//String(byte[] bytes, int offset, int length)
			String s=new String(r.bases, i, k);
			hs.add(s);
			countRead++;
		}*/
		
		//Convert kmer sequences to 2-bit notation, shifting each kmer by one nucleotide
		final int shift=2*k;
		
		//Make mask of 1's for the last k*2 bits
		final long mask=(shift>63 ? -1L : ~((-1L)<<shift));
		if(verbose) {System.out.println(Long.toBinaryString(mask));}
		
		//number of consecutive, valid (non-degenerate) bases
		int len=0;
		
		//access read objects bases
		byte[] bases=r.bases;
		
		//binary representation of current kmer
		long kmer=0;
		
		//iterate over bases in read
		for(int i=0; i<bases.length; i++){
			byte b=bases[i];
			
			//Fast nucleotide conversion from letter to 2-bit encoding
			long x=AminoAcid.baseToNumber[b];
			
			//Shift kmer to left, dropping oldest base, OR in new base on right
			kmer=((kmer<<2)|x)&mask;
			
			
			//ACGTTNTGCGC
			
			//This section handles creating kmers without degenerate bases at any position
			//Check new base is not degenerate (-1) and increment length of kmer
			if(x>=0){
				len++;
			}else{
				//If base is degenerate (-1), restart kmer construction by setting length to 0
				len=0;
				kmer=0;
			}
			
			//Once kmer reaches length k, add kmer to hashset and increment number of processed kmers
			if(len>=k){
				hs.add(kmer);
				proccessedKmers++;
			}
		}
	
		return proccessedKmers;
	}
	
	/**
	 * This method performs byte shifting to perform very fast comparison of kmers from the reads and
	 * compares read kmers to reference kmers. If identical byte-kmers are found, increment the appropriate counts.
	 * Also, increment the count if a valid nucleotide is at that position in the read.
	 * 
	 * @param r read sequence
	 * @param hs set of kmer sequences from the reference
	 * @param count list of counts of the number of kmers found starting at each position
	 * @param readCount list of counts of reads with nucleotides at each position
	 */
	private void processRead(Read r, LongHashSet hs, LongList count, LongList readCount) {
		/*for(int i=0, j=k; j<=r.length(); i++, j++) {
			//String(byte[] bytes, int offset, int length)

			//int x=5;
			String s=new String(r.bases, i, k);
			readCount.increment(i);
			if(hs.contains(s)) {
				count.increment(i);
			}
			//System.out.println(totalEncounter);
//			System.out.println(counts);

		}*/
		//Convert kmer sequences to 2-bit notation, shifting each kmer by one nucleotide
		final int shift=2*k;

		//Make mask of 1's for the last k*2 bits
		final long mask=(shift>63 ? -1L : ~((-1L)<<shift));
		if(verbose) {System.out.println(Long.toBinaryString(mask));}

		//number of consecutive, valid (non-degenerate) bases
		int len=0;

		//access read objects bases
		byte[] bases=r.bases;

		//binary representation of current kmer
		long kmer=0;

		//iterate over bases in read up to the length of the read
		for(int i=0; i<bases.length; i++){
			byte b=bases[i];

			//Fast nucleotide conversion from letter to 2-bit encoding
			long x=AminoAcid.baseToNumber[b];

			//Shift kmer to left, dropping oldest base, OR in new base on right
			kmer=((kmer<<2)|x)&mask;


			//ACGTTNTGCGC

			//This section handles creating kmers without degenerate bases at any position
			//Check new base is not degenerate (-1) and increment length of kmer
			if(x>=0){
				len++;
			}else{
				
				//If base is degenerate (-1), restart kmer construction by setting length to 0
				len=0;
				kmer=0;
			}
			if(len>=k){
				
				//increment list of counts for reads containing nucleotides at each position
				//i - k + 1 is the first base of kmer, i is last base in kmer
				//we want start positions
				readCount.increment(i - k + 1);
				
				//if the read kmer is in the hashset of kmers from the reference,
				//increment count of positions corresponding to the start of a kmer at that position
				if(hs.contains(kmer)) {
					count.increment(i - k + 1);
				}
			}
		}

	}
	


	//A -> 0 -> 00
	//C -> 1 -> 01
	//G -> 2 -> 10
	//T -> 3 -> 11
	//N -> -1 -> 11111111111111111111111111111111111111111
	
	//00110110 -> ATCG
	
	
	//kmer=00000000
	//Add T, 11                           G A A A        G A A A A
	//left shift:  kmer=kmer<<2  -> kmer=10000000<<2 -> 1000000000
	//kmer=00000000                                     0011111111
	//Or it with the new code                           0000000000
	//kmer=kmer|x  ->  kmer=00000000 | 11 -> 0000000011
	//mask it with the mask:
	//kmer=kmer&mask -> kmer=00000000 & 11111111 -> 00000011
	
	//kmer=00000011
	//Add C, 01
	//left shift : -> 00001100
	//Or it with the new code:                A A T C
	//kmer=kmer|x  ->  kmer=00001100 | 01 -> 00001101
	//mask (does nothing)
	
	// kmer= TATC = 11001101
	//Add G, 10
	// left-shift:  1100110100 (TATCA)
	//Or with the new code: kmer=1100110100 | 10 = 1100110110 = TATCG
	//Mask with 11111111 (TTTT): 1100110110
	//                             11111111
	//yields                       00110110 = ATCG

	//This should be a class field.
//	LongHashSet lhs=new LongHashSet();
	/*public final void hashSimple(byte[] bases){
		final int shift=2*k;
		final long mask=(shift>63 ? -1L : ~((-1L)<<shift));
		int len=0;
		
		
		long kmer=0;
		for(int i=0; i<bases.length; i++){
			byte b=bases[i];
			long x=AminoAcid.baseToNumber[b];
			kmer=((kmer<<2)|x)&mask;
			
			
			//ACGTTNTGCGC
			
			if(x>=0){
				len++;
			}else{
				len=0;
				kmer=0;
			}
			if(len>=k){
				lhs.add(kmer);
			}
		}
	}*/
	
	/*--------------------------------------------------------------*/
	
	/*--------------------------------------------------------------*/
	
	/**Primary input file for high-throughput read sequences*/
	private String in1=null;
	
	/**Paired-end read input file. Only use if this file contains the mates of in1*/
	private String in2=null;
	
	/**Output file name. This file will contain all output statistics of kmer positioning and counts*/
	private String out1=null;
	
	/**Reference sequence file. This file should be .fasta format and 
	 * contain reference sequences you wish to be identified in the read files */
	private String ref=null;
	
	/**File format structure FileFormat for in1. This is used to parse the input file type if possible and provide methods.*/
	private final FileFormat ffin1;
	
	/**File format structure FileFormat for in2. This is used to parse the input file type if possible and provide methods.*/
	private final FileFormat ffin2;
	
	/**File format structure FileFormat for out1. This provides methods and structure to the output.*/
	private final FileFormat ffout1;
	
	/**File format structure of ref. This provides methods and structure for the reference file assuming its in FASTA format.*/
	private final FileFormat ffref;
	
	/*--------------------------------------------------------------*/

	/**Variable for the number of reads to analyze. If set to -1, all reads will be used */
	private long maxReads=-1;
	
	/**Boolean variable that changes to true if an error has occurred. 
	 * This will cause the program to exit with 1 status. */
	private boolean errorState=false;
	
	/**Variable for kmer length. Can be changed on the commandline with the k=# flag.*/
	private int k=6;
	
	/**List of Long containing counts of kmers starting at each nucleotide in a read for read set 1. */
	private LongList counts1=new LongList();
	
	/**List of Long containing counts of reads that have nucleotides at a each position for read set 1. */
	private LongList totalEncounter1=new LongList();
	
	/**List of Long containing counts of kmers starting at each nucleotide in a read for read set 2. */
	private LongList counts2=new LongList();
	
	/**List of Long containing counts of reads that have nucleotides at a each position for read set 2. */
	private LongList totalEncounter2=new LongList();
	
	/*--------------------------------------------------------------*/
	
	/**Output stream that output statistics are piped through to the output file. */
	private java.io.PrintStream outstream=System.err;
	
	/**Verbose commandline flag variable. If set to true, will print additional program information. */
	public static boolean verbose=false;
	
}
