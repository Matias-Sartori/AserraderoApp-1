package aserradero.vista.cargaPedido;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Stack;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXToggleNode;

import aserradero.controlador.AserraderoApp;
import aserradero.modelo.DAO.mysql.DetallePedidoDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.PedidoDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.ProduccionDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.ProductoDAOMySQLImpl;
import aserradero.modelo.DTO.DetallePedidoDTO;
import aserradero.modelo.DTO.PedidoDTO;
import aserradero.modelo.DTO.ProduccionDTO;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.conexion.Conexion;
import aserradero.util.UAlertas;
import aserradero.util.UFactoryDAO;
import aserradero.util.UFecha;
import aserradero.util.UPedido;
import aserradero.util.UProducto;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class CargaPedidoControlador
{
	@FXML private SplitPane panelPrincipal;
	
	@FXML private TextField tfEspesor;
	@FXML private TextField tfAncho;
	@FXML private TextField tfLargo;
	

	@FXML private JFXToggleNode toggleEspesor075;
	@FXML private JFXToggleNode toggleEspesor1;
	@FXML private JFXToggleNode toggleEspesor105;
	@FXML private JFXToggleNode toggleEspesor2;
	@FXML private JFXToggleNode toggleEspesor3;
	
	@FXML private JFXCheckBox checkFijarEspesor;
	@FXML private JFXCheckBox checkFijarAncho;
	@FXML private JFXCheckBox checkFijarLargo;
	
	@FXML private JFXButton btnCargar;
	@FXML private JFXButton btnFinalizarCarga;
	@FXML private JFXButton btnCargarAleatorios;
	@FXML private Button btnQuitar;
	
	@FXML private HBox hboxCargar;
	
	@FXML private Hyperlink hpDeshacer;
	
	@FXML private Label lblNumeroPedido;
	@FXML private Label lblUltimaCarga;
	@FXML private Label lblTotal;
	
	@FXML private ObservableList <ProductoDTO> listaCargados = FXCollections.observableArrayList();
	
	@FXML private TableView <ProduccionDTO> tablaCargados;	
	
	@FXML private TableColumn <ProduccionDTO, String> columnaEspesor;
	@FXML private TableColumn <ProduccionDTO, String> columnaAncho;
	@FXML private TableColumn <ProduccionDTO, Double> columnaLargo;
	@FXML private TableColumn <ProduccionDTO, Double> columnaPies;
	@FXML private TableColumn <ProduccionDTO, LocalDate> columnaFecha;
	@FXML private TableColumn <ProduccionDTO, LocalTime> columnaHora;
		
	private static final Logger LOGGER = LogManager.getLogger(CargaPedidoControlador.class);

	private Stack<ProduccionDTO> pilaProduccionesCargadas;
	private Stack<ProduccionDTO> pilaProduccionesQuitadas;
	private ObservableList<ProductoDTO> productosCargados;
	
	private BooleanProperty deshacerProperty = new SimpleBooleanProperty(true);
	
	private BooleanProperty cargandoProperty = new SimpleBooleanProperty(true);
	
	private ToggleGroup grupoEspesores;
	
	private static final Label PLACE_HOLDER_SIN_PRODUCTOS = new Label("No hay productos cargados");
	
	private final JFXSpinner spinner = new JFXSpinner();
	
	private AserraderoApp miCoordinador;
	
	private PedidoDTO pedidoSeleccionado;
	
	private ObservableList<ProduccionDTO> listaProduccion = FXCollections.observableArrayList();
	
	//private boolean conProductos = false;
	
	private boolean cargado = false;
	
	private JFXSpinner spinnerCargando;
	
	public CargaPedidoControlador()
	{		
	}
	
	public void setCoordinador(AserraderoApp c)
	{
		this.miCoordinador = c;
	}

	@FXML public void initialize()
	{
		tfEspesor.setTooltip(new Tooltip("Pulgadas"));
		tfEspesor.setOnKeyReleased(e -> keyReleasedEnter(e));
		tfAncho.setOnKeyReleased(e -> keyReleasedEnter(e));	
		tfLargo.setOnKeyReleased(e -> keyReleasedEnter(e));
		tfEspesor.requestFocus();
		
		toggleEspesor075.setUserData("0.75");
		toggleEspesor1.setUserData("1");
		toggleEspesor105.setUserData("1.5");
		toggleEspesor2.setUserData("2");
		toggleEspesor3.setUserData("3");
	
		grupoEspesores = new ToggleGroup(); /** Deseleccionar grupo si el campo es tipeado */
		grupoEspesores.getToggles().addAll(toggleEspesor075, toggleEspesor1, toggleEspesor105, toggleEspesor2, toggleEspesor3);
				
		spinnerCargando = new JFXSpinner();
		spinnerCargando.setRadius(5);
		spinnerCargando.setVisible(false);
		
		hboxCargar.getChildren().add(spinnerCargando);
		
		
		
		columnaEspesor.setCellValueFactory(cellData -> cellData.getValue().getProducto().espesorVistaPropiedad());
		columnaAncho.setCellValueFactory(cellData -> cellData.getValue().getProducto().anchoVistaPropiedad());
		columnaLargo.setCellValueFactory(cellData -> cellData.getValue().getProducto().largoPropiedad().asObject());
		columnaPies.setCellValueFactory(cellData -> cellData.getValue().getProducto().piesPropiedad().asObject());
		columnaFecha.setCellValueFactory(cellData -> cellData.getValue().fechaCargaPropiedad());
		columnaHora.setCellValueFactory(cellData -> cellData.getValue().horaPropiedad());

		tablaCargados.setEditable(true);
		tablaCargados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tablaCargados.setPlaceholder(PLACE_HOLDER_SIN_PRODUCTOS);
		
		btnQuitar.visibleProperty().bind(tablaCargados.getSelectionModel().selectedItemProperty().isNotNull());
		
		pilaProduccionesCargadas = new Stack<>();
		pilaProduccionesQuitadas = new Stack<>();
		
		productosCargados = FXCollections.observableArrayList();
		productosCargados.addListener(new ListChangeListener<ProductoDTO>()
		{
			@Override
			public void onChanged(Change c) 
			{
				lblTotal.setText(productosCargados.size() + " | " + UProducto.calcularTotalPies(productosCargados));
			}
		});
		
		hpDeshacer.disableProperty().bind(deshacerProperty);
		
		btnFinalizarCarga.setVisible(false);
		
		validarBotonCargar();
		
		formatearCeldas();
		
		formatearCampos();
	}
		
	// METODOS
	
	public  void  cargarDetallePedido()
	{
		// tomamos el pedido seleccionado de la tabla
		pedidoSeleccionado = miCoordinador.getEmpleadoControlador().getPedidoSeleccionado();
	
		lblNumeroPedido.setText("Orden #" + pedidoSeleccionado.getIdPedido());
		
		actualizarTablaProductos();
	}
	
	private void actualizarTablaProductos()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception
			{
				//Thread.sleep(listaPedidos.isEmpty() ? 1000 : 250);
				
				ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
				
				listaProduccion = produccionDao.obtenerProduccionDePedido(pedidoSeleccionado.getIdPedido());			
				
				actualizarDeshacer();
				
				return null;
			}
		};
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea (actualizarTablaProductos)");
		});
		tarea.setOnRunning(e ->
		{
			tablaCargados.setPlaceholder(spinner);
		});
		
		tarea.setOnSucceeded(e ->
		{
			tablaCargados.setItems(listaProduccion);
			tablaCargados.setPlaceholder(PLACE_HOLDER_SIN_PRODUCTOS);
			
			if(listaProduccion != null && !listaProduccion.isEmpty())
			{
				lblUltimaCarga.setVisible(true);
				
				ProduccionDTO ultimoProducto = listaProduccion.get(listaProduccion.size() - 1);
				
				lblUltimaCarga.setText("Última carga: " + UFecha.formatearFecha(ultimoProducto.getFechaCarga(), UFecha.dd_MM_yyyy) + " - " + UFecha.formatearHora(ultimoProducto.getHoraCarga(), UFecha.HH_mm_a));
			}
			else
				lblUltimaCarga.setVisible(false);

			// añadimos los productos de la tabla a la lista
			for(ProduccionDTO prod : listaProduccion)
			{
				productosCargados.add(prod.getProducto());
			}
			
			lblTotal.setText(String.valueOf(UProducto.calcularTotalMaderas(productosCargados) + " | " + UProducto.calcularTotalPies(productosCargados)));
		});
		new Thread(tarea).start();
	}
	
	private void formatearCeldas()
	{
		columnaFecha.setCellFactory(column ->
		{
			return new TableCell<ProduccionDTO, LocalDate>()
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
		

		columnaHora.setCellFactory(column ->
		{
			return new TableCell<ProduccionDTO, LocalTime>()
			{
				@Override
				protected void updateItem(LocalTime item, boolean empty)
				{
					super.updateItem(item, empty);
					
					if (item == null)
					{
						setText(null);
					}
					else
					{
						// Formato de fecha.
						setText(UFecha.formatearHora(item, UFecha.HH_mm_a));
					}
				}
			};
		});
	}
	
	private void formatearCampos()
	{
		final String FORMATO = "\\d{0,2}([\\.]\\d{0,2})?";
		
		tfEspesor.textProperty().addListener(new ChangeListener <String> ()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				if(!newValue.matches(FORMATO) || newValue.startsWith("."))
				{
					tfEspesor.setText(oldValue);
				}
				else
					tfEspesor.setText(!tfEspesor.getText().isEmpty() ? tfEspesor.getText() : "");
			}				
		});
	
	tfAncho.textProperty().addListener(new ChangeListener <String> ()
	{
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
		{
			if(!newValue.matches(FORMATO) || newValue.startsWith("."))
			{
				tfAncho.setText(oldValue);
			}
			else
				tfAncho.setText(!tfAncho.getText().isEmpty() ? tfAncho.getText() : "");
			
		}				
	});
	
	tfLargo.textProperty().addListener(new ChangeListener <String> ()
	{
		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
		{
			if(!newValue.matches(FORMATO) || newValue.startsWith("."))
			{
				tfLargo.setText(oldValue);
			}
			else
				tfLargo.setText(!tfLargo.getText().isEmpty() ? tfLargo.getText() : "");
		}				
	});
	}
	
	public void limpiarCampos()
	{
		if(!checkFijarEspesor.isSelected())
		{
			tfEspesor.clear();
			
			if(grupoEspesores.getSelectedToggle() != null)
				grupoEspesores.getSelectedToggle().setSelected(false);
		}

		if(!checkFijarAncho.isSelected())
		{
			tfAncho.clear();
		}
		
		if(!checkFijarLargo.isSelected())
		{
			tfLargo.clear();
		}
		
		if(tfEspesor.getText().isEmpty()) 
			tfEspesor.requestFocus();
		
		else if(tfAncho.getText().isEmpty() && !tfEspesor.getText().isEmpty()) 
			tfAncho.requestFocus();
		
		else if(tfLargo.getText().isEmpty() && !tfEspesor.getText().isEmpty() && !tfAncho.getText().isEmpty()) 
			tfLargo.requestFocus();
	}
	
	private void limpiarTodosLosCampos()
	{
		tfEspesor.clear();
		
		tfAncho.clear();
		
		tfLargo.clear();
		
		checkFijarEspesor.setSelected(false);
		
		checkFijarAncho.setSelected(false);
		
		checkFijarLargo.setSelected(false);
		
		if(grupoEspesores.getSelectedToggle() != null)
			grupoEspesores.getSelectedToggle().setSelected(false);
		
		tfEspesor.requestFocus();
	}
	
	private void keyReleasedEnter(KeyEvent e)
	{		
		if(e.getCode() == KeyCode.ENTER)
		{


			if(!btnCargar.isDisabled())
			{
				actionCargarProducto();
			}
			else
			{
				if(tfEspesor.getText().trim().isEmpty())
					tfEspesor.requestFocus();
				else if(tfAncho.getText().trim().isEmpty())
					tfAncho.requestFocus();
				else if(tfLargo.getText().trim().isEmpty())
					tfLargo.requestFocus();
			}


		}
		else validarBotonCargar();
		
	}
	
	private void validarBotonCargar()
	{
		if(tfEspesor.getText().isEmpty()
				|| Double.parseDouble(tfEspesor.getText()) <= 0d
				|| tfAncho.getText().isEmpty()
				|| Double.parseDouble(tfAncho.getText()) <= 0d
				|| tfLargo.getText().isEmpty()
				|| Double.parseDouble(tfLargo.getText()) <= 0d)

			btnCargar.setDisable(true);
		else
			btnCargar.setDisable(false);
	}
	
	private void actualizarDeshacer() 
	{
		if(pilaProduccionesCargadas.isEmpty()) 
		{
			deshacerProperty.set(true);
		}
		else 
		{
			deshacerProperty.set(false);
		}
		
	}
	
	// METODOS FXML
	
	@FXML private void actionSeleccionEspesor()
	{
		Toggle toggleSeleccionado = grupoEspesores.getSelectedToggle();
		
		if(toggleSeleccionado != null)
		{
			tfEspesor.setText(toggleSeleccionado.getUserData().toString());
		}
		else tfEspesor.setText("");	
	}
	
	@FXML private void actionCargarProducto()
	{	
		ProductoDTO productoDto = new ProductoDTO();
		
		productoDto.setEspesor(Double.parseDouble(tfEspesor.getText().trim()));
		productoDto.setAncho(Double.parseDouble(tfAncho.getText().trim()));
		productoDto.setLargo(Double.parseDouble(tfLargo.getText().trim()));
		productoDto.setCarga(1);
		productoDto.setPies(UProducto.calcularPiesCuadrados(productoDto.getEspesor(), productoDto.getAncho(), productoDto.getLargo(), 1, UProducto.DOS_DECIMALES));
			
		cargarProducto(productoDto);
	}
	
	private void cargarProducto(ProductoDTO p)
	{
		// instanciamos un ProduccionDTO, seteandi el id del pedido presente, el producto cargado, fecha y hora actual
		ProduccionDTO produccionDto = new ProduccionDTO(pedidoSeleccionado.getIdPedido(), p, LocalDate.now(), LocalTime.now());
		
		DetallePedidoDTO detallePedido = new DetallePedidoDTO();
		
		detallePedido.setPedido(pedidoSeleccionado);
		
		if (Conexion.setAutoCommit(false))
		{
			ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia("PRODUCTO");
			
			ProductoDTO productoExistente = productoDao.obtener((p));
			
			Task<Integer> tarea = new Task<Integer>()
			{
				@Override
				protected Integer call() throws Exception 
				{
					
					// Se comprueba si el producto insertado ya existe en la base de datos
					if (productoExistente != null && productoExistente.getIdProducto() != 0)
					{			
						// preguntamos si el proposito del pedido es Carga de stock
						if(pedidoSeleccionado.getProposito().equalsIgnoreCase("CargaStock"))
						{
							// agregamos una unidad mas en carga del producto
							productoExistente.setCarga(productoExistente.getCarga() + 1);

							// actualizamos el producto del stock (bd)
							cargado = productoDao.actualizar(productoExistente);
						}
						else cargado = true;
					
						// seteamos el id al producto que agregamos (anteriormente no registrado)
						p.setIdProducto(productoExistente.getIdProducto());	
					}
					else if(productoExistente != null && productoExistente.getIdProducto() == 0)
					{
						// Si no existe, lo tenemos que dar de alta en la bd
						
						// si el proposito del pedido es venta, seteamos su carga en 0, sino, lo dejamos como estaba
						p.setCarga(pedidoSeleccionado.getProposito().equalsIgnoreCase("Venta") ? 0 : 1);
						
						cargado = productoDao.insertar(p);
					}
					
					DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
					
					// se pregunta si el producto y pedido existen en el detalle
					ProductoDTO productoExistenteDetalle = detallePedidoDao.obtenerProductoPedido(pedidoSeleccionado.getIdPedido(), p.getIdProducto());
					
					if(productoExistenteDetalle != null && productoExistenteDetalle.getIdProducto() != 0)
					{
						// se suma 1 unidad y se actualizan los pies del producto del detalle
						productoExistenteDetalle.setStock(productoExistenteDetalle.getStock() + 1);
						productoExistenteDetalle.setPies();
						
						// seteamos el/los productos al detalle creado
						detallePedido.setProductos(FXCollections.observableArrayList(productoExistenteDetalle));
						
						// actualizamos el detalle en la bd
						cargado = cargado && detallePedidoDao.actualizar(detallePedido);
					}
					else
					{
						// si no existe en el detalle, se lo inserta en la bd
						
						// seteamos sus unidades a 1, para poder insertar el detalle
						p.setStock(1);
						detallePedido.setProductos(FXCollections.observableArrayList(p));
						
						// insertamos el detalle en la bd
						cargado = cargado && detallePedidoDao.insertar(detallePedido);
					}
					
					// verificamos si es la primera vez que se agrega un producto al pedido
					if(tablaCargados.getItems().isEmpty())
					{
						PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
						actualizarEstadoPedido(true);
						cargado =  cargado && pedidoDao.actualizar(pedidoSeleccionado);
					}
					
					return null;
				}

			};
			
			tarea.setOnRunning(e ->
			{
				spinnerCargando.setVisible(true);
				
				btnCargar.setDisable(true);		
				
				btnQuitar.setDisable(true);	
				
			});
			
			tarea.setOnFailed(e ->
			{
				spinnerCargando.setVisible(false);
				
				btnCargar.setDisable(false);		
				
				btnQuitar.setDisable(false);	
				
				LOGGER.log(Level.ERROR, "Fallo la tarea (cargarProducto)");
			});
			
			tarea.setOnSucceeded(e ->
			{
				spinnerCargando.setVisible(false);
				
				btnCargar.setDisable(false);		
				
				btnQuitar.setDisable(false);	
				
				if(cargado)
				{
						
					ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia("PRODUCCION");
					
					// insertamos la produccion en la bd
					cargado = produccionDao.insertar(produccionDto);
					
					if(cargado && Conexion.commit())
					{
						// añadimos la produccion a la tabla
						tablaCargados.getItems().add(produccionDto);
			
						// agregamos la produccion a la pila de producciones cargadas
						pilaProduccionesCargadas.push(produccionDto);
						
						// agregamos el producto de la produccion a la lista de productos cargados
						productosCargados.add(p);
						
						actualizarDeshacer();
						
						limpiarCampos();
						
						validarBotonCargar();
						
						lblUltimaCarga.setText("Última carga: " + UFecha.formatearFecha(produccionDto.getFechaCarga(), UFecha.dd_MM_yyyy) + " - " + UFecha.formatearHora(produccionDto.getHoraCarga(), UFecha.HH_mm_a));
					}
					else 
					{
						actualizarEstadoPedido(!tablaCargados.getItems().isEmpty() );
						UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Carga de pedido", "No se ha podido cargar el producto");
					}
				}
				else
				{
					actualizarEstadoPedido(!tablaCargados.getItems().isEmpty());
					UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Carga de pedido", "No se ha podido cargar el producto");
				}
			});
			
			new Thread(tarea).start();
		}
		else
		{
			actualizarEstadoPedido(!tablaCargados.getItems().isEmpty());
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Carga de pedido", "No se ha podido cargar el producto");
		}
		
	}
	
	private void quitarProducto(ProduccionDTO produccion)
	{
		boolean quitado = false;
		
		if (Conexion.setAutoCommit(false))
		{
			DetallePedidoDTO detallePedido = new DetallePedidoDTO();
			
			detallePedido.setPedido(pedidoSeleccionado);
			
			// preguntamos el proposito es 'cargaStock'
			if(pedidoSeleccionado.getProposito().equalsIgnoreCase("CargaStock"))
			{
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia("PRODUCTO");
				
				// consultamos y tomamos el producto del stock
				ProductoDTO productoExistente = productoDao.obtener(produccion.getProducto());
				
				if(productoExistente != null && productoExistente.getIdProducto() != 0)
				{
					// descontamos una unidad en carga del producto
					productoExistente.setCarga(productoExistente.getCarga() - 1);
					
					// actualizamos el producto
					quitado = productoDao.actualizar(productoExistente);
				}
				else
					quitado = true;
			}
			else quitado = true;
			
			// actualizar el detalle
			DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
			
			// consultamos el producto del detalle
			ProductoDTO productoExistenteDetalle = detallePedidoDao.obtenerProductoPedido(pedidoSeleccionado.getIdPedido(), produccion.getProducto().getIdProducto());
			
			// descontamos una unidad del producto consultado
			productoExistenteDetalle.setStock(productoExistenteDetalle.getStock() - 1);
			productoExistenteDetalle.setPies();
			
			// seteamos el/los productos al detalle
			detallePedido.setProductos(FXCollections.observableArrayList(productoExistenteDetalle));
			
			if(productoExistenteDetalle.getStock() > 0)
			{
				// actualizamos el detalle de la bd
				quitado = quitado && detallePedidoDao.actualizar(detallePedido);
			}
			else
			{
				// eliminamos el detalle de la bd
				quitado = quitado && detallePedidoDao.eliminar(detallePedido);
			}
			
			if(quitado)
			{
				ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia("PRODUCCION");
				
				// eliminamos la produccion
				quitado = quitado && produccionDao.eliminar(produccion);
			}
			
			// verificamos si es el ultimo producto en la tabla
			if(tablaCargados.getItems().size() == 1)
			{
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
				actualizarEstadoPedido(false);
				quitado = quitado && pedidoDao.actualizar(pedidoSeleccionado);
			}
			
			if(quitado && Conexion.commit())
			{
				// removemos el producto de la tabla
				tablaCargados.getItems().remove(produccion);
				
				// removemos el producto de la pila de producciones cargadas
				pilaProduccionesCargadas.remove(produccion);
				
				// agregamos el producto a la pila de producciones quitadas
				pilaProduccionesQuitadas.push(produccion);
				
				// removemos el producto de la lista de productos cargados
				productosCargados.remove(produccion.getProducto());
				
				actualizarDeshacer();
				
				if(tablaCargados.getItems().isEmpty())
					lblUltimaCarga.setText("Última carga: " + UFecha.formatearFecha(produccion.getFechaCarga(), UFecha.dd_MM_yyyy) + " - " + UFecha.formatearHora(produccion.getHoraCarga(), UFecha.HH_mm_a));
				else
				{
					ProduccionDTO p = tablaCargados.getItems().get(tablaCargados.getItems().size()-1);
					lblUltimaCarga.setText("Última carga: " + UFecha.formatearFecha(p.getFechaCarga(), UFecha.dd_MM_yyyy) + " - " + UFecha.formatearHora(p.getHoraCarga(), UFecha.HH_mm_a));
				}
				
				
			}
			else
			{
				actualizarEstadoPedido(!tablaCargados.getItems().isEmpty());
				UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Carga de pedido", "No se ha podido quitar el producto");
			}
		}
		else
		{
			actualizarEstadoPedido(!tablaCargados.getItems().isEmpty());
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Carga de pedido", "No se ha podido quitar el producto");
		}
	}
	
	private void actualizarEstadoPedido(boolean valor)
	{
		pedidoSeleccionado.setEstado((valor) ? UPedido.EN_CURSO : UPedido.PENDIENTE);
	}
	
	@FXML private void actionLinkDeshacer()
	{
		// Realizar correctamente el deshacer y el rehacer sin errores.
	}
	
	@FXML private void actionQuitarProducto()  
	{
		quitarProducto(tablaCargados.getSelectionModel().getSelectedItem());
	}	
	
	@FXML private void actionDeshacer() 
	{
		quitarProducto(pilaProduccionesCargadas.lastElement());
	}
	
	/*@FXML private void actionRehacer()
	{
		// tomamos el ultimo elemento de la pila
		ProductoDTO p = pilaProduccionesQuitadas.lastElement().getProducto();
		
		cargarProducto(p);
		
		if(cargado)
			actualizarRehacerDeshacer();
	}*/
	
	/*@FXML private void actionQuitados()
	{
		for(ProduccionDTO p : pilaProduccionesQuitadas)
		{
			System.out.println(p);
		}
	}
	
	@FXML private void actionAgregados()
	{
		for(ProduccionDTO p : pilaProduccionesCargadas)
		{
			System.out.println(p);
		}
	}*/
	
	/*@FXML private void actionFinalizarCarga()
	{
		if(!pilaProduccionesQuitadas.isEmpty() || !pilaProduccionesCargadas.isEmpty())
		{
			JFXButton btnAceptar = new JFXButton("Aceptar");
			JFXButton btnCancelar = new JFXButton("Cancelar");
			
			btnAceptar.setOnAction(e ->
			{
				tablaCargados.getItems().clear();

				pilaProduccionesQuitadas.clear();

				pilaProduccionesCargadas.clear();

				actualizarRehacerDeshacer();

				limpiarTodosLosCampos();
			});
			
			UAlertas.mostrarInfo(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Finalización de carga", "¿Desea finalizar la carga?");
		}
	}*/
	
	/*@FXML private void actionCargarProductosAleatorios()
	{
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Entrada por teclado");
		dialog.setContentText("Ingresar cantidad de productos:");
		Optional<String> resultado = dialog.showAndWait();
		if(resultado.isPresent() && !resultado.toString().isEmpty())
		{
			Task<Integer> tarea = new Task<Integer>()
			{

				@Override
				protected Integer call() throws Exception 
				{
					try 
					{
						int cantidad = Integer.parseInt(resultado.get());
						for(int i = 0; i < cantidad; i++)
						{
							DecimalFormat df = new DecimalFormat("0.00");
							df.setRoundingMode(RoundingMode.DOWN);
							String cadena = df.format((Math.random()*((6.0 - 0.5) + 1)) + 0.5);
							double espesorAleatorio = df.parse(cadena).doubleValue();

							cadena = df.format((Math.random()*((15.0 - 0.5) + 1)) + 0.5);
							double anchoAleatorio = df.parse(cadena).doubleValue();
							
							cadena = df.format((Math.random()*((15.0 - 0.5) + 1)) + 0.5);
							double largoAleatorio = df.parse(cadena).doubleValue();
						
							ProductoDTO productoDto = new ProductoDTO();
							productoDto.setEspesor(espesorAleatorio);
							productoDto.setAncho(anchoAleatorio);
							productoDto.setLargo(largoAleatorio);
							productoDto.setStock(1);
							productoDto.setPies();
							productoDto.setReserva(0);
							
							cargarProducto(productoDto);
							
							updateProgress(i, cantidad);
						}	
					}
					catch (ParseException e) 
					{
						// TODO Auto-generated catch block
						LOGGER.log(Level.ERROR, "Ha ocurrido un error. Causa: {}", e);
						UAlertas.alerta("Ingreso de cantidad de productos", "¡Ha ingresado un formato incorrecto!");
					}
					catch(NumberFormatException e2)
					{
						// TODO Auto-generated catch block
						LOGGER.log(Level.ERROR, "Ha ocurrido un error. Causa: {}", e2);
						UAlertas.alerta("Ingreso de cantidad de productos", "¡Ha ingresado un formato incorrecto!");
					}
					
					return null;
				}
				
			};
			
			tarea.setOnFailed(e -> System.out.println("Fallo la carga de productos aleatorios"));
			
			tarea.setOnRunning(e ->
			{
				miCoordinador.startLoadingProgress(tarea, "Cargando productos aleatorios...");
			});
			
			tarea.setOnSucceeded(e ->
			{
				miCoordinador.stopLoadingProgress();
				UAlertas.informacion("Carga aleatoria", "Carga finalizada!");
			});
			
			new Thread(tarea).start();
			
		}
	}*/
}
