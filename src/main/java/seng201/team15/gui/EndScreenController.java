package seng201.team15.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import seng201.team15.GameManager;
import seng201.team15.services.*;

/**
 * Controller for the end_screen.fxml window
 * @author Caleb Cooper
 */
public class EndScreenController {


    private final GameManager gameManager;
    private final NameInputService nameService;
    private final RoundsSelectionService roundsService;
    private final MoneyBalanceService moneyService;
    private final CurrentRoundService currentRoundService;
    private final InventoryService inventoryService;
    private final ShopAvailabilityService shopAvailabilityService;
    private final PlayerScoreService playerScoreService;

    @FXML
    private Label gameMessage;
    @FXML
    private Label roundsChosenLabel;
    @FXML
    private Label roundsCompletedLabel;
    @FXML
    private Label moneyGainedLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Button exitButton;
    @FXML
    private Button restartGameButton;


    /**
     * Constructor
     * @param gameManager an instance of GameManger that is linked through the entirety of the game in order to keep it
     *                    all linked.
     */
    public EndScreenController(GameManager gameManager) {
        this.gameManager = gameManager;
        this.roundsService = gameManager.getRoundsService();
        this.moneyService = gameManager.getMoneyService();
        this.currentRoundService = gameManager.getCurrentRoundService();
        this.inventoryService = gameManager.getInventoryService();
        this.nameService = gameManager.getNameService();
        this.shopAvailabilityService = gameManager.getShopAvailabilityService();
        this.playerScoreService = gameManager.getPlayerScoreService();
    }

    /**
     * Initialize the window, sets up the buttons to change colour when hovered over, checks whether the game was won or lost and
     * displays a message accordingly, displays the users game stats
     */
    public void initialize() {
        restartGameButton.setOnMouseEntered(event -> restartGameButton.setStyle("-fx-background-color: #999999"));
        restartGameButton.setOnMouseExited(event -> restartGameButton.setStyle(""));

        exitButton.setOnMouseEntered(event -> exitButton.setStyle("-fx-background-color: #999999"));
        exitButton.setOnMouseExited(event -> exitButton.setStyle(""));

        if (currentRoundService.getGameSuccess()) {
            gameMessage.setText("Congratulations "+ nameService.getCurrentName() + ", you Won!");
        } else {
            gameMessage.setText(nameService.getCurrentName() + ", you Lost!");
        }

        Integer roundsChosen = roundsService.getRoundsSelection();
        Integer remainingRounds = roundsService.getRoundsSelection() - currentRoundService.getCurrentRound();
        Integer totalEarnings = moneyService.getTotalEarnings();

        roundsChosenLabel.setText(roundsService.getRoundsSelection().toString());
        roundsCompletedLabel.setText(String.valueOf(roundsChosen - remainingRounds));
        moneyGainedLabel.setText("$" + totalEarnings);
        scoreLabel.setText(String.valueOf(playerScoreService.getPlayerScore()));

    }

    /**
     * Method called if the restart game button is clicked, resets all the service classes so that a fresh game can be played.
     */
    @FXML
    private void onRestartGameButtonClicked() {
        currentRoundService.setDifficulty("reset");
        currentRoundService.setCurrentRound(1);
        inventoryService.resetInventory();
        shopAvailabilityService.resetStore(1);
        playerScoreService.setPlayerScore(0);
        currentRoundService.setCarts();
        moneyService.subtractBalance(moneyService.getCurrentBalance());
        gameManager.resetAndLaunchMenuScreen();
    }

    /**
     * Method called when the exit button is clicked, closes the game window and exits
     */
    @FXML
    private void onExitButtonClicked() {
        System.exit(0);
    }
}