package aserradero.vista.productosLibres;

import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;

import aserradero.controlador.AserraderoApp;
import aserradero.modelo.DAO.mysql.ProduccionDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.ProductoDAOMySQLImpl;
import aserradero.modelo.DTO.ProduccionDTO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UAlertas;
import aserradero.util.UFactoryDAO;
import aserradero.util.UNotificaciones;
import aserradero.util.UProducto;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ProductosLibresControlador 
{
	@FXML private TableView<ProductoDTO> tablaProductos;
	
	@FXML private TableColumn<ProductoDTO, String> columnaEspesor;
	@FXML private TableColumn<ProductoDTO, String> columnaAncho;
	@FXML private TableColumn<ProductoDTO, Double> columnaLargo;
	@FXML private TableColumn<ProductoDTO, Integer> columnaCantidad;
	@FXML private TableColumn<ProductoDTO, Double> columnaPies;
	
	@FXML private TableView<ProductoDTO> tablaProductosStock;
	
	@FXML private TableColumn<ProductoDTO, String> columnaEspesor2;
	@FXML private TableColumn<ProductoDTO, String> columnaAncho2;
	@FXML private TableColumn<ProductoDTO, Double> columnaLargo2;
	@FXML private TableColumn<ProductoDTO, Integer> columnaCantidad2;
	@FXML private TableColumn<ProductoDTO, Double> columnaPies2;
	
	@FXML private JFXButton btnAgregar;
	@FXML private Spinner <Integer> spUnidades;
	
	@FXML private JFXButton btnQuitarUnidades;
	@FXML private Spinner <Integer> spUnidades2;
	
	@FXML private JFXButton btnGuardarCambios;
	
	@FXML private Label lblTotal;
	@FXML private Label lblTotal2;
	
	private static final Logger LOGGER = LogManager.getLogger(ProductosLibresControlador.class);
	
	private AserraderoApp miAserraderoApp;
	
	private static final Label PLACE_HOLDER_SIN_PRODUCTOS_INDEFINIDOS = new Label("Sin productos");
	private static final Label PLACE_HOLDER_SELECCIONAR_PRODUCTOS = new Label("Seleccione los productos que desea agregar a stock");
	
	private FontAwesomeIconView iconoFlecha = new FontAwesomeIconView(FontAwesomeIcon.LONG_ARROW_LEFT);
	
	private HBox hboxPlaceHolder = new HBox();
	
	private JFXSpinner spinner = new JFXSpinner();
	
	private VBox vBoxSinConexion = new VBox();
	
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	
	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	
	public ProductosLibresControlador()
	{
		// ...
	}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miAserraderoApp = coordinador;
	}
	
	@FXML public void initialize()
	{
		iconoSinConexion.setSize("40");
		
		linkReintentar.setOnAction(e ->
		{
			actualizarTablaProductos();
		});
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		iconoFlecha.setSize("40");
		
		hboxPlaceHolder.setSpacing(5);
		hboxPlaceHolder.setAlignment(Pos.CENTER);
		hboxPlaceHolder.getChildren().addAll(iconoFlecha, PLACE_HOLDER_SELECCIONAR_PRODUCTOS);
		
		tablaProductos.setPlaceholder(PLACE_HOLDER_SIN_PRODUCTOS_INDEFINIDOS);
		tablaProductos.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tablaProductos.getSelectionModel().selectedItemProperty().addListener((old, obs, newValue) ->
			mostrarUnidades(newValue));
		
		tablaProductosStock.setPlaceholder(hboxPlaceHolder);
		tablaProductosStock.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tablaProductosStock.getSelectionModel().selectedItemProperty().addListener((old, obs, newValue) ->
		mostrarUnidades2(newValue));
		
		columnaEspesor.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
		columnaAncho.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
		columnaLargo.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
		columnaCantidad.setCellValueFactory(cellData -> cellData.getValue().stockPropiedad().asObject());
		columnaPies.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject());
		
		columnaEspesor2.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
		columnaAncho2.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
		columnaLargo2.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
		columnaCantidad2.setCellValueFactory(cellData -> cellData.getValue().stockPropiedad().asObject());
		columnaPies2.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject());
		
		spUnidades.setEditable(false);
		spUnidades2.setEditable(false);
		
		lblTotal.setVisible(false);
		lblTotal2.setVisible(false);
		
		btnAgregar.disableProperty().bind(tablaProductos.getSelectionModel().selectedItemProperty().isNull());
		btnQuitarUnidades.disableProperty().bind(tablaProductosStock.getSelectionModel().selectedItemProperty().isNull());
		
		btnGuardarCambios.disableProperty().bind(Bindings.size(tablaProductosStock.getItems()).lessThan(1));
		
		 actualizarTablaProductos();
	}
	
	private void actualizarTablaProductos()
	{	
		ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
		
		ObservableList<ProductoDTO> listaProductosProduccion = FXCollections.observableArrayList();
		
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{	
				listaProductosProduccion.addAll(productoDao.obtenerProductosProduccion(-1));
				
				return null;
			}
		};
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea");
			
			tablaProductos.setItems(null);
			
			tablaProductos.setPlaceholder(vBoxSinConexion);
			
			lblTotal.setVisible(false);
			lblTotal2.setVisible(false);
		});
		
		tarea.setOnRunning(e ->
		{
			tablaProductos.setPlaceholder(spinner);
		});
		
		tarea.setOnSucceeded(e ->
		{
			tablaProductos.setItems(listaProductosProduccion);
			tablaProductos.setPlaceholder(PLACE_HOLDER_SIN_PRODUCTOS_INDEFINIDOS);
			tablaProductos.refresh();
			
			calcularTotales();
			
			lblTotal.setVisible(true);
			lblTotal2.setVisible(true);

		});
		
		new Thread(tarea).start();	
	}
	
	private void mostrarUnidades(ProductoDTO item)
	{
		if(item != null)
		{
			SpinnerValueFactory <Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, item.getStock(), 1);
			
			spUnidades.setValueFactory(valueFactory);
			
			//btnAgregarUnidades.disableProperty().bind(Bindings.size(spUnidades.valueProperty()));
		}
	}
	
	private void mostrarUnidades2(ProductoDTO item)
	{
		if(item != null)
		{
			SpinnerValueFactory <Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, item.getStock(), 1);
			
			spUnidades2.setValueFactory(valueFactory);
			
			//btnAgregarUnidades.disableProperty().bind(Bindings.size(spUnidades.valueProperty()));
		}
	}
	
	private void calcularTotales()
	{
		lblTotal.setText(UProducto.calcularTotalMaderas(tablaProductos.getItems()) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaProductos.getItems())));

		lblTotal2.setText(UProducto.calcularTotalMaderas(tablaProductosStock.getItems()) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaProductosStock.getItems())));
	}
	
	private boolean agregarProducto(ProductoDTO productoAgregado)
	{
		boolean retorno = false;
		
		// comprobamos si la tabla de productos a stock no esta vacia
		if(!tablaProductosStock.getItems().isEmpty())
		{
			// verificamos si el producto agregado existe en la tabla
			ProductoDTO productoExistente = UProducto.comprobrarExistenciaProducto(productoAgregado, tablaProductosStock.getItems());
		
			if(productoExistente != null)
			{
				// sumamos las unidades del producto
				productoExistente.setStock(productoExistente.getStock() + productoAgregado.getStock());
				productoExistente.setPies();
			}
			else
			{
				// agregamos el producto a la tabla
				tablaProductosStock.getItems().add(productoAgregado);
			}
		}
		else
		{
			// agregamos el producto a la tabla
			tablaProductosStock.getItems().add(productoAgregado);
		}
		
		retorno = true;
		
		return retorno;
	}
	
	private boolean quitarProducto(ProductoDTO productoQuitado)
	{
		boolean retorno = false;
		
		// comprobamos si la tabla de productos disponibles no esta vacia
		if(!tablaProductos.getItems().isEmpty())
		{
			// verificamos si el producto agregado existe en la tabla
			ProductoDTO productoExistente = UProducto.comprobrarExistenciaProducto(productoQuitado, tablaProductos.getItems());
		
			if(productoExistente != null)
			{
				// sumamos las unidades del producto
				productoExistente.setStock(productoExistente.getStock() + productoQuitado.getStock());
				productoExistente.setPies();
			}
			else
			{
				// agregamos el producto a la tabla
				tablaProductos.getItems().add(productoQuitado);
			}
		}
		else
		{
			// agregamos el producto a la tabla
			tablaProductos.getItems().add(productoQuitado);
		}
		
		retorno = true;
		
		return retorno;
	}
	
	@FXML private void actionAgregarUnidades()
	{
		ProductoDTO productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
		
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
			LOGGER.log(Level.DEBUG, "PRODUCTO AGREGADOO A STOCK --> {}", productoAgregado);
			
			// Se actualiza el producto seleccionado de la tabla stock
			productoSeleccionado.setStock(productoSeleccionado.getStock() - productoAgregado.getStock());
			productoSeleccionado.setPies();
			
			// Si el producto de la tabla stock queda sin unidades
			if(productoSeleccionado.getStock() == 0)
			{
				// Se quita el producto seleccionado de la tabla
				tablaProductos.getItems().remove(productoSeleccionado);
				tablaProductos.getSelectionModel().clearSelection();
			}
			else 
			{
				mostrarUnidades(productoSeleccionado);
				
				// actualizamos el spinner
				spUnidades.getValueFactory().setValue(productoAgregado.getStock() <= productoSeleccionado.getStock()
						? productoAgregado.getStock() : 1);
			}
			
			calcularTotales();
		}
	}
	
	@FXML private void actionQuitarUnidades()
	{
		ProductoDTO productoSeleccionado = tablaProductosStock.getSelectionModel().getSelectedItem();
		
		// Se crea un producto y se setea sus propiedades segun la cantidad seleccionada del detalle
		ProductoDTO productoQuitado = new ProductoDTO();
		productoQuitado.setIdProducto(productoSeleccionado.getIdProducto());
		productoQuitado.setEspesor(productoSeleccionado.getEspesor());
		productoQuitado.setAncho(productoSeleccionado.getAncho());
		productoQuitado.setLargo(productoSeleccionado.getLargo());
		productoQuitado.setStock(spUnidades2.getValue());
		productoQuitado.setPies();
		
		// Si el producto es agregado con exito
		if(quitarProducto(productoQuitado))
		{
			LOGGER.log(Level.ERROR, "P AGREGADOO STOCK --> {}", productoQuitado.getStock());
			
			// Se actualiza el producto seleccionado de la tabla stock
			productoSeleccionado.setStock(productoSeleccionado.getStock() - productoQuitado.getStock());
			productoSeleccionado.setPies();
			
			// Si el producto de la tabla stock queda sin unidades
			if(productoSeleccionado.getStock() == 0)
			{
				// Se quita el producto seleccionado de la tabla
				tablaProductosStock.getItems().remove(productoSeleccionado);
				tablaProductosStock.getSelectionModel().clearSelection();
			}
			else 
			{
				mostrarUnidades(productoSeleccionado);
				
				spUnidades2.getValueFactory().setValue(productoQuitado.getStock() <= productoSeleccionado.getStock()
						? productoQuitado.getStock() : 1);
			}
			
			calcularTotales();
		}
	}
	
	@FXML private void actionGuardarCambios()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		btnAceptar.setOnAction(e ->
		{
			boolean guardado = false;
			
			if(Conexion.setAutoCommit(false))
			{
				// tomamos los productos de la tabla stock
				ObservableList<ProductoDTO> listaProductosStock = FXCollections.observableArrayList(tablaProductosStock.getItems());
						
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				
				// recorremos la lista
				for(ProductoDTO p : listaProductosStock)
				{
					// consultamos el producto de la bd y lo tomamos
					ProductoDTO productoAModificar = productoDao.obtenerPorId(p.getIdProducto());
					
					// actualizamos el stock del producto consultado
					productoAModificar.setStock(productoAModificar.getStock() + p.getStock());
					
					if(!productoDao.actualizar(productoAModificar))
					{
						guardado = false;
						
						break;
					}
					else
					{
						guardado = true;
					}
				}
				
				if(guardado)
				{
					ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
					
					// volvemos a recorrer la lista de productos
					for(ProductoDTO p : listaProductosStock)
					{
						// se obtienen las producciones del producto en iteracion
						ObservableList<ProduccionDTO> listaProduccion = produccionDao.obtenerProduccionProductoPedido(p.getIdProducto(), -1);
						
						// se recorre la lista de producciones hasta llegar a la cantidad de stock del producto iterado
						for(int i = 0; i < p.getStock(); i++)
						{
							ProduccionDTO produccion = listaProduccion.get(i);
							produccion.setIdPedido(0);
							
							if(!produccionDao.actualizar(produccion))
							{
								guardado = false;
								break;
							}
						}
					}	
				}
				
				if(guardado)
				{
					// confirmamos la transaccion
					Conexion.commit();
					
					// limpiamos la tabla de productos a stock
					tablaProductosStock.getItems().clear();
					
					// limpiamos la seleccion de la tabla de productos
					if(tablaProductos.getSelectionModel().getSelectedItem() != null)
						tablaProductos.getSelectionModel().clearSelection();
					
					UNotificaciones.notificacion("Carga de stock", "Se ha actualizado el stock.");
				}
				else
				{
					UAlertas.mostrarAlerta(miAserraderoApp.getPanelRaiz(), miAserraderoApp.getPanelPrincipal(), "Carga de stock", "No se ha podido cargar el stock");
				}
			}
			else
			{
				UAlertas.mostrarAlerta(miAserraderoApp.getPanelRaiz(), miAserraderoApp.getPanelPrincipal(), "Carga de stock", "No se ha podido cargar el stock");
			}
		});
			
		UAlertas.confirmacion(miAserraderoApp.getPanelRaiz(), miAserraderoApp.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Carga de stock", "¿Desea confirmar la carga de stock?");
	}
}
