package aserradero.modelo.DAO;

import aserradero.modelo.DTO.EspecificacionPedidoDTO;
import aserradero.modelo.DTO.ProductoDTO;

public interface EspecificacionPedidoDAO extends IDAO<EspecificacionPedidoDTO, Integer>
{
	ProductoDTO obtenerProductoEspecificado(int idPedido, int idProducto);
}
