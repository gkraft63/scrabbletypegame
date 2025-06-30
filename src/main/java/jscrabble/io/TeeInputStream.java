package jscrabble.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream extends FilterInputStream {

    // SKIP_BUFFER_SIZE is used to determine the size of skipBuffer
    private static final int SKIP_BUFFER_SIZE = 2048;
    // skipBuffer is initialized in skip(long), if needed.
    private static byte[] skipBuffer;
    
    private OutputStream os;
    
    public TeeInputStream(InputStream is, OutputStream os) {
        super(is);
        this.os = os;
    }

    public int read() throws IOException {
        int x = in.read();
        try {
            os.write(x);
        } catch(Exception e) {
        }
        return x;
    }
    
    public int read(byte b[], int off, int len) throws IOException {
        int count = in.read(b, off, len);
        try {
            os.write(b, off, count);
        } catch(Exception e) {
        }
        return count;
    }

    
    
    public long skip(long n) throws IOException {
        long remaining = n;
        int nr;
        if(n <= 0)
            return 0;
        
        if(skipBuffer == null)
            skipBuffer = new byte[SKIP_BUFFER_SIZE];
        
        byte[] localSkipBuffer = skipBuffer;
        
        while(remaining > 0) {
            nr = read(localSkipBuffer, 0, (int) Math.min(SKIP_BUFFER_SIZE, remaining));
            if(nr < 0) {
                break;
            } else {
                try {
                    os.write(localSkipBuffer, 0, nr);
                } catch(Exception e) {
                }
            }
            remaining -= nr;
        }
        
        return n - remaining;
    }

    public void close() throws IOException {
        os.close();
        in.close();
    }

    public boolean markSupported() {
        return false;
    }
}
