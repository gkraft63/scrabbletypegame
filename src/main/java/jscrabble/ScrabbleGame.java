package jscrabble;

public class ScrabbleGame implements java.io.Serializable {
    private static final long serialVersionUID = 6885070643712420559L;

    public final ScrabbleBag bag = new ScrabbleBag();
    public final ScrabbleBoard board = new ScrabbleBoard();
    public final ScrabbleSeat[] seats;
    public int currentPlayer = -1;
    public int localPlayerOffset;
    private int numPlayers;
    private boolean running;
    
    public ScrabbleGame(Player[] players) {
        int count = players.length;
        ScrabbleSeat[] seats = new ScrabbleSeat[count];
        for(int i = 0; i < count; i++)
            if(players[i] != null) {
                seats[i] = new ScrabbleSeat(players[i]);
                numPlayers++;
            }
        
        this.seats = seats;
    }
    
    public ScrabbleGame(ScrabbleSeat[] seats) {
        int count = seats.length;
        for(int i = 0; i < count; i++)
            if(seats[i] != null)
                numPlayers++;
        
        this.seats = seats;
    }
    
    public int getNumPlayers() {
        return numPlayers;
    }

    public int addPlayer(ScrabbleSeat newSeat) {
        ScrabbleSeat[] seats = this.seats;
        for(int i = 0; i < seats.length; i++)
            if(newSeat == seats[i])
                throw new IllegalArgumentException("player already exists");
        for(int i = 0; i < seats.length; i++)
            if(seats[i] == null) {
                seats[i] = newSeat;
                numPlayers++;
                return i;
            }
        throw new IllegalStateException("full list of players");
    }

    public void removePlayer(ScrabbleSeat player) {
        ScrabbleSeat[] seats = this.seats;
        for(int i = 0; i < seats.length; i++)
            if(seats[i] == player) {
                seats[i] = null;
                numPlayers--;
                return;
            }
        
        throw new IllegalArgumentException("player not found");
    }
    
    public ScrabbleSeat getCurrentPlayer() {
        try {
            return seats[currentPlayer];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    public ScrabbleSeat getNextPlayer() {
        int i = currentPlayer;
        int j = i;
        while(seats[j = (j + 1)%seats.length] == null)
            if(i == j)
                return null;
        return seats[j];
    }
    
    public ScrabbleSeat nextPlayer() {
        int i = currentPlayer;
        int j = i;
        while(seats[j = (j + 1)%seats.length] == null)
            if(i == j)
                return null;
        return seats[currentPlayer = j];
    }
    
    public void stopGame() {
        running = false;
        currentPlayer = -1;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getLocalPlayerOffset() {
        return localPlayerOffset;
    }

    public void setLocalPlayerOffset(int localPlayerOffset) {
        this.localPlayerOffset = localPlayerOffset;
    }
}
