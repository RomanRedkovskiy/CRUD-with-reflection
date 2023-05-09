package org.example;

import java.io.Serializable;

public class Element implements Serializable {
    String fieldType;
    String fieldName;
    String value;
    int currentLevelOfHierarchy;

    public Element(String fieldType, String fieldName, String value, int currentLevelOfHierarchy)  {
        this.fieldType = fieldType;
        this.fieldName = fieldName;
        this.value = value;
        this.currentLevelOfHierarchy = currentLevelOfHierarchy;
    }

    public Element(){
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCurrentLevelOfHierarchy() {
        return currentLevelOfHierarchy;
    }

    public void setCurrentLevelOfHierarchy(int currentLevelOfHierarchy) {
        this.currentLevelOfHierarchy = currentLevelOfHierarchy;
    }
}
