package infoasys.core.common.util;

public class Timer {
	
	private long startTime;
	
	
	public Timer(){
		startTime = System.nanoTime();
	}
	
	
	public double getElapsedSecs(){
//		System.out.println("#:"+	
//				((float)(System.nanoTime() - startTime))
//				/ 1000000000
//				);
		return  ((double)(System.nanoTime() - startTime)) / 1000000000; 
	}
	
	public void reset(){
		startTime = System.nanoTime();
	}
	
	@Override
	public String toString(){
		return  getElapsedSecs()+" sec.";
	}
	
}
