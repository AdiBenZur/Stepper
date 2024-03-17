package step.api;

import io.api.IODefinitionData;
import io.impl.IODefinitionDataImpl;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStepDefinition implements StepDefinition {

    private final String stepName;
    private final boolean isReadOnly;
    private StepResult stepResult;
    private final List<IODefinitionData> inputs;
    private final List<IODefinitionData> outputs;


    protected AbstractStepDefinition(String name, boolean isReadOnly) {
        this.stepName = name;
        this.isReadOnly = isReadOnly;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        this.stepResult = null;
    }

    @Override
    public void initializeResult() {
        stepResult = null;
    }

    @Override
    public String getName() { return stepName; }

    @Override
    public boolean isReadOnly() { return isReadOnly;}

    @Override
    public List<IODefinitionData> getInputs() {
        return inputs;
    }

    @Override
    public List<IODefinitionData> getOutputs() { return outputs; }

    @Override
    public void setStepResult(StepResult status) { stepResult = status; }

    @Override
    public String getStepResult() {

        try {
            switch (stepResult) {
                case SUCCESS:
                    return "Success";

                case FAILURE:
                    return "Failure";


                case WARNING:
                    return "Warning";

                default:
                    return null;
            }
        }
        catch (NullPointerException e) {
            throw new NullPointerException();
        }

    }

    protected void addInput(IODefinitionData newInput) {
        inputs.add(newInput);
    }

    protected void addOutput(IODefinitionDataImpl newOutput) {
        outputs.add(newOutput);
    }
}
