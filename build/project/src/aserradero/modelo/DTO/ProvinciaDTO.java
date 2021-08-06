package aserradero.modelo.DTO;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProvinciaDTO
{
	private IntegerProperty idProvincia;
	private StringProperty nombreProvincia;
	
	public ProvinciaDTO()
	{
		this.idProvincia = new SimpleIntegerProperty(0);
		this.nombreProvincia = new SimpleStringProperty("");
	}
	
	public ProvinciaDTO(int id, String p)
	{
		this.idProvincia = new SimpleIntegerProperty(id);
		this.nombreProvincia = new SimpleStringProperty(p);
	}
	
	public String toString()
	{
		return nombreProvincia.get();
	}
	
	public IntegerProperty idProvinciaPropiedad()
	{
		return idProvincia;
	}
	
	public StringProperty nombreProvinciaPropiedad()
	{
		return nombreProvincia;
	}
	
	public int getIdProvincia()
	{
		return idProvincia.get();
	}
	
	public void setIdProvincia(int id)
	{
		this.idProvincia.set(id);
	}
	
	public String getnombreProvincia()
	{
		return nombreProvincia.get();
	}

	public void setnombreProvincia(String p)
	{
		this.nombreProvincia.set(p);
	}
}
