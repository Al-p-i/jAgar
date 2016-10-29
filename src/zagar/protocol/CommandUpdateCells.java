package zagar.protocol;

import com.google.gson.JsonObject;
import zagar.protocol.model.Cell;

/**
 * @author apomosov
 */
public class CommandUpdateCells extends Command {
  public static final String NAME = "update";

  private final Cell[] cells;
  public CommandUpdateCells(Cell[] cells) {
    super(NAME);
    this.cells = cells;
  }
}
