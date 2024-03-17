package flow.definition.impl;

import flow.definition.api.StepUsageDeclaration;
import step.api.StepDefinition;

public class StepUsageDeclarationImpl implements StepUsageDeclaration {

    private String finalStepName;
    private final StepDefinition stepDefinition;
    private  boolean skipIfFail;


    public StepUsageDeclarationImpl(String name, StepDefinition stepDefinition, boolean skipIfFail) {
        this.finalStepName = name;
        this.stepDefinition = stepDefinition;
        this.skipIfFail = skipIfFail;
    }


    public StepUsageDeclarationImpl(StepDefinition stepDefinition) {
        this(stepDefinition.getName(), stepDefinition, false);
    }


    public StepUsageDeclarationImpl(String name, StepDefinition stepDefinition) {
        this(stepDefinition.getName(), stepDefinition, false);
    }



    @Override
    public String getStepName() { return finalStepName; }

    @Override
    public StepDefinition getStepDefinition() { return stepDefinition; }

    @Override
    public boolean skipIfFail() { return skipIfFail; }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }

        StepUsageDeclarationImpl that = (StepUsageDeclarationImpl) obj;
        return finalStepName.equals(that.finalStepName);
    }

    @Override
    public int hashCode() {
        return finalStepName.hashCode();
    }

    @Override
    public void setFinalStepName(String finalStepName) {
        this.finalStepName = finalStepName; }

    @Override
    public void setSkipIfFail(boolean skipIfFail) {
        this.skipIfFail = skipIfFail; }
}
