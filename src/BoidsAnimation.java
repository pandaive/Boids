import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class BoidsAnimation extends JComponent implements Runnable {
	
	public BoidsAnimation(){
		birds = new ArrayList<Bird>();
		obstacles = new ArrayList<Obstacle>();
		predators = new ArrayList<Predator>();
		Random random = new Random();
		for (int i = 0; i < numberOfBirds; i++) {
			Bird b = new Bird(random.nextInt(width), random.nextInt(height), birdRadius);
			//Bird b = new Bird(width/2, height/2, birdRadius);
			birds.add(b);
		}
		
	    Thread t = new Thread(this);
	    t.start();
	    
	}

	static int animationSpeed = 7;
	
	static int numberOfBirds = 200;
	static int numberOfPredators = 3;
	static int numberOfObstacles = 5;
	
	static ArrayList<Bird> birds; 
	static ArrayList<Predator> predators;
	static ArrayList<Obstacle> obstacles;
	
	static double seperationParameter = 1.0;
	static double alignmentParameter = 1.0;
	static double cohesionParameter = 1.0;
	
	static int maxVelocity = 10;
	static int maxVelocityPredators = (maxVelocity - 2 >= 0 ? maxVelocity-2 : 0);
	static int maxCloseness = 30; //separation
	static int nbAlignment = 200;
	static int nbCohesion = 170;
	static int width = 1200;
	static int height = 700;
	static int birdRadius = 10;
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
        try {
        	while(true) {
	    		for (Bird b : birds) {
	    			b.calculatePosition(this.width, this.height, birds,
	    					seperationParameter, alignmentParameter, cohesionParameter,
	    					maxVelocity, maxCloseness, nbAlignment, nbCohesion,
	    					obstacles, predators);
	    		}
	    		for (Predator p : predators) {
	    			p.calculatePosition(width, height, birds, 
	    					seperationParameter, alignmentParameter, cohesionParameter, 
	    					maxVelocityPredators, maxCloseness, nbAlignment, nbCohesion, 
	    					obstacles, predators);
	    		}
	    		repaint();
				Thread.sleep(1000 / (5*animationSpeed+1));
        	}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g){
		Graphics2D g2D = (Graphics2D) g;
		for (Bird b : birds) {
			Ellipse2D.Double myCircle = new Ellipse2D.Double(b.getX() - b.radius, b.getY() - b.radius, 2 * b.radius, 2 * b.radius);
			double x = b.velocity.get(0);
			double y = b.velocity.get(1);
			//draw a line showing a direction
			Line2D.Double myLine = new Line2D.Double(b.getX(), b.getY(), 
					(int)b.getX()+ (20*x/Math.abs(x)) - (x*10) % 15 ,//(x > 0.1 ? x*10 : x < -0.1 ? x*10 : 0), 
					(int)b.getY()+ (20*y/Math.abs(y)) - (y*10) % 15);//(y > 0.1 ? y*10 : y < -0.1 ? y*10 : 0));
			g2D.setPaint(b.color);
	    	g2D.fill(myCircle);
	    	g2D.setPaint(Color.BLACK);
	    	g2D.draw(myCircle);
	    	g2D.draw(myLine);
		}
		for (Obstacle o : obstacles) {
			Ellipse2D.Double myCircle = new Ellipse2D.Double(o.getX() - o.radius, o.getY() - o.radius, 2 * o.radius, 2 * o.radius);
			g2D.setPaint(o.color);
	    	g2D.fill(myCircle);
	    	g2D.setPaint(Color.BLACK);
	    	g2D.draw(myCircle);
		}
		
		for (Predator p : predators) {
			Ellipse2D.Double myCircle = new Ellipse2D.Double(p.getX() - p.radius, p.getY() - p.radius, 2 * p.radius, 2 * p.radius);
			g2D.setPaint(p.color);
	    	g2D.fill(myCircle);
	    	g2D.setPaint(Color.BLACK);
	    	g2D.draw(myCircle);
		}
	}
	
	public static void generatePredator(){
		Random random = new Random();
		int x,y;
		do {
		x = random.nextInt(width-10)+5;
		y = random.nextInt(width-10)+5;
		} while (overlap (x,y, 15));
		Predator predator = new Predator((double) x, (double) y, maxVelocityPredators);
		predators.add(predator);
	}
	
	public static void removePredators(){
		predators = new ArrayList<Predator>();
	}
	
	
	public static void generateObstacle() {
		Random random = new Random();
		int size, x,y;
		size = random.nextInt(39) + 30;
		do {
			x = random.nextInt(width-50-2*size) + 25+size;
			y = random.nextInt(height-50-2*size) + 25+size;
		} while (overlap(x,y, size));
		Vector<Double> v = new Vector<Double>();
		v.add((double) x);
		v.add((double) y);
		Obstacle o = new Obstacle(size, v);
		obstacles.add(o);
		
	}
	
	private static boolean overlap(int a, int b, int s) {
		double x = (double) a;
		double y = (double) b;
		double size = (double) s;
		for (Obstacle o : obstacles) {
			double dist = Math.sqrt(Math.pow(x - o.getX(),2)+Math.pow(y-o.getY(), 2));
			//adding the bird radius will prevent birds from getting stuck between objects
			if (dist >= Math.abs(size-o.radius) && dist <= Math.abs(size+o.radius+birdRadius*2))
				return true;
		}
		for (Bird o : birds) {
			double dist = Math.sqrt(Math.pow(x - o.getX(),2)+Math.pow(y-o.getY(), 2));
			if (dist >= Math.abs(size-o.radius) && dist <= Math.abs(size+o.radius))
				return true;
		}
		return false;
	}
	
	public static void removeObstacles(){
		obstacles = new ArrayList<Obstacle>();
	}
	
	public static void saveParameters(){
		PrintWriter writer;
		try {
			writer = new PrintWriter("parameters.txt", "UTF-8");
			writer.println(seperationParameter);
			writer.println(alignmentParameter);
			writer.println(cohesionParameter);
			writer.println(nbAlignment);
			writer.println(nbCohesion);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			File f = new File("parameters.txt");
			try {
				f.createNewFile();
				saveParameters();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static void loadParameters() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("parameters.txt"));
			String line = br.readLine();
			seperationParameter = Double.parseDouble(line);
			line = br.readLine();
			alignmentParameter = Double.parseDouble(line);
			line = br.readLine();
			cohesionParameter = Double.parseDouble(line);
			line = br.readLine();
			nbAlignment = Integer.parseInt(line);
			line = br.readLine();
			nbCohesion = Integer.parseInt(line);
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//it will just leave default params
		}
	}
	
	public static void main(String[] args){
		loadParameters();
	    JFrame f = new JFrame("Boids");
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    f.add(new BoidsAnimation());
	    f.setSize(width, height); 
	    f.setVisible(true);
	    
	    JFrame s = new JFrame("Parameters");
	    s.add(new ParamSlider(seperationParameter, alignmentParameter, cohesionParameter,
	    		nbAlignment, nbCohesion, maxVelocity , animationSpeed));
	    s.setSize(280, 700);
	    s.setVisible(true);
	}

}
