package aserradero.vista.produccion;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalTime;
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
import aserradero.modelo.DAO.mysql.ProduccionDAOMySQLImpl;
import aserradero.modelo.DTO.ProductoDTO;
import aserradero.modelo.clases.ProduccionAnio;
import aserradero.modelo.clases.ProduccionDia;
import aserradero.modelo.clases.ProduccionMes;
import aserradero.reportes.JasperReports;
import aserradero.util.UAlertas;
import aserradero.util.UFactoryDAO;
import aserradero.util.UFecha;
import aserradero.util.UProducto;
import aserradero.util.UTransiciones;
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
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class ProduccionControlador
{
	@FXML private TableView <ProduccionMes> tablaProduccionAnio;
	@FXML private TableView <ProduccionDia> tablaProduccionMes;
	
	@FXML private JFXComboBox<String> cmbFiltroAnio;
	@FXML private Hyperlink linkRefrescar;
	
	@FXML private TableColumn <ProduccionMes, String> columnaMes;
	@FXML private TableColumn <ProduccionMes, Year> columnaAnio;
	
	@FXML private TableColumn <ProduccionDia, LocalDate> columnaFecha;
	@FXML private TableColumn <ProduccionDia, String> columnaMaderas;
	@FXML private TableColumn <ProduccionDia, LocalTime> columnaDuracion;
	@FXML private TableColumn <ProduccionDia, Void> columnaVerDetalle;
	
	@FXML private JFXButton btnEstadisticaGeneral;
	@FXML private JFXButton btnEstadisticaAnual;
	
	@FXML private JFXButton btnReporteGeneral;
	@FXML private JFXButton btnReporteAnual;
	@FXML private JFXButton btnReporteMes;
	
	@FXML private Label lblMesYAnio;
	@FXML private Label lblTotalMes;
	
	@FXML private JFXToggleButton toggleClasificar;
	
	@FXML private SplitPane panelRaiz;
	@FXML private BorderPane panelDerecho;

	private static final Logger LOGGER = LogManager.getLogger(ProduccionControlador.class);
	
	private BorderPane ventanaSeleccionarElemento;
	
	private AserraderoApp miCoordinador;
	
	private JFXSpinner spinner = new JFXSpinner();
	
	private VBox vBoxSinConexion = new VBox();
	
	private HBox hBoxSinConexion = new HBox();

	private FontAwesomeIconView iconoSinConexion = new FontAwesomeIconView(FontAwesomeIcon.PLUG);
	
	private FontAwesomeIconView iconoSinConexion2 = new FontAwesomeIconView(FontAwesomeIcon.PLUG);

	private Hyperlink linkReintentar = new Hyperlink("Reintentar");
	
	private ProduccionDia produccionDiaSeleccinoado = null;
	
	private static final Label PLACE_HOLDER_SIN_RESULTADOS = new Label("Sin resultados");
	
	private static final Label PLACE_HOLDER_SIN_PRODUCCIONES = new Label("No se han registrado producciones");
	
	private ObservableList<String> listaAnios  = FXCollections.observableArrayList();
	
	private ObservableList<ProduccionAnio> listaProduccion = FXCollections.observableArrayList();
	
	Map<String, Object> datosEstadistica = new HashMap<>();
	Map<String, String> datosReporte = new HashMap<>();
	
	public ProduccionControlador()
	{
		// Por el momento, el código no contiene nada.
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
			
			actualizarTablaProduccion();
		});
		
		vBoxSinConexion.setSpacing(5);
		vBoxSinConexion.setAlignment(Pos.CENTER);
		vBoxSinConexion.getChildren().addAll(iconoSinConexion, new Label("No hay conexión con el servidor"), linkReintentar);
		
		hBoxSinConexion.setSpacing(5);
		hBoxSinConexion.setAlignment(Pos.CENTER);
		hBoxSinConexion.getChildren().add(iconoSinConexion2);
		hBoxSinConexion.getChildren().add(new Label("No hay conexión con el servidor"));
		
		ventanaSeleccionarElemento = (BorderPane) AserraderoApp.obtenerPanel(AserraderoApp.PANEL_SELECCIONAR_ELEMENTO_DETALLE);
			
	    columnaMes.setCellValueFactory(cellData -> cellData.getValue().mesPropiedad());
	    columnaAnio.setCellValueFactory(cellData -> cellData.getValue().anioPropiedad());
	    
		columnaFecha.setCellValueFactory(cellData -> cellData.getValue().fechaPropiedad());
		columnaMaderas.setCellValueFactory(cellData -> cellData.getValue().cantidadPiesPropiedad());
		columnaDuracion.setCellValueFactory(cellData -> cellData.getValue().duracionPropiedad());

		tablaProduccionAnio.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue)
				-> mostrarDetalleProduccionMes(newValue));
		
		toggleClasificar.setSelected(true);
		
		lblMesYAnio.setVisible(false);
		
		tablaProduccionMes.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) ->
		{
			if(newValue != null)
				LOGGER.log(Level.DEBUG, "Producion dia --> {}", newValue);
		});
	
		btnEstadisticaAnual.disableProperty().bind(cmbFiltroAnio.getSelectionModel().selectedIndexProperty().isEqualTo(0));

		
		btnEstadisticaAnual.disableProperty().bind(cmbFiltroAnio.getSelectionModel().selectedIndexProperty().isEqualTo(0));
		btnReporteMes.disableProperty().bind(tablaProduccionAnio.getSelectionModel().selectedItemProperty().isNull());
		
		mostrarDetalleProduccionMes(null);
		
		formatearCeldas();
	}
	
	private void formatearCeldas() 
	{
		// Custom rendering of the table cell.
				columnaFecha.setCellFactory(column ->
				{
					return new TableCell<ProduccionDia, LocalDate>()
					{
						@Override
						protected void updateItem(LocalDate item, boolean empty)
						{
							super.updateItem(item, empty);
							
							if (item == null)
							{
								setText(null);
								//setStyle("");
							}
							else
							{
								// Formato de fecha.
								setText(UFecha.formatearFecha(item, UFecha.EEEE_dd).toUpperCase());
							}
						}
					};
				});
				
				// añadimos boton a la columna
				Callback<TableColumn<ProduccionDia, Void>, TableCell<ProduccionDia, Void>> cellFactory = new Callback<TableColumn<ProduccionDia, Void>, TableCell<ProduccionDia, Void>>() 
				{
		            @Override
		            public TableCell<ProduccionDia, Void> call(final TableColumn<ProduccionDia, Void> param) 
		            {
		               return  new TableCell<ProduccionDia, Void>()
		                {
		                    private final JFXButton btn = new JFXButton("Ver detalle");
		                    {
		                    	Text icon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.FILE_TEXT_ALT, "20px");
		                    	btn.setGraphic(icon);
		                        btn.setOnAction((ActionEvent event) -> 
		                        {
		                           // Data data = getTableView().getItems().get(getIndex());
		                        	produccionDiaSeleccinoado = getTableView().getItems().get(getIndex());
		                            actionReporteDia();
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

		        columnaVerDetalle.setCellFactory(cellFactory);

		       // table.getColumns().add(colBtn);  
	}
	
	public void actualizarTablaProduccion()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				tablaProduccionAnio.refresh();
				
				Thread.sleep(listaProduccion == null || listaProduccion.isEmpty() ? 1000 : 250);

				ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
				
				listaProduccion = produccionDao.obtenerProduccionTotal();
				
				return null;
			}
		};

		tarea.setOnRunning(e ->
		{
			tablaProduccionAnio.setPlaceholder(spinner);
			
			linkRefrescar.disableProperty().bind(tarea.runningProperty());
			btnEstadisticaGeneral.disableProperty().bind(tarea.runningProperty());
			btnEstadisticaAnual.disableProperty().bind(tarea.runningProperty());
			btnReporteGeneral.disableProperty().bind(tarea.runningProperty());
			btnReporteAnual.disableProperty().bind(tarea.runningProperty());
	
		});

		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea (actualizarTablaProduccion())");
			btnReporteGeneral.disableProperty().unbind();
			btnEstadisticaGeneral.disableProperty().unbind();
			
			tablaProduccionAnio.setItems(null);
			tablaProduccionAnio.setPlaceholder(vBoxSinConexion);
		});

		tarea.setOnSucceeded(e ->
		{
			if(listaProduccion != null)
			{			
				ObservableList<ProduccionMes> listaProduccionMes = FXCollections.observableArrayList();
				
				for(ProduccionAnio p : listaProduccion)
				{
					listaProduccionMes.addAll(p.getProduccionesMes());
				}
				
				tablaProduccionAnio.setItems(listaProduccionMes);
				tablaProduccionAnio.setPlaceholder(!listaProduccion.isEmpty() ? PLACE_HOLDER_SIN_RESULTADOS : PLACE_HOLDER_SIN_PRODUCCIONES);
				
				btnEstadisticaGeneral.disableProperty().unbind();
				btnEstadisticaAnual.disableProperty().bind(cmbFiltroAnio.getSelectionModel().selectedIndexProperty().isEqualTo(0));
				btnReporteGeneral.disableProperty().unbind();
				btnEstadisticaGeneral.setDisable(listaProduccion == null || listaProduccion.isEmpty() ? true : false);
				btnReporteGeneral.setDisable(listaProduccion == null || listaProduccion.isEmpty() ? true : false);
				
				crearFiltroAnios();
				
				cmbFiltroAnio.getSelectionModel().select(0);
				cmbFiltroAnio.getSelectionModel().select(1);
				
			}
			else
			{
				tablaProduccionAnio.setItems(null);
				tablaProduccionAnio.setPlaceholder(vBoxSinConexion);
			}

		});

		new Thread(tarea).start();
	}
	
	public void actualizarComboAnios()
	{		
		Task<Integer> tarea = new Task<Integer>()
		{
			ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
			
			@Override
			protected Integer call() throws Exception 
			{
				listaAnios = produccionDao.obtenerTodosLosAnios();
				
				return null;
			}
			
		};
		
		tarea.setOnRunning(e ->
		{
			cmbFiltroAnio.setPlaceholder(spinner);
		});
		
		tarea.setOnFailed(e ->
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea");			
			cmbFiltroAnio.setItems(null);
			cmbFiltroAnio.setPlaceholder(hBoxSinConexion);
		});
		
		tarea.setOnSucceeded(e ->
		{
			if(listaAnios != null)
			{				
				cmbFiltroAnio.setItems(listaAnios);
				
				// seleccionamos el anio actual
				cmbFiltroAnio.getSelectionModel().select(cmbFiltroAnio.getItems().get(1));
			}
			else
			{
				cmbFiltroAnio.setPlaceholder(hBoxSinConexion);
			}
		});
		
		new Thread(tarea).start();
	}
	
	private void mostrarDetalleProduccionMes(ProduccionMes produccionMes)
	{
		// Se obtiene el valor de la posicion actual del divisor del panel (SplitPane)
		double[] dividerPosition = panelRaiz.getDividerPositions();
		
		if(produccionMes != null)
		{
			//lblMesYAnio.setText(produccionMesAnio.getMes() + " de " + produccionMesAnio.getAnio());
			
			tablaProduccionMes.setItems(produccionMes.getProduccionesDia());
			
			LOGGER.log(Level.DEBUG, "Producciones dia size --> {}", produccionMes.getProduccionesDia().size());
			
			lblTotalMes.setText(produccionMes.getTotalCantidadPies());
			
			// Si la bandera indica que el panel derecho no esta visible, se aplica el fadeIn al panel derecho
			if(!panelDerecho.isVisible())
			{
				panelRaiz.getItems().remove(1);
				
				panelRaiz.getItems().add(panelDerecho);
				
				panelDerecho.setVisible(true);
				
				UTransiciones.fadeIn(panelDerecho, 250);
			}
			
			LOGGER.log(Level.DEBUG, "Producciones mes --> {}", produccionMes);
		}
		else
		{
			
			panelDerecho.setVisible(false);
			
			panelRaiz.getItems().remove(1);
			
			panelRaiz.getItems().add(ventanaSeleccionarElemento);
			
			ventanaSeleccionarElemento.setVisible(true);
			
			UTransiciones.fadeIn(ventanaSeleccionarElemento, 250);
		}
		
		panelRaiz.setDividerPositions(dividerPosition);
	}
	
	private void crearFiltroAnios()
	{
		// 1. Envuelva el ObservableList en un FilteredList (inicialmente muestra todos los datosReporte).
        FilteredList<ProduccionMes> filteredData = new FilteredList<>(tablaProduccionAnio.getItems(), c -> true);

     // 2. Establezca el Predicado del filtro siempre que el filtro cambie.
        cmbFiltroAnio.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> 
        
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
                
                else return false; // No coincide.
            })
        );

        // 3. Envuelve la FilteredList en una SortedList. 
        SortedList<ProduccionMes> aniosOrdenados = new SortedList<>(filteredData);

        // 4. Enlazar el comparador SortedList al comparador TableView.
        aniosOrdenados.comparatorProperty().bind(tablaProduccionAnio.comparatorProperty());

        // 5. Agregar datosReporte ordenados (y filtrados) a la tabla.
        tablaProduccionAnio.setItems(aniosOrdenados);
	}
	
	@FXML private void actionActualizarTablaProduccion()
	{
		actualizarTablaProduccion();
		actualizarComboAnios();
	}
	
	@FXML private void actionEstadisticaGeneral()
	{
		System.out.println("LISTA --> " + listaProduccion);
		if(listaProduccion != null && !listaProduccion.isEmpty())
		{
			Task<Integer> tarea = new Task<Integer>()
			{
				@Override
				protected Integer call() throws Exception 
				{
					ObservableList<Number> datosX = null;
					
					ObservableList<String> datosY = null;
					
			        String[] anios = new String[listaProduccion.size()];			  
			        
			        // Conteo de total de pies por anio
			        double[] conteoAnio = new double[listaProduccion.size()];    
			        
			        int cont = 0;
			        
			        for (ProduccionAnio prodAnio : listaProduccion) 
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
					datosEstadistica.put("titulo", "Estadística de Producción General");
					
					for(int i = 0; i< 10; i++)
					{
						updateProgress(i, 10);
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
				
				miCoordinador.getEstadisticasControlador().setDatos((datosEstadistica));
				
				datosEstadistica.clear();
				
				miCoordinador.stopLoadingProgress();
			});
			
			tarea.setOnFailed(e -> 
			{
				LOGGER.log(Level.ERROR, "Fallo la tarea");
				datosEstadistica.clear();
				miCoordinador.stopLoadingProgress();
				UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");
				
			});
			
			new Thread(tarea).start();
		}
	}
	
	@FXML private void actionEstadisticaAnual()
	{
		
		if(listaProduccion != null && !listaProduccion.isEmpty())
		{
			Task<Integer> tarea = new Task<Integer>()
			{
				
				@Override
				protected Integer call() throws Exception 
				{
					ObservableList<ProduccionMes> produccionesMes = null;
					
					ProduccionAnio produccionAnio = null;
					
					ObservableList<Number> datosX = null;
					
					ObservableList<String> datosY = null;
					
					for(int i = 0; i< 10; i++)
					{
						updateProgress(i, 10);
						
						if(i == 0)
						{
							//Thread.sleep(2000);
							
							// Creamos un array con los meses en castellano
					        String[] months = DateFormatSymbols.getInstance(new Locale("es", "ES")).getMonths();
					       
					        // los pasamos a una ObservableList
					        datosY = FXCollections.observableArrayList((Arrays.asList(months)));
							
							//anio seleccionado
							int anioSeleccionado = Integer.parseInt(cmbFiltroAnio.getSelectionModel().getSelectedItem());				
							
							for(ProduccionAnio prodAnio : listaProduccion)
							{
								if(prodAnio.getAnio().getValue() == anioSeleccionado)
								{
									produccionAnio = prodAnio;
									break;
								}
							}
							
							produccionesMes = produccionAnio.getProduccionesMes();
							
							// Conteo de total de pies por mes
					        double[] conteoMes = new double[12];
					        
					        for (ProduccionMes prodMes : produccionesMes) 
					        {
					        	int mes = prodMes.getMes().getValue();
					            LOGGER.log(Level.DEBUG, "Mes {} = {}", mes, prodMes.getTotalPies());
					        	conteoMes[mes-1] = prodMes.getTotalPies();
					        	System.out.println("PASOO !");
					        }
							
							updateProgress(i+=5, 10);
							
							datosX = FXCollections.observableArrayList();
							
							for(int n = 0; n<conteoMes.length; n++)
							{
								datosX.add(conteoMes[n]);
							}
						
							System.out.println("dados x -> " + datosX);
							System.out.println("dados y -> " + datosY);
							
							// agregamos las producciones como parametro del mapa
							datosEstadistica.put("datosx", datosX);
							datosEstadistica.put("datosy", datosY);
							datosEstadistica.put("titulox", "Pie²");
							datosEstadistica.put("tituloy", "Meses");
							datosEstadistica.put("titulo", "Estadística de Producción: Año " + produccionAnio.getAnio().getValue() + "");
							
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
				LOGGER.log(Level.ERROR, "Fallo la tarea");
				datosEstadistica.clear();
				miCoordinador.stopLoadingProgress();
				UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");
				
			});
			
			new Thread(tarea).start();
		}
	}
	
	@FXML private void actionReporteGeneral()
	{
		if(listaProduccion != null && !listaProduccion.isEmpty())
		{
			Task<Integer> tarea = new Task<Integer>()
			{
				@Override
				protected Integer call() throws Exception 
				{
					ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);

					ObservableList<ProductoDTO> productos = FXCollections.observableArrayList();
					
					for(int i = 0; i< 10; i++)
					{
						updateProgress(i, 10);
						
						if(productos.isEmpty())
						{
							Thread.sleep(100);
							
							String fecha = UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy_ESPACIOS);
							
							productos.addAll(toggleClasificar.isSelected()
									? produccionDao.obtenerProductosTotalClasificados()
									: produccionDao.obtenerProductosTotal());
							
							String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
							String totalPies = String.valueOf(UProducto.calcularTotalPies(productos));
							
							updateProgress(i+=5, 10);
							
							datosReporte.put("titulo", "Producción General");
							datosReporte.put("descripcion", UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy_ESPACIOS));
						
							JRBeanCollectionDataSource productosJRBeans = new JRBeanCollectionDataSource(productos);
							
							Map <String, Object> parametros = new HashMap<>();
							
							parametros.put("fecha", fecha);
							parametros.put("productosDataSource", productosJRBeans);
							parametros.put("totalMaderas", totalMaderas);
							parametros.put("totalPies", totalPies);
							
							JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_produccion_total.jasper"), parametros);
							
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
	
	
	@FXML private void actionReporteAnual()
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
	
	
	@FXML private void actionReporteMes()
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
						
						// item seleccionado
						ProduccionMes produccion = tablaProduccionAnio.getSelectionModel().getSelectedItem();
						
						ProduccionDAOMySQLImpl produccionDao = (ProduccionDAOMySQLImpl) UFactoryDAO.getInstancia(UFactoryDAO.PRODUCCION);
						
						String fecha = produccion.getMes().getDisplayName(TextStyle.FULL, new Locale("es", "ES")).toUpperCase() + " del " + produccion.getAnio().toString();
												
						productos.addAll(toggleClasificar.isSelected() 
								? produccionDao.obtenerProductosDeMesClasificados(produccion.getMes(), produccion.getAnio())
								: produccionDao.obtenerProductosDeMes(produccion.getMes(), produccion.getAnio()));
						
						String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
						String totalPies = String.valueOf(UProducto.calcularTotalPies(productos));
						
						updateProgress(i+=5, 10);
						
						datosReporte.put("titulo", "Producción " + fecha);
						datosReporte.put("descripcion", "Producción del mes " + fecha);
						
						JRBeanCollectionDataSource productosJRBeans = new JRBeanCollectionDataSource(productos);
						
						Map <String, Object> parametros = new HashMap<>();
						
						parametros.put("productosDataSource", productosJRBeans);
						parametros.put("fecha", fecha);
						parametros.put("totalMaderas", totalMaderas);
						parametros.put("totalPies", totalPies);
							
						JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_produccion_mes.jasper"), parametros);	
						
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
	
	@FXML private void actionReporteDia()
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
						
						String fecha = UFecha.formatearFecha(produccionDiaSeleccinoado.getFecha(), UFecha.EEEE_dd_MMMM_yyyy_ESPACIOS).toUpperCase();
						
						datosReporte.put("titulo", "Producción del " + UFecha.formatearFecha(produccionDiaSeleccinoado.getFecha(), UFecha.dd_MM_yyyy_ESPACIOS));
						datosReporte.put("descripcion", "Producción del " + fecha); 
						
						productos.addAll(toggleClasificar.isSelected()
								? produccionDiaSeleccinoado.getProductosClasificados()
								: produccionDiaSeleccinoado.getProductosIndividuales());
						
						String totalMaderas = String.valueOf(UProducto.calcularTotalMaderas(productos));
						String totalPies = String.valueOf(UProducto.calcularTotalPies(productos));
						
						JRBeanCollectionDataSource productosJRBeans = new JRBeanCollectionDataSource(productos);
						
						updateProgress(i+=5, 10);
						
						Map <String, Object> parametros = new HashMap<>();
						
						parametros.put("productosDataSource", productosJRBeans);
						parametros.put("fecha", fecha);
						parametros.put("totalMaderas", totalMaderas);
						parametros.put("totalPies", totalPies);
						
						JasperReports.crearReporte(AserraderoMain.class.getResource("reportes/Reporte_produccion_dia.jasper"), parametros);
						
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
		tarea.setOnFailed(e -> 
		{
			LOGGER.log(Level.ERROR, "Fallo la tarea");
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
			UAlertas.mostrarAlerta(miCoordinador.getPanelRaiz(), miCoordinador.getPanelPrincipal(), "Reporte", "No se ha podido generar el reporte");

		});
		tarea.setOnSucceeded(e -> 
		{
			miCoordinador.cargarPanelReporte(JasperReports.mostrarReporte(), datosReporte, null);
			datosReporte.clear();
			miCoordinador.stopLoadingProgress();
		});
		
		new Thread(tarea).start();
	}
}
