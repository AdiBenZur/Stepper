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

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FilesRenamerStep extends AbstractStepDefinition {

    public FilesRenamerStep() {
        super("Files Renamer", false);

        // Step inputs
        addInput(new IODefinitionDataImpl("FILES_TO_RENAME", DataDefinitionRegistry.FILE_LIST, DataNecessity.MANDATORY, "Files to rename"));
        addInput(new IODefinitionDataImpl("PREFIX", DataDefinitionRegistry.STRING, DataNecessity.OPTIONAL, "Add this prefix"));
        addInput(new IODefinitionDataImpl("SUFFIX", DataDefinitionRegistry.STRING, DataNecessity.OPTIONAL, "Append this suffix"));

        // Step outputs
        addOutput(new IODefinitionDataImpl("RENAME_RESULT", DataDefinitionRegistry.RELATION, DataNecessity.NA, "Rename operation summary"));

    }


    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();

        List<String> columns = Arrays.asList("Index", "Original file name", "New file name");
        RelationData relationResult = new RelationData(columns);

        StepResult result;

        FileList fileList;
        String prefix;
        String suffix;

        try {
            fileList = context.getDataValue("FILES_TO_RENAME", FileList.class);
            try {
                prefix = context.getDataValue("PREFIX", String.class);
            }
            catch (Exception e) {
                prefix = "";
                context.addLog(new StepLogImpl(stepName, "No prefix provided."));
            }
            try {
                suffix = context.getDataValue("SUFFIX", String.class);
            }
            catch (Exception e) {
                suffix = "";
                context.addLog(new StepLogImpl(stepName, "No suffix provided."));
            }
            context.addLog(new StepLogImpl(stepName, "About to start rename " + fileList.size() + " files." +
                    ( !prefix.equals("") ? (" Adding Prefix: " + prefix + ";") : "") +
                    ( !suffix.equals("") ? (" Adding Suffix: " + suffix + ";") : "")));


            if(fileList.size() == 0) {
                context.addLog(new StepLogImpl(stepName, "No files to rename."));
                result = StepResult.SUCCESS;
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully. The file list given is empty."));
            }
            else {
                String failToRename = ""; // for summary line
                Integer rowNumber = 1; // for relation

                for(File file : fileList) {
                    String originalName = file.getName();
                    String newName = prefix;
                    String name = originalName.substring(0, originalName.lastIndexOf('.'));
                    String extension = originalName.substring(originalName.lastIndexOf('.'));
                    newName += name + suffix + extension;


                    if(file.renameTo(new File(file.getParent() + "\\" + newName))) {
                        relationResult.addRow(Arrays.asList(rowNumber.toString(), originalName, newName));
                        rowNumber ++;
                    }
                    else {
                        relationResult.addRow(Arrays.asList(rowNumber.toString(), originalName, originalName));
                        context.addLog(new StepLogImpl(stepName, "Problem renaming file " + file.getAbsolutePath()));
                        failToRename += file.getAbsolutePath() + '\n';
                    }
                }



                // Check the results
                if(failToRename.equals("")) {
                    // If succeed to rename all files
                    context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully. All the file names have been changed."));
                    result = StepResult.SUCCESS;
                }
                else {
                    // At least one filed
                    context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended with warning. Failed to rename the following files : " + failToRename));
                    result = StepResult.WARNING;
                }
            }

            // Store data
            try {
                context.storeDataValue("RENAME_RESULT" , relationResult);
            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "Error occurred while storing the data: " + e.getMessage()));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot store data to context: " + e.getMessage() ));
                result = StepResult.FAILURE;
            }
        }
        catch (Exception e) {
            context.addLog(new StepLogImpl(stepName, "Error occurred while reading file list: " + e.getMessage()));
            result = StepResult.FAILURE;
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot get data from context: " + e.getMessage() ));
        }

        return result;
    }


    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        // Not User friendly
        return null;
    }
}
