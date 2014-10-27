

// Copyright (C) 2014; Sean Cheatham

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Globals.MAXDEPTH = getDepth();
        // Initialize a new board
        Board b = new Board();
        setBoard(b);
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
    
    static void setBoard(Board b){
        System.out.println("Enter a board number (1 = default, 2 = puzzle): ");
        Scanner in = new Scanner(System.in);
        int result = in.nextInt();
        switch(result){
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
