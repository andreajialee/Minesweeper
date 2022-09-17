package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 10;
    private static final int BOMB_COUNT = 4;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;
    private boolean game_over;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cell_tvs = new ArrayList<TextView>();
        game_over = false;

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);

        // Create an the empty list of cell text views
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
    }

    /*
    This function populates the empty grid with bombs and values
     */
    public void generateGrid() {
        int bombs_added = 0;
        while (bombs_added < BOMB_COUNT) {
            int x = new Random().nextInt(ROW_COUNT);
            int y = new Random().nextInt(COLUMN_COUNT);
            if (getCell(x, y).getText() == "") {
                int index = getCellIndex(x,y);
                TextView bomb_cell = cell_tvs.get(index);
                bomb_cell.setText(R.string.mine);
                bomb_cell.setTextColor(Color.GREEN);
                // hide(bomb_cell);
                cell_tvs.set(index, bomb_cell);
                bombs_added++;
            }
        }
        for (int x = 0; x < ROW_COUNT; x++) {
            for (int y = 0; y < COLUMN_COUNT; y++) {
                // If the cell isn't a bomb, we count its number value
                TextView curr_cell = getCell(x,y);
                if (curr_cell.getText().equals("")) {
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
        int position[] = new int[2];
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
        System.out.println("Cell " + x + y);
        for (TextView cell: temp) {
            if (cell != null) {
                surroundingCells.add(cell);
            }
        }
        return surroundingCells;
    }

    /*
    This function alters the cell given that it is clicked
     */
    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        //tv.setText(String.valueOf(i)+String.valueOf(j));
        // If the cell clicked is a bomb, we end the game
        if (tv.getText() == "\\uD83D\\uDCA3") {
            // End Game
        }
        // If the cell is blank, we clear its adjacent cells
        else if (tv.getText().equals("")) {
            List<TextView> toClear = new ArrayList<>();
            List<TextView> toCheck = new ArrayList<>();
            toCheck.add(tv);

            while(toCheck.size() > 0) {
                TextView curr_tv = toCheck.get(0);
                int index = findIndexOfCellTextView(tv);
                int pos[] = getCellPos(index);
                List<TextView> cells = getSurroundingCells(pos[0], pos[1]);
                for (TextView adj_cell: cells) {
                    if (adj_cell.getText() == "") {
                        if (!toClear.contains(adj_cell) && !toCheck.contains(adj_cell)) {
                            toCheck.add(adj_cell);
                        }
                    }
                    else {
                        if (!toClear.contains(adj_cell)) {
                            toClear.add(adj_cell);
                        }
                    }
                }
                toCheck.remove(curr_tv);
                toClear.add(curr_tv);
            }

            for (TextView cell : toClear) {
                tv.setBackgroundColor(Color.LTGRAY);
            }
        }
        if (tv.getCurrentTextColor() == Color.GREEN) {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
        }
    }
    /*
    Hide the visibility of a TextView
     */
    public void hide(TextView tv) {
        //Toggle
        if (tv.getVisibility() == View.VISIBLE)
            tv.setVisibility(View.INVISIBLE);
        else
            tv.setVisibility(View.VISIBLE);
    }
}