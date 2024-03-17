package servlets.util;


import jakarta.servlet.ServletContext;
import manager.system.Manager;
import manager.system.ManagerImpl;
import role.RolesManager;
import user.UsersManager;


public class ServletContextManager {
    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String STEPPER_MANAGER_ATTRIBUTE_NAME = "stepperManager";
    private static final String ROLES_MANAGER_ATTRIBUTE_NAME = "rolesManager";

    private static final Object userManagerLock = new Object();
    private static final Object stepperManagerLock = new Object();


    public static UsersManager getUserManager(ServletContext servletContext) {
        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                // add the user manager to servlet context, happen once (user manager is singleton).
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UsersManager());
            }
        }
        return (UsersManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }


    public static Manager getStepperManager(ServletContext servletContext) {
        synchronized (stepperManagerLock) {
            if (servletContext.getAttribute(STEPPER_MANAGER_ATTRIBUTE_NAME) == null) {
                // add the stepper manager to servlet context, happen once (stepper manager is singleton).
                servletContext.setAttribute(STEPPER_MANAGER_ATTRIBUTE_NAME, new ManagerImpl());
            }
        }
        return (Manager) servletContext.getAttribute(STEPPER_MANAGER_ATTRIBUTE_NAME);
    }


    public static RolesManager getRolesManager(ServletContext servletContext) {
        // Insert roles manager to context in the servlet context listener
        return (RolesManager) servletContext.getAttribute(ROLES_MANAGER_ATTRIBUTE_NAME);
    }



}
