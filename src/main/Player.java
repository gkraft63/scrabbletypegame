package jscrabble;

import java.awt.Image;
import java.io.IOException;
import java.io.Serializable;

import jscrabble.util.ArrayList;

public class Player implements Serializable {
    private static final long serialVersionUID = -2763440460733689591L;
    
    /** name of this player */
    public String name;
    /** image icon of this player */
    transient Image icon = GlobalCache.ImageCache.FACE_HUMAN;
    
    private int[] scoreList = new int[0];
    public String bestPlay;
    public int bestPlayScore;

    private static int playerNo = 1;
    
    public static Player getSystemPlayer() {
        String userName;
        try {
            userName = System.getProperty("user.name");
        } catch (Exception e) {
            userName = "Player"+(playerNo++);
        }
        return new Player(userName);
    }
    
    public void addRating(int rating) {
        int scoreLength = scoreList.length; 
        if (scoreLength < 30) {
            int[] newScoreList = new int[scoreLength+1];
            System.arraycopy(scoreList, 0, newScoreList, 0, scoreLength);
            newScoreList[scoreLength] = rating;
            scoreList = newScoreList;
        } else {
            System.arraycopy(scoreList, 1, scoreList, 0, scoreLength-1);
            scoreList[scoreLength] = rating;
        }
    }
    
    public int getRating() {
        int scoreLength = scoreList.length; 
        int scoreTotal = 0;
        for(int i = 0; i < scoreLength; i++)
            scoreTotal += scoreList[i];
        return (scoreLength == 0)? 0 : scoreTotal/scoreLength;
    }
    
    public int getTopScore() {
        int scoreLength = scoreList.length; 
        int maxScore = 0;
        for(int i = 0; i < scoreLength; i++)
            if (maxScore < scoreList[i])
                maxScore = scoreList[i];
        return maxScore;
    }
    
    public Player(String name) {
        this.name = name;
    }
    
    public Player(String name, Image icon) {
        this.name = name;
        this.icon = icon;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Image getIcon() {
        return icon;
    }
    
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        icon = GlobalCache.ImageCache.FACE_HUMAN;
    }
    
    public static class Remote extends Player {
        public Remote(String name) {
            super(name);
        }
        
        public Remote(Player player) {
            super(player.name);
        }
    }
    
    public static class Automaton extends Player {
        private int level;
        
        public Automaton(int level) {
            super("Computer ["+level+"]");
            this.level = level;
            icon = GlobalCache.ImageCache.FACE_MACHINE;
        }
        
        public int getLevel() {
            return level;
        }
        
        private void readObject(java.io.ObjectInputStream in)
                throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            icon = GlobalCache.ImageCache.FACE_MACHINE;
        }
    }
}
