package datadefinition.impl.file;

import java.io.File;
import java.net.URI;

public class FileData extends File{


    public FileData(File file){
        super(file.getAbsolutePath());
    }

    public FileData(String pathname) {
        super(pathname);
    }

    public FileData(String parent, String child) {
        super(parent, child);
    }

    public FileData(File parent, String child) {
        super(parent, child);
    }

    public FileData(URI uri) {
        super(uri);
    }

    @Override
    public String toString() {
        return getAbsolutePath();
    }
}
