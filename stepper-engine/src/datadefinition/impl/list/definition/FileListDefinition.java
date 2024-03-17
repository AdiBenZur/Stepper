package datadefinition.impl.list.definition;

import datadefinition.api.AbstractDataDefinition;
import datadefinition.impl.list.type.FileList;

public class FileListDefinition extends AbstractDataDefinition {

    public FileListDefinition() { super("File list", false, FileList.class);}
}
