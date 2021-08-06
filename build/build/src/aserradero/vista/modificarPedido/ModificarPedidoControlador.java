package aserradero.vista.modificarPedido;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ConcurrentModificationException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;

import aserradero.controlador.AserraderoApp;
import aserradero.modelo.DAO.mysql.ClienteDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.DetallePedidoDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.EspecificacionPedidoDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.PedidoDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.ProduccionDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.ProductoDAOMySQLImpl;
import aserradero.modelo.DTO.ClienteDTO;
import aserradero.modelo.DTO.DetallePedidoDTO;
import aserradero.modelo.DTO.EspecificacionPedidoDTO;
import aserradero.modelo.DTO.PedidoDTO;
import aserradero.modelo.DTO.ProduccionDTO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UAlertas;
import aserradero.util.UFactoryDAO;
import aserradero.util.UNotificaciones;
import aserradero.util.UPedido;
import aserradero.util.UProducto;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class ModificarPedidoControlador
{
	@FXML private SplitPane panelCentro;
	@FXML private BorderPane panelDerecho;
	@FXML private BorderPane panelEspecificacion;
	@FXML private BorderPane panelCarga;
	
	@FXML private JFXComboBox <ClienteDTO> cmbClientes;
	@FXML private JFXButton btnNuevoCliente;
	@FXML private JFXRadioButton radioDemanda;
	@FXML private JFXRadioButton radioStock;
	@FXML private JFXRadioButton radioProductosLibres;
	@FXML private JFXButton btnGuardarCambios;
	
	@FXML private Button btnMostrarOriginales;
	@FXML private Button btnMostrarAgregados;
	@FXML private Button btnMostrarModificados;
	@FXML private Button btnMostrarQuitados;
	
	@FXML private Label lblNumeroPedido;
	@FXML private Label lblTotalPies;
	@FXML private Label lblTotalPies1;
	@FXML private Label lblTotal;
	@FXML private Label lblTotal2;
	@FXML private Label lblTotal3;
	
	@FXML private JFXTextField tfEspesorBuscado;
	@FXML private JFXTextField tfAnchoBuscado;
	@FXML private JFXTextField tfLargoBuscado;
	@FXML private JFXTextField tfUnidadesBuscado;
	
	@FXML private JFXTextField tfEspesorEspecificado;
	@FXML private JFXTextField tfAnchoEspecificado;
	@FXML private JFXTextField tfLargoEspecificado;
	@FXML private JFXTextField tfUnidadesEspecificado;
	
	@FXML private Label lblEspesor;
	@FXML private Label lblAncho;
	@FXML private Label lblLargo;
	@FXML private Label lblUnidades;
	@FXML private Label lblPies;
	
	@FXML private HBox hboxDisponibilidad;
	@FXML private Label lblDisponibilidad;
	@FXML private Separator separadorDisponibilidad;
	@FXML private Label lblCantidadDisponible;
	
	@FXML private Label lblEncabezado;

	@FXML private JFXButton btnAgregarProducto;
	@FXML private JFXButton btnAgregarProducto1;
	@FXML private JFXButton btnAgregarUnidades;
	
	@FXML private Spinner <Integer> spUnidades;
	
	@FXML private TableView <ProductoDTO> tablaProductos;
	@FXML private TableView <ProductoDTO> tablaProductosEspecificados;
	@FXML private TableView <ProductoDTO> tablaProductosDisponibles;
	
	@FXML private TableColumn <ProductoDTO, String> columnaEspesor;
	@FXML private TableColumn <ProductoDTO, String> columnaAncho;
	@FXML private TableColumn <ProductoDTO, Double> columnaLargo;
	@FXML private TableColumn <ProductoDTO, Integer> columnaUnidades;
	@FXML private TableColumn <ProductoDTO, Double> columnaPies;
	@FXML private TableColumn <ProductoDTO, Void> columnaQuitarProducto;
	
	@FXML private TableColumn <ProductoDTO, String> columnaEspesor1;
	@FXML private TableColumn <ProductoDTO, String> columnaAncho1;
	@FXML private TableColumn <ProductoDTO, Double> columnaLargo1;
	@FXML private TableColumn <ProductoDTO, Integer> columnaUnidades1;
	@FXML private TableColumn <ProductoDTO, Double> columnaPies1;
	@FXML private TableColumn <ProductoDTO, Void> columnaQuitarProducto1;
	
	@FXML private TableColumn <String, String> columnaStock;
	
	@FXML private TableColumn <ProductoDTO, String> columnaEspesor2;
	@FXML private TableColumn <ProductoDTO, String> columnaAncho2;
	@FXML private TableColumn <ProductoDTO, Double> columnaLargo2;
	@FXML private TableColumn <ProductoDTO, Integer> columnaUnidades2;
	@FXML private TableColumn <ProductoDTO, Double> columnaPies2;
	@FXML private TableColumn <ProductoDTO, Void> columnaAgregarProducto;
	
	private ToggleGroup grupoRadioForma;
	
	private AserraderoApp miCoordinador;
	private ProductoDTO productoEspecificado = null;
	private ProductoDTO productoEspecificado1 = null;
	private ProductoDTO productoExistente = null;
	private EspecificacionPedidoDTO detalleEspecificacionPedido;
	private BooleanProperty pedidoModificado2 = new SimpleBooleanProperty(false);
	private ObservableList<ProductoDTO> productosOriginales;
	private ObservableList<ProductoDTO> listaProductosModificados;
	private ObservableList<ProductoDTO> listaProductosAgregados;
	private ObservableList<ProductoDTO> listaProductosQuitados;
	private DoubleProperty piesProperty = new SimpleDoubleProperty();
	private DoubleProperty piesProperty1 = new SimpleDoubleProperty();
	private FontAwesomeIconView iconoDisponible = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
	private FontAwesomeIconView iconoNoDisponible = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	private final JFXSpinner spinner = new JFXSpinner();
	private VBox vBoxSinConexion = new VBox();
	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	private ObservableList<ProductoDTO> productosDisponibles = FXCollections.observableArrayList();
	
	private static final Logger LOGGER = LogManager.getLogger(ModificarPedidoControlador.class);
	
	private static final Label PLACE_HOLDER_SIN_PRODUCTOS2 = new Label("No hay productos especificados");
	private static final Label PLACE_HOLDER_SIN_PRODUCTOS = new Label("No hay productos cargados");
	private static final Label PLACE_HOLDER_SIN_STOCK = new Label("Sin stock");
	private static final Label PLACE_HOLDER_SIN_PRODUCTOS_INDEFINIDOS = new Label("Sin productos libres");
	private static final Label PLACE_HOLDER_ESPECIFICAR = new Label("Se requiere especificar los productos a aserrar");
	private static final Label PLACE_HOLDER_CARGAR = new Label("Se requiere agregar los productos en stock disponibles");
	private static final Label PLACE_HOLDER_CARGAR_LIBRES = new Label("Se requiere agregar los productos libres disponibles");
	
	private PedidoDTO pedidoSeleccionado;
	
	private boolean pedidoModificado;
	
	public ModificarPedidoControlador() {}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	@FXML public void initialize()
	{
		PLACE_HOLDER_ESPECIFICAR.setId("lblIndicadorObligatorio");
		PLACE_HOLDER_CARGAR.setId("lblIndicadorObligatorio");
		PLACE_HOLDER_CARGAR_LIBRES.setId("lblIndicadorObligatorio");
		
		// PANEL IZQUIERDO
		productosOriginales = FXCollections.observableArrayList();
		
		listaProductosModificados = FXCollections.observableArrayList();
		listaProductosAgregados =  FXCollections.observableArrayList();
		listaProductosQuitados =  FXCollections.observableArrayList();
		
		btnMostrarOriginales.setVisible(false);
		btnMostrarAgregados.setVisible(false);
		btnMostrarModificados.setVisible(false);
		btnMostrarQuitados.setVisible(false);
		
		//ClienteDAOMySQLImpl clienteDao = (ClienteDAOMySQLImpl) UFactoryDAO.getInstancia("CLIENTE");
		//cmbClientes.setItems(clienteDao.obtenerTodos());
		
		grupoRadioForma = new ToggleGroup();
		radioStock.setToggleGroup(grupoRadioForma);
		radioDemanda.setToggleGroup(grupoRadioForma);
		radioProductosLibres.setToggleGroup(grupoRadioForma);
		
		radioDemanda.setUserData("A Pedido");
		radioStock.setUserData("Stock");
		radioProductosLibres.setUserData("Productos libres");
		
		radioDemanda.setSelected(true);
		//lblDisponibilidad.setVisible(false);
		
		lblTotalPies.textProperty().bind(piesProperty.asString());
		lblTotalPies1.textProperty().bind(piesProperty1.asString());
		
		lblDisponibilidad.setContentDisplay(ContentDisplay.RIGHT);
		lblDisponibilidad.visibleProperty().bind(piesProperty.greaterThan(0d));
		
		separadorDisponibilidad.setVisible(false);
		
		lblCantidadDisponible.setVisible(false);
		
		iconoDisponible.setStyleClass("-fx-text-fill: #37b966;");
		iconoNoDisponible.setStyleClass("-fx-text-fill: #37b966;");
		
		//spinnerUnidades.getValueFactory().valueProperty().bindBidirectional(tablaProductos.getSelectionModel().getSelectedItem().stockPropiedad());
		
		// Bindear correctamente el buton confirmar
		btnGuardarCambios.disableProperty().bind(cmbClientes.getSelectionModel().selectedItemProperty().isNull()
				.or(tablaProductos.itemsProperty().isNull()));
		//btnGuardarCambios.styleProperty().bind(Bindings.when(pedidoModificado2).then(thenValue));
		
		pedidoModificado2.addListener((obs, old, newValue) ->
		{
			if(newValue)
			{
				btnGuardarCambios.getStyleClass().add("boton-verde");
			}
			else btnGuardarCambios.getStyleClass().clear();
		});
		
		tablaProductos.setPlaceholder(PLACE_HOLDER_SIN_PRODUCTOS);
		tablaProductos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tablaProductos.getSelectionModel().selectedItemProperty().addListener((old, obs, newValue) ->
		{
			LOGGER.log(Level.DEBUG, "Producto seleccionado --> {}", newValue);
			
		});
		
		columnaEspesor.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
		columnaAncho.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
		columnaLargo.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
		columnaUnidades.setCellValueFactory(cellData -> cellData.getValue().stockPropiedad().asObject());
		columnaPies.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject());
		
		// tabla productos especificados
		tablaProductosEspecificados.setPlaceholder(PLACE_HOLDER_ESPECIFICAR);
		
		columnaEspesor1.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
		columnaAncho1.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
		columnaLargo1.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
		columnaUnidades1.setCellValueFactory(cellData -> cellData.getValue().stockPropiedad().asObject());
		columnaPies1.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject());
		
		// PANEL DERECHO
		
		iconoSinConexion.setSize("40");
		
		linkReintentar.setOnAction(e ->
		{
			cargarDatosPedido();
		});
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		columnaEspesor2.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
		columnaAncho2.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
		columnaLargo2.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
		columnaUnidades2.setCellValueFactory(cellData -> cellData.getValue().stockPropiedad().asObject());
		columnaPies2.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject());
		

		tablaProductosDisponibles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tablaProductosDisponibles.getSelectionModel().selectedItemProperty().addListener((old, obs, newValue) ->
		{
			LOGGER.log(Level.DEBUG, "Producto disponible seleccionado -> {}", newValue);
			mostrarUnidades(newValue);
		});

		// formateamos los campos de textos de busqueda de producto
		formatearCampos();

		// formateamos los campos de textos de especificacion de producto
		formatearCampos2();

		formatearCeldas();
	}
	
	// METODOS
	public void cargarDatosPedido() 
	{
		
		// Se toma el pedido seleccionado, previamente seteado al darle click en modificar
		pedidoSeleccionado = miCoordinador.getPedidosControlador().getPedidoSeleccionado();
		
		// Se selecciona al cliente perteneciente al pedido
		actualizarComboClientes();
		
		lblNumeroPedido.setText(String.valueOf("#" + pedidoSeleccionado.getIdPedido()));
		
		String formaSeleccionada = pedidoSeleccionado.getForma();
		
		// Se selecciona la forma del pedido segun sea la selccionada
		
		if(formaSeleccionada.equalsIgnoreCase("A pedido"))
		{
			grupoRadioForma.selectToggle(radioDemanda);
		}
		else if(formaSeleccionada.equalsIgnoreCase("Stock"))
		{
			grupoRadioForma.selectToggle(radioStock);
		}
		else
		{
			grupoRadioForma.selectToggle(radioProductosLibres);
		}
		
		if(Conexion.comprobarConexion())
		{
			DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
			productosOriginales = detallePedidoDao.obtenerPorId(pedidoSeleccionado.getIdPedido()).getProductos();

			// se setea la reserva a 0, en caso de que la forma sea 'a pedido'
			LOGGER.log(Level.DEBUG, " PEDIDO SELECCINADO ESTADO --> {}", pedidoSeleccionado.getEstado());
			if(pedidoSeleccionado.getForma().equalsIgnoreCase("A pedido"))
			{
				for(ProductoDTO p : productosOriginales)
				{
					p.setReserva(0);
				}
			}
		}
		else
		{
			productosOriginales = null;
		}
		
		actualizarTablaProductos(productosOriginales);
		
		// preguntamos la forma del pedido
		if(pedidoSeleccionado.getForma().equalsIgnoreCase("A pedido"))
		{
			// Se actualizan los productos especificados que tiene el pedido
			actualizarProductosEspecificados(pedidoSeleccionado.getIdPedido());
		}
		
		// Se actualizan los productosOriginales disponibles
		actualizarProductosDisponibles();
		
		//habilitar funcionalidades segun el estado del pedido
		/*btnAgregarProducto.disableProperty().bind(piesProperty.isEqualTo(0)
				.or(pedidoSeleccionado.estadoPropiedad().isEqualTo("Entregado"))
				.or(pedidoSeleccionado.estadoPropiedad().isEqualTo("Listo")));*/
		
		
		btnAgregarUnidades.disableProperty().bind(tablaProductosDisponibles.getSelectionModel().selectedItemProperty().isNull()
				.or(pedidoSeleccionado.estadoPropiedad().isEqualTo("Entregado")
				.or(pedidoSeleccionado.estadoPropiedad().isEqualTo("Listo"))));
		
		spUnidades.setEditable(false);
		

	}
	
	public void actualizarComboClientes()
	{
		ClienteDAOMySQLImpl clienteDao = (ClienteDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.CLIENTE);
		cmbClientes.setItems(clienteDao.obtenerActivos());
		
		if(cmbClientes.getItems() != null)
		{
			for (ClienteDTO c : cmbClientes.getItems())
			{
				if(c.getIdCliente() == pedidoSeleccionado.getCliente().getIdCliente())
				{
					// seleccionamos el cliente perteneciente al pedido
					cmbClientes.getSelectionModel().select(c);
					break;
				}
				else cmbClientes.getSelectionModel().clearSelection();
			}	
		}
		
	}
	
	public void actualizarProductosEspecificados(int idPedido)
	{
		
		EspecificacionPedidoDAOMySQLImpl especificacionPedidoDao = (EspecificacionPedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.ESPECIFICACIONP);
		ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
		ObservableList<ProductoDTO> productos = FXCollections.observableArrayList();
		
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				detalleEspecificacionPedido = especificacionPedidoDao.obtenerPorId(idPedido);
				
				productos.addAll(detalleEspecificacionPedido.getProductos());
				
				return null;
			}
		};
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actualizarProductosEspecificados)");
			detalleEspecificacionPedido = null;
			detalleEspecificacionPedido = null;
			tablaProductosEspecificados.setItems(null);
			tablaProductosEspecificados.setPlaceholder(vBoxSinConexion);
			lblTotal3.setVisible(false);
		});
		
		tarea.setOnRunning(e ->
		{
			tablaProductosEspecificados.setPlaceholder(spinner);
			
		});
		
		tarea.setOnSucceeded(e ->
		{
			tablaProductosEspecificados.setItems(productos);
			tablaProductosEspecificados.refresh();
			tablaProductosEspecificados.setPlaceholder(PLACE_HOLDER_ESPECIFICAR);
			lblTotal3.setText(UProducto.calcularTotalMaderas(tablaProductosEspecificados.getItems()) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaProductosEspecificados.getItems())));
			lblTotal3.setVisible(true);
		});
		
		new Thread(tarea).start();
	}
	
	private void actualizarTablaProductos(ObservableList<ProductoDTO> productos)
	{	
		if(productos != null)
		{
			tablaProductos.getItems().addAll(UProducto.clonarProductos(productos));
			
			tablaProductos.setPlaceholder(PLACE_HOLDER_SIN_PRODUCTOS);
		}
		else
		{
			tablaProductos.setPlaceholder(new Label(""));
		}
	}
	
	private void actualizarProductosDisponibles() 
	{
		String formaSeleccionada = grupoRadioForma.getSelectedToggle().getUserData().toString();
		
		Task<Integer> tarea = new Task<Integer>()
		{

			@Override
			protected Integer call() throws Exception 
			{
				LOGGER.log(Level.DEBUG, "Toggle selected --> {}", formaSeleccionada);
				
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				
				if(formaSeleccionada.equalsIgnoreCase("A pedido"))
				{
					// seleccionamos la forma para indicar
					radioDemanda.setDisable(false);
					radioStock.setDisable(true);
					radioProductosLibres.setDisable(true);
					
					// activamos el panel de especificacion
					panelEspecificacion.setDisable(false);
					
					lblEncabezado.setText("Productos libres");
					
					productosDisponibles = productoDao.obtenerProductosProduccion(-1);
					
					// desactivamos el boton de guardar si no se especifican los productos. Y el cliente siempre es obligatorio
					btnGuardarCambios.disableProperty().bind(Bindings.size(tablaProductosEspecificados.getItems()).isEqualTo(0).or(cmbClientes.getSelectionModel().selectedItemProperty().isNull()));
					
				}
				else if(formaSeleccionada.equalsIgnoreCase("Stock"))
				{
					// seleccionamos la forma para indicar
					radioDemanda.setDisable(true);
					radioStock.setDisable(false);
					radioProductosLibres.setDisable(true);
					
					// desactivamos el panel de especificacion
					panelEspecificacion.setDisable(true);
					
					lblEncabezado.setText("Stock disponible");
					
					productosDisponibles = productoDao.obtenerTodos();
					
					// desactivamos el boton de guardar si no se cargarn los productos. Y el cliente siempre es obligatorio
					btnGuardarCambios.disableProperty().bind(Bindings.size(tablaProductos.getItems()).isEqualTo(0).or(cmbClientes.getSelectionModel().selectedItemProperty().isNull()));
				}
				else
				{
					// seleccionamos la forma para indicar
					radioDemanda.setDisable(true);
					radioStock.setDisable(true);
					radioProductosLibres.setDisable(false);
					
					// desactivamos el panel de especificacion
					panelEspecificacion.setDisable(true);
					
					productosDisponibles = productoDao.obtenerProductosProduccion(-1);
					
					lblEncabezado.setText("Productos libres");
					
					// desactivamos el boton de guardar si no se cargarn los productos libres. Y el cliente siempre es obligatorio
					btnGuardarCambios.disableProperty().bind(Bindings.size(tablaProductos.getItems()).isEqualTo(0).or(cmbClientes.getSelectionModel().selectedItemProperty().isNull()));
				}
				
				return null;
			}
			
		};
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.DEBUG, "Fallo la tarea - (actualizarProductosDisponibles)");
			calcularProducto();
			calcularTotales();
			tablaProductosDisponibles.setPlaceholder(vBoxSinConexion);
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Modificación de pedido", "Ha ocurrido un error. Compruebe la conexión del servidor");
		});

		tarea.setOnRunning(e ->
		{
			tablaProductosDisponibles.setPlaceholder(spinner);	
			tablaProductosDisponibles.disableProperty().bind(tarea.runningProperty());
		});
		
		tarea.setOnSucceeded(e ->
		{
			if(productosDisponibles != null)
			{
				System.out.println("SUCEDIO!!!!!!!!");
				
				tablaProductosDisponibles.setPlaceholder(formaSeleccionada.equalsIgnoreCase("Productos libres") ? PLACE_HOLDER_SIN_PRODUCTOS_INDEFINIDOS : PLACE_HOLDER_SIN_STOCK);
				
				//if(!formaSeleccionada.equalsIgnoreCase("A pedido"))
					tablaProductosDisponibles.setItems(productosDisponibles);
				//else
					//tablaProductosDisponibles.getItems().clear();
				
				tablaProductos.setPlaceholder(formaSeleccionada.equalsIgnoreCase("Stock") ? PLACE_HOLDER_CARGAR : PLACE_HOLDER_CARGAR_LIBRES);
				
				// ocultamos los place holders de las tablas segun la forma seleccionada
				tablaProductosEspecificados.getPlaceholder().setVisible(formaSeleccionada.equalsIgnoreCase("A pedido"));
				tablaProductos.getPlaceholder().setVisible(formaSeleccionada.equalsIgnoreCase("Stock") || formaSeleccionada.equalsIgnoreCase("Productos libres"));
				tablaProductosDisponibles.getPlaceholder().setVisible(!formaSeleccionada.equalsIgnoreCase("A pedido"));
				
			}
				
			else
			{
				tablaProductosDisponibles.setItems(null);
				tablaProductosDisponibles.setPlaceholder(vBoxSinConexion);
				cmbClientes.setItems(null);
				cmbClientes.setPlaceholder(new Label("No hay conexión con el servidor"));
			}
				
			calcularProducto();
			calcularProductoEspecificado();
			calcularTotales();
		});
		
		new Thread(tarea).start();
		
	}
	
	private void actualizarProductosDisponibles2() 
	{
		String formaSeleccionada = grupoRadioForma.getUserData().toString();
		
		Task <Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				LOGGER.log(Level.DEBUG, "Toggle selected --> {}", grupoRadioForma.getUserData());
				
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				
				// A demanda
				if(formaSeleccionada.equalsIgnoreCase("A pedido"))
				{
					// actvivamos el panel de especificacion
					panelEspecificacion.setDisable(false);
					
					// desactivamos el panel de carga
					panelCarga.setDisable(true);
					
					// desactivamos el panel derecho
					panelDerecho.setDisable(true);
					
					if(!tablaProductos.getItems().isEmpty())
					{
						// limpiamos lo que el usuario habia cargado
						tablaProductos.getItems().clear();
					}
					
					if(!tablaProductosEspecificados.getItems().isEmpty())
					{
						// limpiamos lo que el usuario habia especificado
						tablaProductosEspecificados.getItems().clear();
					}
					
					btnGuardarCambios.disableProperty().unbind();
					
					// desactivamos el boton de confirmacion si no se especifican los productos. Y el cliente siempre es obligatorio
					btnGuardarCambios.disableProperty().bind(Bindings.size(tablaProductosEspecificados.getItems()).isEqualTo(0).or(cmbClientes.getSelectionModel().selectedItemProperty().isNull()));
					
					LOGGER.log(Level.DEBUG, "A demanda final");
				}
				// Con stock
				else if(formaSeleccionada.equalsIgnoreCase("Stock"))
				{
					// desactvivamos el panel de especificacion
					panelEspecificacion.setDisable(true);
					
					// activamos el panel de carga
					panelCarga.setDisable(false);
					
					// activamos el panel derecho (productos disponibles [libres o stock])
					panelDerecho.setDisable(false);		
					
					
					if(!tablaProductos.getItems().isEmpty())
					{
						// limpiamos lo que el usuario habia cargado
						tablaProductos.getItems().clear();
					}
					
					if(!tablaProductosEspecificados.getItems().isEmpty())
					{
						// limpiamos lo que el usuario habia especificado
						tablaProductosEspecificados.getItems().clear();
					}
					
					// tomamos los productos disponibles en stock
					productosDisponibles = productoDao.obtenerTodos();
					
					btnGuardarCambios.disableProperty().unbind();
					
					// desactivamos el boton de confirmacion si no se especifican los productos. Y el cliente siempre es obligatorio
					btnGuardarCambios.disableProperty().bind(Bindings.size(tablaProductos.getItems()).isEqualTo(0).or(cmbClientes.getSelectionModel().selectedItemProperty().isNull()));
					
					LOGGER.log(Level.DEBUG, "Stock final");
					
				}
				// con productos libres
				else
				{
					// desactvivamos el panel de especificacion
					panelEspecificacion.setDisable(true);
					
					// activamos el panel de carga
					panelCarga.setDisable(false);
					
					// activamos el panel derecho (productos disponibles [libres o stock])
					panelDerecho.setDisable(false);		
					
					if(!tablaProductos.getItems().isEmpty())
					{
						// limpiamos lo que el usuario habia cargado
						tablaProductos.getItems().clear();
					}
					
					if(!tablaProductosEspecificados.getItems().isEmpty())
					{
						// limpiamos lo que el usuario habia especificado
						tablaProductosEspecificados.getItems().clear();
					}
					
					// tomamos los productos libres que existan en la base de datos
					productosDisponibles = productoDao.obtenerProductosProduccion(-1);
					
					tablaProductosDisponibles.setItems(productosDisponibles);

					btnGuardarCambios.disableProperty().unbind();
					// desactivamos el boton de confirmacion si no se especifican los productos. Y el cliente siempre es obligatorio
					btnGuardarCambios.disableProperty().bind(Bindings.size(tablaProductos.getItems()).isEqualTo(0).or(cmbClientes.getSelectionModel().selectedItemProperty().isNull()));
					
					LOGGER.log(Level.DEBUG, "Con productos libres final");
				}
			
				return null;
			}
			
		};
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.DEBUG, "Fallo la tarea - (actualizarProductosDisponibles)");
			calcularProducto();
			calcularTotales();
			tablaProductosDisponibles.setPlaceholder(vBoxSinConexion);
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Modificación de pedido", "Ha ocurrido un error. Compruebe la conexión del servidor");
		});
		
		tarea.setOnRunning(e ->
		{
			tablaProductosDisponibles.setPlaceholder(spinner);	
			tablaProductosDisponibles.disableProperty().bind(tarea.runningProperty());
		});
		
		tarea.setOnSucceeded(e ->
		{
			if(productosDisponibles != null)
			{
				System.out.println("SUCEDIO!!!!!!!!");
				tablaProductosDisponibles.setPlaceholder(formaSeleccionada.equalsIgnoreCase("A demanda") ? PLACE_HOLDER_SIN_PRODUCTOS_INDEFINIDOS : PLACE_HOLDER_SIN_STOCK);
				tablaProductosDisponibles.setItems(productosDisponibles);
				
			}
			
			else
			{
				tablaProductosDisponibles.setItems(null);
				tablaProductosDisponibles.setPlaceholder(vBoxSinConexion);
				cmbClientes.setItems(null);
				cmbClientes.setPlaceholder(new Label("No hay conexión con el servidor"));
			}
			
			
			calcularProducto();
			calcularProductoEspecificado();
			calcularTotales();
		});
		
		new Thread(tarea).start();
		
	}
	
	private void calcularProducto()
	{
		String e = !tfEspesorBuscado.getText().isEmpty() && !tfEspesorBuscado.getText().startsWith(".") && Double.parseDouble(tfEspesorBuscado.getText()) > 0d ? tfEspesorBuscado.getText() : "";
		String a = !tfAnchoBuscado.getText().isEmpty() && !tfAnchoBuscado.getText().startsWith(".") && Double.parseDouble(tfAnchoBuscado.getText()) > 0d ? tfAnchoBuscado.getText() : "";
		String l = !tfLargoBuscado.getText().isEmpty() && !tfLargoBuscado.getText().startsWith(".") && Double.parseDouble(tfLargoBuscado.getText()) > 0d ? tfLargoBuscado.getText() : "";
		String uni = !tfUnidadesBuscado.getText().startsWith("0") && !tfUnidadesBuscado.getText().startsWith(".") ? tfUnidadesBuscado.getText() : "";

		btnAgregarProducto.setDisable(false);

		if(!e.isEmpty() && !a.isEmpty() && !l.isEmpty() && !uni.isEmpty())
		{
			String forma = grupoRadioForma.getSelectedToggle().getUserData().toString();
			
			double espesor = Double.parseDouble(e);
			double ancho = Double.parseDouble(a);
			double largo = Double.parseDouble(l);
			int unidades = Integer.parseInt(uni);
			
			piesProperty.set(UProducto.calcularPiesCuadrados(espesor, ancho, largo, unidades, UProducto.DOS_DECIMALES));

			lblDisponibilidad.getStyleClass().clear();

			productoEspecificado = new ProductoDTO();
			productoEspecificado.setIdProducto(0);
			productoEspecificado.setEspesor(espesor);
			productoEspecificado.setAncho(ancho);
			productoEspecificado.setLargo(largo);
			productoEspecificado.setStock(unidades);
			productoEspecificado.setPies();
			productoEspecificado.setReserva(0);

			if(tablaProductosDisponibles.getItems() != null)
			{
				// comprobar existencia del producto en la tabla de productosOriginales disponibles
				productoExistente = UProducto.comprobrarExistenciaProducto(productoEspecificado, tablaProductosDisponibles.getItems());
				if(productoExistente != null && lblDisponibilidad.isVisible())
				{
					productoEspecificado.setIdProducto(productoExistente.getIdProducto());

					if(productoEspecificado.getStock() <= productoExistente.getStock())
					{
						lblDisponibilidad.getStyleClass().add("lbl-stock-disponible");
						lblDisponibilidad.setText(forma.equalsIgnoreCase("Stock") ? "Stock disponible" : "Cantidad disponible");
						btnAgregarProducto.setDisable(false);
						separadorDisponibilidad.setVisible(false);
						lblCantidadDisponible.setVisible(false);
						lblDisponibilidad.setGraphic(iconoDisponible);
					}
					else
					{
						lblDisponibilidad.getStyleClass().add("lbl-sin-stock");
						lblDisponibilidad.setText(forma.equalsIgnoreCase("Stock") ? "Stock insuficiente" : "Cantidad insuficiente");
						btnAgregarProducto.setDisable(true);
						separadorDisponibilidad.setVisible(true);
						lblCantidadDisponible.setVisible(true);
						lblCantidadDisponible.setText(String.valueOf(productoExistente.getStock()).concat(" disponibles"));
						lblDisponibilidad.setGraphic(iconoNoDisponible);
					}
				}
				else
				{
					lblDisponibilidad.getStyleClass().add("lbl-sin-stock");
					lblDisponibilidad.setText(forma.equalsIgnoreCase("Stock") ? "Sin stock" : "No disponible");
					btnAgregarProducto.setDisable(true);
				}
			}
			else
			{
				piesProperty.set(0.0);
				separadorDisponibilidad.setVisible(false);
				lblCantidadDisponible.setVisible(false);
				btnAgregarProducto.setDisable(true);
			}
			
		}
		else 
		{
			piesProperty.set(0.0);
			btnAgregarProducto.setDisable(true);
		}
	}
	
	private void calcularProductoEspecificado()
	{
		String e = !tfEspesorEspecificado.getText().isEmpty() && !tfEspesorEspecificado.getText().startsWith(".") && Double.parseDouble(tfEspesorEspecificado.getText()) > 0d ? tfEspesorEspecificado.getText() : "";
		String a = !tfAnchoEspecificado.getText().isEmpty() && !tfAnchoEspecificado.getText().startsWith(".") && Double.parseDouble(tfAnchoEspecificado.getText()) > 0d ? tfAnchoEspecificado.getText() : "";
		String l = !tfLargoEspecificado.getText().isEmpty() && !tfLargoEspecificado.getText().startsWith(".") && Double.parseDouble(tfLargoEspecificado.getText()) > 0d ? tfLargoEspecificado.getText() : "";
		String uni = !tfUnidadesEspecificado.getText().startsWith("0") && !tfUnidadesEspecificado.getText().startsWith(".") ? tfUnidadesEspecificado.getText() : "";
		
		btnAgregarProducto1.setDisable(false);
		
		if(!e.isEmpty() && !a.isEmpty() && !l.isEmpty() && !uni.isEmpty())
		{
			
			double espesor = Double.parseDouble(e);
			double ancho = Double.parseDouble(a);
			double largo = Double.parseDouble(l);
			int unidades = Integer.parseInt(uni);
			
			piesProperty1.set(UProducto.calcularPiesCuadrados(espesor, ancho, largo, unidades, UProducto.DOS_DECIMALES));
			
			productoEspecificado1 = new ProductoDTO();
			productoEspecificado1.setIdProducto(0);
			productoEspecificado1.setEspesor(espesor);
			productoEspecificado1.setAncho(ancho);
			productoEspecificado1.setLargo(largo);
			productoEspecificado1.setStock(unidades);
			productoEspecificado1.setPies();
			productoEspecificado1.setReserva(0);
			
			btnAgregarProducto1.setDisable(false);
		}
		else
		{
			btnAgregarProducto1.setDisable(true);
		}
		
	}
	
	private void calcularTotales()
	{	
		lblTotal.setText(UProducto.calcularTotalMaderas(tablaProductos.getItems()) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaProductos.getItems())));

		lblTotal2.setText(UProducto.calcularTotalMaderas(tablaProductosDisponibles.getItems()) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaProductosDisponibles.getItems())));
		
		lblTotal3.setText(UProducto.calcularTotalMaderas(tablaProductosEspecificados.getItems()) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaProductosEspecificados.getItems())));
	}
	
	/*private void comproparExistencia()
	{
		if(!tfEspesorBuscado.getText().isEmpty() && !tfAnchoBuscado.getText().isEmpty() && !tfLargoBuscado.getText().isEmpty() && !tfUnidadesBuscado.getText().isEmpty())
		{
			//lblDisponibilidad.setVisible(true);
			
			ProductoDTO producto = new ProductoDTO();
			producto.setEspesor(Double.parseDouble(tfEspesorBuscado.getText().trim()));
			producto.setAncho(Double.parseDouble(tfAnchoBuscado.getText().trim()));
			producto.setLargo(Double.parseDouble(tfLargoBuscado.getText().trim()));
			producto.setPies(Double.parseDouble(tfUnidadesBuscado.getText()));
			item = new ProductoDTO();
			
			ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia("PRODUCTO");
			
			int existencia = productoDao.calcularExistencia(producto);
			if(existencia == 1)
			{
				//lblDisponibilidad.setText("Stock disponible");
				//lblDisponibilidad.getStyleClass().clear();
				//lblDisponibilidad.getStyleClass().add("lbl-stock-disponible");
			}
			else if (existencia == 0)
			{
				//lblDisponibilidad.setText("Stock insuficiente");
				//lblDisponibilidad.getStyleClass().clear();
				//lblDisponibilidad.getStyleClass().add("lbl-stock-insuficiente");
			}
			else if (existencia == -1)
			{
				//lblDisponibilidad.setText("Sin stock");
				//lblDisponibilidad.getStyleClass().clear();
				//lblDisponibilidad.getStyleClass().add("lbl-sin-stock");
			}
			//UTransiciones.fadeIn(lblDisponibilidad, 250);
			
			// Se calcula exactamente la cantidad del producto especificado.
			productoDao.calcularReserva(item);
			
		}
		else
		{
			item = null;
			
			//lblDisponibilidad.setText("");
			//lblDisponibilidad.setVisible(false);
			
			lblEspesor.setText("");
			lblAncho.setText("");
			lblLargo.setText("");
			lblUnidades.setText("");
			lblPies.setText("");
		}
	}*/
	
	private void mostrarUnidades(ProductoDTO item)
	{
		if(item != null)
		{
			SpinnerValueFactory <Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, item.getStock(), 1);
			
			spUnidades.setValueFactory(valueFactory);
		}
	}
	
	private void formatearCeldas()
	{
		// añadimos boton quitar a la columna de productos
		Callback<TableColumn<ProductoDTO, Void>, TableCell<ProductoDTO, Void>> cellFactory = new Callback<TableColumn<ProductoDTO, Void>, TableCell<ProductoDTO, Void>>() 
		{
			@Override
			public TableCell<ProductoDTO, Void> call(final TableColumn<ProductoDTO, Void> param) 
			{
				return  new TableCell<ProductoDTO, Void>()
				{
					private final JFXButton btn = new JFXButton("Quitar");
					{
						Text icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.REMOVE, "20px");
						btn.setGraphic(icon);
						btn.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isEqualTo("Entregado")
								.or(pedidoSeleccionado.estadoPropiedad().isEqualTo("Listo")));
						btn.setOnAction((ActionEvent event) -> 
						{
							// Data data = getTableView().getItems().get(getIndex());
							ProductoDTO productoSeleccionado = getTableView().getItems().get(getIndex());
							
							if(quitarProducto(productoSeleccionado))
							{
								tablaProductos.getItems().remove(productoSeleccionado);

								calcularTotales();

								calcularProducto();
							}
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

		columnaQuitarProducto.setCellFactory(cellFactory);
		
			// añadimos boton quitar a la columna de productos especificados
				Callback<TableColumn<ProductoDTO, Void>, TableCell<ProductoDTO, Void>> cellFactory2 = new Callback<TableColumn<ProductoDTO, Void>, TableCell<ProductoDTO, Void>>() 
				{
					@Override
					public TableCell<ProductoDTO, Void> call(final TableColumn<ProductoDTO, Void> param) 
					{
						return  new TableCell<ProductoDTO, Void>()
						{
							private final JFXButton btn = new JFXButton("Quitar");
							{
								Text icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.REMOVE, "20px");
								btn.setGraphic(icon);
								btn.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isEqualTo("Entregado")
										.or(pedidoSeleccionado.estadoPropiedad().isEqualTo("Listo")));
								btn.setOnAction((ActionEvent event) -> 
								{
									// Data data = getTableView().getItems().get(getIndex());
									ProductoDTO productoSeleccionado = getTableView().getItems().get(getIndex());
									
									if(quitarProductoEspecificado(productoSeleccionado))
									{
										tablaProductosEspecificados.getItems().remove(productoSeleccionado);

									}
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

				columnaQuitarProducto1.setCellFactory(cellFactory2);
		
		// añadimos boton agregar a la columna de productos disponibles
		
		Callback<TableColumn<ProductoDTO, Void>, TableCell<ProductoDTO, Void>> cellFactory3 = new Callback<TableColumn<ProductoDTO, Void>, TableCell<ProductoDTO, Void>>() 
		{
			@Override
			public TableCell<ProductoDTO, Void> call(final TableColumn<ProductoDTO, Void> param) 
			{
				return  new TableCell<ProductoDTO, Void>()
				{
					private final JFXButton btn = new JFXButton("Agregar");
					{
						Text icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.PLUS, "20px");
						btn.setGraphic(icon);
						btn.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isEqualTo("Entregado")
								.or(pedidoSeleccionado.estadoPropiedad().isEqualTo("Listo")));
						btn.setOnAction((ActionEvent event) -> 
						{
							// Data data = getTableView().getItems().get(getIndex());
							ProductoDTO productoSeleccionado = getTableView().getItems().get(getIndex());
							
							System.out.println("Producto seleccionado --> " + productoSeleccionado);
							
							if(agregarProducto(productoSeleccionado))
							{
								tablaProductosDisponibles.getItems().remove(productoSeleccionado);

								calcularTotales();

								calcularProducto();
							}
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

		columnaAgregarProducto.setCellFactory(cellFactory3);
	}
	
	public void formatearCampos()
	{
		// formato de decimales con 2 cifras enteras y 2 decimales
		String formatoDobles = "\\d{0,2}([\\.]\\d{0,2})?";
		// formato de enteros maximo de 4 cifras, solo positivos
		String formatoEnteros = "\\d{0,4}?";
		
		final String FORMATONUMEROS = "\\d*";
		final String REPLACENUMEROS = "[^\\d]";
		
		tfEspesorBuscado.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(formatoDobles))
				{
					tfEspesorBuscado.setText(oldValue);
				}
				else calcularProducto();
			}				
		});
	
	tfAnchoBuscado.textProperty().addListener(new ChangeListener <String> ()
	{
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
		{
			if(!newValue.matches(formatoDobles))
			{
				tfAnchoBuscado.setText(oldValue);
			}
			else calcularProducto();
		}				
	});
	
	tfLargoBuscado.textProperty().addListener(new ChangeListener <String> ()
	{
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
		{
			if(!newValue.matches(formatoDobles))
			{
				tfLargoBuscado.setText(oldValue);
			}
			else calcularProducto();
		}				
	});
	
	tfUnidadesBuscado.textProperty().addListener(new ChangeListener <String> ()
	{
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
		{
			if(!newValue.matches(formatoEnteros))
			{
				tfUnidadesBuscado.setText(oldValue);
			}
			else calcularProducto();
		}				
	});
	
	}
	
	public void formatearCampos2()
	{
		// formato de decimales con 2 cifras enteras y 2 decimales
		String formatoDobles = "\\d{0,2}([\\.]\\d{0,2})?";
		// formato de enteros maximo de 4 cifras, solo positivos
		String formatoEnteros = "\\d{0,4}?";
		
		final String FORMATONUMEROS = "\\d*";
		final String REPLACENUMEROS = "[^\\d]";
		
		tfEspesorEspecificado.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(formatoDobles))
				{
					tfEspesorEspecificado.setText(oldValue);
				}
				else calcularProductoEspecificado();
			}				
		});
		
		tfAnchoEspecificado.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(formatoDobles))
				{
					tfAnchoEspecificado.setText(oldValue);
				}
				else calcularProductoEspecificado();
			}				
		});
		
		tfLargoEspecificado.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(formatoDobles))
				{
					tfLargoEspecificado.setText(oldValue);
				}
				else calcularProductoEspecificado();
			}				
		});
		
		tfUnidadesEspecificado.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(formatoEnteros))
				{
					tfUnidadesEspecificado.setText(oldValue);
				}
				else calcularProductoEspecificado();
			}				
		});
		
	}
	
	private void limpiarCampos()
	{
		tfEspesorBuscado.setText("");
		tfAnchoBuscado.setText("");
		tfLargoBuscado.setText("");
		tfUnidadesBuscado.setText("");
	
		tfEspesorBuscado.requestFocus();
	}
	
	private boolean agregarProducto(ProductoDTO productoAgregado)
	{
		boolean retorno = false;
		
		ObservableList<ProductoDTO> productosTabla = tablaProductos.getItems();
		
		try 
		{
			// comprobar si el producto es igual a uno quitado
			ProductoDTO prodcutoQuitado = UProducto.comprobrarExistenciaProducto(productoAgregado, listaProductosQuitados);
			if(prodcutoQuitado != null)
			{
				listaProductosQuitados.remove(prodcutoQuitado);
			}
			
			// - Comprobar si el producto agregado es igual a uno original
			ProductoDTO productoOriginal = UProducto.comprobrarExistenciaProducto(productoAgregado, productosOriginales);
			if(productoOriginal != null)
			{
				// comprobar si ya esta agregado el producto agregado a la lista de modificados
				ProductoDTO productoModificado = UProducto.comprobrarExistenciaProducto(productoAgregado, listaProductosModificados);
				if(productoModificado != null)
				{
					// actualizar los datos del producto modificado
					productoModificado.setStock(productoModificado.getStock() + productoAgregado.getStock());
					productoModificado.setPies();
					productoModificado.setReserva(productoModificado.getReserva() + productoModificado.getStock());
				}
				else
				{
					LOGGER.log(Level.DEBUG, "P AGREGADO RESERVA --> {}", productoAgregado.getReserva());
					
					
					// preguntamos si el producto agregado es de dinstina cantidad al original y si el original no esta en la tabla

					// creamos una copia del producto que vamos a agregar
					ProductoDTO productoAAgregar = UProducto.clonarProducto(productoOriginal);

					// preguntamos si el producto agregado ya esta en la tabla
					ProductoDTO productoTabla = UProducto.comprobrarExistenciaProducto(productoAgregado, productosTabla);
					if(productoTabla != null)
					{
						// actualizamos los atributos con los del producto agregado
						productoAAgregar.setStock(productoTabla.getStock() + productoAgregado.getStock());	
						productoAAgregar.setReserva(productoTabla.getReserva() + productoAgregado.getStock());
						productoAAgregar.setPies();
						
						// agregamos el producto a la lista
						listaProductosModificados.add(productoAAgregar);
					}
					else if(productoAgregado.getStock() != productoOriginal.getStock())
					{
						productoAAgregar.setStock(productoAgregado.getStock());
						productoAAgregar.setReserva(productoAAgregar.getReserva() + productoAgregado.getStock());
						productoAAgregar.setPies();
						
						// agregamos el producto a la lista
						listaProductosModificados.add(productoAAgregar);
					}		
				}
			}
			else
			{
				// si no es igual a uno original, agregar el producto a la lista de agregados
				
				// comprobamos si el producto existe en la lista de agregados
				ProductoDTO pAgregado = UProducto.comprobrarExistenciaProducto(productoAgregado, listaProductosAgregados);
				if(pAgregado == null)
				{
					// agregamos el produto a la lista
					listaProductosAgregados.add(productoAgregado);
				}
			}
			
			// comprobamos si el producto existe en la tabla
			ProductoDTO productoTabla = UProducto.comprobrarExistenciaProducto(productoAgregado, productosTabla);
			if(productoTabla != null)
			{		
				// actualizar los datos del producto de la tabla
				productoTabla.setStock(productoTabla.getStock() + productoAgregado.getStock());
				productoTabla.setPies();
				
			}
			else
			{
				// si no existe en la tabla

				// agregamos el producto agregado a la tabla de productosOriginales
				tablaProductos.getItems().add(productoAgregado);
			}
			
			retorno = true;
		} 
		catch (ConcurrentModificationException e) 
		{
			//e.printStackTrace();
			LOGGER.log(Level.DEBUG, "Ocurrio la excepcion: ConcurrentModificationException {}", e.getLocalizedMessage());
			
		}
		
		return retorno;
	}
	
	private boolean agregarProductoEspecificado(ProductoDTO productoEspecificado)
	{
		boolean retorno = false;
		
		ObservableList<ProductoDTO> productosEspecificados = tablaProductosEspecificados.getItems();
		
		ProductoDTO productoTabla = UProducto.comprobrarExistenciaProducto(productoEspecificado, productosEspecificados);
		
		// si el producto especificado ya existe en la tabla, actualizamos el de la tabla
		if(productoTabla != null)
		{		
			// actualizar los datos del producto de la tabla
			productoTabla.setStock(productoTabla.getStock() + productoEspecificado.getStock());
			productoTabla.setPies();
			
		}
		else
		{
			// si no existe en la tabla
			
			// agregamos el producto agregado a la tabla de productosOriginales
			tablaProductosEspecificados.getItems().add(productoEspecificado);
		}
		
		return retorno;
	}
	
	private boolean quitarProducto(ProductoDTO pQuitado)
	{
		System.out.println("ActionQuitarProducto()");
		boolean ret = false;

		// Comprobar si un producto quitado existe en la tabla de stock


		LOGGER.log(Level.DEBUG, "Producto quitado --> {}", pQuitado);

		// Se quita el producto de la lista de agregados, si que existe en la misma
		LOGGER.log(Level.DEBUG, "Se removio de la lista de agregados? --> {}", listaProductosAgregados.remove(pQuitado));

		// Se quita el producto de la lista de modificados, si es que existe en la misma
		LOGGER.log(Level.DEBUG, "Se removio de la lista de modificados? --> {}", listaProductosModificados.remove(pQuitado));

		// Se pregunta si el producto existe en la lista de productosOriginales originales
		ProductoDTO pOriginal = UProducto.clonarProducto(UProducto.comprobrarExistenciaProducto(pQuitado, productosOriginales));
		if(pOriginal != null)
		{
			// Si las unidades del producto original son mayores que el producto quitado
			// (el quitado fue agregado nuevamente, pero con menos o igual unidades)
			/*if(pOriginal.getStock() >= pQuitado.getStock())
				{
					listaProductosQuitados.add(pOriginal);
					System.out.println("\tSe agrego a la lista de productosOriginales quitados" + pOriginal);
				}
				// (El quitado fue agregado nuevamente, pero con mas unidades)
				else
				{
					listaProductosQuitados.add(pQuitado);
					System.out.println("\tSe agrego a la lista de productosOriginales quitados======" + pQuitado);
				}*/

			listaProductosQuitados.add(pOriginal);
			LOGGER.log(Level.DEBUG, "Se agrego el producto a la lista de quitados");

		}

		// comprobamos si el producto quitado existe en la tabla de productosOriginales disponibles
		ProductoDTO productoExistente = UProducto.comprobrarExistenciaProducto(pQuitado, tablaProductosDisponibles.getItems());

		if(productoExistente != null)
		{
			// SE actualizan las unidades y reserva del producto de la tabla stock;
			productoExistente.setStock(productoExistente.getStock() + pQuitado.getStock());
			productoExistente.setReserva(productoExistente.getReserva() - pQuitado.getStock());
			productoExistente.setPies();
			
			if(!tablaProductosDisponibles.getSelectionModel().isEmpty() && tablaProductosDisponibles.getSelectionModel().getSelectedItem().equals(productoExistente))
				mostrarUnidades(productoExistente);
			
			ret = true;
		}
		else
		{
			LOGGER.log(Level.DEBUG, "No existe.");

			// Si no existe en la tabla stock, se agrega el producto a la tabla
			pQuitado.setReserva(pQuitado.getReserva() - pQuitado.getStock());
			ret = tablaProductosDisponibles.getItems().add(pQuitado);
		}
		
		
		LOGGER.log(Level.DEBUG, "Lista originales size --> {}", productosOriginales.size());
		LOGGER.log(Level.DEBUG, "Lista agregados size --> {}", listaProductosAgregados.size());
		LOGGER.log(Level.DEBUG, "Lista modificados size --> {}", listaProductosModificados.size());
		LOGGER.log(Level.DEBUG, "Lista quitados size --> {}}", listaProductosQuitados.size());
		LOGGER.log(Level.DEBUG, "Lista Ret --> {}",ret);
		
		return ret;
	}
	
	private boolean quitarProductoEspecificado(ProductoDTO pQuitado)
	{
		System.out.println("Producto especificado quitado --> " + pQuitado); 
		
		
		
		return true;
	}
	
	// METODOS FXML
	
	@FXML private void actionNuevoCliente()
	{
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_NUEVO_CLIENTE);
	}
	
	// ** Tratar de reducir repeticion de codigo
	@FXML private void keyReleased(KeyEvent e)
	{
		if(e.getCode() == KeyCode.ENTER 
				&&!tfEspesorBuscado.getText().isEmpty() 
				&& !tfAnchoBuscado.getText().isEmpty()
				&& !tfLargoBuscado.getText().isEmpty() 
				&& !tfUnidadesBuscado.getText().isEmpty()
				&& !tfUnidadesBuscado.getText().startsWith("0"))
		{
			double espesor = Double.parseDouble(tfEspesorBuscado.getText());
			double ancho = Double.parseDouble(tfAnchoBuscado.getText());
			double largo = Double.parseDouble(tfLargoBuscado.getText());
			int stock = Integer.parseInt(tfUnidadesBuscado.getText());
			double pies = Double.parseDouble(lblTotalPies.getText());
			ProductoDTO productoAgregado = new ProductoDTO(0, espesor, ancho, largo, pies, stock, 0, 0);
					
			agregarProducto(productoAgregado);
		}
	}
		
	@FXML private void actionAgregarProducto()
	{
		if(agregarProducto(productoEspecificado))
		{
			LOGGER.log(Level.DEBUG, "Producto especificado stock --> {}}", productoEspecificado.getStock());
			LOGGER.log(Level.DEBUG, "Producto existente stock antes --> {}", productoExistente.getStock());
			
			productoExistente.setStock(productoExistente.getStock() - productoEspecificado.getStock());

			LOGGER.log(Level.DEBUG, "Producto existente stock despues --> {}", productoExistente.getStock());
			
			if(productoExistente.getStock() <= 0)
			{
				tablaProductosDisponibles.getItems().remove(productoExistente);
				tablaProductosDisponibles.getSelectionModel().clearSelection();
			}
			else if(tablaProductosDisponibles.getSelectionModel().getSelectedItem().equals(productoExistente))
				mostrarUnidades(productoExistente);

			calcularProducto();
			
			calcularTotales();
		}
	}
	
	@FXML private void actionAgregarProductoEspecificado()
	{
		if(agregarProductoEspecificado(productoEspecificado1))
		{
			System.out.println("Se agrego con exito");
		}
	}
	
	@FXML private void actionGuardarCambios()
	{
		System.out.println("actionGuardarCambios()");
		System.out.println("Lista agregados: " + listaProductosAgregados.size());
		System.out.println("Lista modificados: " + listaProductosModificados.size());
		System.out.println("Lista quitados: " + listaProductosQuitados.size());
		
		for(ProductoDTO p : productosOriginales)
		{
			System.out.println("POriginal: " + p );
		}
		
		// comprobamos si se realizaron modificaciones al pedido
		
		//ProductoEliminadoDTO pedidoSeleccionado = miCoordinador.getPedidosControlador().getPedidoSeleccionado();
		
		PedidoDTO pedidoAModificar = new PedidoDTO();
		pedidoAModificar.setIdPedido(miCoordinador.getPedidosControlador().getPedidoSeleccionado().getIdPedido());
		pedidoAModificar.setCliente(cmbClientes.getSelectionModel().getSelectedItem());
		pedidoAModificar.setFechaToma(miCoordinador.getPedidosControlador().getPedidoSeleccionado().getFechaToma());
		pedidoAModificar.setHoraToma(miCoordinador.getPedidosControlador().getPedidoSeleccionado().getHoraToma());	
		pedidoAModificar.setFechaEntrega(miCoordinador.getPedidosControlador().getPedidoSeleccionado().getFechaEntrega());
		pedidoAModificar.setHoraEntrega(miCoordinador.getPedidosControlador().getPedidoSeleccionado().getHoraEntrega());
		pedidoAModificar.setProposito("venta");
		pedidoAModificar.setForma(grupoRadioForma.getSelectedToggle().getUserData().toString());
		
		ObservableList<ProductoDTO> productosNuevos = tablaProductos.getItems();
		
		if(pedidoSeleccionado.getEstado().equals(UPedido.ENTREGADO))
			pedidoAModificar.setEstado(UPedido.ENTREGADO);
		else
			pedidoAModificar.setEstado(productosNuevos.isEmpty() ? UPedido.PENDIENTE : UPedido.EN_CURSO);
	
		System.out.println("Productos antes de agregar: " + productosOriginales.size());
		System.out.println("Memoria: " + productosOriginales);
		System.out.println("Tabla productosOriginales size: " + productosNuevos.size());
		System.out.println("Memoria: " + productosNuevos);
		
		// Se pregunta si se modifico al menos un dato del pedido o si la cantidad de productosOriginales de la tabla difiere de 
		// la cantidad de los productosOriginales originales
		if(!pedidoAModificar.equals(pedidoSeleccionado)
				|| productosOriginales.size() != productosNuevos.size())
		{
			System.out.println("Entro al primero!");
			pedidoModificado = true;
		}
		else
		{
			// ** FALTA ARREGALR ACAA!
			// Se recorre la tabla de productosOriginales y se compara con los productosOriginales originales
			// Se pregunta si se agrego un nuevo producto en la tabla o si alguno de los existentes fueron modficados
			for(ProductoDTO productoTabla : productosNuevos)
			{
				ProductoDTO pOriginal = UProducto.clonarProducto(UProducto.comprobrarExistenciaProducto(productoTabla, productosOriginales));
				if(pOriginal == null || pOriginal.getStock() != productoTabla.getStock())
				{
					System.out.println("ENTRO ACA  !! __");
					pedidoModificado = true;
					break;
				}
			}
		}
		
		if(pedidoModificado)
		{
			JFXButton btnAceptar = new JFXButton("Aceptar");
			JFXButton btnCancelar = new JFXButton("Cancelar");
			
			btnAceptar.setOnAction(e ->
			{
				Conexion.setAutoCommit(false);
				
				// Se actualiza la fecha de modificacion del pedido
				pedidoAModificar.setFechaModificacion(LocalDateTime.now());
				
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
				
				// actualizamos los datos del pedido en la bd
				pedidoModificado = pedidoDao.actualizar(pedidoAModificar);
				
				// comprobamos si hay productosOriginales agregados
				if(!listaProductosAgregados.isEmpty())
				{
					// instanciamos el detalle con los productosOriginales agregados
					DetallePedidoDTO detallePedidoAInsertar = new DetallePedidoDTO(pedidoAModificar, listaProductosAgregados);
					
					DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia("DETALLEP");
					
					// insertamos el detalle en la bd
					if(pedidoModificado && detallePedidoDao.insertar(detallePedidoAInsertar))
					{
						// preguntamos si la forma del pedido es por 'stock'
						if(pedidoSeleccionado.getForma().equalsIgnoreCase("Stock"))
						{
							ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia("PRODUCTO");
							
							// recorremos la lista de los productosOriginales agregados
							for(ProductoDTO p : listaProductosAgregados)
							{
								// consultamos y tomamos de la bd el producto en iteracion
								ProductoDTO productoAModificar = productoDao.obtenerPorId(p.getIdProducto());
								
								// seteamos la reserva del producto a modificar
								productoAModificar.setReserva(productoAModificar.getReserva() + p.getStock());
								
								// actualizamos el producto en la bd
								if(!productoDao.actualizar(productoAModificar))
								{
									pedidoModificado = false;
									break;
								}
							}	
						}
						else
						{
							ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
							
							System.out.println("ProductosAgregados---------------------------------------------|");
							
							// recorremos la lista de los productos agregados
							for(ProductoDTO productoAgregado : listaProductosAgregados)
							{
								// se obtienen las producciones del producto en iteracion
								ObservableList<ProduccionDTO> listaProduccion = produccionDao.obtenerProduccionProductoPedido(productoAgregado.getIdProducto(), -1);
								
								System.out.println("Lista produccion size ---> " + listaProduccion.size());
								
								// recorremos la lista de producciones
								for(int i = 0; i < productoAgregado.getStock(); i++)
								{
									System.out.println("i ----> " + i);
									ProduccionDTO produccion = listaProduccion.get(i);
									produccion.setIdPedido(pedidoSeleccionado.getIdPedido());
									if(!produccionDao.actualizar(produccion))
									{
										pedidoModificado = false;
										break;
									}
								}
							}
							
							System.out.println("------------------------------------------------------------------|");
						}
						
					}
				}				
				
				// preguntamos si existen productosOriginales modificados
				if(!listaProductosModificados.isEmpty() && pedidoModificado)
				{
					// Se actualizan todos los detalles de productosOriginales modificados
					
					// instanciamos el detalle a modificar
					DetallePedidoDTO detallePedidoAModificar = new DetallePedidoDTO(pedidoAModificar, listaProductosModificados);
					
					DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia("DETALLEP");
					
					// actualizamos el detalle en la bd
					if(pedidoModificado && detallePedidoDao.actualizar(detallePedidoAModificar)) 
					{
						// preguntamos si la forma del pedido es por 'stock'
						if(pedidoSeleccionado.getForma().equalsIgnoreCase("Stock"))
						{
							ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia("PRODUCTO");
							
							// recorremos la lista de productosOriginales modificados
							for(ProductoDTO p : listaProductosModificados)
							{
								// tomamos el producto original
								ProductoDTO productoOriginal = UProducto.comprobrarExistenciaProducto(p, productosOriginales);
								
								// consultamos y tomamos de la bd el producto en iteracion
								ProductoDTO productoAModificar = productoDao.obtenerPorId(p.getIdProducto());
								
								// preguntamos si la cantidad del producto a modificar es menor a la del original
								if(p.getStock() < productoOriginal.getStock())
								{
									// calculamos la reserva
									int reserva = productoOriginal.getStock() - p.getStock();
									
									// seteamos la reserva del producto a modificar
									productoAModificar.setReserva(productoAModificar.getReserva() - reserva);
								}
								else
								{
									// calculamos la reserva
									int reserva = p.getStock() - productoOriginal.getStock();
									
									// seteamos la reserva del producto a modificar
									productoAModificar.setReserva(productoAModificar.getReserva() + reserva);
								}
								
								System.out.println("Productoa modificar reserva: " + productoAModificar.getReserva());
								
								// actualizamos el producto en la bd
								if(!productoDao.actualizar(productoAModificar))
								{
									pedidoModificado = false;
									break;
								}
							}
						}
						else
						{
							ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
				
							
							// recorremos la lista de los productosOriginales que fueron modificados
							for(ProductoDTO productoModificado : listaProductosModificados)
							{			
								// tomamos el producto original
								ProductoDTO productoOriginal = UProducto.comprobrarExistenciaProducto(productoModificado, productosOriginales);
								
								// preguntamos si las unidades del producto modificado son menores a las del original
								if(productoModificado.getStock() < productoOriginal.getStock())
								{
									// actualziamos la producción de esos productosOriginales, seteando su idPedido a -1. Esto significa que la produccion esta
									// indefinida a que pertenece (si a un pedido o stock)
									
									// se obtienen las producciones de pedido del producto en iteracion
									ObservableList<ProduccionDTO> listaProduccion = produccionDao.obtenerProduccionProductoPedido(productoModificado.getIdProducto(), pedidoSeleccionado.getIdPedido());
									
									LOGGER.log(Level.INFO, "Lista produccion size ---> {}", listaProduccion.size());
									LOGGER.log(Level.INFO, "P modificado ---> {}", productoModificado);
									
									// recorremos la lista de producciones de manera inversa
									for(int i = 1; i <= productoOriginal.getStock() - productoModificado.getStock(); i++)
									{
										System.out.println("i ----> " + i);
										ProduccionDTO produccion = listaProduccion.get(listaProduccion.size() - i);
										produccion.setIdPedido(-1);
										if(!produccionDao.actualizar(produccion))
										{
											pedidoModificado = false;
											break;
										}
									}
								}
								else
								{
									// se obtienen las producciones de pedido indefinido del producto en iteracion
									ObservableList<ProduccionDTO> listaProduccion = produccionDao.obtenerProduccionProductoPedido(productoModificado.getIdProducto(), -1);
									
									LOGGER.log(Level.INFO, "Lista produccion indefinidos size ---> {}", listaProduccion.size());
									LOGGER.log(Level.INFO, "P modificado ---> {}", productoModificado);
									
									// recorremos la lista de producciones
									for(int i = 0; i < productoModificado.getStock() - productoOriginal.getStock(); i++)
									{
										System.out.println("i ----> " + i);
										ProduccionDTO produccion = listaProduccion.get(i);
										produccion.setIdPedido(pedidoSeleccionado.getIdPedido());
										if(!produccionDao.actualizar(produccion))
										{
											pedidoModificado = false;
											break;
										}
									}
								}
								
							}
							System.out.println("------------------------------------------------------------------|");
						}
					}
					
					System.out.println("Pedido modificado dentro de IF 3 - Despues de actualizar detalle" + pedidoModificado);
				}
				// preguntamos si hay productosOriginales quitados
				if(!listaProductosQuitados.isEmpty() && pedidoModificado)
				{
					// instanciamos el detalle a eliminar
					DetallePedidoDTO detallePedidoAEliminar = new DetallePedidoDTO(pedidoAModificar, listaProductosQuitados);
					
					DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
					
					// eliminamos el detalle de la bd
					pedidoModificado = pedidoModificado && detallePedidoDao.eliminar(detallePedidoAEliminar);
					if(pedidoModificado)
					{
						// preguntamos si la forma del pedido es por 'stock'
						if(pedidoSeleccionado.getForma().equalsIgnoreCase("Stock"))
						{
							// Se actualizan los productosOriginales eliminados del detalle recientemente
							ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);					// Se actualizan todos los productosOriginales que fueron modificados
							
							// recorremos la lista de productosOriginales quitados
							for(ProductoDTO pQuitado : listaProductosQuitados)
							{
								// consultamos y tomamos de la bd el producto en iteracion
								ProductoDTO productoAModificar = productoDao.obtenerPorId(pQuitado.getIdProducto());
								
								// seteamos la reserva del producto a modificar
								productoAModificar.setReserva(productoAModificar.getReserva() - pQuitado.getStock());
								
								// actualizamos el producto en la bd
								if(!productoDao.actualizar(productoAModificar))
								{
									pedidoModificado = false;
									break;
								}
							}
						}
						else
						{
							ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
								
							// recorremos la lista de los productosOriginales quitados
							for(ProductoDTO productoQuitado : listaProductosQuitados)
							{
								// se obtienen las producciones del producto en iteracion
								ObservableList<ProduccionDTO> listaProduccion = produccionDao.obtenerProduccionProductoPedido(productoQuitado.getIdProducto(), pedidoSeleccionado.getIdPedido());
							
								// actualziamos la producción de esos productosOriginales, seteando su idPedido a -1. Esto significa que la produccion esta
								// indefinida a que pertenece (si a un pedido o stock)
								
								// se recorre la lista de producciones de manera inversa hasta llegar a la cantidad de stock del producto iterado
								for(int i = 1; i <= productoQuitado.getStock(); i++)
								{
									ProduccionDTO produccion = listaProduccion.get(listaProduccion.size() - i);
									produccion.setIdPedido(-1);
									if(!produccionDao.actualizar(produccion))
									{
										pedidoModificado = false;
										break;
									}
								}
							}
							
							System.out.println("------------------------------------------------------------------|");

						}
					}
				}
				
				System.out.println("Pedido modificado final = " + pedidoModificado);
				
				if(pedidoModificado && Conexion.commit())
				{
					miCoordinador.getPedidosControlador().setPedidoSeleccionado(pedidoAModificar);
					miCoordinador.getPedidosControlador().actualizarTablaPedidos();
					
					productosOriginales.clear();
					productosOriginales.addAll(UProducto.clonarProductos(tablaProductos.getItems()));
					
					listaProductosAgregados.clear();
					listaProductosModificados.clear();
					listaProductosQuitados.clear();
					
					pedidoSeleccionado = pedidoAModificar;
					
					UNotificaciones.notificacion("Modificación de pedido", "¡Se han guardado los cambios!");
				}
				else UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Modificación de pedido", "No se ha podido modificar el pedido");
			});
			
			UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Modificación de pedido", "¿Desea confirmar los cambios del pedido?");
		}
	}	
	
	@FXML private void actionQuitarTodo()
	{
		if(!tablaProductos.getItems().isEmpty())
			tablaProductos.getItems().removeAll(tablaProductos.getItems());
	}
	
	@FXML private void actionAgregarUnidades()
	{
		// Clasificar los productosOriginales y sumar su largo total...!
		// Se toma el producto seleccionado de la tabla stock
		ProductoDTO productoSeleccionado = tablaProductosDisponibles.getSelectionModel().getSelectedItem();
			
		// Se crea un producto y se setea sus propiedades segun la cantidad seleccionada del detalle
		ProductoDTO productoAgregado = new ProductoDTO();
		productoAgregado.setIdProducto(productoSeleccionado.getIdProducto());
		productoAgregado.setEspesor(productoSeleccionado.getEspesor());
		productoAgregado.setAncho(productoSeleccionado.getAncho());
		productoAgregado.setLargo(productoSeleccionado.getLargo());
		productoAgregado.setStock(spUnidades.getValue());
		productoAgregado.setPies();
		
		// Si el producto es agregado con exito
		if(agregarProducto(productoAgregado))
		{
			System.out.println("P AGREGADOO STOCK : " + productoAgregado.getStock());
			
			// Se actualiza el producto seleccionado de la tabla stock
			productoSeleccionado.setStock(productoSeleccionado.getStock() - productoAgregado.getStock());
			productoSeleccionado.setReserva(productoSeleccionado.getReserva() + productoAgregado.getStock());
			productoSeleccionado.setPies();
			productoAgregado.setReserva(productoSeleccionado.getReserva());
			
			// Si el producto de la tabla stock ha quedado sin unidades
			if(productoSeleccionado.getStock() == 0)
			{
				// Se quita el producto seleccionado de la tabla stock
				tablaProductosDisponibles.getItems().remove(productoSeleccionado);
				tablaProductosDisponibles.getSelectionModel().clearSelection();
			}
			else
			{
				mostrarUnidades(productoSeleccionado);
				
				spUnidades.getValueFactory().setValue(productoAgregado.getStock() <= productoSeleccionado.getStock()
						? productoAgregado.getStock() : 1);
			}
			
			calcularProducto();
			
			calcularTotales();
		}
	}
	
	@FXML private void actionAgregarTodo()
	{
		ProductoDTO productoAgregado = tablaProductosDisponibles.getSelectionModel().getSelectedItem();
		
		if(agregarProducto(productoAgregado))
		{
			tablaProductosDisponibles.getItems().remove(productoAgregado);
		}
	}
	
	@FXML private void actionMostrarOriginales()
	{
		System.out.println("actionMostrarOriginales()");
		for(ProductoDTO p : productosOriginales)
		{
			System.out.println("\t " + p);
		}
		
		ProductoDTO p1 = new ProductoDTO();
		p1.setEspesor(5d);
		p1.setAncho(4d);
		p1.setLargo(1d);
		
		ProductoDTO p2 = new ProductoDTO();
		p2.setEspesor(5d);
		p2.setAncho(4d);
		p2.setLargo(1d);
		
		System.out.println("\tp1 es igual a p2: " + p1.equals(p2));
		
	}
	
	@FXML private void actionMostrarAgregados()
	{
		System.out.println("actionMostrarAgregados()");
		for(ProductoDTO p : listaProductosAgregados)
		{
			System.out.println("\t " + p);
		}
	}
	
	@FXML private void actionMostrarModificados()
	{
		System.out.println("actionMostrarModificados()");
		for(ProductoDTO p : listaProductosModificados)
		{
			System.out.println("\t " + p);
		}
	}
	
	@FXML private void actionMostrarQuitados()
	{
		System.out.println("actionMostrarQuitados()");
		for(ProductoDTO p : listaProductosQuitados)
		{
			System.out.println("\t " + p);
		}
	}
}
