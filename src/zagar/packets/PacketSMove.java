package zagar.packets;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.Session;

import org.jetbrains.annotations.NotNull;
import zagar.protocol.Command;
import zagar.protocol.CommandEjectMass;
import zagar.protocol.CommandMove;
import zagar.util.JSONHelper;

public class PacketSMove {
  public float x;
  public float y;

  public PacketSMove(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public void write(@NotNull Session s) throws IOException {
    String msg = JSONHelper.toJSON(new CommandMove(x ,y));
    Command.log.info("Sending [" + msg + "]");
    s.getRemote().sendString(msg);
  }
}
