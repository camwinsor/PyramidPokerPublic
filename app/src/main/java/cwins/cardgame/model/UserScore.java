package cwins.cardgame.model;


public class UserScore {
    private User user;
    private Game game;
    private Integer totalScore;

    public UserScore(Game game, User user, Integer totalScore) {
        this.game = game;
        this.user = user;
        this.totalScore = totalScore;
    }

    public User getUser() {
        return user;
    }

    public Game getGame() {
        return game;
    }

    public Integer getTotalScore() {
        return totalScore;
    }
}
