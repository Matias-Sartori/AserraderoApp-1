package aserradero.util;

import aserradero.modelo.DTO.PedidoDTO;

public final class UPedido
{
	private UPedido()
	{
	}
	
	public static final String PENDIENTE = "Pendiente";
	public static final String EN_CURSO = "En curso";
	public static final String LISTO = "Listo";
	public static final String ENTREGADO = "Entregado";
	public static final String TERMINADO = "Terminado";
	
	/**
	 * Método encargado de transformar el estado tomado de la base de datosReporte (int) y convertirlo a String.
	 * (Necesario para mostrarlo en la interfaz de usuario)
	 * @param n - Estado en formato int
	 * @return - Retorna el estado convertido a String
	 */
	public static String estadoString(int n)
	{
		String estado = null;
		
		switch(n)
		{
			case 0: estado = PENDIENTE; break;

			case 1: estado = EN_CURSO; break;
			
			case 2: estado = LISTO; break;
			
			case 3: estado = ENTREGADO; break;
			
			case 4: estado = TERMINADO; break;
		}
		
		return estado;
	}
	
	/**
	* Método encargado de transformar el estado tomado del objecto ProductoEliminadoDTO (String) y convertirlo a int. 
	* (Necesario para insertarlo en la base de datosReporte)
	* @param e - Estado en formato String
	* @return - Retorna el estado convertido a int
	*/
	public static int estadoInt(String e)
	{
		int estado = 0;
		
		switch(e)
		{
			case PENDIENTE: estado = 0; break;
				
			case EN_CURSO: estado = 1; break;
			
			case LISTO: estado = 2; break;
			
			case ENTREGADO: estado = 3; break;
			
			case TERMINADO: estado = 4; break;
		}
		
		return estado;
	}	
	
	public static PedidoDTO clonarPedido(PedidoDTO p)
	{
		PedidoDTO pedidoClonado = null;
		
		if(p != null)
		{
			pedidoClonado = new PedidoDTO();
			pedidoClonado.setIdPedido(p.getIdPedido());
			pedidoClonado.setCliente(p.getCliente());
			pedidoClonado.setProposito(p.getProposito());
			pedidoClonado.setEstado(p.getEstado());
			pedidoClonado.setFechaToma(p.getFechaToma());
			pedidoClonado.setFechaEntrega(p.getFechaEntrega());
			pedidoClonado.setHoraToma(p.getHoraToma());
			pedidoClonado.setHoraEntrega(p.getHoraEntrega());
			pedidoClonado.setFechaModificacion(p.getFechaModificacion());
			pedidoClonado.setForma(p.getForma());
		}
		
		return pedidoClonado;
	}
}
