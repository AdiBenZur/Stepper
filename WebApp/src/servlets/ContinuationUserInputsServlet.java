package servlets;

import com.google.gson.Gson;
import dto.io.IODefinitionDataDTO;
import dto.io.UserFreeInputsDTO;
import flow.definition.api.FlowDefinition;
import flow.execution.FlowExecution;
import io.api.IODefinitionData;
import io.impl.UserFreeInputs;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.system.Manager;
import servlets.util.Constants;
import servlets.util.ServletContextManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ContinuationUserInputsServlet", urlPatterns = "/collect/free/inputs/continuation")
public class ContinuationUserInputsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get uuid and flow to continue to

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        String flowName = req.getParameter(Constants.FLOW_NAME);
        String uuid = req.getParameter(Constants.UUID);
        uuid = uuid.substring(1, uuid.length() - 1);

        FlowExecution flowExecution = manager.getDataManager().returnExecutionByUUID(uuid);
        FlowDefinition flow = manager.getStepper().getAllFlows()
                .stream()
                .filter(flowDefinition -> flowDefinition.getName().equals(flowName))
                .findFirst().orElse(null);

        if(flow != null) {
            UserFreeInputs userFreeInputs = manager.getFlowUserInputsAfterApplyContinuation(flowExecution, flowExecution.getFlowFreeInputs(), flow);

            // Convert to dto
            List<IODefinitionDataDTO> userInputToInsert = new ArrayList<>();
            Map<String, String> fromInputToStep = new HashMap<>();
            Map<String, String > fromInputNameToData = new HashMap<>();

            for (IODefinitionData freeInput : userFreeInputs.getFreeInputs()) {
                if (!userFreeInputs.isInputHasInitializeValue(freeInput)) {
                    IODefinitionDataDTO ioDefinitionDataDTO = new IODefinitionDataDTO(freeInput);
                    userInputToInsert.add(ioDefinitionDataDTO);

                    fromInputToStep.put(freeInput.getName(), userFreeInputs.fromInputToStepName(freeInput));
                    if(userFreeInputs.getFromInputToObject().containsKey(freeInput)) {
                        // There is value inside
                        fromInputNameToData.put(freeInput.getName(), userFreeInputs.getFromInputToObject().get(freeInput).toString());
                    }
                }

            }

            UserFreeInputsDTO userFreeInputsDTO = new UserFreeInputsDTO(flowName, userInputToInsert, fromInputToStep, fromInputNameToData);
            Gson gson = new Gson();
            String jsonInputs = gson.toJson(userFreeInputsDTO);
            out.print(jsonInputs);
            resp.setStatus(HttpServletResponse.SC_OK);

        }
        else
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
}
