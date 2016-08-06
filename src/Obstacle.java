import java.awt.Color;
import java.util.Vector;

public class Obstacle extends AnimationObject {
	
	public Obstacle(int radius, Vector<Double> position){
		color = Color.green;
		this.radius = radius;
		this.position = position;
	}
}
