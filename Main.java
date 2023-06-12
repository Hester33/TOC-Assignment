package com.company;

import java.util.*;

class FiniteAutomataToRegularGrammar {
    private static final String EPSILON = "e";
    private static final String OR = " | ";
    private static final String ARROW = " -> ";

    private Set<String> states;
    private Set<String> alphabet;
    private String startState;
    private Set<String> finalStates;
    private Map<String, Map<String, String>> transitionMap;

    public FiniteAutomataToRegularGrammar(Set<String> states, Set<String> alphabet, String startState, Set<String> finalStates, Map<String, Map<String, String>> transitionMap) {
        this.states = states;
        this.alphabet = alphabet;
        this.startState = startState;
        this.finalStates = finalStates;
        this.transitionMap = transitionMap;
    }

    public Map<String, String> toRegularGrammar() {
        Map<String, String> regularGrammar = new HashMap<>();
        for (String state : states) {
            StringBuilder sb = new StringBuilder();
                //states means A,B,C
            for (String alphabet : alphabet) {
                //alphabet means 0,1
                if (transitionMap.containsKey(state) && transitionMap.get(state).containsKey(alphabet)) {
                    sb.append(alphabet).append(transitionMap.get(state).get(alphabet)).append(OR);
                }
            }

            if (transitionMap.containsKey(state) && transitionMap.get(state).containsKey("")) {
                sb.append(transitionMap.get(state).get("")).append(OR);
            }

            if (finalStates.contains(state)) {
                sb.append(EPSILON).append(OR);
            }

            if (sb.length() > 0) {
                sb.setLength(sb.length() - OR.length());
                regularGrammar.put(state, sb.toString());
            }
        }
        return regularGrammar;
    }

    public void printRegularGrammar(Map<String, String> regularGrammar) {
        for (String state : regularGrammar.keySet()) {
            System.out.println(state + ARROW + regularGrammar.get(state));
        }
    }

    public boolean checkString(String inputString, Map<String, String> regularGrammar) {
        Set<String> currentStates = new HashSet<>();
        currentStates.add(startState);

        if (inputString.isEmpty()) {
            return finalStates.contains(startState);
        }

        for (char c : inputString.toCharArray()) {
            Set<String> nextStates = new HashSet<>();
            for (String state : currentStates) {
                if (regularGrammar.containsKey(state)) {
                    String[] productions = regularGrammar.get(state).split("\\" + OR);
                    for (String production : productions) {
                        if (production.charAt(0) == c) {
                            nextStates.add(production.substring(1));
                        } else if (production.equals(EPSILON) && alphabet.isEmpty()) {
                            nextStates.add(state);
                        }
                    }
                }
            }
            currentStates = nextStates;
            if (currentStates.isEmpty()) {
                return false;  // No possible transitions, string is not accepted
            }
        }

        for (String state : currentStates) {
            if (finalStates.contains(state)) {
                return true;// String is accepted
            }
            if (finalStates.contains(state) == currentStates.contains(state));
            {
                return true;
            }
        }

        return false;  // String is not accepted
    }



    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input states
        System.out.print("Enter the set of states (comma-separated): ");
        String statesInput = scanner.nextLine();
        Set<String> states = new HashSet<>(Arrays.asList(statesInput.split(",")));

        // Input alphabet
        System.out.print("Enter the input alphabet (comma-separated): ");
        String alphabetInput = scanner.nextLine();
        Set<String> alphabet = new HashSet<>(Arrays.asList(alphabetInput.split(",")));

        // Input initial state
        System.out.print("Enter the initial state: ");
        String startState = scanner.nextLine();

        // Input final states
        System.out.print("Enter the final states (comma-separated): ");
        String finalStatesInput = scanner.nextLine();
        Set<String> finalStates = new HashSet<>(Arrays.asList(finalStatesInput.split(",")));

        // Input transition table
        System.out.println("Enter the transition table (state, input, next state) - enter 'done' to finish:");
        Map<String, Map<String, String>> transitionMap = new HashMap<>();
        String transitionInput;
        while (true) {
            transitionInput = scanner.nextLine();
            if (transitionInput.equalsIgnoreCase("done")) {
                break;
            }

            String[] transitionParts = transitionInput.split(",");
            String currentState = transitionParts[0].trim();
            String inputSymbol = transitionParts[1].trim();
            String nextState = transitionParts[2].trim();

            transitionMap.putIfAbsent(currentState, new HashMap<>());
            transitionMap.get(currentState).put(inputSymbol, nextState);
        }

        // Input test strings
        System.out.print("Enter strings to test (comma-separated): ");
        String testStringsInput = scanner.nextLine();
        List<String> testStrings = Arrays.asList(testStringsInput.split(","));

        FiniteAutomataToRegularGrammar finiteAutomataToRegularGrammar = new FiniteAutomataToRegularGrammar(states, alphabet, startState, finalStates, transitionMap);

        System.out.println("The Regular Grammar is:");
        Map<String, String> regularGrammar = finiteAutomataToRegularGrammar.toRegularGrammar();
        finiteAutomataToRegularGrammar.printRegularGrammar(regularGrammar);

        System.out.println();

        System.out.println("The results of checking the strings are:");
        for (String testString : testStrings) {
            boolean isAccepted = finiteAutomataToRegularGrammar.checkString(testString.trim(), regularGrammar);
            System.out.println(testString + ": " + (isAccepted ? "OK" : "NO"));
        }
    }
}
