package servlets;

import com.google.gson.Gson;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@WebServlet(name = "RolesServlet", urlPatterns = "/roles")
public class RolesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get all roles except the manager fictive role

        resp.setContentType("text/plain;charset=UTF-8");
        RolesManager rolesManager = ServletContextManager.getRolesManager(getServletContext());
        PrintWriter out = resp.getWriter();

        List<Role> allRoles = rolesManager.getDefinedRoles()
                .stream()
                .filter(role -> !role.getName().equals(Constants.MANAGER_FICTIVE_ROLE_NAME))
                .collect(Collectors.toList());

        Gson gson = new Gson();
        String jsonRoles = gson.toJson(allRoles);
        out.print(jsonRoles);
        resp.setStatus(HttpServletResponse.SC_OK);
        out.flush();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Add new role to manager

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
        Role newRoleToAdd = gson.fromJson(bodyData, Role.class);

        PrintWriter out = resp.getWriter();

        RolesManager rolesManager = ServletContextManager.getRolesManager(getServletContext());
        if(!rolesManager.isRoleExist(newRoleToAdd.getName())) {
            synchronized (rolesManager) {
                rolesManager.addRole(newRoleToAdd);
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("Role added successfully.");
        }
        else {
            // Role exists
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.print(" Error: role name '" + newRoleToAdd.getName() + "' already exist in the system. ");
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Update role

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
        Role updatedRole = gson.fromJson(bodyData, Role.class);
        RolesManager rolesManager = ServletContextManager.getRolesManager(getServletContext());

        Optional<Role> roleToChange = rolesManager.getDefinedRoles()
                .stream()
                .filter(role -> role.getName().equals(updatedRole.getName()))
                .findFirst();

        if(roleToChange.isPresent()) {
            Role role = roleToChange.get();

            // Change allowed flows
            role.setAllowedFlows(updatedRole.getAllowedFlows());

            String updatedRoleString = gson.toJson(role);
            out.print(updatedRoleString);
            resp.setStatus(HttpServletResponse.SC_OK);

            // Update role in every user that use the role
            UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
            synchronized (usersManager.getUserMap().values()) {
                usersManager.getUserMap().values().forEach(user -> user.replaceRole(role));
            }
        }
        else
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}
