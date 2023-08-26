package com.example.pj2_2023.models;

import com.example.pj2_2023.models.Terminal.*;

import java.util.*;

public class GranicniPrelaz {
    private static List<Terminal> terminali = new ArrayList<>();

    public GranicniPrelaz() {
        for (int i = 0; i < 5; i++) {
            if (i < 3) {
                terminali.add(new PolicijskiTerminal());
            } else {
                terminali.add(new CarinskiTerminal());
            }
        }
        terminali.get(0).setZaKamione(true);
        terminali.get(terminali.size() - 1).setZaKamione(true);

        for(Terminal t:terminali)
        {
            System.out.println(t);
        }
    }

    public static List<Terminal> getTerminali() {
        return terminali;
    }

}
