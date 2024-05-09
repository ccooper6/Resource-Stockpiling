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
import seng201.team0.services.InventoryService;
import seng201.team0.services.MoneyBalanceService;
import seng201.team0.services.RoundsSelectionService;

/**
 * Controller for the sell_screen.fxml window
 * @author Caleb Cooper
 */
public class SellScreenController {

    private final GameManager gameManager;
    private final RoundsSelectionService roundsService;
    private final MoneyBalanceService moneyService;
    private final CurrentRoundService currentRoundService;
    private final InventoryService inventoryService;

    @FXML
    public Label title;
    public Label currentMoney;
    public Label currentMoneyLabel;
    public Label currentRound;
    public Label currentRoundLabel;
    public Label roundsRemaining;
    public Label roundsRemainingLabel;
    public Label mainTowers;
    public ListView<Tower> mainTowerList;
    public Label reserveTowers;
    public ListView<Tower> reserveTowerList;
    public Label upgrades;
    public ListView<Upgrade> upgradeList;
    public Button sellTower;
    public Button sellUpgrade;
    public Button backButton;

    private Tower selectedMainTower;
    private Tower selectedReserveTower;
    private Upgrade selectedUpgrade;
    private Boolean isTowerSelectedMain;

    /**
     * Constructor
     * @param gameManager an instance of GameManger that is linked through the entirety of the game in order to keep it
     *                    all linked.
     */
    public SellScreenController(GameManager gameManager) {
        this.gameManager = gameManager;
        this.roundsService = gameManager.getRoundsService();
        this.moneyService = gameManager.getMoneyService();
        this.currentRoundService = gameManager.getCurrentRoundService();
        this.inventoryService = gameManager.getInventoryService();
    }

    /**
     * Initialize the window, sets up the buttons to change colour when hovered over, displays the users current money, round and rounds remaining.
     * Sets up the three list views - main towers, reserve towers and upgrades that are in the users inventory
     */
    public void initialize() {

        backButton.setOnMouseEntered(event -> backButton.setStyle("-fx-background-color: #999999"));
        backButton.setOnMouseExited(event -> backButton.setStyle(""));

        sellTower.setOnMouseEntered(event -> sellTower.setStyle("-fx-background-color: #999999"));
        sellTower.setOnMouseExited(event -> sellTower.setStyle(""));

        sellUpgrade.setOnMouseEntered(event -> sellUpgrade.setStyle("-fx-background-color: #999999"));
        sellUpgrade.setOnMouseExited(event -> sellUpgrade.setStyle(""));

        int remainingRounds = roundsService.getRoundsSelection() - currentRoundService.getCurrentRound();
        currentMoneyLabel.setText("$" + moneyService.getCurrentBalance().toString());
        currentRoundLabel.setText(currentRoundService.getCurrentRound().toString());
        roundsRemainingLabel.setText(Integer.toString(remainingRounds));

        mainTowerList.setCellFactory(new TowerCellFactory());
        mainTowerList.setItems(FXCollections.observableArrayList(inventoryService.getMainTowerSelection()));
        // Used Tut 3 code here as a template
        mainTowerList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tower>) r -> {
            selectedMainTower = mainTowerList.getSelectionModel().getSelectedItem();
            isTowerSelectedMain = true;
        });

        reserveTowerList.setCellFactory(new TowerCellFactory());
        reserveTowerList.setItems(FXCollections.observableArrayList(inventoryService.getReserveTowerSelection()));
        reserveTowerList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Tower>) r -> {
            selectedReserveTower = reserveTowerList.getSelectionModel().getSelectedItem();
            isTowerSelectedMain = false;
        });

        upgradeList.setCellFactory(new UpgradeCellFactory());
        upgradeList.setItems(FXCollections.observableArrayList(inventoryService.getUserUpgrades()));
        upgradeList.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Upgrade>) r -> {
            selectedUpgrade = upgradeList.getSelectionModel().getSelectedItem();
        });

    }

    /**
     * Method called when the back button is clicked, resets the pane and displays the shop screen
     */
    @FXML
    private void onBackButtonClicked() {
        gameManager.resetAndOpenShopScreen();
    }

    /**
     * Removes selected tower from the users inventory and adds money to the users balance as long as prerequisites are met
     * otherwise opens an error dialog
     */
    @FXML
    private void onSellTowerButtonClicked() {
        if (isTowerSelectedMain != null) {
            if (isTowerSelectedMain) {
                if (inventoryService.getMainTowerSelection().size() != 1) {
                    inventoryService.removeMainTower(selectedMainTower);
                    moneyService.setNewBalance(moneyService.getCurrentBalance() + selectedMainTower.getSellPrice());
                    currentMoneyLabel.setText("$" + moneyService.getCurrentBalance().toString());

                    mainTowerList.getItems().remove(selectedMainTower);
                    clearSelections();
                } else {
                    openErrorDialog("Error - There must always be at least one main tower");
                }
            } else {
                inventoryService.removeReserveTower(selectedReserveTower);
                moneyService.setNewBalance(moneyService.getCurrentBalance() + selectedReserveTower.getSellPrice());
                currentMoneyLabel.setText("$" + moneyService.getCurrentBalance().toString());

                reserveTowerList.getItems().remove(selectedReserveTower);
                clearSelections();
            }
        } else {
            openErrorDialog("Error - Please select a tower to sell");
        }
    }

    /**
     * Removes any selection from the list view and sets all values to be none
     */
    private void clearSelections() {
        mainTowerList.getSelectionModel().clearSelection();
        reserveTowerList.getSelectionModel().clearSelection();
        upgradeList.getSelectionModel().clearSelection();
        selectedMainTower = null;
        selectedReserveTower = null;
        isTowerSelectedMain = null;
        selectedUpgrade = null;
    }

    /**
     * Called when the sell upgrade button is clicked, removes selected upgrade from inventory and adds money to users balance.
     * Opens error dialog if no upgrade is selected
     */
    @FXML
    private void onSellUpgradeButtonClicked() {
        if (selectedUpgrade != null) {
            inventoryService.removeUserUpgrade(selectedUpgrade);
            moneyService.setNewBalance(moneyService.getCurrentBalance() + selectedUpgrade.getCost());
            currentMoneyLabel.setText("$" + moneyService.getCurrentBalance().toString());

            upgradeList.getItems().remove(selectedUpgrade);
            clearSelections();
        } else {
            openErrorDialog("Error - Please select an upgrade to sell");
        }
    }

    /**
     * Displays information about the provided error in a dialog box
     * @param message the error that should be displayed to the user
     */
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