package com.example.pj2_2023;

import com.example.pj2_2023.models.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.net.*;
import java.util.*;

public class IncidentiController implements Initializable {

    @FXML
    private ScrollPane idIncidenti;

    @FXML
    private ScrollPane idSankcionisani;

    @FXML
    private ScrollPane idNisuPresla;

    @FXML
    private ScrollPane idPresla;

    private void generisiSkrolSankcionisani()
    {
        List<Putnik>putnici=FileUtils.deserijalizuj();
 //       List<Putnik>putnici=new ArrayList<>();
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        for (Putnik p:putnici) {
                Label label = new Label(p.toString());
                vbox.getChildren().add( label);
        }
        idSankcionisani.setContent(vbox);
    }

    private void generisiSkrolEvidencijaCarine()
    {
        List<String> evidencija=FileUtils.readFromFile(Simulacija.evidencijaCarinskogFile);
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        for (String line:evidencija) {
            Label label = new Label(line);
            vbox.getChildren().add( label);
        }
        idIncidenti.setContent(vbox);
    }

    private void generisiSkrolNisuPresla()
    {
        List<String> evidencija=FileUtils.readFromFile(Simulacija.vozilaKojaNisuPreslaFile);
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        for (String line:evidencija) {
            Label label = new Label(line);
            vbox.getChildren().add( label);
        }
        idNisuPresla.setContent(vbox);
    }

    private void generisiSkrolPresla()
    {
        List<String> evidencija=FileUtils.readFromFile(Simulacija.vozilaKojaSuPreslaFile);
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        for (String line:evidencija) {
            Label label = new Label(line);
            vbox.getChildren().add( label);
        }
        idPresla.setContent(vbox);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        generisiSkrolSankcionisani();
        generisiSkrolEvidencijaCarine();
        generisiSkrolPresla();
        generisiSkrolNisuPresla();
    }
}
