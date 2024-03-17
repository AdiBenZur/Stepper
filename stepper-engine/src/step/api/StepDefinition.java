package step.api;

import flow.execution.context.FlowExecutionContext;
import io.api.IODefinitionData;
import java.util.List;

public interface StepDefinition {

    String getName();
    boolean isReadOnly();
    List<IODefinitionData> getInputs();
    List<IODefinitionData> getOutputs();
    void setStepResult( StepResult status);
    StepResult run(FlowExecutionContext context);
    StepResult validateInput(FlowExecutionContext context);
    String getStepResult();
    void initializeResult();


}
