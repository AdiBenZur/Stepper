package client.component.impl;

import client.component.api.AbstractComponent;
import com.google.gson.internal.LinkedTreeMap;
import dto.io.IODataValueDTO;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class MappingComponent extends AbstractComponent {

    public MappingComponent(IODataValueDTO data) {
        super(data);
    }

    @Override
    public Parent showComponent() {
        LinkedTreeMap value = (LinkedTreeMap) data.getValue();

        Double car = (Double) value.get("car");
        Double cdr = (Double) value.get("cdr");

        int carInt = (int) Math.round(car);
        int cdrInt = (int) Math.round(cdr);

        Label returnValue = new Label("[" + carInt + " , " + cdrInt + "]");
        return returnValue;
    }
}
