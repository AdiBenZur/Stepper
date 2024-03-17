package datadefinition.impl.list.type;

import datadefinition.impl.file.FileData;

import java.io.File;

public class FileList extends GeneralList<FileData>{


    public boolean add(File file) {
        return super.add(new FileData(file));
    }
}
