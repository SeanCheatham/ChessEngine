/**
 * Created by Sean on 9/29/2014.
 */
public class Move {
    public int from;
    public int fromPiece;
    public int to;
    public int toPiece;
    public int moveNumber;

    public Move(int f, int fp, int t, int tp, int mn){
        this.from = f;
        this.fromPiece = fp;
        this.to = t;
        this.toPiece = tp;
        this.moveNumber = mn;
    }
    public String toString() {
        String s = new String();
        s = "(" + fromPiece + ")"
                //+ (from - 20) % 10 + "," + (from - 20) / 10
                +from
                + ((toPiece != 0) ? " X " : " -> ")
                + "(" + toPiece + ")"
                +to;
                //+ (to - 20) % 10 + "," + (to - 20) / 10;
        return s;
    }
}
