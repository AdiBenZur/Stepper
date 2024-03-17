package client.component.api;

import dto.io.IODataValueDTO;

public abstract class AbstractComponent implements Component{

    protected final IODataValueDTO data;

    public AbstractComponent(IODataValueDTO data) {
        this.data = data;
    }
}
