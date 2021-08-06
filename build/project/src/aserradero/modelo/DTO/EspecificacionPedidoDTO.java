package aserradero.modelo.DTO;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EspecificacionPedidoDTO 
{
	private ObjectProperty <PedidoDTO> pedido;
	private ObservableList <ProductoDTO> productos;
	
	public EspecificacionPedidoDTO()
	{
		this.pedido = new SimpleObjectProperty <>(null);
		this.productos = FXCollections.observableArrayList();
	}
	
	public EspecificacionPedidoDTO(PedidoDTO pedido, ObservableList<ProductoDTO> productos)
	
	{
		this.pedido = new SimpleObjectProperty <>(pedido);
		this.productos = FXCollections.observableArrayList(productos);
	}
	
	public  ObjectProperty <PedidoDTO> pedidoPropiedad()
	{
		return pedido;
	}
	
	
	public void setPedido(PedidoDTO p)
	{
		this.pedido.set(p); 
	}
	
	public PedidoDTO getPedido()
	{
		return pedido.get();
	}
	
	public void setProductos(ObservableList<ProductoDTO> productos)
	{
		this.productos = productos;
	}
	
	public ObservableList<ProductoDTO> getProductos()
	{
		return productos;
	}
}
