package dto.io;

public class IOInStepMappingDTO {
    private final String name;
    private final String necessity;
    private final String isConnectedToOther; // If so- the io name, if not- null
    private final String stepNameOfConnectedData; // The step name that the comes the connected io

    public IOInStepMappingDTO(String name, String necessity, String isConnectedToOther, String stepName) {
        this.name = name;
        this.necessity = necessity;
        this.isConnectedToOther = isConnectedToOther;
        this.stepNameOfConnectedData = stepName;
    }

    public String getName() {
        return name;
    }

    public String getNecessity() {
        return necessity;
    }

    public String getIsConnectedToOther() {
        return isConnectedToOther;
    }

    public String getStepNameOfConnectedData() {
        return stepNameOfConnectedData;
    }
}
