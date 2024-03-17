package dto.io;

import io.api.IODefinitionData;

import java.util.Objects;

public class IODefinitionDataDTO {
    private final String name;
    private final String userString;
    private final String necessity;
    private final String dataDefinition;


    public IODefinitionDataDTO(IODefinitionData ioDefinitionData) {
        this.name = ioDefinitionData.getName();
        this.userString = ioDefinitionData.getUserString();
        this.necessity = ioDefinitionData.getNecessity().toString();
        this.dataDefinition = ioDefinitionData.getDataDefinition().getType().getSimpleName();;
    }

    public IODefinitionDataDTO(String name, String userString, String necessity, String dataDefinition) {
        this.name = name;
        this.userString = userString;
        this.necessity = necessity;
        this.dataDefinition = dataDefinition;
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

    public String getType() {
        return dataDefinition;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        IODefinitionDataDTO dataIODTO = (IODefinitionDataDTO) object;

        return Objects.equals(name, dataIODTO.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
