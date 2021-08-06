package aserradero.vista.panelRaiz;

import java.util.Stack;

import aserradero.controlador.AserraderoApp;
import aserradero.util.UTransiciones;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PanelRaizControlador 
{
	@FXML private BorderPane panelRaiz;
	@FXML private BorderPane panelNorte;
	@FXML private MenuBar menu;
	@FXML private MenuItem menuItemPreferencias;
	@FXML private MenuItem menuItemPantallaCompleta;
	@FXML private MenuItem menuItemSalir;
	@FXML private Button btnSalir;
	@FXML private Label lblUsuario;
	@FXML private HBox hboxCentro;
	@FXML private Hyperlink hyperlinkAtras;
	@FXML private Hyperlink hyperlinkAdelante;
	@FXML private Hyperlink hyperlinkCerrarSesion;
	
	// Pila que almacena la paginacion de la iu
	private Stack<Node> pilaPanelesAnteriores = new Stack<>();
	
	private Stack<Node> pilaPanelesSiguientes = new Stack<>();
	
	private BooleanProperty paginasAnteriores = new SimpleBooleanProperty(true);
	private BooleanProperty paginasSiguientes = new SimpleBooleanProperty(true);
	
	/*private final long[] frameTimes = new long[100];
	private int frameTimeIndex = 0 ;
	private boolean arrayFilled = false ;
	*/
	private AserraderoApp miCoordinador;

	public PanelRaizControlador()
	{
		// Por el momento, el método no contiene nada.
	}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;	
	}
	
	public void setUsuario(String usuario)
	{
		lblUsuario.setText(usuario.equalsIgnoreCase("Administracion") ? "Administración" : "Producción");
		
		menuItemPreferencias.setVisible(usuario.equalsIgnoreCase("Administracion"));

	}
	
	public void initialize()
	{
		hyperlinkAtras.disableProperty().bind(paginasAnteriores);
		
		hyperlinkAdelante.disableProperty().bind(paginasSiguientes);
		
		// muestreo de frame rates time
		/*Label labelFrameRate = new Label();
        AnimationTimer frameRateMeter = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long oldFrameTime = frameTimes[frameTimeIndex] ;
                frameTimes[frameTimeIndex] = now ;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length ;
                if (frameTimeIndex == 0) {
                    arrayFilled = true ;
                }
                if (arrayFilled) {
                    long elapsedNanos = now - oldFrameTime ;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length ;
                    double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame ;
                    labelFrameRate.setText(String.format("Current frame rate: %.3f", frameRate));
                }
            }
        };

        frameRateMeter.start();
		
        hboxCentro.getChildren().add(labelFrameRate);*/
	}
	
	public void cargarNuevaPantalla(Node nodo)
	{	
		// preguntamos si no existen paneles anteriores
		//if(pilaPanelesAnteriores.isEmpty())
		//{
			// preguntamos si existen paneles siguientes
			if(!pilaPanelesSiguientes.isEmpty())
				// eliminamos los paneles siguientes
				pilaPanelesSiguientes.clear();
		//}
			
		// verifiamos si un nodo de los paneles siguientes es igual al nodo que se va a cargar
		Node nodoAQuitar = null;
		
		for(Node n : pilaPanelesSiguientes)
		{
			if(n.getUserData().equals(nodo.getUserData()))
			{
				nodoAQuitar = n;
				break;
			}
		}
		
		if(nodoAQuitar != null)
			pilaPanelesSiguientes.remove(nodoAQuitar);
		
		pilaPanelesAnteriores.push(panelRaiz.getCenter());
		
		paginasAnteriores.set(pilaPanelesAnteriores.isEmpty());
		paginasSiguientes.set(pilaPanelesSiguientes.isEmpty());
		
		panelRaiz.setCenter(nodo);
		
		nodo.setVisible(true);
		
		UTransiciones.fadeIn(nodo, 250);
	}
	
	public void cargarPantallaAnterior()
	{
		pilaPanelesSiguientes.push(panelRaiz.getCenter());
		
		Node nodo = pilaPanelesAnteriores.pop();
		
		panelRaiz.setCenter(nodo);
		
		paginasAnteriores.setValue(pilaPanelesAnteriores.isEmpty());
		paginasSiguientes.setValue(pilaPanelesSiguientes.isEmpty());
		
		UTransiciones.fadeIn(nodo, 250);
	}
	
	@FXML private void actionAtras()
	{
		cargarPantallaAnterior();
	}
	
	@FXML private void actionSiguiente()
	{
		pilaPanelesAnteriores.push(panelRaiz.getCenter());
		
		Node nodo = pilaPanelesSiguientes.pop();
				
		panelRaiz.setCenter(nodo);
		
		nodo.setVisible(true);
		
		UTransiciones.fadeIn(nodo, 250);
		
		paginasAnteriores.setValue(pilaPanelesAnteriores.isEmpty());
		paginasSiguientes.set(pilaPanelesSiguientes.isEmpty());
	}
	
	@FXML private void actionPreferencias()
	{
		miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_PREFERENCIAS);
	}
	
	@FXML private void actionPantallaCompleta()
	{
		miCoordinador.pantallaCompleta();
	}
	
	@FXML private void actionSalir()
	{
		miCoordinador.cerrarAplicacion();
	}
	
	@FXML private void actionCerrarSesion()
	{
		miCoordinador.cerrarPanelRaiz();
		
		if(miCoordinador.getLoginStage() == null)
			miCoordinador.iniciarLogin(new Stage());
		else
			miCoordinador.mostrarLogin();
		
		
	}
}   
