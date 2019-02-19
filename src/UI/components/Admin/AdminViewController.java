package UI.components.Admin;

import domain.entities.User;
import domain.validator.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import service.AuthService;
import util.Utility;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class AdminViewController implements Observer {

    AuthService service;
    @FXML
    TableView<User> tableView;
    @FXML
    TextField emailTxt;
    @FXML
    ComboBox<String> typeCombo;

    public void setService(AuthService service){
        this.service=service;
        service.addObserver(this);
        initCombo();
        loadData(service.getAll());
    }

    @FXML
    private void initialize() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldvalue, newValue) -> showUserDetails(newValue));
    }

    private void showUserDetails(User newValue) {
        if(newValue==null){
            clearFields();
        }else {
            emailTxt.setText(newValue.getEmail());
            typeCombo.getSelectionModel().select(newValue.getType().ordinal());
        }
    }

    private void clearFields(){
        emailTxt.clear();
        typeCombo.setValue(null);
    }

    private void initCombo(){
        ArrayList<String> list=new ArrayList<>();
        list.add("RESTRICTED");
        list.add("FULL");
        list.add("ADMIN");
        typeCombo.getItems().setAll(list);
        typeCombo.setPromptText("Type");
    }

    private void loadData(Iterable<User> users){
        tableView.getItems().clear();
        for(User u:users){
            tableView.getItems().add(u);
        }
    }

    @FXML
    public void deleteClicked(ActionEvent event){
        try {
            service.deleteUser(emailTxt.getText());
        }catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }

    @FXML
    public void updateClicked(ActionEvent event){
        service.updateUser(emailTxt.getText(),typeCombo.getValue());
    }

    @Override
    public void update(Observable o, Object arg) {
        loadData(service.getAll());
    }
}
