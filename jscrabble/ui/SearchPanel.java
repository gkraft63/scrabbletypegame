package jscrabble.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;

import jscrabble.GlobalCache;
import jscrabble.Scrabble;
import jscrabble.Settings;
import jscrabble.cawt.CLabel;
import jscrabble.cawt.CList;
import jscrabble.util.ArrayList;

public class SearchPanel extends Panel implements ActionListener {
    private TextField searchText = new TextField();
    private CList list = CList.newInstance();
    private Scrabble scrabble;
    
    public SearchPanel(Scrabble scrabble) {
        super(new BorderLayout());
        setFont(GlobalCache.FontCache.ARIAL_SMALL);
        setBackground(GlobalCache.ColorCache.TABBED_PANEL_BGCOLOR);
        this.scrabble = scrabble;
        Panel top = new Panel(new GridLayout());
        top.add(new Label("Search: ", Label.RIGHT), "West");
        searchText.addActionListener(this);
        top.add(searchText, "East");
        
        add(top, "North");
        String[] intro = {"[ can use '*' and '?' metacharacters in a pattern ]"};
        list.setItems(intro);
        add(list.getScrollComponent(), "Center");
        list.setItemListener(scrabble);
    }
    
    public void actionPerformed(ActionEvent e) {
        String search = searchText.getText();
        boolean isPattern = (search.indexOf('*') >= 0 || search.indexOf('?') >= 0);
        list.removeAllItems();
        ArrayList result = scrabble.engine.match(search);
        if(result.size() == 0) {
            list.setItems(new String[] {isPattern? "[ matches not found ]" : "[ word not found ]"});
        } else {
            result.add(0, isPattern? ("[ found " + ((result.size() < Settings.DIC_MATCH_THRESHOLD)?  "": "above ") + result.size() + " matching words ]") : "[ word found ]");
            list.setItems((String[]) result.toArray(new String[result.size()]));
        }
        searchText.selectAll();
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible) {
//          field.requestFocus();
            searchText.selectAll();
        }
    }
}


