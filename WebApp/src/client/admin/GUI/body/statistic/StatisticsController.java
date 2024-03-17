package client.admin.GUI.body.statistic;

import client.admin.GUI.body.AdminBodyController;
import dto.statistics.StatisticsDTO;
import dto.statistics.StatisticsDetailsDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StatisticsController {

    public static final int REFRESH_RATE = 1500;
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
    private AdminBodyController bodyController;
    private Timer timer;
    private TimerTask refresher;


    public void setBodyController(AdminBodyController bodyController) {
        this.bodyController = bodyController;
    }

    @FXML
    public void initialize() {
        flowNameColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("name"));
        flowNumberOfExecutionsColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("nofTimeExecute"));
        flowAverageExecutionTimesColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("average"));

        stepNameColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("name"));
        stepNumberOfExecutionsColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("nofTimeExecute"));
        stepAverageExecutionTimesColumn.setCellValueFactory(new PropertyValueFactory<StatisticsRowData, String>("average"));
    }

    public void startScene() {
        startRefresher();
    }

    private void updateUsersList(StatisticsDTO statisticsDTO) {
        // Update tables
        Platform.runLater(() -> {
            fillTables(statisticsDTO);
        });
    }

    public void startRefresher() {
        refresher = new StatisticsRefresher(
                this::updateUsersList);
        timer = new Timer();
        timer.schedule(refresher, 0, REFRESH_RATE);
    }

    public void fillTables(StatisticsDTO statisticsDTO) {
        populateFlowsTableView(statisticsDTO.getFlows());
        populateStepsTableView(statisticsDTO.getSteps());
    }

    public void populateFlowsTableView(List<StatisticsDetailsDTO> flows) {
        ObservableList<StatisticsRowData> data = FXCollections.observableArrayList();

        for(StatisticsDetailsDTO statistic : flows) {
            String avgStr = String.valueOf(statistic.getAvg()) + " ms";

            StatisticsRowData statisticsRowData = new StatisticsRowData(statistic.getName(), statistic.getNofTimeExecuted().toString(), avgStr);
            data.add(statisticsRowData);
        }
        flowsTableView.setItems(data);
    }


    public void populateStepsTableView(List<StatisticsDetailsDTO> steps) {
        ObservableList<StatisticsRowData> dataStep = FXCollections.observableArrayList();

        for(StatisticsDetailsDTO statistic : steps) {
            String avgStr = String.valueOf(statistic.getAvg()) + " ms";

            StatisticsRowData statisticsRowData = new StatisticsRowData(statistic.getName(), statistic.getNofTimeExecuted().toString(), avgStr);
            dataStep.add(statisticsRowData);
        }
        stepsTableView.setItems(dataStep);
    }

}
