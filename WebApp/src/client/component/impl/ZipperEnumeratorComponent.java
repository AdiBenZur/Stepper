package client.component.impl;

import client.component.api.AbstractComponent;
import datadefinition.impl.enumerator.type.ZipperEnumerator;
import dto.io.IODataValueDTO;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class ZipperEnumeratorComponent extends AbstractComponent {

    public ZipperEnumeratorComponent(IODataValueDTO data) {
        super(data);
    }

    @Override
    public Parent showComponent() {
        ZipperEnumerator value = (ZipperEnumerator) data.getValue();
        return new Label(value.toString());
    }
}
