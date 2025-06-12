package jscrabble.tools;

import jscrabble.Support;

public class KeyGenerator {

    public static final String UserName = "Mariusz Bernacki";
    
    public static void main(String[] args) {
        long l = Support.generateKey(UserName);
        l = ((l) ^ (l >> 2));
        System.out.println("Wygenerowany Klucz-Licencji: "+Long.toString(l, 16));
    }
}
