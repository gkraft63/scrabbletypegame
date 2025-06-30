package jscrabble.ui;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.*;

import jscrabble.Support;
import jscrabble.GlobalCache;
import jscrabble.Settings;
import jscrabble.cawt.CButton;
import jscrabble.cawt.CTextArea;


public class AboutWindow extends Dialog implements ActionListener {
    private CTextArea cText;
    
    public AboutWindow(Component comp) {
        super(Support.getFrame(comp), "About JScrabble");
        Support.setDisposeOnClose(this);
        Color c = new Color(0xFFFCFE8C);
        setBackground(c);
        setForeground(Color.black);
        
        CTextArea cp = cText = CTextArea.newInstance();
        StringBuffer buf = new StringBuffer();
        if(Settings.DEMO) {
            buf.append("JScrabble DEMO (c) Bernacek, ");
            buf.append("version: "+Settings.VERSION+"\n\n");
            buf.append("WWW: http://scrabble.net.pl/en/\n");
            buf.append("Contact me: bernacek@gmail.com\n");
            buf.append("License: Freeware\n\n");
            buf.append("This version of JScrabble game is DEMO version containing limited functionalities.");
              //      +" compared to PROFESSIONAL version. You are permitted to freely use and distributed"
               //     +" this game with restrictions to GNU license but with unmodified form only."
              //      +" In particular it is forbidden to decompile Java byte codes,"
                //    +" which comprise exclusive intelectual property of Author of this Game"
                //    +".");
        } else {
            buf.append("JScrabble PROFESSIONAL (c) Bernacek, ");
            buf.append("version: "+Settings.VERSION+"\n\n");
            buf.append("WWW: http://scrabble.net.pl/en/\n");
            buf.append("Contact me: bernacek@gmail.com\n");
         //   buf.append("License: Shareware, unauthorised distribution forbidden\n\n");
           // buf.append("It is forbidden to distribute, licensing, modifying,"
             //       +" processing and decompiling pieces of this Software and all"
               //     +" included in the bundle."
                 //   +" In particular it is forbidden to decompile Java byte codes,"
                   // +" which comprise exclusive intelectual property of Author of this Game"
                    //+".");
        }
        cp.setText(buf.toString());
        
        cp.getComponent().setBackground(c);
        cp.getComponent().setFont(GlobalCache.FontCache.ARIAL_MEDIUM);
        add(cp.getScrollComponent());
        CButton b;
        add((b = CButton.newInstance()).getComponent());
        b.setLabel("Close");
        b.addActionListener(this);
        setFont(GlobalCache.FontCache.ARIAL_SMALL);
        setResizable(false);
        pack();
        Support.centerOnScreen(this, Support.getScrabble(comp));
    }
    
    public void setText(String text) {
        cText.setText(text);
    }
    
    public void actionPerformed(ActionEvent e) {
        dispose();
    }
    
    public Dimension getPreferredSize() {
        Insets i = getInsets();
        return new Dimension(i.left + 440 + i.right, i.top + 440 + i.bottom);
    }
    
    public void doLayout() {
        Dimension size = getSize();
        Dimension d1; Component c1;
        Insets i = getInsets();
        d1 = (c1 = getComponent(1)).getPreferredSize();
        c1.setBounds(i.left, size.height - d1.height - i.bottom, size.width - i.left - i.right, d1.height);
        getComponent(0).setBounds(i.left, i.top + 198, size.width - i.left - i.right, size.height - d1.height - 198 - i.top - i.bottom);
    }
    
    public void paint(Graphics g) {
        Insets i = getInsets();
        g.drawImage(GlobalCache.ImageCache.LOGO, i.left, i.top, this);
        super.paint(g);
    }
}
