package zagar.protocol;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * @author apomosov
 */
public class CommandLeaderBoard extends Command {
  public static final String NAME = "leader_board";

  private final String[] leaderBoard;

  public CommandLeaderBoard(String[] leaderBoard) {
    super(NAME);
    this.leaderBoard = leaderBoard;
  }
}
