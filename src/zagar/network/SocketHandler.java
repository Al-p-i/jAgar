package zagar.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.extensions.Frame;

import zagar.packets.PacketCUpdateCells;
import zagar.packets.PacketCResetLevel;
import zagar.packets.PacketCLeaderBoard;
import zagar.packets.PacketSAuth;
import org.jetbrains.annotations.NotNull;
import zagar.protocol.*;
import zagar.util.JSONHelper;
import zagar.view.Game;

@WebSocket(maxTextMessageSize = 2 ^ 32)
public class SocketHandler {

  @NotNull
  private final CountDownLatch closeLatch;
  @NotNull
  public Session session;

  public SocketHandler() {
    this.closeLatch = new CountDownLatch(1);
  }

  public boolean awaitClose(int duration, @NotNull TimeUnit unit) throws InterruptedException {
    return this.closeLatch.await(duration, unit);
  }

  @OnWebSocketClose
  public void onClose(int statusCode, @NotNull String reason) {
    System.out.println("Closed." + statusCode + "<" + reason + ">");
    this.closeLatch.countDown();
  }

  @OnWebSocketConnect
  public void onConnect(@NotNull Session session) throws IOException {
    this.session = session;

    System.out.println("Connected!");

    new PacketSAuth(Game.login, Game.serverToken).write();//TODO from now being session is authorized
    Game.spawnPlayer = 100;
    long oldTime = 0;
  }

  @OnWebSocketMessage
  public void onTextPacket(@NotNull String msg) {
    if (session.isOpen()) {
      handlePacket(msg);
    }
  }

  public void handlePacket(String msg) {
    JsonObject json = JSONHelper.getJSONObject(msg);
    String name = json.get("name").getAsString();
    switch (name){
      case CommandLeaderBoard.NAME:
        new PacketCLeaderBoard(json);
        break;
      case CommandResetLevel.NAME:
        new PacketCResetLevel();
        break;
      case CommandUpdateCells.NAME:
        new PacketCUpdateCells(json);
    }
  }
}
