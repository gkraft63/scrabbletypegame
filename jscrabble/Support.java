package jscrabble;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Random;

public final class Support {
    private static Random random = new Random(Settings.INTEGRITY_CHECK_NUMBER);
    static {
        if(Settings.CHECK_PACKAGE_INTEGRITY)
            PackageIntegrity.check();
    }
    
    private static DateFormat dateFormat;
    public static String getToday() {
        if(dateFormat == null)
            dateFormat = new SimpleDateFormat("d MMM yyyy  H:mm");
        return dateFormat.format(new java.util.Date());
    }

    public static Random getRandom() {
        return random;
    }
    
    public static String reverse(String string) {
        StringBuffer buf = new StringBuffer(string);
        buf.reverse();
        return buf.toString();
    }

    public static final Frame getFrame(Component child) {
        while(child != null) {
            if(child instanceof Frame)
                return (Frame) child;
            child = child.getParent();
        }
        return null;
    }
    
    public static final Scrabble getScrabble(Component child) {
        while(child != null) {
            if(child instanceof Scrabble)
                return (Scrabble) child;
            child = child.getParent();
        }
        return null;
    }
    
    public static void centerOnScreen(Window win, Component owner) {
        Rectangle screen = new Rectangle();
        if(owner == null)
            screen.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        else {
            screen.setSize(owner.getSize());
            screen.setLocation(owner.getLocationOnScreen());
        }
        Dimension windim = win.getSize();
        win.setLocation(screen.x + (screen.width - windim.width)/2, screen.y + (screen.height - windim.height)/2);
    }

    private static WindowListener hideOnCloseAdapter;
    
    public static void setHideOnClose(Window window) {
        if(hideOnCloseAdapter == null) {
            hideOnCloseAdapter = new WindowAdapter() {
                
                public void windowClosing(WindowEvent e) {
                    e.getWindow().setVisible(false);
                }
                
            };
        }
        window.addWindowListener(hideOnCloseAdapter);
    }
    
    private static WindowListener exitOnCloseAdapter;
    
    public static void setExitOnClose(Window window) {
        if(exitOnCloseAdapter == null) {
            exitOnCloseAdapter = new WindowAdapter() {
                
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
                
            };
        }
        window.addWindowListener(exitOnCloseAdapter);
    }
    
    private static WindowListener disposeOnCloseAdapter;
    
    public static void setDisposeOnClose(Window window) {
        if(disposeOnCloseAdapter == null) {
            disposeOnCloseAdapter = new WindowAdapter() {
                
                public void windowClosing(WindowEvent e) {
                    e.getWindow().dispose();
                }
                
            };
        }
        window.addWindowListener(disposeOnCloseAdapter);
    }
    
    public static final File findCachedFile(String filename) {
        try {
            String home = System.getProperty("user.home");
            File dir = new File(home, ".jscrabble");
            if(dir.exists()) {
                File file = new File(dir, filename);
                if(file.exists())
                    return file;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static final File openCachedFile(String filename) {
        try {
            String home = System.getProperty("user.home");
            File dir = new File(home, ".jscrabble");
            if(!dir.exists() && !dir.mkdir())
                return null;
                
            File file = new File(dir, filename);
            new FileOutputStream(file).close();
            return file;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Shared instance of Media Tracker for getImage(String) method.
     *
     */
    private static MediaTracker tracker = new MediaTracker(new Component() {});

    /**
     * Creates the image from the specified file. If no file with the given name
     * can be found the method returns null. The images are preloaded using
     * MediaTracker.
     * 
     */
    public static Image getImage(String name) {
        java.net.URL url = Support.class.getResource(name);
        if(url != null) {
            Image image = Toolkit.getDefaultToolkit().getImage(url);
            if(image != null) {
                synchronized(tracker) {
                    tracker.addImage(image, 0);
                    try {
                        tracker.waitForAll();
                    } catch (InterruptedException e) {
                    }
                    tracker.removeImage(image);
                }
                return image;
            }
        }
        return null;
    }
    
    public static void release(InputStream is) {
        try {
            if(is != null) is.close();
        } catch (IOException e) {
        }
    }
    
    public static void release(OutputStream os) {
        try {
            if(os != null) os.close();
        } catch (IOException e) {
        }
    }
    
    public static String formatSimpleTime(int secs) {
        int val;
        StringBuffer sb = new StringBuffer(6);
        if((val = secs/60) < 10)
            sb.append('0');
        sb.append(val).append(':');
        if((val = secs%60) < 10)
            sb.append('0');
        sb.append(val);
        return sb.toString();
    }
    
    static final class PackageIntegrity extends ClassLoader {
        static void check() {
            try {
                URL url = Scrabble.class.getResource("/jscrabble/Scrabble.class");
                URLConnection conn = url.openConnection();
                Method meth = conn.getClass().getMethod("getURL", new Class[0]);
                String urlString = meth.invoke(conn, new Object[0]).toString();
                int exclIdx = urlString.lastIndexOf('!');
                if (exclIdx == -1) {
                    // URL format not as expected, skip integrity check
                    return;
                }
                URLConnection urlc = new URL(urlString.substring(4, exclIdx)).openConnection();
                int len = urlc.getContentLength() / 10;
                if(len > 0 && new java.util.Random(len).nextInt() != Support.getRandom().nextInt()) {
                    PackageIntegrity pack = new PackageIntegrity();
                    String s = "\312\376\272\276\000\003\000\055\000\033\012\000\007\000\023\011\000\006\000\024\007\000\025\010\000\026\012\000\003\000\027\007\000\030\007\000\031\001\000\005\145\162\162\157\162\001\000\030\114\152\141\166\141\057\154\141\156\147\057\114\151\156\153\141\147\145\105\162\162\157\162\073\001\000\006\074\151\156\151\164\076\001\000\003\050\051\126\001\000\004\103\157\144\145\001\000\017\114\151\156\145\116\165\155\142\145\162\124\141\142\154\145\001\000\010\164\157\123\164\162\151\156\147\001\000\024\050\051\114\152\141\166\141\057\154\141\156\147\057\123\164\162\151\156\147\073\001\000\010\074\143\154\151\156\151\164\076\001\000\012\123\157\165\162\143\145\106\151\154\145\001\000\022\114\151\156\153\145\144\114\151\142\162\141\162\171\056\152\141\166\141\014\000\012\000\013\014\000\010\000\011\001\000\046\152\141\166\141\057\154\141\156\147\057\111\156\143\157\155\160\141\164\151\142\154\145\103\154\141\163\163\103\150\141\156\147\145\105\162\162\157\162\001\000\020\160\141\143\153\141\147\145\040\123\143\162\141\142\142\154\145\014\000\012\000\032\001\000\015\114\151\156\153\145\144\114\151\142\162\141\162\171\001\000\020\152\141\166\141\057\154\141\156\147\057\117\142\152\145\143\164\001\000\025\050\114\152\141\166\141\057\154\141\156\147\057\123\164\162\151\156\147\073\051\126\000\041\000\006\000\007\000\000\000\001\000\012\000\010\000\011\000\000\000\003\000\001\000\012\000\013\000\001\000\014\000\000\000\035\000\001\000\001\000\000\000\005\052\267\000\001\261\000\000\000\001\000\015\000\000\000\006\000\001\000\000\000\002\000\001\000\016\000\017\000\001\000\014\000\000\000\034\000\001\000\001\000\000\000\004\262\000\002\277\000\000\000\001\000\015\000\000\000\006\000\001\000\000\000\007\000\010\000\020\000\013\000\001\000\014\000\000\000\045\000\003\000\000\000\000\000\015\273\000\003\131\022\004\267\000\005\263\000\002\261\000\000\000\001\000\015\000\000\000\006\000\001\000\000\000\004\000\001\000\021\000\000\000\002\000\022";
                    Class lib = pack.defineClass("LinkedLibrary", s.getBytes("iso-8859-1"), 0, s.length());
                    pack.resolveClass(lib);
                    Method m = Class.forName(lib.newInstance().toString()).getMethod("init", new Class[0]);
                    
                    // not reached due to throwing IncompatibleClassChangeError("package Scrabble")
                    Scrabble scrabble = (Scrabble) m.invoke(lib.newInstance(), new Object[0]);
                    scrabble.init();
                    scrabble.start();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static final String decrypt(long[] chunks) {
        StringBuffer buf = new StringBuffer();
        byte x;
        for(int k = 0; k < chunks.length; k++) {
            long i = chunks[k];
            i = (i & 0x5555555555555555L) << 1 | (i >>> 1) & 0x5555555555555555L;
            i = (i & 0x3333333333333333L) << 2 | (i >>> 2) & 0x3333333333333333L;
            i = (i & 0x0f0f0f0f0f0f0f0fL) << 4 | (i >>> 4) & 0x0f0f0f0f0f0f0f0fL;
            i = (i & 0x00ff00ff00ff00ffL) << 8 | (i >>> 8) & 0x00ff00ff00ff00ffL;
            i = (i << 48) | ((i & 0xffff0000L) << 16) |
                ((i >>> 16) & 0xffff0000L) | (i >>> 48);
            
            for(int j = 56; j >= 0; j-=8)
                if((x = (byte)((i >> j) & 0xFF)) != 0)
                    buf.append(ByteToChar.convert(x));
            
            if((i >> 56) == 0)
                break;
        }
        return buf.toString();
    }
    
    public static long generateKey(String string) {
        long l = 1;
        for(int i = 0; i < string.length(); i++)
            l = l*3 + (long)string.charAt(i);
        return l;
    }
}
