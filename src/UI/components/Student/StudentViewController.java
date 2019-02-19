package UI.components.Student;

import domain.entities.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import service.Service;
import util.ChangeType;
import util.Utility;

import java.util.Observable;
import java.util.Observer;


public class StudentViewController implements Observer {
    private Service service;
    @FXML
    TableView<Student> tableView;
    @FXML
    TextField idTxt;
    @FXML
    TextField nameTxt;
    @FXML
    TextField groupTxt;
    @FXML
    TextField emailTxt;
    @FXML
    HBox modifyBtns;

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        loadData();
    }

    public void initFullView(){
        modifyBtns.setVisible(false);
    }

    public void initAdminView(){
        modifyBtns.setVisible(true);
    }


    @FXML
    private void initialize(){
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable,oldvalue,newValue)->showStudentDetails(newValue) );
    }

    private void showStudentDetails(Student student) {
        if(student==null){
            clearFields();
        } else {
            idTxt.setText(student.getID());
            nameTxt.setText(student.getName());
            groupTxt.setText(student.getGroup());
            emailTxt.setText(student.getEmail());
        }
    }

    private void clearFields() {
        idTxt.clear();
        nameTxt.clear();
        groupTxt.clear();
        emailTxt.clear();
    }

    public void loadData(){
        tableView.getItems().clear();
        for(Student s:service.getAllStudents()){
            tableView.getItems().add(s);
        }
    }

    @FXML
    private void addClicked(ActionEvent event){
        try {
            service.addStudent(idTxt.getText(),nameTxt.getText(),groupTxt.getText(),emailTxt.getText());
            clearFields();
        }catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void deleteClicked(ActionEvent event){
        try{
            service.deleteStudent(idTxt.getText());
            clearFields();
        }catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }
    @FXML
    private void updateClicked(ActionEvent event){
        try {
            service.modifyStudent(idTxt.getText(),nameTxt.getText(),groupTxt.getText(),emailTxt.getText());
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
        String s=o.getClass().getName();
        if(arg.equals(ChangeType.STUDENT)) {
            loadData();
        }
    }
}
