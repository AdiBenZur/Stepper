package datadefinition.api;

import exception.data.NotUserFriendlyException;
import exception.data.TypeDontMatchException;

public interface DataDefinition {
    String getName();
    boolean isUserFriendly();
    Class<?> getType();
    <T> T scanInput(String data) throws NotUserFriendlyException, TypeDontMatchException, TypeNotPresentException;
}
