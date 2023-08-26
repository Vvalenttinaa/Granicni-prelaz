package com.example.pj2_2023.models.vozila;
import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.Terminal.*;

import java.io.*;

public class Autobus extends Vozilo{
    private static final int kapacitett = 52;
    private static final int zadrzavanje=100;
    public Autobus()
    {
        kapacitet = rand.nextInt(kapacitett)+1;
        setVrijemePolicija(zadrzavanje);
        setVrijemeCarina(zadrzavanje);
        generisiPutnike();
        slika= new File(System.getProperty("user.dir") + File.separator + "slike" + File.separator + "bus.png");
    }
    public boolean voziloUslov(Terminal t)
    {
        if(!t.isZaKamione())
            return true;
        return false;
    }

    @Override
    public String toString()
    {
        String pozicija="";
        if(this.pozicija != -1)
        {
            pozicija+=" na poziciji " + this.pozicija;
        }
        String s="Autobus " + idVozilo + pozicija + ", broj putnika je " + kapacitet +".";
        for(Putnik p:putnici)
        {
            s+="\n"+p;
        }
        return s;
    }
}
