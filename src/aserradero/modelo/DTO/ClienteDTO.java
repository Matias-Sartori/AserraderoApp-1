package aserradero.modelo.DTO;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClienteDTO  
{
	private IntegerProperty idCliente;
	private StringProperty nombre;
	private StringProperty apellido;
	private StringProperty telefono;
	private StringProperty dni;
	private StringProperty cuit;
	private StringProperty razonSocial;
	private StringProperty eMail;
	private ObjectProperty<LocalidadDTO> localidad;
	private StringProperty direccion;
	private BooleanProperty habilitado;
	
	public ClienteDTO()
	{
		super();
		this.idCliente = new SimpleIntegerProperty(0);
		this.nombre = new SimpleStringProperty("");
		this.apellido = new SimpleStringProperty("");
		this.telefono = new SimpleStringProperty("");
		this.dni = new SimpleStringProperty("");
		this.cuit = new SimpleStringProperty("");
		this.razonSocial = new SimpleStringProperty("");
		this.direccion = new SimpleStringProperty("");
		this.eMail = new SimpleStringProperty("");
		this.localidad = new SimpleObjectProperty<LocalidadDTO>(new LocalidadDTO());
		this.habilitado = new SimpleBooleanProperty(true);
	}
	
	public ClienteDTO(String n, String ape)
	{
		super();
		this.idCliente = new SimpleIntegerProperty(0);
		this.nombre = new SimpleStringProperty(n);
		this.apellido = new SimpleStringProperty(ape);
		this.telefono = new SimpleStringProperty("");
		this.dni = new SimpleStringProperty("");
		this.cuit = new SimpleStringProperty("");
		this.razonSocial = new SimpleStringProperty("");
		this.direccion = new SimpleStringProperty("");
		this.eMail = new SimpleStringProperty("");
		this.localidad = new SimpleObjectProperty<>(new LocalidadDTO());
		this.habilitado = new SimpleBooleanProperty(true);
	}
	
	public ClienteDTO(int id, String n, String a, String tel, String dni, String cuit, String razon, String dir, String em, boolean ac)
	{
		super();
		this.idCliente = new SimpleIntegerProperty(id);
		this.nombre = new SimpleStringProperty(n);
		this.apellido = new SimpleStringProperty(a);
		this.telefono = new SimpleStringProperty(tel);
		this.dni = new SimpleStringProperty(dni);
		this.cuit = new SimpleStringProperty(cuit);
		this.razonSocial = new SimpleStringProperty(razon);
		this.direccion = new SimpleStringProperty(dir);
		this.eMail = new SimpleStringProperty(em);
		this.localidad = new SimpleObjectProperty<>(new LocalidadDTO());
		this.habilitado = new SimpleBooleanProperty(ac);		
	}
	
	//SOBREESCRITURA DE METODOS
	
	public String toString()
	{
		return this.nombre.get() + " " + this.apellido.get();
		
		// nombre + razon (si la tiene)
		//return this.nombre.get() + " " + this.apellido.get() + (!razonSocial.get().isEmpty() ? " | " + razonSocial.get() : "");
	}

	public boolean equals(Object obj)
	{
		boolean ret = false;
		
		if(obj instanceof ClienteDTO)
		{
			ClienteDTO c = (ClienteDTO) obj;
			
			if (this.nombre.get().equals(c.getNombre()) 
					&& this.apellido.get().equals(c.getApellido())
					&& this.telefono.get().equals(c.getTelefono())
					&& this.dni.get().equals(c.getDni()) 
					&& this.direccion.get().equals(c.getDireccion())
					&& this.localidad.get().getNombreLocalidad().equals(c.getLocalidad().getNombreLocalidad())
					&& this.razonSocial.get().equals(c.getRazonSocial())
					&& this.cuit.get().equals(c.getCuit())
					&& this.eMail.get().equals(c.getEMail()))
			{
				ret = true;
			}
			else ret = false;
		}
		else ret = false;
		
		return ret;
	}
	
	// PROPIEDADES
	public IntegerProperty idPropiedad()
	{
		return idCliente;
	}
	
	public StringProperty nombrePropiedad()
	{
		return nombre;
	}
	
	public StringProperty apellidoPropiedad()
	{
		return apellido;
	}
	
	public StringProperty nombreApellidoPropiedad()
	{
		return new SimpleStringProperty(this.toString());
	}
	
	public StringProperty telefonoPropiedad()
	{
		return telefono;
	}
	
	public StringProperty dniPropiedad()
	{
		return dni;
	}
	
	public StringProperty cuitPropiedad()
	{
		return cuit;
	}
	
	public StringProperty razonSocialPropiedad()
	{
		return razonSocial;
	}
	
	public ObjectProperty <LocalidadDTO> localidadPropiedad()
	{
		return localidad;
	}
	
	public StringProperty direccionPropiedad()
	{
		return direccion;
	}
	
	public StringProperty eMailPropiedad()
	{
		return eMail;
	}
	
	public BooleanProperty habilitadoPropiedad()
	{
		return habilitado;
	}
	
	// GETTERS Y SETTERS
	public int getIdCliente()
	{
		return idCliente.get();
	}
	
	public void setIdCliente(int id)
	{
		this.idCliente.set(id);
	}
	
	public String getNombre()
	{
		return nombre.get();
	}
	
	public void setNombre(String n)
	{
		this.nombre.set(n);
	}
	
	public String getApellido()
	{
		return apellido.get();
	}
	
	public void setApellido(String a)
	{
		this.apellido.set(a);
	}
	
	public String getTelefono()
	{
		return telefono.get();
	}
	
	public void setTelefono(String t)
	{
		if(t == null)
		{
			this.telefono.set("");
		}
		else this.telefono.set(t);
	}
	
	public String getDni()
	{
		return dni.get();
	}
	
	public void setDni(String dni)
	{
		if(dni == null) this.dni.set("");
		else this.dni.set(dni);
	}
	
	public String getCuit()
	{
		return cuit.get();
	}
	
	public void setCuit(String cuit)
	{
		if(cuit == null) this.cuit.set("");
		else this.cuit.set(cuit);
	}
	
	public String getRazonSocial()
	{
		return razonSocial.get();
	}
	
	public void setRazonSocial(String r)
	{
		if(r == null) this.razonSocial.set("");
		else this.razonSocial.set(r);
	}
	
	public String getDireccion()
	{
		return direccion.get();
	}
	
	public void setDireccion(String d)
	{
		if (d == null) this.direccion.set("");
		else this.direccion.set(d);
	}
	
	public String getEMail()
	{
		return eMail.get();
	}
	
	public void setEMail(String e)
	{
		this.eMail.set(e);
	}
	
	public void setLocalidad(LocalidadDTO l)	
	{
		this.localidad.set(l);
	}
	
	public LocalidadDTO getLocalidad()
	{
		return localidad.get();
	}
	
	public void setHabilitado(boolean v)
	{
		this.habilitado.set(v);
	}
	
	public boolean getHabilitado()
	{
		return habilitado.get();
	}
}
