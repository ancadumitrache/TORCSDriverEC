package ecprac.drivers.kamikaze;

import java.util.Random;

import ecprac.ea.abstracts.AbstractEA;
import ecprac.ea.abstracts.DriversUtils;
import ecprac.ea.abstracts.AbstractRace.DefaultTracks;
import ecprac.torcs.controller.Driver;

public class KamikazeEA extends AbstractEA{

	private static final long serialVersionUID = 1L;
	Random r = new Random(42); 
	
	// parameters
	
	double minimalSigma=0.01;
	double tau= 1;
	double initialSigma=0.05;

	// The population 
	KamikazeDriverGenome[] population = new KamikazeDriverGenome[10];
	
	// The fitness values
	int[] fitness = new int[population.length];
	
	// The number of evaluations 
	int evals;
	
	public Class<? extends Driver> getDriverClass(){
		return KamikazeDriver.class;
	}
	
	public void run() {
		// start a completely new run
		run(false);
	}
	
	public void run(boolean continue_from_checkpoint) {

		/*
		 * 
		 * If we are not continuing from a previously
		 * terminated run, we need to initialize the population
		 * 
		 */
		if(!continue_from_checkpoint){
			//  initialization of the genomes
			initialize();
			evaluatePopulation();
			
		}


		// the evolutionary loop
		while (evals < 10000) {
			
			// find the best genome
			int best = 1;
			for(int i=0; i<population.length; i++){
				if (fitness[i] == 1) {
					best = i;
				}
			}
			
			// replace the worst two from the population with 
			// a mutated version of the best genome
			for(int i=0; i<population.length; i++){
				if(fitness[i] >=9 ){
					population[i] = mutate(population[best]);
				}
			}
			
			// Re-evaluate the population on a different track
			evaluatePopulation();
			
			// create a checkpoint 
			// this allows you to continue this run later
			DriversUtils.createCheckpoint(this);
			
			System.out.println("Evaluations: " + evals);
			
		}

		// clear the checkpoint
		DriversUtils.clearCheckpoint();
	
	}
	
	public void evaluatePopulation(){
		
		int[] fitness1 = new int[population.length];
		int[] fitness2 = new int[population.length];
		
		// Create a new Race object with a random track
		// from the set of default tracks
		DefaultRace race1 = new DefaultRace();
		race1.setTrack( DefaultTracks.getTrack(1) );
		race1.laps = 2;
		// Run the race, fitness = rank
		// The GUI is set to true, for speedup set withGUI to false
		fitness1 = race1.runRace(population, true);
		
		// race 2
		DefaultRace race2 = new DefaultRace();
		race2.setTrack( DefaultTracks.getTrack(3) );
		race2.laps = 2;
		// Run the race, fitness = rank
		// The GUI is set to true, for speedup set withGUI to false
		fitness2 = race2.runRace(population, true);
		
		// average rankings
		double[] avgRank = new double[population.length];
		int[] pos = new int[population.length];
		for (int i = 0; i < population.length; i++) {
			avgRank[i] = (double)(fitness1[i] + fitness2[i]) / 2;
			pos[i] = i;
		}
		
		// sort rankings
		for (int i = 1; i < population.length; i++) {
			for (int j = 0; j < i; j++) {
				if (avgRank[j] > avgRank[i]) {
					double aux = avgRank[i];
					avgRank[i] = avgRank[j];
					avgRank[j] = aux;
					
					int aux2 = pos[i];
					pos[i] = pos[j];
					pos[j] = aux2;
				}
			}
		}
		
		// compute fitness function
		for (int i = 0; i < population.length; i++) {
			fitness[pos[i]] = i + 1;
		}
		
		// Increment the number of evaluations
		evals += population.length;
		
		// Save the best genome from the population
		for(int i=0; i<population.length; i++){
			if(fitness[i] == 1) {
				DriversUtils.storeGenome(population[i]);
			}
		}
	}

	public void initialize() {

		// Create an array of DefaultDriverGenome objects and
		// assign a random number [0 1] to each gene
		
		for (int i = 0; i < population.length; i++) {
			
			KamikazeDriverGenome genome = new KamikazeDriverGenome();
						
			// Randomly initialize the genome with values [0,1]
			for(int j=0;j<genome.weights.length;j++) {
			genome.weights[j]=r.nextGaussian();
			genome.sigma[j]=initialSigma;
			}
			population[i] = genome;
			fitness[i] = 0;
		}
	
	}

	public KamikazeDriverGenome mutate(KamikazeDriverGenome genome) {

		KamikazeDriverGenome genome2 = new KamikazeDriverGenome();
		
		// mutate sigma
		
			for (int i=0;i<genome.weights.length;i++) {
			genome2.sigma[i] = genome.sigma[i] * Math.exp(tau * r.nextGaussian());
			genome2.sigma[i] = genome2.sigma[i] < minimalSigma ? minimalSigma : genome2.sigma[i];
			genome2.weights[i] = genome.weights[i] + (genome2.sigma[i] * r.nextGaussian());
		}
		
		return genome2;

	}

	
	public static void main(String[] args) {
		
		/*
		 * 
		 * Start without arguments to run the EA
		 * Start with -continue to continue a previous run
		 * Start with -show to show the best found
		 * Start with -show-race to show a race with 10 copies of the best found
		 * Start with -human to race against the best found
		 * 
		 */
		KamikazeEA ea = new KamikazeEA();
		DriversUtils.registerMemory(ea.getDriverClass());
		
		if(args.length > 0 && args[0].equals("-show")){
			new DefaultRace().showBest();
		} else if(args.length > 0 && args[0].equals("-show-race")){
			new DefaultRace().showBestRace();
		} else if(args.length > 0 && args[0].equals("-human")){
			new DefaultRace().raceBest();
		} else if(args.length > 0 && args[0].equals("-continue")){
			if(DriversUtils.hasCheckpoint()){
				DriversUtils.loadCheckpoint().run(true);
			} else {
				ea.run();
			}
		} else {
			ea.run();
		}
	}

}
