package jscrabble;

import java.io.InputStream;
import jscrabble.util.ArrayList;

public class Engine {

    private ScrabblePiece[][] copyboard = new ScrabblePiece[Settings.BOARD_SIZE][Settings.BOARD_SIZE];
    private boolean[][] neighbourhood = new boolean[Settings.BOARD_SIZE][Settings.BOARD_SIZE];
    private boolean mirror;
    private StringBuffer cachedWord = new StringBuffer(15);
    private Dictionary dictionary = new Dictionary();
    public Lexicon lexicon = new Lexicon(dictionary);
    private ArrayList moves = new ArrayList();
    private Evaluator evaluator = Evaluator.basicEvaluator;
    private int timeUsed;

    public void loadDictionary(InputStream in) {
        dictionary.readFromStream(in);
    }

    public ArrayList match(String pattern) {
        return dictionary.match(pattern);
    }
    
    public boolean checkSpelling(ScrabbleBoard board) {
        ScrabblePiece piece;
        ScrabblePiece[][] pieces = board.pieces;
        int mini = 14, maxi = 0, minj = 14, maxj = 0;
        String foundWord = null;
        
        for(int j = 0; j < 15; j++)
            for(int i = 0; i < 15; i++)
                if((piece = pieces[i][j]) != null && piece.getColor() != ScrabblePiece.YELLOW) {
                    if(i < mini)
                        mini = i;
                    if(i > maxi)
                        maxi = i;
                    if(j < minj)
                        minj = j;
                    if(j > maxj)
                        maxj = j;
                }
        
        if(minj == maxj) {
            if(!isCorrectHorizontally(pieces, mini, minj))
                return false;
            boolean horizontal = cachedWord.length() > 1;
            if(horizontal)
                foundWord = cachedWord.toString();
            for(int i = mini; i <= maxi; i++)
                if(pieces[i][minj].getColor() != ScrabblePiece.YELLOW && !isCorrectVertically(pieces, i, minj))
                    return false;
            if(horizontal)
                cachedWord = new StringBuffer(foundWord);
            
        } else if(mini == maxi) {
            if(!isCorrectVertically(pieces, mini, minj))
                return false;
            foundWord = cachedWord.toString();
            for(int j = minj; j <= maxj; j++)
                if(pieces[mini][j].getColor() != ScrabblePiece.YELLOW && !isCorrectHorizontally(pieces, mini, j))
                    return false;
            cachedWord = new StringBuffer(foundWord);
            
        } else
            return false;
        
        return true;
    }
    
    public String getCachedWord() {
        return cachedWord.toString();
    }
    
    private static boolean[][] createNeighbourhoodTable(
            ScrabblePiece[][] board, ScrabblePiece[][] copyboard, boolean[][] cache) {
        if(cache == null)
            cache = new boolean[Settings.BOARD_SIZE][Settings.BOARD_SIZE];
        boolean filled = false;
        for(int j = 0; j < Settings.BOARD_SIZE; j++)
            for(int i = 0; i < Settings.BOARD_SIZE; i++)
                filled |= cache[i][j] = (copyboard[i][j] = board[i][j]) == null
                    && ( ((i >  0)? board[i-1][j] != null : false) ||
                         ((i < 14)? board[i+1][j] != null : false) ||
                         ((j >  0)? board[i][j-1] != null : false) ||
                         ((j < 14)? board[i][j+1] != null : false)
                );
        
        if(!filled)
            cache[7][7] = true;
        return cache;
    }

    public ArrayList scrabble(ScrabblePiece[][] board, ScrabblePiece[] rack) {
        long startTime = System.currentTimeMillis();
        ScrabblePiece[][] copyboard = this.copyboard;
        boolean[][] neighbour = createNeighbourhoodTable(board, copyboard, neighbourhood);
        
        moves.clear();
        mirror = false;
        do {
            long timestamp = System.currentTimeMillis();
            for(int j=0; j<15; j++) {
                for(int i=0; i<15; i++)
                    if(neighbour[i][j])
                        prefix(copyboard, i, j, rack);
                if(System.currentTimeMillis() - timestamp > 1000)
                    break;
            }
            mirror = !mirror;
            transpose(copyboard);
            transpose(neighbour);
        } while(mirror);
        timeUsed = (int)(System.currentTimeMillis() - startTime);
        
        return moves;
    }
    
    public int getTimeUsed() {
        return timeUsed;
    }

    private static void transpose(ScrabblePiece[][] board) {
        for(int j = 0; j < 14; j++)
            for(int i = j+1; i < 15; i++) {
                ScrabblePiece swap = board[i][j];
                board[i][j] = board[j][i];
                board[j][i] = swap;
            }
    }
    
    private static void transpose(boolean[][] table) {
        for(int j = 0; j < 14; j++)
            for(int i = j+1; i < 15; i++) {
                boolean swap = table[i][j];
                table[i][j] = table[j][i];
                table[j][i] = swap;
            }
    }
    
    private void prefix(ScrabblePiece[][] board, int i, int j, ScrabblePiece[] rack) {
        while(board[i][j] != null)
            if(--i < 0)
                return;
        for(int pi = rack.length; pi > 0; ) {
            if(rack[--pi] != null) {
                ScrabblePiece p = rack[pi];
                board[i][j] = p;
                rack[pi] = null;
                
                if(p instanceof ScrabblePiece.BlankPiece) {
                    ScrabblePiece.BlankPiece blank = (ScrabblePiece.BlankPiece)p;
                    for(int bi = GameSystem.ALPHABET.length-1; bi > 0; bi--) {
                        blank.setIndex(bi);
                        if(isCorrectVertically(board, i, j)) {
                            if(isCorrectHorizontally(board, i, j))
                                acceptSolution(board, i, j);
                            if(dictionary.containsInfix(cachedWord))
                                prefix(board, i, j, rack);
                            if(containsPrefix(board, i, j))
                                suffix(board, i, j, rack);
                        }
                    }
                    blank.setIndex(0);
                } else {
                    if(isCorrectVertically(board, i, j)) {
                        if(isCorrectHorizontally(board, i, j))
                            acceptSolution(board, i, j);
                        if(dictionary.containsInfix(cachedWord))
                            prefix(board, i, j, rack);
                        if(containsPrefix(board, i, j))
                            suffix(board, i, j, rack);
                    }
                }
                
                board[i][j] = null;
                rack[pi] = p;
            }
        }
    }
    
    private void suffix(ScrabblePiece[][] board, int i, int j, ScrabblePiece[] rack) {
        while(board[i][j] != null)
            if(++i >= 15)
                return;
        for(int pi=rack.length; pi > 0; ) {
            if(rack[--pi] != null) {
                ScrabblePiece p = rack[pi];
                
                
                board[i][j] = p; rack[pi] = null;
                
                if(p instanceof ScrabblePiece.BlankPiece) {
                    ScrabblePiece.BlankPiece blank = (ScrabblePiece.BlankPiece)p;
                    for(int bi = GameSystem.ALPHABET.length-1; bi > 0; bi--) {
                        blank.setIndex(bi);
                        if(isCorrectVertically(board, i, j)) {
                            if(isCorrectHorizontally(board, i, j))
                                acceptSolution(board, i, j);
                            if(dictionary.containsPrefix(cachedWord))
                                suffix(board, i, j, rack);
                        }
                    }
                    blank.setIndex(0);
                } else {
                    if(isCorrectVertically(board, i, j)) {
                        if(isCorrectHorizontally(board, i, j))
                            acceptSolution(board, i, j);
                        if(dictionary.containsPrefix(cachedWord))
                            suffix(board, i, j, rack);
                    }
                }
                
                board[i][j] = null; rack[pi] = p;
            }
        }
    }
    
    public void setEvaluator(int id) {
        this.evaluator = Evaluator.getInstance(id);
    }
    
    public Evaluator getEvaluator() {
        return evaluator;
    }
    
    private void acceptSolution(ScrabblePiece[][] board, int i, int j) {
        PlayerMove move = new PlayerMove(board, i, j, mirror);
        move.rank = evaluator.evaluate(move, board, i, j);
        moves.add(move);
    }

    private boolean isCorrectVertically(ScrabblePiece[][] board, int i, int j) {
        while(j > 0 && board[i][j-1] != null)
            j--;
        StringBuffer sb = cachedWord;
        sb.setLength(0);
        ScrabblePiece p;
        while(j < 15 && (p = board[i][j++]) != null)
            sb.append(p.letter);
        if(sb.length() > 1)
            return dictionary.contains(sb);
        return sb.length() > 0;
    }

    private boolean isCorrectHorizontally(ScrabblePiece[][] board, int i, int j) {
        while(i > 0 && board[i-1][j] != null)
            i--;
        StringBuffer sb = cachedWord;
        sb.setLength(0);
        ScrabblePiece p;
        while(i < 15 && (p = board[i++][j]) != null)
            sb.append(p.letter);
        if(sb.length() > 1)
            return dictionary.contains(sb);
        return sb.length() > 0;
    }

    private boolean containsPrefix(ScrabblePiece[][] board, int i, int j) {
        while(i > 0 && board[i-1][j] != null)
            i--;
        StringBuffer sb = cachedWord;
        sb.setLength(0);
        ScrabblePiece p;
        while(i < 15 && (p = board[i++][j]) != null)
            sb.append(p.letter);
        if(sb.length() > 0)
            return dictionary.containsPrefix(sb);
        return false;
    }
}
