package uy.edu.um.doors.exceptions;

import java.io.IOException;

public class DataLoadExeption extends Exception {
    public DataLoadExeption(String message, IOException e) { super(message); }
}