package jscrabble.tools;

import java.io.*;
import java.util.ArrayList;

public class TWLFileConverter {

    public static void main(String[] args) {
        String line = null;
        Writer out = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader("f:/scrabble/en/TWLdefs.txt"));
            out = new FileWriter("f:/scrabble/en/znaczenia.txt");
            while((line = in.readLine()) != null)
                if(line.length() > 0)
                    processLine(line, out);
            in.close();
            out.close();
        } catch (Exception e) {
            try {
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println(line);
            e.printStackTrace();
        }
    }
    
    public static void processLine(String line, Writer out) throws IOException {
        int pos = line.indexOf('\t');
        String head = line.substring(0, pos);
        String tail = line.substring(pos+1);
        String[] words = head.split(" ");
        pos = tail.indexOf(' ');
        if(!tail.startsWith(tail.substring(0, pos).toUpperCase()))
            System.err.println(tail);
        tail = tail.substring(pos+1);
        for(int i=0; i<words.length; i++)
            out.write(words[i]+": "+tail+"\n");
    }
}
