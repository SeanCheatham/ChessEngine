
public class Main {
    public static void main(String[] args) {
        Board b = new Board();
        long t1 = System.currentTimeMillis();
        Move m = b.search(2);
        long t2 = System.currentTimeMillis();
        System.out.println("Recommended Move: "+m);
        long time = t2-t1 + 100;
        System.out.println("Nodes Searched: "+b.nodeCount+" in "+(time)/100+" seconds");
        System.out.println(""+b.nodeCount/((time)/100)+" nodes/sec");
        System.out.println(b.toString());
        System.out.println(b.evaluate());
    }
}
