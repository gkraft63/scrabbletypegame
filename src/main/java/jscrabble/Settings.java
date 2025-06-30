package jscrabble;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Settings {
    //public static final char[] ALPHABET = " a�bc�de�fghijkl�mn�o�prs�tuwyz��".toCharArray();
    
    public static final int RACK_SIZE = 8;
    public static final int BOARD_SIZE = 15;
    public static final int GAME_MAX_SEATS = 4;
    public static final int PIECE_WIDTH = 28;
    public static final int DIC_MATCH_THRESHOLD = 10000;
    public static final String VERSION = "5.0";
    // Disable integrity checks in PRO mode
    public static final boolean CHECK_PACKAGE_INTEGRITY = false;
    // Switch to professional mode
    //public static final int INTEGRITY_CHECK_NUMBER = 221261 / 10; // DEMO
    public static final int INTEGRITY_CHECK_NUMBER = 222140 / 10; // PRO
    public static final boolean DEMO = false;
    public static final boolean VERBOSE = false;
    //public static final long expiryPeriodInMillis = 1000*3600*24*7;
    //public static final long expiryTimeInMillis = new GregorianCalendar(2006, 10, 22)
    //        .getTimeInMillis() + expiryPeriodInMillis;
    
}
