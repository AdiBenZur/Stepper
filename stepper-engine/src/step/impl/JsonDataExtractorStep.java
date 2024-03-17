package step.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import datadefinition.impl.DataDefinitionRegistry;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import io.api.DataNecessity;
import io.impl.IODefinitionDataImpl;
import step.api.AbstractStepDefinition;
import step.api.StepResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class JsonDataExtractorStep extends AbstractStepDefinition {

    public JsonDataExtractorStep() {
        super("Json Data Extractor", true);

        // Step inputs
        addInput(new IODefinitionDataImpl("JSON", DataDefinitionRegistry.JSON, DataNecessity.MANDATORY, "Json source" ));
        addInput(new IODefinitionDataImpl("JSON_PATH", DataDefinitionRegistry.STRING, DataNecessity.MANDATORY, "data" ));


        // Step outputs
        addOutput(new IODefinitionDataImpl("VALUE", DataDefinitionRegistry.STRING, DataNecessity.NA, "Data value"));
    }

    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();

        StepResult result;
        String jsonPath;
        JsonObject json;
        StringBuilder value = new StringBuilder();


        try {
            json = context.getDataValue("JSON", JsonObject.class);
            jsonPath = context.getDataValue("JSON_PATH", String.class);

            try {
                List<String> json_paths = Arrays.asList(jsonPath.split("\\|"));
                String jsonString = json.toString();

                // Parse the JSON string
                Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
                context.addLog(new StepLogImpl(stepName, "Start the operation."));

                for(String json_Path : json_paths){
                    if(json_Path.length() == 0)
                        continue;
                    // Extract the data using JsonPath
                    Object resultVal = JsonPath.read(document, json_Path);

                    List<Object> extractedData = new ArrayList<>();

                    if (resultVal instanceof List<?>) {
                        // The result is a list of values
                        extractedData = (List<Object>) resultVal;
                    } else {
                        // The result is a single value
                        extractedData.add(resultVal);
                    }
                    // Append the data with commas
                    StringBuilder tmp = new StringBuilder();
                    for (int i = 0; i < extractedData.size(); i++) {
                        tmp.append(extractedData.get(i).toString());
                        if (i < extractedData.size() - 1) {
                            tmp.append(", ");
                        }
                    }
                    if(extractedData.size() == 0)
                        context.addLog(new StepLogImpl(stepName, "No value found for json path " + json_Path));
                    else {
                        context.addLog(new StepLogImpl(stepName, "Extracting data " + json_Path + ". Value: " + tmp.toString()));

                        if(value.toString().length() > 0)
                            value.append(", " + tmp);
                        else
                            value.append(tmp);
                    }
                }

                result = StepResult.SUCCESS;
                context.addLog(new StepLogImpl(stepName, "The operation ended."));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully"));
            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "The operation failed."));
                result = StepResult.FAILURE;
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Something went wrong: " + e.getMessage() ));
                value = null;
            }

            // Store data
            try {
                context.storeDataValue("VALUE", value.toString());
            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "Error occurred while storing the data: " + e.getMessage()));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Fail storing data to context: " + e.getMessage() ));
                result = StepResult.FAILURE;
            }
        }
        catch (Exception e) {
            context.addLog(new StepLogImpl(stepName, "Failed to get input."));
            result = StepResult.FAILURE;
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot get data from context: " + e.getMessage() ));

        }
        return result;
    }

    private List<String> extractJsonPathList(String fullJsonPath){
        return Arrays.asList(fullJsonPath.split(Pattern.quote("|")));
    }

    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        return null;
    }
}
