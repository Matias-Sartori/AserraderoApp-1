package aserradero.vista.reporte;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.controlador.AserraderoApp;
import aserradero.reportes.JasperReports;
import aserradero.util.UAlertas;
import aserradero.util.UCorreo;
import aserradero.util.UFecha;
import aserradero.util.UNotificaciones;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;

public class ReporteControlador 
{
	@FXML private Button btnEnviarCorreoEspecifico;
	@FXML private Button btnImprimirReporte;
	@FXML private Button btnEnviarCorreoCliente;
	@FXML private Label lblHeader;
	
	private static final Logger LOGGER = LogManager.getLogger(ReporteControlador.class);
	
	private boolean resultadoReporteEnviado;
	
	private AserraderoApp aserraderoApp;
	
	private String tituloReporte;
	
	private String descripcionReporte;
	
	private String correoCliente;
	
	public ReporteControlador(){}
	
	public void setVentanaRaiz(AserraderoApp raiz)
	{
		this.aserraderoApp = raiz;
	}
	
	public void setTituloReporte(String t)
	{
		this.tituloReporte = t;
	}
	
	public void setDescripcionReporte(String d)
	{
		this.descripcionReporte = d;
	}
	
	public void setCorreoCliente(String correo)
	{
		this.correoCliente = correo;
		
		if(correoCliente != null)
			btnEnviarCorreoCliente.setVisible(true);
		else
			btnEnviarCorreoCliente.setVisible(false);
	}
	
	public void initialize()
	{
		btnImprimirReporte.setVisible(false);
	}
	
	
	@FXML private void actionEnviarCorreo()
	{
		TextInputDialog dialog = new TextInputDialog();
		
		dialog.setTitle("Entrada por teclado");
		
		dialog.setHeaderText("Ingresar correo electrónico");
		
		dialog.setContentText("Dirección E-Mail: ");
		
		Optional<String> resultado = dialog.showAndWait();
		
		System.out.println("Resultado: " + resultado.toString());
		
		if(resultado.isPresent() && resultado.toString().contains("@"))
		{
			String correoIngresado = resultado.get().trim();
			
			Task<Integer> tarea = new Task<Integer>()
			{
				@Override
				protected Integer call() throws Exception 
				{
					boolean enviado = false;
					
					for(int i = 0; i< 10; i++)
					{
						updateProgress(i, 10);
						
						if(!enviado)
						{
							// creamos la fecha momentanea y la formateamos
							String fecha = UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy_GUION);
							
							// Exportamos el reporte a pdf
							JasperReports.exportarAPDF(fecha);
							
							updateProgress(i+=5, 10);
							
							// Enviamos el pdf exportado al correo destinatario
							resultadoReporteEnviado = UCorreo.enviarCorreoAdjunto(tituloReporte, descripcionReporte, correoIngresado, JasperReports.rutaReportePdfActual, JasperReports.nombrePdfActual);
						
							enviado = true;
							
							updateProgress(i+=2, 10);
						}
						else
							Thread.sleep(25);		
					}
					
					return null;
			
				}
			};
			tarea.setOnRunning(e -> 
			{
				aserraderoApp.startLoadingProgress(tarea, "Enviando reporte...");
			});
			tarea.setOnFailed(e ->
			{
				LOGGER.log(Level.ERROR, "Fallo la tarea - (actionEnviarCorreo)");
				
				aserraderoApp.stopLoadingProgress();
				
				UAlertas.mostrarAlerta(aserraderoApp.getPanelRaiz(), aserraderoApp.getPanelPrincipal(), "Envío a E-Mail", "No se pudo enviar el reporte.");
				
				JasperReports.eliminarPdfTemporal();
			});
			tarea.setOnSucceeded(e -> 
			{
				aserraderoApp.stopLoadingProgress();
				
				if(resultadoReporteEnviado)
					UNotificaciones.notificacion("Envío de reporte", "El reporte ha sido enviado");
				else
				{
					UAlertas.mostrarAlerta(aserraderoApp.getPanelRaiz(), aserraderoApp.getPanelPrincipal(), "Envío a E-Mail", "No se pudo enviar el reporte.");
					JasperReports.eliminarPdfTemporal();
				}
					
			});
			
			new Thread(tarea).start();
		}
	}
	
	@FXML private void actionEnviarCorreoCliente()
	{
		if(!correoCliente.isEmpty() && correoCliente.contains("@"))
		{
			Task<Integer> tarea = new Task<Integer>()
			{
				@Override
				protected Integer call() throws Exception 
				{
					boolean enviado = false;
					
					for(int i = 0; i< 10; i++)
					{
						updateProgress(i, 10);
						
						if(!enviado)
						{
							// creamos la fecha momentanea y la formateamos
							String fecha = UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy_GUION);
							
							// Exportamos el reporte a pdf
							JasperReports.exportarAPDF(fecha);
							
							updateProgress(i+=5, 10);
							
							// Enviamos el pdf exportado al correo destinatario
							resultadoReporteEnviado = UCorreo.enviarCorreoAdjunto(tituloReporte, descripcionReporte, correoCliente, JasperReports.rutaReportePdfActual, JasperReports.nombrePdfActual);
						
							enviado = true;
							
							updateProgress(i+=2, 10);
						}
						else
							Thread.sleep(25);		
					}
					
					return null;
				}
			};
			tarea.setOnRunning(e -> 
			{
				aserraderoApp.startLoadingProgress(tarea, "Enviando reporte...");
			});
			tarea.setOnFailed(e ->
			{
				LOGGER.log(Level.ERROR, "Fallo la tarea - (actionEnviarCorreoCliente)");
				
				aserraderoApp.stopLoadingProgress();
				
				UAlertas.mostrarAlerta(aserraderoApp.getPanelRaiz(), aserraderoApp.getPanelPrincipal(), "Envío a E-Mail", "No se pudo enviar el reporte.");
				
				JasperReports.eliminarPdfTemporal();
			});
			tarea.setOnSucceeded(e -> 
			{
				aserraderoApp.stopLoadingProgress();
				
				if(resultadoReporteEnviado)
				{
					UNotificaciones.notificacion("Envío de reporte", "El reporte ha sido enviado");
					
					// por las dudas, intentamos eliminar el pdf temporal, en caso de q no haya sido
					JasperReports.eliminarPdfTemporal();
				}
					
				else
				{
					UAlertas.mostrarAlerta(aserraderoApp.getPanelRaiz(), aserraderoApp.getPanelPrincipal(), "Envío a E-Mail", "No se pudo enviar el reporte.");
					JasperReports.eliminarPdfTemporal();
				}
					
			});
			
			new Thread(tarea).start();
		}
		else
			UAlertas.mostrarInfo(aserraderoApp.getPanelRaiz(), aserraderoApp.getPanelPrincipal(), "Envío a E-Mail", "El cliente no posee correo electrónico.");
	}
	
	
	@FXML private void actionImprimirReporte()
	{
		JasperReports.imprimirReporte();
	}
}
