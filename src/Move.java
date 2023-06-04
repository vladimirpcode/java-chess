class Move{
    Position start;
    Position end;
    boolean doneEnPassant; //делается взятие на проходе
    boolean pawnTransform; //превращение пешки
    int pawnTransformPiece; //во что превращается
    boolean takingIsDone;
    int capturedPiece;
    boolean shortCastlingLost; //потеря рокировки // для ладьи и короля
    boolean longCastlingLost; //потеря рокировки // для ладьи и короля
    int localMovesWhithoutMistakes;
    int evaluate;

    Move(int evaluate){
        this.evaluate = evaluate;
    }
    Move(int x1, int y1, int x2, int y2){
        start = new Position(x1,y1);
        end = new Position(x2,y2);
        doneEnPassant = false;
        pawnTransform = false;
        pawnTransformPiece = ChessEngine.BLANK;
        takingIsDone = false;
        capturedPiece = ChessEngine.BLANK;
        shortCastlingLost = false;
        longCastlingLost = false;
    }

    Move(Position start, Position end){
        this.start = start;
        this.end = end;
        doneEnPassant = false;
        pawnTransform = false;
        pawnTransformPiece = ChessEngine.BLANK;
        takingIsDone = false;
        capturedPiece = ChessEngine.BLANK;
        shortCastlingLost = false;
        longCastlingLost = false;
    }
    Move(Move mv){
        this.start = new Position(mv.start);
        this.end = new Position(mv.end);
        this.doneEnPassant = mv.doneEnPassant;
        this.pawnTransform = mv.pawnTransform;
        this.pawnTransformPiece = mv.pawnTransformPiece;
        this.takingIsDone = mv.takingIsDone;
        this.capturedPiece = mv.capturedPiece;
        this.shortCastlingLost = mv.shortCastlingLost;
        this.longCastlingLost = mv.longCastlingLost;
        this.localMovesWhithoutMistakes = mv.localMovesWhithoutMistakes;
    }

    @Override
    public String toString() {

        //ходы пешек вперед
        if(ChessEngine.board[start.x][start.y] == ChessEngine.PAWN && start.x == end.x && !pawnTransform){
            return end.toString();
        }
        //рокировка


        return start.toString() + "-"+ end.toString();
    }

    void printAll(){
        System.out.println("Start: " + start);
        System.out.println("End: " + end);
        System.out.println("doneEnPassant: " + doneEnPassant);
        System.out.println("pawnTransform: " + pawnTransform);
        System.out.println("pawnTransformPiece: " + pawnTransformPiece);
        System.out.println("takingIsDone: " + takingIsDone);
        System.out.println("capturedPiece: " + capturedPiece);
        System.out.println("shortCastlingLost: " + shortCastlingLost);
        System.out.println("longCastlingLost: " + longCastlingLost);
        System.out.println("localMovesWhithoutMistakes: " + localMovesWhithoutMistakes);
    }
}