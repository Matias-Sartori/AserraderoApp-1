package aserradero.modelo.clases;

import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Locale;

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

public class VentasMes 
{
	private ObjectProperty<Month> mes;
	private ObjectProperty<Year> anio;
	private ObservableList<ProductoDTO> productosMes;
	private IntegerProperty cantidad;
	private DoubleProperty totalPies;
	private StringProperty totalCantidadPies;
	
	
	public VentasMes(Month m, Year a)
	{
		this.mes = new SimpleObjectProperty<>(m);
		this.anio = new SimpleObjectProperty<>(a);
		this.productosMes = FXCollections.observableArrayList();
		this.cantidad = new SimpleIntegerProperty(0);
		this.totalPies = new SimpleDoubleProperty(0);
		this.totalCantidadPies = new SimpleStringProperty();
	}
	
	// METODOS SOBREESCRITOS
	
	public String toString()
	{
		return "Mes: " + mes + " | " + "Año: " + anio;
	}
	
	// PROPIEDADES
	
	public StringProperty mesPropiedad()
	{
		// Se traduce el mes a español
		return new SimpleStringProperty(this.mes.get().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase());
	}
	
	public ObjectProperty<Year> anioPropiedad()
	{
		return anio;
	}
	
	
	public IntegerProperty cantidadPropiedad()
	{
		return cantidad;
	}
	
	public DoubleProperty totalPiesPropiedad()
	{
		return totalPies;
	}
	
	public StringProperty totalCantidadPiesPropiedad()
	{
		return totalCantidadPies;
	}
	
	// GETTERS Y SETTERS
	
	public Month getMes()
	{
		return mes.get();
	}
	
	public void setMes(Month m)
	{
		this.mes.set(m);
	}
	
	public Year getAnio()
	{
		return anio.get();
	}

	public void setAnio(Year a)
	{
		this.anio.set(a);
	}
	
	public ObservableList<ProductoDTO> getProductosMes()
	{
		return productosMes;
	}
	
	public void setProductosMes(ObservableList<ProductoDTO> p)
	{
		this.productosMes = p;
	}
	
	public int getCantidad()
	{
		return cantidad.get();
	}
	
	public void setCantidad(int c)
	{
		this.cantidad.set(c);
	}
	
	public double getTotalPies()
	{
		return totalPies.get();
	}
	
	public void setTotalPies(String t)
	{
		this.totalCantidadPies.set(t);
	}
	
	public String getTotalCantidadPies()
	{
		return totalCantidadPies.get();
	}
	
	public void setTotalCantidadPies(String t)
	{
		this.totalCantidadPies.set(t);
	}
	
	//
	
	public void calcularTotal()
	{
		int totalMaderas = 0;
		double pies = 0d;
		
		// recorremos la lista de producciones dia
		
		System.out.println("VENTA MES  " + this.getMes().getValue() + " __ CALCULAR TOTAL _");
		System.out.println("\t PRODUCTOS ---> " + productosMes);
		
		totalMaderas = UProducto.calcularTotalMaderas(productosMes);
		
		pies = UProducto.formatearMedida(UProducto.calcularTotalPies(productosMes), UProducto.DOS_DECIMALES);
		
		System.out.println("\t TOTAL MADERAS ---> " + totalMaderas);
		System.out.println("\t TOTAL PIES ---> " + pies);
		
		this.cantidad.set(totalMaderas);
		
		this.totalPies.set(pies);
		
		this.totalCantidadPies.set(totalMaderas + " | " + pies);
	}
}
