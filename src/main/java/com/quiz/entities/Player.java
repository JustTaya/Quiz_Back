package com.quiz.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;




@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Player implements Comparable<Player> {
    private int userId;
    @NonNull
    private int userScore;
    @NonNull
    private String userName;
    private boolean authorize;

    public Player(int userId, String userName, boolean authorize) {
        this.userId = userId;
        this.userScore = 0;
        this.userName = userName;
        this.authorize = authorize;
    }

    @Override
    public int compareTo(Player player) {
        int result = Integer.compare(player.userScore, this.userScore);
        return result == 0 ? Integer.compare(this.userId, player.getUserId()) : result;
    }
}
