package aserradero.modelo.DTO;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ProduccionDTO
{
	private IntegerProperty idProduccion;
	private ObjectProperty <ProductoDTO> producto;
	private IntegerProperty idPedido;
	private ObjectProperty <LocalDate> fechaCarga;
	private ObjectProperty <LocalTime> horaCarga;
	
	public ProduccionDTO()
	{
		this.idProduccion = new SimpleIntegerProperty(0);
		this.idPedido = new SimpleIntegerProperty(0);
		this.producto = new SimpleObjectProperty <ProductoDTO>(null);
		this.fechaCarga = new SimpleObjectProperty <LocalDate>(null);
		this.horaCarga = new SimpleObjectProperty <LocalTime>(null);
	}
	
	public ProduccionDTO(int idProduccion, int idPedido, ProductoDTO p, LocalDate fecha, LocalTime hora)
	{
		this.idProduccion = new SimpleIntegerProperty(idProduccion);
		this.idPedido = new SimpleIntegerProperty(idPedido);
		this.producto = new SimpleObjectProperty <ProductoDTO>(p);
		this.fechaCarga = new SimpleObjectProperty <LocalDate>(fecha);
		this.horaCarga = new SimpleObjectProperty <LocalTime>(hora);
	}
	
	public ProduccionDTO(int idPedido, ProductoDTO p, LocalDate fecha, LocalTime hora)
	{
		this.idProduccion = new SimpleIntegerProperty(0);
		this.idPedido = new SimpleIntegerProperty(idPedido);
		this.producto = new SimpleObjectProperty <ProductoDTO>(p);
		this.fechaCarga = new SimpleObjectProperty <LocalDate>(fecha);
		this.horaCarga = new SimpleObjectProperty <LocalTime>(hora);
	}
	
	public ProduccionDTO(LocalDate fecha, LocalTime hora)
	{
		this.idProduccion = new SimpleIntegerProperty(0);
		this.producto = new SimpleObjectProperty <ProductoDTO>();
		this.idPedido = new SimpleIntegerProperty(0);
		this.fechaCarga = new SimpleObjectProperty <LocalDate>(fecha);
		this.horaCarga = new SimpleObjectProperty <LocalTime>(hora);
	}
	
	// SOBREESCRITURA DE METODOS
	
	public String toString()
	{
		return "ID Produccion: " + this.getIdProduccion() + " - ID Pedido: " + this.getIdPedido() + " - ID Producto: " + this.getProducto().getIdProducto()
				+ " - FechaCarga: " + this.getFechaCarga() + " - HoraCarga: " + this.getHoraCarga();
	}
	
	public IntegerProperty id_produccionPropiedad()
	{
		return idProduccion;
	}
	
	public ObjectProperty <ProductoDTO> productoPropiedad()
	{
		return producto;
	}
	
	public IntegerProperty idPedidoPropiedad()
	{
		return idPedido;
	}

	public ObjectProperty <LocalDate> fechaCargaPropiedad()
	{
		return fechaCarga;
	}
	
	public ObjectProperty <LocalTime> horaPropiedad()
	{
		return horaCarga;
	}
	
	public void setIdProduccion(int id)
	{
		this.idProduccion.set(id);
	}
	
	public int getIdProduccion()
	{
		return idProduccion.get();
	}
	
	public void setProducto(ProductoDTO p)
	{
		this.producto.set(p);
	}
	
	public ProductoDTO getProducto()
	{
		return producto.get();
	}
	
	public void setIdPedido(int id)
	{
		this.idPedido.set(id);
	}
	
	public int getIdPedido()
	{
		return idPedido.get();
	}
	
	public void setFechaCarga(LocalDate f)
	{
		this.fechaCarga.set(f);
	}
	
	public LocalDate getFechaCarga()
	{
		return fechaCarga.get();
	}
	
	public void setHoraCarga(LocalTime h)
	{
		this.horaCarga.set(h);
	}
	
	public LocalTime getHoraCarga()
	{
		return horaCarga.get();
	}

}

