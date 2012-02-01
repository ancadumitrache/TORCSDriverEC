package ecprac.drivers.kamikaze;

import ecprac.ea.abstracts.AbstractDriver;
import ecprac.ea.abstracts.DriversUtils;
import ecprac.ea.abstracts.map.Corner;
import ecprac.torcs.client.Action;
import ecprac.torcs.client.SensorModel;
import ecprac.torcs.genome.IGenome;

public class KamikazeDriver extends AbstractDriver {

	private static final double bias=1;
	private NeuralNetwork network=new NeuralNetwork(KamikazeDriverGenome.NNinputs,KamikazeDriverGenome.NNnumOutputs);
	private double[] weights=new double[60];
	
	public void loadGenome(IGenome genome) {

		if (genome instanceof KamikazeDriverGenome) {
			KamikazeDriverGenome llgenome = (KamikazeDriverGenome) genome;
			
		 weights=llgenome.weights;
			


		} else {
			System.err.println("Invalid Genome assigned");
		}

	}
	
	public double[] rescaleOutputs(double[] output) {
		
	double[] newOutput=new double[output.length];
	for(int i=0;i<output.length;i++) {
	newOutput[i]=output[i]*2-1;	
	}
	return newOutput;
	}
	
	public double[] getInput(SensorModel sensors) {
		Corner c = trackmap.getNextCorner(sensors.getDistanceFromStartLine());
		double[] inputs=new double[6];
		
		inputs[0]= Math.tan(sensors.getSpeed() * Math.PI / 300);
		inputs[1]= Math.tan(sensors.getTrackPosition() * Math.PI / 6);
		inputs[2]= Math.tan(sensors.getAngleToTrackAxis() / 2);
		inputs[3]= Math.exp(-c.distance);
		inputs[4]= Math.tan(c.sharpness * Math.PI / 720);
		inputs[5]=bias;
		return inputs;
	}

	public double getAcceleration(SensorModel sensors) {
		double[] inputs= getInput(sensors);
		
		network.changeWeights(weights);
		network.changeInputs(inputs);

		
		double[] output=rescaleOutputs(network.getOutput());
		
		if (sensors.getSpeed()<50) {
		return 1;
		} else {
		return output[0];
		}
		
	}
	
	public double getSteering(SensorModel sensors) {
		double[] inputs= getInput(sensors);
		
		network.changeWeights(weights);
		network.changeInputs(inputs);
		
		
		double[] output=rescaleOutputs(network.getOutput()); //rescaled to outputs between -1 and 1.
		if (output[3]<=0) {
		return DriversUtils.moveTowardsTrackPosition( sensors, (output[1]+1)*0.5, output[2]);	
		} else {
		return DriversUtils.alignToTrackAxis(sensors, (output[1]+1)*0.5);	
			
		}
		
		
		
	}
	
	
	public String getDriverName() {
		return "Kamikaze Driver";
	}

	
	/*
	 * The following methods are only here as a reminder that you can,
	 * and may change all driver methods, including the already implemented
	 * ones, such as those beneath.
	 */
	public void controlQualification(Action action, SensorModel sensors) {	
		super.controlQualification(action, sensors);
	}
	
	public void controlRace(Action action, SensorModel sensors) {
		super.controlRace(action, sensors);
	}
	
	public void defaultControl(Action action, SensorModel sensors){
		super.defaultControl(action, sensors);
	}
	
	
}