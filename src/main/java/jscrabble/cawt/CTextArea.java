package jscrabble.cawt;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Label;
import java.awt.List;
import java.awt.TextArea;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import jscrabble.JavaSystem;

public abstract class CTextArea implements CComponent {

    public abstract String getText();
    
    public abstract void setText(String text);
    
    public abstract void append(String text);
    
    public static CTextArea newInstance() {
        try {
            Class.forName("java.awt.Graphics2D");
            return (CTextArea)Class.forName("jscrabble.cawt.SwingTextAreaImpl").newInstance();
        } catch (Exception e) {
            return new BasicTextAreaImpl();
        }
    }
    
    public abstract Component getScrollComponent();
    
    public abstract void installScrollBottomFixer();
}

class BasicTextAreaImpl extends CTextArea {
    private TextArea comp;
    
    public BasicTextAreaImpl() {
        comp = new TextArea("", 0, 0, TextArea.SCROLLBARS_VERTICAL_ONLY);
        comp.setEditable(false);
    }
    
    
    public String getText() {
        return comp.getText();
    }
    
    public void setText(String text) {
        comp.setText(text);
    }
    
    public void append(String text) {
        comp.append(text);
    }
    
    public Component getComponent() {
        return comp;
    }
    
    public void installScrollBottomFixer() {
    }
    
    public Component getScrollComponent() {
        return comp;
    }
}

class SwingTextAreaImpl extends CTextArea implements ComponentListener {
    private JTextArea jcomp;
    private JScrollPane scroll;
    
    public SwingTextAreaImpl() {
        JTextArea jt = new JTextArea();
        jt.setEditable(false);
        jt.setWrapStyleWord(true);
        jt.setLineWrap(true);
        jt.setMargin(new Insets(2, 2, 2, 2));
        jcomp = jt;
        scroll = new JScrollPane(jcomp,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    public String getText() {
        return jcomp.getText();
    }
    
    public void setText(String text) {
        jcomp.setText(text);
    }
    
    public void append(String text) {
        jcomp.append(text);
    }
    
    /**
     * Invoked when the component's size changes.
     */
    public void componentResized(ComponentEvent e) {
        ((JScrollPane) scroll).getVerticalScrollBar().setValue(Integer.MAX_VALUE);
    }
    public void componentMoved(ComponentEvent e) {}
    public void componentShown(ComponentEvent e) {}
    public void componentHidden(ComponentEvent e) {}
    
    
    public Component getComponent() {
        return jcomp;
    }
    
    public void installScrollBottomFixer() {
        jcomp.addComponentListener((ComponentListener)this);
    }
    
    public Component getScrollComponent() {
        return scroll;
    }
}
