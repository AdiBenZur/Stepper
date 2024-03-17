package client.component.impl;

import client.component.api.AbstractComponent;
import com.google.gson.internal.LinkedTreeMap;
import dto.io.IODataValueDTO;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class FileListComponent extends AbstractComponent {

    public FileListComponent(IODataValueDTO data) {
        super(data);
    }

    @Override
    public Parent showComponent() {
        ArrayList value = (ArrayList)data.getValue();
        VBox listValue = new VBox();
        listValue.setSpacing(5);

        if(value.size() == 0)
            listValue.getChildren().add(new Label("The list is empty!"));
        else {
            Integer count = 1;

            for(int i = 0; i < value.size(); i ++) {
                LinkedTreeMap linkedTreeMap = (LinkedTreeMap) value.get(i);
                Text text = new Text((count) + ". " + linkedTreeMap.get("path"));
                text.setWrappingWidth(400);
                listValue.getChildren().add(text);
                count ++;
            }
        }
        return listValue;
    }
}
