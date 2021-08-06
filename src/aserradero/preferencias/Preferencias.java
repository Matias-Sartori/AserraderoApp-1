package aserradero.preferencias;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

public class Preferencias 
{
	private static final Logger LOGGER = LogManager.getLogger(Preferencias.class);
	
	public static final String CONFIG_FILE = "config.txt";

    private String administrador;
    private String passwordAdmin;
    
    private String empleado;
    private String passwordEmpleado;

    public Preferencias() 
    {
        administrador = "administrador";
        setPasswordAdmin("admin12345");
        
        empleado = "empleado";
        setPasswordEmpleado("empleado12345");
    }

    public String getAdministrador()
    {
        return administrador;
    }

    public void setAdministrador(String admin) 
    {
        this.administrador = admin;
    }

    public String getPasswordAdmin()
    {
        return passwordAdmin;
    }

    public void setPasswordAdmin(String password)
    { 
    	this.passwordAdmin = DigestUtils.sha1Hex(password);
    }
    
    public String getEmpleado()
    {
        return empleado;
    }

    public void setEmpleado(String empleado) 
    {
        this.empleado = empleado;
    }
    
    public String getPasswordEmpleado()
    {
    	return passwordEmpleado;
    }
    
    public void setPasswordEmpleado(String password)
    {
    	this.passwordEmpleado = DigestUtils.sha1Hex(password);
    }

    public static void initConfig()
    {
        Writer writer = null;
        try 
        {
            Preferencias preference = new Preferencias();
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);
            
            LOGGER.log(Level.INFO, "Se iniciaron las preferencias por defecto");
        } 
        catch (IOException ex) 
        {
            LOGGER.log(Level.ERROR, "No se ha podido iniciar las preferencias.\nCausa: {}", ex);
        }
        finally 
        {
            try
            {
                if(writer != null) writer.close();
            } 
            catch (IOException ex)
            {
            	LOGGER.log(Level.ERROR, "No se ha podido cerrar el Writer.\nCausa: {}", ex);
            }
        }
    }

    public static Preferencias getPreferencias() 
    {
        Gson gson = new Gson();
       
        Preferencias preferencias = new Preferencias();
       
        try 
        {
            preferencias = gson.fromJson(new FileReader(CONFIG_FILE), Preferencias.class);
        }
        catch (FileNotFoundException ex) 
        {
        	LOGGER.log(Level.INFO, "No se ha encontrado el archivo de preferencias.");
            initConfig();
        }
        
        return preferencias;
    }

    public static boolean escribirPreferenciaArchivo(Preferencias preference)
    {
    	boolean ret = false;
        Writer writer = null;
        try 
        {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference, writer);

            ret = true;
            
            LOGGER.log(Level.INFO, "Se han modificado las contraseñas de usuario.");
        } 
        catch (IOException ex) 
        {
        	LOGGER.log(Level.ERROR, "No se han podido guardar las preferencias.\nCausa: {}", ex);
        } 
        finally 
        {
            try 
            {
                writer.close();
            } 
            catch (IOException ex) 
            {
            	LOGGER.log(Level.ERROR, "No se ha podido cerrar el Writer.\nCausa: {}", ex);
            }
            
        }
        
       LOGGER.log(Level.DEBUG, "Siempre llega al final");
       return ret;
    }
}
