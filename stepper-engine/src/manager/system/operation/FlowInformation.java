package manager.system.operation;

import flow.definition.api.StepUsageDeclaration;
import io.api.IODefinitionData;

import java.util.List;
import java.util.Map;

public class FlowInformation {

    private String name;
    private String flowDescription;
    private List<IODefinitionData> formalOutputs;
    private Boolean isRadOnly;
    private List<StepUsageDeclaration> steps;
    private List<IODefinitionData> freeInputs;
    private List<IODefinitionData> allOutputs;

    private final Map<IODefinitionData, List<StepUsageDeclaration>> fromInputToSteps; // (IOData, step). Which step use the input.
    private final Map<IODefinitionData, StepUsageDeclaration> fromOutputToStep; // (IOData, step). Which step produce the output.


    public FlowInformation(String name, String flowDescription, List<IODefinitionData> formalOutputs, Boolean isRadOnly,
                           List<StepUsageDeclaration> steps, List<IODefinitionData> freeInputs, List<IODefinitionData> allOutputs,
                           Map<IODefinitionData, List<StepUsageDeclaration>> fromInputToSteps,
                           Map<IODefinitionData, StepUsageDeclaration> fromOutputToStep) {
        this.name = name;
        this.flowDescription = flowDescription;
        this.formalOutputs = formalOutputs;
        this.isRadOnly = isRadOnly;
        this.steps = steps;
        this.freeInputs = freeInputs;
        this.allOutputs = allOutputs;
        this.fromInputToSteps = fromInputToSteps;
        this.fromOutputToStep = fromOutputToStep;
    }

    public String getName() {
        return name;
    }

    public String getFlowDescription() {
        return flowDescription;
    }

    public List<IODefinitionData> getFormalOutputs() {
        return formalOutputs;
    }

    public Boolean getRadOnly() {
        return isRadOnly;
    }

    public List<StepUsageDeclaration> getSteps() {
        return steps;
    }

    public List<IODefinitionData> getFreeInputs() {
        return freeInputs;
    }

    public List<IODefinitionData> getAllOutputs() {
        return allOutputs;
    }

    public Map<IODefinitionData, List<StepUsageDeclaration>> getFromInputToSteps() {
        return fromInputToSteps;
    }

    public Map<IODefinitionData, StepUsageDeclaration> getFromOutputToStep() {
        return fromOutputToStep;
    }

    public List<StepUsageDeclaration> getLinkedStepsByInputKey(IODefinitionData input) {
        return fromInputToSteps.get(input);
    }

    public StepUsageDeclaration getStepProduceByKey(IODefinitionData input) {
        return fromOutputToStep.get(input);
    }
}
