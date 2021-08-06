package aserradero.vista.informesPedidos;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXToggleButton;

import aserradero.AserraderoMain;
import aserradero.controlador.AserraderoApp;
import aserradero.modelo.DAO.mysql.PedidoDAOMySQLImpl;
import aserradero.modelo.DAO.mysql.ProduccionDAOMySQLImpl;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.clases.ProduccionMes;
import aserradero.modelo.clases.VentasAnio;
import aserradero.modelo.clases.VentasMes;
import aserradero.reportes.JasperReports;
import aserradero.util.UAlertas;
import aserradero.util.UFactoryDAO;
import aserradero.util.UFecha;
import aserradero.util.UProducto;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class InformesPedidosControlador  
{
	@FXML private TableView <VentasMes> tablaVentasMes;
	
	@FXML private JFXComboBox<String> cmbFiltroAnio;
	@FXML private Hyperlink linkRefrescar;
	
	@FXML private TableColumn <VentasMes, String> columnaMes;
	@FXML private TableColumn <VentasMes, Year> columnaAnio;
	@FXML private TableColumn <VentasMes, String> columnaTotal;
	@FXML private TableColumn <VentasMes, Void> columnaVerMes;
	
	@FXML private JFXButton btnEstadisticaGeneral;
	@FXML private JFXButton btnEstadisticaAnual;
	
	@FXML private JFXButton btnReporteGeneral;
	
	@FXML private Label lblTotal;
	
	@FXML private JFXToggleButton toggleClasificar;
	
	private static final Logger LOGGER = LogManager.getLogger(InformesPedidosControlador.class);
	
	private AserraderoApp miCoordinador;
	
	private JFXSpinner spinner = new JFXSpinner();
	
	private VBox vBoxSinConexion = new VBox();
	
	private HBox hBoxSinConexion = new HBox();

	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	
	private FontAwesomeIconView iconoSinConexion2 = new FontAwesomeIconView(FontAwesomeIcon.PLUG);

	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	
	private static final Label PLACE_HOLDER_SIN_RESULTADOS = new Label("Sin resultados");
	
	private static final Label PLACE_HOLDER_SIN_PRODUCCIONES = new Label("No se han registrado producciones");
	
	private ObservableList<String> listaAnios  = FXCollections.observableArrayList();
	
	private ObservableList<VentasAnio> listaVentasAnio = FXCollections.observableArrayList();
	
	private  ObservableList<ProductoDTO> productosTotales = FXCollections.observableArrayList();
	
	private VentasMes ventasMesSeleccionada = null;
	
	private String totalMaderas;
	private String totalPies;
	
	Map<String, Object> datosEstadistica = new HashMap<>();
	Map<String, String> datosReporte = new HashMap<>();
	
	public InformesPedidosControlador() 
	{	
	}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	@FXML public void initialize()
	{
		spinner.setRadius(20.0);
		
		iconoSinConexion.setSize("40");
		iconoSinConexion2.setSize("10");
		
		linkReintentar.setOnAction(e ->
		{
			actualizarComboAnios();
			actualizarTablaVentas();
		});
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		hBoxSinConexion.setSpacing(5);
		hBoxSinConexion.setAlignment(Pos.CENTER);
		hBoxSinConexion.getChildren().add(iconoSinConexion2);
		hBoxSinConexion.getChildren().add(new Label("No hay conexión con el servidor"));
		
	    columnaMes.setCellValueFactory(cellData -> cellData.getValue().mesPropiedad());
	    columnaAnio.setCellValueFactory(cellData -> cellData.getValue().anioPropiedad());
	    columnaTotal.setCellValueFactory(cellData -> cellData.getValue().totalCantidadPiesPropiedad());
	  
		/*tablaVentasMes.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue)
				-> mostrarDetalleProduccionMes(newValue));*/
		
		toggleClasificar.setSelected(true);
	
		btnEstadisticaAnual.disableProperty().bind(cmbFiltroAnio.getSelectionModel().selectedIndexProperty().isEqualTo(0));

		tablaVentasMes.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue)
				-> LOGGER.log(Level.DEBUG, "tablaVentaMes selectedItem [{}] de [{}] --> {}, TOTAL = {}", newValue.getAnio(), newValue.getMes().getValue(), newValue.getProductosMes(), newValue.getTotalCantidadPies()));
		
		//btnReporteAnual.disableProperty().bind(cmbFiltroAnio.getSelectionModel().selectedItemProperty().isEqualTo("Todos los años"));
		//btnReporteMes.disableProperty().bind(tablaVentasMes.getSelectionModel().selectedItemProperty().isNull());
		
		formatearCeldas();
		
		lblTotal.setVisible(false);
	}
	
	private void formatearCeldas() 
	{
		// añadimos boton a la columna
		Callback<TableColumn<VentasMes, Void>, TableCell<VentasMes, Void>> cellFactory = new Callback<TableColumn<VentasMes, Void>, TableCell<VentasMes, Void>>() 
		{
			@Override
			public TableCell<VentasMes, Void> call(final TableColumn<VentasMes, Void> param) 
			{
				return  new TableCell<VentasMes, Void>()
				{
					private final JFXButton btn = new JFXButton("Ver mes");
					{
						Text icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.FILE_TEXT_ALT, "20px");
						btn.setGraphic(icon);
						btn.setOnAction((ActionEvent event) -> 
						{
							// tomamos el elemento de la celda
							ventasMesSeleccionada = getTableView().getItems().get(getIndex());
							reporteMes();
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

		columnaVerMes.setCellFactory(cellFactory);
	}
	
	public void actualizarTablaVentas()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				
				tablaVentasMes.refresh();
				
				Thread.sleep(listaVentasAnio == null || listaVentasAnio.isEmpty() ? 1000 : 250);

				PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
				
				listaVentasAnio = pedidoDao.obtenerPedidosVendidos();
				
				return null;
			}
		};

		tarea.setOnRunning(e ->
		{
			tablaVentasMes.setPlaceholder(spinner);
			
			linkRefrescar.disableProperty().bind(tarea.runningProperty());
			btnEstadisticaGeneral.disableProperty().bind(tarea.runningProperty());
			btnEstadisticaAnual.disableProperty().bind(tarea.runningProperty());
			btnReporteGeneral.disableProperty().bind(tarea.runningProperty());
	
		});

		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea (actualizarTablaPedidos)");
			btnReporteGeneral.disableProperty().unbind();
			btnEstadisticaGeneral.disableProperty().unbind();
			tablaVentasMes.setItems(null);
			tablaVentasMes.setPlaceholder(vBoxSinConexion);
			lblTotal.setVisible(false);
		});

		tarea.setOnSucceeded(e ->
		{
			if(listaVentasAnio != null)
			{			
				
				ObservableList<VentasMes> listaVentasMes = FXCollections.observableArrayList();
				
				for(VentasAnio p : listaVentasAnio)
				{
					listaVentasMes.addAll(p.getProduccionesMes());
				}
				
				tablaVentasMes.setItems(listaVentasMes);
				tablaVentasMes.setPlaceholder(!listaVentasAnio.isEmpty() ? PLACE_HOLDER_SIN_RESULTADOS : PLACE_HOLDER_SIN_PRODUCCIONES);
				
				btnEstadisticaGeneral.disableProperty().unbind();
				btnEstadisticaAnual.disableProperty().bind(cmbFiltroAnio.getSelectionModel().selectedIndexProperty().isEqualTo(0));
				btnReporteGeneral.disableProperty().unbind();
				btnEstadisticaGeneral.setDisable(listaVentasAnio == null || listaVentasAnio.isEmpty() ? true : false);
				btnReporteGeneral.setDisable(listaVentasAnio == null || listaVentasAnio.isEmpty() ? true : false);
				
				aplicarFiltros();
				
				cmbFiltroAnio.getSelectionModel().select(0);
				cmbFiltroAnio.getSelectionModel().select(1);
			}
			else
			{
				tablaVentasMes.setItems(null);
				tablaVentasMes.setPlaceholder(vBoxSinConexion);
				lblTotal.setVisible(false);
			}

		});

		new Thread(tarea).start();
	}
	
	public void actualizarComboAnios()
	{		
		Task<Integer> tarea = new Task<Integer>()
		{
			PedidoDAOMySQLImpl pedidoDao = (PedidoDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PEDIDO);
			
			@Override
			protected Integer call() throws Exception 
			{
				listaAnios = pedidoDao.obtenerTodosLosAnios();
				
				return null;
			}
			
		};
		
		tarea.setOnRunning(e ->
		{
			cmbFiltroAnio.setPlaceholder(spinner);
		});
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea (actualizarComboAnios)");			
			cmbFiltroAnio.setItems(null);
			cmbFiltroAnio.setPlaceholder(hBoxSinConexion);
		});
		
		tarea.setOnSucceeded(e ->
		{
			if(listaAnios != null)
			{				
				cmbFiltroAnio.setItems(listaAnios);
				
				// seleccionamos el anio actual
				cmbFiltroAnio.getSelectionModel().select(listaAnios.size() > 1 ? cmbFiltroAnio.getItems().get(1) : cmbFiltroAnio.getItems().get(0));
			}
			else
			{
				cmbFiltroAnio.setPlaceholder(hBoxSinConexion);
			}
		});
		
		new Thread(tarea).start();
	}
	
	private void aplicarFiltros()
	{
		// 1. Envuelva el ObservableList en un FilteredList (inicialmente muestra todos los datosReporte).
        FilteredList<VentasMes> filteredData = new FilteredList<>(tablaVentasMes.getItems(), c -> true);

        // 2. Establezca el Predicado del filtro siempre que el filtro cambie.
        cmbFiltroAnio.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
        {
            filteredData.setPredicate(anio ->
            {
                // Si el filtro de texto esta vacio, se muestra todos los listaClientes.
                if (newValue == null || newValue.isEmpty() || newValue.equalsIgnoreCase("Todos los años")) 
                {
                    return true;
                }

                // Comparar el nombre y el apellido de cada persona con texto de filtro.
                String lowerCaseFilter = newValue.toLowerCase();

                if (anio.getAnio().toString().contains(lowerCaseFilter))
                {
                    return true; // // El filtro coincide con el primer nombre.
                } 
                return false; // No coincide.
            });
            
            calcularTotal(tablaVentasMes.getItems());
       
        });
            
        // 3. Envuelve la FilteredList en una SortedList. 
        SortedList<VentasMes> aniosOrdenados = new SortedList<>(filteredData);

        // 4. Enlazar el comparador SortedList al comparador TableView.
        aniosOrdenados.comparatorProperty().bind(tablaVentasMes.comparatorProperty());

        // 5. Agregar datosReporte ordenados (y filtrados) a la tabla.
        tablaVentasMes.setItems(aniosOrdenados);        
        
        calcularTotal(tablaVentasMes.getItems());
       
	}
	
	private void calcularTotal(ObservableList<VentasMes> ventasMes)
	{
		if(ventasMes != null)
		{
			System.out.println("Calcular totales()");
			System.out.println("\tventasMes size = " + ventasMes.size());
			System.out.println("\tventasMes productos = " + ventasMes);
			System.out.println("\tproductosTotales size = " + productosTotales.size()); 
			System.out.println("\tproductosTotales = " + productosTotales); 
			
			ObservableList<ProductoDTO> productos = FXCollections.observableArrayList();
			productosTotales.clear();
			
			System.out.println("\tproductosTotales despues del clear size = " + productosTotales.size());
			System.out.println("\tproductosTotales despues del clear = " + productosTotales);
			
			// recorremos los items actuales de la tabla
			for(VentasMes ventaMes : ventasMes)
			{
				productos.addAll(ventaMes.getProductosMes());
			}
			
			productosTotales.addAll(UProducto.clonarProductos(productos));
			
			System.out.println("\tproductosTotales despues del addAll size = " + productosTotales.size());
			System.out.println("\tproductosTotales despues del addAll = " + productosTotales);
			
			// clasificamos los productos
			for(int i = 0; i < productosTotales.size(); i++)
			{
				ProductoDTO p = productosTotales.get(i);
				
				// recorremos los productos totales a partir del segundo lugar
				for(int i2 = i + 1; i2 < productosTotales.size(); i2++)
				{
					ProductoDTO prodSiguiente = productosTotales.get(i2);
					
					if(p.getIdProducto() == prodSiguiente.getIdProducto())
					{
						System.out.println("[" + i + "] -> " + p + " ES IGUAL A [" + i2 + "]" + " -> " + prodSiguiente);
						
						p.setStock(p.getStock() + prodSiguiente.getStock());
						
						productosTotales.remove(i2);
					}
				}
					
				p.setPies();
			}
			
			System.out.println("\tproductosTotales despues de clasificar size = " + productosTotales.size());
			System.out.println("\tproductosTotales despues de clasificar = " + productosTotales);
		
			totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productosTotales));
			
			totalPies = String.valueOf(UProducto.calcularTotalPies(productosTotales));
			
			lblTotal.setText(!productosTotales.isEmpty() ? totalMaderas + " | " + totalPies : "0 | 0");
			
			lblTotal.setVisible(true);
		}
		
		
	}
	
	private void reporteMes()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				ObservableList<ProductoDTO> productos = FXCollections.observableArrayList();
				
				for(int i = 0; i<10; i++)
				{
					updateProgress(i, 10);
					if(productos.isEmpty())
					{
						Thread.sleep(100);
						
						//ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
						
						String fecha = ventasMesSeleccionada.getMes().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase() + " del " + ventasMesSeleccionada.getAnio().toString();

						productos.addAll(ventasMesSeleccionada.getProductosMes());
						
						String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
						String totalPies = String.valueOf(UProducto.calcularTotalPies(productos));
						
						updateProgress(i+=5, 10);
						
						datosReporte.put("descripcion", fecha);
						
						JRBeanCollectionDataSource productosJRBeans = new JRBeanCollectionDataSource(productos);
						
						Map <String, Object> parametros = new HashMap<>();
						
						parametros.put("productosDataSource", productosJRBeans);
						parametros.put("fecha", fecha);
						parametros.put("totalMaderas", totalMaderas);
						parametros.put("totalPies", totalPies);
							
						JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_ventas_mes.jasper"), parametros);	
						
						updateProgress(i+=2, 10);

					}
					else 
					{
						Thread.sleep(25);
					}
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
			miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, null);
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
			
		});
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea");
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");
		});
		new Thread(tarea).start();
	}
	
	@FXML private void actionActualizarTablaVentas()
	{
		actualizarComboAnios();
		actualizarTablaVentas();
	}
	
	@FXML private void actionEstadisticaGeneral()
	{
		if(listaVentasAnio != null || listaVentasAnio.size() != 0)
		{
			Task<Integer> tarea = new Task<Integer>()
			{
				@Override
				protected Integer call() throws Exception 
				{
					ObservableList<Number> datosX = null;
					
					ObservableList<String> datosY = null;
					
			        String[] anios = new String[listaVentasAnio.size()];			  
			        
			        // Conteo de total de pies por anio
			        double[] conteoAnio = new double[listaVentasAnio.size()];   
					
					for(int i = 0; i< 10; i++)
					{
						updateProgress(i, 10);
						
						if(i == 1)
						{  
					        int cont = 0;
					        
					        for (VentasAnio prodAnio : listaVentasAnio) 
					        {
					        	int anio = prodAnio.getAnio().getValue();
					        	
					        	anios[cont] = anio +"";
					            
					            conteoAnio[cont] = prodAnio.getTotalPies();
					            
					            cont++;
					        }
					        
					        datosX = FXCollections.observableArrayList();
					        		
					        for(int n = 0; n<conteoAnio.length; n++)
					        {
					        	datosX.add(conteoAnio[n]);
					        }
					        
					        datosY = FXCollections.observableArrayList(Arrays.asList(anios));
						
					        // agregamos las producciones como parametro del mapa
							datosEstadistica.put("datosx", datosX);
							datosEstadistica.put("datosy", datosY);
							datosEstadistica.put("titulox", "Pie²");
							datosEstadistica.put("tituloy", "Años");
							datosEstadistica.put("titulo", "Estadística de Ventas General ");
							
							updateProgress(i+=2, 10);
						}
						else Thread.sleep(25);
					}
					
					
					
					return null;
				}
			};
			
			
			tarea.setOnRunning(e -> 
			{
				miCoordinador.startLoadingProgress(tarea, "Generando estadística...");
				
				btnEstadisticaGeneral.disableProperty().bind(tarea.runningProperty());
				btnEstadisticaAnual.disableProperty().bind(tarea.runningProperty());
				btnReporteGeneral.disableProperty().bind(tarea.runningProperty());
			});
			
			tarea.setOnSucceeded(e -> 
			{	
				miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_ESTADISTICAS);
				
				miCoordinador.getEstadisticasControlador().setDatos((datosEstadistica));
				
				datosEstadistica.clear();
				
				miCoordinador.stopLoadingProgress();
			});
			
			tarea.setOnFailed(e -> 
			{
				LOGGER.log(Level.ERROR, "Fallo la tarea (actionEstadisticaGeneral)");
				datosEstadistica.clear();
				miCoordinador.stopLoadingProgress();
				UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");
				
			});
			
			new Thread(tarea).start();
		}
	}
	
	@FXML private void actionEstadisticaAnual()
	{
		if(listaVentasAnio != null || listaVentasAnio.size() != 0)
		{
			Task<Integer> tarea = new Task<Integer>()
			{
				@Override
				protected Integer call() throws Exception 
				{
					
					ObservableList<ProduccionMes> produccionesMes = FXCollections.observableArrayList();
							
					ObservableList<Number> datosX = null;
					
					ObservableList<String> datosY = null;
					
					for(int i = 0; i< 10; i++)
					{
						updateProgress(i, 10);
						
						if(produccionesMes.isEmpty())
						{
							//Thread.sleep(2000);
							
							// Creamos un array con los meses en castellano
					        String[] months = DateFormatSymbols.getInstance(new Locale("es", "ES")).getMonths();
					       
					        // los pasamos a una ObservableList
					        datosY = FXCollections.observableArrayList((Arrays.asList(months)));
							
							// tomamos el anio seleccionado
							int anioSeleccionado = Integer.parseInt(cmbFiltroAnio.getSelectionModel().getSelectedItem());
							
							ObservableList<VentasMes> ventasMes = null;
							
							// recorremos la lista de las ventas de anios
							for(VentasAnio ventaAnio : listaVentasAnio)
							{
								if(ventaAnio.getAnio().getValue() == anioSeleccionado)
								{
									ventasMes = ventaAnio.getProduccionesMes();
									break;
								}
							}
							
							// Conteo de total de pies por mes
					        double[] conteoMes = new double[12];													    
					        
					        for (VentasMes ventaMes : ventasMes) 
					        {
					        	int mes = ventaMes.getMes().getValue();					           
					        	conteoMes[mes-1] = ventaMes.getTotalPies();
					        	
					        	 LOGGER.log(Level.DEBUG, "Mes {} {} = {}", mes, "("+months[mes-1]+")",ventaMes.getTotalPies());
					        }
							
							updateProgress(i+=5, 10);
							
							datosX = FXCollections.observableArrayList();
							
							for(int n = 0; n<conteoMes.length; n++)
							{
								datosX.add(conteoMes[n]);
							}
							
							// agregamos las producciones como parametro del mapa
							datosEstadistica.put("datosx", datosX);
							datosEstadistica.put("datosy", datosY);
							datosEstadistica.put("titulox", "Pie²");
							datosEstadistica.put("tituloy", "Meses");
							datosEstadistica.put("titulo", "Estadística de Ventas: Año " + anioSeleccionado + "");
							
							updateProgress(i+=2, 10);
						}
						else Thread.sleep(25);
					}
					
					return null;
				}
			};
			
			
			tarea.setOnRunning(e -> 
			{
				miCoordinador.startLoadingProgress(tarea, "Generando estadística...");
			});
			
			tarea.setOnSucceeded(e -> 
			{	
				miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_ESTADISTICAS);
				
				miCoordinador.getEstadisticasControlador().setDatos(datosEstadistica);
				
				datosEstadistica.clear();
				
				miCoordinador.stopLoadingProgress();
			});
			
			tarea.setOnFailed(e -> 
			{
				LOGGER.log(Level.ERROR, "Fallo la tarea (actionEstadisticaAnual)");
				datosEstadistica.clear();
				miCoordinador.stopLoadingProgress();
				UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Estadística", "No se ha podido generar la estadística");
				
			});
			
			new Thread(tarea).start();
		}
	}
	
	@FXML private void actionReporteVentas()
	{
		if(listaVentasAnio != null || listaVentasAnio.size() != 0)
		{
			Task<Integer> tarea = new Task<Integer>()
			{
				@Override
				protected Integer call() throws Exception 
				{			
					for(int i = 0; i< 10; i++)
					{
						updateProgress(i, 10);
						
						if(i == 1)
						{
							Thread.sleep(100);
							
							String fecha = null;
							
							if(cmbFiltroAnio.getSelectionModel().getSelectedItem().equalsIgnoreCase("Todos los años"))
							{
								fecha = "Generado el " + UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy);
							}
							else
							{
								fecha = "Año " + cmbFiltroAnio.getSelectionModel().getSelectedItem();
							}
							
							updateProgress(i+=5, 10);					
	
							datosReporte.put("titulo",  cmbFiltroAnio.getSelectionModel().getSelectedItem().equalsIgnoreCase("Todos los años") ? "Ventas General" : "Total Ventas");
							datosReporte.put("descripcion", UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy_ESPACIOS));
						
							JRBeanCollectionDataSource productosJRBeans = new JRBeanCollectionDataSource(productosTotales);
							
							Map <String, Object> parametros = new HashMap<>();
							
							parametros.put("titulo", datosReporte.get("titulo"));
							parametros.put("fecha", fecha);
							parametros.put("productosDataSource", productosJRBeans);
							parametros.put("totalMaderas", totalMaderas);
							parametros.put("totalPies", totalPies);
							
							JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_ventas_anio.jasper"), parametros);
							
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
				miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, null);
				JasperReports.limpiarReporte();
				datosReporte.clear();
				miCoordinador.stopLoadingProgress();
			});
			
			tarea.setOnFailed(e -> 
			{
				LOGGER.log(Level.ERROR, "Fallo la tarea");
				datosReporte.clear();
				miCoordinador.stopLoadingProgress();
				UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");

			});
			
			new Thread(tarea).start();
		}
		
		
	}
	
	
	private void actionReporteAnual()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
				ObservableList<ProductoDTO> productos = FXCollections.observableArrayList();
				
				for(int i = 0; i<10; i++)
				{
					updateProgress(i, 10);
					
					if(productos.isEmpty())
					{
						Thread.sleep(100);
						
						//anio seleccionado
						Year anioSeleccionado = Year.of(Integer.parseInt(cmbFiltroAnio.getSelectionModel().getSelectedItem()));
						
						String fecha = anioSeleccionado.toString();
						
						String columnaLargo = "Largo";
						
						datosReporte.put("titulo", "Producción año " + anioSeleccionado.toString());
						datosReporte.put("descripcion", "Total de maderas producidas");
						
						if(toggleClasificar.isSelected())
						{
							productos.addAll(produccionDao.obtenerProductosDeAnioClasificados(anioSeleccionado));
							columnaLargo = "Total largo";
						}
						else
						{
							productos.addAll(produccionDao.obtenerProductosDeAnio(anioSeleccionado));
							columnaLargo = "Largo";
						}

						String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
						String totalPies = String.valueOf(UProducto.calcularTotalPies(productos));
						
						JRBeanCollectionDataSource productosJRBeans = new JRBeanCollectionDataSource(productos);
						
						updateProgress(i+=5, 10);
						
						Map <String, Object> parametros = new HashMap<>();
						
						parametros.put("productosDataSource", productosJRBeans);
						parametros.put("fecha", fecha);
						parametros.put("totalMaderas", totalMaderas);
						parametros.put("totalPies", totalPies);
						parametros.put("columnaLargo", columnaLargo);
						
						JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_produccion_anio.jasper"), parametros);
					
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
			miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, null);
			miCoordinador.stopLoadingProgress();
			datosReporte.clear();
			
		});
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea");
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");
		});
		new Thread(tarea).start();

	}
	
	
	
}
