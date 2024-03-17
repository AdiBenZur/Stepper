package xml.validate.api;

import java.util.List;

public interface Validator {
    void validate(List<String> errors);
}
