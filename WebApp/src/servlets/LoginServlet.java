package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import user.User;
import user.UsersManager;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import servlets.util.Constants;
import servlets.util.ServletContextManager;
import servlets.util.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "LoginServlet", urlPatterns = "/users/login")
public class LoginServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");


        String userNameFromSession = SessionUtils.getUserName(request);
        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
        PrintWriter out = response.getWriter();


        // Check if the user exist
        if(userNameFromSession == null) {
            // The user not logged in

            String userNameFromParameter = request.getParameter(Constants.USERNAME);

            if (userNameFromParameter == null || userNameFromParameter.isEmpty()) {

                // The username is empty or null. Return response of an error (client's fault)
                response.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            else {
                userNameFromParameter = userNameFromParameter.trim(); // Remove spaces from the beginning and end.

                synchronized (this) {
                    // Check if the username exist
                    if (usersManager.isUserExistsByName(userNameFromParameter)) {

                        // Check if the user is offline
                        if(!usersManager.isUserExists(userNameFromParameter)) {
                            // The user is offline
                            request.getSession(true).setAttribute(Constants.USERNAME, userNameFromParameter);
                            usersManager.loginEnteredToSystemAgain(request.getSession().getId(), userNameFromParameter);
                            String errorMsg = "Welcome back!";
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.print(errorMsg);
                            out.flush();
                        }
                        else {
                            String errorMsg = "Username '" + userNameFromParameter + "' already exist. Cannot login.";
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            out.print(errorMsg);
                            out.flush();
                        }
                    }
                    else {
                        // The username is new
                        // Add the user to map
                        User newUser = new User(userNameFromParameter);

                        // Create session
                        request.getSession(true).setAttribute(Constants.USERNAME, userNameFromParameter);
                        usersManager.addUser(request.getSession().getId(), newUser);
                        out.print("login successfully!");
                        out.flush();
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                }
            }
        }
        else {
            // The user already logged in
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("The user already checked in.");
        }
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // User logout
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
        String loginParam = req.getParameter("login");
        if (loginParam == null || loginParam.isEmpty()) {
            resp.setStatus(510);
            out.print("Invalid request");
        }

        // Find user by session
        synchronized (this) {
            User user = usersManager.getUserMap().get(req.getSession().getId());
            if (loginParam.equals("logout")) {
                user.setOnline(false);
                //usersManager.updateUserInMap(user);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(510);
                out.print("Invalid request");
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Admin login/ logout

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        UsersManager usersManager = ServletContextManager.getUserManager(getServletContext());
        String loginParam = req.getParameter("login");
        if (loginParam == null || loginParam.isEmpty()) {
            resp.setStatus(510);
            out.print("Invalid request");
        }
        else {
            if(loginParam.equals("login")) {
                if (usersManager.isAdminLogin()) {
                    out.print("Admin already login!");
                    resp.setStatus(510);
                } else {
                    usersManager.setAdminLogin(true);
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            }
            else {
                if(loginParam.equals("logout")) {
                    usersManager.setAdminLogin(false);
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                else {
                    resp.setStatus(510);
                    out.print("Invalid request");
                }
            }
        }
    }
}
