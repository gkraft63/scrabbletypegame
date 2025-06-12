package jscrabble;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import jscrabble.cawt.CLabel;
import jscrabble.cawt.CTextArea;
import jscrabble.interfaces.Collection;
import jscrabble.io.TeeInputStream;
import jscrabble.ui.AboutWindow;
import jscrabble.ui.EditorPanel;
import jscrabble.ui.JBoard;
import jscrabble.ui.ButtonPanel;
import jscrabble.ui.JImage;
import jscrabble.ui.JTurnPlayer;
import jscrabble.ui.JUsedPlates;
import jscrabble.ui.OptionPanel;
import jscrabble.ui.JPieceHolder;
import jscrabble.ui.PlayerPanel;
import jscrabble.ui.JRack;
import jscrabble.ui.SearchPanel;
import jscrabble.ui.TabbedPanel;
import jscrabble.ui.JTextImage;
import jscrabble.util.StartupIcon;

public final class Scrabble extends java.applet.Applet implements ItemListener {
    private ScrabbleGame game = new ScrabbleGame(new Player[1]);
    public final Engine engine = new Engine();
    public final JBoard jBoard = new JBoard(game.board);
    public final JRack jPlayRack = new JRack();
    public final JRack jSwapRack = new JRack();
    public final Properties properties = new Properties();
    
    private JImage boardImageUI = new JImage(GlobalCache.ImageCache.BOARD);
    private JImage playRackImageUI = new JImage(GlobalCache.ImageCache.RACK);
    private JImage throwRackImageUI = new JTextImage("exchange  *  exchange  *  exchange", GlobalCache.ImageCache.RACK, 0.5f, 0.55f);
    
    public final JPieceHolder jPieceHolder = new JPieceHolder();
    private CLabel bagLabel = CLabel.newInstance();
    private CLabel status = CLabel.newInstance();
    TabbedPanel tabbedPanel = new TabbedPanel();
    EditorPanel editorPanel;
    public final CTextArea messageText = CTextArea.newInstance();
    private JUsedPlates jUsedPlates;
    private BlankSelector blankSelector;
    public final ButtonPanel buttonPanel = new ButtonPanel();
    private Image offscreen;
    private Graphics graphics;
    private ActionAdapter actionAdapter;
    OptionPanel optionPanel;
    Window startupIcon;
    private JTurnPlayer jTurnPlayer;
    public final ScrabbleBean bean = new ScrabbleBean();
    public RankTable jRankTable;
    public final ClockTimer timer = new ClockTimer();
    
    public class ClockTimer extends Timer implements ActionListener {
        private volatile boolean pause;
        public int timeForMove;
        public int timeForGame;
        public int moveTime;
        public int gameTime;
        
        public ClockTimer() {
            super(1000, null);
            addActionListener(this);
        }

        public void setPause(boolean pause) {
            this.pause = pause;
        }
        
        public void actionPerformed(ActionEvent e) {
            if(pause)
                return;
            int moveTime = this.moveTime + ((timeForMove > 0)? -1 : 1);
            int gameTime = this.gameTime + ((timeForGame > 0)? -1 : 1);
            if(moveTime < 0 || gameTime < 0) {
                actionAdapter.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Timeout"));
            } else {
                this.moveTime = moveTime;
                this.gameTime = gameTime;
                tabbedPanel.repaint();
            }
        }
        
        public void start() {
            ScrabbleSeat currentPlayer = game.getCurrentPlayer();
            if(currentPlayer != null && currentPlayer.gameTime >= 0)
                gameTime = currentPlayer.gameTime;
            else
                gameTime = (timeForGame > 0)? timeForGame : 0;
            moveTime = (timeForMove > 0)? timeForMove : 0;
            super.start();
        }
        
        public void stop() {
            super.stop();
            ScrabbleSeat currentPlayer = game.getCurrentPlayer();
            if(currentPlayer != null) {
                currentPlayer.gameTime = gameTime;
            }
        }
    }
    
    public Scrabble() {
        try {
            setLayout(null);
            setForeground(Color.black);
            setBackground(new Color(0xFFFFEAC9));
            setFont(JavaSystem.getInstance().newFont("garamond", Font.BOLD, 18.5f));
            
            // Initialize UI components
            initializeUIComponents();
            
            // Add components to the layout
            addUIComponents();
            
            // Enable mouse events
            enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        } catch(Throwable x) {
            x.printStackTrace();
        }
    }
    
    private void initializeUIComponents() {
        // Initialize board and racks
        throwRackImageUI.setForeground(new Color(0xFF8CC6C6));
        throwRackImageUI.setFont(new Font("helvetica", Font.BOLD, 12));
        
        // Initialize labels
        bagLabel.setText("Number of plates left in the bag (" + 100 + ")");
        bagLabel.getComponent().setForeground(new Color(0xFF008080));
        
        // Initialize panels
        tabbedPanel.add(new PlayerPanel(this)).setName("Main ");
        editorPanel = new EditorPanel(this);
        editorPanel.setName("Editor ");
        optionPanel = new OptionPanel();
        optionPanel.setName("Options");
        
        // Initialize message area
        messageText.installScrollBottomFixer();
        Component c = messageText.getComponent();
        c.setBackground(Color.white);
        c.setForeground(new Color(0x00804000));
        c.setFont(GlobalCache.FontCache.ARIAL_SMALL);
        
        // Initialize status
        status.getComponent().setForeground(Color.black);
        status.getComponent().setFont(GlobalCache.FontCache.ARIAL_SMALL);
        status.setText(" ");
    }
    
    private void addUIComponents() {
        // Add main components
        add(jPieceHolder);
        add(tabbedPanel);
        tabbedPanel.add(editorPanel);
        tabbedPanel.add(optionPanel);
        tabbedPanel.add(new SearchPanel(this)).setName("Dictionary");
        
        // Add game board and racks
        add(jPlayRack);
        add(jSwapRack);
        add(playRackImageUI);
        add(throwRackImageUI);
        add(jBoard);
        add(boardImageUI);
        
        // Add labels and panels
        add(bagLabel.getComponent());
        add(buttonPanel);
        add(messageText.getScrollComponent());
        add(status.getComponent());
    }
    
    public void init() {
        if (startupIcon == null)
            startupIcon = StartupIcon.show(Support.getFrame(this));
        loadProperties();
        loadBeans();
    }
    
    private void loadProperties() {
        InputStream is = null;
        try {
            URL codeBase = getCodeBase();
            if(codeBase != null) {
                is = new URL(codeBase, "scrabble.properties").openStream();
            } else {
                is = new FileInputStream("scrabble.properties");
            }
            properties.load(is);
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            Support.release(is);
        }
    }
    
    private void loadBeans() {
        try {
            URL base = getCodeBase();
            File file = null;
            if (base != null) {
                file = new File(new File(base.getFile()), "scrabble.ejb");
            } else {
                file = new File("scrabble.ejb");
            }
            bean.setStorage(file);
            
            Class.forName("java.awt.Graphics2D");
            jRankTable = (RankTable) Class.forName("jscrabble.ui.JRankTable").newInstance();
            jRankTable.setBean(bean);
            buttonPanel.getComponent(ButtonPanel.BTN_RANK).setEnabled(true);
        } catch(ClassNotFoundException e) {
            showStatus("You are using Java 1.1: Some of game options has been disabled.");
        } catch(Exception e) {
            showStatus(e.toString());
        }
        optionPanel.setPlayers(bean.getPlayers());
    }
    
    public JUsedPlates getJUsedPlates() {
        if(jUsedPlates == null)
            jUsedPlates = new JUsedPlates(this);
        return jUsedPlates;
    }
    
    public boolean isTraceUsedPlatesEnabled() {
        return jUsedPlates != null && jUsedPlates.isVisible();
    }
    
    public void hideBlankSelector() {
        if(blankSelector != null)
            blankSelector.setTarget(null, null);
    }
    
    public JTurnPlayer getJTurnPlayer() {
        if(jTurnPlayer == null)
            jTurnPlayer = new JTurnPlayer(this);
        return jTurnPlayer;
    }
    
    public void setGame(ScrabbleGame game) {
        this.game = game;
        jBoard.setModel(game.board);
        ScrabbleSeat current = game.getCurrentPlayer();
        if (current != null) {
            jPlayRack.setModel(current.playRack);
            jSwapRack.setModel(current.swapRack);
        }
        editorPanel.add(game.bag.new BagComponent(), 1);
        editorPanel.remove(2);
        editorPanel.validate();
        repaint();
    }
    
    public ActionAdapter getActionAdapter() {
        return actionAdapter;
    }
    
    public void appendMessageText(String text) {
        messageText.append(text);
    }
    
    public ScrabbleGame getGame() {
        return game;
    }
    
    public static final int DBG = 1;
    public static final int INF = 2;
    public static final int ERR = 3;
    public void showStatus(String msg) {
        status.setText(msg);
    }
    
    public void doLayout() {
        Dimension size = getSize();
        Dimension boardSize = jBoard.getPreferredSize();
        jBoard.setBounds(30, 30, boardSize.width, boardSize.height);
        Dimension playRackSize = jPlayRack.getPreferredSize();
        jPlayRack.setBounds(30, boardSize.height + 90, playRackSize.width, playRackSize.height);
        jSwapRack.setBounds(30 + playRackSize.width + 30, boardSize.height + 90, playRackSize.width, playRackSize.height);
        jPieceHolder.setSize(jPieceHolder.getPreferredSize());
        
        boardImageUI.setBounds(30 - 19, 30 - 19, 461, 489);
        playRackImageUI.setBounds(30 - 13, boardSize.height + 90 - 4, 251, 46);
        throwRackImageUI.setBounds(30 + playRackSize.width + 30 - 13, boardSize.height + 90 - 4, 251, 46);
        
        //playerSequenceBounds.setBounds(boardSize.width + 30 + 40, 20, 300, 300);
        
//      sackCollection.setSize(sackCollection.getPreferredSize());
//      sackCollection.setLocation(boardSize.width + 30 + 40, 220);
        
        Dimension d;
        bagLabel.getComponent().setSize(d = bagLabel.getComponent().getPreferredSize());
        bagLabel.getComponent().setLocation(30 + (boardSize.width - d.width)/2, boardSize.height + playRackSize.height + 102);
        
        tabbedPanel.setSize(d = tabbedPanel.getPreferredSize());
        tabbedPanel.setLocation(boardSize.width + 30 + 30, 11);
        
        d = buttonPanel.getPreferredSize();
        buttonPanel.setBounds(812, 10, 106, d.height);
        
        //int x, y = d.height;
        
        messageText.getScrollComponent().setBounds(485, 376, 322, 125);
        
        d = status.getComponent().getPreferredSize();
        status.getComponent().setBounds(10, size.height - d.height - 6, size.width - 60, d.height);
        if(offscreen != null) {
            if(size.width != offscreen.getWidth(null) || size.height != offscreen.getHeight(null)) {
                graphics.dispose();
                offscreen.flush();
                graphics = (offscreen = createImage(size.width, size.height)).getGraphics();
            }
        }
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(920, 600);
    }
    
    public void update(Graphics g) {
        paint(g);
    }
    
    public void paint(Graphics g) {
        if(graphics == null) {
            Dimension size = getSize();
            graphics = (offscreen = createImage(size.width, size.height)).getGraphics();
            g.setClip(null);
        }
        if(actionAdapter == null)
            buttonPanel.setActionListener(actionAdapter = new ActionAdapter(this));
        
        Graphics g2 = graphics;
        if(g2 != null) {
            g2.setClip(g.getClip());
            Dimension d = getSize();
            g2.setColor(getBackground());
            g2.fillRect(0, 0, d.width, d.height);
            Util.getInstance().paintChildren(this, g2);
        }
        g.drawImage(offscreen, 0, 0, this);
        getToolkit().sync();
    }
    
    public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            Object[] objs = e.getItemSelectable().getSelectedObjects();
            if(objs == null || objs.length == 0 || objs[0] == null)
                return;
            String item = (String) objs[0];
            int i = item.indexOf(' ');
            showStatus(engine.lexicon.getString((i < 0)? item : item.substring(0, i)));
        }
    }

    protected void processMouseEvent(MouseEvent e) {
        int id = e.getID();
        ScrabblePiece piece;
        if(id == MouseEvent.MOUSE_PRESSED || id == MouseEvent.MOUSE_RELEASED) {
            int x = e.getX(), y = e.getY();
            
            Point targetLocation = new Point();
            Collection target = findCollectionAt(x, y, targetLocation);
            
            if(id == MouseEvent.MOUSE_PRESSED) {
                if(target != null) {
                    piece = target.getPieceAt(x-targetLocation.x, y-targetLocation.y);
                    if(piece != null && jPieceHolder.getPiece() == null) {
                        target.removePiece(piece);
                        jPieceHolder.handle(piece, x-targetLocation.x, y-targetLocation.y, x, y, target);
                    } else if(piece == null) {
                        if(target == jPlayRack) {
                            jBoard.getModel().moveToRack(jPlayRack.getModel());
                            jSwapRack.getModel().moveToRack(jPlayRack.getModel());
                        } else if(target == jSwapRack) {
                            piece = jPlayRack.getModel().removeFirst();
                            jSwapRack.getModel().addPiece(piece);
                        } else if(target == jBoard && !jBoard.containsPieceAt(x-targetLocation.x, y-targetLocation.y)) {
                            piece = jPlayRack.getModel().removeFirst();
                            jBoard.setPieceAt(x-targetLocation.x, y-targetLocation.y, piece);
                        } else return;
                        
                        updateView();
                    }
                }
            } else { // MouseEvent.MOUSE_RELEASED
                
                piece = jPieceHolder.getPiece();
                if(piece != null)
                if(jPieceHolder.drop(target, x-targetLocation.x, y-targetLocation.y)) {
                    updateView();
                    if(piece != null && piece instanceof ScrabblePiece.BlankPiece) {
                        if(target instanceof ScrabbleBag.BagComponent) {
                            if(blankSelector != null)
                                blankSelector.setTarget(null, null);
                        } else {
                            if(blankSelector == null)
                                blankSelector = new BlankSelector(this);
                            blankSelector.setTarget((ScrabblePiece.BlankPiece)piece, target);
                        }
                    }
                }
            }
        }
    }
    
    public void updateView() {
        int score = jBoard.getModel().getTurnScore();
        ScrabbleSeat currentPlayer = game.getCurrentPlayer();
        if(currentPlayer != null)
            currentPlayer.turnScore = score;
        Container buttonPanel = this.buttonPanel;
        if(editorPanel != null && !editorPanel.isVisible()) {
            buttonPanel.getComponent(ButtonPanel.BTN_PLAY).setEnabled(score >= 0);
            buttonPanel.getComponent(ButtonPanel.BTN_PASS).setEnabled(currentPlayer != null);
            int count = jSwapRack.getModel().getNumPieces();
            buttonPanel.getComponent(ButtonPanel.BTN_SWAP).setEnabled(count > 0 && count <= game.bag.getNumPieces());
        }
        repaint();
    }
    
    protected void processMouseMotionEvent(MouseEvent e) {
        if(e.getID() == MouseEvent.MOUSE_DRAGGED && jPieceHolder.getPiece() != null)
            jPieceHolder.setLocation(e.getX() - jPieceHolder.getHotSpotX(), e.getY() - jPieceHolder.getHotSpotY());
    }

    private Collection findCollectionAt(int x, int y, Point location) {
        if(location == null)
            location = new Point();
        else
            location.setLocation(0, 0);
        Component comp = this;
        Component comp2;
        Rectangle bounds;
        loop:
            while(comp != null) {
                if(comp instanceof Collection)
                    return (Collection)comp;
                else if(comp instanceof Container) {
                    Container cont = (Container)comp;
                    int ccount = cont.getComponentCount();
                    for(int i = 0; i < ccount; i++) {
                        if(((comp2 = cont.getComponent(i)) instanceof Collection || comp2 instanceof Container) && (bounds = comp2.getBounds()).contains(x - location.x, y - location.y) && comp2.isVisible()) {
                            location.translate(bounds.x, bounds.y);
                            comp = comp2;
                            continue loop;
                        }
                    }
                    return null;
                } else return null;
            }
        return null;
    }
    
    public static void main(String[] args) {
        Frame frame = new Frame(Settings.DEMO ? "JScrabble - DEMO": "JScrabble PROFESSIONAL - UNREGISTERED");
        Window startupIcon = StartupIcon.show(frame);
        Scrabble scrabble = new Scrabble();
        scrabble.startupIcon = startupIcon;
        scrabble.init();
        Support.setExitOnClose(frame);
        frame.setIconImage(GlobalCache.ImageCache.SMALL_ICON);
        
        //scrabble.startup = StartupIcon.open(f);
        frame.add(scrabble);
        frame.setResizable(false);
        frame.pack();
        Support.centerOnScreen(frame, null);
        frame.setVisible(true);
    }

    public void loadDictionary(String dictname) {
        InputStream is = null;
        URL base = getCodeBase();
        try {
            if(base == null) {
                File file = new File(dictname);
                messageText.append("Loading dictionary: "+file.getAbsolutePath()+"\n");
                is = new FileInputStream(file);
                
            } else { //in an applet
                File file = Support.findCachedFile(dictname);
                if(file != null) {
                    messageText.append("Loading dictionary: "+file.getAbsolutePath()+"\n");
                    is = new FileInputStream(file);
                    try {
                        engine.loadDictionary(is);
                        messageText.append("Dictionary loaded.\n");
                        return;
                        
                    } catch(Exception e) {
                    } finally {
                        Support.release(is);
                    }
                }
                URL url = new URL(base, dictname);
                messageText.append("Loading dictionary: "+url+"\n");
                is = url.openStream();
                File cache = Support.openCachedFile(dictname);
                if(cache != null) {
                    is = new TeeInputStream(is, new FileOutputStream(cache));
                }
            }
            engine.loadDictionary(is);
            messageText.append("Dictionary loaded.\n");
            
        } catch(Throwable x) {
            messageText.append("Error while loading dictionary: "+x+"\n");
        } finally {
            Support.release(is);
        }
    }
    
    public void loadLexicon(String dictname) {
        InputStream is = null;
        URL base = getCodeBase();
        try {
            if(base == null) {
                is = new FileInputStream(new File(dictname));
                
            } else { //in an applet
                File file = Support.findCachedFile(dictname);
                if(file != null) {
                    is = new FileInputStream(file);
                    try {
                        engine.lexicon.load(is);
                        return;
                        
                    } catch(Exception e) {
                    } finally {
                        Support.release(is);
                    }
                }
                is = new URL(base, dictname).openStream();
                File cache = Support.openCachedFile(dictname);
                if(cache != null) {
                    is = new TeeInputStream(is, new FileOutputStream(cache));
                }
            }
            engine.lexicon.load(is);
            
        } catch(Throwable x) {
        }
    }
    
    public URL getCodeBase() {
        try {
            return super.getCodeBase();
        } catch(Exception e) {
            return null;
        }
    }
    
    public void updateBagNumPieces() {
        bagLabel.setText("Number of plates left in the bag (" + game.bag.getNumPieces() + ")");
    }
    
    public void popupExpirationDialog() {
        String a1 = Support.decrypt(new long[] {
                3910890869079067218L,
                -8759933838492957530L,
                -6429286259282766282L,
                -7014684054116207104L,
            }
        );
        String a2 = Support.decrypt(new long[] {
                -7586711363508627926L,
                3928949384240604718L,
                3315906027105191598L,
                5909961817842955926L,
                -6469906545956174134L,
                -6454215775549775356L,
                -8784829114149843378L,
                -6463063427842177842L,
                -8789717958595939290L,
                -7625008931609899506L,
                303490046760351478L,
                -6426928903100258746L,
                5689740086531617830L,
                5045428160062989862L,
                303490147557875374L,
                301396676360687118L,
                321715550970357358L,
                301370597037741806L,
                -7625009584436505034L,
                -7579955723546757386L,
                1586575687770047534L,
                -1229776317871935954L,
                5081835845306447086L,
                8371811785021732422L,
                -664839157809138162L,
                -7571109324617535882L,
                -714332163784042962L,
                8380635956583071744L,
            });
        AboutWindow win = new AboutWindow(this);
        win.setTitle(a1);
        win.setText("The limited evaluation time of JScrabble DEMO version has expired. The application will be closed. In order to buy full product version without limitations visit web site http://scrabble.net.pl/en/ or contact with author.\n\nhttp://scrabble.net.pl/en/\nbernacek@gmail.com");
        win.setModal(true);
        win.setVisible(true);
        
        try {
            System.exit(0);
        } catch (Exception e) {
            setEnabled(false);
            setVisible(false);
        }
    }
}
