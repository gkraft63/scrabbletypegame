package jscrabble;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

public final class ScrabbleBean {
    private transient File storage;
    
    private Player[] players = new Player[4]; {
        String player0 = "Player 1";
        try {
            player0 = System.getProperty("user.name");
        } catch (Throwable x) {
        }
        players[0] = new Player(player0);
        for (int i = 1; i < 4; i++) {
            players[i] = new Player("Player "+(i+1));
        }
    }
    private Object[][] rankCells = new Object[0][0];
    

    public ScrabbleBean() {
    }
    
    public void setStorage(File storage) {
        if ((this.storage = storage) != null && storage.exists())
            ejbLoad();
    }
    
    public Player[] getPlayers() {
        return players;
    }

    public Object[][] getRankCells() {
        return rankCells;
    }

    public void setRankCells(Object[][] rankCells) {
        this.rankCells = rankCells;
    }

    public void ejbLoad() {
        if(storage == null)
            return;
        
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(storage));
            rankCells = (Object[][]) in.readObject();
            players = (Player[]) in.readObject();
            
        } catch(Exception e) {
        } finally {
            Support.release(in);
        }
    }
    
    public void ejbStore() {
        if(storage == null)
            return;
        
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(new FileOutputStream(storage));
            out.writeObject(rankCells);
            out.writeObject(players);
            
        } catch(Exception e) {
        } finally {
            Support.release(out);
        }
    }
}
