package aserradero.modelo.DAO;

import aserradero.modelo.DTO.DetallePedidoDTO;
import aserradero.modelo.DTO.ProductoDTO;

public interface DetallePedidoDAO extends IDAO <DetallePedidoDTO, Integer>
{
	ProductoDTO obtenerProductoPedido(int idPedido, int idProducto);
}
