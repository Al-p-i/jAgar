package zagar.network.packets;

import java.io.IOException;

import zagar.protocol.Command;
import zagar.protocol.CommandEjectMass;
import zagar.util.JSONHelper;
import zagar.view.Game;

public class PacketEjectMass {
  public PacketEjectMass() {
  }

  public void write() throws IOException {
    String msg = JSONHelper.toJSON(new CommandEjectMass());
    Command.log.info("Sending [" + msg + "]");
    Game.socket.session.getRemote().sendString(msg);
  }
}
