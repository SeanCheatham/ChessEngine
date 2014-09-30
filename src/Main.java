
public class Main {
    public static void main(String[] args) {
        Board b = new Board();
        long t1 = System.currentTimeMillis();
        Move m = b.search(4);
        long t2 = System.currentTimeMillis();
        System.out.println("Recommended Move: "+m);
        long time = t2-t1 + 1000;
        System.out.println("Nodes Searched: "+b.nodeCount+" in "+(time)/1000+" seconds");
        System.out.println(""+b.nodeCount/((time)/1000)+" nodes/sec");
        b.move(m);
        System.out.println(b.toString());
    }
}
