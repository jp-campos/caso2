package caso2;

public class Monitor {

	
	
	
	private long start; 
	private long tiempo; 
	
	
	public void start()
	{
		start = System.currentTimeMillis(); 
		
	}
	
	
	public long end()
	{
		return start - tiempo; 
	}
	
	
}
