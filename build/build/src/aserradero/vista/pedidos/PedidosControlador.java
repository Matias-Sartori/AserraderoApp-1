package aserradero.vista.pedidos;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXToggleButton;

import aserradero.AserraderoMain;
import aserradero.controlador.AserraderoApp;
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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class PedidosControlador 
{
	@FXML private Hyperlink linkRefrescar;
	
	@FXML private TableView <PedidoDTO> tablaPedidos;
	@FXML private TableView <ProductoDTO> tablaDetalle;
	
	@FXML private GridPane gridDetalle;
	
	@FXML private TableColumn <PedidoDTO, Integer> columnaNumeroPedido;
	@FXML private TableColumn <PedidoDTO, String> columnaCliente;
	@FXML private TableColumn <PedidoDTO, LocalDate> columnaFechaToma;
	@FXML private TableColumn <PedidoDTO, LocalDate> columnaFechaEntrega;
	@FXML private TableColumn <PedidoDTO, String> columnaEstado;
	@FXML private TableColumn <PedidoDTO, String> columnaForma;
	
	@FXML private TableColumn <ProductoDTO, String> columnaEspesor;
	@FXML private TableColumn <ProductoDTO, String> columnaAncho;
	@FXML private TableColumn <ProductoDTO, Double> columnaLargo;
	@FXML private TableColumn <ProductoDTO, Integer> columnaUnidades;
	@FXML private TableColumn <ProductoDTO, Double> columnaPies;
	
	@FXML private Label lblDetalle;
	
	// labels datosReporte cliente
	@FXML private Label lblCliente;
	@FXML private Label lblTelefono;
	@FXML private Label lblDni;
	@FXML private Label lblCuit;
	@FXML private Label lblRazonSocial;
	@FXML private Label lblDireccion;
	@FXML private Label lblLocalidad;
	
	@FXML private ToggleSwitch tsListo;
	
	// labels datosReporte pedido
	@FXML private Label lblNumPedido;
	@FXML private Label lblFechaT;
	@FXML private Label lblEstado;
	@FXML private Label lblFechaE;
	@FXML private Label lblForma;
	@FXML private Label lblFechaModificacion;
	
	@FXML private JFXToggleButton toggleClasificar;
	
	@FXML private Label lblTotal;
	
	@FXML private TextField filtroCliente;
	
	@FXML private JFXCheckBox checkEntregados;
	@FXML private JFXCheckBox checkEnCurso;
	@FXML private JFXCheckBox checkListos;
	@FXML private JFXCheckBox checkPendientes;
	
	@FXML private JFXButton btnInformes;
	@FXML private JFXButton btnNuevoPedido;
	@FXML private JFXButton btnReporteOrdenAserrado;
	@FXML private JFXButton btnReporteOrdenArmado;
	@FXML private JFXButton btnReporteDetallePedido;
	@FXML private JFXButton btnCancelarPedido;
	@FXML private JFXButton btnConfEntrega;
	@FXML private JFXButton btnModificarPedido;
	
	@FXML private SplitPane panelRaiz;
	@FXML private BorderPane panelDerecho;

	private static final Logger LOGGER = LogManager.getLogger(PedidosControlador.class);
	
	private BorderPane ventanaSeleccionarElemento;
	
	private JFXSpinner spinner = new JFXSpinner();
	
	private VBox vBoxSinConexion = new VBox();
	
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	
	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	
	private AserraderoApp miCoordinador;
	
	private PedidoDTO pedidoSeleccionado;
	
	private DetallePedidoDTO detallePedidoSeleccionado;
	
	private EspecificacionPedidoDTO especificacionPedidoSeleccionado;
	
	private static final Label PLACE_HOLDER_SIN_RESULTADOS = new Label("Sin resultados");
	
	private static final Label PLACE_HOLDER_SIN_PEDIDOS = new Label("No hay ventas registradas");
	
	private static final Label PLACE_HOLDER_SIN_PRODUCTOS = new Label("No hay productos cargados");
	
	private static final double DIVIDER_POSITION_DEFECTO = 0.4658203125;
	
	private ObservableList<PedidoDTO> listaPedidos = FXCollections.observableArrayList();
	
	Map<String, String> datosReporte = new HashMap<>();
	
	// Constantes
	
	// Constructores
	
	public PedidosControlador() 
	{
		// Por el momento, el metodo no contiene nada
	}
	
	// Setters and Getters
	
	public void setCoordinador(AserraderoApp c)
	{
		this.miCoordinador = c;
	}
	
	public void setPedidoSeleccionado(PedidoDTO p)
	{
		this.pedidoSeleccionado = p;
	}
	
	public PedidoDTO getPedidoSeleccionado()
	{
		return this.pedidoSeleccionado;
	}
	
	@FXML public void initialize()
	{
		iconoSinConexion.setSize("40");
		
		linkReintentar.setOnAction(e ->
		{
			actualizarTablaPedidos();
		});
		
		toggleClasificar.setSelected(true);
		
		toggleClasificar.selectedProperty().addListener((obs, old, newValue) -> 
			actualizarTablaDetalle(pedidoSeleccionado.getIdPedido()));

		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		checkEntregados.setUserData("Entregado");
		checkEnCurso.setUserData("En curso");
		checkListos.setUserData("Listo");
		checkPendientes.setUserData("Pendiente");
		
		ventanaSeleccionarElemento = (BorderPane) AserraderoApp.obtenerPanel(AserraderoApp.PANEL_SELECCIONAR_ELEMENTO_DETALLE);
		
	    tablaPedidos.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) ->
	    {
			mostrarDetallePedido(newValue);
		
		});
	    
	    //panelDerecho.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(3))));
	    
	    columnaNumeroPedido.setCellValueFactory(cellData -> cellData.getValue().idPedidoPropiedad().asObject());
	    columnaCliente.setCellValueFactory(cellData -> cellData.getValue().clientePropiedad().asString());
		columnaFechaToma.setCellValueFactory(cellData -> cellData.getValue().fechaTomaPropiedad());
		columnaFechaEntrega.setCellValueFactory(cellData -> cellData.getValue().fechaEntregaPropiedad());
		columnaEstado.setCellValueFactory(cellData -> cellData.getValue().estadoPropiedad());
		columnaForma.setCellValueFactory(cellData -> cellData.getValue().formaPropiedad());
	    
		tablaPedidos.setEditable(false);
		
		columnaEspesor.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
		columnaAncho.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
		columnaLargo.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
		columnaUnidades.setCellValueFactory(cellData -> cellData.getValue().stockPropiedad().asObject());
		columnaPies.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject());
		
		tsListo.selectedProperty().addListener((obs, old, newValue) -> 
		{
			LOGGER.log(Level.DEBUG, "SE presiono el switch");
			LOGGER.log(Level.DEBUG, "Pedido seleccionado --> {}", pedidoSeleccionado);

			if(pedidoSeleccionado != null)
			{
				actualizarEstadoPedido(old, newValue, pedidoSeleccionado);
			}
			
		});
		
		tablaDetalle.setPlaceholder(PLACE_HOLDER_SIN_PRODUCTOS);
		tablaDetalle.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue)->
		{
			if(newValue != null)
			{
				LOGGER.log(Level.DEBUG, "Producto seleccionado --> {}", newValue);
				
			}
		});
		
	    formatearCeldas();
	    lblTotal.setVisible(false);
		mostrarDetallePedido(null);
	
	}
	
	private void actualizarEstadoPedido(boolean oldValue, boolean newValue, PedidoDTO pedido)
	{
		if(newValue && !pedido.getEstado().equalsIgnoreCase(UPedido.LISTO) && !tsListo.isDisabled()
				|| !newValue && !pedido.getEstado().equalsIgnoreCase(UPedido.EN_CURSO) && !tsListo.isDisabled())
		{
			boolean modificado = false;
			modificado = Conexion.setAutoCommit(false);
			PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
			PedidoDTO pedidoAModificar = UPedido.clonarPedido(pedido);
			pedidoAModificar.setEstado(newValue ? UPedido.LISTO : UPedido.EN_CURSO);
			pedidoAModificar.setProposito("venta");

			if(pedidoDao.actualizar(pedidoAModificar) && modificado)
			{
				pedido.setEstado(pedidoAModificar.getEstado());
				Conexion.commit();
			}
			else 
			{
				pedido = null;
				
				tsListo.setSelected(oldValue);
				
				UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Modificación de pedido", "No se ha podido cambiar el estado del pedido. Servidor sin conexión");
			}
		}
	}
	
	private void formatearCeldas()
	{
		final String FONT_WEIGHT_BOLD = "-fx-font-weight: bold;" ;
		final String FONT_STYLE_ITALIC = "-fx-font-style: italic;";
		
		// Custom rendering of the table cell.
		columnaFechaToma.setCellFactory(column ->
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
						// Formato de fecha.
						setText(UFecha.formatearFecha(item, UFecha.dd_MM_yyyy));
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
					
					// icono
					FontAwesomeIconView fontCheck = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
					
					setText(item);
					setStyle(FONT_WEIGHT_BOLD + FONT_STYLE_ITALIC);

					setContentDisplay(ContentDisplay.RIGHT);
					setGraphic(null);
					
					if (item == null || empty)
					{
						setText(null);
						setStyle("");
						setContentDisplay(null);
					}
					else
					{
						switch(item)
						{
							case UPedido.PENDIENTE:
								setTextFill(Color.valueOf(Color.ORANGE.toString()));
								break;
							case UPedido.EN_CURSO:
								setTextFill(Color.valueOf(Color.BLUE.toString()));
								break;
							case UPedido.LISTO:
								setTextFill(Color.valueOf("#0f9d58"));
								break;
							case UPedido.ENTREGADO:
								setTextFill(Color.valueOf(Color.BLACK.toString()));
								setGraphic(fontCheck);
								break;
							default:
								setGraphic(null);
								setText(null);
								setStyle("");
						}
					}
				}
			};
		});
		
		columnaFechaEntrega.setCellFactory(column ->
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
						// Format date.
						setText(UFecha.formatearFecha(item, UFecha.dd_MM_yyyy));
						
						// Style all dates in March with a different color.
						if (item.getYear() == 9999)
						{
							setText("----");
						}
					}
				}
			};
		});
	}

	public void actualizarTablaPedidos()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				if(ventanaSeleccionarElemento.getOpacity() < 1.0)
					ventanaSeleccionarElemento.setOpacity(1.0);
				
				Thread.sleep(listaPedidos == null || listaPedidos.isEmpty() ? 1000 : 250);
				
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);

				listaPedidos = pedidoDao.obtenerTodos();
				
				return null;
			}
		};
		tarea.setOnRunning(e ->
		{
			tablaPedidos.setPlaceholder(spinner);
			linkRefrescar.disableProperty().bind(tarea.runningProperty());
			btnNuevoPedido.disableProperty().bind(tarea.runningProperty());
		});
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actualizarTablaPedidos)");
			tablaDetalle.setItems(null);
			tablaPedidos.setPlaceholder(vBoxSinConexion);
		});
		
		tarea.setOnSucceeded(e ->
		{
			if(listaPedidos != null)
			{
				tablaPedidos.setPlaceholder(!listaPedidos.isEmpty() ? PLACE_HOLDER_SIN_RESULTADOS : PLACE_HOLDER_SIN_PEDIDOS);
				tablaPedidos.setItems(listaPedidos);
				checkPendientes.setSelected(true);
			    checkEnCurso.setSelected(true);
			    checkListos.setSelected(true);
			    checkEntregados.setSelected(true);
				aplicarFiltros();
			}
			else
			{
				tablaDetalle.setItems(null);
				tablaPedidos.setPlaceholder(vBoxSinConexion);
			}
			
		});
		new Thread(tarea).start();
	}
	
	private void actualizarTablaDetalle(int idPedido)
	{
		DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
		ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
		ObservableList<ProductoDTO> productos = FXCollections.observableArrayList();
		
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				detallePedidoSeleccionado = detallePedidoDao.obtenerPorId(idPedido);
				
				productos.addAll(toggleClasificar.isSelected()
						? productoDao.obtenerProductosPedidoClasificado(idPedido)
						: detallePedidoSeleccionado.getProductos());
				
				return null;
			}
		};
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actualizarTablaDetalle)");
			pedidoSeleccionado = null;
			detallePedidoSeleccionado = null;
			tablaDetalle.setItems(null);
			tablaDetalle.setPlaceholder(vBoxSinConexion);
			lblTotal.setVisible(false);
		});
		
		tarea.setOnRunning(e ->
		{
			tablaDetalle.setPlaceholder(spinner);
			btnReporteDetallePedido.disableProperty().bind(tablaDetalle.itemsProperty().isNull().or(tarea.runningProperty()));
			btnReporteOrdenAserrado.disableProperty().bind(tablaDetalle.itemsProperty().isNull().or(tarea.runningProperty()));
			btnReporteOrdenArmado.disableProperty().bind(tablaDetalle.itemsProperty().isNull().or(tarea.runningProperty()));
		});
		
		tarea.setOnSucceeded(e ->
		{
			String formaSeleccionada = pedidoSeleccionado.getForma();
			
			if(formaSeleccionada.equalsIgnoreCase("A pedido"))
			{
				btnReporteDetallePedido.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isNotEqualTo(UPedido.ENTREGADO));
				btnReporteOrdenAserrado.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isEqualTo(UPedido.LISTO).or(pedidoSeleccionado.estadoPropiedad().isEqualTo(UPedido.ENTREGADO)));
				btnReporteOrdenArmado.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isNotEqualTo(UPedido.LISTO));
			}
			else if(formaSeleccionada.equalsIgnoreCase("Stock"))
			{
				btnReporteDetallePedido.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isNotEqualTo(UPedido.ENTREGADO));
				btnReporteOrdenAserrado.disableProperty().bind(pedidoSeleccionado.formaPropiedad().isEqualToIgnoreCase("Stock"));
				btnReporteOrdenArmado.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isNotEqualTo(UPedido.LISTO).and(pedidoSeleccionado.estadoPropiedad().isNotEqualTo(UPedido.EN_CURSO)));
			}
			else
			{
				btnReporteDetallePedido.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isNotEqualTo(UPedido.ENTREGADO));
				btnReporteOrdenAserrado.disableProperty().bind(pedidoSeleccionado.formaPropiedad().isEqualToIgnoreCase("Productos libres"));
				btnReporteOrdenArmado.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isNotEqualTo(UPedido.LISTO).and(pedidoSeleccionado.estadoPropiedad().isNotEqualTo(UPedido.EN_CURSO)));
			}

			
			tablaDetalle.setItems(productos);
			tablaDetalle.setPlaceholder(PLACE_HOLDER_SIN_PRODUCTOS);
			tablaDetalle.refresh();
			lblTotal.setText(UProducto.calcularTotalMaderas(productos) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaDetalle.getItems())));
			lblTotal.setVisible(true);
		});
		
		new Thread(tarea).start();
	}
	
	private void mostrarDetallePedido(PedidoDTO pedido)
	{	
		// Se obtiene el valor de la posicion actual del divisor del splitpane
		double[] dividerPosition = panelRaiz.getDividerPositions();
		
		if (pedido != null)
		{			
			pedidoSeleccionado = pedido;
			
			tsListo.disableProperty().unbind();
			
			tsListo.disableProperty().bind(pedido.estadoPropiedad().isEqualTo(UPedido.PENDIENTE).or(pedido.estadoPropiedad().isEqualTo(UPedido.ENTREGADO)));
			
			tsListo.setSelected(pedido.getEstado().equalsIgnoreCase(UPedido.LISTO) ? true : false);
			
			ClienteDTO cliente = pedido.getCliente();
			
			IntegerProperty numPedido = pedido.idPedidoPropiedad();
			StringProperty nombreCliente = cliente.nombreApellidoPropiedad();
			StringProperty telefono = cliente.telefonoPropiedad();
			StringProperty direccion = cliente.direccionPropiedad();
			StringProperty nombreLocalidad = cliente.getLocalidad().localidadPropiedad();
			StringProperty nombreProvincia = cliente.getLocalidad().getProvincia().nombreProvinciaPropiedad();
			StringProperty dni = cliente.dniPropiedad();
			StringProperty cuit = cliente.cuitPropiedad();
			StringProperty razon = cliente.razonSocialPropiedad();
			StringProperty estado = pedido.estadoPropiedad();
			StringProperty forma = pedido.formaPropiedad();
			StringProperty fechaToma = new SimpleStringProperty(UFecha.formatearFecha(pedido.getFechaToma(), UFecha.EEEE_dd_MMMM_yyyy));
			StringProperty fechaEntrega = new SimpleStringProperty(UFecha.formatearFecha(pedido.getFechaEntrega(), UFecha.EEEE_dd_MMMM_yyyy));
			IntegerProperty anioEntrega = new SimpleIntegerProperty(pedido.getFechaEntrega().getYear());
			StringProperty horaToma = new SimpleStringProperty(UFecha.formatearHora(pedido.getHoraToma(), UFecha.HH_mm_a));
			StringProperty horaEntrega = new SimpleStringProperty(UFecha.formatearHora(pedido.getHoraEntrega(), UFecha.HH_mm_a));
			StringProperty fechaModificacion = new SimpleStringProperty(UFecha.formatearFechaHora(pedido.getFechaModificacion(), UFecha.dd_MM_yyyy_HH_mm_ss));
			IntegerProperty anioModificacion = new SimpleIntegerProperty(pedido.getFechaModificacion().getYear());

			// Muestra los datosReporte del pedido en labels con binding
			
			lblCliente.textProperty().bind(nombreCliente);
			lblTelefono.textProperty().bind(new When(telefono.isNotEmpty()).then(telefono).otherwise("----"));
			lblDni.textProperty().bind(new When(dni.isNotEmpty()).then(dni).otherwise("----"));
			lblCuit.textProperty().bind(new When(cuit.isNotEmpty()).then(cuit).otherwise("----"));
			lblRazonSocial.textProperty().bind(new When(razon.isNotEmpty()).then(razon).otherwise("----"));
			lblDireccion.textProperty().bind(Bindings.when(direccion.isNotEmpty()).then(direccion).otherwise("----"));
			lblLocalidad.textProperty().bind(Bindings.when(nombreLocalidad.isNotEmpty()).then(nombreLocalidad.concat(" - " + nombreProvincia.getValue())).otherwise("----"));
			
			lblFechaT.textProperty().bind(fechaToma.concat(" " + horaToma.getValue()));
			lblEstado.textProperty().bind(estado);
			lblForma.textProperty().bind(forma);
			lblNumPedido.textProperty().bind(numPedido.asString());
			lblFechaE.textProperty().bind(Bindings.when(anioEntrega.lessThan(9999)).then(fechaEntrega.concat(" - " + horaEntrega.getValue())).otherwise("----"));
			lblFechaModificacion.textProperty().bind(Bindings.when(anioModificacion.lessThan(9999)).then(fechaModificacion).otherwise("----"));
			
			if(pedidoSeleccionado.getForma().equalsIgnoreCase("A pedido"))
			{
				
				
				// tomamos la especificacion del pedido
				EspecificacionPedidoDAOMySQLImpl especificacionPedidoDao = (EspecificacionPedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.ESPECIFICACIONP);
				
				especificacionPedidoSeleccionado = especificacionPedidoDao.obtenerPorId(pedidoSeleccionado.getIdPedido());
				
				System.out.println("ESPECIFICACION PEDIDO PRODUCTOS--> " + especificacionPedidoSeleccionado.getProductos());
			}	
			
			// Se desactivan los botones de acuerdo al estado del pedido, con binding
			//btnReporteOrdenAserrado.disableProperty().bind(estado.isEqualTo(UPedido.ENTREGADO)));
			
			// se actualiza la tabla de productos
			actualizarTablaDetalle(pedido.getIdPedido());
			
			btnCancelarPedido.disableProperty().bind(estado.isEqualTo(UPedido.ENTREGADO));
			
			btnConfEntrega.disableProperty().bind(estado.isEqualTo(UPedido.ENTREGADO).or(estado.isEqualTo(UPedido.EN_CURSO)
					.or(estado.isEqualTo(UPedido.PENDIENTE))));
			
			if(!panelDerecho.isVisible())
			{
				panelRaiz.getItems().remove(1);
				
				panelRaiz.getItems().add(panelDerecho);
				
				panelDerecho.setVisible(true);
				
				UTransiciones.fadeIn(panelDerecho, 250);
			}
		}
		else
		{	
			panelRaiz.getItems().remove(1);
			
			panelDerecho.setVisible(false);			
			
			panelRaiz.getItems().add(ventanaSeleccionarElemento);
			
			ventanaSeleccionarElemento.setVisible(true);
			
			UTransiciones.fadeIn(ventanaSeleccionarElemento, 250);
		}
		
		panelRaiz.setDividerPositions(dividerPosition);
	}
	
	private void aplicarFiltros()
	{
		ObservableList<PedidoDTO> items = !tablaPedidos.getItems().isEmpty() ? tablaPedidos.getItems() : FXCollections.observableArrayList();

		// 1. Envuelva el ObservableList en un FilteredList (inicialmente muestra todos los datosReporte).
        FilteredList<PedidoDTO> clientesFiltrados = new FilteredList<>(items, p -> true);

     // 2. Establezca el Predicado del filtro siempre que el filtro cambie.
        filtroCliente.textProperty().addListener((observable, oldValue, newValue) -> 
        {
        	clientesFiltrados.setPredicate(pedido ->
            {
                // Si el filtro de texto esta vacio, se muestra todos los clientes.
                if (newValue == null || newValue.isEmpty()) 
                {
                    return true;
                }

                // Comparar el estado de cada pedido con texto de filtro.
                String lowerCaseFiltro = newValue.toLowerCase();

                if (pedido.getCliente().toString().toLowerCase().contains(lowerCaseFiltro))
                {
                    return true; // // El filtro coincide con el nombre del cliente.
                } 
                return false; // No coincide.
            });
        });
        
     // 3. Envuelve la FilteredList en una SortedList. 
        SortedList<PedidoDTO> clientesOrdenados = new SortedList<>(clientesFiltrados);

        // 4. Enlazar el comparador SortedList al comparador TableView.
        clientesOrdenados.comparatorProperty().bind(tablaPedidos.comparatorProperty());

        // 5. Agregar datosReporte ordenados (y filtrados) a la tabla.
        tablaPedidos.setItems(clientesOrdenados);
        
       // ----------------------------------------------------------------------------------------------
       // ----------------------------------------------------------------------------------------------
        
     // 1. Envuelva el ObservableList en un FilteredList (inicialmente muestra todos los datosReporte).
        FilteredList<PedidoDTO> entregadosFltrados = new FilteredList<>(tablaPedidos.getItems(), p -> true);

     // 2. Establezca el Predicado del filtro siempre que el filtro cambie.
        checkEntregados.selectedProperty().addListener((observable, oldValue, newValue) -> 
        {
        	entregadosFltrados.setPredicate(pedido ->
            {
            	// Si el filtro de texto esta vacio, se muestran todos los pedidos.
                if (newValue) 
                {
                    return true;
                }

                // Comparar el estado de cada pedido con texto de filtro.
                String lowerCaseFiltro = checkEntregados.getUserData().toString();

                if (pedido.getEstado().equalsIgnoreCase(lowerCaseFiltro))
                {
                    return false; // // El filtro coincide con el estado del pedido.
                } 
                return true; // No coincide.
            });
        });
        
     // 3. Envuelve la FilteredList en una SortedList. 
        SortedList<PedidoDTO> entregadosOrdenados = new SortedList<>(entregadosFltrados);

        // 4. Enlazar el comparador SortedList al comparador TableView.
        entregadosOrdenados.comparatorProperty().bind(tablaPedidos.comparatorProperty());

        // 5. Agregar datosReporte ordenados (y filtrados) a la tabla.
        tablaPedidos.setItems(entregadosOrdenados);
        
        // ----------------------------------------------------------------------------------------------
        // ----------------------------------------------------------------------------------------------
        
        // 1. Envuelva el ObservableList en un FilteredList (inicialmente muestra todos los datosReporte).
           FilteredList<PedidoDTO> enCursoFiltrados = new FilteredList<>(tablaPedidos.getItems(), p -> true);

        // 2. Establezca el Predicado del filtro siempre que el filtro cambie.
           checkEnCurso.selectedProperty().addListener((observable, oldValue, newValue) -> 
           {
        	   enCursoFiltrados.setPredicate(pedido ->
               {
                   // Si el filtro de texto esta vacio, se muestran todos los pedidos.
                   if (newValue) 
                   {
                       return true;
                   }

                   // Comparar el estado de cada pedido con texto de filtro.
                   String lowerCaseFiltro = checkEnCurso.getUserData().toString();

                   if (pedido.getEstado().equalsIgnoreCase(lowerCaseFiltro))
                   {
                       return false; // // El filtro coincide con el estado del pedido.
                   } 
                   return true; // No coincide.
               });
           });
           
        // 3. Envuelve la FilteredList en una SortedList. 
           SortedList<PedidoDTO> enCursoOrdenados = new SortedList<>(enCursoFiltrados);

           // 4. Enlazar el comparador SortedList al comparador TableView.
           enCursoOrdenados.comparatorProperty().bind(tablaPedidos.comparatorProperty());

           // 5. Agregar datosReporte ordenados (y filtrados) a la tabla.
           tablaPedidos.setItems(enCursoOrdenados);
           
        // ----------------------------------------------------------------------------------------------
        // ----------------------------------------------------------------------------------------------

           // 1. Envuelva el ObservableList en un FilteredList (inicialmente muestra todos los datosReporte).
              FilteredList<PedidoDTO> pendientesFiltrados = new FilteredList<>(tablaPedidos.getItems(), p -> true);

           // 2. Establezca el Predicado del filtro siempre que el filtro cambie.
              checkPendientes.selectedProperty().addListener((observable, oldValue, newValue) -> 
              {
            	  pendientesFiltrados.setPredicate(pedido ->
                  {
                      // Si el filtro de texto esta vacio, se muestran todos los pedidos.
                      if (newValue) 
                      {
                          return true;
                      }

                      // Comparar el estado de cada pedido con texto de filtro.
                      String lowerCaseFiltro = checkPendientes.getUserData().toString();

                      if (pedido.getEstado().equalsIgnoreCase(lowerCaseFiltro))
                      {
                          return false; // // El filtro coincide con el estado del pedido.
                      } 
                      return true; // No coincide.
                  });
              });
              
           // 3. Envuelve la FilteredList en una SortedList. 
              SortedList<PedidoDTO> pendientesOrdenados = new SortedList<>(pendientesFiltrados);

              // 4. Enlazar el comparador SortedList al comparador TableView.
              pendientesOrdenados.comparatorProperty().bind(tablaPedidos.comparatorProperty());

              // 5. Agregar datosReporte ordenados (y filtrados) a la tabla.
              tablaPedidos.setItems(pendientesOrdenados);
		
		//-------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------
              
           // 1. Envuelva el ObservableList en un FilteredList (inicialmente muestra todos los datosReporte).
              FilteredList<PedidoDTO> listosFiltrados = new FilteredList<>(tablaPedidos.getItems(), p -> true);

           // 2. Establezca el Predicado del filtro siempre que el filtro cambie.
              checkListos.selectedProperty().addListener((observable, oldValue, newValue) -> 
              {
            	  listosFiltrados.setPredicate(pedido ->
                  {
                      // Si el filtro de texto esta vacio, se muestran todos los pedidos.
                      if (newValue) 
                      {
                          return true;
                      }

                      // Comparar el estado de cada pedido con texto de filtro.
                      String lowerCaseFiltro = checkListos.getUserData().toString();

                      if (pedido.getEstado().equalsIgnoreCase(lowerCaseFiltro))
                      {
                          return false; // // El filtro coincide con el estado del pedido, se eliminan los desmarcados.
                      } 
                      return true; // No coincide.
                  });
              });
              
           // 3. Envuelve la FilteredList en una SortedList. 
              SortedList<PedidoDTO> listosOrdenados = new SortedList<>(listosFiltrados);

              // 4. Enlazar el comparador SortedList al comparador TableView.
              listosOrdenados.comparatorProperty().bind(tablaPedidos.comparatorProperty());

              // 5. Agregar datosReporte ordenados (y filtrados) a la tabla.
              tablaPedidos.setItems(listosOrdenados);
		
		/*FilteredList<ProductoEliminadoDTO>filteredObjects = new FilteredList<>(tablaPedidos.getItems(), p -> true);
		SortedList<ProductoEliminadoDTO> listosOrdenados = new SortedList<>(objetosFiltrados);
		objetosOrdenados.comparatorProperty().bind(tablaPedidos.comparatorProperty());
		tablaPedidos.setItems(objetosOrdenados);
		
		filtroCliente.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredObjects.setPredicate(obj -> {
			    if (!checkEntregados.isSelected() && !checkEnCurso.isSelected()) {
			        return false;
			    }

			    if (newValue == null || newValue.isEmpty()) {
			    } else {
			        String lowerCaseFilter = newValue.toLowerCase();
			        if ( obj.getName().toLowerCase().contains(lowerCaseFilter) ||
			             obj.getLocation().toLowerCase().contains(lowerCaseFilter)) {
			        } else {
			            return false;
			        }
			    }

			    if (chkLiveObj.isSelected() && !obj.getLive()) {
			        return false;
			    }

			    if (!chkRel5.isSelected() && obj.getVersion() == 5) {
			        return false;
			    }

			    if (!chkRel6.isSelected() && obj.getVersion() == 6) {
			        return false;
			    }

			    return true;
			  });
			});*/
	}
	
	private void reporteOrdenCarga()
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
						
						DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
						DetallePedidoDTO detallePedidoSeleccionado = detallePedidoDao.obtenerPorId(pedidoSeleccionado.getIdPedido());
						
						updateProgress(i+=5, 10);

						String titulo = "Orden de aserrado #" + String.valueOf(pedidoSeleccionado.getIdPedido());
						String descripcion = "Especificacion de maderas a aserrar";
						String numPedido = String.valueOf("#" + pedidoSeleccionado.getIdPedido());
						
						datosReporte.put("titulo", titulo + pedidoSeleccionado.getIdPedido());
						datosReporte.put("descripcion", descripcion);
								
						productos = especificacionPedidoSeleccionado.getProductos();

						String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
						String totalPies = String.valueOf(UProducto.calcularTotalPies(productos));

						JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(productos);

						Map <String, Object> parametros = new HashMap<>();
						parametros.put("productosDataSource", itemsJRBean);
						parametros.put("titulo", titulo);
						parametros.put("descripcion", descripcion);
						parametros.put("numPedido", numPedido);
						parametros.put("totalMaderas", totalMaderas);
						parametros.put("totalPies", totalPies);

						JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_orden_carga.jasper"), parametros);

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
			miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, pedidoSeleccionado.getCliente().getEMail());
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
		});
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actionReporteOrdenCarga)");
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");
		});
		new Thread(tarea).start();
	}
	
	private void reporteOrdenCarga2()
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
						
						DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
						DetallePedidoDTO detallePedidoSeleccionado = detallePedidoDao.obtenerPorId(pedidoSeleccionado.getIdPedido());
						
						updateProgress(i+=5, 10);
						
						String titulo = "Orden de armado #" + String.valueOf(pedidoSeleccionado.getIdPedido());
						String descripcion = "Especificacion de maderas a armar";
						String numPedido = String.valueOf("#" + pedidoSeleccionado.getIdPedido());
						String forma = pedidoSeleccionado.getForma().equalsIgnoreCase("Stock") ? "Productos de stock" : "Productos libres";
						
						datosReporte.put("titulo", titulo + pedidoSeleccionado.getIdPedido());
						datosReporte.put("descripcion", descripcion);
						
						productos = detallePedidoSeleccionado.getProductos();
						
						String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
						String totalPies = String.valueOf(UProducto.calcularTotalPies(productos));
						
						JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(productos);
						
						Map <String, Object> parametros = new HashMap<>();
						parametros.put("productosDataSource", itemsJRBean);
						parametros.put("titulo", titulo);
						parametros.put("descripcion", descripcion);
						parametros.put("forma", forma);
						parametros.put("numPedido", numPedido);
						parametros.put("totalMaderas", totalMaderas);
						parametros.put("totalPies", totalPies);
						
						JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_orden_carga2.jasper"), parametros);
						
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
			miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, pedidoSeleccionado.getCliente().getEMail());
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
		});
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actionReporteOrdenCarga)");
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");
		});
		new Thread(tarea).start();
	}
	
	
	
	// Metodos FXML
	
	@FXML private void actionActualizarTablaPedidos() 
	{
		actualizarTablaPedidos();
	}
	
	@FXML private void actionInformes() 
	{
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_INFORMES_PEDIDOS);
		
		miCoordinador.getInformesPedidosControlador().actualizarComboAnios();
		
		miCoordinador.getInformesPedidosControlador().actualizarTablaVentas();
	}
	
	
	@FXML private void actionNuevoPedido()
	{
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_NUEVO_PEDIDO);
		
		//miCoordinador.getInformesPedidosControlador().actualizarTablaPedidos();
		
	}
	
	@FXML private void actionCancelarPedido()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		btnAceptar.setOnAction(e ->
		{
			// tomamos el pedido seleccionado de la tabal
			pedidoSeleccionado = tablaPedidos.getSelectionModel().getSelectedItem();
			
			// tomamos la forma
			String formaSeleccionada = pedidoSeleccionado.getForma();
			
			// hacemos una copia de los productos del pedido
			ObservableList<ProductoDTO> productos = UProducto.clonarProductos(detallePedidoSeleccionado.getProductos());
			
			boolean pedidoEliminado = true;
			
			Conexion.setAutoCommit(false);

			System.out.println("FORMA SELECCIONADA ----> " + formaSeleccionada);
			
			// preguntamos si la forma del pedido es "stock o "A pedido"
			if(formaSeleccionada.equalsIgnoreCase("Stock"))
			{
				// Se actualiza la reserva de cada producto en la BD
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				for(ProductoDTO p : productos)
				{
					ProductoDTO productoAModificar = productoDao.obtenerPorId(p.getIdProducto());
					productoAModificar.setReserva(productoAModificar.getReserva() - p.getStock());
					if(productoDao.actualizar(productoAModificar)) 
					{
						pedidoEliminado = true;
					}
					else
					{
						pedidoEliminado = false;
						break;
					}
					
					// verificamos nuevamente si existen productos cargados en el pedido
					if(!productos.isEmpty())
					{
						DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
						
						// eliminamos el detalle del pedido
						pedidoEliminado = pedidoEliminado && detallePedidoDao.eliminar(detallePedidoSeleccionado);	
					}
					
					PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
					
					pedidoEliminado = pedidoEliminado && pedidoDao.eliminar(pedidoSeleccionado);
				}	
			}
			
			else if(formaSeleccionada.equalsIgnoreCase("A pedido"))
			{
				System.out.println("ENTRA EN A PEDIDO ");
				
				ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
				
				// obtenemos las producciones de los productos del pedido
				ObservableList<ProduccionDTO> produccionesPedido = FXCollections.observableArrayList(produccionDao.obtenerProduccionDePedido(pedidoSeleccionado.getIdPedido()));
				
				System.out.println("Producciones pedido size: " + produccionesPedido.size());
				System.out.println("ID PEDIDO: " + pedidoSeleccionado.getIdPedido());
				
				System.out.println("BANDERA PEDIDO ELIMINADO ANTES DE ENTRAR A ELIMINAR EL DETALLE -> " + pedidoEliminado);
				System.out.println("Productos SIZE -> " + productos.size());
				
				// actualziamos la producción de esos productos, seteando su idPedido a -1. Esto significa que la produccion esta
				// indefinida a que pertenece (si a un pedido o stock)
				for(ProduccionDTO p : produccionesPedido)
				{
					p.setIdPedido(-1);
					if(produccionDao.actualizar(p))
					{
						pedidoEliminado = true;
					}
					else 
					{
						System.out.println("Entro al else");
						pedidoEliminado = false;
						break;
					}
				}
				
				// eliminamos las especificacion de productos
				EspecificacionPedidoDAOMySQLImpl especificacionPedidoDao = (EspecificacionPedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.ESPECIFICACIONP);	
				
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
				
				// verificamos nuevamente si existen productos cargados en el pedido
				if(!productos.isEmpty())
				{
					DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
					
					// eliminamos el detalle del pedido
					pedidoEliminado = pedidoEliminado && detallePedidoDao.eliminar(detallePedidoSeleccionado);	
				}

				// eliminamos el detalle de especificacion y luego el pedido
				pedidoEliminado = pedidoEliminado && especificacionPedidoDao.eliminar(especificacionPedidoSeleccionado) && pedidoDao.eliminar(pedidoSeleccionado);
			}
			else
			{
				System.out.println("ENTRA EN A PRODUCTOS LIBRES ");
				
				ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
				
				// obtenemos las producciones de los productos del pedido
				ObservableList<ProduccionDTO> produccionesPedido = FXCollections.observableArrayList(produccionDao.obtenerProduccionDePedido(pedidoSeleccionado.getIdPedido()));
				
				System.out.println("Producciones pedido size: " + produccionesPedido.size());
				System.out.println("ID PEDIDO: " + pedidoSeleccionado.getIdPedido());
				
				System.out.println("BANDERA PEDIDO ELIMINADO ANTES DE ENTRAR A ELIMINAR EL DETALLE -> " + pedidoEliminado);
				System.out.println("Productos SIZE -> " + productos.size());
				
				// actualziamos la producción de esos productos, seteando su idPedido a -1. Esto significa que la produccion esta
				// indefinida a que pertenece (si a un pedido o stock)
				for(ProduccionDTO p : produccionesPedido)
				{
					p.setIdPedido(-1);
					if(produccionDao.actualizar(p))
					{
						pedidoEliminado = true;
					}
					else 
					{
						System.out.println("Entro al else");
						pedidoEliminado = false;
						break;
					}
				}
				
				
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
				
				// verificamos nuevamente si existen productos cargados en el pedido
				if(!productos.isEmpty())
				{
					DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
					
					// eliminamos el detalle del pedido
					pedidoEliminado = pedidoEliminado && detallePedidoDao.eliminar(detallePedidoSeleccionado);	
				}

				// eliminamos el detalle de especificacion y luego el pedido
				pedidoEliminado = pedidoEliminado && pedidoDao.eliminar(pedidoSeleccionado);
			}
			
			if(pedidoEliminado)
			{
				// confirmamos las transacciones
				Conexion.commit();
				
				UNotificaciones.notificacion("Cancelación de orden.", "La orden ha sido cancelada.");
				
				actualizarTablaPedidos();
			}
			else
			{
				UAlertas.alerta("Baja de pedido", "No se ha podido dar de baja al pedido");
			}
		});
		
		UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Baja de pedido", "¿Está seguro de cancelar el pedido?");
		
	}
	
	@FXML private void actionConfirmarEntrega()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		btnAceptar.setOnAction(e ->
		{
			Conexion.setAutoCommit(false);
			boolean confirmado = false;
			PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
			PedidoDTO pedidoConfirmado = tablaPedidos.getSelectionModel().getSelectedItem();
			pedidoConfirmado.setFechaEntrega(LocalDate.now());
			pedidoConfirmado.setHoraEntrega(LocalTime.now());
			pedidoConfirmado.setEstado(UPedido.ENTREGADO);
			pedidoConfirmado.setProposito(pedidoSeleccionado.getProposito());
			
			if(detallePedidoSeleccionado == null)
			{
				DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
			
				detallePedidoSeleccionado = detallePedidoDao.obtenerPorId(pedidoConfirmado.getIdPedido());
			}
				
			ObservableList <ProductoDTO> productos = UProducto.clonarProductos(detallePedidoSeleccionado.getProductos());
			
			// Se actualiza el estado del pedido en la BD
			if(pedidoDao.actualizar(pedidoConfirmado))
			{
				// se pregunta si la forma del pedido es "a pedido" o "stock"
				if(pedidoConfirmado.getForma().equalsIgnoreCase("Stock")) 
				{
					// Se resta la reserva menos la cantidad del producto
					ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
					// Se actualiza la reserva y el stock de cada producto en la BD
					for(ProductoDTO p : productos)
					{
						
						ProductoDTO productoAModificar = productoDao.obtenerPorId(p.getIdProducto());
						
						
						LOGGER.log(Level.DEBUG, "PRODUCTO A MODIFICAR antes del set --> {}", productoAModificar);
						
						productoAModificar.setStock(productoAModificar.getStock() - p.getStock());
						productoAModificar.setReserva(productoAModificar.getReserva() - p.getStock());
					
						LOGGER.log(Level.DEBUG, "PRODUCTO A MODIFICAR despues del set --> {}", productoAModificar);
						
						if(!productoDao.actualizar(productoAModificar))
						{
							confirmado = false;
							break;
						}
						else confirmado = true;
					}
				}
				else confirmado = true;
			}
			
			if(confirmado && Conexion.commit())
			{
				mostrarDetallePedido(pedidoConfirmado);
				
				UNotificaciones.notificacion("Confirmación de entrega", "Se ha confirmado la entrega.");
				
				if(miCoordinador.getInformesPedidosControlador() != null) 
					miCoordinador.getInformesPedidosControlador().actualizarTablaVentas();;			
				
				//CorreoUtil.notificarPedidoEntregado(pedidoConfirmado);
			}
			else
			{
				// Se revierten los cambios realizados al pedido
				pedidoConfirmado.setFechaEntrega(LocalDate.of(9999, 9, 9));
				pedidoConfirmado.setHoraEntrega(LocalTime.of(00, 00, 00));
				pedidoConfirmado.setEstado(UPedido.LISTO);
				
				detallePedidoSeleccionado = null;
				
				UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Confirmación de entrega", "No se ha podido confirmar la entrega del pedido");
			}
		});
		
		UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Confirmación de entrega", "¿Desea confirmar la entrega del pedido?");
	}
	
	@FXML private void actionModificarPedido()
	{

		setPedidoSeleccionado(tablaPedidos.getSelectionModel().getSelectedItem());
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_MODIFICAR_PEDIDO);
		miCoordinador.getModificarPedidoControlador().cargarDatosPedido();

	}
	
	@FXML private void actionReporteDetallePedido()
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
						
						DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
						DetallePedidoDTO detallePedidoSeleccionado = detallePedidoDao.obtenerPorId(pedidoSeleccionado.getIdPedido());

						updateProgress(i+=5, 10);
						
						datosReporte.put("titulo", "Pedido_detalle");
						datosReporte.put("descripcion", "Descripción");
						
						String numPedido = String.valueOf(pedidoSeleccionado.getIdPedido());
						String cliente = pedidoSeleccionado.getCliente().toString();
						String fechaToma = String.valueOf(pedidoSeleccionado.getFechaToma());
						String forma = pedidoSeleccionado.getForma();
						String dni = !pedidoSeleccionado.getCliente().getDni().isEmpty() 
								? String.valueOf(pedidoSeleccionado.getCliente().getDni())
								: "________";
						
						productos = tablaDetalle.getItems();
						
						String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
						String totalPies = String.valueOf(UProducto.calcularTotalPies(productos));
						
						JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(productos);
						
						Map <String, Object> parametros = new HashMap<>();
						parametros.put("productosDataSource", itemsJRBean);
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
			miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, pedidoSeleccionado.getCliente().getEMail());
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
		});
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - (actionReporteDetallePedido)");
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");
		});
		new Thread(tarea).start();
	}
	
	@FXML private void actionReporteOrdenPedido()
	{
		String formaPedido = pedidoSeleccionado.getForma();
		
		if(formaPedido.equalsIgnoreCase("Stock") || formaPedido.equalsIgnoreCase("Productos libres"))
		{
			reporteOrdenCarga2();
		}
		else
		{
			reporteOrdenCarga();
		}
	}
}
