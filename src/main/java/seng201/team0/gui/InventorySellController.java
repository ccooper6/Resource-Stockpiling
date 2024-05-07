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
 * Controller for the inventory.fxml window
 * @author Caleb Cooper
 */
public class InventorySellController {

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

    private GameManager gameManager;
    private RoundsSelectionService roundsService;
    private MoneyBalanceService moneyService;
    private CurrentRoundService currentRoundService;
    private InventoryService inventoryService;

    private Tower selectedMainTower;
    private Tower selectedReserveTower;
    private Upgrade selectedUpgrade;
    private Boolean isTowerSelectedMain;

    /**
     * Constructor
     * @param gameManager an instance of GameManger that is linked through the entirety of the game in order to keep it
     *                    all linked.
     */
    public InventorySellController(GameManager gameManager) {
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
     * Method to call when the back button is clicked
     */
    @FXML
    private void onBackButtonClicked() {
        gameManager.resetAndOpenShopScreen();
    }

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
    private void clearSelections() {
        mainTowerList.getSelectionModel().clearSelection();
        reserveTowerList.getSelectionModel().clearSelection();
        selectedMainTower = null;
        selectedReserveTower = null;
        isTowerSelectedMain = null;
    }

    @FXML
    private void onSellUpgradeButtonClicked() {
        if (selectedUpgrade != null) {
            inventoryService.removeUserUpgrade(selectedUpgrade);
            moneyService.setNewBalance(moneyService.getCurrentBalance() + selectedUpgrade.getCost());
            currentMoneyLabel.setText("$" + moneyService.getCurrentBalance().toString());

            upgradeList.getItems().remove(selectedUpgrade);
            upgradeList.getSelectionModel().clearSelection();
            selectedUpgrade = null;
        } else {
            openErrorDialog("Error - Please select an upgrade to sell");
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
