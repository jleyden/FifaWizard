package FifaWizard;

/**
 * Created by jleyden on 3/23/16.
 */

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class FifaWizard extends Application {

    int windowWidth;
    int windowHeight;
    String currDisplay;

    Group mainRoot;
    Group menuRoot;
    Group infoRoot;
    Group gameRoot;
    Scene scene;
    InputInfoHandler inputInfoHandler;
    MouseClickEventHandler mouseClickEventHandler;
    ImageMouseClick imageMouseClick;

    //Main Menu Items
    Text title;
    Text infoButton;
    Text gameButton;

    //Players
    Player gabe;
    Player matt;
    Player john;
    Player joey;
    public ArrayList<Player> playerList;
    public ArrayList<ImageView> playerImages;

    //Games
    public ArrayList<Game> gameList;

    //Player Info Items
    int imageHeight = 100;
    int imageWidth = 100;
    Text backButton;

    //Game Menu Items
    Text gamePrompt;
    Text winner;
    Text loser;
    Text winningTeam;
    Text winningScore;
    Text losingTeam;
    Text losingScore;
    Text currGameText;
    Text confirmation;
    String gameMenuStatus;
    ArrayList<ImageView> playerPics;
    Player currWinner;
    Player currLoser;
    String currWinningTeam;
    String currLosingTeam;
    int currWinningScore;
    int currLosingScore;

    private class MouseClickEventHandler implements  EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            int mousePressedX = (int) Math.round(mouseEvent.getX());
            int mousePressedY = (int) Math.round(mouseEvent.getY());
            if (currDisplay == "mainMenu") {
                mainMenuMouseClick(mousePressedX, mousePressedY);
            } else if (currDisplay == "infoMenu") {
                infoMenuMouseClick(mousePressedX, mousePressedY);
            } else if (currDisplay == "gameMenu") {
                gameMenuMouseClick(mousePressedX, mousePressedY);
            }

        }
    }

    private int imageLeftBound(ImageView i) {
        return (int) i.getX();
    }

    private int imageRightBound(ImageView i) {
        return imageLeftBound(i) + (int) i.getLayoutBounds().getWidth();
    }

    private class ImageMouseClick implements  EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (currDisplay == "gameMenu") {
                if (gameMenuStatus == "confirmation") {
                    implementGame();
                }
                int mousePressedX = (int) Math.round(mouseEvent.getX());
                for (ImageView i : playerPics) {
                    if (mousePressedX >= imageLeftBound(i) && mousePressedX <= imageRightBound(i)) {
                        if (gameMenuStatus == "winner") {
                            setWinner(i);
                        } else if (gameMenuStatus == "loser") {
                            setLoser(i);
                        }
                    }
                }
            }
        }
    }

    private void setWinner (ImageView i) {
        winner.setY(i.getY() - 20);
        winner.setX(i.getX());
        gameRoot.getChildren().add(winner);
        for (Player p : playerList) {
            if (p.view.equals(i)) {
                currWinner = p;
            }
        }
        winningTeam.setX(i.getX());
        winningTeam.setY(i.getY() + i.getLayoutBounds().getHeight() + 10);
        winningScore.setX(i.getX());
        winningScore.setY(winningTeam.getY() + winningTeam.getLayoutBounds().getHeight() + 10);
        gameMenuStatus = "loser";
        gamePrompt.setText("Who lost the game?");
        centerText(gamePrompt);
    }

    private void setLoser (ImageView i) {
        loser.setY(i.getY() - 20);
        loser.setX(i.getX());
        gameRoot.getChildren().add(loser);
        for (Player p : playerList) {
            if (p.view.equals(i)) {
                currLoser = p;
            } else if (!p.equals(currWinner)) {
                gameRoot.getChildren().remove(p.view);
            }
        }
        losingTeam.setX(i.getX());
        losingTeam.setY(i.getY() + i.getLayoutBounds().getHeight() + 10);
        losingScore.setX(i.getX());
        losingScore.setY(losingTeam.getY() + losingTeam.getLayoutBounds().getHeight() + 10);
        gameMenuStatus = "winTeam";
        gamePrompt.setText("What team did " + currWinner.name + " play with?");
        centerText(gamePrompt);
        currGameText = winningTeam;
        currGameText.setOnKeyPressed(inputInfoHandler);
        currGameText.setOnKeyTyped(inputInfoHandler);
        gameRoot.getChildren().add(winningTeam);
    }

    private void centerText(Text t) {
        t.setX((windowWidth - t.getLayoutBounds().getWidth()) / 2);
    }

    private class InputInfoHandler implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown()) {

                String characterTyped = keyEvent.getCharacter();

                if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    // Ignore control keys, which have non-zero length, as well as the backspace key, which is
                    // represented as a character of value = 8 on Windows
                    String lastChar = currGameText.getText().substring(
                            currGameText.getText().length()-1, currGameText.getText().length());
                    if (characterTyped.equals("\r") && !lastChar.equals(":")) {
                        characterTyped = "";
                        completeInput();
                    }
                    if (gameMenuStatus == "winScore" || gameMenuStatus == "loseScore") {
                        try {
                            Integer.parseInt(characterTyped);
                        } catch (NumberFormatException n) {
                            characterTyped = "";
                        }
                    }
                    currGameText.setText(currGameText.getText() + characterTyped);
                    keyEvent.consume();
                }

            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {

                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.BACK_SPACE) {
                    currGameText.setText(removeLast(currGameText.getText()));
                }
                keyEvent.consume();
            }
        }
    }

    public void completeInput() {
        if (gameMenuStatus == "winTeam") {
            currWinningTeam = currGameText.getText().substring(5, currGameText.getText().length());
            currGameText.removeEventHandler(KeyEvent.KEY_TYPED, inputInfoHandler);
            currGameText.removeEventHandler(KeyEvent.KEY_PRESSED, inputInfoHandler);
            currGameText = winningScore;
            currGameText.setOnKeyTyped(inputInfoHandler);
            currGameText.setOnKeyPressed(inputInfoHandler);
            gameRoot.getChildren().add(currGameText);
            gameMenuStatus = "winScore";
            gamePrompt.setText("How many goals did " + currWinner.name + " score?");
            centerText(gamePrompt);
        } else if (gameMenuStatus == "winScore") {
            currWinningScore = Integer.parseInt(currGameText.getText().substring(6, currGameText.getText().length()));
            currGameText.removeEventHandler(KeyEvent.KEY_TYPED, inputInfoHandler);
            currGameText.removeEventHandler(KeyEvent.KEY_PRESSED, inputInfoHandler);
            currGameText = losingTeam;
            currGameText.setOnKeyTyped(inputInfoHandler);
            currGameText.setOnKeyPressed(inputInfoHandler);
            gameRoot.getChildren().add(currGameText);
            gameMenuStatus = "loseTeam";
            gamePrompt.setText("What team did " + currLoser.name + " play with?");
            centerText(gamePrompt);
        } else if (gameMenuStatus == "loseTeam") {
            currLosingTeam = currGameText.getText().substring(5, currGameText.getText().length());
            currGameText.removeEventHandler(KeyEvent.KEY_TYPED, inputInfoHandler);
            currGameText.removeEventHandler(KeyEvent.KEY_PRESSED, inputInfoHandler);
            currGameText = losingScore;
            currGameText.setOnKeyTyped(inputInfoHandler);
            currGameText.setOnKeyPressed(inputInfoHandler);
            gameRoot.getChildren().add(currGameText);
            gameMenuStatus = "loseScore";
            gamePrompt.setText("How many goals did " + currLoser.name + " score?");
            centerText(gamePrompt);
        } else if (gameMenuStatus == "loseScore") {
            currLosingScore = Integer.parseInt(currGameText.getText().substring(6, currGameText.getText().length()));
            currGameText.removeEventHandler(KeyEvent.KEY_TYPED, inputInfoHandler);
            currGameText.removeEventHandler(KeyEvent.KEY_PRESSED, inputInfoHandler);
            gameMenuStatus = "confirmation";
            gameRoot.getChildren().add(confirmation);
            confirmation.setOnMouseClicked(imageMouseClick);
        }
    }

    private void implementGame() {
        Game game = new Game(currWinner, currWinningScore, currWinningTeam, currLoser, currLosingScore, currLosingTeam, false, false);
        currWinner.readGame(game);
        currLoser.readGame(game);
        gameList.add(game);
        for (Object o : gameRoot.getChildren().toArray()) {
            gameRoot.getChildren().remove(o);
        }
        mainRoot.getChildren().remove(gameRoot);
        mainMenu();
    }

    public String removeLast(String str) {
        if (str != null && str.length() > 0 && !str.substring(str.length() - 1, str.length()).equals(":")) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

    private String playerInfo(Player p){
        String record = p.recordString();
        String winPercentage = p.winPercentageString();
        String goalsPerGame = p.goalsPerGameString();
        String info = "Record: " + record + "\nWin Percent: " + winPercentage + "%\nGoals/Game: " + goalsPerGame;
        return info;
    }

    private void setPlayers() {
        playerList = new ArrayList<>();
        playerImages = new ArrayList<>();
        john = new Player("John", "FifaWizard/johnpic.jpg");
        playerList.add(john);
        joey = new Player("Joey", "FifaWizard/joeypic.jpg");
        playerList.add(joey);
        gabe = new Player("Gabe", "FifaWizard/gabepic.jpg");
        playerList.add(gabe);
        matt = new Player("Matt", "FifaWizard/mattpic.jpg");
        playerList.add(matt);
    }

    private void setMainMenu() {
        Font titleFont = new Font("Verdana", 30);
        title = new Text("Welcome to FIFA Wizard!");
        title.setFont(titleFont);
        int titleWidth = (int) Math.round(title.getLayoutBounds().getWidth());
        title.setY(windowHeight/4);
        title.setX((windowWidth - titleWidth) / 2);
        Font buttonFont = new Font("Verdana", 20);
        infoButton = new Text("Scope Player Info");
        infoButton.setFont(buttonFont);
        int infoButtonWidth = (int) Math.round(infoButton.getLayoutBounds().getWidth());
        infoButton.setTextOrigin(VPos.TOP);
        infoButton.setY(title.getY() + 30);
        infoButton.setX((windowWidth - infoButtonWidth) / 2);
        gameButton = new Text("Implement a Game");
        gameButton.setFont(buttonFont);
        int gameButtonWidth = (int) Math.round(gameButton.getLayoutBounds().getWidth());
        gameButton.setTextOrigin(VPos.TOP);
        gameButton.setY(infoButton.getY() + 30);
        gameButton.setX((windowWidth - gameButtonWidth) / 2);
        menuRoot.getChildren().add(title);
        menuRoot.getChildren().add(infoButton);
        menuRoot.getChildren().add(gameButton);
    }

    private void mainMenu() {
        mainRoot.getChildren().add(menuRoot);
        currDisplay = "mainMenu";
    }

    private void mainMenuMouseClick(int x, int y) {
        int infoButtonLeft = (int) Math.round(infoButton.getX());
        int infoButtonRight = infoButtonLeft + (int) Math.round(infoButton.getLayoutBounds().getWidth());
        int infoButtonTop = (int) Math.round(infoButton.getY());
        int infoButtonBottom = infoButtonTop + (int) Math.round(infoButton.getLayoutBounds().getHeight());
        int gameButtonLeft = (int) Math.round(gameButton.getX());
        int gameButtonRight = gameButtonLeft + (int) Math.round(gameButton.getLayoutBounds().getWidth());
        int gameButtonTop = (int) Math.round(gameButton.getY());
        int gameButtonBottom = gameButtonTop + (int) Math.round(gameButton.getLayoutBounds().getHeight());
        if (x > infoButtonLeft && x < infoButtonRight && y > infoButtonTop && y < infoButtonBottom) {
            mainRoot.getChildren().remove(menuRoot);
            infoMenu();
        } else if (x > gameButtonLeft && x < gameButtonRight && y > gameButtonTop && y < gameButtonBottom) {
            mainRoot.getChildren().remove(menuRoot);
            gameMenu();
        }
    }

    private void setInfoMenu() {
        Font infoTitleFont = new Font("Verdana", 20);
        Text infoTitle = new Text("Player Info");
        Text playerInfo;
        infoTitle.setFont(infoTitleFont);
        int infoTitleWidth = (int) Math.round(infoTitle.getLayoutBounds().getWidth());
        infoTitle.setTextOrigin(VPos.TOP);
        infoTitle.setY(25);
        infoTitle.setX((windowWidth - infoTitleWidth) / 2);
        infoRoot.getChildren().add(infoTitle);
        Font infoFont = new Font("Verdana", 14);
        int xPos = 50;
        for (Player p: playerList) {
            ImageView playerView = new ImageView(p.playerImage);
            playerView.setFitHeight(imageHeight);
            playerView.setFitWidth(imageWidth);
            playerView.setY(100);
            playerView.setX(xPos);
            playerInfo = new Text(playerInfo(p));
            playerInfo.setFont(infoFont);
            playerInfo.setTextOrigin(VPos.TOP);
            playerInfo.setY(playerView.getY() + playerView.getLayoutBounds().getHeight() + 5);
            playerInfo.setX(xPos);
            xPos += 185;
            infoRoot.getChildren().add(playerView);
            infoRoot.getChildren().add(playerInfo);
        }
        infoRoot.getChildren().add(backButton);
    }

    private void infoMenu() {
        setInfoMenu();
        mainRoot.getChildren().add(infoRoot);
        currDisplay = "infoMenu";
    }

    private void infoMenuMouseClick(int x, int y) {
        int backButtonLeft = (int) Math.round(backButton.getX());
        int backButtonRight = backButtonLeft + (int) Math.round(backButton.getLayoutBounds().getWidth());
        int backButtonTop = (int) Math.round(backButton.getY());
        int backButtonBottom = backButtonTop + (int) Math.round(backButton.getLayoutBounds().getHeight());
        if (x > backButtonLeft && x < backButtonRight && y > backButtonTop && y < backButtonBottom) {
            infoRoot.getChildren().remove(0, infoRoot.getChildren().size());
            mainRoot.getChildren().remove(infoRoot);
            mainMenu();
        }
    }

    private void setGameText() {
        Font winnerFont = new Font("Verdana", 18);
        Font promptFont = new Font("Verdana", 14);
        Font confirmationFont = new Font("Verdana", 20);
        winner = new Text("WINNER");
        winner.setFont(winnerFont);
        winner.setFill(Color.BLUE);
        winner.setTextOrigin(VPos.TOP);
        loser = new Text("LOSER");
        loser.setFont(winnerFont);
        loser.setFill(Color.RED);
        loser.setTextOrigin(VPos.TOP);
        winningTeam = new Text("Team:");
        winningTeam.setFont(promptFont);
        winningTeam.setTextOrigin(VPos.TOP);
        winningScore = new Text("Score:");
        winningScore.setFont(promptFont);
        winningScore.setTextOrigin(VPos.TOP);
        losingTeam = new Text("Team:");
        losingTeam.setFont(promptFont);
        losingTeam.setTextOrigin(VPos.TOP);
        losingScore = new Text("Score:");
        losingScore.setFont(promptFont);
        losingScore.setTextOrigin(VPos.TOP);
        confirmation = new Text("Implement Game -->");
        confirmation.setFont(confirmationFont);
        confirmation.setTextOrigin(VPos.TOP);
        centerText(confirmation);
        confirmation.setY(backButton.getY() - 30);
    }

    private void setGameMenu() {
        gameMenuStatus = "winner";
        Font gamePromptFont = new Font("Verdana", 20);
        gamePrompt = new Text("Who won the game?");
        gamePrompt.setFont(gamePromptFont);
        int gamePromptWidth = (int) Math.round(gamePrompt.getLayoutBounds().getWidth());
        gamePrompt.setTextOrigin(VPos.TOP);
        gamePrompt.setY(25);
        gamePrompt.setX((windowWidth - gamePromptWidth) / 2);
        gameRoot.getChildren().add(gamePrompt);
        playerPics = new ArrayList<>();
        Font infoFont = new Font("Verdana", 14);
        int xPos = 50;
        for (Player p: playerList) {
            ImageView playerView = new ImageView(p.playerImage);
            p.setView(playerView);
            playerView.setFitHeight(imageHeight);
            playerView.setFitWidth(imageWidth);
            playerView.setY(100);
            playerView.setX(xPos);
            xPos += 185;
            gameRoot.getChildren().add(playerView);
            playerPics.add(playerView);
            playerView.setOnMouseClicked(imageMouseClick);
        }
        gameRoot.getChildren().add(backButton);
    }

    private void gameMenu() {
        setGameText();
        setGameMenu();
        mainRoot.getChildren().add(gameRoot);
        currDisplay = "gameMenu";
    }

    private void gameMenuMouseClick(int x, int y) {
        int backButtonLeft = (int) Math.round(backButton.getX());
        int backButtonRight = backButtonLeft + (int) Math.round(backButton.getLayoutBounds().getWidth());
        int backButtonTop = (int) Math.round(backButton.getY());
        int backButtonBottom = backButtonTop + (int) Math.round(backButton.getLayoutBounds().getHeight());
        if (x > backButtonLeft && x < backButtonRight && y > backButtonTop && y < backButtonBottom) {
            gameRoot.getChildren().remove(0, gameRoot.getChildren().size());
            mainRoot.getChildren().remove(gameRoot);
            mainMenu();
        }
    }

    private void setBackButton() {
        backButton = new Text("<-- Back to Main Menu");
        Font backButtonFont = new Font("Verdana", 20);
        backButton.setFont(backButtonFont);
        int backButtonWidth = (int) Math.round(backButton.getLayoutBounds().getWidth());
        backButton.setTextOrigin(VPos.TOP);
        backButton.setY(450);
        backButton.setX((windowWidth - backButtonWidth) / 2);
    }

    @Override
    public void start(Stage primaryStage) {
        windowWidth = 800;
        windowHeight = 500;

        mainRoot = new Group();
        menuRoot = new Group();
        infoRoot = new Group();
        gameRoot = new Group();
        scene = new Scene(mainRoot, windowWidth, windowHeight, Color.LAWNGREEN);
        inputInfoHandler = new InputInfoHandler();
        mouseClickEventHandler = new MouseClickEventHandler();
        imageMouseClick = new ImageMouseClick();

        scene.setOnKeyTyped(inputInfoHandler);
        scene.setOnKeyPressed(inputInfoHandler);
        scene.setOnMouseClicked(mouseClickEventHandler);

        gameList = new ArrayList<>();

        setPlayers();
        setBackButton();
        setGameText();
        setMainMenu();

        mainMenu();
        primaryStage.setTitle("FIFA Wizard");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
