package com.example.pj2_2023.models.vozila;

import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.Terminal.*;

import java.io.*;

public class LicnoVozilo extends Vozilo {
    private static final int zadrzavanje=500;
    public LicnoVozilo() {
        kapacitet = rand.nextInt(5) + 1;
        setVrijemePolicija(zadrzavanje);
        generisiPutnike();
        setVrijemeCarina(zadrzavanje);
        slika = new File(System.getProperty("user.dir") + File.separator + "slike" + File.separator + "car.png");
    }

    public boolean voziloUslov(Terminal t)
    {
        if(!t.isZaKamione())
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
        String s="Licno vozilo " + idVozilo + pozicija + ", broj putnika je " + kapacitet +".";
        for(Putnik p:putnici){
            s+="\n" + p;
        }
        if(cekamSlobodanPolicijski)
        {
            s+="Cekam slobodan policijski.";
        }
        if(presaoNaPolicijski)
        {
            s+="\nPresao na policijski.";
        }
        if(cekamSlobodanCarinski)
        {
            s+="Cekam slobodan carisnki.";
        }
        if(presaoNaCarinski)
        {
            s+="\nPresao na carinski.";
        }
        if(kraj)
        {
            s+="\nZavrsio kretanje.";
        }
        return s;
    }

 /*
    @Override
    public String toString() {
        return "LicnoVozilo{" +
                "idVozilo=" + idVozilo +
                ", kraj=" + kraj +
                ", pozicija=" + pozicija +
                ", cekanje=" + cekanje +
                ", presaoNaPolicijski=" + presaoNaPolicijski +
                ", cekamNaKrajPolicijskog=" + cekamNaKrajPolicijskog +
                ", presaoNaCarinski=" + presaoNaCarinski +
                ", cekamNaKrajCarinskog=" + cekamNaKrajCarinskog +
                ", cekamNaSlobodanPolicijski=" + cekamSlobodanPolicijski +
                ", cekaoSamNekada=" + cekaoSamNekada +
                '}';
    }

 */
}
