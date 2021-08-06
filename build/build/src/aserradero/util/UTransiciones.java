package aserradero.util;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.util.Duration;

public final class UTransiciones 
{
	private UTransiciones()
	{
	}
	
	private static FadeTransition transicion = new FadeTransition();
	//private static DoubleTransition dt = new DoubleTransition();
	private static BooleanProperty collapsed = new SimpleBooleanProperty();
	
	public static final void fadeIn(Node nodo1, int duracion)
	{
		transicion.setNode(nodo1);
		transicion.setDuration(Duration.millis(duracion));
		transicion.setFromValue(0.0);
		transicion.setToValue(1.0);
		transicion.setCycleCount(1);
		transicion.setAutoReverse(false);
		transicion.playFromStart();
	}
	
	public static final void fadeOut(Node nodo1, Node nodo2, int duracion)
	{
		transicion.setNode(nodo1);
		transicion.setDuration(Duration.millis(duracion));
		transicion.setFromValue(1);
		transicion.setToValue(0);
		//transicion.setCycleCount(1);
		//transicion.setAutoReverse(false);
		//transicion.setOnFinished(e -> nodo2.setVisible(true));
		nodo1.setVisible(false);
		transicion.play();
	}
	
	public static final void animateSplitPane(SplitPane splitPane, Double duration)
	{
        collapsed.bind(splitPane.getDividers().get(0).positionProperty().isEqualTo(0, 0.01));
        
        double target = collapsed.get() ? 0.2 : 0.0 ;
        System.out.println("U- target: " + target);
        KeyValue keyValue = new KeyValue(splitPane.getDividers().get(0).positionProperty(), target);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), keyValue));
        timeline.play();
	}
}
