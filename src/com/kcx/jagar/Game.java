package com.kcx.jagar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.kcx.jagar.packet.PacketS000SetNick;
import com.kcx.jagar.packet.PacketS016Move;
import com.kcx.jagar.packet.PacketS021EjectMass;
import org.jetbrains.annotations.NotNull;

public class Game {
  @NotNull
  public static Cell[] cells = new Cell[32768];
  public static int cellsNumber = 0;
  @NotNull
  public static ArrayList<Cell> player = new ArrayList<>();
  @NotNull
  public static String[] leaderboard = new String[10];
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
  public static String serverIP = "ws://" + (JOptionPane.showInputDialog(null, "Ip (leave blank for official server)", ""));
  @NotNull
  public static String serverToken = "";
  @NotNull
  public static String nick = (JOptionPane.showInputDialog(null, "Nick", "jAgar"));
  private static boolean playbackRewind;
  public static int maxPlayback;
  public static int bots = 0;
  public static int spawnPlayer = -1;
  @NotNull
  public static HashMap<Integer, String> cellNames = new HashMap<Integer, String>();
  public static int level = 0;
  public static int exp = 0;
  public static int maxExp = 1;
  @NotNull
  public static String mode = "";
  public static long fps = 60;
  public static boolean rapidEject;
  @NotNull
  public static HashMap<Integer, ArrayList<ByteBuffer>> playback = new HashMap<Integer, ArrayList<ByteBuffer>>();
  public static int playbackTime = 0;
  public static boolean isPlaybacking = false;
  private static float playbackSpeed = 0;
  private static boolean playbackSpeeding;

  public Game() {
    mode = selectMode();

    balance();

    spawnPlayer = 100;

    final WebSocketClient client = new WebSocketClient();
    this.socket = new SocketHandler();
    new Thread(() -> {
      try {
        client.start();
        URI serverURI = new URI(serverIP);
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        request.setHeader("Origin", "http://agar.io");
        client.connect(socket, serverURI, request);
        System.out.println("Trying to connect <" + serverIP + ">");
        socket.awaitClose(7, TimeUnit.DAYS);
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }).start();

    generateBots();
  }

  private void generateBots() {
    new Thread(() -> {
      long xx = System.currentTimeMillis();
      int yy = 0;
      boolean stop = true;
      while (stop) {
        if (System.currentTimeMillis() - xx > 150 && yy < bots) {
          yy++;
          new Thread(() -> {
            try {
              SocketHandler socket1 = new SocketHandler();
              socket1.bot = true;
              WebSocketClient client = new WebSocketClient();
              client.start();
              URI serverURI = new URI(serverIP);
              ClientUpgradeRequest request = new ClientUpgradeRequest();
              request.setHeader("Origin", "http://agar.io");
              client.connect(socket1, serverURI, request);
              System.out.println("Bot Trying to connect <" + serverIP + ">");
              socket1.awaitClose(7, TimeUnit.DAYS);
            } catch (Throwable t) {
              t.printStackTrace();
            }
          }).start();
          xx = System.currentTimeMillis();
        }
        if (yy == bots) {
          stop = false;
        }
      }
    }).start();
  }

  private void balance() {
    if (serverIP.equals("ws://")) {
      String[] data;
      try {
        data = ServerConnector.getServerData();
        serverIP = "ws://" + data[0];
        serverToken = data[1];
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(-99);
      }
    }
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
        new PacketS000SetNick(Game.nick).write(socket.session);
      }
      if (Game.player.size() == 0) {
        if (socket.session.isOpen() && spawnPlayer == -1) {
          if (!isPlaybacking) {
            score = 0;
            Game.player.clear();
            Game.cells = new Cell[Game.cells.length];
            cellsNumber = 0;
            cellNames.clear();

            maxPlayback = playbackTime;
            playbackTime = 0;
            isPlaybacking = true;
          }
        }
      }
    }

    if (isPlaybacking) {
      if (playbackRewind) {
        if (playback.get(playbackTime) != null) {
          for (ByteBuffer b : playback.get(playbackTime)) {
            socket.handlePacket(b);
          }
        }
      } else {
        for (int i = playbackTime; i < playbackTime + (playbackSpeed + 1); i++) {
          if (playback.get(i) != null) {
            for (ByteBuffer b : playback.get(i)) {
              socket.handlePacket(b);
            }
          }
        }
      }
      playbackTime += 1 + playbackSpeed;
      if (playbackTime < 0) {
        playbackTime = maxPlayback;
      }
      if (playbackTime > maxPlayback && maxPlayback > 100) {
        playbackTime = 0;
        score = 0;
        Game.player.clear();
        Game.cells = new Cell[Game.cells.length];
        cellsNumber = 0;
        cellNames.clear();
      }
      if (playbackTime > maxPlayback && maxPlayback <= 100) {
        respawn();
      }
    }

    if (playbackSpeeding) {
      playbackSpeed += 0.03;
    }

    if (playbackRewind) {
      playbackSpeed = -2;
    }

    if (player.size() > 0) {
      if (!isPlaybacking) {
        Game.playbackTime++;
      }
    }

    ArrayList<Integer> toRemove = new ArrayList<>();

    for (int i : playerID) {
      try {
        for (Cell c : Game.cells) {
          if (c != null) {
            if (c.id == i && !player.contains(c)) {
              System.out.println("Centered cell " + c.name);
              player.add(c);
              toRemove.add(i);
            }
          }
        }
      } catch (ConcurrentModificationException e) {
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
        (new PacketS016Move(x, y)).write(socket.session);

        if (rapidEject) {
          new PacketS021EjectMass().write();
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

  public static void pressMouse(int x, int y, int button) {
    if (Game.isPlaybacking) {
      if (button == 1) {
        playbackSpeeding = true;
      }
      if (button == 3) {
        playbackRewind = true;
      }
    }
  }

  public static void releaseMouse(int x, int y, int button) {
    if (Game.isPlaybacking) {
      if (button == 1) {
        playbackSpeeding = false;
        playbackSpeed = 0;
      }
      if (button == 3) {
        playbackRewind = false;
        playbackSpeed = 0;
      }
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
    Arrays.sort(cells, new Comparator<Cell>() {
      @Override
      public int compare(Cell o1, Cell o2) {
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
      }
    });
  }

  public static void respawn() {
    if (spawnPlayer == -1) {
      playback.clear();
      playbackTime = 0;
      isPlaybacking = false;
      maxPlayback = 0;
      spawnPlayer = 100;
    }
  }
}
