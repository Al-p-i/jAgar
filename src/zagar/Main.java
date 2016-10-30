package zagar;

import org.jetbrains.annotations.NotNull;
import zagar.view.GameFrame;

public class Main {
  @NotNull
  private static GameThread thread;
  @NotNull
  public static GameFrame frame;
  @NotNull
  private static Game game;

  public static void main(@NotNull String[] args) {
    thread = new GameThread();
    frame = new GameFrame();
    game = new Game();

    thread.run();
  }

  public static void updateGame() {
    try {
      game.tick();
    } catch (Exception e) {
      e.printStackTrace();
    }
    frame.render();
    game.afterRender();
  }
}
