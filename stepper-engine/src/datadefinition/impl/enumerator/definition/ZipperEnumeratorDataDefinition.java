package datadefinition.impl.enumerator.definition;

import datadefinition.api.AbstractDataDefinition;
import datadefinition.impl.enumerator.type.ZipperEnumerator;
import exception.data.TypeDontMatchException;


public class ZipperEnumeratorDataDefinition extends AbstractDataDefinition {

    public ZipperEnumeratorDataDefinition() { super("Enumerator", true, ZipperEnumerator.class);}

    @Override
    public ZipperEnumerator scanInput(String data) throws TypeDontMatchException {
        ZipperEnumerator result;

        if (data.equals("ZIP")) {
            result = ZipperEnumerator.ZIP;
        } else if (data.equals("UNZIP")) {
            result = ZipperEnumerator.UNZIP;
        } else {
            throw new TypeDontMatchException("Should enter only ZIP/ UNZIP.");
        }
        return result;
    }

    @Override
    public String toString() {
        return"ZipperEnumerator";
    }
}
