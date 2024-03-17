package servlets;

import com.google.gson.Gson;
import dto.flow.FlowExecutionDTO;
import dto.io.IODataValueDTO;
import dto.step.StepExecutionDTO;
import dto.step.StepLogDTO;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import flow.execution.log.StepLog;
import io.api.IODefinitionData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.system.Manager;
import servlets.util.Constants;
import servlets.util.ServletContextManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "FlowExecutionServlet", urlPatterns = "/flow/execution")
public class FlowExecutionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // Get flow execution dto from uuid

        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        String uuid = req.getParameter(Constants.UUID);
        uuid = uuid.substring(1, uuid.length() - 1);


        FlowExecution flowExecution = manager.getDataManager().returnExecutionByUUID(uuid);

        Gson gson = new Gson();

        // Build flow execution dto
        String flowName = null;
        String uniqueId = null;
        String startTimeOfExecution = null;
        long duration = -1;
        String executionResult = null;
        String summaryLine = null;
        List<String> logs = new ArrayList<>();
        List<StepExecutionDTO> stepExecutionDTOList = new ArrayList<>();
        List<IODataValueDTO> freeInputsValues = new ArrayList<>();
        List<IODataValueDTO> allOutputsValues = new ArrayList<>();
        List<StepLogDTO> stepLogs = new ArrayList<>();
        List<String> stepNames = flowExecution.getStepsInFlow().stream().map(StepUsageDeclaration::getStepName).collect(Collectors.toList());

        try {

            // Steps
            for (StepUsageDeclaration step : flowExecution.getStepsInFlow()) {
                // Names
                String name = step.getStepName();
                String originalName = step.getStepDefinition().getName();

                // Result
                String result = step.getStepDefinition().getStepResult();

                // Summary line
                String summaryLineStep = null;
                for (int i = flowExecution.getContext().getStepsSummaryLine().size() - 1; i >= 0; i--) {
                    if (flowExecution.getContext().getStepsSummaryLine().get(i).getStepName().equals(step.getStepName())) {
                        summaryLineStep = flowExecution.getContext().getStepsSummaryLine().get(i).getSummaryLine();
                    }
                }

                // Time
                String startTime = flowExecution.getFromStepToStart(step);
                String endTime = flowExecution.getFromStepToEnd(step);
                long stepDuration = manager.getStepDurationFromStepStatisticsManager(flowExecution, step).toMillis();

                // Data values
                List<IODataValueDTO> stepValues = new ArrayList<>();
                Map<StepUsageDeclaration, Map<IODefinitionData, Object>> allInputsAndOutputs = flowExecution.getFromStepToDataMap();

                Map<IODefinitionData, Object> stepMap = allInputsAndOutputs.get(step);
                for (IODefinitionData data : stepMap.keySet()) {
                    stepValues.add(new IODataValueDTO(data, stepMap.get(data)) );
                }


                stepExecutionDTOList.add(new StepExecutionDTO(name, originalName, result, startTime, endTime, stepDuration, stepValues, summaryLineStep));
            }

            // Free inputs
            for (IODefinitionData input : flowExecution.getFlowFreeInputs().getFromInputToObject().keySet()) {
                freeInputsValues.add(new IODataValueDTO(input, flowExecution.getFlowFreeInputs().getFromInputToObject().get(input)));
            }

            // All outputs
            for (IODefinitionData output : flowExecution.getAllOutputProduceDuringFlow().keySet()) {
                allOutputsValues.add(new IODataValueDTO(output, flowExecution.getAllOutputProduceDuringFlow().get(output)));
            }

            // logs
            for(StepLog stepLog : flowExecution.getContext().getFlowLogs()) {
                stepLogs.add(new StepLogDTO(stepLog.getStepName(), stepLog.getStepLog()));
            }

            flowName = flowExecution.getFlowName();
            uniqueId = flowExecution.getUniqueId();
            startTimeOfExecution = flowExecution.getStartTimeOfExecution();
            duration = flowExecution.getTotalTime();
            executionResult = flowExecution.getFlowExecutionStatus();
            summaryLine = flowExecution.getFlowSummaryLine();


            FlowExecutionDTO flowExecutionDTO = new FlowExecutionDTO(flowName, uniqueId, startTimeOfExecution, duration, executionResult, summaryLine, stepExecutionDTOList, freeInputsValues, allOutputsValues, flowExecution.isProcessing(), stepLogs, stepNames);

            String flowExecutionString = gson.toJson(flowExecutionDTO);
            out.print(flowExecutionString);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        catch (Exception e) {
            FlowExecutionDTO flowExecutionDTO = new FlowExecutionDTO(flowName, uniqueId, startTimeOfExecution, duration, executionResult, summaryLine, stepExecutionDTOList, freeInputsValues, allOutputsValues, flowExecution.isProcessing(), stepLogs, stepNames);

            String flowExecutionString = gson.toJson(flowExecutionDTO);
            out.print(flowExecutionString);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
