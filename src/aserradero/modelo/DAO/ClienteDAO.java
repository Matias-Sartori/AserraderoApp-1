package aserradero.modelo.DAO;

import aserradero.modelo.DTO.ClienteDTO;
import javafx.collections.ObservableList;

public interface ClienteDAO extends IDAO<ClienteDTO, Integer>
{
	ObservableList<ClienteDTO> obtenerActivos();
}
