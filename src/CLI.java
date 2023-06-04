import java.util.Scanner;

public class CLI {
    static void start(){
        Scanner in = new Scanner(System.in);
        String cmd = in.nextLine();
        while(!cmd.equals("exit")){

            in.nextLine();
        }
    }
}
