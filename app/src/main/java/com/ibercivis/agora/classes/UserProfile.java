package com.ibercivis.agora.classes;

public class UserProfile {
    private int totalPoints;
    private int totalGamesPlayed;
    private int totalGamesAbandoned;
    private int totalCorrectAnswers;
    private int totalIncorrectAnswers;
    private String profileImageUrl;

    // Constructor, getters y setters
    public UserProfile(int totalPoints, int totalGamesPlayed, int totalGamesAbandoned,
                       int totalCorrectAnswers, int totalIncorrectAnswers, String profileImageUrl) {
        this.totalPoints = totalPoints;
        this.totalGamesPlayed = totalGamesPlayed;
        this.totalGamesAbandoned = totalGamesAbandoned;
        this.totalCorrectAnswers = totalCorrectAnswers;
        this.totalIncorrectAnswers = totalIncorrectAnswers;
        this.profileImageUrl = profileImageUrl;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public int getTotalGamesAbandoned() {
        return totalGamesAbandoned;
    }

    public void setTotalGamesAbandoned(int totalGamesAbandoned) {
        this.totalGamesAbandoned = totalGamesAbandoned;
    }

    public int getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(int totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

    public int getTotalIncorrectAnswers() {
        return totalIncorrectAnswers;
    }

    public void setTotalIncorrectAnswers(int totalIncorrectAnswers) {
        this.totalIncorrectAnswers = totalIncorrectAnswers;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
