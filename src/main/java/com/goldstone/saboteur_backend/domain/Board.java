package com.goldstone.saboteur_backend.domain;

import com.goldstone.saboteur_backend.domain.card.GoalCard;
import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.enums.GoalCardType;

import java.util.*;

public class Board {
    private final Cell[][] cells;
    private final Position startPosition;
    private final List<Position> goalPositions;
    private final Map<Position, GoalCard> goalCards;
    private final boolean[] revealedGoals;
    private boolean goldReached;

    public Board() {
        this.cells = new Cell[5][9];
        for (int i = 0; i < 5; i++) for (int j = 0; j < 9; j++) cells[i][j] = new Cell();
        this.startPosition = new Position(0, 2);
        // 시작점: 십자(╋) 모양, 네 방향 모두 PATH
        cells[2][0].placeCard(new PathCard("start", "start", new Cell.Side[]{
                Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH, Cell.Side.PATH
        }));

        // 목표 카드는 반드시 오른쪽 끝(x=8)에만 위치해야 함
        this.goalPositions = Arrays.asList(
                new Position(8, 1),
                new Position(8, 2),
                new Position(8, 3)
        );
        this.goalCards = new HashMap<>();
        List<GoalCard> goals = new ArrayList<>();
        goals.add(new GoalCard("goal_gold", "goal_gold", GoalCardType.GOLD));
        goals.add(new GoalCard("goal_rock_1", "goal_rock_1", GoalCardType.ROCK));
        goals.add(new GoalCard("goal_rock_2", "goal_rock_2", GoalCardType.ROCK));
        Collections.shuffle(goals);
        for (int i = 0; i < goalPositions.size(); i++) goalCards.put(goalPositions.get(i), goals.get(i));
        this.revealedGoals = new boolean[3];
        this.goldReached = false;
    }

    public boolean canPlaceCard(PathCard card, Position position) {
        int x = position.getX(), y = position.getY();
        if (x < 0 || x >= 9 || y < 0 || y >= 5) return false;
        if (cells[y][x].hasCard()) return false;
        if ((x == startPosition.getX() && y == startPosition.getY())) return false;
        // 목표 위치에는 path/deadend 카드를 절대 놓을 수 없음
        if (goalPositions.contains(position)) return false;

        // 1. 시작점 인접 위치에 놓는 경우는 "해당 방향만 PATH면 반드시 허용"
        if (isAdjacentToStart(x, y)) {
            int dir = getDirectionFromStart(x, y);
            if (dir != -1) {
                if (card.getSides()[dir] == Cell.Side.PATH &&
                        cells[startPosition.getY()][startPosition.getX()].getSides()[opposite(dir)] == Cell.Side.PATH) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        // 2. 나머지는 기존 규칙 적용
        boolean hasConnectedNeighbor = false;
        boolean hasAnyNeighbor = false;
        for (int dir = 0; dir < 4; dir++) {
            int nx = x + dx(dir), ny = y + dy(dir);
            Cell.Side mySide = card.getSides()[dir];
            boolean neighborExists = (nx >= 0 && nx < 9 && ny >= 0 && ny < 5) && cells[ny][nx].hasCard();

            if (mySide == Cell.Side.PATH) {
                if (!neighborExists) return false;
                Cell.Side neighborSide = cells[ny][nx].getSides()[opposite(dir)];
                if (neighborSide != Cell.Side.PATH) return false;
                if (isConnectedToStart(new Position(nx, ny))) hasConnectedNeighbor = true;
            } else if (neighborExists) {
                Cell.Side neighborSide = cells[ny][nx].getSides()[opposite(dir)];
                if (neighborSide == Cell.Side.PATH) return false;
            }
            if (neighborExists) hasAnyNeighbor = true;
        }
        return hasAnyNeighbor && hasConnectedNeighbor;
    }

    private boolean isAdjacentToStart(int x, int y) {
        int sx = startPosition.getX(), sy = startPosition.getY();
        return (Math.abs(x - sx) + Math.abs(y - sy)) == 1;
    }

    // 0:상, 1:우, 2:하, 3:좌 (시작점 기준)
    private int getDirectionFromStart(int x, int y) {
        int sx = startPosition.getX(), sy = startPosition.getY();
        if (x == sx && y == sy - 1) return 2; // 위쪽(시작점 기준)
        if (x == sx && y == sy + 1) return 0; // 아래쪽(시작점 기준)
        if (x == sx - 1 && y == sy) return 1; // 왼쪽(시작점 기준)
        if (x == sx + 1 && y == sy) return 3; // 오른쪽(시작점 기준)
        return -1;
    }

    private int dx(int dir) { return new int[]{0, 1, 0, -1}[dir]; }
    private int dy(int dir) { return new int[]{-1, 0, 1, 0}[dir]; }
    private int opposite(int dir) { return (dir + 2) % 4; }

    private boolean isConnectedToStart(Position position) {
        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        queue.add(startPosition);
        visited.add(startPosition);
        while (!queue.isEmpty()) {
            Position cur = queue.poll();
            if (cur.equals(position)) return true;
            int x = cur.getX(), y = cur.getY();
            for (int dir = 0; dir < 4; dir++) {
                int nx = x + dx(dir), ny = y + dy(dir);
                if (nx < 0 || nx >= 9 || ny < 0 || ny >= 5) continue;
                Position np = new Position(nx, ny);
                if (visited.contains(np)) continue;
                if (!cells[ny][nx].hasCard()) continue;
                Cell.Side mySide = cells[y][x].getSides()[dir];
                Cell.Side neighborSide = cells[ny][nx].getSides()[opposite(dir)];
                if (mySide == Cell.Side.PATH && neighborSide == Cell.Side.PATH) {
                    queue.add(np);
                    visited.add(np);
                }
            }
        }
        return false;
    }

    public void placeCard(PathCard card, Position position) {
        int x = position.getX(), y = position.getY();
        if (!canPlaceCard(card, position)) throw new IllegalArgumentException("Cannot place card at position " + position);
        cells[y][x].placeCard(card);
        checkGoldReached();
    }

    public void removeCard(Position position) {
        int x = position.getX(), y = position.getY();
        if (x < 0 || x >= 9 || y < 0 || y >= 5) throw new IllegalArgumentException("Position out of bounds: " + position);
        if (!cells[y][x].hasCard()) throw new IllegalArgumentException("No card at position: " + position);
        cells[y][x].removeCard();
    }

    public void revealGoal(int index) {
        if (index < 0 || index >= revealedGoals.length) throw new IllegalArgumentException("Invalid goal index: " + index);
        revealedGoals[index] = true;
    }

    private void checkGoldReached() {
        Queue<Position> queue = new LinkedList<>();
        Set<Position> visited = new HashSet<>();
        queue.add(startPosition);
        visited.add(startPosition);
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int x = current.getX(), y = current.getY();
            for (int i = 0; i < goalPositions.size(); i++) {
                Position goalPos = goalPositions.get(i);
                if (x == goalPos.getX() - 1 && y == goalPos.getY()) {
                    GoalCard goalCard = goalCards.get(goalPos);
                    if (goalCard.getGoalType() == GoalCardType.GOLD) {
                        goldReached = true;
                        revealedGoals[i] = true;
                        return;
                    }
                }
            }
            checkAdjacentCell(queue, visited, x, y-1, 0, 2);
            checkAdjacentCell(queue, visited, x+1, y, 1, 3);
            checkAdjacentCell(queue, visited, x, y+1, 2, 0);
            checkAdjacentCell(queue, visited, x-1, y, 3, 1);
        }
    }

    private void checkAdjacentCell(Queue<Position> queue, Set<Position> visited, int x, int y, int fromSide, int toSide) {
        if (x < 0 || x >= 9 || y < 0 || y >= 5) return;
        Position pos = new Position(x, y);
        if (visited.contains(pos)) return;
        Cell cell = cells[y][x];
        if (!cell.hasCard()) return;
        if (cell.getSides()[toSide] == Cell.Side.PATH) {
            queue.add(pos);
            visited.add(pos);
        }
    }

    public boolean isGoldReached() { return goldReached; }
    public Cell[][] getCells() { return cells; }
    public Position getStartPosition() { return startPosition; }
    public List<Position> getGoalPositions() { return goalPositions; }
    public Map<Position, GoalCard> getGoalCards() { return goalCards; }
    public boolean[] getRevealedGoals() { return revealedGoals; }

    // deadend 카드는 중간에 뚫려있는걸 감안
    public static String getPathAscii(Cell.Side[] sides, boolean isDeadend) {
        boolean up = sides[0] == Cell.Side.PATH;
        boolean right = sides[1] == Cell.Side.PATH;
        boolean down = sides[2] == Cell.Side.PATH;
        boolean left = sides[3] == Cell.Side.PATH;

        if (isDeadend) {
            if (up && !right && !down && !left) return "╹";
            if (!up && !right && down && !left) return "╻";
            if (!up && right && !down && !left) return "╶";
            if (!up && !right && !down && left) return "╴";
            if (up && down && !right && !left) return "║";
            if (!up && right && !down) return "═";
            if (up && right && down && left) return "╬";
            if (!up && right && left) return "╦";
            if (up && right && !down && left) return "╩";
            if (up && !right && down) return "╣";
            if (up && right && down) return "╠";
            if (!up && right && down) return "╝";
            if (!up && down) return "┘";
            return "X";
        }

        if (up && right && down && left) return "+";
        if (up && right && down) return "├";
        if (up && down && left) return "┤";
        if (up && right && left) return "┴";
        if (right && down && left) return "┬";
        if (up && right) return "└";
        if (right && down) return "┌";
        if (down && left) return "┐";
        if (up && left) return "┘";
        if (up && down) return "│";
        if (right && left) return "─";
        if (down) return "↓";
        if (up) return "↑";
        if (right) return "→";
        if (left) return "←";
        return ".";
    }
}
