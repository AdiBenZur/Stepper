package dto.io;

public class IOValueDTO {
    private final String flowName;
    private final IODefinitionDataDTO input;
    private final String value;


    public IOValueDTO(String flowName, IODefinitionDataDTO input, String value) {
        this.flowName = flowName;
        this.input = input;
        this.value = value;
    }

    public String getFlowName() {
        return flowName;
    }

    public IODefinitionDataDTO getInput() {
        return input;
    }

    public String getValue() {
        return value;
    }
}
