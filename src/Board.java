import java.util.ArrayList;
import java.util.Stack;

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
    public static final int[] WHITEPIECES= {1,2,3,4,5,6};
    //  Array of black pieces
    public static final int[] BLACKPIECES= {7,8,9,10,11,12};
    // Stack to keep track of moves
    public Stack<Integer> moves;
    // Counter to keep track of move
    public int moveCount;
    // Counter to keep track of search nodes
    public int nodeCount;

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
        
        squares = new int[]{
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 10, 8, 9, 11, 12, 9, 8, 10, 13,
                13, 7, 7, 7, 7, 7, 7, 7, 7, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 1, 1, 1, 1, 1, 1, 1, 1, 13,
                13, 4, 2, 3, 5, 6, 3, 2, 4, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13
        };

        /*
        squares = new int[]{
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 10, 0, 0, 0, 0, 0, 0, 10, 13,
                13, 7, 0, 7, 0, 12, 7, 0, 7, 13,
                13, 0, 8, 0, 7, 0, 0, 7, 0, 13,
                13, 0, 0, 0, 9, 0, 0, 0, 0, 13,
                13, 0, 0, 2, 0, 0, 1, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 1, 1, 0, 0, 1, 0, 3, 1, 13,
                13, 4, 0, 4, 0, 0, 0, 6, 0, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13
        };
        */
        /*
        squares = new int[]{
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 0, 0, 0, 0, 13,
                13, 0, 0, 0, 0, 6, 0, 0, 4, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
                13, 13, 13, 13, 13, 13, 13, 13, 13, 13
        };
        */
        sideToMove = 0;
        result = -1;
        castling = new int[] {1,1,1,1};
        enPassant = -1;
        moves = new Stack<Integer>();
        moveCount = 1;
        nodeCount = 0;
    }

    public void move(Move m){
        if(m == null){
            System.out.println("Illegal Move Attempted");
            System.exit(0);
        }
        // Set en passant square if applicable
        if(m.fromPiece == 1 && m.from-m.to == 20) enPassant = m.from-10;
        if(m.fromPiece == 7 && m.to-m.from == 20) enPassant = m.from+10;
        // Push the move information onto a stack
        moves.push(m.fromPiece);
        moves.push(m.from);
        moves.push(m.toPiece);
        moves.push(m.to);
        moves.push(castling[0]);
        moves.push(castling[1]);
        moves.push(castling[2]);
        moves.push(castling[3]);
        moves.push(enPassant);
        moves.push(moveCount+1);
        // Increment the move counter
        moveCount++;
        // King was captured, so set the result
        if(m.toPiece == 6) result = 1;
        if(m.toPiece == 12) result = 0;
        // Do en passant if applicable
        if(m.fromPiece == 1 && m.to == enPassant) squares[m.to-10] = 0;
        if(m.fromPiece == 7 && m.to == enPassant) squares[m.to+10] = 0;
        // Reset en passant square
        enPassant = -1;
        // Move the pieces
        squares[m.to] = m.fromPiece;
        squares[m.from] = 0;
        // Switch the side to move
        sideToMove = 1 - sideToMove;
        // Automatically promote to queen
        if(m.fromPiece == 1 && m.to >= 21 && m.to <= 28) squares[m.to] = 5;
        if(m.fromPiece == 7 && m.to >= 91 && m.to <= 98) squares[m.to] = 11;
        // Castling
        // White King Side
        if(m.fromPiece == 6 && m.from == 95 && m.to == 97){
            squares[98] = 0;
            squares[96] = 4;
            castling[0] = 0;
        }
        // White Queen Side
        if(m.fromPiece == 6 && m.from == 95 && m.to == 93){
            squares[91] = 0;
            squares[94] = 4;
            castling[1] = 0;
        }
        // Black King Side
        if(m.fromPiece == 12 && m.from == 25 && m.to == 27){
            squares[28] = 0;
            squares[26] = 10;
            castling[2] = 0;
        }
        // Black Queen Side
        if(m.fromPiece == 12 && m.from == 25 && m.to == 23){
            squares[21] = 0;
            squares[24] = 10;
            castling[3] = 0;
        }
    }

    public void undoMove(){
        // If for some reason our move count is negative, just return
        if(moveCount < 0) return;
        // Pop the last move off the stack
        this.moveCount = moves.pop()-1;
        this.enPassant = moves.pop();
        this.castling[3] = moves.pop();
        this.castling[2] = moves.pop();
        this.castling[1] = moves.pop();
        this.castling[0] = moves.pop();
        int to = moves.pop();
        int toSq = moves.pop();
        int from = moves.pop();
        int fromSq = moves.pop();
        // Move the pieces back to their original locations
        this.squares[from] = fromSq;
        this.squares[to] = toSq;
        // If previous move was en passant, reset the captured piece
        if(to == enPassant && fromSq == 7) squares[to-10] = 1;
        if(to == enPassant && fromSq ==17) squares[to+10] = 7;
        // If our game ended, reset result back to -1;
        if(toSq == 6) result = -1;
        if(toSq == 12) result = -1;
        sideToMove = 1 - sideToMove;
        // Castling
        // White King Side
        if(fromSq == 6 && from == 95 && to == 97){
            squares[98] = 4;
            squares[96] = 0;
            castling[0] = 1;
        }
        // White Queen Side
        if(fromSq == 6 && from == 95 && to == 93){
            squares[91] = 4;
            squares[94] = 0;
            castling[1] = 1;
        }
        // Black King Side
        if(fromSq == 12 && from == 25 && to == 27){
            squares[28] = 10;
            squares[26] = 0;
            castling[2] = 1;
        }
        // Black Queen Side
        if(fromSq == 12 && from == 25 && to == 23){
            squares[21] = 10;
            squares[24] = 0;
            castling[3] = 1;
        }

    }

    @Override
    public String toString(){
        String s = new String();
        for(int i = 20; i <= 90; i += 10){
            for(int j = 1; j <= 8; j++){
                switch(this.squares[i+j]){
                    case 0:
                        s += "-";
                        break;
                    case 1:
                        s += "P";
                        break;
                    case 2:
                        s += "N";
                        break;
                    case 3:
                        s += "B";
                        break;
                    case 4:
                        s += "R";
                        break;
                    case 5:
                        s += "Q";
                        break;
                    case 6:
                        s += "K";
                        break;
                    case 7:
                        s += "p";
                        break;
                    case 8:
                        s += "n";
                        break;
                    case 9:
                        s += "b";
                        break;
                    case 10:
                        s += "r";
                        break;
                    case 11:
                        s += "q";
                        break;
                    case 12:
                        s += "k";
                        break;
                    case 13:
                        s += "*";
                        break;
                    default:
                        break;
                }
            }
            s += "\n";
        }
        return s;
    }

    public double evaluate(){
        double val = 0.0;
        // Number of pawns for each team
        // If there are fewer than 8 pawns on the board, the game is considered "open".  Else, game is considered "closed".
        int wPawns = 0;
        int bPawns = 0;
        for(int i=0; i<=squares.length-1; i++){
            if(squares[i]== 1) wPawns++;
            if(squares[i]== 7) bPawns++;
        }
        // Weight of knight pieces
        // This formula guarantees a weight between 0.75 and 1
        // If there are 16 pawns on the board, the game is "closed", and knights are more useful (thus weighted at 1.0)
        double knightWeight = (double) (1 - (16.0-(wPawns+bPawns))/64.0);
        // Weight of bishop pieces
        // This formula guarantees a weight between 1 and 1.25
        // If there are no pawns on the board, the game is "open", and bishops are more useful (thus weighted at 1.25)
        double bishopWeight = (double) (1 + (16.0-(wPawns+bPawns))/64.0);
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
    // Function that generates the possible moves that the piece aat a given square can make
    public ArrayList<Integer> calculateMoves(int index){
        ArrayList<Integer> result = new ArrayList<Integer>();
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
                Move m = new Move(i, squares[i], n, squares[n], moveCount+1);
                availableMoves.add(m);
            }
        }
        // Return the list of legal moves
        return availableMoves;
    }
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
            move(m);
            // Call the Alpha/Beta function (start with max), and store it in a temporary variable
            double x = alphaBetaMax(-1000000,1000000,depth-1);
            // Output the move we are looking at, as well as whatever Alpha/Beta says for its value
            System.out.println(m+" : "+x);
            // Undo the move so that we're back to the previous state
            undoMove();
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
        nodeCount++;
        // Base case: If we're at the bottom of the tree, evaluate the board and return
        if (depth == 0) return evaluate();
        // Iterate through all possible moves
        for(Move m : possibleMoves()){
            // If we somehow end up with a null move, just return the evaluation of the board
            if(m == null) return evaluate();
            // Do the move
            move(m);
            // If that move we just did ends the game
            if(result > -1) {
                // Evaluate the board
                double eval = evaluate();
                // Undo the move to return to our previous state
                undoMove();
                // Return the board evaluation
                return eval;
            }
            // Run Alpha/Beta (min)
            score = alphaBetaMin(alpha, beta, depth-1);
            // Undo the move to return to our previous state
            undoMove();
            // If our score somehow ends up higher than beta, then we cut the line (pruning)
            if(score>= beta) return beta;
            // If the score is greater than alpha, set alpha to the score
            if(score>alpha) alpha = score;
        }
        // Return alpha
        return alpha;
    }
    // Same idea as with Alpha/Beta Max, except we switch the side for which we are evaluating
    public double alphaBetaMin(double alpha, double beta, int depth){
        double score = beta;
        nodeCount++;
        if (depth == 0) return evaluate();
        for(Move m : possibleMoves()){
            if(m == null) return beta;
            move(m);
            if(result > -1) {
                double eval = evaluate();
                undoMove();
                return eval;
            }
            score = alphaBetaMax(alpha, beta, depth-1);
            undoMove();
            if(score <= alpha) return alpha;
            if(score < beta) beta = score;
        }
        return beta;
    }

}
