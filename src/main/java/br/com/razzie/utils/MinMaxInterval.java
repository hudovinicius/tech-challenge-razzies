package br.com.razzie.utils;

import lombok.Getter;

@Getter
public class MinMaxInterval {
    private int min;
    private int max;

    public MinMaxInterval() {
        this.min = Integer.MAX_VALUE;
        this.max = Integer.MIN_VALUE;
    }

    public void update(int value) {
        if (value < this.min) this.min = value;
        if (value > this.max) this.max = value;
    }

    public boolean greaterThanMax(int value) {
        return value > this.max;
    }

    public boolean lessThanMin(int value) {
        return value < this.min;
    }

    public boolean equalsMin(int value) {
        return value == this.min;
    }

    public boolean equalsMax(int value) {
        return value == this.max;
    }
}
