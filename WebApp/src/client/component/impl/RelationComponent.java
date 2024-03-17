package client.component.impl;

import client.component.api.AbstractComponent;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import datadefinition.impl.relation.RelationData;
import dto.io.IODataValueDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class RelationComponent extends AbstractComponent {

    public RelationComponent(IODataValueDTO data) {
        super(data);
    }

    @Override
    public Parent showComponent() {
        LinkedTreeMap value = (LinkedTreeMap) data.getValue();

        Gson gson = new Gson();
        RelationData relationData = gson.fromJson(gson.toJsonTree(value), RelationData.class);


        TableView<ObservableList<String>> tableView = new TableView<>();
        List<String> columns = relationData.getColumns();

        // Add columns
        for(int i = 0; i < columns.size(); i ++) {
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columns.get(i));
            final int columnIndex = i;

            // Set cell value factory
            column.setCellValueFactory(cellData -> {
                ObservableList<String> rowData = cellData.getValue();
                if (rowData != null && columnIndex < rowData.size()) {
                    return new SimpleStringProperty(rowData.get(columnIndex));
                } else {
                    return null;
                }
            });

            tableView.getColumns().add(column);
        }

        // Convert column data to row data
        List<ObservableList<String>> rowData = new ArrayList<>();
        int numColumns = relationData.getColumns().size();
        int numRows = relationData.getNumberOfRows();

        for (int row = 0; row < numRows; row ++) {
            ObservableList<String> rowItems = FXCollections.observableArrayList();
            for (int col = 0; col < numColumns; col++) {
                rowItems.add(relationData.getRowByColumnsOrder(row).get(col));
            }
            rowData.add(rowItems);
        }

        // Set the data items for the TableView
        tableView.getItems().addAll(rowData);
        tableView.setPrefHeight(300);
        tableView.setPrefWidth(300);

        return tableView;
    }
}
