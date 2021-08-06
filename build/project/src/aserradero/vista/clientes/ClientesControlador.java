package aserradero.vista.clientes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.textfield.CustomTextField;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;

import aserradero.AserraderoMain;
import aserradero.controlador.AserraderoApp;
import aserradero.modelo.DAO.mysql.ClienteDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.DetallePedidoDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.LocalidadDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.PedidoDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.ProductoDAOMySQLImpl;
import aserradero.modelo.DTO.ClienteDTO;
import aserradero.modelo.DTO.LocalidadDTO;
import aserradero.modelo.DTO.PedidoDTO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.reportes.JasperReports;
import aserradero.util.UAlertas;
import aserradero.util.UFactoryDAO;
import aserradero.util.UFecha;
import aserradero.util.UNotificaciones;
import aserradero.util.UPedido;
import aserradero.util.UProducto;
import aserradero.util.UTransiciones;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ClientesControlador
{
	// FXML
	@FXML private SplitPane panelRaiz;
	@FXML private BorderPane panelIzquierdo;
	@FXML private SplitPane panelDerecho;
	
	// Tabla de listaClientes
	@FXML private TableView <ClienteDTO> tablaClientes;
	@FXML private TableView <PedidoDTO> tablaHistorial;
	
	@FXML private HBox hboxBotonesCliente;
	
	// Botones
	@FXML private Button btnReporteClientes;
	@FXML private Button btnNuevoCliente;
	@FXML private JFXButton btnGuardar;
	@FXML private JFXButton btnDarDeBaja;
	@FXML private JFXButton btnRehabilitar;
	
	// Las columnas de los datosReporte del cliente
	@FXML private TableColumn <ClienteDTO, String> columnaNombre;
	@FXML private TableColumn <ClienteDTO, String> columnaApellido;
	@FXML private TableColumn <ClienteDTO, Boolean> columnaEstadoCliente;
	
	@FXML private TableColumn <PedidoDTO, Integer> columnaNumeroPedido;
	@FXML private TableColumn <PedidoDTO, LocalDate> columnaFechaT;
	@FXML private TableColumn <PedidoDTO, LocalDate> columnaFechaE;
	@FXML private TableColumn <PedidoDTO, String> columnaEstado;
	@FXML private TableColumn <PedidoDTO, String> columnaForma;
	@FXML private TableColumn <PedidoDTO, Void> columnaVerDetalle;
	
	@FXML private CustomTextField tfFiltro;
	@FXML private Hyperlink linkRefrescar;
	
	@FXML private AnchorPane splitDerecho;
	
	@FXML private Label lblDatosCliente;
	@FXML private Label lblHistorial;
	
	@FXML private TextField tfNombre;
	@FXML private TextField tfApellido;
	@FXML private TextField tfTelefono;
	@FXML private TextField tfDni;
	@FXML private TextField tfCuit;
	@FXML private TextField tfRazonSocial;
	@FXML private TextField tfDireccion;
	@FXML private TextField tfCorreo;
	@FXML private ComboBox <LocalidadDTO> cmbLocalidad;
	
	// DEMAS ATRIBUTOS 
	
	private static final Logger LOGGER = LogManager.getLogger(ClientesControlador.class);
	
	private  SortedList<ClienteDTO> clientesOrdenados;
	
	private BorderPane ventanaSeleccionarElemento;
	
	private JFXSpinner spinner = new JFXSpinner();
	
	private VBox vbox = new VBox();
	private VBox vBoxSinConexion = new VBox();
	private HBox hBoxSinConexion = new HBox();
	
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	private FontAwesomeIconView iconoSinConexion2 = new FontAwesomeIconView(FontAwesomeIcon.PLUG);

	private static final Label PLACE_HOLDER_SIN_RESULTADOS = new Label("Sin resultados");
	private static final Label PLACE_HOLDER_SIN_CLIENTES = new Label("No hay clientes registrados");
	private static final Label PLACE_HOLDER_SIN_PEDIDOS = new Label("Sin pedidos");
	
	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	
	private static final double DIVIDER_POSITION_DEFECTO = 0.4658203125;
	
	private ObservableList<ClienteDTO> listaClientes = FXCollections.observableArrayList();
	
	private ObservableList<LocalidadDTO> listaLocalidades = FXCollections.observableArrayList();
	
	private ObservableList<PedidoDTO> listaPedidos = FXCollections.observableArrayList();
	
	private Map<String, String> datosReporte = new HashMap<>();
	
	private ClienteDTO clienteSeleccionado;
	
	private PedidoDTO pedidoClienteSeleccionado;
	
	// Referencia a la clase coordinadora
	private AserraderoApp miCoordinador;
	
	public ClientesControlador()
	{
		// Por el momento, el método no contiene nada.
	}

	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	public ClienteDTO getClienteSeleccionado()
	{
		return clienteSeleccionado;
	}
	
	@FXML public void initialize()
	{
		tfNombre.setPromptText("(Obligatorio)");
		tfApellido.setPromptText("(Obligatorio)");
		
		/*tfTelefono.setPromptText("Sin espacios, Ej: 3755321367");
		tfDni.setPromptText("Sin espacios. Ej: 365853741");
		tfCuit.setPromptText("Sin espacios. Ej: 20368537412");
		tfRazonSocial.setPromptText("Ej: Los Pinos SRL");
		tfCorreo.setPromptText("Ej: correo@gmail.com");
		tfDireccion.setPromptText("");*/
		
		iconoSinConexion.setSize("40");
		iconoSinConexion2.setSize("10");
		
		linkReintentar.setOnAction(e ->
		{
			actualizarTablaClientes();
		});
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		hBoxSinConexion.setSpacing(5);
		hBoxSinConexion.setAlignment(Pos.CENTER);
		hBoxSinConexion.getChildren().add(iconoSinConexion2);
		hBoxSinConexion.getChildren().add(new Label("No hay conexión con el servidor"));
		
		vbox.setSpacing(5);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().add(spinner);
		
		panelRaiz.setDividerPositions(DIVIDER_POSITION_DEFECTO);
				
		// Se coloca un icono de busqueda al campo de texto
		tfFiltro.setLeft(FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.SEARCH, "18px"));
		
		ventanaSeleccionarElemento = (BorderPane) AserraderoApp.obtenerPanel(AserraderoApp.PANEL_SELECCIONAR_ELEMENTO_DETALLE);
		
		// se inicializa la tabla con las columnas
		columnaNombre.setCellValueFactory(cellData -> cellData.getValue().nombrePropiedad());
		columnaApellido.setCellValueFactory(cellData -> cellData.getValue().apellidoPropiedad());
		columnaEstadoCliente.setCellValueFactory(cellData -> cellData.getValue().habilitadoPropiedad());
	
		//
		tablaClientes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
		{
			
			mostrarDatosCliente(newValue);
		});
		
		tablaHistorial.setPlaceholder(new Label("Sin pedidos"));

		BooleanBinding bb = tfNombre.textProperty().isEmpty().or(tfApellido.textProperty().isEmpty());
		btnGuardar.disableProperty().bind(bb);
		
		columnaNumeroPedido.setCellValueFactory(cellData -> cellData.getValue().idPedidoPropiedad().asObject());
		columnaFechaT.setCellValueFactory(cellData -> cellData.getValue().fechaTomaPropiedad());
		columnaFechaE.setCellValueFactory(cellData -> cellData.getValue().fechaEntregaPropiedad());
		columnaEstado.setCellValueFactory(cellData -> cellData.getValue().estadoPropiedad());
		columnaForma.setCellValueFactory(cellData -> cellData.getValue().formaPropiedad());
		
		// Procedimiento encargado de crear el filtro para el campo de texto de busqueda
		formatearCeldas();
		formatearCampos();
		
		// Se limpian los campos de texto de los datosReporte del cliente
		mostrarDatosCliente(null);
		
		ventanaSeleccionarElemento.setOpacity(1.0);
	}
	
	public void actualizarTablaClientes()
	{			
		//panelRaiz.setDividerPositions(DIVIDER_POSITION_DEFECTO);
		Task<Integer> tarea = new Task<Integer>()
		{			
			@Override
			protected Integer call() throws Exception 
			{
				if(ventanaSeleccionarElemento.getOpacity() < 1.0)
					ventanaSeleccionarElemento.setOpacity(1.0);
				
				Thread.sleep(listaClientes == null || listaClientes.isEmpty() ? 1000 : 250);
				
				ClienteDAOMySQLImpl clienteDao = (ClienteDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.CLIENTE);
				
				listaClientes = clienteDao.obtenerTodos();
				
				System.out.println("LISTA CLIENTE -----------> " + listaClientes);
				
				return null;
			}
		};
		tarea.setOnRunning(e -> 
		{
			tablaClientes.setPlaceholder(vbox);
			linkRefrescar.disableProperty().bind(tarea.runningProperty());
			btnNuevoCliente.disableProperty().bind(tarea.runningProperty());
		});
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actualizarTablaClientes)");
		});
		tarea.setOnSucceeded(e -> 
		{
			if(listaClientes != null)
			{
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
				
				// se filtran los clientes inactivos que no posean pedidos registrados
				/*for(ClienteDTO c : listaClientes)
				{
					if(!c.getHabilitado() && pedidoDao.obtenerPedidosCliente(c.getIdCliente()).isEmpty())
					{
						listaClientes.remove(c);
					}
				}*/
				
				tablaClientes.setItems(listaClientes);
				tablaClientes.setPlaceholder(!listaClientes.isEmpty() ? PLACE_HOLDER_SIN_RESULTADOS : PLACE_HOLDER_SIN_CLIENTES);	
				tablaClientes.refresh();
				aplicarFiltros();
			}
			else
			{
				tablaClientes.setPlaceholder(listaClientes != null && !listaClientes.isEmpty() ? PLACE_HOLDER_SIN_RESULTADOS : vBoxSinConexion);
			}
			
		});
		new Thread(tarea).start();
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
					
				
				return null;
			}
		};
		tarea.setOnRunning(e -> 
		{
			cmbLocalidad.setPlaceholder(spinner);
			
		});
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actualizarComboLocalidades)");
			cmbLocalidad.setItems(null);
			cmbLocalidad.setPlaceholder(hBoxSinConexion);
		});
		tarea.setOnSucceeded(e -> 
		{
			if(listaLocalidades != null)
				if(cmbLocalidad.getItems().isEmpty())
					cmbLocalidad.setItems(listaLocalidades);
			else
			{
				cmbLocalidad.setPlaceholder(hBoxSinConexion);
			}
			
		});
		new Thread(tarea).start();
	}
	
	private void actualizarPedidosCliente(ClienteDTO cliente)
	{
		PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
		
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception
			{
				Thread.sleep(listaPedidos == null || listaPedidos.isEmpty() ? 500 : 250);

				listaPedidos = pedidoDao.obtenerPedidosCliente(cliente.getIdCliente());
				
				return null;
			}
		};
		tarea.setOnRunning(e ->
		{
			tablaHistorial.setPlaceholder(spinner);
		});
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actualizarPedidosCliente)");
		});
		tarea.setOnSucceeded(e ->
		{
			if(listaPedidos != null)
			{
				tablaHistorial.setItems(listaPedidos);
				tablaHistorial.setPlaceholder(PLACE_HOLDER_SIN_PEDIDOS);
			}
			else
			{
				tablaHistorial.setItems(null);
				tablaHistorial.setPlaceholder(vBoxSinConexion);
			}
			
		});
		
		new Thread(tarea).start();
	}
	
	// Carga los datosReporte del cliente en los campos de texto
	private void mostrarDatosCliente(ClienteDTO cliente)
	{
		// Se obtiene el valor de la posicion actual del divisor del panel
		double[] dividerPosition = panelRaiz.getDividerPositions();
		
		if (cliente != null)
		{
			LOGGER.log(Level.DEBUG, "Cliente seleccionado localidad --> [{}]", cliente.getLocalidad().getNombreLocalidad());
		
			clienteSeleccionado = cliente;
			
			tfNombre.setText(cliente.getNombre());
			tfApellido.setText(cliente.getApellido());
			tfTelefono.setText(cliente.getTelefono());
			tfDni.setText(cliente.getDni());
			tfCuit.setText(cliente.getCuit());
			tfRazonSocial.setText(cliente.getRazonSocial());
			tfDireccion.setText(cliente.getDireccion());
			tfCorreo.setText(cliente.getEMail());
			
			if(cliente.getHabilitado())
			{
				if(hboxBotonesCliente.getChildren().contains(btnRehabilitar))
					hboxBotonesCliente.getChildren().remove(btnRehabilitar);
				
				if(!hboxBotonesCliente.getChildren().contains(btnDarDeBaja))
					hboxBotonesCliente.getChildren().add(btnDarDeBaja);
			}
			else
			{
				if(hboxBotonesCliente.getChildren().contains(btnDarDeBaja))
					hboxBotonesCliente.getChildren().remove(btnDarDeBaja);
				
				if(!hboxBotonesCliente.getChildren().contains(btnRehabilitar))
					hboxBotonesCliente.getChildren().add(btnRehabilitar);
			}
			
			if(!panelDerecho.isVisible()) 
			{
				panelRaiz.getItems().remove(1);
				panelRaiz.getItems().add(panelDerecho);
				
				panelDerecho.setVisible(true);
				UTransiciones.fadeIn(panelDerecho, 250);
			}
			
			ventanaSeleccionarElemento.setVisible(false);
			
			// se actualiza el combo de localidades
			actualizarComboLocalidades();
						
			// Se selecciona la localidad del cliente
			if(cmbLocalidad.getItems() != null && !cmbLocalidad.getItems().isEmpty())
				cmbLocalidad.getSelectionModel().select(cliente.getLocalidad());
			
			actualizarPedidosCliente(cliente);	
		}
		else
		{
			panelDerecho.setVisible(false);

			tfNombre.setText("");
			tfApellido.setText("");
			tfTelefono.setText("");
			tfDni.setText("");
			tfCuit.setText("");
			tfRazonSocial.setText("");
			tfDireccion.setText("");
			tfCorreo.setText("");

			cmbLocalidad.getSelectionModel().clearSelection();
			
			tablaHistorial.setItems(null);
			
			panelRaiz.getItems().remove(1);
			panelRaiz.getItems().add(ventanaSeleccionarElemento);
			
			panelDerecho.setVisible(false);
			
			ventanaSeleccionarElemento.setVisible(true);
			
			UTransiciones.fadeIn(ventanaSeleccionarElemento, 250);
		}
		
		// Se setea el valor de la posicion obtenida previamente
		panelRaiz.setDividerPositions(dividerPosition);
	}
	
	private void formatearCeldas()
	{
		final String FONT_WEIGHT_BOLD = "-fx-font-weight: bold;" ;
		final String TEXT_FILL_WHITE = "-fx-text-fill: white;" ;
		
		// Formato de celdas
		columnaFechaT.setCellFactory(column ->
		{
			return new TableCell<PedidoDTO, LocalDate>()
			{
				@Override
				protected void updateItem(LocalDate item, boolean empty)
				{
					super.updateItem(item, empty);
					
					if (item == null)
					{
						setText(null);
					}
					else
					{
						// Formatear Fecha
						setText(UFecha.formatearFecha(item, UFecha.dd_MM_yyyy));
						
						// Si el año obtenido es 9999 ('nulo')
						if (item.getYear() == 9999)
						{
							setText("--");
						}
					}
				}
			};
		});
		
		columnaEstado.setCellFactory(column ->
		{
			return new TableCell<PedidoDTO, String>()
			{
				@Override
				protected void updateItem(String item, boolean empty)
				{
					super.updateItem(item, empty);
					
					setText(item);
					setStyle(FONT_WEIGHT_BOLD);
					if (item == null || empty)
					{
						setText(null);
						setStyle("");
					}
					else
					{
						switch(item)
						{
						case UPedido.PENDIENTE:
							setTextFill(Color.valueOf(Color.ORANGE.toString()));
							setStyle(FONT_WEIGHT_BOLD);
							break;
						case UPedido.EN_CURSO:
							setTextFill(Color.valueOf(Color.BLUE.toString()));
							setStyle(FONT_WEIGHT_BOLD);
							break;
						case UPedido.LISTO:
							setTextFill(Color.valueOf("#0f9d58"));
							setStyle(FONT_WEIGHT_BOLD);
							break;
						case UPedido.ENTREGADO:
							setTextFill(Color.valueOf(Color.BLACK.toString()));
							setStyle(FONT_WEIGHT_BOLD);
							break;
						default:
							setText(null);
							//setStyle("");
						}
					}
				}
			};
		});
		
		columnaFechaE.setCellFactory(column ->
		{
			return new TableCell<PedidoDTO, LocalDate>()
			{
				@Override
				protected void updateItem(LocalDate item, boolean empty)
				{
					super.updateItem(item, empty);
					
					if (item == null)
					{
						setText(null);
					}
					else
					{
						// Formatear Fecha
						setText(UFecha.formatearFecha(item, UFecha.dd_MM_yyyy));
						
						// Si el año obtenido es 9999 ('nulo')
						if (item.getYear() == 9999)
						{
							setText("----");
						}
					}
				}
			};
		});	
		
		columnaEstadoCliente.setCellFactory(column ->
		{
			return new TableCell<ClienteDTO, Boolean>()
			{
				@Override
				protected void updateItem(Boolean item, boolean empty)
				{
					super.updateItem(item, empty);
					
					//setText(item);
					setStyle(FONT_WEIGHT_BOLD);
					if (item == null || empty)
					{
						setText(null);
						setStyle("");
					}
					else
					{
						if(item)
						{
							setText("Activo");
							setTextFill(Color.valueOf("#0f9d58"));
						}
						else
						{
							setText("Inactivo");
							setTextFill(Color.RED);
						}
					}
				}
			};
		});
		
		// añadimos boton a la columna
		Callback<TableColumn<PedidoDTO, Void>, TableCell<PedidoDTO, Void>> cellFactory = new Callback<TableColumn<PedidoDTO, Void>, TableCell<PedidoDTO, Void>>() 
		{
            @Override
            public TableCell<PedidoDTO, Void> call(final TableColumn<PedidoDTO, Void> param) 
            {
               return  new TableCell<PedidoDTO, Void>()
                {
                    private final JFXButton btn = new JFXButton("Ver detalle");
                    {
                    	Text icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.FILE_TEXT_ALT, "20px");
                    	btn.setGraphic(icon);
                    	//btn.disableProperty().bind(peido); // arreglar en los posile desactivar el boton de ver si el pedido no esta entregado
                        btn.setOnAction((ActionEvent event) -> 
                        {
                           // Data data = getTableView().getItems().get(getIndex());
                        	pedidoClienteSeleccionado = getTableView().getItems().get(getIndex());
                            reporteDetallePedido();
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) 
                    {
                        super.updateItem(item, empty);
                        if (empty) 
                        {
                            setGraphic(null);
                        } 
                        else 
                        {
                            setGraphic(btn);
                        }
                    }
                };

            }
        };

        columnaVerDetalle.setCellFactory(cellFactory);
	}
	
	// Formato de campos de texto.
	public void formatearCampos()
	{
		//final String FORMATOLETRAS = "\\sa-zA-Zñ*";
		final String FORMATOLETRAS = "[a-z]";
		final String FORMATONUMEROS = "\\d*";
		
		final String REPLACELETRAS = "[^\\\\sa-zA-Z\\s]";
		final String REPLACENUMEROS = "[^\\d]";
		
		tfFiltro.textProperty().addListener((observable, oldValue, newValue) ->
		{
			if (!newValue.matches(FORMATOLETRAS)) 
			{
				tfFiltro.setText(newValue.replaceAll(REPLACELETRAS, ""));
		    }
		});
		
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
	
	private void aplicarFiltros()
	{
		// 1. Envuelva el ObservableList en un FilteredList (inicialmente muestra todos los datosReporte).
        FilteredList<ClienteDTO> filteredData = new FilteredList<>(tablaClientes.getItems(), c -> true);

     // 2. Establezca el Predicado del filtro siempre que el filtro cambie.
        tfFiltro.textProperty().addListener((observable, oldValue, newValue) -> 
        
            filteredData.setPredicate(cliente ->
            {
                // Si el filtro de texto esta vacio, se muestra todos los listaClientes.
                if (newValue == null || newValue.isEmpty()) 
                {
                    return true;
                }

                // Comparar el nombre y el apellido de cada persona con texto de filtro.
                String lowerCaseFilter = newValue.toLowerCase();

                if (cliente.getNombre().toLowerCase().contains(lowerCaseFilter))
                {
                    return true; // // El filtro coincide con el primer nombre.
                } 
                else if (cliente.getApellido().toLowerCase().contains(lowerCaseFilter))
                {
                    return true; // El filtro coincide con el apellido.
                } 
                else return false; // No coincide.
            })
        );

        // 3. Envuelve la FilteredList en una SortedList. 
        clientesOrdenados = new SortedList<>(filteredData);

        // 4. Enlazar el comparador SortedList al comparador TableView.
        clientesOrdenados.comparatorProperty().bind(tablaClientes.comparatorProperty());

        // 5. Agregar datosReporte ordenados (y filtrados) a la tabla.
        tablaClientes.setItems(clientesOrdenados);
	}
	
	public void limpiarCampos()
	{
		tfFiltro.setText("");
	}
	
	private void reporteDetallePedido()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				ObservableList<ProductoDTO> productos = null;
				
				for(int i = 0; i< 10; i++)
				{
					updateProgress(i, 10);
					
					if(productos == null)
					{
						Thread.sleep(100);
						
						ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
						DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
						
						updateProgress(i+=5, 10);
						
						datosReporte.put("titulo", "Pedido_detalle");
						datosReporte.put("descripcion", "Descripción");
						
						String numPedido = String.valueOf(pedidoClienteSeleccionado.getIdPedido());
						String cliente = pedidoClienteSeleccionado.getCliente().toString();
						String fechaToma = String.valueOf(pedidoClienteSeleccionado.getFechaToma());
						String forma = pedidoClienteSeleccionado.getForma();
						String dni = !pedidoClienteSeleccionado.getCliente().getDni().isEmpty() 
								? String.valueOf(pedidoClienteSeleccionado.getCliente().getDni())
								: "________";
						
						// por el momento, productos clasificados
						productos = productoDao.obtenerProductosPedidoClasificado(pedidoClienteSeleccionado.getIdPedido());
						
						String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
						String totalPies = String.valueOf(UProducto.calcularTotalPies(productos));
						
						JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(tablaHistorial.getSelectionModel().getSelectedItems());
						JRBeanCollectionDataSource itemsJRBean2 = new JRBeanCollectionDataSource(productos);
						
						Map <String, Object> parametros = new HashMap<>();
						parametros.put("pedidoDataSource", itemsJRBean);
						parametros.put("productosDataSource", itemsJRBean2);
						parametros.put("numPedido", numPedido);
						parametros.put("cliente", cliente);
						parametros.put("fechaToma", fechaToma);
						parametros.put("forma", forma);
						parametros.put("dni", dni);
						parametros.put("totalMaderas", totalMaderas);
						parametros.put("totalPies", totalPies);		
						
						JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_detalle_pedido.jasper"), parametros);
												
						updateProgress(i+=2, 10);
					}
					else Thread.sleep(25);
				}
				return null;
			}	
		};
		tarea.setOnRunning(e -> 
		{
			miCoordinador.startLoadingProgress(tarea, "Generando reporte...");
		});
		tarea.setOnSucceeded(e -> 
		{
			miCoordinador.stopLoadingProgress();
			miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, clienteSeleccionado.getEMail());	
			datosReporte.clear();
		});
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actionVerPedido)");			
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");
		});
		new Thread(tarea).start();
	}
	
	// FXML
	
	@FXML private void refrescar()
	{
		double dividerPosition = panelRaiz.getDividers().get(0).getPosition();
		actualizarTablaClientes();
	}
	
	/*@FXML private void actionReporteClientes() 
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				for(double i = 0; i<1; i++)
				{
					JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(tablaClientes.getItems());
					Map <String, Object> parametros = new HashMap<>();
					parametros.put("clientesDataSource", itemsJRBean);
					JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_clientes.jasper"), parametros);
					Thread.sleep(1000);
					updateProgress(i += 0.1, 1);
				}
				return null;
			}
		};
		tarea.setOnRunning(e -> 
		{
			miCoordinador.startLoadingProgress(tarea, "Generando reporte...");
		});
		tarea.setOnSucceeded(e -> 
		{
			miCoordinador.stopLoadingProgress();
			miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, clienteSeleccionado.getEMail());			
		});
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea");
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
		});
		new Thread(tarea).start();
    }
	*/
	@FXML private void actionNuevoCliente()
	{
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_NUEVO_CLIENTE);
	}
	
	@FXML private void actionGuardarCambios()
	{	
		// eliminamos los espacios en blanco de los extremos y reemplazamos los espacios dobles intermedios por un espacio
		String nombre = tfNombre.getText().trim().replaceAll(" +", " ");
		String apellido = tfApellido.getText().trim().replaceAll(" +", " ");
		String telefono = tfTelefono.getText().trim().replaceAll(" +", " ");
		String dni = tfDni.getText().trim().replaceAll(" +", " ");
		String cuit = tfCuit.getText().trim().replaceAll(" +", " ");
		String razonSocial = tfRazonSocial.getText().trim().replaceAll(" +", " ");
		String direccion = tfDireccion.getText().trim().replaceAll(" +", " ");
		String correo = tfCorreo.getText().trim().replaceAll(" +", " ");
		
		// si la longitud es mayor a la pautada, cortamos el texto hasta el valor de la longitud pactada, sino, cortamos el texto con el valor de la longitud
		String nombreFinal = nombre.substring(0, nombre.length() > 50 ? 50 : nombre.length());
		String apellidoFinal = apellido.substring(0, apellido.length() > 50 ? 50 : apellido.length());
		String telefonoFinal = telefono.substring(0, telefono.length() > 13 ? 13 : telefono.length());
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
		
		ClienteDTO cliente = new ClienteDTO();
		cliente.setIdCliente(clienteSeleccionado.getIdCliente());
		cliente.setNombre(nombreFinal);
		cliente.setApellido(apellidoFinal);
		cliente.setDni(dniFinal);
		cliente.setCuit(cuitFinal);
		cliente.setRazonSocial(razonSocialFinal);
		cliente.setDireccion(direccionFinal);
		cliente.setEMail(correoFinal);
		cliente.setTelefono(telefonoFinal);
		cliente.setHabilitado(clienteSeleccionado.getHabilitado());
		cliente.setLocalidad(new LocalidadDTO());
		
		// si el usuario selecciono una localidad, la setemos al cliente
		if(cmbLocalidad.getSelectionModel().getSelectedItem() != null)
		{
			cliente.getLocalidad().setIdLocalidad(cmbLocalidad.getSelectionModel().getSelectedItem().getIdLocalidad());
			cliente.getLocalidad().setNombreLocalidad(cmbLocalidad.getSelectionModel().getSelectedItem().getNombreLocalidad());
			cliente.getLocalidad().setProvincia(cmbLocalidad.getSelectionModel().getSelectedItem().getProvincia());
		}
				
		// preguntamos si el cliente original no es igual al cliente modificado
		if(!cliente.equals(clienteSeleccionado))
		{
			JFXButton btnAceptar = new JFXButton("Aceptar");
			JFXButton btnCancelar = new JFXButton("Cancelar");
			
			btnAceptar.setOnAction(e ->
			{
				ClienteDAOMySQLImpl clienteDao = (ClienteDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.CLIENTE);
				if(clienteDao.actualizar(cliente))
				{
					clienteSeleccionado.setNombre(cliente.getNombre());
					clienteSeleccionado.setApellido(cliente.getApellido());
					clienteSeleccionado.setTelefono(cliente.getTelefono());
					clienteSeleccionado.setDni(cliente.getDni());
					clienteSeleccionado.setCuit(cliente.getCuit());
					clienteSeleccionado.setRazonSocial(cliente.getRazonSocial());
					clienteSeleccionado.setDireccion(cliente.getDireccion());
					clienteSeleccionado.setLocalidad(cliente.getLocalidad());
					clienteSeleccionado.setEMail(cliente.getEMail());
					
					UNotificaciones.notificacion("Modificación de cliente", "Se han guardado los cambios.");
					
				}
				else
				{
					UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Modificación de cliente", "No se ha podido modificar el cliente");
				}
			});
			
			UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Modificación de cliente", "¿Desea confirmar los cambios?");
		}
	}	
	
	@FXML private void actionBajaCliente()
	{
		boolean habilitado = true;

		ClienteDTO c = tablaClientes.getSelectionModel().getSelectedItem();
		
		ClienteDAOMySQLImpl clienteDao = (ClienteDAOMySQLImpl) UFactoryDAO.getInstancia("CLIENTE");
		
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		if(tablaHistorial.getItems() != null)
		{
			// Se comprueba si al menos un pedido no esta entregado
			for(PedidoDTO p : tablaHistorial.getItems())
			{
				if(!p.getEstado().equalsIgnoreCase(UPedido.ENTREGADO))
				{
					// de ser asi, no estara habilitado para darlo de baja
					habilitado = false;
					break;
				}
			}	
		}
		//else return;
		
		if(habilitado)
		{
			if(tablaHistorial.getItems().isEmpty())
			{		
				btnAceptar.setOnAction(e ->
				{
					c.setHabilitado(false);
					
					// si no posee, se lo elimina de manera fisica
					if(clienteDao.actualizar(c))
					{
						UNotificaciones.notificacion("Baja de cliente", "Se ha dado de baja al cliente");
						actualizarTablaClientes();
					}
					else
					{
						clienteSeleccionado.setHabilitado(true);
						UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Baja de cliente", "No se ha podido dar de baja al cliente.");
					}
				});
				
				UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Baja de cliente", "¿Desea confirmar la baja del cliente?");
			}
			else
			{
				/*TextInputDialog dialog = new TextInputDialog();
				dialog.setTitle("Baja de cliente");
				dialog.setHeaderText("¿Desea confirmar la baja del cliente?");
				dialog.setContentText("Ingrese el motivo de la baja (opcional):");
				Optional<String> resultado = dialog.showAndWait();
				if(resultado.isPresent())
				{
					System.out.println("Resultado: " + resultado.get());
					clienteSeleccionado.setHabilitado(false);
					clienteSeleccionado.setMotivoBaja(resultado.get());
					if(clienteDao.actualizar(clienteSeleccionado))
					{
						actualizarTablaClientes();
						UNotificaciones.notificacion("Baja de cliente", "Se ha dado de baja al cliente");
					}
					else
					{
						clienteSeleccionado.setHabilitado(true);
						clienteSeleccionado.setMotivoBaja("");
						UAlertas.alerta("Baja de cliente", "No se ha podido dar de baja al cliente.");
					}
				}*/
				
				btnAceptar.setOnAction(e ->
				{	
					c.setHabilitado(false);
					
					if(clienteDao.actualizar(c))
					{
						UNotificaciones.notificacion("Baja de cliente", "Se ha dado de baja al cliente");
						mostrarDatosCliente(c);
					}
					else
					{
						c.setHabilitado(true);
						UAlertas.alerta("Baja de cliente", "No se ha podido dar de baja al cliente.");
					}
				});
				
				UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Baja de cliente", "¿Confirmar la baja del cliente?");
			}
		}
		else UAlertas.mostrarInfo(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Baja de cliente", "No se puede eliminar al cliente, debido a que posee pedidos pendientes");	
	}
	
	@FXML private void actionRehabilitarCliente()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		btnAceptar.setOnAction(e ->
		{
			ClienteDAOMySQLImpl clienteDao = (ClienteDAOMySQLImpl) UFactoryDAO.getInstancia("CLIENTE");
			
			ClienteDTO c = tablaClientes.getSelectionModel().getSelectedItem();
			c.setHabilitado(true);
			System.out.println("Cliente getHabilitado -> " + c.getHabilitado());
			System.out.println("Cliente habilitadoPropiedad -> " + c.habilitadoPropiedad());
			
			if(clienteDao.actualizar(c))
			{
				UNotificaciones.notificacion("Alta de cliente", "El cliente ha sido rehabilitado.");
				mostrarDatosCliente(c);
			}
			else
			{
				c.setHabilitado(false);
				UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Alta de cliente", "No se ha podido dar de alta al cliente");
			}
		});
		
		UAlertas.mostrarDialogoEntrada(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Alta de cliente", "¿Confirmar el alta de cliente?");
	}
	
	
	
}
