import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Finite State Automata simulation
 * Must be given input
 * (1) Total Number of States
 * (2) an int array of final states
 * (3) a boolean determing whether or not each state is final
 * (4) A String array of the language allowed for the FSA design
 * (5) A String array of the transitions
 * (6) A dynamic ArrayList containing all the input read in from the file
 * 
 * @author Boris Borelly
 * 009813375
 * CS 311
 * October 16, 2014
 * Project #1
 *
 */
public class FSA {
	private final int TOTAL_STATES; // total # of sets
	private final int[] FINAL_STATES; // the set of final states
	private final String[] ALPHABET; // the language allowed, each char is stored at separate index
	private final String[] INPUTS; // each input string is stored at separate index
	private final String[] DELTA_STRING; // each index contains a transition (p a q)
	private final boolean[] FINAL; // FINAL[state] = true if state is final
	private int[][] next_state; // table for final state given [p][a] = [q]

	private int state; 
	private boolean exit;
	
	//users to iterate through the string/states and by getNextState()
	private int pointer;
	private int head;

	public FSA(int totalStates, int[] finalStates, boolean[] f, String[] alpha, String[] t, ArrayList in) {
		TOTAL_STATES = totalStates;
		FINAL_STATES = finalStates;
		ALPHABET = alpha;
		DELTA_STRING = t;
		FINAL = f;
		next_state = new int[totalStates][ALPHABET.length];
		INPUTS = new String[in.size()];
		
		in.toArray(INPUTS);
	
		exit = false;
		head = 0;
		state = 0;
		
		//builds transition table next_state[][]
		
		for (int i = 0; i < DELTA_STRING.length; ++i) {
			String line = DELTA_STRING[i];
			line = line.substring(1, line.length() - 1);
			String[] split = line.split(" ");

			// [0]=p, [1]=a, [2]=q for transitions (p a q)
			int index = 0;
			for (int j = 0; j < ALPHABET.length; ++j) {
				if (ALPHABET[j].equals(split[1])) {
					// System.out.println(ALPHABET[j] + " | " + split[0] + " | " + split[1] + " | " + split[2] + " | [" + j + "]");
					index = j;
					break;
				}
			}
			// System.out.println("["+split[0]+"] ["+index+"] = [" + split[2] +"]");
			next_state[Integer.parseInt(split[0])][index] = Integer.parseInt(split[2]);
		}

	}

	/**
	 * returns the next state given the current state and the next input symbol
	 * @param current - current state
	 * @param symbol - current symbol
	 * @return - next state for [state][symbol]
	 */
	private int getNextState(int current, String symbol) {
		int index = -1;
		for (int i = 0; i < ALPHABET.length; ++i) {
			if (ALPHABET[i].equals(symbol)) {
				index = i;
				break;
			}
		}
		return next_state[current][index];
	}

	/**
	 * Gets the next symbol in the string
	 * returns null when the string has been read
	 * @return Next symbol
	 */
	private String getNextSymbol() {
		if (pointer < INPUTS[head].length()) {
			return String.valueOf(INPUTS[head].charAt(pointer++));
		}
		else { 
			return null;
		}
	}

	/**
	 * Determines whether or not a character is in the alphabet
	 * @param test - test char passed as String
	 * @return true or false
	 */
	private boolean isAlpha(String test) {
		for (int i = 0; i < ALPHABET.length; i++) {
			if (test.equals(ALPHABET[i]))
				return true;
		}
		return false;
	}

	/**
	 * Sends each input line through the FSA algorithm
	 */
	public void run() {
		head = 0;
		for (int i = 0; i < INPUTS.length; ++i) {
			System.out.print("Testing " + INPUTS[i] + " -> ");
			startFSA(INPUTS[i]);
			head++;
		}
	}

	/**
	 * Main FSA algorithm to reject/accept strings
	 * @param in
	 */
	private void startFSA(String in) {
		String symbol = "";
		state = 0;
		exit = false;
		pointer = 0;

		do {
			symbol = getNextSymbol();
			//System.out.println(symbol + " symbol");
			if (symbol == null) {
				if (FINAL[state]) {
					System.out.println("Accepted");
					exit = true;
					break;
				}
				else {
					System.out.println("Rejected");
					exit = true;
					break;
				}
				
			} else if (isAlpha(symbol)) {
				state = getNextState(state, symbol);
				if (state == (TOTAL_STATES -1)) { // is a trap state
					System.out.println("Rejected");
					exit = true;
					break;
				}
			} else {
				System.out.println("Rejected");
				exit = true;
				break;
			}
		} while (!exit);
	}


	/**
	 * Printing method for FSA info
	 */
	public void print() {
		System.out.println("Total States: " + TOTAL_STATES);
		System.out.print("Final States: ");
		for (int i = 0; i < FINAL_STATES.length; ++i) {
			if (i == FINAL_STATES.length - 1)
				System.out.print(FINAL_STATES[i]);
			else
				System.out.print(FINAL_STATES[i] + ",");
		}

		System.out.println();
		System.out.print("Alphabet: {");

		for (int i = 0; i < ALPHABET.length; ++i) {
			if (i == ALPHABET.length - 1)
				System.out.print(ALPHABET[i] + "}");
			else
				System.out.print(ALPHABET[i] + ",");
		}

		System.out.println();
		System.out.print("Transitions:\n");
	
		for (int i = 0; i < DELTA_STRING.length; ++i) {
			System.out.println("\t" + DELTA_STRING[i]);
		}

		System.out.println();
		
	}

}