package zagar.packets;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import zagar.view.Cell;
import zagar.view.Game;
import org.jetbrains.annotations.NotNull;

public class PacketCResetLevel {
  public PacketCResetLevel() {
    Game.player.clear();
    Game.cells = new Cell[Game.cells.length];
    System.out.println("Resetting level. (protocol)");
  }
}
