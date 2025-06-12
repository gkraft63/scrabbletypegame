package jscrabble;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

public class GlobalCache {

    
    public static class ColorCache {
        public static final Color DARK_SEAGREEN = new Color(0x00007A6C);
        public static final Color TABBED_PANEL_BGCOLOR = new Color(0xFFECEEE4);
        public static final Color LAVENDER = new Color(0x00ECEEE4);
        public static final Color DEEP_PURPLE = new Color(0x00D1027B);
        public static final Color OLD_GREEN = new Color(0x00408080);
    }
    
    public static class FontCache {
        public static final Font ARIAL_SMALL = new Font("helvetica", Font.BOLD, 12);
        public static final Font ARIAL_MEDIUM = new Font("helvetica", Font.BOLD, 16);
        public static final Font ARIAL_BIG = new Font("helvetica", Font.BOLD, 16);
    }
    
    public static class ImageCache {
        public static final Image LOGO = Support.getImage("/img/logo.gif");
        public static final Image BANNER = Support.getImage("/img/banner.gif");
        public static final Image USED_PLATES = Support.getImage("/img/used_plates.gif");
        public static final Image ALPHABET = Support.getImage("/img/alphabet.gif");
        public static final Image BOARD = Support.getImage("/img/board.gif");
        public static final Image RACK = Support.getImage("/img/rack.gif");
        public static final Image THEME = Support.getImage("/img/theme03.gif");
        public static final Image SMALL_ICON = Support.getImage("/img/icon.gif");
        public static final Image FACE_HUMAN = Support.getImage("/img/human.gif");
        public static final Image FACE_MACHINE = Support.getImage("/img/machine.gif");
    }
}
