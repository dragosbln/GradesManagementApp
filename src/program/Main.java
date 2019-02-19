package program;

import UI.App.AppController;
import domain.validator.GradeValidator;
import domain.validator.HomeworkValidator;
import domain.validator.StudentValidator;
import domain.validator.UserValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import repository.GradesXMLRepo;
import repository.HomeworkXMLRepo;
import repository.StudentXMLRepo;
import repository.UsersXMLRepo;
import service.AuthService;
import service.ReportService;
import service.Service;
import util.GoogleMail;

import javax.mail.MessagingException;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        StudentValidator studentValidator=new StudentValidator();
        StudentXMLRepo studentXMLRepo=new StudentXMLRepo(studentValidator, "D:\\JavaProjects\\PolishedApp\\src\\repository\\files\\students.xml");

        HomeworkValidator homeworkValidator=new HomeworkValidator();
        HomeworkXMLRepo homeworkXMLRepo=new HomeworkXMLRepo(homeworkValidator, "D:\\JavaProjects\\PolishedApp\\src\\repository\\files\\homeworks.xml");

        GradeValidator gradeValidator=new GradeValidator();
        GradesXMLRepo gradesXMLRepo=new GradesXMLRepo(gradeValidator, "D:\\JavaProjects\\PolishedApp\\src\\repository\\files\\grades.xml");

        Service service=new Service(homeworkXMLRepo, studentXMLRepo, gradesXMLRepo);

        UserValidator userValidator=new UserValidator();
        UsersXMLRepo usersXMLRepo=new UsersXMLRepo(userValidator, "D:\\JavaProjects\\PolishedApp\\src\\repository\\files\\users.xml");
        AuthService authService=new AuthService(usersXMLRepo);

        ReportService reportService=new ReportService(service);

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/UI/App/App.fxml")); //URL
        BorderPane rootLayout= (BorderPane) loader.load();
        AppController controller=loader.getController();
        controller.setServices(service, authService, reportService);


        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(rootLayout));
        primaryStage.show();

    }



    public static void main(String[] args) throws ClassNotFoundException {


        launch(args);
    }
}
