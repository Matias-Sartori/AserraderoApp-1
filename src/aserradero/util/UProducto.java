package aserradero.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import aserradero.modelo.DAO.mysql.ProductoDAOMySQLImpl;
import aserradero.modelo.DTO.ProduccionDTO;
import aserradero.modelo.DTO.ProductoDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class UProducto
{
	
	/**
	 * Constante necesaria para igualar todos los valores, puesto que el espesor y ancho están en pulgadas, el largo, en metros.
	 */
	private static final double CONSTANTE = 0.2734; 
	public static final String UN_DECIMAL = "0.0";
	public static final String DOS_DECIMALES = "0.00";
	public static final String TRES_DECIMALES = "0.000";
	
	private static final String TABLA = "Tabla";
	private static final String TABLON = "Tablón";
	private static final String INDEFINIDO = "Indefinido";
	
	private static final Logger LOGGER = Logger.getLogger(ProductoDAOMySQLImpl.class.getName());
	
	private UProducto()
	{
		//..
	}
	/**
	 * Método que calcula los pies de un producto, multiplicando (constante x espesor x ancho x largo) x unidades
	 * @param e - Espesor del producto en pulgadas
	 * @param a - Ancho del producto en pulgadas
	 * @param l - Largo del producto en metros
	 * @param unidades - Cantidad del producto
	 * @param - Formato de cantidad de decimales
	 * @return Retorna el total de pies en formato Double
	 */
	public static Double calcularPiesCuadrados(double e, double a, double l, int unidades, String formato)
	{
		System.out.println("calcularPiesCuadrados -> " + CONSTANTE + " - " + e + " - " + a + " - " + l + " - " + unidades);
		
		Double pies = (CONSTANTE * e * a * l) * unidades;
		
		return formatearMedida(pies, formato);
	}
	
	public static Double calcularPiesClasificado(double e, double a, double l, String formato)
	{
		System.out.println("calcularPiesClasificado -> " + CONSTANTE + " - " + e + " - " + a + " - " + l);
		
		Double pies = (CONSTANTE * e * a * l);

		return formatearMedida(pies, formato);
	}
	
	public static Double calcularVolumen()
	{
		return 10d;
	}
	
	/**
	 * Se encarga de formatear y definir la cantidad de decimales del valor pasado como parámetro.
	 * @param p - Valor de la medida.
	 * @param fomato - Formato de cantidad de decimales
	 * @return - Retorna el valor Double formateado.
	 */
	public static Double formatearMedida(Double valor, String formato)
	{
		Double valorFormateado = 0d;
		
		try 
		{
			DecimalFormat df = new DecimalFormat(formato);
			//df.setRoundingMode(RoundingMode.DOWN);
			String cadena = df.format(valor);
			valorFormateado = df.parse(cadena).doubleValue();
		} 
		catch (ParseException e) 
		{
			LOGGER.log(Level.SEVERE, e.getMessage());
			return valor;
		}
		
		return valorFormateado;
	}
	
	
	
	// Tabla = Anchura >= 80mm(3.14961") - Espesor <= 40mm(1.5748")
	// Tablón = Anchura > 3 Espesor - Espesor h > 40mm(1.5748")
	// Listón = Anchura < 80 mm(3.14961") - Espesor 6mm <= Espesor <= 40mm(1.5748")
	/**
	 * Método que se encarga de definir el tipo de corte de la madera, basándose en un criterio según las dimensiones del producto
	 * @param e - Espesor de la madera
	 * @param a - Ancho de la madera
	 * @param l - Largo de la madera
	 * @return - Retorna el nombre del corte (tabla, tablón, etc)
	 */
	public static String calcularTipoMadera(Double e, Double a, Double l)
	{
		String tipo = INDEFINIDO;
		
		if (e <= 1.5748)
		{
			if (a >= 3.14961)
			{
				tipo = TABLA;
			}
		}
		
		return tipo;
	}
	
	public static int convertirTipoInt(String t)
	{
		int tipo = 0;
		switch(t)
		{
			case TABLA:
				tipo = 1;
				break;	
				
			case INDEFINIDO:
				tipo = 0;
				break;
		}
		return tipo;
	}
	
	public static String convertirTipoString(int t)
	{
		String tipo = null;
		switch(t)
		{
			case 1: 
				tipo = TABLA;
				break;
				
			default: 
				tipo = INDEFINIDO;
		}
		return tipo;
	}
	
	public static ProductoDTO clonarProducto(ProductoDTO p)
	{
		ProductoDTO pClonado = null;
		
		if(p != null)
		{
			pClonado = new ProductoDTO();
			pClonado.setIdProducto(p.getIdProducto());
			pClonado.setEspesor(p.getEspesor());
			pClonado.setAncho(p.getAncho());
			pClonado.setLargo(p.getLargo());
			pClonado.setStock(p.getStock());
			pClonado.setPies();
			pClonado.setReserva(p.getReserva());
			pClonado.setCarga(p.getCarga());
		}
		
		return pClonado;
	}
	
	public static ObservableList<ProductoDTO> clonarProductos(ObservableList<ProductoDTO> productos)
	{
		ObservableList<ProductoDTO> productosClonados = FXCollections.observableArrayList();
		
		for(ProductoDTO p : productos)
		{
			ProductoDTO pClonado = new ProductoDTO();
			pClonado.setIdProducto(p.getIdProducto());
			pClonado.setEspesor(p.getEspesor());
			pClonado.setAncho(p.getAncho());
			pClonado.setLargo(p.getLargo());
			pClonado.setStock(p.getStock());
			pClonado.setPies();
			pClonado.setReserva(p.getReserva());
			pClonado.setCarga(p.getCarga());
			
			productosClonados.add(pClonado);	
		}
		
		return productosClonados;
	}
	
	// Metodo momentaneo. * Implementar correctamente el metodo equals y el hascode de la clase ProductoDTO
	
	/**
	 * Método que comprueba la existencia de un producto en una lista de productos.
	 * @param productoDto - Objeto tipo ProductoDTO con datosReporte a comprobar.
	 * @return Retorna el producto existente. De lo contrario, nulo.
	 */
	public static ProductoDTO comprobrarExistenciaProducto(ProductoDTO producto, ObservableList<ProductoDTO> productos)
	{	
		ProductoDTO pExistente = null;
		for(ProductoDTO p : productos)
		{
			if(producto.equals(p))
			{
				pExistente = p;
				break;
			}
		}
		
		return pExistente;
	}
	
	public static ProductoDTO comprobrarExistenciaProductoClasificado(ProductoDTO producto, ObservableList<ProductoDTO> productos)
	{
		ProductoDTO pExistente = null;
		for(ProductoDTO p : productos)
		{
			if(producto.getEspesor().equals(p.getEspesor()) && producto.getAncho().equals(p.getAncho()))
			{
				pExistente = p;
				break;
			}
		}
		
		return pExistente;
	}
	
	public static ObservableList<ProductoDTO> clasificarProductos(ObservableList<ProductoDTO> productos)
	{
		ObservableList<ProductoDTO> productosClasificados = FXCollections.observableArrayList();
		boolean saltear = false;
		int salteo = 0;
			
		// se recorre cada producto de la lista
		for(int i = 0; i < productos.size();  i += salteo + 1)
		{
			System.out.println(">>>>>>> [" + i + "] <<<<<<<");
			
			ProductoDTO pNuevo = clonarProducto(productos.get(i));
			
			System.out.println("[" + i + "]" + " ----> " + pNuevo);
			
			
			// se recorre la misma lista, pero empezando desde el indice 1 en adelante
			for(int i2 = i+1; i2 <= productos.size(); i2++)
			{
				
				if(i2 > productos.size() - 1)
					break;
				
				ProductoDTO p = productos.get(i2);
				
				System.out.println("\t ES IGUAL A " + "[" + i2 +"]" + " ----> "+ p);
				
				if(pNuevo.getEspesor().equals(p.getEspesor()) && pNuevo.getAncho().equals(p.getAncho()))
				{
					System.out.println("\t \t E S    I G U A L    !!!!");
					
					pNuevo.setStock(pNuevo.getStock() + p.getStock());
					pNuevo.setLargo(!saltear ? (pNuevo.getLargo() * pNuevo.getStock()) + (p.getLargo() * p.getStock()) : pNuevo.getLargo() + (p.getLargo() * p.getStock()));
				
					saltear = true;
					salteo ++;
				}
			}
				
			pNuevo.setIdProducto(0);
			pNuevo.setPiesClasificado();
			
			System.out.println("PRODUCTO FINAL  ------> " + pNuevo);
			
			productosClasificados.add(pNuevo);		
			
			if(!saltear)
			{
				System.out.println("salteo = 0");
				salteo = 0;
			}
			else saltear = false;
				
		}
		
		System.out.println("------------ PRODUCTOS CLASIFICADOS ------------");
		
		for(ProductoDTO p : productosClasificados)
		{
			System.out.println("\t " + p);
		}
		
		return productosClasificados;
	}
	
	public static double calcularTotalPies(ObservableList<ProductoDTO> productos)
	{
		double totalPies = 0;
		
		if(productos != null)
		{
			for(ProductoDTO p : productos)
			{
				totalPies += p.getPies();
			}
		}
		
		totalPies = formatearMedida(totalPies, DOS_DECIMALES);

		return totalPies;
	}
	
	public static int calcularTotalMaderas(ObservableList<ProductoDTO> productos)
	{
		int total = 0;
		
		if(productos != null)
		{
			for(ProductoDTO p : productos)
			{
				total += p.getStock();
			}
			
		}
		
		return total;
	}
	
	public static ObservableList<ProductoDTO> convertirProduccionProducto(ObservableList<ProduccionDTO> producciones)
	{
		 ObservableList<ProductoDTO> productos = FXCollections.observableArrayList();
		
		 if(productos != null)
		 {
				for(ProduccionDTO p: producciones)
				{
					productos.add(p.getProducto());
				}
		 }
		
		return productos;
	}
}
