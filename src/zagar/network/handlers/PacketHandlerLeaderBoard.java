package zagar.network.handlers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.google.gson.JsonObject;
import zagar.view.Game;
import org.jetbrains.annotations.NotNull;

public class PacketHandlerLeaderBoard {
  public PacketHandlerLeaderBoard(@NotNull JsonObject json) {
    //TODO
/*    b.order(ByteOrder.LITTLE_ENDIAN);
    int leaderBoardSize = b.getInt(1);

    Game.leaderBoard = new String[leaderBoardSize];

    int offset = 9;

    for (int i = 0; i < leaderBoardSize; i++) {
      String nick = "";

      while (b.getShort(offset) != 0) {
        nick += b.getChar(offset);
        offset += 2;
      }

      offset += 6;

      if (nick.length() == 0) {
        nick = "An unnamed cell";
      }

      Game.leaderBoard[i] = (i + 1) + ". " + nick;
    }*/
  }
}
