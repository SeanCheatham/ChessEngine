/**
 * Created by Sean on 10/23/2014.
 */
public class Globals {

    // Config Variables
    final public static int MAXDEPTH = 6;
    final public static int[] INITIALBOARD = new int[]{
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13,  0, 12,  0, 10,  0,  9, 10,  0, 13,
                13,  7,  0, 11,  0,  8,  7,  0,  0, 13,
                13,  7,  8,  7,  0,  7,  0,  0,  0, 13,
                13,  0,  0,  2,  7,  1,  9,  0,  7, 13,
                13,  1,  1,  0,  1,  0,  0,  7,  0, 13,
                13,  0,  0,  1,  0,  0,  0,  0,  0, 13,
                13,  0,  0,  0,  0,  5,  1,  1,  1, 13,
                13,  4,  0,  3,  0,  2,  4,  6,  0, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13
    };

    // Global Variables
    public static int NODECOUNT = 0;
    public static int NODESEVALUATED = 0;

    //Constants
    public static final int EMPTY = 0;
    public static final int WPAWN = 1;
    public static final int WKNIGHT = 2;
    public static final int WBISHOP = 3;
    public static final int WROOK = 4;
    public static final int WQUEEN = 5;
    public static final int WKING = 6;
    public static final int BPAWN = 7;
    public static final int BBISHOP = 8;
    public static final int BKNIGHT = 9;
    public static final int BROOK = 10;
    public static final int BQUEEN = 11;
    public static final int BKING = 12;
    public static final int ILLEGAL = 13;


}
