package servlets;

import com.google.gson.Gson;
import flow.definition.api.FlowDefinition;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.system.Manager;
import servlets.util.ServletContextManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "FlowsServlet", urlPatterns = "/flows")
public class FlowsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get all flows

        resp.setContentType("text/plain;charset=UTF-8");
        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        PrintWriter out = resp.getWriter();

        List<String> flows = manager.getStepper().getAllFlows().stream().map(FlowDefinition::getName).collect(Collectors.toList());

        Gson gson = new Gson();
        String jsonRoles = gson.toJson(flows);
        out.print(jsonRoles);
        out.flush();
    }
}
