package zagar.packets;

import java.io.IOException;

import zagar.protocol.Command;
import zagar.protocol.CommandAuth;
import zagar.protocol.CommandEjectMass;
import zagar.util.JSONHelper;
import zagar.view.Game;

public class PacketSEjectMass {
  public PacketSEjectMass() {
  }

  public void write() throws IOException {
    String msg = JSONHelper.toJSON(new CommandEjectMass());
    Command.log.info("Sending [" + msg + "]");
    Game.socket.session.getRemote().sendString(msg);
  }
}
