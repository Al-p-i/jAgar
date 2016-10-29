package zagar.packets;

import java.io.IOException;

import zagar.protocol.Command;
import zagar.protocol.CommandMove;
import zagar.protocol.CommandSplit;
import zagar.util.JSONHelper;
import zagar.view.Game;

public class PacketSSplit {
  public PacketSSplit() {
  }

  public void write() throws IOException {
    String msg = JSONHelper.toJSON(new CommandSplit());
    Command.log.info("Sending [" + msg + "]");
    Game.socket.session.getRemote().sendString(msg);
  }
}
