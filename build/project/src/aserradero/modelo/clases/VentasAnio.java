package aserradero.modelo.clases;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

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

public class VentasAnio 
{
	private ObjectProperty<Year> anio;
	private ObservableList<VentasMes> ventasMeses;
	private IntegerProperty cantidad;
	private DoubleProperty totalPies;
	private StringProperty totalCantidadPies;
	private ObservableList<ProductoDTO> productosTotales;

	
	public VentasAnio()
	{
		this.anio = new SimpleObjectProperty<>();
		this.ventasMeses = FXCollections.observableArrayList();
		this.cantidad = new SimpleIntegerProperty(0);
		this.totalPies = new SimpleDoubleProperty(0);
		this.totalCantidadPies = new SimpleStringProperty();
		this.productosTotales = FXCollections.observableArrayList();
	}
	
	public VentasAnio(int anio, ObservableList<VentasMes> ventasMeses)
	{
		this.anio = new SimpleObjectProperty<>(Year.of(anio));
		this.ventasMeses = FXCollections.observableArrayList(ventasMeses);
		this.cantidad = new SimpleIntegerProperty(0);
		this.totalPies = new SimpleDoubleProperty(0);
		this.totalCantidadPies = new SimpleStringProperty();
		this.productosTotales = FXCollections.observableArrayList();
		
		calcularTotal();
	}

	public ObjectProperty<Year> anioProperty() 
	{
		return this.anio;
	}
	
	public IntegerProperty cantidadPropiedad()
	{
		return this.cantidad;
	}

	public DoubleProperty totalPiesProperty() 
	{
		return this.totalPies;
	}
	

	public StringProperty totalCantidadPiesProperty() 
	{
		return this.totalCantidadPies;
	}
	
	public Year getAnio() 
	{
		return this.anioProperty().get();
	}
	

	public void setAnio(Year anio) 
	{
		this.anioProperty().set(anio);
	}
	
	public ObservableList<VentasMes> getProduccionesMes()
	{
		return this.ventasMeses;
	}

	public void setProduccionesMes(ObservableList<VentasMes> producciones)
	{
		this.ventasMeses = producciones;
	}
	
	public int getCantidad()
	{
		return this.cantidad.get();
	}
	
	public void setCantidad(int c)
	{
		this.cantidad.set(c);
	}
	
	public double getTotalPies() 
	{
		return this.totalPiesProperty().get();
	}
	

	public void setTotalPies(final double totalPies) 
	{
		this.totalPiesProperty().set(totalPies);
	}
	
	public String getTotalCantidadPies() 
	{
		return this.totalCantidadPiesProperty().get();
	}

	public void setTotalCantidadPies(final String totalCantidadPies) 
	{
		this.totalCantidadPiesProperty().set(totalCantidadPies);
	}
	
	public ObservableList<ProductoDTO> getProductosTotales()
	{
		return this.productosTotales;
	}

	public void calcularTotal()
	{
		
		int cant = 0;
		double pies = 0;
		
		for(VentasMes ventaMes : this.ventasMeses)
		{
			this.productosTotales.addAll(UProducto.clonarProductos(ventaMes.getProductosMes()));
			
			cant += ventaMes.getCantidad();
			
			pies += ventaMes.getTotalPies();
		}
		
		// recorremos la lista de los productos totales
		for(int i = 0; i < this.productosTotales.size(); i++)
		{
			List<Integer> indices = new ArrayList<>();
			ProductoDTO p = productosTotales.get(i);
			
			// recorremos los productos totales a partir del segundo lugar
			for(int i2 = i + 1; i2<this.productosTotales.size()-1; i2++)
			{
				ProductoDTO prodSiguiente = productosTotales.get(i2);
				
				if(p.equals(prodSiguiente))
				{
					p.setStock(p.getStock() + prodSiguiente.getStock());
					
					this.productosTotales.remove(i2);
					
				}
			}
			
			p.setPies();
		}
		
		this.cantidad.set(cant);
		
		this.totalPies.set(UProducto.formatearMedida(pies, UProducto.DOS_DECIMALES));
		
		this.totalCantidadPies.set(cantidad + " | " + totalPies);
	}
}
