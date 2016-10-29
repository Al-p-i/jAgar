package zagar.protocol.model;

/**
 * @author apomosov
 */
public class Cell {
  private final int playerId;
  private final boolean isVirus;
  private int x;
  private int y;

  public Cell(int playerId, boolean isVirus, int x, int y) {
    this.playerId = playerId;
    this.isVirus = isVirus;
    this.x = x;
    this.y = y;
  }
}
