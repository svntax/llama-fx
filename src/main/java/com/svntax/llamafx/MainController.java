package com.svntax.llamafx;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import javafx.beans.binding.Bindings;
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
import javafx.util.converter.NumberStringConverter;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

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

    private ConfigModel configModel;

    public MainController(){
        configModel = new ConfigModel();
    }

    @FXML
    public void initialize(){
        configModel.pathToLlamacppFolderProperty().bindBidirectional(pathToLlamacppFolderField.textProperty());
        configModel.pathToModelProperty().bindBidirectional(pathToModelField.textProperty());
        Bindings.bindBidirectional(portField.textProperty(), configModel.portProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(gpuLayersField.textProperty(), configModel.gpuLayersProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(threadsField.textProperty(), configModel.threadsCountProperty(), new NumberStringConverter());
        configModel.contextSizeProperty().bindBidirectional(contextSizeSlider.valueProperty());

        pathToLlamacppFolder = new SimpleStringProperty("");
        pathToModel = new SimpleStringProperty("");

        gpuLayersField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), ConfigModel.DEFAULT_GPU_LAYERS));
        threadsField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), ConfigModel.DEFAULT_THREADS_COUNT));
        portField.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), ConfigModel.DEFAULT_PORT));
        contextSizeSlider.setValue(ConfigModel.DEFAULT_CONTEXT_SIZE);

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
        dirChooser.setTitle("Select Folder to llama.cpp");
        File initialDir = new File(pathToLlamacppFolder.getValue());
        if(initialDir.exists()){
            dirChooser.setInitialDirectory(initialDir);
        }
        File selectedDir = dirChooser.showDialog(stage);
        if(selectedDir != null){
            pathToLlamacppFolder.set(selectedDir.getAbsolutePath());
            pathToLlamacppFolderField.setText(pathToLlamacppFolder.getValue());
        }
    }

    @FXML
    private void onBrowsePathToModel(ActionEvent event){
        Button source = (Button) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select GGUF Model File");
        File initialFile = new File(pathToModel.getValue());
        if(initialFile.exists()){
            fileChooser.setInitialFileName(initialFile.getAbsolutePath());
            fileChooser.setInitialDirectory(initialFile.getParentFile());
        }
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile != null){
            pathToModel.set(selectedFile.getAbsolutePath());
            pathToModelField.setText(pathToModel.getValue());
        }
    }

    @FXML
    private void onSave(ActionEvent event){
        Button source = (Button) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Config");
        fileChooser.setInitialFileName("config.xml");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Document (*.xml)", "*.xml"));
        File initialFile = new File(pathToModel.getValue());
        if(initialFile.exists()){
            // Name the config file using the current model's name
            fileChooser.setInitialDirectory(initialFile.getParentFile());
            String fileName = initialFile.getName();
            // Remove extension from file name
            int pos = fileName.lastIndexOf(".");
            if(0 < pos && pos < (fileName.length() - 1)){
                fileName = fileName.substring(0, pos);
            }
            fileChooser.setInitialFileName(fileName + " config.xml");
        }

        File saveFile = fileChooser.showSaveDialog(stage);
        if(saveFile != null){
            boolean success = configModel.saveToConfigFile(saveFile);
            if(success){
                showNotification("Saved " + saveFile.getName(), NotificationType.SUCCESS);
            }
            else{
                showNotification("Error: Failed to save config file.", NotificationType.DANGER);
            }
        }
    }

    @FXML
    private void onLoad(ActionEvent event){
        Button source = (Button) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Config");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML Document (*.xml)", "*.xml"));
        File initialFile = new File(pathToModel.getValue());
        if(initialFile.exists()){
            fileChooser.setInitialDirectory(initialFile.getParentFile());
        }

        File configFile = fileChooser.showOpenDialog(stage);
        if(configFile != null){
            boolean success = configModel.loadConfigFile(configFile);
            if(success){
                showNotification("Config loaded!", NotificationType.SUCCESS);
                // Also update the view properties for directory/file choosers
                pathToLlamacppFolder.set(configModel.getPathToLlamacppFolder());
                pathToModel.set(configModel.getPathToModel());
            }
            else{
                showNotification("Error: Failed to load " + configFile.getName(), NotificationType.DANGER);
            }
        }
    }

    @FXML
    private void onOpenWebUi(ActionEvent event){
        String url = "http://localhost:" + configModel.getPort();
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException e) {
                showNotification("Error: " + e.getMessage(), NotificationType.DANGER);
            } catch (URISyntaxException e) {
                showNotification("Error: " + e.getMessage(), NotificationType.DANGER);
            }
        }
        else{
            showNotification("This feature is not supported on your platform.", NotificationType.DANGER);
        }
    }

    private void showNotification(String msg, NotificationType notificationType){
        Notification popup = new Notification(msg);
        popup.getStyleClass().add(Styles.ELEVATED_1);
        if(notificationType == NotificationType.NORMAL){
            popup.getStyleClass().add(Styles.ACCENT);
        }
        else if(notificationType == NotificationType.SUCCESS){
            popup.getStyleClass().add(Styles.SUCCESS);
        }
        else if(notificationType == NotificationType.WARNING){
            popup.getStyleClass().add(Styles.WARNING);
        }
        else if(notificationType == NotificationType.DANGER){
            popup.getStyleClass().add(Styles.DANGER);
        }
        popup.setPrefHeight(Region.USE_PREF_SIZE);
        popup.setMaxHeight(Region.USE_PREF_SIZE);
        popup.translateYProperty().set(-100);

        popup.setOnClose(e -> {
            var out = Animations.slideOutUp(popup, Duration.millis(250));
            out.setOnFinished(f -> stackPane.getChildren().remove(popup));
            out.playFromStart();
        });

        StackPane.setAlignment(popup, Pos.TOP_RIGHT);
        StackPane.setMargin(popup, new Insets(10, 10, 0, 0));
        stackPane.getChildren().add(popup);
        popup.widthProperty().addListener(((observable, oldValue, newValue) -> {
            var in = Animations.slideInDown(popup, Duration.millis(250));
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
            showNotification("Error: Failed to find llama-server.exe\nCheck the llama.cpp path.", NotificationType.DANGER);
            return;
        }
        File model = new File(pathToModelField.getText());
        if(!model.exists() || model.isDirectory()){
            showNotification("Error: Cannot find model file: " + pathToModelField.getText(), NotificationType.DANGER);
            return;
        }
        builder.directory(llamacppDir);
        Process process = null;
        try {
            process = builder.start();

            int exitCode = process.waitFor();
            if(exitCode != 0){
                showNotification("Error: Process ended with exit code " + exitCode, NotificationType.DANGER);
            }
        } catch (IOException e) {
            showNotification("Error: " + e.getMessage(), NotificationType.DANGER);
        } catch (InterruptedException e) {
            showNotification("Error: " + e.getMessage(), NotificationType.DANGER);
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