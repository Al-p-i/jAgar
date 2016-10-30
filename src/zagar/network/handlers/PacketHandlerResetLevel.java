package zagar.network.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import zagar.auth.AuthClient;
import zagar.view.Cell;
import zagar.Game;

public class PacketHandlerResetLevel {
  @NotNull
  private static final Logger log = LogManager.getLogger(PacketHandlerResetLevel.class);

  public PacketHandlerResetLevel() {
    Game.player.clear();
    Game.cells = new Cell[Game.cells.length];
    log.info("Resetting level. (protocol)");
  }
}
