package jscrabble;
import java.awt.Component;
import java.io.File;
import java.net.URL;

public interface RankTable {

  public void setBean(ScrabbleBean bean);

  public void addRank(String player, int score, String opponent);

  public boolean isShowing();

  public void setShowing(boolean show, Component frameResolver);

}
