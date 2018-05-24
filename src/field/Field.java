package field;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlRootElement;

import com.sun.javafx.geom.Vec2d;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import application.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import logic.Entity;
import logic.Player;
import xml.GameData;

@XStreamAlias("FIELD")
public class Field {

	@Override
	public String toString() {
		return "Field [type=" + type + ", x=" + x + ", y=" + y + ", image=" + image + "]";
	}

	private FieldType type;
	private double x;
	private double y;
	private Entity entity;
	private String nextMap;
	
	@XStreamOmitField
	private Image image;

	public Field() {};

	public Field(double x, double y, FieldType type) {
		this();
		this.setType(type);
		this.setX(x);
		this.setY(y);
		applyImage();
	}
	

	public static Field findFieldNextTo(double x, double y, List<Field> listOfFields) {
    	Vec2d localPoint = new Vec2d(x, y);
    	List<Vec2d> listOfVector = listOfFields.stream().map(Field::toVector).collect(Collectors.toList());	
    	double distance = listOfVector.stream().map(each -> localPoint.distance(each)).sorted().findFirst().get();
    	Vec2d vec = listOfVector.stream().filter(each -> each.distance(localPoint) == distance).findFirst().get(); ;
    	Field field = listOfFields.stream().filter(each -> each.getX() == vec.x-15 && each.getY() == vec.y-15).findFirst().get();
    	return field;
	}
	
	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
		applyImage();
	}

	public Vec2d toVector() {
		return new Vec2d(this.x + 15, this.y + 15);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public boolean isBlocked() {
		return this.getType().isBlocked() || this.getEntity()!=null;

	}
	
	public void applyImage() {
		switch (type) {
		case GRASS:
			setImage(Main.grass);
			break;
		case HOHESGRASS:
			setImage(Main.hohesgrass);
			break;
		case SAND:
			setImage(Main.sand);
			break;
		case TIEFERSAND:
			setImage(Main.tiefersand);
			break;
		case STEIN:
			setImage(Main.stein);
			break;
		case WASSER:
			setImage(Main.wasser);
			break;
		case UEBERGANG:
			setImage(Main.uebergang);
			break;
		case HAUS_1a:
			setImage(Main.haus_1a);
			break;
		case HAUS_1b:
			setImage(Main.haus_1b);
			break;
		case HAUS_1c:
			setImage(Main.haus_1c);
			break;
		case HAUS_1d:
			setImage(Main.haus_1d);
			break;
		case HAUS_2a:
			setImage(Main.haus_2a);
			break;
		case HAUS_2b:
			setImage(Main.haus_2b);
			break;
		case HAUS_2c:
			setImage(Main.haus_2c);
			break;
		case HAUS_2d:
			setImage(Main.haus_2d);
			break;
		case HAUS_2e:
			setImage(Main.haus_2e);
			break;
		case HAUS_2f:
			setImage(Main.haus_2f);
			break;
		case HAUS_3a:
			setImage(Main.haus_3a);
			break;
		case HAUS_3b:
			setImage(Main.haus_3b);
			break;
		case HAUS_3c:
			setImage(Main.haus_3c);
			break;
		case HAUS_3d:
			setImage(Main.haus_3d);
			break;
		case HAUS_3e:
			setImage(Main.haus_3e);
			break;
		case HAUS_3f:
			setImage(Main.haus_3f);
			break;
		case HAUS_3g:
			setImage(Main.haus_3g);
			break;
		case HAUS_3h:
			setImage(Main.haus_3h);
			break;
		case HAUS_3i:
			setImage(Main.haus_3i);
			break;
		case HAUS_4a:
			setImage(Main.haus_4a);
			break;
		case HAUS_4b:
			setImage(Main.haus_4b);
			break;
		case HAUS_4c:
			setImage(Main.haus_4c);
			break;
		case HAUS_4d:
			setImage(Main.haus_4d);
			break;
		case HAUS_4e:
			setImage(Main.haus_4e);
			break;
		case HAUS_4f:
			setImage(Main.haus_4f);
			break;
			
		case FELS_WATER:
			setImage(Main.fels_water);
			break;
		case FELS_GRASS:
			setImage(Main.fels_grass);
			break;
		case FELS_SAND:
			setImage(Main.fels_sand);
			break;
		case CACTUS_BOTTOM:
			setImage(Main.cactus_bottom);
			break;	
		case CACTUS_TOP:
			setImage(Main.cactus_top);
			break;	
		case BAUM_BOTTOM:
			setImage(Main.baum_bottom);
			break;	
		case BAUM_TOP:
			setImage(Main.baum_top);
			break;	
			
			
		case MTR_GRASS:
			setImage(Main.mtr_grass);
			break;
		case MTL_GRASS:
			setImage(Main.mtl_grass);
			break;
		case MTB_GRASS:
			setImage(Main.mtb_grass);
			break;
		case MTF_GRASS:
			setImage(Main.mtf_grass);
			break;
		case MTLB_GRASS:
			setImage(Main.mtlb_grass);
			break;
		case MTLF_GRASS:
			setImage(Main.mtlf_grass);
			break;
		case MTRB_GRASS:
			setImage(Main.mtrb_grass);
			break;
		case MTRF_GRASS:
			setImage(Main.mtrf_grass);
			break;
		case MTILB_GRASS:
			setImage(Main.mtilb_grass);
			break;
		case MTILF_GRASS:
			setImage(Main.mtilf_grass);
			break;
		case MTIRB_GRASS:
			setImage(Main.mtirb_grass);
			break;
		case MTIRF_GRASS:
			setImage(Main.mtirf_grass);
			break;
		case MTR_WATER:
			setImage(Main.mtr_water);
			break;
		case MTL_WATER:
			setImage(Main.mtl_water);
			break;
		case MTB_WATER:
			setImage(Main.mtb_water);
			break;
		case MTF_WATER:
			setImage(Main.mtf_water);
			break;
		case MTLF_WATER:
			setImage(Main.mtlf_water);
			break;
		case MTLB_WATER:
			setImage(Main.mtlb_water);
			break;
		case MTRF_WATER:
			setImage(Main.mtrf_water);
			break;
		case MTRB_WATER:
			setImage(Main.mtrb_water);
			break;
		case MTILF_WATER:
			setImage(Main.mtilf_water);
			break;
		case MTILB_WATER:
			setImage(Main.mtilb_water);
			break;
		case MTIRF_WATER:
			setImage(Main.mtirf_water);
			break;
		case MTIRB_WATER:
			setImage(Main.mtirb_water);
			break;
			
		case MTR_SAND:
			setImage(Main.mtr_sand);
			break;
		case MTL_SAND:
			setImage(Main.mtl_sand);
			break;
		case MTB_SAND:
			setImage(Main.mtb_sand);
			break;
		case MTF_SAND:
			setImage(Main.mtf_sand);
			break;
		case MTLF_SAND:
			setImage(Main.mtlf_sand);
			break;
		case MTLB_SAND:
			setImage(Main.mtlb_sand);
			break;
		case MTRF_SAND:
			setImage(Main.mtrf_sand);
			break;
		case MTRB_SAND:
			setImage(Main.mtrb_sand);
			break;
		case MTILF_SAND:
			setImage(Main.mtilf_sand);
			break;
		case MTILB_SAND:
			setImage(Main.mtilb_sand);
			break;
		case MTIRF_SAND:
			setImage(Main.mtirf_sand);
			break;
		case MTIRB_SAND:
			setImage(Main.mtirb_sand);
			break;	
			
		default:
			setImage(Main.grass);
			break;
		}	
		if (entity != null) {
			BufferedImage image = SwingFXUtils.fromFXImage(getImage(), null);
			Image eImage = entity.getImage();
			if(entity.getClass().equals(Player.class)) {
				if(eImage==null) {
					eImage = Main.player_straight;
				}
			}
			BufferedImage entitysImage = null;
			if(eImage!=null)
				 entitysImage = SwingFXUtils.fromFXImage(eImage, null);
			else
				entitysImage = SwingFXUtils.fromFXImage(Main.man_1_straight,null);
	//		BufferedImage inGrass = SwingFXUtils.fromFXImage(Main.inGrass, null);
			
			
			BufferedImage combined = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			Graphics g = combined.getGraphics();
			g.drawImage(image, 0, 0, null);
			g.drawImage(entitysImage, 0, 0, null);
//			if(type.equals(FieldType.HOHESGRASS) ) {
//				g.drawImage(inGrass, 0, 0, null);
//			}
//			
			
			Image combinedImage = SwingFXUtils.toFXImage(combined, null);
			setImage(combinedImage);
			
		}

	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Field other = (Field) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	/**
	 * @return the nextMap
	 */
	public String getNextMap() {
		return nextMap;
	}

	/**
	 * @param nextMap the nextMap to set
	 */
	public void setNextMap(String nextMap) {
		this.nextMap = nextMap;
	}

}
