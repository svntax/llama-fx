module com.svntax.llamafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires java.desktop;

    opens com.svntax.llamafx to javafx.fxml;
    exports com.svntax.llamafx;
}