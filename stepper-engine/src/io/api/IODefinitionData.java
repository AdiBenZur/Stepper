package io.api;

import datadefinition.api.DataDefinition;

public interface IODefinitionData {

    String getName();
    String getUserString();
    void setUserString(String userStr);
    DataNecessity getNecessity();
    DataDefinition getDataDefinition();
    void setName(String alias);

    int compareTo(String otherName);
}
