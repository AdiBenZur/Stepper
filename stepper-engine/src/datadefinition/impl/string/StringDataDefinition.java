package datadefinition.impl.string;

import datadefinition.api.AbstractDataDefinition;

public class StringDataDefinition extends AbstractDataDefinition {

    public StringDataDefinition() {
        super("String", true, String.class);
    }

    @Override
    public String scanInput(String data) {
        return data;
    }
}
