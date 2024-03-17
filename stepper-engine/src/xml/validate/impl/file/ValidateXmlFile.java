package xml.validate.impl.file;

import xml.validate.api.Validator;

import java.io.File;
import java.util.List;

public class ValidateXmlFile implements Validator {
    private final File xmlFile;

    public ValidateXmlFile(String xmlPath) {
        this.xmlFile = new File(xmlPath);
    }

    @Override
    public void validate(List<String> errors) {
        // Check if the given path is a file
        if(!xmlFile.isFile()) {
            errors.add("The path: '" + xmlFile.getName() + "' is not a file");
        }
        else {
            // Check if the file exist
            if(!xmlFile.exists()) {
                errors.add("File name: '" + xmlFile.getName() + "' does not exist.");
            }
            else {

                // check if the file is from xml type
                if(!xmlFile.getPath().endsWith(".xml")) {
                    errors.add("The given path: '" + xmlFile.getPath() + "' is not an xml file.");
                }
            }
        }
    }
}
