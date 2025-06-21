package com.svntax.llamafx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigModel {

    public static final int DEFAULT_PORT = 5001;
    public static final int DEFAULT_GPU_LAYERS = 0;
    public static final int DEFAULT_THREADS_COUNT = 1;
    public static final int DEFAULT_CONTEXT_SIZE = 4096;

    private final StringProperty pathToLlamacppFolder = new SimpleStringProperty("");
    private final StringProperty pathToModel = new SimpleStringProperty("");
    private final IntegerProperty port = new SimpleIntegerProperty(DEFAULT_PORT);
    private final IntegerProperty gpuLayers = new SimpleIntegerProperty(DEFAULT_GPU_LAYERS);
    private final IntegerProperty threadsCount = new SimpleIntegerProperty(DEFAULT_THREADS_COUNT);
    private final IntegerProperty contextSize = new SimpleIntegerProperty(DEFAULT_CONTEXT_SIZE);

    /**
     * Saves the config model's properties to a file.
     * @param file The file to save to
     * @return true if the file was saved successfully, false otherwise
     */
    public boolean saveToConfigFile(File file){
        Properties props = new Properties();

        props.setProperty("PATH_TO_LLAMACPP_FOLDER", getPathToLlamacppFolder());
        props.setProperty("PATH_TO_MODEL", getPathToModel());
        props.setProperty("PORT", Integer.toString(getPort()));
        props.setProperty("GPU_LAYERS", Integer.toString(getGpuLayers()));
        props.setProperty("THREADS_COUNT", Integer.toString(getThreadsCount()));
        props.setProperty("CONTEXT_SIZE", Integer.toString(getContextSize()));

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            props.storeToXML(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Loads data from a config file and sets the config model's properties to them.
     * @param file The file to load
     * @return true if the file was loaded successfully, false otherwise
     */
    public boolean loadConfigFile(File file){
        Properties props = new Properties();
        String[] keys = {"PATH_TO_LLAMACPP_FOLDER", "PATH_TO_MODEL",
                "PORT", "GPU_LAYERS", "THREADS_COUNT", "CONTEXT_SIZE"
        };
        try (FileInputStream inputStream = new FileInputStream(file)) {
            props.loadFromXML(inputStream);
            for(String key : keys){
                if(!props.containsKey(key)){
                    return false;
                }
                if(key.equalsIgnoreCase("PATH_TO_LLAMACPP_FOLDER")){
                    setPathToLlamacppFolder(props.getProperty(key));
                }
                else if(key.equalsIgnoreCase("PATH_TO_MODEL")){
                    setPathToModel(props.getProperty(key));
                }
                else if(key.equalsIgnoreCase("PORT")){
                    setPort(Integer.parseInt(props.getProperty(key)));
                }
                else if(key.equalsIgnoreCase("GPU_LAYERS")){
                    setGpuLayers(Integer.parseInt(props.getProperty(key)));
                }
                else if(key.equalsIgnoreCase("THREADS_COUNT")){
                    setThreadsCount(Integer.parseInt(props.getProperty(key)));
                }
                else if(key.equalsIgnoreCase("CONTEXT_SIZE")){
                    setContextSize(Integer.parseInt(props.getProperty(key)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String getPathToLlamacppFolder(){
        return pathToLlamacppFolder.get();
    }

    public StringProperty pathToLlamacppFolderProperty(){
        return pathToLlamacppFolder;
    }

    public void setPathToLlamacppFolder(String path){
        pathToLlamacppFolder.setValue(path);
    }

    public String getPathToModel(){
        return pathToModel.get();
    }

    public StringProperty pathToModelProperty(){
        return pathToModel;
    }

    public void setPathToModel(String path){
        pathToModel.set(path);
    }

    public int getPort(){
        return port.get();
    }

    public IntegerProperty portProperty(){
        return port;
    }

    public void setPort(int port){
        this.port.set(port);
    }

    public int getGpuLayers(){
        return gpuLayers.get();
    }

    public IntegerProperty gpuLayersProperty(){
        return gpuLayers;
    }

    public void setGpuLayers(int gpuLayers){
        this.gpuLayers.set(gpuLayers);
    }

    public int getThreadsCount(){
        return threadsCount.get();
    }

    public IntegerProperty threadsCountProperty(){
        return threadsCount;
    }

    public void setThreadsCount(int threads){
        threadsCount.set(threads);
    }

    public int getContextSize(){
        return contextSize.get();
    }

    public IntegerProperty contextSizeProperty(){
        return contextSize;
    }

    public void setContextSize(int contextSize){
        this.contextSize.set(contextSize);
    }
}
