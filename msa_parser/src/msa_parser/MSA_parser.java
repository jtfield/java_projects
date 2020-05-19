package msa_parser;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.*;

public class MSA_parser {

	public static void main(String[] args) throws Exception 
	{
		// TODO Auto-generated method stub
		int j, i = 0;
        String arg;
        char flag;
        boolean vflag = false;
        String inputfile = "";
        String target_taxon = "";
        String st;
        String joined_seqs;
        int start;
        int stop;
        int start_pos;
        int stop_pos;
        String action = null;
        //int arg_count = 0;
        
        
        //initialize ArrayList for storing lines from the alignment
        List<String> list_of_string = new ArrayList<String>();

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            
         // use this type of check for arguments that require arguments
            if (arg.equals("-h")) {
            	System.out.println("Options:\n -action: single or multi\n -align: alignment file \n -taxon_select: taxon you wish to search for if action=single\n ");
            	System.exit(0);
            }
            
            else if (arg.contentEquals("-action")) {
            	if (i < args.length)
            		action = args[i++];
            }
            
            else if (arg.equals("-align")) {
                if (i < args.length)
                    inputfile = args[i++];
                	if (inputfile.endsWith(".fasta") || inputfile.endsWith(".fa") || inputfile.endsWith(".fas")) {
                		System.out.println("Fasta file accepted");
                	}
                	else
                		System.err.println("-align requires a .fasta filename");
                	if (vflag)
                		System.out.println("input file = " + inputfile); 
            }
            
            else if (arg.contentEquals("-slice")) {
            	System.out.println("slicing");
            	start = i;
            	start_pos = i + 1;
            	stop = i + 2;
            	stop_pos = i + 3;
            	
            	System.out.println(args[start]);
            	System.out.println(args[start_pos]);
            	System.out.println(args[stop]);
            	System.out.println(args[stop_pos]);
            }
            
            else if (arg.equals("-taxon")) {
            	if (i < args.length)
            	
            	// why does having the carrot in front of the input name (taxon_target)
            	// cause the program to think the argument is empty? The below .replace doesnt work
                target_taxon = args[i++].replace(">", "");
            else
                System.err.println("-taxon_select requires a taxon name from the alignment");
            if (vflag)
                System.out.println("taxon name = " + target_taxon);
            	
            }
        }
        //initialize ArrayList for storing lines from the alignment
        //List<String> list = new ArrayList<String>();
        
        //open fasta file
        BufferedReader br = new BufferedReader(new FileReader(inputfile));
        
        while ((st = br.readLine()) != null) 
			//System.out.println(st);
            list_of_string.add(st);
        
        if (action.contentEquals("single")) {
        	for (i = 0; i < list_of_string.size(); i++) {
        		if (list_of_string.get(i).replace(">", "").contentEquals(target_taxon))
        			System.out.println("found");
        	}
        }
        
        //joined_seqs = String.join("", list_of_string);
        //System.out.println(joined_seqs);
        
        
        
        
        //if (target_taxon.length() > 0) {
        //	String no_crt_taxon_name = target_taxon.replace(">", "");
        //	for (i = 0; i < list_of_string.size(); i++) {
        //		//System.out.println(list_of_string.get(i));
        //		if (list_of_string.get(i).replace(">", "").contentEquals(no_crt_taxon_name)) {
        //			System.out.println(list_of_string.get(i));
        //			System.out.println(list_of_string.get(i + 1));
        			
        			
        //		}
        //	}
        
        //}
    System.out.println("Program complete.");
	}

}
