package jscrabble;

public class Square {

    private int wordMultiplier;
    
    private int letterMultiplier;
    
    public Square(int wordMultiplier, int letterMultiplier) {
        this.wordMultiplier = wordMultiplier;
        this.letterMultiplier = letterMultiplier;
    }

    public int getLetterMultiplier() {
        return letterMultiplier;
    }

    public int getWordMultiplier() {
        return wordMultiplier;
    }
}
