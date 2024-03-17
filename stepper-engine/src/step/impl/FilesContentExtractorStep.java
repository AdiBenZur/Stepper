package step.impl;

import datadefinition.impl.DataDefinitionRegistry;
import datadefinition.impl.list.type.FileList;
import datadefinition.impl.relation.RelationData;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import step.api.AbstractStepDefinition;
import io.api.DataNecessity;
import step.api.StepResult;
import io.impl.IODefinitionDataImpl;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FilesContentExtractorStep extends AbstractStepDefinition {

    public FilesContentExtractorStep() {
        super("Files Content Extractor", true);

        // Step Inputs
        addInput(new IODefinitionDataImpl("FILES_LIST", DataDefinitionRegistry.FILE_LIST, DataNecessity.MANDATORY, "Files to extract"));
        addInput(new IODefinitionDataImpl("LINE", DataDefinitionRegistry.NUMBER, DataNecessity.MANDATORY, "Line number to extract"));

        // Step outputs
        addOutput(new IODefinitionDataImpl("DATA", DataDefinitionRegistry.RELATION, DataNecessity.NA, "Data extraction"));
    }

    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();
        FileList fileList = new FileList();
        StepResult result;

        List<String> columns = Arrays.asList("Index", "Original file name", "Extracted data");
        RelationData relationResult = new RelationData(columns);

        try {
            fileList = context.getDataValue("FILES_LIST", FileList.class);
            Integer line = (Integer) context.getDataValue("LINE", Number.class);

            if(fileList.size() == 0) {
                context.addLog(new StepLogImpl(stepName, "No files to extract from."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully. The file list given is empty."));
                result = StepResult.SUCCESS;
            }
            else {
                // List not empty
                for (Integer i = 1; i <= fileList.size(); i++) {
                    File file = fileList.get(i - 1);
                    context.addLog(new StepLogImpl(stepName, "About to work on file " + file.getName()));

                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                        // Check if the line number exists
                        Optional<String> extractedLine = reader.lines().skip(line - 1).limit(1).findFirst();
                        relationResult.addRow(Arrays.asList(i.toString(), file.getName(), extractedLine.orElse("No such line")));
                    }
                    catch (FileNotFoundException e) {
                        context.addLog(new StepLogImpl(stepName, "Problem extracting line number " + line + " from file " + file.getName()));
                        relationResult.addRow(Arrays.asList(i.toString(), file.getName(), "File not found"));
                    }
                    catch (IOException e) {
                        context.addLog(new StepLogImpl(stepName, "Problem extracting line number " + line + " from file " + file.getName()));
                        relationResult.addRow(Arrays.asList(i.toString(), file.getName(), "Problem reading file"));
                    }
                }
                result  = StepResult.SUCCESS;
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully."));
            }

            // Store data
            try {
                context.storeDataValue("DATA", relationResult);
            }
            catch (Exception e) {
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
        // The line input check is already include in the run method
       return  null;
    }
}
