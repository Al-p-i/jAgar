package zagar.network.packets;

import java.io.IOException;

import zagar.protocol.Command;
import zagar.protocol.CommandSplit;
import zagar.util.JSONHelper;
import zagar.view.Game;

public class PacketSplit {
  public PacketSplit() {
  }

  public void write() throws IOException {
    String msg = JSONHelper.toJSON(new CommandSplit());
    Command.log.info("Sending [" + msg + "]");
    Game.socket.session.getRemote().sendString(msg);
  }
}
