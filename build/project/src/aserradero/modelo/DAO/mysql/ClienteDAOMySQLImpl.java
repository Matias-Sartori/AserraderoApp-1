package aserradero.modelo.DAO.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.modelo.DAO.ClienteDAO;
import aserradero.modelo.DTO.ClienteDTO;
import aserradero.modelo.DTO.ProvinciaDTO;
import aserradero.modelo.conexion.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ClienteDAOMySQLImpl implements ClienteDAO
{
	
	private final String INSERT = "INSERT INTO cliente (nombre, apellido, telefono, dni, cuit, razonSocial, direccion, eMail, idLocalidad, habilitado)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private final String UPDATE = "UPDATE cliente SET nombre = ?, apellido = ?, dni = ?, cuit = ?, razonSocial = ?,"
			+ " telefono = ?, direccion = ?, eMail = ?, idLocalidad = ?, habilitado = ?"
			+ " WHERE idCliente = ?";
	
	private final String DELETE = "DELETE FROM cliente WHERE idCliente = ?";
	
	private final String SELECT_TODOS = "SELECT cliente.idCliente AS idClie, nombre, apellido, dni, cuit, razonSocial,"
			+ " direccion, eMail, nombreLocalidad, telefono, cliente.idLocalidad AS idLoc, localidad.idProvincia"
			+ " AS idProv, nombreProvincia, habilitado FROM cliente "
			+ " LEFT JOIN localidad ON localidad.idLocalidad = cliente.idLocalidad"
			+ " LEFT JOIN provincia ON provincia.idProvincia = localidad.idProvincia";
	
	private final String SELECT = "SELECT cliente.idCliente AS idClie, nombre, apellido, telefono, dni, cuit, razonSocial, direccion, eMail, cliente.idLocalidad AS idLoc"
			+ ", nombreLocalidad, localidad.idProvincia AS idProv, nombreProvincia, habilitado FROM cliente"
			+ " LEFT JOIN localidad ON localidad.idLocalidad = cliente.idLocalidad"
			+ " LEFT JOIN provincia ON provincia.idProvincia = localidad.idProvincia"
			+ " WHERE cliente.idCliente = ?";
	
	private final String SELECT_HABILITADOS = "SELECT cliente.idCliente AS idClie, nombre, apellido, telefono, dni, cuit, razonSocial, direccion, eMail, cliente.idLocalidad AS idLoc"
			+ ", nombreLocalidad, localidad.idProvincia AS idProv, nombreProvincia, habilitado FROM cliente"
			+ " LEFT JOIN localidad ON localidad.idLocalidad = cliente.idLocalidad"
			+ " LEFT JOIN provincia ON provincia.idProvincia = localidad.idProvincia"
			+ " WHERE habilitado = true";
	
	private static final Logger LOGGER = LogManager.getLogger(ClienteDAOMySQLImpl.class);
	
	Connection con;

	@Override
	public boolean insertar(ClienteDTO cliente)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			ResultSet rs = null;
			boolean insertado = false;

			try
			{
				con.setAutoCommit(false);
				ps = con.prepareStatement(INSERT);
				ps.setString(1, cliente.getNombre());
				ps.setString(2, cliente.getApellido());
				ps.setString(3, cliente.getTelefono());
				ps.setString(4, cliente.getDni());
				ps.setString(5, cliente.getCuit());
				ps.setString(6, cliente.getRazonSocial());
				ps.setString(7, cliente.getDireccion());
				ps.setBoolean(10, cliente.getHabilitado());
				
				if(!cliente.getEMail().isEmpty())
					ps.setString(8, cliente.getEMail());
				else ps.setNull(8, java.sql.Types.VARCHAR);
				
				if(cliente.getLocalidad().getIdLocalidad() != 0)
					ps.setInt(9, cliente.getLocalidad().getIdLocalidad());
				else ps.setNull(9, java.sql.Types.INTEGER);
				
				int res = ps.executeUpdate();
				if(res == 1)
				{
					rs = ps.getGeneratedKeys();
					if(rs.next())
					{
						cliente.setIdCliente(rs.getInt(1));
						insertado = true;	
						LOGGER.log(Level.INFO, "Se ha dado de alta al cliente. ID = [{}]", cliente.getIdCliente());
					}
					else insertado = false;	
				}
			}
			catch(SQLException e)
			{
				try
				{
					con.rollback();
					LOGGER.log(Level.ERROR, "No se ha podido dar de alta al cliente. Se han revertido los cambios (rollBack)\nCausa: {}", e.getMessage());
					return false;
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido Revertir los cambios.\nCausa: {}", e2.getMessage());
					return false;
				}

			}
			finally
			{
				try
				{
					if (ps != null) ps.close();
					if (ps2 != null) ps2.close();
					if (rs != null) rs.close();
				}
				catch(SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa: {} ", e.getMessage());
					return false;
				}
			}
			
			return insertado;
		}
		
		else return false;
	}

	@Override
	public boolean actualizar(ClienteDTO clienteDto)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean modificado = false;
			
			try
			{
				ps = con.prepareStatement(UPDATE);
				ps.setString(1, clienteDto.getNombre());
				ps.setString(2, clienteDto.getApellido());
				ps.setString(3, clienteDto.getDni());
				ps.setString(4, clienteDto.getCuit());
				ps.setString(5, clienteDto.getRazonSocial());
				ps.setString(6, clienteDto.getTelefono());
				ps.setString(7, clienteDto.getDireccion());
				ps.setBoolean(10, clienteDto.getHabilitado());
				ps.setInt(11, clienteDto.getIdCliente());
				
				if(!clienteDto.getEMail().isEmpty())
					ps.setString(8, clienteDto.getEMail());
				else ps.setNull(8, java.sql.Types.VARCHAR);
				
				// Idem 
				if(clienteDto.getLocalidad().getIdLocalidad() != 0)
					ps.setInt(9, clienteDto.getLocalidad().getIdLocalidad());
				else ps.setNull(9, java.sql.Types.INTEGER);
				
				int res = ps.executeUpdate();
				if (res == 1)
				{
					modificado = true;
					LOGGER.log(Level.INFO, "Se ha modificado el cliente. ID = [{}]", clienteDto.getIdCliente());

				}
			}
			catch (SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido modificar el cliente.\nCausa: {}", e.getMessage());
				return false;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
				}
				catch(SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa: {}", e.getMessage());
					return false;
				}
			}
			
			return modificado;
		}
		
		else return false;
	}

	@Override
	public boolean eliminar(ClienteDTO c)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean eliminado = false;
			try
			{
				ps = con.prepareStatement(DELETE);
				ps.setInt(1, c.getIdCliente());
				if(ps.executeUpdate() == 1)
				{
					eliminado = true;
					LOGGER.log(Level.INFO, "Se ha eliminado al cliente. ID = [{}]", c.getIdCliente());
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido eliminar al cliente.\nCausa: {}", e.getMessage());
				return false;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa: {}", e2.getMessage());
					return false;
				}
			}
			
			return eliminado;
		}
		
		else return false;
		
	}

	@Override
	public ObservableList<ClienteDTO> obtenerTodos()
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			// Conexion, etc.
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ClienteDTO> listaClientes = FXCollections.observableArrayList();
			
			try 
			{
				ps = con.prepareStatement(SELECT_TODOS);
				rs = ps.executeQuery();
				while (rs.next())
				{
					listaClientes.add(convertirResultSet(rs));
				}
			} 
			catch (SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido cargar los clientes.\nCausa: {} ", e.getMessage());
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
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa: {} ", e.getMessage());
					return null;
				}
				
			}
			
			return listaClientes;
		}
		
		else return null;
	}

	@Override
	public ClienteDTO obtenerPorId(Integer idCliente)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ClienteDTO cliente = null;
			
			try
			{
				ps = con.prepareStatement(SELECT);
				ps.setInt(1, idCliente);
				rs = ps.executeQuery();
				if(rs.next())
				{
					cliente = convertirResultSet(rs);
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener el cliente.\nCausa: {} ", e.getMessage());
				
				return null;
			}
			finally 
			{
				try
				{
					if (ps != null) ps.close();
					if (rs != null) rs.close();
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {} ", e2.getMessage());
					
					return null;
				}
			}
			
			return cliente;
		}
		
		else return null;
	}
	
	@Override
	public ObservableList<ClienteDTO> obtenerActivos()
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ClienteDTO> listaClientes = FXCollections.observableArrayList();
			try
			{
				ps = con.prepareStatement(SELECT_HABILITADOS);
				rs = ps.executeQuery();
				while(rs.next())
				{
					listaClientes.add(convertirResultSet(rs));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtener los clientes.\nCausa: {} ", e.getMessage());

				return null;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
					if(rs != null) rs.close();
				}
				catch(SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {} ", e.getMessage());

					return null;
				}
			}
			return listaClientes;
		}
		
		else return null;
		
	}
	
	private ClienteDTO convertirResultSet(ResultSet rs)
	{	
		ClienteDTO cliente = null;
		
		try 
		{	
			int idCliente = rs.getInt("idClie");
			String nombre = rs.getString("nombre");
			String apellido = rs.getString("apellido");
			String telefono = rs.getString("telefono");
			String dni = rs.getString("dni");
			String cuit = rs.getString("cuit");
			String razonSocial = rs.getString("razonSocial");
			String direccion = rs.getString("direccion");
			String em = rs.getString("eMail") != null ? rs.getString("eMail") : "";
			boolean activo = rs.getBoolean("habilitado");
			
			cliente = new ClienteDTO(idCliente, nombre, apellido, telefono, dni, cuit, razonSocial, direccion, em, activo);
			// ARRELAR ESTO MEJOR. Si el campo localidad no es nulo
			if (rs.getInt("idLoc") != 0)
			{
				cliente.getLocalidad().setIdLocalidad(rs.getInt("idLoc"));
				cliente.getLocalidad().setNombreLocalidad(rs.getString("nombreLocalidad"));
				cliente.getLocalidad().setProvincia(new ProvinciaDTO(rs.getInt("idProv"), rs.getString("nombreProvincia")));
			}
		} 
		catch (SQLException e) 
		{
			LOGGER.log(Level.ERROR, "No se ha podido convertir el ResultSet.\nCausa: {} ", e.getMessage());

			return null;
		}
		
		return cliente;

	}

	@Override
	public ClienteDTO obtener(ClienteDTO v) {
		// TODO Auto-generated method stub
		return null;
	}
}
