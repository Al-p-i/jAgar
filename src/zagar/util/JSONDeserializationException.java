package zagar.util;

import com.google.gson.JsonSyntaxException;

/**
 * @author apomosov
 */
public class JSONDeserializationException extends Exception {

  public JSONDeserializationException(JsonSyntaxException cause) {
    super(cause);
  }
}
