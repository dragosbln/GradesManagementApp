package UI.components.Homework;


import domain.entities.Homework;
import domain.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.Service;
import util.ChangeType;
import util.Utility;

import java.util.Observable;
import java.util.Observer;

public class HomeworkViewController implements Observer {

    private Service service;
    @FXML
    TableView<Homework> tableView;
    @FXML
    TextField idTxt;
    @FXML
    TextField deadlineTxt;
    @FXML
    TextField descriptionTxt;
    @FXML
    VBox btns;
    @FXML
    Button deleteH;

    @FXML
    private void initialize() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldvalue, newValue) -> showHomeworkDetails(newValue));
    }

    public void initRestrictedView(){
        btns.setVisible(false);
        deleteH.setVisible(false);
    }

    public void initFullView(){
        btns.setVisible(true);
        deleteH.setVisible(false);
    }

    public void initAdminView(){
        deleteH.setVisible(true);
    }

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        loadData();
    }

    private void showHomeworkDetails(Homework newValue) {
        if(newValue==null){
            clearFields();
        }else {
            idTxt.setText(newValue.getID());
            deadlineTxt.setText(Integer.toString(newValue.getDeadlineWeek()));
            descriptionTxt.setText(newValue.getDescription());
        }
    }

    private void clearFields(){
        idTxt.clear();
        deadlineTxt.clear();
        descriptionTxt.clear();
    }

    public void loadData(){
        tableView.getItems().clear();
        for(Homework h:service.getAllHomeworks()){
            tableView.getItems().add(h);
        }
    }

    @FXML
    private void addClicked(ActionEvent event){
        try {
            service.addHomework(idTxt.getText(),Integer.parseInt(deadlineTxt.getText()),descriptionTxt.getText());
            clearFields();
        }catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void deleteClicked(ActionEvent event){
        try{
            service.deleteHomework(idTxt.getText());
            clearFields();
        }catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void extendDeadline(ActionEvent event){
        try{
            service.extendDeadline(idTxt.getText(), Integer.parseInt(deadlineTxt.getText()));
            clearFields();
        }catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void clearClicked(ActionEvent event){
        clearFields();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg.equals(ChangeType.HOMEWORK)) {
            loadData();
        }
    }

}
