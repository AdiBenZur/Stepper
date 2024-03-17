package servlets;

import com.google.gson.Gson;
import dto.user.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import role.Role;
import role.RolesManager;
import servlets.util.Constants;
import servlets.util.ServletContextManager;
import user.User;
import user.UsersManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = "/users")
public class UsersServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get user by session
        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
        PrintWriter out = resp.getWriter();

        synchronized (this) {
            User user = usersManager.getUserMap().get(req.getSession().getId());

            if (user != null) {
                Gson gson = new Gson();
                String jasonUser = gson.toJson(user);

                out.print(jasonUser);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                // User doesnt have session
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Update user data and return the updated user

        resp.setContentType("text/plain;charset=UTF-8");
        InputStream requestBody = req.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        String bodyData = stringBuilder.toString();
        Gson gson = new Gson();
        UserDTO userDTO = gson.fromJson(bodyData, UserDTO.class);

        PrintWriter out = resp.getWriter();

        RolesManager rolesManager = ServletContextManager.getRolesManager(getServletContext());
        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());


        // Convert the user dto to user
        User user = new User(userDTO.getUsername());
        user.setOnline(userDTO.isOnline());

        // Assign manager
        user.setManager(userDTO.isManager());

        // Assign user roles
        List<Role> userRoles = new ArrayList<>();

        // Add role manager to user is manager
        if(user.isManager())
            userRoles.add(rolesManager.findRoleByName(Constants.MANAGER_FICTIVE_ROLE_NAME));

        // Assign other new roles
        for(String roleName : userDTO.getRoles()) {
            Role role = rolesManager.findRoleByName(roleName);
            if(role != null)
                userRoles.add(role);
            else {
                // Cannot find role in system
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("Role did not find in the system");
                return;
            }
        }

        user.setRoleList(userRoles);

        if(usersManager.updateUserInMap(user)) {
            String updatedUserString = gson.toJson(user);
            out.print(updatedUserString);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            // User doesn't found in the system
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.print("Cannot find user in the system! Apparently the user logged out at the moment.");
        }
    }


}
