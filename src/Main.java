// Copyright (C) 2014; Sean Cheatham
public class Main {

    public static void main(String[] args) {
        // Initialize a new board
        Board b = new Board();
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
        System.out.println(b.toString());
    }
}
