package jscrabble;
/**
 *
 * @(#) Dictionary.java      1.8    2004/08/08
 *
 *
 * Sownik ortograficzny Scrabble.
 *
 *
 *
 * @author               Mariusz  Bernacki
 * @version              1.8,  8 sierpnia 2004
 * @character-encoding   iso-8859-2
 * @since                JDK1.1
 *
 */

import java.io.*;
import java.util.Vector;

import jscrabble.util.ArrayList;

public final class Dictionary {

    /**
     * Graf sownika ortograficznego.
     * 
     */
    Node root;

    /**
     * Graf sufiksw. Zawiera wszystkie sufiksy wszystkich sw sownika.
     * 
     */
    Node suffix;

    String version = "(dictionary not loaded)";

    /**
     * Konstruktor tworzcy pusty sownik ortograficzny.
     * 
     */
    public Dictionary() {
        root = new Node();
    }

    /**
     * Metoda wczytuje zawarto swownika oraz graf infiksw z podanego adresu
     * URL.
     * 
     */
    public void readFromStream(InputStream stream) {
        new Reader().readFromStream(stream);
    }

    class Reader {
        private String alphabet;

        private int alphLen;

        private int GENOM_CODING_RANGE;

        private Vector refs;

        private int off;

        private int depth;

        public void readFromStream(InputStream stream) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new BufferedInputStream(stream));
                ois.readUTF();
                version = ois.readUTF();
                alphabet = ois.readUTF();
                alphLen = alphabet.length();
                long[] rootStamp = (long[]) ois.readObject();
                long[] suffStamp = (long[]) ois.readObject();
                GENOM_CODING_RANGE = (((1 * 2 + 1) * alphabet.length()
                        + alphabet.length() - 1)
                        * (alphabet.length() + 1) + alphabet.length());
                refs = new Vector();

                off = 0;
                root = readNode(rootStamp);
                off = 0;
                suffix = readNode(suffStamp);
            } catch (Exception e) {
                Support.release(ois);
                e.printStackTrace();
                throw new RuntimeException(
                        "DICTIONARY FILE IS CORRUPTED. CHECK ORIGIN OF THE FILE OR CONTACT WITH THE SOFTWARE AUTHOR.\nDetails: [-201]: "
                                + Util.stackTraceToString(e));
            }
        }

        private Dictionary.Node readNode(long[] stamps) {
            if (depth++ > 16)
                throw new RuntimeException(
                        "DICTIONARY FILE IS CORRUPTED. CHECK ORIGIN OF THE FILE OR CONTACT WITH THE SOFTWARE AUTHOR.\nDetails: [-202]: Building tree error:\n\tdepth = "
                                + depth + "\n");
            long stamp = (long) stamps[off];
            long modulus = (long) (refs.size() + GENOM_CODING_RANGE + 1);
            long genom = stamp % modulus;
            stamp /= modulus;
            if (stamp == 0)
                off++;
            else
                stamps[off] = stamp;
            Dictionary.Node n = stampToNode((int) genom);

            if (genom <= GENOM_CODING_RANGE) {
                int len = n.children.length;
                for (int i = 0; i < len; i++)
                    n.children[i] = readNode(stamps);
            }
            depth--;
            return n;
        }

        private Dictionary.Node stampToNode(int stamp) {
            if (stamp > GENOM_CODING_RANGE)
                return (Dictionary.Node) refs.elementAt(stamp - GENOM_CODING_RANGE - 1);
            int children = stamp % (alphLen + 1);
            stamp /= (alphLen + 1);
            Dictionary.Node node = new Dictionary.Node(alphabet.charAt(stamp % alphLen));
            stamp /= alphLen;
            node.complete = (stamp & 1) != 0;
            if ((stamp & 2) != 0) {
                refs.addElement(node);
            }

            node.children = new Dictionary.Node[children];
            return node;
        }
    }

    private static int search(char value, Node[] nodes) {

        int low = 0;
        int high = nodes.length - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Node midNode = nodes[mid];
            int cmp = midNode.value - value;

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1); // key not found.: returns -(insertion point) - 1
    }

    /**
     * Sprawdza czy podane swowo istnieje w sowniku. Jeli istnieje zwraca
     * true, w przeciwnym wypadku zwraca false.
     * 
     */
    public boolean contains(String word) {
        Node node = getNode(root, word);
        return node != null && node.complete;
    }

    /**
     * Sprawdza czy podane swowo istnieje w sowniku. Jeli istnieje zwraca
     * true, w przeciwnym wypadku zwraca false.
     * 
     */
    public boolean contains(StringBuffer word) {
        Node node = getNode(root, word);
        return node != null && node.complete;
    }

    /**
     * Sprawdza czy podany prefiks istnieje w sowniku. Jeli istnieje zwraca
     * true, w przeciwnym wypadku zwraca false.
     * 
     */
    public boolean containsPrefix(String word) {
        Node node = getNode(root, word);
        return node != null && !node.isLeaf();
    }

    /**
     * Sprawdza czy podany prefiks istnieje w sowniku. Jeli istnieje zwraca
     * true, w przeciwnym wypadku zwraca false.
     * 
     */
    public boolean containsPrefix(StringBuffer word) {
        Node node = getNode(root, word);
        return node != null && !node.isLeaf();
    }

    /**
     * Sprawdza czy podany infiks istnieje w sowniku. Jeli istnieje zwraca
     * true, w przeciwnym wypadku zwraca false. Ponisza metoda jako infiksy
     * traktuje rwnie sufiksy sw.
     * 
     */
    public boolean containsInfix(String word) {
        Node suffix = this.suffix;
        if (suffix == null)
            return true;
        return getNode(suffix, word) != null;
    }

    /**
     * Sprawdza czy podany infiks istnieje w sowniku. Jeli istnieje zwraca
     * true, w przeciwnym wypadku zwraca false. Ponisza metoda jako infiksy
     * traktuje rwnie sufiksy sw.
     * 
     */
    public boolean containsInfix(StringBuffer word) {
        Node suffix = this.suffix;
        if (suffix == null)
            return true;
        return getNode(suffix, word) != null;
    }

    /**
     * Metoda przemierza graf sownika wedug podanej cieki wierzchokw oraz
     * zwraca ostatni ga grafu, do ktrej udao si doj, Jeli graf nie
     * zawiera wskazanej cieki, zwracana jest warto null.
     * 
     */
    private static Node getNode(Node node, String path) {
        for (int i = 0; i < path.length() && node != null; i++)
            node = node.getChild(path.charAt(i));
        return node;
    }

    /**
     * Metoda przemierza graf sownika wedug podanej cieki wierzchokw oraz
     * zwraca ostatni ga grafu, do ktrej udao si doj, Jeli graf nie
     * zawiera wskazanej cieki, zwracana jest warto null.
     * 
     */
    private static Node getNode(Node node, StringBuffer path) {
        for (int i = 0; i < path.length() && node != null; i++)
            node = node.getChild(path.charAt(i));
        return node;
    }

    public ArrayList match(String pattern) {
        ArrayList result = new ArrayList();
        root.match(pattern, 0, result, new char[20], -1);
        return result;
    }

    public String getVersion() {
        return version;
    }

    private static final Node[] NO_CHILDREN = new Node[0];

    static final class Node {
        boolean complete = false;
        char value;
        Node[] children;

        public Node() {
            children = NO_CHILDREN;
        }

        public Node(char value) {
            this();
            this.value = value;
        }

        public boolean isLeaf() {
            return children.length == 0;
        }

        public final Node getChild(char value) {
            Node midNode;
            char midValue;
            Node[] nodes = children;
            int mid, low = 0, high = nodes.length - 1;

            while (low <= high) {
                midValue = (midNode = nodes[mid = (low + high) >> 1]).value;

                if (midValue > value)
                    high = mid - 1;
                else if (midValue < value)
                    low = mid + 1;
                else
                    return midNode; // key found
            }
            return null;
        }

        public Node appendChild(char value) {
            Node[] nodes = children;
            if (nodes.length == 0) {
                Node child = new Node(value);
                children = new Node[] { child };
                return child;
            }
            int off = search(value, nodes);
            if (off < 0) {
                off = -off - 1;
                Node child = new Node(value);
                Node[] newest = new Node[nodes.length + 1];
                System.arraycopy(nodes, 0, newest, 0, off);
                newest[off] = child;
                System.arraycopy(nodes, off, newest, off + 1, nodes.length
                        - off);
                this.children = newest;
                return child;
            }
            return nodes[off];
        }

        public void complete() {
            this.complete = true;
        }

        public boolean containsAll(Node node) {
            if (value != node.value)
                return false;
            Node[] nch = node.children;
            for (int i = nch.length; i-- > 0;) {
                Node nchild = nch[i];
                Node cont = getChild(nchild.value);
                if (cont == null || !cont.containsAll(nchild))
                    return false;
            }
            return true;
        }

        void match(String pattern, int offset, ArrayList searchResults,
                char[] path, int depth) {
            if (depth < path.length) {
                if (depth >= 0)
                    path[depth] = value;
                depth++;

                if (offset >= pattern.length()) {
                    if (complete && searchResults.size() < Settings.DIC_MATCH_THRESHOLD)
                        searchResults.add(new String(path, 0, depth));

                } else {
                    char c = pattern.charAt(offset);
                    if (c == '?') {
                        Node[] nodes = children;
                        int len = nodes.length;
                        for (int i = 0; i < len && searchResults.size() < Settings.DIC_MATCH_THRESHOLD; i++)
                            nodes[i].match(pattern, offset + 1, searchResults,
                                    path, depth);
                    } else if (c == '*') {

                        int off = offset;
                        while (++off < pattern.length()
                                && pattern.charAt(off) == '*')
                            ;
                        match(pattern, offset + 1, searchResults, path,
                                depth - 1);

                        Node[] nodes = children;
                        int len = nodes.length;
                        for (int i = 0; i < len && searchResults.size() < Settings.DIC_MATCH_THRESHOLD; i++)
                            nodes[i].match(pattern, offset, searchResults,
                                    path, depth);
                    } else {
                        Node n = getChild(c);
                        if (n != null)
                            n.match(pattern, offset + 1, searchResults, path,
                                    depth);
                    }
                }
            }
        }

        public boolean equals(Object obj) {
            if (obj != null && obj instanceof Node) {
                Node n = (Node) obj;
                if (this.value == n.value && this.complete == n.complete) {
                    Node[] nodes = n.children;
                    if (this.children.length == nodes.length) {
                        for (int i = nodes.length; i-- > 0;)
                            if (!this.children[i].equals(nodes[i]))
                                return false;
                        return true;
                    }
                }
            }
            return false;
        }

        public int hashCode() {
            int hash = (int) value;
            Node[] nodes = this.children;
            for (int i = nodes.length; i-- > 0;)
                hash = 31 * hash + (int) nodes[i].value;
            return complete ? hash : -hash;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            proxyToString(sb, 0);
            return sb.toString();
        }

        void proxyToString(StringBuffer sb, int indent) {
            if (sb.length() > 100000)
                return;
            sb.append('\n');
            for (int i = 0; i < indent; i++)
                sb.append(' ');
            sb.append(value);
            for (int i = 0; i < children.length; i++)
                children[i].proxyToString(sb, indent + 1);
        }
    }
}
