package servlets;

import com.google.gson.Gson;
import dto.io.UserFreeInputsDTO;
import exception.data.TypeDontMatchException;
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
import servlets.util.ServletContextManager;
import user.User;
import user.UsersManager;

import java.io.*;

@WebServlet(name = "RunFlowServlet", urlPatterns = "/run/flow")
public class RunFlowServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Run a flow and return uuid

        resp.setContentType("text/plain;charset=UTF-8");
        InputStream requestBody = req.getInputStream();
        PrintWriter out = resp.getWriter();
        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
        User user = usersManager.getUserMap().get(req.getSession().getId());

        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        String bodyData = stringBuilder.toString();
        Gson gson = new Gson();
        UserFreeInputsDTO userFreeInputsDTO = gson.fromJson(bodyData, UserFreeInputsDTO.class);

        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        FlowDefinition flow = manager.getStepper().getAllFlows()
                .stream()
                .filter(flowDefinition -> flowDefinition.getName().equals(userFreeInputsDTO.getFlowName()))
                .findFirst().orElse(null);

        if(flow != null) {
            UserFreeInputs userFreeInputs = manager.getFlowUserInputs(flow);

            // Insert data values to the object
            try {
                userFreeInputs.insertInitValues();
                for(String inputName : userFreeInputsDTO.getFromInputToDataInserted().keySet()) {
                    for(IODefinitionData input : userFreeInputs.getFreeInputs()) {
                        if(input.getName().equals(inputName)) {
                            userFreeInputs.scanInput(input, userFreeInputsDTO.getFromInputToDataInserted().get(inputName));
                            break;
                        }
                    }
                }

                // Run the flow
                FlowExecution flowExecution = manager.flowExecution(flow, userFreeInputs);

                // Add to execution manager
                manager.addNewDataToDataManager(flowExecution);

                // Get the user and add his execute
                usersManager.addUserExecutions(user.getUserName(), flowExecution.getUniqueId());
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(flowExecution.getUniqueId());
            }
            catch (TypeDontMatchException e) {
                out.print("error store data.");
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        }
    }
}
