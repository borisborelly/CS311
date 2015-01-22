/**
 * NFSA with and without e moves -> DFSA using subset construction algorithm
 * 
 * 
 * @author Boris Borelly
 * 009813375
 * CS 311
 * November 13, 2014
 * Project #2
 *
 */
import java.util.*;
public class NFSA {
	private final int TOTAL_STATES; // total # of sets
	private final int[] FINAL_STATES; // the set of final states
	private final String[] ALPHABET; // the language allowed, each char is stored at separate index
	private final String[] TRANSITIONS; // each index contains a transition (p a q)
	private final boolean[] FINAL; // FINAL[state] = true if state is final
	
	private Set[][] nfsa_next_state; 
	
	private DFSA dfsa;
	
	private Set<Integer> initialStates;
	private ArrayList<Set> eclosure;
	
	public NFSA(int totalStates, int[] finalStates, boolean[] f, String[] alpha, ArrayList<String> t) { 
		TOTAL_STATES = totalStates;
		FINAL_STATES = finalStates;
		ALPHABET = alpha;
		TRANSITIONS = new String[t.size()];
		t.toArray(TRANSITIONS);
		FINAL = f;
		nfsa_next_state = new Set[TOTAL_STATES][ALPHABET.length];
		dfsa = new DFSA();
		
		initialStates = new HashSet<Integer>();
		
		for (int i=0; i<TRANSITIONS.length; ++i) { 
			String in = TRANSITIONS[i];
			String[] line = in.split(" ");
			Set<Integer> finalState = new HashSet<Integer>();
			
			for (int k=2; k<line.length; ++k) 
				finalState.add(Integer.parseInt(line[k]));
			
			int inputState = Integer.parseInt(line[0]);
			int index = 0;
			
			if (!line[1].equals("e")) { 	//NOT AN E MOVE, able to index in alphabet
				index = findSymbolInAlpha(line[1]);
				nfsa_next_state [inputState] [index] = finalState;
			}
			initialStates.add(inputState); //used for e-closure
		}
	}
	
	
	/**
	 * Subset construction algorithm for converting NFSA WITHOUT e-moves to a DFSA
	 */
	public void run() { 
		Set s = new HashSet();	s.add(0);
		dfsa.states.add(s);
		dfsa.num_state = 0; dfsa.current_state = 0;
		
		Set<?>[] transitionStates = new HashSet<?>[ALPHABET.length];
		for (int i = 0; i < transitionStates.length; ++i)
		    transitionStates[i] = new HashSet<Integer>();
		
		while(dfsa.current_state <= dfsa.num_state) { 
			for(String symbol: ALPHABET) { 
				Set nextState = computeDfsaNextState(symbol);
				int symbolIndex = findSymbolInAlpha(symbol);
				
				if(nextState.size() == 0) 
					transitionStates[symbolIndex] = null;
				 else 
					transitionStates[symbolIndex] = nextState;
				
				if(symbolIndex == ALPHABET.length - 1) {
					dfsa.transitions.add(transitionStates);
					transitionStates = new HashSet<?>[ALPHABET.length];
				}
				if (!dfsa.states.contains(nextState)) { //not a new state
					if (nextState.size() > 0) { 
						dfsa.num_state = dfsa.num_state + 1;
						dfsa.states.add(nextState); 
					}
				}
			}	//for every symbol in the alphabet
			dfsa.current_state = dfsa.current_state + 1;
		}
	}
	

	/**
	 * Computes the next DFSA state and returns it
	 * @param symbol - current input symbol in the alphabet 
	 * @return next DFSA state
	 */
	private Set computeDfsaNextState(String symbol) { 
		dfsa.next_state = new HashSet();
		Set currentDfsaState = dfsa.states.get(dfsa.current_state);
		Iterator setIterator = currentDfsaState.iterator();
		int index = findSymbolInAlpha(symbol);	//finds the index of the symbol in the ALPHABET array

		while(setIterator.hasNext()) { 
			int tempState = (int)setIterator.next();
			Set nextNFSA = nfsa_next_state[tempState][index];	//returns null if state not defined (isTrap)
			if (nextNFSA != null) 
				dfsa.next_state.addAll(nextNFSA);
		}
		return dfsa.next_state;
	}
	
	
	/**
	 * Converts NFSA WITH e-moves to the equivalent NFSA WITHOUT e-moves
	 */
	public void convertNFSA() { 
		closure(initialStates); //populations the eclosure ArrayList<Set>
		
		for(Set<Integer> ec: eclosure) { 
			System.out.println("E closure " + ec);
		}

		int stateNum = 0;
		for(Integer state: initialStates) { 
			Set<Integer> e = eclosure.get(stateNum);
			
			for(int j=0; j<ALPHABET.length; ++j) { 
				Set<Integer> newSet = new HashSet<Integer>();
				Set<Integer> addSet = new HashSet<Integer>();
				
				int idx = findSymbolInAlpha(ALPHABET[j]);
				
				for(Integer i: e) { 
					if (nfsa_next_state[i][idx] != null)
						newSet.addAll(nfsa_next_state[i][idx]);
				}
				for(Integer s: newSet) 
					addSet.addAll(eclosure.get(s));
				
				if (addSet.size() > 0) 
					nfsa_next_state[stateNum][idx] = addSet;
				else 
					nfsa_next_state[stateNum][idx] = null;
				
			}
			stateNum++;
		}
	}
	
	/**
	 * Creats an ArrayList<Sets> containing all the e closures 
	 * Each index of array list corresponds to the NFSA state
	 * @param inputStates - a SET of NFSA states 
	 */
	private void closure(Set<Integer> inputStates) { 
		Set<Integer> output = new HashSet<Integer>();
		eclosure = new ArrayList<Set>();
		int size = 0;
		
		for(Integer state: inputStates) { 
			output.add(state);
			
			while(true) { 
				size = output.size();	//keep track of the size of output
				Set<Integer> statesToAdd = new HashSet<Integer>();
				
				for(Integer s: output) { //iterate through the current set of states
					for(int i=0; i<TRANSITIONS.length; ++i) { //iterate through the transition table
						String line = TRANSITIONS[i];
						String[] split = line.split(" ");
						if (Integer.parseInt(split[0]) == s && split[1].equals("e")) { //if current state == transition state && it has an e-move...
							for(int k=2; k<split.length; ++k) { 
								statesToAdd.add(Integer.parseInt(split[k]));	//add all new e-move states to temporary set
							}
						}
					}
				}
				output.addAll(statesToAdd); //union output set with statesToAdd
				if (output.size() == size) 	//if output set size does not change, no more e-moves can be done - break out of the loop
					break;
			}
			eclosure.add(output);	//add output to the e closure ArrayList<Set> and reset output set
			output = new HashSet();	
		}
	}
	
	/**
	 * returns the array index for a symbol in the alphabet 
	 * returns null if does not exist in alpha
	 */
	public int findSymbolInAlpha(String input) { 
		int index = -1;
		
		for (int j=0; j<ALPHABET.length; ++j) { 
			if (ALPHABET[j].equals(input)) { 
				index = j;
				break;
			}
		}
		return index;
	}
	
	/**
	 * prints the converted DFSA for the converted NFSA
	 */
	public void printDfsa() { 
		System.out.println("\n\nThe equivalent NFSA -> DFSA by subset construction:\n");
		System.out.println("(1) Number of states: " + dfsa.states.size());
		
		int counter = 0;
		Set<Integer> f = new HashSet<Integer>();
		for(Set a: dfsa.states) { 
			for (int i=0; i< FINAL_STATES.length; ++i) { 
				if (a.contains(FINAL_STATES[i]))
					f.add(counter);
			}
			System.out.println("\tState " + counter + ": " + a);
			counter++;
		}
		
		System.out.print("\n(2) Set of final states: ");
		for(int i: f) 
			System.out.print(i + " ");
		System.out.println("\n");
		
		System.out.println("(3) Transitions:");
		
		for(Set[] transition : dfsa.transitions) {
			System.out.println("\t" + Arrays.toString(transition));
		}
	}
	
	/**
	 * prints the nfsa properties specified in the project description
	 */
	public void printNfsa() { 
		System.out.println("The original input NFSA:\n");
		System.out.println("(1) Number of states: " + TOTAL_STATES);
		
		System.out.print("(2) Set of final states: ");
		for(int i=0; i<FINAL_STATES.length; ++i) { 
			System.out.print(FINAL_STATES[i] + " ");
		}
		System.out.println("");
		
		System.out.print("(3) Alphabet: ");
		for(int i=0; i<ALPHABET.length; ++i) { 
			System.out.print(ALPHABET[i] + " ");
		}
		System.out.println("");
		
		System.out.println("(4) Transitions:");
		for (int i=0; i<TRANSITIONS.length; ++i) { 
			System.out.println("\t" + TRANSITIONS[i] + " ");
		}
		
	}
	
	/**
	 * @return total number of dfsa states 
	 */
	public int getTotalDfsaStates() { 
		return dfsa.num_state + 1;
	}
	
	
	/**
	 * used to determine whether or not a nfsa machine contains e-moves
	 * @return true or false
	 */
	public boolean hasEMoves() { 
		for (int i=0; i<TRANSITIONS.length; ++i) { 
			String in = TRANSITIONS[i];
			String[] line = in.split(" ");
			if (line[1].equals("e"))
				return true;
		}
		return false;
	}
	
	
	public class DFSA { 
		public ArrayList<Set> states;
		public int num_state;
		public int current_state;
		public Set next_state;
		public ArrayList<Set[]> transitions;
		
		public DFSA() { 
			states= new ArrayList<Set>();
			num_state = 0;
			current_state = 0;
			next_state = new HashSet();
			transitions = new ArrayList<Set[]>();
		}
	}
}