package aserradero;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.controlador.AserraderoApp;
import javafx.application.Application;
import javafx.stage.Stage;

public class AserraderoMain extends Application
{	
	private static final Logger LOGGER = LogManager.getLogger(AserraderoMain.class);
	
	 @Override
	public void start(Stage stage) throws Exception
	{	
		AserraderoApp aserraderoApp = new AserraderoApp();
		
		aserraderoApp.iniciarLogin(stage);
		
	}

	public static void main(String[] args)
	{
		LOGGER.log(Level.INFO, "AserraderoApp lanzado");
		
		launch(args);
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{				
				LOGGER.log(Level.INFO, "AserraderoApp finalizando");
			}
		});
	}
}
