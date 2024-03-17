package step.impl;

import datadefinition.impl.DataDefinitionRegistry;
import datadefinition.impl.file.FileData;
import datadefinition.impl.list.type.FileList;
import datadefinition.impl.list.type.StringList;
import datadefinition.impl.mapping.MappingData;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import step.api.AbstractStepDefinition;
import io.api.DataNecessity;
import step.api.StepResult;
import io.impl.IODefinitionDataImpl;

public class FilesDeleterStep extends AbstractStepDefinition {

    public FilesDeleterStep() {
        super("Files Deleter", false);

        // Step inputs
        addInput(new IODefinitionDataImpl("FILES_LIST", DataDefinitionRegistry.FILE_LIST, DataNecessity.MANDATORY, "Files to delete" ));

        // Step outputs
        addOutput(new IODefinitionDataImpl("DELETED_LIST", DataDefinitionRegistry.STRING_LIST, DataNecessity.NA, "Files failed to be deleted"));
        addOutput(new IODefinitionDataImpl("DELETION_STATS", DataDefinitionRegistry.MAPPING, DataNecessity.NA,  "Deletion summary results"));

    }

    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();

        FileList fileList = new FileList();
        StringList failedToDeleteList = new StringList();

        Integer failed = 0;
        Integer success = 0;

        StepResult result;

        try {
            fileList = context.getDataValue("FILES_LIST", FileList.class);

            if(!fileList.isEmpty()) {
                context.addLog(new StepLogImpl(stepName, "About to start delete " + fileList.size() + " files."));

                // Delete the files
                for (FileData file : fileList) {
                    if (!file.delete()) {
                        context.addLog(new StepLogImpl(stepName, "Failed to delete file: " + file.getAbsolutePath()));
                        failed++;
                        failedToDeleteList.add(file.getName());
                    } else
                        success++;
                }
            }
            else {
                context.addLog(new StepLogImpl(stepName, "The file list given is empty"));
            }


            // Store data
            try {
                context.storeDataValue("DELETED_LIST", failedToDeleteList);
                context.storeDataValue("DELETION_STATS", new MappingData<Integer, Integer>(success, failed));
            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "Error occurred while storing the data: " + e.getMessage()));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Fail storing data to context: " + e.getMessage() ));
                result = StepResult.FAILURE;
                return result;
            }

            // Update step result

            // Check if all the files deleted successfully or all the file list is empty
            if(failedToDeleteList.isEmpty()) {
                result = StepResult.SUCCESS;
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully. "
                        + (fileList.isEmpty() ? "the file list is empty, no files hava been deleted." : "All files deleted successfully.")));
            }
            else {
                // Check if not all the files deleted
                if(failedToDeleteList.size() != fileList.size()) {
                    result = StepResult.WARNING;
                    context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended with warning. " + success +
                            " files deleted successfully out of " + fileList.size()));
                }
                else {
                    // All the files not deleted
                    if(failedToDeleteList.size() == fileList.size()) {
                        result = StepResult.FAILURE;
                        context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. No files were deleted."));
                    }
                    else {
                        result = StepResult.SUCCESS;
                    }
                }
            }
        }
        catch (Exception e) {
            context.addLog(new StepLogImpl(stepName, "Failed to get files list."));
            result = StepResult.FAILURE;
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot get data from context: " + e.getMessage() ));
        }


        return result;
    }

    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        // Not user friendly
        return null;
    }
}
