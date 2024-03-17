package desktop.component.impl;

import datadefinition.impl.enumerator.type.ZipperEnumerator;
import desktop.component.api.AbstractComponent;
import io.api.IODefinitionData;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.util.Map;

public class ZipperEnumeratorComponent extends AbstractComponent {

    public ZipperEnumeratorComponent(IODefinitionData data, Map<IODefinitionData, Object> map) {
        super(data, map);
    }

    @Override
    public Parent showComponent() {
        ZipperEnumerator value = (ZipperEnumerator) map.get(data);
        return new Label(value.toString());
    }
}
