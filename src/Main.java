

// Copyright (C) 2014; Sean Cheatham

import java.util.Scanner;

public class Main {
    // Takes in two ints as arguments: Search Depth and Board Number
    public static void main(String[] args) {
        int board = 1;
        if (args.length == 2) {
            try {
                Globals.MAXDEPTH = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
            try {
                board = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[1] + " must be an integer.");
                System.exit(1);
            }

        }
        System.out.println("Max Memory: "+Runtime.getRuntime().maxMemory());

        //Globals.MAXDEPTH = getDepth();
        // Initialize a new board
        Board b = new Board();
        setBoard(b,board);
        // For debug purposes, I like to know how long the engine takes.  Record the time now, run the engine, record again
        long t1 = System.currentTimeMillis();
        // START YOUR ENGINES!!! Call the search(depth) function
        Move m = b.search(Globals.MAXDEPTH);
        // Re-read the time
        long t2 = System.currentTimeMillis();
        // So, Mr. Engine, what move did you come up with?
        System.out.println("Recommended Move: "+m.toString());
        // And how long did you take, Mr. Engine?
        long time = t2-t1 + 1000;
        // Make the move that the engine came up with
        m.move();
        // Print out some stuff.
        System.out.println("Nodes Expanded: "+Globals.NODECOUNT+" in "+(time)/1000+" seconds");
        System.out.println(""+Globals.NODECOUNT/((time)/1000)+" nodes/sec");
        System.out.println("Nodes Evaluated: "+Globals.NODESEVALUATED);
        System.out.println("Branches Pruned: "+Globals.BRANCHESPRUNED);
        System.out.println(b.toString());
    }
    
    static int getDepth(){
        System.out.println("Enter a search depth: ");
        Scanner in = new Scanner(System.in);
        int result = in.nextInt();
        if(result < 1 || result > 64){
            System.out.println("Invalid depth");
            System.exit(-1);
        }
        return result;
    }
    
    static void setBoard(Board b, int i){
        /*System.out.println("Enter a board number (1 = default, 2 = puzzle): ");
        Scanner in = new Scanner(System.in);
        int result = in.nextInt();*/
        switch(i){
            case 1:
                b.squares = Globals.DEFAULTBOARD;
                break;
            case 2:
                b.squares = Globals.PUZZLEBOARD;
                break;
            default:
                System.out.println("Invalid Board Specified.  Using default.");
                b.squares = Globals.DEFAULTBOARD;
        }
    }
}
