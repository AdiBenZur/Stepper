package step.impl;

import datadefinition.impl.DataDefinitionRegistry;
import datadefinition.impl.relation.RelationData;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import step.api.AbstractStepDefinition;
import io.api.DataNecessity;
import step.api.StepResult;
import io.impl.IODefinitionDataImpl;

public class PropertiesExporterStep extends AbstractStepDefinition {

    public PropertiesExporterStep() {
        super("Properties Exporter", true);

        // Step inputs
        addInput(new IODefinitionDataImpl("SOURCE", DataDefinitionRegistry.RELATION, DataNecessity.MANDATORY, "Source data"));

        // Step outputs
        addOutput(new IODefinitionDataImpl("RESULT", DataDefinitionRegistry.STRING, DataNecessity.NA, "Properties export result"));
    }

    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();
        StepResult result;

        try {
            RelationData relationSource = context.getDataValue("SOURCE", RelationData.class);

            StringBuilder resultBuilder = new StringBuilder(); // Output

            if(relationSource.getNumberOfRows() == 0) {
                context.addLog(new StepLogImpl(stepName, "There is no data to export."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended with warning. The relation given is empty."));
                result = StepResult.WARNING;
            }
            else {
                context.addLog(new StepLogImpl(stepName, "About to process " + relationSource.getNumberOfRows() + " lines of data."));

                for(String column : relationSource.getColumns()) {
                    for(Integer i = 1; i <= relationSource.getColumnByRowsOrder(column).size(); i ++)
                        resultBuilder.append("\trow-").append(i.toString()).append(".").append(column).append("=").append(relationSource.getColumnByRowsOrder(column).get(i - 1)).append("\n\t");
                }
                context.addLog(new StepLogImpl(stepName, "Extracted total of " + relationSource.getNumberOfRows() + " properties."));
                result = StepResult.SUCCESS;
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully."));
            }

            // Store data
            try {
                context.storeDataValue("RESULT", resultBuilder.toString());
            }
            catch(Exception e) {
                context.addLog(new StepLogImpl(stepName, "Error occurred while storing the data: " + e.getMessage()));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Fail storing data to context: " + e.getMessage() ));
                result = StepResult.FAILURE;
            }
        }
        catch (Exception e) {
            context.addLog(new StepLogImpl(stepName, "Error occurred while reading the data: " + e.getMessage()));
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot get data from context: " + e.getMessage() ));
            result =  StepResult.FAILURE;
        }

        return result;
    }

    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        // Not user friendly
        return null;
    }
}
