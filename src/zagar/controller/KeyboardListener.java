package zagar.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import zagar.packets.PacketSSplit;
import zagar.packets.PacketSEjectMass;
import org.jetbrains.annotations.NotNull;
import zagar.view.Game;

public class KeyboardListener implements KeyListener {
  @Override
  public void keyPressed(@NotNull KeyEvent e) {
    try {
      if (Game.socket != null && Game.socket.session != null) {
        if (Game.socket.session.isOpen()) {
          if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            new PacketSSplit().write();
          }
          if (e.getKeyCode() == KeyEvent.VK_W) {
            new PacketSEjectMass().write();
          }
          if (e.getKeyCode() == KeyEvent.VK_T) {
            Game.rapidEject = true;
          }
          if (e.getKeyCode() == KeyEvent.VK_R) {
            if (Game.player.size() == 0) {
              Game.respawn();
            }
          }
        }
      }
    } catch (IOException ioEx) {
      ioEx.printStackTrace();
    }
  }

  @Override
  public void keyReleased(@NotNull KeyEvent e) {
    if (Game.socket != null && Game.socket.session != null) {
      if (Game.socket.session.isOpen()) {
        if (e.getKeyCode() == KeyEvent.VK_T) {
          Game.rapidEject = false;
        }
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }
}
