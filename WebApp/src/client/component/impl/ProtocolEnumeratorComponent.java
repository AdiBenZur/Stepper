package client.component.impl;

import client.component.api.AbstractComponent;
import datadefinition.impl.enumerator.type.ProtocolEnumerator;
import dto.io.IODataValueDTO;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class ProtocolEnumeratorComponent extends AbstractComponent {

    public ProtocolEnumeratorComponent(IODataValueDTO data) {
        super(data);
    }

    @Override
    public Parent showComponent() {
        ProtocolEnumerator value = (ProtocolEnumerator) data.getValue();
        return new Label(value.toString());
    }
}
