package UI.components.Grade;

import domain.entities.Grade;
import domain.entities.Homework;
import domain.entities.Student;
import domain.entities.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.controlsfx.control.textfield.TextFields;
import service.Service;
import util.ChangeType;
import util.GoogleMail;
import util.Utility;

import javax.mail.MessagingException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GradeViewController implements Observer {

    enum filter {NAME, DATE, GROUP, HOMEWORK};
    private Service service;
    private ArrayList<Grade> currentList=new ArrayList<>();
    private User user;
    @FXML
    TableView<Grade> tableView;
    @FXML
    StackPane namePane;
    @FXML
    TextField hidTxt;
    @FXML
    TextField gradeTxt;
    @FXML
    CheckBox modivatedCheckBox;
    @FXML
    TextArea feedbackTxt;
    @FXML
    ComboBox<String> homeworkCombo;
    @FXML
    ComboBox<String> groupCombo;
    @FXML
    DatePicker beginDate;
    @FXML
    DatePicker endDate;
    @FXML
    HBox addBtn;
    @FXML
    VBox filterView;
    @FXML
    TextField filterNameTxt;

    TextField sidTxt;

    ArrayList<Grade> dateFilter, nameFilter, groupFilter, homeworkFilter;

    public void initRestrictedView(){
        addBtn.setVisible(false);
        filterView.setVisible(false);
        loadData(service.getGradesByEmail(user.getEmail()));
    }

    public void initFullView(){
        addBtn.setVisible(true);
        filterView.setVisible(true);
        loadData(service.getAllGrades());
    }

    private void initHCombo(){
        ArrayList<String> homeworks=new ArrayList<>();
        for(Homework h:service.getAllHomeworks()){
            homeworks.add(h.getID()+". "+h.getDescription());
        }
        homeworkCombo.getItems().setAll(homeworks);

        homeworkCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                homeworkFilter.clear();
                homeworkFilter=service.getHomeworkGrades(newValue.split("\\.")[0]);
                updateFilter();
            }
        });
    }

    private void initGCombo(){
        ArrayList<String> groups=new ArrayList<>();
        for(Student s:service.getAllStudents()){
            if(!groups.contains(s.getGroup())){
                groups.add(s.getGroup());
            }
        }
        groups.sort(Comparator.naturalOrder());
        groupCombo.getItems().setAll(groups);

        groupCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            groupFilter.clear();
            groupFilter=service.getGroupGrades(newValue);
            updateFilter();
        });
    }

    private void initNameFiletr(){
        filterNameTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            nameFilter.clear();
            nameFilter=service.getGradesByStudentName(newValue);
            updateFilter();
        });
    }

    private void initComboBoxes(){
        initGCombo();
        initHCombo();
        initNameFiletr();
    }

    private void initAutocomplete(){
        namePane.getChildren().clear();
        sidTxt=new TextField();
        TextFields.bindAutoCompletion(sidTxt, service.getStudentNames());
        namePane.getChildren().add(sidTxt);
    }

    @FXML
    private void initialize() {
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldvalue, newValue) -> showGradeDetails(newValue));
    }

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
        loadData(service.getAllGrades());
        initComboBoxes();
        sidTxt=new TextField();
        namePane.getChildren().add(sidTxt);
        initAutocomplete();

        nameFilter=new ArrayList<>();
        dateFilter=new ArrayList<>();
        groupFilter=new ArrayList<>();
        homeworkFilter=new ArrayList<>();
        service.getAllGrades().forEach(grade -> {
            nameFilter.add(grade);
            dateFilter.add(grade);
            groupFilter.add(grade);
            homeworkFilter.add(grade);
        });
    }

    public void setUser(User user){
        this.user=user;
    }

    private void showGradeDetails(Grade newValue) {
        if(newValue==null){
            clearFields();
        }else {
            sidTxt.setText(newValue.getStudentName());
            hidTxt.setText(newValue.getHid());
            gradeTxt.setText(Float.toString(newValue.getGrade()));
            feedbackTxt.setText(newValue.getFeedback());
        }
    }

    private void clearFields(){
        sidTxt.clear();
        hidTxt.clear();
        gradeTxt.clear();
        feedbackTxt.clear();
        modivatedCheckBox.setSelected(false);
        return;
    }

    private void loadData(Iterable<Grade> list){
        tableView.getItems().clear();
        currentList.clear();
        for(Grade g:list){
            currentList.add(g);
            tableView.getItems().add(g);
        }
    }

    @FXML
    private void addClicked(ActionEvent event){
        try {
            int sid=-1;
            String studId=sidTxt.getText();
            try{
                sid=Integer.parseInt(studId);
            } catch (NumberFormatException ne){}
            if(sid==-1){
                studId=service.getStudentByName(studId).getID();
            }
            String hid=hidTxt.getText();

            service.addGrade(studId, hidTxt.getText(), user.getEmail(), Float.parseFloat(gradeTxt.getText()), feedbackTxt.getText(), modivatedCheckBox.isSelected());

            clearFields();
        }catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }

    @FXML
    public void clearPressed(ActionEvent event){
        homeworkCombo.setValue(null);
        groupCombo.setValue(null);
        filterNameTxt.clear();
        loadData(service.getAllGrades());
    }

    @FXML
    public void filterByDate(ActionEvent event){
        try{
            if(beginDate.getValue()==null||endDate.getValue()==null)
                throw new Exception("Both dates need to be picked and valid!");
            dateFilter.clear();
            dateFilter=service.getGradesByDate(Date.from(beginDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            updateFilter();
        } catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }

    private void updateFilter() {
        int min=Integer.MAX_VALUE;
        filter type=null;
        ArrayList<Grade> filteredList=new ArrayList<>();
        if(!filterNameTxt.equals("")){
            if(nameFilter.size()<min){
                min=nameFilter.size();
                type=filter.NAME;
            }
        }
        if(groupCombo.getSelectionModel().getSelectedIndex()>=0){
            if(groupFilter.size()<min){
                min=groupFilter.size();
                type=filter.GROUP;
            }
        }
        if(homeworkCombo.getSelectionModel().getSelectedIndex()>=0){
            if(homeworkFilter.size()<min){
                min=homeworkFilter.size();
                type=filter.HOMEWORK;
            }
        }
        if(beginDate.getValue()!=null&&endDate.getValue()!=null){
            if(dateFilter.size()<min){
                type=filter.DATE;
            }
        }
        if(type!=null){
            switch (type){
                case DATE:
                    for(Grade g:dateFilter){
                        if(nameFilter.contains(g)&&groupFilter.contains(g)&&homeworkFilter.contains(g)){
                            filteredList.add(g);
                        }
                    }
                    break;
                case NAME:
                    for(Grade g:nameFilter){
                        if(dateFilter.contains(g)&&groupFilter.contains(g)&&homeworkFilter.contains(g)){
                            filteredList.add(g);
                        }
                    }
                    break;
                case GROUP:
                    for(Grade g:groupFilter){
                        if(nameFilter.contains(g)&&dateFilter.contains(g)&&homeworkFilter.contains(g)){
                            filteredList.add(g);
                        }
                    }
                    break;
                case HOMEWORK:
                    for(Grade g:homeworkFilter){
                        if(nameFilter.contains(g)&&groupFilter.contains(g)&&dateFilter.contains(g)){
                            filteredList.add(g);
                        }
                    }
                    break;
            }
            loadData(filteredList);
        } else {
            loadData(service.getAllGrades());
        }

    }


    @Override
    public void update(Observable o, Object arg) {
        if(arg.equals(ChangeType.GRADE)||arg.equals(ChangeType.STUDENT)) {
            loadData(service.getAllGrades());
            initAutocomplete();
        }
    }

}
