package step.impl;

import com.google.gson.JsonObject;
import datadefinition.impl.DataDefinitionRegistry;
import datadefinition.impl.enumerator.type.MethodEnumerator;
import datadefinition.impl.enumerator.type.ProtocolEnumerator;
import flow.execution.context.FlowExecutionContext;
import flow.execution.log.StepLogImpl;
import flow.execution.summaryline.StepSummaryLineImpl;
import io.api.DataNecessity;
import io.impl.IODefinitionDataImpl;
import okhttp3.*;
import step.api.AbstractStepDefinition;
import step.api.StepResult;

public class HttpCallStep extends AbstractStepDefinition {

    public HttpCallStep() {
        super("HTTP Call", false);

        // Step inputs
        addInput(new IODefinitionDataImpl("RESOURCE", DataDefinitionRegistry.STRING, DataNecessity.MANDATORY, "Resource Name (include query parameters)"));
        addInput(new IODefinitionDataImpl("ADDRESS", DataDefinitionRegistry.STRING, DataNecessity.MANDATORY, "Domain:Port"));
        addInput(new IODefinitionDataImpl("PROTOCOL", DataDefinitionRegistry.PROTOCOL_ENUMERATOR, DataNecessity.MANDATORY, "protocol"));
        addInput(new IODefinitionDataImpl("METHOD", DataDefinitionRegistry.METHOD_ENUMERATOR, DataNecessity.OPTIONAL, "Method"));
        addInput(new IODefinitionDataImpl("BODY", DataDefinitionRegistry.JSON, DataNecessity.OPTIONAL, "Request Body"));

        // Step outputs
        addOutput(new IODefinitionDataImpl("CODE", DataDefinitionRegistry.NUMBER, DataNecessity.NA, "Response code"));
        addOutput(new IODefinitionDataImpl("RESPONSE_BODY", DataDefinitionRegistry.STRING, DataNecessity.NA, "Response body"));
    }

    @Override
    public StepResult run(FlowExecutionContext context) {
        String stepName = context.getCurrentRunningStep().getStepName();

        StepResult result;
        String resource;
        String address;
        ProtocolEnumerator protocol;
        MethodEnumerator method;
        JsonObject body;
        Number code;
        String responseBody;

        try {
            resource = context.getDataValue("RESOURCE", String.class);
            address = context.getDataValue("ADDRESS", String.class);
            protocol = context.getDataValue("PROTOCOL", ProtocolEnumerator.class);

            try {
                method = context.getDataValue("METHOD", MethodEnumerator.class);
            }
            catch (Exception e) {
                method = MethodEnumerator.GET;
                context.addLog(new StepLogImpl(stepName, "No method provided."));
            }

            try {
                body = context.getDataValue("BODY", JsonObject.class);
            }
            catch (Exception e) {
                body = null;
                context.addLog(new StepLogImpl(stepName, "No body provided."));
            }

            // ensure address doesn't end with '/' and resource doesn't start with '/'
            if (address.endsWith("/")) address = address.substring(0, address.length() - 1);
            if (resource.startsWith("/")) resource = resource.substring(1);

            String bodyString = (body == null ? "" : body.toString());

            String url = protocol + "://" + address + "/" + resource;
            Request.Builder requestBuilder = new Request.Builder().url(url);


            switch (method) {
                case GET:
                    requestBuilder.get();
                    break;
                case POST:
                    requestBuilder.post(RequestBody.create(MediaType.parse("application/json"), bodyString));
                    break;
                case PUT:
                    requestBuilder.put(RequestBody.create(MediaType.parse("application/json"), bodyString));
                    break;
                case DELETE:
                    requestBuilder.delete(RequestBody.create(MediaType.parse("application/json"), bodyString));
                    break;
                default:
                    context.addLog(new StepLogImpl(stepName, "Step failed dut to invalid method."));
                    context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. The http verb given is not get/ post/ put/ delete."));
                    result = StepResult.FAILURE;
            }

            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(requestBuilder.build());
            try {
                context.addLog(new StepLogImpl(stepName, "About to invoke http request: " + protocol + " | " + method + " | " + address + " | " + resource));
                Response response = call.execute();
                context.addLog(new StepLogImpl(stepName, "Received Response. Status code: " + response.code()));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step ended successfully. The request submitted."));
                result = StepResult.SUCCESS;
                code = response.code();
                responseBody = response.body() != null ? response.body().string() : "";
            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "The request failed to send."));
                result = StepResult.FAILURE;
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot send the request: " + e.getMessage() ));
                code = 0;
                responseBody = "";
            }

            // Store data
            try {
                context.storeDataValue("CODE", code);
                context.storeDataValue("RESPONSE_BODY", responseBody);

            }
            catch (Exception e) {
                context.addLog(new StepLogImpl(stepName, "Error occurred while storing the data: " + e.getMessage()));
                context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Fail storing data to context: " + e.getMessage() ));
                result = StepResult.FAILURE;
                return result;
            }
        }
        catch (Exception e) {
            context.addLog(new StepLogImpl(stepName, "Failed to get input."));
            result = StepResult.FAILURE;
            context.addStepSummaryLine(new StepSummaryLineImpl(stepName, "The step failed. Cannot get data from context: " + e.getMessage() ));
            code = null;
            responseBody = "";
        }
        return result;
    }


    @Override
    public StepResult validateInput(FlowExecutionContext context) {
        return null;
    }
}
