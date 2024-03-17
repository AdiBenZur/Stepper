package client.component.impl;

import client.component.api.AbstractComponent;
import dto.io.IODataValueDTO;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class StringListComponent extends AbstractComponent {

    public StringListComponent(IODataValueDTO data) {
        super(data);
    }

    @Override
    public Parent showComponent() {
        ArrayList value = (ArrayList) data.getValue();
        VBox listValue = new VBox();
        listValue.setSpacing(5);

        if(value.size() == 0)
            listValue.getChildren().add(new Label("The list is empty!"));
        else {
            for (int i = 0; i < value.size(); i++) {
                Text text = new Text((i + 1) + ". " + value.get(i));
                text.setWrappingWidth(400);
                listValue.getChildren().add(text);
            }
        }

        return listValue;
    }
}
