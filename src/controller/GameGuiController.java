package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import logic.GameLogic;
import logic.PokemonServer;
import views.MapView;

public class GameGuiController implements Initializable {

	private GameLogic logic;
	@FXML
	private MenuItem musicToggle;
	
	
	@FXML
	void back(ActionEvent event) {
		logic.isRunning = false;
		Main.routeMediaPlayer.stop();
		if(Main.server!=null)
			Main.server.shutdown();
		Main.changer.changeWindow("/guis/MenuGui.fxml");
		
	}
	
	@FXML
	void save(ActionEvent event) {
		String gameFileName = "ressources/Spielstand.xml";
		File file = new File(gameFileName);
		System.out.println(file.exists());
		try {
			FileWriter writer = new FileWriter(file);
			Main.xmlControll.saveGameData(Main.gameData, writer);
			System.out.println("Gespeichert");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    @FXML
    void toggleMusic(ActionEvent event) {
    	if(Main.routeMediaPlayer.isMute()) {
    		Main.routeMediaPlayer.setMute(false);
    		musicToggle.setText("Musik ausschalten");
    	} else {
    		Main.routeMediaPlayer.setMute(true);
    		musicToggle.setText("Musik einschalten");
    	}
    }

	@FXML
	private AnchorPane anchor2;

	@FXML
	private AnchorPane anchor;
	
	private void setUpServer() {
		if(Main.server==null) {
			Main.server = new PokemonServer(3333);
			Main.server.start();	
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		Main.routeMediaPlayer.setVolume(0.5);
		Main.routeMediaPlayer.play();
		MapView mapView = null;
		
		if (Main.gameData.getMap() == null) {
			mapView = new MapView();
			Main.gameData.setMap(mapView.getMap());
		} else {
			mapView = new MapView();
			mapView.setMap(Main.gameData.getMap());
		}
		
		if(Main.allowViewer)
			this.setUpServer();
		

		mapView.setPrefWidth(600);
		mapView.setPrefHeight(500);
		mapView.setLayoutY(37);
		anchor.getChildren().add(mapView);

		anchor.setFocusTraversable(false);
		anchor.requestFocus();

		logic = new GameLogic(mapView, anchor2, Main.gameData);
		logic.start();
		mapView.setOnKeyPressed(event -> {
			logic.moveEvent(event.getCode().getName());
		});
		mapView.setFocusTraversable(true);
		mapView.requestFocus();
	}

}
