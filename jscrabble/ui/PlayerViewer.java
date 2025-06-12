package jscrabble.ui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;

import jscrabble.Player;
import jscrabble.Support;
import jscrabble.GlobalCache;
import jscrabble.Settings;
import jscrabble.cawt.CButton;
import jscrabble.cawt.CLabel;
import jscrabble.cawt.CTextArea;


public class PlayerViewer extends Panel {
    private Player[] players;
    private int selectedPlayer = -1;
    private List playerList = new List(4);
    private TextField playerNameField = new TextField("", 12);
    private TextField playerTopScoreField = new TextField();
    private TextField playerRatingField = new TextField();
    private TextField playerBestPlayField = new TextField();
    private CLabel playerBestPlayLabel = CLabel.newInstance();
    
    public PlayerViewer(final Player[] players) {
        super(null);
        this.players = players;
        setFont(GlobalCache.FontCache.ARIAL_SMALL);
        setBackground(new Color(0x00ECEEE4));
        for(int i = 0; i < players.length; i++) {
            playerList.add(players[i].getName());
        }
        playerList.addItemListener(new ItemListener() {
            
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    selectedPlayer = playerList.getSelectedIndex();
                    if (selectedPlayer < 0)
                        return;
                        
                    Player selectedPlayer = players[PlayerViewer.this.selectedPlayer];
                    playerNameField.setText(selectedPlayer.name);
                    playerTopScoreField.setText(String.valueOf(selectedPlayer.getTopScore()));
                    playerRatingField.setText(String.valueOf(selectedPlayer.getRating()));
                    playerBestPlayField.setText(String.valueOf(selectedPlayer.bestPlayScore));
                    playerBestPlayLabel.setText(selectedPlayer.bestPlay);
                }
            }
            
        });
        playerList.select(0);
        playerList.dispatchEvent(new ItemEvent(playerList, ItemEvent.ITEM_STATE_CHANGED, playerList.getItem(0), ItemEvent.SELECTED));
        add(playerList).setFont(GlobalCache.FontCache.ARIAL_MEDIUM);
        playerNameField.setFont(GlobalCache.FontCache.ARIAL_MEDIUM);
        playerNameField.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                handlePlayerNameChanged();
            }
            
        });
        playerNameField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                handlePlayerNameChanged();
            }
            
        });
        add(playerNameField);
        add(new Label("Top score ", Label.RIGHT));
        playerTopScoreField.setEditable(false);
        add(playerTopScoreField);
        add(new Label("Rating ", Label.RIGHT));
        playerRatingField.setEditable(false);
        add(playerRatingField);
        add(new Label("Best play ", Label.RIGHT));
        playerBestPlayField.setEditable(false);
        add(playerBestPlayField);
        playerBestPlayLabel.center();
        add(playerBestPlayLabel.getComponent()).setFont(GlobalCache.FontCache.ARIAL_MEDIUM);
    }
    
    private void handlePlayerNameChanged() {
        int selectedIndex = this.selectedPlayer;
        if (selectedIndex >= 0) {
            String playerName = playerNameField.getText();
            players[selectedIndex].name = playerName;
            playerList.replaceItem(playerName, selectedIndex);
            playerList.select(selectedIndex);
        }
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(350, 135);
    }
    
    public void doLayout() {
        int x, y, w;
        Dimension size = getSize();
        playerList.setBounds(1, 1, size.width/2 - 2, size.height - 2);
        Dimension pref = playerNameField.getPreferredSize(10);
        playerNameField.setBounds(x = size.width/2 + 1, 1, w = size.width/2 - 2, y = pref.height);
        pref = playerTopScoreField.getPreferredSize(5);
        getComponent(2).setBounds(x, y += 2, w - pref.width, pref.height);
        getComponent(3).setBounds(size.width - pref.width, y, pref.width, pref.height);
        y += pref.height + 2;
        getComponent(4).setBounds(x, y, w - pref.width, pref.height);
        getComponent(5).setBounds(size.width - pref.width, y, pref.width, pref.height);
        y += pref.height + 2;
        getComponent(6).setBounds(x, y, w - pref.width, pref.height);
        getComponent(7).setBounds(size.width - pref.width, y, pref.width, pref.height);
        
        y += pref.height + 2;
        pref = playerBestPlayLabel.getComponent().getPreferredSize();
        getComponent(8).setBounds(x, y, size.width/2, pref.height);
    }
    
    public void paint(Graphics g) {
        super.paint(g);
    }
    
    public static Dialog create(Component comp, Player[] players) {
        Dialog dialog = new Dialog(Support.getFrame(comp), "List of players");
        Support.setHideOnClose(dialog);
        Color c = new Color(0xFFFCFE8C);
        dialog.setBackground(c);
        dialog.setForeground(Color.black);
        
        dialog.add(new PlayerViewer(players));
        dialog.setFont(GlobalCache.FontCache.ARIAL_SMALL);
        dialog.setResizable(false);
        dialog.pack();
        Support.centerOnScreen(dialog, Support.getScrabble(comp));
        return dialog;
    }
}
