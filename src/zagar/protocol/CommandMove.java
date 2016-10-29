package zagar.protocol;

/**
 * @author apomosov
 */
public class CommandMove extends Command {
  public static final String NAME = "move";

  private final float dx;
  private final float dy;
  public CommandMove(float dx, float dy) {
    super(NAME);
    this.dx = dx;
    this.dy = dy;
  }
}
