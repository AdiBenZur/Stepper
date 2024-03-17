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

@WebServlet(name = "UserExecutionsServlets", urlPatterns = "/user/executions")
public class UserExecutionsServlets extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get user by session and return lise of executions headlines

        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        User user = usersManager.getUserMap().get(req.getSession().getId());
        PrintWriter out = resp.getWriter();

        if(user != null) {
            List<ExecutionDetailsHeadlines> list = new ArrayList<>();
            for(String uuid : user.getUserExecutions()) {

                // Find the correlate flow execution
                FlowExecution flowExecution = manager.getDataManager().returnExecutionByUUID(uuid);
                list.add(new ExecutionDetailsHeadlines(flowExecution.getFlowName(), flowExecution.getStartTimeOfExecution(), flowExecution.getFlowExecutionStatus(), uuid, null));
            }

            Gson gson = new Gson();
            String jsonList = gson.toJson(list);
            out.print(jsonList);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

        }
    }
}
