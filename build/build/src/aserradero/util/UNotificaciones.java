package aserradero.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.util.Duration;


/**
 * Clase que sirve para la creación de diferentes tipos de notificaciones del sistema.
 * @author King
 *
 */
public final class UNotificaciones
{
	private static final Logger LOGGER = LogManager.getLogger(UNotificaciones.class);
	
	private UNotificaciones()
	{
	}
	
	private static Notifications noti;
	
	/**
	 * Método encargado de instanciar y mostrar la notifiación.
	 * @param titulo - Título de la notificación.
	 * @param contenido - Mensaje de la noticicación.
	 */
	public static void notificacion(String titulo, String contenido)
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{	
				if(noti == null)
					noti = Notifications.create();
				
				noti.title(titulo);
				noti.text(contenido);
				noti.graphic( FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.CHECK, "50px"));
				noti.hideAfter(Duration.seconds(5));
				noti.position(Pos.BOTTOM_CENTER);

				Thread.sleep(500);
				
				return null;
			}

		};
		
		tarea.setOnRunning(e ->
		{
			
		});
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo al crear la notificacion - (notificacion)");
		});
		
		tarea.setOnSucceeded(e ->
		{
			noti.showInformation();

		});
		
		new Thread(tarea).start();
	}
}
