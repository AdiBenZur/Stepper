package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import role.Role;
import servlets.util.Constants;
import servlets.util.ServletContextManager;
import user.User;
import user.UsersManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "UsersAllowedFlowsServlet", urlPatterns = "/users/allowed/flows")
public class UsersAllowedFlowsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // get user by session and return its list of allowed flows.

        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
        User user = usersManager.getUserMap().get(req.getSession().getId());
        PrintWriter out = resp.getWriter();

        synchronized (this) {
            if (user != null) {
                List<String> allowedFlows = user.getUserRoles()
                        .stream()
                        .flatMap(role -> role.getAllowedFlows().stream())
                        .distinct()
                        .collect(Collectors.toList());

                Gson gson = new Gson();
                String allowedFlowsString = gson.toJson(allowedFlows);
                out.print(allowedFlowsString);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                // User doesnt have session
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
