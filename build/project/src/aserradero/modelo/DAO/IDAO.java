package aserradero.modelo.DAO;

import javafx.collections.ObservableList;

public interface IDAO <T, K>
{
	boolean insertar(T v) ;
	
	boolean actualizar(T v);
	
	boolean eliminar(T v);
	
	ObservableList<T> obtenerTodos();
	
	T obtener(T v) ;
	
	T obtenerPorId(K id);
}
