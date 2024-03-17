package servlets;

import com.google.gson.Gson;
import flow.definition.api.FlowDefinition;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.system.Manager;
import servlets.util.Constants;
import servlets.util.ServletContextManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ContinuationServlet", urlPatterns = "/flow/continuations")
public class ContinuationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get flow name and return the name of continuations

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        String name = req.getParameter(Constants.FLOW_NAME);

        // Find the flow
        FlowDefinition flow = manager.getStepper().getAllFlows()
                .stream()
                .filter(flowDefinition -> flowDefinition.getName().equals(name))
                .findFirst().orElse(null);

        if(flow != null) {
            List<String> continuations = new ArrayList<>();
            for(FlowDefinition continuation : flow.getFlowContinuationCustomMapping().keySet()) {
                continuations.add(continuation.getName());
            }

            Gson gson = new Gson();
            String jsonList = gson.toJson(continuations);
            out.print(jsonList);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
