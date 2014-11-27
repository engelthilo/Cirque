package model;

import java.sql.Timestamp;

/**
 * Created by frederik on 27/11/14.
 */
public class buildHolder {

    private String movieName;
    private Timestamp time;
    private int columns;
    private int rows;
    private int[] reserved_x;
    private int[] reserved_y;
    private String cinemaName;

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int[] getReserved_x() {
        return reserved_x;
    }

    public void setReserved_x(int[] reserved_x) {
        this.reserved_x = reserved_x;
    }

    public int[] getReserved_y() {
        return reserved_y;
    }

    public void setReserved_y(int[] reserved_y) {
        this.reserved_y = reserved_y;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }
}
