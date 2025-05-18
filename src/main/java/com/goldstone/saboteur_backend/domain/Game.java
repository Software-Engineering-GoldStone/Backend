package com.goldstone.saboteur_backend.domain;

import com.goldstone.saboteur_backend.domain.card.*;
import com.goldstone.saboteur_backend.domain.enums.GameRole;
import com.goldstone.saboteur_backend.domain.card.CardDeck;


import java.util.*;

public class Game {
    private String id;
    private CardDeck cardDeck;
    private List<Player> players;
    private int currentPlayerIndex;
    private Board board;
    private boolean gameOver;
    private boolean goldFound;
    private Map<String, Integer> goldNuggets;
    private List<GameStateChangeListener> listeners;

    public Game() {
        this.id = UUID.randomUUID().toString();
        this.cardDeck = new CardDeck();
        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.board = new Board();
        this.gameOver = false;
        this.goldFound = false;
        this.goldNuggets = new HashMap<>();
        this.listeners = new ArrayList<>();
    }

    public void addListener(GameStateChangeListener listener) {
        listeners.add(listener);
    }

    public void addPlayer(Player player) {
        players.add(player);
        goldNuggets.put(player.getId(), 0);
        notifyPlayerAdded(player);
    }

    public void start() {
        // 역할 분배
        distributeRoles();

        // 초기 카드 분배
        dealInitialCards();

        // 게임 시작 알림
        notifyGameStarted();
    }

    private void distributeRoles() {
        int playerCount = players.size();
        int saboteurCount = getSaboteurCount(playerCount);
        int goldDiggerCount = playerCount - saboteurCount;

        List<GameRole> roles = new ArrayList<>();
        for (int i = 0; i < goldDiggerCount; i++) {
            roles.add(GameRole.MINE_WORKER);
        }
        for (int i = 0; i < saboteurCount; i++) {
            roles.add(GameRole.SABOTEUR);
        }

        Collections.shuffle(roles);

        for (int i = 0; i < players.size(); i++) {
            players.get(i).setRole(roles.get(i));
        }
    }

    private int getSaboteurCount(int playerCount) {
        if (playerCount <= 3) return 1;
        if (playerCount <= 5) return 2;
        if (playerCount <= 8) return 3;
        return 4; // 9-10 players
    }

    private void dealInitialCards() {
        int handSize = getInitialHandSize(players.size());

        for (Player player : players) {
            for (int i = 0; i < handSize; i++) {
                Card card = cardDeck.drawCard();
                if (card != null) {
                    player.addCardToHand(card);
                }
            }
        }
    }

    private int getInitialHandSize(int playerCount) {
        if (playerCount <= 5) return 6;
        if (playerCount <= 7) return 5;
        return 4; // 8-10 players
    }

    public void playCard(Player player, PathCard card, Position position) {
        // 카드 사용
        player.removeCardFromHand(card);
        board.placeCard(card, position);

        // 골드 도달 확인
        checkGoldReached();

        // 카드 뽑기
        drawCardForPlayer(player);

        // 게임 종료 조건 확인
        checkGameEndConditions();

        // 알림
        notifyCardPlayed(player, card, position);

        // 다음 턴
        if (!gameOver) {
            nextTurn();
        }
    }

    public void playActionCard(Player player, ActionCard card, Player targetPlayer) {
        // 카드 사용
        player.removeCardFromHand(card);

        // 카드 뽑기
        drawCardForPlayer(player);

        // 게임 종료 조건 확인
        checkGameEndConditions();

        // 알림
        notifyActionCardPlayed(player, card, targetPlayer);

        // 다음 턴
        if (!gameOver) {
            nextTurn();
        }
    }

    public void discardAndEndTurn(Player player, Card card) {
        // 카드 버리기
        player.removeCardFromHand(card);

        // 카드 뽑기
        drawCardForPlayer(player);

        // 게임 종료 조건 확인
        checkGameEndConditions();

        // 다음 턴
        if (!gameOver) {
            nextTurn();
        }
    }

    public Card drawCardForPlayer(Player player) {
        if (cardDeck.isEmpty()) {
            return null;
        }

        Card card = cardDeck.drawCard();
        if (card != null) {
            player.addCardToHand(card);
        }
        return card;
    }

    private void checkGoldReached() {
        if (board.isGoldReached()) {
            goldFound = true;
            gameOver = true;
            distributeGoldNuggets();
            notifyGameEnded();
        }
    }

    private void checkGameEndConditions() {
        // 이미 골드를 찾았으면 게임 종료
        if (goldFound) {
            return;
        }

        // 모든 플레이어가 카드를 낼 수 없는지 확인
        boolean allPlayersUnable = true;
        for (Player player : players) {
            if (player.canPlayCards()) {
                allPlayersUnable = false;
                break;
            }
        }

        // 카드 덱이 비었고 모든 플레이어가 카드를 낼 수 없으면 게임 종료
        if (cardDeck.isEmpty() && allPlayersUnable) {
            gameOver = true;
            notifyGameEnded();
        }
    }

    private void distributeGoldNuggets() {
        // 골드 발굴자에게 금덩이 분배
        for (Player player : players) {
            if (player.getRole() == GameRole.MINE_WORKER) {
                goldNuggets.put(player.getId(), goldNuggets.getOrDefault(player.getId(), 0) + 1);
            }
        }
    }

    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        notifyNextTurn();
    }

    public void nextTurnPublic() {
        nextTurn();
    }

    public void endGame() {
        gameOver = true;
        notifyGameEnded();
    }

    // 알림 메서드들
    private void notifyGameStarted() {
        for (GameStateChangeListener listener : listeners) {
            listener.onGameStarted(this);
        }
    }

    private void notifyPlayerAdded(Player player) {
        for (GameStateChangeListener listener : listeners) {
            listener.onPlayerAdded(this, player);
        }
    }

    private void notifyCardPlayed(Player player, Card card, Position position) {
        for (GameStateChangeListener listener : listeners) {
            listener.onCardPlayed(this, player, card, position);
        }
    }

    private void notifyActionCardPlayed(Player player, Card card, Player targetPlayer) {
        for (GameStateChangeListener listener : listeners) {
            listener.onActionCardPlayed(this, player, card, targetPlayer);
        }
    }

    private void notifyNextTurn() {
        for (GameStateChangeListener listener : listeners) {
            listener.onNextTurn(this);
        }
    }

    private void notifyGameEnded() {
        for (GameStateChangeListener listener : listeners) {
            listener.onGameEnded(this);
        }
    }

    // 게임 상태 변경 리스너 인터페이스
    public interface GameStateChangeListener {
        void onGameStarted(Game game);
        void onPlayerAdded(Game game, Player player);
        void onCardPlayed(Game game, Player player, Card card, Position position);
        void onActionCardPlayed(Game game, Player player, Card card, Player targetPlayer);
        void onNextTurn(Game game);
        void onGameEnded(Game game);
    }


    // Getter 메서드들(테스트용)
    public String getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public Board getBoard() {
        return board;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGoldFound() {
        return goldFound;
    }

    public Map<String, Integer> getGoldNuggets() {
        return goldNuggets;
    }

    public CardDeck getCardDeck() {
        return cardDeck;
    }

}
