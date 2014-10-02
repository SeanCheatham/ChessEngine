
public class Main {
    final public static int MAXDEPTH = 8;
    public static void main(String[] args) {
        Board b = new Board();
        /*Move s = new Move(85,1,65,0,1);
        b.move(s);
        Move x = new Move(27,8,46,0,2);
        b.move(x);
        Move y = new Move(97,2,76,0,3);
        b.move(y);*/
        long t1 = System.currentTimeMillis();
        Move m = b.search(MAXDEPTH);
        long t2 = System.currentTimeMillis();
        System.out.println("Recommended Move: "+m);
        long time = t2-t1 + 1000;
        b.move(m);
        System.out.println("Nodes Searched: "+b.nodeCount+" in "+(time)/1000+" seconds");
        System.out.println(""+b.nodeCount/((time)/1000)+" nodes/sec");
        System.out.println(b.toString());
    }
}
