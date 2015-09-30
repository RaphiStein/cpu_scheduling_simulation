import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Process {
	private String processName;
	private Queue<Cycle> cycles;
	private Queue<Cycle> cyclesArchive;
	private final int numberOfCyclesStarting;
	private Cycle cycleInProgress;

	// Properties
	private int currentReadyQWaitTime;

	private int timeWhenEnteredWaitQ;
	private int currentWaitBurst;

	private int totalWaitTime_fcfs;
	private int totalWaitTime_sjf;

	private boolean sjf_firstCycle = true;
	private int sjf_lastEstimate;
	private int sjf_thisBurst; // really just a temp placeholder. Gets assigned
								// to lastburst and used when new cycle is
								// checked out
	private int sjf_lastBurst;

	Random random;

	public Process(String name, int numberOfCycles, int burstBase) {
		this.processName = name;
		this.numberOfCyclesStarting = numberOfCycles; // won't change during
														// execution
		this.totalWaitTime_fcfs = 0;
		// FOR SJF
		this.sjf_thisBurst = -1;
		this.sjf_lastBurst = -1;
		this.sjf_lastEstimate = -1;
		this.totalWaitTime_sjf = 0;
		cyclesArchive = new LinkedList<Cycle>();

		// For all. Initialize
		cycles = new LinkedList<Cycle>();

		for (int i = 0; i < numberOfCycles; i++) {
			Cycle cycle = new Cycle(generateBurstTime(burstBase),
					generateWaitTime());
			cycles.add(cycle);
		}
	}

	private int generateBurstTime(int base) {
		random = new Random();
		return base + (random.nextInt(10) + 1);
	}

	private int generateWaitTime() {
		random = new Random();
		return random.nextInt(1000) + 1;
	}

	public int getAverageWaitTime_fcfs() {
		return totalWaitTime_fcfs / numberOfCyclesStarting;
	}

	public int getAverageWaitTime_sjf() {
		return totalWaitTime_sjf / numberOfCyclesStarting;
	}

	public int generateEstimateForNextBurst() {
		if (sjf_firstCycle) {
			sjf_lastEstimate = 500; // initialize it
			return sjf_lastEstimate;
		} else {
			sjf_lastEstimate = (int) ((sjf_lastEstimate + sjf_lastBurst) / 2.0);
			return sjf_lastEstimate;
		}
	}

	public void setLastEstimate(int estimate) {
		sjf_lastEstimate = estimate;
	}

	public int getNumberOfCycles() {
		return cycles.size();
	}

	public boolean hasNextCycle() {
		return cycles.peek() != null;
	}

	public int getNextCycleBurstTime() {
		if (hasNextCycle()) {
			return cycles.peek().getBurst();
		} else {
			try {
				throw new Exception("Process has no more bursts");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}
	}

	public Cycle getNextCycle() {
		if (hasNextCycle()) {
			// remove cycle from Queue
			cycleInProgress = cycles.poll();
			// Archive this cycle for future schedulers
			cyclesArchive.add(cycleInProgress);
			// Set the burst time (for use with sjf)
			if (!sjf_firstCycle){
				
			}
			//sjf_lastBurst = sjf_thisBurst;
			sjf_lastBurst = cycleInProgress.getBurst();
			sjf_firstCycle = false;
			return cycleInProgress;
		} else {
			return null;
		}
	}

	public Cycle getCycleInProgress() {
		return cycleInProgress;
	}

	public String getName() {
		return processName;
	}

	public int getTimeInWaitQ(int currentTime) {
		return currentTime - timeWhenEnteredWaitQ;
	}

	public int getRemainingWaitQTime(int currentTime) {
		return currentWaitBurst - (currentTime - timeWhenEnteredWaitQ);
	}

	public void setCurrentWaitBurst(int currentWaitBurst, int currentTime) {
		this.currentWaitBurst = currentWaitBurst;
		this.timeWhenEnteredWaitQ = currentTime;
	}

	public int getCurrentWaitBurst() {
		return currentWaitBurst;
	}

	public int getCurrentReadyQWaitTime() {
		return currentReadyQWaitTime;
	}

	public void addWaitTime_fcfs(int waited) {
		totalWaitTime_fcfs += waited;
	}

	public void addWaitTime_sjf(int waited) {
		totalWaitTime_sjf += waited;
	}

	public void addCurrentReadyQWaitTime(int time, SchedulerTypes scheduler) {
		this.currentReadyQWaitTime += time;
		if (scheduler == SchedulerTypes.FCFS)
			addWaitTime_fcfs(time);
		else if (scheduler == SchedulerTypes.SJF)
			addWaitTime_sjf(time);
		else
			try {
				throw new Exception("Only FCFS and SJF supported");
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public void completedCycle() {
		cycleInProgress = null;
	}

	public void resetCurrentReadyQWaitTime() {
		this.currentReadyQWaitTime = 0;
	}

	public void printProcessResults() {
		System.out.println(getName());
		System.out.println(" FCFS: " + getAverageWaitTime_fcfs());
		System.out.println(" SJF: " + getAverageWaitTime_sjf());
	}

	public void printAllCycles() {
		for (Cycle cycle : cycles) {
			System.out.print("< " + cycle.getBurst() + ", " + cycle.getWait()
					+ "> ");
		}
	}

	/*
	 * Reset cycles so another scheduler can try running on it
	 */
	public void resetCycles() {
		for (Cycle cycle : cyclesArchive) {
			cycles.add(cycle);
		}
	}

	public void resetForSJF() {
		sjf_lastBurst = -1;
		sjf_thisBurst = 500;
		sjf_firstCycle = true;
		currentReadyQWaitTime = 0;
	}
}
