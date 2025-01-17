# llama-fx
A simple GUI wrapper for llama.cpp built with JavaFX.

![llama-fx_screenshot](https://github.com/user-attachments/assets/1b0a8696-bfdc-4673-8fd1-d1ad8bfc934f)

## About
This project is a simple GUI wrapper for [llama.cpp](https://github.com/ggerganov/llama.cpp). Currently it supports the `llama-server` tool only.

## Installation/How to use
After downloading or building the application, simply run the binary file `llama-fx`, no installation needed.

Set the path to your llama.cpp folder (you should see `llama-server` in this folder). Then you can set parameters for the llama server and pick a GGUF model.

Once your configuration is ready, click Launch to start a llama-server instance.

You can click Open Web UI to open a new tab in your browser to the llama server's web UI.

You can also save configurations and load them, which is helpful for switching between different models and settings, or for launching multiple instances.

## Getting Started
This project was built with the following:
- Java 21
- IntelliJ
- Maven
- JavaFX

### Setup
Clone the repository, and then open the project in IntelliJ as a Maven project.

You can run the application either with `mvn javafx:run` or by running the main class `MainApplication.java` normally.

### Building
Builds are created using jlink and jpackage Maven plugins. You can see the result in `/target/dist` where you'll find the binary file `llama-fx`, and `/app` and `/runtime` folders next to it.

Run one of the Maven commands below depending on your platform:

#### Windows
```
mvn clean javafx:jlink jpackage:jpackage@win
```

#### Linux (untested)
```
mvn clean javafx:jlink jpackage:jpackage@linux
```

#### Mac (untested)
```
mvn clean javafx:jlink jpackage:jpackage@mac
```

## Notes
- llama-fx does not come with llama.cpp included. You have to download llama.cpp yourself and point llama-fx to it.
- llama-fx does not come with any models either. You pick a GGUF model you already have for llama-fx to pass to llama.cpp when launching.
