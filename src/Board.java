import java.util.ArrayList;
import java.util.Random;
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
        sideToMove = 0;
        result = -1;
        castling = new int[] {1,1,1,1};
        moves = new Stack<Integer>();
    }

    public void move(int from, int to){
        System.out.println("(" + squares[from] + ")"
                + (from-20)%10 + "," + (from-20)/10
                + ((squares[to] != 0)?" X ":" -> ")
                + "(" + squares[from] + ")"
                + (to-20)%10 + "," + (to-20)/10);
        if(squares[to] == 6) result = 1;
        if(squares[to] == 12) result = 0;
        squares[to] = squares[from];
        squares[from] = 0;
        if(sideToMove == 0) sideToMove = 1;
        else sideToMove = 0;
    }

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

    public float evaluate(){
        int result = 0;
        for(int i=0; i<=squares.length-1; i++){
            switch(squares[i]){
                case 1:
                    result += 1;
                    break;
                case 2:
                    result += 3;
                    break;
                case 3:
                    result += 3;
                    break;
                case 4:
                    result += 5;
                    break;
                case 5:
                    result += 10;
                    break;
                case 6:
                    result += 1000;
                    break;
                case 7:
                    result -= 1;
                    break;
                case 8:
                    result -= 3;
                    break;
                case 9:
                    result -= 3;
                    break;
                case 10:
                    result -= 5;
                    break;
                case 11:
                    result -= 10;
                    break;
                case 12:
                    result -= 1000;
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    public ArrayList<Integer> calculateMoves(int index){
        ArrayList<Integer> result = new ArrayList<Integer>();
        int i = index;
        switch(squares[index]){
            case 1: // White Pawn
                if(squares[index-9] >= 7 && squares[index-9] <= 12) result.add(index-9);
                if(squares[index-11] >= 7 && squares[index-11] <= 12) result.add(index-9);
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
                break;
            case 7: // Black Pawn
                if(squares[index+9] >= 1 && squares[index+9] <= 6) result.add(index+9);
                if(squares[index+11] >= 1 && squares[index+11] <= 6) result.add(index+9);
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
                break;
        }
        return result;
    }

    public void search(int depth){
        Random randomGenerator = new Random();
        while(depth >= 0 && this.result == -1){
            int r = randomGenerator.nextInt(80) + 20;
            if(squares[r] == 0 || squares[r] == 13) continue;
            ArrayList<Integer> attackable = calculateMoves(r);
            if(attackable.size() < 1) continue;
            if(sideToMove == 0){
                //White to move
                if(squares[r] >= 7 && squares[r] <= 12 && attackable.size() > 0){
                    int r2 = randomGenerator.nextInt(attackable.size());
                    move(r,attackable.get(r2));
                }
            }

            else if(sideToMove == 1){
                //White to move
                if(squares[r] >= 1 && squares[r] <= 6 && attackable.size() > 0){
                    int r2 = randomGenerator.nextInt(attackable.size());
                    move(r,attackable.get(r2));
                }
            }
            depth--;
        }

    }

}
