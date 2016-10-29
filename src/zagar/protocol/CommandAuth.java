package zagar.protocol;

/**
 * @author apomosov
 */
public class CommandAuth extends Command {
  public static final String NAME = "auth";
  private final String token;
  private final String login;

  public CommandAuth(String login, String token) {
    super(NAME);
    this.token = token;
    this.login = login;
  }
}
