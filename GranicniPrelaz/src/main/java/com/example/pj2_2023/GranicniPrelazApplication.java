package com.example.pj2_2023;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.*;

public class GranicniPrelazApplication extends Application {
    public static Handler handler;
    public static Logger logger;

    static {
        try {
            handler = new FileHandler("GranicniPrelazLogger.log", true);
            logger = Logger.getLogger(GranicniPrelazApplication.class.getName());
            logger.addHandler(handler);
            //   handler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
      public void start(Stage stage) throws IOException {
          FXMLLoader fxmlLoader = new FXMLLoader(GranicniPrelazApplication.class.getResource("simulacija.fxml"));
          Scene scene = new Scene(fxmlLoader.load(), 740, 740);
          stage.setTitle("GRANICNI PRELAZ");
          stage.setScene(scene);
          stage.show();
      }
      public static void main(String[] args) {
          launch();
      }
}