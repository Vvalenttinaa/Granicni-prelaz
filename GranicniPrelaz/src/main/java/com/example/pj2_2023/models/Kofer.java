package com.example.pj2_2023.models;

import java.io.*;

public class Kofer implements Serializable {
    private boolean nedozvoljeneStvari;

    public Kofer() {
    }

    public boolean isNedozvoljeneStvari() {
        return nedozvoljeneStvari;
    }

    public void setNedozvoljeneStvari(boolean nedozvoljeneStvari) {
        this.nedozvoljeneStvari = nedozvoljeneStvari;
    }

    @Override
    public String toString() {
        String s = "Kofer sa ";
        if (nedozvoljeneStvari) {
            s += "nedozvoljenim stvarima. ";
        } else {
            s += "dozvoljenim stvarima. ";
        }
        return s;
    }
}
