package caso2;

import uniandes.gload.core.LoadGenerator;
import uniandes.gload.core.Task;

public class Generator {
	

	
	private LoadGenerator generator; 
	
	
	
	public Generator(int numberTasks, int gapBetween)
	{
		Task work = createTask();
	
		generator = new LoadGenerator("Cliente-Server" , numberTasks, work, gapBetween); 
		generator.generate();
		
	}
	
	
	public Task createTask()
	{
		
		return new ClienteTask(); 
	}
	
	
}
