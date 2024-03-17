package datadefinition.impl.list.definition;

import datadefinition.api.AbstractDataDefinition;
import datadefinition.impl.list.type.NumberList;

public class NumberListDefinition extends AbstractDataDefinition {

    public NumberListDefinition() { super("Number list", false, NumberList.class);}
}
