import java.util.ArrayList;

class ChessEngineIncorrectWork extends Exception{

}

class Predicate{
    String fen;
    boolean value;

    Predicate(String fen, boolean value){
        this.fen = fen;
        this.value = value;
    }
}

class Tester {
    static ArrayList<Predicate> whiteKingIsAttacked;
    static ArrayList<Predicate> blackKingIsAttacked;

    static {
        whiteKingIsAttacked = new ArrayList<>();
        blackKingIsAttacked = new ArrayList<>();
    }

    static void checkWhiteKingIsAtaccked() throws ChessEngineIncorrectWork{
        whiteKingIsAttacked.add(new Predicate("3q4/8/8/8/8/3K4/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/8/8/3K2q1/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/8/8/3K4/8/3q3k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/8/8/q2K4/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("1q6/8/8/4K3/8/8/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("7q/8/8/4K3/8/8/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/4K3/8/6q1/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/4K3/8/2q5/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/4K3/8/8/8/b6k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/4K3/8/6b1/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/6b1/8/4K3/8/8/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("1b6/8/8/4K3/8/8/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/1b6/8/8/4K3/8/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/7b/8/8/4K3/8/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/8/4K3/8/6b1/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/8/4K3/8/2b5/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/2nRRR2/3RKR2/3RRR2/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/3RRR2/3RKR2/2nRRR2/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/3RRR2/3RKR2/3RRR2/3n4/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/3RRR2/3RKR2/3RRR2/5n2/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/3RRR2/3RKR2/3RRRn1/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/8/3RRRn1/3RKR2/3RRR2/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/5n2/3RRR2/3RKR2/3RRR2/8/7k w KQkq - 0 1", true));
        whiteKingIsAttacked.add(new Predicate("8/8/3n4/3RRR2/3RKR2/3RRR2/8/7k w KQkq - 0 1", true));

        for(int i = 0; i < whiteKingIsAttacked.size(); i++){
            ChessEngine.setPositionFromFen(whiteKingIsAttacked.get(i).fen);
            if(ChessEngine.isAttacked(ChessEngine.whiteKingPosition, ChessEngine.BLANK) != whiteKingIsAttacked.get(i).value){
                throw new ChessEngineIncorrectWork();
            }
        }
    }
}
