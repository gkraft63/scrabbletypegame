package jscrabble.ui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jscrabble.GlobalCache;
import jscrabble.Settings;
import jscrabble.cawt.CButton;

public class ButtonPanel extends Panel implements ActionListener {
    public static final int BTN_NEW_GAME = 0;
    public static final int BTN_SCRAMBLE = 2;
    public static final int BTN_PROMPT = 3;
    public static final int BTN_USED_PLATES = 4;
    public static final int BTN_SAVE = 5;
    public static final int BTN_LOAD = 6;
    public static final int BTN_RANK = 7;
    public static final int BTN_SCREENSHOT = 8;
    public static final int BTN_ABOUT = 9;
    public static final int BTN_PLAY = 11;
    public static final int BTN_PASS = 12;
    public static final int BTN_SWAP = 13;
    
    private ActionListener delegate;
    
    public ButtonPanel() {
        setLayout(new GridLayout(0, 1));
        add(createActionButton("New game").getComponent());
        add(new Label(" "));
        add(createActionButton("Shuffle").getComponent());
        add(createActionButton("Hint").getComponent());
        add(createActionButton("Used plates").getComponent());
        add(createActionButton("Save game").getComponent());
        add(createActionButton("Load game").getComponent());
        Component c = add(createActionButton("Rank").getComponent());
        c.setEnabled(false);
        add(createActionButton("Screenshot").getComponent());
        add(createActionButton("About...").getComponent());
        add(new Label(" "));
        add(createActionButton("OK").getComponent()).setEnabled(false);
        add(createActionButton("Pass").getComponent()).setEnabled(false);
        add(createActionButton("Exchange").getComponent()).setEnabled(false);
    }
    
    private CButton createActionButton(String label) {
        CButton c = CButton.newInstance();
        c.setLabel(label);
        c.addActionListener(this);
        c.getComponent().setFont(GlobalCache.FontCache.ARIAL_SMALL);
        return c;
    }

    public void actionPerformed(ActionEvent e) {
        ActionListener delegate = this.delegate;
        if (delegate != null)
            delegate.actionPerformed(e);
    }

    public ActionListener getActionListener() {
        return delegate;
    }

    public void setActionListener(ActionListener delegate) {
        this.delegate = delegate;
    }
}
