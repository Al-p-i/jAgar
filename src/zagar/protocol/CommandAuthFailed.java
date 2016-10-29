package zagar.protocol;

/**
 * @author apomosov
 */
public class CommandAuthFailed extends Command {
  public static final String NAME = "auth_fail";

  public CommandAuthFailed(String login, String token) {
    super(NAME);
  }
}
