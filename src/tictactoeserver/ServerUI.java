package tictactoeserver;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

public class ServerUI extends AnchorPane {

    protected final FlowPane flowPane;
    protected final Button startBtn;
    protected final Button stopBtn;

    public ServerUI() {

        flowPane = new FlowPane();
        startBtn = new Button();
        stopBtn = new Button();

        setMaxHeight(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setPrefHeight(400.0);
        setPrefWidth(600.0);

        flowPane.setLayoutY(6.0);
        flowPane.setPrefHeight(88.0);
        flowPane.setPrefWidth(600.0);

        startBtn.setMnemonicParsing(false);
        startBtn.setText("Start");
        startBtn.setOpaqueInsets(new Insets(0.0));
        FlowPane.setMargin(startBtn, new Insets(0.0, 0.0, 0.0, 150.0));

        stopBtn.setMnemonicParsing(false);
        stopBtn.setText("Stop");
        stopBtn.setOpaqueInsets(new Insets(0.0));
        FlowPane.setMargin(stopBtn, new Insets(0.0, 0.0, 0.0, 150.0));

        flowPane.getChildren().add(startBtn);
        flowPane.getChildren().add(stopBtn);
        getChildren().add(flowPane);

    }
}
