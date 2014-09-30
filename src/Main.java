
public class Main {
    public static void main(String[] args) {
        Board b = new Board();
        Move m = b.search(8);
        System.out.println("Recommended Move: "+m);
        System.out.println("Nodes Searched: "+b.nodeCount);
        System.out.println(b.toString());
        System.out.println(b.evaluate());
    }
}
