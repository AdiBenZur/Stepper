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

@WebServlet(name = "GetUpdatedUserServlet", urlPatterns = "/get/user/last/update")
public class GetUpdatedUserServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        String username = req.getParameter(Constants.USERNAME);
        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());

        User userToSend = null;
        for(User user : usersManager.getUserMap().values()) {
            if(user.getUserName().equals(username)) {
                userToSend = user;
            }
        }

        if(userToSend != null) {
            Gson gson = new Gson();
            String userString = gson.toJson(userToSend);
            out.print(userString);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("User dont found in system.");
        }
    }
}
