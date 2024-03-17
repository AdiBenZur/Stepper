package servlets;

import com.google.gson.Gson;
import dto.io.IODefinitionDataDTO;
import dto.io.IOValueDTO;
import dto.io.UserFreeInputsDTO;
import exception.data.TypeDontMatchException;
import flow.definition.api.FlowDefinition;
import io.api.IODefinitionData;
import io.impl.UserFreeInputs;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.system.Manager;
import role.Role;
import servlets.util.Constants;
import servlets.util.ServletContextManager;
import user.User;
import user.UsersManager;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@WebServlet(name = "FreeInputsServlet", urlPatterns = "/collect/free/inputs")
public class FreeInputsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Check the user authority and get the free inputs to collect

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        String flowName = req.getParameter(Constants.FLOW_NAME_TO_EXECUTE);

        FlowDefinition flow = manager.getStepper().getAllFlows()
                .stream()
                .filter(flowDefinition -> flowDefinition.getName().equals(flowName))
                .findFirst().orElse(null);

        if(flow != null) {

            // Check if the user can run this flow

            UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
            User user = usersManager.getUserMap().get(req.getSession().getId());

            boolean isAllowed = false;
            for(Role role : user.getUserRoles()) {
                if(role.getAllowedFlows().contains(flow.getName()))
                    isAllowed = true;
            }

            if(!isAllowed) {
                out.print("The user not allowed to run the flow anymore.");
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }

            UserFreeInputs userFreeInputs = manager.getFlowUserInputs(flow);

            // define which input is not init value
            List<IODefinitionDataDTO> userInputToInsert = new ArrayList<>();
            Map<String, String> fromInputToStep = new HashMap<>();

            for (IODefinitionData freeInput : userFreeInputs.getFreeInputs()) {
                if (!userFreeInputs.isInputHasInitializeValue(freeInput)) {
                    IODefinitionDataDTO ioDefinitionDataDTO = new IODefinitionDataDTO(freeInput);
                    userInputToInsert.add(ioDefinitionDataDTO);

                    fromInputToStep.put(freeInput.getName(), userFreeInputs.fromInputToStepName(freeInput));
                }
            }

            UserFreeInputsDTO userFreeInputsDTO = new UserFreeInputsDTO(userInputToInsert, fromInputToStep, flow.getName());
            Gson gson = new Gson();
            String userFreeInputString = gson.toJson(userFreeInputsDTO);
            out.print(userFreeInputString);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Check if the data inserted is the correct type

        resp.setContentType("text/plain;charset=UTF-8");
        InputStream requestBody = req.getInputStream();
        PrintWriter out = resp.getWriter();

        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        String bodyData = stringBuilder.toString();
        Gson gson = new Gson();
        IOValueDTO ioValueDTO = gson.fromJson(bodyData, IOValueDTO.class);

        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        FlowDefinition flow = manager.getStepper().getAllFlows()
                .stream()
                .filter(flowDefinition -> flowDefinition.getName().equals(ioValueDTO.getFlowName()))
                .findFirst().orElse(null);

        if(flow != null) {
            UserFreeInputs userFreeInputs = manager.getFlowUserInputs(flow);

            // Find the correlate input
            for(IODefinitionData freeInput : userFreeInputs.getFreeInputs()) {
                if(freeInput.getName().equals(ioValueDTO.getInput().getName())) {
                    try {
                        userFreeInputs.scanInput(freeInput, ioValueDTO.getValue());
                        resp.setStatus(HttpServletResponse.SC_OK);
                    } catch (TypeDontMatchException e) {
                        // The user insert data in a wrong type of the input
                        resp.setStatus(HttpServletResponse.SC_CONFLICT);
                        out.print(e.getMessage());
                    }
                    break;
                }
            }
        }
    }
}
