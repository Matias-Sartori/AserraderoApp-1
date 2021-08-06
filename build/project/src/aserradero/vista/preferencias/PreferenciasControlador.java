package aserradero.vista.preferencias;

import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;

import aserradero.controlador.AserraderoApp;
import aserradero.preferencias.Preferencias;
import aserradero.util.UAlertas;
import aserradero.util.UCorreo;
import aserradero.util.UNotificaciones;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

public class PreferenciasControlador
{
	@FXML private HBox panelCentro;
	@FXML private PasswordField pfAdministrador;
	@FXML private PasswordField pfEmpleado;
	@FXML private Button btnGuardarCambios;

	private static final Logger LOGGER = LogManager.getLogger(UCorreo.class);
	
	private AserraderoApp miCoordinador;
	
	public PreferenciasControlador() {}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	public void initialize()
	{
		cargarValoresUsuarios();
		
		// * arreglar para que funcione correctamente el guardar cambios al presionar enter
		
		/*pfAdministrador.setOnKeyReleased(e -> 
		{
			if(e.getCode() == KeyCode.ENTER && !btnGuardarCambios.isDisabled() && pfAdministrador.isFocused())
				actionGuardarCambios();
		});
		
		pfEmpleado.setOnKeyReleased(e -> 
		{
			
			if(e.getCode() == KeyCode.ENTER && !btnGuardarCambios.isDisabled() && pfEmpleado.isFocused())
				actionGuardarCambios();
		});*/
		
		btnGuardarCambios.disableProperty().bind(pfAdministrador.textProperty().isEmpty().or(pfEmpleado.textProperty().isEmpty()));
	}
	
	private void cargarValoresUsuarios()
	{
		// obtenemos las preferencias actuales
		Preferencias preferencias = Preferencias.getPreferencias();
		
		// seteamos los campos de contraseña de cada usuario de las preferencias obtenidas
		pfAdministrador.setText(preferencias.getPasswordAdmin());
		pfEmpleado.setText(preferencias.getPasswordEmpleado());		
	}
	
	
	@FXML private void actionGuardarCambios()
	{
		Preferencias preferencias = Preferencias.getPreferencias();
		
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		String passAdmin = pfAdministrador.getText().trim();
		String passEmp = pfEmpleado.getText().trim();
		
		// preguntamos si la contraseña introducida es difierente a la actual
		if((!pfAdministrador.getText().trim().equals(preferencias.getPasswordAdmin())
				|| !pfEmpleado.getText().trim().equals(preferencias.getPasswordEmpleado()))
			)
		{
			btnAceptar.setOnAction(e ->
			{
				if(!pfAdministrador.getText().trim().equals(Preferencias.getPreferencias().getPasswordAdmin()))
					preferencias.setPasswordAdmin(pfAdministrador.getText().trim());

				if(!pfEmpleado.getText().trim().equals(Preferencias.getPreferencias().getPasswordEmpleado()))
					preferencias.setPasswordEmpleado(pfEmpleado.getText().trim());

				if(Preferencias.escribirPreferenciaArchivo(preferencias))
				{
					UNotificaciones.notificacion("Preferencias", "Se han guardado las contraseñas");
					
					// notificamos las nuevas contraseñas al correo pre-establecido
					
					Task<Integer> tarea = new Task<Integer>()
					{
					
						@Override
						protected Integer call() throws Exception
						{
							String asunto = "Se han cambiado las contraseñas de usuarios de los sectores";
							
							String msje = "Contraseña de Administración = " + passAdmin + "\nContraseña de Producción = " + passEmp;
							
							UCorreo.enviarMensaje(asunto, msje);
							
							return null;
						}
					};
					
					tarea.setOnFailed(t ->
					{
						LOGGER.log(Level.ERROR, "Fallo la tarea - (actionGuardarCambios)");
						
						// iniciamos las preferencias por defecto
						Preferencias.initConfig();
						
						
					});
					
					new Thread(tarea).start();
					
				}
				else
					UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Preferencias", "Ha ocurrido un error al guardar las contraseñas");
				
				cargarValoresUsuarios();
			});
			
			UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Modificación de contraseñas", "¿Desea guardar los cambios?");
		}	
	}
	
	/*// eliminamos los espacios en blanco de los extremos y reemplazamos los espacios dobles intermedios por un espacio
			String passAdmin = pfAdministrador.getText().trim().replaceAll(" +", " ");
			String passEmp = pfEmpleado.getText().trim().replaceAll(" +", " ");
			
			// si la longitud es mayor a la pautada, cortamos el texto hasta el valor de la longitud pactada, sino, cortamos el texto con el valor de la longitud
			String passAdminFinal = passAdmin.substring(0, passAdmin.length() > 20 ? 20 : passAdmin.length());
			String passEmpFinal = passEmp.substring(0, passEmp.length() > 20 ? 20 : passEmp.length());
			
			System.out.println("passAdmin -> [" + passAdmin + "]");
			System.out.println("passEmp -> [" + passEmp + "]");
			
			System.out.println("passAdminFinal -> [" + passAdminFinal + "]" + "[" + passAdminFinal.length() + "]");
			System.out.println("passEmpFinal -> [" + passEmpFinal + "]" + "[" + passEmpFinal.length() + "]");*/
}
