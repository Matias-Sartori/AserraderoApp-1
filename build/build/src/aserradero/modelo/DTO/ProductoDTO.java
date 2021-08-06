package aserradero.modelo.DTO;

import aserradero.util.UProducto;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProductoDTO implements Cloneable
{
	private IntegerProperty idProducto;
	private DoubleProperty espesor;
	private DoubleProperty ancho;
	private DoubleProperty largo;
	private DoubleProperty pies;
	private IntegerProperty stock;
	private IntegerProperty reserva;
	private IntegerProperty carga;
	
	// esto se puede solucionar formateando las celdas o alguna clse utilitaria intermedia
	private StringProperty espesorVista;
	private StringProperty anchoVista;
	// ------------------------------------------------------------------------------------
	
	// CONSTRUCTORES
	
	public ProductoDTO()
	{
		this.idProducto = new SimpleIntegerProperty(0);
		this.espesor = new SimpleDoubleProperty(0);
		this.ancho = new SimpleDoubleProperty(0);
		this.largo = new SimpleDoubleProperty(0);
		this.pies = new SimpleDoubleProperty(0);
		this.stock = new SimpleIntegerProperty(0);
		this.reserva = new SimpleIntegerProperty(0);
		this.carga = new SimpleIntegerProperty(0);
		
		this.espesorVista = new SimpleStringProperty(null);
		this.anchoVista = new SimpleStringProperty(null);
		
		setEspesorVista();
		setAnchoVista();
	}
	
	public ProductoDTO(int idp, Double e, Double a, Double l, Double pies, int s, int r, int c)
	{
		this.idProducto = new SimpleIntegerProperty(idp);
		this.espesor = new SimpleDoubleProperty(e);
		this.ancho = new SimpleDoubleProperty(a);
		this.largo = new SimpleDoubleProperty(l);
		this.pies = new SimpleDoubleProperty(pies); 
		this.stock = new SimpleIntegerProperty(s);
		this.reserva = new SimpleIntegerProperty(r);
		this.carga = new SimpleIntegerProperty(c);
		
		this.espesorVista = new SimpleStringProperty(null);
		this.anchoVista = new SimpleStringProperty(null);
		
		setEspesorVista();
		setAnchoVista();
	}
	
	// SOBREESCRITURA DE METODOS
	
	/*public boolean equals(Object obj)
	{
		if(obj instanceof ProductoDTO)
		{
			ProductoDTO p = (ProductoDTO) obj;
			
			if(this.espesor.get() != p.getEspesor() || this.ancho.get() != p.getAncho()  || this.largo.get() != p.getLargo())
			{
				return false;
			}
			else return true;
		}
		else return false;
	}*/
	
	public String toString()
	{
		return
		"ID: " + this.getIdProducto()
		+ " - Espesor: " + this.espesor.get()
		+ " - Ancho: " + this.ancho.get()
		+ " - Largo: " + this.largo.get()
		+ " - Pies: " + this.getPies()
		+ " - Stock: " + this.stock.get()
		+ " - Reserva: " + this.reserva.get()
		+ " - Carga: " + this.carga.get()
		+ " - EspesorVista: " + this.espesorVista.get()
		+ " - AnchoVista: " + this.anchoVista.get()
		+ " - Pies: " + this.pies.get()
		+ " <\n ";
	}
	
	
	
	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ancho == null) ? 0 : ancho.hashCode());
		result = prime * result + ((espesor == null) ? 0 : espesor.hashCode());
		result = prime * result + ((largo == null) ? 0 : largo.hashCode());
		return result;
	}*/

	@Override
	public boolean equals(Object obj) 
	{
		if(obj instanceof ProductoDTO)
		{
			ProductoDTO p = (ProductoDTO) obj;
			
			if(this.espesor.get() != p.getEspesor() || this.ancho.get() != p.getAncho()  || this.largo.get() != p.getLargo())
			{
				return false;
			}
			else return true;
		}
		else return false;
	}

	// Metodos de propiedad
	
	public IntegerProperty idProductoPropiedad()
	{
		return idProducto;
	}

	public DoubleProperty espesorPropiedad()
	{
		return espesor;
	}
	
	public DoubleProperty anchoPropiedad()
	{
		return ancho;
	}
	
	public DoubleProperty largoPropiedad()
	{
		return largo;
	}
	
	public DoubleProperty piesPropiedad()
	{
		return pies;
	}
	
	
	public IntegerProperty stockPropiedad()
	{
		return stock;
	}
	
	public IntegerProperty reservaPropiedad()
	{
		return reserva;
	}
	
	public IntegerProperty cargaPropiedad()
	{
		return carga;
	}
	
	public StringProperty espesorVistaPropiedad()
	{
		return espesorVista;
	}
	
	public StringProperty anchoVistaPropiedad()
	{
		return anchoVista;
	}
	
	
	// SETTERS Y GETTER 	<----------------------------------------------
	
	public void setIdProducto(int idStock)
	{
		this.idProducto.set(idStock);
	}
	
	public int getIdProducto()
	{
		return idProducto.get();
	}
	
	public void setEspesor(Double e)
	{
		this.espesor.set(e);
		
		setEspesorVista();
	}
	
	public Double getEspesor()
	{
		return espesor.get();
	}
	
	public void setAncho(Double a)
	{
		this.ancho.set(a);
		
		setAnchoVista();
	}
	
	public Double getAncho()
	{
		return ancho.get();
	}
	
	public void setLargo(Double l)
	{
		this.largo.set(l);
	}
	
	public Double getLargo()
	{
		return largo.get();
	}
	
	public void setPies()
	{
		this.pies.set(UProducto.calcularPiesCuadrados(this.getEspesor(), this.getAncho(), this.getLargo(), this.getStock(), UProducto.DOS_DECIMALES));
	}
	
	public void setPies(Double p)
	{
		this.pies.set(p);
	}
	
	public void setPiesClasificado()
	{
		this.pies.set(UProducto.calcularPiesClasificado(this.getEspesor(), this.getAncho(), this.getLargo(), UProducto.DOS_DECIMALES));
	}
	
	public Double getPies()
	{
		return pies.get();
	}
	
	public void setStock(int uni)
	{
		this.stock.set(uni);
	}
	
	public int getStock()
	{
		return stock.get();
	}
	
	public void setReserva(int r)
	{
		this.reserva.set(r);
	}
	
	public int getReserva()
	{
		return reserva.get();
	}
	
	public void setCarga(int c)
	{
		this.carga.set(c);
	}
	
	public int getCarga()
	{
		return carga.get();
	}
	
	private void setEspesorVista()
	{
		// pasamos el valor de ancho a String
		String a = String.valueOf(this.espesor.get());

		// comprobamos si el valor es entero o decimal
		if(this.espesor.get() % 1 == 0)
		{
			// es entero

			this.espesorVista.set(String.valueOf((int) this.espesor.get()));
		}
		else
		{
			// es decimal
			
			switch(a)
			{
			case "0.75":
				this.espesorVista.set("3/4");
				break;
			case "1.5":
				this.espesorVista.set("1 1/2");
				break;
			case "2.5":
				this.espesorVista.set("2 1/2");
				break;
			case "3.5":
				this.espesorVista.set("3 1/2");
				break;
			case "4.5":
				this.espesorVista.set("4 1/2");
				break;
			case "5.5":
				this.espesorVista.set("5 1/2");
				break;
			case "6.5":
				this.espesorVista.set("6 1/2");
				break;
			default:
				this.espesorVista.set(a);		
			}
		}
	}
	
	public String getEspesorVista()
	{
		return espesorVista.get();
	}
	
	private void setAnchoVista()
	{
		// pasamos el valor de ancho a String
		String a = String.valueOf(this.ancho.get());
		
		// comprobamos si el valor es entero o decimal
		if(this.ancho.get() % 1 == 0)
		{
			// es entero

			this.anchoVista.set(String.valueOf((int) this.ancho.get()));
		}
		else
		{
			// es decimal

			switch(a)
			{
				case "0.75":
					this.anchoVista.set("3/4");
					break;
				case "1.5":
					this.anchoVista.set("1 1/2");
					break;
				case "2.5":
					this.anchoVista.set("2 1/2");
					break;
				case "3.5":
					this.anchoVista.set("3 1/2");
					break;
				case "4.5":
					this.anchoVista.set("4 1/2");
					break;
				case "5.5":
					this.anchoVista.set("5 1/2");
					break;
				case "6.5":
					this.anchoVista.set("6 1/2");
					break;
				default:
					this.anchoVista.set(a);		
			}
		}
	}
	
	public String getAnchoVista()
	{
		return anchoVista.get();
	}
}
