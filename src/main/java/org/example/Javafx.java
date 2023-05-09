package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import guitarHierarchy.Guitar;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import serialization.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Javafx extends Application {
    private static Stage window = null;
    private static Scene defaultScene = null;
    private static final String hierarchyPath = "guitarHierarchy.";
    private static ArrayList<ObjectDescription> guitarRecords = new ArrayList<>();
    private static final ArrayList<String> currentValues = new ArrayList<>();
    private static final HashSet<String> commonDataTypes = new HashSet<>(Arrays.asList("int", "double", "java.lang.String"));
    private static final ArrayList<String> classPaths = new ArrayList<>(Arrays.asList(
            hierarchyPath + "Guitar", hierarchyPath + "UnpluggedGuitar", hierarchyPath + "PluggedGuitar",
            hierarchyPath + "ElectricGuitar", hierarchyPath + "BassGuitar"));
    private static final ArrayList<String> hierarchyClassnames = castHierarchyClassnamesFromClasspaths(classPaths);
    private static final TableView<Guitar> guitarTable = new TableView<>();
    private static int errorCounter = 0;
    private static final int defaultMenuComboBoxInsetY = 415;
    private static final int defaultMenuComboBoxInsetX = 520;
    private static final int distanceBetweenUIElements = 40;
    private static final int defaultErrorWindowHeight = 75;
    private static final int listOperationListOffset = 45;
    private static final int defaultMenuButtonHeight = 45;
    private static final int defaultMenuButtonWidth = 160;
    private static final int defaultMenuTableInsetY = 40;
    private static final int secondButtonLayoutX = 270;
    private static final int defaultMenuOffsetX = 340;
    private static final int defaultSceneLength = 680;
    private static final int mediumButtonHeight = 35;
    private static final int defaultSceneWidth = 510;
    private static final int defaultMenuInsetY = 435;
    private static final int defaultMenuInsetX = 140;
    private static final int errorButtonWidth = 210;
    private static final int inheritanceWidth = 300;
    private static final int listSceneHeight = 190;
    private static final int listMainLayoutX = 180;
    private static final int listSceneWidth = 350;
    private static final int listButtonWidth = 70;
    private static final int bigButtonHeight = 30;
    private static final int listMainLayoutY = 60;
    private static final int comboBoxWidth = 147;
    private static final int menuBarMargin = 490;
    private static final int defaultLayoutX = 20;
    private static final int defaultWidth = 120;
    private static final int mediumOffset = 150;
    private static final int rightOffset = 250;
    private static final int tableHeight = 400;
    private static final int listLayoutX = 100;
    private static final int listLayoutY = 50;
    private static final int tableWidth = 500;
    private static final int labelHeight = 15;
    private static final int listLayout = 10;
    private static final int mediumFont = 19;
    private static final int bigFont = 24;

    @Override
    public void start(Stage stage) {
        StackPane defaultLayout = new StackPane();
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem saveMenu = new Menu("Save");
        MenuItem openMenu = new Menu("Open");
        fileMenu.getItems().addAll(saveMenu, openMenu);
        Menu pluginMenu = new Menu("Plugin");
        menuBar.getMenus().addAll(fileMenu, pluginMenu);
        StackPane.setMargin(menuBar, new Insets(0, 0, menuBarMargin, 0));
        defaultLayout.getChildren().add(menuBar);

        window = stage;
        ArrayList<String> tableParameters = new ArrayList<>(Arrays.asList("brand", "model", "color", "material"));

        Button addObject = createNamedButton(defaultLayout, "Add guitar of this class");
        Button editObject = createNamedButton(defaultLayout, "Edit chosen guitar");
        Button deleteObject = createNamedButton(defaultLayout, "Delete chosen guitar");
        addObject.setDisable(true);
        editObject.setDisable(true);
        deleteObject.setDisable(true);

        ComboBox<String> classComboBox = createComboBox(defaultLayout, hierarchyClassnames, hierarchyClassnames.get(0));
        classComboBox.setOnAction(e -> onComboBoxChange(classComboBox.getValue(), addObject, editObject, deleteObject));

        addSerializedMenuItems(saveMenu, openMenu, classComboBox);

        changeElementParams(addObject, -1, -1, defaultMenuButtonWidth, defaultMenuButtonHeight, "");
        changeElementParams(editObject, -1, -1, defaultMenuButtonWidth, defaultMenuButtonHeight, "");
        changeElementParams(deleteObject, -1, -1, defaultMenuButtonWidth, defaultMenuButtonHeight, "");
        addTableColumns(tableParameters);
        setTableSize(defaultLayout);

        StackPane.setMargin(addObject, new Insets(defaultMenuInsetY, defaultMenuInsetX + defaultMenuOffsetX, 0, 0));
        StackPane.setMargin(editObject, new Insets(defaultMenuInsetY, defaultMenuInsetX, 0, 0));
        StackPane.setMargin(deleteObject, new Insets(defaultMenuInsetY, defaultMenuInsetX - defaultMenuOffsetX, 0, 0));
        StackPane.setMargin(guitarTable, new Insets(0, defaultMenuInsetX, defaultMenuTableInsetY, 0));
        StackPane.setMargin(classComboBox, new Insets(0, 0, defaultMenuComboBoxInsetY, defaultMenuComboBoxInsetX));

        defaultScene = new Scene(defaultLayout, defaultSceneLength, defaultSceneWidth);
        setNewScene(window, defaultScene, "Guitar table");
        window.show();

        addObject.setOnAction(addLambda -> generateClassWindow(classComboBox.getValue(), -1));
        editObject.setOnAction(editLambda -> processObjectEditing(classComboBox.getValue()));
        deleteObject.setOnAction(deleteLambda -> processObjectDeleting(classComboBox.getValue()));
        guitarTable.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1 && guitarTable.getSelectionModel().getSelectedIndex() != -1) {
                processDoubleClickingOnTable(guitarTable.getSelectionModel().getSelectedIndex(), classComboBox.getValue());
            }
        });
    }

    public static void addSerializedMenuItems(MenuItem saveMenu, MenuItem openMenu, ComboBox<String> classComboBox) {
        final List<Serializer> serializers = Arrays.asList(new Arbitrary(), new Binary(), new JSON());
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        for (Serializer serializer : serializers) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    serializer.getType().getFileFilter(), serializer.getType().getStrFilter()));
        }
        saveMenu.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(null);
            for(Serializer serializer: serializers){
                if(getExtension(selectedFile.getName()).equals(getExtension(serializer.getType().getStrFilter()))){
                    serializer.serialize(guitarRecords, selectedFile);
                    break;
                }
            }
        });
        openMenu.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(null);
            for(Serializer serializer: serializers){
                if(getExtension(selectedFile.getName()).equals(getExtension(serializer.getType().getStrFilter()))){
                    guitarRecords = serializer.deserialize(selectedFile);
                    updateTableView(classComboBox.getValue());
                }
            }
        });
    }

    public static String getExtension(String path) {
        StringBuilder name = new StringBuilder();
        int i = path.length();
        boolean wasLastDotFound = false;
        while (!wasLastDotFound) {
            i--;
            if (i == -1 || path.charAt(i) == '.') {
                name.append(".");
                wasLastDotFound = true;
            }
        }
        for (i += 1; i < path.length(); i++) {
            name.append(path.charAt(i));
        }
        return name.toString();
    }

    public static Label createLabel(Pane layout, String str) {
        Label label = new Label(str);
        layout.getChildren().add(label);
        return label;
    }

    public static void addTableColumns(ArrayList<String> tableParameters) {
        for (String parameter : tableParameters) {
            TableColumn<Guitar, String> column = new TableColumn<>(parameter);
            column.setCellValueFactory(new PropertyValueFactory<>(parameter));
            guitarTable.getColumns().add(column);
        }
    }

    public static void processDoubleClickingOnTable(int chosenPos, String enumValue) {
        if (!enumValue.equals("All")) {
            int pos = findChosenPosInGuitarRecords(hierarchyPath + enumValue, chosenPos);
            generateClassWindow(enumValue, pos);
            updateTableView(enumValue);
        }
    }

    public static void processObjectEditing(String enumValue) {
        int chosenPos = guitarTable.getSelectionModel().getSelectedIndex();
        if (chosenPos != -1) {
            int pos = findChosenPosInGuitarRecords(hierarchyPath + enumValue, chosenPos);
            generateClassWindow(enumValue, pos);
            updateTableView(enumValue);
        }
    }

    public static void processObjectDeleting(String enumValue) {
        int chosenPos = guitarTable.getSelectionModel().getSelectedIndex();
        if (chosenPos != -1) {
            int pos = findChosenPosInGuitarRecords(hierarchyPath + enumValue, chosenPos);
            guitarRecords.remove(pos);
            updateTableView(enumValue);
        }
    }

    public static int findChosenPosInGuitarRecords(String classPath, int chosenPos) {
        int i = 0;
        while (chosenPos >= 0) {
            if (guitarRecords.get(i).getClassName().equals(classPath)) {
                chosenPos--;
            }
            i++;
        }
        return i - 1;
    }

    public static void addGuitarRecord(ObjectDescription objectDescription) {
        ObjectDescription copy = getDefaultClassDescription(objectDescription.getClassName());
        ArrayList<Element> newElements = new ArrayList<>();
        for (Element element : objectDescription.getElements()) {
            newElements.add(new Element(element.getFieldType(), element.getFieldName(),
                    element.getValue(), element.getCurrentLevelOfHierarchy()));
        }
        copy.setElements(newElements);
        guitarRecords.add(copy);
    }

    public static ArrayList<Guitar> filterRecordsByClass(ArrayList<ObjectDescription> guitarRecord, String path) {
        ArrayList<Guitar> filteredRecords = new ArrayList<>();
        for (ObjectDescription objectDescription : guitarRecord) {
            boolean isTrue = findValueOfGivenType((ArrayList<Element>) objectDescription.getElements(),
                    "isLeftHanded").equals("true");
            if (objectDescription.getClassName().equals(path) || path.equals(hierarchyPath + "All")) {
                Guitar currGuitar = new Guitar(
                        findValueOfGivenType((ArrayList<Element>) objectDescription.getElements(), "brand"),
                        findValueOfGivenType((ArrayList<Element>) objectDescription.getElements(), "model"),
                        findValueOfGivenType((ArrayList<Element>) objectDescription.getElements(), "color"),
                        findValueOfGivenType((ArrayList<Element>) objectDescription.getElements(), "material"),
                        Integer.parseInt(findValueOfGivenType((ArrayList<Element>) objectDescription.getElements(), "frets")), isTrue);
                filteredRecords.add(currGuitar);
            }
        }
        return filteredRecords;
    }

    public static String findValueOfGivenType(ArrayList<Element> elements, String type) {
        boolean isReached = false;
        int i = 0;
        while (!isReached && i < elements.size()) {
            if (elements.get(i).getFieldName().equals(type)) {
                isReached = true;
            } else {
                i++;
            }
        }
        return elements.get(i).getValue();
    }

    public static ArrayList<String> getEnumValues(String path) {
        ArrayList<String> enumValues = new ArrayList<>();
        Class<?> myClass;
        try {
            myClass = Class.forName(path);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        Field[] fields = myClass.getDeclaredFields();
        for (int i = 0; i < fields.length - 1; i++) {
            enumValues.add(fields[i].getName());
        }
        return enumValues;
    }

    public static int getInheritanceLevel(String classPath) {
        Class<?> myClass;
        try {
            myClass = Class.forName(classPath);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        int i = -1;
        do {
            myClass = myClass.getSuperclass();
            i++;
        } while (!myClass.getName().equals("java.lang.Object")); //as a superclass of all classes
        return i;
    }

    public static void generateClassWindow(String className, int pos) {
        Pane sceneLayout = new Pane();
        String classpath = hierarchyPath + className;
        int inheritanceLevel = getInheritanceLevel(classpath);
        ObjectDescription objectDescription = getObjectDescriptionByPos(pos, classpath);
        String context = getContextFromPos(pos);
        Button exitFromAddWindow = createNamedButton(sceneLayout, "Exit window");
        Button actionWithList = createNamedButton(sceneLayout, context + "guitar");

        changeElementParams(exitFromAddWindow, defaultLayoutX, rightOffset, defaultWidth + mediumOffset *
                (inheritanceLevel), bigButtonHeight, "");
        changeElementParams(actionWithList, listLayout + mediumOffset * (inheritanceLevel + 1), rightOffset,
                defaultWidth + mediumOffset * (inheritanceLevel), bigButtonHeight, "");
        addUIElements(objectDescription, sceneLayout);
        Scene classScene = new Scene(sceneLayout, inheritanceWidth * (inheritanceLevel + 1), inheritanceWidth);

        setNewScene(window, classScene, context + convertPathToName(classpath) + " record");

        exitFromAddWindow.setOnAction(e -> {
            setNewScene(window, defaultScene, "Guitar table");
            updateTableView(className);
        });
        actionWithList.setOnAction(e -> {
            String errorStr = checkingFieldsForAdding((ArrayList<Element>) objectDescription.getElements(),
                    currentValues, false);
            if (errorCounter == 0) {
                for (int j = 0; j < currentValues.size(); j++) {
                    objectDescription.getElements().get(j).setValue(currentValues.get(j));
                }
                if (pos == -1) {
                    addGuitarRecord(objectDescription);
                } else {
                    changeGuitarRecord(objectDescription, pos);
                }
            } else {
                processErrorWindow(errorStr, window);
            }
        });
    }

    public static void setTableSize(StackPane layout) {
        guitarTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        guitarTable.setMaxHeight(tableHeight);
        guitarTable.setMaxWidth(tableWidth);
        layout.getChildren().add(guitarTable);
    }

    public static ObjectDescription getObjectDescriptionByPos(int pos, String classPath) {
        if (pos == -1)
            return getDefaultClassDescription(classPath);
        return guitarRecords.get(pos);
    }

    public static String getContextFromPos(int pos) {
        if (pos == -1)
            return "Add ";
        return "Edit ";
    }

    public static void changeGuitarRecord(ObjectDescription objectDescription, int pos) {
        guitarRecords.set(pos, objectDescription);
    }

    public static String checkingFieldsForAdding(ArrayList<Element> elements, ArrayList<String> list, boolean isList) {
        StringBuilder stringBuilder = new StringBuilder();
        System.out.println("New size: " + list.size());
        errorCounter = 0;
        for (int i = 0; i < list.size(); i++) {
            System.out.println(elements.get(i).getFieldType() + " = " + list.get(i));
            if (!checkingTextFieldsCorrectness(elements.get(i), list.get(i), stringBuilder, "") &&
                    elements.get(i).getFieldType().contains("java.util.List")) {
                checkListEmptiness(elements.get(i).getFieldName(), list.get(i), isList, stringBuilder);
                Element extractedElement = castDefaultElementFromStringName
                        (extractGenericType(elements.get(i).getFieldType()), elements.get(i).getFieldName());
                if (isList) {
                    if (extractedElement.getFieldType().equals("") && elements.get(i).getFieldType().contains("associations.")) {
                        ObjectDescription objectDescription = getDefaultClassDescription
                                (extractGenericType(elements.get(i).getFieldType()));
                        ArrayList<String> strList = destructString(list.get(i));
                        for (int j = 0; j < strList.size(); j++) {
                            System.out.println("Size: " + strList.size());
                            checkingTextFieldsCorrectness(objectDescription.getElements().get(j),
                                    strList.get(j), stringBuilder, i + ". ");
                        }
                        strList.clear();
                    } else {
                        checkingTextFieldsCorrectness(extractedElement, list.get(i), stringBuilder, i + ". ");
                    }
                }
            }
        }
        System.out.println("not in this method");
        return stringBuilder.toString();
    }

    public static void checkListEmptiness(String elementType, String str, boolean isList, StringBuilder stringBuilder) {
        if (!isList && str.equals("")) {
            errorCounter++;
            stringBuilder.append(elementType).append(" list is empty\n");
        }
    }

    public static boolean checkingTextFieldsCorrectness(Element element, String currValue, StringBuilder stringBuilder,
                                                        String additionalString) {
        boolean wasReached = false;
        switch (element.getFieldType()) {
            case "java.lang.String" -> {
                wasReached = true;
                if (currValue.equals("")) {
                    stringBuilder.append(additionalString).append(element.getFieldName()).append(" field is empty.\n");
                    errorCounter++;
                }
            }
            case "double" -> {
                wasReached = true;
                try {
                    Double.parseDouble(currValue);
                } catch (NumberFormatException numberFormatException) {
                    stringBuilder.append(additionalString).append(element.getFieldName()).append(" must contain double.\n");
                    errorCounter++;
                }
            }
            case "int" -> {
                wasReached = true;
                try {
                    Integer.parseInt(currValue);
                } catch (NumberFormatException numberFormatException) {
                    stringBuilder.append(additionalString).append(element.getFieldName()).append(" must contain int.\n");
                    errorCounter++;
                }
            }
        }
        return wasReached;
    }


    public static Element castDefaultElementFromStringName(String name, String fieldName) {
        Element defaultElement = new Element();
        defaultElement.setFieldType("");
        defaultElement.setFieldName(fieldName);
        switch (name) {
            case "java.lang.Integer" -> defaultElement.setFieldType("int");
            case "java.lang.Double" -> defaultElement.setFieldType("double");
            case "java.lang.String" -> defaultElement.setFieldType(name);
        }
        return defaultElement;
    }

    public static void processErrorWindow(String str, Stage stage) {
        Pane errorLayout = new Pane();
        Label errorLabel = createLabel(errorLayout, str);
        changeElementParams(errorLabel, defaultLayoutX, listLayout, -1, -1, "");
        Button confirmForErrorLayout = createNamedButton(errorLayout, "Click to close!");
        changeElementParams(confirmForErrorLayout, defaultLayoutX, defaultLayoutX + labelHeight *
                (errorCounter + 1), errorButtonWidth, bigButtonHeight, "");

        Stage errorWindow = new Stage();
        errorWindow.initModality(Modality.WINDOW_MODAL);
        errorWindow.initOwner(stage);
        Scene errorScene = new Scene(errorLayout, rightOffset, defaultErrorWindowHeight + labelHeight * errorCounter);
        setNewScene(errorWindow, errorScene, "Errors occurred!");
        errorWindow.show();

        confirmForErrorLayout.setOnAction(pressErrorButton -> {
            errorWindow.close();
            errorCounter = 0;
        });

        errorWindow.setOnCloseRequest(closeRequest -> {
            errorWindow.close();
            errorCounter = 0;
        });
    }

    public static void setNewScene(Stage window, Scene scene, String title) {
        window.setScene(scene);
        window.setTitle(title);
    }

    public static void addUIElements(ObjectDescription objectDescription, Pane sceneLayout) {
        int currLevelOfHierarchy = getInheritanceLevel(objectDescription.getClassName());
        int currY = 1;
        int i = 0;
        boolean isFinished = false;
        setCurrentValues((ArrayList<Element>) objectDescription.getElements());
        while (!isFinished) {
            while (objectDescription.getElements().get(i).getCurrentLevelOfHierarchy() != currLevelOfHierarchy) {
                i++;
            }
            Label label = createLabel(sceneLayout, objectDescription.getElements().get(i).getFieldName());
            changeElementParams(label, defaultLayoutX + inheritanceWidth *
                    (getInheritanceLevel(objectDescription.getClassName()) - objectDescription.getElements().get(i).
                            getCurrentLevelOfHierarchy()), -defaultLayoutX + (distanceBetweenUIElements * currY), -1, -1, "");

            if (objectDescription.getElements().get(i).getFieldType().contains("java.util.List")) {
                Button listButton = createNamedButton(sceneLayout, "Go to list");
                changeElementParams(listButton, defaultWidth + inheritanceWidth * (getInheritanceLevel(objectDescription.getClassName()) - objectDescription.getElements().get(i).getCurrentLevelOfHierarchy()), -defaultLayoutX + (distanceBetweenUIElements * currY), comboBoxWidth, defaultLayoutX, String.valueOf(i));
                listButton.setOnAction(buttonPressed -> moveToListWindow(window, objectDescription.getElements().
                        get(Integer.parseInt(listButton.getAccessibleHelp())), Integer.parseInt(listButton.getAccessibleHelp())));
            } else if (commonDataTypes.contains(objectDescription.getElements().get(i).getFieldType())) {
                TextField textField = createTextField(sceneLayout, objectDescription.getElements().get(i).getValue());
                changeElementParams(textField, defaultWidth + inheritanceWidth * (getInheritanceLevel(objectDescription.getClassName()) - objectDescription.getElements().get(i).getCurrentLevelOfHierarchy()),
                        -defaultLayoutX + (distanceBetweenUIElements * currY), -1, -1, String.valueOf(i));
                textField.textProperty().addListener((observable, oldValue, newValue) -> currentValues.set
                        (Integer.parseInt(textField.getAccessibleHelp()), newValue));
            } else if (objectDescription.getElements().get(i).getFieldType().equals("boolean")) {
                RadioButton radioButton = createRadioButton(sceneLayout, objectDescription.getElements().get(i).getValue().equals("true"));
                changeElementParams(radioButton, defaultWidth + inheritanceWidth * (getInheritanceLevel(objectDescription.getClassName()) -
                        objectDescription.getElements().get(i).getCurrentLevelOfHierarchy()), -defaultLayoutX + (distanceBetweenUIElements * currY), -1, -1, String.valueOf(i));
                AtomicBoolean isSelected = new AtomicBoolean();
                isSelected.set(objectDescription.getElements().get(i).getValue().equals("true"));
                radioButton.setOnAction(e -> {
                    if (isSelected.get()) {
                        currentValues.set(Integer.parseInt(radioButton.getAccessibleHelp()), "false");
                        isSelected.set(false);
                    } else {
                        currentValues.set(Integer.parseInt(radioButton.getAccessibleHelp()), "true");
                        isSelected.set(true);
                    }
                });
            } else if (objectDescription.getElements().get(i).getFieldType().contains("enums.")) {
                ArrayList<String> enumValues = getEnumValues(objectDescription.getElements().get(i).getFieldType());
                ComboBox<String> enumComboBox = createComboBox(sceneLayout, enumValues,
                        enumValues.get(findPosInNum(enumValues, objectDescription.getElements().get(i).getValue())));
                changeElementParams(enumComboBox, defaultWidth + inheritanceWidth * (getInheritanceLevel(objectDescription.getClassName()) -
                                objectDescription.getElements().get(i).getCurrentLevelOfHierarchy()), -defaultLayoutX + (distanceBetweenUIElements * currY),
                        comboBoxWidth, -1, String.valueOf(i));
                enumComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                        currentValues.set(Integer.parseInt(enumComboBox.getAccessibleHelp()), newValue));
            }
            i++;
            currY++;
            if (i == objectDescription.getElements().size() || objectDescription.getElements().get(i).getCurrentLevelOfHierarchy() > currLevelOfHierarchy) {
                i = 0;
                currY = 1;
                currLevelOfHierarchy--;
                if (currLevelOfHierarchy == -1)
                    isFinished = true;
            }
        }
    }

    public static String extractGenericType(String fullType) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isGenericSignReached = false;
        for (int i = 0; i < fullType.length(); i++) {
            if (isGenericSignReached && fullType.charAt(i) != '>') {
                stringBuilder.append(fullType.charAt(i));
            } else if (fullType.charAt(i) == '<') {
                isGenericSignReached = true;
            }
        }
        return stringBuilder.toString();
    }

    public static void moveToListWindow(Stage stage, Element element, int i) {
        Pane listLayout = new Pane();
        String extractedType = extractGenericType(element.getFieldType());
        ArrayList<String> valueList = getListFromValue(element.getValue());
        ArrayList<String> listCount = getListCount(valueList.size());
        ComboBox<String> currentListElement = createComboBox(listLayout, listCount, "do not set");
        if (valueList.size() != 0) {
            currentListElement.setValue(String.valueOf(0));
            showListUI(listLayout, valueList, extractedType, Integer.parseInt(currentListElement.getValue()),
                    element.getFieldName());
        }
        changeElementParams(currentListElement, secondButtonLayoutX, Javafx.listLayout, listButtonWidth, mediumButtonHeight, "");

        Button changeList = createNamedButton(listLayout, "Change");
        Button exitFromListEditor = createNamedButton(listLayout, "Exit without saving");
        Button addInList = createNamedButton(listLayout, "+");
        Button removeFromList = createNamedButton(listLayout, "-");
        addInList.setFont(new Font("Arial", mediumFont));
        removeFromList.setFont(new Font("Arial", mediumFont));

        changeElementParams(changeList, Javafx.listLayout, mediumOffset, defaultMenuButtonWidth, bigButtonHeight, "");
        changeElementParams(exitFromListEditor, listMainLayoutX, mediumOffset, defaultMenuButtonWidth, bigButtonHeight, "");
        changeElementParams(addInList, secondButtonLayoutX, listMainLayoutY, listButtonWidth, bigButtonHeight, "");
        changeElementParams(removeFromList, secondButtonLayoutX, listMainLayoutY + listOperationListOffset, listButtonWidth, bigButtonHeight, "");

        Scene listScene = new Scene(listLayout, listSceneWidth, listSceneHeight);
        Stage listWindow = new Stage();
        setNewScene(listWindow, listScene, "List window");
        listWindow.initModality(Modality.WINDOW_MODAL);
        listWindow.initOwner(stage);
        listWindow.show();

        exitFromListEditor.setOnAction(pressExitButton -> listWindow.close());

        changeList.setOnAction(pressChangeButton -> {
            ArrayList<Element> listForElement = createListOfTypesForValueList(element, valueList.size());
            String errorStr = checkingFieldsForAdding(listForElement, valueList, true);
            if (errorStr.equals("")) {
                element.setValue(constructString(valueList));
                currentValues.set(i, element.getValue());
                listWindow.close();
            } else {
                processErrorWindow(errorStr, listWindow);
            }
        });

        addInList.setOnAction(addListAction -> {
            valueList.add("");
            listCount.add(String.valueOf(valueList.size() - 1));
            currentListElement.getItems().add(String.valueOf(valueList.size() - 1));
            currentListElement.setValue(String.valueOf(valueList.size() - 1));
            showListUI(listLayout, valueList, extractedType, valueList.size() - 1, element.getFieldName());
        });

        removeFromList.setOnAction(removeAction -> {
            if (valueList.size() < 2) {
                processErrorWindow("List cannot be empty!", listWindow);
            } else {
                valueList.remove(Integer.parseInt(currentListElement.getValue()));
                listCount.remove(Integer.parseInt(currentListElement.getValue()));
                currentListElement.getItems().remove(valueList.size());
                showListUI(listLayout, valueList, extractedType, Integer.parseInt(currentListElement.getValue()),
                        element.getFieldName());
            }
        });

        currentListElement.setOnAction(changeAction -> showListUI(listLayout, valueList, extractedType,
                Integer.parseInt(currentListElement.getValue()), element.getFieldName()));
    }

    public static ArrayList<Element> createListOfTypesForValueList(Element element, int n) {
        ArrayList<Element> listOfTypesForValueList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            listOfTypesForValueList.add(element);
        }
        return listOfTypesForValueList;
    }

    public static ComboBox<String> createComboBox(Pane layout, ArrayList<String> list, String value) {
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(list));
        if (!value.equals("do not set")) {
            comboBox.setValue(value);
        }
        layout.getChildren().add(comboBox);
        return comboBox;
    }

    public static void changeElementParams(Control element, double layoutX, double layoutY,
                                           double width, double height, String accessibleHelp) {
        if (layoutX != -1) {
            element.setLayoutX(layoutX);
        }
        if (layoutY != -1) {
            element.setLayoutY(layoutY);
        }
        if (width != -1) {
            element.setMinWidth(width);
        }
        if (height != -1) {
            element.setMinHeight(height);
        }
        element.setAccessibleHelp(accessibleHelp);
    }

    public static String constructString(ArrayList<String> stringArrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : stringArrayList) {
            stringBuilder.append(str).append('\n');
        }
        return stringBuilder.toString();
    }

    public static ArrayList<String> destructString(String str) {
        ArrayList<String> strArray = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        System.out.println(str);
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '|' || i == str.length() - 1) {
                strArray.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            } else {
                stringBuilder.append(str.charAt(i));
            }
        }
        for (int i = 0; i < strArray.size(); i++) {
            System.out.println(i + " " + strArray.get(i));
        }
        return strArray;
    }

    public static void showListUI(Pane layout, ArrayList<String> valueList, String extractedType, int pos, String fieldName) {
        Label titleLabel = createLabel(layout, convertPathToName(extractedType) + ":");
        changeElementParams(titleLabel, listLayout, listLayout, -1, -1, "");
        titleLabel.setFont(new Font("Arial", bigFont));
        if (extractedType.contains("associations")) {
            ObjectDescription objectDescription = getDefaultClassDescription(extractedType);
            if (valueList.get(pos).equals("")) {
                valueList.set(pos, "|".repeat(objectDescription.getElements().size() - 1));
            }
            for (int i = 0; i < objectDescription.getElements().size(); i++) {
                Label label = createLabel(layout, objectDescription.getElements().get(i).getFieldName());
                changeElementParams(label, bigButtonHeight, listLayoutY + bigButtonHeight * i, -1, -1, "");
                if (commonDataTypes.contains(objectDescription.getElements().get(i).getFieldType())) {
                    TextField textField = createTextField(layout, getSubstringFromSeparatedString(valueList.get(pos), i));
                    changeElementParams(textField, listLayoutX, listLayoutY + bigButtonHeight * i, -1, -1, String.valueOf(i));
                    textField.textProperty().addListener((observable, oldValue, newValue) -> valueList.set(pos,
                            buildNewListStr(valueList.get(pos), newValue, Integer.parseInt(textField.getAccessibleHelp()))));
                } else if (objectDescription.getElements().get(i).getFieldType().equals("boolean")) {
                    AtomicBoolean isSelected = new AtomicBoolean();
                    isSelected.set(getSubstringFromSeparatedString(valueList.get(pos), i).equals("true"));
                    RadioButton radioButton = createRadioButton(layout, getSubstringFromSeparatedString(valueList.get(pos), i).equals("true"));
                    changeElementParams(radioButton, listLayoutX, listLayoutY + bigButtonHeight * i, -1, -1, String.valueOf(i));
                    radioButton.setOnAction(e -> {
                        if (isSelected.get()) {
                            System.out.println(getSubstringFromSeparatedString(valueList.get(pos), Integer.parseInt(radioButton.getAccessibleHelp())));
                            valueList.set(pos, buildNewListStr(valueList.get(pos), "false", Integer.parseInt(radioButton.getAccessibleHelp())));
                            isSelected.set(false);
                            System.out.println(getSubstringFromSeparatedString(valueList.get(pos), Integer.parseInt(radioButton.getAccessibleHelp())));
                        } else {
                            System.out.println(getSubstringFromSeparatedString(valueList.get(pos), Integer.parseInt(radioButton.getAccessibleHelp())));
                            valueList.set(pos, buildNewListStr(valueList.get(pos), "true", Integer.parseInt(radioButton.getAccessibleHelp())));
                            isSelected.set(true);
                            System.out.println(getSubstringFromSeparatedString(valueList.get(pos), Integer.parseInt(radioButton.getAccessibleHelp())));
                        }
                    });
                } else if (objectDescription.getElements().get(i).getFieldType().contains("enums.")) {
                    ArrayList<String> enumValues = getEnumValues(objectDescription.getElements().get(i).getFieldType());
                    ComboBox<String> enumComboBox = createComboBox(layout, enumValues, enumValues.get(findPosInNum(enumValues, getSubstringFromSeparatedString(valueList.get(pos), i))));
                    changeElementParams(enumComboBox, listLayoutX, listLayoutY + bigButtonHeight * i, comboBoxWidth, -1, String.valueOf(i));
                    enumComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                            valueList.set(pos, buildNewListStr(valueList.get(pos), newValue, Integer.parseInt(enumComboBox.getAccessibleHelp()))));
                }
            }
        } else if (extractedType.contains("enums.")) {
            Label label = createLabel(layout, fieldName);
            ComboBox<String> enumComboBox = createComboBox(layout, getEnumValues(extractedType),
                    getEnumValues(extractedType).get(findPosInNum(getEnumValues(extractedType), valueList.get(pos))));
            changeElementParams(label, listLayout, listLayoutY, -1, -1, "");
            changeElementParams(enumComboBox, listLayoutX, listLayoutY, comboBoxWidth, -1, "");
            enumComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) ->
                    valueList.set(pos, newValue));
        } else if (extractedType.equals("java.lang.Integer") || extractedType.equals("java.lang.String") ||
                extractedType.equals("java.lang.Double")) {
            Label label = createLabel(layout, fieldName);
            changeElementParams(label, listLayout, listLayoutY, -1, -1, "");
            TextField textField = createTextField(layout, valueList.get(pos));
            changeElementParams(textField, listLayoutX, listLayoutY, -1, -1, "");
            textField.textProperty().addListener((observable, oldValue, newValue) -> valueList.set(pos, newValue));
        }
    }

    public static String buildNewListStr(String str, String newPartOfText, int pos) {
        int start = 0, separatorCounter = 0;
        while (separatorCounter < pos) {
            if (str.charAt(start) == '|')
                separatorCounter++;
            start++;
        }
        int end = start;
        while (separatorCounter == pos && end < str.length()) {
            if (str.charAt(end) == '|') {
                separatorCounter++;
            } else {
                end++;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.delete(start, end);
        stringBuilder.insert(start, newPartOfText);

        return stringBuilder.toString();
    }

    public static RadioButton createRadioButton(Pane layout, boolean initialValue) {
        RadioButton radioButton = new RadioButton();
        radioButton.setSelected(initialValue);
        layout.getChildren().add(radioButton);
        return radioButton;
    }

    public static String getSubstringFromSeparatedString(String str, int pos) {
        StringBuilder strBuilder = new StringBuilder();
        int i = 0, separatorCounter = 0;
        while (separatorCounter < pos) {
            if (str.charAt(i) == '|')
                separatorCounter++;
            i++;
        }
        while (i < str.length() && str.charAt(i) != '|') {
            strBuilder.append(str.charAt(i));
            i++;
        }
        return strBuilder.toString();
    }

    public static ArrayList<String> getListCount(int n) {
        ArrayList<String> listCount = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            listCount.add(String.valueOf(i));
        }
        return listCount;
    }

    public static ArrayList<String> getListFromValue(String str) {
        ArrayList<String> valueList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '\n') {
                stringBuilder.append(str.charAt(i));
            } else {
                valueList.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            }
        }
        if (stringBuilder.length() != 0) {
            valueList.add(stringBuilder.toString());
        }
        return valueList;
    }

    public static void setCurrentValues(ArrayList<Element> elements) {
        currentValues.clear();
        for (Element element : elements) {
            if (commonDataTypes.contains(element.getFieldType()) || element.getFieldType().contains("java.util.List")) {
                currentValues.add(element.getValue());
            } else if (element.getFieldType().equals("boolean")) {
                if (element.getValue().equals("true")) {
                    currentValues.add("true");
                } else {
                    currentValues.add("false");
                }
            } else if (element.getFieldType().contains("enums.")) {
                currentValues.add(getEnumValues(element.getFieldType()).
                        get(findPosInNum(getEnumValues(element.getFieldType()), element.getValue())));
            }
        }
    }

    public static int findPosInNum(ArrayList<String> list, String element) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(element)) {
                return i;
            }
        }
        return 0;
    }

    public static Button createNamedButton(Pane layout, String text) {
        Button button = new Button(text);
        layout.getChildren().add(button);
        return button;
    }

    public static TextField createTextField(Pane layout, String text) {
        TextField textField = new TextField();
        textField.setText(text);
        layout.getChildren().add(textField);
        return textField;
    }

    public static ArrayList<String> castHierarchyClassnamesFromClasspaths(ArrayList<String> classPaths) {
        ArrayList<String> classNames = new ArrayList<>();
        classNames.add("All");
        for (String classPath : classPaths) {
            classNames.add(convertPathToName(classPath));
        }
        return classNames;
    }

    public static ObjectDescription getDefaultClassDescription(String classPath) {
        ObjectDescription objectDescription = new ObjectDescription();
        objectDescription.setClassName(classPath);
        ArrayList<Element> classFields = new ArrayList<>();
        Class<?> myClass;
        try {
            myClass = Class.forName(classPath);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        int i = 0;
        do {
            addClassFields(classFields, myClass, i);
            i++;
            myClass = myClass.getSuperclass();
        } while (!myClass.getName().equals("java.lang.Object"));

        objectDescription.setElements(classFields);
        return objectDescription;
    }

    public static void onComboBoxChange(String value, Button add, Button edit, Button delete) {
        add.setDisable(value.equals("All"));
        edit.setDisable(value.equals("All"));
        delete.setDisable(value.equals("All"));
        updateTableView(value);
    }

    public static void updateTableView(String value) {
        ArrayList<Guitar> filteredRecords = filterRecordsByClass(guitarRecords, hierarchyPath + value);
        guitarTable.getItems().clear();
        guitarTable.getItems().addAll(filteredRecords);
    }

    public static void addClassFields(ArrayList<Element> classFields, Class<?> myClass, int i) {
        Field[] fields = myClass.getDeclaredFields();
        for (Field field : fields) {
            Element element = new Element();
            element.setFieldType(field.getAnnotatedType().toString());
            element.setFieldName(field.getName());
            element.setValue("");
            element.setCurrentLevelOfHierarchy(i);
            classFields.add(element);
        }
    }

    public static String convertPathToName(String path) {
        StringBuilder name = new StringBuilder();
        int i = path.length();
        boolean wasLastDotFound = false;
        while (!wasLastDotFound) {
            i--;
            if (i == -1 || path.charAt(i) == '.')
                wasLastDotFound = true;
        }
        for (i += 1; i < path.length(); i++) {
            name.append(path.charAt(i));
        }
        return name.toString();
    }

    public static void main(String[] args) {
        launch();
    }
}