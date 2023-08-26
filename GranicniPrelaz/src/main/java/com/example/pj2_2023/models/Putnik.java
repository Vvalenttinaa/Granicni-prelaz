package com.example.pj2_2023.models;

import java.io.*;
import java.util.*;

public class Putnik implements Serializable
{
    private boolean imaKofer;
    private Kofer kofer;
    private static int id=0;
    private int identifikacioniDokument;
    private boolean ispravniDokmnti;
    private boolean vozac;

    Random rand = new Random();

    public Putnik()
    {
        this.identifikacioniDokument=id++;
        this.ispravniDokmnti=true;
    }

    public boolean imaKofer() {
        return imaKofer;
    }

    public void setImaKofer() {
        int k = rand.nextInt(100);
        if(k>70)
        {
            imaKofer = true;
            kofer= new Kofer();
        }
        else
            imaKofer=false;
    }

    public boolean isIspravniDokmnti() {
        return ispravniDokmnti;
    }

    public void setIspravniDokmnti(boolean ispravniDokmnti) {
        this.ispravniDokmnti = ispravniDokmnti;
    }

    public boolean isVozac() {
        return vozac;
    }

    public void setVozac(boolean vozac) {
        this.vozac = vozac;
    }

    public Kofer getKofer() {
        return kofer;
    }

    public void setKofer(Kofer kofer) {
        this.kofer = kofer;
    }

    public int getIdentifikacioniDokument() {
        return identifikacioniDokument;
    }

    public void setIdentifikacioniDokument(int identifikacioniDokument) {
        this.identifikacioniDokument = identifikacioniDokument;
    }

    @Override
    public String toString() {
        String s = "Putnik: " + identifikacioniDokument;
        if(ispravniDokmnti){
            s+=", ispravni dokumenti";
        }else{
            s+=", neispravni dokumenti";
        }
        if(vozac){
            s+=", vozac. ";
        }else{
            s+=", nije vozac. ";
        }
        if(imaKofer)
        {
            s+= kofer.toString();
        }
        return s;
    }
}
