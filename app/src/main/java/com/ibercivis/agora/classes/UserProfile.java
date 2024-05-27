package com.ibercivis.agora.classes;

public class UserProfile {
    private String username;
    private String email;
    private int totalPoints;
    private int totalGamesPlayed;
    private int totalGamesAbandoned;
    private int totalCorrectAnswers;
    private int totalIncorrectAnswers;
    private int maxTimeTrialTime;
    private int maxTimeTrialPoints;
    private String profileImageUrl;

    // Constructor, getters y setters
    public UserProfile(int totalPoints, int totalGamesPlayed, int totalGamesAbandoned,
                       int totalCorrectAnswers, int totalIncorrectAnswers, int maxTimeTrialTime, int maxTimeTrialPoints, String profileImageUrl) {
        this.totalPoints = totalPoints;
        this.totalGamesPlayed = totalGamesPlayed;
        this.totalGamesAbandoned = totalGamesAbandoned;
        this.totalCorrectAnswers = totalCorrectAnswers;
        this.totalIncorrectAnswers = totalIncorrectAnswers;
        this.maxTimeTrialTime = maxTimeTrialTime;
        this.maxTimeTrialPoints = maxTimeTrialPoints;
        this.profileImageUrl = profileImageUrl;
    }

    public UserProfile(int totalPoints, int totalGamesPlayed, int totalGamesAbandoned,
                       int totalCorrectAnswers, int totalIncorrectAnswers, String profileImageUrl) {
        this.totalPoints = totalPoints;
        this.totalGamesPlayed = totalGamesPlayed;
        this.totalGamesAbandoned = totalGamesAbandoned;
        this.totalCorrectAnswers = totalCorrectAnswers;
        this.totalIncorrectAnswers = totalIncorrectAnswers;
        this.profileImageUrl = profileImageUrl;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getMaxTimeTrialPoints() {
        return maxTimeTrialPoints;
    }

    public void setMaxTimeTrialPoints(int maxTimeTrialPoints) {
        this.maxTimeTrialPoints = maxTimeTrialPoints;
    }

    public int getMaxTimeTrialTime() {
        return maxTimeTrialTime;
    }

    public void setMaxTimeTrialTime(int maxTimeTrialTime) {
        this.maxTimeTrialTime = maxTimeTrialTime;
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
