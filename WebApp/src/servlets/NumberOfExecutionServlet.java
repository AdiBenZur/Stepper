package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.system.Manager;
import servlets.util.Constants;
import servlets.util.ServletContextManager;
import user.User;
import user.UsersManager;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "NumberOfExecutionServlet", urlPatterns = "/get/number/of/executions")
public class NumberOfExecutionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Return number of executions by username

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
        String userName = req.getParameter(Constants.USERNAME);

        // Find user in system
        User correlateUser = null;
        for(User user : usersManager.getUserMap().values()) {
            if(user.getUserName().equals(userName)) {
                correlateUser = user;
            }
        }

        if(correlateUser != null) {
            Integer nofExecutions = correlateUser.getUserExecutions().size();
            Gson gson = new Gson();
            String jsonString = gson.toJson(nofExecutions);
            out.print(jsonString);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
