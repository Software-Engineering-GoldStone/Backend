package com.goldstone.saboteur_backend.test;

import com.goldstone.saboteur_backend.domain.*;
import com.goldstone.saboteur_backend.domain.card.*;
import com.goldstone.saboteur_backend.domain.enums.ActionCardType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Profile("test")
public class GameSimulator implements CommandLineRunner, Game.GameStateChangeListener {

    private static final int PLAYER_COUNT = 5;
    private final Random random = new Random();

    public GameSimulator() {
        long seed = System.currentTimeMillis();
        random.setSeed(seed);
        System.out.println("랜덤 시드: " + seed);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== 사보타지 게임 시뮬레이션 시작 ===");
        Game game = new Game();
        game.addListener(this);

        List<Player> players = createPlayers();
        players.forEach(game::addPlayer);

        System.out.println("플레이어 " + PLAYER_COUNT + "명이 게임에 참가했습니다.");
        game.start();
        printBoardState(game);

        while (!game.isGameOver()) {
            if (isAllPlayersUnable(game) && game.getCardDeck().isEmpty()) {
                System.out.println("모든 플레이어가 카드를 낼 수 없고 덱이 비었습니다. 게임을 종료합니다.");
                game.endGame();
                break;
            }
            simulateTurn(game);
            sleep();
        }
        printGameResults(game);
    }

    private List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < GameSimulator.PLAYER_COUNT; i++) {
            players.add(new Player("Player " + (i + 1)));
        }
        return players;
    }

    private void simulateTurn(Game game) {
        Player currentPlayer = game.getCurrentPlayer();
        System.out.println("\n=== " + currentPlayer.getName() + "의 턴 (역할: " + currentPlayer.getRole() + ") ===");
        printPlayerStatus(currentPlayer);
        printPlayerHand(currentPlayer);

        if (handleHandicappedPlayer(game, currentPlayer)) return;
        if (handleEmptyHand(game, currentPlayer)) return;

        playRandomCard(game, currentPlayer);
    }

    private boolean handleHandicappedPlayer(Game game, Player player) {
        if (!player.isHandicapped()) return false;
        System.out.println(player.getName() + "은(는) 도구가 파괴되어 카드를 사용할 수 없습니다.");
        if (!player.getHand().isEmpty()) {
            discardRandomCard(game, player);
        } else {
            System.out.println(player.getName() + "의 손에 카드가 없습니다. 턴을 넘깁니다.");
            game.nextTurnPublic();
        }
        return true;
    }

    private boolean handleEmptyHand(Game game, Player player) {
        if (!player.getHand().isEmpty()) return false;
        System.out.println(player.getName() + "의 손에 카드가 없습니다. 턴을 넘깁니다.");
        game.nextTurnPublic();
        return true;
    }

    private void playRandomCard(Game game, Player player) {
        List<Card> hand = player.getHand();
        Card selectedCard = hand.get(random.nextInt(hand.size()));
        System.out.println(player.getName() + "이(가) " + selectedCard.getName() + " 카드를 선택했습니다.");

        if (selectedCard instanceof PathCard) {
            playPathCard(game, player, (PathCard) selectedCard);
        } else if (selectedCard instanceof ActionCard) {
            playActionCard(game, player, (ActionCard) selectedCard);
        } else {
            game.discardAndEndTurn(player, selectedCard);
        }
    }

    private void playPathCard(Game game, Player player, PathCard card) {
        Board board = game.getBoard();
        List<Position> validPositions = new ArrayList<>();
        List<Integer> validRotations = new ArrayList<>();

        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 9; x++) {
                Position pos = new Position(x, y);
                for (int rotation : new int[]{0, 2}) { // 0도, 180도 회전
                    PathCard rotatedCard = card.copy().rotate(rotation);
                    if (board.canPlaceCard(rotatedCard, pos)) {
                        validPositions.add(pos);
                        validRotations.add(rotation);
                    }
                }
            }
        }

        if (validPositions.isEmpty()) {
            System.out.println("카드를 놓을 수 있는 위치가 없습니다. 카드를 버립니다.");
            game.discardAndEndTurn(player, card);
            return;
        }

        int idx = random.nextInt(validPositions.size());
        Position position = validPositions.get(idx);
        int rotation = validRotations.get(idx);
        PathCard rotatedCard = card.copy().rotate(rotation);

        System.out.println(player.getName() + "이(가) 위치 (" + position.getX() + ", " + position.getY() +
                ")에 " + rotatedCard.getName() + " 카드를 놓았습니다. (회전: " + rotation * 90 + "도)");
        game.playCard(player, rotatedCard, position);

        // 길카드 놓은 뒤 즉시 맵 상태 출력
        printBoardState(game);
    }

    private void playActionCard(Game game, Player player, ActionCard card) {
        ActionCardType type = card.getActionType();

        if (type == ActionCardType.MAP || type == ActionCardType.COLLAPSE) {
            System.out.println(player.getName() + "이(가) " + card.getName() + " 액션 카드를 사용합니다.");
            game.playActionCard(player, card, player);
        } else if (type.name().startsWith("BREAK")) {
            List<Player> targets = new ArrayList<>(game.getPlayers());
            targets.remove(player);
            Player target = targets.get(random.nextInt(targets.size()));
            String tool = card.getActionType().name().split("_")[1];
            if (!isToolAlreadyBroken(target, tool)) {
                System.out.println(player.getName() + "이(가) " + target.getName() + "의 도구를 파괴합니다: " + card.getName());
                target.breakTool(tool);
                game.playActionCard(player, card, target);
            } else {
                System.out.println("이미 파괴된 도구입니다. 다른 카드를 선택합니다.");
                playRandomCard(game, player);
            }
        } else if (type.name().startsWith("FIX")) {
            List<Player> eligiblePlayers = new ArrayList<>();
            List<String> toolsToFix = new ArrayList<>();
            for (Player p : game.getPlayers()) {
                checkFixableTools(p, card.getActionType(), toolsToFix, eligiblePlayers);
            }
            if (eligiblePlayers.isEmpty()) {
                System.out.println("수리할 수 있는 도구가 없습니다. 카드를 버립니다.");
                game.discardAndEndTurn(player, card);
                return;
            }
            int idx = random.nextInt(eligiblePlayers.size());
            Player target = eligiblePlayers.get(idx);
            String tool = toolsToFix.get(idx);
            System.out.println(player.getName() + "이(가) " + target.getName() + "의 " + tool + "을(를) 수리합니다: " + card.getName());
            target.fixTool(tool);
            game.playActionCard(player, card, target);
        } else {
            game.discardAndEndTurn(player, card);
        }
    }

    private void printBoardState(Game game) {
        Board board = game.getBoard();
        Cell[][] cells = board.getCells();
        System.out.println("\n=== 현재 보드 상태 ===");
        System.out.print("   ");
        for (int x = 0; x < cells[0].length; x++) System.out.printf("%2d ", x);
        System.out.println();
        for (int y = 0; y < cells.length; y++) {
            System.out.printf("%2d ", y);
            for (int x = 0; x < cells[y].length; x++) {
                printCell(board, cells[y][x], x, y);
            }
            System.out.println();
        }
        System.out.println("\n카드 덱 남은 카드 수: " + game.getCardDeck().getCards().size());
    }

    private void printCell(Board board, Cell cell, int x, int y) {
        Position pos = new Position(x, y);
        if (pos.equals(board.getStartPosition())) {
            System.out.print("[S]");
        } else if (board.getGoalPositions().contains(pos)) {
            System.out.print("[?]");
        } else if (cell.hasCard()) {
            String cardId = cell.getCard().getId();
            boolean isDeadend = cardId.startsWith("deadend");
            System.out.print("[" + Board.getPathAscii(cell.getSides(), isDeadend) + "]");
        } else {
            System.out.print("[ ]");
        }
    }


    private void printPlayerStatus(Player player) {
        StringBuilder status = new StringBuilder(player.getName() + "의 상태: ");
        if (player.isPickBroken()) status.append("곡괭이 파괴됨 ");
        if (player.isLampBroken()) status.append("램프 파괴됨 ");
        if (player.isCartBroken()) status.append("수레 파괴됨 ");
        if (!player.isHandicapped()) status.append("정상");
        System.out.println(status);
    }

    private void printPlayerHand(Player player) {
        System.out.println("손에 있는 카드:");
        if (player.getHand().isEmpty()) {
            System.out.println("카드 없음");
        } else {
            for (Card card : player.getHand()) {
                System.out.println("- " + card.getName() + " (" + card.getType() + ")");
            }
        }
    }

    private void printGameResults(Game game) {
        System.out.println("\n=== 게임 종료 ===");
        System.out.println(game.isGoldFound() ? "광부 팀이 금광을 찾았습니다!" : "카드가 모두 소진되었습니다. 사보타지 팀의 승리!");
        System.out.println("\n=== 플레이어 역할 ===");
        game.getPlayers().forEach(p -> System.out.println(p.getName() + ": " + p.getRole()));
        System.out.println("\n=== 금덩이 획득 현황 ===");
        game.getGoldNuggets().forEach((playerId, nuggets) -> {
            Player player = game.getPlayers().stream()
                    .filter(p -> p.getId().equals(playerId))
                    .findFirst().get();
            System.out.println(player.getName() + ": " + nuggets + "개");
        });
    }

    private void sleep() {
        try { Thread.sleep(500); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private boolean isAllPlayersUnable(Game game) {
        return game.getPlayers().stream().noneMatch(Player::canPlayCards);
    }

    private void discardRandomCard(Game game, Player player) {
        Card card = player.getHand().remove(random.nextInt(player.getHand().size()));
        System.out.println(player.getName() + "이(가) " + card.getName() + " 카드를 버렸습니다.");
        game.discardAndEndTurn(player, card);
    }

    private boolean isToolAlreadyBroken(Player target, String tool) {
        return switch (tool) {
            case "PICK" -> target.isPickBroken();
            case "LAMP" -> target.isLampBroken();
            case "CART" -> target.isCartBroken();
            default -> false;
        };
    }

    private void checkFixableTools(Player player, ActionCardType actionType, List<String> toolsToFix, List<Player> eligiblePlayers) {
        if (actionType == ActionCardType.FIX_PICK && player.isPickBroken()) {
            toolsToFix.add("PICK"); eligiblePlayers.add(player);
        }
        if (actionType == ActionCardType.FIX_LAMP && player.isLampBroken()) {
            toolsToFix.add("LAMP"); eligiblePlayers.add(player);
        }
        if (actionType == ActionCardType.FIX_CART && player.isCartBroken()) {
            toolsToFix.add("CART"); eligiblePlayers.add(player);
        }
        if (actionType == ActionCardType.FIX_PICK_LAMP) {
            if (player.isPickBroken()) { toolsToFix.add("PICK"); eligiblePlayers.add(player); }
            if (player.isLampBroken()) { toolsToFix.add("LAMP"); eligiblePlayers.add(player); }
        }
        if (actionType == ActionCardType.FIX_PICK_CART) {
            if (player.isPickBroken()) { toolsToFix.add("PICK"); eligiblePlayers.add(player); }
            if (player.isCartBroken()) { toolsToFix.add("CART"); eligiblePlayers.add(player); }
        }
        if (actionType == ActionCardType.FIX_LAMP_CART) {
            if (player.isLampBroken()) { toolsToFix.add("LAMP"); eligiblePlayers.add(player); }
            if (player.isCartBroken()) { toolsToFix.add("CART"); eligiblePlayers.add(player); }
        }
    }

    // GameStateChangeListener 메서드들 (필요시 구현)
    @Override public void onGameStarted(Game game) {}
    @Override public void onPlayerAdded(Game game, Player player) {}
    @Override public void onCardPlayed(Game game, Player player, Card card, Position position) {}
    @Override public void onActionCardPlayed(Game game, Player player, Card card, Player targetPlayer) {}
    @Override public void onNextTurn(Game game) {}
    @Override public void onGameEnded(Game game) {}
}
