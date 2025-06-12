package jscrabble;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class Lexicon extends Hashtable implements Runnable {
    
    Dictionary dictionary;
    String[][] grammarDefs;
    private InputStream in;
    
    
    public Lexicon(Dictionary dictionary) {
        super(100, 2.0f);
        this.dictionary = dictionary;
    }
    
    public String getString(String key) {
        if(grammarDefs != null && !isEmpty() && !containsKey(key)) {
            for(int i=0, size=grammarDefs.length; i<size; i++) {
                String[] ends = grammarDefs[i];
                for(int j = ends.length; --j >= 0; )
                    if(key.endsWith(ends[j])) {
                        String stem = key.substring(0, key.length() - ends[j].length());
                        String key2 = null;
                        for(int k = ends.length; --k >= 0; )
                            if(!dictionary.contains(key2 = stem + ends[k]) && key2.length() <= 15)
                                continue;
                        if(containsKey(key2))
                            return getString(key2);
                    }
            }
        }
        
        Object val = get(key);
        if(val != null)
            return key + ": " + ByteToChar.convert((byte[])val);
        return "";
    }
    
    public Object put(Object key, Object value) {
        return super.put((String)key, (byte[])value);
    }
    
    
    public void load(InputStream in) {
        this.in = in;
        Thread t = new Thread(this);
        try {
            t.setPriority(Thread.MIN_PRIORITY);
        } catch(Exception e) {}
        t.start();
    }
    
    public void run() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(in)));
            ois.readUTF();
            ois.readUTF();
            Hashtable h = (Hashtable)ois.readObject();
            String[][] grammarDefs = (String[][])ois.readObject();
            ois.close();
            
            Enumeration e = h.keys();
            while(e.hasMoreElements()) {
                Object key = e.nextElement();
                put(key, h.get(key));
            }
            this.grammarDefs = grammarDefs;
            new LicenseManager.Spy().spy();
        } catch(Exception e) {
            if(Settings.VERBOSE)
                e.printStackTrace();
        } finally {
            Support.release(ois);
        }
    }
}
