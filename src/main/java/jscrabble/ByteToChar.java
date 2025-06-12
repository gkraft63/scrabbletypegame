package jscrabble;

/*
 * iso-8859-2
 */
public class ByteToChar {
    
    private static final char[] BTC = "\200\201\202\203\204\205\206\207\210\211\212\213\214\215\216\217\220\221\222\223\224\225\226\227\230\231\232\233\234\235\236\237\240\u0104\u02D8\u0141\244\u013D\u015A\247\250\u0160\u015E\u0164\u0179\255\u017D\u017B\260\u0105\u02DB\u0142\264\u013E\u015B\u02C7\270\u0161\u015F\u0165\u017A\u02DD\u017E\u017C\u0154\301\302\u0102\304\u0139\u0106\307\u010C\311\u0118\313\u011A\315\316\u010E\u0110\u0143\u0147\323\324\u0150\326\327\u0158\u016E\332\u0170\334\335\u0162\337\u0155\341\342\u0103\344\u013A\u0107\347\u010D\351\u0119\353\u011B\355\356\u010F\u0111\u0144\u0148\363\364\u0151\366\367\u0159\u016F\372\u0171\374\375\u0163\u02D9\000\001\002\003\004\005\006\007\b\t\n\013\f\r\016\017\020\021\022\023\024\025\026\027\030\031\032\033\034\035\036\037 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\177".toCharArray();
    
    public static char convert(byte x) {
        return BTC[x + 128];
    }
    
    public static String convert(byte[] bytes) {
        int length = bytes.length;
        char[] chars = new char[length];
        char[] table = BTC;
        for(int i = 0; i < length; i++)
            chars[i] = table[bytes[i] + 128];
        
        return new String(chars);
    }
}
