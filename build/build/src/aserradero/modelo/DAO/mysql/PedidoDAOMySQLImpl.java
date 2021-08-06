package aserradero.modelo.DAO.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.modelo.DAO.PedidoDAO;
import aserradero.modelo.DTO.ClienteDTO;
import aserradero.modelo.DTO.PedidoDTO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.clases.VentasAnio;
import aserradero.modelo.clases.VentasMes;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UFactoryDAO;
import aserradero.util.UPedido;
import aserradero.util.UProducto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PedidoDAOMySQLImpl implements PedidoDAO
{
	
	private static final String INSERT = "INSERT INTO pedido (idCliente, fechaToma, horaToma, fechaEntrega, horaEntrega, proposito, estado, forma, fechaModificacion)"
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String UPDATE = "UPDATE pedido SET idCliente = ?, fechaToma = ?, fechaEntrega = ?, horaToma = ?, horaEntrega = ?, proposito = ?, estado = ?, forma = ?, fechaModificacion = ?"
			+ " WHERE idPedido = ?";
	
	private static final String DELETE = "DELETE FROM pedido WHERE idPedido = ?";
	
	private static final String SELECT = "SELECT idPedido AS idPed, idCliente AS idClie, fechaToma, fechaEntrega, horaToma, horaEntrega, proposito, estado, forma, fechaModificacion"
			+ " FROM pedido WHERE idPedido = ?";
	
	private static final String SELECT_TODOS = "SELECT idPedido AS idPed, idCliente AS idClie, fechaToma, fechaEntrega, horaToma, horaEntrega, proposito, estado, forma, fechaModificacion"
			+ " FROM pedido"
			+ " WHERE idPedido > 0 AND proposito = 'venta' ORDER BY idPedido DESC";
	
	private static final String SELECT_PEDIDOS_VENDIDOS = "SELECT pedido.idPedido AS idPed, pedido.idCliente AS idClie, fechaToma, fechaEntrega, horaToma, horaEntrega, proposito, estado, forma, fechaModificacion"
			+ " FROM pedido"
			+ " WHERE proposito = 'venta' ORDER BY idPedido DESC";
	
	private static final String SELECT_PEDIDOS_CLIENTE = "SELECT pedido.idPedido AS idPed, pedido.idCliente AS idClie, fechaToma, fechaEntrega, horaToma, horaEntrega, proposito, estado, forma, fechaModificacion"
			+ " FROM pedido INNER JOIN cliente ON cliente.idCliente = pedido.idCliente"
			+ " WHERE pedido.idCliente = ? ORDER BY idPedido DESC";
	
	private static final String SELECT_PEDIDOS_EMPLEADO =  "SELECT idPedido AS idPed, idCliente AS idClie, fechaToma, fechaEntrega, horaToma, horaEntrega, proposito, estado, forma, fechaModificacion"
			+ " FROM pedido"
			+ " WHERE (estado = ? OR estado = ?) AND forma != 'Stock' AND idPedido > 0"
			+ " ORDER BY idPedido DESC";
	
	private static final String SELECT_PEDIDOS_STOCK = "SELECT idPedido AS idPed, idCliente AS idClie, fechaToma, fechaEntrega, horaToma, horaEntrega, proposito, estado, forma, fechaModificacion"
			+ " FROM pedido"
			+ " WHERE proposito = 'cargaStock'"
			+ " ORDER BY idPed DESC";
	
	private static final String SELECT_DISTINCT_ANIOS = "SELECT DISTINCT YEAR(fechaEntrega) AS anio FROM pedido WHERE estado = 3 ORDER BY anio DESC";
	
	private static final String SELECT_DISTINCT_MESES = "SELECT DISTINCT MONTH(fechaEntrega) AS mes FROM pedido WHERE YEAR(fechaEntrega) = ? AND estado = 3 ORDER BY mes DESC";
	
	private static final String SELECT_PRODUCTOS_MES = "SELECT producto.idProducto AS idProd, producto.espesor, producto.ancho, producto.largo, producto.pies, detalle_pedido.unidades"
			+ " FROM producto INNER JOIN detalle_pedido ON detalle_pedido.idProducto = producto.idProducto"
			+ " INNER JOIN pedido on pedido.idPedido = detalle_pedido.idPedido"
			+ " WHERE YEAR(fechaEntrega) = ? AND MONTH(fechaEntrega) = ? AND estado = 3";
			
			
	
	private Connection con;
	
	private static final Logger LOGGER = LogManager.getLogger(PedidoDAOMySQLImpl.class);
	
	@Override
	public ObservableList<VentasAnio> obtenerPedidosVendidos() 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			PreparedStatement ps2 = null;
			PreparedStatement ps3 = null;
			ResultSet rs = null;
			ResultSet rs2 = null;
			ResultSet rs3 = null;
			ObservableList <VentasAnio> ventasAnios = FXCollections.observableArrayList();
			ObservableList <VentasMes> ventasMeses = FXCollections.observableArrayList();
			
			try
			{
				// seleccionamos los distintos años en donde existan pedidos entregados
				ps = con.prepareStatement(SELECT_DISTINCT_ANIOS);
				rs = ps.executeQuery();
				
				while(rs.next())
				{
					
					
					ps2 = con.prepareStatement(SELECT_DISTINCT_MESES);
					ps2.setInt(1, rs.getInt("anio"));
					rs2 = ps2.executeQuery();
					
					while(rs2.next())
					{
						ObservableList<ProductoDTO> listaProductosMes = FXCollections.observableArrayList();
						ps3 = con.prepareStatement(SELECT_PRODUCTOS_MES);
						ps3.setInt(1, rs.getInt("anio"));
						ps3.setInt(2, rs2.getInt("mes"));
						rs3 = ps3.executeQuery();
						while(rs3.next())
						{
							ProductoDTO producto = new ProductoDTO();
							producto.setIdProducto(rs3.getInt("idProd"));
							producto.setEspesor(rs3.getDouble("espesor"));
							producto.setAncho(rs3.getDouble("ancho"));
							producto.setLargo(UProducto.formatearMedida(rs3.getDouble("largo"), UProducto.UN_DECIMAL));
							producto.setStock(rs3.getInt("detalle_pedido.unidades"));
							producto.setPies();
							
							listaProductosMes.add(producto);
						}
						// instanciamos VentaMes
						VentasMes ventaMes = new VentasMes(Month.of(rs2.getInt("mes")), Year.of(rs.getInt("anio")));
						ventaMes.setProductosMes(listaProductosMes);
						ventaMes.calcularTotal();
						
						ventasMeses.add(ventaMes);
						
					}
					
					VentasAnio ventasAnio = new VentasAnio(rs.getInt("anio"), ventasMeses);
					
					ventasAnios.add(ventasAnio);
					
					ventasMeses.clear();
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtener los pedidos vendidos.\nCausa: {}", e.getMessage());
				return null;
			}
			finally
			{
				try
				{
					if (ps != null) ps.close();
					if (ps2 != null) ps2.close();
					if (ps3 != null) ps3.close();
				}
				catch(SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa: {}", e.getMessage());
					return null;
				}
			}
			return ventasAnios;
		}
		
		else return null;
	}
	
	@Override
	public ObservableList<PedidoDTO> obtenerPedidosEmpleado()
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList <PedidoDTO> listaPedidos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PEDIDOS_EMPLEADO);
				ps.setInt(1, UPedido.estadoInt(UPedido.PENDIENTE));
				ps.setInt(2, UPedido.estadoInt(UPedido.EN_CURSO));
				rs = ps.executeQuery();
				while(rs.next())
				{
					listaPedidos.add(convertirResultSet(rs));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido cargar los pedidos.\nCausa: {}", e.getMessage());
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
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa: {}", e.getMessage());
					return null;
				}
			}
			return listaPedidos;
		}
		
		else return null;
		
	}

	@Override
	public boolean insertar(PedidoDTO pedido) 
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
				
				if(pedido.getCliente() != null) 
					ps.setInt(1, pedido.getCliente().getIdCliente());
				else ps.setNull(1, java.sql.Types.INTEGER);
				
				ps.setDate(2, java.sql.Date.valueOf(pedido.getFechaToma()));
				ps.setTime(3, java.sql.Time.valueOf(pedido.getHoraToma()));
				ps.setDate(4, java.sql.Date.valueOf(LocalDate.of(9999, 9, 9))); // Solucionar el tema de la fecha nula!
				ps.setTime(5, java.sql.Time.valueOf(LocalTime.of(00, 00, 00))); // Solucionar el tema de la fecha nula!
				ps.setString(6, pedido.getProposito());
				ps.setInt(7, UPedido.estadoInt(pedido.getEstado()));
				ps.setString(8, pedido.getForma());
				
				if(pedido.getFechaModificacion().getYear() != 9999) 
					ps.setTimestamp(9, Timestamp.valueOf(pedido.getFechaModificacion()));
				else ps.setNull(9, java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
				
				int res = ps.executeUpdate();
				if(res == 1)
				{
					// Se obtiene el ultimo id insertado
					rs = ps.getGeneratedKeys();
					if (rs.next())
					{
						int idPedido = rs.getInt(1);
						pedido.setIdPedido(idPedido);
						insertado = true;
						LOGGER.log(Level.INFO, "Pedido dado de alta. ID = [{}]", idPedido);
					}
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido dar de alta el pedido.\nCausa: {}", e.getMessage());
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
	public boolean actualizar(PedidoDTO pedido)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			boolean modificado = false;
			
			try
			{
				ps = con.prepareStatement(UPDATE);
				if(pedido.getCliente() != null && pedido.getCliente().getIdCliente() > 0)
					ps.setInt(1, pedido.getCliente().getIdCliente());
				else ps.setNull(1, java.sql.Types.INTEGER);
				
				ps.setDate(2, java.sql.Date.valueOf(pedido.getFechaToma()));
				ps.setDate(3, java.sql.Date.valueOf(pedido.getFechaEntrega()));
				ps.setTime(4, java.sql.Time.valueOf(pedido.getHoraToma()));
				ps.setTime(5, java.sql.Time.valueOf(pedido.getHoraEntrega()));
				ps.setString(6, pedido.getProposito());
				ps.setInt(7, UPedido.estadoInt(pedido.getEstado()));
				ps.setString(8, pedido.getForma());
				
				// si el año es distinto a 9999, se coloca el año, sino, se coloca nulo
				if(pedido.getFechaModificacion().getYear() != 9999)
					ps.setTimestamp(9, Timestamp.valueOf(pedido.getFechaModificacion()));
				else ps.setNull(9, java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
				
				ps.setInt(10, pedido.getIdPedido());
				
				if(ps.executeUpdate() == 1)
				{
					modificado = true;
					LOGGER.log(Level.INFO, "Se ha actualizado el pedido. ID = [{}]", pedido.getIdPedido());
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
		
		else return false;
	}

	@Override
	public boolean eliminar(PedidoDTO pedido)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			System.out.println("Entra a eliminar el pedido");
			PreparedStatement ps = null;
			boolean eliminado = false;
			
			try
			{
				ps = con.prepareStatement(DELETE);
				ps.setInt(1, pedido.getIdPedido());
				int res = ps.executeUpdate();
				
				if(res == 1)
				{
					eliminado = true;
					LOGGER.log(Level.INFO, "Se ha eliminado el pedido. ID [{}]", pedido.getIdPedido());
					
					// restamos el auto-incremento
					//descontarAutoIncremento("pedido");
				}
			}
			catch(SQLException e)
			{
				try
				{
					LOGGER.log(Level.ERROR, "No se ha podido eliminar el pedido\nCausa {}", e.getMessage());
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
	public ObservableList<PedidoDTO> obtenerTodos()
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;		
			ObservableList <PedidoDTO> listaPedidos = FXCollections.observableArrayList();
			try
			{
				ps = con.prepareStatement(SELECT_TODOS);
				rs = ps.executeQuery();
				while (rs.next())
				{				
					listaPedidos.add(convertirResultSet(rs));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido cargar los pedidos.\nCausa: {}", e.getMessage());
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
			
			
			return listaPedidos;
		}
		
		else return null;
		
		
	}

	@Override
	public PedidoDTO obtenerPorId(Integer id)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			PedidoDTO pedido = null;
			
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
				LOGGER.log(Level.ERROR, "No se ha podido obtener el pedido.\nCausa: {} ", e.getMessage());
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

	@Override
	public ObservableList<PedidoDTO> obtenerPedidosCliente(int idCliente)
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList <PedidoDTO> listaPedidos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PEDIDOS_CLIENTE);
				ps.setInt(1, idCliente);
				
				rs = ps.executeQuery();
				while(rs.next())
				{
					listaPedidos.add(convertirResultSet(rs));
				}
			}
			catch (SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se ha podido cargar los pedidos del cliente.\nCausa: {} ", e.getMessage());
				return null;
			}
			finally
			{
				try
				{
					if (ps != null) ps.close();
					if (rs != null) rs.close();
				}
				catch (SQLException e)
				{
					LOGGER.log(Level.ERROR, "No se ha podido cerrar la sentencia.\nCausa: {} ", e.getMessage());
					
					return null;
				}
			}
			
			return listaPedidos;
		}
		
		else return null;
		
	}
	
	private PedidoDTO convertirResultSet(ResultSet rs) throws SQLException
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PedidoDTO pedido = null;
			try 
			{
				int idPedido = rs.getInt("idPed");
				int idCliente = rs.getInt("idClie");
				
				// Se obtiene el cliente perteneciente al peddido
				ClienteDAOMySQLImpl clienteDao = (ClienteDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.CLIENTE);
				ClienteDTO cliente = clienteDao.obtenerPorId(idCliente);
						
				LocalDate fechaToma = rs.getDate("fechaToma").toLocalDate();
				LocalDate fechaEntrega = rs.getDate("fechaEntrega").toLocalDate();
				LocalTime horaToma = rs.getTime("horaToma").toLocalTime();
				LocalTime horaEntrega = rs.getTime("horaEntrega").toLocalTime();
				String proposito = rs.getString("proposito");
				String estado = UPedido.estadoString(rs.getInt("estado"));
				String forma = rs.getString("forma");
				LocalDateTime fm = null;
				
				if(rs.getTimestamp("fechaModificacion") != null)
					fm = rs.getTimestamp("fechaModificacion").toLocalDateTime();
				else fm = LocalDateTime.of(9999, 9, 9, 00, 00, 00);
				
				pedido = new PedidoDTO(idPedido, cliente, fechaToma, fechaEntrega, horaToma, horaEntrega, proposito, estado, forma, fm);
			} 
			catch (Exception e) 
			{
				LOGGER.log(Level.ERROR, "Error al convertir ResultSet.\nCausa: {} ", e.getMessage());
				
				return null;
			}
					
			return pedido;
		}
		
		else return null;
		
	}

	@Override
	public ObservableList<PedidoDTO> obtenerPedidosStock() 
	{
		con = Conexion.tomarConexion();
		
		if(con != null)
		{
			PreparedStatement ps = null;
			ResultSet rs = null;
			ObservableList <PedidoDTO> listaPedidos = FXCollections.observableArrayList();
			
			try
			{
				ps = con.prepareStatement(SELECT_PEDIDOS_STOCK);
				rs = ps.executeQuery();
				while(rs.next())
				{
					listaPedidos.add(convertirResultSet(rs));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtener los pedidos de stock.\nCausa: {}", e.getMessage());
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
					LOGGER.log(Level.ERROR, "No se ha cerra la sentencia.\nCausa: {}", e.getMessage());
				}
			}
			return listaPedidos;
		}
		
		else return null;
	}

	@Override
	public PedidoDTO obtener(PedidoDTO v) 
	{
		// TODO Auto-generated method stub
		return null;
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
				ps = con.prepareStatement(SELECT_DISTINCT_ANIOS);
				rs = ps.executeQuery();
				while(rs.next())
				{
					listaAnios.add(String.valueOf(rs.getInt("anio")));
				}
			}
			catch(SQLException e)
			{
				LOGGER.log(Level.ERROR, "No se han podido obtener los años.\nCausa: {}", e.getMessage());
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

	public void descontarAutoIncremento(String nombreTabla)
	{
		con = Conexion.tomarConexion();
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		
		int autoIncremento = 0;
		try
		{
			ps = con.prepareStatement("SELECT AUTO_INCREMENT FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");
			ps.setString(1, "aserradero_bd");
			ps.setString(2, nombreTabla);
			rs = ps.executeQuery();
			if (rs.next())
			{
				autoIncremento = rs.getInt("auto_increment");
				autoIncremento--;
				
				ps2 = con.prepareStatement("ALTER TABLE ? AUTO_INCREMENT = ?");
				ps2.setString(1, nombreTabla);
				ps2.setInt(2, autoIncremento);
				
				int res = ps2.executeUpdate();
				if(res == 1)
				{
					LOGGER.log(Level.ERROR, "No se ha descontado el auto-incremento. Tabla [{}].\nCausa: {}", nombreTabla);
					
					System.out.println("AUTO_INCREMENT --> " + autoIncremento);
				}
			}
		
			

		}
		catch(SQLException e)
		{
			LOGGER.log(Level.ERROR, "No se ha podido descontar el auto-incremento. Tabla [{}].\nCausa: {}", nombreTabla, e.getMessage());
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
