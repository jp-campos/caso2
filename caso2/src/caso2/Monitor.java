package caso2;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class Monitor{

	
	
	
	private long startVer; 
	
	
	private static ArrayList<Long> tiemposVerificacion = new ArrayList<>();
	private static ArrayList<Long> tiemposConsulta = new ArrayList<>();
	
	private double memoria;
	
	private boolean terminado;

	private String caso;
	
	
	public void termino(String pCaso) {
		terminado = true; 
		caso= pCaso;
		
	}
	
	public void start()
	{
		startVer = System.currentTimeMillis(); 
		System.out.println("Tiempo start " + startVer);
	}
	
	
	public long endVer()
	{	
		
		long fin = System.currentTimeMillis(); 
		long resta = fin-startVer; 
		
		System.out.println("Tiempo fin " + fin );
		
		System.out.println("Tiempo resta ver "+ resta);
		synchronized (this) {
			
			
				tiemposVerificacion.add(resta);
			
		}
		
		return fin - startVer; 
	}
	
	public long endConsu()
	{	
		
		long fin = System.currentTimeMillis(); 

		long resta = fin-startVer; 
		
		System.out.println("Tiempo fin consul "+ resta);
		synchronized (this) {
				tiemposConsulta.add(resta);
		}
		
		return fin - startVer; 
	}
	
	
	public synchronized void addConsulta(long consulta)
	{
		tiemposConsulta.add(consulta);
	}
	
	
	public synchronized void addVer(long ver)
	{
		tiemposVerificacion.add(ver);
	}
	
	public double getSystemCpuLoad() throws Exception {
		
		
		 MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		 ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		 AttributeList list = mbs.getAttributes(name, new String[]{ "SystemCpuLoad" });
		 if (list.isEmpty()) return Double.NaN;
		 Attribute att = (Attribute)list.get(0);
		 Double value = (Double)att.getValue();
		 // usually takes a couple of seconds before we get real values
		 if (value == -1.0) return Double.NaN;
		 // returns a percentage value with 1 decimal point precision
		 return ((int)(value * 1000) / 10.0);
		 }
	
	
	
	public static double getTiemposDeVerificacionPromedio()
	{
		double suma = 0.0; 
		
		for (Long long1 : tiemposVerificacion) {
			suma += (double)long1;
		}
		
		
		System.out.println("Suma = " + suma);
		System.out.println("Tamaño = " + tiemposVerificacion.size());
		
		double tamanio = (double)tiemposVerificacion.size();
		
		return suma/tamanio ; 
		
	}
	
	
	public static ArrayList<Long> getTiemposVerificacion()
	{
		return tiemposVerificacion; 
	}
	public static ArrayList<Long> getTiemposConsulta()
	{
		return tiemposConsulta; 
	}
	
	public static void reiniciarArrayListVer()
	{
		tiemposVerificacion.clear(); 
	}
	
	public static void reiniciarArrayListConsu()
	{
		tiemposConsulta.clear(); 
	}
	
	public static double getTiemposDeConsultaPromedio()
	{
		double suma = 0L; 
		
		for (Long long1 : tiemposConsulta) {
			suma += long1;
		}
		
		return suma/(double)tiemposConsulta.size(); 
		
	}
	
	
//	@Override
//	public void run()
//	{
//		long start = System.currentTimeMillis(); 
//		terminado = false; 
//		
//		while(!terminado)
//		{
//			
//		}
//		
//		long fin = System.currentTimeMillis();
//		
//		long resta = fin-start;
//		
//		if(caso.equals("verificacion"))
//		{
//			addVer(resta);
//		}else if(caso.equals("consulta")) {
//			addConsulta(resta);
//		}
		
		
	//}
	
	
}
