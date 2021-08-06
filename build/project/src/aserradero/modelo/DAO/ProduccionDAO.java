package aserradero.modelo.DAO;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import aserradero.modelo.DTO.ProduccionDTO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.clases.ProduccionAnio;
import aserradero.modelo.clases.ProduccionDia;
import aserradero.modelo.clases.ProduccionMes;
import javafx.collections.ObservableList;

public interface ProduccionDAO extends IDAO <ProduccionDTO, Integer>
{
	public ObservableList<ProduccionAnio> obtenerProduccionTotal();
	
	/**
	 * Método encargado de calcular el total de registros de maderas cargadas, clasificados por fecha y hora de inserción.
	 * @return - Lista de objetos Produccion con los datosReporte a mostrar.
	 */
	public ObservableList <ProduccionDia> obtenerProduccionDeMes(Month mes, Year anio);
	
	/**
	 * Método que obtiene los productos pertenecientes a la producción pasada como parámetro.
	 * @param p - Objeto Produccion con los datosReporte a consultar.
	 * @return Lista de objetos ProduccionDTO obtenidos.
	 */
	public ObservableList <ProduccionDTO> obtenerProduccionDeDia(ProduccionDia p);
	
	public ObservableList<ProduccionMes> obtenerProduccionDeAnio(int anio);
	
	public ObservableList<String> obtenerTodosLosAnios();
	
	public LocalDate obtenerFechaMaxima();
	
	public LocalDate obtenerFechaMinima();
	
	public ObservableList <ProduccionDTO> obtenerProduccionDePedido(int idPedido);
	
	public ObservableList<ProduccionDTO> obtenerProduccionProductoPedido(int idProducto, int idPedido);
	
	public ObservableList<ProductoDTO> obtenerProductosDeMes(Month mes, Year anio);
	
	public ObservableList<ProductoDTO> obtenerProductosDeAnio(Year anio);
	
	public ObservableList<ProductoDTO> obtenerProductosTotal();
	
	public ObservableList<ProductoDTO> obtenerProductosDeMesClasificados(Month mes, Year anio);
	
	public ObservableList<ProductoDTO> obtenerProductosDeAnioClasificados(Year anio);
	
	public ObservableList<ProductoDTO> obtenerProductosTotalClasificados();
}
