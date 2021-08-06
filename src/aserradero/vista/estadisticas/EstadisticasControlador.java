package aserradero.vista.estadisticas;

import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.controlador.AserraderoApp;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class EstadisticasControlador 
{
	@FXML private BorderPane raiz;

	private BarChart <Number, String> barChart;
	private NumberAxis xAxis;
	private CategoryAxis yAxis;

	//private ObservableList<String> infoX = FXCollections.observableArrayList();
	//private ObservableList<String> infoY = FXCollections.observableArrayList();

	private static final Logger LOGGER = LogManager.getLogger(EstadisticasControlador.class);
	private AserraderoApp miCoordinador;
	
	public EstadisticasControlador()
	{
		//...
	}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	@FXML
    private void initialize() 
    {
        // instanciamos los ejes
        xAxis = new NumberAxis();
        yAxis = new CategoryAxis();
        
        // instanciamos el barChart
        barChart = new BarChart<>(xAxis, yAxis);
        
        barChart.setStyle("-fx-font-size: " + 12 + "px;");
        
        // colocamos el barchart al centro del panel raiz
        raiz.setCenter(barChart);
    }
	
	public void setDatos(Map <String, Object> datos)
	{
		
		ObservableList<Number> datosX = (ObservableList<Number>) datos.get("datosx");  
		
		ObservableList<String> datosY = (ObservableList<String>) datos.get("datosy");
		
		String tituloX = (String) datos.get("titulox");
		String tituloY = (String) datos.get("tituloy");
		
		String title = (String) datos.get("titulo");
		
		// asignamos los nombres de los meses como categoria del eje vertical
        yAxis.setCategories(datosY);
        xAxis.setLabel(tituloX);
        yAxis.setLabel(tituloY);

        XYChart.Series series = new XYChart.Series<>();
      
        // Creamos un objeto XYChart.Data para cada mes y lo agregamos a las series.
        for (int i = 0; i < datosX.size(); i++) 
        {
        	XYChart.Data<Number, String> data = new XYChart.Data<>((datosX.get(i)), datosY.get(i));
        	
        	data.nodeProperty().addListener(new ChangeListener<Node>()
        	{
				@Override
				public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) 
				{
					if(newValue != null)
					{
						displayLabelForData(data);
					}
				}
        		
        	});
        	
            series.getData().add(data);
        }

        barChart.setTitle(title);
        barChart.getData().add(series);
        barChart.getXAxis().setTickLabelRotation(0);
        
        
        LOGGER.log(Level.DEBUG, "Datos X --> {}", datosX);
        LOGGER.log(Level.DEBUG, "Datos Y --> {}", datosY);
	}
	
	 /** places a text label with a bar's value above a bar node for a given XYChart.Data */
	  private void displayLabelForData(XYChart.Data<Number, String> data) 
	  {
	     Node node = data.getNode();
	     Text dataText = new Text(data.getXValue().intValue() != 0 ? data.getXValue() + "" : null);
	     TextFlow textFlow = new TextFlow(dataText);
	     textFlow.setTextAlignment(TextAlignment.CENTER);
	    
	    node.parentProperty().addListener(new ChangeListener<Parent>() 
	    {
	      @Override public void changed(ObservableValue<? extends Parent> ov, Parent oldParent, Parent parent) 
	      {
	        Group parentGroup = (Group) parent;
	        parentGroup.getChildren().add(dataText);
	      }
	      
	      
	    });
	    
	  node.boundsInParentProperty().addListener(new ChangeListener<Bounds>() 
	  {
	      @Override public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds bounds) 
	      {
	        dataText.setLayoutX(
	          Math.round(
	            bounds.getMinX() + bounds.getWidth() / 2 - dataText.prefWidth(-1) / 2
	          )
	        );
	        
	        
	        dataText.setLayoutY(
	          Math.round(
	            bounds.getMinY() - dataText.prefHeight(-1) * 0.1
	          )
	        );
	      }
	    });
	  }
}
