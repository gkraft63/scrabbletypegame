package jscrabble;

import java.io.Serializable;

public class ScrabbleSeat implements Serializable {
    private static final long serialVersionUID = -444115643732395524L;
    
    public final ScrabbleRack playRack = new ScrabbleRack();
    public final ScrabbleRack swapRack = new ScrabbleRack();
    public final Player player;
    public int gameScore;
    public int turnScore = -1;
    public int leftPieces;
    public int gameTime = -1;
    
    private transient boolean[] usedPlates;
    
    
    public ScrabbleSeat(Player p) {
        this.player = p;
    }
    
    public void won() {
        gameScore += turnScore;
        turnScore = -1;
    }
    
    public void lost() {
        turnScore = -1;
    }
    
    public void setTraceUsedPlates(boolean trace) {
        if(!trace)
            usedPlates = null;
        else if(usedPlates == null)
            usedPlates = new boolean[100];
    }
    
    public boolean[] getUsedPlates() {
        return usedPlates;
    }
}
