package jscrabble.cawt;

import java.awt.Component;
import java.awt.ItemSelectable;
import java.awt.List;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jscrabble.GlobalCache;

public abstract class CList implements CComponent {

    public abstract void setItems(String[] items);
    
    public abstract int getSelectedIndex();
    
    public abstract void select(int index);
    
    public void removeAllItems() {
        setItems(new String[0]);
    }
    
    public abstract void setItemListener(ItemListener l);
    
    public static CList newInstance() {
        try {
            Class.forName("java.awt.Graphics2D");
            return (CList)Class.forName("jscrabble.cawt.SwingListImpl").newInstance();
        } catch (Exception e) {
            return new BasicListImpl();
        }
    }
    
    public abstract Component getScrollComponent();
    
}

class BasicListImpl extends CList {
    private List comp;
    
    public BasicListImpl() {
        comp = new List();
        comp.setFont(GlobalCache.FontCache.ARIAL_SMALL);
    }
    
    public void setItems(String[] items) {
        comp.removeAll();
        for(int i=0, size=items.length; i<size; i++)
            comp.add(items[i]);
    }
    
    public int getSelectedIndex() {
        return comp.getSelectedIndex();
    }
    
    public void select(int index) {
        comp.select(index);
        comp.dispatchEvent(new ItemEvent(comp, ItemEvent.ITEM_STATE_CHANGED, comp.getSelectedItem(), ItemEvent.SELECTED));
    }
    
    public void setItemListener(ItemListener l) {
        comp.addItemListener(l);
    }
    
    public Component getComponent() {
        return comp;
    }
    
    public Component getScrollComponent() {
        return comp;
    }
}

class SwingListImpl extends CList {
    private JList comp;
    private JScrollPane scroll;
    
    public SwingListImpl() {
        comp = new JList();
        scroll = new JScrollPane(comp,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    public void setItems(String[] items) {
        comp.setListData(items);
        if(items.length > 1)
            comp.setSelectedIndex(1);
    }
    
    public int getSelectedIndex() {
        return comp.getSelectedIndex();
    }
    
    public void select(int index) {
        comp.setSelectedIndex(index);
    }
    
    public void setItemListener(final ItemListener l) {
        comp.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    
                    private ItemEvent e;
                    
                    public void valueChanged(ListSelectionEvent e) {
                        if(this.e == null)
                            this.e = new ItemEvent(new ItemSelectable() {
                                private Object[] buff = new Object[1];
                                public Object[] getSelectedObjects() {
                                    buff[0] = ((JList) comp).getSelectedValue();
                                    return buff;
                                }
                                public void addItemListener(ItemListener l) {}
                                public void removeItemListener(ItemListener l) {}
                            }, ItemEvent.ITEM_STATE_CHANGED, null, ItemEvent.SELECTED) {
                            
                            private static final long serialVersionUID = -7243451732605152143L;
                            
                            public Object getItem() {
                                return ((JList) comp).getSelectedValue();
                            }
                        };
                        l.itemStateChanged(this.e);
                    }
                }
        );
    }
    
    public Component getComponent() {
        return comp;
    }
    
    public Component getScrollComponent() {
        return scroll;
    }
}
