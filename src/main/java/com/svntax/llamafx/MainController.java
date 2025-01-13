package com.svntax.llamafx;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainController {

    @FXML
    private StackPane stackPane;

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

    private void showErrorNotification(String msg){
        Notification errorNotification = new Notification(msg);
        errorNotification.getStyleClass().addAll(Styles.ELEVATED_1, Styles.DANGER);
        errorNotification.setPrefHeight(Region.USE_PREF_SIZE);
        errorNotification.setMaxHeight(Region.USE_PREF_SIZE);
        errorNotification.translateYProperty().set(-100);

        errorNotification.setOnClose(e -> {
            var out = Animations.slideOutUp(errorNotification, Duration.millis(250));
            out.setOnFinished(f -> stackPane.getChildren().remove(errorNotification));
            out.playFromStart();
        });

        StackPane.setAlignment(errorNotification, Pos.TOP_RIGHT);
        StackPane.setMargin(errorNotification, new Insets(10, 10, 0, 0));
        stackPane.getChildren().add(errorNotification);
        errorNotification.widthProperty().addListener(((observable, oldValue, newValue) -> {
            var in = Animations.slideInDown(errorNotification, Duration.millis(250));
            in.playFromStart();
        }));
    }

    @FXML
    private void onLaunch(ActionEvent event){
        List<String> commands = new LinkedList<String>();
        ProcessBuilder builder = new ProcessBuilder();
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            commands.add("cmd.exe");
            commands.add("/c");
            commands.add("start");
        }
        else{
            commands.add("sh");
            commands.add("-c");
        }

        String[] llamaCommands = getAllCommands();
        commands.addAll(Arrays.asList(llamaCommands));
        builder.command(commands);
        File llamacppDir = new File(pathToLlamacppFolderField.getText());
        File llamaServer = new File(pathToLlamacppFolderField.getText() + "/llama-server.exe");
        if(!llamacppDir.exists() || !llamacppDir.isDirectory() || !llamaServer.exists()){
            showErrorNotification("Error: Failed to find llama-server.exe\nCheck the llama.cpp path.");
            return;
        }
        File model = new File(pathToModelField.getText());
        if(!model.exists() || model.isDirectory()){
            showErrorNotification("Error: Cannot find model file: " + pathToModelField.getText());
            return;
        }
        builder.directory(llamacppDir);
        Process process = null;
        try {
            process = builder.start();

            int exitCode = process.waitFor();
            if(exitCode != 0){
                showErrorNotification("Error: Process ended with exit code " + exitCode);
            }
        } catch (IOException e) {
            showErrorNotification("Error: " + e.getMessage());
        } catch (InterruptedException e) {
            showErrorNotification("Error: " + e.getMessage());
        }
    }

    private String[] getAllCommands(){
        String modelPath = pathToModelField.getText();
        int gpuLayers = Integer.parseInt(gpuLayersField.getText());
        gpuLayers = Math.max(0, gpuLayers);
        int threads = Integer.parseInt(threadsField.getText());
        threads = Math.max(1, threads);
        int port = Integer.parseInt(portField.getText());
        int contextSize = Integer.parseInt(contextSizeLabel.getText());
        String[] llamaCommands = {"llama-server.exe", "-m", modelPath,
                "--n-gpu-layers", Integer.toString(gpuLayers), "--threads", Integer.toString(threads),
                "--ctx-size", Integer.toString(contextSize), "--port", Integer.toString(port)};
        return llamaCommands;
    }
}