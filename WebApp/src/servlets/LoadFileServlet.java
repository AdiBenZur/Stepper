package servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import flow.definition.api.FlowDefinition;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import manager.system.Manager;
import role.RolesManager;
import servlets.util.ServletContextManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

@WebServlet(name = "LoadFileServlet", urlPatterns = "/admin/upload/file")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 50, maxRequestSize = 1024 * 1024 * 100)
public class LoadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();

        Collection<Part> parts = req.getParts();
        if (parts == null || parts.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        StringBuilder fileContent = new StringBuilder();

        for (Part part : parts) {
            //to write the content of the file to a string
            fileContent.append(readFromInputStream(part.getInputStream()));
        }

        Manager manager = (Manager) ServletContextManager.getStepperManager(getServletContext());

        int nofFlowsSoFar = manager.getStepper().getAllFlows().size();

        List<String> errors = manager.loadDataFromXmlWhileSavingPreviousSystemFlows(fileContent.toString());

        if(errors.isEmpty()) {

            int nofFlowsAfterLoading = manager.getStepper().getAllFlows().size();

            if (nofFlowsAfterLoading != nofFlowsSoFar) {
                List<FlowDefinition> newFlowsAdded = new ArrayList<>();
                for (int i = nofFlowsSoFar; i < manager.getStepper().getAllFlows().size(); i++)
                    newFlowsAdded.add(manager.getStepper().getAllFlows().get(i));

                RolesManager rolesManager = (RolesManager) ServletContextManager.getRolesManager(getServletContext());

                for (FlowDefinition flow : newFlowsAdded) {
                    rolesManager.addFlowNameToAllFlowsRole(flow.getName());
                    rolesManager.addFlowNameToFictiveManagerRole(flow.getName());
                    if (flow.getReadOnly())
                        rolesManager.addFlowNameToReadOnlyRole(flow.getName());
                }

                out.print("New flows added to the system successfully!");
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                if(nofFlowsSoFar == 0) {
                    out.print("New flows added to the system successfully!");
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
                else {
                    out.print("All flow exist in the system already!");
                    resp.setStatus(HttpServletResponse.SC_OK);
                }
            }
        }
        else {
            // There are errors
            Gson gson = new Gson();
            String errorListJson = gson.toJson(errors);
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.println(errorListJson);
            out.flush();
        }
    }


    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
