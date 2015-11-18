/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hms;

import hms.model.FrontDeskDAO;
import hms.model.Profile;
import hms.model.ProfileBuilder;
import hms.model.States;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * FXML Controller class
 *
 * @author jgreene
 */
public class ProfileFormController implements Initializable {
    
    public static String HEADING_NEW = "Create Guest Profile";
    public static String HEADING_EDIT = "Edit Guest Profile";
    
    @FXML
    private Text lblHeading;
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private Label lblErrorMsg;
    
    @FXML
    private TextField txtStreet;

    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private ChoiceBox<String> cbxTitles;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtZip;
    @FXML
    private TextField txtApt;
    @FXML
    private TextField txtCountry;
    @FXML
    private ChoiceBox<String> cbxStates;
    @FXML
    private CheckBox chkVIP;
    @FXML
    private TextArea txtNotes;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label zipLabel;
    
    
    private Stage stage;
    private FrontDeskDAO dao;
    
    private boolean RESULT = false;
    private boolean EDIT = false;
    private Profile profile;
    private Profile newProfile;
    
    private ObservableList<String> states;
    private ObservableList<String> titles;
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dao = new FrontDeskDAO();
        initChoiceBoxes();
    }    

    @FXML
    private void onActionFirstName(ActionEvent event) {
        txtLastName.requestFocus();
    }

    @FXML
    private void onActionLastName(ActionEvent event) {
        txtPhoneNumber.requestFocus();
    }

    @FXML
    private void onActionPhoneNumber(ActionEvent event) {
        txtEmail.requestFocus();
    }
    
    @FXML
    private void onActionEmail(ActionEvent event) {
        txtStreet.requestFocus();
    }
    
    @FXML
    private void onActionStreet(ActionEvent event) {
        txtApt.requestFocus();
    }

    @FXML
    private void onActionApt(ActionEvent event) {
        txtCity.requestFocus();
    }
    
    @FXML
    private void onActionCity(ActionEvent event) {
        txtZip.requestFocus();
    }

    @FXML
    private void onActionCountry(ActionEvent event) {
    }

    @FXML
    private void onActionVIP(ActionEvent event) {
    }

    @FXML
    private void onActionNotes(MouseEvent event) {
    }
    @FXML
    private void onActionZip(ActionEvent event){
        cbxStates.requestFocus();
    }

    @FXML
    private void onActionSave(ActionEvent event) {        
       
       try {
            if (!validateFields()) {
                return;
            }
       } catch (Exception e) {
           System.out.println("Error Validating Fields");
       } 
       
        
        // Edit or Create
        if (EDIT) {
            handleSaveEditProfile();
        } else {
            handleSaveNewProfile();
        }
    }

    @FXML
    private void onActionCancel(ActionEvent event) {
        RESULT = false;
        stage.close();
    }

    
    void setStage(Stage stage) {
        this.stage = stage;
    }

    void setProfileInformation(Profile p) {
      System.out.println("Setting profile info");
            txtFirstName.setText(p.getFirstName());
            txtLastName.setText(p.getLastName());
            txtPhoneNumber.setText(p.getPhoneNumber());
            txtEmail.setText(p.getEmail());
            txtNotes.setText(p.getNotes());
            cbxTitles.setValue(p.getTitle());
            cbxStates.setValue(p.getState());
            chkVIP.setSelected(p.isVIP());
            txtStreet.setText(p.getStreet());
            txtApt.setText(p.getApt());
            txtCity.setText(p.getCity());
            txtZip.setText(p.getZip());
            txtCountry.setText(p.getCountry());
            
            System.out.println("p:" + p.getFirstName());
    }

   
    private void initChoiceBoxes() {
        states = FXCollections.observableArrayList();
        titles = FXCollections.observableArrayList();
        states.addAll(States.getStates());
        titles.addAll("Mr", "Ms", "Mrs", "Dr");
        cbxStates.setItems(states);
        cbxTitles.setItems(titles);
        cbxStates.setValue("GA");
        cbxTitles.setValue("");
    }

    private boolean validateFields() {
        
        boolean valid = true;
        
        //RESET LABELS
        phoneLabel.setText("");
        emailLabel.setText("");
        zipLabel.setText("");
        lblErrorMsg.setText("");
        
        if (txtPhoneNumber.getText().isEmpty() ){
            phoneLabel.setText("Please enter your phone number");
            valid = false;
        } else if (!validatePhoneNumber(txtPhoneNumber.getText()) ){
            phoneLabel.setText("Please enter correct phone number");
            valid = false;
        }
        
        if (txtEmail.getText().isEmpty() ){
            emailLabel.setText("Please enter your email");
            valid = false;
        } else if (!isEmailValid(txtEmail.getText())){
            emailLabel.setText("Please enter correct email");
            valid = false;
        }
        
        String zipCodePattern = "\\d{5}(-\\d{4})?";
        if (txtZip.getText().isEmpty() ){
            zipLabel.setText("Please enter your ZIP code");
            valid = false;
        } else if (!txtZip.getText().matches(zipCodePattern)){
            zipLabel.setText("Please enter correct ZIP code");
            valid = false;
        }
        if (txtFirstName.getText().isEmpty() ||
                txtLastName.getText().isEmpty() ||
                txtStreet.getText().isEmpty() ||
                txtCity.getText().isEmpty() ||
                cbxStates.getValue().isEmpty() ) {
                
                lblErrorMsg.setText("Please fill out Name and Address Fields");
                valid = false;
        }
        
        return valid;
    }

    boolean getResult() {
        //Used by caller to check status
        return RESULT;
    }
    
    void setEditFlag(boolean value) {
        this.EDIT = value;
        if (EDIT){
            lblHeading.setText(HEADING_EDIT);
        } else {
            lblHeading.setText(HEADING_NEW);
        }
    }
 
    void setProfile(Profile profile) {
        this.profile = profile;
    }

    private void handleSaveEditProfile() {
        try {
            boolean result = false;
            Profile p = new ProfileBuilder()
                    .setFirstName(txtFirstName.getText())
                    .setLastName(txtLastName.getText())
                    .setEmail(txtEmail.getText())
                    .setPhoneNumber(txtPhoneNumber.getText())
                    .setStreet(txtStreet.getText())
                    .setApt(txtApt.getText())
                    .setCity(txtCity.getText())
                    .setState(cbxStates.getValue())
                    .setZip(txtZip.getText())
                    .setCountry(txtCountry.getText())
                    .setVIP(chkVIP.isSelected())
                    .setNotes(txtNotes.getText())
                    .setTitle(cbxTitles.getValue())
                    .createProfile();
            
            System.out.println("VIP:" + p.isVIP());
            
            //Restore Profile ID
            p.setMemberID(profile.getMemberID());
            
            if ( p == null ) {
                RESULT = false;
                stage.close();
            }

            result = dao.updateProfile(p);
            
            if (result) {
                newProfile = p;
                RESULT = true;
            }
        
        } catch (Exception e) {
            System.out.println("DB Error");
        }
    
        stage.close();
    }
    
    private void handleSaveNewProfile() {
         try {
            boolean result = false;
            int profileID = 0;
            System.out.println("Handling SaveNewProfile");
            
            System.out.println("Creating profile from fields");
            Profile p = new ProfileBuilder()
                    .setFirstName(txtFirstName.getText())
                    .setLastName(txtLastName.getText())
                    .setEmail(txtEmail.getText())
                    .setPhoneNumber(txtPhoneNumber.getText())
                    .setStreet(txtStreet.getText())
                    .setApt(txtApt.getText())
                    .setCity(txtCity.getText())
                    .setState(cbxStates.getValue())
                    .setZip(txtZip.getText())
                    .setCountry(txtCountry.getText())
                    .setVIP(chkVIP.isSelected())
                    .setNotes(txtNotes.getText())
                    .setTitle(cbxTitles.getValue())
                    .createProfile();
           
            System.out.println("VIP:" + p.isVIP());
            
            
            System.out.println("Checking null p");
            if ( p == null ) {
                
            System.out.println("P could not be created by the form fields");
                RESULT = false;
                stage.close();
            }
            
            System.out.println("Sending p to db" + p.getFirstName() + p.getLastName());
            profileID = dao.createProfile(p);
            
            System.out.println("Result" + result);
            if (profileID > 0) {
                
            System.out.println("If DB result true, save and set p");
            System.out.println("");
                p.setMemberID(profileID);
                newProfile = p;
                RESULT = true;
            } else {
                
            System.out.println("New Profile not pass db..");
            }
        
        } catch (Exception e) {
            System.out.println("DB Error");
        }
    
        stage.close();
    }
    
    //Checks phone number for validity
    private boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
        //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
        //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
        //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
        //return false if nothing matches the input
        else return false;

    }
    //Checks email for validity
    public boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
    public Profile getNewProfile() {
        return newProfile;
    }
}
