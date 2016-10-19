package FifaWizard;

/**
 * Created by jleyden on 3/23/16.
 */
public class Game {

    protected Player winner;
    protected int winningGoals;
    protected String winningTeam;
    protected Player loser;
    protected int losingGoals;
    protected String losingTeam;
    protected boolean overtime;
    protected boolean penalties;

    public Game(Player w, int wg, String wt, Player l, int lg, String lt, boolean ot, boolean p) {
        winner = w;
        winningGoals = wg;
        winningTeam = wt;
        loser = l;
        losingGoals = lg;
        losingTeam = lt;
        overtime = ot;
        penalties = p;
    }

}
