package com.goldstone.saboteur_backend.domain.board;

import com.goldstone.saboteur_backend.domain.card.PathCard;
import com.goldstone.saboteur_backend.domain.card.StartCard;

public class Board {
    public static final int DEFAULT_WIDTH = 9;
    public static final int DEFAULT_HEIGHT = 5;

    private int width;
    private int height;
    private Cell[][] cells;
    private Goals goals;

    public Board() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.cells = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(x, y);
            }
        }

        // 시작 카드 배치
        int startX = 0;
        int startY = height / 2;
        cells[startY][startX].setCard(new StartCard());

        // 골 카드 배치
        this.goals = new Goals();
        this.goals.assignToBoard(cells);
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public boolean placeCard(int x, int y, PathCard card) {
        if (isValidPosition(x, y) && cells[y][x].canPlacePathCard(card)) {
            cells[y][x].setCard(card);
            return true;
        }
        return false;
    }

    public Cell getCell(int x, int y) {
        if (!isValidPosition(x, y)) return null;
        return cells[y][x];
    }

    public Goals getGoals() {
        return goals;
    }

    // x1 & y1: from
    // x2 & y2: to
    public boolean isConnected(int x1, int y1, int x2, int y2) {
        if (!isValidPosition(x1, y1) || !isValidPosition(x2, y2)) return false;

        Cell from = cells[y1][x1];
        Cell to = cells[y2][x2];

        int dx = x2 - x1;
        int dy = y2 - y1;

        int direction = -1;

        if (dx == 0 && dy == -1) direction = 0; // 위
        else if (dx == 1 && dy == 0) direction = 1; // 오른쪽
        else if (dx == 0 && dy == 1) direction = 2; // 아래
        else if (dx == -1 && dy == 0) direction = 3; // 왼쪽

        if (direction == -1) return false; // 인접하지 않은 셀

        return PathValidator.isConnected(from, to, direction);
    }
}
