package aserradero.vista.empleado;

import java.time.LocalDate;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;

import aserradero.controlador.AserraderoApp;
import aserradero.modelo.DAO.mysql.PedidoDAOMySQLImpl;
import aserradero.modelo.DTO.PedidoDTO;
import aserradero.modelo.DTO.ProduccionDTO;
import aserradero.util.UFactoryDAO;
import aserradero.util.UFecha;
import aserradero.util.UPedido;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class EmpleadoControlador 
{	
	@FXML private Hyperlink hpActualizarPedidos;
	
	@FXML private TableView <PedidoDTO> tablaPedidos;
	
	@FXML private TableColumn <PedidoDTO, Integer> columnaNumeroPedido;
	@FXML private TableColumn <PedidoDTO, LocalDate> columnaFechaT;
	@FXML private TableColumn <PedidoDTO, String> columnaEstado;
	
	@FXML private JFXButton btnCargarPedido;
	
	private AserraderoApp miCoordinador;
	
	private static final Logger LOGGER = LogManager.getLogger(EmpleadoControlador.class);
	
	private ObservableList<PedidoDTO> listaPedidos = FXCollections.observableArrayList();
	
	private final JFXSpinner spinner = new JFXSpinner();

	private VBox vBoxSinConexion = new VBox();
	
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	
	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	
	private static final Label PLACE_HOLDER_SIN_RESULTADOS = new Label("Sin resultados");
	private static final Label PLACE_HOLDER_SIN_PEDIDOS = new Label("No hay pedidos pendientes");
	
	private PedidoDTO pedidoSeleccionado;
	
	public EmpleadoControlador()
	{
		// ...
	}
	
	public void setCoordinador(AserraderoApp c)
	{
		this.miCoordinador = c;
	}
	
	public PedidoDTO getPedidoSeleccionado()
	{
		return pedidoSeleccionado;
	}
	
	@FXML public void initialize()
	{
		iconoSinConexion.setSize("40");
		
		linkReintentar.setOnAction(e ->
		{
			actualizarTablaPedidos();
		});
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		columnaNumeroPedido.setCellValueFactory(cellData -> cellData.getValue().idPedidoPropiedad().asObject());
		columnaFechaT.setCellValueFactory(cellData -> cellData.getValue().fechaTomaPropiedad());
		columnaEstado.setCellValueFactory(cellData -> cellData.getValue().estadoPropiedad());
		
		btnCargarPedido.disableProperty().bind(tablaPedidos.getSelectionModel().selectedItemProperty().isNull());
		
		formatearCeldas();
		actualizarTablaPedidos();
	}
	
	private void actualizarTablaPedidos()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception
			{
				Thread.sleep(listaPedidos == null || listaPedidos.isEmpty() ? 1000 : 250);
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia("PEDIDO");
				listaPedidos = pedidoDao.obtenerPedidosEmpleado();
				
				return null;
			}
		};
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - actualizarTablaPedidos");
			tablaPedidos.setPlaceholder(vBoxSinConexion);
		});
		tarea.setOnRunning(e ->
		{
			tablaPedidos.setPlaceholder(spinner);
			//linkRefrescar.disableProperty().bind(tarea.runningProperty());
			//btnNuevo.disableProperty().bind(tarea.runningProperty());
		});
		
		tarea.setOnSucceeded(e ->
		{
			if(listaPedidos != null)
			{
				tablaPedidos.setItems(listaPedidos);
				tablaPedidos.setPlaceholder(listaPedidos.size() > 0 ? PLACE_HOLDER_SIN_RESULTADOS : PLACE_HOLDER_SIN_PEDIDOS);
			}
			else
			{
				tablaPedidos.setPlaceholder(vBoxSinConexion);
			}
		});
		new Thread(tarea).start();
	}
		
	private void formatearCeldas()
	{
		final String FONT_WEIGHT_BOLD = "-fx-font-weight: bold;" ;
		final String FONT_STYLE_ITALIC = "-fx-font-style: italic;";
		
		// Custom rendering of the table cell.
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
					
					setText(item);
					setStyle(FONT_WEIGHT_BOLD + FONT_STYLE_ITALIC);
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
								setTextFill(Color.valueOf("#FFA500"));
								break;
							case UPedido.EN_CURSO:
								setTextFill(Color.valueOf(Color.BLUE.toString()));
								break;
							case UPedido.LISTO:
								setTextFill(Color.valueOf("#0f9d58"));
								break;
							case UPedido.ENTREGADO:
								setTextFill(Color.valueOf(Color.BLACK.toString()));
								break;
							default:
								setText(null);
								setStyle("");
						}
					}
				}
			};
		});
		
		// añadimos boton a la columna
		Callback<TableColumn<ProduccionDTO, Void>, TableCell<ProduccionDTO, Void>> cellFactory = new Callback<TableColumn<ProduccionDTO, Void>, TableCell<ProduccionDTO, Void>>() 
		{
            @Override
            public TableCell<ProduccionDTO, Void> call(final TableColumn<ProduccionDTO, Void> param) 
            {
               return  new TableCell<ProduccionDTO, Void>()
                {
                    private final JFXButton btn = new JFXButton("Quitar");
                    {
                    	// creamos el icono
                    	Text icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.CLOSE, "20px");
                    	
                    	// seteamos el icono al boton
                    	btn.setGraphic(icon);
                    	
                    	// seteamos el accion listener del boton
                        btn.setOnAction((ActionEvent event) -> 
                        {
                           // obtenemos el item seleccionado
                        	ProduccionDTO produccionSeleccionada = getTableView().getItems().get(getIndex());
                        	
                        	// llamamos al metodo de quitar producto
                        	//quitarProducto(produccionSeleccionada);
                            
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

        //columnaQuitarElemento.setCellFactory(cellFactory);
	}
	
	private void actualizarEstadoPedido(boolean valor)
	{
		pedidoSeleccionado.setEstado((valor) ? UPedido.EN_CURSO : UPedido.PENDIENTE);
	}
	
	@FXML private void actionActualizarPedidos()
	{
		actualizarTablaPedidos();
	}
	
	@FXML private void actionCargarPedido()	
	{
		// seteamos el pedido seleccionado
		pedidoSeleccionado = tablaPedidos.getSelectionModel().getSelectedItem();
		
		// cargamos la nueva pantalla
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_CARGA_PEDIDO);
		
		// tomamos el controlador de la carga de pedido a traves de nuestro coordinador y cargamos los datos del pedido
		miCoordinador.getCargaPedidoControlador().cargarDetallePedido();
	}
}
