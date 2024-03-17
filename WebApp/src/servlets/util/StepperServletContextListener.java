package servlets.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import role.Role;
import role.RolesManager;

import java.util.ArrayList;

@WebListener
public class StepperServletContextListener implements ServletContextListener {

    private static final String ROLES_MANAGER_ATTRIBUTE_NAME = "rolesManager";


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // Called when the ServletContext is initialized
        ServletContext servletContext = servletContextEvent.getServletContext();
        servletContext.setAttribute(ROLES_MANAGER_ATTRIBUTE_NAME, new RolesManager());

        // initialize roles All Flows and Read Only Flows
        RolesManager rolesManager = ServletContextManager.getRolesManager(servletContext);
        rolesManager.addRole(new Role("Read Only Flows", "This role contains all the read only flows.", new ArrayList<>())); // Place 0
        rolesManager.addRole(new Role("All Flows", "This role contains all the flows exists in the system.", new ArrayList<>())); // Place 1
        rolesManager.addRole(new Role("Manager fictive role", "Fictive role", new ArrayList<>())); // Place 2
    }
}
