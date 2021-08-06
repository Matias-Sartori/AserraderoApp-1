package aserradero.vista.nuevoPedido;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class NuevoPedidoControlador
{
	// Paneles
	@FXML private SplitPane panelCentro;
	@FXML private BorderPane splitDerechoPanelSur;
	@FXML private BorderPane panelDerecho;
	@FXML private BorderPane panelEspecificacion;
	@FXML private BorderPane panelCarga;
	@FXML private Node panelSeleccionarElemento;

	// Split izquierdo
	@FXML private JFXComboBox <ClienteDTO> cmbClientes;
	@FXML private JFXButton btnNuevoCliente;
	@FXML private JFXRadioButton radioStock;
	@FXML private JFXRadioButton radioDemanda;
	@FXML private JFXRadioButton radioProductosLibres;
	@FXML private JFXButton btnAgregarProducto;
	@FXML private JFXButton btnAgregarProductoEspecificado;
	@FXML private JFXButton btnConfirmar;
	@FXML private JFXButton btnQuitar;

	@FXML private HBox hboxDisponibilidad;
	@FXML private Label lblDisponibilidad;
	@FXML private Separator separadorDisponibilidad;
	@FXML private Label lblCantidadDisponible;
	
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
	@FXML private Label lblTotalPiesBuscado;
	@FXML private Label lblTotalPiesEspecificado;
	
	@FXML private Label lblTotal;
	@FXML private Label lblTotal2;	
	@FXML private Label lblTotal3;	

	// Split derecho

	@FXML private Label lblEncabezado;

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

	private ToggleGroup grupoRadio;
	
	private AserraderoApp miCoordinador;
	
	private static final Logger LOGGER = LogManager.getLogger(NuevoPedidoControlador.class);
	
	/*private ProductoDTO item;
	private DoubleProperty espesorProperty = new SimpleDoubleProperty();
	private DoubleProperty anchoProperty = new SimpleDoubleProperty();
	private DoubleProperty largoProperty = new SimpleDoubleProperty();
	
	private IntegerProperty unidadesProperty = new SimpleIntegerProperty();*/
	private DoubleProperty piesPropertyBuscado = new SimpleDoubleProperty();
	private DoubleProperty piesPropertyEspecificado = new SimpleDoubleProperty();
	
	private ProductoDTO productoBuscado = null;
	private ProductoDTO productoEspecificado = null;
	private ProductoDTO productoExistente = null;
	
	private static final Label PLACE_HOLDER_SIN_PRODUCTOS = new Label("Sin productos");
	private static final Label PLACE_HOLDER_SIN_STOCK = new Label("Sin stock");
	private static final Label PLACE_HOLDER_SIN_PRODUCTOS_INDEFINIDOS = new Label("Sin productos libres");
	private static final Label PLACE_HOLDER_SIN_CONEXION = new Label("Sin conexión");
	
	private static final Label PLACE_HOLDER_ESPECIFICAR = new Label("Se requiere especificar los productos a aserrar");
	private static final Label PLACE_HOLDER_CARGAR = new Label("Se requiere agregar los productos en stock disponibles");
	private static final Label PLACE_HOLDER_CARGAR_LIBRES = new Label("Se requiere agregar los productos libres disponibles");
	
	private FontAwesomeIconView iconoDisponible = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
	private FontAwesomeIconView iconoNoDisponible = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);

	private ObservableList<ProductoDTO> productosDisponibles = FXCollections.observableArrayList();
	private final JFXSpinner spinner = new JFXSpinner();
	private VBox vBoxSinConexion = new VBox();
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	private static final Label PLACE_HOLDER_SIN_RESULTADOS = new Label("Sin productos");
	
	public NuevoPedidoControlador() {}

	public void setCoordinador(AserraderoApp c)
	{
		this.miCoordinador = c;
	}

	@FXML public void initialize()
	{
		// Listen to the position property
        panelCentro.getDividers().get(0).positionProperty().addListener((obs, oldVal, newVal) -> {
            //if(mouseDragOnDivider)
                System.out.println("It's a mouse drag to pos: " + newVal.doubleValue());
        });
		
		PLACE_HOLDER_ESPECIFICAR.setId("lblIndicadorObligatorio");
		PLACE_HOLDER_CARGAR.setId("lblIndicadorObligatorio");
		PLACE_HOLDER_CARGAR_LIBRES.setId("lblIndicadorObligatorio");
		
		iconoSinConexion.setSize("40");
		
		linkReintentar.setOnAction(e ->
		{
			actualizarProductosDisponibles(grupoRadio.getSelectedToggle());
		});
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		panelSeleccionarElemento = (BorderPane) AserraderoApp.obtenerPanel(AserraderoApp.PANEL_SELECCIONAR_ELEMENTO_DETALLE2);
		btnAgregarProducto.setDisable(true);
		btnAgregarProductoEspecificado.setDisable(true);

		// Split izquierdo

		grupoRadio = new ToggleGroup();
		grupoRadio.selectedToggleProperty().addListener((obs, oldValue, newValue) -> 
		{
			actualizarProductosDisponibles(newValue);
		});
		
		radioStock.setToggleGroup(grupoRadio);
		radioDemanda.setToggleGroup(grupoRadio);
		radioProductosLibres.setToggleGroup(grupoRadio);
		
		radioStock.setUserData("Stock");
		radioDemanda.setUserData("A pedido");
		radioProductosLibres.setUserData("Productos libres");

		lblDisponibilidad.setContentDisplay(ContentDisplay.RIGHT);
		
		lblDisponibilidad.visibleProperty().bind(piesPropertyBuscado.greaterThan(0d));
		
		separadorDisponibilidad.setVisible(false);
		lblCantidadDisponible.setVisible(false);
		
		iconoDisponible.setStyleClass("-fx-text-fill: #37b966;");
		iconoNoDisponible.setStyleClass("-fx-text-fill: #37b966;");

		/*tfEspesorBuscado.textProperty().addListener((obs, oldValue, newValue) -> comproparExistenciaStock());
		tfAnchoBuscado.textProperty().addListener((obs, oldValue, newValue) -> comproparExistenciaStock());
		tfLargoBuscado.textProperty().addListener((obs, oldValue, newValue) -> comproparExistenciaStock());
		tfUnidadesBuscado.textProperty().addListener((obs, oldValue, newValue) -> comproparExistenciaStock());*/

		lblTotalPiesBuscado.textProperty().bind(piesPropertyBuscado.asString());
		lblTotalPiesEspecificado.textProperty().bind(piesPropertyEspecificado.asString());

		tablaProductos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue)->
		{
			LOGGER.log(Level.DEBUG, " Producto seleccionado --> {}", newValue);

		});

		// tabla productos cargados
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

		calcularTotales();
		
		separadorDisponibilidad.setVisible(false);
		lblCantidadDisponible.setVisible(false);
		
		// SPLIT DERECHO
		
		//lblEncabezado = new Label();
		
		// tabla productos disponibles
		tablaProductosDisponibles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tablaProductosDisponibles.getSelectionModel().selectedItemProperty().addListener((obs ,old, newValue) -> 
		{
			LOGGER.log(Level.DEBUG, "Producto disponible seleccionado --> {}", newValue);
			mostrarUnidades(newValue);
		});
		
		columnaEspesor2.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
		columnaAncho2.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
		columnaLargo2.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
		columnaUnidades2.setCellValueFactory(cellData -> cellData.getValue().stockPropiedad().asObject());
		columnaPies2.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject());

		btnAgregarUnidades.disableProperty().bind(tablaProductosDisponibles.getSelectionModel().selectedItemProperty().isNull());

		mostrarUnidades(null);

		formatearCampos();
		
		formatearCampos2();
		
		formatearCeldas();

		panelCentro.setDividerPositions(0.7424);

		grupoRadio.selectToggle(radioDemanda);
	}

	// METODOS

	public void actualizarComboClientes()
	{
		ClienteDAOMySQLImpl clienteDao = (ClienteDAOMySQLImpl) UFactoryDAO.getInstancia("CLIENTE");
		cmbClientes.setItems(clienteDao.obtenerActivos());
	}

	public void actualizarProductosDisponibles(Toggle toggle)
	{
		
		String formaSeleccionada = toggle.getUserData().toString();
		
		Task <Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				LOGGER.log(Level.DEBUG, "Toggle selected --> {}", toggle.getUserData());
				
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
					
					btnConfirmar.disableProperty().unbind();
					
					// desactivamos el boton de confirmacion si no se especifican los productos. Y el cliente siempre es obligatorio
					btnConfirmar.disableProperty().bind(Bindings.size(tablaProductosEspecificados.getItems()).isEqualTo(0).or(cmbClientes.getSelectionModel().selectedItemProperty().isNull()));
					
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
					
					btnConfirmar.disableProperty().unbind();
					
					// desactivamos el boton de confirmacion si no se especifican los productos. Y el cliente siempre es obligatorio
					btnConfirmar.disableProperty().bind(Bindings.size(tablaProductos.getItems()).isEqualTo(0).or(cmbClientes.getSelectionModel().selectedItemProperty().isNull()));
					
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

					btnConfirmar.disableProperty().unbind();
					// desactivamos el boton de confirmacion si no se especifican los productos. Y el cliente siempre es obligatorio
					btnConfirmar.disableProperty().bind(Bindings.size(tablaProductos.getItems()).isEqualTo(0).or(cmbClientes.getSelectionModel().selectedItemProperty().isNull()));
					
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
			lblEncabezado.setText(toggle.getUserData().toString().equalsIgnoreCase("A pedido") ? "Productos libres" : "Stock disponible");
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Alta de pedido", "Ha ocurrido un error. Compruebe la conexión del servidor");
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
				
				if(!formaSeleccionada.equalsIgnoreCase("A pedido"))
					tablaProductosDisponibles.setItems(productosDisponibles);
				else
					tablaProductosDisponibles.getItems().clear();
				
				tablaProductos.setPlaceholder(formaSeleccionada.equalsIgnoreCase("Stock") ? PLACE_HOLDER_CARGAR : PLACE_HOLDER_CARGAR_LIBRES);
				
				// ocultamos los place holders de las tablas segun la forma seleccionada
				tablaProductosEspecificados.getPlaceholder().setVisible(formaSeleccionada.equalsIgnoreCase("A pedido"));
				tablaProductos.getPlaceholder().setVisible(formaSeleccionada.equalsIgnoreCase("Stock") || formaSeleccionada.equalsIgnoreCase("Productos libres"));
				tablaProductosDisponibles.getPlaceholder().setVisible(!formaSeleccionada.equalsIgnoreCase("A pedido"));
				
				// Carga el ComboBox con los clientes existentes
				actualizarComboClientes();
			}
			else
			{
				tablaProductosDisponibles.setItems(null);
				tablaProductosDisponibles.setPlaceholder(vBoxSinConexion);
				cmbClientes.setItems(null);
				cmbClientes.setPlaceholder(new Label("No hay conexión con el servidor"));
			}
				
			
			calcularProducto();
			calcularTotales();
			lblEncabezado.setText(formaSeleccionada.equalsIgnoreCase("Productos libres") ? "Productos libres" : "Stock disponible");
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

		LOGGER.log(Level.DEBUG, "Espesor --> {}", e);
		LOGGER.log(Level.DEBUG, "Ancho --> {}", a);
		LOGGER.log(Level.DEBUG, "Largo --> {}", l);
		LOGGER.log(Level.DEBUG, "Unidades --{}> ", uni);

		if(!e.isEmpty() && !a.isEmpty() && !l.isEmpty() && !uni.isEmpty())
		{
			String forma = grupoRadio.getSelectedToggle().getUserData().toString();
			
			double espesor = Double.parseDouble(e);
			double ancho = Double.parseDouble(a);
			double largo = Double.parseDouble(l);
			int unidades = Integer.parseInt(uni);
			
			piesPropertyBuscado.set(UProducto.calcularPiesCuadrados(espesor, ancho, largo, unidades, UProducto.DOS_DECIMALES));

			lblDisponibilidad.getStyleClass().clear();

			productoBuscado = new ProductoDTO();
			productoBuscado.setIdProducto(0);
			productoBuscado.setEspesor(espesor);
			productoBuscado.setAncho(ancho);
			productoBuscado.setLargo(largo);
			productoBuscado.setStock(unidades);
			productoBuscado.setPies();
			productoBuscado.setReserva(0);

			LOGGER.log(Level.DEBUG, "Producto especificado final --> {}", productoBuscado);

			if(tablaProductosDisponibles.getItems() != null)
			{
				// comprobar existencia del producto en la tabla de productos disponibles
				productoExistente = UProducto.comprobrarExistenciaProducto(productoBuscado, tablaProductosDisponibles.getItems());
				if(productoExistente != null && lblDisponibilidad.isVisible())
				{
					LOGGER.log(Level.DEBUG, "PRODUCTO EXISTENTE ENCONTRADO --> {}", productoExistente);
					productoBuscado.setIdProducto(productoExistente.getIdProducto());

					if(productoBuscado.getStock() <= productoExistente.getStock())
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
				piesPropertyBuscado.set(0.0);
				separadorDisponibilidad.setVisible(false);
				lblCantidadDisponible.setVisible(false);
				btnAgregarProducto.setDisable(true);
			}
			
		}
		else 
		{
			piesPropertyBuscado.set(0.0);
			btnAgregarProducto.setDisable(true);
		}
	}
	
	private void calcularProductoEspecificado()
	{
		String e = !tfEspesorEspecificado.getText().isEmpty() && !tfEspesorEspecificado.getText().startsWith(".") && Double.parseDouble(tfEspesorEspecificado.getText()) > 0d ? tfEspesorEspecificado.getText() : "";
		String a = !tfAnchoEspecificado.getText().isEmpty() && !tfAnchoEspecificado.getText().startsWith(".") && Double.parseDouble(tfAnchoEspecificado.getText()) > 0d ? tfAnchoEspecificado.getText() : "";
		String l = !tfLargoEspecificado.getText().isEmpty() && !tfLargoEspecificado.getText().startsWith(".") && Double.parseDouble(tfLargoEspecificado.getText()) > 0d ? tfLargoEspecificado.getText() : "";
		String uni = !tfUnidadesEspecificado.getText().startsWith("0") && !tfUnidadesEspecificado.getText().startsWith(".") ? tfUnidadesEspecificado.getText() : "";
		
		btnAgregarProductoEspecificado.setDisable(false);
		
		if(!e.isEmpty() && !a.isEmpty() && !l.isEmpty() && !uni.isEmpty())
		{
			
			double espesor = Double.parseDouble(e);
			double ancho = Double.parseDouble(a);
			double largo = Double.parseDouble(l);
			int unidades = Integer.parseInt(uni);
			
			piesPropertyEspecificado.set(UProducto.calcularPiesCuadrados(espesor, ancho, largo, unidades, UProducto.DOS_DECIMALES));
			
			productoEspecificado = new ProductoDTO();
			productoEspecificado.setEspesor(espesor);
			productoEspecificado.setAncho(ancho);
			productoEspecificado.setLargo(largo);
			productoEspecificado.setStock(unidades);
			productoEspecificado.setPies();
			productoEspecificado.setReserva(0);
			
			System.out.println("Producto especificado --> " + productoEspecificado)	;
			
			btnAgregarProductoEspecificado.setDisable(false);
		}
		else
		{
			btnAgregarProductoEspecificado.setDisable(true);
			piesPropertyEspecificado.set(0.0);
		}
		
	}

	private void mostrarUnidades(ProductoDTO item)
	{
		if(item != null)
		{
			SpinnerValueFactory <Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, item.getStock());

			spUnidades.setValueFactory(valueFactory);
		}
	}
	
	private void formatearCeldas()
	{
		// añadimos boton de quitar a la columna de la tabla de productos en carga
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
		
		// añadimos boton de quitar a la columna de la tabla de productos especificados
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
						
						btn.setOnAction((ActionEvent event) -> 
						{
							// Data data = getTableView().getItems().get(getIndex());
							ProductoDTO productoSeleccionado = getTableView().getItems().get(getIndex());
							
							if(quitarProductoEspecificado(productoSeleccionado))
							{
								tablaProductos.getItems().remove(productoSeleccionado);
								
								calcularTotales();
								
								calcularProductoEspecificado();
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
	
	private void calcularTotales()
	{	
		lblTotal.setText(UProducto.calcularTotalMaderas(tablaProductos.getItems()) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaProductos.getItems())));

		lblTotal2.setText(UProducto.calcularTotalMaderas(tablaProductosDisponibles.getItems()) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaProductosDisponibles.getItems())));
		
		lblTotal3.setText(UProducto.calcularTotalMaderas(tablaProductosEspecificados.getItems()) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaProductosEspecificados.getItems())));
	}

	private ObservableList<ProductoDTO> mostrarUnidadesIndividual(ProductoDTO item)
	{
		ObservableList<ProductoDTO> productos = FXCollections.observableArrayList();
		for(int i = 0; i < item.getStock(); i++)
		{
			ProductoDTO p = new ProductoDTO();
			p.setIdProducto(item.getIdProducto());
			p.setEspesor(item.getEspesor());
			p.setAncho(item.getAncho());
			p.setLargo(item.getLargo());
			p.setStock(1);
			p.setPies();
			p.setReserva(0);

			productos.add(p);
		}

		return productos;
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

	private void limpiarMedidas()
	{
		tfEspesorBuscado.clear();
		tfAnchoBuscado.clear();
		tfLargoBuscado.clear();
		tfUnidadesBuscado.clear();

		tfEspesorBuscado.requestFocus();
	}

	private void limpiarTodosLosCampos() 
	{
		cmbClientes.getSelectionModel().clearSelection();
		tablaProductos.getItems().clear();
		actualizarProductosDisponibles(grupoRadio.getSelectedToggle());

		limpiarMedidas();
	}

	private boolean agregarProducto(ProductoDTO productoAgregado)
	{
		boolean agregado = false;
		try 
		{		
			LOGGER.log(Level.DEBUG, "Producto agregado --> {}" + productoAgregado);
			LOGGER.log(Level.DEBUG, "Producto existente --> {}" + productoExistente);
			
			// tomamos los productos de la tabla
			ObservableList<ProductoDTO> productosTabla = tablaProductos.getItems();
			
			LOGGER.log(Level.DEBUG, "Productos size --> {}" + productosTabla.size());
			
			// si la tabla no esta vacia
			if(!productosTabla.isEmpty())
			{
				// comprobamos si el producto agregado existe en la tabla
				ProductoDTO productoEncontrado = UProducto.comprobrarExistenciaProducto(productoAgregado, productosTabla);
				
				// existe el producto?
				if(productoEncontrado != null)
				{
					// sumamos el stock y actualizamos los pies del producto encontrado
					productoEncontrado.setStock(productoEncontrado.getStock() + productoAgregado.getStock());
					productoEncontrado.setPies();
				}
				// sino, agregamos el producto a la tabla
				else tablaProductos.getItems().add(productoAgregado);
			}
			// Si esta vacia, agregamos el producto
			else tablaProductos.getItems().add(productoAgregado);

			// preguntamos si la forma del pedido es de 'stock'
			if(grupoRadio.getSelectedToggle().getUserData().toString().equalsIgnoreCase("Stock"))
			{
				// preguntamos si el producto agregado existe en la tabla de productos disponibles (stock)
				productoExistente = UProducto.comprobrarExistenciaProducto(productoAgregado, tablaProductosDisponibles.getItems());
				
				// actualizamos la reserva del producto del stock y del producto agregado
				productoExistente.setReserva(productoExistente.getReserva() + productoAgregado.getStock());
				productoAgregado.setReserva(productoExistente.getReserva());
				
				agregado = true;
			}
			else agregado = true;
		} 
		catch (ConcurrentModificationException e) 
		{
			// TODO Auto-generated catch block
			
			LOGGER.log(Level.ERROR, "Ocurrio la excepcion: ConcurrentModificationException");
		}

		LOGGER.log(Level.DEBUG, "Producto existente final --> {}", productoExistente);
		
		return agregado;
	}
	
	private boolean agregarProductoEspecificado(ProductoDTO productoEspecificado)
	{
		boolean agregado = false;
		try 
		{		
			LOGGER.log(Level.DEBUG, "Producto agregado --> {}" + productoEspecificado);
			LOGGER.log(Level.DEBUG, "Producto existente --> {}" + productoExistente);
			
			// tomamos los productos de la tabla
			ObservableList<ProductoDTO> productosEspecificados = tablaProductosEspecificados.getItems();
			
			LOGGER.log(Level.DEBUG, "Productos size --> {}" + productosEspecificados.size());
			
			// si la tabla no esta vacia
			if(!productosEspecificados.isEmpty())
			{
				// comprobamos si el producto agregado existe en la tabla
				ProductoDTO productoEncontrado = UProducto.comprobrarExistenciaProducto(productoEspecificado, productosEspecificados);
				
				// existe el producto?
				if(productoEncontrado != null)
				{
					// sumamos el stock y actualizamos los pies del producto encontrado
					productoEncontrado.setStock(productoEncontrado.getStock() + productoEspecificado.getStock());
					productoEncontrado.setPies();
				}
				// sino, agregamos el producto a la tabla
				else tablaProductosEspecificados.getItems().add(productoEspecificado);
			}
			// Si esta vacia, agregamos el producto
			else tablaProductosEspecificados.getItems().add(productoEspecificado);

			// preguntamos si la forma del pedido es de 'stock'
			/*if(grupoRadio.getSelectedToggle().getUserData().toString().equalsIgnoreCase("Stock"))
			{
				// preguntamos si el producto agregado existe en la tabla de productos disponibles (stock)
				productoExistente = UProducto.comprobrarExistenciaProducto(productoBuscado, tablaProductosDisponibles.getItems());
				
				// actualizamos la reserva del producto del stock y del producto agregado
				//productoExistente.setReserva(productoExistente.getReserva() + productoBuscado.getStock());
				//productoAgregado.setReserva(productoExistente.getReserva());
				
				agregado = true;
			}
			else agregado = true;*/
			
			agregado = true;
		} 
		catch (ConcurrentModificationException e) 
		{
			// TODO Auto-generated catch block
			
			LOGGER.log(Level.ERROR, "Ocurrio la excepcion: ConcurrentModificationException");
		}

		LOGGER.log(Level.DEBUG, "Producto existente final --> {}", productoExistente);
		
		return agregado;
	}

	private boolean quitarProducto(ProductoDTO pQuitado)
	{
		boolean quitado = false;
		
		String formaSeleccionada = grupoRadio.getSelectedToggle().getUserData().toString();
		
		ObservableList<ProductoDTO> productosDisponibles = tablaProductosDisponibles.getItems();
		
		if(!productosDisponibles.isEmpty())
		{
			// Comprobar si el producto quitado existe en la tabla de productos disponibles

			ProductoDTO pExistente = UProducto.comprobrarExistenciaProducto(pQuitado, productosDisponibles);
			if(pExistente != null)
			{
				if(formaSeleccionada.equalsIgnoreCase("Stock"))
				{
					pExistente.setStock(pExistente.getStock() + pQuitado.getStock());
					pExistente.setReserva(pExistente.getReserva() - pQuitado.getStock());
					pExistente.setPies();
				}
				else 
				{
					pExistente.setStock(pExistente.getStock() + pQuitado.getStock());
					pExistente.setPies();
				}
				
				if(tablaProductosDisponibles.getSelectionModel().getSelectedItem().equals(pExistente))
					mostrarUnidades(pExistente);
					
			}
			else 
			{
				if(formaSeleccionada.equalsIgnoreCase("Stock"))
				{
					pQuitado.setReserva(pQuitado.getReserva() - pQuitado.getStock());
				}
				tablaProductosDisponibles.getItems().add(pQuitado);
			}
		}
		else
		{
			if(formaSeleccionada.equalsIgnoreCase("Stock"))
			{
				pQuitado.setReserva(pQuitado.getReserva() - pQuitado.getStock());			
			}
			tablaProductosDisponibles.getItems().add(pQuitado);
		}

		quitado = true;

		return quitado;
	}
	
	private boolean quitarProductoEspecificado(ProductoDTO pQuitado)
	{
		
		return tablaProductosEspecificados.getItems().remove(pQuitado);
	}

	// METODOS FXML

	@FXML private void actionNuevoCliente()
	{
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_NUEVO_CLIENTE);
	}

	// ** Tratar de reducir repeticion de codigo
	@FXML private void keyReleased(KeyEvent e)
	{
		if(e.getCode() == KeyCode.ENTER && !btnAgregarProducto.isDisabled() && productoBuscado != null)
		{
			actionAgregarProducto();
		}
	}

	@FXML private void actionAgregarProducto()
	{
		if(agregarProducto(productoBuscado))
		{
			LOGGER.log(Level.DEBUG, "Producto especificado stock --> {}", productoBuscado.getStock());
			LOGGER.log(Level.DEBUG, "Producto existente stock --> {}", productoExistente.getStock());
			
			productoExistente.setStock(productoExistente.getStock() - productoBuscado.getStock());
			
			LOGGER.log(Level.DEBUG, "Producto existente stock despues --> {}", productoExistente.getStock());
			
			if(productoExistente.getStock() <= 0)
			{
				tablaProductosDisponibles.getItems().remove(productoExistente);
			}
			else if(!tablaProductosDisponibles.getSelectionModel().isEmpty() && tablaProductosDisponibles.getSelectionModel().getSelectedItem().equals(productoExistente))
				mostrarUnidades(productoExistente);
					

			calcularProducto();
			
			calcularTotales();
		}
	}
	
	@FXML private void actionAgregarProductoEspecificado()
	{
		System.out.println("ACTION AGREGAR PRODUCTOS ESPECIFICADO");
		
		if(agregarProductoEspecificado(productoEspecificado))
		{
			System.out.println("Se agrego con exito");
			
			calcularProductoEspecificado();
			
			calcularTotales();
		}
	}
	

	@FXML private void actionConfirmarPedido()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		btnAceptar.setOnAction(e ->
		{
			boolean autoCommit = Conexion.setAutoCommit(false);
			
			// bandera indicadora si las transacciones se hicieron correctamente
			boolean pedidoInsertado = true;
			
			// tomamos la forma seleccionada 
			String formaSeleccionada = grupoRadio.getSelectedToggle().getUserData().toString();
			
			// instanciamos el pedido con los datos obtenidos
			PedidoDTO nuevoPedido = new PedidoDTO();
			nuevoPedido.setCliente(cmbClientes.getSelectionModel().getSelectedItem());
			nuevoPedido.setFechaToma(LocalDate.now());
			nuevoPedido.setHoraToma(LocalTime.now());
			nuevoPedido.setFechaEntrega(LocalDate.of(9999, 9, 9));
			nuevoPedido.setHoraEntrega(LocalTime.of(00, 00, 00));
			nuevoPedido.setProposito("venta");
			nuevoPedido.setForma(formaSeleccionada);
			nuevoPedido.setFechaModificacion(LocalDateTime.of(9999, 9, 9, 00, 00, 00));
			
			// preguntamos la forma seleccionada
			
			if(formaSeleccionada.equalsIgnoreCase("A pedido"))
			{
				// tomamos los productos especificados
				ObservableList <ProductoDTO> producosEspecificados = tablaProductosEspecificados.getItems();
				
				// seteamos el estado del pedido a "pendiente"
				nuevoPedido.setEstado(UPedido.PENDIENTE);
				
				System.out.println("Pedido estado: " + nuevoPedido.getEstado());
				
				// primero verificamos los productos especificados
				for(ProductoDTO p : producosEspecificados)
				{
					ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia("PRODUCTO");
					
					ProductoDTO productoExistente = productoDao.obtener(p);
					if(productoExistente != null && productoExistente.getIdProducto() == 0)
					{
						// dar de alta al producto especificado, creamos un producto auxiliar y seteamos su stock a 0
						ProductoDTO productoAInsertar = UProducto.clonarProducto(p);
						productoAInsertar.setStock(0);
						// insertamos el producto auxiliar
						pedidoInsertado = pedidoInsertado && productoDao.insertar(productoAInsertar);
						// si el producto auxiliar fue insertado con exito, seteamos el nuevo id creado al producto especificado original
						p.setIdProducto(pedidoInsertado ? productoAInsertar.getIdProducto() : 0);
					}
					else if(productoExistente != null && productoExistente.getIdProducto() != 0)
					{
						// si el producto ya existe en la bd, lo seteamos con el id existente
						p.setIdProducto(productoExistente.getIdProducto());
					}
				}
				
				// instanciamos el detalle de especificacion
				EspecificacionPedidoDTO especificacionDto = new EspecificacionPedidoDTO(nuevoPedido, producosEspecificados);
				
				EspecificacionPedidoDAOMySQLImpl especificacionPedidoDao = (EspecificacionPedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.ESPECIFICACIONP);
				
				// insertamos el pedido primero y si tiene exito insertamos tambien el detalle
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia("PEDIDO");
				pedidoInsertado = pedidoInsertado && pedidoDao.insertar(nuevoPedido) && especificacionPedidoDao.insertar(especificacionDto);
				
				System.out.println("Forma productos libres BANDERA FINAL -> " + pedidoInsertado);
						
			}
			else if(formaSeleccionada.equalsIgnoreCase("Stock"))
			{
				pedidoInsertado = true;
				
				// seteamos el estado del pedido a "en curso"
				nuevoPedido.setEstado(UPedido.EN_CURSO);
				
				// tomamos los productos de stock agregados
				ObservableList <ProductoDTO> productosAgregados = tablaProductos.getItems();
				
				// creamos el detalle con los productos agregados
				DetallePedidoDTO detallePedido = new DetallePedidoDTO(nuevoPedido, UProducto.clonarProductos(productosAgregados));
				
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia("PRODUCTO");
				
				// Actualizamos la reserva de los productos, primeramente
				for(ProductoDTO p : productosAgregados)
				{
					// tomamos el producto a modificar de la base de datos
					ProductoDTO productoAModificar = productoDao.obtenerPorId(p.getIdProducto());
					productoAModificar.setReserva(productoAModificar.getReserva() + p.getStock());
					if(!productoDao.actualizar(productoAModificar))
					{
						pedidoInsertado = false;
						break;
					}
				}

				
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia("PEDIDO");
				
				DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
				
				// si el detalle fue insertado con exito, insertamos el pedido y el detalle
				pedidoInsertado = pedidoInsertado && pedidoDao.insertar(nuevoPedido) && detallePedidoDao.insertar(detallePedido);
				
				System.out.println("Forma productos libres BANDERA FINAL -> " + pedidoInsertado);
			}
			else
			{
				pedidoInsertado = true;
				
				// seteamos el estado del pedido
				nuevoPedido.setEstado(UPedido.EN_CURSO);
				
				// tomamos los productos libres agregados
				ObservableList <ProductoDTO> productosAgregados = tablaProductos.getItems();
				
				// instanciamos el detalle
				DetallePedidoDTO detallePedidoDto = new DetallePedidoDTO(nuevoPedido, productosAgregados);
				
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia("PEDIDO");
				
				DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
				
				// si las producciones fueron actualizadas con exito, insertamos el pedido y el detalle
				pedidoInsertado = pedidoInsertado && pedidoDao.insertar(nuevoPedido) && detallePedidoDao.insertar(detallePedidoDto);
				
				ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
				
				// recorremos los productos agregados
				for(ProductoDTO p : detallePedidoDto.getProductos())
				{
					// se obtienen las producciones de productos libres del producto en iteracion
					ObservableList<ProduccionDTO> listaProduccion = produccionDao.obtenerProduccionProductoPedido(p.getIdProducto(), -1);
					
					// se recorre la lista de producciones hasta llegar a la cantidad de stock del producto iterado
					for(int i = 0; i < p.getStock(); i++)
					{
						ProduccionDTO produccion = listaProduccion.get(i);
						produccion.setIdPedido(nuevoPedido.getIdPedido());
						if(!produccionDao.actualizar(produccion))
						{
							pedidoInsertado = false;
							break;
						}
					}
				}
				
				System.out.println("Forma productos libres BANDERA FINAL -> " + pedidoInsertado);
			}
			
			// salida del if de pregunta de forma
			if(pedidoInsertado && Conexion.commit())
			{
				UNotificaciones.notificacion("Alta de pedido", "Se ha dado de alta el pedido.");
				limpiarTodosLosCampos();
				miCoordinador.getPedidosControlador().actualizarTablaPedidos();
			}
			else UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Alta de pedido", "No se ha podido dar de alta al pedido");

		});
		
		UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Alta de pedido", "¿Desea confirmar el alta del pedido?");
	
	}

	@FXML private void actionAgregarUnidades()
	{
		// Clasificar los productos y sumar su largo total...!
		ProductoDTO productoSeleccionado = tablaProductosDisponibles.getSelectionModel().getSelectedItem();

		ProductoDTO productoAgregado = UProducto.clonarProducto(productoSeleccionado);
		productoAgregado.setStock(spUnidades.getValue());
		productoAgregado.setPies();

		if(agregarProducto(productoAgregado))
		{
			productoSeleccionado.setStock(productoSeleccionado.getStock() - productoAgregado.getStock());
			productoSeleccionado.setPies();
			
			// si el producto seleccionado se queda sin unidades
			if(productoSeleccionado.getStock() < 1)
			{
				// removemos el producto seleccionado de la tabla
				tablaProductosDisponibles.getItems().remove(productoSeleccionado);
				
				// limpiamos la seleccion de la tabla
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
		ProductoDTO productoSeleccionado = tablaProductosDisponibles.getSelectionModel().getSelectedItem();

		ProductoDTO productoAgregado = UProducto.clonarProducto(productoSeleccionado);
		
		if(agregarProducto(productoAgregado))
		{
			tablaProductosDisponibles.getItems().remove(tablaProductosDisponibles.getSelectionModel().getSelectedItem());
			
			calcularProducto();
		}
	}
}
