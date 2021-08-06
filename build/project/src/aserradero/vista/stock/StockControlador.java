package aserradero.vista.stock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;

import aserradero.AserraderoMain;
import aserradero.controlador.AserraderoApp;
import aserradero.modelo.DAO.mysql.ProductoDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.ProductoEliminadoDAOMySQLImpl;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.DTO.ProductoEliminadoDTO;
import aserradero.modelo.conexion.Conexion;
import aserradero.reportes.JasperReports;
import aserradero.util.UAlertas;
import aserradero.util.UFactoryDAO;
import aserradero.util.UFecha;
import aserradero.util.UNotificaciones;
import aserradero.util.UProducto;
import aserradero.util.UTransiciones;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class StockControlador
{
	@FXML private SplitPane panelRaiz;
	@FXML private BorderPane panelDerecho;
	//@FXML private StackPane panelCentroDerecho;
	//@FXML private VBox vboxCentro;
	@FXML private VBox panelSur;
	
	@FXML private TextField filtroEspesor;
	@FXML private TextField filtroAncho;
	@FXML private TextField filtroLargo;
	
	@FXML private TableView<ProductoDTO> tablaStock;
	@FXML private TableView<ProductoDTO> tablaDetalleProducto;
	
	//@FXML private TableColumn <ProductoDTO, String> columnaTipo;
	@FXML private TableColumn <ProductoDTO, String> columnaEspesor;
	@FXML private TableColumn <ProductoDTO, String> columnaAncho;
	@FXML private TableColumn <ProductoDTO, Double> columnaLargo;
	@FXML private TableColumn <ProductoDTO, Integer> columnaCantidad;
	@FXML private TableColumn <ProductoDTO, Double> columnaPies;
	
	@FXML private TableColumn <ProductoDTO, String> columnaEspesor2;
	@FXML private TableColumn <ProductoDTO, String> columnaAncho2;
	@FXML private TableColumn <ProductoDTO, Double> columnaLargo2;
	@FXML private TableColumn <ProductoDTO, Double> columnaPies2;
	
	@FXML private Hyperlink linkRefrescar;

	@FXML private Label lblEspesor;
	@FXML private Label lblAncho;
	@FXML private Label lblLargo;
	
	@FXML private TextField tfEspesor;
	@FXML private TextField tfAncho;
	@FXML private TextField tfLargo;
	
	@FXML private RadioButton radioClasificado;
	@FXML private RadioButton radioIndividual;
	
	@FXML private Label lblTotal;
	
	@FXML private JFXButton btnPedidosStock;
	@FXML private JFXButton btnReporteStock;
	@FXML private JFXButton btnVerProductosEliminados;
	@FXML private JFXButton btnGuardar;
	@FXML private JFXButton btnEliminarProducto;
	
	@FXML private Label labelHeader;
	
	private static final Logger LOGGER = LogManager.getLogger(StockControlador.class);
	
	private ToggleGroup grupoRadio;
	
	private final JFXSpinner spinner = new JFXSpinner();
	
	private VBox vBoxSinConexion = new VBox();
	
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);

	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	
	private static final Label PLACE_HOLDER_SIN_RESULTADOS = new Label("Sin resultados");
	
	private static final Label PLACE_HOLDER_SIN_STOCK = new Label("Sin stock");
	
	private ObservableList<ProductoDTO> stockDisponible = FXCollections.observableArrayList();
	
	private BorderPane ventanaSeleccionarElemento;
	
	private BorderPane ventanaSeleccionarProducto;
	
	private AserraderoApp miCoordinador;
	
	private Map<String, String> datosReporte = new HashMap<>();

	
	public StockControlador()
	{
		// ...
	}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	@FXML private void initialize() 
	{
		iconoSinConexion.setSize("40");
		
		linkReintentar.setOnAction(e ->
		{
			actualizarTablaStock();
		});
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		ventanaSeleccionarElemento = (BorderPane) AserraderoApp.obtenerPanel(AserraderoApp.PANEL_SELECCIONAR_ELEMENTO_DETALLE);
		ventanaSeleccionarProducto = (BorderPane) AserraderoApp.obtenerPanel(AserraderoApp.PANEL_SELECCIONAR_PRODUCTO_EDITAR);
		ventanaSeleccionarProducto.setOpacity(1.0);
		ventanaSeleccionarProducto.setPrefSize(panelSur.getPrefWidth(), panelSur.getPrefHeight());
		
		filtroEspesor.setPromptText("Espesor");
		filtroAncho.setPromptText("Ancho");
		filtroLargo.setPromptText("Largo");	
				
		// Modo de seleccion simple
		tablaStock.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);		
		tablaStock.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue)
						-> mostrarUnidadesProducto(newValue));		
		
		tablaDetalleProducto.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tablaDetalleProducto.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue)
				-> mostrarValores(newValue));

		tfEspesor.setOnKeyReleased(e -> keyReleasedGuardarCambios(e));
		tfAncho.setOnKeyReleased(e -> keyReleasedGuardarCambios(e));
		tfLargo.setOnKeyReleased(e -> keyReleasedGuardarCambios(e));
				
		radioClasificado.setUserData("Clasificado");
		radioIndividual.setUserData("Individual");
		
		radioClasificado.setSelected(true);
		
		btnGuardar.disableProperty().bind(tfEspesor.textProperty().isEmpty()
				.or(tfAncho.textProperty().isEmpty()
				.or(tfLargo.textProperty().isEmpty())));
		
		btnEliminarProducto.disableProperty().bind(tablaDetalleProducto.getSelectionModel().selectedItemProperty().isNull());
		
		grupoRadio = new ToggleGroup();
		
		grupoRadio.getToggles().addAll(radioClasificado, radioIndividual);
		
		grupoRadio.selectedToggleProperty().addListener((obs, oldValue, newValue) ->
		{
			actualizarTablaStock();
		});
		
		lblTotal.setVisible(false);
		
		formatearCampos();
		formatearCeldas();
		
		mostrarUnidadesProducto(null);
		mostrarValores(null);
	}

	private void keyReleasedGuardarCambios(KeyEvent e)
	{
		if(e.getCode() == KeyCode.ENTER && !btnGuardar.isDisabled())
		{
				actionGuardarCambios();
		}
	}
	
	private void formatearCeldas()
	{
		//columnaTipo.setCellValueFactory(cellData -> cellData.getValue().tipoPropiedad());
		columnaEspesor.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
		columnaAncho.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
		columnaLargo.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
		columnaLargo.setText("Largo");
		columnaCantidad.setCellValueFactory(cellData -> cellData.getValue().stockPropiedad().asObject());
		columnaPies.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject());
		
		columnaEspesor2.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
		columnaAncho2.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
		columnaLargo2.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
		columnaPies2.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject());
	}
	
	public void formatearCampos()
	{
		final String FORMATOLETRAS = "\\sa-zA-Z*";
		final String FORMATONUMEROS = "\\d{0,2}([\\.]\\d{0,2})?";
		
		final String REPLACELETRAS = "[^\\\\sa-zA-Z\\s]";
		
		
		filtroEspesor.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(FORMATONUMEROS) || newValue.startsWith("."))
				{
					filtroEspesor.setText(oldValue);
				}
			}				
		});
	
		filtroAncho.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(FORMATONUMEROS) || newValue.startsWith("."))
				{
					filtroAncho.setText(oldValue);
				}
			}				
		});
		
		filtroLargo.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(FORMATONUMEROS) || newValue.startsWith("."))
				{
					filtroLargo.setText(oldValue);
				}
			}				
		});
		
		tfEspesor.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(FORMATONUMEROS) || newValue.startsWith("."))
				{
					tfEspesor.setText(oldValue);
				}
			}				
		});
	
		tfAncho.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(FORMATONUMEROS) || newValue.startsWith("."))
				{
					tfAncho.setText(oldValue);
				}
			}				
		});
		
		tfLargo.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(FORMATONUMEROS) || newValue.startsWith("."))
				{
					tfLargo.setText(oldValue);
				}
			}				
		});
}
	
	private void aplicarFiltros()
	{	
		ObservableList<ProductoDTO> items = !tablaStock.getItems().isEmpty() ? tablaStock.getItems() : FXCollections.observableArrayList();
		
		/*// Ajustar la Lista observable en una lista filtrada (inicialmente mostrar todos los datosReporte)
		FilteredList <ProductoDTO> tipoFiltrado = new FilteredList <>(items, p -> true);

		// Establezca el filtro Predicado cada vez que cambie el filtro
		filtroProducto.textProperty().addListener((observable, oldValue, newValue) ->
		{
			tipoFiltrado.setPredicate(p -> 
			{
				// Compare el nombre y el apellido de cada persona con el texto de filtro
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}

				//
				String lowerCaseFiltro = newValue.toLowerCase();

				if(p.getTipo().toLowerCase().contains(lowerCaseFiltro))
				{
					return true; // Filtro coincide con el apellido
				}
				return false; // No coincide

			});
			
			System.out.println("Se muestra el total");
			lblTotal.setText(!tablaStock.getItems().isEmpty() ? UProducto.calcularTotalMaderas(tablaStock.getItems()) + " | " + UProducto.calcularTotalPies(tablaStock.getItems())
			: "0 | 0");
		});
					
		//
		SortedList <ProductoDTO> productosOrdenados = new SortedList <>(tipoFiltrado);

		//
		productosOrdenados.comparatorProperty().bind(tablaStock.comparatorProperty());

		//
		tablaStock.setItems(productosOrdenados);*/

		FilteredList <ProductoDTO> espesorFiltrado = new FilteredList <>(tablaStock.getItems(), p -> true);

		// Establezca el filtro Predicado cada vez que cambie el filtro
		filtroEspesor.textProperty().addListener((observable, oldValue, newValue) -> 
		{
			espesorFiltrado.setPredicate(producto ->
			{
				// Compare el nombre y el apellido de cada persona con el texto de filtro
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}

				//
				String lowerCaseFiltro = newValue;

				if(String.valueOf(producto.getEspesor()).contains(lowerCaseFiltro))
				{
					return true; //
				}
				return false;

			});
			
			lblTotal.setText(!tablaStock.getItems().isEmpty() ? UProducto.calcularTotalMaderas(tablaStock.getItems()) + " | " + UProducto.calcularTotalPies(tablaStock.getItems())
			: "0 | 0");		
		});

		//
		SortedList <ProductoDTO> espesoresOrdenados = new SortedList <>(espesorFiltrado);

		//
		espesoresOrdenados.comparatorProperty().bind(tablaStock.comparatorProperty());

		//
		tablaStock.setItems(espesoresOrdenados);

		FilteredList <ProductoDTO> anchoFiltrado = new FilteredList <>(tablaStock.getItems(), p -> true);

		//
		filtroAncho.textProperty().addListener((observable, oldValue, newValue) -> 
		{
			anchoFiltrado.setPredicate(producto -> 
			{
				//
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}

				//
				String filtroAncho = newValue;

				if(String.valueOf(producto.getAncho()).contains(filtroAncho))
				{
					return true; //
				}
				return false;
			});
			
			lblTotal.setText(!tablaStock.getItems().isEmpty() ? UProducto.calcularTotalMaderas(tablaStock.getItems()) + " | " + UProducto.calcularTotalPies(tablaStock.getItems())
			: "0 | 0");
		});

		//
		SortedList <ProductoDTO> anchosOrdenados = new SortedList <>(anchoFiltrado);

		//
		anchosOrdenados.comparatorProperty().bind(tablaStock.comparatorProperty());

		//
		tablaStock.setItems(anchosOrdenados);

		FilteredList <ProductoDTO> largoFiltrado = new FilteredList <>(tablaStock.getItems(), a -> true);

		//
		filtroLargo.textProperty().addListener((observable, oldValue, newValue) -> 
		{
			largoFiltrado.setPredicate(producto -> 
			{
				//
				if(newValue == null || newValue.isEmpty())
				{
					return true;
				}

				//
				String filtroLargo = newValue;

				if(String.valueOf(producto.getLargo()).contains(filtroLargo))
				{
					return true; //
				}
				return false;
			});
			
			lblTotal.setText(!tablaStock.getItems().isEmpty() ? UProducto.calcularTotalMaderas(tablaStock.getItems()) + " | " + UProducto.calcularTotalPies(tablaStock.getItems())
			: "0 | 0");		
		});

		//
		SortedList <ProductoDTO> largosOrdenados = new SortedList <>(largoFiltrado);

		//
		largosOrdenados.comparatorProperty().bind(tablaStock.comparatorProperty());

		//
		tablaStock.setItems(largosOrdenados);
	}
	
	private void limpiarFiltros()
	{
		//filtroProducto.clear();
		filtroEspesor.clear();
		filtroAncho.clear();
		filtroLargo.clear();
	}

	public void limpiarTablaStock()
	{
		tablaStock.itemsProperty().unbind();
		tablaStock.getItems().clear();
	}
	
	private void mostrarUnidadesProducto(ProductoDTO item)
	{
		// Se obtiene el valor de la posicion actual del divisor del panel
		double[]dividerPosition = panelRaiz.getDividerPositions();
		
		LOGGER.log(Level.DEBUG, "Producto seleccionado --> {}", item);
		
		if (item != null && item.getStock() > 0)
		{
			if(grupoRadio.getSelectedToggle().getUserData().toString().equalsIgnoreCase("Clasificado"))
			{
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				tablaDetalleProducto.setItems(productoDao.obtenerUnidadesClasificadas(item));
			}
			else
			{
				tablaDetalleProducto.setItems(mostrarUnidadesIndividual(item));
			}
			
			if (!panelDerecho.isVisible())
			{
				panelRaiz.getItems().remove(1);
				
				panelRaiz.getItems().add(panelDerecho);
				
				panelDerecho.setVisible(true);
				
				UTransiciones.fadeIn(panelDerecho, 250);
			}
			
			if(ventanaSeleccionarProducto.getOpacity() < 1.0)
				ventanaSeleccionarProducto.setOpacity(1.0);
		} 
		else
		{
			tablaDetalleProducto.getItems().clear();
			
			panelDerecho.setVisible(false);
			
			panelRaiz.getItems().remove(1);
			
			panelRaiz.getItems().add(ventanaSeleccionarElemento);
			
			ventanaSeleccionarElemento.setVisible(true);
			
			UTransiciones.fadeIn(ventanaSeleccionarElemento, 250);
		}
		
		// Se setea el valor de la posicion obtenida anteriormente
		panelRaiz.setDividerPositions(dividerPosition);
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
	
	/** Método encargado de mostrar en campos de texto los valores de las medidas (espesor, ancho, largo) del item pasado como parámetro.
	 * Se rellenan los campos siempre que el item no sea nulo y se haya seleccionado solamente un item de la tabla.
	 * @param item - Item seleccionado de la tabla.
	 */
	private void mostrarValores(ProductoDTO item)
	{
		if(item != null)
		{
			LOGGER.log(Level.DEBUG, "Unidad seleccionada --> {}", tablaDetalleProducto.getSelectionModel().getSelectedItem());
			
			panelDerecho.setBottom(panelSur);
			
			if(tablaDetalleProducto.getSelectionModel().getSelectedItems().size() > 1)
			{
				tfEspesor.setDisable(true);
				tfAncho.setDisable(true);
				tfLargo.setDisable(true);
				
				lblEspesor.setDisable(true);
				lblAncho.setDisable(true);
				lblLargo.setDisable(true);
				
			}
			else
			{
				tfEspesor.setDisable(false);
				tfAncho.setDisable(false);
				tfLargo.setDisable(false);
				
				lblEspesor.setDisable(false);
				lblAncho.setDisable(false);
				lblLargo.setDisable(false);
				
			}
			
			tfEspesor.setText(item.getEspesor()+"");
			tfAncho.setText(item.getAncho()+"");
			tfLargo.setText(item.getLargo()+"");

			if(!panelSur.isVisible())
			{
				panelSur.setVisible(true);
				UTransiciones.fadeIn(panelSur, 250);
			}
		}
		else
		{
			tfEspesor.setText("");
			tfAncho.setText("");
			tfLargo.setText("");
			
			panelSur.setVisible(false);
			
			panelDerecho.setBottom(ventanaSeleccionarProducto);
			
			ventanaSeleccionarProducto.setVisible(true);
			
			UTransiciones.fadeIn(ventanaSeleccionarProducto, 250);
		}
	}
	
	/** Método encargado de actualizar la tabla de stock.
	 * Se chequea qué radioButton está seleccionado. De acuerdo a ello, opta por traer el stock de una forma u otra.
	 * @throws DAOException 
	 */
	public synchronized void actualizarTablaStock()
	{
		Task <Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception
			{
				
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				
				Thread.sleep(stockDisponible == null || stockDisponible.isEmpty() ? 1000 : 250);
				
				lblTotal.setVisible(false);
				
				stockDisponible = radioClasificado.isSelected() 
						? productoDao.obtenerStockClasificado() 
						: productoDao.obtenerTodos();
				
				
				return null;
			}
		};
		tarea.setOnRunning(e -> 
		{
			tablaStock.setPlaceholder(spinner);	
			btnReporteStock.disableProperty().bind(tarea.runningProperty());
		});
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.DEBUG, "Fallo la tarea");
			tablaStock.setItems(null);
			tablaStock.setPlaceholder(vBoxSinConexion);
			lblTotal.setText("0 | 0");
			btnReporteStock.disableProperty().unbind();
		});
		
		tarea.setOnSucceeded(e -> 
		{
			if(stockDisponible != null)
			{
				tablaStock.setPlaceholder(!stockDisponible.isEmpty() ? PLACE_HOLDER_SIN_RESULTADOS : PLACE_HOLDER_SIN_STOCK);
				tablaStock.setItems((stockDisponible));
				aplicarFiltros();
				limpiarFiltros();
				tablaStock.refresh();
				
				columnaLargo.setText(grupoRadio.getSelectedToggle().getUserData().toString().equalsIgnoreCase("Clasificado")
						? "Total largo" : "Largo");
				
				lblTotal.setText(UProducto.calcularTotalMaderas(stockDisponible) + " | " + UProducto.calcularTotalPies(stockDisponible));

				btnReporteStock.disableProperty().unbind();
				btnReporteStock.setDisable(stockDisponible != null && stockDisponible.isEmpty());	
			}
			else
			{
				tablaStock.setPlaceholder(vBoxSinConexion);
				lblTotal.setText("0 | 0");
			}
			
			lblTotal.setVisible(true);
			
			//notifyAll();
				
		});
		
		new Thread(tarea).start();
	}
	
	public void limpiarSeleccionTablaStock()
	{
		Task <Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception
			{
				Thread.sleep(250);
				//tablaStock.getSelectionModel().clearSelection();
				limpiarFiltros();
				return null;
			}
		};
		new Thread(tarea).start();
	}
	
	// FXML
	
	@FXML private void actionPedidosStock()
	{
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_PEDIDOS_STOCK);
	}
	
	@FXML private void actionActualizarTablaStock()
	{
		actualizarTablaStock();
	}
	
	@FXML private void actionReporteStock()
	{
		
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				ObservableList<ProductoDTO> productos = FXCollections.observableArrayList();
				
				for(int i = 0; i< 10; i++)
				{
					updateProgress(i, 10);
					
					if(productos.isEmpty())
					{
						Thread.sleep(100);
						
						String fecha = UFecha.formatearFecha(LocalDate.now(), UFecha.EEEE_dd_MMMM_yyyy_ESPACIOS);
						
						productos = tablaStock.getItems();
						
						String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
						String totalPies = String.valueOf(UProducto.calcularTotalPies(productos) + " P");
						
						JRBeanCollectionDataSource productosJRBeans = new JRBeanCollectionDataSource(productos);
						
						datosReporte.put("titulo", "Stock actual - " + UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy_ESPACIOS));
						datosReporte.put("descripcion", "Stock actual. Fecha de generación " + fecha);

						updateProgress(i+=5, 10);
						
						Map <String, Object> parametros = new HashMap<>();
						
						parametros.put("productosDataSource", productosJRBeans);
						parametros.put("fecha", fecha);
						parametros.put("totalMaderas", totalMaderas);
						parametros.put("totalPies", totalPies);
						
						JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_stock.jasper"), parametros);
																	
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
			miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, null);
			datosReporte.clear();
		});
		tarea.setOnFailed(e -> 
		{
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
			miCoordinador.stopLoadingProgress();
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");

		});
		new Thread(tarea).start();
	}
	
	@FXML private void actionVerProductosEliminados()
	{
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_PRODUCTOS_ELIMINADOS);
	}
	
	@FXML private void actionGuardarCambios()
	{
		ProductoDTO productoSeleccionadoTablaStock = tablaStock.getSelectionModel().getSelectedItem();
		ProductoDTO productoSeleccionadoDetalle = tablaDetalleProducto.getSelectionModel().getSelectedItem();

		LOGGER.log(Level.DEBUG, "ID producto seleccionado --> {}", productoSeleccionadoDetalle.getIdProducto());
		
		// Se instancia un nuevo producto con los nuevos valores introducidos por el usuario
		ProductoDTO productoModificado = new ProductoDTO();
		productoModificado.setEspesor(Double.parseDouble(tfEspesor.getText().trim()));
		productoModificado.setAncho(Double.parseDouble(tfAncho.getText().trim()));
		productoModificado.setLargo(Double.parseDouble(tfLargo.getText().trim()));
		productoModificado.setStock(1);
		productoModificado.setPies();
		
		LOGGER.log(Level.DEBUG, "Producto modificado --> {}", productoModificado);
		
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		// Se pregunta si el producto modificado es distinto al producto seleccionado
		if(!productoModificado.equals(productoSeleccionadoDetalle))
		{
			btnAceptar.setOnAction(e ->
			{
				Conexion.setAutoCommit(false);
				boolean modificado = false;
				ProductoDTO productoExistenteBd = null;
				
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				
				ObservableList<ProductoDTO> productosStock = productoDao.obtenerTodos();
				ObservableList<ProductoDTO> productosStockClasificado = productoDao.obtenerStockClasificado();
				
				// Se verifica si los nuevos valores del producto modifcado ya existe en la tabla (tambien en la BD)
				productoExistenteBd = productosStock != null ? UProducto.comprobrarExistenciaProducto(productoModificado, productosStock) : null;
				
				LOGGER.log(Level.DEBUG, "Producto existente --> {}", productoExistenteBd);

				// Si el producto devuelto por el metodo no es nulo y su id es distinto a cero (significa que existe)
				if(productoExistenteBd != null && productoExistenteBd.getIdProducto() != 0)
				{
					LOGGER.log(Level.DEBUG, "Producto existente: Si");
					
					System.out.println("P EXISTENTE ANTES DEL SET = " + productoExistenteBd.getStock());
					
					// Se agrega una unidad mas a su stock y se lo actualiza en la BD
					productoExistenteBd.setStock((productoExistenteBd.getStock() + productoExistenteBd.getReserva()) + 1);
					
					System.out.println("P EXISTENTE DESPUES DEL SET = " + productoExistenteBd.getStock());
					
					modificado = productoDao.actualizar(productoExistenteBd);		// > PRIMERA TRANSACCION
					
					LOGGER.log(Level.DEBUG, "Producto modificado? --> {}", modificado);
				}
				else
				{
					//se da de alta al nuevo producto
					modificado = productoDao.insertar(productoModificado);		// > PRIMERA TRANSACCION				
	
				}
				
				// COMPLETAR  !!! <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<!!
				
				if(modificado)
				{	
					// Se descuenta una unidad al producto seleccionado y se lo actualiza
					ProductoDTO productoAModificar = productoDao.obtenerPorId(productoSeleccionadoDetalle.getIdProducto());
					productoAModificar.setStock(productoAModificar.getStock() - 1);

					if(productoDao.actualizar(productoAModificar) && Conexion.commit())		// > SEGUNDA TRANSACCION
					{						
						actualizarTablaStock();
						
						if(radioClasificado.isSelected())
						{
							
							ProductoDTO p = UProducto.comprobrarExistenciaProductoClasificado(productoSeleccionadoTablaStock, tablaStock.getItems());
							tablaStock.getSelectionModel().select(p);
							LOGGER.log(Level.DEBUG, "Producto clasificado --> {}", p);
						}
						else
						{
							ProductoDTO p = UProducto.comprobrarExistenciaProducto(productoSeleccionadoTablaStock, tablaStock.getItems());
							tablaStock.getSelectionModel().select(p);
							LOGGER.log(Level.DEBUG, "Producto individual --> {}", p);
						}
						
						tablaStock.getSelectionModel().clearSelection();
						
						UNotificaciones.notificacion("Modificación de producto", "¡Se han guardado los cambios!");
					}
					else 
					{
						UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Modificación de producto", "No se ha podido modificar el producto");

					}
				}
				else
				{
					UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Modificación de producto", "No se ha podido modificar el producto");

				}
			});
			
			UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Modificación de producto", "¿Confirmar los cambios ralizados?");
		}
	}
	
	@FXML private void actionEliminarProducto()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		btnAceptar.setDisable(true);
		
		Label lbl = new Label("Ingresar motivo de eliminación (obligatorio)");
		TextField tf = new TextField();
		
		tf.textProperty().addListener((obs, old, newValue) ->
		{
			btnAceptar.setDisable(newValue.isEmpty() || newValue.startsWith(" ") || newValue.contains(" +"));
		});
		
		//btnAceptar.disableProperty().bind(tf.textProperty().isNull().or(tf.textProperty().isEmpty()));
		
		if(radioIndividual.isSelected() || radioClasificado.isSelected())
		{
			btnAceptar.setOnAction(e ->
			{
				Conexion.setAutoCommit(false);
				
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				
				ProductoEliminadoDAOMySQLImpl productoEliminadoDao = (ProductoEliminadoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTOS_ELIMINADOS);
				
				ProductoDTO productoSeleccionado = tablaStock.getSelectionModel().getSelectedItem();
				
				ProductoDTO unidadSeleccionada = tablaDetalleProducto.getSelectionModel().getSelectedItem();
				
				LocalDateTime fechaEliminacion = LocalDateTime.now();
				
				// tomamos el motivo insertado y recortamos los espacios de mas
				String motivo = tf.getText().trim().replaceAll(" +", " ");
				
				String motivoFiltrado = motivo.substring(0, motivo.length() > 50 ? 50 : motivo.length());
				
				// instanciamos el objeto de producto eliminado a insertar 
				ProductoEliminadoDTO productoEliminado = new ProductoEliminadoDTO();
				productoEliminado.setProducto(unidadSeleccionada);
				productoEliminado.setMotivo(motivoFiltrado);
				productoEliminado.setFechaEliminacion(fechaEliminacion);
				
				//int cantidad = tablaDetalleProducto.getSelectionModel().getSelectedItems().size();
				
				LOGGER.log(Level.DEBUG, "ID Producto seleccionado --> {}", productoSeleccionado.getIdProducto());
				
				ProductoDTO productoAEliminar = radioIndividual.isSelected() ? productoDao.obtenerPorId(unidadSeleccionada.getIdProducto()) : productoDao.obtener(unidadSeleccionada);
				
				LOGGER.log(Level.DEBUG, "Producto a eliminar --> {}", productoAEliminar);
				
				if(productoAEliminar != null)
					productoAEliminar.setStock(productoAEliminar.getStock() - 1);
				
				
				if(productoDao.actualizar(productoAEliminar) && productoEliminadoDao.insertar(productoEliminado) && Conexion.commit())
				{
					// Se quita el/los productos eliminados de la tabla detalle
					tablaDetalleProducto.getItems().remove(tablaDetalleProducto.getSelectionModel().getSelectedItem());
					tablaDetalleProducto.getSelectionModel().clearSelection();

					// Se descuenta las unidades y los pies eliminados del producto seleccionado
					productoSeleccionado.setStock(productoSeleccionado.getStock() - 1);
					
					System.out.println("P antes de set pies -> " + productoSeleccionado);

					if(radioClasificado.isSelected())
					{
						// restamos el total de largo 
						productoSeleccionado.setLargo(productoSeleccionado.getLargo() - unidadSeleccionada.getLargo());
						productoSeleccionado.setPiesClasificado();
					}
						
					else
					{
						productoSeleccionado.setPies();
					}
						
					System.out.println("P antes de set pies -> " + productoSeleccionado);
					
					if(productoSeleccionado.getStock() <= 0) 
						actualizarTablaStock();

					lblTotal.setText(UProducto.calcularTotalMaderas(stockDisponible) + " | " + UProducto.calcularTotalPies(stockDisponible));
					
					
					
					UNotificaciones.notificacion("Eliminación de producto", "Se ha eleminado el producto.");
				}
				else 
				{
					UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), null, "Eliminación de producto","No se ha podido eliminar el producto");	
				}
			});
			
			UAlertas.mostrarDialogoEntrada(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(lbl, tf, btnAceptar, btnCancelar), "Eliminación de producto", "¿Está seguro de eliminar el producto seleccionado?");
		}
		
		
	}
	
	@FXML private void insertarProductosAleatorios()
	{
	}
}

