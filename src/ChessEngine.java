import java.util.ArrayList;
import java.util.Stack;


class WrongMoveException extends Exception{

}
class Position{
    public int x;
    public int y;

    Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    Position(Position pos){
        this.x = pos.x;
        this.y = pos.y;
    }

    @Override
    public String toString() {

        return (char)(x+97) + String.valueOf(y+1);
    }
}


public class ChessEngine {
    static final int BLANK = 0; //ноль обязательно, участвует в расчетах
    static final int PAWN = 100;
    static final int BISHOP = 310;
    static final int KNIGHT = 300;
    static final int ROCK = 500;
    static final int QUEEN = 900;
    static final int KING = 100_000;
    static final int INFINITY = 1_000_000;
    static final int BLACK = 0;
    static final int WHITE = 1;
    static final int DEFAULT_DEPTH = 6;

    //для подсчета количества просчитанных позиций
    static int calculatedPositionsCount = 0;
    static int maxDepth = 4;
    //отражает текущую позицию
    static int[][] board = new int[8][8];
    static int[][] colors = new int[8][8];
    //флаги рокировки и др
    static boolean canWhiteShortCastling;
    static boolean canWhiteLongCastling;
    static boolean canBlackShortCastling;
    static boolean canBlackLongCastling;
    static boolean canEnPassant; //взятие на проходе
    static Position enPassantPos; //позиция, куда пешка пойдет при взятии на проходе
    static Position whiteKingPosition; //позиция белого короля
    static Position blackKingPosition;
    static Stack<Move> globalMoves; //стек ходов
    static int nextMoveClr; //следующий ход белых или черных?
    static int movesWithoutMistakes; //количество ходов без взятий и движений пешек для правила 50 ходов
    static int nextMoveNumber; //номер следующего хода в текущей позиции
    static Move bestMove; //лучший ход при поиске

    static boolean isCheckmate(int color){
        Position kingPos;
        if(color == WHITE)
            kingPos = whiteKingPosition;
        else
            kingPos = blackKingPosition;
        if(isAttacked(kingPos, getEnemyColor(color)) && getAllMoves(color).size() == 0)
            return true;
        return false;
    }

    static int evaluate(int color) {
        if(isCheckmate(color)){
            return -INFINITY;
        }
        int sum = 0;
        int enemySum = 0;
        for (int i = 0; i < 8; i++) {
            for (int k = 0; k < 8; k++) {
                if (color == colors[i][k] && board[i][k] != BLANK)
                    sum += board[i][k];
                else if(color != colors[i][k] && board[i][k] != BLANK)
                    enemySum += board[i][k] ;
            }
        }

        return sum-enemySum;
    }



    //служебный метод для методов getAllMoves..();
    //проверяет на шах своему королю после сделанного хода
    //если шах есть, значит ход сделать нельзя
    static boolean isCorrectMove(Move move, int enemyClr){
        makeMove(move);
        Position kingPos;
        if(enemyClr == BLACK)
            kingPos = whiteKingPosition;
        else
            kingPos = blackKingPosition;
        boolean result = !isAttacked(kingPos, enemyClr);
        cancelMove();
        return  result;
    }

    //служебный метод
    //проверяет не выходят ли значения за диапозон [0..7]
    static  boolean isFieldNumbersCorrect(int x, int y){
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }
    //служебный метод; определяет цвет противника
    static int getEnemyColor(int color){
        if(color == WHITE)
            return BLACK;
        else
            return WHITE;
    }

    static void getAllMovesWithPawn(int x, int y, int color, ArrayList<Move> result) {
        int enemyClr = getEnemyColor(color);

        if(color == WHITE) {
            //проверяем можно ли кого побить
            if (isPieceInField(enemyClr, x - 1, y + 1)) {
                Move mv = new Move(x, y, x - 1, y + 1);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x-1][y+1];
                //заодно обрабатываем превращение пешки
                if(y == 6){
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = QUEEN;
                }
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                    if(y == 6){
                        mv = new Move(mv);
                        mv.pawnTransformPiece = ROCK;
                        result.add(mv);
                        mv = new Move(mv);
                        mv.pawnTransformPiece = KNIGHT;
                        result.add(mv);
                        mv = new Move(mv);
                        mv.pawnTransformPiece = BISHOP;
                        result.add(mv);
                    }
                }
            }
            if (isPieceInField(enemyClr, x + 1, y + 1)) {
                Move mv = new Move(x, y, x + 1, y + 1);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x+1][y+1];
                //заодно обрабатываем превращение пешки
                if(y == 6){
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = QUEEN;
                }
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                    if(y == 6){
                        mv = new Move(mv);
                        mv.pawnTransformPiece = ROCK;
                        result.add(mv);
                        mv = new Move(mv);
                        mv.pawnTransformPiece = KNIGHT;
                        result.add(mv);
                        mv = new Move(mv);
                        mv.pawnTransformPiece = BISHOP;
                        result.add(mv);
                    }
                }
            }
            //можно ли пойти вверх на 1 поле
            if(y <= 5){
                if(!isPieceInField(x,y+1)){
                    Move mv = new Move(x,y,x,y+1);
                    if(isCorrectMove(mv,enemyClr)){
                        result.add(mv);
                    }
                }
            }
            //можно ли пойти вверх на 2 поля
            if(y == 1 && !isPieceInField(x,y+1) && !isPieceInField(x,y+2)){
                Move mv = new Move(x,y,x,y+2);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
            //превращение пешки по прямой
            if(y == 6 && !isPieceInField(x,y+1)){
                Move mv = new Move(x,y,x,y+1);
                mv.pawnTransform = true;
                mv.pawnTransformPiece = QUEEN;
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                    //т.к. шаха не будет, генерируем превращения для остальных фигур
                    mv = new Move(x,y,x,y+1);
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = ROCK;
                    result.add(mv);
                    mv = new Move(x,y,x,y+1);
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = KNIGHT;
                    result.add(mv);
                    mv = new Move(x,y,x,y+1);
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = BISHOP;
                    result.add(mv);
                }
            }
            //можно ли взять на проходе
            if(canEnPassant && y == 4 && (Math.abs(enPassantPos.x - x)==1)  && colors[enPassantPos.x][enPassantPos.y-1] == enemyClr){
                //проверка на цвет, если кто вдруг захочет найти ходы для своего же цвета после своего же хода пешкой на 2...
                Move mv = new Move(x,y,enPassantPos.x,enPassantPos.y);
                mv.doneEnPassant = true;
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
            }
        }else{
            //color == BLACK
            //проверяем можно ли кого побить
            if (isPieceInField(enemyClr, x - 1, y - 1)) {
                Move mv = new Move(x, y, x - 1, y - 1);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x-1][y-1];
                //заодно обрабатываем превращение пешки
                if(y == 1){
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = QUEEN;
                }
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                    if(y == 1){
                        mv = new Move(mv);
                        mv.pawnTransformPiece = ROCK;
                        result.add(mv);
                        mv = new Move(mv);
                        mv.pawnTransformPiece = KNIGHT;
                        result.add(mv);
                        mv = new Move(mv);
                        mv.pawnTransformPiece = BISHOP;
                        result.add(mv);
                    }
                }
            }
            if (isPieceInField(enemyClr, x + 1, y - 1)) {
                Move mv = new Move(x, y, x + 1, y - 1);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x+1][y-1];
                //заодно обрабатываем превращение пешки
                if(y == 1){
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = QUEEN;
                }
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                    if(y == 1){
                        mv = new Move(mv);
                        mv.pawnTransformPiece = ROCK;
                        result.add(mv);
                        mv = new Move(mv);
                        mv.pawnTransformPiece = KNIGHT;
                        result.add(mv);
                        mv = new Move(mv);
                        mv.pawnTransformPiece = BISHOP;
                        result.add(mv);
                    }
                }
            }
            //можно ли пойти вниз на 1 поле
            if(y >= 2){
                if(!isPieceInField(x,y-1)){
                    Move mv = new Move(x,y,x,y-1);
                    if(isCorrectMove(mv,enemyClr)){
                        result.add(mv);
                    }
                }
            }
            //можно ли пойти вниз на 2 поля
            if(y == 6 && !isPieceInField(x,y-1) && !isPieceInField(x,y-2)){
                Move mv = new Move(x,y,x,y-2);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
            //превращение пешки
            if(y == 1 && !isPieceInField(x,y-1)){
                Move mv = new Move(x,y,x,y-1);
                mv.pawnTransform = true;
                mv.pawnTransformPiece = QUEEN;
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                    //т.к. шаха не будет, генерируем превращения для остальных фигур
                    mv = new Move(x,y,x,y-1);
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = ROCK;
                    result.add(mv);
                    mv = new Move(x,y,x,y-1);
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = KNIGHT;
                    result.add(mv);
                    mv = new Move(x,y,x,y-1);
                    mv.pawnTransform = true;
                    mv.pawnTransformPiece = BISHOP;
                    result.add(mv);
                }
            }
            //можно ли взять на проходе
            if(canEnPassant && y == 3 && (Math.abs(enPassantPos.x - x)==1) && colors[enPassantPos.x][enPassantPos.y+1] == enemyClr){
                //проверка на цвет, если кто вдруг захочет найти ходы для своего же цвета после своего же хода пешкой на 2...
                Move mv = new Move(x,y,enPassantPos.x,enPassantPos.y);
                mv.doneEnPassant = true;
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
            }
        }

    }
    //служебный ход для getAllMovesWithRock
    static void handleRockMove(int x, int y, int xIncrement, int yIncrement, int color, int enemyClr,
                               boolean shortCastlingLost, boolean longCastlingLost, ArrayList<Move> result){
        int xx = x + xIncrement;
        int yy = y + yIncrement;
        while(isFieldNumbersCorrect(xx,yy) && !isPieceInField(color,xx,yy)) {
            if (isPieceInField(enemyClr, xx, yy)) {
                //взятие
                Move mv = new Move(x, y, xx, yy);
                mv.takingIsDone = true;
                mv.capturedPiece = board[xx][yy];
                if(shortCastlingLost)
                    mv.shortCastlingLost = true;
                if(longCastlingLost)
                    mv.longCastlingLost = true;
                if (isCorrectMove(mv, enemyClr)) {
                    result.add(mv);
                }
                break;
            } else {
                //просто ход
                Move mv = new Move(x, y, xx, yy);
                if(shortCastlingLost)
                    mv.shortCastlingLost = true;
                if(longCastlingLost)
                    mv.longCastlingLost = true;
                if (isCorrectMove(mv, enemyClr)) {
                    result.add(mv);
                }
            }
            xx += xIncrement;
            yy += yIncrement;
        }
    }
    static void getAllMovesWithRock(int x, int y, int color, ArrayList<Move> result) {
        int enemyClr = getEnemyColor(color);

        boolean shortCastlingLost = false;
        boolean longCastlingLost = false;

        //фиксация потери рокировки в Move, если она имеет место (сама фиксация в handleRockMove)
        if(x == 7){
            if(color == WHITE && canWhiteShortCastling){
                shortCastlingLost = true;
            }
            if(color == BLACK && canBlackShortCastling){
                shortCastlingLost = true;
            }
        }
        if(x == 0){
            if(color == WHITE && canWhiteLongCastling){
                longCastlingLost = true;
            }
            if(color == BLACK && canBlackLongCastling){
                longCastlingLost = true;
            }
        }
        //ходы вверх
        handleRockMove(x,y,0,1,color,enemyClr,shortCastlingLost,longCastlingLost,result);
        //ходы вниз
        handleRockMove(x,y,0,-1,color,enemyClr,shortCastlingLost,longCastlingLost,result);
        //ходы влево
        handleRockMove(x,y,-1,0,color,enemyClr,shortCastlingLost,longCastlingLost,result);
        //ходы вправо
        handleRockMove(x,y,1,0,color,enemyClr,shortCastlingLost,longCastlingLost,result);
    }
    static void getAllMovesWithKnight(int x, int y, int color, ArrayList<Move> result) {
        int enemyClr;
        if(color == WHITE)
            enemyClr = BLACK;
        else
            enemyClr = WHITE;
        //ходы влево
        if(isFieldNumbersCorrect(x-2,y+1) && !isPieceInField(color,x-2,y+1)){
            if(isPieceInField(enemyClr, x-2, y+1)){
                //взятие
                Move mv = new Move(x,y,x-2,y+1);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x-2][y+1];
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }else{
                //простой ход
                Move mv = new Move(x,y,x-2,y+1);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
        }
        if(isFieldNumbersCorrect(x-2,y-1) && !isPieceInField(color,x-2,y-1)){
            if(isPieceInField(enemyClr, x-2, y-1)){
                //взятие
                Move mv = new Move(x,y,x-2,y-1);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x-2][y-1];
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }else{
                //простой ход
                Move mv = new Move(x,y,x-2,y-1);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
        }
        //ходы вправо
        if(isFieldNumbersCorrect(x+2,y+1) && !isPieceInField(color,x+2,y+1)){
            if(isPieceInField(enemyClr, x+2, y+1)){
                //взятие
                Move mv = new Move(x,y,x+2,y+1);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x+2][y+1];
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }else{
                //простой ход
                Move mv = new Move(x,y,x+2,y+1);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
        }
        if(isFieldNumbersCorrect(x+2,y-1) && !isPieceInField(color,x+2,y-1)){
            if(isPieceInField(enemyClr, x+2, y-1)){
                //взятие
                Move mv = new Move(x,y,x+2,y-1);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x+2][y-1];
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }else{
                //простой ход
                Move mv = new Move(x,y,x+2,y-1);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
        }
        //ходы вверх
        if(isFieldNumbersCorrect(x-1,y+2) && !isPieceInField(color,x-1,y+2)){
            if(isPieceInField(enemyClr, x-1, y+2)){
                //взятие
                Move mv = new Move(x,y,x-1,y+2);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x-1][y+2];
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }else{
                //простой ход
                Move mv = new Move(x,y,x-1,y+2);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
        }
        if(isFieldNumbersCorrect(x+1,y+2) && !isPieceInField(color,x+1,y+2)){
            if(isPieceInField(enemyClr, x+1, y+2)){
                //взятие
                Move mv = new Move(x,y,x+1,y+2);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x+1][y+2];
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }else{
                //простой ход
                Move mv = new Move(x,y,x+1,y+2);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
        }
        //ходы вниз
        if(isFieldNumbersCorrect(x-1,y-2) && !isPieceInField(color,x-1,y-2)){
            if(isPieceInField(enemyClr, x-1, y-2)){
                //взятие
                Move mv = new Move(x,y,x-1,y-2);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x-1][y-2];
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }else{
                //простой ход
                Move mv = new Move(x,y,x-1,y- 2);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
        }
        if(isFieldNumbersCorrect(x+1,y-2) && !isPieceInField(color,x+1,y-2)){
            if(isPieceInField(enemyClr, x+1, y-2)){
                //взятие
                Move mv = new Move(x,y,x+1,y-2);
                mv.takingIsDone = true;
                mv.capturedPiece = board[x+1][y-2];
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }else{
                //простой ход
                Move mv = new Move(x,y,x+1,y-2);
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
        }
    }
    static void getAllMovesWithBishop(int x, int y, int color, ArrayList<Move> result) {
        int enemyClr;
        if(color == WHITE){
            enemyClr = BLACK;
        }else{
            enemyClr = WHITE;
        }
        //вправо вверх
        int xx = x+1;
        int yy = y+1;
        while(isFieldNumbersCorrect(xx,yy) && !isPieceInField(color,xx,yy)){
            if(isPieceInField(enemyClr,xx,yy)){
                //взятие
                Move mv = new Move(x,y,xx,yy);
                mv.takingIsDone = true;
                mv.capturedPiece = board[xx][yy];
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
                break;
            }else{
                //просто ход
                Move mv = new Move(x,y,xx,yy);
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
            }
            xx++;
            yy++;
        }
        //влево вверх
        xx = x-1;
        yy = y+1;
        while(isFieldNumbersCorrect(xx,yy) && !isPieceInField(color,xx,yy)){
            if(isPieceInField(enemyClr,xx,yy)){
                //взятие
                Move mv = new Move(x,y,xx,yy);
                mv.takingIsDone = true;
                mv.capturedPiece = board[xx][yy];
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
                break;
            }else{
                //просто ход
                Move mv = new Move(x,y,xx,yy);
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
            }
            xx--;
            yy++;
        }
        //влево вниз
        xx = x-1;
        yy = y-1;
        while(isFieldNumbersCorrect(xx,yy) && !isPieceInField(color,xx,yy)){
            if(isPieceInField(enemyClr,xx,yy)){
                //взятие
                Move mv = new Move(x,y,xx,yy);
                mv.takingIsDone = true;
                mv.capturedPiece = board[xx][yy];
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
                break;
            }else{
                //просто ход
                Move mv = new Move(x,y,xx,yy);
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
            }
            xx--;
            yy--;
        }
        //вправо вниз
        xx = x+1;
        yy = y-1;
        while(isFieldNumbersCorrect(xx,yy) && !isPieceInField(color,xx,yy)){
            if(isPieceInField(enemyClr,xx,yy)){
                //взятие
                Move mv = new Move(x,y,xx,yy);
                mv.takingIsDone = true;
                mv.capturedPiece = board[xx][yy];
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
                break;
            }else{
                //просто ход
                Move mv = new Move(x,y,xx,yy);
                if(isCorrectMove(mv, enemyClr)){
                    result.add(mv);
                }
            }
            xx++;
            yy--;
        }
    }
    static void getAllMovesWithQueen(int x, int y, int color, ArrayList<Move> result) {
        //поскольку класс Move не хранит информацию о фигуре, которая ходит, это работает
        getAllMovesWithBishop(x,y,color,result);
        getAllMovesWithRock(x,y,color,result);
    }
    //служебный метод для getAllMovesWithKing
    static void handleKingMove(int x,int y, int dstX, int dstY, int color, int enemyClr, boolean shortCastlingLost,
                               boolean longCastlingLost, ArrayList<Move> result){
        if(isFieldNumbersCorrect(dstX, dstY) && !isPieceInField(color,dstX,dstY)){
            if(isPieceInField(enemyClr,dstX,dstY)){
                //взятие
                Move mv = new Move(x,y,dstX,dstY);
                mv.takingIsDone = true;
                mv.capturedPiece = board[dstX][dstY];
                if(shortCastlingLost)
                    mv.shortCastlingLost = true;
                if(longCastlingLost)
                    mv.longCastlingLost = true;
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }else{
                //просто ход
                Move mv = new Move(x,y,dstX,dstY);
                if(shortCastlingLost)
                    mv.shortCastlingLost = true;
                if(longCastlingLost)
                    mv.longCastlingLost = true;
                if(isCorrectMove(mv,enemyClr)){
                    result.add(mv);
                }
            }
        }
    }
    static void getAllMovesWithKing(int x, int y, int color, ArrayList<Move> result) {
        int enemyClr = getEnemyColor(color);
        //определяем потерю рокировки. Если обе рокировки потеряны ладьями, то дополнительно ничего сохранять не надо
        //т.к. при обратном ходе рекурсии все равно рокировка восстановится раньше чем походит король
        //главное записать потерю рокировки в Move, остальное решается в makeMove() и cancelMove()
        boolean shortCastlingLost = false;
        boolean longCastlingLost = false;
        if(color == WHITE){
            if(canWhiteShortCastling)
                shortCastlingLost = true;
            if(canWhiteLongCastling)
                longCastlingLost = true;
        }else{
            if(canBlackShortCastling)
                shortCastlingLost = true;
            if(canBlackLongCastling)
                longCastlingLost = true;
        }

        //обычные ходы
        handleKingMove(x,y,x,y+1,color,enemyClr, shortCastlingLost, longCastlingLost, result);
        handleKingMove(x,y,x,y-1,color,enemyClr,shortCastlingLost, longCastlingLost,result);
        handleKingMove(x,y,x-1,y,color,enemyClr,shortCastlingLost, longCastlingLost,result);
        handleKingMove(x,y,x+1,y,color,enemyClr,shortCastlingLost, longCastlingLost,result);
        handleKingMove(x,y,x-1,y-1,color,enemyClr,shortCastlingLost, longCastlingLost,result);
        handleKingMove(x,y,x+1,y-1,color,enemyClr,shortCastlingLost, longCastlingLost,result);
        handleKingMove(x,y,x-1,y+1,color,enemyClr,shortCastlingLost, longCastlingLost,result);
        handleKingMove(x,y,x+1,y+1,color,enemyClr,shortCastlingLost, longCastlingLost,result);

        //рокировка
        //нельзя рокироваться если под ударом король или поля, через которые он пройдет и на которое встанет
        //также если между королем и ладьей стоит фигура
        if(color == WHITE){
            if(canWhiteShortCastling && !isPieceInField(x+1,y) && !isPieceInField(x+2,y)
                && !isAttacked(new Position(x+1,y), enemyClr) && !isAttacked(new Position(x+2,y),enemyClr)
                && !isAttacked(whiteKingPosition, enemyClr)){
                Move mv = new Move(x,y,x+2,y);
                //м.б. ситуция что оба параметра false и рокировка совершена. Так и должно быть для корректной
                //отмены хода. Эта ситуация учитывается в make/cancelMove()
                mv.longCastlingLost = longCastlingLost;
                mv.shortCastlingLost = shortCastlingLost;
                result.add(mv);
            }
            if(canWhiteLongCastling && !isPieceInField(x-1,y) && !isPieceInField(x-2,y)
                && !isPieceInField(x-3,y) && !isAttacked(new Position(x-1,y),enemyClr)
                && !isAttacked(new Position(x-2,y),enemyClr) && !isAttacked(whiteKingPosition, enemyClr)){
                Move mv = new Move(x,y,x-2,y);
                mv.longCastlingLost = longCastlingLost;
                mv.shortCastlingLost = shortCastlingLost;
                result.add(mv);
            }
        }else{
            //color == BLACK
            if(canBlackShortCastling && !isPieceInField(x+1,y) && !isPieceInField(x+2,y)
                    && !isAttacked(new Position(x+1,y), enemyClr) && !isAttacked(new Position(x+2,y),enemyClr)
                    && !isAttacked(blackKingPosition, enemyClr)){
                Move mv = new Move(x,y,x+2,y);
                mv.longCastlingLost = longCastlingLost;
                mv.shortCastlingLost = shortCastlingLost;
                result.add(mv);
            }
            if(canBlackLongCastling && !isPieceInField(x-1,y) && !isPieceInField(x-2,y)
                    && !isPieceInField(x-3,y) && !isAttacked(new Position(x-1,y),enemyClr)
                    && !isAttacked(new Position(x-2,y),enemyClr) && !isAttacked(blackKingPosition, enemyClr)){
                Move mv = new Move(x,y,x-2,y);
                mv.longCastlingLost = longCastlingLost;
                mv.shortCastlingLost = shortCastlingLost;
                result.add(mv);
            }
        }
    }

    static ArrayList<Move> getAllMoves(int color) {
        ArrayList<Move> result = new ArrayList<>();
        for(int y = 0; y < 8; y++){
            for(int x = 0; x < 8; x++){
                if(board[x][y] != BLANK && colors[x][y] == color){
                    switch (board[x][y]){
                        case PAWN: getAllMovesWithPawn(x,y,color,result); break;
                        case ROCK: getAllMovesWithRock(x,y,color, result); break;
                        case KNIGHT: getAllMovesWithKnight(x,y,color, result); break;
                        case BISHOP: getAllMovesWithBishop(x,y,color,result); break;
                        case QUEEN: getAllMovesWithQueen(x,y,color,result); break;
                        case KING: getAllMovesWithKing(x,y,color,result); break;
                    }
                }
            }
        }
        return result;
    }

    //находится ли определенная фигура нужного цвета на позиции
    static boolean isPieceInField(int color, int piece, int x, int y){
        if(x < 0 || x > 7 || y < 0 || y > 7)
            return false;
        if(board[x][y] == piece && colors[x][y] == color)
            return true;
        return false;
    }
    //находится ли любая фигура нужного цвета на позиции
    static boolean isPieceInField(int color, int x,int y){
        if(x < 0 || x > 7 || y < 0 || y > 7)
            return false;
        if(board[x][y] != BLANK && colors[x][y] == color)
            return true;
        return false;
    }
    //находится ли любая фигура на позиции
    static boolean isPieceInField(int x,int y){
        if(x < 0 || x > 7 || y < 0 || y > 7)
            return false;
        if(board[x][y] != BLANK)
            return true;
        return false;
    }
    static boolean isAttacked(Position pos, int enemyClr){
        int friendClr;
        if(enemyClr == BLACK)
            friendClr = WHITE;
        else
            friendClr = BLACK;
        int x = pos.x;
        int y = pos.y;
        if(enemyClr == BLACK){
            //пешки
            if (isPieceInField(BLACK, PAWN, x-1, y+1)) return true;
            if ( isPieceInField(BLACK, PAWN, x + 1, y + 1)) return true;
            //кони сверху
            if ( isPieceInField(BLACK, KNIGHT, x-1, y+2)) return true;
            if ( isPieceInField(BLACK, KNIGHT, x+1, y+2)) return true;
            //кони снизу
            if ( isPieceInField(BLACK, KNIGHT, x-1, y-2)) return true;
            if ( isPieceInField(BLACK, KNIGHT, x+1, y-2)) return true;
            //кони слева
            if ( isPieceInField(BLACK, KNIGHT, x-2, y+1)) return true;
            if ( isPieceInField(BLACK, KNIGHT, x-2, y-1)) return true;
            //кони справа
            if ( isPieceInField(BLACK, KNIGHT, x+2, y+1)) return true;
            if ( isPieceInField(BLACK, KNIGHT, x+2, y-1)) return true;
            //король
            if ( isPieceInField(BLACK, KING, x+1, y)) return true;
            if ( isPieceInField(BLACK, KING, x-1, y)) return true;
            if ( isPieceInField(BLACK, KING, x, y+1)) return true;
            if ( isPieceInField(BLACK, KING, x, y-1)) return true;
            if ( isPieceInField(BLACK, KING, x-1, y+1)) return true;
            if ( isPieceInField(BLACK, KING, x-1, y-1)) return true;
            if ( isPieceInField(BLACK, KING, x+1, y+1)) return true;
            if ( isPieceInField(BLACK, KING, x+1, y-1)) return true;
            //проверка 4-х диагоналей на слона/ферзя
            //влево вверх
            int xx =x-1;
            int yy =y+1;
            while(xx >= 0 && yy < 8){
                if(board[xx][yy] != BLANK && colors[xx][yy] == friendClr)
                    break;
                if(isPieceInField(BLACK, BISHOP,xx,yy)) return true;
                if(isPieceInField(BLACK, QUEEN,xx,yy)) return true;
                xx--;
                yy++;
            }
            //вправо вверх
            xx = x+1;
            yy = y+1;
            while(xx < 8 && yy < 8){
                if(board[xx][yy] != BLANK && colors[xx][yy] == friendClr)
                    break;
                if(isPieceInField(BLACK, BISHOP,xx,yy)) return true;
                if(isPieceInField(BLACK, QUEEN,xx,yy)) return true;
                xx++;
                yy++;
            }
            //влево вниз
            xx =x-1;
            yy =y-1;
            while ((yy>=0 && xx>=0)){
                if(board[xx][yy] != BLANK && colors[xx][yy] == friendClr)
                    break;;
                if(isPieceInField(BLACK, BISHOP,xx,yy)) return true;
                if(isPieceInField(BLACK, QUEEN,xx,yy)) return true;
                xx--;
                yy--;
            }
            //вправо вниз
            xx =x+1;
            yy =y-1;
            while ((yy>=0 && xx < 8)){
                if(board[xx][yy] != BLANK && colors[xx][yy] == friendClr)
                    break;;
                if(isPieceInField(BLACK, BISHOP,xx,yy)) return true;
                if(isPieceInField(BLACK, QUEEN,xx,yy)) return true;
                xx++;
                yy--;
            }
            //проверка вертикалей и горизонталей на ладью/ферзя
            //вверх
            yy =y+1;
            while(yy < 8){
                if(board[x][yy] != BLANK && colors[x][yy] == friendClr)
                    break;;
                if(isPieceInField(BLACK, QUEEN, x, yy)) return true;
                if(isPieceInField(BLACK, ROCK, x, yy)) return true;
                yy++;
            }
            //вниз
            yy =y-1;
            while(yy >= 0){
                if(board[x][yy] != BLANK && colors[x][yy] == friendClr)
                    break;;
                if(isPieceInField(BLACK, QUEEN, x, yy)) return true;
                if(isPieceInField(BLACK, ROCK, x, yy)) return true;
                yy--;
            }
            //влево
            xx = x-1;
            while(xx >= 0){
                if(board[xx][y] != BLANK && colors[xx][y] == friendClr)
                    break;;
                if(isPieceInField(BLACK, QUEEN, xx, y)) return true;
                if(isPieceInField(BLACK, ROCK, xx, y)) return true;
                xx--;
            }
            //вправо
            xx = x+1;
            while(xx <= 7){
                if(board[xx][y] != BLANK && colors[xx][y] == friendClr)
                    break;;
                if(isPieceInField(BLACK, QUEEN, xx, y)) return true;
                if(isPieceInField(BLACK, ROCK, xx, y)) return true;
                xx++;
            }
            return false;

        }else{
            //пешки
            if (isPieceInField(WHITE, PAWN, x-1, y-1)) return true;
            if ( isPieceInField(WHITE, PAWN, x + 1, y - 1)) return true;
            //кони сверху
            if ( isPieceInField(WHITE, KNIGHT, x-1, y+2)) return true;
            if ( isPieceInField(WHITE, KNIGHT, x+1, y+2)) return true;
            //кони снизу
            if ( isPieceInField(WHITE, KNIGHT, x-1, y-2)) return true;
            if ( isPieceInField(WHITE, KNIGHT, x+1, y-2)) return true;
            //кони слева
            if ( isPieceInField(WHITE, KNIGHT, x-2, y+1)) return true;
            if ( isPieceInField(WHITE, KNIGHT, x-2, y-1)) return true;
            //кони справа
            if ( isPieceInField(WHITE, KNIGHT, x+2, y+1)) return true;
            if ( isPieceInField(WHITE, KNIGHT, x+2, y-1)) return true;
            //король
            if ( isPieceInField(WHITE, KING, x+1, y)) return true;
            if ( isPieceInField(WHITE, KING, x-1, y)) return true;
            if ( isPieceInField(WHITE, KING, x, y+1)) return true;
            if ( isPieceInField(WHITE, KING, x, y-1)) return true;
            if ( isPieceInField(WHITE, KING, x-1, y+1)) return true;
            if ( isPieceInField(WHITE, KING, x-1, y-1)) return true;
            if ( isPieceInField(WHITE, KING, x+1, y+1)) return true;
            if ( isPieceInField(WHITE, KING, x+1, y-1)) return true;
            //проверка 4-х диагоналей на слона/ферзя
            //влево вверх
            int xx =x-1;
            int yy =y+1;
            while(xx >= 0 && yy < 8){
                if(board[xx][yy] != BLANK && colors[xx][yy] == friendClr)
                    break;
                if(isPieceInField(WHITE, BISHOP,xx,yy)) return true;
                if(isPieceInField(WHITE, QUEEN,xx,yy)) return true;
                xx--;
                yy++;
            }
            //вправо вверх
            xx = x+1;
            yy = y+1;
            while(xx < 8 && yy < 8){
                if(board[xx][yy] != BLANK && colors[xx][yy] == friendClr)
                    break;
                if(isPieceInField(WHITE, BISHOP,xx,yy)) return true;
                if(isPieceInField(WHITE, QUEEN,xx,yy)) return true;
                xx++;
                yy++;
            }
            //влево вниз
            xx =x-1;
            yy =y-1;
            while ((yy>=0 && xx>=0)){
                if(board[xx][yy] != BLANK && colors[xx][yy] == friendClr)
                    break;
                if(isPieceInField(WHITE, BISHOP,xx,yy)) return true;
                if(isPieceInField(WHITE, QUEEN,xx,yy)) return true;
                xx--;
                yy--;
            }
            //вправо вниз
            xx =x+1;
            yy =y-1;
            while ((yy>=0 && xx < 8)){
                if(board[xx][yy] != BLANK && colors[xx][yy] == friendClr)
                    break;
                if(isPieceInField(WHITE, BISHOP,xx,yy)) return true;
                if(isPieceInField(WHITE, QUEEN,xx,yy)) return true;
                xx++;
                yy--;
            }
            //проверка вертикалей и горизонталей на ладью/ферзя
            //вверх
            yy =y+1;
            while(yy < 8){
                if(board[x][yy] != BLANK && colors[x][yy] == friendClr)
                    break;
                if(isPieceInField(WHITE, QUEEN, x, yy)) return true;
                if(isPieceInField(WHITE, ROCK, x, yy)) return true;
                yy++;
            }
            //вниз
            yy =y-1;
            while(yy >= 0){
                if(board[x][yy] != BLANK && colors[x][yy] == friendClr)
                    break;
                if(isPieceInField(WHITE, QUEEN, x, yy)) return true;
                if(isPieceInField(WHITE, ROCK, x, yy)) return true;
                yy--;
            }
            //влево
            xx = x-1;
            while(xx >= 0){
                if(board[xx][y] != BLANK && colors[xx][y] == friendClr)
                    break;
                if(isPieceInField(WHITE, QUEEN, xx, y)) return true;
                if(isPieceInField(WHITE, ROCK, xx, y)) return true;
                xx--;
            }
            //вправо
            xx = x+1;
            while(xx <= 7){
                if(board[xx][y] != BLANK && colors[xx][y] == friendClr)
                    break;
                if(isPieceInField(WHITE, QUEEN, xx, y)) return true;
                if(isPieceInField(WHITE, ROCK, xx, y)) return true;
                xx++;
            }
            return false;
        }
    }

    //проверка на шах своему королю производися отдельно через isAttacked(kingPos)
    static void makeMove(Move move) {
        globalMoves.push(move);
        if(!move.takingIsDone && board[move.start.x][move.start.y] != PAWN)
            movesWithoutMistakes++;
        else{
            move.localMovesWhithoutMistakes = movesWithoutMistakes;
            movesWithoutMistakes = 0;
        }
        int color = colors[move.start.x][move.start.y];
        nextMoveNumber++;
        nextMoveClr = getEnemyColor(color);
        //взятие на проходе
        if(move.doneEnPassant) {
            canEnPassant = false;
            enPassantPos = null;
            board[move.end.x][move.end.y] = PAWN;
            colors[move.end.x][move.end.y] = colors[move.start.x][move.start.y];
            board[move.start.x][move.start.y] = BLANK;
            board[move.end.x][move.start.y] = BLANK;
            return;
        }
        //превращение пешки (возможно со взятием)
        if(move.pawnTransform){
            board[move.end.x][move.end.y] = move.pawnTransformPiece;
            colors[move.end.x][move.end.y] = colors[move.start.x][move.start.y];
            board[move.start.x][move.start.y] = BLANK;
            return;
        }
        //рокировка
        if(board[move.start.x][move.start.y] == KING && Math.abs(move.start.x - move.end.x) == 2){
            if(color == WHITE){
                canWhiteShortCastling = false;
                canWhiteLongCastling = false;
                whiteKingPosition = new Position(move.end.x,move.end.y);
            }else{
                canBlackShortCastling = false;
                canBlackLongCastling = false;
                blackKingPosition = new Position(move.end.x,move.end.y);
            }
            board[move.end.x][move.end.y] = KING;
            colors[move.end.x][move.end.y] = color;
            board[move.start.x][move.start.y] = BLANK;
            if(move.end.x == 6){
                //короткая рокировка
                board[move.end.x-1][move.end.y] = ROCK;
                colors[move.end.x -1][move.end.y] = color;
                board[7][move.end.y] = BLANK;
            }else{
                //длинная рокировка
                board[move.end.x + 1][move.end.y] = ROCK;
                colors[move.end.x+1][move.end.y] = color;
                board[0][move.end.y] = BLANK;
            }
            return;
        }
        //взятие и простые ходы
        if(board[move.start.x][move.start.y] == KING){
            if(color == WHITE){
                canWhiteLongCastling = false;
                canWhiteShortCastling = false;
                whiteKingPosition = new Position(move.end.x,move.end.y);
            }else{
                canBlackShortCastling = false;
                canBlackLongCastling = false;
                blackKingPosition = new Position(move.end.x,move.end.y);
            }
        }
        board[move.end.x][move.end.y] = board[move.start.x][move.start.y];
        colors[move.end.x][move.end.y] = color;
        board[move.start.x][move.start.y] = BLANK;
    }

    static void cancelMove() {
        Move move = globalMoves.pop();
        if(!move.takingIsDone && board[move.end.x][move.end.y] != PAWN)
            movesWithoutMistakes--;
        else{
            movesWithoutMistakes = move.localMovesWhithoutMistakes;

        }
        int color = colors[move.end.x][move.end.y];
        int enemyClr = getEnemyColor(color);
        nextMoveNumber--;
        nextMoveClr = color;
        //взятие на проходе
        if(move.doneEnPassant){
            //восстанавливаем вражескую пешку
            board[move.end.x][move.start.y] = PAWN;
            colors[move.end.x][move.start.y] = enemyClr;
            //возвращаем свою на место
            board[move.start.x][move.start.y] = PAWN;
            colors[move.start.x][move.start.y] = color;
            board[move.end.x][move.end.y] = BLANK;
            canEnPassant = true;
            enPassantPos = new Position(move.end.x, move.end.y);
            return;
        }
        //превращение пешки (может быть со взятием)
        if(move.pawnTransform){
            board[move.start.x][move.start.y] = PAWN;
            colors[move.start.x][move.start.y] = color;
            if(move.takingIsDone){
                board[move.end.x][move.end.y] = move.capturedPiece;
                colors[move.end.x][move.end.y] = enemyClr;
            }else{
                board[move.end.x][move.end.y] = BLANK;

            }
            return;
        }
        //рокировка
        if(board[move.end.x][move.end.y] == KING && Math.abs(move.end.x - move.start.x) == 2){
            if(move.shortCastlingLost){
                if(color == WHITE){
                    canWhiteShortCastling = true;
                    whiteKingPosition = new Position(move.start.x,move.start.y);
                }else{
                    canBlackShortCastling = true;
                    blackKingPosition = new Position(move.start.x,move.start.y);
                }
            }
            if(move.longCastlingLost){
                if(color == WHITE){
                    canWhiteLongCastling = true;
                }else {
                    canBlackLongCastling = true;
                }
            }
            board[move.start.x][move.start.y] = KING;
            colors[move.start.x][move.start.y] = color;
            board[move.end.x][move.end.y] = BLANK;
            if(move.end.x == 6){
                //короткая рокировка
                board[move.end.x-1][move.end.y] = BLANK;
                board[7][move.end.y] = ROCK;
                colors[7][move.end.y] = color;
            }else{
                //длинная рокировка
                board[move.end.x+1][move.end.y] = BLANK;
                board[0][move.end.y] = ROCK;
                colors[0][move.end.y] = color;
            }
            return;
        }
        //взятие фигуры и простые ходы
        if(board[move.end.x][move.end.y] == KING){
            if(color == WHITE)
                whiteKingPosition = new Position(move.start.x,move.start.y);
            else
                blackKingPosition = new Position(move.start.x,move.start.y);
        }
        board[move.start.x][move.start.y] = board[move.end.x][move.end.y];
        colors[move.start.x][move.start.y] = color;
        if(move.takingIsDone){
            board[move.end.x][move.end.y] = move.capturedPiece;
            colors[move.end.x][move.end.y] = enemyClr;
        }else{
            board[move.end.x][move.end.y] = BLANK;
        }
    }

    //вариант с полным перебором (вместо альфа-бета)

    static Move getBestMove(int color, int depth){
        calculatedPositionsCount++;
        if (depth >= maxDepth){
            return new Move(evaluate(color));
        }
        ArrayList<Move> moves = getAllMoves(color);

        Move bestMove = new Move(-ChessEngine.INFINITY);
        Move tmpMove;
        if(moves.size() == 0){
            if(isCheckmate(getEnemyColor(color)))
                return new Move(INFINITY);
            else if(isCheckmate(color))
                return new Move(-INFINITY);
            else
                return  new Move(0);
        }
        for(Move move:moves){
            makeMove(move);
            tmpMove = getBestMove(getEnemyColor(color), depth+1);
            tmpMove.evaluate = -tmpMove.evaluate;
            cancelMove();
            if(tmpMove.evaluate > bestMove.evaluate){
                bestMove = move;
                bestMove.evaluate = tmpMove.evaluate;
            }
        }
        return bestMove;
    }

    static int alphaBeta(int alpha, int beta, int depth, int color) {
        //считаем количество просчитанных позиций
        calculatedPositionsCount++;
        if (depth >= maxDepth)
            return evaluate(color);
        //получаем все ходы
        ArrayList<Move> moves = getAllMoves(color);

        for (Move move : moves) {
            makeMove(move);
            int tmp = -alphaBeta(-beta, -alpha, depth + 1, getEnemyColor(color));
            cancelMove();
            if (tmp > alpha) alpha = tmp;
            bestMove = move;
            if (alpha >= beta) return beta; //отсечение
        }
        //если пришли сюда, отсечения не было
        return alpha;
    }


    static void setPositionFromFen(String fen) {

        //rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
        canWhiteShortCastling = false;
        canWhiteLongCastling = false;
        canBlackShortCastling = false;
        canBlackLongCastling = false;
        canEnPassant = false;
        globalMoves = new Stack<>();
        for(int i = 0; i < 8; i++){
            for(int k = 0; k < 8; k++){
                board[i][k] = BLANK;
            }
        }
        char[] chars = fen.toCharArray();
        int x = 0;
        int y = 7;
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case 'r':
                    board[x][y] = ROCK;
                    colors[x][y] = BLACK;
                    x++;
                    break;
                case 'n':
                    board[x][y] = KNIGHT;
                    colors[x][y] = BLACK;
                    x++;
                    break;
                case 'b':
                    board[x][y] = BISHOP;
                    colors[x][y] = BLACK;
                    x++;
                    break;
                case 'q':
                    board[x][y] = QUEEN;
                    colors[x][y] = BLACK;
                    x++;
                    break;
                case 'k':
                    board[x][y] = KING;
                    colors[x][y] = BLACK;
                    blackKingPosition = new Position(x,y);
                    x++;
                    break;
                case 'p':
                    board[x][y] = PAWN;
                    colors[x][y] = BLACK;
                    x++;
                    break;
                case 'R':
                    board[x][y] = ROCK;
                    colors[x][y] = WHITE;
                    x++;
                    break;
                case 'N':
                    board[x][y] = KNIGHT;
                    colors[x][y] = WHITE;
                    x++;
                    break;
                case 'B':
                    board[x][y] = BISHOP;
                    colors[x][y] = WHITE;
                    x++;
                    break;
                case 'Q':
                    board[x][y] = QUEEN;
                    colors[x][y] = WHITE;
                    x++;
                    break;
                case 'K':
                    board[x][y] = KING;
                    colors[x][y] = WHITE;
                    whiteKingPosition = new Position(x,y);
                    x++;
                    break;
                case 'P':
                    board[x][y] = PAWN;
                    colors[x][y] = WHITE;
                    x++;
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                    x += (chars[i] - 48);
                    break;
                case '/':
                    x = 0;
                    y--;
                    break;
                case ' ':
                    i++;
                    if (chars[i] == 'w')
                        nextMoveClr = WHITE;
                    else if (chars[i] == 'b')
                        nextMoveClr = BLACK;
                    i++;
                    if (chars[i] == ' ')
                        i++;
                    while (chars[i] != ' ') {
                        switch (chars[i]) {
                            case 'K':
                                canWhiteShortCastling = true;
                                break;
                            case 'Q':
                                canWhiteLongCastling = true;
                                break;
                            case 'k':
                                canBlackShortCastling = true;
                                break;
                            case 'q':
                                canBlackLongCastling = true;
                                break;
                        }
                        i++;
                    }
                    i++;
                    if (chars[i] == '-') {
                        canEnPassant = false;
                        i++;
                    } else {
                        int enPassantX = -1;
                        int enPassantY = -1;
                        switch (chars[i]) {
                            case 'a':
                                enPassantX = 0;
                                break;
                            case 'b':
                                enPassantX = 1;
                                break;
                            case 'c':
                                enPassantX = 2;
                                break;
                            case 'd':
                                enPassantX = 3;
                                break;
                            case 'e':
                                enPassantX = 4;
                                break;
                            case 'f':
                                enPassantX = 5;
                                break;
                            case 'g':
                                enPassantX = 6;
                                break;
                            case 'h':
                                enPassantX = 7;
                                break;
                        }
                        i++;
                        switch (chars[i]) {
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                                enPassantY = (chars[i] - 49);
                                break;
                        }
                        i++;
                        enPassantPos = new Position(enPassantX, enPassantY);
                        canEnPassant = true;
                    }
                    if (chars[i] == ' ')
                        i++;
                    String num = "";
                    while (chars[i] != ' ') {
                        num += chars[i];
                        i++;
                    }
                    movesWithoutMistakes = Integer.parseInt(num);
                    num = "";
                    i++;
                    while (i < chars.length) {
                        num += chars[i];
                        i++;
                    }
                    nextMoveNumber = Integer.parseInt(num);

            }
        }
    }

    static void printPosition() {
        for (int y = 7; y >= 0; y--) {
            System.out.print(y+1 + "\t");
            for (int x = 0; x < 8; x++) {
                switch (board[x][y]) {
                    case BLANK:
                        System.out.print(' ');
                        break;
                    case ROCK:
                        if (colors[x][y] == WHITE)
                            System.out.print('R');
                        else
                            System.out.print('r');
                        break;
                    case KNIGHT:
                        if (colors[x][y] == WHITE)
                            System.out.print('N');
                        else
                            System.out.print('n');
                        break;
                    case BISHOP:
                        if (colors[x][y] == WHITE)
                            System.out.print('B');
                        else
                            System.out.print('b');
                        break;
                    case QUEEN:
                        if (colors[x][y] == WHITE)
                            System.out.print('Q');
                        else
                            System.out.print('q');
                        break;
                    case KING:
                        if (colors[x][y] == WHITE)
                            System.out.print('K');
                        else
                            System.out.print('k');
                        break;
                    case PAWN:
                        if (colors[x][y] == WHITE)
                            System.out.print('P');
                        else
                            System.out.print('p');
                        break;
                }
                if (x != 7)
                    System.out.print("|");

            }
            System.out.println();
            //System.out.println("--------");
        }
        System.out.println("\n\ta b c d e f g h");
    }

    static void printPositionInfo(){
        System.out.println("Рокировки за черных:");
        System.out.println("\tкороткая: " + canBlackShortCastling);
        System.out.println("\tдлинная: " + canBlackLongCastling);
        System.out.println("Рокировки за белых:");
        System.out.println("\tкороткая: " + canWhiteShortCastling);
        System.out.println("\tдлинная: " + canWhiteLongCastling);
        System.out.println("Возможно ли взятие на проходе: " + canEnPassant );
        if(canEnPassant){
            System.out.println("\t"+enPassantPos);
        }
        System.out.println("следующий ход будет иметь номер: " + nextMoveNumber);
        if(nextMoveClr == BLACK)
            System.out.println("ходят черные");
        else
            System.out.println("ходят белые");
        System.out.println("Ходов без взятия и движений пешки: " + movesWithoutMistakes);
        System.out.print("белый король: " + whiteKingPosition);
        if(isAttacked(whiteKingPosition, BLACK)) System.out.println(" (под шахом)");
        else System.out.println();
        System.out.print("черный король: " + blackKingPosition);
        if(isAttacked(blackKingPosition, WHITE)) System.out.println(" (под шахом)");
        else System.out.println();
    }

    static void makeMoveFromUserInput(String input){
        char[] chars = input.toCharArray();
        Position start = new Position(chars[0]-97, chars[1]-49);
        Position end = new Position(chars[2]-97, chars[3]-49);
        Move userMove = new Move(start, end);

        //трансформация пешки
        if(board[start.x][start.y] == PAWN && (end.y == 0 || end.y==7)){
            userMove.pawnTransform = true;
            userMove.pawnTransformPiece = QUEEN;
        }
        //взятие на проходе
        if(board[start.x][start.y] == PAWN && start.x != end.x){
            userMove.doneEnPassant = true;
        }
        //взятие
        if(board[end.x][end.y] != BLANK){
            userMove.takingIsDone = true;
            userMove.capturedPiece = board[end.x][end.y];
        }
        makeMove(userMove);
    }

}
