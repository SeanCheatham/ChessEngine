// Copyright (C) 2014; Sean Cheatham
public class Move {
    public int from;
    public int fromPiece;
    public int to;
    public int toPiece;
    public int moveNumber;
    public int[] castling;
    public int enPassant;
    public Board board;

    public Move(int f, int fp, int t, int tp, int mn, int[] c, int ep, Board b){
        this.from = f;
        this.fromPiece = fp;
        this.to = t;
        this.toPiece = tp;
        this.moveNumber = mn;
        this.castling = c;
        this.enPassant = ep;
        this.board = b;

    }

    // Perform a move
    public void move(){
        // Set en passant square if applicable
        if(fromPiece == 1 && from-to == 20) board.enPassant = from-10;
        if(fromPiece == 7 && to-from == 20) board.enPassant = from+10;
        // Increment the move counter
        board.moveCount = moveNumber;
        // King was captured, so set the result
        if(toPiece == 6) board.result = 1;
        if(toPiece == 12) board.result = 0;
        // Do en passant if applicable
        if(fromPiece == 1 && to == enPassant) board.squares[to-10] = 0;
        if(fromPiece == 7 && to == enPassant) board.squares[to+10] = 0;
        // Reset en passant square
        board.enPassant = -1;
        // Move the pieces
        board.squares[to] = fromPiece;
        board.squares[from] = 0;
        // Switch the side to move
        board.sideToMove = 1 - board.sideToMove;
        // Automatically promote to queen
        if(fromPiece == 1 && to >= 21 && to <= 28) board.squares[to] = 5;
        if(fromPiece == 7 && to >= 91 && to <= 98) board.squares[to] = 11;
        // Castling
        // White King Side
        if(fromPiece == 6 && from == 95 && to == 97){
            if(board.castling[0] == 0){
                System.out.println("Illegal Move");
                System.exit(-1);
            }
            board.squares[98] = 0;
            board.squares[96] = 4;
            board.castling[0] = 0;
        }
        // White Queen Side
        if(fromPiece == 6 && from == 95 && to == 93){
            if(board.castling[1] == 0){
                System.out.println("Illegal Move");
                System.exit(-1);
            }
            board.squares[91] = 0;
            board.squares[94] = 4;
            board.castling[1] = 0;
        }
        // Black King Side
        if(fromPiece == 12 && from == 25 && to == 27){
            if(board.castling[2] == 0){
                System.out.println("Illegal Move");
                System.exit(-1);
            }
            board.squares[28] = 0;
            board.squares[26] = 10;
            board.castling[2] = 0;
        }
        // Black Queen Side
        if(fromPiece == 12 && from == 25 && to == 23){
            if(board.castling[3] == 0){
                System.out.println("Illegal Move");
                System.exit(-1);
            }
            board.squares[21] = 0;
            board.squares[24] = 10;
            board.castling[3] = 0;
        }
        // Detect King/Rook moves and disable castling
        if((fromPiece == 6 && from == 95) || (fromPiece == 4 && from == 98))  board.castling[0] = 0;
        if((fromPiece == 6 && from == 95) || (fromPiece == 4 && from == 91))  board.castling[1] = 0;
        if((fromPiece == 12 && from == 25) || (fromPiece == 10 && from == 28))  board.castling[2] = 0;
        if((fromPiece == 12 && from == 25) || (fromPiece == 10 && from == 91))  board.castling[3] = 0;
    }

    // Undo the move
    public void undoMove(){
        // If for some reason our move count is negative, just return
        if(board.moveCount < 0) return;
        // Pop the last move off the stack
        // Move the pieces back to their original locations
        board.squares[from] = fromPiece;
        board.squares[to] = toPiece;
        // If previous move was en passant, reset the captured piece
        if(to == board.enPassant && fromPiece == 7) board.squares[to-10] = 1;
        if(to == board.enPassant && fromPiece ==17) board.squares[to+10] = 7;
        // If our game ended, reset result back to -1;
        if(toPiece == 6) board.result = -1;
        if(toPiece == 12) board.result = -1;
        // Switch side to move
        board.sideToMove = 1 - board.sideToMove;
        // Decrement move counter
        board.moveCount = moveNumber;
        // Castling
        // White King Side
        if(fromPiece == 6 && from == 95 && to == 97){
            board.squares[98] = 4;
            board.squares[96] = 0;
            board.castling[0] = 1;
        }
        // White Queen Side
        if(fromPiece == 6 && from == 95 && to == 93){
            board.squares[91] = 4;
            board.squares[94] = 0;
            castling[1] = 1;
        }
        // Black King Side
        if(fromPiece == 12 && from == 25 && to == 27){
            board.squares[28] = 10;
            board.squares[26] = 0;
            board.castling[2] = 1;
        }
        // Black Queen Side
        if(fromPiece == 12 && from == 25 && to == 23){
            board.squares[21] = 10;
            board.squares[24] = 0;
            board.castling[3] = 1;
        }
        // FIX ME
        if((fromPiece == 6 && from == 95) || (fromPiece == 4 && from == 98))  board.castling[0] = 1;
        if((fromPiece == 6 && from == 95) || (fromPiece == 4 && from == 91))  board.castling[1] = 1;
        if((fromPiece == 12 && from == 25) || (fromPiece == 10 && from == 28))  board.castling[2] = 1;
        if((fromPiece == 12 && from == 25) || (fromPiece == 10 && from == 91))  board.castling[3] = 1;
    }

    @Override
    public String toString() {
        return board.indexToCoordinates(from)+board.indexToCoordinates(to);
        /*return "(" + board.intToPiece(fromPiece) + ")"
                +board.indexToCoordinates(from)
                + ((toPiece != 0) ? " X " : " -> ")
                + "(" + board.intToPiece(toPiece) + ")"
                +board.indexToCoordinates(to);*/
    }
}
