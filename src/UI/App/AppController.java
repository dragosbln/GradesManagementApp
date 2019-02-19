package UI.App;

import UI.components.Admin.AdminViewController;
import UI.components.Grade.GradeViewController;
import UI.components.Homework.HomeworkViewController;
import UI.components.Report.ReportViewController;
import UI.components.Student.StudentViewController;
import UI.components.auth.AuthController;
import domain.entities.User;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.AuthService;
import service.ReportService;
import service.Service;
import util.UserType;

import java.awt.*;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


public class AppController implements Observer {

    private User user=null;
    private Service service;
    private AuthService authService;
    private ReportService reportService;
    private AnchorPane authLy, studentLy, homeworkLy, gradesLy, reportLy, adminLy;
    private StudentViewController studentViewController;
    private HomeworkViewController homeworkViewController;
    private GradeViewController gradesViewController;
    private AuthController authController;
    private AdminViewController adminViewController;
    private ReportViewController reportViewController;

    @FXML
    HBox menu;
    @FXML
    BorderPane pane;
    @FXML
    ImageView closeImg;
    @FXML
    VBox logInfo;
    @FXML
    Label emailLabel;

    private ImageView studentImg, gradeImg, homeworkImg, reportImg, adminImg;
    private void init() {
        try {
            FXMLLoader studentLoader=new FXMLLoader();
            studentLoader.setLocation(getClass().getResource("/UI/components/Student/StudentView.fxml")); //URL
            studentLy=studentLoader.load();
            studentViewController=studentLoader.getController();
            studentViewController.setService(service);

            FXMLLoader homeworkLoader=new FXMLLoader();
            homeworkLoader.setLocation(getClass().getResource("/UI/components/Homework/HomeworkView.fxml"));
            homeworkLy=homeworkLoader.load();
            homeworkViewController=homeworkLoader.getController();
            homeworkViewController.setService(service);

            FXMLLoader gradeLoader = new FXMLLoader();
            gradeLoader.setLocation(getClass().getResource("/UI/components/Grade/GradeView.fxml"));
            gradesLy=gradeLoader.load();
            gradesViewController=gradeLoader.getController();
            gradesViewController.setService(service);

            FXMLLoader authLoader = new FXMLLoader();
            authLoader.setLocation(getClass().getResource("/UI/components/auth/Auth.fxml"));
            authLy=authLoader.load();
            authController=authLoader.getController();
            authController.setService(authService);

            FXMLLoader adminLoader = new FXMLLoader();
            adminLoader.setLocation(getClass().getResource("/UI/components/Admin/AdminView.fxml"));
            adminLy=adminLoader.load();
            adminViewController=adminLoader.getController();
            adminViewController.setService(authService);

            FXMLLoader reportLoader=new FXMLLoader();
            reportLoader.setLocation(getClass().getResource("/UI/components/Report/ReportView.fxml"));
            reportLy=reportLoader.load();
            reportViewController=reportLoader.getController();
            reportViewController.setService(reportService);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pane.setCenter(authLy);
        logInfo.setVisible(false);

        studentImg=initOption("student.png", event -> {optionClicked(studentLy, studentImg);});
        homeworkImg=initOption("homework.png", event -> {optionClicked(homeworkLy, homeworkImg);});
        gradeImg=initOption("grade.png", event -> {optionClicked(gradesLy, gradeImg);});
        reportImg=initOption("report.png", event -> {optionClicked(reportLy, reportImg);});
        adminImg=initOption("admin.png", event -> {optionClicked(adminLy, adminImg);});
    }

    private ImageView initOption(String res, EventHandler handler){
        ImageView img=new ImageView();
        Image image=new Image(getClass().getClassLoader().getResourceAsStream("assets/"+res));
        img.setImage(image);
        img.setFitHeight(75);
        img.setFitWidth(75);
        img.setOnMouseClicked(handler);
        return img;
    }

    public void setServices(Service service, AuthService authService, ReportService reportService){
        this.service=service;
        this.authService=authService;
        this.reportService=reportService;
        authService.addObserver(this);
        init();
    }


    private void clearSelected(){
        studentImg.getStyleClass().removeAll("selected");
        gradeImg.getStyleClass().removeAll("selected");
        homeworkImg.getStyleClass().removeAll("selected");
        reportImg.getStyleClass().removeAll("selected");
        adminImg.getStyleClass().removeAll("selected");
    }

    private void optionClicked(AnchorPane ly, ImageView img){
        pane.setCenter(ly);
        clearSelected();
        img.getStyleClass().add("selected");
    }

    private void initUserView(){
        gradesViewController.setUser(user);
        pane.setCenter(null);
        emailLabel.setText(user.getEmail());
        logInfo.setVisible(true);

        menu.getChildren().add(homeworkImg);
        menu.getChildren().add(gradeImg);
        if(user.getType()== UserType.RESTRICTED) {
            homeworkViewController.initRestrictedView();
            gradesViewController.initRestrictedView();
            return;
        }
        gradesViewController.initFullView();
        homeworkViewController.initFullView();
        menu.getChildren().add(0,studentImg);
        menu.getChildren().add(reportImg);
        studentViewController.initFullView();
        if(user.getType()==UserType.FULL)
            return;
        menu.getChildren().add(adminImg);
        studentViewController.initAdminView();
        homeworkViewController.initAdminView();
    }

    @FXML
    public void logout(){
        pane.setCenter(authLy);
        menu.getChildren().clear();
        logInfo.setVisible(false);
        user=null;
        clearSelected();
    }

    @FXML
    public void closeApp(){
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.close();
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof User){
            user=(User)arg;
            initUserView();
        }
    }


}
