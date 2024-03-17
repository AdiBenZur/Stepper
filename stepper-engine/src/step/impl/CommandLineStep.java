package step.impl;

import datadefinition.impl.DataDefinitionRegistry;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import io.api.DataNecessity;
import io.impl.IODefinitionDataImpl;
import step.api.AbstractStepDefinition;
import step.api.StepResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLineStep extends AbstractStepDefinition {


    public CommandLineStep() {
        super("Command Line", false);

        // Step inputs
        addInput(new IODefinitionDataImpl("COMMAND", DataDefinitionRegistry.STRING, DataNecessity.MANDATORY, "Command" ));
        addInput(new IODefinitionDataImpl("ARGUMENTS", DataDefinitionRegistry.STRING, DataNecessity.OPTIONAL, "Command arguments" ));

        // Step outputs
        addOutput(new IODefinitionDataImpl("RESULT", DataDefinitionRegistry.STRING, DataNecessity.NA, "Command output"));
    }


    @Override
    public StepResult run(FlowExecutionContext context) {

        String stepName = context.getCurrentRunningStep().getStepName();

        String command;
        String arguments;
        String resultOutput = "";

        try {
            command = context.getDataValue("COMMAND", String.class);
            try {
                arguments = context.getDataValue("ARGUMENTS", String.class);
            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "No arguments provided."));
                arguments = "";
            }

            context.addLog(new StepLogImpl(stepName, "About to invoke " + command + " " + arguments ));

            // Do action
            String args = arguments.replace(",", "");
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("cmd.exe", "/c", command + (args.equals("") ? args : " " + args));

            // Run command

            try {
                Process p = pb.start();

                // Read process builder output to string
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append(System.getProperty("line.separator"));
                }


                resultOutput = builder.toString();
                context.addLog(new StepLogImpl(stepName, "Operation ended successfully."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully."));
            }
            catch (IOException e) {
                context.addLog(new StepLogImpl(stepName, "Operation failed."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully but an error occurred: error occurred while preform the operation: " + e.getMessage() ));
            }

            // Store data
            try {
                context.storeDataValue("RESULT", resultOutput);
            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "Error occurred while storing the data: " + e.getMessage()));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully but an error occurred: Fail storing data to context, " + e.getMessage() ));
            }

        }
        catch (Exception e) {
            context.addLog(new StepLogImpl(stepName, "Failed to get command input."));
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully but an error occurred: Cannot get data from context, " + e.getMessage() ));
        }

        return StepResult.SUCCESS;
    }

    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        return null;
    }
}
