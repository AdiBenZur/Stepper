package servlets;

import com.google.gson.Gson;
import dto.io.IODefinitionDataDTO;
import dto.flow.FlowInformationDTO;
import dto.io.IOInStepMappingDTO;
import dto.step.StepUsageDeclarationDTO;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import io.api.DataNecessity;
import io.api.IODefinitionData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.system.Manager;
import manager.system.operation.FlowInformation;
import servlets.util.Constants;
import servlets.util.ServletContextManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "FlowInformationServlet", urlPatterns = "/flow/information")
public class FlowInformationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get flow information by name

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        String flowName = req.getParameter(Constants.FLOW_INFORMATION_NAME);

        FlowDefinition flow = manager.getStepper().getAllFlows()
                .stream()
                .filter(flowDefinition -> flowDefinition.getName().equals(flowName))
                .findFirst().orElse(null);

        if(flow != null) {
            FlowInformation flowInformation = flow.getFlowInformation();

            // Convert formal outputs to dto
            List<String> formalOutputs = flowInformation.getFormalOutputs()
                    .stream()
                    .map(IODefinitionData::getName)
                    .collect(Collectors.toList());


            // Convert from steps to steps dto
            List<StepUsageDeclarationDTO> listOfSteps = new ArrayList<>();
            for (StepUsageDeclaration stepUsageDeclaration : flowInformation.getSteps()) {

                Map<IODefinitionData, IODefinitionData> stepMapping = flow.getMappings().get(stepUsageDeclaration);
                List<IOInStepMappingDTO> inputs = new ArrayList<>();
                List<IOInStepMappingDTO> outputs = new ArrayList<>();

                for(IODefinitionData data : stepMapping.values()) {
                    if(data.getNecessity() != DataNecessity.NA) {
                        // Input
                        IODefinitionData result = flow.isInputConnectToOutput(stepUsageDeclaration, data);

                        if(result != null)
                            inputs.add(new IOInStepMappingDTO(data.getName(), data.getNecessity().toString(), result.getName(), flow.fromOutputToStepProduce(result).getStepName()));
                        else
                            inputs.add(new IOInStepMappingDTO(data.getName(), data.getNecessity().toString(), null, null));
                    }
                    else {
                        // OutPut
                        IODefinitionData result = flow.isOutputConnectToInput(stepUsageDeclaration, data);
                        if(result != null)
                            outputs.add(new IOInStepMappingDTO(data.getName(), data.getNecessity().toString(), result.getName(), flow.fromOutputToStepProduce(result).getStepName()));
                        else
                            outputs.add(new IOInStepMappingDTO(data.getName(), data.getNecessity().toString(), null, null));
                    }
                }

                StepUsageDeclarationDTO stepUsageDeclarationDTO = new StepUsageDeclarationDTO(stepUsageDeclaration, inputs, outputs);
                listOfSteps.add(stepUsageDeclarationDTO);
            }

            // Convert free inputs to dto
            List<IODefinitionDataDTO> freeInputs = flowInformation.getFreeInputs()
                    .stream()
                    .map(IODefinitionDataDTO::new)
                    .collect(Collectors.toList());

            // Convert all outputs to dto
            List<IODefinitionDataDTO> allOutputs = flowInformation.getAllOutputs()
                    .stream()
                    .map(IODefinitionDataDTO::new)
                    .collect(Collectors.toList());

            // Convert from output to step produce
            Map<String, String> fromOutputToStepProduce = new HashMap<>();
            for(IODefinitionData output : flowInformation.getFromOutputToStep().keySet()) {

                // Get correlate object
                StepUsageDeclaration stepUsageDeclaration = flowInformation.getFromOutputToStep().get(output);

                fromOutputToStepProduce.put(output.getName(), stepUsageDeclaration.getStepName());
            }

            // Convert from input to steps use
            Map<String, List<String>> fromInputToStepsUse = new HashMap<>();
            for(IODefinitionData input : flowInformation.getFromInputToSteps().keySet()) {
                List<String> list = new ArrayList<>();
                for(StepUsageDeclaration step : flowInformation.getFromInputToSteps().get(input)) {
                    list.add(step.getStepName());
                }
                fromInputToStepsUse.put(input.getName(), list);
            }

            FlowInformationDTO flowInformationToPass = new FlowInformationDTO(flowInformation.getName(), flowInformation.getFlowDescription()
                    , formalOutputs,flowInformation.getRadOnly(), listOfSteps, freeInputs, allOutputs, fromOutputToStepProduce, fromInputToStepsUse);

            Gson gson = new Gson();
            String flowInformationDto = gson.toJson(flowInformationToPass);
            out.print(flowInformationDto);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }


}
