import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Boris Borelly
 * 009813375
 * CS 311
 * October 16, 2014
 * Project #1
 * 
 * 
 * To run:
 * Use Javac to compile the source files
 * The program will then read input in from the file in a specific format 
 * The output for each FSA and its' test strings will be output into console
 * I have copy/pasted my console contents into the output.txt file
 *
 */
public class Driver {

	public static void main(String[] args) throws FileNotFoundException {
		File inputFile = new File("input.txt");
		Scanner input = new Scanner(inputFile);
		
		int TOTAL_STATES = 0;
		int[] FINAL_STATES = null;
		String[] ALPHABET = null;
		String[] DELTA = null; 
		ArrayList INPUTS = new ArrayList();
		boolean[] FINAL = null;
		int counter = 0; //line counter
		
		
		ArrayList temp = new ArrayList();
		
		while (input.hasNextLine()) { 
			String line = input.nextLine();
			
			if (counter == 0) 
				TOTAL_STATES = Integer.parseInt(line); 
			
			else if (counter == 1) {	//create the set of final states & the final boolean
				String[] splits = line.split(" ");
				FINAL_STATES = new int[splits.length];
				FINAL = new boolean[TOTAL_STATES]; //FINAL[n-1]
				for (int i=0; i<splits.length; ++i) { 
					FINAL_STATES[i] = Integer.parseInt(splits[i]);
					FINAL[Integer.parseInt(splits[i])] = true;
				}
            }
			
			else if (counter == 2)  
				ALPHABET = line.split(" ");
			
			else if (counter == 3) { //begin processing transition table until do not see '('
				temp = new ArrayList();
				temp.add(line);				
				int transitionCounter = 1;
				
				while (line.startsWith("(")) {
					line = input.nextLine();
					temp.add(line);
					transitionCounter++;
				}
				DELTA = new String[transitionCounter - 1];
				
				for (int i=0; i<DELTA.length; i++) { //copy temp array into DELTA[]
					DELTA[i] = (String) temp.get(i);
				}
				
				INPUTS.add(temp.get(transitionCounter - 1)); //first string after transition gets tored into the end of temp
			}
			else {
				if (!line.startsWith(".")) { //line is not a delimiter 
					INPUTS.add(line);
				}
				else { //Delimiter is found
					FSA fsa = new FSA(TOTAL_STATES, FINAL_STATES, FINAL, ALPHABET, DELTA, INPUTS);
					
					fsa.print();
					fsa.run();
					System.out.println("\n....................\n");
					//reset temp array and line counter 
					temp = null;
					INPUTS = new ArrayList();
					counter = -1;
				}
			}
			counter++;
		}	//end while(input.hasNextLine())
	}	//main
}
