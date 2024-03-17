package desktop.component.api;

import io.api.IODefinitionData;

import java.util.Map;

public abstract class AbstractComponent implements Component{

    protected final IODefinitionData data;
    protected final Map<IODefinitionData, Object> map;

    public AbstractComponent(IODefinitionData data, Map<IODefinitionData, Object> map) {
        this.data = data;
        this.map = map;
    }
}
