package com.example.pj2_2023;

import com.example.pj2_2023.models.vozila.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.net.*;
import java.util.*;

public class OpisVozilaController implements Initializable {
    private static int id;

    @FXML
    private Label idLabel;

    @FXML
    private VBox vbox;

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        OpisVozilaController.id = id;
    }

    public void generisiSliku(Vozilo v) {
       // VBox vbox = new VBox();
        vbox.setSpacing(10);
        Image image = new Image(v.getSlika().toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        Label label = new Label("Vozilo " + v.getIdVozilo() + " , Pozicija:" + v.getPozicija());
        vbox.getChildren().addAll(imageView, label);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Vozilo v : Simulacija.podaci) {
            if (v.getIdVozilo() == this.id) {
                generisiSliku(v);
                idLabel.setWrapText(true);
                idLabel.setText(String.valueOf(v));
            }
        }
    }
}
