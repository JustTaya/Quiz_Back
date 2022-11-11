package com.quiz.entities;

import com.quiz.dto.GameQuestionsDto;
import lombok.Data;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.LongAdder;

@Data
public class GameSession {
    private final int hostId;
    private final Map<Integer, Question> questions;
    private Set<Player> playerSet;
    private int currentQuestion;
    private LongAdder collectedAnswers;
    private int questionTimer;

    private Iterator<Question> mapIterator;

    public GameSession(int hostId, Map<Integer, Question> questions, int questionTimer) {
        this.hostId = hostId;
        this.questions = questions;
        this.currentQuestion = 0;
        this.playerSet = new ConcurrentSkipListSet<>();
        this.collectedAnswers = new LongAdder();
        this.questionTimer = questionTimer;
        this.mapIterator = questions.values().iterator();
    }

    public synchronized GameQuestionsDto nextQuestion() {
        if (this.collectedAnswers.intValue() == this.playerSet.size()) {
            this.collectedAnswers.reset();
            this.currentQuestion++;
        }

        if (this.mapIterator.hasNext()) {
            return new GameQuestionsDto(this.currentQuestion, this.questionTimer, this.mapIterator.next());
        }

        return new GameQuestionsDto();
    }

    public void addScorePoint(int score, int userId, boolean isAuthorize) {
        for (Player player : this.playerSet) {
            if (player.getUserId() == userId && player.isAuthorize() == isAuthorize) {
                player.setUserScore(player.getUserScore() + score);
            }
        }
    }

    public void addPlayer(Player player) {
        this.playerSet.add(player);
    }

    public boolean isAllAnswerCollected() {
        this.collectedAnswers.increment();
        return this.collectedAnswers.intValue() == this.playerSet.size();
    }
}

