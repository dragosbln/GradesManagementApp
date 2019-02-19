package util;

import javafx.scene.control.Alert;

public class Utility {
    public static void showErrorMessage(String text){
        Alert message=new Alert(Alert.AlertType.ERROR);
        message.setTitle("Oroare :(((");
        message.setContentText(text);
        message.showAndWait();
    }
}
