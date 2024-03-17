package desktop.body.impl.scene4;

import desktop.application.EngineController;
import desktop.body.BodyController;
import desktop.body.api.SceneBody;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.FlowExecution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import step.api.StepDefinition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsController implements SceneBody {

    @FXML
    private TableView<StatisticsRowData> flowsTableView;
    @FXML
    private TableColumn<StatisticsRowData, String> flowNameColumn;
    @FXML
    private TableColumn<StatisticsRowData, String> flowNumberOfExecutionsColumn;
    @FXML
    private TableColumn<StatisticsRowData, String> flowAverageExecutionTimesColumn;
    @FXML
    private TableView<StatisticsRowData> stepsTableView;
    @FXML
    private TableColumn<StatisticsRowData, String> stepNameColumn;
    @FXML
    private TableColumn<StatisticsRowData, String> stepNumberOfExecutionsColumn;
    @FXML
    private TableColumn<StatisticsRowData, String> stepAverageExecutionTimesColumn;
    private BodyController bodyController;


    @FXML
    public void initialize() {
        flowNameColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("name"));
        flowNumberOfExecutionsColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("nofTimeExecute"));
        flowAverageExecutionTimesColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("average"));

        stepNameColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("name"));
        stepNumberOfExecutionsColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("nofTimeExecute"));
        stepAverageExecutionTimesColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("average"));
    }

    public void setBodyController(BodyController bodyController) {
        this.bodyController = bodyController;
    }

    @Override
    public void fillScene() {

        populateFlowsTableView();
        populateStepsTableView();
    }

    public void populateFlowsTableView() {
        ObservableList<StatisticsRowData> data = FXCollections.observableArrayList();

        for(FlowDefinition flowDefinition : EngineController.getInstance().getStepper().getAllFlows()) {
            String flowName = flowDefinition.getName();
            Integer nofTimeExecute = getNofExecute(flowName);

            // Check if current flow has run
            if(nofTimeExecute != 0) {
                long avg = getAverageFlow(flowName);
                String avgStr = String.valueOf(avg) + " ms";
                StatisticsRowData statisticsRowData = new StatisticsRowData(flowName, nofTimeExecute.toString(), avgStr);
                data.add(statisticsRowData);
            }
        }

        flowsTableView.setItems(data);
    }

    public void populateStepsTableView() {
        ObservableList<StatisticsRowData> dataStep = FXCollections.observableArrayList();
        List<FlowExecution> executionsSoFar = EngineController.getInstance().getDataManager().getExecutionData();

        Map<StepDefinition, List<Duration>> statisticsMap = new HashMap<>();

        for(FlowExecution flowExecution : executionsSoFar) {
            addStepsStatistics(flowExecution, statisticsMap);
        }


        // Run over statistics manager key
        for (StepDefinition stepDefinition : statisticsMap.keySet()) {
            List<Duration> stepDurationList = statisticsMap.get(stepDefinition);
            Integer nofTimeExecute = stepDurationList.size();

            if (nofTimeExecute != 0) {
                long stepAvg = getAverageStep(stepDurationList);
                String avgStr = String.valueOf(stepAvg) + " ms";
                StatisticsRowData statisticsRowData = new StatisticsRowData(stepDefinition.getName(), nofTimeExecute.toString(), avgStr);
                dataStep.add(statisticsRowData);
            }
        }
        stepsTableView.setItems(dataStep);
    }


    public long getAverageFlow(String flowName) {
        long sum = 0;
        Integer counter = 0;

        for(FlowExecution flowExecution : EngineController.getInstance().getDataManager().getExecutionData()) {
            if(flowExecution.getFlowDefinition().getName().equals(flowName)) {
                counter ++;
                sum += flowExecution.getTotalTime();
            }
        }
        if(counter == 0)
            return 0;
        else
            return (sum / counter);
    }

    public Integer getNofExecute(String flowName) {
        Integer counter = 0;

        for(FlowExecution flowExecution : EngineController.getInstance().getDataManager().getExecutionData()) {
            if(flowExecution.getFlowDefinition().getName().equals(flowName))
                counter ++;
        }
        return counter;
    }


    public long getAverageStep(List<Duration> durationList) {
        long sum  = 0;
        Integer counter = durationList.size();

        for(Duration duration : durationList) {
            sum += duration.toMillis();
        }
        return (sum / counter);
    }


    public void addStepsStatistics(FlowExecution flowExecution, Map<StepDefinition, List<Duration>> statisticsMap) {
        Map<StepDefinition, Map<StepUsageDeclaration, Duration>> stepsStatistics = flowExecution.getStatistics();
        for(StepDefinition stepDefinition : stepsStatistics.keySet()) {
            Map<StepUsageDeclaration, Duration> innerStepMap = stepsStatistics.get(stepDefinition);

            if(statisticsMap.containsKey(stepDefinition)) {
                List<Duration> durationList = statisticsMap.get(stepDefinition);

                for(Duration duration : innerStepMap.values()) {
                    durationList.add(duration);
                }
            }
            else {
                List<Duration> durationList = new ArrayList<>();
                for(Duration duration : innerStepMap.values()) {
                    durationList.add(duration);
                }
                statisticsMap.put(stepDefinition, durationList);
            }
        }
    }
}


