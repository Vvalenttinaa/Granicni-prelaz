package com.example.pj2_2023.models.Terminal;

import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.vozila.*;

import java.util.*;

public abstract class Terminal extends Thread {
    protected boolean zaKamione;
    protected boolean pauza;
    protected static int identifikator=1;
    protected int idTerminal;
    public Vozilo vozilo;

    public Object terminalLock = new Object();
    public Object terminalLockPauzaSimulacije = new Object();

    protected boolean pauzaSimulacije=false;

    protected List<Putnik> sankcionisaniPutnici = new ArrayList<>();
    protected List<Vozilo> sankcionisanaVozila = new ArrayList<>();

    public Terminal()
    {
        this.idTerminal =identifikator++;
        this.pauza=false;
    }

    public boolean isZaKamione() {
        return zaKamione;
    }

    public void setZaKamione(boolean zaKamione) {
        this.zaKamione = zaKamione;
    }

    public boolean isPauza() {
        return pauza;
    }

    public void setPauza(boolean pauza) {
        this.pauza = pauza;
    }

    protected abstract void provjeraPauze();

    public int getIdentifikator() {
        return idTerminal;
    }

    public boolean isPauzaSimulacija() {
        return pauzaSimulacije;
    }

    public void setPauzaSimulacija(boolean pauzaSimulacije) {
        this.pauzaSimulacije = pauzaSimulacije;
    }

    @Override
    public String toString() {
        return "Terminal{" +
                "zaKamione=" + zaKamione +
                ", pauza=" + pauza +
                ", idTerminal=" + idTerminal +
                ", vozilo=" + vozilo +
                ", terminalLock=" + terminalLock +
                ", sankcionisaniPutnici=" + sankcionisaniPutnici +
                ", sankcionisanaVozila=" + sankcionisanaVozila +
                '}';
    }
}
