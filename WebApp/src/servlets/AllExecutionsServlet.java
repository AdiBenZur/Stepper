package servlets;

import com.google.gson.Gson;
import dto.flow.ExecutionDetailsHeadlines;
import flow.execution.FlowExecution;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.system.Manager;
import servlets.util.ServletContextManager;
import user.User;
import user.UsersManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


@WebServlet(name = "AllExecutionsServlet", urlPatterns = "/all/executions")
public class AllExecutionsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get all executions

        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
        PrintWriter out = resp.getWriter();

        List<ExecutionDetailsHeadlines> executionList = new ArrayList<>();

        String name;
        for(FlowExecution flowExecution : manager.getDataManager().getExecutionData()) {
            name = null;
            // Find who run this flow
            for(User user : usersManager.getUserMap().values()) {
                if(user.getUserExecutions().contains(flowExecution.getUniqueId())) {
                    name = user.getUserName();
                    break;
                }
            }
            executionList.add(new ExecutionDetailsHeadlines(flowExecution.getFlowName(), flowExecution.getStartTimeOfExecution(), flowExecution.getFlowExecutionStatus(), flowExecution.getUniqueId(), name));
        }

        Gson gson = new Gson();
        String json = gson.toJson(executionList);
        out.println(json);
        resp.setStatus(HttpServletResponse.SC_OK);
        out.flush();
    }
}
