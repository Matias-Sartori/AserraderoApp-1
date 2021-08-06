package aserradero.modelo.DAO;

import java.net.SocketException;

import aserradero.modelo.DTO.LocalidadDTO;
import javafx.collections.ObservableList;

public interface LocalidadDAO 
{
	/**
	 * M�todo engarcado de buscar todas las localidades de la base de datosReporte.
	 * @return Lista con todas las localidades.
	 * @throws SocketException 
	 */
	public ObservableList <LocalidadDTO> obtenerLocalidades();
}
