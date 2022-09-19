package com.example.gridlayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Values to keep track of count
    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int BOMB_COUNT = 4;
    // Array of TextViews represents each cell
    private ArrayList<TextView> cell_tvs;
    // Values for our timer
    private int clock;
    private boolean running = false;
    // Boolean to keep track of the game
    private boolean gameOver = false;
    // Create values for flagging
    private int flag_count = BOMB_COUNT;
    private boolean flagging = false;
    private TextView flagButton;
    private TextView flagCount;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Create clock
        if (savedInstanceState != null) {
            clock = savedInstanceState.getInt("clock");
            running = savedInstanceState.getBoolean("running");
        }
        // Create the bottom button to toggle between flagging and digging mode
        flagButton = findViewById(R.id.activity_main_flag);
        flagCount = findViewById(R.id.activity_main_flag_count);
        flagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flagging = !flagging;
                if (flagging) {
                    flagButton.setText(R.string.flag);
                }
                else {
                    flagButton.setText(R.string.pick);
                }
            }
        });
        // Create empty grid for the Minesweeper game
        cell_tvs = new ArrayList<TextView>();
        GridLayout grid = findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);
        // Populate our ArrayList with empty cells
        for (int i = 0; i <ROW_COUNT; i++) {
            for (int j=0; j < COLUMN_COUNT; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                tv.setText("");
                tv.setTextColor(Color.GREEN);
                tv.setBackgroundColor(Color.GREEN);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }
        generateGrid();
        runTimer();
    }
    /*
    This function populates the empty grid with bombs and values
     */
    public void generateGrid() {
        int mines_added = 0;
        while (mines_added < BOMB_COUNT) {
            int x = new Random().nextInt(ROW_COUNT);
            int y = new Random().nextInt(COLUMN_COUNT);
            TextView mine = getCell(x,y);
            if (mine != null && mine.getText() == "") {
                int index = getCellIndex(x,y);
                TextView mine_cell = cell_tvs.get(index);
                mine_cell.setText(R.string.mine);
                mine_cell.setTextColor(Color.TRANSPARENT);
                cell_tvs.set(index, mine_cell);
                mines_added++;
            }
        }
        for (int x = 0; x < ROW_COUNT; x++) {
            for (int y = 0; y < COLUMN_COUNT; y++) {
                // If the cell isn't a bomb, we count its number value
                TextView curr_cell = getCell(x,y);
                if (curr_cell != null && curr_cell.getText().equals("")) {
                    List<TextView> surrounding_cells = getSurroundingCells(x, y);
                    int bombCount = 0;
                    // Iterate through all the surrounding cells, and see if there is a bomb
                    for (TextView adj_cell: surrounding_cells) {
                        if (adj_cell.getText().toString().compareTo(getResources().getString(R.string.mine)) == 0) {
                            bombCount++;
                        }
                    }
                    if (bombCount > 0) {
                        int index = getCellIndex(x,y);
                        curr_cell.setText(String.valueOf(bombCount));
                        cell_tvs.set(index, curr_cell);
                    }
                }
            }
        }

    }

    /*
    This function returns the index of a given cell
     */
    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    /*
    This function returns the index of the cell, given an x & y coordinate
    (1,7) = 15 = x * ROW_COUNT + 7 - 2(1)
    (3,0) = 24 = x * ROW_COUNT + 0 - 2(3)
    (3,7) = 31 = x * ROW_COUNT + 7 - 2(3)
    Therefore, (x,y) = n = x * ROW_COUNT + y - 2(x)
     */
    private int getCellIndex(int x, int y) {
        if (x < 0 || x >= ROW_COUNT || y < 0 || y >= COLUMN_COUNT) {
            return -1;
        }
        return (x * ROW_COUNT) + y - (2*x);
    }

    /*
    This function returns the x and y coordinates, given an index
     */
    private int[] getCellPos(int index) {
        int x;
        if (index <= 7) {
            x = 0;
        } else if (index <= 15) {
            x = 1;
        } else if (index <= 23) {
            x = 2;
        } else if (index <= 31) {
            x = 3;
        } else if (index <= 39) {
            x = 4;
        } else if (index <= 47) {
            x = 5;
        } else if (index <= 55) {
            x = 6;
        } else if (index <= 63) {
            x = 7;
        } else if (index <= 71) {
            x = 8;
        } else {
            x = 9;
        }
        int y = index - (x * ROW_COUNT) + (2 * x);
        int[] position = new int[2];
        position[0] = x;
        position[1] = y;
        return position;
    }

    /*
    This function returns the TextView given an x and y coordinate
     */
    private TextView getCell(int x, int y) {
        int n = getCellIndex(x,y);
        if (n == -1) {
            return null;
        }
        else {
            return cell_tvs.get(n);
        }
    }

    /*
    This function returns the surrounding cells (adjacent cells)
    of a given cell
     */
    public List<TextView> getSurroundingCells(int x, int y) {
        // Create a List with all of the current cell's adjacent cells
        List<TextView> temp = new ArrayList<>();
        temp.add(getCell(x-1, y));
        temp.add(getCell(x+1, y));
        temp.add(getCell(x-1, y-1));
        temp.add(getCell(x, y-1));
        temp.add(getCell(x+1, y-1));
        temp.add(getCell(x-1, y+1));
        temp.add(getCell(x, y+1));
        temp.add(getCell(x+1, y+1));
        // If that cell isn't null, we add it to the surroundingCells list
        List<TextView> surroundingCells = new ArrayList<>();
        for (TextView cell: temp) {
            if (cell != null) {
                surroundingCells.add(cell);
            }
        }
        return surroundingCells;
    }

    /*
    This function alters the clicked cell
     */
    public void onClickTV(View view){
        running = true;
        TextView tv = (TextView) view;
        // If game is won, we end the game
        if (isGameOver()) {
            gameFinished();
        }
        else if (isGameWon()) {
            showBombs();
            running = false;
            gameOver = true;
        }
        // If we are on flagging mode, we invoke the flagCell() function
        if (flagging) {
            flagCell(tv);
        }
        // If the cell clicked is a bomb, we end the game
        else if (tv.getText().toString().equals(getResources().getString(R.string.mine))) {
            showBombs();
            running = false;
            gameOver = true;
        }
        // If the cell is blank, we clear its adjacent cells
        else if (tv.getText().equals("")) {
            List<TextView> toClear = new ArrayList<>();
            List<TextView> toCheck = new ArrayList<>();
            toCheck.add(tv);
            while (toCheck.size() > 0) {
                TextView curr_cell = toCheck.get(0);
                int cellIndex = findIndexOfCellTextView(curr_cell);
                int[] cellPos = getCellPos(cellIndex);
                List<TextView> surrounding_cells = getSurroundingCells(cellPos[0], cellPos[1]);
                for (TextView adj_cell: surrounding_cells) {
                    if (adj_cell.getText() == "" && !toClear.contains(adj_cell) && !toCheck.contains(adj_cell)) {
                        adj_cell.setTextColor(Color.GRAY);
                        adj_cell.setBackgroundColor(Color.LTGRAY);
                        toCheck.add(adj_cell);
                    }
                }
                toCheck.remove(curr_cell);
                toClear.add(curr_cell);
            }
        }
        if (tv.getCurrentTextColor() == Color.GREEN && !flagging && !tv.getText().toString().equals(getResources().getString(R.string.flag))) {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
        }
    }

    /*
    This function flags and un-flags a clicked cell
    */
    public void flagCell(TextView tv) {
        // If the cell is not revealed, we can flag it
        if (tv.getCurrentTextColor() != Color.GRAY) {
            // If the cell is not a flag, we can flag it
            if (!tv.getText().toString().equals(getResources().getString(R.string.flag))) {
                // We set the TextView's Hint as the current value
                tv.setHint(tv.getText());
                tv.setText(R.string.flag);
                tv.setTextColor(Color.GREEN);
                flag_count--;
            }
            // If the cell is a flag, we undo it
            else if (tv.getText().toString().equals(getResources().getString(R.string.flag))) {
                tv.setText(tv.getHint());
                System.out.println(tv.getHint());
                // If the cell is a mine, we set the color to be transparent
                if (tv.getText().toString().equals(getResources().getString(R.string.mine))) {
                    tv.setTextColor(Color.TRANSPARENT);
                }
                flag_count++;
            }
        }
        flagCount.setText(Integer.toString(flag_count));
    }

    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("clock", clock);
        savedInstanceState.putBoolean("running", running);
    }

    /*
    This function begins the timer and runs it
     */
    private void runTimer() {
        final TextView timeView = findViewById(R.id.activity_main_time_count);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(clock == 60) {
                    gameFinished();
                    return;
                }
                int seconds = clock%60;
                String time = String.format("%02d", seconds);
                timeView.setText(time);
                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    /*
    This function checks if the game is won
     */
    public boolean isGameWon() {
        int revealedCell = 0;
        for (TextView c: cell_tvs) {
            // Ignore the cell if it is a mine
            if (!c.getText().toString().equals(getResources().getString(R.string.mine))) {
                if (!c.getText().equals("")) {
                    if (c.getCurrentTextColor() != Color.GRAY) {
                            ++revealedCell;
                    }
                }
            }
            // If the cell is flagged and if that cell is a mine, we subtract one
            if (c.getText().toString().equals(getResources().getString(R.string.flag))) {
                if (c.getHint().toString().equals(getResources().getString(R.string.mine))) {
                    --revealedCell;
                }
            }
        }
        if (revealedCell == 0) {
            showBombs();
            gameOver = true;
            return true;
        } else {
            return false;
        }
    }

    /*
    This function checks if the game is over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /*
    This function creates the new intent, and directs the user to the landing page
     */
    public void gameFinished() {
        running = false;
        String secondsUsed = "Used " + clock + " seconds.";
        String gameResult;
        if (isGameWon()) {
            gameResult = "You Won.";
        } else {
            gameResult = "You Lost.";
        }
        Intent gameOver = new Intent(MainActivity.this, GameFinished.class);
        gameOver.putExtra("gameResult", gameResult);
        gameOver.putExtra("secondsUsed", secondsUsed);
        startActivity(gameOver);
    }

    /*
    This function shows all the bombs within the game
     */
    public void showBombs() {
        for (TextView tv : cell_tvs) {
            // If the text value is a bomb, we reveal it
            if (tv.getText().toString().compareTo(getResources().getString(R.string.mine)) == 0) {
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.RED);
            }
            // If the text value is a flag and it's a bomb under, we reveal it
            else if (tv.getText().toString().equals(getResources().getString(R.string.flag))) {
                if (tv.getHint().toString().equals(getResources().getString(R.string.mine))) {
                    tv.setText(R.string.mine);
                    tv.setTextColor(Color.GRAY);
                    tv.setBackgroundColor(Color.RED);
                }
            }
        }
    }

}