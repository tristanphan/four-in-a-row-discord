package com.tristanphan.fourrow;

import java.util.ArrayList;
import java.util.Arrays;

public class FourInARow {

    // Game settings
    private final int rowCount;
    private final int columnCount;
    private final int winningSeries;

    // A string represents each column, from bottom to top
    // columns[column].charAt(row)
    // Example board layout:
    //      0 1 2 3 4 5 6  column
    //    5
    //    4
    //    3
    //    2
    //    1
    //    0       0 1 2
    //  row
    private final String[] columns;
    private int turn = 1; // Players are 1 and 2

    public FourInARow(int aRowCount, int aColumnCount, int aWinningSeries) {

        // Set game settings
        rowCount = aRowCount;
        columnCount = aColumnCount;
        winningSeries = aWinningSeries;

        // Set up columns array length
        columns = new String[columnCount];

        // Make columns array not null
        Arrays.fill(columns, "");

        // Fill columns with 0's for empty spots
        populateColumns();
    }

    public String playMove(int player, int column) {

        // Correct value for actual numbers to array indices
        column -= 1;

        // Error management, returns all the way to the interface

        // General errors
        if (player != turn) return "Wrong turn! It is currently Player " + turn + "'s turn!";
        if (column < 0 || column > columnCount) return "Column out of range! Select a column between 1 and " + columnCount + 1 + "!";

        // Un-populate columns to get realistic length and to append to the correct position
        columns[column] = columns[column].replace("0", "");
        if (columns[column].length() >= rowCount) return "This column is already full!";

        // Add the chosen spot to the array
        columns[column] += Integer.toString(turn);

        // Repopulate
        populateColumns();

        // Switch turns
        if (turn == 1) turn = 2;
        else if (turn == 2) turn = 1;

        // Successful exit code, used by interface
        return "Done!";
    }

    public int getWinner() {

        // Get all possible combinations: horizontal, vertical, diagonal
        ArrayList<String> combinations = new ArrayList<>();

        for (int i = 0; i < columnCount; i++) {

            // Check vertical
            String column = getColumn(i + 1);
            if (!combinations.contains(column)) combinations.add(column);

            // Check diagonals, starting from the column number (row 0)
            // Left side
            String diagonalRight = getDiagonal(i, 0, true);
            if (!combinations.contains(diagonalRight)) combinations.add(diagonalRight);

            // Right side
            String diagonalLeft = getDiagonal(i, 0, false);
            if (!combinations.contains(diagonalLeft)) combinations.add(diagonalLeft);
        }

        for (int i = 0; i < rowCount; i++) {

            // Check horizontal
            String row = getRow(i + 1);
            if (!combinations.contains(row)) combinations.add(row);

            // Check diagonals, starting from the row number (column 0)
            // Right side
            String diagonalRight = getDiagonal(0, i, true);
            if (!combinations.contains(diagonalRight)) combinations.add(diagonalRight);

            // Right left
            String diagonalLeft = getDiagonal(0, i, false);
            if (!combinations.contains(diagonalLeft)) combinations.add(diagonalLeft);
        }

        // Creates the strings to check the combinations against
        // Uses char array and string replacement to dynamically multiply the strings "1" and "2" by winningSeries
        String winnerOne = new String(new char[winningSeries]).replace("\0", "1");
        String winnerTwo = new String(new char[winningSeries]).replace("\0", "2");

        // Booleans to check if both wins (just in case this somehow happens)
        boolean[] winners = new boolean[2];

        // Checks all possible combinations and updates if there is a winner
        for (String combination : combinations) {
            if (combination.contains(winnerOne)) winners[0] = true;
            if (combination.contains(winnerTwo)) winners[1] = true;
        }

        if (winners[0] && winners[1]) return 0; // Tie
        if (winners[0]) return 1; // Player 1 won
        if (winners[1]) return 2; // Player 2 won
        return -1; // No win, continue the game
    }


    // GETTERS
    // left -> right
    private String getRow(int number) {

        // Correct for actual numbers and array indices
        number -= 1;

        // Make sure the columns are the right length
        populateColumns();

        // Get all the values in the row by iterating through the columns
        StringBuilder row = new StringBuilder();
        for (String column : columns) row.append(column.charAt(number));
        return row.toString();
    }

    // bottom -> top
    private String getColumn(int number){

        // Correct for actual numbers and array indices
        number -= 1;

        // Make sure the columns are the right length
        populateColumns();

        // Reorient the column's values so it goes from top to bottom
        StringBuilder column = new StringBuilder();
        for (int i = (columns[number]).toCharArray().length - 1; i >= 0; i--) column.append(columns[number].charAt(i));
        return column.toString();
    }

    private String getDiagonal(int column, int row, boolean toRight) {

        // Reminder: this method is run for every column and row, so it only fetches one line

        // Make sure the columns are the right length
        populateColumns();

        // Iterate through the diagonal
        StringBuilder result = new StringBuilder();
        while (column >= 0 && row >= 0 && column < columnCount && row < rowCount) {

            // Get the value at the coordinate
            result.append(columns[column].charAt(row));

            // Always move down the row, but move left or right depending on the boolean
            column += toRight ? 1 : -1;
            row += 1;
        }

        return result.toString();
    }

    private void populateColumns() {

        // Iterate through the columns
        for (int i = 0; i < columns.length; i++) {

            // Un-populate the columns
            columns[i] = columns[i].replace("0", "");

            // Fill up the columns with 0 until it reaches the count
            while (columns[i].length() < rowCount) columns[i] += "0";
        }
    }


    // GETTERS
    public String getBoard() {

        // Iterate through every coordinate
        StringBuilder board = new StringBuilder();
        for (int row = rowCount - 1; row >= 0; row--) {

            // Searches horizontally, then vertically
            for (int column = 0; column < columnCount; column++) board.append(columns[column].charAt(row));

            // Move down a line when row ends
            board.append("\n");
        }

        return board.substring(0,board.length()-1);
    }

    public int getTurn() {
        return turn;
    }
}
