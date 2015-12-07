/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hms;

import hms.model.BillingCode;
import hms.model.BillingMenuDAO;
import hms.model.BillingMenuDTO;
import hms.model.FolioCharge;
import hms.model.FrontDeskDAO;
import hms.model.Item;
import hms.model.Reservation;
import hms.model.User;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.util.converter.CurrencyStringConverter;

/**
 * FXML Controller class
 *
 * @author jgreene
 */
public class BillingMenuController implements Initializable, SubMenu {

    @FXML
    private TableView<FolioCharge> tblCharges;
    @FXML
    private TableColumn<FolioCharge, String> colDate;
    @FXML
    private TableColumn<FolioCharge, String> colCode;
    @FXML
    private TableColumn<FolioCharge, String> colDescription;
    @FXML
    private TableColumn<FolioCharge, String> colAmount;
    @FXML
    private Button btnCashPayment;
    @FXML
    private Label lblSubTotal;
    @FXML
    private Label lblTaxes;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblGuestName;
    @FXML
    private TextField txtAmount;
    @FXML
    private Button btnDeleteCharge;
    @FXML
    private TableView<BillingMenuDTO> tblGuests;
    @FXML
    private TableColumn<BillingMenuDTO, String> colFirst;
    @FXML
    private TableColumn<BillingMenuDTO, String> colLast;
    @FXML
    private TableColumn<BillingMenuDTO, Integer> colRoom;
    @FXML
    private Button btnSelectGuest;
    @FXML
    private Button btnPrintFolio;
    @FXML
    private Button btnCheckOut;
    @FXML
    private Label lblPrice;
    @FXML
    private Label lblItem;
    @FXML
    private Label lblDescription;
    @FXML
    private ScrollPane spItems;
    @FXML
    private FlowPane itemPane;
    @FXML
    private AnchorPane itemDetailPane;
    @FXML
    private GridPane chargesPane;
    @FXML
    private RadioButton radioActive;
    @FXML
    private RadioButton radioPast;

    private MainMenuController main;
    private User user;
    private Reservation currentReservation;
    private BillingMenuDAO dao;
    private double taxRate = .08;

    private ObservableList<FolioCharge> charges;
    private ObservableList<BillingMenuDTO> guests;
    private ObservableList<Item> items;
    private double total;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        dao = new BillingMenuDAO();
        initGuestsTable();
        initChargesTable();
        initItemPane();
    }

    @FXML
    private void onClickCashPayment(ActionEvent event) {
        try {
            System.out.println("Starting cash payment");
            
            double cashAmount = Double.parseDouble(txtAmount.getText());
            double owedAmount = total;
            
            System.out.println("cashAmount:" + cashAmount);
            System.out.println("owedAmount:" + owedAmount);
            
            if (cashAmount>owedAmount)
                return;
            
            Item item = new Item("Cash", "Cash Deposit", BillingCode.OTHER, cashAmount*-1);
            
            addCharge(item);
            
        } catch (Exception e) {
            System.out.println("Error processing cash payment");
        }
    }

    @FXML
    private void onClickDeleteCharge(ActionEvent event) {
        FolioCharge charge = tblCharges.getSelectionModel().getSelectedItem();
        if (charge == null) {
            return;
        }
        try {

            dao.delCharge(charge.getId());
        } catch (Exception t) {
            System.out.println("error deleting charge");
        }
        handleSelectGuest();
    }

    @FXML
    private void onClickSelectGuest(ActionEvent event) {
        charges.clear();
        Label msg = new Label("Loading Guest Folio...");
        msg.setFont(new Font(24));
        msg.setOpacity(.5);
        tblCharges.setPlaceholder(msg);
        btnSelectGuest.setDisable(true);
        handleSelectGuest();
    }

    @FXML
    private void onClickPrintFolio(ActionEvent event) {

//        PrinterJob printerJob = PrinterJob.createPrinterJob();
//        if (printerJob == null) {
//            System.out.println("Null");
//        }
//        printerJob.showPrintDialog(HMS.stage);
    }

    @FXML
    private void onClickCheckOut(ActionEvent event) {
        handleCheckOut();
    }

    @FXML
    private void onSelectRadioActive(ActionEvent event) {
        charges.clear();
        btnCheckOut.setDisable(false);
        btnDeleteCharge.setDisable(false);
        btnCashPayment.setDisable(false);
        txtAmount.setDisable(false);
        spItems.setVisible(true);
        itemDetailPane.setVisible(true);
        updateActiveGuests();
    }

    @FXML
    private void onSelectRadioPast(ActionEvent event) {
        charges.clear();
        btnCheckOut.setDisable(true);
        btnDeleteCharge.setDisable(true);
        btnCashPayment.setDisable(true);
        txtAmount.setDisable(true);
        spItems.setVisible(false);
        itemDetailPane.setVisible(false);
        updatePastGuests();
    }

    @Override
    public void setSubMenuParent(MainMenuController main) {
        this.main = main;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    public void setCurrentReservation(Reservation currentReservation) {
        this.currentReservation = currentReservation;
    }

    private void initGuestsTable() {

        guests = FXCollections.observableArrayList();

        tblGuests.setItems(guests);

        colFirst.setCellValueFactory(
                new PropertyValueFactory<>("FirstName"));
        colLast.setCellValueFactory(
                new PropertyValueFactory<>("LastName"));
        colRoom.setCellValueFactory(
                new PropertyValueFactory<>("RoomNumber"));

        //Set Default Message
        Label msg = new Label("Guests");
        msg.setFont(new Font(24));
        msg.setOpacity(.5);
        tblGuests.setPlaceholder(msg);

        //Populate with data
        updateActiveGuests();

    }

    private void initChargesTable() {
        charges = FXCollections.observableArrayList();

        tblCharges.setItems(charges);

        colCode.setCellValueFactory(
                new PropertyValueFactory<>("Code"));
        colDescription.setCellValueFactory(
                new PropertyValueFactory<>("Description"));
        colDate.setCellValueFactory(
                new PropertyValueFactory<>("Date"));
        colAmount.setCellValueFactory(
                new PropertyValueFactory<>("Amount"));
        colAmount.setCellValueFactory(cellData
                -> Bindings.format("%.2f", cellData.getValue().getAmount())
        );

        //Set Default Message
        Label msg = new Label("Charges");
        msg.setFont(new Font(24));
        msg.setOpacity(.5);
        tblCharges.setPlaceholder(msg);

    }

    private void initItemPane() {

        items = FXCollections.observableArrayList();

        System.out.println("Retrieving list of Items from BillingMenuDAO");
        try {
            ObservableList result = dao.queryItems();
            if (result != null) {
                items = result;
            }

        } catch (Exception e) {
            System.out.println("Error getting current item list");
        }

        for (Item item : items) {

            item.setOnMouseEntered(e -> {
                lblItem.textProperty().bind(item.nameProperty());
                lblDescription.textProperty().bind(item.descriptionProperty());
                lblPrice.textProperty().bind(item.priceProperty().asString("$%4.2f"));
            });
            item.setOnMouseExited(e -> {
                lblItem.textProperty().unbind();
                lblDescription.textProperty().unbind();
                lblPrice.textProperty().unbind();
            });
            item.setOnMouseClicked(e -> {
                addCharge(item);
            });
            itemPane.getChildren().add(item);

        }

    }

    private void handleSelectGuest() {
        BillingMenuDTO selectedGuest = tblGuests.getSelectionModel().getSelectedItem();

        if (selectedGuest == null) {
            Alert alert = new Alert(AlertType.WARNING, "Please select a guest from the list");
            alert.showAndWait();
            return;
        }

        lblGuestName.setText(selectedGuest.getLastName() + ", " + selectedGuest.getFirstName());
        updateCharges(selectedGuest);

    }

    private void calculateTotals() {
        CurrencyStringConverter csc = new CurrencyStringConverter();
        total = 0;
        double subtotal = 0, taxes = 0;
        subtotal = tblCharges.getItems().stream().mapToDouble(FolioCharge::getAmount).sum();
        taxes = subtotal * taxRate;
        total = subtotal + taxes;
        lblTotal.setText(csc.toString(total));
        lblTaxes.setText(csc.toString(taxes));
        lblSubTotal.setText(csc.toString(subtotal));
    }

    public void updateActiveGuests() {
        try {
            guests.clear();
            charges.clear();
            System.out.println("Retrieving list of Active Guests from BillingMenuDAO");
            ObservableList<BillingMenuDTO> result = dao.queryCurrentGuests();
            if (result != null) {
                guests.setAll(result);
            }
            for (BillingMenuDTO d : result) {
                System.out.println(d);
            }
        } catch (Exception e) {
            System.out.println("Error getting current guest list");
        }

    }

    public void updatePastGuests() {
        guests.clear();
        try {
            System.out.println("Retrieving list of Past Guests from BillingMenuDAO");
            ObservableList<BillingMenuDTO> result = dao.queryPastGuests();
            if (result != null) {
                guests.setAll(result);
            }
            for (BillingMenuDTO d : result) {
                System.out.println(d);
            }
        } catch (Exception e) {
            System.out.println("Error getting current guest list");
        }

    }

    private void updateCharges(BillingMenuDTO selectedGuest) {
        try {
            
            postRoomCharges(selectedGuest);

            System.out.println("Retriving list of charges for guest: "
                    + selectedGuest.getConfirmation()
                    + " "
                    + selectedGuest.getFirstName()
                    + " "
                    + selectedGuest.getLastName()
                    + " "
                    + selectedGuest.getRoomNumber());
            //charges = dao.queryCharges(1234);
            charges = dao.queryCharges(selectedGuest.getConfirmation());
            tblCharges.setItems(charges);
            calculateTotals();
        } catch (Exception e) {
            System.out.println("Error retrieving current guest list");
        }
        if (radioActive.isSelected())
            postRoomCharges(selectedGuest);
    }

    private void addCharge(Item item) {
        try {

            //Get Confirmation Number
            int confirmationNumber = tblGuests
                    .getSelectionModel()
                    .getSelectedItem()
                    .getConfirmation();

            //Create Charge 
            FolioCharge charge = new FolioCharge(
                    0,
                    item.getCode(),
                    item.getDescription(),
                    LocalDate.now().toString(),
                    item.getPrice()
            );
            System.out.println("Adding Charge...");
            dao.addCharge(confirmationNumber, charge);
        } catch (Exception e) {
            System.out.println("Error adding Charge");
        }
        handleSelectGuest();
    }

    private void postRoomCharges(BillingMenuDTO guest) {
        try {
            
        //Delete Old Room Charges
        System.out.println("Deleting old Room Charges...");
        dao.deleteRoomDays(guest.getConfirmation());

        //Add Room Charges from start of reservation to todays date
        LocalDate today = LocalDate.now();
        LocalDate start = LocalDate.parse(guest.getArrivalDate());
        long x = ChronoUnit.DAYS.between(start, today);
        
        for (; x >= 0; x--) {
            String tmpDate = start.toString();
            
            //Create Charge 
            FolioCharge charge = new FolioCharge(
                    0,
                    BillingCode.ROOM,
                    "Room#"+guest.getRoomNumber(),
                    tmpDate,
                    150.00);
            
            System.out.println("Posting charge for : " + tmpDate);
            dao.addCharge(guest.getConfirmation(), charge);
            
            //Increment the day
            start = start.plusDays(1);
        }
        
        } catch (Exception e) {
            System.out.println("Error posting room transactions");
        }
        
        //Re-Enable the button
        Label msg = new Label("Guest Charges");
        msg.setFont(new Font(24));
        msg.setOpacity(.5);
        tblCharges.setPlaceholder(msg);
        btnSelectGuest.setDisable(false);

    }

    private void handleCheckOut() {
        BillingMenuDTO guest = tblGuests.getSelectionModel().getSelectedItem();
        if (guest == null) {
            return;
        }
        
        //Populate Charges
        handleSelectGuest();
        
        boolean result = false;
        try {
            System.out.println("Checking Out Guest");
            result = dao.checkOutGuest(guest.getConfirmation(),guest.getRoomNumber(), total);
        } catch (Exception e) {
            System.out.println("Error while checking out the guest");
        }
        if (result) {
            updateActiveGuests();
            Alert alert = new Alert(AlertType.INFORMATION,
                    String.format(
                            "You are checked out of room %s\nYour "
                            + "credit card has been billed $%.2f"
                            ,guest.getRoomNumber(),total));
            alert.showAndWait();
        } else {
            Alert alert = new Alert(AlertType.ERROR,
                "You're checkout could not be processed at this time");
            alert.showAndWait();
        }
    }

}
