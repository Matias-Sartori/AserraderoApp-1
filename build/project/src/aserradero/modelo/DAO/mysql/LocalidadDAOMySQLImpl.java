package aserradero.modelo.DAO.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.modelo.DAO.LocalidadDAO;
import aserradero.modelo.DTO.LocalidadDTO;
import aserradero.modelo.DTO.ProvinciaDTO;
import aserradero.modelo.conexion.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LocalidadDAOMySQLImpl implements LocalidadDAO
{
	private final static Logger LOGGER = LogManager.getLogger(LocalidadDAOMySQLImpl.class);
	
	private static final String SELECT_TODAS = "SELECT * FROM localidad INNER JOIN provincia ON provincia.idProvincia = localidad.idProvincia";
	
	Connection con;
	
	@Override
	public ObservableList<LocalidadDTO> obtenerLocalidades()
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList <LocalidadDTO> listaLocalidades = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_TODAS);
				rs = ps.executeQuery();
				while(rs.next())
				{
					LocalidadDTO localidad = new LocalidadDTO();
					localidad.setIdLocalidad(rs.getInt("idLocalidad"));
					localidad.setNombreLocalidad(rs.getString("nombreLocalidad"));
					localidad.setProvincia(new ProvinciaDTO(rs.getInt("idProvincia"), rs.getString("nombreProvincia")));
					listaLocalidades.add(localidad);
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido cargar las localidades. \nCausa: {}", e);
				return null;
			}
			finally
			{
				try
				{
					if (ps != null) ps.close();
					if (rs != null) rs.close();
				}
				catch(SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia. \nCausa: {}", e);
					return null;
				}
			}
			
			LOGGER.log(Level.DEBUG, "Localidades --> {}", listaLocalidades);
			return listaLocalidades;
		}
		
		else return null;
	}
}
