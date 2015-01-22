/**
 * @author Boris Borelly
 * 009813375
 * CS 311
 * November 13, 2014
 * Project #2
 * 
 * To run:
 * Use Javac to compile the source files
 * The program will then read input in from the file in a specific format specified in the project directions
 * The output requested in the project description is sent to console
 * I have copy/pasted my console contents into the output.txt file - can be verified with compile
 *
 */
import java.io.*;
import java.util.*;
public class Driver {
	public static void main(String[] args) throws FileNotFoundException { 
		File inputFile = new File("input.txt");
		Scanner input = new Scanner(inputFile);
		
		int total_states = 0;
		int[] final_states = null;
		String[] alpha = null;
		ArrayList<String> transitions = new ArrayList<String>(); 
		boolean[] FINAL = null;	
		
		int counter = 0; //line counter
		int machineCounter = 0; //machine counter
		while (input.hasNextLine()) { 
			String line = input.nextLine();
			
			if (counter == 0) {
				total_states = Integer.parseInt(line);
				FINAL = new boolean[total_states];
			}	
			else if (counter == 1) {	//create the set of final states & the final boolean
				String[] splits = line.split(" ");
				final_states = new int[splits.length];
				
				for (int i=0; i<splits.length; ++i) { 
					final_states[i] = Integer.parseInt(splits[i]);
					FINAL[Integer.parseInt(splits[i])] = true;
				}
            }
			else if (counter == 2)  {
				alpha = line.split(" ");
			}		
			else if (line.startsWith("(")) { //begin processing transition table until delimiter '.' is found
				line = line.substring(1, line.length() -1); //remove parenthesis
				transitions.add(line);
			}
			else { //Delimiter is found, line starts with '.'
				machineCounter++;
				NFSA nfsa = new NFSA(total_states, final_states, FINAL, alpha, transitions);
				System.out.println("***** NUMBER " + machineCounter + " *****\n");
				
				if (!nfsa.hasEMoves()) { //no e moves detected, convert nfsa -> nfsa
					if (machineCounter == 7) { //final conversion, only print total DFSA states & runtime
						long start = System.currentTimeMillis();
						nfsa.run();
						long end = System.currentTimeMillis();
						
						System.out.println("Total DFSA states: " + nfsa.getTotalDfsaStates() );
						System.out.println("Elapsed time: " + ((end - start)/1000) + " seconds" ); 
					}
					else { //not the final conversion, print all details specified in project description
						nfsa.printNfsa();
						nfsa.run();	//subset construction algorithm to convert to dfsa
						nfsa.printDfsa();
					}
					System.out.println("\n....................\n");
				}
				else { //nfsa HAS e-moves, 
					nfsa.convertNFSA(); //first convert NFSA with e-moves to NFSA without e-moves
					nfsa.printNfsa();
					nfsa.run();	//subset construction algorithm to convert to dfsa
					nfsa.printDfsa();
					System.out.println("\n....................\n");
				}
				transitions = new ArrayList<String>();
				counter = -1; //reset line counter for each machine, incremented to 0 at the end of while(scanner.hasNextLine)
			}
			counter++;
		}	//end while(input.hasNextLine())
	}
}
