package jscrabble;

import jscrabble.util.ArrayList;
import jscrabble.util.Comparator;


public class Evaluator {
    public static final int NOVICE        = 1;
    public static final int BASIC         = 2;
    public static final int INTERMEDIATE  = 3;
    public static final int ADVANCED      = 4;
    public static final int EXPERT        = 5;
    
    public static Evaluator getInstance(int level) {
        switch(level) {
            case NOVICE:
                return noviceEvaluator;
            case BASIC:
                return basicEvaluator;
            case INTERMEDIATE:
                return intermediateEvaluator;
            case ADVANCED:
                return advancedEvaluator;
            case EXPERT:
                return expertEvaluator;
            default:
                throw new IllegalArgumentException("Illegal EVALUATOR id: "+level);
        }
    }
    
    private static Square[][] squares = ScrabbleBoard.getSquares();
    
    int wordWorth;
    int adjoinWorth;
    int counter;
    int leftPieces;
    
    public void setLeftPieces(int n) {
        this.leftPieces = n;
    }
    
    public static final Evaluator noviceEvaluator = new Evaluator() {
        
        public int evaluate(PlayerMove move, ScrabblePiece[][] board, int i, int j) {
            return super.evaluate(move, board, i, j);
        }
        
        public PlayerMove getBestMove(ArrayList solutions) {
            solutions.sort(RANK_DESCENDING);
            return (PlayerMove) solutions.get(2*solutions.size()/3);
        }
        
    };
    
    public static final Evaluator basicEvaluator = new Evaluator() {
        
        public int evaluate(PlayerMove move, ScrabblePiece[][] board, int i, int j) {
            super.evaluate(move, board, i, j);
            return adjoinWorth;
        }
        
    };
    
    public static final Evaluator intermediateEvaluator = new Evaluator() {
        
        public int evaluate(PlayerMove move, ScrabblePiece[][] board, int i, int j) {
            super.evaluate(move, board, i, j);
            return (counter != 7? wordWorth : wordWorth-50)/(7+counter);
        }
        
    };
    
    public static final Evaluator advancedEvaluator = new Evaluator() {
        
        public int evaluate(PlayerMove move, ScrabblePiece[][] board, int i, int j) {
            super.evaluate(move, board, i, j);
            return (counter != 7? wordWorth : wordWorth-50)*200/(100+adjoinWorth);
        }
        
    };
    
    public static final Evaluator expertEvaluator = new Evaluator();
    
    public int evaluate(PlayerMove move, ScrabblePiece[][] board, int i, int j) {
        counter = 0;
        wordWorth = 0;
        adjoinWorth = 0;
        int wordPremium = 1, crossWorth = 0;
        int adjoinPremium = 1;
        int mini = i, maxi = i;
        ScrabblePiece p, p2;
        for(i=mini; i > 0 && board[--i][j] != null; )
            mini = i;
        for(i=maxi; i < 14 && board[++i][j] != null; )
            maxi = i;
        for(i=mini; i <= maxi; i++) {
            if((p = board[i][j]).getColor() == ScrabblePiece.ORANGE) {
                counter++;
                wordPremium *= squares[i][j].getWordMultiplier();
                wordWorth += squares[i][j].getLetterMultiplier() * p.value;
                int crossWordWorth = 0, adjoinCrossWorth = 0;
                for(int j2 = j; j2 > 0 && (p2 = board[i][--j2]) != null; )
                    crossWordWorth += p2.value;
                for(int j2 = j; j2 < 14 && (p2 = board[i][++j2]) != null; )
                    crossWordWorth += p2.value;
                adjoinPremium = 1;
                for(int j2 = j, fieldWorth = 5; j2 > 0 && fieldWorth > 0 && board[i][--j2] == null; fieldWorth--) {
                    adjoinPremium *= squares[i][j2].getWordMultiplier();
                    adjoinCrossWorth += squares[i][j2].getLetterMultiplier() * fieldWorth;
                }
                if(adjoinCrossWorth > 0)
                    adjoinWorth += adjoinCrossWorth * adjoinPremium;
                adjoinCrossWorth = 0; adjoinPremium = 1;
                for(int j2 = j, fieldWorth = 5; j2 < 14 && fieldWorth > 0 && board[i][++j2] == null; fieldWorth--) {
                    adjoinPremium *= squares[i][j2].getWordMultiplier();
                    adjoinCrossWorth += squares[i][j2].getLetterMultiplier() * fieldWorth;
                }
                if(adjoinCrossWorth > 0)
                    adjoinWorth += adjoinCrossWorth * adjoinPremium;
                if(crossWordWorth > 0)
                    crossWorth += (crossWordWorth + squares[i][j].getLetterMultiplier() * p.value) * squares[i][j].getWordMultiplier();
            }
            else
                wordWorth += p.value;
        }
        if(mini == maxi)
            wordWorth = 0;
        wordWorth = wordWorth * wordPremium + crossWorth + ((counter < 7)? 0 : 50);
        move.setScore(wordWorth);
        return wordWorth;
    }
    
    public PlayerMove getBestMove(ArrayList moves) {
        moves.sort(RANK_DESCENDING);
        return (PlayerMove) moves.get(0);
    }
    
    public static final Comparator RANK_DESCENDING = new Comparator() {
        public int compare(Object o1, Object o2) {
            return ((PlayerMove) o2).rank - ((PlayerMove) o1).rank;
        }
    };
}
