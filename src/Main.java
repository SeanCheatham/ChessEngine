
public class Main {
    public static void main(String[] args) {
        Board b = new Board();
        b.search(80);
        System.out.println(b.toString());
        System.out.println(b.evaluate());
        while(b.moveCount >= 0) b.undoMove();
        System.out.println(b.toString());
    }
}
