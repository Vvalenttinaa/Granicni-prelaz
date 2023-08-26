package com.example.pj2_2023;

import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.Terminal.*;
import com.example.pj2_2023.models.vozila.*;
import javafx.application.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.fxml.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import static com.example.pj2_2023.Simulacija.aktivnaVozila;

public class SimulacijaController implements Initializable {

    private static long startVrijeme;
    private static long ostatak;

    private Object lockTrajanje = new Object();

    private Simulacija s;

    private static final Pane[] redGui = new Pane[5];

    @FXML
    private GridPane idRedV;

    @FXML
    private Pane idPol1;

    @FXML
    private Pane idPol2;

    @FXML
    private Pane idPol3;

    @FXML
    private Pane idCar1;

    @FXML
    private Pane idCar2;

    @FXML
    private Label idLabelCarinski1;

    @FXML
    private Label idLabelCarinski2;

    @FXML
    private Label idLabelPolicijski1;

    @FXML
    private Label idLabelPolicijski2;

    @FXML
    private Label idLabelPolicijski3;

    @FXML
    private Button idIncidenti;

    @FXML
    private Button idRed;

    final static Label trajanje = new Label();

    @FXML
    private Label idTrajanje;

    @FXML
    private TextField idID;

    @FXML
    private Label idSpecijalniDogadjaji;

    public static void ispisiTrajanjeNaGui() {
        Platform.runLater(() -> {
            trajanje.setText(String.valueOf((int) (System.currentTimeMillis() - startVrijeme + ostatak) / 1000) + "s");
        });
    }

    public static void ispisiSpecDogadjajNaGui(String text) {
        Platform.runLater(() -> {
            specijalniDogadjaji.setText(text);
        });
    }

    private void ispisTrajanja() {
        Thread ispis = new Thread(new Runnable() {
            @Override
            public void run() {
                while (aktivnaVozila()) {
                    while (!Simulacija.pauzaSimulacije) {
                        ispisiTrajanjeNaGui();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                        if(!aktivnaVozila())
                        {
                            boolean zauzet=false;
                            for(Terminal t:GranicniPrelaz.getTerminali())
                            {
                                if(t.vozilo!=null)
                                    zauzet=true;
                            }
                            if(!zauzet)
                            {
                                System.out.println("\n\nKraj");
                                break;
                            }

                        }
                    }
                    if (Simulacija.pauzaSimulacije) {
                        synchronized (lockTrajanje) {
                            try {
                                lockTrajanje.wait();
                            } catch (InterruptedException e) {
                                GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                            }
                        }
                    }
                }
                System.out.println("kraj ispisa vremena");
            }
        });
        ispis.start();
    }

    public final static Label opisPol1 = new Label();
    public final static Label opisPol2 = new Label();
    public final static Label opisPol3 = new Label();
    public final static Label opisCar1 = new Label();
    public final static Label opisCar2 = new Label();

    public static void ispisiOpisTerminalaNaGui(String opis, Label terminal) {
        Platform.runLater(() -> {
            terminal.setText(opis);
        });
    }

    private final static Label specijalniDogadjaji = new Label();

    public static void ispisiSpecijalniDogadjajNaGui(String opis) {
        Platform.runLater(() -> {
            specijalniDogadjaji.setText(opis);
        });
    }

    public static void addToGui(int k, File file, int id) {
        Image image1 = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image1);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);

        Label l = new Label();
        l.setText(String.valueOf(id));
        Platform.runLater(() -> {
            redGui[k].getChildren().addAll(imageView, l);
        });
    }

    public static void removeImageFromGui(int k) {
        ImageView iw = new ImageView();
        iw.setFitWidth(40);
        iw.setFitHeight(40);
        Platform.runLater(() -> {
             redGui[k].getChildren().removeIf(node -> node instanceof ImageView || node instanceof Label);
        });
    }

    private void generisiRedGui() {
        int polje = 1;

        for (int i = 0; i < 5; i++) {
            Label labela = new Label(String.valueOf(polje));
            labela.setTextFill(Color.BLACK);
            labela.setAlignment(Pos.CENTER);
            labela.setStyle("-fx-font-size: 1.7em;" + "-fx-border-color: gray");
            redGui[i] = new Pane();
            redGui[i].setStyle("-fx-border-width: 8px 8px" + "align-content: center;");
            redGui[i].getChildren().add(labela);
            polje++;
        }

        idRedV.getRowConstraints().removeAll();
        final int brojKolona = 1;
        final int brojVrsta = 5;

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setFillWidth(true);
        columnConstraints.setHgrow(Priority.ALWAYS);
        columnConstraints.setPercentWidth(100d / brojKolona);

        for (int i = 0; i < brojKolona; i++) {
            idRedV.getColumnConstraints().add(columnConstraints);
        }

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setFillHeight(true);
        rowConstraints.setVgrow(Priority.ALWAYS);
        rowConstraints.setPercentHeight(100d / brojVrsta);

        for (int i = 0; i < brojVrsta; i++) {
            idRedV.getRowConstraints().add(rowConstraints);
        }
        for (int i = 0; i < 5; i++) {
            idRedV.add(redGui[i], 0, i);
        }
    }

    final static Pane idPol11 = new Pane();
    final static Pane idPol22 = new Pane();
    final static Pane idPol33 = new Pane();

    final static Pane idCar11 = new Pane();
    final static Pane idCar22 = new Pane();

    private void generisiTerminaleGui() {
        idPol1.getChildren().add(idPol11);
        idPol2.getChildren().add(idPol22);
        idPol3.getChildren().add(idPol33);

        idCar1.getChildren().add(idCar11);
        idCar2.getChildren().add(idCar22);
    }

    public static void nacrtajVoziloNaTerminal(File vozilo, int idVozilo, int idTerminal) {
        Platform.runLater(() -> {
            try {
                Label l = new Label(String.valueOf(idVozilo));
                l.setTextFill(Color.BLACK);
                l.setStyle("-fx-font-size: 1.7em;");
                Image image1 = new Image(new FileInputStream(vozilo));
                ImageView imageView = new ImageView(image1);
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                Platform.runLater(() ->
                {
                    if (idTerminal == 1)
                        idPol11.getChildren().addAll(imageView, l);
                    else if (idTerminal == 2)
                        idPol22.getChildren().addAll(imageView, l);
                    else if (idTerminal == 3)
                        idPol33.getChildren().addAll(imageView, l);
                    else if (idTerminal == 4)
                        idCar22.getChildren().addAll(imageView, l);
                    else if (idTerminal == 5)
                        idCar11.getChildren().addAll(imageView, l);
                });
            } catch (FileNotFoundException e) {
                GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        });
    }

    public synchronized static void obrisiVoziloNaTerminalu(int idTerminal) {
        Platform.runLater(() ->
        {
            if (idTerminal == 1)
                idPol11.getChildren().clear();
            else if (idTerminal == 2)
                idPol22.getChildren().clear();
            else if (idTerminal == 3)
                idPol33.getChildren().clear();
            else if (idTerminal == 4)
                idCar22.getChildren().clear();
            else if (idTerminal == 5)
                idCar11.getChildren().clear();
        });
    }

    private void otvoriOpisVozila(int id) throws IOException {
        OpisVozilaController.setId(id);
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("opis-vozila.fxml"));
        primaryStage.setTitle("DETALJI");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    void onClickRed(ActionEvent event) throws IOException {
        //   RedController.setRed(Simulacija.red);
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("red.fxml"));
        primaryStage.setTitle("RED");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    void onClickedIncidenti(ActionEvent event) throws IOException {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("incidenti.fxml"));
        primaryStage.setTitle("INCIDENTI");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    @FXML
    void onClickStart(ActionEvent event) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                s.pokreniSimulaciju();
            }
        });
        t.start();
    }

    @FXML
    void onClickPokreniZaustavi(ActionEvent event) {
        if (!Simulacija.pauzaSimulacije) {
            ostatak+=System.currentTimeMillis()-startVrijeme;
            Simulacija.pauzaSimulacije=true;
            for (Terminal t : GranicniPrelaz.getTerminali()) {
                {
                    if (!t.isPauzaSimulacija()) {
                        t.setPauzaSimulacija(true);
                    }
                }
            }
        } else {
            Simulacija.pauzaSimulacije=false;
            startVrijeme=System.currentTimeMillis();
            synchronized (lockTrajanje) {
                lockTrajanje.notify();
            }
            for (Terminal t : GranicniPrelaz.getTerminali()) {
                if (t.isPauzaSimulacija()) {
                    t.setPauzaSimulacija(false);
                    System.out.println(t.getIdentifikator() + " " +  t.isPauzaSimulacija());
                    synchronized (t.terminalLockPauzaSimulacije) {
                        t.terminalLockPauzaSimulacije.notify();
                    }
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        generisiRedGui();
        generisiTerminaleGui();
        s = new Simulacija();
       /* try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
        }

        */
        startVrijeme = System.currentTimeMillis();
        ispisTrajanja();

        for (Vozilo v:Simulacija.noviRed) {
            if(v.getPozicija()<5) {
                addToGui(v.getPozicija(), v.getSlika(), v.getIdVozilo());
            }
        }

        dodajListenera(opisPol1, idLabelPolicijski1);
        dodajListenera(opisPol2, idLabelPolicijski2);
        dodajListenera(opisPol3, idLabelPolicijski3);
        dodajListenera(opisCar1, idLabelCarinski1);
        dodajListenera(opisCar2, idLabelCarinski2);

        dodajListenera(trajanje, idTrajanje);
        dodajListenera(specijalniDogadjaji, idSpecijalniDogadjaji);
        dodajListeneraIIzvrsi(idID);
    }

    private void dodajListenera(ImageView imageView, ImageView idImageView) {
        imageView.imageProperty().addListener(new ChangeListener<Image>() {
            @Override
            public void changed(ObservableValue<? extends Image> observableValue, Image image, Image t1) {
                System.out.println(imageView);
                idImageView.setImage(imageView.getImage());
            }
        });
    }

    private void dodajListenera(Label labell, Label idLabel) {
        labell.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                idLabel.setText(labell.getText());
            }
        });
    }

    private void dodajListeneraIIzvrsi(TextField idTextField) {
        idTextField.setOnAction(event -> {
            String input = idTextField.getText();
            System.out.println("Received input: " + input);
            try {
                otvoriOpisVozila(Integer.valueOf(input));
            } catch (IOException e) {
                e.printStackTrace();
            }
            idTextField.clear();
        });
    }
}

