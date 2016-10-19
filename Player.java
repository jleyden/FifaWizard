package FifaWizard;

/**
 * Created by jleyden on 3/23/16.
 */

import java.util.ArrayList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {

    public String name;
    private String playerImageFile;
    public Image playerImage;
    public ImageView view;
    private ArrayList<Game> games;
    private int wins;
    private int losses;
    private int goals;

    public Player(String n, String imageFile) {
        name = n;
        playerImageFile = imageFile;
        playerImage = new Image(imageFile);
        games = new ArrayList<>();
        wins = 0;
        losses = 0;
        goals = 0;
    }

    public void setView(ImageView i) {
        view = i;
    }

    public boolean equals(Player p){
        if (name == p.name) {
            return true;
        } else {
            return false;
        }
    }

    public void readGame(Game game) {
        if (this.equals(game.winner)) {
            games.add(game);
            wins += 1;
            goals += game.winningGoals;
        } else if (this.equals(game.loser)) {
            games.add(game);
            losses += 1;
            goals += game.losingGoals;
        }
    }

    private int totalGames() {
        return wins + losses;
    }

    public String winsString() {
        Integer intWins = wins;
        return intWins.toString();
    }

    public String lossesString() {
        Integer intLosses = losses;
        return intLosses.toString();
    }

    public String recordString() {
        return winsString() + "-" + lossesString();
    }

    public String goalsString() {
        Integer intGoals = goals;
        return intGoals.toString();
    }

    public String winPercentageString() {
        Double winPercentage = ((double) wins / (double) totalGames()) * (double) 100;
        long winPercentageLong = Math.round(winPercentage * 100);
        winPercentage = winPercentageLong / (double) 100;
        return winPercentage.toString();
    }

    public String goalsPerGameString() {
        Double goalsPerGame = ((double) goals / (double) totalGames());
        long goalsPerGameLong = Math.round(goalsPerGame * 100);
        goalsPerGame = goalsPerGameLong / (double) 100;
        return goalsPerGame.toString();
    }

}
