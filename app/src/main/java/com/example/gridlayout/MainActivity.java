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
    private static final boolean GAME_OVER = false;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cell_tvs = new ArrayList<TextView>();

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);

        LayoutInflater li = LayoutInflater.from(this);

        for (int i = 0; i <ROW_COUNT; i++) {
            for (int j=0; j < COLUMN_COUNT; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                tv.setText(String.valueOf(i)+String.valueOf(j));
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
    This function returns the TextView given an x and y coordinate
     */
    private TextView getCell(int x, int y) {
        if (x < 0 || x >= ROW_COUNT || y < 0 || y >= COLUMN_COUNT) {
            return null;
        }
        return cell_tvs.get(x + (y*COLUMN_COUNT));
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
        for (TextView cell: cell_tvs) {
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
        if (tv.getCurrentTextColor() == Color.GREEN) {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
        }
        // If the cell clicked is a bomb, we end the game
        if (tv.getText() == "\\uD83D\\uDCA3") {
            // End Game
        }
        else if (tv.getText() == "")  {

        }
    }

}