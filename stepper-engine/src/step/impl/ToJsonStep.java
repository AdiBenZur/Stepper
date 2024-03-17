package step.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import datadefinition.impl.DataDefinitionRegistry;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import io.api.DataNecessity;
import io.impl.IODefinitionDataImpl;
import step.api.AbstractStepDefinition;
import step.api.StepResult;

public class ToJsonStep extends AbstractStepDefinition {

    public ToJsonStep() {
        super("To Json", true);

        // Step inputs
        addInput(new IODefinitionDataImpl("CONTENT", DataDefinitionRegistry.STRING, DataNecessity.MANDATORY, "Content" ));

        // Step outputs
        addOutput(new IODefinitionDataImpl("JSON", DataDefinitionRegistry.JSON, DataNecessity.NA, "Json representation"));
    }

    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();

        StepResult result;
        String content;
        JsonObject object;

        try {
            content = context.getDataValue("CONTENT", String.class);
            try {
                object = JsonParser.parseString(content).getAsJsonObject();
                context.addLog(new StepLogImpl(stepName, "Content is JSON string. Converting it to json..."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully." ));
                result = StepResult.SUCCESS;
            }
            catch (Exception e) {
                object = null;
                context.addLog(new StepLogImpl(stepName, "Content is not a valid JSON representation"));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Could not convert to Json." ));
                return StepResult.FAILURE;
            }

            // Store data
            try {
                context.storeDataValue("JSON", object);
            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "Error occurred while storing the data: " + e.getMessage()));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Fail storing data to context: " + e.getMessage() ));
                result = StepResult.FAILURE;
                return result;
            }
        }
        catch (Exception e) {
            context.addLog(new StepLogImpl(stepName, "Failed to get content input."));
            result = StepResult.FAILURE;
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot get data from context: " + e.getMessage() ));
        }
        return result;
    }


    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        return null;
    }
}
