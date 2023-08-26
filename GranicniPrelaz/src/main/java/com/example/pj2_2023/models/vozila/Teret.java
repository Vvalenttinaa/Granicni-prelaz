package com.example.pj2_2023.models.vozila;

import java.util.*;

public class Teret {

    private double deklarisanaMasa;
    private double stvarnaMasa;

    Random rand = new Random();

    public Teret()
    {
        this.deklarisanaMasa=rand.nextInt(7)+3;
        this.stvarnaMasa=deklarisanaMasa;
    }

    @Override
    public String toString()
    {
        return "\nDeklarisana masa: " + deklarisanaMasa + "\nStvarna masa: "+stvarnaMasa+".";
    }


    public double getDeklarisanaMasa() {
        return deklarisanaMasa;
    }

    public void setDeklarisanaMasa(double deklarisanaMasa) {
        this.deklarisanaMasa = deklarisanaMasa;
    }

    public double getStvarnaMasa() {
        return stvarnaMasa;
    }

    public void setStvarnaMasa(double stvarnaMasa) {
        this.stvarnaMasa = stvarnaMasa;
    }

}
