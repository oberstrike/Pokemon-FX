package logic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Vector;
import java.util.stream.Collectors;

import application.Main;
import controller.FightGuiController;
import field.Field;
import field.FieldType;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import player.Player;
import player.PlayerType;
import pokemon.Pokemon;
import views.MapView;
import views.PokemonView;
import xml.GameData;

/* Noch fehlt:

	- NN	evtl Level (automatisches Laden der n�chsten Karte bei Besiegen eines Trainers o.�.
	- CHECK Bilder der Pokemon, sowohl rechts in der Liste als auch beim Kampf
	- CHECK	Pl�tze tauschen (erstes Pokemon der Liste durch ein anderes ersetzen)
	- CHECK	Motivation abh�ngig vom Ausgang der letzten 5-10 K�mpfe setzen
	- CHECK XP-Balken zeigt noch keinen Fortschritt CHECK
	- CHECK	Klick auf Beenden beendet Spiel
	- CHECK Spielerposition bei Kartenwechsel anpassen
	- CHECK	mehrere �berg�nge auf einer Map
	- CHECK	Bild bei Entwicklung anpassen
	- CHECK	Maps bauen
*/

public class GameLogic extends Thread {
	private MapView mapView;
	private Player player;
	private long lastMovementTime = 0;
	private AnchorPane anchor2;
	public boolean isRunning = true;
	
	public GameLogic(MapView mapView, AnchorPane anchor2, GameData gameData) {
		this.mapView = mapView;
		this.setDaemon(true);
		if (gameData.getPlayer() == null) {
			this.player = new Player();
			List<Field> freeFields = this.mapView.getFields().stream().filter(each -> each.getType().equals(FieldType.HOHESGRASS))
					.collect(Collectors.toList());

			Field field = freeFields.get(new Random().nextInt(freeFields.size()));			
			List<Integer> pokedexList = new ArrayList<>();
			for (int i = 0; i <= 150; i++) {
				pokedexList.add(0);
			}
			player.setType(PlayerType.STRAIGHT);
			player.setField(field);
			field.setEntity(player);
			Main.gameData.setPlayer(player);
		} else {
			player = gameData.getPlayer();
		}
		mapView.update();
		this.anchor2 = anchor2;
	}

	public void update() {
		Platform.runLater(() -> {

			this.mapView.update();

			long countOfPokeViews = anchor2.getChildren().stream()
					.filter(each -> each.getClass().equals(PokemonView.class)).count();

			if (countOfPokeViews < player.getPokemons().size()) {
				//Falls ein neues Pokemon gefangen wurde muss das Gui aktualisiert werden
				
				for (int i = (int) countOfPokeViews; i < player.getPokemons().size(); i++) {
					Pokemon myPokemon = player.getPokemons().get(i);
					PokemonView pv = new PokemonView(myPokemon, i > 0 ? false : true);
					pv.setLayoutX(45);
					pv.setLayoutY(10 + i * 95);
					if(pv.getUpButton()!=null) {
						pv.getUpButton().setOnAction(event -> {

							// Pokemon Swap Mechanic
							int oldIndex = player.getPokemons().indexOf(pv.getPokemon());
							int newIndex = (oldIndex == 0 ? player.getPokemons().size() - 1 : oldIndex - 1);

							Pokemon oldPokemon = player.getPokemons().get(oldIndex);
							Pokemon newPokemon = player.getPokemons().get(newIndex);

							PokemonView oldView = (PokemonView) anchor2.getChildren().get(oldIndex);
							PokemonView newView = (PokemonView) anchor2.getChildren().get(newIndex);

							player.getPokemons().set(newIndex, oldPokemon);
							player.getPokemons().set(oldIndex, newPokemon);

							oldView.setPokemon(newPokemon);
							newView.setPokemon(oldPokemon);

						});
					}
				
					anchor2.getChildren().add(pv);
				}
			} else {
				for (int i = 0; i < player.getPokemons().size(); i++) {
					((PokemonView) anchor2.getChildren().get(i)).update();
				}
			}
			mapView.requestFocus();
		});
	}

	@Override
	public void run() {
		while (isRunning) {
			update();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.interrupt();
	}

	private void fightMenu() {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Aktionsfenster");
		List<Pokemon> listOfPokemons = new ArrayList<>();

		if (player.getPokemons().size() > 0) {
			listOfPokemons = mapView.getMap().getPokemons();
			listOfPokemons.forEach(each -> each
					.setLevel(1 + new Random().nextInt(player.getAverageLevel()) + player.getPokemons().size()));
		} else {
			listOfPokemons.add(Main.xmlControll.getPokemonByName("Schiggy"));
			listOfPokemons.add(Main.xmlControll.getPokemonByName("Bisasam"));
			listOfPokemons.add(Main.xmlControll.getPokemonByName("Glumanda"));
			listOfPokemons.forEach(each -> each.setLevel(5));
		}

		List<Double> chances = listOfPokemons.stream().map((each) -> each.getSpawn()).collect(Collectors.toList());
		double sumChances = chances.stream().reduce(0.0, (a, b) -> a + b);
		double randomValue = sumChances * new Random().nextDouble();

		Pokemon spawnedPokemon = null;
		for (Pokemon currentPokemon : listOfPokemons) {
			if ((sumChances - currentPokemon.getSpawn()) < randomValue) {
				spawnedPokemon = new Pokemon(currentPokemon);
				break;
			} else {
				sumChances -= currentPokemon.getSpawn();
			}
		}

		if (player.getPokemons().size() > 0) {
			FightGuiController controller = FightGuiController.create(player.getPokemons(),
					new Vector<>(Arrays.asList(spawnedPokemon)), false);
			Platform.runLater(() -> {
				Main.changer.changeWindow("/guis/FightGui.fxml", (loader) -> {
					loader.setController(controller);
				});
			});
			isRunning = false;
		} else {
			// Starter Pokemon fangen.
			alert.setContentText("Ein wildes " + spawnedPokemon.getName() + " erscheint willst du es behalten?");
			ButtonType yes = new ButtonType("Ja");
			ButtonType no = new ButtonType("Nein");
			alert.getButtonTypes().setAll(yes, no);
			Optional<ButtonType> btn = alert.showAndWait();
			if (btn.isPresent()) {
				ButtonType btnValue = btn.get();
				System.out.println(btnValue);
				if (btnValue.equals(yes)) {
					spawnedPokemon.setXp(0);
					player.getPokemons().add(spawnedPokemon);
				}
			}

		}


	}

	public void moveEvent(String keyName) {
		double newX = player.getField().getX();
		double newY = player.getField().getY();
		Optional<Field> newField = null;

		switch (keyName) {
		case "W":
			newY -= 30;
			player.setType(PlayerType.STRAIGHT);
			break;
		case "D":
			newX += 30;
			player.setType(PlayerType.RIGHT);
			break;
		case "S":
			newY += 30;
			player.setType(PlayerType.BACK);
			break;
		case "A":
			newX -= 30;
			player.setType(PlayerType.LEFT);
			break;
		case "Space":
			Optional<Field> oField = null;
			if (player.getImage() != null) {
				PlayerType playerType = player.getType();
				if (playerType.equals(PlayerType.LEFT))
					oField = mapView.getMap().leftField(player.getField());
				else if (playerType.equals(PlayerType.RIGHT))
					oField = mapView.getMap().rightField(player.getField());
				else if (playerType.equals(PlayerType.STRAIGHT))
					oField = mapView.getMap().upField(player.getField());
				else if (playerType.equals(PlayerType.BACK))
					oField = mapView.getMap().bottomField(player.getField());
				if (oField.isPresent()) {
					Field field = oField.get();
					if (field.getEntity() != null) {
						if (field.getEntity().getClass().equals(Trainer.class))
							fightAgainstTrainer((Trainer) field.getEntity());
					}
				}
			}
			break;
		default:
			break;
		}
		//Effective Final 
		double x = newX;
		double y = newY;

		newField = mapView.getFields().stream().filter(each -> each.getX() == x && each.getY() == y).findFirst();

		if (newField.isPresent()) {
			Field playerField = player.getField();
			if (!newField.get().equals(player.getField())) {
				if (!newField.get().isBlocked()) {
					int difference = player.getField().getType().equals(FieldType.TIEFERSAND) ? 300 : 110;
					if (lastMovementTime == 0 || System.currentTimeMillis() - lastMovementTime > difference) {
						lastMovementTime = System.currentTimeMillis();
						Field newF = newField.get();
						for (Pokemon mon : player.getPokemons()) {
							if (mon.calculateMaxHp() > mon.getHp()) {
								mon.setHp(mon.getHp() + 1);
							}
						}
						if (newF.getType().equals(FieldType.UEBERGANG)) {
							Map map = Main.xmlControll.getMap(new File("ressources/" + newF.getNextMap()));
							mapView.setMap(map);
							Main.gameData.setMap(map);
							if (x == 570) {
								newX = 0;
							} else if (x == 0) {
								newX = 570;
							} else if (y == 480) {
								newY = 0;
							} else if (y == 0) {
								newY = 480;
							}
							newF.setX(newX);
							newF.setY(newY);

							// Effective Final
							double xx = newX;
							double yy = newY;
							newField = mapView.getFields().stream()
									.filter(each -> each.getX() == xx && each.getY() == yy).findFirst();
						}
						playerField.setEntity(null);
						newF.setEntity(player);
						player.setField(newField.get());
						Platform.runLater(() -> mapView.update());
						if (newF.getType().equals(FieldType.HOHESGRASS)
								|| newF.getType().equals(FieldType.TIEFERSAND)) {
							int randDig = new Random().nextInt(100);
							if (randDig < 13) {
								fightMenu();
							}
						}

						if(Main.server != null) {
							if(Main.server.getListOfHandler().size()>0) {
								Main.server.sendAll(Main.xmlControll.toXml(Main.gameData.getMap()));
							}
						}
						mapView.update();
					}

				}

			}
		}

	}

	private void fightAgainstTrainer(Trainer entity) {
		if (entity.getPokemons() != null) {
			if (entity.getPokemons().size() > 0) {
				entity.getPokemons().forEach(each -> each.setHp(each.calculateMaxHp()));
				FightGuiController controller = FightGuiController.create(player.getPokemons(), entity.getPokemons(),
						true);

				Platform.runLater(() -> {
					Main.changer.changeWindow("/guis/FightGui.fxml", (loader) -> loader.setController(controller));
				});
				isRunning = false;
			}
		}

	}

}
