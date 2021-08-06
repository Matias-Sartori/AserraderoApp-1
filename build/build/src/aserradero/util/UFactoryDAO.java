package aserradero.util;

import java.util.Hashtable;
import java.util.ResourceBundle;

public final class UFactoryDAO 
{
	public static final String CLIENTE = "CLIENTE";
	public static final String PEDIDO = "PEDIDO";
	public static final String PRODUCTO = "PRODUCTO";
	public static final String PRODUCTOS_ELIMINADOS = "PRODUCTOS_ELIMINADOS";
	public static final String DETALLEP = "DETALLEP";
	public static final String ESPECIFICACIONP = "ESPECIFICACIONP";
	public static final String PRODUCCION = "PRODUCCION";
	public static final String LOCALIDAD = "LOCALIDAD";
	
	private UFactoryDAO()
	{
		
	}
	
	private static Hashtable<String,Object> instancias = new Hashtable<>();
	
	public static Object getInstancia(String objName)
	{
		try
		{
			// verifico si existe un objeto relacionado a objName en la hashtable
			Object obj = instancias.get(objName);
			
			// si no existe entonces lo instancio y lo agrego
			if( obj == null )
			{
				ResourceBundle rb = ResourceBundle.getBundle("factoryDAO");
				String sClassname = rb.getString(objName);
				obj = Class.forName(sClassname).newInstance();
				// agrego el objeto a la hashtable
				instancias.put(objName,obj);
			}
			return obj;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
}	
