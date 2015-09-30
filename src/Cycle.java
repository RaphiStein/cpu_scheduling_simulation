public class Cycle {
	private int burst;
	private int wait;
	
	
	public Cycle(int burst, int wait) {
		super();
		this.burst = burst;
		this.wait = wait;
	}
	
	public int getBurst() {
		return burst;
	}
	public void setBurst(int burst) {
		this.burst = burst;
	}
	public int getWait() {
		return wait;
	}
	public void setWait(int wait) {
		this.wait = wait;
	}
	
	
}
