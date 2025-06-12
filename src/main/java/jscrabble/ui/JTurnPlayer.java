package jscrabble.ui;

import java.awt.*;
import java.awt.event.*;

import jscrabble.Settings;
import jscrabble.Support;
import jscrabble.GlobalCache;
import jscrabble.Scrabble;
import jscrabble.cawt.CButton;
import jscrabble.cawt.CTextArea;


public class JTurnPlayer extends Panel implements ActionListener {
    
    static Rectangle rect = new Rectangle(0, 0, 150, 150);
    Dialog parent;
    Scrabble scrabble;
    WindowEvent event;
    private CTextArea jArea;
    
    public JTurnPlayer(Scrabble scrabble) {
        this.scrabble = scrabble;
        Color color = new Color(0xFFFCFE8C);
        Dialog dialog = parent = new Dialog(Support.getFrame(scrabble), "Change Turn", true);
        dialog.setBackground(GlobalCache.ColorCache.LAVENDER);
        dialog.setFont(scrabble.getFont());
        setFont(scrabble.getFont());
        setBackground(color);
        setForeground(Color.black);
        event = new WindowEvent(parent, WindowEvent.WINDOW_CLOSING);
        Support.setHideOnClose(dialog);
        
        CTextArea cp = jArea = CTextArea.newInstance();
        cp.setText("Next player: ");
        cp.getComponent().setBackground(color);
        cp.getComponent().setFont(GlobalCache.FontCache.ARIAL_MEDIUM);
        add(cp.getScrollComponent());
        CButton b;
        add((b = CButton.newInstance()).getComponent());
        b.setLabel("OK");
        b.addActionListener(this);

        dialog.add(this);
        dialog.setResizable(false);
        dialog.setModal(true);
        dialog.pack();
        Support.centerOnScreen(dialog, scrabble);
    }
    
    public void actionPerformed(ActionEvent e) {
        parent.dispatchEvent(event);
    }
    
    public void paint(Graphics g) {
        g.drawImage(GlobalCache.ImageCache.BANNER, 0, 0, this);
        super.paint(g);
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(255, 255);
    }
    
    public void doLayout() {
        Dimension size = getSize();
        Dimension d1; Component c1;
        d1 = (c1 = getComponent(1)).getPreferredSize();
        c1.setBounds(0, size.height - d1.height, size.width, d1.height);
        getComponent(0).setBounds(0, 103, size.width, size.height - d1.height - 103);
    }
    
    public void show(String status) {
        jArea.setText(status);
        parent.setVisible(true);
    }
}
