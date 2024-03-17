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

public class CSVExporterStep extends AbstractStepDefinition {

    public CSVExporterStep() {
        super("CSV Exporter", true);

        // Step inputs
        addInput(new IODefinitionDataImpl("SOURCE", DataDefinitionRegistry.RELATION, DataNecessity.MANDATORY, "Source data"));

        // Step outputs
        addOutput(new IODefinitionDataImpl("RESULT", DataDefinitionRegistry.STRING, DataNecessity.NA, "CVS export result"));
    }


    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();
        StepResult result ;

        try {
            RelationData relationSource = context.getDataValue("SOURCE", RelationData.class);

            StringBuilder resultBuilder = new StringBuilder(); // Output
            relationSource.getColumns().forEach(column -> resultBuilder.append(column).append(","));
            // Replace last ","
            resultBuilder.replace(resultBuilder.length() - 1, resultBuilder.length(), "\n");

            if(relationSource.getNumberOfRows() == 0) {
                context.addLog(new StepLogImpl(stepName, "There is no data to export."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended with warning. The relation given is empty."));
                result = StepResult.WARNING;
            }
            else {
                for(Integer i = 0; i < relationSource.getNumberOfRows(); i ++) {

                    // Add to the result
                    relationSource.getRowByColumnsOrder(i).forEach(value -> resultBuilder.append("\t").append(value).append(", "));
                    // replace last ","
                    resultBuilder.replace(resultBuilder.length() - 2, resultBuilder.length(), "\n");
                }
                result = StepResult.SUCCESS;
                context.addLog(new StepLogImpl(stepName, "End to convert to CSV."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully."));
            }

            // store data
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
