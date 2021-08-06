package aserradero.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.events.JFXDialogEvent;

import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * Clase encargada de la creación de diferentes tipos de alertas del sistema (confirmaciones, excepciones, errores, etc.)
 * @author King
 *
 */
public class UAlertas 
{
	private static Alert alerta = new Alert(null);
	private static boolean res = false;
	
	/**
	 * Método encargado de crear y mostrar alerta de confirmación.
	 * @param titulo - Título del encabezado.
	 * @param contenido - Texto del mensaje.
	 * @return - Retorna verdadero si el botón presionado es el "OK".
	 */
	public static boolean confirmacion(String titulo, String contenido)
	{
		boolean ret = false;
		alerta.setAlertType(AlertType.CONFIRMATION);
		alerta.setTitle("Confirmación");
		alerta.setHeaderText(titulo);
		alerta.setContentText(contenido);
		
		Optional <ButtonType> res = alerta.showAndWait();
		if(res.get() == ButtonType.OK)
		{
			ret = true;
		}
		
		System.out.println("Ret: " + ret);
		return ret;
	}
	
	public static void informacion(String titulo, String contenido)
	{
		alerta.setAlertType(AlertType.INFORMATION);
		alerta.setTitle("Información");
		alerta.setHeaderText(titulo);
		alerta.setContentText(contenido);
		alerta.showAndWait();
	}
	
	public static void alerta(String titulo, String contenido)
	{
		alerta.setAlertType(AlertType.WARNING);
		alerta.setTitle("Alerta");
		alerta.setHeaderText(titulo);
		alerta.setContentText(contenido);
		alerta.showAndWait();
	}
	
	public static void alertaExcepcion(String titulo, Exception e)
	{
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Alerta");
        alert.setHeaderText(titulo);
        alert.setContentText(e.getLocalizedMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("La excepción fue:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }
	
	public static void errorExcepcion(String titulo, String error, int codigo, String estado)
	{
		Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Alerta");
        alert.setHeaderText(titulo);
        alert.setContentText("Error: " + error + "\nCódigo de error: " + codigo + "\nEstado: " + estado);
        alert.showAndWait();
	}
	
	 public static void confirmacion(StackPane root, Node nodeToBeBlurred, List<Button> controls, String header, String body) 
	 {     
		 BoxBlur blur = new BoxBlur(3, 3, 3);

		 if (controls == null) 
		 {
			 controls = Arrays.asList(new JFXButton("Aceptar"));
		 }

		 JFXDialogLayout dialogLayout = new JFXDialogLayout();
		 JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.TOP);

		 controls.forEach(controlButton -> 
		 {
			 controlButton.getStyleClass().add("dialog-button");
			 controlButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> 
			 {
				 dialog.close();
			 });
		 });

		 dialogLayout.setHeading(new Label(header));
		 dialogLayout.setBody(new Label(body));
		 dialogLayout.setActions(controls);
		 dialog.show();
		 dialog.setOnDialogClosed((JFXDialogEvent event1) -> 
		 {
			 nodeToBeBlurred.setEffect(null);
		 });
		 
		 nodeToBeBlurred.setEffect(blur);
	 }
	 
	 public static void mostrarAlerta(StackPane root, Node nodeToBeBlurred, String header, String body) 
	 {     
		 BoxBlur blur = new BoxBlur(3, 3, 3);

		 JFXButton btnAceptar = new JFXButton("Aceptar");

		 JFXDialogLayout dialogLayout = new JFXDialogLayout();
		 JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.TOP);
		 //System.out.println("dialog style --> " + dialogLayout.getStyleClass());

		 btnAceptar.getStyleClass().add("dialog-button");
		 btnAceptar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> 
		 {
			 dialog.close();
		 });

		 dialogLayout.setHeading(new Label(header));
		 dialogLayout.setBody(new Label(body));
		 dialogLayout.setActions(btnAceptar);	 
		 
		 
		 dialog.show();
		 dialog.setOnDialogClosed((JFXDialogEvent event1) -> 
		 {
			 nodeToBeBlurred.setEffect(null);
		 });
		 
		 nodeToBeBlurred.setEffect(blur);
	 }
	 
	 public static void mostrarInfo(StackPane root, Node nodeToBeBlurred, String header, String body) 
	 {     
		 BoxBlur blur = new BoxBlur(3, 3, 3);

		 JFXButton btnAceptar = new JFXButton("Aceptar");

		 JFXDialogLayout dialogLayout = new JFXDialogLayout();
		 JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.TOP);

		 btnAceptar.getStyleClass().add("dialog-button");
		 btnAceptar.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> 
		 {
			 dialog.close();
		 });

		 dialogLayout.setHeading(new Label(header));
		 dialogLayout.setBody(new Label(body));
		 dialogLayout.setActions(btnAceptar);
		 dialog.show();
		 dialog.setOnDialogClosed((JFXDialogEvent event1) -> 
		 {
			 nodeToBeBlurred.setEffect(null);
		 });
		 
		 nodeToBeBlurred.setEffect(blur);
	 }
	 
	 public static void mostrarDialogoEntrada(StackPane root, Node nodeToBeBlurred, List<Control>controles, String header, String body) {     
		 BoxBlur blur = new BoxBlur(3, 3, 3);
		 JFXDialogLayout dialogLayout = new JFXDialogLayout();
		 JFXDialog dialog = new JFXDialog(root, dialogLayout, JFXDialog.DialogTransition.TOP);

		 controles.forEach(control -> 
		 {
			 if(control instanceof Button)
			 {
				 control.getStyleClass().add("dialog-button");
				 ((Button) control).addEventHandler(ActionEvent.ACTION, (ActionEvent e) -> 
				 {
					 System.out.println("action - dialog.close");
					 dialog.close();
				 });	
			 }
		 });

		 dialogLayout.setHeading(new Label(header));
		 dialogLayout.setBody(new Label(body));
		 dialogLayout.setActions(controles);
		 dialogLayout.setAlignment(Pos.CENTER);
		 dialog.show();
		 dialog.setOnDialogClosed((JFXDialogEvent event1) -> 
		 {
			 nodeToBeBlurred.setEffect(null);
		 });
		 nodeToBeBlurred.setEffect(blur);
	    }
}
