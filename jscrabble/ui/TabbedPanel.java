package jscrabble.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;

import jscrabble.GlobalCache;

public class TabbedPanel extends Container {
    
    private Color c1 = new Color(0x00bdb76b);
    
    private Image skin = GlobalCache.ImageCache.THEME;
    
    private int selected = -1;
    
    private Vector tabs = new Vector();
    
    public TabbedPanel() {
        setLayout(null);
    }
    
    
    public void doLayout() {
        Vector tabs = this.tabs;
        tabs.removeAllElements();
        int count = getComponentCount();
        FontMetrics fm = getFontMetrics(getFont());
        int x = 0;
        Dimension size = getSize();
        Tab tab = new Tab("");
        for(int i = 0; i < count; i++) {
            Component c = getComponent(i);
            String title = c.getName();
            tab = new Tab(title);
            int w = fm.stringWidth(title) + 22;
            tab.setBounds(x, 0, w, 28);
            tabs.addElement(tab);
            c.setBounds(10, 50, size.width - 20, size.height - 60);
            x = x + w - 10;
        }
        super.getComponent(0).setBounds(0, tab.y, tab.x + tab.width, tab.y + tab.height);
    }
    
    public Dimension getPreferredSize() {
        int ccount = getComponentCount();
        Dimension prefSize = new Dimension(100, 80);
        for(int i = 0; i < ccount; i++) {
            Dimension childSize = getComponent(i).getPreferredSize();
            if(childSize.width > prefSize.width)
                prefSize.width = childSize.width;
            if(childSize.height > prefSize.height)
                prefSize.height = childSize.height;
        }
        prefSize.width += 20;
        prefSize.height += 60;
        return prefSize;
    }
    
    
    public int getSelectedIndex() {
        return selected;
    }
    
    public void setSelectedIndex(int index) {
        Component c = getSelectedComponent();
        if(c != null)
            c.setVisible(false);
        selected = index;
        c = getSelectedComponent();
        if(c != null)
            c.setVisible(true);
        repaint();
    }
    
    public void clearSelection() {
        Component c = getSelectedComponent();
        if(c != null)
            c.setVisible(false);
        selected = -1;
        repaint();
    }
    
    
    public int getComponentCount() {
        int ccount = super.getComponentCount();
        return (ccount > 0)? ccount - 1 : ccount;
    }
    
    public Component getComponent(int n) {
        return super.getComponent(n + 1);
    }
    
    public Component[] getComponents() {
        Component[] cc = super.getComponents();
        if(cc.length > 0) {
            Component[] cc2 = new Component[cc.length - 1];
            System.arraycopy(cc, 1, cc2, 0, cc2.length);
            return cc2;
        }
        return cc;
    }
    
    
    protected final void addImpl(Component comp, Object constraints, int index) {
        if(getComponentCount() == 0)
            super.addImpl(new Glass(), null, 0);
        
        comp.setVisible(false);
        super.addImpl(comp, constraints, (index == 0)? 1 : index);
    }
    
    public void remove(int index) {
        if(selected == index)
            clearSelection();
        tabs.removeElementAt(index);
        super.remove(index);
    }
    
    public void removeAll() {
        tabs.removeAllElements();
        clearSelection();
        super.removeAll();
    }
    
    
    
    public Component getSelectedComponent() {
        int index = getSelectedIndex();
        if(index == -1)
            return null;
        return getComponent(index);
    }
    
    public void setSelectedComponent(Component comp) {
        for(int i = getComponentCount(); i > 0; )
            if(getComponent(--i) == comp) {
                setSelectedIndex(i);
                return;
            }
        throw new IllegalArgumentException("component not found in tabbed panel");
    }
    
    public void setSkin(Image skin) {
        prepareImage(skin, this);
        this.skin = skin;
        repaint();
    }
    
    public void paint(Graphics g) {
        Dimension size = getSize();
        
        Vector tabs = this.tabs;
        int tabCount = getComponentCount();
        int selected = this.selected;
        for(int i = 0; i < tabCount; i++)
            if(i != selected)
                paintTab((Tab) tabs.elementAt(i), g, false);
        Tab tab = (Tab) tabs.lastElement();
        
        g.drawImage(skin, 0, tab.y + tab.height, 10, size.height - 10,
                1, 30, 11, 40, null);
        g.drawImage(skin, 10, tab.y + tab.height, size.width - 10, size.height - 10,
                12, 30, 22, 40, null);
        g.drawImage(skin, size.width - 10, tab.y + tab.height, size.width, size.height - 10,
                23, 30, 33, 40, null);
        g.drawImage(skin, 0, size.height - 10, 10, size.height,
                1, 41, 11, 51, null);
        g.drawImage(skin, 10, size.height - 10, size.width - 10, size.height,
                12, 41, 22, 51, null);
        g.drawImage(skin, size.width - 10, size.height - 10, size.width, size.height,
                23, 41, 33, 51, null);
        g.setColor(c1);
        g.drawLine(tab.x + tab.width - 4, tab.y + tab.height, size.width - 4, tab.y + tab.height);
        
        if(selected >= 0) {
            g.setColor(getForeground());
            paintTab((Tab) tabs.elementAt(selected), g, true);
            super.paint(g);
        }
    }
    
    public Insets getInsets() {
        Insets i = super.getInsets();
        i.top += 28;
        i.bottom += 10;
        i.left += 10;
        i.right += 10;
        return i;
    }
    
    public void paintTab(Tab tab, Graphics g, boolean selected) {
        int dsrc, ddst;
        if(selected) {
            dsrc = 33;
            ddst = 2;
        } else {
            dsrc = 0;
            ddst = 0;
        }
        g.drawImage(skin, tab.x, tab.y, tab.x + 10, tab.y + tab.height,
                dsrc + 1, 1, dsrc + 11, 29, null);
        g.drawImage(skin, tab.x + 10, tab.y, tab.x + tab.width - 10, tab.y + tab.height,
                dsrc + 12, 1, dsrc + 22, 29, null);
        g.drawImage(skin, tab.x + tab.width - 10, tab.y, tab.x + tab.width, tab.y + tab.height,
                dsrc + 23, 1, dsrc + 33, 29, null);
        g.drawString(tab.title, tab.x + 11, tab.y + 3*tab.height/4 - ddst);
    }
    
    public class Tab extends Rectangle {
        
        protected String title = "";
        
        public Tab(String title) {
            this.title = title;
        }
        
    }
    
    
    private class Glass extends Component {
        
        public Glass() {
            enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        }
        
        protected void processMouseEvent(MouseEvent e) {
            if(e.getID() == MouseEvent.MOUSE_RELEASED && tabs.size() > 0) {
                int x = e.getX(), y = e.getY();
                Tab tab = (Tab) tabs.firstElement();
                if(y > tab.y && y < tab.y + tab.width) {
                    Enumeration enu = tabs.elements();
                    int index = 0;
                    while(enu.hasMoreElements()) {
                        if(((Tab) enu.nextElement()).contains(x, y)) {
                            setSelectedIndex(index);
                            break;
                        }
                        index++;
                    }
                }
            }
        }
    }
}
