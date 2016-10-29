package zagar.protocol;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author apomosov
 */
public abstract class Command {
  @NotNull
  public static final Logger log = LogManager.getLogger(Command.class);
  private final String name;

  protected Command(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
