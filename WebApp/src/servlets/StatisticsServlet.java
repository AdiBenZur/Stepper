package servlets;

import com.google.gson.Gson;
import dto.statistics.StatisticsDTO;
import dto.statistics.StatisticsDetailsDTO;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.system.Manager;
import servlets.util.ServletContextManager;
import step.api.StepDefinition;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@WebServlet(name = "StatisticsServlet", urlPatterns = "/statistics")
public class StatisticsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Manager manager = ServletContextManager.getStepperManager(getServletContext());
        PrintWriter out = resp.getWriter();

        List<StatisticsDetailsDTO> flows = new ArrayList<>();
        List<StatisticsDetailsDTO> steps = new ArrayList<>();

        // Flows
        for(FlowDefinition flowDefinition : manager.getStepper().getAllFlows()) {
            String flowName = flowDefinition.getName();

            // Find how much time execute
            Integer executionsCounter = 0;
            for(FlowExecution flowExecution : manager.getDataManager().getExecutionData()) {
                if(flowExecution.getFlowDefinition().getName().equals(flowName))
                    executionsCounter ++;
            }

            if(executionsCounter != 0) {
                // Calculate average
                long avg;

                long sum = 0;
                Integer counter = 0;
                for(FlowExecution flowExecution : manager.getDataManager().getExecutionData()) {
                    if(flowExecution.getFlowDefinition().getName().equals(flowName)) {
                        counter ++;
                        sum += flowExecution.getTotalTime();
                    }
                }
                if(counter == 0)
                    avg = 0;
                else
                    avg = (sum / counter);

                // Add to list
                flows.add(new StatisticsDetailsDTO(flowName, counter, avg));
            }
        }

        // Steps
        Map<StepDefinition, List<Duration>> allStepsStatisticsMap = new HashMap<>();
        for(FlowExecution flowExecution : manager.getDataManager().getExecutionData()) {

            Map<StepDefinition, Map<StepUsageDeclaration, Duration>> stepsStatistics = flowExecution.getStatistics();
            for(StepDefinition stepDefinition : stepsStatistics.keySet()) {
                Map<StepUsageDeclaration, Duration> innerStepMap = stepsStatistics.get(stepDefinition);

                if(allStepsStatisticsMap.containsKey(stepDefinition)) {
                    List<Duration> durationList = allStepsStatisticsMap.get(stepDefinition);

                    for(Duration duration : innerStepMap.values()) {
                        durationList.add(duration);
                    }
                }
                else {
                    List<Duration> durationList = new ArrayList<>();
                    for(Duration duration : innerStepMap.values()) {
                        durationList.add(duration);
                    }
                    allStepsStatisticsMap.put(stepDefinition, durationList);
                }
            }
        }

        // Convert to dto
        for(StepDefinition stepDefinition : allStepsStatisticsMap.keySet()) {
            String name = stepDefinition.getName();
            List<Duration> stepDurationList = allStepsStatisticsMap.get(stepDefinition);
            Integer nofTimeExecute = stepDurationList.size();

            if(nofTimeExecute != 0) {
                // Calculate average
                long avg;

                long sum = 0;
                Integer counter = stepDurationList.size();
                for(Duration duration : stepDurationList) {
                    sum += duration.toMillis();
                }

                avg = (sum / counter);

                // Add to list
                steps.add(new StatisticsDetailsDTO(name, counter, avg));
            }
        }

        // Convert to dto
        StatisticsDTO statisticsDTO = new StatisticsDTO(flows, steps);
        Gson gson = new Gson();
        String statJson = gson.toJson(statisticsDTO);
        out.print(statJson);
        resp.setStatus(HttpServletResponse.SC_OK);
    }


}
