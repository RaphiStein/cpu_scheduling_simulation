import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class FCFSscheduler extends Scheduler {

	
	public FCFSscheduler(ArrayList<Process> processes) {
		super(processes, SchedulerTypes.FCFS);
	}

	public void printHeader(){
		System.out.println("** FCFS Scheduler **\n\n");
	}
	
}
