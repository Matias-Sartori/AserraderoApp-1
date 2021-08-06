package aserradero.modelo.DTO;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LocalidadDTO 
{
	private IntegerProperty idLocalidad;
	private StringProperty nombreLocalidad;
	private ObjectProperty <ProvinciaDTO> provincia;
	
	public LocalidadDTO()
	{
		this.idLocalidad = new SimpleIntegerProperty(0);
		this.nombreLocalidad = new SimpleStringProperty("");
		this.provincia = new SimpleObjectProperty<>(new ProvinciaDTO());
	}

	public LocalidadDTO(String loc)
	{
		this.idLocalidad = new SimpleIntegerProperty(0);
		this.nombreLocalidad = new SimpleStringProperty(loc);
		this.provincia = new SimpleObjectProperty<>(null);
	}
	
	public LocalidadDTO(ProvinciaDTO provincia)
	{
		this.idLocalidad = new SimpleIntegerProperty(0);
		this.nombreLocalidad = new SimpleStringProperty("");
		this.provincia = new SimpleObjectProperty<>(provincia);
	}
	
	public LocalidadDTO(int id, String nom, ProvinciaDTO provincia)
	{
		this.idLocalidad = new SimpleIntegerProperty(id);
		this.nombreLocalidad = new SimpleStringProperty(nom);
		this.provincia = new SimpleObjectProperty<>(provincia);
	}

	public String toString()
	{
		return nombreLocalidad.get();
	}
	
	public IntegerProperty idLocalidadPropiedad()
	{
		return idLocalidad;
	}
	
	public StringProperty localidadPropiedad()
	{
		return nombreLocalidad;
	}
	
	public ObjectProperty <ProvinciaDTO> provinciaPropiedad()
	{
		return provincia;
	}
	
	public int getIdLocalidad()
	{
		return idLocalidad.get();
	}
	
	public void setIdLocalidad(int id)
	{
		this.idLocalidad.set(id);
	}
	
	public String getNombreLocalidad()
	{
		return nombreLocalidad.get();
	}
	
	public void setNombreLocalidad(String l)
	{
		this.nombreLocalidad.set(l);
	}
	
	public ProvinciaDTO getProvincia()
	{
		return provincia.get();
	}
	
	public void setProvincia(ProvinciaDTO p)
	{
		this.provincia.set(p);
	}
}
