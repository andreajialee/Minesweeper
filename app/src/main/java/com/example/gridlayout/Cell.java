package com.example.gridlayout;

public class Cell {
    public static final int BOMB = -1;
    public static final int EMPTY = 0;

    private int value;
    private boolean isRevealed;
    private boolean isFlagged;

    public Cell(int value)  {
        this.value = value;
        this.isRevealed = false;
        this.isFlagged = false;
    }

    public int getValue() {
        return this.value;
    }
    public boolean isRevealed() {
        return this.isRevealed;
    }
    public boolean isFlagged() {
        return this.isFlagged;
    }
    public void setRevealed() {
        this.isRevealed = true;
    }
    public void setFlagged() {
        this.isFlagged = true;
    }
}
