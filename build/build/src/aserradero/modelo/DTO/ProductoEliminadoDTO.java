package aserradero.modelo.DTO;

import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProductoEliminadoDTO 
{
	private IntegerProperty idProductoEliminado;
	private ObjectProperty <ProductoDTO> producto;
	private StringProperty motivo;
	private ObjectProperty <LocalDateTime> fechaEliminacion;
	
	public ProductoEliminadoDTO()
	{
		this.idProductoEliminado = new SimpleIntegerProperty(0);
		this.producto = new SimpleObjectProperty <> (null);

		this.motivo = new SimpleStringProperty(null);

		this.fechaEliminacion = new SimpleObjectProperty<>(null);
	}
	
	public ProductoEliminadoDTO(int idP, ProductoDTO p, String m, LocalDateTime fm)
	{
		this.idProductoEliminado = new SimpleIntegerProperty(idP);
		this.producto = new SimpleObjectProperty <ProductoDTO> (p);

		this.motivo = new SimpleStringProperty(m);

		this.fechaEliminacion = new SimpleObjectProperty<>(fm);
	}
	
	// SOBREESRITURA DE METODOS
	
	/*public boolean equals(ProductoEliminadoDTO p)
	{
		if(this.idProductoEliminado.get() != p.getIdPedido()
				|| this.producto.get().getIdProducto() != p.getIdProducto().getIdCliente()
				)
		{
			return false;
		}
		else return true;
	}*/
	
	// PROPIEDADES 
	
	public IntegerProperty idProductoEliminadoPropiedad()
	{
		return idProductoEliminado;
	}
	
	public ObjectProperty <ProductoDTO> productoPropiedad()
	{
		return producto;
	}
	
	public StringProperty motivoPropiedad()
	{
		return motivo;
	}
	
	
	public ObjectProperty <LocalDateTime> fechaEliminacionPropiedad()
	{
		return fechaEliminacion;
	}
	
	// SETTERS AND GETTERS
	
	public void setIdProductoEliminado(int id)
	{
		this.idProductoEliminado.set(id);
	}
	
	public int getIdProductoEliminado()
	{
		return idProductoEliminado.get();
	}
	
	public void setProducto(ProductoDTO producto)
	{
		this.producto.set(producto);
	}
	
	public ProductoDTO getProducto()
	{
		return producto.get();
	}
	
	public String getMotivo()
	{
		return this.motivo.get();
	}
	
	public void setMotivo(String m)
	{
		this.motivo.set(m);
	}

	
	public void setFechaEliminacion(LocalDateTime f)
	{
		this.fechaEliminacion.set(f);
	}
	
	public LocalDateTime getFechaModificacion()
	{
		return this.fechaEliminacion.get();
	}
}
