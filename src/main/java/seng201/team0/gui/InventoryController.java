package seng201.team0.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import seng201.team0.GameManager;
import seng201.team0.gui.cellfactories.TowerCellFactory;
import seng201.team0.gui.cellfactories.UpgradeCellFactory;
import seng201.team0.models.Tower;
import seng201.team0.models.Upgrade;
import seng201.team0.services.CurrentRoundService;
import seng201.team0.services.MoneyBalanceService;
import seng201.team0.services.RoundsSelectionService;
import seng201.team0.services.InventoryService;

/**
 * Controller for the inventory.fxml window
 * @author Caleb Cooper
 */
public class InventoryController {

    private GameManager gameManager;
    private RoundsSelectionService roundsService;
    private MoneyBalanceService moneyService;
    private CurrentRoundService currentRoundService;
    private InventoryService inventoryService;
    private Tower selectedMainTower;
    private Tower selectedReserveTower;
    private Upgrade selectedUpgrade;

    @FXML
    public Label inventoryLabel;
    public Label currentRound;
    public Label roundsRemaining;
    public Label currentMoney;
    public Label currentMoneyLabel;
    public Label currentRoundLabel;
    public Label roundsRemainingLabel;
    public ListView<Tower> mainTowerList;
    public ListView<Tower> reserveTowerList;
    public ListView<Upgrade> upgradeList;
    public Button moveTowerButton;
    public Button useUpgradeButton;
    public Button backButton;
    public Label upgrades;
    public Label reserveTowers;
    public Label mainTowers;

    private String towerSelected;
    private boolean upgradeSelected = false;

    /**
     * Constructor
     * @param gameManager an instance of GameManger that is linked through the entirety of the game in order to keep it
     *                    all linked.
     */
    public InventoryController(GameManager gameManager) {
        this.gameManager = gameManager;
        this.roundsService = gameManager.getRoundsService();
        this.moneyService = gameManager.getMoneyService();
        this.currentRoundService = gameManager.getCurrentRoundService();
        this.inventoryService = gameManager.getInventoryService();
    }
    /**
     * Initialize the window
     */
    public void initialize() {

        backButton.setOnMouseEntered(event -> backButton.setStyle("-fx-background-color: #999999"));
        backButton.setOnMouseExited(event -> backButton.setStyle(""));

        moveTowerButton.setOnMouseEntered(event -> moveTowerButton.setStyle("-fx-background-color: #999999"));
        moveTowerButton.setOnMouseExited(event -> moveTowerButton.setStyle(""));

        useUpgradeButton.setOnMouseEntered(event -> useUpgradeButton.setStyle("-fx-background-color: #999999"));
        useUpgradeButton.setOnMouseExited(event -> useUpgradeButton.setStyle(""));

        int remainingRounds = roundsService.getRoundsSelection() - currentRoundService.getCurrentRound();
        currentMoneyLabel.setText("$" + moneyService.getCurrentBalance().toString());
        currentRoundLabel.setText(currentRoundService.getCurrentRound().toString());
        roundsRemainingLabel.setText(Integer.toString(remainingRounds));

        mainTowerList.setCellFactory(new TowerCellFactory());
        mainTowerList.setItems(FXCollections.observableArrayList(inventoryService.getMainTowerSelection()));
        // Used Tut 3 code here as a template
        mainTowerList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tower>) r -> {
            selectedMainTower = mainTowerList.getSelectionModel().getSelectedItem();
            towerSelected = "Main";
        });

        reserveTowerList.setCellFactory(new TowerCellFactory());
        reserveTowerList.setItems(FXCollections.observableArrayList(inventoryService.getReserveTowerSelection()));
        reserveTowerList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tower>) r -> {
            selectedReserveTower = reserveTowerList.getSelectionModel().getSelectedItem();
            towerSelected = "Reserve";
        });

        upgradeList.setCellFactory(new UpgradeCellFactory());
        upgradeList.setItems(FXCollections.observableArrayList(inventoryService.getUserUpgrades()));
        upgradeList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Upgrade>) r -> {
            selectedUpgrade = upgradeList.getSelectionModel().getSelectedItem();
            upgradeSelected = true;
        });
    }
    /**
     * Method to call when the back button is clicked
     */
    @FXML
    private void onBackButtonClicked() {
        gameManager.resetAndLaunchMainScreen();
    }

    @FXML
    private void onMoveTowerButtonClicked() {
        if (towerSelected != null) {
            if (towerSelected.equals("Main")) {
                if (inventoryService.getMainTowerSelection().size() == 1) {
                    openErrorDialog("Error - There must always be at least one main tower");
                } else if(inventoryService.getReserveTowerSelection().size() < 5) {
                    inventoryService.addToReserveTowerSelection(selectedMainTower);
                    reserveTowerList.getItems().add(selectedMainTower);

                    inventoryService.removeMainTower(selectedMainTower);
                    mainTowerList.getItems().remove(selectedMainTower);
                } else {
                    openErrorDialog("Error - Too many towers in reserve towers");
                }
                    clearSelections();
            } else {
                if (inventoryService.getMainTowerSelection().size() < 5) {
                    inventoryService.addToMainTowerSelection(selectedReserveTower);
                    mainTowerList.getItems().add(selectedReserveTower);

                    inventoryService.removeReserveTower(selectedReserveTower);
                    reserveTowerList.getItems().remove(selectedReserveTower);
                } else {
                    openErrorDialog("Error - Too many towers in main towers");
                }
                clearSelections();
            }
        } else {
            openErrorDialog("Error - Please select a tower");
        }
    }

    private void clearSelections() {
        mainTowerList.getSelectionModel().clearSelection();
        reserveTowerList.getSelectionModel().clearSelection();
        upgradeList.getSelectionModel().clearSelection();
        selectedMainTower = null;
        selectedReserveTower = null;
        selectedUpgrade = null;
        towerSelected = null;
        upgradeSelected = false;
    }

    @FXML
    private void onUseUpgradeButtonClicked() {
        if (upgradeSelected && towerSelected != null) {
            if (towerSelected.equals("Main")) {
                selectedMainTower.applyUpgrade(selectedUpgrade);
            } else {
                selectedReserveTower.applyUpgrade(selectedUpgrade);
            }
            inventoryService.removeUserUpgrade(selectedUpgrade);
            upgradeList.getItems().remove(selectedUpgrade);
            clearSelections();
        } else {
            openErrorDialog("Error - Please select both a tower AND an upgrade");
        }
    }

    private void openErrorDialog(String message) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Error");
        VBox dialogContent = new VBox(10);
        dialogContent.getChildren().add(new Label(message));
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.show();
    }
}