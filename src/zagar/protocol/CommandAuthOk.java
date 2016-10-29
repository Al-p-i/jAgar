package zagar.protocol;

/**
 * @author apomosov
 */
public class CommandAuthOk extends Command {
  public static final String NAME = "auth_ok";

  public CommandAuthOk(String login, String token) {
    super(NAME);
  }
}
