package jscrabble;

public class ScrabbleBoard implements java.io.Serializable {
    private static final long serialVersionUID = 2976616285474259062L;

    public final ScrabblePiece[][] pieces = new ScrabblePiece[15][15];
    
    private static Square[][] squares = new Square[15][15];
    static {
        String decode = "400100040001004030002000200030003000101000300100300010003001000030000030000020002000200020001000101000100400100030001004001000101000100020002000200020000030000030000100300010003001003000101000300030002000200030400100040001004";
        Square basic = new Square(1, 1);
        Square twiceLetter = new Square(1, 2);
        Square tripleLetter = new Square(1, 3);
        Square twiceWord = new Square(2, 1);
        Square tripleWord = new Square(3, 1);
        for(int i = 0; i < 15; i++)
            for(int j = 0; j < 15; j++) {
                switch(decode.charAt(i + 15*j)) {
                    case '0':
                        squares[i][j] = basic;
                        break;
                    case '1':
                        squares[i][j] = twiceLetter;
                        break;
                    case '2':
                        squares[i][j] = tripleLetter;
                        break;
                    case '3':
                        squares[i][j] = twiceWord;
                        break;
                    case '4':
                        squares[i][j] = tripleWord;
                        break;
                }
            }
        
    }
    
    static Square[][] getSquares() {
        return squares;
    }
    
    public final int getTurnScore() {
        ScrabblePiece[][] pieces = this.pieces;
        ScrabblePiece p, p2;
        int mini = 14, maxi = 0, minj = 14, maxj = 0;
        int counter = 0;
        
        for(int j = 0; j < 15; j++)
            for(int i = 0; i < 15; i++)
                if((p = pieces[i][j]) != null && p.getColor() != ScrabblePiece.YELLOW) {
                    counter++;
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
            for(int i = mini; i <= maxi; i++)
                if(pieces[i][minj] == null)
                    return -1;
        } else if(mini == maxi) {
            for(int j = minj; j < maxj; j++)
                if(pieces[mini][j] == null)
                    return -1;
        } else return -1;
        
        if((p = pieces[7][7]) == null)
            return -1;
        // plates[7][7] != null
        if(p.getColor() != ScrabblePiece.YELLOW) {
            if(counter == 1)
                return -1;
        }
        else
            block:
            {
                if(minj == maxj) {
                    if(containsPlateAt(mini-1, minj) || containsPlateAt(maxi+1, minj))
                        break block;
                    for(int i = mini; i <= maxi; i++)
                        if(containsPlateAt(i, minj-1) || containsPlateAt(i, minj+1))
                            break block;
                } else {
                    if(containsPlateAt(mini, minj-1) || containsPlateAt(mini, maxj+1))
                        break block;
                    for(int j = minj; j <= maxj; j++)
                        if(containsPlateAt(mini-1, j) || containsPlateAt(mini+1, j))
                            break block;
                }
                return -1;
            }
        
        int wordPremium = 1;
        int wordWorth = 0, crossWorth = 0;
        if(mini == maxi) {
            for(int j = minj; j > 0 && pieces[mini][--j] != null; )
                minj = j;
            for(int j = maxj; j < 14 && pieces[mini][++j] != null; )
                maxj = j;
            for(int j = minj; j <= maxj; j++) {
                if((p = pieces[mini][j]).getColor() != ScrabblePiece.YELLOW) {
                    wordPremium *= squares[mini][j].getWordMultiplier();
                    wordWorth += squares[mini][j].getLetterMultiplier() * p.value;
                    int crossWordWorth = 0;
                    boolean crossed = false;
                    for(int i = mini; i > 0 && (p2 = pieces[--i][j]) != null; crossed = true)
                        crossWordWorth += p2.value;
                    for(int i = mini; i < 14 && (p2 = pieces[++i][j]) != null; crossed = true)
                        crossWordWorth += p2.value;
                    if(crossed)
                        crossWorth += (crossWordWorth + squares[mini][j].getLetterMultiplier()*p.value) * squares[mini][j].getWordMultiplier();
                }
                else
                    wordWorth += p.value;
            }
        } else {
            for(int i = mini; i > 0 && pieces[--i][minj] != null; )
                mini = i;
            for(int i = maxi; i < 14 && pieces[++i][minj] != null; )
                maxi = i;
            for(int i = mini; i <= maxi; i++) {
                if((p = pieces[i][minj]).getColor() != ScrabblePiece.YELLOW) {
                    wordPremium *= squares[i][minj].getWordMultiplier();
                    wordWorth += squares[i][minj].getLetterMultiplier() * p.value;
                    int crossWordWorth = 0;
                    boolean crossed = false;
                    for(int j = minj; j > 0 && (p2 = pieces[i][--j]) != null; crossed = true)
                        crossWordWorth += p2.value;
                    for(int j = minj; j < 14 && (p2 = pieces[i][++j]) != null; crossed = true)
                        crossWordWorth += p2.value;
                    if(crossed)
                        crossWorth += (crossWordWorth + squares[i][minj].getLetterMultiplier() * p.value) * squares[i][minj].getWordMultiplier();
                }
                else
                    wordWorth += p.value;
            }
        }
        if(mini == maxi && minj == maxj)
            wordWorth = 0;
        
        wordWorth = wordWorth * wordPremium + crossWorth + ((counter < 7)? 0 : 50);
        return wordWorth;
    }
    
    private boolean containsPlateAt(int i, int j) {
        if(i < 0 || i > 14 || j < 0 || j > 14)
            return false;
        return pieces[i][j] != null;
    }

    public ScrabblePiece getPieceAt(int i, int j) {
        return pieces[i][j];
    }
    
    public void setPieceAt(int i, int j, ScrabblePiece piece) {
        if(pieces[i][j] == null)
            pieces[i][j] = piece;
        else if(pieces[i][j] != piece)
            throw new IllegalArgumentException("piece already placed at board");
    }
    
    public void removePiece(ScrabblePiece piece) {
        ScrabblePiece[][] pieces = this.pieces;
        if(piece == null)
            throw new NullPointerException("piece is NULL");
        for(int i = 0; i < 15; i++)
            for(int j = 0; j < 15; j++)
                if(pieces[i][j] == piece) {
                    pieces[i][j] = null;
                    return;
                }
        throw new IllegalArgumentException("piece not found");
    }
    
    public void collectAll(ScrabbleBag bag) {
        ScrabblePiece[][] pieces = this.pieces;
        for(int i = 0; i < 15; i++)
            for(int j = 0; j < 15; j++)
                if(pieces[i][j] != null) {
                    bag.returnPiece(pieces[i][j]);
                    pieces[i][j] = null;
                }
        
    }
    
    public void moveToRack(ScrabbleRack rack) {
        ScrabblePiece piece;
        ScrabblePiece[][] pieces = this.pieces;
        for(int i = 0; i < 15; i++)
            for(int j = 0; j < 15; j++)
                if((piece = pieces[i][j]) != null && piece.getColor() == ScrabblePiece.ORANGE) {
                    pieces[i][j] = null;
                    rack.addPiece(piece);
                }
        
    }
    
    public void replaceColor(int oldColor, int newColor) {
        ScrabblePiece piece;
        ScrabblePiece[][] pieces = this.pieces;
        for(int i = 0; i < 15; i++)
            for(int j = 0; j < 15; j++)
                if((piece = pieces[i][j]) != null && piece.getColor() == oldColor)
                    piece.setColor(newColor);
        
    }
    
    public PlayerMove getMove() {
        ScrabblePiece piece;
        ScrabblePiece[][] pieces = this.pieces;
        int mini = 14, maxi = 0, minj = 14, maxj = 0;
        
        for(int j = 0; j < 15; j++)
            for(int i = 0; i < 15; i++)
                if((piece = pieces[i][j]) != null && piece.getColor() == ScrabblePiece.ORANGE) {
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
            ScrabblePiece[] moves = new ScrabblePiece[15];
            for(int i = 0; i < 15; i++)
                moves[i] = pieces[i][minj];
            return new PlayerMove(moves, mini, minj);
        }
        return (mini == maxi)? new PlayerMove(pieces[mini], minj, ~mini) : null;
    }
}
