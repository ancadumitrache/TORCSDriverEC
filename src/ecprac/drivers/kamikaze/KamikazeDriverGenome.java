package ecprac.drivers.kamikaze;

import ecprac.torcs.genome.IGenome;

public class KamikazeDriverGenome implements IGenome {
	
	private static final long serialVersionUID = 4325760702492294028L;
	public static final double[] NNinputs={0,0,0,0,0,0};
	public static final int NNnumOutputs=4;
	public double[] weights=new double[60];
	public double[] sigma=new double[60];

}
