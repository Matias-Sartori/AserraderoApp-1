package aserradero.modelo.clases;

import java.time.Year;

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

public class ProduccionAnio
{
	private ObjectProperty<Year> anio;
	private ObservableList<ProduccionMes> produccionesMes;
	private IntegerProperty cantidad;
	private DoubleProperty totalPies;
	private StringProperty totalCantidadPies;
	
	public ProduccionAnio()
	{
		this.anio = new SimpleObjectProperty<>();
		this.produccionesMes = FXCollections.observableArrayList();
		this.cantidad = new SimpleIntegerProperty(0);
		this.totalPies = new SimpleDoubleProperty(0);
		this.totalCantidadPies = new SimpleStringProperty();
	}
	
	public ProduccionAnio(int anio, ObservableList<ProduccionMes> produccionesMes)
	{
		this.anio = new SimpleObjectProperty<>(Year.of(anio));
		this.produccionesMes = FXCollections.observableArrayList(produccionesMes);
		this.cantidad = new SimpleIntegerProperty(0);
		this.totalPies = new SimpleDoubleProperty(0);
		this.totalCantidadPies = new SimpleStringProperty();
		
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
	
	public ObservableList<ProduccionMes> getProduccionesMes()
	{
		return this.produccionesMes;
	}

	public void setProduccionesMes(ObservableList<ProduccionMes> producciones)
	{
		this.produccionesMes = producciones;
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

	public void calcularTotal()
	{
		int c = 0;
		double pies = 0;
		
		for(ProduccionMes p : this.produccionesMes)
		{
			c += p.getCantidad();
			
			pies += p.getTotalPies();
		}
		
		this.cantidad.set(c);
		
		this.totalPies.set(pies);
		
		this.totalCantidadPies.set(cantidad + " | " + totalPies);
	}
}
