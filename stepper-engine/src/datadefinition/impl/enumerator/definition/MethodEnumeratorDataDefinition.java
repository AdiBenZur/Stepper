package datadefinition.impl.enumerator.definition;

import datadefinition.api.AbstractDataDefinition;
import datadefinition.impl.enumerator.type.MethodEnumerator;
import exception.data.TypeDontMatchException;

public class MethodEnumeratorDataDefinition extends AbstractDataDefinition {

    public MethodEnumeratorDataDefinition() { super("Enumerator", true, MethodEnumerator.class);}

    @Override
    public MethodEnumerator scanInput(String data) throws TypeDontMatchException {
        MethodEnumerator result;

        if (data.equals("GET")) {
            result = MethodEnumerator.GET;
        } else if (data.equals("PUT")) {
            result = MethodEnumerator.PUT;
        } else if (data.equals("POST")) {
            result = MethodEnumerator.POST;
        } else if (data.equals("DELETE")) {
            result = MethodEnumerator.DELETE;
        } else {
            throw new TypeDontMatchException("Should enter only GET/ PUT/ POST/ DELETE.");
        }

        return result;
    }
}
