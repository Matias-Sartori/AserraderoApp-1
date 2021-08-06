package aserradero.vista.nuevoCliente;

import java.util.Arrays;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import aserradero.controlador.AserraderoApp;
import aserradero.modelo.DAO.mysql.ClienteDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.LocalidadDAOMySQLImpl;
import aserradero.modelo.DTO.ClienteDTO;
import aserradero.modelo.DTO.LocalidadDTO;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UAlertas;
import aserradero.util.UFactoryDAO;
import aserradero.util.UNotificaciones;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class NuevoClienteControlador
{
	@FXML private JFXTextField tfNombre;
	@FXML private JFXTextField tfApellido;
	@FXML private JFXTextField tfTelefono;
	@FXML private JFXTextField tfDni;
	@FXML private JFXTextField tfCuit;
	@FXML private JFXTextField tfRazon;
	@FXML private JFXTextField tfDireccion;
	@FXML private JFXTextField tfCorreo;
	@FXML private JFXComboBox <LocalidadDTO> cmbLocalidad;
	@FXML private Hyperlink hyperlinkAtras;
	@FXML private JFXButton btnConfirmar;
	
	private HBox hBoxSinConexion = new HBox();
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	private JFXSpinner spinner = new JFXSpinner();
	
	// Referencia a la clase principal
	private AserraderoApp miAserraderoApp;
	private ObservableList<LocalidadDTO> listaLocalidades = FXCollections.observableArrayList();
		
	public NuevoClienteControlador()
	{		
	}
	
	public void setVentanaPrincipal(AserraderoApp a)
	{
		this.miAserraderoApp = a;
	}

	@FXML public void initialize()
	{
		iconoSinConexion.setSize("10");
		
		hBoxSinConexion.setSpacing(5);
		hBoxSinConexion.setAlignment(Pos.CENTER);
		hBoxSinConexion.getChildren().add(iconoSinConexion);
		hBoxSinConexion.getChildren().add(new Label("Sin conexión"));
		
		/*tfNombre.setPromptText("");
		tfApellido.setPromptText("");
		tfTelefono.setPromptText("Sin espacios, Ej: 3755321367");
		tfDni.setPromptText("Sin espacios. Ej: 365853741");
		tfCuit.setPromptText("Sin espacios. Ej: 20368537412");
		tfRazon.setPromptText("Ej: Los Pinos SRL");
		tfCorreo.setPromptText("Ej: correo@gmail.com");
		tfDireccion.setPromptText("");*/
		
		btnConfirmar.disableProperty().bind(tfNombre.textProperty().isEmpty().or(tfApellido.textProperty().isEmpty()));
		
		actualizarComboLocalidades();
		
		//new UAutoCompleteCmb<LocalidadDTO>(cmbLocalidad);
		//TextFields.bindAutoCompletion(cmbLocalidad.getEditor(), cmbLocalidad.getItems());
		//cmbLocalidad.setEditable(true);
		
		formatearCampos();
		
	}
	
	private void limpiarCampos()
	{
		tfNombre.clear();
		tfApellido.clear();
		tfTelefono.clear();
		tfDni.clear();
		tfCuit.clear();
		tfRazon.clear();
		tfDireccion.clear();
		tfCorreo.clear();
		cmbLocalidad.getSelectionModel().clearSelection();
		tfNombre.requestFocus();
	}
	
	private void actualizarComboLocalidades()
	{
		Task<Integer> tarea = new Task<Integer>()
		{			
			@Override
			protected Integer call() throws Exception 
			{
				LocalidadDAOMySQLImpl localidadDao = (LocalidadDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.LOCALIDAD);
				
				if(listaLocalidades.isEmpty())
				{
					Thread.sleep(250);
					
					listaLocalidades = localidadDao.obtenerLocalidades();
				}
					
				
				System.out.println("llego a ca!!!!!!!!" + listaLocalidades);
				
				return null;
			}
		};
		tarea.setOnRunning(e -> 
		{
			cmbLocalidad.setPlaceholder(spinner);
			
		});
		tarea.setOnFailed(e ->
		{
			System.out.println("Actualizar combo localidades. Fallo");
		});
		tarea.setOnSucceeded(e -> 
		{
			if(listaLocalidades != null)
			{
				System.out.println("AQUI");
				if(cmbLocalidad.getItems() == null || cmbLocalidad.getItems().isEmpty())
					cmbLocalidad.setItems(listaLocalidades);
			}
				
			else
			{
				System.out.println("ACASSSSSS");
				cmbLocalidad.setPlaceholder(hBoxSinConexion);
			}
			
		});
		new Thread(tarea).start();
	}
	
	// Formato de campos de texto.
		public void formatearCampos()
		{
			final String FORMATOLETRAS = "\\sa-zA-Z*";
			final String FORMATONUMEROS = "\\d*";
			
			final String REPLACELETRAS = "[^\\\\sa-zA-Z\\s]";
			final String REPLACENUMEROS = "[^\\d]";
			
			tfNombre.textProperty().addListener((observable, oldValue, newValue) ->
			{
				if (!newValue.matches(FORMATOLETRAS)) 
				{
			       tfNombre.setText(newValue.replaceAll(REPLACELETRAS, ""));
			    }
			});
			
			tfApellido.textProperty().addListener((observable, oldValue, newValue) ->
			{
				if (!newValue.matches(FORMATOLETRAS)) 
				{
					tfApellido.setText(newValue.replaceAll(REPLACELETRAS, ""));
				}
			});
			
			tfTelefono.textProperty().addListener((observable, oldValue, newValue) ->
			{
		        if (!newValue.matches(FORMATONUMEROS)) 
		        {
		        	tfTelefono.setText(newValue.replaceAll(REPLACENUMEROS, ""));
		        }
		    });
			
			tfDni.textProperty().addListener((observable, oldValue, newValue) ->
			{
				if (!newValue.matches(FORMATONUMEROS))
				{
					tfDni.setText(newValue.replaceAll(REPLACENUMEROS, ""));
				}
			});
			
			tfCuit.textProperty().addListener((observable, oldValue, newValue) -> 
			{
				if (!newValue.matches(FORMATONUMEROS))
				{
					tfCuit.setText(newValue.replaceAll(REPLACENUMEROS, ""));
				}
			});
			
		}
		
		private void validarCampos()
		{
			
		}
	
	@FXML private void actionConfirmar()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		// eliminamos los espacios en blanco de los extremos y reemplazamos los espacios dobles intermedios por un espacio
		String nombre = tfNombre.getText().trim().replaceAll(" +", " ");
		String apellido = tfApellido.getText().trim().replaceAll(" +", " ");
		String telefono = tfTelefono.getText().trim().replaceAll(" +", " ");
		String dni = tfDni.getText().trim().replaceAll(" +", " ");
		String cuit = tfCuit.getText().trim().replaceAll(" +", " ");
		String razonSocial = tfRazon.getText().trim().replaceAll(" +", " ");
		String direccion = tfDireccion.getText().trim().replaceAll(" +", " ");
		String correo = tfCorreo.getText().trim().replaceAll(" +", " ");
		
		// si la longitud es mayor a la pautada, cortamos el texto hasta el valor de la longitud pactada, sino, cortamos el texto con el valor de la longitud
		String nombreFinal = nombre.substring(0, nombre.length() > 50 ? 50 : nombre.length());
		String apellidoFinal = apellido.substring(0, apellido.length() > 50 ? 50 : apellido.length());
		String telefonoFinal = telefono.substring(0, telefono.length() > 13	? 13 : telefono.length());
		String dniFinal = dni.substring(0, dni.length() > 8 ? 8 : dni.length());
		String cuitFinal = cuit.substring(0, cuit.length() > 11 ? 11 : cuit.length());
		String razonSocialFinal = razonSocial.substring(0, razonSocial.length() > 50 ? 50 : razonSocial.length());
		String direccionFinal = direccion.substring(0, direccion.length() > 150 ? 150 : direccion.length());
		String correoFinal = correo.substring(0, correo.length() > 50 ? 50 : correo.length());
		
		System.out.println("tfNombre -> [" + nombre + "]");
		System.out.println("tfApellido -> [" + apellido + "]");
		System.out.println("tftelefono -> [" + telefono + "]");
		System.out.println("tfdni -> [" + dni + "]");
		System.out.println("tfcuit -> [" + cuit + "]");
		System.out.println("tfrazonSocial -> [" + razonSocial + "]");
		System.out.println("tfdireccion -> [" + direccion + "]");
		System.out.println("tfcorreo -> [" + correo + "]" + "\n");
		
		System.out.println("nombreFinal -> [" + nombreFinal + "]" + "[" + nombreFinal.length() + "]");
		System.out.println("apellidoFinal -> [" + apellidoFinal + "]" + "[" + apellidoFinal.length() + "]");
		System.out.println("telefonoFinal -> [" + telefonoFinal + "]" + "[" + telefonoFinal.length() + "]");
		System.out.println("dniFinal -> [" + dniFinal + "]" + "[" + dniFinal.length() + "]");
		System.out.println("cuitFinal -> [" + cuitFinal + "]" + "[" + cuitFinal.length() + "]");
		System.out.println("razonSocialFinal -> [" + razonSocialFinal + "]" + "[" + razonSocialFinal.length() + "]");
		System.out.println("direccionFinal -> [" + direccionFinal + "]" + "[" + direccionFinal.length() + "]");
		System.out.println("correoFinal -> [" + correoFinal + "]" + "[" + correoFinal.length() + "]");

		
		
		btnAceptar.setOnAction(e ->
		{
			Conexion.setAutoCommit(false);
			
			ClienteDTO nuevoCliente = new ClienteDTO(nombreFinal, apellidoFinal);
			nuevoCliente.setDni(dniFinal);
			nuevoCliente.setCuit(cuitFinal);
			nuevoCliente.setTelefono(telefonoFinal);
			nuevoCliente.setRazonSocial(razonSocialFinal);
			nuevoCliente.setDireccion(direccionFinal);
			nuevoCliente.setEMail(correoFinal);
			nuevoCliente.setLocalidad(new LocalidadDTO());
			
			// Si el comboBox de localidad no esta nulo, se setea la localidad al cliente
			if(cmbLocalidad.getSelectionModel().getSelectedItem() != null)
			{
				nuevoCliente.getLocalidad().setIdLocalidad(cmbLocalidad.getSelectionModel().getSelectedItem().getIdLocalidad());
				nuevoCliente.getLocalidad().setNombreLocalidad(cmbLocalidad.getSelectionModel().getSelectedItem().getNombreLocalidad());
				nuevoCliente.getLocalidad().setProvincia(cmbLocalidad.getSelectionModel().getSelectedItem().getProvincia());
			}
			
			ClienteDAOMySQLImpl clienteDao = (ClienteDAOMySQLImpl) UFactoryDAO.getInstancia("CLIENTE");
			
			if (clienteDao.insertar(nuevoCliente) && Conexion.commit())
			{	
				UNotificaciones.notificacion("Alta de Cliente", "¡El cliente ha sido dado de alta exitosamente!");
				
				limpiarCampos();
				
				miAserraderoApp.getClientesControlador().actualizarTablaClientes();
				
				if(miAserraderoApp.getNuevoPedidoControlador() != null) 
					miAserraderoApp.getNuevoPedidoControlador().actualizarComboClientes();
				
				if(miAserraderoApp.getModificarPedidoControlador() != null)
					miAserraderoApp.getModificarPedidoControlador().actualizarComboClientes();
			}
			else
			{
				UAlertas.mostrarAlerta(miAserraderoApp.getPanelRaiz(), miAserraderoApp.getPanelPrincipal(), "Alta de cliente", "No se ha podido dar de alta al cliente");
			}
		});
		
		UAlertas.confirmacion(miAserraderoApp.getPanelRaiz(), miAserraderoApp.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Alta de cliente", "¿Desea confirmar el alta del cliente?");
		
	}
}
