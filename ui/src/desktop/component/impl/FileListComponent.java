package desktop.component.impl;

import datadefinition.impl.list.type.FileList;
import desktop.component.api.AbstractComponent;
import io.api.IODefinitionData;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Map;

public class FileListComponent extends AbstractComponent {

    public FileListComponent(IODefinitionData data, Map<IODefinitionData, Object> map) {
        super(data, map);
    }

    @Override
    public Parent showComponent() {
        FileList value = (FileList)map.get(data);
        VBox listValue = new VBox();
        listValue.setSpacing(5);

        if(value.size() == 0)
            listValue.getChildren().add(new Label("The list is empty!"));
        else {
            for (int i = 0; i < value.size(); i++) {
                Text text = new Text((i + 1) + ". " + value.get(i).getAbsolutePath());
                text.setWrappingWidth(400);
                listValue.getChildren().add(text);
            }
        }
        return listValue;
    }
}
