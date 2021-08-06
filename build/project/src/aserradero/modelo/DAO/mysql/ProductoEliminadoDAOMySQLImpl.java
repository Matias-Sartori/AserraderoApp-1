package aserradero.modelo.DAO.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.modelo.DAO.ProductoEliminadoDAO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.DTO.ProductoEliminadoDTO;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UFactoryDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductoEliminadoDAOMySQLImpl implements ProductoEliminadoDAO
{
	
	private static final String INSERT = "INSERT INTO productos_eliminados (idProducto, motivo, fechaEliminacion)"
			+ " VALUES (?, ?, ?)";
	
	/*private static final String UPDATE = "UPDATE pedido SET idCliente = ?, fechaToma = ?, fechaEntrega = ?, horaToma = ?, horaEntrega = ?, proposito = ?, estado = ?, forma = ?, fechaModificacion = ?"
			+ " WHERE idPedido = ?";*/
	
	private static final String DELETE = "DELETE FROM pedido WHERE idPedido = ?";
	
	private static final String SELECT = "SELECT idProductoEliminado AS idProdEli, idProducto AS idProd, motivo, fechaEliminacion"
			+ " FROM productos_eliminados WHERE idProductoEliminado = ?";
	
	private static final String SELECT_TODOS = "SELECT idProductoEliminado AS idProdEli, idProducto AS idProd, motivo, fechaEliminacion"
			+ " FROM productos_eliminados";
			
	private Connection con;
	
	private static final Logger LOGGER = LogManager.getLogger(ProductoEliminadoDAOMySQLImpl.class);

	@Override
	public boolean insertar(ProductoEliminadoDTO productoEliminado) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			boolean insertado = false;
			try
			{
				ps = con.prepareStatement(INSERT);
				

				ps.setInt(1, productoEliminado.getProducto().getIdProducto());
				ps.setString(2, productoEliminado.getMotivo());
				ps.setTimestamp(3, Timestamp.valueOf(productoEliminado.getFechaModificacion()));

				int res = ps.executeUpdate();
				if(res == 1)
				{
					// Se obtiene el ultimo id insertado
					rs = ps.getGeneratedKeys();
					if (rs.next())
					{
						int idProductoEliminado = rs.getInt(1);
						productoEliminado.setIdProductoEliminado(idProductoEliminado);
						insertado = true;
						LOGGER.log(Level.INFO, "Producto eliminado registrado. ID = [{}]", idProductoEliminado);
					}
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido registrar el producto eliminado.\nCausa: {}", e.getMessage());
				return false;
			}
			finally
			{
				try
				{
					if (ps != null) ps.close();
				}
				catch(SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia\nCausa: {}", e.getMessage());
					return false;
				}
			}
			return insertado;
		}
		
		else return false;
		
	}

	@Override
	public boolean actualizar(ProductoEliminadoDTO productoEliminado)
	{
		/*con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean modificado = false;
			
			try
			{
				ps = con.prepareStatement(UPDATE);
				if(productoEliminado.getCliente() != null && productoEliminado.getCliente().getIdCliente() > 0)
					ps.setInt(1, productoEliminado.getCliente().getIdCliente());
				else ps.setNull(1, java.sql.Types.INTEGER);
				
				ps.setDate(2, java.sql.Date.valueOf(productoEliminado.getFechaToma()));
				ps.setDate(3, java.sql.Date.valueOf(productoEliminado.getFechaEntrega()));
				ps.setTime(4, java.sql.Time.valueOf(productoEliminado.getHoraToma()));
				ps.setTime(5, java.sql.Time.valueOf(productoEliminado.getHoraEntrega()));
				ps.setString(6, productoEliminado.getProposito());
				ps.setInt(7, UPedido.estadoInt(productoEliminado.getEstado()));
				ps.setString(8, productoEliminado.getForma());
				
				// si el año es distinto a 9999, se coloca el año, sino, se coloca nulo
				if(productoEliminado.getFechaModificacion().getYear() != 9999)
					ps.setTimestamp(9, Timestamp.valueOf(productoEliminado.getFechaModificacion()));
				else ps.setNull(9, java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
				
				ps.setInt(10, productoEliminado.getIdPedido());
				
				if(ps.executeUpdate() == 1)
				{
					modificado = true;
					LOGGER.log(Level.INFO, "Se ha actualizado el pedido. ID = [{}]", productoEliminado.getIdPedido());
				}
			}
			catch(SQLException e)
			{
				try
				{
					LOGGER.log(Level.ERROR, "No se ha podido actualizar el pedido.\nCausa: {}", e.getMessage());
					
					con.rollback();
					
					LOGGER.log(Level.INFO, "Se han revertido los cambios (rollBack).");
					
					return false;
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido revertir los cambios (rollBack).\nCausa: {}", e2.getMessage());
					return false;
				}				
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
				}
				catch(SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e.getMessage());
					return false;
				}
			}	
			
			return modificado;
		}
		
		else return false;*/
		
		return false;
	}

	@Override
	public boolean eliminar(ProductoEliminadoDTO productoEliminado)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			System.out.println("Entra a eliminar el producto eliminado");
			PreparedStatement ps = null;
			boolean eliminado = false;
			
			try
			{
				ps = con.prepareStatement(DELETE);
				ps.setInt(1, productoEliminado.getIdProductoEliminado());
				int res = ps.executeUpdate();
				
				if(res == 1)
				{
					eliminado = true;
					LOGGER.log(Level.INFO, "Se ha eliminado el producto eliminado. ID [{}]", productoEliminado.getIdProductoEliminado());
					
					// restamos el auto-incremento
					//descontarAutoIncremento("pedido");
				}
			}
			catch(SQLException e)
			{
				try
				{
					LOGGER.log(Level.ERROR, "No se ha podido eliminar el producto eliminado\nCausa {}", e.getMessage());
					con.rollback();
					LOGGER.log(Level.INFO, "Se han revertido los cambios (rollBack).");
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se ha podido revertir los cambios\nCausa {}", e2.getMessage());
				}
				
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
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}" , e.getMessage());
				}
			}
			
			return eliminado;
		}
		
		else return false;
	}

	@Override
	public ObservableList<ProductoEliminadoDTO> obtenerTodos()
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;		
			ObservableList <ProductoEliminadoDTO> listaProductosEliminados = FXCollections.observableArrayList();
			try
			{
				ps = con.prepareStatement(SELECT_TODOS);
				rs = ps.executeQuery();
				while (rs.next())
				{				
					listaProductosEliminados.add(convertirResultSet(rs));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtener los productos eliminados.\nCausa: {}", e.getMessage());
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
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa: {}", e.getMessage());
					return null;
				}
			}
			
			
			return listaProductosEliminados;
		}
		
		else return null;
		
		
	}

	@Override
	public ProductoEliminadoDTO obtenerPorId(Integer id)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ProductoEliminadoDTO pedido = null;
			
			try
			{
				ps = con.prepareStatement(SELECT);
				ps.setInt(1, id);
				rs = ps.executeQuery();
				if(rs.next())
				{
					pedido = convertirResultSet(rs);
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener el producto eliminado.\nCausa: {} ", e.getMessage());
				return null;
			}
			finally
			{
				try
				{
					if(rs != null) ps.close();
					if(rs != null) rs.close();
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {} ", e2.getMessage());
					return null;
				}
			}
			
			return pedido;
		}
		
		else return null;
		
	}
	
	private ProductoEliminadoDTO convertirResultSet(ResultSet rs) throws SQLException
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			ProductoEliminadoDTO productoEliminado = null;
			try 
			{
				int idProductoEliminado = rs.getInt("idProdEli");
				int idProducto = rs.getInt("idProd");
				
				// Se obtiene el cliente perteneciente al peddido
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				ProductoDTO producto = productoDao.obtenerPorId(idProducto);
				producto.setStock(1);
				producto.setPies();
				String motivo = rs.getString("motivo");
				LocalDateTime fechaE = rs.getTimestamp("fechaEliminacion").toLocalDateTime();
				
				productoEliminado = new ProductoEliminadoDTO(idProductoEliminado, producto, motivo, fechaE);
			} 
			catch (Exception e) 
			{
				LOGGER.log(Level.ERROR, "Error al convertir ResultSet.\nCausa: {} ", e.getMessage());
				
				return null;
			}
					
			return productoEliminado;
		}
		
		else return null;
	}

	@Override
	public ProductoEliminadoDTO obtener(ProductoEliminadoDTO v) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
