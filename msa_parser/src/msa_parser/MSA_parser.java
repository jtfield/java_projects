package msa_parser;
import java.io.*;


public class MSA_parser {

	public static void main(String[] args) throws Exception 
	{
		// TODO Auto-generated method stub
		int i = 0, j;
        String arg;
        char flag;
        boolean vflag = false;
        String inputfile = "";
        String st;

        while (i < args.length && args[i].startsWith("-")) {
            arg = args[i++];
            
         // use this type of check for arguments that require arguments
            if (arg.equals("-align")) {
                if (i < args.length)
                    inputfile = args[i++];
                else
                    System.err.println("-align requires a .fasta filename");
                if (vflag)
                    System.out.println("input file = " + inputfile);
            }
        }
        
        //open fasta file
        BufferedReader br = new BufferedReader(new FileReader(inputfile));
        
        while ((st = br.readLine()) != null) 
			System.out.println(st);
        
		/*
		File file = new File(args[0]); 
		  
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		  
		String st; 
		while ((st = br.readLine()) != null) 
			System.out.println(st); 
		*/
	}

}
