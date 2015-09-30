import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class Main {

	public static void main(String[] args) {
		int PROCESS_COUNT_MINIMUM 	= 2;
		int CYCLE_MAX_MINIMUM		= 2;
		
		int processCount 	= PROCESS_COUNT_MINIMUM;
		int cycleMax 		= CYCLE_MAX_MINIMUM;
		
		try {
			if (Integer.parseInt(args[0]) < PROCESS_COUNT_MINIMUM || Integer.parseInt(args[0]) > 20) throw new Exception();
				processCount = Integer.parseInt(args[0]);
			if (Integer.parseInt(args[1]) < CYCLE_MAX_MINIMUM || Integer.parseInt(args[1]) > 1000) throw new Exception();
				cycleMax	= Integer.parseInt(args[1]);
		}
		catch (Exception e){
			System.out.println("ERROR: There was a problem with your input. Please ensure they are within proper range.");
			System.exit(-1);
		}
		
		// 
		ArrayList<Process> processes = new ArrayList<Process>();
		// Create the processes
		for (int i = 0; i < processCount; i++){
			//Generate Burst base for this process
			Random random	= new Random();
			int burstBase 	= (random.nextInt(9)+1)*100;
			int cycles		= random.nextInt(cycleMax) + 1;
			Process process = new Process("Process " + i, cycles, burstBase);
			processes.add(process);
		}
		
		// Run FCFS
		Scheduler fcfs = new FCFSscheduler(processes);
		fcfs.printHeader();
		while (fcfs.isNotFinished()){
			// Run Iteration
			fcfs.printReadyQ();
			fcfs.printWaitQ();
			fcfs.runNextProcess();
			fcfs.printIterationComplete();
		}
		fcfs.printFooter();
		
		for (Process process : processes) {
			process.resetCycles();
			process.resetForSJF();
		}
		
		// Run SJF
		Scheduler sjf = new SJFscheduler(processes);
		sjf.printHeader();
		while (sjf.isNotFinished()){
			// Run Iteration
			sjf.printReadyQ();
			sjf.printWaitQ();
			sjf.runNextProcess();
			sjf.printIterationComplete();
		}
		sjf.printFooter();
		
		// Print Results for each process
		System.out.println(" ** Results: Average Wait Time per process\n");
		for (Process process : processes) {
			process.printProcessResults();
		}
	}

}
