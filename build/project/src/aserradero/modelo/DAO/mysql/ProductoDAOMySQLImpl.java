package aserradero.modelo.DAO.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.modelo.DAO.ProductoDAO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.clases.VentasAnio;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UAlertas;
import aserradero.util.UProducto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductoDAOMySQLImpl implements ProductoDAO
{	
		
	private static final String INSERT = "INSERT INTO producto (espesor, ancho, largo, pies, stock, reserva, carga)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	private static final String UPDATE = "UPDATE producto SET espesor = ?, ancho = ?, largo = ?, pies = ?, stock = ?, reserva = ?, carga = ?"
			+ " WHERE idProducto = ?";
	
	private static final String DELETE = "DELETE FROM producto WHERE idProducto = ?";
	
	private static final String SELECT_TODOS = "SELECT idProducto AS idProd, espesor, ancho, largo, pies AS p, stock AS uni, reserva, carga"
			+ " FROM producto WHERE (stock - reserva) > 0 ORDER BY espesor, ancho ASC";
	
	private static final String SELECT = "SELECT idProducto AS idProd, espesor, ancho, largo, pies AS p, stock AS uni, reserva, carga FROM producto"
			+ " WHERE idProducto = ? ORDER BY espesor, ancho ASC";
	
	private static final String SELECT_PRODUCTOS_PEDIDO = "SELECT producto.idProducto AS idProd, espesor, ancho, largo, reserva, carga,"
			+ " detalle_pedido.unidades AS uni, detalle_pedido.pies AS p"
			+ " FROM detalle_pedido"
			+ " INNER JOIN pedido ON pedido.idPedido = detalle_pedido.idPedido"
			+ " INNER JOIN producto ON producto.idProducto = detalle_pedido.idProducto"
			+ " WHERE detalle_pedido.idPedido = ?";
	
	private static final String SELECT_PRODUCTOS_ESPECIFICADOS = "SELECT producto.idProducto AS idProd, espesor, ancho, largo, reserva, carga,"
			+ " especificacion_pedido.unidades AS uni, especificacion_pedido.pies AS p"
			+ " FROM especificacion_pedido"
			+ " INNER JOIN pedido ON pedido.idPedido = especificacion_pedido.idPedido"
			+ " INNER JOIN producto ON producto.idProducto = especificacion_pedido.idProducto"
			+ " WHERE especificacion_pedido.idPedido = ?";

	private static final String SELECT_PRODUCTOS_PRODUCCION = "SELECT produccion.idProducto AS idProd, espesor, ancho, largo, count(produccion.idProducto)"
			+ " AS productos FROM produccion"
			+ " INNER JOIN producto ON producto.idProducto = produccion.idProducto"
			+ " WHERE idPedido = ? GROUP BY produccion.idProducto";
	
	private final String SELECT_PRODUCTO_EXISTENTE = "SELECT idProducto AS idProd, espesor, ancho, largo, pies AS p, stock AS uni, reserva, carga"
			+ " FROM producto WHERE espesor = ? AND ancho = ? AND largo = ?";
	
	private static final String SELECT_DISTINCT = "SELECT DISTINCT espesor, ancho FROM producto"
			+ " WHERE (stock - reserva) > 0 ORDER BY espesor ASC, ancho ASC, largo ASC";
	
	private static final String SELECT_DISTINCT_ANIOS = "SELECT DISTINCT YEAR(fecha), ancho FROM producto"
			+ " WHERE (stock - reserva) > 0 ORDER BY espesor ASC, ancho ASC, largo ASC";
	
	private static final String SELECT_SUM = "SELECT *, SUM(largo * (stock - reserva)) AS suma, SUM(stock - reserva) AS uni FROM producto"
			+ " WHERE espesor = ? AND ancho = ? AND (stock - reserva) > 0";
	
	private static final String SELECT_ESPESORES_ANCHOS = "SELECT producto.idProducto, espesor, ancho, largo, stock, reserva, carga FROM producto"
			+ " WHERE espesor = ? AND ancho = ?"
			+ " ORDER BY espesor, ancho ASC";
	
	private static final String SELECT_DISTINCT_PEDIDO = "SELECT DISTINCT espesor, ancho FROM producto"
			+ " INNER JOIN detalle_pedido ON detalle_pedido.idProducto = producto.idProducto"
			+ " WHERE detalle_pedido.idPedido = ?"
			+ " ORDER BY espesor, ancho ASC";
	
	private static final String SELECT_SUM_PEDIDO = "SELECT *, SUM(largo * (detalle_pedido.unidades)) AS suma, SUM(detalle_pedido.unidades) AS uni"
			+ " FROM producto"
			+ " INNER JOIN detalle_pedido ON detalle_pedido.idProducto = producto.idProducto"
			+ " WHERE espesor = ? AND ancho = ? AND detalle_pedido.idPedido = ?";
	
	private static final Logger LOGGER = LogManager.getLogger(ProductoDAOMySQLImpl.class);
	
	private Connection con;
	
	/**
	 * Método que obtiene todos los productos de la base de datosReporte, agrupados por espesor y ancho.
	 * @return Lista tipo ItemStock con los productos obtenidos.
	 */
	public ObservableList <ProductoDTO> obtenerStockClasificado()		// <------------- CONSULTAR STOCK
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			ResultSet rs = null;
			ResultSet rs2 = null;
			ObservableList <ProductoDTO> listaProductos = FXCollections.observableArrayList();
			
			try
			{
				// Selecciona las distintas combinaciones de espesor y ancho
				ps = con.prepareStatement(SELECT_DISTINCT);
				rs = ps.executeQuery();
				while(rs.next())
				{
					// Selecciona la suma de todos los largos de la combinacion de espesor y ancho
					ps2 = con.prepareStatement(SELECT_SUM);
					ps2.setDouble(1, rs.getDouble("espesor"));
					ps2.setDouble(2, rs.getDouble("ancho"));
					
					rs2 = ps2.executeQuery();
					while(rs2.next())
					{
						ProductoDTO productoDto = new ProductoDTO();
						productoDto.setEspesor(rs2.getDouble("espesor"));
						productoDto.setAncho(rs2.getDouble("ancho"));
						productoDto.setLargo(UProducto.formatearMedida(rs2.getDouble("suma"), UProducto.DOS_DECIMALES));
						productoDto.setStock(rs2.getInt("uni"));
						
						productoDto.setPiesClasificado();
						
						listaProductos.add(productoDto);
					}
				}
				
			}
			catch (SQLException e) 
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener el stock clasificado.\nCausa: {}", e.getMessage());
				
				return null;
			}
			
			finally
			{
				try
				{
					if (ps != null) ps.close();
					if (ps2 != null) ps2.close();
					if (rs != null) rs.close();
					if (rs2 != null) rs2.close();
				}
				catch(SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e.getMessage());
				}
			}	
			
			return listaProductos;
		}
		else return null;
	} 
	
	/**
	 * Método que se encarga de obtener productos de un espesor y ancho pasado como parámetro.
	 * @param item - Objeto a consultar.
	 * @return Lista con los productos obtenidos.
	 */
	public ObservableList <ProductoDTO> obtenerUnidadesClasificadas(ProductoDTO item)		// <------------- MOSTRAR CANTIDADES
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList <ProductoDTO> listaProductos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_ESPESORES_ANCHOS);
				ps.setDouble(1, item.getEspesor());
				ps.setDouble(2, item.getAncho());
				rs = ps.executeQuery();
				while(rs.next())
				{
					for (int i = 0; i < rs.getInt("stock") - rs.getInt("reserva"); i++)
					{
						ProductoDTO productoDto = new ProductoDTO();
						productoDto.setIdProducto(rs.getInt("idProducto"));
						productoDto.setEspesor(rs.getDouble("espesor"));
						productoDto.setAncho(rs.getDouble("ancho"));
						productoDto.setLargo(rs.getDouble("largo"));
						productoDto.setStock(1);
						productoDto.setPies();
						productoDto.setReserva(rs.getInt("reserva"));
						productoDto.setCarga(rs.getInt("carga"));
						
						listaProductos.add(productoDto);
					}
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido cargas las unidades.\nCausa: {}", e);
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
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e.getMessage());
					return null;
				}
			}
			return listaProductos;
		}
		
		else return null;
		
		
	}

	public int calcularExistencia(ProductoDTO item) 
	{
		con = Conexion.tomarConexion();
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		int unidades = 0;
		double pies = 0d;
		int existencia = 3;
		
		final String SELECT = "SELECT idProducto, espesor, ancho, largo, tipo, pies, stock, reserva FROM producto"
				+ " WHERE espesor = ? AND ancho = ? AND largo = ? AND (stock - reserva) > 0";
		
		try 
		{
			ps = con.prepareStatement(SELECT);
			ps.setDouble(1, item.getEspesor());
			ps.setDouble(2, item.getAncho());
			ps.setDouble(3, item.getLargo());
			
			rs = ps.executeQuery();
			if(rs.next())
			{
				unidades = rs.getInt("stock") - rs.getInt("reserva");
				//pies = UCalculos.calcularPiesCuadrados(rs.getDouble("espesor"), rs.getDouble("ancho"), rs.getDouble("largo"), unidades);
				if(pies >= item.getPies())
				{
					existencia = 1;
				}
				else existencia = 0;
				
			}
			else existencia = -1;
		} 
		catch (SQLException e) 
		{
			UAlertas.alertaExcepcion("No se ha podido calcular existencia de stock", e);
		}
		finally
		{
			try
			{
				if (ps != null) ps.close();
			}
			catch(SQLException e2)
			{
				e2.printStackTrace();
			}
		}
		return existencia;
	}

	public void calcularReserva(ProductoDTO item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean insertar(ProductoDTO producto)
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
				ps.setDouble(1, producto.getEspesor());
				ps.setDouble(2, producto.getAncho());
				ps.setDouble(3, producto.getLargo());
				ps.setDouble(4, producto.getPies());
				ps.setInt(5, producto.getStock());
				ps.setInt(6, producto.getReserva());
				ps.setInt(7, producto.getCarga());
				int res = ps.executeUpdate();
				if(res == 1)
				{
					// Se obtiene el producto insertado reciente
					rs = ps.getGeneratedKeys();
					if (rs.next())
					{
						// Se setea el id del producto insertado recientemente
						producto.setIdProducto(rs.getInt(1));
						insertado = true;
					}
				}
				
				LOGGER.log(Level.INFO, "Se ha dado de alta a un nuevo producto. ID: {}", producto.getIdProducto());
			}
			catch(SQLException e)
			{
				try
				{
					LOGGER.log(Level.ERROR, "No se ha podido insertar el producto.\nCausa: {}", e.getMessage());
					
					con.rollback();
					
					LOGGER.log(Level.INFO, "Se han revertido los cambios (rollBack).");
					
					return false;
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido revertir los cambios.\nCausa: {}", e.getMessage());
					return false;
				}
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
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e2.getMessage());
					return false;
				}
			}
			
			return insertado;
		}
		
		else return false;
		
	}	

	@Override
	public boolean actualizar(ProductoDTO producto)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean modificado = false;
			try
			{
				ps = con.prepareStatement(UPDATE);
				ps.setDouble(1, producto.getEspesor());
				ps.setDouble(2, producto.getAncho());
				ps.setDouble(3, producto.getLargo());
				ps.setDouble(4, producto.getPies());
				ps.setInt(5, producto.getStock());
				ps.setInt(6, producto.getReserva());
				ps.setInt(7, producto.getCarga());
				ps.setInt(8, producto.getIdProducto());
				if(ps.executeUpdate() == 1)
				{
					modificado = true;
				}
				
				LOGGER.log(Level.INFO, "Se ha actualizado el producto. ID: {}", producto.getIdProducto());
			}
			catch(SQLException e)
			{
				try
				{
					LOGGER.log(Level.ERROR, "No se ha podido modificar el producto.\nCausa: {}", e.getMessage());
					
					con.rollback();
					
					LOGGER.log(Level.INFO, "Se han revertido los cambios (rollBack).");
					
					return false;
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han revertir los cambios (rollBack).\nCausa: {}", e2.getMessage());
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
		
		else return false;
		
	}

	@Override
	public boolean eliminar(ProductoDTO producto)  
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean eliminado = false;
			try
			{
				ps = con.prepareStatement(DELETE);
				ps.setInt(1, producto.getIdProducto());
				if(ps.executeUpdate() == 1)
				{
					eliminado = true;
					LOGGER.log(Level.INFO, "Se ha eliminado el producto. ID: {}", producto.getIdProducto());
				}
					
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido eliminar el producto.\nCausa: {}" , e.getMessage());
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
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: " , e2.getMessage());
					return false;
				}
			}
			return eliminado;
		}
		
		else return false;
		
	}

	@Override
	public ObservableList<ProductoDTO> obtenerTodos()   
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProductoDTO> listaProductos = FXCollections.observableArrayList();
			try
			{
				ps = con.prepareStatement(SELECT_TODOS);
				rs = ps.executeQuery();
				while(rs.next())
				{
					listaProductos.add(convertirResultSet2(rs));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener el stock.\nCausa: {}", e.getMessage());

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
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e2.getMessage());

					return null;
				}
		
			}
			
			return listaProductos;
		}
		
		else return null;
		
	}

	@Override
	public ProductoDTO obtenerPorId(Integer id)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ProductoDTO producto = null;
			
			try
			{
				ps = con.prepareStatement(SELECT);
				ps.setInt(1, id);
				rs = ps.executeQuery();
				if(rs.next())
				{
					producto = convertirResultSet(rs);
				}
			}
			catch(SQLException e)
			{
				try
				{
					LOGGER.log(Level.ERROR, "No se ha podido obtener el producto.\nCausa: {}", e.getMessage());
					
					con.rollback();
					
					LOGGER.log(Level.INFO, "Se han revertido los cambios (rollBack).");
					
					return null;
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido revertir los cambios.\nCausa: {}", e2.getMessage());
					return null;
				}
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
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e2.getMessage());
					return null;
				}
			}
			return producto;
		}
		
		else return null;
		
	}
	
	private ProductoDTO convertirResultSet(ResultSet rs) throws SQLException
	{
		int idProducto = rs.getInt("idProd");
		double espesor = rs.getDouble("espesor");
		double ancho = rs.getDouble("ancho");
		double largo = UProducto.formatearMedida(rs.getDouble("largo"), UProducto.UN_DECIMAL);
		int stock = rs.getInt("uni");
		double piesPorUnidad = rs.getDouble("p");
		int reserva = rs.getInt("reserva");
		int carga = rs.getInt("carga");
		
		ProductoDTO productoConvertido = new ProductoDTO(idProducto, espesor, ancho, largo, piesPorUnidad, stock, reserva, carga);
			
		return productoConvertido;
	}
	
	private ProductoDTO convertirResultSet2(ResultSet rs) throws SQLException
	{
		int idProducto = rs.getInt("idProd");
		double espesor = rs.getDouble("espesor");
		double ancho = rs.getDouble("ancho");
		double largo = UProducto.formatearMedida(rs.getDouble("largo"), UProducto.UN_DECIMAL);
		int stock = (rs.getInt("uni") - rs.getInt("reserva"));
		double totalPies = UProducto.calcularPiesCuadrados(espesor, ancho, largo, stock, UProducto.DOS_DECIMALES);
		int reserva = rs.getInt("reserva");
		int carga = rs.getInt("carga");
		
		ProductoDTO p = new ProductoDTO(idProducto, espesor, ancho, largo, totalPies, stock, reserva, carga);
		
		return p;
	}

	@Override
	public ObservableList<ProductoDTO> obtenerProductosPedido(int idPedido)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProductoDTO> listaProductos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCTOS_PEDIDO);
				ps.setInt(1, idPedido);
				rs = ps.executeQuery();
				while(rs.next())
				{
					listaProductos.add(convertirResultSet(rs));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener los productos.\nCausa: {}", e.getMessage());

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
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e2.getMessage());
					
					return null;
				}
			}
			
			return listaProductos;
		}
		
		else return null;
		
		
	}
	
	@Override
	public ObservableList<ProductoDTO> obtenerProductosEspecificados(int idPedido) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProductoDTO> listaProductos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCTOS_ESPECIFICADOS);
				ps.setInt(1, idPedido);
				rs = ps.executeQuery();
				while(rs.next())
				{
					listaProductos.add(convertirResultSet(rs));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener los productos especificados.\nCausa: {}\n ", e.getMessage());

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
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e2.getMessage());
					
					return null;
				}
			}
			
			return listaProductos;
		}
		
		else return null;
	}
	
	@Override
	public ObservableList<ProductoDTO> obtenerProductosPedidoClasificado(int idPedido)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			ResultSet rs = null;
			ResultSet rs2 = null;
			ObservableList<ProductoDTO> listaProductos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_DISTINCT_PEDIDO);
				ps.setInt(1, idPedido);
				rs = ps.executeQuery();
				while(rs.next())
				{
					ps2 = con.prepareStatement(SELECT_SUM_PEDIDO);
					ps2.setDouble(1, rs.getDouble("espesor"));
					ps2.setDouble(2, rs.getDouble("ancho"));
					ps2.setDouble(3, idPedido);
					
					rs2 = ps2.executeQuery();
					while(rs2.next())
					{
						ProductoDTO producto = new ProductoDTO();
						producto.setEspesor(rs2.getDouble("espesor"));
						producto.setAncho(rs2.getDouble("ancho"));
						producto.setLargo(UProducto.formatearMedida(rs2.getDouble("suma"), UProducto.UN_DECIMAL));
						producto.setStock(rs2.getInt("uni"));
						producto.setPiesClasificado();
						
						listaProductos.add(producto);
					}					
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener los productos clasificados.\nCausa: {}", e.getMessage());
				
				return null;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
					if(ps2 != null) ps.close();
					if(rs != null) rs.close();
					if(rs2 != null) rs.close();
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e2.getMessage());
					
					return null;
				}
			}
			
			return listaProductos;
		}
		
		else return null;
		
		
	}

	@Override
	public ObservableList<ProductoDTO> obtenerProductosProduccion(int idPedido)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProductoDTO> listaProductos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCTOS_PRODUCCION);
				ps.setInt(1, idPedido);
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					ProductoDTO p = new ProductoDTO();
					p.setIdProducto(rs.getInt("idProd"));
					p.setEspesor(rs.getDouble("espesor"));
					p.setAncho(rs.getDouble("ancho"));
					p.setLargo(rs.getDouble("largo"));
					p.setStock(rs.getInt("productos"));
					p.setReserva(0);
					p.setPies();
					
					listaProductos.add(p);
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido los productos de produccion.\nCausa: {}", e.getMessage());

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
					LOGGER.log(Level.ERROR, "No se han podido cerrar las sentencias.\nCausa: {}", e2.getMessage());

					return null;
				}
			}
			
			return listaProductos;
		}
		
		else return null;
	
	}

	@Override
	public ProductoDTO obtener(ProductoDTO prod) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ProductoDTO productoExistente = null;
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCTO_EXISTENTE);
				ps.setDouble(1, prod.getEspesor());
				ps.setDouble(2, prod.getAncho());
				ps.setDouble(3, prod.getLargo());
				System.out.println("PRODUCTO A CONSULTAR -> " + prod);
				rs = ps.executeQuery();
				if (rs.next())
				{
					productoExistente = convertirResultSet(rs);
					System.out.println("PRODUCTOS EXISTENTE --> " + productoExistente);

				}
				else productoExistente = new ProductoDTO();
				
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido comprobar existencia del producto.\nCausa: {}", e.getMessage());
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
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia. \nCausa {}", e2.getMessage());
					return null;
				}
			}
			return productoExistente;
		}
		
		else return null;
	}

	@Override
	public ObservableList<VentasAnio> obtenerProductosVendidos() 
	{
		
		
		return null;
	}
	
	public void descontarAutoIncremento(String nombreTabla)
	{
		con = Conexion.tomarConexion();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		int autoIncremento = 0;
		try{
			ps = con.prepareStatement("SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");
			ps.setString(1, "aserradero_bd");
			ps.setString(2, nombreTabla);
			rs = ps.executeQuery();
			if (rs.next())
			{
				autoIncremento = rs.getInt("auto_increment");

			}
		
			System.out.println("AUTO_INCREMENT --> " + autoIncremento);

		}
		catch(SQLException e)
		{
			LOGGER.log(Level.ERROR, "No se ha podido comprobar existencia del producto.\nCausa: {}", e.getMessage());
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
				LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia. \nCausa {}", e2.getMessage());
			}
		}
	}

	
}	
