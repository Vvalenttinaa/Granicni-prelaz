module com.example.pj2_2023 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.example.pj2_2023 to javafx.fxml;
    exports com.example.pj2_2023;
}