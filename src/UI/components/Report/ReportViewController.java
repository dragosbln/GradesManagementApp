package UI.components.Report;

import domain.entities.Homework;
import domain.validator.ValidationException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.omg.PortableInterceptor.INACTIVE;
import service.ReportService;
import util.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class ReportViewController {

    //TODO directory chooser
    ReportService service;
    @FXML
    ComboBox<String> optionCombo;
    @FXML
    PieChart pieChart;
    @FXML
    StackPane extraPane;
    @FXML
    ImageView img;
    @FXML
    TextField chosenFileTxt;

    private ComboBox<String> homeworkCombo;
    private File selectedDirectory = new File("C:\\");

    public void setService(ReportService service) {
        this.service = service;
        initOptionCombo();
        chosenFileTxt.setEditable(false);
    }

    private void initOptionCombo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("Averages");
        list.add("Grades per Homework");
        list.add("Pass/Fail Averages");
        list.add("Late homeworks");

        optionCombo.getItems().setAll(list);
        optionCombo.getSelectionModel().selectFirst();

        optionCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Grades per Homework") || newValue.equals("Late homeworks")) {
                homeworkCombo = new ComboBox<>();
                ArrayList<String> homeworks = new ArrayList<>();
                for (Homework h : service.getService().getAllHomeworks()) {
                    homeworks.add(h.getID() + ". " + h.getDescription());
                }
                homeworkCombo.getItems().setAll(homeworks);
                homeworkCombo.getSelectionModel().selectFirst();
                extraPane.getChildren().add(homeworkCombo);
            } else {
                extraPane.getChildren().clear();
            }
        });
    }

    private void allAveragesChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<Integer, Integer> e : service.getAllAvgsReport(selectedDirectory).entrySet()) {
            String label = e.getKey() + (e.getKey() == 10 ? "" : "-" + e.getKey() + ".99");
            pieChartData.add(new PieChart.Data(label, e.getValue()));
        }
        pieChart.dataProperty().setValue(pieChartData);
    }

    private void gradesPerHomeworkChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<Float, Integer> e : service.getGradesPerHomeworkReport(homeworkCombo.getValue().split("\\.")[0], selectedDirectory).entrySet()) {
            pieChartData.add(new PieChart.Data(e.getKey().toString(), e.getValue()));
        }
        pieChart.dataProperty().setValue(pieChartData);
    }

    private void passedFailedChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> e : service.getPassedFailedReport(selectedDirectory).entrySet()) {
            pieChartData.add(new PieChart.Data(e.getKey(), e.getValue()));
        }
        pieChart.dataProperty().setValue(pieChartData);
    }

    private void lateGradesChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> e : service.getLateHomeworksReport(homeworkCombo.getValue().split("\\.")[0], selectedDirectory).entrySet()) {
            pieChartData.add(new PieChart.Data(e.getKey(), e.getValue()));
        }
        pieChart.dataProperty().setValue(pieChartData);
    }

    @FXML
    public void openFile() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File defaultDirectory = new File("C:\\Users\\Dragos\\Desktop");
        chooser.setInitialDirectory(defaultDirectory);
        selectedDirectory = chooser.showDialog(pieChart.getScene().getWindow());
        if (selectedDirectory != null)
            chosenFileTxt.setText(selectedDirectory.toString());
        else {
            chosenFileTxt.setText("C:/Users/Dragos/Desktop");
            selectedDirectory = new File("C:\\Users\\Dragos\\Desktop");
        }
    }

    @FXML
    public void generate(ActionEvent event) {
        try {
            if(chosenFileTxt.getText().equals("")){
                throw new ValidationException("Choose file!");
            }
            img.setVisible(false);
            switch (optionCombo.getSelectionModel().getSelectedIndex()) {
                case 0:
                    allAveragesChart();
                    break;
                case 1:
                    gradesPerHomeworkChart();
                    break;
                case 2:
                    passedFailedChart();
                    break;
                case 3:
                    lateGradesChart();
                    break;
            }
        }catch (ValidationException e){
            Utility.showErrorMessage(e.getMessage());
        }
    }

}
