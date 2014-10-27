// Copyright (C) 2014; Sean Cheatham
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Board {
    // Array of integers to represent piece types
    public int[] squares;
    // Keep track of who is moving (0 = white, 1 = black)
    public int sideToMove;
    // Keep track of the result of the game (-1 = in progress, 0 = white, 1 = black)
    public int result;
    // Keep track of castling rights (indexes: 0 = WK, 1= WQ, 2= BK, 3= WK)
    public int[] castling;
    // Keep track of en passant square (index)
    public int enPassant;
    // Array of white pieces
    // Counter to keep track of move
    public int moveCount;
    // HashMap to keep track of previous moves and their evaluations
    HashMap<Integer,Double> hmap;

    public Board(){
        // 120 bit board is setup as follows:
        /*
        **********
        **********
        *rnbqkbnr*
        *pppppppp*
        *--------*
        *--------*
        *--------*
        *--------*
        *PPPPPPPP*
        *RNBQKBNR*
        **********
        **********



        Padding on sides allows for quick detection of knight moves
        0=- (empty)
        1=P
        2=N
        3=B
        4=R
        5=Q
        6=K
        7=p
        8=n
        9=b
        10=r
        11=q
        12=k
        13=* (out of bounds)
         */
        
        squares = Globals.PUZZLEBOARD;
        sideToMove = 0;
        result = -1;
        castling = new int[] {1,1,1,1};
        enPassant = -1;
        moveCount = 1;
        hmap = new HashMap<Integer,Double>(1024000);
    }

    //Copy constructor
    public Board(Board b){
        this.squares = b.squares;
        this.sideToMove = b.sideToMove;
        this.result = b.result;
        this.castling = b.castling;
        this.enPassant = b.enPassant;
        this.moveCount = b.moveCount;
        this.hmap = b.hmap;
    }

    
    // Search Functions
    // Search initializer function.  Takes in a depth limit, and returns the "best" move
    public Move search(int depth){
        // Initialize a temp variable to store our current "max" value
        double max;
        // If it is white's move, then set the max to a ridiculously low number.  This way, it can be overwritten by pretty much any move
        if(sideToMove == 0) max = -10000;
            // Otherwise, set the max to a high number.
        else max = 10000;
        // Initialize a variable to store the "best" move
        Move finalMove = null;
        // Iterate through all possible moves
        for(Move m : possibleMoves()){
            // If (for some reason), we end up with a blank move, we can assume that there are no possible moves
            if(m == null){
                // Thus we exit
                System.out.println("No legal moves");
                System.exit(0);
            }
            // Make the move for the current iteration
            m.move();
            // Call the Alpha/Beta function (start with max), and store it in a temporary variable
            double x = alphaBetaMin(-1000000,1000000,depth-1);
            // Output the move we are looking at, as well as whatever Alpha/Beta says for its value
            System.out.println(m+" : "+x);
            // Undo the move so that we're back to the previous state
            m.undoMove();
            // If it is white's move
            if(sideToMove == 0) {
                // And if the value returned by Alpha/Beta is GREATER than (or equal to) our current "best move"
                if (x >= max) {
                    // Set the best move to our new move
                    max = x;
                    finalMove = m;
                }
            }
            // If it is black's move
            else {
                // And if the value returned by Alpha/Beta is LESS than (or equal to) our current "best move"
                if (x <= max) {
                    // Set the best move to our new move
                    max = x;
                    finalMove = m;
                }
            }
        }
        // Return the "best" move
        return finalMove;
    }
    
    // Relatively generic implementation of Alpha Beta (Max side)
    // Takes in an alpha value (initially an infinitely negative value), a beta value (initially an infinitely positive value), and a depth to search to
    public double alphaBetaMax(double alpha, double beta, int depth){
        // Initialize the score to alpha
        double score = alpha;
        // Increment the number of nodes searched
        Globals.NODECOUNT++;

        // Check if the hmap contains this board
        if(hmap.containsKey(this.hashCode())){
            double d = hmap.get(this.hashCode());
            Globals.NODECOUNT++;
            return d;
        }
        // Base case: If we're at the bottom of the tree, evaluate the board and return
        if (depth == 0){
            double d = this.evaluate();
            hmap.put(this.hashCode(),d);
            return d;
        }
        // Iterate through all possible moves
        for(Move m : possibleMoves()){
            // Do the move
            m.move();

            // If that move we just did ends the game
            if(result > -1) {
                // Evaluate the board
                double eval = evaluate();
                // Undo the move to return to our previous state
                m.undoMove();
                // Return the board evaluation
                return eval;
            }
            // Run Alpha/Beta (min)
            score = alphaBetaMin(alpha, beta, depth-1);
            // Undo the move to return to our previous state
            m.undoMove();
            // If our score somehow ends up higher than beta, then we cut the line (pruning)
            if(score>= beta){
                Globals.BRANCHESPRUNED++;
                return beta;
            }
            // If the score is greater than alpha, set alpha to the score
            if(score>alpha) alpha = score;
        }
        // Return alpha
        return alpha;
    }
    
    // Same idea as with Alpha/Beta Max, except we switch the side for which we are evaluating
    public double alphaBetaMin(double alpha, double beta, int depth){
        double score = beta;
        Globals.NODECOUNT++;
        if(hmap.containsKey(this.hashCode())){
            double d = hmap.get(this.hashCode());
            Globals.NODECOUNT++;
            return d;
        }
        if (depth == 0){
            double d = this.evaluate();
            hmap.put(this.hashCode(),d);
            return d;
        }
        for(Move m : possibleMoves()){
            if(m == null) return beta;
            m.move();

            if(result > -1) {
                double eval = evaluate();
                m.undoMove();
                return eval;
            }
            score = alphaBetaMax(alpha, beta, depth-1);
            m.undoMove();
            if(score <= alpha){
                Globals.BRANCHESPRUNED++;
                return alpha;
            }
            if(score < beta) beta = score;
        }
        return beta;
    }

    
    // Evaluation Functions
    public double evaluate(){
        Globals.NODESEVALUATED++;
        double val = 0.0;
        // Number of pawns for each team
        // If there are fewer than 8 pawns on the board, the game is considered "open".  Else, game is considered "closed".
        int wPawns = 0;
        int bPawns = 0;
        for(int i=21; i<=98; i++){
            if(squares[i]== 1) wPawns++;
            if(squares[i]== 7) bPawns++;
        }
        // Doubled pawns tend to be bad, so we subtract a quarter point for each doubled pawn (meaning that a set of doubled pawns adds to a half point off)
        val += getDoubledPawns(1).size()*0.25 - getDoubledPawns(0).size()*0.25;
        // Weight of knight pieces
        // This formula guarantees a weight between 0.75 and 1
        // If there are 16 pawns on the board, the game is "closed", and knights are more useful (thus weighted at 1.0)
        double knightWeight = (1 - (16.0-(wPawns+bPawns))/64.0);
        // Weight of bishop pieces
        // This formula guarantees a weight between 1 and 1.25
        // If there are no pawns on the board, the game is "open", and bishops are more useful (thus weighted at 1.25)
        double bishopWeight = (1 + (16.0-(wPawns+bPawns))/64.0);
        //System.out.println(""+knightWeight+";"+bishopWeight);
        for(int i=21; i<=98; i++){
            switch(squares[i]){
                case 1: // White Pawns have a value of 1
                    val += 1;
                    break;
                case 2: // White knights have a value of 3 (times the weight of knights)
                    val += 3*knightWeight;
                    break;
                case 3: // White bishops have a value of 3 (times the weight of the bishops)
                    val += 3*bishopWeight;
                    break;
                case 4: // White rooks have a value of 5
                    val += 5;
                    break;
                case 5: // White queens have a value of 10
                    val += 10;
                    break;
                case 6: // White kings have a value of 1000
                    val += 1000;
                    break;
                case 7: // Black pawns have a value of -1
                    val -= 1;
                    break;
                case 8: // Black knights have a value of -3 (times the weight of the knights)
                    val -= 3*knightWeight;
                    break;
                case 9: // Black bishops have a value of -3 (times the weight of the bishops)
                    val -= 3*bishopWeight;
                    break;
                case 10: // Black rooks have a value of -5
                    val -= 5;
                    break;
                case 11: // Black queens have a value of -10
                    val -= 10;
                    break;
                case 12: // Black kings have a value of -1000
                    val -= 1000;
                    break;
                default: // If we somehow ended up looking at something else (such as an empty or illegal square(0 or 13 respectively)), break
                    break;
            }
        }
        // return the score of the board
        return val;
    }   
    
    public ArrayList<Integer> getDoubledPawns(int s){
        ArrayList<Integer> vals = new ArrayList<Integer>();
        // Use 31 to 88 because pawns can't exist on the back ranks for either side
        for(int i = 31; i <= 88; i++){
            // Get black doubled pawns
            if(squares[i] == 7 && s == 1){
                for(int j = i; j<=98; j+= 10){
                    if(squares[j] == 7){
                        vals.add(i);
                        vals.add(j);
                    }
                }
            }
            // Get white doubled pawns
            else if(squares[i] == 1 && s == 0){
                for(int j = i; j>=21; j-= 10){
                    if(squares[j] == 1){
                        vals.add(i);
                        vals.add(j);
                    }
                }
            }
        }
        return vals;
    }    
    
    
    // Move Generation Functions
    // Function that generates the possible moves that the piece aat a given square can make
    public ArrayList<Integer> calculateMoves(int index){
        ArrayList<Integer> result = new ArrayList<Integer>();

        // Setup a variable that we can play with throughout
        int i = index;
        switch(squares[index]){
            case 1: // White Pawn
                if((squares[index-9] >= 7 && squares[index-9] <= 12) || index-9 == enPassant) result.add(index-9);
                if((squares[index-11] >= 7 && squares[index-11] <= 12) || index-11 == enPassant) result.add(index-11);
                if(squares[index-10] == 0) result.add(index-10);
                if(squares[index-20] == 0 && squares[index-10] == 0) result.add(index-20);
                break;
            case 2: // White Knight
                if((squares[index+8] >= 7 && squares[index+8] <13) || squares[index+8] == 0) result.add(index+8);
                if((squares[index+12] >= 7 && squares[index+12] <13) || squares[index+12] == 0) result.add(index+12);
                if((squares[index-12] >= 7 && squares[index-12] <13) || squares[index-12] == 0) result.add(index-12);
                if((squares[index-8] >= 7 && squares[index-8] <13) || squares[index-8] == 0) result.add(index-8);
                if((squares[index+21] >= 7 && squares[index+21] <13) || squares[index+21] == 0) result.add(index+21);
                if((squares[index+19] >= 7 && squares[index+19] <13) || squares[index+19] == 0) result.add(index+19);
                if((squares[index-19] >= 7 && squares[index-19] <13) || squares[index-19] == 0) result.add(index-19);
                if((squares[index-21] >= 7 && squares[index-21] <13) || squares[index-21] == 0) result.add(index-21);
                break;
            case 3: // White Bishop
                i = index;
                while(i-9>=21){
                    if(squares[i-9] >= 7 && squares[i-9] < 13){
                        result.add(i-9);
                        break;
                    }
                    else if(squares[i-9] == 0){
                        result.add(i-9);
                        i-=9;
                    }
                    else break;
                }
                i = index;
                while(i-11>=21){
                    if(squares[i-11] >= 7 && squares[i-11] < 13){
                        result.add(i-11);
                        break;
                    }
                    else if(squares[i-11] == 0){
                        result.add(i-11);
                        i-=11;
                    }
                    else break;
                }
                i=index;
                while(i+9 <= 98){
                    if(squares[i+9] >= 7 && squares[i+9] < 13){
                        result.add(i+9);
                        break;
                    }
                    else if(squares[i+9] == 0){
                        result.add(i+9);
                        i+=9;
                    }
                    else break;
                }
                i=index;
                while(i+11 <= 98){
                    if(squares[i+11] >= 7 && squares[i+11] < 13){
                        result.add(i+11);
                        break;
                    }
                    else if(squares[i+11] == 0){
                        result.add(i+11);
                        i+=11;
                    }
                    else break;
                }
                break;
            case 4: // White Rook
                i = index;
                while(i-10>=21){
                    if(squares[i-10] >= 7 && squares[i-10] <= 12){
                        result.add(i-10);
                        break;
                    }
                    else if(squares[i-10] == 0){
                        result.add(i-10);
                        i-=10;
                    }
                    else break;
                }
                i = index;
                while(i-1>=21){
                    if(squares[i-1] >= 7 && squares[i-1] <= 12){
                        result.add(i-1);
                        break;
                    }
                    else if(squares[i-1] == 0){
                        result.add(i-1);
                        i-=1;
                    }
                    else break;
                }
                i = index;
                while(i+10<=98){
                    if(squares[i+10] >= 7 && squares[i+10] <= 12){
                        result.add(i+10);
                        break;
                    }
                    else if(squares[i+10] == 0){
                        result.add(i+10);
                        i+=10;
                    }
                    else break;
                }
                i = index;
                while(i+1<=98){
                    if(squares[i+1] >= 7 && squares[i+1] <= 12){
                        result.add(i+1);
                        break;
                    }
                    else if(squares[i+1] == 0){
                        result.add(i+1);
                        i+=1;
                    }
                    else break;
                }
                break;
            case 5: // White Queen
                i = index;
                while(i-9>=21){
                    if(squares[i-9] >= 7 && squares[i-9] < 13){
                        result.add(i-9);
                        break;
                    }
                    else if(squares[i-9] == 0){
                        result.add(i-9);
                        i-=9;
                    }
                    else break;
                }
                i = index;
                while(i-11>=21){
                    if(squares[i-11] >= 7 && squares[i-11] < 13){
                        result.add(i-11);
                        break;
                    }
                    else if(squares[i-11] == 0){
                        result.add(i-11);
                        i-=11;
                    }
                    else break;
                }
                i=index;
                while(i+9 <= 98){
                    if(squares[i+9] >= 7 && squares[i+9] < 13){
                        result.add(i+9);
                        break;
                    }
                    else if(squares[i+9] == 0){
                        result.add(i+9);
                        i+=9;
                    }
                    else break;
                }
                i=index;
                while(i+11 <= 98){
                    if(squares[i+11] >= 7 && squares[i+11] < 13){
                        result.add(i+11);
                        break;
                    }
                    else if(squares[i+11] == 0){
                        result.add(i+11);
                        i+=11;
                    }
                    else break;
                }
                i = index;
                while(i-10>=21){
                    if(squares[i-10] >= 7 && squares[i-10] <= 12){
                        result.add(i-10);
                        break;
                    }
                    else if(squares[i-10] == 0){
                        result.add(i-10);
                        i-=10;
                    }
                    else break;
                }
                i = index;
                while(i-1>=21){
                    if(squares[i-1] >= 7 && squares[i-1] <= 12){
                        result.add(i-1);
                        break;
                    }
                    else if(squares[i-1] == 0){
                        result.add(i-1);
                        i-=1;
                    }
                    else break;
                }
                i = index;
                while(i+10<=98){
                    if(squares[i+10] >= 7 && squares[i+10] <= 12){
                        result.add(i+10);
                        break;
                    }
                    else if(squares[i+10] == 0){
                        result.add(i+10);
                        i+=10;
                    }
                    else break;
                }
                i = index;
                while(i+1<=98){
                    if(squares[i+1] >= 7 && squares[i+1] <= 12){
                        result.add(i+1);
                        break;
                    }
                    else if(squares[i+1] == 0){
                        result.add(i+1);
                        i+=1;
                    }
                    else break;
                }
                break;
            case 6: // White King
                if((squares[index-1] >= 7 && squares[index-1] <13) || squares[index-1] == 0) result.add(index-1);
                if((squares[index-10] >= 7 && squares[index-10] <13) || squares[index-10] == 0) result.add(index-10);
                if((squares[index+1] >= 7 && squares[index+1] <13) || squares[index+1] == 0) result.add(index+1);
                if((squares[index+10] >= 7 && squares[index+10] <13) || squares[index+10] == 0) result.add(index+10);
                if((squares[index-9] >= 7 && squares[index-9] <13) || squares[index-9] == 0) result.add(index-9);
                if((squares[index-11] >= 7 && squares[index-11] <13) || squares[index-11] == 0) result.add(index-11);
                if((squares[index+9] >= 7 && squares[index+9] <13) || squares[index+9] == 0) result.add(index+9);
                if((squares[index+11] >= 7 && squares[index+11] <13) || squares[index+11] == 0) result.add(index+11);
                // King side castle
                if(castling[0] == 1 && squares[96] == 0 && squares[97] == 0 && squares[98] == 4 && squares[95] == 6) result.add(97);
                // Queen side castle
                if(castling[1] == 1 && squares[94] == 0 && squares[93] == 0 && squares[92] == 0 && squares[91] == 4 && squares[95] == 6) result.add(93);
                break;
            case 7: // Black Pawn
                if((squares[index+9] >= 1 && squares[index+9] <= 6) || index+9 == enPassant) result.add(index+9);
                if((squares[index+11] >= 1 && squares[index+11] <= 6) || index+11 == enPassant) result.add(index+11);
                if(squares[index+10] == 0) result.add(index+10);
                if(squares[index+20] == 0 && squares[index+10] == 0) result.add(index+20);
                break;
            case 8: // Black Knight
                if((squares[index+8] >= 1 && squares[index+8] <=6) || squares[index+8] == 0) result.add(index+8);
                if((squares[index+12] >= 1 && squares[index+12] <=6) || squares[index+12] == 0) result.add(index+12);
                if((squares[index-12] >= 1 && squares[index-12] <=6) || squares[index-12] == 0) result.add(index-12);
                if((squares[index-8] >= 1 && squares[index-8] <=6) || squares[index-8] == 0) result.add(index-8);
                if((squares[index+21] >= 1 && squares[index+21] <=6) || squares[index+21] == 0) result.add(index+21);
                if((squares[index+19] >= 1 && squares[index+19] <=6) || squares[index+19] == 0) result.add(index+19);
                if((squares[index-19] >= 1 && squares[index-19] <=6) || squares[index-19] == 0) result.add(index-19);
                if((squares[index-21] >= 1 && squares[index-21] <=6) || squares[index-21] == 0) result.add(index-21);
                break;
            case 9:
                i = index;
                while(i-9>=21){
                    if(squares[i-9] >= 1 && squares[i-9] <= 6){
                        result.add(i-9);
                        break;
                    }
                    else if(squares[i-9] == 0){
                        result.add(i-9);
                        i-=9;
                    }
                    else break;
                }
                i = index;
                while(i-11>=21){
                    if(squares[i-11] >= 1 && squares[i-11] <= 6){
                        result.add(i-11);
                        break;
                    }
                    else if(squares[i-11] == 0){
                        result.add(i-11);
                        i-=11;
                    }
                    else break;
                }
                i=index;
                while(i+9 <= 98){
                    if(squares[i+9] >= 1 && squares[i+9] <= 6){
                        result.add(i+9);
                        break;
                    }
                    else if(squares[i+9] == 0){
                        result.add(i+9);
                        i+=9;
                    }
                    else break;
                }
                i=index;
                while(i+11 <= 98){
                    if(squares[i+11] >= 1 && squares[i+11] <= 6){
                        result.add(i+11);
                        break;
                    }
                    else if(squares[i+11] == 0){
                        result.add(i+11);
                        i+=11;
                    }
                    else break;
                }
                break;
            case 10:
                i = index;
                while(i-10>=21){
                    if(squares[i-10] >= 1 && squares[i-10] <= 6){
                        result.add(i-10);
                        break;
                    }
                    else if(squares[i-10] == 0){
                        result.add(i-10);
                        i-=10;
                    }
                    else break;
                }
                i = index;
                while(i-1>=21){
                    if(squares[i-1] >= 1 && squares[i-1] <= 6){
                        result.add(i-1);
                        break;
                    }
                    else if(squares[i-1] == 0){
                        result.add(i-1);
                        i-=1;
                    }
                    else break;
                }
                i = index;
                while(i+10<=98){
                    if(squares[i+10] >= 1 && squares[i+10] <= 6){
                        result.add(i+10);
                        break;
                    }
                    else if(squares[i+10] == 0){
                        result.add(i+10);
                        i+=10;
                    }
                    else break;
                }
                i = index;
                while(i+1<=98){
                    if(squares[i+1] >= 1 && squares[i+1] <= 6){
                        result.add(i+1);
                        break;
                    }
                    else if(squares[i+1] == 0){
                        result.add(i+1);
                        i+=1;
                    }
                    else break;
                }
                break;
            case 11:
                i = index;
                while(i-9>=21){
                    if(squares[i-9] >= 1 && squares[i-9] <= 6){
                        result.add(i-9);
                        break;
                    }
                    else if(squares[i-9] == 0){
                        result.add(i-9);
                        i-=9;
                    }
                    else break;
                }
                i = index;
                while(i-11>=21){
                    if(squares[i-11] >= 1 && squares[i-11] <= 6){
                        result.add(i-11);
                        break;
                    }
                    else if(squares[i-11] == 0){
                        result.add(i-11);
                        i-=11;
                    }
                    else break;
                }
                i=index;
                while(i+9 <= 98){
                    if(squares[i+9] >= 1 && squares[i+9] <= 6){
                        result.add(i+9);
                        break;
                    }
                    else if(squares[i+9] == 0){
                        result.add(i+9);
                        i+=9;
                    }
                    else break;
                }
                i=index;
                while(i+11 <= 98){
                    if(squares[i+11] >= 1 && squares[i+11] <= 6){
                        result.add(i+11);
                        break;
                    }
                    else if(squares[i+11] == 0){
                        result.add(i+11);
                        i+=11;
                    }
                    else break;
                }
                i = index;
                while(i-10>=21){
                    if(squares[i-10] >= 1 && squares[i-10] <= 6){
                        result.add(i-10);
                        break;
                    }
                    else if(squares[i-10] == 0){
                        result.add(i-10);
                        i-=10;
                    }
                    else break;
                }
                i = index;
                while(i-1>=21){
                    if(squares[i-1] >= 1 && squares[i-1] <= 6){
                        result.add(i-1);
                        break;
                    }
                    else if(squares[i-1] == 0){
                        result.add(i-1);
                        i-=1;
                    }
                    else break;
                }
                i = index;
                while(i+10<=98){
                    if(squares[i+10] >= 1 && squares[i+10] <= 6){
                        result.add(i+10);
                        break;
                    }
                    else if(squares[i+10] == 0){
                        result.add(i+10);
                        i+=10;
                    }
                    else break;
                }
                i = index;
                while(i+1<=98){
                    if(squares[i+1] >= 1 && squares[i+1] <= 6){
                        result.add(i+1);
                        break;
                    }
                    else if(squares[i+1] == 0){
                        result.add(i+1);
                        i+=1;
                    }
                    else break;
                }
                break;
            case 12:
                if((squares[index-1] >= 1 && squares[index-1] <= 6) || squares[index-1] == 0) result.add(index-1);
                if((squares[index-10] >= 1 && squares[index-10] <= 6) || squares[index-10] == 0) result.add(index-10);
                if((squares[index+1] >= 1 && squares[index+1] <= 6) || squares[index+1] == 0) result.add(index+1);
                if((squares[index+10] >= 1 && squares[index+10] <= 6) || squares[index+10] == 0) result.add(index+10);
                if((squares[index-9] >= 1 && squares[index-9] <= 6) || squares[index-9] == 0) result.add(index-9);
                if((squares[index-11] >= 1 && squares[index-11] <= 6) || squares[index-11] == 0) result.add(index-11);
                if((squares[index+9] >= 1 && squares[index+9] <= 6) || squares[index+9] == 0) result.add(index+9);
                if((squares[index+11] >= 1 && squares[index+11] <= 6) || squares[index+11] == 0) result.add(index+11);
                // King side castle
                if(castling[2] == 1 && squares[26] == 0 && squares[27] == 0 && squares[28] == 10 && squares[25] == 12) result.add(27);
                // Queen side castle
                if(castling[3] == 1 && squares[24] == 0 && squares[23] == 0 && squares[22] == 0 && squares[21] == 10 && squares[25] == 12) result.add(23);
                break;
        }
        return result;
    }
    
    // Generate a list of possible moves that the current side to move can make
    public ArrayList<Move> possibleMoves(){
        // Initialize a new Array List to store our moves
        ArrayList<Move> availableMoves = new ArrayList<Move>();
        // Iterate through all legal squares on the board
        for(int i = 21; i<= 98; i++){
            // If it is white's move, and if the square we are looking at is either empty or belongs to black, skip this square
            if(sideToMove == 0 && (squares[i]==0 || squares[i] >= 7)) continue;
            // If it is black's move, and if the square we are looking at is either empty or belongs to white, skip this square
            if(sideToMove == 1 && (squares[i] <= 6 || squares[i] == 13)) continue;
            // The square belongs to the current side to move, so we can gather all of the moves that piece can make
            for(Integer n : calculateMoves(i)){
                // Create a move object for the potential move and add it to the list of possible moves
                Move m = new Move(i, squares[i], n, squares[n], moveCount+1, castling, enPassant, this);
                availableMoves.add(m);
            }
        }
        // Return the list of legal moves
        return sortMoves(availableMoves);
    }
    
    // Sort the list of possible moves based on the evaluation of performing each move
    public ArrayList<Move> sortMoves(ArrayList<Move> moves){
        Collections.sort(moves, new Comparator<Move>() {
            @Override public int compare(Move m1, Move m2){
                m1.move();
                double e1 = evaluate();
                m1.undoMove();
                m2.move();
                double e2 = evaluate();
                m2.undoMove();
                if((e1 < e2 && sideToMove == 0) || (e1 > e2 && sideToMove == 1)) return -1;
                if((e1 > e2 && sideToMove == 0) || (e1 < e2 && sideToMove == 1)) return 1;
                return 0;
            }
        });
        return moves;
    }


    // Utility Functions
    @Override
    public String toString(){
        String s = "";
        s += "   ________________\n";
        for(int i = 20; i <= 90; i += 10){
            if(i%10 == 0){
                s+= ""+(100-i)/10+" |";
            }
            for(int j = 1; j <= 8; j++){
                switch(this.squares[i+j]){
                    case 0:
                        s += "-|";
                        break;
                    case 1:
                        s += "P|";
                        break;
                    case 2:
                        s += "N|";
                        break;
                    case 3:
                        s += "B|";
                        break;
                    case 4:
                        s += "R|";
                        break;
                    case 5:
                        s += "Q|";
                        break;
                    case 6:
                        s += "K|";
                        break;
                    case 7:
                        s += "p|";
                        break;
                    case 8:
                        s += "n|";
                        break;
                    case 9:
                        s += "b|";
                        break;
                    case 10:
                        s += "r|";
                        break;
                    case 11:
                        s += "q|";
                        break;
                    case 12:
                        s += "k|";
                        break;
                    case 13:
                        s += "*|";
                        break;
                    default:
                        break;
                }
            }
            s += "\n";
            s += "   ________________\n";
        }
        s += "   A B C D E F G H";
        return s;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        Board other = (Board) obj;
        return this.squares == other.squares
                && this.sideToMove == other.sideToMove
                && this.result == other.result
                && this.enPassant == other.enPassant
                && this.castling == other.castling;
    }
    
    @Override
    public int hashCode(){
        final int prime = 31;
        int r = 1;
        r = prime * r + Arrays.hashCode(this.squares);
        r = prime * r + this.sideToMove;
        r = prime * r + this.result;
        r = prime * r + this.enPassant;
        r = prime * r + Arrays.hashCode(this.castling);
        return r;
    }
    
    public String intToPiece(int i){
        switch(i){
            case 1:
                return "P";
            case 2:
                return "N";
            case 3:
                return "B";
            case 4:
                return "R";
            case 5:
                return "Q";
            case 6:
                return "K";
            case 7:
                return "p";
            case 8:
                return "n";
            case 9:
                return "b";
            case 10:
                return "r";
            case 11:
                return "q";
            case 12:
                return "k";
            default:
                return "";
                        
        }
    
    }

    public String indexToCoordinates(int index){
        int i = index - 20;
        String s = "";
        switch(i%10){
            case 1:
                s+="A";
                break;
            case 2:
                s+="B";
                break;
            case 3:
                s+="C";
                break;
            case 4:
                s+="D";
                break;
            case 5:
                s+="E";
                break;
            case 6:
                s+="F";
                break;
            case 7:
                s+="G";
                break;
            case 8:
                s+="H";
                break;
            default:
                break;
        }
        s+= 9-(i/10 + 1);
        return s;
    }
    
}
