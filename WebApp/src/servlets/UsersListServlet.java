package servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlets.util.ServletContextManager;
import user.User;
import user.UsersManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "UsersListServlet", urlPatterns = "/users/list")
public class UsersListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
            List<User> usersList = new ArrayList<>();
            usersList.addAll(usersManager.getUserMap().values());

            String json = gson.toJson(usersList);
            out.println(json);
            out.flush();
        }
    }


}
