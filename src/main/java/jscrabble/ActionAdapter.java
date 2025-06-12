package jscrabble;

import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jscrabble.dto.GameData;
import jscrabble.ui.AboutWindow;
import jscrabble.ui.EditorPanel;
import jscrabble.ui.JRack;
import jscrabble.util.ArrayList;
import jscrabble.util.StartupIcon;

public class ActionAdapter implements ActionListener, Runnable {
    private Scrabble scrabble;
    private boolean stopped;
    private ActionEvent action = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "New game");
    private int lossesCount;
    private GameData gameCache;
    private LicenseManager manager;
    private License license;
    private long startupTime = System.currentTimeMillis();
    
    public ActionAdapter(Scrabble scrabbleUI) {
        this.scrabble = scrabbleUI;
        new Thread(this).start();
    }
    
    public void actionPerformed(ActionEvent e) {
        if(action == null) {
            synchronized(this) {
                action = e;
                notifyAll();
            }
        }
    }

    public boolean initialize(GameData data) {
        gameCache = data;
        Player[] players = data.getPlayers();
        scrabble.setGame(new ScrabbleGame(players));
        scrabble.timer.timeForMove = data.getTimeForMove();
        scrabble.timer.timeForGame = data.getTimeForGame();
        return true;
    }
    
    public synchronized void run() {
        scrabble.startupIcon.toFront();
        scrabble.tabbedPanel.setSelectedIndex(1);
        scrabble.loadDictionary("slownik.yin");
        StartupIcon.hide(scrabble.startupIcon);
        scrabble.startupIcon = null;
        scrabble.loadLexicon("znaczenia.yin");
        scrabble.tabbedPanel.setSelectedIndex(0);
        manager = new LicenseManager(scrabble);
        try {
            int m1 = (int)(Runtime.getRuntime().totalMemory()/1024);
            int m2 = m1 - (int)(Runtime.getRuntime().freeMemory()/1024);
            scrabble.appendMessageText("Memory usage: "+(m2)+"/"+m1+"KB ("+(100*m2/m1)+"%)\n");
        } catch (Exception e) {
        }
        while(!stopped) {
            try {
                ActionEvent action = this.action;
                if(action != null) {
                    this.action = null;
                    processAction(action);
                }
                wait(1000);
            } catch(Exception e) {
                if(Settings.VERBOSE)
                    e.printStackTrace();
                scrabble.showStatus(e.toString());
            }
        }
    }
    
    protected void processAction(ActionEvent e) throws Exception {
        String command = e.getActionCommand();
        long when = System.currentTimeMillis();
        if (license == null && !Settings.DEMO) {
            license = manager.getLicense();
            if (license.getState() != License.STATE_OK) {
                Timer timer = new Timer(20, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                scrabble.popupExpirationDialog();
                            }
                        }).start();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            } else {
                String title = Support.decrypt(new long[] {
                        3910890869079067218L,
                        -3556041647705160538L,
                        -5475191295826290994L,
                        -6472008466974749180L,
                        310456342351881806L,
                    }) + scrabble.properties.getProperty("");
                Support.getFrame(scrabble).setTitle(title);
                scrabble.showStatus(title);
            }
        } else if (Settings.DEMO) {
            if (when > startupTime + 4*60*1000
                    /*|| when < Settings.expiryTimeInMillis - Settings.expiryPeriodInMillis
                    || when > Settings.expiryTimeInMillis*/) {
                Timer timer = new Timer(20, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                scrabble.popupExpirationDialog();
                            }
                        }).start();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
        if(NEW_GAME_ACTION.equals(command)) {
            if(e.getSource() != this) scrabble.messageText.setText("");
            fireNewGame();
        } else if(MOVE_ACCEPT_ACTION.equals(command))
            fireMoveAccept();
        else if(SCRAMBLE_ACTION.equals(command))
            fireScramble();
        else if(SWAP_PIECES_ACTION.equals(command))
            fireSwapPieces();
        else if(PASS_TURN_ACTION.equals(command))
            firePassTurn();
        else if(TIMEOUT_ACTION.equals(command))
            fireTimeout();
        else if(USED_PLATES_ACTION.equals(command))
            fireTraceUsedPlates();
        else if(SAVE_ACTION.equals(command))
            fireSave();
        else if(LOAD_ACTION.equals(command))
            fireLoad();
        else if(ABOUT_ACTION.equals(command))
            fireAbout();
        else if(RANK_ACTION.equals(command))
            fireRankTable();
        else if(SCREENSHOT_ACTION.equals(command))
            fireScreenshot();
        else if(PROMPT_ACTION.equals(command))
            firePrompt();
    }
    
    private void fireNewGame() {
        lossesCount = 0;
        if(!initialize(scrabble.optionPanel.getData()))
            return;
        ScrabbleGame game = scrabble.getGame();
        ScrabbleBag bag = game.bag;
        try {
            bag.setSeed(Long.parseLong(scrabble.properties.getProperty("random.seed")));
        } catch (Exception e) {
            // ignore
        }
        scrabble.appendMessageText("> Automaton started a new game\n");
        ScrabbleSeat[] seats = game.seats;
        for(int i = 0; i < seats.length; i++)
            if(seats[i] != null)
                seats[i].leftPieces = GameSystem.drawPieces(seats[i].playRack, seats[i].swapRack, bag);
        scrabble.timer.start();
        scrabble.appendMessageText("> "+game.getNextPlayer().player.getName()+" starts\n");
        turnPlayer();
        scrabble.updateView();
    }
    
    private void fireMoveAccept() {
        ScrabbleGame game = scrabble.getGame();
        Engine engine = scrabble.engine;
        ScrabbleSeat currentPlayer = game.getCurrentPlayer();
        if(engine.checkSpelling(game.board)) {
            scrabble.appendMessageText("> "+engine.getCachedWord()+": word found\n");
            game.board.replaceColor(ScrabblePiece.ORANGE, ScrabblePiece.YELLOW);
            if (currentPlayer.player.bestPlayScore < currentPlayer.turnScore) {
                currentPlayer.player.bestPlayScore = currentPlayer.turnScore;
                currentPlayer.player.bestPlay = engine.getCachedWord();
            }
            currentPlayer.won();
            lossesCount = 0;
        } else {
            scrabble.appendMessageText("> "+engine.getCachedWord()+": word not found\n");
            game.board.moveToRack(currentPlayer.playRack);
            currentPlayer.lost();
            lossesCount++;
        }
        turnPlayer();
    }
    
    private void fireSwapPieces() {
        ScrabbleSeat currentPlayer = scrabble.getGame().getCurrentPlayer();
        
        int numPieces = currentPlayer.swapRack.getNumPieces();
        scrabble.appendMessageText("> "+currentPlayer.player.getName()+" has exchanged "+getNumPieces(numPieces)+"\n");
        scrabble.getGame().board.moveToRack(currentPlayer.playRack);
        currentPlayer.lost();
        lossesCount++;
        GameSystem.exchangePieces(currentPlayer.playRack, currentPlayer.swapRack, scrabble.getGame().bag);
        turnPlayer();
    }
    
    private void firePassTurn() {
        ScrabbleGame game = scrabble.getGame();
        ScrabbleSeat currentPlayer = game.getCurrentPlayer();
        game.board.moveToRack(currentPlayer.playRack);
        scrabble.messageText.append("> "+currentPlayer.player.getName()+" has passed his turn\n");
        currentPlayer.lost();
        lossesCount++;
        turnPlayer();
    }
    
    private void fireTimeout() {
        scrabble.timer.stop();
        ScrabbleGame game = scrabble.getGame();
        ScrabbleSeat currentPlayer = game.getCurrentPlayer();
        game.board.moveToRack(currentPlayer.playRack);
        scrabble.messageText.append("> Game time for "+currentPlayer.player.getName()+" has expired\n");
        currentPlayer.lost();
        lossesCount++;
        turnPlayer();
    }
    
    private void fireTraceUsedPlates() {
        ScrabbleGame game = scrabble.getGame();
        ScrabbleSeat currentPlayer = game.getCurrentPlayer();
        if(currentPlayer != null) {
            currentPlayer.setTraceUsedPlates(true);
            scrabble.getJUsedPlates().setUsedPlates(currentPlayer.getUsedPlates());
        }
    }
    
    private static String getNumPieces(int num) {
        if(num == 1)
            return num + " plate";
        return num + ((num < 5)? " plates" : " plates");
    }

    private void fireScramble() {
        JRack rackUI = scrabble.jPlayRack;
        rackUI.getModel().scramble();
        rackUI.repaint();
    }
    
    private void fireSave() throws IOException {
        FileDialog dlg = new FileDialog(Support.getFrame(scrabble), "Save game to file", FileDialog.SAVE);
        dlg.setVisible(true);
        String filename = dlg.getFile();
        if(filename != null) {
            File file = new File(dlg.getDirectory(), filename);
            ObjectOutputStream os = null;
            try {
                os = new ObjectOutputStream(new FileOutputStream(file));
                os.writeObject(scrabble.getGame());
                os.flush();
                scrabble.showStatus("INFO: Game saved to the file: "+file);
            } finally {
                Support.release(os);
            }
        }
    }
    
    private void fireLoad() throws IOException, ClassNotFoundException {
        FileDialog dlg = new FileDialog(Support.getFrame(scrabble), "Load game from file", FileDialog.LOAD);
        dlg.setVisible(true);
        String filename = dlg.getFile();
        if(filename != null) {
            File file = new File(dlg.getDirectory(), filename);
            if(!file.exists()) {
                scrabble.showStatus("ERROR! File not found: "+file);
                return;
            }
            if(!file.canRead()) {
                scrabble.showStatus("ERROR! Can't read file: "+file);
                return;
            }
            ObjectInputStream is = null;
            try {
                is = new ObjectInputStream(new FileInputStream(file));
                scrabble.setGame((ScrabbleGame) is.readObject());
                scrabble.editorPanel.clear();
                scrabble.showStatus("INFO: Game loaded sucessfully");
            } finally {
                Support.release(is);
            }
        }
    }
    
    private void fireAbout() {
        AboutWindow win = new AboutWindow(scrabble);
        win.setTitle("About");
        win.setModal(true);
        win.setVisible(true);
    }
    
    private void fireRankTable() {
        RankTable table = scrabble.jRankTable;
        if(table != null)
            table.setShowing(!table.isShowing(), scrabble);
    }
    
    private static String screenShotDirectory;
    private static String screenShotFile = "Screenshot.png";
    
    private void fireScreenshot() {
        try {
            FileDialog dlg = new FileDialog(Support.getFrame(scrabble), "Save board image", FileDialog.SAVE);
            dlg.setFilenameFilter(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".png");
                }
            });
            if (screenShotDirectory != null)
                dlg.setDirectory(screenShotDirectory);
            dlg.setFile(screenShotFile);
            dlg.show();
            
            FileOutputStream os = null;
            String file = dlg.getFile();
            if (file != null && (screenShotDirectory = dlg.getDirectory()) != null) {
                if(file.indexOf('.') < 0)
                    file = file+".png";
                screenShotFile = file;
                
                Image image = scrabble.createImage(461, 489);
                Graphics g = image.getGraphics();
                g.translate(-11, -11);
                scrabble.paint(g);
                g.dispose();
                byte[] bytes = new PngEncoder(image, false, 0, 9).pngEncode();
                
                try {
                    os = new FileOutputStream(new File(screenShotDirectory, file));
                    os.write(bytes);
                } finally {
                    Support.release(os);
                }
                scrabble.showStatus(file+": "+bytes.length+" bytes saved");
            }
        } catch (Exception e) {
            scrabble.showStatus(e.toString());
        }
    }
    
    private void firePrompt() {
        EditorPanel editorPanel = scrabble.editorPanel;
        int selectedIndex = editorPanel.getSelectedIndex();
        if(selectedIndex < 0) {
            editorPanel.generateSolutions();
            editorPanel.select(1);
        } else {
            editorPanel.select(selectedIndex+1);
        }
        scrabble.updateView();
    }
    
    private void turnPlayer() {
        ScrabbleSeat newPlayer;
        Scrabble scrabble = this.scrabble;
        scrabble.timer.stop();
        ScrabbleGame game = scrabble.getGame();
        ScrabbleSeat oldPlayer = game.getCurrentPlayer();
        scrabble.jPieceHolder.drop();
        scrabble.editorPanel.clear();
        if(oldPlayer != null) {
            if(scrabble.isTraceUsedPlatesEnabled())
                scrabble.getJUsedPlates().setVisible(false);
            else
                oldPlayer.setTraceUsedPlates(false);
        }
        scrabble.hideBlankSelector();
        if(lossesCount >= 2*game.getNumPlayers()) {
            closeGame();
            return;
        }
        
        while((newPlayer = game.nextPlayer()) != null && newPlayer.player instanceof Player.Automaton) {
            scrabble.engine.setEvaluator(((Player.Automaton) newPlayer.player).getLevel());
            ArrayList moves = scrabble.engine.scrabble(game.board.pieces, newPlayer.playRack.getPieces());
            if(moves.size() > 0) {
                PlayerMove bestMove = scrabble.engine.getEvaluator().getBestMove(moves);
                newPlayer.turnScore = bestMove.getScore();
                bestMove.play(scrabble);
                game.board.replaceColor(ScrabblePiece.ORANGE, ScrabblePiece.GREEN);
                scrabble.showStatus(scrabble.engine.lexicon.getString(bestMove.toString(ScrabblePiece.GREEN)));
                scrabble.repaint();
                try {
                    Thread.sleep(1500);
                    wait(1);
                } catch (Exception e) {
                }
                newPlayer.won();
                lossesCount = 0;
                game.board.replaceColor(ScrabblePiece.GREEN, ScrabblePiece.YELLOW);
                if((newPlayer.leftPieces = GameSystem.drawPieces(newPlayer.playRack, newPlayer.swapRack, game.bag)) == 0) {
                    closeGame();
                    return;
                }
                scrabble.updateBagNumPieces();
                scrabble.repaint();
                scrabble.appendMessageText("> Possible computer moves: "+moves.size()+" ["+scrabble.engine.getTimeUsed()+" ms]\n");
                if(gameCache.isShowComputerPieces())
                    scrabble.appendMessageText("> Computer rack: "+newPlayer.playRack+"\n");
                
            } else {
                int swapingPiecesCount = Math.min(newPlayer.leftPieces, game.bag.getNumPieces());
                if(swapingPiecesCount == 0) {
                    scrabble.messageText.append("> "+newPlayer.player.getName()+" has passed turn\n");
                } else {
                    GameSystem.exchangePieces(newPlayer.playRack, newPlayer.playRack, game.bag);
                    scrabble.messageText.append("> "+newPlayer.player.getName()+" has exchanged "+getNumPieces(swapingPiecesCount)+"\n");
                }
                newPlayer.lost();
                if(++lossesCount >= 2*game.getNumPlayers()) {
                    closeGame();
                    return;
                }
            }
        }
        if((newPlayer.leftPieces = GameSystem.drawPieces(newPlayer.playRack, newPlayer.swapRack, game.bag)) == 0) {
            closeGame();
            return;
        }
        if(newPlayer != oldPlayer && oldPlayer != null) {
            try {
                scrabble.jPlayRack.setVisible(false);
                scrabble.jSwapRack.setVisible(false);
                scrabble.showStatus("Next player: "+newPlayer.player.getName());
                scrabble.repaint();
                scrabble.getJTurnPlayer().show("Next player:\n"+newPlayer.player.getName());
            } finally {
                scrabble.jPlayRack.setVisible(true);
                scrabble.jSwapRack.setVisible(true);
                scrabble.showStatus("");
            }
        }
        scrabble.getJUsedPlates().setUsedPlates(newPlayer.getUsedPlates());
        scrabble.jPlayRack.setModel(newPlayer.playRack);
        scrabble.jSwapRack.setModel(newPlayer.swapRack);
        scrabble.updateBagNumPieces();
        scrabble.timer.start();
        scrabble.repaint();
    }
    
    private void closeGame() {
        ScrabbleSeat[] players = scrabble.getGame().seats;
        ScrabbleBag bag = scrabble.getGame().bag;
        int playScore = 0;
        ScrabbleSeat finisher = null;
        ScrabblePiece p;
        
        //timer.stop();
        for(int i = 0; i < players.length; i++)
            if(players[i] != null) {
                ScrabbleRack playRack = players[i].playRack;
                ScrabbleRack swapRack = players[i].swapRack;
                int playerScore = 0, leftPlates = 0;
                for(int j = 0; j < Settings.RACK_SIZE; j++) {
                    if((p = playRack.getPieceAt(j)) != null) {
                        playerScore += p.value;
                        leftPlates++;
                    }
                    if((p = swapRack.getPieceAt(j)) != null) {
                        playerScore += p.value;
                        leftPlates++;
                    }
                }
                players[i].gameScore = players[i].gameScore - playerScore;
                if(leftPlates == 0)
                    finisher = players[i];
                playScore += playerScore;
            }
        
        if(finisher != null)
            finisher.gameScore = finisher.gameScore + playScore;
        
        // Determine winner player
        boolean tie = false;
        ScrabbleSeat winner = null;
        int winScore = Integer.MIN_VALUE;
        for(int i = 0; i < players.length; i++)
            if(players[i] != null) {
                int pScore = players[i].gameScore;
                players[i].player.addRating(pScore);
                if(winScore < pScore) {
                    winner = players[i];
                    winScore = pScore;
                    tie = false;
                } else if(winScore == pScore) {
                    tie = true;
                }
            }
        
        scrabble.getGame().stopGame();
        scrabble.updateView();
        
        if(scrabble.jRankTable != null)
            scrabble.jRankTable.addRank(winner.player.getName(), winner.gameScore, getOpposites(winner));
        scrabble.messageText.append((tie)? "> Game ends with a tie\n" : "> " + winner.player.getName() + " wins\n");
        scrabble.repaint();
        scrabble.bean.ejbStore();
    }
    
    private String getOpposites(ScrabbleSeat except) {
        ScrabbleSeat[] seats = scrabble.getGame().seats;
        StringBuffer buf = new StringBuffer();
        ScrabbleSeat seat;
        boolean afterFirst = false;
        
        for(int i = 0; i < seats.length; i++)
            if((seat = seats[i]) != null && seat != except) {
                if(afterFirst)
                    buf.append('/');
                else
                    afterFirst = true;
                buf.append(seat.player.getName());
            }
        
        return buf.toString();
    }

    public void stop() {
        stopped = true;
    }
    
    public static final String NEW_GAME_ACTION = "New game";
    public static final String MOVE_ACCEPT_ACTION = "OK";
    public static final String PASS_TURN_ACTION = "Pass";
    public static final String SWAP_PIECES_ACTION = "Exchange";
    public static final String SCRAMBLE_ACTION = "Shuffle";
    public static final String TIMEOUT_ACTION = "Timeout";
    public static final String USED_PLATES_ACTION = "Used plates";
    public static final String SAVE_ACTION = "Save game";
    public static final String LOAD_ACTION = "Load game";
    public static final String RANK_ACTION = "Rank";
    public static final String ABOUT_ACTION = "About...";
    public static final String SCREENSHOT_ACTION = "Screenshot";
    public static final String PROMPT_ACTION = "Hint";

}
