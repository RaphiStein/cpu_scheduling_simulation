import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.print.attribute.standard.SheetCollate;


public abstract class Scheduler {
	private int clock;
	private SchedulerTypes schedulerType;
	
	private Queue<Process> readyQ; 
	private Queue<Process> waitQ; 

	private Process currentProcess;
	private Cycle currentCycle;

	public Scheduler(ArrayList<Process> processes, SchedulerTypes schedulerType){
		if (schedulerType == SchedulerTypes.FCFS){
			this.schedulerType = schedulerType;
			readyQ = new LinkedList<Process>();
			for (int i = processes.size()-1; i > -1; i--){
				readyQ.add(processes.get(i));
			}
		}
		else if (schedulerType == SchedulerTypes.SJF){
			this.schedulerType = schedulerType;
			readyQ = new PriorityQueue<Process>(processes.size(), new CycleComparator());
			for (int i = processes.size()-1; i > -1; i--){
				Process processTemp = processes.get(i);
				if (processTemp.hasNextCycle()){
					readyQ.add(processTemp);					
				}
			}
		}
		//Initialize WaitQ
		waitQ = new LinkedList<Process>();
		// Initialize Clock
		clock = 0;
	}

	public void printReadyQ(){
		System.out.println("Current ReadyQ:");
		ArrayList<Process> tempReadyQ = new ArrayList<Process>(readyQ);
		for (int i = 0; i < tempReadyQ.size(); i++){
			if (schedulerType == SchedulerTypes.FCFS){
				System.out.println(" " + tempReadyQ.get(i).getName() + ", cycle count: " + tempReadyQ.get(i).getNumberOfCycles());				
			}
			else if (schedulerType == SchedulerTypes.SJF){
				System.out.println(" " + tempReadyQ.get(i).getName() + ", cycle count: " + tempReadyQ.get(i).getNumberOfCycles() + ", estimated burst: " + tempReadyQ.get(i).generateEstimateForNextBurst());
			}
		}
		System.out.println("");
	}
	public void printWaitQ(){
		System.out.println("Current WaitQ:");
		ArrayList<Process> tempWaitQ = new ArrayList<Process>(waitQ);
		for (int i = 0; i < tempWaitQ.size(); i++){
			System.out.println(" " + tempWaitQ.get(i).getName() + ", wait time: " + tempWaitQ.get(i).getRemainingWaitQTime(clock));
		}
		System.out.println("");
	}
	public boolean isNotFinished(){
		return !(readyQ.isEmpty() && waitQ.isEmpty());
	}
	public abstract void printHeader();
	public void printFooter(){
		System.out.println("** We are done. All processes have terminated**\n\n");
	}
	public void printIterationComplete(){
		System.out.println("** Scheduling Iteration Complete\n\n");
	}
	private void addClockTime(int time){
		this.clock = this.clock + time;
	}
	public void runNextProcess(){
		// Get next process
		if (readyQ.peek() != null){
			handleReadyQ();
			System.out.println("Checking wait queue...");
			handleWaitQ();
		}
		else if (waitQ.peek() != null){
			// There was nothing in the readyQ so speed up the clock by smallest remaining time in waitQ
			jumpAheadInTime();
			currentCycle 	= null;
			currentProcess 	= null;
			System.out.println("Checking wait queue...");
			handleWaitQ();
		}
		else {
			System.out.println("DONE");
			// DONE
		}	
	}
	private void handleReadyQ(){
		currentProcess = readyQ.remove();
		// Remove current burst from burst queue
		currentCycle = currentProcess.getNextCycle();
		// Printout
		printProcessSpecs(currentProcess.getName(), currentProcess.getCurrentReadyQWaitTime(), currentCycle.getBurst(), currentProcess.getNumberOfCycles(), currentCycle.getWait());

		//Increment Clock
		addClockTime(currentCycle.getBurst());
		//Increment readyQ wait properties of all processes on the Q
		Iterator<Process> readyQIerator = readyQ.iterator();
		while(readyQIerator.hasNext()){
			readyQIerator.next().addCurrentReadyQWaitTime(currentCycle.getBurst(), schedulerType);
		}
		//Move process to waitQ or terminate
		if (currentProcess.getNumberOfCycles() != 0){
			//Move to wait Queue
			currentProcess.setCurrentWaitBurst(currentCycle.getWait(), clock);
			currentProcess.resetCurrentReadyQWaitTime();
			waitQ.add(currentProcess);
		}
		else {
			// Terminate
			// Do nothing for now
		}
	}
	private void handleWaitQ(){
		// WAIT Q
		// Move wait queue items to ready q and reset readyQ wait times
		Iterator<Process> waitQIterator = waitQ.iterator();
		if (!waitQIterator.hasNext()){
			System.out.println("Queue is empty");
		}
		else {
			boolean processesReadyToBeMoved = false;
			while(waitQIterator.hasNext()){
				Process process = waitQIterator.next();
				//System.out.println("===--- " + clock + "---===");
				if (process.getRemainingWaitQTime(clock) <= 0){
					System.out.println(" " + process.getName() + ", wait time: " + process.getRemainingWaitQTime(clock));
					System.out.println(" moving " + process.getName() + " to ready Q");

					processesReadyToBeMoved = true;
					// Remove from waitQ and move process back to readyQ
					waitQIterator.remove();
					readyQ.add(process);
					
				}
			}
			if (!processesReadyToBeMoved) System.out.println(" No processes ready to be moved to readyQ");
		}
		System.out.println("");
	}
	public void printProcessSpecs(String name, int waitTime, int burstTime, int remainingCycles, int waitBurst){
		System.out.println("Running " + name + "...");
		System.out.println(" wait time in readyQ (ms): " + waitTime);
		System.out.println(" burst time (ms): " + burstTime);		
		System.out.println(" remaining cycles: " + remainingCycles);
		if (remainingCycles == 0){
			System.out.println(" process has terminated");
		}
		else {
			System.out.println(" process moved to wait Q for " + waitBurst + " ms");
		}
		System.out.println("");
	}
	private void jumpAheadInTime(){
		Iterator<Process> waitQIterator = waitQ.iterator();
		// Set Lowest Time
		int lowestTime = waitQIterator.next().getRemainingWaitQTime(clock);
		while(waitQIterator.hasNext()){
			Process process = waitQIterator.next();
			if (process.getRemainingWaitQTime(clock) <= lowestTime){
				lowestTime = process.getRemainingWaitQTime(clock);
			}
		}
		clock += lowestTime;
	}
}
