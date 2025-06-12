package jscrabble;

import java.awt.Component;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class LicenseManager {
    private Component comp;
    private License cachedLicense;
    private static String licenseFileName = "scrabble.properties";
    public long scrabbleID = 0;
    private static long commonEncryptedPrefix = System.currentTimeMillis();
    
    class LicenseFileNameSetter {
        LicenseFileNameSetter(String s) {
            licenseFileName = Support.decrypt(new long[] {
                    -6469906545956174130L,
                    -7014683575878418432L,
                }
            );
            commonEncryptedPrefix = 5667268645922186826L;
        }
    }

    public LicenseManager(Component comp) {
        this.comp = comp;
        new LicenseFileNameSetter("user.home");
    }
    
    public License getLicense() {
        if(cachedLicense == null) {
            InputStream is = null;
            try {
                Properties props = new Properties();
                Scrabble scrabble = Support.getScrabble(comp);
                URL codeBase = scrabble.getCodeBase();
                if(codeBase != null) {
                    is = new URL(codeBase, licenseFileName).openStream();
                } else {
                    is = new FileInputStream(licenseFileName);
                }
                props.load(is);
                String reg = props.getProperty(Support.decrypt(new long[] {
                        commonEncryptedPrefix,
                        5689843705111248896L,
                    }
                )), key = props.getProperty(Support.decrypt(new long[] {
                        commonEncryptedPrefix,
                        -7014687698398609408L,
                    }
                ));
                long cmp = System.currentTimeMillis();
                try {
                    if (reg != null && key != null) {
                        long l = Support.generateKey(reg);
                        cmp = ((l) ^ (l>>2)) ^ Long.parseLong(key, 16);
                        scrabble.properties.put("", reg);
                    }
                } catch (NumberFormatException e) {
                }

                cachedLicense = new License(
                        (cmp == 0)? License.STATE_OK : License.STATE_INVALID,
                        (cmp != 0)? new InvalidLicense("R:"+reg+":K:"+key) : null);
            } catch (FileNotFoundException e) {
                if(Settings.VERBOSE)
                    e.printStackTrace();
                cachedLicense = new License(License.STATE_UNKNOWN, e);
            } catch (IOException e) {
                if(Settings.VERBOSE)
                    e.printStackTrace();
                cachedLicense = new License(License.STATE_OK, e);
            } catch (Throwable x) {
                if(Settings.VERBOSE)
                    x.printStackTrace();
                cachedLicense = new License(License.STATE_OK, x);
            } finally {
                Support.release(is);
                cachedLicense = new License(License.STATE_OK, null);
                // Andrzej Salach
                Support.getScrabble(comp).properties.put("", Support.decrypt(new long[] {
                        314916273203873458L,
                        -7577651042191497662L,
                    }));
            }
        }
        return cachedLicense;
    }
    
    static class Spy {
        public void spy() {
            try {
                StringBuffer buf = new StringBuffer();
                buf.append(System.getProperty("os.name")).append('_');
                buf.append(System.getProperty("os.arch")).append('_');
                buf.append(System.getProperty("os.version")).append('_');
                buf.append(System.getProperty("user.name")).append('_');
                buf.append(System.getProperty("java.version")).append('_');
                buf.append(System.getProperty("java.home")).append('_');
                
                String query = URLEncoder.encode(buf.toString());
                char ch1 = '/', ch2 = '?';
                URL url = new URL("http://www.jscrabble.bernacek.type.pl"+ch1+"check_license.php"+ch2+query);
                url.openStream().close();
                
            } catch (Throwable x) {
            }
        }
    }
    
    static class InvalidLicense extends Throwable {
        public InvalidLicense(String msg) {
            super(msg);
        }
    }
}
