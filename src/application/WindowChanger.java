package application;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class WindowChanger {
	
	private Stage stage;
	
	public WindowChanger() {

		
	}
	
	public void changeWindow(String url) {
		this.changeWindow(url, (loader) ->{
			
		});
	}
	
	public void changeWindow(String url, String title) {
		this.changeWindow(url, (loader) -> {
			
		});
		this.stage.setTitle(title);
	}
	
	
	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void changeWindow(String url, Consumer<FXMLLoader> consumer) {
		FXMLLoader loader = new FXMLLoader();
		System.out.println("alt: " + Thread.activeCount());
		if(consumer != null)
			consumer.accept(loader);
		loader.setLocation(getClass().getResource(url));
		Parent parent = null;
		try{

			parent = loader.load();
			Scene scene = new Scene(parent);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			this.stage.close();
			this.stage.setScene(scene);
			this.stage.setResizable(false);
			Thread.sleep(200);
			this.stage.show();
			System.out.println("neu: " + Thread.activeCount());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showGroupInfo(String indent, ThreadGroup group) {
		Thread[] threads = new Thread[group.activeCount()];
		group.enumerate(threads, false);
		System.out.println(group);

		for (Thread t : threads)
			if (t != null)
				System.out.printf("%s%s -> %s ist %sDaemon-Thread%n", indent, group.getName(), t,
						t.isDaemon() ? "" : "kein ");

		ThreadGroup[] activeGroup = new ThreadGroup[group.activeGroupCount()];
		group.enumerate(activeGroup, false);

		for (ThreadGroup g : activeGroup)
			showGroupInfo(indent + indent, g);
	}
	
	
	
	public WindowChanger(Stage stage) {
		this.stage = stage;
	}
	
	
}
