package datadefinition.impl.enumerator.definition;

import datadefinition.api.AbstractDataDefinition;
import datadefinition.impl.enumerator.type.ProtocolEnumerator;
import exception.data.TypeDontMatchException;

public class ProtocolEnumeratorDataDefinition extends AbstractDataDefinition {

    public ProtocolEnumeratorDataDefinition() { super("Enumerator", true, ProtocolEnumerator.class);}

    @Override
    public ProtocolEnumerator scanInput(String data) throws TypeDontMatchException {
        ProtocolEnumerator result;

        if (data.equals("http")) {
            result = ProtocolEnumerator.HTTP;
        } else if (data.equals("https")) {
            result = ProtocolEnumerator.HTTPS;
        } else {
            throw new TypeDontMatchException("Should enter only http/ https.");
        }

        return result;
    }


}
