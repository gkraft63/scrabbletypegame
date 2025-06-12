package jscrabble;

import java.io.File;
import java.net.URL;

public class OptionData {
    private boolean remote;
    private boolean server;
    private URL remoteURL;
    private Player[] players;
    private File dictionaryFile;
    private boolean spellCheckingEnabled;
    private boolean showAutomatonPieces;
    private int timeForMove;
    private int timeForGame;
    
    
    public Player[] getPlayers() {
        return players;
    }
    
    public void setPlayers(Player[] players) {
        this.players = players;
    }
    
    public boolean isRemote() {
        return remote;
    }
    
    public void setRemote(boolean remote) {
        this.remote = remote;
    }
    
    public URL getRemoteURL() {
        return remoteURL;
    }
    
    public void setRemoteURL(URL remoteURL) {
        this.remoteURL = remoteURL;
    }
    
    public boolean isServer() {
        return server;
    }
    
    public void setServer(boolean server) {
        this.server = server;
    }

    public File getDictionaryFile() {
        return dictionaryFile;
    }

    public void setDictionaryFile(File dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }

    public boolean isShowAutomatonPieces() {
        return showAutomatonPieces;
    }

    public void setShowAutomatonPieces(boolean showAutomatonPieces) {
        this.showAutomatonPieces = showAutomatonPieces;
    }

    public boolean isSpellCheckingEnabled() {
        return spellCheckingEnabled;
    }

    public void setSpellCheckingEnabled(boolean spellCheckingEnabled) {
        this.spellCheckingEnabled = spellCheckingEnabled;
    }

    public int getTimeForGame() {
        return timeForGame;
    }

    public void setTimeForGame(int timeForGame) {
        this.timeForGame = timeForGame;
    }

    public int getTimeForMove() {
        return timeForMove;
    }

    public void setTimeForMove(int timeForMove) {
        this.timeForMove = timeForMove;
    }
}
