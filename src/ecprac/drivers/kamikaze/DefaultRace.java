package ecprac.drivers.kamikaze;

import ecprac.ea.abstracts.AbstractRace;
import ecprac.ea.abstracts.DriversUtils;
import ecprac.torcs.controller.Driver;
import ecprac.torcs.controller.Human;

public class DefaultRace extends AbstractRace {

	public int[] runQualification(KamikazeDriverGenome[] population, boolean withGUI){
		
		KamikazeDriver[] drivers = new KamikazeDriver[population.length];
			
		for(int i=0; i<population.length; i++){
			drivers[i] = new KamikazeDriver();
			drivers[i].loadGenome(population[i]);
		}
		
		return runQualification(drivers, withGUI);
	}
	
	public int[] runRace(KamikazeDriverGenome[] population, boolean withGUI){
		
		int size = Math.min(10, population.length);
		
		KamikazeDriver[] drivers = new KamikazeDriver[size];
		
		for(int i=0; i<size; i++){
			drivers[i] = new KamikazeDriver();
			drivers[i].loadGenome(population[i]);
		}
		
		return runRace(drivers, withGUI, true);
	}

	
	
	public void showBest(){
		
		if(DriversUtils.getStoredGenome() == null ){
			System.err.println("No best-genome known");
			return;
		}
		
		KamikazeDriverGenome best = (KamikazeDriverGenome) DriversUtils.getStoredGenome();
		KamikazeDriver driver = new KamikazeDriver();
		driver.loadGenome(best);
		
		KamikazeDriver[] drivers = new KamikazeDriver[1];
		drivers[0] = driver;
		runQualification(drivers, true);
		
	}
	
	public void showBestRace(){
		
		if(DriversUtils.getStoredGenome() == null ){
			System.err.println("No best-genome known");
			return;
		}
	
		KamikazeDriver[] drivers = new KamikazeDriver[1];
		
		for(int i=0; i<10; i++){
			KamikazeDriverGenome best = (KamikazeDriverGenome) DriversUtils.getStoredGenome();
			KamikazeDriver driver = new KamikazeDriver();
			driver.loadGenome(best);
			drivers[i] = driver;
		}
		
		runRace(drivers, true, true);
	}
	
	public void raceBest(){
		
		if(DriversUtils.getStoredGenome() == null ){
			System.err.println("No best-genome known");
			return;
		}
		
		Driver[] drivers = new Driver[10];
		for(int i=0; i<9; i++){
			KamikazeDriverGenome best = (KamikazeDriverGenome) DriversUtils.getStoredGenome();
			KamikazeDriver driver = new KamikazeDriver();
			driver.loadGenome(best);
			drivers[i] = driver;
		}
		drivers[9] = new Human();
		runRace(drivers, true, true);
		
	}
	
}
