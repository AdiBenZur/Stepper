package flow.definition.api;

import step.api.StepDefinition;

public interface StepUsageDeclaration {

    String getStepName(); // The final name.
    StepDefinition getStepDefinition();
    boolean skipIfFail();
    boolean equals(Object obj);
    int hashCode();
    void setFinalStepName(String finalStepName);
    void setSkipIfFail(boolean skipIfFail);

}
