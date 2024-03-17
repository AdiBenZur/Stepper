package client.admin.GUI.body.statistic;

public class StatisticsRowData {
    private final String name;
    private final String nofTimeExecute;
    private final String average;

    public StatisticsRowData(String name, String nofTimeExecute, String average) {
        this.name = name;
        this.nofTimeExecute = nofTimeExecute;
        this.average = average;
    }

    public String getName() {
        return name;
    }

    public String getNofTimeExecute() {
        return nofTimeExecute;
    }

    public String getAverage() {
        return average;
    }
}
