package step.impl;

import datadefinition.impl.DataDefinitionRegistry;
import datadefinition.impl.enumerator.type.ZipperEnumerator;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import io.api.DataNecessity;
import io.impl.IODefinitionDataImpl;
import step.api.AbstractStepDefinition;
import step.api.StepResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ZipperStep extends AbstractStepDefinition {

    public ZipperStep() {
        super("Zipper", false);

        // Step inputs
        addInput(new IODefinitionDataImpl("SOURCE", DataDefinitionRegistry.STRING, DataNecessity.MANDATORY, "Source" ));
        addInput(new IODefinitionDataImpl("OPERATION", DataDefinitionRegistry.ZIPPER_ENUMERATOR, DataNecessity.MANDATORY, "Operation type" ));


        // Step outputs
        addOutput(new IODefinitionDataImpl("RESULT", DataDefinitionRegistry.STRING, DataNecessity.NA, "Zip operation result"));
    }


    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();

        StepResult result;
        String source;
        ZipperEnumerator operation;
        String resultOutput;

        try {
            source = context.getDataValue("SOURCE", String.class);
            try {
                operation = context.getDataValue("OPERATION", ZipperEnumerator.class);

                if(operation == ZipperEnumerator.UNZIP) {

                    // Check if the source is ok
                    if(!checkIfThePathIsFile(source)) {
                        context.addLog(new StepLogImpl(stepName, "Failed to unzip. The path given is not a file path."));
                        result = StepResult.FAILURE;
                        context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. The operation choose is unzip and the path given is not a file path."));
                        resultOutput = "Failure! The operation selected is unzip but the path given is not a file path.";
                    }
                    else {
                        if(!checkIfThePathEndsWithDotZip(source)) {
                            context.addLog(new StepLogImpl(stepName, "Failed to unzip. The path given is not ended with 'zip' ."));
                            result = StepResult.FAILURE;
                            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. The operation choose is unzip and the file given is not a compressed."));
                            resultOutput = "Failure! The operation selected is unzip but the path given is not a compressed file.";
                        }
                        else {
                            context.addLog(new StepLogImpl(stepName, "About to preform operation unzip on source " + source ));

                            // Unzip, do action

                            File sourceFile = new File(source);
                            File outFile = new File(sourceFile.getParentFile().getAbsolutePath());

                            try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(sourceFile.toPath()))) {
                                unzip(zis, outFile.toPath());

                                result = StepResult.SUCCESS;
                                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully. Success unzip file path: " + source));
                                resultOutput = "SUCCESS";
                            }
                            catch (Exception e) {
                                context.addLog(new StepLogImpl(stepName, "Failed to unzip."));
                                result = StepResult.FAILURE;
                                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Failed unzip the file: " + e.getMessage()));
                                resultOutput = "Failure! The operation unzip failed.";
                            }
                        }
                    }
                }
                else {
                    context.addLog(new StepLogImpl(stepName, "About to preform operation zip on source " + source ));

                    // Zip, do action
                    File sourceFile = new File(source);
                    String sourceName = sourceFile.getName();
                    String zipFilePath = Paths.get(sourceFile.getParent(), sourceName + ".zip").toString();

                    try (FileOutputStream fos = new FileOutputStream(zipFilePath);
                         ZipOutputStream zipOut = new ZipOutputStream(fos)) {
                        if (sourceFile.isDirectory()) {
                            zipDirectory(sourceFile, sourceFile.getName(), zipOut);
                        } else {
                            zipFile(sourceFile, "", zipOut);
                        }

                        context.addLog(new StepLogImpl(stepName, "The operation succeed."));
                        result = StepResult.SUCCESS;
                        context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully. Success zip file/ directory path: " + source));
                        resultOutput = "SUCCESS";
                    }
                    catch (Exception e) {
                        context.addLog(new StepLogImpl(stepName, "Failed to zip."));
                        result = StepResult.FAILURE;
                        context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Failed zip the file/ directory: " + e.getMessage()));
                        resultOutput = "Failure! The operation zip failed.";

                    }
                }

                // Store data
                try {
                    context.storeDataValue("RESULT", resultOutput);
                }
                catch (Exception e) {
                    context.addLog(new StepLogImpl(stepName, "Error occurred while storing the data: " + e.getMessage()));
                    context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Fail storing data to context: " + e.getMessage() ));
                    result = StepResult.FAILURE;
                    return result;
                }

            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "Failed to get operation input."));
                result = StepResult.FAILURE;
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot get data from context: " + e.getMessage() ));
                resultOutput = "Failure! Cannot get data from context.";

            }
        }
        catch (Exception e) {
            context.addLog(new StepLogImpl(stepName, "Failed to get source input."));
            result = StepResult.FAILURE;
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot get data from context: " + e.getMessage() ));
            resultOutput = "Failure! Cannot get data from context.";
        }

        return result;
    }

    @Override
    public StepResult validateInput(FlowExecutionContext context) {

        return null;
    }


    public Boolean checkIfThePathIsFile(String source) {
        File file = new File(source);
        return file.isFile();
    }


    public Boolean checkIfThePathEndsWithDotZip(String source) {
        return source.endsWith("zip");
    }


    private static void unzip(ZipInputStream zis, Path dest){
        ZipEntry entry;
        try{
            while((entry = zis.getNextEntry()) != null){
                File file = dest.resolve(entry.getName()).toFile();
                if(entry.isDirectory()){
                    file.mkdirs();
                }
                else{
                    file.getParentFile().mkdirs();
                    if(!file.exists())
                        file.createNewFile();
                    try(FileOutputStream fos = new FileOutputStream(file)){
                        int readChar = -1;
                        while((readChar = zis.read()) != -1){
                            fos.write(readChar);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void zipFile(File file, String basePath, ZipOutputStream zipOut) throws IOException {
        byte[] buffer = new byte[1024];

        try (FileInputStream fis = new FileInputStream(file)) {
            String entryPath = basePath.isEmpty() ? file.getName() : basePath + File.separator + file.getName();
            zipOut.putNextEntry(new ZipEntry(entryPath));

            int length;
            while ((length = fis.read(buffer)) > 0) {
                zipOut.write(buffer, 0, length);
            }

            zipOut.closeEntry();
        }
    }


    private static void zipDirectory(File directory, String basePath, ZipOutputStream zipOut) throws IOException {
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    zipDirectory(file, basePath + File.separator + file.getName(), zipOut);
                } else {
                    zipFile(file, basePath, zipOut);
                }
            }
        }
    }
}
