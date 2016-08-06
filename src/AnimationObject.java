import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Vector;


public abstract class AnimationObject {
	Color color;
	int radius;
	Vector<Double> position;
	
	Vector<Double> velocity;
	int width, height;
	boolean obstacle;
	
	static final int X = 0;
	static final int Y = 1;
	
	
	double getX() {	
		return position.get(X); 
	}
	
	double getY(){	
		return position.get(1); 
	}
	

	Vector<Double> separate(ArrayList<Bird> neighbours, int max) {
		Vector<Double> s = init();
		int i = 0;
		for (Bird b : neighbours) {
			if (!b.equals(this)) {
				double d = distance(b.position, this.position);
				if (d < max && d >= 0) {
					s = sub(s, sub(b.position, this.position));
					i++;
				}
			}
		}
		if (i > 0)
			s = divide(s, i);
		return s;
	}
	
	Vector<Double> separatePredators(ArrayList<Predator> neighbours, int max) {
		Vector<Double> s = init();
		int i = 0;
		for (Predator b : neighbours) {
			if (!b.equals(this)) {
				double d = distance(b.position, this.position);
				if (d < max && d >= 0) {
					s = sub(s, sub(b.position, this.position));
					i++;
				}
			}
		}
		if (i > 0)
			s = divide(s, i);
		return s;
	}
	
	Vector<Double> align(ArrayList<Bird> neighbours, int k) {
		Vector<Double> c = init();
		int i = 0;
		for (Bird b : neighbours) {
			if (!b.equals(this)) {
				double d = distance(b.position, this.position);
				if (d < k && d > 0) {
					c = add(c, b.velocity);
					i++;
				}
			}
		}
		if (i > 0) {
			c = divide(c, i);
			c = divide(sub(c, this.velocity), 5);
		}
		return c;
	}
	
	Vector<Double> cohesion(ArrayList<Bird> neighbours, int k) {
		Vector<Double> a = init();
		int i = 0;
		for (Bird b : neighbours) {
			if (!b.equals(this)) {
				double d = distance(b.position, this.position);
				if (d < k && d > 0) {
					a = add(a, b.position);
					i++;
				}
			}
		}
		if (i > 0) {
			a = divide(a, i);
			a = divide(sub(a, this.position), 100.0);
		}

		return a;
	}
	
	void limitSpeed(int max){
		if (distance(init(), this.velocity) > max) {
			//velocity = (velocity / length) * max
			this.velocity = multiply(divide(this.velocity, distance(init(), this.velocity)), max);

		}
	}
	
	void limitBorders(int width, int height){
		if (getX() < 0.0) { 
			position.set(0, width-5.0);
			position = add(position,velocity);
		}
		if (getX() > width-1) {
			position.set(0, 5.0);
			position = add(position,velocity);
		}
		if (getY() < 0.0) {
			position.set(1, height-5.0);
			position = add(position,velocity);
		}
		if (getY() > height-1){
			position.set(1, 5.0);
			position = add(position,velocity);
		}
	}

	Vector<Double> avoidObstacles(ArrayList<Obstacle> obstacles) {
		Vector<Double> v = init();
		int i = 0;
		Vector<Double> newPosition = add(position, velocity);
		for (Obstacle o : obstacles) {
			double dist = distance(position, o.position);
			double r = (double)(o.radius+radius);
			if (dist <= r) {
				obstacle = true;
				v = add(v, sub(position, o.position));
			}
			else if (dist <= 3*r && dist > r) {
				double a = (Math.pow(getX()-o.getX(), 2) + Math.pow(getY() - o.getY(), 2))/Math.pow(getX()-o.getX(), 2);
				double b1 = -o.getY() - getY();
				double b2 = -(2*Math.pow(getY() - o.getY(),2))/Math.pow(getX()-o.getX(), 2);
				double b3 = (2*r*r*(getY()-o.getY()))/Math.pow(getX()-o.getX(),2);
				double b = b1 + b2*o.getY()-b3 + (getY()-o.getY());
				double c1 = Math.pow(r, 4)/Math.pow(getX()-o.getX(), 2);
				double c2 = ((2*r*r*(getY()-o.getY())/Math.pow(getX()-o.getX(), 2)))*o.getY();
				double c3 = (Math.pow(getY() - o.getY(), 2)/Math.pow(getX()-o.getX(), 2))*Math.pow(o.getY(),2);
				double c = o.getY() * getY() - r*r - (getY() - o.getY())*o.getY() + c1 + c2 + c3;
				Vector<Double> p = new Vector<Double>();
				Vector<Double> p1 = new Vector<Double>();
				Vector<Double> p2 = new Vector<Double>();
				double y1 = (-b-Math.sqrt(b*b-4*a*c))/(2*a);
				double y2 = (-b+Math.sqrt(b*b-4*a*c))/(2*a);
				double x1 = (r*r/(getX() - o.getX())) + o.getX() + ((getY() - o.getY())/(getX() - o.getX()))*(o.getY() - y1);
				double x2 = (r*r/(getX() - o.getX())) + o.getX() + ((getY() - o.getY())/(getX() - o.getX()))*(o.getY() - y2);
				p1.add(x1); p1.add(y1);
				p2.add(x2); p2.add(x2);
				double d1 = distance(newPosition, p1);
				double d2 = distance(newPosition, p2);
				
				if ((Double.isNaN(d1) && Double.isNaN(d2)) || Math.abs(d1-d2) < BoidsAnimation.maxVelocity*2) { //avoid being stuck
					obstacle = true;
					v = add(v, sub(position, o.position));
					i++;
					break;
				}
				
				if (d1 < d2)
					p = p1;
				else
					p = p2;
				
				Vector<Double> v1 = sub(o.position, position);
				Vector<Double> v2 = sub(p, position);
				Vector<Double> v3 = sub(newPosition, position);
				double cos1 = dotProduct(v1, v2)/(distance(v1, init())*distance(v2, init()));
				double cos2 = dotProduct(v1, v3)/(distance(v1, init())*distance(v3, init()));
				if (cos1 <= cos2) {
					obstacle = true;
					v = add(v, v2);
					i++;
				}
			}
		}
		if (i > 0) {
			v = divide(v, (double)i);
			v = multiply(v, 30.0); //force to avoid obstacle
		}
		return v;
	}
	
	Vector<Double> add(Vector<Double> a, Vector<Double> b) {
		Vector<Double> c = new Vector<Double>();
		c.add(a.get(0) + b.get(0));
		c.add(a.get(1) + b.get(1));
		return c;
	}
	
	double dotProduct(Vector<Double> a, Vector<Double> b) {
		return ((a.get(X)*b.get(X))+(a.get(Y)*b.get(Y)));
	}
	
	Vector<Double> add(Vector<Double> a, Vector<Double> b,
			Vector<Double> c, Vector<Double> d) {
		return add(add(a,b), add(c,d));
	}
	
	Vector<Double> divide(Vector<Double> a, double n) {
		Vector<Double> v = new Vector<Double>();
		v.add(a.get(0)/n);
		v.add(a.get(1)/n);
		return v;
	}
	
	Vector<Double> sub(Vector<Double> a, Vector<Double> b) {
		Vector<Double> v = new Vector<Double>();
		v.add(a.get(0) - b.get(0));
		v.add(a.get(1) - b.get(1));
		return v;
	}
	
	Vector<Double> multiply(Vector<Double> a, double d) {
		Vector<Double> v = new Vector<Double>();
		v.add((a.get(0) * d));
		v.add((a.get(1) * d));
		return v;
	}
	
	double distance(Vector<Double> a, Vector<Double> b) {
		return Math.sqrt(Math.pow(a.get(0) - b.get(0), 2) 
				+ Math.pow(a.get(1) - b.get(1), 2));
	}
	
	Vector<Double> init(){
		Vector<Double> v = new Vector<Double>();
		v.add(0.0); 
		v.add(0.0);
		return v;
	}
	
	Vector<Double> init(double x, double y){
		Vector<Double> v = new Vector<Double>();
		v.add(x); 
		v.add(y);
		return v;
	}
	
	Vector<Double> randomVelocity(int maxVelocity){
		Vector<Double> v = new Vector<Double>();
		Random random = new Random();
		v.add((double)random.nextInt(maxVelocity)+1);
		v.add((double)random.nextInt(maxVelocity)+1);
		return v;
	}

	public void calculatePosition(int width, int height, ArrayList<Bird> neighbours, double sepParam, double alParam,
			double cohParam, int maxVelocity, int maxCloseness, int kNeighboursAl, int kNeighboursCoh,
			ArrayList<Obstacle> obstacles, ArrayList<Predator> predators) {
		// TODO Auto-generated method stub
		
	}
}
