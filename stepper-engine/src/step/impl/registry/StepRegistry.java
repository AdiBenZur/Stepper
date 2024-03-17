package step.impl.registry;


import flow.execution.context.FlowExecutionContext;
import io.api.IODefinitionData;
import step.api.StepDefinition;
import step.api.StepResult;
import step.impl.*;

import java.util.List;

public enum StepRegistry implements StepDefinition {


    // All the steps.
    SPEND_SOME_TIME(new SpendSomeTimeStep()),
    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolderStep()),
    CSV_EXPORTER(new CSVExporterStep()),
    FILES_CONTENT_EXTRACTOR(new FilesContentExtractorStep()),
    FILES_DELETER(new FilesDeleterStep()),
    FILE_DUMPER(new FileDumperStep()),
    PROPERTIES_EXTRACTOR(new PropertiesExporterStep()),
    FILES_RENAMER(new FilesRenamerStep()),
    ZIPPER(new ZipperStep()),
    COMMAND_LINE(new CommandLineStep()),
    HTTP_CALL(new HttpCallStep()),
    TO_JSON(new ToJsonStep()),
    JSON_DATA_EXTRACTOR(new JsonDataExtractorStep())
    ;


    private final StepDefinition stepDefinition;

    StepRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }

    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }

    @Override
    public String getName() {
        return stepDefinition.getName();
    }

    @Override
    public boolean isReadOnly() {
        return stepDefinition.isReadOnly();
    }

    @Override
    public List<IODefinitionData> getInputs() {
        return stepDefinition.getInputs();
    }

    @Override
    public List<IODefinitionData> getOutputs() {
        return stepDefinition.getOutputs();
    }

    @Override
    public void setStepResult(StepResult status) {

    }

    @Override
    public StepResult run(FlowExecutionContext context) {
        return stepDefinition.run(context);
    }

    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        return null;
    }

    @Override
    public String getStepResult() {
        return null;
    }

    @Override
    public void initializeResult() {
        stepDefinition.initializeResult();
    }

}
