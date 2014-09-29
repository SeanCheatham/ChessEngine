/**
 * Created by Sean on 9/27/2014.
 */
public class Main {
    public static void main(String[] args){
        Board b = new Board();
        b.search(80);
        System.out.println(b.toString());
        System.out.println(b.evaluate());
    }


}
