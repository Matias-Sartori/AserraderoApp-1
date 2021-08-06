package aserradero.vista.pedidosStock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXToggleButton;

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
import aserradero.util.UNotificaciones;
import aserradero.util.UPedido;
import aserradero.util.UProducto;
import aserradero.util.UTransiciones;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class PedidosStockControlador 
{
	@FXML private Hyperlink linkRefrescar;
	
	@FXML private SplitPane splitCentral;
	@FXML private BorderPane panelDerecho;
	
	@FXML private TableView<PedidoDTO> tablaPedidos;
	
	@FXML private TableColumn<PedidoDTO, Integer> columnaIdPedido;
	@FXML private TableColumn<PedidoDTO, LocalDate> columnaFechaToma;
	@FXML private TableColumn<PedidoDTO, LocalDate> columnaFechaFinalizacion;
	@FXML private TableColumn<PedidoDTO, String> columnaEstado;
	
	@FXML private JFXCheckBox checkPendientes;
	@FXML private JFXCheckBox checkEnCurso;
	@FXML private JFXCheckBox checkTerminados;
	
	@FXML private JFXButton btnProductosLibres;
	@FXML private JFXButton btnNuevoPedidoStock;
	@FXML private JFXButton btnCancelarPedido;
	@FXML private JFXButton btnFinalizarPedido;
	
	@FXML private JFXToggleButton toggleClasificar;
	
	@FXML private Label lblTotal;
	
	@FXML private TableView<ProductoDTO> tablaDetalle;
	
	@FXML private TableColumn<ProductoDTO, String> columnaEspesor;
	@FXML private TableColumn<ProductoDTO, String> columnaAncho;
	@FXML private TableColumn<ProductoDTO, Double> columnaLargo;
	@FXML private TableColumn<ProductoDTO, Integer> columnaCantidad;
	@FXML private TableColumn<ProductoDTO, Double> columnaPies;
	
	private static final Logger LOGGER = LogManager.getLogger(PedidosStockControlador.class);
	
	private AserraderoApp miCoordinador;
	
	private BorderPane ventanaSeleccionarElemento;
	
	private ObservableList<PedidoDTO> listaPedidos = FXCollections.observableArrayList();
	
	private PedidoDTO pedidoSeleccionado;
	
	private DetallePedidoDTO detallePedidoSeleccionado;
	
	private static final Label PLACE_HOLDER_SIN_RESULTADOS = new Label("Sin resultados");
	
	private static final Label PLACE_HOLDER_SIN_PEDIDOS = new Label("No hay pedidos registrados");
	
	private static final Label PLACE_HOLDER_SIN_PRODUCTOS = new Label("No hay productos cargados");
	
	private static final double DIVIDER_POSITION_DEFECTO = 0.4658203125;
	
	private JFXSpinner spinner = new JFXSpinner();
	
	private VBox vBoxSinConexion = new VBox();
	
	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	
	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	
	Map<String, String> datosReporte = new HashMap<>();
	
	public PedidosStockControlador()
	{
		//...
	}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	public PedidoDTO getPedidoSeleccionado()	
	{
		return pedidoSeleccionado;
	}
	
	@FXML private void initialize()
	{
		columnaIdPedido.setCellValueFactory(cellData -> cellData.getValue().idPedidoPropiedad().asObject());
		columnaFechaToma.setCellValueFactory(cellData -> cellData.getValue().fechaTomaPropiedad());
		columnaFechaFinalizacion.setCellValueFactory(cellData -> cellData.getValue().fechaEntregaPropiedad());
		columnaEstado.setCellValueFactory(cellData -> cellData.getValue().estadoPropiedad());
		
		iconoSinConexion.setSize("40");
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		linkReintentar.setOnAction(e ->
		{
			actualizarTablaPedidos();
		});
		
		ventanaSeleccionarElemento = (BorderPane) AserraderoApp.obtenerPanel(AserraderoApp.PANEL_SELECCIONAR_ELEMENTO_DETALLE);
		
		checkPendientes.setUserData("Pendiente");
		checkEnCurso.setUserData("En curso");
		checkTerminados.setUserData("Terminado");
		
	    tablaPedidos.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) ->
	    {
			mostrarDetallePedido(newValue);
		
		});
	    
	    toggleClasificar.setSelected(true);
	    
	    toggleClasificar.selectedProperty().addListener((obs, old, newValue) -> 
			actualizarTablaDetalle(pedidoSeleccionado.getIdPedido()));
	    
	    columnaEspesor.setCellValueFactory(cellData -> cellData.getValue().espesorVistaPropiedad());
	    columnaAncho.setCellValueFactory(cellData -> cellData.getValue().anchoVistaPropiedad());
	    columnaLargo.setCellValueFactory(cellData -> cellData.getValue().largoPropiedad().asObject());
	    columnaCantidad.setCellValueFactory(cellData -> cellData.getValue().stockPropiedad().asObject());
	    columnaPies.setCellValueFactory(cellData -> cellData.getValue().piesPropiedad().asObject()); 
	    
	    tablaDetalle.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
	    {
	    	LOGGER.log(Level.DEBUG, "Item seleccionado ----> {}", newValue);
	    });
	    
	    lblTotal.setVisible(false);
	    
		formatearCeldas();
		
		actualizarTablaPedidos();
		
		mostrarDetallePedido(null);
	}
	
	private void formatearCeldas()
	{
		// formatear columna fecha
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
		
		columnaFechaFinalizacion.setCellFactory(column ->
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
							case UPedido.TERMINADO:
								setTextFill(Color.valueOf(Color.BLACK.toString()));
								setGraphic(fontCheck);
								break;
							default:
								setText(null);
								setGraphic(null);
								setStyle("");
						}
					}
				}
			};
		});
	}
	
	private void aplicarFiltros()
	{
		ObservableList<PedidoDTO> items = !tablaPedidos.getItems().isEmpty() ? tablaPedidos.getItems() : FXCollections.observableArrayList();
        
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
              checkTerminados.selectedProperty().addListener((observable, oldValue, newValue) -> 
              {
            	  listosFiltrados.setPredicate(pedido ->
                  {
                      // Si el filtro de texto esta vacio, se muestran todos los pedidos.
                	  System.out.println("Listos - newValue: " + newValue);
                      if (newValue) 
                      {
                          return true;
                      }

                      // Comparar el estado de cada pedido con texto de filtro.
                      String lowerCaseFiltro = checkTerminados.getUserData().toString();

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
	}

	
	private void actualizarTablaPedidos()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{		
				
				Thread.sleep(listaPedidos == null || listaPedidos.isEmpty() ? 1000 : 250);
				
				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
				
				// obtenemos los pedidos de carga de stock
				listaPedidos = pedidoDao.obtenerPedidosStock();
				
				return null;
			}
		};
		tarea.setOnRunning(e ->
		{
			tablaPedidos.setPlaceholder(spinner);
			linkRefrescar.disableProperty().bind(tarea.runningProperty());
			btnNuevoPedidoStock.disableProperty().bind(tarea.runningProperty());
		});
		
		tarea.setOnFailed(e ->
		{
			tablaDetalle.setItems(null);
			tablaPedidos.setPlaceholder(vBoxSinConexion);
			LOGGER.log(Level.ERROR, "Fallo la tarea - actualizarTablaPedidos");
		});
		
		tarea.setOnSucceeded(e ->
		{
			if(listaPedidos != null)
			{
				tablaPedidos.setPlaceholder(!listaPedidos.isEmpty() ? PLACE_HOLDER_SIN_RESULTADOS : PLACE_HOLDER_SIN_PEDIDOS);
				tablaPedidos.setItems(listaPedidos);
				
				checkPendientes.setSelected(true);
			    checkEnCurso.setSelected(true);
			    checkTerminados.setSelected(true);
				
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
	
	private void mostrarDetallePedido(PedidoDTO p)
	{
		// Se obtiene el valor de la posicion actual del divisor del splitpane
		double[] dividerPosition = splitCentral.getDividerPositions();
		
		if(p != null)
		{
			pedidoSeleccionado = p;
			
			// se actualiza la tabla de productos
			actualizarTablaDetalle(p.getIdPedido());
			
			btnCancelarPedido.disableProperty().unbind();
			
			btnCancelarPedido.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isEqualTo(UPedido.TERMINADO));
			
			btnFinalizarPedido.disableProperty().unbind();
			
			btnFinalizarPedido.disableProperty().bind(pedidoSeleccionado.estadoPropiedad().isEqualTo(UPedido.PENDIENTE).or(pedidoSeleccionado.estadoPropiedad().isEqualTo(UPedido.TERMINADO)));

			if(!panelDerecho.isVisible())
			{
				splitCentral.getItems().remove(1);
				
				splitCentral.getItems().add(panelDerecho);
				
				panelDerecho.setVisible(true);
				
				UTransiciones.fadeIn(panelDerecho, 250);
			}
		}
		else
		{	
			splitCentral.getItems().remove(1);
			
			panelDerecho.setVisible(false);			
			
			splitCentral.getItems().add(ventanaSeleccionarElemento);
			
			ventanaSeleccionarElemento.setVisible(true);
			
			UTransiciones.fadeIn(ventanaSeleccionarElemento, 250);
		}
		
		splitCentral.setDividerPositions(dividerPosition);
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

				// obtenemos los productos individuales, para poder actualizar correctamente el stock al finalizar el pedido
				detallePedidoSeleccionado = detallePedidoDao.obtenerPorId(idPedido);
				System.out.println("DETALLE PEDIDO SELECCUIOANDO PRODCUTOS __> " + detallePedidoSeleccionado.getProductos().size());
				System.out.println("ID PEDIDO __> " + idPedido);
				
				// elegimos como vamos a motrar los productos en la tabla, segun el switch
				productos.addAll(toggleClasificar.isSelected()
						? productoDao.obtenerProductosPedidoClasificado(idPedido)
						: detallePedidoSeleccionado.getProductos());
				
				return null;
			}
		};
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea - actualizarTablaDetalle");
			pedidoSeleccionado = null;
			detallePedidoSeleccionado = null;
			tablaDetalle.setItems(null);
			tablaDetalle.setPlaceholder(vBoxSinConexion);
			lblTotal.setVisible(false);
		});
		
		tarea.setOnRunning(e ->
		{
			tablaDetalle.setPlaceholder(spinner);
			//btnVerParaImprimir.disableProperty().bind(tablaDetalle.itemsProperty().isNull().or(tarea.runningProperty()));
		});
		
		tarea.setOnSucceeded(e ->
		{
			
			if(productos != null)
			{
				tablaDetalle.setItems(productos);
				tablaDetalle.setPlaceholder(PLACE_HOLDER_SIN_PRODUCTOS);
				
				columnaLargo.setText(toggleClasificar.isSelected() ? "Total largo" : "Largo");
				
				tablaDetalle.refresh();
				
				lblTotal.setText(UProducto.calcularTotalMaderas(productos) + " | " + String.valueOf(UProducto.calcularTotalPies(tablaDetalle.getItems())));
				
				lblTotal.setVisible(true);
			}
			else
			{
				tablaDetalle.setPlaceholder(vBoxSinConexion);
				
				lblTotal.setVisible(false);
			}
			

		});
		
		new Thread(tarea).start();
	}

	// FXML
	
	@FXML private void actionActualizarTablaPedidos()
	{
		actualizarTablaPedidos();
	}
	
	@FXML private void actionProductosLibres()
	{
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_PRODUCTOS_LIBRES);
	}
	
	@FXML private void actionNuevoPedidoStock()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		btnAceptar.setOnAction(e ->
		{
			Conexion.setAutoCommit(false);
			
			boolean pedidoInsertado = false;
			
			PedidoDTO nuevoPedido = new PedidoDTO();
			nuevoPedido.setFechaToma(LocalDate.now());
			nuevoPedido.setHoraToma(LocalTime.now());
			nuevoPedido.setFechaEntrega(LocalDate.of(9999, 9, 9));
			nuevoPedido.setHoraEntrega(LocalTime.of(00, 00, 00));
			nuevoPedido.setFechaModificacion(LocalDateTime.of(9999, 9, 9, 00, 00, 00));
			nuevoPedido.setProposito("cargaStock");
			nuevoPedido.setForma("INDEFINIDO");
			nuevoPedido.setEstado(UPedido.PENDIENTE);

			PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia("PEDIDO");
			pedidoInsertado = pedidoDao.insertar(nuevoPedido);

			if(pedidoInsertado && Conexion.commit())
			{
				UNotificaciones.notificacion("Alta de pedido", "Se ha dado de alta el pedido.");
				actualizarTablaPedidos();
			}
			else UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Alta de pedido de stock", "No se ha podido dar de alta el pedido. Comprobar conexión con el servidor");
		});
		
		UAlertas.confirmacion(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Alta de pedido", "¿Desea confirmar el alta de pedido?");
	}
	
	@FXML private void actionCancelarPedido()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		btnAceptar.setOnAction(e ->
		{
			ObservableList<ProductoDTO> productos = detallePedidoSeleccionado.getProductos();
			
			boolean pedidoEliminado = true;
			
			Conexion.setAutoCommit(false);

			ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
			
			// se obtienen las producciones de los productos del pedido
			ObservableList<ProduccionDTO> produccionesPedido = FXCollections.observableArrayList(produccionDao.obtenerProduccionDePedido(pedidoSeleccionado.getIdPedido()));
		
			// se actualiza la producción de esos productos, seteando su idPedido a -1. Esto significa que la produccion esta
			// indefinida a que pedido pertenece (productos libres)
			
			// recorremos la lista de producciones
			for(ProduccionDTO p : produccionesPedido)
			{
				// setemos el id del pedido de la produccion a -1
				p.setIdPedido(-1);
				
				// actualizamos la produccion en la bd
				if(produccionDao.actualizar(p))
				{
					pedidoEliminado = true;
				}
				else 
				{
					pedidoEliminado = false;
					break;
				}
			}

			// Se verifica si existen productos cargados en el pedido
			if(!productos.isEmpty())
			{
				System.out.println("ENTRA A ELIMINAR DETALLE -> " + pedidoEliminado);
				
				DetallePedidoDAOMySQLImpl detallePedidoDao = (DetallePedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.DETALLEP);
				
				// eliminamos el detalle del pedido
				pedidoEliminado = pedidoEliminado && detallePedidoDao.eliminar(detallePedidoSeleccionado);
				
				if(pedidoEliminado)
				{
					// actualizamos las unidades en carga de cada producto en la bd;
					ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
					for(ProductoDTO p : productos)
					{
						ProductoDTO productoAModificar = productoDao.obtenerPorId(p.getIdProducto());
						
						productoAModificar.setCarga(productoAModificar.getCarga() - p.getStock());
						
						if(productoDao.actualizar(productoAModificar)) 
						{
							pedidoEliminado = true;
						}
						else
						{
							pedidoEliminado = false;
							break;
						}
					}	
				}
			}

			PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
			
			// eliminamos el pedido de la BD
			pedidoEliminado = pedidoEliminado && pedidoDao.eliminar(pedidoSeleccionado);
			
			System.out.println("BANDERA PEDIDO ELIMINADO FINAL --> " + pedidoEliminado);

			if(pedidoEliminado && Conexion.commit())
			{
				UNotificaciones.notificacion("Baja de pedido.", "El pedido ha sido cancelado.");
				
				actualizarTablaPedidos();
				
			}
			else
			{
				UAlertas.alerta("Baja de pedido", "No se ha podido dar de baja al pedido");
			}
		});
		
		UAlertas.mostrarDialogoEntrada(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Eliminación de pedido", "¿Está seguro de cancelar el pedido?");
	}
	
	@FXML private void actionFinalizarPedido()
	{
		JFXButton btnAceptar = new JFXButton("Aceptar");
		JFXButton btnCancelar = new JFXButton("Cancelar");
		
		btnAceptar.setOnAction(e ->
		{
			ObservableList<ProductoDTO> productos = UProducto.clonarProductos(detallePedidoSeleccionado.getProductos());
			
			boolean pedidoFinalizado = false;

			// Se verifica si existen productos cargados en el pedido
			if(!productos.isEmpty() && Conexion.setAutoCommit(false))
			{		
				// Se actualiza el stock de cada producto de la bd;
				ProductoDAOMySQLImpl productoDao = (ProductoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCTO);
				for(ProductoDTO p : productos)
				{
					ProductoDTO productoAModificar = productoDao.obtenerPorId(p.getIdProducto());
					productoAModificar.setStock(productoAModificar.getStock() + p.getStock());
					productoAModificar.setCarga(productoAModificar.getCarga() - p.getStock());
					if(productoDao.actualizar(productoAModificar)) 
					{
						pedidoFinalizado = true;
					}
					else
					{
						pedidoFinalizado = false;
						break;
					}
				}	
			}

			PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
			
			// clonamos el pedido
			PedidoDTO pedidoAModificar = UPedido.clonarPedido(pedidoSeleccionado);
			pedidoAModificar.setEstado(UPedido.TERMINADO);
			pedidoAModificar.setFechaEntrega(LocalDate.now());
			pedidoAModificar.setHoraEntrega(LocalTime.now());
			
			// actualizamos el estado del pedido en la BD
			pedidoFinalizado = pedidoFinalizado && pedidoDao.actualizar(pedidoAModificar);

			if(pedidoFinalizado && Conexion.commit())
			{
				UNotificaciones.notificacion("Alta de pedido.", "Se ha confirmado la carga de stock.");
				
				//SOLUCIONAR AL QUITAR DE LA TABLA EL PEDIDO ELIMINADO!
				actualizarTablaPedidos();
				
				// se actualiza la tabla de stock de la pestaña 'stock'
				miCoordinador.getStockControlador().actualizarTablaStock();
			}
			else
			{
				UAlertas.alerta("Baja de pedido", "No se ha podido dar de alta al pedido");
			}
		});
		
		UAlertas.mostrarDialogoEntrada(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), Arrays.asList(btnAceptar, btnCancelar), "Finalización de pedido", "¿Desea confirmar la carga de stock?");
	}
}
