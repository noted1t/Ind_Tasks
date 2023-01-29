package org.unko;

import java.util.Date;

public class Task {
    String value;
    Date date;
    String executor;
    int status;

    public Task(String value, Date date, String executor, int status ) {
        this.value = value;
        this.date = date;
        this.executor = executor;
        this.status = status;
    }
}
