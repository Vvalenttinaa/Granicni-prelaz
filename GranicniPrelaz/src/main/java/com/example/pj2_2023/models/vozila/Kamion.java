package com.example.pj2_2023.models.vozila;
import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.Terminal.*;

import java.io.*;

public class Kamion extends Vozilo {
    private Teret teret;
    private boolean carinskaDokumentacija;
    private static final int zadrzavanje=500;
    private static final int kapacitett=3;

    public Kamion() {
        kapacitet = rand.nextInt(kapacitett) + 1;
        this.teret = new Teret();
        setVrijemePolicija(zadrzavanje);
        setVrijemeCarina(zadrzavanje);
        generisiPutnike();
        carinskaDokumentacija = rand.nextBoolean();
        slika = new File(System.getProperty("user.dir") + File.separator + "slike" + File.separator + "truck.png");
    }

    public boolean voziloUslov(Terminal t)
    {
        if(t.isZaKamione())
            return true;
        return false;
    }


    @Override
    public String toString(){
        String pozicija="";
        if(this.pozicija != -1)
        {
            pozicija+=" na poziciji " + this.pozicija;
        }
        String s="Kamion " + idVozilo + pozicija + ", broj putnika je " + kapacitet +".";
        for(Putnik p:putnici){
            s+="\n" + p;
        }
        if(carinskaDokumentacija)
        {
            s+=" Ima carinsku dokumentaciju.";
        }else
        {
            s+=" Nema carinsku dokumentaciju.";
        }
        s+=teret.toString();
        return s;
    }


/*
    @Override
    public String toString() {
        return "Kamion{" +
                "idVozilo=" + idVozilo +
                ", kraj=" + kraj +
                ", pozicija=" + pozicija +
                ", cekanje=" + cekanje +
                ", presaoNaPolicijski=" + presaoNaPolicijski +
                ", cekamNaKrajPolicijskog=" + cekamNaKrajPolicijskog +
                ", presaoNaCarinski=" + presaoNaCarinski +
                ", cekamNaKrajCarinskog=" + cekamNaKrajCarinskog +
                ", cekamSlobodanPolicijski=" + cekamSlobodanPolicijski +
                ", cekamSlobodanCarinski=" + cekamSlobodanCarinski +
                ", cekaoSamNekada=" + cekaoSamNekada +
                '}';
    }

 */

    public boolean carinskaDokumentacija() {
        return carinskaDokumentacija;
    }

    public void setCarinskaDokumentacija(boolean carinskaDokumentacija) {
        this.carinskaDokumentacija = carinskaDokumentacija;
    }

    public Teret getTeret() {
        return teret;
    }

    public void setTeret(Teret teret) {
        this.teret = teret;
    }

    public void uvecajTeret()
    {
        teret.setStvarnaMasa(teret.getStvarnaMasa()+ teret.getStvarnaMasa()*30/100);
    }
}
