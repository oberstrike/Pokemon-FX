package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import logic.Field;
import logic.FieldType;

public class CreateGuiController implements Initializable {
	
	XStream stream;
	
    @FXML
    void load(ActionEvent event) {
    	Alert alert = new Alert(AlertType.CONFIRMATION, "Wollen Sie den aktuellen Zustand verwerfen?");
    	alert.showAndWait().ifPresent((button) ->{
    		if(button.getText().equals("OK")) {
    			loadFile(event);
    		}
    	}); 
    	
    }
    
    @FXML
    private TextField name;
	
    @SuppressWarnings("unchecked")
	private void loadFile(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle("Open XML");
    	fileChooser.getExtensionFilters().add(new ExtensionFilter("Xml Files", "*.xml"));
    
    	File selectedFile = fileChooser.showOpenDialog((Stage)((Button)event.getSource()).getScene().getWindow());
    	
    	String name = "";
    	if(selectedFile != null) {
    		System.out.println(selectedFile.getName());
    	}
    	
    	
    	FileReader reader;
    	
    	try {
			reader = new FileReader("save.xml");
			Object object = stream.fromXML(reader);
			fields = (ArrayList<Field>) object;
			reader.close();
			new Alert(AlertType.INFORMATION, "Geladen").show();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
    }
    
	@FXML
	private void save(ActionEvent event) {	
		FileWriter writer;
		if(this.name.getText().length()<1 || this.name.getText().length()>12) {
			new Alert(AlertType.ERROR, "Bitte geben Sie einen Namen ein, der zwischen 4-12 Zeichen besitzt").show();
		}else {
			try {
				writer = new FileWriter("save.xml", false);
				System.out.println("Speichere..");
				stream.toXML(this.fields, writer);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
	}
	
	@FXML
	private Canvas grass;

	@FXML
	private Canvas stein;

	@FXML
	private Canvas hohesGrass;
	@FXML
	private Canvas canvas;

	List<Field> fields;
	GraphicsContext context;
	ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	private void fill() {
		for (Field field : fields) {
			if (field.getType().equals(FieldType.GRASS)) {
				context.setFill(Color.GREEN);
				context.fillRect(field.getX(), field.getY(), 20, 20);
			} else {
				context.setFill(Color.BROWN);
				context.fillRect(field.getX(), field.getY(), 20, 20);
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		stream = new XStream(new StaxDriver());
		stream.addPermission(NoTypePermission.NONE);
		stream.addPermission(NullPermission.NULL);
		stream.addPermission(PrimitiveTypePermission.PRIMITIVES);
		stream.processAnnotations(Field.class);
		stream.allowTypeHierarchy(Collection.class);
		stream.allowTypeHierarchy(Field.class);
		
		GraphicsContext gc = grass.getGraphicsContext2D();
		gc.setFill(Color.GREEN);
		gc.fillRect(0,0,20,20);
		 
		gc = hohesGrass.getGraphicsContext2D();
		gc.setFill(Color.DARKGREEN);
		gc.fillRect(0,0,20,20);
		
		gc = stein.getGraphicsContext2D();
		gc.setFill(Color.BROWN);
		gc.fillRect(0,0,20,20);
		
		fields = new ArrayList<>();
		context = canvas.getGraphicsContext2D();
		for (int i = 0; i < 600; i += 20) {
			for (int j = 0; j < 500; j += 20) {
				fields.add(new Field(i, j, FieldType.GRASS));
			}
		}

		executor.schedule(() -> this.fill(), 200, TimeUnit.MILLISECONDS);

	}

}
