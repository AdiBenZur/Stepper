package desktop.component.impl;

import datadefinition.impl.mapping.MappingData;
import desktop.component.api.AbstractComponent;
import io.api.IODefinitionData;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import java.util.Map;

public class MappingComponent extends AbstractComponent {

    public MappingComponent(IODefinitionData data, Map<IODefinitionData, Object> map) {
        super(data, map);
    }

    @Override
    public Parent showComponent() {
        MappingData value = (MappingData) map.get(data);
        Label returnValue = new Label("[" + value.getCar().toString() + " , " + value.getCdr().toString() + "]");
        return returnValue;
    }
}
