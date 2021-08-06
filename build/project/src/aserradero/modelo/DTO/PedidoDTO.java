package aserradero.modelo.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PedidoDTO 
{
	private IntegerProperty idPedido;
	private ObjectProperty <ClienteDTO> cliente;
	private ObjectProperty <LocalDate> fechaToma;
	private ObjectProperty <LocalDate> fechaEntrega;
	private ObjectProperty <LocalTime> horaToma;
	private ObjectProperty <LocalTime> horaEntrega;
	private StringProperty proposito;
	private StringProperty estado;
	private StringProperty forma;
	private ObjectProperty <LocalDateTime> fechaModificacion;
	
	public PedidoDTO()
	{
		this.idPedido = new SimpleIntegerProperty(0);
		this.cliente = new SimpleObjectProperty <> (null);
		this.fechaToma = new SimpleObjectProperty<>(null);
		this.fechaEntrega = new SimpleObjectProperty<>(null);
		this.horaToma = new SimpleObjectProperty<>(null);
		this.horaEntrega = new SimpleObjectProperty<>(null);
		this.proposito = new SimpleStringProperty(null);
		this.estado = new SimpleStringProperty(null);
		this.forma = new SimpleStringProperty(null);
		this.fechaModificacion = new SimpleObjectProperty<>(null);
	}
	
	public PedidoDTO(int idP, ClienteDTO c, LocalDate fechaT, LocalDate fechaE, LocalTime horaT, LocalTime horaE, String p, String e, String f, LocalDateTime fm)
	{
		this.idPedido = new SimpleIntegerProperty(idP);
		this.cliente = new SimpleObjectProperty <ClienteDTO> (c);
		this.fechaToma = new SimpleObjectProperty<LocalDate>(fechaT);
		this.fechaEntrega = new SimpleObjectProperty<LocalDate>(fechaE);
		this.horaToma = new SimpleObjectProperty<LocalTime>(horaT);
		this.horaEntrega = new SimpleObjectProperty<LocalTime>(horaE);
		this.proposito = new SimpleStringProperty(p);
		this.estado = new SimpleStringProperty(e);
		this.forma = new SimpleStringProperty(f);
		this.fechaModificacion = new SimpleObjectProperty<>(fm);
	}
	
	// SOBREESRITURA DE METODOS
	
	public boolean equals(PedidoDTO p)
	{
		if(this.idPedido.get() != p.getIdPedido()
				|| this.cliente.get().getIdCliente() != p.getCliente().getIdCliente()
				)
		{
			return false;
		}
		else return true;
	}
	
	// PROPIEDADES 
	
	public IntegerProperty idPedidoPropiedad()
	{
		return idPedido;
	}
	
	public ObjectProperty <ClienteDTO> clientePropiedad()
	{
		return cliente;
	}
	
	public StringProperty estadoPropiedad()
	{
		return estado;
	}
	
	public ObjectProperty <LocalDate> fechaTomaPropiedad()
	{
		return fechaToma;
	}
	
	public ObjectProperty <LocalDate> fechaEntregaPropiedad()
	{
		return fechaEntrega;
	}
	
	public ObjectProperty <LocalTime> horaTomaPropiedad()
	{
		return horaToma;
	}
	
	public ObjectProperty <LocalTime> horaEntregaPropiedad()
	{
		return horaEntrega;
	}
	
	public StringProperty propositoPropiedad()
	{
		return proposito;
	}
	
	public StringProperty formaPropiedad()
	{
		return forma;
	}
	
	public ObjectProperty <LocalDateTime> fechaUltimaModificacionPropiedad()
	{
		return fechaModificacion;
	}
	
	// SETTERS AND GETTERS
	
	public void setIdPedido(int id)
	{
		this.idPedido.set(id);
	}
	
	public int getIdPedido()
	{
		return idPedido.get();
	}
	
	public void setCliente(ClienteDTO cliente)
	{
		this.cliente.set(cliente);
	}
	
	public ClienteDTO getCliente()
	{
		return cliente.get();
	}
	
	public void setFechaToma (LocalDate f)
	{
		this.fechaToma.set(f);
	}
	
	public LocalDate getFechaToma()
	{
		return fechaToma.get();
	}
	
	public void setFechaEntrega(LocalDate f)
	{
		System.out.println("F: " + f);
		this.fechaEntrega.set(f); 
	}
	
	public LocalDate getFechaEntrega()
	{
		return fechaEntrega.get();
	}
	
	public LocalTime getHoraToma()
	{
		return horaToma.get();
	}

	public void setHoraToma(LocalTime horaToma)
	{
		this.horaToma.set(horaToma);
	}

	public LocalTime getHoraEntrega() 
	{
		return horaEntrega.get();
	}

	public void setHoraEntrega(LocalTime horaEntrega) 
	{
		this.horaEntrega.set(horaEntrega);
	}
	
	public String getProposito()
	{
		return this.proposito.get();
	}
	
	public void setProposito(String p)
	{
		this.proposito.set(p);
	}

	public void setEstado(String e)
	{
		this.estado.set(e);
	}
	
	public String getEstado()
	{
		return estado.get();
	}
	
	public void setForma(String f)
	{
		this.forma.set(f);
	}
	
	public String getForma()
	{
		return forma.get();
	}
	
	public void setFechaModificacion(LocalDateTime f)
	{
		this.fechaModificacion.set(f);
	}
	
	public LocalDateTime getFechaModificacion()
	{
		return this.fechaModificacion.get();
	}
}
