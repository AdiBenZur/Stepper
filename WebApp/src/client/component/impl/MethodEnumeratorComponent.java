package client.component.impl;

import client.component.api.AbstractComponent;
import datadefinition.impl.enumerator.type.MethodEnumerator;
import dto.io.IODataValueDTO;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class MethodEnumeratorComponent extends AbstractComponent {

    public MethodEnumeratorComponent(IODataValueDTO data) {
        super(data);
    }

    @Override
    public Parent showComponent() {
        MethodEnumerator value = (MethodEnumerator) data.getValue();
        return new Label(value.toString());
    }
}
