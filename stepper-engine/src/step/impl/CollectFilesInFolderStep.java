package step.impl;

import datadefinition.impl.DataDefinitionRegistry;
import datadefinition.impl.list.type.FileList;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import step.api.AbstractStepDefinition;
import io.api.DataNecessity;
import step.api.StepResult;
import io.impl.IODefinitionDataImpl;

import java.io.File;
import java.util.Arrays;

public class CollectFilesInFolderStep extends AbstractStepDefinition {

    public CollectFilesInFolderStep() {
        super("Collect Files In Folder", true);

        // Step inputs
        addInput(new IODefinitionDataImpl("FOLDER_NAME", DataDefinitionRegistry.STRING, DataNecessity.MANDATORY, "Folder name to scan"));
        addInput(new IODefinitionDataImpl("FILTER", DataDefinitionRegistry.STRING, DataNecessity.OPTIONAL, "Filter only these files"));

        // Step outputs
        addOutput(new IODefinitionDataImpl("FILES_LIST", DataDefinitionRegistry.FILE_LIST, DataNecessity.NA, "Files list"));
        addOutput(new IODefinitionDataImpl("TOTAL_FOUND", DataDefinitionRegistry.NUMBER, DataNecessity.NA, "Total files found"));

    }

    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();

        StepResult result;
        String folderName;
        String filter;
        FileList fileList = new FileList();

        try {
            folderName = context.getDataValue("FOLDER_NAME", String.class);
            try {
                filter = context.getDataValue("FILTER", String.class);
            }
            catch (Exception e) {
                // Filter not provided
                context.addLog(new StepLogImpl(stepName, "No filter provided."));
                filter = null;
            }

            File folder = new File(folderName);

            // Check if the folder exists
            if(folder.exists()) {

                // Check if path is a folder
                if(folder.isDirectory()) {

                    // The folder is ok, do action
                    context.addLog(new StepLogImpl(stepName, "Reading folder " + folderName + " content " + (filter != null ? " with filter: " + filter : "")));
                    Arrays.stream(folder.listFiles()).filter(File::isFile).forEach(fileList::add);
                    FileList remove = new FileList();
                    if(filter != null && !filter.isEmpty()) {
                        String finalFilterInput = filter;
                        fileList.stream().filter(f->!f.getName().endsWith(finalFilterInput)).forEach(remove::add);
                    }
                    fileList.removeAll(remove);

                    context.addLog(new StepLogImpl(stepName, "Find " + fileList.size() + " files in folder matching the filter."));

                    // Check if the folder was empty
                    if(fileList.isEmpty()) {
                        context.addLog(new StepLogImpl(stepName, "The folder is empty."));
                        context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended with warning. The folder given is empty."));
                        result = StepResult.WARNING;
                    }
                    else {
                        // The folder is not empty
                        context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully."));
                        result = StepResult.SUCCESS;
                    }

                    // Store data
                    try {
                        context.storeDataValue("FILES_LIST", fileList);
                        context.storeDataValue("TOTAL_FOUND", fileList.size());
                    }
                    catch (Exception e) {
                        context.addLog(new StepLogImpl(stepName, "Error occurred while storing the data: " + e.getMessage()));
                        context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Fail storing data to context: " + e.getMessage() ));
                        result = StepResult.FAILURE;
                    }

                }
                else {
                    // The folder is not a directory
                    context.addLog(new StepLogImpl(stepName, "The folder inserted is not a folder."));
                    context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. The folder given is not a folder."));
                    result = StepResult.FAILURE;
                }
            }
            else {
                // The folder does not exist
                context.addLog(new StepLogImpl(stepName, "The folder inserted does not exist."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. The folder given does not exist."));
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
        return null;
    }

}
