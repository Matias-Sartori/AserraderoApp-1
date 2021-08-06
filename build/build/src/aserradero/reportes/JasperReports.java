package aserradero.reportes;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.util.UFecha;
import javafx.embed.swing.SwingNode;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

//Clase que exporta a PDF
// Clase para capturar excepciones o errores


public final class JasperReports
{
	private static final Logger LOGGER = LogManager.getLogger(JasperReports.class);
	private static JasperReport reporte; // El reporte
	private static JasperPrint reporteLleno; // Reporte ya lleno
	private static JasperViewer visor; // Clase que se ecngara de ejecutar query con una conex como paremetro, que llena y muestra el reporte

	public static String rutaReportePdfActual = "";
	public static String nombrePdfActual = "";
	
	private static File pdfTemporal;
	
	private JasperReports() {}
	
	public static void crearReporte(URL url, Map<String, Object> parametros)
	{
		try
		{
			if(reporte != null || reporteLleno != null)
				Thread.sleep(1000);
			
			LOGGER.log(Level.DEBUG, "Reporte URL = {}", url);
			
			// tomamos el reporte
			reporte = (JasperReport) JRLoader.loadObject(url);
			
			// llenamos el reporte con los parametros
			reporteLleno = JasperFillManager.fillReport(reporte, parametros, new JREmptyDataSource());
			
			LOGGER.log(Level.DEBUG, "Se ha creado el reporte exitosamente!", UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy));
		}
		catch(JRException | InterruptedException e)
		{
			LOGGER.log(Level.ERROR, "Ocurrió un error al crear reporte.\nError: " + e.getMessage(), UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy));
		
		}
	}
	
	/**
	 * Método que muestra el visor.
	 */
	public static void mostrarVisor()
	{
		visor = new JasperViewer(reporteLleno);
		visor.setVisible(true);
	}
	
	public static SwingNode mostrarReporte()
	{
		// creamos objeto SwingNode
		SwingNode swingNode = new SwingNode();

		// creamos JPanel distribuido de forma BorderLayout
		JPanel panel = new JPanel(new BorderLayout());
		//panel.setLayout(new BorderLayout());
		
		// creamos un JRViewer con el reporte lleno
		JRViewer jr = new JRViewer(reporteLleno);
		
		// removemos el toolbar
		//jr.remove(0);
		
		// ponemos el jr al centro del panel
		panel.add(jr, BorderLayout.CENTER);
		
		// seteamos el panel al swingnode
		swingNode.setContent(panel);

		return swingNode;
	}
	
	public static void limpiarReporte()
	{
		reporte = null;
	}
	
	public static void imprimirReporte()
	{
		try 
		{
			JasperPrintManager.printReport(reporteLleno, true);
		} 
		catch (JRException e) 
		{
			LOGGER.log(Level.ERROR, "Ocurrió un error al imprimir el reporte: {}", e.getMessage());
		}
	}
	
	public static void exportarAPDF(String fecha)
	{
		try
		{
			// asignamos el nombre del archivo pdf a exportar
			nombrePdfActual = reporteLleno.getName() + "_" + fecha + ".pdf";				

			// creamos un archivo temporal
			pdfTemporal = File.createTempFile(reporteLleno.getName() + "_" + fecha, ".pdf");
			
			// indicamos que se elimine al salir
			pdfTemporal.deleteOnExit();
			
			// asignamos la ruta completa donde sera exportado (ruta + \nombreArchivo)
			rutaReportePdfActual = pdfTemporal.getPath();
			
			LOGGER.log(Level.DEBUG, "Ruta de reporte actual --> {}", rutaReportePdfActual);
			
			// creamos el file ot
			FileOutputStream fos = new FileOutputStream(pdfTemporal);
			
			// exportamos el pdf
			JasperExportManager.exportReportToPdfStream(reporteLleno, fos);
			
			LOGGER.log(Level.DEBUG, "Se ha exportado el reporte a PDF [{}]", reporteLleno.getName());
			
		}
		catch(JRException | IOException e)
		{	
			LOGGER.log(Level.ERROR, "No se pudo exportar reporte.\nCausa: {}" , e.getMessage());
		}
	}
	
	public static void eliminarPdfTemporal()
	{
		try 
		{
			if(pdfTemporal != null && pdfTemporal.exists() && Files.deleteIfExists(pdfTemporal.toPath()))
			{
				LOGGER.log(Level.DEBUG, "Se elimino el pdf temporal debido al error ocurrido anteriormente");
			}
		} 
		catch (IOException e) 
		{
			LOGGER.log(Level.DEBUG, "No se pudo eliminar el reporte temporal! \nCausa: {}", e);
		}
	}
}
