package aserradero.modelo.DAO;

import aserradero.modelo.DTO.PedidoDTO;
import aserradero.modelo.clases.VentasAnio;
import javafx.collections.ObservableList;

public interface PedidoDAO extends IDAO <PedidoDTO, Integer>
{
	public ObservableList <VentasAnio> obtenerPedidosVendidos();
	
	/**
	 * Método encargado de obtener los pedidos pertenecientes a los empleados (pedidos no stock).
	 * @return Lista de pedidos de empleados.
	 */
	public ObservableList <PedidoDTO> obtenerPedidosEmpleado();
	/**
	 * Método encargado de buscar todos los pedidos pertenecientes al ID del cliente pasado como parámetro.
	 * @param idCliente - ID del cliente.
	 * @return Lista de pedidos del cliente.
	 */
	public ObservableList <PedidoDTO> obtenerPedidosCliente(int idCliente);
	
	public ObservableList <PedidoDTO> obtenerPedidosStock();
	
	public ObservableList <String> obtenerTodosLosAnios();
}
