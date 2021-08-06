package aserradero.vista.menuPrincipal;

import com.jfoenix.controls.JFXButton;

import aserradero.controlador.AserraderoApp;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;

public class MenuPrincipalControlador
{
	@FXML private BorderPane panelRaiz;
	@FXML private JFXButton btnNuevoPedido;
	@FXML private JFXButton btnNuevoCliente;
	@FXML private JFXButton btnPedidosStock;
	
	private AserraderoApp miCoordinador;
	
	public MenuPrincipalControlador() {}
	
	public void setCoordinador(AserraderoApp coordinador)
	{
		this.miCoordinador = coordinador;
	}
	
	@FXML public void initialize()
	{
		// ...
	}
	
	@FXML private void actionNuevoPedido()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				Thread.sleep(250);
				
				return null;
			}
			
		};
		tarea.setOnSucceeded(e -> 
		{
			//miCoordinador.cargarNuevaPantalla(AserraderoApp.panelNuevoPedido, AserraderoApp.PANEL_NUEVO_PEDIDO);
			miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_NUEVO_PEDIDO);
		});
		
		new Thread(tarea).start();
	}
	
	@FXML private void actionNuevoCliente()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				Thread.sleep(250);
				
				return null;
			}
			
		};
		tarea.setOnSucceeded(e -> 
		{
			miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_NUEVO_CLIENTE);
		});
		
		new Thread(tarea).start();
	}
	
	@FXML private void actionPedidosStock()
	{
		Task<Integer> tarea = new Task<Integer>()
		{
			@Override
			protected Integer call() throws Exception 
			{
				Thread.sleep(250);
				
				return null;
			}
			
		};
		tarea.setOnSucceeded(e -> 
		{
			miCoordinador.cargarNuevaPantalla(AserraderoApp.PANEL_PEDIDOS_STOCK);
		});
		
		new Thread(tarea).start();
	}
}
