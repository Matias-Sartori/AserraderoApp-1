package aserradero.vista.productosEliminados;

import java.time.LocalDateTime;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXSpinner;

import aserradero.controlador.AserraderoApp;
import aserradero.modelo.DAO.mysql.ProductoEliminadoDAOMySQLImpl;
import aserradero.modelo.DTO.ProductoEliminadoDTO;
import aserradero.util.UFactoryDAO;
import aserradero.util.UFecha;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class ProductosEliminadosControlador 
{
	@FXML private TableView<ProductoEliminadoDTO> tablaProductosEliminados;
	
	@FXML private TableColumn<ProductoEliminadoDTO, String> columnaEspesor;
	@FXML private TableColumn<ProductoEliminadoDTO, String> columnaAncho;
	@FXML private TableColumn<ProductoEliminadoDTO, Double> columnaLargo;
	@FXML private TableColumn<ProductoEliminadoDTO, Double> columnaPies;
	@FXML private TableColumn<ProductoEliminadoDTO, String> columnaMotivo;
	@FXML private TableColumn<ProductoEliminadoDTO, LocalDateTime> columnaFechaEliminacion;
	
	private static final Logger LOGGER = LogManager.getLogger(ProductosEliminadosControlador.class);
	
	private static final Label PLACE_HOLDER_SIN_ELIMINADOS = new Label("No hay productos eliminados");
	private static final Label PLACE_HOLDER_SIN_RESULTADOS = new Label("Sin resultados");
	
	private JFXSpinner spinner = new JFXSpinner();
	
	private VBox vBoxSinConexion = new VBox();
	
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	
	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	
	private ObservableList<ProductoEliminadoDTO> listaProductosEliminados = FXCollections.observableArrayList();
	
	private AserraderoApp miCoordinador;
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		miCoordinador = coordinador;
	}
	
	@FXML public void initialize()
	{
		iconoSinConexion.setSize("40");
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		linkReintentar.setOnAction(e ->
		{
			actualizarTablaProductosEliminados();
		});
		
		columnaEspesor.setCellValueFactory(cellData -> cellData.getValue().getProducto().espesorVistaPropiedad());
	    columnaAncho.setCellValueFactory(cellData -> cellData.getValue().getProducto().anchoVistaPropiedad());
	    columnaLargo.setCellValueFactory(cellData -> cellData.getValue().getProducto().largoPropiedad().asObject());
	    columnaPies.setCellValueFactory(cellData -> cellData.getValue().getProducto().piesPropiedad().asObject()); 
	    columnaMotivo.setCellValueFactory(cellData -> cellData.getValue().motivoPropiedad()); 
	    columnaFechaEliminacion.setCellValueFactory(cellData -> cellData.getValue().fechaEliminacionPropiedad()); 	
	    
	    formatearCeldas();
	    
	    actualizarTablaProductosEliminados();
	}
	
	private void actualizarTablaProductosEliminados()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{		
				
				Thread.sleep(listaProductosEliminados == null || listaProductosEliminados.isEmpty() ? 1000 : 250);
				
				ProductoEliminadoDAOMySQLImpl productosEliminadosDao = (ProductoEliminadoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTOS_ELIMINADOS);
				
				// obtenemos los pedidos de carga de stock
				listaProductosEliminados = productosEliminadosDao.obtenerTodos();
				
				return null;
			}
		};
		tarea.setOnRunning(e ->
		{
			tablaProductosEliminados.setPlaceholder(spinner);

		});
		
		tarea.setOnFailed(e ->
		{
			tablaProductosEliminados.setPlaceholder(vBoxSinConexion);
			LOGGER.log(Level.ERROR, "Fallo la tarea - actualizarTablaProductosEliminados");
		});
		
		tarea.setOnSucceeded(e ->
		{
			if(listaProductosEliminados != null)
			{
				tablaProductosEliminados.setPlaceholder(!listaProductosEliminados.isEmpty() ? PLACE_HOLDER_SIN_RESULTADOS : PLACE_HOLDER_SIN_ELIMINADOS);
				tablaProductosEliminados.setItems(listaProductosEliminados);
				
				//checkPendientes.setSelected(true);
			    //checkEnCurso.setSelected(true);
			    //checkTerminados.setSelected(true);
				
			    //aplicarFiltros();
			
			}
			else
			{
				//tablaDetalle.setItems(null);
				tablaProductosEliminados.setPlaceholder(vBoxSinConexion);
			}
			
		});
		
		new Thread(tarea).start();
	}
	
	private void formatearCeldas()
	{
		// formatear columna fecha
		//final String FONT_WEIGHT_BOLD = "-fx-font-weight: bold;" ;
		//final String FONT_STYLE_ITALIC = "-fx-font-style: italic;";
		
		// Custom rendering of the table cell.
		columnaFechaEliminacion.setCellFactory(column ->
		{
			return new TableCell<ProductoEliminadoDTO, LocalDateTime>()
			{
				@Override
				protected void updateItem(LocalDateTime item, boolean empty)
				{
					super.updateItem(item, empty);
					
					if (item == null)
					{
						setText(null);
					}
					else
					{
						// Formato de fecha.
						setText(UFecha.formatearFechaHora(item, UFecha.dd_MM_yyyy_HH_mm_ss));
					}
				}
			};
		});
	}
}
