package UI.components.auth;

import domain.entities.User;
import domain.validator.ValidationException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import service.AuthService;
import util.Utility;

import java.util.Observable;

public class AuthController {
    AuthService service;
    @FXML
    TextField emailTxt;
    @FXML
    PasswordField passwordTxt, confirmPasswordTxt;
    @FXML
    Button btn;
    @FXML
    Label label, titleLabel;
    @FXML
    VBox box;

    public void setService(AuthService service){
        this.service=service;
        init();
    }

    private void init(){
        emailTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordTxt.clear();
            confirmPasswordTxt.clear();
        });
        passwordTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmPasswordTxt.clear();
        });
        confirmPasswordTxt=new PasswordField();
        confirmPasswordTxt.setPromptText("Confirm Password");
        confirmPasswordTxt.setMaxWidth(200);
    }

    @FXML
    public void btnPressed(ActionEvent event){
        if(btn.getText().equals("Login")){
            login();
        } else {
            signUp();
        }
    }

    @FXML
    public void txtClicked(){
        if(label.getText().equals("or, Sign Up!")){
            label.setText("or, Login!");
            btn.setText("Sign Up");
            box.getChildren().add(3, confirmPasswordTxt);
            titleLabel.setText("Sign Up");
            emailTxt.clear();
            passwordTxt.clear();
            confirmPasswordTxt.clear();
        } else {
            label.setText("or, Sign Up!");
            btn.setText("Login");
            box.getChildren().remove(3);
            titleLabel.setText("Login");
            emailTxt.clear();
            passwordTxt.clear();
        }
    }

    private void login(){
        try{
            service.login(emailTxt.getText(), passwordTxt.getText());


        } catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }

    private void signUp(){
        try{
            if(!passwordTxt.getText().equals(confirmPasswordTxt.getText()))
                throw new ValidationException("Passwords don't match!");
            service.addUser(emailTxt.getText(),passwordTxt.getText());
            login();
        } catch (Exception e){
            Utility.showErrorMessage(e.getMessage());
        }
    }


}
