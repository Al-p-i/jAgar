package zagar.util;

import com.google.gson.*;
import zagar.protocol.Command;

/**
 * @author apomosov
 */
public class JSONHelper {
  private static Gson gson = new GsonBuilder().create();

  public static String toJSON(Object object) {
    return gson.toJson(object);
  }

  public static Command fromJSON(String json) throws JSONDeserializationException {
    try {
      return gson.fromJson(json, Command.class);
    } catch (JsonSyntaxException e) {
      throw new JSONDeserializationException(e);
    }
  }

  public static JsonObject getJSONObject(String string) {
    return gson.fromJson(string, JsonObject.class);
  }
}
