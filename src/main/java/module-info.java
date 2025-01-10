module com.svntax.llamafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.svntax.llamafx to javafx.fxml;
    exports com.svntax.llamafx;
}