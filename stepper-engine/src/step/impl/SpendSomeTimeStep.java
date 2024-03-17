package step.impl;

import datadefinition.impl.DataDefinitionRegistry;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import io.api.DataNecessity;
import step.api.*;
import io.impl.IODefinitionDataImpl;

public class SpendSomeTimeStep extends AbstractStepDefinition {


    public SpendSomeTimeStep() {
        super("Spend Some Time", true);

        // Step inputs
        addInput(new IODefinitionDataImpl("TIME_TO_SPEND",  DataDefinitionRegistry.NUMBER, DataNecessity.MANDATORY, "Total sleeping time (sec)"));
    }



    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();
        StepResult stepResult = validateInput(context);

        if(stepResult == StepResult.FAILURE) {
            context.addLog(new StepLogImpl(stepName, "Step failure. The input is not valid- cannot sleep for not positive amount of seconds."));
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed due to an invalid input."));
            return stepResult;
        }

        if(stepResult == StepResult.SUCCESS) {
            try {
                Number timeToSpend = context.getDataValue("TIME_TO_SPEND", Number.class);
                context.addLog(new StepLogImpl(stepName, "About sleeping for " + timeToSpend.intValue() + " seconds..."));

                Thread.sleep(timeToSpend.longValue() * 1000);

                context.addLog(new StepLogImpl(stepName, "Done sleeping..."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "Step ended successfully. Slept for " + timeToSpend.intValue() + " seconds."));
            }
            catch(Exception e) {
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "Step failed- something went wrong: " + e.getMessage()));
                context.addLog(new StepLogImpl(stepName, "Something went wrong. Sleeping interrupted"));
                return StepResult.FAILURE;
            }
        }
        return stepResult;
    }


    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();
        try {
            Number timeToSpend = context.getDataValue("TIME_TO_SPEND", Number.class);
            if(timeToSpend.intValue() > 0)
                return StepResult.SUCCESS;
            else
                return  StepResult.FAILURE;
        }
        catch (Exception e) {
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "Cannot get data from context: " + e.getMessage()));
            return StepResult.FAILURE;
        }
    }


}
