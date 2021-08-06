package aserradero.modelo.DAO.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.modelo.DAO.ProduccionDAO;
import aserradero.modelo.DTO.ProduccionDTO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.clases.ProduccionAnio;
import aserradero.modelo.clases.ProduccionDia;
import aserradero.modelo.clases.ProduccionMes;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UFecha;
import aserradero.util.UProducto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProduccionDAOMySQLImpl implements ProduccionDAO
{
	private static final String INSERT = "INSERT INTO produccion (idProducto, idPedido, fechaCarga, horaCarga)VALUES (?, ?, ?, ?)";
	
	private static final String UPDATE = "UPDATE produccion SET idProducto = ?, idPedido = ?, fechaCarga = ?, horaCarga = ?"
			+ " WHERE idProduccion = ?";
	
	private static final String DELETE = "DELETE FROM produccion WHERE idProduccion = ?";
	
	private static final String SELECT_TODOS = "SELECT idProduccion, produccion.idPedido AS idPed, produccion.idProducto AS idProd, fechaCarga, horaCarga"
			+ ", espesor, ancho, largo FROM produccion"
			+ " INNER JOIN producto ON producto.idProducto = produccion.idProducto"
			+ " INNER JOIN pedido ON pedido.idPedido = produccion.idPedido"
			+ " ORDER BY idProduccion ASC";
	
	private static final String SELECT_DISTINCT_ANIOS = "SELECT DISTINCT YEAR(fechaCarga) AS anio FROM produccion"
			+ " ORDER BY YEAR(fechaCarga) DESC";
	
	private static final String SELECT_DISTINCT_MESES = "SELECT DISTINCT MONTH(fechaCarga) AS mes"
			+ " FROM produccion"
			+ " WHERE YEAR(fechaCarga) = ?"
			+ " ORDER BY MONTH(fechaCarga) DESC";
	
	private static final String SELECT_DISTINCT_DIAS = "SELECT DISTINCT DAY(fechaCarga) AS dia FROM produccion"
			+ " WHERE MONTH(fechaCarga) = ? AND YEAR(fechaCarga) = ?"
			+ " ORDER BY dia DESC";
	
	private static final String SELECT_DISTINCT_PRODUCTOS_TOTAL = "SELECT DISTINCT espesor, ancho"
			+ " FROM producto"
			+ " INNER JOIN produccion ON produccion.idProducto = producto.idProducto"
			+ " ORDER BY espesor ASC, ancho ASC";
	
	private static final String SELECT_DISTINCT_PRODUCTOS_DIA = "SELECT DISTINCT espesor, ancho"
			+ " FROM producto"
			+ " INNER JOIN produccion ON produccion.idProducto = producto.idProducto"
			+ " WHERE MONTH(fechaCarga) = ? AND YEAR(fechaCarga) = ? AND DAY(fechaCarga) = ?"
			+ " ORDER BY espesor ASC, ancho ASC";
	
	private static final String SELECT_DISTINCT_PRODUCTOS_MES = "SELECT DISTINCT espesor, ancho"
			+ " FROM producto"
			+ " INNER JOIN produccion ON produccion.idProducto = producto.idProducto"
			+ " WHERE MONTH(fechaCarga) = ? AND YEAR(fechaCarga) = ?"
			+ " ORDER BY espesor ASC, ancho ASC";
	
	private static final String SELECT_DISTINCT_PRODUCTOS_ANIO = "SELECT DISTINCT espesor, ancho"
			+ " FROM producto"
			+ " INNER JOIN produccion ON produccion.idProducto = producto.idProducto"
			+ " WHERE YEAR(fechaCarga) = ?"
			+ " ORDER BY espesor ASC, ancho ASC";
	
	private static final String SELECT_PRODUCTOS_DIA = "SELECT produccion.idProducto AS idProd, espesor, ancho, largo, count(produccion.idProducto)"
			+ " AS productos FROM produccion"
			+ " INNER JOIN producto ON producto.idProducto = produccion.idProducto"
			+ " WHERE MONTH(fechaCarga) = ? AND YEAR(fechaCarga) = ? AND DAY(fechaCarga) = ?"
			+ " GROUP BY produccion.idProducto";
	
	private static final String SELECT_PRODUCTOS_MES = "SELECT produccion.idProducto AS idProd, espesor, ancho, largo, count(produccion.idProducto)"
			+ " AS productos FROM produccion"
			+ " INNER JOIN producto ON producto.idProducto = produccion.idProducto"
			+ " WHERE MONTH(fechaCarga) = ? AND YEAR(fechaCarga) = ?"
			+ " GROUP BY produccion.idProducto"
			+ " ORDER BY espesor, ancho ASC";
	
	
	private static final String SELECT_PRODUCTOS_ANIO = "SELECT produccion.idProducto AS idProd, espesor, ancho, largo, count(produccion.idProducto)"
			+ " AS productos FROM produccion"
			+ " INNER JOIN producto ON producto.idProducto = produccion.idProducto"
			+ " WHERE YEAR(fechaCarga) = ?"
			+ " GROUP BY produccion.idProducto";
	
	private static final String SELECT_PRODUCTOS_TOTAL = "SELECT produccion.idProducto AS idProd, espesor, ancho, largo, count(produccion.idProducto)"
			+ " AS productos FROM produccion"
			+ " INNER JOIN producto ON producto.idProducto = produccion.idProducto"
			+ " GROUP BY produccion.idProducto";
	
	private static final String SELECT_PRODUCTOS_DIA_CLASIFICADO = "SELECT producto.espesor, producto.ancho,"
			+ " SUM(largo) AS suma, COUNT(produccion.idProduccion) AS canti"
			+ " FROM producto"
			+ " INNER JOIN produccion ON produccion.idProducto = producto.idProducto"
			+ " WHERE MONTH(fechaCarga) = ? AND YEAR(fechaCarga) = ? AND DAY(fechaCarga) = ? AND espesor = ? AND ancho = ?";
	
	private static final String SELECT_PRODUCTOS_MES_CLASIFICADO = "SELECT producto.espesor, producto.ancho,"
			+ " SUM(largo) AS suma, COUNT(produccion.idProduccion) AS canti"
			+ " FROM producto"
			+ " INNER JOIN produccion ON produccion.idProducto = producto.idProducto"
			+ " WHERE MONTH(fechaCarga) = ? AND YEAR(fechaCarga) = ? AND espesor = ? AND ancho = ?";

	private static final String SELECT_PRODUCTOS_ANIO_CLASIFICADO = "SELECT producto.espesor, producto.ancho,"
			+ " SUM(largo) AS suma, COUNT(produccion.idProduccion) AS canti"
			+ " FROM producto"
			+ " INNER JOIN produccion ON produccion.idProducto = producto.idProducto"
			+ " WHERE YEAR(fechaCarga) = ? AND espesor = ? AND ancho = ?";

	private static final String SELECT_PRODUCTOS_TOTAL_CLASIFICADO = "SELECT producto.espesor, producto.ancho,"
			+ " SUM(largo) AS suma, COUNT(produccion.idProduccion) AS canti"
			+ " FROM producto"
			+ " INNER JOIN produccion ON produccion.idProducto = producto.idProducto"
			+ " WHERE espesor = ? AND ancho = ?";
	
	private static final String SELECT_TIME_DIFF = "SELECT TIMEDIFF((SELECT MAX(horaCarga)), (SELECT MIN(horaCarga))) AS dif, fechaCarga"
			+ " FROM produccion WHERE MONTH(fechaCarga) = ? AND YEAR(fechaCarga) = ? AND DAY(fechaCarga) = ?";
	
	private static final String SELECT_DISTINCT_YEAR = "SELECT DISTINCT YEAR(fechaCarga) AS anio FROM produccion";
	
	private static final String SELECT_FECHA_MAXIMA = "SELECT MAX(fechaCarga) AS fechaMaxima FROM produccion";
	
	private static final String SELECT_FECHA_MINIMA = "SELECT MIN(fechaCarga) AS fechaMinima FROM produccion";
	
	private static final String SELECT_DISTINCT_MES_DE_ANIO = "SELECT DISTINCT MONTH(fechaCarga) AS mes FROM produccion"
			+ " WHERE YEAR(fechaCarga) = ?";
	
	private static final String SELECT_PRODUCCION_PEDIDO = "SELECT idProduccion, produccion.idPedido AS idPed, fechaCarga, horaCarga, produccion.idProducto AS idProd, espesor, ancho, largo"
			+ " FROM produccion"
			+ " INNER JOIN producto ON producto.idProducto = produccion.idProducto"
			+ " WHERE produccion.idPedido = ? ORDER BY idProduccion ASC";
	
	private static final String SELECT_PRODUCCION_PRODUCTO_PEDIDO = "SELECT idProduccion, produccion.idProducto AS idProd, produccion.idPedido AS idPed, fechaCarga, horaCarga, espesor, ancho, largo"
			+ " FROM produccion"
			+ " INNER JOIN producto ON producto.idProducto = produccion.idProducto"
			+ " WHERE produccion.idProducto = ? AND produccion.idPedido = ?"
			+ " ORDER BY idProduccion ASC";
	
	private static final Logger LOGGER = LogManager.getLogger(ProduccionDAOMySQLImpl.class);
	
	Connection con;
	
	@Override
	public ObservableList<ProduccionAnio> obtenerProduccionTotal() 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;	
			PreparedStatement ps2 = null;	
			ResultSet rs = null;
			ResultSet rs2 = null;
			ObservableList<ProduccionAnio> listaProduccionAnio = FXCollections.observableArrayList();
			ObservableList<ProduccionMes> listaProduccionMes = FXCollections.observableArrayList();
			int iteraciones = 0;
			try
			{
				// Selecciona los distintos anios 
				ps = con.prepareStatement(SELECT_DISTINCT_ANIOS);
				rs = ps.executeQuery();
				while(rs.next())
				{					
					ps2 = con.prepareStatement(SELECT_DISTINCT_MESES);
					ps2.setInt(1, rs.getInt("anio"));
					rs2 = ps2.executeQuery();
					while(rs2.next())
					{
						ProduccionMes produccionMes = new ProduccionMes(Month.of(rs2.getInt("mes")), Year.of(rs.getInt("anio")));
						produccionMes.setProduccionesDia(obtenerProduccionDeMes(produccionMes.getMes(), produccionMes.getAnio()));
						produccionMes.calcularTotal();
						
						listaProduccionMes.add(produccionMes);
					}

					ProduccionAnio produccionAnio = new ProduccionAnio();
					produccionAnio.setAnio(Year.of(rs.getInt("anio")));
					produccionAnio.setProduccionesMes(FXCollections.observableArrayList(listaProduccionMes));
					produccionAnio.calcularTotal();
					
					listaProduccionAnio.add(produccionAnio);
					
					listaProduccionMes.clear();
				}
					
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener la produccion. \nCausa: {}", e.getMessage());

				return null;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia. \nCausa: {}", UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy));
					return null;
				}
			}
			
			/*for(ProduccionMes prod: listaProduccion)
			{
				System.out.println("<<<<< " + prod.getMes() + " " + prod.getAño() + " >>>>>");
				for(ProductoDTO p : prod.getProductos())
				{
					System.out.println(p);
				}
			}*/
			
			return listaProduccionAnio;
		}
		
		else return null;
		
	}

	@Override
	public ObservableList<ProduccionDia> obtenerProduccionDeMes(Month mes, Year anio) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			PreparedStatement ps3 = null;
			PreparedStatement ps4 = null;
			PreparedStatement ps5 = null;
			
			ResultSet rs = null;
			ResultSet rs2 = null;
			ResultSet rs3 = null;
			ResultSet rs4 = null;
			ResultSet rs5 = null;
			
			ObservableList <ProduccionDia> listaProduccionDia = FXCollections.observableArrayList();

			try
			{
				// SE seleccionan los distintos dias pertenecientes a un mes y año determinado
				ps = con.prepareStatement(SELECT_DISTINCT_DIAS);
				ps.setInt(1, mes.getValue());
				ps.setInt(2, anio.getValue());
				rs = ps.executeQuery();
				while(rs.next())
				{
					ObservableList<ProductoDTO> listaProductosIndividuales = FXCollections.observableArrayList();
					ObservableList<ProductoDTO> listaProductosClasificados = FXCollections.observableArrayList();
					
					// se seleccionan los productos del dia de forma individual
					ps2 = con.prepareStatement(SELECT_PRODUCTOS_DIA);
					ps2.setInt(1, mes.getValue());
					ps2.setInt(2, anio.getValue());
					ps2.setInt(3, rs.getInt("dia"));
					
					rs2 = ps2.executeQuery();
					while(rs2.next())
					{
						ProductoDTO producto = new ProductoDTO();
						producto.setIdProducto(rs2.getInt("idProd"));
						producto.setEspesor(rs2.getDouble("espesor"));
						producto.setAncho(rs2.getDouble("ancho"));
						producto.setLargo(UProducto.formatearMedida(rs2.getDouble("largo"), UProducto.UN_DECIMAL));
						producto.setStock(rs2.getInt("productos"));
						producto.setPies();
						
						listaProductosIndividuales.add(producto);
					}
										
					// se seleccionan las distintas combinaciones de espesores y anchos
					ps3 = con.prepareStatement(SELECT_DISTINCT_PRODUCTOS_DIA);
					ps3.setInt(1, mes.getValue());
					ps3.setInt(2, anio.getValue());
					ps3.setInt(3, rs.getInt("dia"));
					rs3 = ps3.executeQuery();
					while(rs3.next())
					{
						// se seleccionan los productos del dia de forma clasificada
						ps4 = con.prepareStatement(SELECT_PRODUCTOS_DIA_CLASIFICADO);
						ps4.setInt(1, mes.getValue());
						ps4.setInt(2, anio.getValue());
						ps4.setInt(3, rs.getInt("dia"));
						ps4.setDouble(4, rs3.getDouble("espesor"));
						ps4.setDouble(5, rs3.getDouble("ancho"));
						
						rs4 = ps4.executeQuery();
						while(rs4.next())
						{
							
							ProductoDTO producto = new ProductoDTO();
							producto.setEspesor(rs4.getDouble("espesor"));
							producto.setAncho(rs4.getDouble("ancho"));
							producto.setLargo(UProducto.formatearMedida(rs4.getDouble("suma"), UProducto.UN_DECIMAL));
							producto.setStock(rs4.getInt("canti"));
							producto.setPiesClasificado();
							
							listaProductosClasificados.add(producto);
						}
					}
					
					//Se selecciona la diferencia entre la hora del primer producto insertado y el ultimo del dia
					ps5 = con.prepareStatement(SELECT_TIME_DIFF);
					ps5.setInt(1, mes.getValue());
					ps5.setInt(2, anio.getValue());
					ps5.setInt(3, rs.getInt("dia"));
					rs5 = ps5.executeQuery();
					while(rs5.next())
					{
						ProduccionDia produccionDia = new ProduccionDia();
						produccionDia.setFecha(rs5.getDate("fechaCarga").toLocalDate());
						produccionDia.setProductosIndividuales(listaProductosIndividuales);
						produccionDia.setProductosClasificados(listaProductosClasificados);
						produccionDia.calcularTotal();
						produccionDia.setDuracion(rs5.getTime("dif").toLocalTime());
						
						listaProduccionDia.add(produccionDia);
					}
				}
			}
			catch (SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido calcular la producción.\nCausa: {}", e.getMessage());
				return null;
			}
			finally 
			{
				try
				{
					if (ps != null) ps.close();
				}
				catch (SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia. \nCausa {}", e.getMessage());
					return null;
				}
			}
			
			return listaProduccionDia;
		}
		
		else return null;
		
	}

	@Override
	public ObservableList<ProduccionDTO> obtenerProduccionDeDia(ProduccionDia p)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			ResultSet rs = null;
			ResultSet rs2 = null;
			ObservableList <ProduccionDTO> listaProduccion = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCCION_PEDIDO);
				ps.setDate(1, java.sql.Date.valueOf(p.getFecha()));
				rs = ps.executeQuery();
				while (rs.next())
				{
					ProductoDTO producto = new ProductoDTO();
					producto.setIdProducto(rs.getInt("idProducto"));
					producto.setEspesor(rs.getDouble("espesor"));
					producto.setAncho(rs.getDouble("ancho"));
					producto.setLargo(UProducto.formatearMedida(rs.getDouble("largo"), UProducto.UN_DECIMAL));
					producto.setStock(1);
					producto.setPies();
					
					ProduccionDTO produccion = new ProduccionDTO(rs.getInt("idPedido"), producto, rs.getDate("fechaCarga").toLocalDate(), rs.getTime("horaCarga").toLocalTime());
						
					listaProduccion.add(produccion);
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido mostrar el detalle.\nCausa: {}" , e.getMessage());
				return null;
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
					return null;
				}
			}
			return listaProduccion;
		}
		
		else return null;
		
	}


	@Override
	public boolean insertar(ProduccionDTO produccion)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			boolean insertada = false;
			try
			{
				ps = con.prepareStatement(INSERT);
				ps.setInt(1, produccion.getProducto().getIdProducto());
				
				if(produccion.getIdPedido() != 0) 
					ps.setInt(2, produccion.getIdPedido());
				else ps.setNull(2, java.sql.Types.INTEGER);
				
				ps.setDate(3, java.sql.Date.valueOf(produccion.getFechaCarga()));
				ps.setTime(4, java.sql.Time.valueOf(produccion.getHoraCarga()));
				if(ps.executeUpdate() == 1)
				{
					// Se obtiene el ultimo id generado
					rs = ps.getGeneratedKeys();
					if(rs.next())
					{
						// Se setea el ultimo id
						produccion.setIdProduccion(rs.getInt(1));
						insertada = true;
					}
				}
				
				LOGGER.log(Level.INFO, "Se ha insertado una nueva produccion. ID = [{}]", produccion.getIdProduccion());
			}
			catch(SQLException e)
			{
				try 
				{
					LOGGER.log(Level.ERROR, "No se ha podido insertar la produccion.\nCausa: {}", e.getMessage());
					con.rollback();
					LOGGER.log(Level.ERROR, "Se han revertido los cambios (rollBack).");
					return false;
				} 
				catch (SQLException e2) 
				{
					LOGGER.log(Level.ERROR, "No se han podido revertir los cambios.", UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy));
					return false;
				}
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
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia. \nCausa {}", e.getMessage());
				}
			}
			
			return insertada;
		}
		
		else return false;
	}


	@Override
	public boolean actualizar(ProduccionDTO produccion)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean modificada = false;
			try
			{
				ps = con.prepareStatement(UPDATE);
				ps.setInt(1, produccion.getProducto().getIdProducto());
				
				if(produccion.getIdPedido() != 0) ps.setInt(2, produccion.getIdPedido());
				else ps.setNull(2, java.sql.Types.INTEGER);
				
				ps.setDate(3, java.sql.Date.valueOf(produccion.getFechaCarga()));
				ps.setTime(4, java.sql.Time.valueOf(produccion.getHoraCarga()));
				ps.setInt(5, produccion.getIdProduccion());
			
				int update = ps.executeUpdate();
				if(update == 1)
				{
					modificada = true;
					LOGGER.log(Level.INFO, "Se modificado la produccion. ID = [{}]", produccion.getIdProduccion());
				}
			}
			catch(SQLException e)
			{
				try
				{
					LOGGER.log(Level.ERROR, "No se ha podido modificar la produccion.\nCausa: {}", e.getMessage());
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
			
			return modificada;
		}
		
		else return false;
		
	}


	@Override
	public boolean eliminar(ProduccionDTO produccion) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean eliminado = false;
			try
			{
				ps = con.prepareStatement(DELETE);
				ps.setInt(1, produccion.getIdProduccion());
				if(ps.executeUpdate() == 1)
				{
					eliminado = true;
				}
				
			LOGGER.log(Level.INFO, "Se ha eliminado la produccion. ID = [{}]", produccion.getIdProduccion());
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
					LOGGER.log(Level.ERROR, "No se han podido revertir los cambios.\nCausa: {}", e2.getMessage());
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
				}
			}
			return eliminado;
		}
		
		else return false;
		
	}


	@Override
	public ObservableList<ProduccionDTO> obtenerTodos()
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProduccionDTO> listaProducciones = FXCollections.observableArrayList();
			try
			{
				ps = con.prepareStatement(SELECT_TODOS);
				rs = ps.executeQuery();
				while(rs.next())
				{
					ProduccionDTO produccion = new ProduccionDTO();
					produccion.setIdProduccion(rs.getInt("idProduccion"));
					produccion.setIdPedido(rs.getInt("idPed"));
					produccion.setFechaCarga(rs.getDate("fechaCarga").toLocalDate());
					produccion.setHoraCarga(rs.getTime("horaCarga").toLocalTime());
					
					ProductoDTO producto = new ProductoDTO();
					producto.setIdProducto(rs.getInt("idProd"));
					producto.setEspesor(rs.getDouble("espesor"));
					producto.setAncho(rs.getDouble("ancho"));
					producto.setLargo(UProducto.formatearMedida(rs.getDouble("largo"), UProducto.UN_DECIMAL));
					
					produccion.setProducto(producto);
					
					listaProducciones.add(produccion);
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtener las producciones.\nCausa: {}", e.getMessage());
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
			
			// TODO Auto-generated method stub
			return listaProducciones;
		}
		
		else return null;
		
	}


	@Override
	public ProduccionDTO obtenerPorId(Integer id) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ObservableList<ProduccionMes> obtenerProduccionDeAnio(int anio) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;	
			ResultSet rs = null;
			ObservableList<ProduccionMes> listaProduccionMes = FXCollections.observableArrayList();		
			try
			{
				// Selecciona los distintos meses de un año determinado
				ps = con.prepareStatement(SELECT_DISTINCT_MES_DE_ANIO);
				ps.setInt(1, anio);
				rs = ps.executeQuery();
				while(rs.next())
				{	
					ProduccionMes produccionMes = new ProduccionMes(Month.of(rs.getInt("mes")), Year.of(anio));
					produccionMes.setProduccionesDia(obtenerProduccionDeMes(produccionMes.getMes(), produccionMes.getAnio()));
					produccionMes.calcularTotal();
					listaProduccionMes.add(produccionMes);
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido obtener la produccion.\nCausa {}", e.getMessage());
				return null;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
				}
				catch(SQLException e2)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa {}.", e2.getMessage());
					return null;
				}
			}		
			return listaProduccionMes;
		}
		
		else return null;
		
	}

	@Override
	public ObservableList<String> obtenerTodosLosAnios() 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<String> listaAnios = FXCollections.observableArrayList();
			listaAnios.add("Todos los años");
			try
			{
				ps = con.prepareStatement(SELECT_DISTINCT_YEAR);
				rs = ps.executeQuery();
				while(rs.next())
				{
					listaAnios.add(String.valueOf(rs.getInt("anio")));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han obtener los años.\nCausa: {}", e.getMessage());
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
			return listaAnios;
		}
		
		else return null;
		
	}
	
	@Override
	public LocalDate obtenerFechaMaxima() 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			LocalDate fechaMaxima = null;
			
			try
			{
				ps = con.prepareStatement(SELECT_FECHA_MAXIMA);
				rs = ps.executeQuery();
				if(rs.next())
				{
					fechaMaxima = rs.getDate("fechaMaxima").toLocalDate();
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner la fecha maxima.\nCausa: {}", e.getMessage());
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
			
			return fechaMaxima;
		}
		
		else return null;
		
	}

	@Override
	public LocalDate obtenerFechaMinima() 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			LocalDate fechaMinima = null;
			
			try
			{
				ps = con.prepareStatement(SELECT_FECHA_MINIMA);
				rs = ps.executeQuery();
				if(rs.next())
				{
					fechaMinima = rs.getDate("fechaMinima").toLocalDate();
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner la fecha minima.\nCausa: {}", e.getMessage());
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
			
			return fechaMinima;
		}
		
		else return null;
		
	}

	@Override
	public ObservableList<ProduccionDTO> obtenerProduccionDePedido(int idPedido) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProduccionDTO> listaProduccion = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCCION_PEDIDO);
				ps.setInt(1, idPedido);
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					ProductoDTO producto = new ProductoDTO();
					producto.setIdProducto(rs.getInt("idProd"));
					producto.setEspesor(rs.getDouble("espesor"));
					producto.setAncho(rs.getDouble("ancho"));
					producto.setLargo(UProducto.formatearMedida(rs.getDouble("largo"), UProducto.UN_DECIMAL));
					producto.setReserva(0);
					producto.setStock(1);
					producto.setCarga(1);
					producto.setPies(UProducto.calcularPiesCuadrados(producto.getEspesor(), producto.getAncho(), producto.getLargo(), 1, UProducto.DOS_DECIMALES));
					int idProduccion = rs.getInt("idProduccion");
					int idPed = rs.getInt("idPed");
					LocalDate fechaCarga = rs.getDate("fechaCarga").toLocalDate();
					LocalTime horaCarga = rs.getTime("horaCarga").toLocalTime();
					
					listaProduccion.add(new ProduccionDTO(idProduccion, idPed, producto, fechaCarga, horaCarga));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner la produccion de pedido.\nCausa: {}", e.getMessage());
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
		
			return listaProduccion;
		}
		
		else return null;
		
	}

	@Override
	public ObservableList<ProduccionDTO> obtenerProduccionProductoPedido(int idProducto, int idPedido) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProduccionDTO> listaProduccion = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCCION_PRODUCTO_PEDIDO);
				ps.setInt(1, idProducto);
				ps.setInt(2, idPedido);
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					ProductoDTO producto = new ProductoDTO();
					producto.setIdProducto(rs.getInt("idProd"));
					producto.setEspesor(rs.getDouble("espesor"));
					producto.setAncho(rs.getDouble("ancho"));
					producto.setLargo(UProducto.formatearMedida(rs.getDouble("largo"), UProducto.UN_DECIMAL));
					producto.setStock(1);
					producto.setReserva(0);
					producto.setPies();
					int idProduccion = rs.getInt("idProduccion");
					int idPed = rs.getInt("idPed");
					LocalDate fechaCarga = rs.getDate("fechaCarga").toLocalDate();
					LocalTime horaCarga = rs.getTime("horaCarga").toLocalTime();
					
					listaProduccion.add(new ProduccionDTO(idProduccion, idPed, producto, fechaCarga, horaCarga));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner productos de produccion de pedido.\nCausa: {}", e.getMessage());
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
			
			return listaProduccion;
		}
		
		else return null;
		
	}

	@Override
	public ObservableList<ProductoDTO> obtenerProductosDeMes(Month mes, Year anio) 
	{
		con = Conexion.tomarConexion();
		
		System.out.println("-------- > obtenetProductisDeMes");
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProductoDTO> listaProductos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCTOS_MES);
				ps.setInt(1, mes.getValue());
				ps.setInt(2, anio.getValue());
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					ProductoDTO producto = new ProductoDTO();
					producto.setIdProducto(rs.getInt("idProd"));
					producto.setEspesor(rs.getDouble("espesor"));
					producto.setAncho(rs.getDouble("ancho"));
					producto.setLargo(rs.getDouble("largo"));
					producto.setStock(rs.getInt("productos"));
					producto.setPies();
					
					listaProductos.add(producto);
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner los productos de mes.\nCausa: {}", e.getMessage());
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
	public ObservableList<ProductoDTO> obtenerProductosDeAnio(Year anio) 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProductoDTO> listaProductos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCTOS_ANIO);
				ps.setInt(1, anio.getValue());
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					ProductoDTO producto = new ProductoDTO();
					producto.setIdProducto(rs.getInt("idProd"));
					producto.setEspesor(rs.getDouble("espesor"));
					producto.setAncho(rs.getDouble("ancho"));
					producto.setLargo(rs.getDouble("largo"));
					producto.setStock(rs.getInt("productos"));
					producto.setPies();
					
					listaProductos.add(producto);
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner los productos de anio.\nCausa: {}", e.getMessage());
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
	public ObservableList<ProductoDTO> obtenerProductosTotal() 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList<ProductoDTO> listaProductos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PRODUCTOS_TOTAL);
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					ProductoDTO producto = new ProductoDTO();
					producto.setIdProducto(rs.getInt("idProd"));
					producto.setEspesor(rs.getDouble("espesor"));
					producto.setAncho(rs.getDouble("ancho"));
					producto.setLargo(rs.getDouble("largo"));
					producto.setStock(rs.getInt("productos"));
					producto.setPies();
					
					listaProductos.add(producto);
				}
				
				if(listaProductos.isEmpty()) listaProductos = null;
				
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner los productos de produccion total.\nCausa: {}", e.getMessage());
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
	public ObservableList<ProductoDTO> obtenerProductosDeMesClasificados(Month mes, Year anio) 
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
				ps = con.prepareStatement(SELECT_DISTINCT_PRODUCTOS_MES);
				ps.setInt(1, mes.getValue());
				ps.setInt(2, anio.getValue());
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					// se seleccionan los productos del mes de forma clasificada
					ps2 = con.prepareStatement(SELECT_PRODUCTOS_MES_CLASIFICADO);
					ps2.setInt(1, mes.getValue());
					ps2.setInt(2, anio.getValue());
					ps2.setDouble(3, rs.getDouble("espesor"));
					ps2.setDouble(4, rs.getDouble("ancho"));
					
					rs2 = ps2.executeQuery();
					while(rs2.next())
					{
						ProductoDTO producto = new ProductoDTO();
						producto.setEspesor(rs2.getDouble("espesor"));
						producto.setAncho(rs2.getDouble("ancho"));
						producto.setLargo(UProducto.formatearMedida(rs2.getDouble("suma"), UProducto.UN_DECIMAL));
						producto.setStock(rs2.getInt("canti"));
						producto.setPiesClasificado();
						
						listaProductos.add(producto);
					}
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner los productos de mes clasificados.\nCausa: {}", e.getMessage());
				return null;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
					if(ps2 != null) ps2.close();
					if(rs != null) rs.close();
					if(rs2 != null) rs2.close();
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
	public ObservableList<ProductoDTO> obtenerProductosDeAnioClasificados(Year anio) 
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
				ps = con.prepareStatement(SELECT_DISTINCT_PRODUCTOS_ANIO);
				ps.setInt(1, anio.getValue());
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					// se seleccionan los productos del mes de forma clasificada
					ps2 = con.prepareStatement(SELECT_PRODUCTOS_ANIO_CLASIFICADO);
					ps2.setInt(1, anio.getValue());
					ps2.setDouble(2, rs.getDouble("espesor"));
					ps2.setDouble(3, rs.getDouble("ancho"));
					
					rs2 = ps2.executeQuery();
					while(rs2.next())
					{
						ProductoDTO producto = new ProductoDTO();
						producto.setEspesor(rs2.getDouble("espesor"));
						producto.setAncho(rs2.getDouble("ancho"));
						producto.setLargo(UProducto.formatearMedida(rs2.getDouble("suma"), UProducto.UN_DECIMAL));
						producto.setStock(rs2.getInt("canti"));
						producto.setPiesClasificado();
						
						listaProductos.add(producto);
					}
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner los productos del año clasificados.\nCausa: {}", e.getMessage());
				return null;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
					if(ps2 != null) ps2.close();
					if(rs != null) rs.close();
					if(rs2 != null) rs2.close();
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
	public ObservableList<ProductoDTO> obtenerProductosTotalClasificados() 
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
				ps = con.prepareStatement(SELECT_DISTINCT_PRODUCTOS_TOTAL);
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					// se seleccionan los productos del mes de forma clasificada
					ps2 = con.prepareStatement(SELECT_PRODUCTOS_TOTAL_CLASIFICADO);
					ps2.setDouble(1, rs.getDouble("espesor"));
					ps2.setDouble(2, rs.getDouble("ancho"));
					
					rs2 = ps2.executeQuery();
					while(rs2.next())
					{
						ProductoDTO producto = new ProductoDTO();
						producto.setEspesor(rs2.getDouble("espesor"));
						producto.setAncho(rs2.getDouble("ancho"));
						producto.setLargo(UProducto.formatearMedida(rs2.getDouble("suma"), UProducto.UN_DECIMAL));
						producto.setStock(rs2.getInt("canti"));
						producto.setPiesClasificado();
						
						listaProductos.add(producto);
					}
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtner los productos del año clasificados.\nCausa: {}", e.getMessage());
				return null;
			}
			finally
			{
				try
				{
					if(ps != null) ps.close();
					if(ps2 != null) ps2.close();
					if(rs != null) rs.close();
					if(rs2 != null) rs2.close();
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
	public ProduccionDTO obtener(ProduccionDTO v) {
		// TODO Auto-generated method stub
		return null;
	}
}
