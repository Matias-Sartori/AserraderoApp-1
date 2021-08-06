package aserradero.modelo.DAO.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.modelo.DAO.EspecificacionPedidoDAO;
import aserradero.modelo.DTO.EspecificacionPedidoDTO;
import aserradero.modelo.DTO.PedidoDTO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UFactoryDAO;
import javafx.collections.ObservableList;

public class EspecificacionPedidoDAOMySQLImpl implements EspecificacionPedidoDAO
{

	private static final String INSERT = "INSERT INTO especificacion_pedido (idPedido, idProducto, unidades, pies)"
			+ " VALUES (?, ?, ?, ?)";
	
	private static final String UPDATE = "UPDATE especificacion_pedido SET unidades = ?, pies = ?"
			+ " WHERE idPedido = ? AND idProducto = ?";
	
	private static final String DELETE = "DELETE FROM especificacion_pedido WHERE idPedido = ? AND idProducto = ?";
	
	private static final String SELECT_PRODUCTO_PEDIDO = "SELECT especificacion_pedido.idProducto, especificacion_pedido.unidades, especificacion_pedido.pies, espesor, ancho, largo"
			+ " FROM especificacion_pedido INNER JOIN producto ON producto.idProducto = especificacion_pedido.idProducto"
			+ " WHERE especificacion_pedido.idPedido = ? AND especificacion_pedido.idProducto = ?";
	
	private static final Logger LOGGER = LogManager.getLogger(EspecificacionPedidoDAOMySQLImpl.class);
	
	Connection con = Conexion.tomarConexion();
	
	@Override
	public boolean insertar(EspecificacionPedidoDTO especificacionPedidoDto) 
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
				for (ProductoDTO p : especificacionPedidoDto.getProductos())
				{
					System.out.println("ID Pedido a insertar detalle especificacion: " + especificacionPedidoDto.getPedido().getIdPedido());
					System.out.println("ID Producto a insertar detalle especificacion: " + p.getIdProducto());
					ps = con.prepareStatement(INSERT);
					ps.setInt(1, especificacionPedidoDto.getPedido().getIdPedido());
					ps.setInt(2, p.getIdProducto());
					ps.setInt(3, p.getStock());
					ps.setDouble(4, p.getPies());
					if(ps.executeUpdate() == 1)
					{
						insertado = true;
						LOGGER.log(Level.INFO, "Se ha insertado un detalle de especificacion pedido. ID Pedido: {}.\n ", especificacionPedidoDto.getPedido().getIdPedido());
					}
					else break;
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha pedido insertar el detalle de especificacion de pedido.\nCausa: {}", e.getMessage());
				
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
	public boolean actualizar(EspecificacionPedidoDTO especificacionPedidoDto) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean modificado = false;
			try
			{
				for(ProductoDTO p : especificacionPedidoDto.getProductos())
				{
					ps = con.prepareStatement(UPDATE);
					ps.setInt(1, p.getStock());
					ps.setDouble(2, p.getPies());
					ps.setInt(3, especificacionPedidoDto.getPedido().getIdPedido());
					ps.setInt(4, p.getIdProducto());
					
					if(ps.executeUpdate() == 0)
					{
						modificado = false;
						break;
					}
					else 
					{
						modificado = true;
						LOGGER.log(Level.INFO, "Se ha modificado el detalle de especificacion de pedido. ID producto: {}\n ", p.getIdProducto());
					}
					
				}
				
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido actualizar el detalle de especificacion de pedido.\nCausa: {}\n", e.getMessage());
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
	public boolean eliminar(EspecificacionPedidoDTO especificacionPedidoDto) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean eliminado = false;
			try
			{
				ps = con.prepareStatement(DELETE);
				for(ProductoDTO p : especificacionPedidoDto.getProductos())
				{
					ps.setInt(1, especificacionPedidoDto.getPedido().getIdPedido());
					ps.setInt(2, p.getIdProducto());
					if(ps.executeUpdate() == 1)
					{
						eliminado = true;
						LOGGER.log(Level.INFO, "Se ha eliminado el detalle del pedido. ID: {}", especificacionPedidoDto.getPedido().getIdPedido());
					}
					else 
					{
						eliminado = false;
						break;
					}
				}
				System.out.println("DetalleEspecificacionEliminado: " + eliminado);
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
	public ObservableList<EspecificacionPedidoDTO> obtenerTodos()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EspecificacionPedidoDTO obtener(EspecificacionPedidoDTO v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EspecificacionPedidoDTO obtenerPorId(Integer idPedido) 
	{
		EspecificacionPedidoDTO especificacionPedidoDto = null;
		
		especificacionPedidoDto = crearEspecificacionPedido(idPedido);
		
		return especificacionPedidoDto;
	}

	@Override
	public ProductoDTO obtenerProductoEspecificado(int idPedido, int idProducto) 
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
	
	private EspecificacionPedidoDTO crearEspecificacionPedido(int idPedido)
	{
		System.out.println("ID PEDIDO A CREAR ESPECIFICACION --> " + idPedido);
		
		// Se crea el objeto pedido que sera parte del detalle
		PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
	
		PedidoDTO pedido = pedidoDao.obtenerPorId(idPedido);
		
		// Se obtienen los productos perteneciantes al pedido
		
		ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
		
		ObservableList<ProductoDTO> listaProductos = productoDao.obtenerProductosEspecificados(idPedido);
		
		return new EspecificacionPedidoDTO(pedido, listaProductos);
	}

}
