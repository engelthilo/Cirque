package model;

import java.sql.Timestamp;
//denne klasse er opbygget af metoder som henter information fra  databassen gennem DBConnect, og returnere
//de forskellige kolonner mm's værdier til JavaFX skelettet. 
public class buildHolder {

    private String movieName;
    private Timestamp time;
    private int columns;
    private int rows;
    private String[] reserved_x;
    private String[] reserved_y;
    private String cinemaName;
    private int reservedNumber;
    private int showId;
    private Boolean[][] resSeat;

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

    public String[] getReserved_x() {
        return reserved_x;
    }

    public void setReserved_x(String[] reserved_x) {
        this.reserved_x = reserved_x;
    }

    public String[] getReserved_y() {
        return reserved_y;
    }

    public void setReserved_y(String[] reserved_y) {
        this.reserved_y = reserved_y;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public int getReservedNumber() {
        return reservedNumber;
    }

    public void setReservedNumber(int reservedNumber) {
        this.reservedNumber = reservedNumber;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public Boolean[][] getResSeat() {
        return resSeat;
    }

    public void setResSeat(Boolean[][] resSeat) {
        this.resSeat = resSeat;
    }
}
