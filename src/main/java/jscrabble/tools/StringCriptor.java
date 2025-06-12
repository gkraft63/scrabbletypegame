package jscrabble.tools;

import java.io.UnsupportedEncodingException;

import jscrabble.ByteToChar;
import jscrabble.Support;

public class StringCriptor {

    public static long[] encrypt(String s) {
        try {
            byte[] bytes = s.getBytes("iso-8859-2");
            long[] result = new long[1 + (bytes.length-1)/8];
            int i = 0;
            while(i < bytes.length) {
                long chunk = 0;
                for(int j = 0; j < 8 && i < bytes.length; i++, j++)
                    chunk = (chunk << 8) + ((int)bytes[i] & 0xFF);
                result[((i - 1)>>3)] = Long.reverse(chunk);
            }
            return result;
            
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    
    public static String decrypt(long[] chunks) {
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
    
    public static void main(String[] args) {
        long[] chunks = encrypt("Mariusz Bernacki");
        
        StringBuffer buf = new StringBuffer();
        buf.append("Support.decrypt(new long[] {").append('\n');
        for(int i = 0; i < chunks.length; i++)
            buf.append("    ").append(chunks[i]).append("L,\n");
        buf.append("});").append('\n');
        System.out.println(buf.toString());
        
        String a1 = Support.decrypt(new long[] {
                3910890869079067218L,
                -3556041647705160538L,
                -5475191295826290994L,
                -3556059583480439292L,
                357603542329347630L,
                326007559867596800L,
        }
        );
        System.out.println(a1);
        System.out.println(decrypt(chunks));
    }
}
