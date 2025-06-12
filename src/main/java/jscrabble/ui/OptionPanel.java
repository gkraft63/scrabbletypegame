package jscrabble.ui;

import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import jscrabble.GlobalCache;
import jscrabble.Player;
import jscrabble.Scrabble;
import jscrabble.Support;
import jscrabble.cawt.CButton;
import jscrabble.cawt.CLabel;
import jscrabble.dto.GameData;

public class OptionPanel extends Panel {
    private Panel playerPanel = new Panel(new GridLayout(0, 2));
    private Choice[] playerChoice = new Choice[4];
    private Choice moveTimeChoice = new Choice();
    private Choice gameTimeChoice = new Choice();
    private Checkbox showComputerPieces = new Checkbox("Show computer rack");
    private Player[] activePlayers = null;
    private Dialog playerViewer = null;
    
    public OptionPanel() {
        setLayout(null);
        setFont(GlobalCache.FontCache.ARIAL_SMALL);
        setBackground(GlobalCache.ColorCache.TABBED_PANEL_BGCOLOR);
        
        for(int i = 0; i < 4; i++) {
            Choice c = new Choice();
            c.add("None");
            c.add("Player 1");
            c.add("Player 2");
            c.add("Player 3");
            c.add("Player 4");
            c.add("Comp: novice");
            c.add("Comp: basic");
            c.add("Comp: intermediate");
            c.add("Comp: advanced");
            c.add("Comp: expert");
            if(i == 0)
                c.select(1);
            else if(i == 1)
                c.select(6);
            
            playerChoice[i] = c;
            playerPanel.add(new Label("Player "+(i+1)+": ", Label.RIGHT));
            playerPanel.add(c);
        }
        add(playerPanel);
        
        CButton b = CButton.newInstance();
        b.setLabel("Solitaire");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playerChoice[0].select(1);
                playerChoice[1].select(0);
                playerChoice[2].select(0);
                playerChoice[3].select(0);
            }
        });
        add(b.getComponent());
        
        b = CButton.newInstance();
        b.setLabel("Computer");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playerChoice[0].select(1);
                playerChoice[1].select(6);
                playerChoice[2].select(0);
                playerChoice[3].select(0);
            }
        });
        add(b.getComponent());
        
        b = CButton.newInstance();
        b.setLabel("Human");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playerChoice[0].select(1);
                playerChoice[1].select(2);
                playerChoice[2].select(0);
                playerChoice[3].select(0);
            }
        });
        add(b.getComponent());
        add(new Label("Time for move: ", Label.RIGHT));
        Choice c = moveTimeChoice;
        c.add("unlimited");
        c.add("30 sec.");
        c.add("60 sec.");
        c.add("120 sec.");
        add(c);
        
        add(new Label("Time for game: ", Label.RIGHT));
        c = gameTimeChoice;
        c.add("unlimited");
        c.add("5 min.");
        c.add("10 min.");
        c.add("15 min.");
        c.add("20 min.");
        c.add("30 min.");
        add(c);
        add(showComputerPieces);
        final CButton cb = CButton.newInstance();
        cb.setLabel("Player list");
        cb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (activePlayers != null) {
                    if (playerViewer == null) {
                        playerViewer = PlayerViewer.create(OptionPanel.this, activePlayers);
                        playerViewer.addComponentListener(new ComponentAdapter() {
                            public void componentHidden(ComponentEvent e) {
                                updatePlayerChoices();
                                Support.getScrabble(OptionPanel.this).bean.ejbStore();
                            }
                        });
                    }
                    playerViewer.setVisible(true);
                }
            }
        });
        add(cb.getComponent());
    }
    
    public void setPlayers(Player[] players) {
        this.activePlayers = players;
        updatePlayerChoices();
    }
    
    private synchronized void updatePlayerChoices() {
        Choice[] playerChoice = this.playerChoice;
        Player[] players = this.activePlayers;
        for(int i = 0; i < playerChoice.length; i++) {
            Choice choice = playerChoice[i];
            int index = choice.getSelectedIndex();
            for(int k = 1; k <= 4; k++) {
                choice.insert(players[k-1].name, k);
                choice.remove(k+1);
            }
            choice.select(index);
        }
    }
    
    public void doLayout() {
        int x, y;
        Dimension size = getSize();
        Dimension pref = playerPanel.getPreferredSize();
        playerPanel.setBounds(0, y = 0, size.width, pref.height);
        
        y += pref.height + 10;
        pref = getComponent(1).getPreferredSize();
        getComponent(1).setBounds(0, y, x = size.width/3, pref.height);
        getComponent(2).setBounds(x, y, x, pref.height);
        getComponent(3).setBounds(2*x, y, x, pref.height);
        y += pref.height + 10;
        pref = getComponent(5).getPreferredSize();
        getComponent(4).setBounds(0, y, size.width/2, pref.height);
        getComponent(5).setBounds(size.width/2, y, size.width/2, pref.height);
        y += pref.height + 2;
        getComponent(6).setBounds(0, y, size.width/2, pref.height);
        getComponent(7).setBounds(size.width/2, y, size.width/2, pref.height);
        y += pref.height + 2;
        getComponent(8).setBounds(0, y, size.width, pref.height);
        y += pref.height + 10;
        getComponent(9).setBounds(size.width/3, y, size.width/3, getComponent(9).getPreferredSize().height);
    }
    
    public GameData getData() {
        GameData data = new GameData();
        
        Player[] players = new Player[4];
        for(int i = 0; i < 4; i++) {
            Player player = null;
            int selectedIndex = playerChoice[i].getSelectedIndex();
            switch(selectedIndex) {
                case 1:
                case 2:
                case 3:
                case 4:
                    if(activePlayers == null)
                        player = new Player(playerChoice[i].getSelectedItem());
                    else
                        player = activePlayers[playerChoice[i].getSelectedIndex() - 1];
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    player = new Player.Automaton(selectedIndex-4);
                    break;
                default:
                    break;
            }
            players[i] = player;
        }
        data.setPlayers(players);
        int time = 0;
        switch(moveTimeChoice.getSelectedIndex()) {
            case 1:
                time = 30;
                break;
            case 2:
                time = 60;
                break;
            case 3:
                time = 120;
                break;
        }
        data.setTimeForMove(time);
        
        time = 0;
        switch(gameTimeChoice.getSelectedIndex()) {
            case 1:
                time = 5*60;
                break;
            case 2:
                time = 10*60;
                break;
            case 3:
                time = 15*60;
                break;
            case 4:
                time = 20*60;
                break;
            case 5:
                time = 30*60;
                break;
        }
        data.setTimeForGame(time);
        data.setShowComputerPieces(showComputerPieces.getState());
        
        return data;
    }
}
