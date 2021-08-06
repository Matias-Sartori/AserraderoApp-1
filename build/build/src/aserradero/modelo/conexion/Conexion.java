package aserradero.modelo.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Conexion
{
	// Permiten que los referencia directamente de la clase
	private static Connection con = null;
	
	// Establecer los recursos necesarios 

	private static final String BD = "aserradero_bd";
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost/"+BD;
	private static final String USUARIO = "root";
	private static final String PASSWORD = "admin3333";
	
	
	private static final Logger LOGGER = LogManager.getLogger(Conexion.class.getName());
	
	private Conexion()
	{
	}
	
	public static Connection tomarConexion()
	{
		try
		{		
			// Si la conexion es nula, se crea una nueva
			if (con == null || con.isClosed()) 
			{
				// Determinar en que momento finaliza el programa
				// Gancho apagado es un "shutdown hook" - Listener
				Runtime.getRuntime().addShutdownHook(new GanchoApagado()); 

				// Levantamos el driver para establecer la conexion 
				Class.forName(DRIVER);
				
				con = DriverManager.getConnection(URL, USUARIO, PASSWORD);

				LOGGER.log(Level.INFO, "Conexión a [{}] establecida", BD);
		
			}
		
		}
		catch(Exception e)
		{
			LOGGER.log(Level.ERROR,"No se ha podido establecer conexión con el servidor.\nCausa: {}", e.getMessage());
			
			return null;
		}
		
		return con;
	}
	
	public static boolean comprobarConexion()
	{
		try 
		{
			//Connection c = Conexion.tomarConexion();
			//LOGGER.log(Level.DEBUG, "CONN --> {} - IS CLOSED? --> {}", c, c.isClosed());
			return con != null && !con.isClosed();
		}
		catch (SQLException e) 
		{
			LOGGER.log(Level.ERROR, "No se ha podido comprobar la conexión. \nCausa: {}", e.getMessage());
			
			return false;
		}
	}
	
	public static void cerrarConexion()
	{
		try 
		{
			if(con != null)
			{
				con.close();
				
				LOGGER.log(Level.INFO, "Conexión cerrada.");
			}
		} 
		catch (SQLException e) 
		{
			LOGGER.log(Level.ERROR,"No se ha podido cerrar la conexión.\nCausa: {}" , e.getMessage());
		}
	}
	
	// Creamos una clase de tipo "hook" o "Listener" que se conoce como Shutdown hook"
	static class GanchoApagado extends Thread
	{
		//Antes de finalizar el programa, la JVM invocara este metodo
		// El metodo va a cerrar la conexion
		@Override
		public void run()
		{
			try
			{
				Connection c = Conexion.tomarConexion();
				
				if(c != null) 
					c.close();
				
				LOGGER.log(Level.INFO, "Conexión cerrada.");
			}
			catch(Exception e)
			{
				LOGGER.log(Level.ERROR,"No se ha podido cerrar la conexión.\nCausa: " , e.getMessage());
			}
		}
	}
	
	public static boolean setAutoCommit(boolean v)
	{
		boolean ret = false;
		
		try 
		{
			Connection c = tomarConexion();
			
			if(c != null && !c.isClosed())
			{
				c.setAutoCommit(v);
				
				ret = true;
			}
				
		} 
		catch (SQLException e) 
		{
			LOGGER.log(Level.ERROR,"No se ha podido setear AutoCommit.\nCausa: " , e.getMessage());
			
			return false;
		}
		
		return ret;
	}
	
	public static boolean commit()
	{
		try 
		{
			con.commit();
			
			LOGGER.log(Level.INFO,"Commit.");
			
			return true;
		} 
		catch (SQLException e)
		{
			try 
			{
				con.rollback();
			} 
			catch (SQLException e1)
			{
				LOGGER.log(Level.ERROR,"No se ha podido realizar rollBack.\nCausa: " , e1.getMessage());
			}
			
			LOGGER.log(Level.ERROR,"No se ha podido realizar el commit.\nCausa: " , e.getMessage());

			return false;
			
		}
	}
}