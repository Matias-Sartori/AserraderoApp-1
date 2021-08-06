package aserradero.vista.login;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSpinner;

import aserradero.controlador.AserraderoApp;
import aserradero.preferencias.Preferencias;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;


public class LoginControlador // Clase que se encarga de controlar los componentes de la interfaz login
{
	@FXML private ImageView candadoImagen;
	@FXML private AnchorPane panelCentro;
	@FXML private Button btnSalir;
	@FXML private Button btnIngresar;
	@FXML private JFXComboBox <String> cmbTipo;
	@FXML private JFXRadioButton radioAdministrador;
	@FXML private JFXRadioButton radioEmpleado;	
	@FXML private JFXPasswordField pfPassword;
	@FXML private Label lblPasswordIncorrecta;
	@FXML private HBox hboxLoading;
	@FXML private JFXSpinner spinnerLogin;
	@FXML private Hyperlink hpSalir;

	private ToggleGroup grupoRadio;
	
	private Task <Integer> tarea;
	
	private AserraderoApp miCoordinador;
	
	private static final Logger LOGGER = LogManager.getLogger(LoginControlador.class);
	
	public LoginControlador()
	{
	}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	@FXML public void initialize()
	{	
		grupoRadio = new ToggleGroup();
		
		radioAdministrador.setToggleGroup(grupoRadio);
		
		radioAdministrador.setUserData("Administracion");
		
		radioAdministrador.setSelected(true);
		
		radioEmpleado.setToggleGroup(grupoRadio);
		
		radioEmpleado.setUserData("Produccion");
		
		pfPassword.requestFocus();
		
		pfPassword.selectAll();
		
		lblPasswordIncorrecta.setVisible(false);
		
		spinnerLogin = new JFXSpinner();
		
		spinnerLogin.setRadius(5);
		
		grupoRadio.selectedToggleProperty().addListener((obs, old, newValue)->
		{
			if(newValue.isSelected())
				pfPassword.requestFocus();
		});
		
		pfPassword.setOnKeyReleased(e ->
		{
			if (e.getCode() == KeyCode.ENTER)
				actionIngresar();
		});
		
		btnIngresar.disableProperty().bind(pfPassword.textProperty().isEmpty().or(grupoRadio.selectedToggleProperty().isNull()));
	}
	
	@FXML private void actionSalir()
	{
		if(tarea != null && tarea.isRunning())
			tarea.cancel();
		
		//System.exit(0);
		
		Platform.exit();	
	}
	
	@FXML private void actionIngresar()
	{
		if(tarea != null && tarea.isRunning())
		{
			tarea.cancel();
		}
		else
		{
			// tomamos el usuario seleccionado
			String usuarioSeleccionado = grupoRadio.getSelectedToggle().getUserData().toString();
			
			// tomamos la contraseña introducida y la encriptamos
			String passwordItroducida = DigestUtils.sha1Hex(pfPassword.getText().trim());
			
			if(validarUsuario(usuarioSeleccionado, passwordItroducida))
			{
				lblPasswordIncorrecta.setVisible(false);
				
				pfPassword.getStyleClass().clear();
				
				pfPassword.getStyleClass().add("jfx-password-field");
		
				tarea = new Task<Integer>()
				{
					@Override
					protected Integer call() throws Exception
					{
						Thread.sleep(250);
						
						miCoordinador.inicarPantallaPrincipal(usuarioSeleccionado);
			
						return null;
					}
				};
				
				tarea.setOnRunning(e -> 
				{
					
					radioAdministrador.disableProperty().bind(tarea.runningProperty());
					
					radioEmpleado.disableProperty().bind(tarea.runningProperty());
					
					pfPassword.disableProperty().bind(tarea.runningProperty());
					
					hboxLoading.getChildren().add(spinnerLogin);
					
					btnIngresar.textProperty().bind(Bindings.when(tarea.runningProperty()).then("Cancelar").otherwise("Ingresar"));
					
				});
				
				tarea.setOnFailed(e -> 
				{
					hboxLoading.getChildren().clear();
					
					LOGGER.log(Level.ERROR, "Fallo la tarea (actionINgresar) {}", ("["+ usuarioSeleccionado + "]"));
					
					System.exit(0);
				});
				
				tarea.setOnCancelled(e ->
				{
					hboxLoading.getChildren().clear();
				});
				
				tarea.setOnSucceeded(e -> 
				{
					pfPassword.clear();
					
					pfPassword.requestFocus();						
					
					hboxLoading.getChildren().clear();
					
					miCoordinador.cerrarPanelLogin();
					
					miCoordinador.mostrarPantallaPrincipal();
					
					LOGGER.log(Level.INFO, "Usuario logueado {}", ("[" + usuarioSeleccionado + "]"));
				});
				
				new Thread(tarea).start();
			}
			else if(!pfPassword.getText().trim().isEmpty())
			{
				LOGGER.log(Level.INFO, "Se ha introducido contraseña incorrecta. Usuario = [{}]", usuarioSeleccionado);
				
				pfPassword.getStyleClass().add("wrong-credentials");
				
				lblPasswordIncorrecta.setText("La contraseña ingresada es incorrecta");
				
				lblPasswordIncorrecta.setVisible(true);
				
				pfPassword.requestFocus();
				
				pfPassword.selectAll();
			}
			else
			{		
				pfPassword.getStyleClass().add("wrong-credentials");
				
				lblPasswordIncorrecta.setText("Debe ingresar la contraseña de usuario");
				
				lblPasswordIncorrecta.setVisible(true);
				
				pfPassword.requestFocus();
				
				pfPassword.selectAll();
			}
		}
	}
	
	private boolean validarUsuario(String usuarioSeleccionado, String passwordIntroducida)
	{
		boolean validado = false;
		
		Preferencias preferencias = Preferencias.getPreferencias();	
		
		if(usuarioSeleccionado.equalsIgnoreCase("Administracion") && passwordIntroducida.equals(preferencias.getPasswordAdmin()))
		{	
			validado = true;
		}
		else if(usuarioSeleccionado.equalsIgnoreCase("Produccion") && passwordIntroducida.equals(preferencias.getPasswordEmpleado()))
		{
			validado = true;
		}
		
		return validado;
	}
}
