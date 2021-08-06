package aserradero.modelo.DAO.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.modelo.DAO.DetallePedidoDAO;
import aserradero.modelo.DTO.DetallePedidoDTO;
import aserradero.modelo.DTO.PedidoDTO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UFactoryDAO;
import javafx.collections.ObservableList;

public class DetallePedidoDAOMySQLImpl implements DetallePedidoDAO
{
	private static final String INSERT = "INSERT INTO detalle_pedido (idPedido, idProducto, unidades, pies)"
			+ " VALUES (?, ?, ?, ?)";
	
	private static final String UPDATE = "UPDATE detalle_pedido SET unidades = ?, pies = ?"
			+ " WHERE idPedido = ? AND idProducto = ?";
	
	private static final String DELETE = "DELETE FROM detalle_pedido WHERE idPedido = ? AND idProducto = ?";
	
	private static final String SELECT_PRODUCTO_PEDIDO = "SELECT detalle_pedido.idProducto, detalle_pedido.unidades, detalle_pedido.pies, espesor, ancho, largo"
			+ " FROM detalle_pedido INNER JOIN producto ON producto.idProducto = detalle_pedido.idProducto"
			+ " WHERE detalle_pedido.idPedido = ? AND detalle_pedido.idProducto = ?";
	
	private static final Logger LOGGER = LogManager.getLogger(DetallePedidoDAOMySQLImpl.class);
	
	Connection con = Conexion.tomarConexion();
	
	@Override
	public boolean insertar(DetallePedidoDTO detallePedido)  
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean insertado = false;
		
			try
			{
				//con.setAutoCommit(false);
				// Se recore la lista de productos del detalle
				for (ProductoDTO p : detallePedido.getProductos())
				{
					System.out.println("ID Pedido a insertar detalle: " + detallePedido.getPedido().getIdPedido());
					System.out.println("ID Producto a insertar detalle: " + p.getIdProducto());
					ps = con.prepareStatement(INSERT);
					ps.setInt(1, detallePedido.getPedido().getIdPedido());
					ps.setInt(2, p.getIdProducto());
					ps.setInt(3, p.getStock());
					ps.setDouble(4, p.getPies());
					if(ps.executeUpdate() == 1)
					{
						insertado = true;
						LOGGER.log(Level.INFO, "Se ha insertado un detalle de pedido. ID Pedido: {}.", detallePedido.getPedido().getIdPedido());
					}
					else break;
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha pedido insertar el detalle de pedido.\nCausa: {}", e.getMessage());
				
				try
				{
					con.rollback();
					LOGGER.log(Level.INFO, "Se han revertidos los cambios (rollBack).");
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido revertir los cambios. \nCausa {}", e2.getMessage());
				}
				
		
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
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia. \nCausa {}", e.getMessage());
				}
			}
			
			return insertado;
		}
		else return false;
	}

	@Override
	public boolean actualizar(DetallePedidoDTO detallePedidoDto)  
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean modificado = false;
			try
			{
				for(ProductoDTO p : detallePedidoDto.getProductos())
				{
					ps = con.prepareStatement(UPDATE);
					ps.setInt(1, p.getStock());
					ps.setDouble(2, p.getPies());
					ps.setInt(3, detallePedidoDto.getPedido().getIdPedido());
					ps.setInt(4, p.getIdProducto());
					
					if(ps.executeUpdate() == 0)
					{
						modificado = false;
						break;
					}
					else 
					{
						modificado = true;
						LOGGER.log(Level.INFO, "Se ha modificado el detalle pedido. ID producto: {}", p.getIdProducto());
					}
					
				}
				
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido actualizar el detalle de pedido.\nCausa: {}", e.getMessage());
				try
				{
					con.rollback();
					LOGGER.log(Level.ERROR, "Se han revertido los cambios (rollBack).");
					return false;
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido revertir los cambios.\nCausa: {}", e.getMessage());
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
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa: {}", e.getMessage());
				}
			}
			return modificado;
		}
		else return false;
		
	}

	@Override
	public boolean eliminar(DetallePedidoDTO detallePedido)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean eliminado = false;
			try
			{
				ps = con.prepareStatement(DELETE);
				for(ProductoDTO p : detallePedido.getProductos())
				{
					ps.setInt(1, detallePedido.getPedido().getIdPedido());
					ps.setInt(2, p.getIdProducto());
					if(ps.executeUpdate() == 1)
					{
						eliminado = true;
						LOGGER.log(Level.INFO, "Se ha eliminado el detalle del pedido. ID: {}", detallePedido.getPedido().getIdPedido());
					}
					else 
					{
						eliminado = false;
						break;
					}
				}
				System.out.println("DetalleEliminado: " + eliminado);
			}
			catch(SQLException e)
			{
				try
				{
					LOGGER.log(Level.ERROR, "No se ha podido eliminar el detalle del pedido\nCausa {}", e.getMessage());
					con.rollback();
					LOGGER.log(Level.INFO, "Se han revertido los cambios (rollBack).");
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se ha podido revertir los cambios\nCausa {}", e2.getMessage());
				}
				
				return false;
			}
			
			return eliminado;
		}
		else return false;
		
	}

	@Override
	public ObservableList<DetallePedidoDTO> obtenerTodos() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DetallePedidoDTO obtenerPorId(Integer idPedido)
	{
		DetallePedidoDTO detallePedido = null;
		
		detallePedido = crearDetallePedido(idPedido);
		/*try
		{
			
			
		}
		catch (DAOException e)
		{
			UAlertas.alerta("No se ha podido obtener el detalle del pedido.", e.getMessage());
			LOGGER.log(Level.SEVERE, "No se ha podido mostrar el detalle del pedido.", UFecha.formatearFecha(LocalDate.now(), UFecha.DD_MM_YYYY));
			return null;
		}*/
		
		return detallePedido;
	}
	
	@Override
	public ProductoDTO obtenerProductoPedido(int idPedido, int idProducto)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ProductoDTO productoObtenido = new ProductoDTO();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCTO_PEDIDO);
				ps.setInt(1, idPedido);
				ps.setInt(2, idProducto);
				rs = ps.executeQuery();
				if(rs.next())
				{
					productoObtenido.setIdProducto(rs.getInt("idProducto"));
					productoObtenido.setEspesor(rs.getDouble("espesor"));
					productoObtenido.setAncho(rs.getDouble("ancho"));
					productoObtenido.setLargo(rs.getDouble("largo"));
					productoObtenido.setStock(rs.getInt("unidades"));
					productoObtenido.setPies(rs.getDouble("pies"));
				}
				
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener el producto.\nCausa {}", e.getMessage());

				return null;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
					if(rs != null) rs.close();
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa {}", e2.getMessage());
				}
			}
			
			return productoObtenido;
		}
		else return null;
		
	}
	
	

	@Override
	public DetallePedidoDTO obtener(DetallePedidoDTO v) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private DetallePedidoDTO crearDetallePedido(int idPedido)
	{
		// Se crea el objeto pedido que sera parte del detalle
		PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
	
		PedidoDTO pedido = pedidoDao.obtenerPorId(idPedido);
		
		// Se obtienen los productos perteneciantes al pedido
		
		ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
		
		ObservableList<ProductoDTO> listaProductos = productoDao.obtenerProductosPedido(idPedido);
		
		return new DetallePedidoDTO(pedido, listaProductos);
	}

	
}
