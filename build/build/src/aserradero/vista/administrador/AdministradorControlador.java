package aserradero.vista.administrador;

import com.jfoenix.controls.JFXTabPane;

import aserradero.controlador.AserraderoApp;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

/**
 * Clase que controla a las pestañas de la interfaz de usuario administrador
 * @author King
 *
 */
public class AdministradorControlador
{
	// Referencias a FXML
	@FXML private JFXTabPane panelAdministrador;
	
	@FXML private Tab tabMenuPrincipal;
	@FXML private Tab tabClientes;
	@FXML private Tab tabPedidos;
	@FXML private Tab tabStock;
	@FXML private Tab tabProduccion;
	
	private AserraderoApp miCoordinador;
	
	public AdministradorControlador()
	{
		// Constructor vacio...
	}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	@FXML public void initialize()
	{
		//...
		panelAdministrador.requestFocus();
		
		panelAdministrador.widthProperty().addListener((observable, oldValue, newValue) ->
	    {
	    	panelAdministrador.setTabMinWidth(panelAdministrador.getWidth() / panelAdministrador.getTabs().size());
	    	panelAdministrador.setTabMaxWidth(panelAdministrador.getWidth() / panelAdministrador.getTabs().size());      
	    });
		
		tabClientes.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				miCoordinador.getClientesControlador().actualizarTablaClientes();
			}
			else
			{
				
			}
		});
		
		tabPedidos.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				miCoordinador.getPedidosControlador().actualizarTablaPedidos();
			}
			else
			{
				
			}
		});
		
		tabStock.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				miCoordinador.getStockControlador().actualizarTablaStock();
			}
			else
			{
				
			}
		});
		
		tabProduccion.selectedProperty().addListener((observable, oldValue, newValue) ->
		{
			if(newValue)
			{
				miCoordinador.getProduccionControlador().actualizarTablaProduccion();
				
				miCoordinador.getProduccionControlador().actualizarComboAnios();
			}
			else
			{
				
			}
		});
		
		panelAdministrador.setStyle("-fx-open-tab-animation: NONE;");
		
		/*panelAdministrador.setCache(true);
		panelAdministrador.setCacheShape(true);
		panelAdministrador.setCacheHint(CacheHint.SPEED);*/
	}
	
	public void crearTabs()
	{
		tabMenuPrincipal.setContent(miCoordinador.crearPanel(AserraderoApp.PANEL_MENU_PRINCIPAL));
		tabClientes.setContent(miCoordinador.crearPanel(AserraderoApp.PANEL_CLIENTES));
		tabPedidos.setContent(miCoordinador.crearPanel(AserraderoApp.PANEL_PEDIDOS));
		tabStock.setContent(miCoordinador.crearPanel(AserraderoApp.PANEL_STOCK));
		tabProduccion.setContent(miCoordinador.crearPanel(AserraderoApp.PANEL_PRODUCCION));
	}
}
