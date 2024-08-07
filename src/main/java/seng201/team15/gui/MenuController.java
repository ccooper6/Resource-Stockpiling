package seng201.team15.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import seng201.team15.GameManager;
import seng201.team15.models.Tower;
import seng201.team15.models.Upgrade;
import seng201.team15.models.towertypes.*;
import seng201.team15.services.*;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for the menu.fxml window
 * @author Caleb Cooper, Quinn Le Lievre
 */
public class MenuController {

    private final GameManager gameManager;
    private final DifficultySelectionService difficultyService;
    private final NameInputService nameService;
    private final RoundsSelectionService roundsService;
    private final InventoryService inventoryService;
    private final MoneyBalanceService moneyService;
    private final CurrentRoundService currentRoundService;
    private final ShopAvailabilityService shopAvailabilityService;

    @FXML
    private TextField nameInput;
    @FXML
    private Slider roundsSlider;
    @FXML
    private ChoiceBox<String> difficultyDropdown;
    @FXML
    private Button towerButton1;
    @FXML
    private Button towerButton2;
    @FXML
    private Button towerButton3;
    @FXML
    private Button towerButton4;
    @FXML
    private Label selectedTowerResourcesLabel;
    @FXML
    private Label selectedTowerReloadSpeedLabel;
    @FXML
    private Label selectedTowerResourceTypeLabel;
    @FXML
    private Label selectedTowerLevelLabel;
    @FXML
    private Label selectedTowerCostLabel;
    @FXML
    private Button submitButton;
    @FXML
    private GridPane towerGrid;

    private int selectedRounds = 5;
    private final ArrayList<Tower> towerTypes = new ArrayList<>();
    private final ArrayList<Integer> tempSelectedTowers = new ArrayList<>();
    private final ArrayList<Tower> selectedTowers = new ArrayList<>();
    private final List<String> errorsList = new ArrayList<>();



    /**
     * Constructor
     * @param gameManager an instance of GameManger that is linked through the entirety of the game in order to keep it
     *                    all linked.
     */
    public MenuController(GameManager gameManager) {
        this.gameManager = gameManager;
        this.difficultyService = gameManager.getDifficultyService();
        this.nameService = gameManager.getNameService();
        this.roundsService = gameManager.getRoundsService();
        this.inventoryService = gameManager.getInventoryService();
        this.moneyService = gameManager.getMoneyService();
        this.currentRoundService = gameManager.getCurrentRoundService();
        this.shopAvailabilityService = gameManager.getShopAvailabilityService();
    }

    /**
     * Initialize the window, sets up the buttons to change colour when hovered over, displays the users current money, round and rounds remaining.
     * Sets up all the elements that make up the menu page
     */
    public void initialize() {
        difficultyDropdown.getItems().addAll("Easy", "Medium", "Hard", "Impossible"); //https://www.youtube.com/watch?v=K3CenJ2bMok&ab_channel=thenewboston

        roundsSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            selectedRounds = newValue.intValue();
        });

        submitButton.setOnMouseEntered(event -> submitButton.setStyle("-fx-background-color: #A08B27"));
        submitButton.setOnMouseExited(event -> submitButton.setStyle("-fx-background-color: #D4AF37"));

        Collections.addAll(towerTypes, new StartingTowerOne(), new StartingTowerTwo(), new StartingTowerThree(), new StartingTowerFour());

        //Tutorial 2
        List<Button> towerButtons = List.of(towerButton1, towerButton2, towerButton3, towerButton4);

        //https://www.tutorialspoint.com/how-to-add-an-image-to-a-button-action-in-javafx

        int tempIndex = 0;
        for(Tower tower: towerTypes) {
            ImageView imageView = new ImageView("/images/deposit-" + tower.getResourceType().toLowerCase() + ".png");
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            towerButtons.get(tempIndex).setGraphic(imageView);
            tempIndex++;
        }


        for (int i = 0; i < 4; i++) {
            final int finalI = i;
            towerButtons.get(i).setOnAction(event -> {
                if (tempSelectedTowers.contains(finalI)) {
                    showStats(finalI);
                    tempSelectedTowers.remove(Integer.valueOf(finalI)); // Use Integer.valueOf to remove by object (the value) not index
                    towerButtons.get(finalI).setStyle(""); // Reset style
                } else {
                    if (tempSelectedTowers.size() < 3) {
                        showStats(finalI);
                        tempSelectedTowers.add(finalI); // Add tower to the ArrayList
                        towerButtons.get(finalI).setStyle("-fx-background-color: #7d7d7d; -fx-background-radius: 5;"); // Set button to look like it has been selected
                    }
                }
            });
        }
    }

    /**
     * Method to show stats of the selected tower
     * @param towerIndex the index of the intended tower to view
     */
    private void showStats(int towerIndex) {
        Tower selectedTower = towerTypes.get(towerIndex);
        selectedTowerResourcesLabel.setText("Resources: " + selectedTower.getResourceAmount());
        selectedTowerReloadSpeedLabel.setText("Reload Speed: " + selectedTower.getReloadSpeed());
        selectedTowerResourceTypeLabel.setText("Resource Type: " + selectedTower.getResourceType());
        selectedTowerLevelLabel.setText("Level: " + selectedTower.getLevel());
        selectedTowerCostLabel.setText("Cost: " + selectedTower.getCost());
    }

    /**
     * Method to call when the name submit button is clicked, checks to see if any errors have occurred and adds them to a list
     * to be displayed. Otherwise, sets and stores the choices in the service classes
     */
    @FXML
    private void onSubmitButtonClicked() {
        /*
        Used code found on https://stackoverflow.com/questions/1795402/check-if-a-string-contains-a-special-character to figure
        out how to find if a string contained special characters
        */
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(nameInput.getText());

        nameInput.setStyle("");
        difficultyDropdown.setStyle("");
        towerGrid.setStyle("");

        currentRoundService.setTotalRounds(selectedRounds);
        shopAvailabilityService.setTotalRounds(selectedRounds);
        shopAvailabilityService.resetStore(1);

        /*
        Stores an int to check how many errors there are.
        Displays all current error messages if there are any.
        */
       int errors = 0;
        if (nameInput.getText().length() < 3 || nameInput.getText().length() > 15 || m.find()) {
            //If matcher finds a character not in a-z, A-Z or 0-9, or length is not between 3 and 15, run error
            //errorsLabelResult.setText(errorsLabelResult.getText() + "Error - Please enter a valid name\n");
            errorsList.add("Please enter a valid name");
            nameInput.setStyle("-fx-text-box-border: red");
            nameInput.setText("");
            errors += 1;

        }
        if (difficultyDropdown.getValue() == null) {
            errorsList.add("Please select a difficulty");
            difficultyDropdown.setStyle("-fx-border-color: red");
            errors += 1;

        }
        if (tempSelectedTowers.size() != 3) {
            errorsList.add("Please select at least 3 starting deposits");
            towerGrid.setStyle("-fx-border-color: red");
            errors += 1;

        }
        if (errors == 0) {
            nameService.setNewName(nameInput.getText());
            roundsService.setRoundsSelection(selectedRounds);
            difficultyService.setDifficultySelection(difficultyDropdown.getValue());
            for (Integer tempSelectedTower : tempSelectedTowers) {
                selectedTowers.add(towerTypes.get(tempSelectedTower));
            }
            inventoryService.setMainTowerSelection(selectedTowers);

            String difficulty = difficultyService.getDifficultySelection();
            setStartingAddons(difficulty);

            gameManager.resetAndLaunchMainScreen();
        } else {
            openErrorDialog();
        }
    }

    /**
     * Opens a dialog box to display each random event that has occurred within errorsList
     */
    private void openErrorDialog() {
        //Tutorial 2 Extension
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Error");
        VBox dialogContent = new VBox(10);
        for (String error: errorsList) {
            dialogContent.getChildren().add(new Label(error));
        }
        dialog.getDialogPane().setContent(dialogContent);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.show();
        errorsList.clear();
    }

    /**
     * Based on the difficulty, gives the user a helping hand to give a noticeable assistance for easier difficulties - essentially makes it quite difficult to lose
     * starting rounds with easy or medium as the user should be able to match all carts
     * @param difficulty users choice of difficulty
     */
    private void setStartingAddons(String difficulty) {
        if (Objects.equals(difficulty, "Easy")) { //Used Object.equals instead of == just in case of null value
            moneyService.addBalance(100);
            inventoryService.addUserUpgrade(new Upgrade("Change Resource Type to Diamond", "Resource Type" , "Diamond", 0, 1.0));
            inventoryService.addUserUpgrade(new Upgrade("Change Resource Type to Gold", "Resource Type" , "Gold", 0, 1.0));
            inventoryService.addUserUpgrade(new Upgrade("Change Resource Type to Copper", "Resource Type" , "Copper", 0, 1.0));
        } else if (Objects.equals(difficulty, "Medium")) {
            moneyService.addBalance(75);
            inventoryService.addUserUpgrade(new Upgrade("Change Resource Type to Gold", "Resource Type" , "Gold", 0, 1.0));
        } else if (Objects.equals(difficulty, "Hard")) {
            moneyService.addBalance(50);
        } else  {
            moneyService.addBalance(25);
        }
    }
}