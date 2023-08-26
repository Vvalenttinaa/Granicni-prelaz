package com.example.pj2_2023;

import com.example.pj2_2023.models.vozila.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.net.*;
import java.util.*;

public class RedController implements Initializable{

    @FXML
    private ScrollPane scrollPane;

    private void generisiSkrol()
    {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        for (Vozilo v:Simulacija.noviRed) {
            if (v.getPozicija() > 4) {
                Image image = new Image(v.getSlika().toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(20);
                imageView.setFitHeight(20);
                Label label = new Label("Vozilo " + v.getIdVozilo() + " , Pozicija:" + v.getPozicija());
                vbox.getChildren().addAll(imageView, label);
            }
        }
        scrollPane.setContent(vbox);
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generisiSkrol();
    }
}
