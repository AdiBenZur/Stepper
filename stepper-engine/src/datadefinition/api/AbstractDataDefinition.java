package datadefinition.api;

import exception.data.NotUserFriendlyException;
import exception.data.TypeDontMatchException;

public abstract class AbstractDataDefinition implements DataDefinition{

    private final String name;
    private final boolean isUserFriendly;
    private final Class<?> type;

    // empty default Ctor
    protected AbstractDataDefinition(String name, boolean isUserFriendly, Class<?> type) {
        this.name = name;
        this.isUserFriendly = isUserFriendly;
        this.type = type;
    }

    @Override
    public String getName() { return name;}

    @Override
    public boolean isUserFriendly() { return isUserFriendly;}

    @Override
    public Class<?> getType() { return type;}

    @Override
    public <T> T scanInput(String data) throws NotUserFriendlyException, TypeDontMatchException, TypeNotPresentException {
        throw new NotUserFriendlyException("The data definition is not user friendly.");
    }
}


