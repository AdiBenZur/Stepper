package datadefinition.impl.number;

import datadefinition.api.AbstractDataDefinition;
import exception.data.TypeDontMatchException;

public class NumberDataDefinition extends AbstractDataDefinition {

    public NumberDataDefinition() {
        super("Number", true, Integer.class);
    }

    @Override
    public Integer scanInput(String data) throws TypeDontMatchException{
        try {
            return Integer.valueOf(data);
        }
        catch (NumberFormatException e) {
            throw new TypeDontMatchException(e.getMessage());
        }
    }
}
