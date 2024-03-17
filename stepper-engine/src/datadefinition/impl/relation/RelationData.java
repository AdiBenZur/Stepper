package datadefinition.impl.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationData {

    private List<String> columns;
    private List<SingelRow> rows;

    public RelationData(List<String> columns) {
        this.columns = columns;
        rows = new ArrayList<>();
    }

    public List<String> getColumns() { return columns; }

    // Get Column by order.
    public List<String> getColumnByRowsOrder(String title){
        List<String> column = new ArrayList<>();
        for(SingelRow row : rows)
            column.add(row.getData(title));

        return column;
    }

    // Get row by order
    public List<String> getRowByColumnsOrder(Integer index) {
        // index = row number
        List<String> row = new ArrayList<>();
        for (String title : columns)
            row.add(rows.get(index).getData(title));

        return row;
    }

    public void addRow(List<String> row) {
        SingelRow singelRow = new SingelRow();
        for(int i = 0; i < columns.size(); i ++) {
            singelRow.addData(columns.get(i), row.get(i));
        }
        rows.add(singelRow);
    }

    public Integer getNumberOfRows() {
        return rows.size();
    }

    @Override
    public String toString() {

        if(columns.size() == 0)
            return "There is no data in the table";

        String printStr = "Columns: ";
        for(String str : columns)
            printStr += str + ", ";

        printStr = printStr.substring(0, printStr.length() - 2);
        printStr += "\n\t       Number of rows: " + rows.size();

        return printStr;
    }

    // Inner class
    private class SingelRow {
        private Map<String, String> columnTitleToCellValue; // from column title to cell value

        public SingelRow() {
            columnTitleToCellValue = new HashMap<>();
        }

        public void addData(String title, String value) {
            columnTitleToCellValue.put(title, value);
        }

        public String getData(String title) {
            return columnTitleToCellValue.get(title);
        }
    }
}
