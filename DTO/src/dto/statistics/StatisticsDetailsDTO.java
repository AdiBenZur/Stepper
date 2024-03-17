package dto.statistics;

public class StatisticsDetailsDTO {
    private String name;
    private Integer nofTimeExecuted;
    private long avg;

    public StatisticsDetailsDTO(String name, Integer nofTimeExecuted, long avg) {
        this.name = name;
        this.nofTimeExecuted = nofTimeExecuted;
        this.avg = avg;
    }

    public String getName() {
        return name;
    }

    public Integer getNofTimeExecuted() {
        return nofTimeExecuted;
    }

    public long getAvg() {
        return avg;
    }
}
