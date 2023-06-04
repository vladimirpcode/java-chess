import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public  static void main(String[] args) throws ChessEngineIncorrectWork{
        //CLI.start();
        //Tester.checkWhiteKingIsAtaccked();
        //GUI.start();
        /*
        ChessEngine.setPositionFromFen("r5k1/ppp5/n2pNbpQ/3P2Bp/1P6/1P6/2K4P/5q2 b - - 0 1");
        ChessEngine.printPosition();
        ChessEngine.printPositionInfo();
        ArrayList<Move> moves = ChessEngine.getAllMoves(ChessEngine.BLACK);
        for(int i = 0; i < moves.size(); i++){
            System.out.print(moves.get(i) + " ");
        }

        ChessEngine.maxDepth = 8;
        System.out.println("\n\n");
        */
        /*
        Move theBest = ChessEngine.getBestMove(ChessEngine.nextMoveClr, 0);

        System.out.println(theBest);
        System.out.println(theBest.evaluate);
        */
        /*
        int eval = ChessEngine.alphaBeta(-ChessEngine.INFINITY, ChessEngine.INFINITY, 0, ChessEngine.nextMoveClr);
        System.out.println("Оценка хода: " + eval);
        System.out.println(ChessEngine.bestMove);
        System.out.println("Позиций просчитано: "  + ChessEngine.calculatedPositionsCount);
        */


        Scanner scan = new Scanner(System.in);
        ChessEngine.setPositionFromFen("3k3r/Q5pp/2P1Rn2/8/8/2P5/P4PPK/8 b - - 0 30");
        //ChessEngine.setPositionFromFen("rnbqkb1r/pppppppp/7n/8/3PP3/5N2/PPP2PPP/RNBQKB1R w KQq - 1 4");
        ArrayList<Move> moves = ChessEngine.getAllMoves(ChessEngine.BLACK);
        for(Move mv : moves) {
            System.out.println(mv);
        }
        /*
        ChessEngine.printPosition();
        while(!ChessEngine.isCheckmate(ChessEngine.BLACK) && !ChessEngine.isCheckmate(ChessEngine.WHITE)){
            String userMove = scan.nextLine();
            ChessEngine.makeMoveFromUserInput(userMove);
            ChessEngine.printPosition();
            ChessEngine.maxDepth = 6;
            //ChessEngine.alphaBeta(-ChessEngine.INFINITY, ChessEngine.INFINITY, 0, ChessEngine.nextMoveClr);
            Move bestMove = ChessEngine.getBestMove(ChessEngine.nextMoveClr, 0);
            ChessEngine.makeMove(bestMove);
            ChessEngine.printPosition();
            System.out.println("Враг походил: " + bestMove);
        }
        */
    }
}
