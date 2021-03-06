package controller;

import java.net.URL;
import java.util.ResourceBundle;
import application.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import player.Player;
import pokemon.Pokemon;
import views.MapView;
import views.PokemonView;

public class ViewerController implements Initializable {

	private MapView mapView;

	@FXML
	private AnchorPane anchor;
	@FXML
	private AnchorPane anchor2;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		if (Main.gameData.getMap() != null)
			mapView.setMap(Main.gameData.getMap());
		if (Main.client != null) {
			Main.client.setOnReceive(each -> {
				Platform.runLater(() -> {
					mapView = new MapView(each.getFields());
					mapView.setLayoutX(0);
					mapView.setLayoutY(32);
					mapView.update();
					anchor.getChildren().add(mapView);
					try {
						Player player = each.getPlayer();
						anchor2.getChildren().clear();
						for (int i = 0; i < player.getPokemons().size(); i++) {
							Pokemon pokemon = player.getPokemons().get(i);
							PokemonView pView = new PokemonView(pokemon, true);
							pView.setLayoutX(45);
							pView.setLayoutY(10 + i * 95);
							anchor2.getChildren().add(pView);
						}
					}catch (Exception e) {
						e.printStackTrace();
					}


				});
			});
		}
	}

}
