package zagar.view;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import zagar.network.SocketHandler;
import zagar.network.packets.PacketMove;
import zagar.network.packets.PacketEjectMass;
import org.jetbrains.annotations.NotNull;

public class Game {
  @NotNull
  public static Cell[] cells = new Cell[32768];
  public static int cellsNumber = 0;
  @NotNull
  public static ArrayList<Cell> player = new ArrayList<>();
  @NotNull
  public static String[] leaderBoard = new String[10];
  public static double maxSizeX, maxSizeY, minSizeX, minSizeY;
  @NotNull
  public static ArrayList<Integer> playerID = new ArrayList<>();
  public static float followX;
  public static float followY;
  public static double zoom;
  private double zoomm = -1;
  private int sortTimer;
  public static int score;
  @NotNull
  public static SocketHandler socket;

  @NotNull
  public String serverIP;
  @NotNull
  public static String serverToken = "default_token";
  @NotNull
  public static String login = "default_login";
  public static int spawnPlayer = -1;
  @NotNull
  public static HashMap<Integer, String> cellNames = new HashMap<>();
  @NotNull
  public static String mode = "";
  public static long fps = 60;
  public static boolean rapidEject;

  public Game() {
    this.serverIP = "ws://" + (JOptionPane.showInputDialog(null, "Ip", "localhost:7001"));
    this.login = (JOptionPane.showInputDialog(null, "Login", "login"));
    String password = (JOptionPane.showInputDialog(null, "Password", "pass"));
    this.mode = selectMode();

    this.serverToken = login();

    this.spawnPlayer = 100;

    final WebSocketClient client = new WebSocketClient();
    this.socket = new SocketHandler();
    new Thread(() -> {
      try {
        client.start();
        URI serverURI = new URI(serverIP + "/clientConnection");
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setHeader("Origin", "http://agar.io");
        client.connect(socket, serverURI, request);
        System.out.println("Trying to connect <" + serverIP + ">");
        socket.awaitClose(7, TimeUnit.DAYS);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }).start();
  }

  private String login() {
    return "testtoken";//TODO
  }

  @NotNull
  private String selectMode() {
    String mode = (JOptionPane.showInputDialog(null, "Game mode:\n\nffa\nteams\nexperimental\nparty", "ffa")).toLowerCase().replace("ffa", "");
    if (mode.length() > 0) {
      mode = ":" + mode;
    }
    return mode;
  }

  public void tick() throws IOException {
    if (socket != null && socket.session != null && socket.session.isOpen()) {
      if (spawnPlayer != -1) {
        spawnPlayer--;
      }

      if (spawnPlayer == 0) {
        System.out.println("Resetting level (death)");
      }
      if (Game.player.size() == 0) {
        if (socket.session.isOpen() && spawnPlayer == -1) {
          score = 0;
          Game.player.clear();
          Game.cells = new Cell[Game.cells.length];
          cellsNumber = 0;
          cellNames.clear();
        }
      }
    }

    ArrayList<Integer> toRemove = new ArrayList<>();

    for (int i : playerID) {
      for (Cell c : Game.cells) {
        if (c != null) {
          if (c.id == i && !player.contains(c)) {
            System.out.println("Centered cell " + c.name);
            player.add(c);
            toRemove.add(i);
          }
        }
      }
    }

    for (int i : toRemove) {
      playerID.remove(playerID.indexOf(i));
    }

    if (socket.session != null && player.size() > 0) {
      float totalSize = 0;
      int newScore = 0;
      for (Cell c : player) {
        totalSize += c.size;
        newScore += (c.size * c.size) / 100;
      }

      if (newScore > score) {
        score = newScore;
      }

      zoomm = GameFrame.size.height / (1024 / Math.pow(Math.min(64.0 / totalSize, 1), 0.4));

      if (zoomm > 1) {
        zoomm = 1;
      }

      if (zoomm == -1) {
        zoomm = zoom;
      }
      zoom += (zoomm - zoom) / 40f;

      if (socket.session.isOpen()) {
        float avgX = 0;
        float avgY = 0;
        totalSize = 0;

        for (Cell c : Game.player) {
          avgX += c.x;
          avgY += c.y;
          totalSize += c.size;
        }

        avgX /= Game.player.size();
        avgY /= Game.player.size();

        float x = avgX;
        float y = avgY;
        x += (float) ((GameFrame.mouseX - GameFrame.size.width / 2) / zoom);
        y += (float) ((GameFrame.mouseY - GameFrame.size.height / 2) / zoom);
        followX = x;
        followY = y;
        (new PacketMove(x, y)).write(socket.session);

        if (rapidEject) {
          new PacketEjectMass().write();
        }
      }
    }

    for (int i = 0; i < cellsNumber; i++) {
      if (cells[i] != null) {
        cells[i].tick();
      }
    }

    sortTimer++;

    if (sortTimer > 10) {
      sortCells();
      sortTimer = 0;
    }
  }

  public void afterRender() {
  }

  public static void addCell(@NotNull Cell c) {
    cells[cellsNumber] = c;
    cellsNumber++;
    if (cellsNumber > cells.length) {
      cellsNumber = 0;
    }
  }

  public static void sortCells() {
    Arrays.sort(cells, (o1, o2) -> {
      if (o1 == null && o2 == null) {
        return 0;
      }
      if (o1 == null) {
        return 1;
      }
      if (o2 == null) {
        return -1;
      }
      return Float.compare(o1.size, o2.size);
    });
  }

  public static void respawn() {
    if (spawnPlayer == -1) {
      spawnPlayer = 100;
    }
  }
}
