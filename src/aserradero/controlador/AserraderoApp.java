package aserradero.controlador;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.events.JFXDialogEvent;

import aserradero.AserraderoMain;
import aserradero.vista.administrador.AdministradorControlador;
import aserradero.vista.cargaPedido.CargaPedidoControlador;
import aserradero.vista.clientes.ClientesControlador;
import aserradero.vista.empleado.EmpleadoControlador;
import aserradero.vista.estadisticas.EstadisticasControlador;
import aserradero.vista.informesPedidos.InformesPedidosControlador;
import aserradero.vista.login.LoginControlador;
import aserradero.vista.menuPrincipal.MenuPrincipalControlador;
import aserradero.vista.modificarPedido.ModificarPedidoControlador;
import aserradero.vista.nuevoCliente.NuevoClienteControlador;
import aserradero.vista.nuevoPedido.NuevoPedidoControlador;
import aserradero.vista.panelRaiz.PanelRaizControlador;
import aserradero.vista.pedidos.PedidosControlador;
import aserradero.vista.pedidosStock.PedidosStockControlador;
import aserradero.vista.preferencias.PreferenciasControlador;
import aserradero.vista.produccion.ProduccionControlador;
import aserradero.vista.productosEliminados.ProductosEliminadosControlador;
import aserradero.vista.productosLibres.ProductosLibresControlador;
import aserradero.vista.reporte.ReporteControlador;
import aserradero.vista.stock.StockControlador;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Clase encargada de crear y administrar las pantallas, controlarlas y coordinarlas.
 * @author King
 *
 */
public class AserraderoApp
{
	// Paneles
	
	// Panel base donde sera montado el panel raiz
	private StackPane stackPane;
	
	// Panel que contiene toda la interfaz de usuario
	private BorderPane panelRaiz;
	
	// Paneles que seran montados en el centro del panel principal
	
	// Panel que contiene la interfaz del usuario con privilegio "Administrador", que ira al centro del panel raiz
	private JFXTabPane panelAdministrador;
	
	// Panel que contiene la interfaz del usuario con privilegio "Empleado", que ira al centro del panel raiz
	private BorderPane panelEmpleado;
	
	// Paneles adicionales de paginacion (nuevas pantallas)
	public static BorderPane panelPreferencias;
	public static Node panelNuevoPedido;
	public static BorderPane panelModificarPedido;	
	public static BorderPane panelClientesDeshabilitados;
	public static BorderPane panelNuevoCliente;
	public static BorderPane panelEstadisticas;
	public static BorderPane panelReporte;
	
	public static Node panelSeleccionarElemento;
	public static Node panelSeleccionarElemento2;
	public static Node panelSeleccionarProducto;
	
	// Ruta de panel raiz
	public static final String PANEL_RAIZ = "vista/panelRaiz/PanelRaiz.fxml";
	
	// Rutas de pantallas principales
	public static final String PANEL_LOGIN = "vista/login/PanelLogin.fxml";
	public static final String PANEL_ADMINISTRADOR = "vista/administrador/PanelAdministrador.fxml";
	public static final String PANEL_EMPLEADO = "vista/empleado/PanelEmpleado.fxml";
	
	// Rutas de Tabs (admin)
	public static final String PANEL_MENU_PRINCIPAL = "vista/menuPrincipal/PanelMenuPrincipal.fxml";
	public static final String PANEL_CLIENTES = "vista/clientes/PanelClientes.fxml";
	public static final String PANEL_PEDIDOS = "vista/pedidos/PanelPedidos.fxml";
	public static final String PANEL_STOCK = "vista/stock/PanelStock.fxml";
	public static final String PANEL_PRODUCCION = "vista/produccion/PanelProduccion.fxml";
	
	// Rutas de paneles adicionales
	public static final String PANEL_INFORMES_PEDIDOS = "vista/informesPedidos/PanelInformesPedidos.fxml";
	//public static final String PANEL_NUEVO_PEDIDO = "vista/nuevoPedido/PanelNuevoPedido.fxml";
	public static final String PANEL_NUEVO_PEDIDO = "vista/nuevoPedido/PanelNuevoPedido2.fxml";
	public static final String PANEL_MODIFICAR_PEDIDO = "vista/modificarPedido/PanelModificarPedido.fxml";
	public static final String PANEL_NUEVO_CLIENTE = "vista/nuevoCliente/PanelNuevoCliente.fxml";
	public static final String PANEL_PREFERENCIAS = "vista/preferencias/PanelPreferencias.fxml";
	public static final String PANEL_ESTADISTICAS = "vista/estadisticas/PanelEstadisticas.fxml";
	public static final String PANEL_REPORTE = "vista/reporte/PanelReporte.fxml";
	public static final String PANEL_PEDIDOS_STOCK = "vista/pedidosStock/PanelPedidosStock.fxml";
	public static final String PANEL_CARGA_PEDIDO = "vista/cargaPedido/PanelCargaPedido.fxml";
	public static final String PANEL_PRODUCTOS_LIBRES = "vista/productosLibres/PanelProductosLibres.fxml";
	public static final String PANEL_PRODUCTOS_ELIMINADOS = "vista/productosEliminados/PanelProductosEliminados.fxml";

	public static final String PANEL_SELECCIONAR_ELEMENTO_DETALLE = "vista/seleccionarElementoDetalle/PanelSeleccionarElementoDetalle.fxml";
	public static final String PANEL_SELECCIONAR_ELEMENTO_DETALLE2 = "vista/seleccionarElementoDetalle2/PanelSeleccionarElementoDetalle2.fxml";
	public static final String PANEL_SELECCIONAR_PRODUCTO_EDITAR = "vista/seleccionarProductoEditar/PanelSeleccionarProductoEditar.fxml";	
	
	// Ruta de Tabs (empleado)
	public static final String PANEL_PEDIDOS_EMPLEADO = "vista/pedidosEmpleado/PanelPedidosEmpleado.fxml";
	
	// Controladores de cada archivo FXML
	private LoginControlador loginControlador;
	private MenuPrincipalControlador menuPrincipalControlador;
	private PanelRaizControlador panelRaizControlador;
	private AdministradorControlador administradorControlador;
	private ClientesControlador clientesControlador;
	private InformesPedidosControlador informesPedidosControlador;
	private NuevoClienteControlador nuevoClienteControlador;
	private NuevoPedidoControlador nuevoPedidoControlador;
	private ModificarPedidoControlador modificarPedidoControlador;
	private EstadisticasControlador estadisticasControlador;
	private ReporteControlador reporteControlador;
	private StockControlador stockControlador;
	private ProduccionControlador produccionControlador;
	private PedidosControlador pedidosControlador;
	private EmpleadoControlador empleadoControlador;
	private CargaPedidoControlador cargaPedidoControlador;
	private PedidosStockControlador pedidosStockControlador;
	private ProductosLibresControlador productosLibresControlador;
	private ProductosEliminadosControlador productosEliminadosControlador;
	private PreferenciasControlador preferenciasControlador;
	
	// Stage que contiene la interfaz de usuario login
	private Stage loginStage;
	
	// Stage que contiene el panel raiz
	private Stage primaryStage;
	
	private Scene scene;
	
	// Spinner para mostrar cuando se este haciendo un proceso
	private JFXSpinner spinnerLoading = new JFXSpinner();
	
	// Barra de progreso para mostrar cuando se este haciendo un proceso
	private JFXProgressBar progressBar = new JFXProgressBar();
	
	// Label que indica el tipo de proceso que se esta haciendo
	private Label lblIndicador = new Label("");
	
	// VBox que contiene al spiner, progress y label anteriores
	private VBox vbox = new VBox();
	
	private JFXDialogLayout dialogLayout;
	
	private JFXDialog dialog = null;
	
	public static final Logger LOGGER = LogManager.getLogger(AserraderoApp.class);
	public static final String DIRECTORIO_APLICACCION = System.getProperty("user.dir");
	public static final String NOMBRE_APLICACION = "Aserradero";
	public static final String VERSION_APLICACION = "1.0b";
	public static final String ICONO_APLICACION = "recursos/imagenes/aserradero.png";
	public static final String ESTILOS_APLICACION = "recursos/estilos/aserradero_naranja_claro.css";
	
	public AserraderoApp() throws SecurityException, IOException
	{
		spinnerLoading.setRadius(20);
		
		lblIndicador.setStyle("-fx-font-weight: bold;");
		
		vbox.getChildren().addAll(spinnerLoading, progressBar, lblIndicador);
		vbox.setSpacing(15);
		vbox.setAlignment(Pos.CENTER);
		
		panelSeleccionarElemento = crearPanel(PANEL_SELECCIONAR_ELEMENTO_DETALLE);
		panelSeleccionarElemento2 = crearPanel(PANEL_SELECCIONAR_ELEMENTO_DETALLE2);
		panelSeleccionarProducto = crearPanel(PANEL_SELECCIONAR_PRODUCTO_EDITAR);
		
	}
	
	public Stage getPrimaryStage()
	{
		return this.primaryStage;
	}
	
	public Stage getLoginStage()
	{
		return this.loginStage;
	}
	
	public StackPane getPanelRaiz()
	{
		return this.stackPane;
	}
	
	public Node getPanelPrincipal()
	{
		return this.panelRaiz;
	}
	
	public AdministradorControlador getAministradorControlador()
	{
		return administradorControlador;
	}
	
	public MenuPrincipalControlador getMenuPrincipalControlador()
	{
		return menuPrincipalControlador;
	}
	
	public ClientesControlador getClientesControlador()
	{
		return clientesControlador;
	}
	
	public PedidosControlador getPedidosControlador()
	{
		return pedidosControlador;
	}
	
	public StockControlador getStockControlador()
	{
		return stockControlador;
	}
	
	public ProduccionControlador getProduccionControlador()
	{
		return produccionControlador;
	}
	
	public EmpleadoControlador getEmpleadoControlador()
	{
		return empleadoControlador;
	}
	
	public InformesPedidosControlador getInformesPedidosControlador()
	{
		return informesPedidosControlador;
	}
	
	public NuevoPedidoControlador getNuevoPedidoControlador()
	{
		return nuevoPedidoControlador;
	}
	
	public ModificarPedidoControlador getModificarPedidoControlador()
	{
		return modificarPedidoControlador;
	}
	
	public PedidosStockControlador getCargasStockControlador()
	{
		return pedidosStockControlador;
	}
	
	public CargaPedidoControlador getCargaPedidoControlador()
	{
		return cargaPedidoControlador;
	}
	
	public EstadisticasControlador getEstadisticasControlador()
	{
		return estadisticasControlador;
	}
	
	public ReporteControlador getReporteControlador()
	{
		return reporteControlador;
	}
	
	/** Método encargado de iniciar y mostrar la interfaz de usuario Login.
	 * 
	 * @param stage - Stage donde será montada la interfaz.
	 */
	public void iniciarLogin(Stage stage)
	{
		Parent login = (Parent) crearPanel(PANEL_LOGIN);

		Scene scene = new Scene(login);
		scene.getStylesheets().add(ESTILOS_APLICACION);

		this.loginStage = stage;
		loginStage.setScene(scene);
		loginStage.setTitle(NOMBRE_APLICACION + " - " + VERSION_APLICACION);
		loginStage.setResizable(false);
		loginStage.getIcons().add(new Image(ICONO_APLICACION));
		loginStage.show();

		/*Rectangle2D primScreenBounds = Screen.getPrimary().getBounds();
		if (primScreenBounds.getWidth() < 1024.0)
		{
			loginStage.setMaximized(true);
		}
		else
		{
			loginStage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
			loginStage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 4);
		}*/
	}

	public void inicarPantallaPrincipal(String usuario)
	{
		stackPane = new StackPane();

		panelRaiz = (BorderPane) crearPanel(PANEL_RAIZ);

		panelRaizControlador.setUsuario(usuario);

		// Si el usuario seleccionado es igual a "Administrador"
		if (usuario.equalsIgnoreCase("Administracion"))
		{
			panelAdministrador = (JFXTabPane) crearPanel(PANEL_ADMINISTRADOR);
			
			// Colocamos la ventana en el centro de la ventana raiz
			panelRaiz.setCenter(panelAdministrador);
		}
		else if(usuario.equalsIgnoreCase("Produccion"))
		{
			panelEmpleado = (BorderPane) crearPanel(PANEL_EMPLEADO);
			
			// Colocamos la ventana en el centro de la ventana raiz
			panelRaiz.setCenter(panelEmpleado);
		}

		stackPane.getChildren().add(panelRaiz);
		
		scene = new Scene(stackPane);
		scene.getStylesheets().add(ESTILOS_APLICACION);
	}
	
	public void mostrarPantallaPrincipal()
	{
		Platform.runLater(() ->
		{
			this.primaryStage = new Stage(StageStyle.DECORATED);
			primaryStage.setScene(scene);
			primaryStage.setTitle(NOMBRE_APLICACION + " " + VERSION_APLICACION);
			primaryStage.setMinHeight(400);
			primaryStage.setMinWidth(400);
			primaryStage.sizeToScene();
			primaryStage.getIcons().add(new Image(ICONO_APLICACION));
			primaryStage.show();
			primaryStage.setMaximized(true);
			
			/*
			this.primaryStage = new Stage(StageStyle.DECORATED);
			
			JFXDecorator decorator = new JFXDecorator(primaryStage, stackPane);
			
			scene = new Scene(decorator);
			
			//scene = new Scene(stackPane, Color.valueOf("#EFA116"));
			scene.getStylesheets().add(ESTILOS_APLICACION);
			
			primaryStage.setScene(scene);
			primaryStage.setTitle(NOMBRE_APLICACION + " " + VERSION_APLICACION);
			primaryStage.setMinHeight(400);
			primaryStage.setMinWidth(400);
			primaryStage.sizeToScene();
			primaryStage.getIcons().add(new Image(ICONO_APLICACION));
			primaryStage.show();
			primaryStage.setMaximized(true);
			*/
		});
	}
	
	public void ejecutarTarea(Task<Integer> tarea, String indicador)
	{
		tarea.setOnRunning(e ->
		{
			lblIndicador.setText(indicador);
			vbox.setVisible(true);
			stackPane.getChildren().add(vbox);
			progressBar.progressProperty().bind(tarea.progressProperty());
			panelRaiz.disableProperty().bind(tarea.runningProperty());
		});
		
		tarea.setOnSucceeded(e ->
		{
			stackPane.getChildren().remove(vbox);
			vbox.setVisible(false);
			progressBar.progressProperty().unbind();
			panelRaiz.disableProperty().unbind();
			lblIndicador.textProperty().unbind();
			lblIndicador.setText("");
		});
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo al ejecutar tarea - (ejecutarTarea)");
			tarea.cancel();
			stackPane.getChildren().remove(vbox);
		});
		
		new Thread(tarea).start();
	}
	
	public void startLoadingProgress(Task<Integer> tarea, String indicador)
	{
		if(dialogLayout == null)
			dialogLayout = new JFXDialogLayout(); 
		
        dialogLayout.setBody(vbox);
		
		if(dialog == null)
			dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.TOP);
		
		dialog.show();
        dialog.setOnDialogClosed((JFXDialogEvent event1) -> 
        {
        	panelRaiz.setEffect(null);
        });
        
        BoxBlur blur = new BoxBlur(3, 3, 3);
        
        panelRaiz.setEffect(blur);
		
		
		lblIndicador.textProperty().bind(new SimpleStringProperty(indicador));
		vbox.setVisible(true);
		progressBar.progressProperty().bind(tarea.progressProperty());
		panelRaiz.disableProperty().bind(tarea.runningProperty());
	}
	
	public void stopLoadingProgress()
	{
		dialog.close();
		
		vbox.setVisible(false);
		
		progressBar.progressProperty().unbind();
		panelRaiz.disableProperty().unbind();
		lblIndicador.textProperty().unbind();
		
		lblIndicador.setText("");
	}
	
	public void cargarPanelReporte(SwingNode swingNode, Map <String, String> datosReporte, String correoCliente)
	{	
		// Se obtiene el panel de reporte
		panelReporte = (BorderPane) crearPanel(PANEL_REPORTE);
		
		reporteControlador.setTituloReporte(datosReporte.get("titulo"));
		reporteControlador.setDescripcionReporte(datosReporte.get("descripcion"));
		reporteControlador.setCorreoCliente(correoCliente);
		
		// Se agrega al centro del panel el reporte (swingNode) pasado como parametro
		panelReporte.setCenter(swingNode);
		
		panelRaizControlador.cargarNuevaPantalla(panelReporte);
	}
	
	public void cargarNuevaPantalla(String ruta)
	{
		Node nodo = crearPanel(ruta);
		panelRaizControlador.cargarNuevaPantalla(nodo);
	}
	
	public Node crearPanel (String ruta)
	{
		Node node = null;
		
		try
		{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(AserraderoMain.class.getResource(ruta));
			node = loader.load();
			node.setUserData(ruta);
			Object controlador = loader.getController();

			// se inicializan los controladores
			if(controlador instanceof LoginControlador)
			{
				loginControlador = (LoginControlador) controlador;
				loginControlador.setCoordinador(this);
			}
			else if (controlador instanceof PanelRaizControlador)
			{
				panelRaizControlador = (PanelRaizControlador) controlador;
				panelRaizControlador.setCoordinador(this);
			}
			else if(controlador instanceof AdministradorControlador)
			{
				administradorControlador = (AdministradorControlador) controlador;
				administradorControlador.setCoordinador(this);
				administradorControlador.crearTabs();
			}
			else if(controlador instanceof MenuPrincipalControlador)
			{
				menuPrincipalControlador = (MenuPrincipalControlador) controlador;
				menuPrincipalControlador.setCoordinador(this);
			}
			else if (controlador instanceof ClientesControlador)
			{
				clientesControlador = (ClientesControlador) controlador;
				clientesControlador.setCoordinador(this);
			}
			else if (controlador instanceof PedidosControlador)
			{
				pedidosControlador = (PedidosControlador) controlador;
				pedidosControlador.setCoordinador(this);
			}
			else if(controlador instanceof ModificarPedidoControlador)
			{
				modificarPedidoControlador = (ModificarPedidoControlador) controlador;
				modificarPedidoControlador.setCoordinador(this);
				//modificarPedidoControlador.cargarDatosPedido();
				
			}
			else if (controlador instanceof StockControlador)
			{
				stockControlador = (StockControlador) controlador;
				stockControlador.setCoordinador(this);
			}
			else if (controlador instanceof ProduccionControlador)
			{
				produccionControlador = (ProduccionControlador) controlador;
				produccionControlador.setCoordinador(this);
			}
			else if(controlador instanceof InformesPedidosControlador)
			{
				informesPedidosControlador = (InformesPedidosControlador) controlador;
				informesPedidosControlador.setCoordinador(this);
			}
			else if(controlador instanceof NuevoPedidoControlador)
			{
				nuevoPedidoControlador = (NuevoPedidoControlador) controlador;
				nuevoPedidoControlador.setCoordinador(this);
			}
			else if(controlador instanceof NuevoClienteControlador)
			{
				nuevoClienteControlador = (NuevoClienteControlador) controlador;
				nuevoClienteControlador.setVentanaPrincipal(this);
			}
			else if(controlador instanceof EstadisticasControlador)
			{
				estadisticasControlador = (EstadisticasControlador) controlador;
				estadisticasControlador.setCoordinador(this);
			}
			else if(controlador instanceof ReporteControlador)
			{
				reporteControlador = (ReporteControlador) controlador;
				reporteControlador.setVentanaRaiz(this);
			}
			else if(controlador instanceof EmpleadoControlador)
			{
				empleadoControlador = (EmpleadoControlador) controlador;
				empleadoControlador.setCoordinador(this);
			}
			else if(controlador instanceof CargaPedidoControlador)
			{
				cargaPedidoControlador = (CargaPedidoControlador) controlador;
				cargaPedidoControlador.setCoordinador(this);
				//cargaPedidoControlador.cargarDetallePedido();
			}
			else if(controlador instanceof PreferenciasControlador)
			{
				preferenciasControlador = (PreferenciasControlador) controlador;
				preferenciasControlador.setCoordinador(this);
			}
			else if(controlador instanceof PedidosStockControlador)
			{
				pedidosStockControlador = (PedidosStockControlador) controlador;
				pedidosStockControlador.setCoordinador(this);
			}
			
			else if(controlador instanceof ProductosLibresControlador)
			{
				productosLibresControlador = (ProductosLibresControlador) controlador;
				productosLibresControlador.setCoordinador(this);
			}
			
			else if(controlador instanceof ProductosEliminadosControlador)
			{
				productosEliminadosControlador = (ProductosEliminadosControlador) controlador;
				productosEliminadosControlador.setCoordinador(this);
			}
		}
		catch (IOException e)
		{
			LOGGER.log(Level.ERROR, "Ha ocurrido un error al crear el panel. Causa --> {}", e.getMessage());
		}
		
		return node;
	}
	
	public static Node obtenerPanel(String ruta)
	{
		Node nodo = null;
		
		try
		{
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(AserraderoMain.class.getResource(ruta));
			nodo = loader.load();
			nodo.setUserData(ruta);
			Object controlador = loader.getController();
			
		}
		catch (IOException e)
		{
			LOGGER.log(Level.ERROR, "Ha ocurrido un error al obtener el panel. Causa --> {}", e.getMessage());
		}
		
		return nodo;
	}
	
	public void mostrarLogin()
	{
		loginStage.show();
	}
	
	public void cerrarPanelLogin()
	{
		//loginStage.close();
		loginStage.hide();
	}
	
	public void cerrarPanelRaiz()
	{
		primaryStage.close();
		System.out.println("primaryStage memoria: " + primaryStage);
	}
	
	public void pantallaCompleta()
	{
		primaryStage.setFullScreen(!primaryStage.isFullScreen());
	}
	
	public void cerrarAplicacion()
	{
		System.exit(0);
	}
}
