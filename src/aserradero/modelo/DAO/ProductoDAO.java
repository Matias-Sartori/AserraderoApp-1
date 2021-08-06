package aserradero.modelo.DAO;

import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.clases.VentasAnio;
import javafx.collections.ObservableList;

public interface ProductoDAO extends IDAO<ProductoDTO, Integer>
{
	public ObservableList<ProductoDTO> obtenerProductosPedido(int idPedido);
	
	public ObservableList<ProductoDTO> obtenerProductosEspecificados(int idPedido);
	
	public ObservableList<VentasAnio> obtenerProductosVendidos();
	
	public ObservableList<ProductoDTO> obtenerProductosProduccion(int idPedido);
	
	public ObservableList<ProductoDTO> obtenerProductosPedidoClasificado(int idPedido);
}
