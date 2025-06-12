package jscrabble.ui;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

import jscrabble.RankTable;
import jscrabble.ScrabbleBean;
import jscrabble.Support;


public class JRankTable implements RankTable {
    static final String[] lpSym = {"1.", "2.", "3.", "4.", "5.", "6.", "7.", "8.", "9.", "10."};
    static final Font plainFont = new Font("dialog", Font.PLAIN, 12);
    static final Font boldFont = new Font("dialog", Font.BOLD, 12);
    static final String[] columnNames = new String[] {"No", "Player", "Points", "Date", "Opponent"};
    JDialog dialog;
    JTable table;
    AbstractTableModel model;
    Object[][] cells = new Object[0][0];
    int currRank = -1;
    // data source
    ScrabbleBean bean;
    
    
    public JRankTable() {
        model = new AbstractTableModel() {
            public int getRowCount() {
                return cells.length;
            }
            public int getColumnCount() {
                return columnNames.length;
            }
            public String getColumnName(int index) {
                return columnNames[index];
            }
            public Object getValueAt(int row, int col) {
                return cells[row][col];
            }
            
        };
    }
    
    public void setBean(ScrabbleBean bean) {
        cells = (this.bean = bean).getRankCells();
        model.fireTableDataChanged();
    }
    
    public boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }
    
    public void setShowing(boolean show, Component frameResolver) {
        if(isShowing() == show)
            return;
        if(show) {
            JDialog dialog = this.dialog = new JDialog(Support.getFrame(frameResolver));
            dialog.setTitle("Top 10 Rank");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.getContentPane().add(createTable());
            dialog.pack();
            Insets i = dialog.getInsets();
            dialog.setSize(500, i.top + table.getRowHeight()*11 + i.bottom + 5);
            dialog.show();
        } else {
            dialog.removeAll();
            dialog.dispose();
            dialog = null;
            table = null;
        }
    }
    
    public void addRank(String player, int score, String opponent) {
        Object[][] cells = this.cells;
        int pos = getInsertionPoint(cells, score), rows = cells.length;
        if(pos < 0 && rows >= 10)
            return;
        if(rows < 10) {
            Object[][] newCells = new Object[rows+1][];
            System.arraycopy(cells, 0, newCells, 0, rows);
            cells = newCells;
        } else {
            rows--;
        }
        Object[] row = new Object[] {lpSym[rows], player, new Integer(score), Support.getToday(), "vs " + opponent};
        if(pos < 0) {
            cells[rows] = row;
        } else {
            for(int i=rows; i>pos; i--) {
                cells[i] = cells[i-1];
                cells[i][0] = lpSym[i];
            }
            row[0] = lpSym[pos];
            cells[pos] = row;
        }
        this.cells = cells;
        model.fireTableDataChanged();
        if(pos < 0)
            pos = rows;
        currRank = pos;
        bean.setRankCells(cells);
        bean.ejbStore();
    }
    
    private static int getInsertionPoint(Object[][] cells, int score) {
        for(int i=0; i<cells.length; i++)
            if(((Integer)cells[i][2]).intValue() <= score)
                return i;
        return -1;
    }
    
    JScrollPane createTable() {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setForeground((row < 3)? Color.red : Color.black);
                this.setFont(row == currRank? boldFont : plainFont);
                setHorizontalAlignment(column == 1? SwingConstants.LEFT : column == 4? SwingConstants.RIGHT : SwingConstants.CENTER);
                
                return this;
            }
        };
        JTable table = this.table = new JTable((TableModel)model);
        table.setBackground(new Color(0xFFECE9D8));
        table.setFont(plainFont);
        TableColumn col = table.getColumn("No");
        col.setResizable(false);
        col.setPreferredWidth(32);
        col.setMaxWidth(32);
        table.setDefaultRenderer(new Object().getClass(), cellRenderer);
        JScrollPane scrollpane = new JScrollPane(table);
        return scrollpane;
    }
}
