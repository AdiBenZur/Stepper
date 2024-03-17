package step.impl;

import datadefinition.impl.DataDefinitionRegistry;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import step.api.AbstractStepDefinition;
import io.api.DataNecessity;
import step.api.StepResult;
import io.impl.IODefinitionDataImpl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDumperStep extends AbstractStepDefinition {

    public FileDumperStep() {
        super("File Dumper", true);

        // Step input
        addInput(new IODefinitionDataImpl("CONTENT", DataDefinitionRegistry.STRING, DataNecessity.MANDATORY, "Content"));
        addInput(new IODefinitionDataImpl("FILE_NAME", DataDefinitionRegistry.STRING, DataNecessity.MANDATORY, "Target file path"));

        // Step output
        addOutput(new IODefinitionDataImpl("RESULT", DataDefinitionRegistry.STRING, DataNecessity.NA, "File Creation Result"));

    }


    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();
        StepResult result;

        String content;
        String targetFilePath;
        String stepOutput;

        try {
            content = context.getDataValue("CONTENT", String.class);
            targetFilePath = context.getDataValue("FILE_NAME", String.class);

            File file = new File(targetFilePath);

            try {

                // Create a new file
                if(file.createNewFile()) {
                    if(content.isEmpty()) {
                        context.addLog(new StepLogImpl(stepName, "No content to write."));
                        result = StepResult.WARNING;
                        context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended with warning due to an empty content given."));
                        stepOutput = "Success";
                    }
                    else {

                        // Write to file
                        try (FileWriter fileWriter = new FileWriter(file)) {
                            fileWriter.write(content);
                            context.addLog(new StepLogImpl(stepName, "The file created successfully and filled with the content given." ));
                            stepOutput = "Success";
                            result = StepResult.SUCCESS;
                            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully."));

                        }
                        catch (IOException e) {
                            context.addLog(new StepLogImpl(stepName, "Failed to write content to file: " + e.getMessage()));
                            stepOutput = "Failed to write content to file.";
                            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. An error occurred while writing the content to file."));
                            result = StepResult.FAILURE;
                        }
                    }
                }
                else {
                    // The file exists
                    context.addLog(new StepLogImpl(stepName, "The file already exists."));
                    stepOutput = "Failed- The file already exists.";
                    result = StepResult.FAILURE;
                    context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. The file is already exists."));
                }

            }
            catch (IOException e) {
                context.addLog(new StepLogImpl(stepName, "Failed in creating file: " + e.getMessage()));
                result = StepResult.FAILURE;
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. An error occurred while creating the file."));
                stepOutput = "Failed in creation new file.";
            }
        }
        catch (Exception e) {
            context.addLog(new StepLogImpl(stepName, "Error occurred while reading the data: " + e.getMessage()));
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot get data from context: " + e.getMessage() ));
            result =  StepResult.FAILURE;
            stepOutput = "failed to get data from context.";
        }

        // Store data
        context.storeDataValue("RESULT", stepOutput);
        return result;
    }

    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        return null;
    }
}
