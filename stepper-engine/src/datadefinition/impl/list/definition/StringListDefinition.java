package datadefinition.impl.list.definition;

import datadefinition.api.AbstractDataDefinition;
import datadefinition.impl.list.type.StringList;

public class StringListDefinition extends AbstractDataDefinition {

    public StringListDefinition() { super("String list", false, StringList.class); }
}
