package xml.parse.impl;


import flow.definition.api.StepUsageDeclaration;
import flow.definition.impl.StepUsageDeclarationImpl;
import step.api.StepDefinition;
import step.impl.registry.StepRegistry;
import xml.jaxb.schema.generated.STStepInFlow;
import xml.parse.api.Parser;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class StepParser implements Parser {
    private STStepInFlow stStep = null;
    private StepUsageDeclaration stepUsageDeclaration = null;


    public StepParser(STStepInFlow stStep) {
        this.stStep = stStep;
    }

    public StepParser() {}

    @Override
    public List<String> parse() {
        List<String> errors = new ArrayList<>();

        // Find step in registry by name
        StepDefinition stepDefinition = null;

        for(StepRegistry stepRegistry : StepRegistry.values()) {
            if(stepRegistry.getName().equals(stStep.getName())) {
                stepDefinition = stepRegistry.getStepDefinition();
                break;
            }
        }

        // Check if found
        if(stepDefinition == null) {
            errors.add("Step " + stStep.getName() + " is not defined in the system.");
            return errors;
        }

        // Check if the flow should continue if the step failed
        Optional<Boolean> skipIfFailed = Optional.ofNullable(stStep.isContinueIfFailing());

        // Check whether 'skipIfFailed' is empty
        if(!skipIfFailed.isPresent()) {
            skipIfFailed = Optional.of(false); // set as false
        }

        // Find if the step has alias
        Optional<String> alias = Optional.ofNullable(stStep.getAlias());

        // Check if 'alias' is empty
        if(!alias.isPresent()) {
            alias = Optional.of(stStep.getName()); // Set step alias to be the regular name
        }

        stepUsageDeclaration = new StepUsageDeclarationImpl(alias.get(), stepDefinition, skipIfFailed.get());
        return errors;
    }

    public StepUsageDeclaration getStepUsageDeclaration() { return stepUsageDeclaration; }

    public StepParser load(STStepInFlow stStep) {
        this.stStep = stStep;
        this.stepUsageDeclaration = null;
        return this;
    }

    @Override
    public StepUsageDeclaration getObject() {
        return stepUsageDeclaration;
    }
}
