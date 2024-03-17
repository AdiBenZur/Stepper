package datadefinition.impl.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import datadefinition.api.AbstractDataDefinition;
import exception.data.TypeDontMatchException;

public class JsonDataDefinition extends AbstractDataDefinition {

    public JsonDataDefinition() {
        super("Json", true, JsonObject.class);
    }

    @Override
    public JsonObject scanInput(String data) throws TypeDontMatchException {
        try {
            return JsonParser.parseString(data).getAsJsonObject();
        }
        catch (Exception e) {
            throw new TypeDontMatchException("The string is not valid Json object");
        }
    }
}
