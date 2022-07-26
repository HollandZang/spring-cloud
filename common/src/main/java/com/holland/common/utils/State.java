package com.holland.common.utils;

public class State<T> {
    public T val;
    public Throwable e;

    public State(T val) {
        this.val = val;
    }

    public State(Throwable e) {
        this.e = e;
    }

    public boolean ok() {
        return this.e == null;
    }
}
