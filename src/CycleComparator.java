import java.util.Comparator;


public class CycleComparator implements Comparator<Process> {

	@Override
	public int compare(Process p0, Process p1) {
		int p0Estimate 	= p0.generateEstimateForNextBurst();
		int p1Estimate	= p1.generateEstimateForNextBurst();
		
		if (p0Estimate > p1Estimate){
			return 1;
		}
		else if (p0Estimate < p1Estimate){
			return -1;
		}
		else {
			return 0;			
		}
	}
}
