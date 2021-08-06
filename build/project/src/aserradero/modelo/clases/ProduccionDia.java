package aserradero.modelo.clases;

import java.time.LocalDate;
import java.time.LocalTime;

import aserradero.modelo.DTO.ProductoDTO;
import aserradero.util.UProducto;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Clase que representa a la sesión de un día de aserrado en concreto.
 * Colecciona sus productosIndividuales pertinentes, la cantidad total de productosIndividuales, y la fecha y duración aproximada de la sesión.
 * @author King
 *
 */
public class ProduccionDia 
{
	private ObjectProperty <LocalDate> fecha;
	private ObservableList<ProductoDTO> productosIndividuales;
	private ObservableList<ProductoDTO> productosClasificados;
	private IntegerProperty cantidad;
	private DoubleProperty pies;
	private StringProperty cantidadPies;
	private ObjectProperty <LocalTime> duracion;
	
	public ProduccionDia()
	{
		this.fecha = new SimpleObjectProperty <>(null);
		this.productosIndividuales = FXCollections.observableArrayList();
		this.productosClasificados = FXCollections.observableArrayList();
		this.cantidad = new SimpleIntegerProperty(0);
		this.pies = new SimpleDoubleProperty(0);
		this.cantidadPies = new SimpleStringProperty("");
		this.duracion = new SimpleObjectProperty <>(null);
	}
	
	// PROPIEDADES
	
	public ObjectProperty <LocalDate> fechaPropiedad()
	{
		return fecha;
	}
	
	public IntegerProperty cantidadPropiedad()
	{
		return cantidad;
	}
	
	public DoubleProperty piesPropiedad()
	{
		return pies;
	}
	
	public StringProperty cantidadPiesPropiedad()
	{
		return cantidadPies;
	}
	
	public ObjectProperty <LocalTime> duracionPropiedad()
	{
		return duracion;
	}
	
	// GETTERS AND SETTERS
	
	public void setFecha(LocalDate f)
	{
		this.fecha.set(f);
	}
	
	public LocalDate getFecha()
	{
		return fecha.get();
	}
	
	public void setProductosIndividuales(ObservableList<ProductoDTO> productos)
	{
		this.productosIndividuales = productos;
	}
	
	public ObservableList<ProductoDTO> getProductosIndividuales()
	{
		return productosIndividuales;
	}
	
	public void setProductosClasificados(ObservableList<ProductoDTO> productos)
	{
		this.productosClasificados = productos;
	}
	
	public ObservableList<ProductoDTO> getProductosClasificados()
	{
		return productosClasificados;
	}
	
	public void setCantidad(int c)
	{
		this.cantidad.set(UProducto.calcularTotalMaderas(this.productosIndividuales));
	}
	
	public double getCantidad()
	{
		return this.cantidad.get();
	}
	public void setPies()
	{
		this.pies.set(UProducto.calcularTotalPies(this.productosIndividuales));
	}
	
	public double getPies()
	{
		return this.pies.get();
	}
	
	/*public void setCantidadPies()
	{
		// Solucion momentanea
		
	}*/
	
	public String getCantidadPies()
	{
		return cantidadPies.get();
	}
	
	public void setDuracion(LocalTime d)
	{
		this.duracion.set(d);
	}
	
	public LocalTime getDuracion()
	{
		return duracion.get();
	}
	
	public void calcularTotal()
	{
		int c = 0;
		double p = 0;
		
		for(ProductoDTO prod : this.productosIndividuales)
		{
			c += prod.getStock();
			
			p += prod.getPies();
		}
		
		this.cantidad.set(c);
		this.pies.set(p);
		this.cantidadPies.set(cantidad.get() + " | " + pies.get());
	}
}
