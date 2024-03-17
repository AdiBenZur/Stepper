package dto.io;

import io.api.IODefinitionData;

public class IODataValueDTO {
    private final String name;
    private final String userString;
    private final String necessity;
    private final String dataDefinition;
    private Object value;


    public IODataValueDTO(IODefinitionData ioDefinitionData, Object value) {
        this.name = ioDefinitionData.getName();
        this.userString = ioDefinitionData.getUserString();
        this.necessity = ioDefinitionData.getNecessity().toString();
        this.dataDefinition = ioDefinitionData.getDataDefinition().getType().getSimpleName();
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getUserString() {
        return userString;
    }

    public String getNecessity() {
        return necessity;
    }

    public String getDataDefinition() {
        return dataDefinition;
    }

    public Object getValue() {
        return value;
    }
}
