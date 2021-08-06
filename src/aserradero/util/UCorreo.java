package aserradero.util;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import aserradero.AserraderoMain;


public final class UCorreo
{
	public static final String PEDIDO_ENTREGADO = "¡Se ha confirmado la entrega del pedido!";
	
	private static final Logger LOGGER = LogManager.getLogger(UCorreo.class);
	
	public static void pruebaEnviarCorreo()
	{
		try
		{
			// Propiedades para la conexion a la cuenta ----------------------Email
			Properties props = new Properties();
			
			// indicamos el host
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			
			// indicamos que usamos tls
			props.setProperty("mail.smtp.starttls.enable", "true");
			
			// indicamos el puerto
			props.setProperty("mail.smtp.port", "587");
			
			// incicamos si va a ser autentificacion, directamente al sv de gmail
			props.setProperty("mail.smtp.auth", "true");
			
			// Preparamos la sesion, enviando las propiedades
			Session session = Session.getDefaultInstance(props);	
			
			// variables con los datosReporte del correo
			String correoRemitente = "a.donjose.cr@gmail.com";
			String passRemitente = "donjose3333";
			String correoReceptor = "masartori85@gmail.com";
			String parametroAsunto = "Prueba de correo desde Java";
			String parametroMensaje = "Te he enviado un correo desde Java";
			
			// Mensaje en formato HTML
			//String mensajeHtml = "Te he enviado un correo desde <b>Java!</b>";
			// establecemos el mensaje con el tipo de codificacion y formato
			// message.setText(mensaje, "ISO-8859-1", "html");
			
			// Construimos el mensaje
			MimeMessage message = new MimeMessage(session);
			
			// indicamos quien va a mandar el correo
			message.setFrom(new InternetAddress(correoRemitente));
			
			// indicamos el receptor principal (to)
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoReceptor));
			
			// tambien podemos indicar a quien enviar una copia (cc)
			// message.addRecipient(Message.RecipientType.CC, new InternetAddress(correoReceptor));
			
			// o enviar una copia sin que los demas la vean (bcc)
			// message.addRecipient(Message.RecipientType.BCC, new InternetAddress(correoReceptor));
			
			// establecemos el asunto
			message.setSubject(parametroAsunto);
			
			// establecemos el mensaje
			message.setText(parametroMensaje);
			
			// creamos un obj tipo Transport, indicando el tipo de transporte (smtp)
			Transport t = session.getTransport("smtp");
			
			// establecemos la conexion del correo remitente
			t.connect(correoRemitente, passRemitente);
			
			// enviamos el mensaje con el destinatario principal
			t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			
			// terminamos la conexion
			t.close();
			
			LOGGER.log(Level.INFO, "Se ha enviado el correo exitosamente!", UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy));
		}
		
		catch (AddressException e) 
		{
			e.printStackTrace();

		}
		catch (MessagingException e)
		{
			LOGGER.log(Level.ERROR, "Ha orurrido un error al enviar el correo.\nCausa {}", e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static boolean enviarCorreoAdjunto(String asunto, String mensaje, String receptor, String rutaArchivo, String nombreArchivo)
	{
		// ANTES, DEBEMOS ESTABLECER UNA CONFIGURACION EN EL CORREO REMITENTE.
		
		// 1 - Ir a 'Mi cuenta'
		// 2 - Seleccionar la opcion 'Inicio de sesion y seguridad'
		// 3 - Activar la opcion 'Permitir el acceso de aplicaciones menos seguras'
		
		// ESTO PERMITIRA QUE CUALQUIER APLICACION PUEDA ENVIAR CORREOS DESDE EL CORREO REMITENTE
		
		boolean ret = false;
		
		try
		{
			// Propiedades para la conexion a la cuenta ----------------------Email
			Properties props = new Properties();
			
			// indicamos el host
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			
			// indicamos que usamos tls
			props.setProperty("mail.smtp.starttls.enable", "true");
			
			// indicamos el puerto
			props.setProperty("mail.smtp.port", "587");
			
			// incicamos si va a ser autentificacion, directamente al sv de gmail
			props.setProperty("mail.smtp.auth", "true");
			
			// Preparamos la sesion, enviando las propiedades
			Session session = Session.getDefaultInstance(props);	
			
			// variables con los datosReporte del correo
			String correoRemitente = "a.donjose.cr@gmail.com";
			String passRemitente = "donjose3333";
			String correoReceptor = receptor;
			String parametroAsunto = asunto;
			String parametroMensaje = mensaje;
			
			// creamos un bodyPart
			BodyPart texto = new MimeBodyPart();
			
			// le damos el contenido
			texto.setText(parametroMensaje);
			
			// creamos un bodyPart para el dato adjunto
			BodyPart adjunto = new MimeBodyPart();
			
			System.out.println("UCorreo ruta archivo ---->> " + rutaArchivo);
			
			// tomamos el archivo pdf
			FileDataSource archivo = new FileDataSource(rutaArchivo);
			
			// asignamos el archivo
			adjunto.setDataHandler(new DataHandler(archivo));
			
			// le damos nombre al archivo con su extencion(opcional)
			adjunto.setFileName(nombreArchivo);
			
			// unimos los bodyParts
			MimeMultipart multiParte = new MimeMultipart();
			multiParte.addBodyPart(texto);
			multiParte.addBodyPart(adjunto);
			
			// Construimos el mensaje
			MimeMessage message = new MimeMessage(session);
			
			// indicamos quien va a mandar el correo
			message.setFrom(new InternetAddress(correoRemitente));
			
			// indicamos el receptor principal (to)
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoReceptor));
			
			// tambien podemos indicar a quien enviar una copia (cc)
			// message.addRecipient(Message.RecipientType.CC, new InternetAddress(correoReceptor));
			
			// o enviar una copia sin que los demas la vean (bcc)
			// message.addRecipient(Message.RecipientType.BCC, new InternetAddress(correoReceptor));
			
			// establecemos el asunto
			message.setSubject(parametroAsunto);
			
			// establecemos el mensaje, pasandole la multiparte
			message.setContent(multiParte);
			
			// creamos un obj tipo Transport, indicando el tipo de transporte (smtp)
			Transport t = session.getTransport("smtp");
			
			// establecemos la conexion del correo remitente
			t.connect(correoRemitente, passRemitente);
			
			// enviamos el mensaje con el destinatario principal
			t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			
			// terminamos la conexion
			t.close();

			ret = true;
			
			LOGGER.log(Level.INFO, "Se ha enviado el archivo [{}] desde [{}] a [{}]", nombreArchivo, correoRemitente,receptor);
			
		}
		catch (AddressException e) 
		{
			e.printStackTrace();
			
			LOGGER.log(Level.ERROR, "Error al enviar el correo.\nCausa: {}", e.getMessage());
			
			return false;

		}
		catch (MessagingException e)
		{
			LOGGER.log(Level.ERROR, "Error al enviar el correo.\nCausa: {}", e.getMessage());
			
			return false;
		}
		catch(Exception e)
		{
			LOGGER.log(Level.ERROR, "Error al enviar el correo.\nCausa: {}", e.getMessage());

			return false;
		}
		
		return ret;
	}
	
	public static boolean enviarCorreoAdjuntoMultiple(String asunto, String msje, String rutaArchivo, String nombreArchivo, String [] correos) throws IOException
	{
		boolean correosEnviados = false;
		
		try
		{
			// Propiedades para la conexion a la cuenta ----------------------Email
			Properties props = new Properties();
			
			// indicamos el host
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			
			// indicamos que usamos tls
			props.setProperty("mail.smtp.starttls.enable", "true");
			
			// indicamos el puerto
			props.setProperty("mail.smtp.port", "587");
			
			// incicamos si va a ser autentificacion, directamente al sv de gmail
			props.setProperty("mail.smtp.auth", "true");
			
			// Preparamos la sesion, enviando las propiedades
			Session session = Session.getDefaultInstance(props);	
			
			// variables con los datosReporte del correo
			String correoRemitente = "a.donjose.cr@gmail.com";
			String passRemitente = "donjose3333";
			String parametroAsunto = asunto;
			String parametroMensaje = msje;
			
			// creamos un bodyPart
			BodyPart texto = new MimeBodyPart();
			
			// le damos el contenido
			texto.setText(parametroMensaje);
			
			// creamos un bodyPart para el dato adjunto
			BodyPart adjunto = new MimeBodyPart();
			
			// se reemplazan las \ por /
			rutaArchivo = rutaArchivo.replaceAll("\\\\", "/");
			
			System.out.println("Ruta archivo: " + rutaArchivo);
			
			FileDataSource archivo = new FileDataSource(rutaArchivo);
			
			// le asignamos el archivo
			adjunto.setDataHandler(new DataHandler(archivo));
			
			System.out.println("Nombre archivo: " + nombreArchivo);
			
			// le damos nombre al archivo con su extencion(opcional)
			adjunto.setFileName(nombreArchivo);
			
			// unimos los bodyParts
			MimeMultipart multiParte = new MimeMultipart();
			multiParte.addBodyPart(texto);
			multiParte.addBodyPart(adjunto);
			
			// Construimos el mensaje
			MimeMessage message = new MimeMessage(session);
			
			// indicamos quien va a mandar el correo
			message.setFrom(new InternetAddress(correoRemitente));
			
			// creamos array de receptores
			Address[] receptores = new Address[correos.length];
			
			// rellenamos el array
			for(int i = 0; i<receptores.length; i++)
			{
				receptores[i] = new InternetAddress(correos[i]);
			}
			
			// indicamos los receptores
			message.addRecipients(Message.RecipientType.BCC, receptores);
			
			// tambien podemos indicar a quien enviar una copia (cc)
			// message.addRecipient(Message.RecipientType.CC, new InternetAddress(correoReceptor));
			
			// o enviar una copia sin que los demas la vean (bcc)
			// message.addRecipient(Message.RecipientType.BCC, new InternetAddress(correoReceptor));
			
			// establecemos el asunto
			message.setSubject(parametroAsunto);
			
			// establecemos el mensaje, pasandole la multiparte
			message.setContent(multiParte);
			
			// creamos un obj tipo Transport, indicando el tipo de transporte (smtp)
			Transport t = session.getTransport("smtp");
			
			// establecemos la conexion del correo remitente
			t.connect(correoRemitente, passRemitente);
			
			// enviamos el mensaje con el destinatario principal
			t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			
			// terminamos la conexion
			t.close();
						
			System.out.println("Paso!");
			
			System.out.println("Elimino el archivo? " + Files.deleteIfExists(archivo.getFile().toPath()));

			correosEnviados = true;
			
			LOGGER.log(Level.INFO, "Se ha enviado el correo exitosamente!", UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy));
		}
		catch (AddressException e) 
		{
			e.printStackTrace();

			LOGGER.log(Level.ERROR, "Ha orurrido un error al enviar el correo.\nCausa: {}", e.getMessage());

		}
		catch (MessagingException e)
		{
			LOGGER.log(Level.ERROR, "Ha orurrido un error al enviar el correo.\nCausa: {}", e.getMessage());
		}
		
		return correosEnviados;
	}
	
	public static void enviarMensaje(String asunto, String msje)
	{
		try
		{
			// Propiedades para la conexion a la cuenta ----------------------Email
			Properties props = new Properties();
			
			// indicamos el host
			props.setProperty("mail.smtp.host", "smtp.gmail.com");
			
			// indicamos que usamos tls
			props.setProperty("mail.smtp.starttls.enable", "true");
			
			// indicamos el puerto
			props.setProperty("mail.smtp.port", "587");
			
			// incicamos si va a ser autentificacion, directamente al sv de gmail
			props.setProperty("mail.smtp.auth", "true");
			
			// Preparamos la sesion, enviando las propiedades
			Session session = Session.getDefaultInstance(props);	
			
			// variables con los datosReporte del correo
			String correoRemitente = "a.donjose.cr@gmail.com";
			String passRemitente = "donjose3333";
			String correoReceptor = "masartori85@gmail.com";
			String parametroAsunto = asunto;
			String parametroMensaje = msje;
			
			// Mensaje en formato HTML
			//String mensajeHtml = "Te he enviado un correo desde <b>Java!</b>";
			// establecemos el mensaje con el tipo de codificacion y formato
			// message.setText(mensaje, "ISO-8859-1", "html");
			
			// Construimos el mensaje
			MimeMessage message = new MimeMessage(session);
			
			// indicamos quien va a mandar el correo
			message.setFrom(new InternetAddress(correoRemitente));
			
			// indicamos el receptor principal (to)
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(correoReceptor));
			
			// tambien podemos indicar a quien enviar una copia (cc)
			// message.addRecipient(Message.RecipientType.CC, new InternetAddress(correoReceptor));
			
			// o enviar una copia sin que los demas la vean (bcc)
			// message.addRecipient(Message.RecipientType.BCC, new InternetAddress(correoReceptor));
			
			// establecemos el asunto
			message.setSubject(parametroAsunto);
			
			// establecemos el mensaje
			message.setText(msje);
			
			// creamos un obj tipo Transport, indicando el tipo de transporte (smtp)
			Transport t = session.getTransport("smtp");
			
			// establecemos la conexion del correo remitente
			t.connect(correoRemitente, passRemitente);
			
			// enviamos el mensaje con el destinatario principal
			t.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			
			// terminamos la conexion
			t.close();
			
			LOGGER.log(Level.INFO, "Se ha enviado el correo exitosamente!", UFecha.formatearFecha(LocalDate.now(), UFecha.dd_MM_yyyy));
		}
		
		catch (AddressException e) 
		{
			e.printStackTrace();

		}
		catch (MessagingException e)
		{
			LOGGER.log(Level.ERROR, "Ha orurrido un error al enviar el correo.\nCausa {}", e.getMessage());
			e.printStackTrace();
		}
	}
}
