package io.impl;

import datadefinition.api.DataDefinition;
import io.api.DataNecessity;
import io.api.IODefinitionData;

public class IODefinitionDataImpl implements IODefinitionData {

    private String name;
    private final DataDefinition dataDefinition;
    private final DataNecessity necessity;
    private String userString;


    public IODefinitionDataImpl(String name, DataDefinition dataDefinition, DataNecessity necessity, String userString) {
        this.name = name;
        this.dataDefinition = dataDefinition;
        this.necessity = necessity;
        this.userString = userString;
    }

    @Override
    public String getName() { return name; }

    @Override
    public String getUserString() { return userString; }

    @Override
    public void setUserString(String userStr) { userString = userStr; }

    @Override
    public DataNecessity getNecessity() { return necessity; }

    @Override
    public DataDefinition getDataDefinition() { return dataDefinition; }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }
        IODefinitionData ioDefinitionData = (IODefinitionData) obj;
        return name.equals(ioDefinitionData.getName());
    }

    @Override
    public int hashCode() { return name.hashCode(); }


    @Override
    public int compareTo(String otherName) {
        if (otherName == null) {
            return 1;
        }
        return name.compareTo(otherName);
    }

    @Override
    public void setName(String alias) { name = alias; }
}
