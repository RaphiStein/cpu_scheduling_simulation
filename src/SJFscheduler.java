import java.util.ArrayList;


public class SJFscheduler extends Scheduler {

	public SJFscheduler(ArrayList<Process> processes){
		super(processes, SchedulerTypes.SJF);
	}
	
	public void printHeader(){
		System.out.println("** SJF Scheduler **\n\n");
	}
}
