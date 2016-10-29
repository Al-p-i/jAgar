package zagar.packets;

import java.io.IOException;

import zagar.protocol.Command;
import zagar.protocol.CommandAuth;
import zagar.util.JSONHelper;
import zagar.view.Game;

public class PacketSAuth {
  private final String login;
  private final String token;

  public PacketSAuth(String login, String token) {
    this.login = login;
    this.token = token;
  }

  public void write() throws IOException {
    String msg = JSONHelper.toJSON(new CommandAuth(login, token));
    Command.log.info("Sending [" + msg + "]");
    Game.socket.session.getRemote().sendString(msg);
  }
}
