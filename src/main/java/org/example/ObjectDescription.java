package org.example;
import java.io.Serializable;
import java.util.List;

public class ObjectDescription implements Serializable {
    private List<Element> elements;
    private String className;

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
