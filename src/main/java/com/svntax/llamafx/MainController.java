package com.svntax.llamafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;

public class MainController {

    private StringProperty pathToLlamacppFolder;
    @FXML
    private TextField pathToLlamacppFolderField;

    @FXML
    private TextField gpuLayersField;
    @FXML
    private TextField threadsField;
    @FXML
    private TextField portField;

    @FXML
    private Label contextSizeLabel;
    @FXML
    private Slider contextSizeSlider;

    private StringProperty pathToModel;
    @FXML
    private TextField pathToModelField;

    @FXML
    public void initialize(){
        pathToLlamacppFolder = new SimpleStringProperty("");
        pathToModel = new SimpleStringProperty("");

        gpuLayersField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), -1));
        threadsField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 1));
        portField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 5001));

        contextSizeSlider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number oldValue, Number newValue) -> {
            int value = newValue.intValue();
            int interval = (int) contextSizeSlider.getMajorTickUnit();
            int roundedValue = Math.round((float) value / interval) * interval;
            contextSizeLabel.setText(Integer.toString(roundedValue));
        });
    }

    @FXML
    private void onBrowsePathToLlamacpp(ActionEvent event){
        Button source = (Button) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        DirectoryChooser dirChooser = new DirectoryChooser();
        File initialDir = new File(pathToLlamacppFolder.getValue());
        if(initialDir.exists()){
            dirChooser.setInitialDirectory(initialDir);
        }
        File selectedDir = dirChooser.showDialog(stage);
        if(selectedDir != null){
            pathToLlamacppFolder.setValue(selectedDir.getAbsolutePath());
            pathToLlamacppFolderField.setText(pathToLlamacppFolder.getValue());
        }
    }

    @FXML
    private void onBrowsePathToModel(ActionEvent event){
        Button source = (Button) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        File initialFile = new File(pathToModel.getValue());
        if(initialFile.exists()){
            fileChooser.setInitialFileName(initialFile.getAbsolutePath());
            fileChooser.setInitialDirectory(initialFile.getParentFile());
        }
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile != null){
            pathToModel.setValue(selectedFile.getAbsolutePath());
            pathToModelField.setText(pathToModel.getValue());
        }
    }

    @FXML
    private void onSave(ActionEvent event){

    }

    @FXML
    private void onLoad(ActionEvent event){

    }

    @FXML
    private void onHelp(ActionEvent event){

    }

    @FXML
    private void onLaunch(ActionEvent event){

    }
}