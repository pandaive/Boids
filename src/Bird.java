import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;

public class Bird extends AnimationObject {

	
	public Bird(int x, int y, int radius){
		this.color = Color.BLUE;
		this.radius = radius;
		velocity = init();
		position = init(x,y);
		obstacle = false;
			
	}
	
	@Override
	public void calculatePosition(int width, int height, ArrayList<Bird> neighbours,
			double sepParam, double alParam, double cohParam, int maxVelocity, int maxCloseness,
			int kNeighboursAl, int kNeighboursCoh, ArrayList<Obstacle> obstacles,
			ArrayList<Predator> predators){
		
		this.width = width;
		this.height = height;
		Vector<Double> s = separate(neighbours, maxCloseness);
		Vector<Double> a = align(neighbours, kNeighboursAl);
		Vector<Double> c = cohesion(neighbours, kNeighboursCoh);
		Vector<Double> p = avoidPredators(predators);
		
		s = multiply(s, sepParam);
		a = multiply(a, alParam);
		c = multiply(c, cohParam);
		p = multiply(p, 1);

		this.color=Color.blue;
		this.velocity = add(this.velocity, s, a, c);
		this.velocity = add(this.velocity, p);
		Vector<Double> o = avoidObstacles(obstacles);
		if (obstacle) {
			o = multiply(o, 5);
			velocity = add(this.velocity, o);
		}
		limitSpeed(maxVelocity);
		this.position = add(this.position, this.velocity);
		limitBorders(width, height);
		obstacle = false;
		
	}
	
	private Vector<Double> avoidPredators(ArrayList<Predator> predators){
		Vector<Double> v = init();
		int i = 0;
		for (Predator p : predators) {
			double dist = distance(p.position, position);
			if (dist < 150) {
				v = add(v, sub(position, p.position));
				i++;
			}
		}
		if (i > 0) {
			v = divide(v, (double) i);
		}
		return v;
	}
	
	
}

