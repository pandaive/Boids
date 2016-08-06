import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//class for a parameter slider used to customize parameters
public class ParamSlider  extends JPanel implements ChangeListener {
	static final int VALUE_MIN = 0;
	static final int VALUE_MAX = 50;
	static final int STEP = 1;
	
	static final int NB_MIN = 0;
	static final int NB_MAX = BoidsAnimation.width/2;
	
	static final String SEPARATION = "Separation Parameter";
	static final String ALIGNMENT = "Alignment Parameter";
	static final String COHESION = "Cohesion Parameter";
	static final String ALIGNMENT_NEIGHBOURS = "Alignment - Radius of neighbours";
	static final String COHESION_NEIGHBOURS = "Cohesion - Radius of neighbours";
	static final String MAX_VELOCITY = "Max velocity";
	
	static final String ADD_OBSTACLE = "Add obstacle";
	static final String REMOVE_OBSTACLES = "Remove obstacles";
	
	static final String ADD_PREDATOR = "Add predator";
	static final String REMOVE_PREDATORS = "Remove predators";
	
	static final String ANIMATION_SPEED = "Animation speed";

	
	public ParamSlider(double sep, double align, double coh,
			int nbAlign, int nbCoh, int maxVelocity, int animSpeed){
		super(new FlowLayout());
		
		initIntSlider(animSpeed, ANIMATION_SPEED);
		initSlider((int) (sep*10), SEPARATION);
		initSlider((int) (align*10), ALIGNMENT);
		initSlider((int) (coh*10), COHESION);
		initIntSlider(nbAlign, ALIGNMENT_NEIGHBOURS);
		initIntSlider(nbCoh, COHESION_NEIGHBOURS);
		initIntSlider(maxVelocity, MAX_VELOCITY);
		
		JButton saveParameters = new JButton("Save parameters");
        saveParameters.setVerticalTextPosition(AbstractButton.CENTER);
        saveParameters.setHorizontalAlignment(AbstractButton.CENTER);
        saveParameters.setActionCommand(REMOVE_OBSTACLES);
        add(saveParameters);
        saveParameters.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				BoidsAnimation.saveParameters();
				
			}
		});
		
		JButton addObstacle = new JButton("Add an obstacle");
        addObstacle.setVerticalTextPosition(AbstractButton.CENTER);
        addObstacle.setHorizontalAlignment(AbstractButton.CENTER);
        addObstacle.setActionCommand(ADD_OBSTACLE);
        add(addObstacle);
        addObstacle.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				BoidsAnimation.generateObstacle();
				
			}
		});
        
        JButton removeObstacles = new JButton("Remove all obstacles");
        addObstacle.setVerticalTextPosition(AbstractButton.CENTER);
        addObstacle.setHorizontalAlignment(AbstractButton.CENTER);
        addObstacle.setActionCommand(REMOVE_OBSTACLES);
        add(removeObstacles);
        removeObstacles.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				BoidsAnimation.removeObstacles();
				
			}
		});
        
        JButton addPredator = new JButton("Add a predator");
        addPredator.setVerticalTextPosition(AbstractButton.CENTER);
        addPredator.setHorizontalAlignment(AbstractButton.CENTER);
        addPredator.setActionCommand(ADD_OBSTACLE);
        add(addPredator);
        addPredator.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				BoidsAnimation.generatePredator();
				
			}
		});
        
        JButton removePredators = new JButton("Remove all predators");
        removePredators.setVerticalTextPosition(AbstractButton.CENTER);
        removePredators.setHorizontalAlignment(AbstractButton.CENTER);
        removePredators.setActionCommand(REMOVE_PREDATORS);
        add(removePredators);
        removePredators.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				BoidsAnimation.removePredators();
				
			}
		});
			
	}

	private void initSlider(int init, String param) {

		JSlider slider = new JSlider(JSlider.HORIZONTAL, VALUE_MIN, VALUE_MAX, init);
		slider.setName(param);
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		
		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		for (int i = 0; i < 6; i++) {
			labels.put(i*10, new JLabel(i + ".0"));
		}
		slider.setLabelTable(labels);
		slider.setPaintLabels(true);
		
		add(new JLabel(param));
        add(slider);
        
	}
	
	private void initIntSlider(int init, String param) {
		JSlider slider;
		int max = NB_MAX;
		if (param == MAX_VELOCITY || param == ANIMATION_SPEED)
			max = 10;
		slider = new JSlider(JSlider.HORIZONTAL, NB_MIN, max, init);
		slider.setName(param);
		slider.addChangeListener(this);
		slider.setMajorTickSpacing(50);
		slider.setMinorTickSpacing(10);
		slider.setPaintTicks(true);
		
		Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
		for (int i = 0; i < (max == NB_MAX ? max/100+1 : max+1); i++) {
			labels.put((max == NB_MAX ? i*100 : i), new JLabel((max == NB_MAX ? i*100 : i) + ""));
		}
		slider.setLabelTable(labels);
		slider.setPaintLabels(true);
		
		add(new JLabel(param));
        add(slider);
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		// TODO Auto-generated method stub
		
		JSlider source = (JSlider)arg0.getSource();
        if (!source.getValueIsAdjusting()) {
        	double value = (double)source.getValue()/10;
            switch(source.getName()) {
            case SEPARATION:
            	BoidsAnimation.seperationParameter = value;
            	break;
            case ALIGNMENT:
            	BoidsAnimation.alignmentParameter = value;
            	break;
            case COHESION:
            	BoidsAnimation.cohesionParameter = value;
            	break;
            case ALIGNMENT_NEIGHBOURS:
            	BoidsAnimation.nbAlignment = (int) (value*10);
            	break;
            case COHESION_NEIGHBOURS:
            	BoidsAnimation.nbCohesion = (int) (value*10);
            	break;
            case MAX_VELOCITY:
            	BoidsAnimation.maxVelocity = (int) (value*10);
            	break;
            case ANIMATION_SPEED:
            	BoidsAnimation.animationSpeed = (int) (value*10);
            	break;
            }
        }	
	}
}
