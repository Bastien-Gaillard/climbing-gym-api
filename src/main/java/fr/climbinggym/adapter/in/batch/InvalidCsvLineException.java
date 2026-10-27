package fr.climbinggym.adapter.in.batch;

public class InvalidCsvLineException extends RuntimeException {

    public InvalidCsvLineException(String message) {
        super(message);
    }
}
