package jscrabble;

import java.applet.Applet;
import java.applet.AudioClip;
import java.lang.reflect.Method;
import java.net.URL;

public final class RuntimeEnvironment {

    
    public static AudioClip newAudioClip(Applet applet, URL url) {
        try {
            Method m = Class.forName("java.applet.Applet").getMethod("newAudioClip", new Class[] {url.getClass()});
            return (AudioClip) m.invoke(null, new Object[] {url});
        } catch(Exception e) {
        } catch(Error e) {
        }
        if(applet != null)
            return applet.getAudioClip(url);
        return null;
    }
}
