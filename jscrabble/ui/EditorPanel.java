package jscrabble.ui;

import java.awt.*;
import java.awt.event.*;

import jscrabble.ActionAdapter;
import jscrabble.Evaluator;
import jscrabble.GlobalCache;
import jscrabble.PlayerMove;
import jscrabble.ScrabbleBag;
import jscrabble.ScrabbleGame;
import jscrabble.ScrabblePiece;
import jscrabble.ScrabbleSeat;
import jscrabble.Scrabble;
import jscrabble.cawt.CButton;
import jscrabble.cawt.CList;
import jscrabble.util.ArrayList;

public final class EditorPanel extends Container implements ActionListener {
    
    private ArrayList solutions = new ArrayList(0);
    private CList cList = CList.newInstance();
    private Scrabble scrabble;
    
    public EditorPanel(Scrabble scrabble) {
        this.scrabble = scrabble;
        setName("Editor");
        setBackground(new Color(0xFFECEEE4));
        setLayout(null);
        add(new Label("The bag content: ", Label.LEFT)).setFont(GlobalCache.FontCache.ARIAL_SMALL);
        add(scrabble.getGame().bag.new BagComponent());
        Panel p = new Panel(new GridLayout(0, 3));
        p.add(createButton("Take off"));
        p.add(createButton("All moves"));
        p.add(createButton("Lay out"));
        add(p);
        add(cList.getScrollComponent());
        
        cList.setItemListener(new ItemListener() {
            
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    int index = cList.getSelectedIndex();
                    Scrabble scrabble = EditorPanel.this.scrabble;
                    scrabble.jBoard.getModel().moveToRack(scrabble.jPlayRack.getModel());
                    if(index > 0) {
                        ((PlayerMove) solutions.get(index - 1)).play(scrabble);
                        Object[] objs = e.getItemSelectable().getSelectedObjects();
                        if(objs == null || objs.length == 0 || objs[0] == null)
                            return;
                        String item = (String) objs[0];
                        int i = item.indexOf(' ');
                        scrabble.showStatus(scrabble.engine.lexicon.getString((i < 0)? item : item.substring(0, i)));
                    }
                    scrabble.itemStateChanged(e);
                    scrabble.jBoard.repaint();
                    scrabble.jPlayRack.repaint();
                    scrabble.jSwapRack.repaint();
                }
            }
            
        });
        
        setVisible(false);
    }
    
    public int getSelectedIndex() {
        return cList.getSelectedIndex();
    }
    
    public void select(int index) {
        int size = solutions.size();
        if (size > 0) {
            cList.select(index % size);
        }
    }
    
    public void clear() {
        solutions.clear();
        cList.removeAllItems();
    }
    
    private Component createButton(String label) {
        CButton cb = CButton.newInstance();
        cb.setLabel(label);
        cb.addActionListener(this);
        cb.getComponent().setFont(GlobalCache.FontCache.ARIAL_SMALL);
        return cb.getComponent();
    }
    
    
    private void setContent(ArrayList list) {
        ArrayList list2 = new ArrayList();
        boolean isVisible = isVisible();
        
        CList listWrapper = this.cList;
        Component comp = listWrapper.getScrollComponent();
        if (isVisible)
            comp.setVisible(false);
        scrabble.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        listWrapper.removeAllItems();
        
        int size = list.size();
        String[] desc = new String[size+1];
        String prev = desc[0] = " ";
        int i, k;
        for(i = 1, k = 0; k < size; k++) {
            String s = ((PlayerMove) list.get(k)).toString();
            if(!prev.equals(s)) {
                desc[i++] = prev = s;
                list2.add(list.get(k));
            }
        }
        String[] desc2 = new String[i];
        System.arraycopy(desc, 0, desc2, 0, i);
        
        solutions = list2;
        listWrapper.setItems(desc2);
        scrabble.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if (isVisible)
            comp.setVisible(true);
    }
    
    
    public void doLayout() {
        Dimension size = getSize();
        Component c;
        Dimension d;
        int y;
        d = (c = getComponent(0)).getPreferredSize();
        c.setBounds(0, 0, d.width, y = d.height);
        d = (c = getComponent(1)).getPreferredSize();
        c.setBounds((size.width - d.width)/2, y, d.width, d.height);
        y += d.height + 10;
        d = (c = getComponent(2)).getPreferredSize();
        c.setBounds(0, y, size.width, d.height);
        y += d.height;
        getComponent(3).setBounds(0, y, size.width, size.height - y);
    }
    
    public Dimension getPreferredSize() {
        return getComponent(1).getPreferredSize();
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        scrabble.timer.setPause(visible);
        Container buttonPanel = scrabble.buttonPanel;
        if(visible) {
            buttonPanel.getComponent(ButtonPanel.BTN_PLAY).setEnabled(false);
            buttonPanel.getComponent(ButtonPanel.BTN_PASS).setEnabled(false);
            buttonPanel.getComponent(ButtonPanel.BTN_SWAP).setEnabled(false);
        } else {
            buttonPanel.getComponent(ButtonPanel.BTN_PASS).setEnabled(true);
            scrabble.updateView();
        }
        scrabble.jBoard.getModel().replaceColor(visible? ScrabblePiece.YELLOW : ScrabblePiece.GREEN, visible? ScrabblePiece.GREEN : ScrabblePiece.YELLOW);
        scrabble.jBoard.repaint();
    }
    
    public final synchronized ArrayList generateSolutions() {
        clear();
        scrabble.jBoard.getModel().moveToRack(scrabble.jPlayRack.getModel());
        scrabble.engine.setEvaluator(Evaluator.EXPERT);
        ArrayList moves = scrabble.engine.scrabble(scrabble.getGame().board.pieces, scrabble.jPlayRack.getModel().getPieces());
        moves.sort(Evaluator.RANK_DESCENDING);
        setContent(moves);
        return moves;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if("Take off".equals(command)) {
            clear();
            ScrabbleGame game = scrabble.getGame();
            ScrabbleBag bag = game.bag;
            game.board.collectAll(bag);
            ScrabbleSeat[] seats = game.seats;
            for(int i = 0; i < seats.length; i++) {
                if(seats[i] != null) {
                    seats[i].playRack.collectAllPieces(bag);
                    seats[i].swapRack.collectAllPieces(bag);
                }
            }
            scrabble.repaint();
        } else if("All moves".equals(command)) {
            generateSolutions();
            scrabble.repaint();
        } else if("Lay out".equals(command)) {
            scrabble.jBoard.getModel().replaceColor(ScrabblePiece.ORANGE, ScrabblePiece.GREEN);
            scrabble.jBoard.repaint();
            cList.removeAllItems();
        }
    }
}
