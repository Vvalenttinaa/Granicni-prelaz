package com.example.pj2_2023;

import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.vozila.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class Simulacija {

    public static boolean pauzaSimulacije;
    private static List<Vozilo> red = new ArrayList<>();
    public static ConcurrentLinkedQueue<Vozilo> noviRed = new ConcurrentLinkedQueue<>();
    public static List<Vozilo> podaci = new ArrayList<>();

    private GranicniPrelaz gr;
    private KontrolaTerminala k;

    private static Random rand = new Random();

    private static final int BROJ_KAMIONA = 10;
    private static final int BROJ_AUTA=35;
    private static final int BROJ_AUTOBUSA=5;

    public static final File sankcionisaniPutniciFile = new File(System.getProperty("user.dir") + File.separator + "sankcionisani" + System.currentTimeMillis() + ".ser");
    public static final File evidencijaCarinskogFile = new File(System.getProperty("user.dir") + File.separator + "evidencija_carine" + System.currentTimeMillis() + ".TXT");
    public static final File vozilaKojaNisuPreslaFile = new File(System.getProperty("user.dir") + File.separator + "vozilaKojaNisuPresla" + System.currentTimeMillis() + ".TXT");
    public static final File vozilaKojaSuPreslaFile = new File(System.getProperty("user.dir") + File.separator + "vozilaKojaSuPresla"+System.currentTimeMillis() + ".TXT");

    public Simulacija()
    {
        dodajVozila();
        for(Vozilo v: red)
        {
            noviRed.add(v);
        }

        gr = new GranicniPrelaz();
        k = new KontrolaTerminala();

        for(Vozilo v:red)
        {
            podaci.add(v);
        }
    }

    private static void dodajVozila() {
        for (int i = 0; i < BROJ_AUTA; i++)
            red.add(new LicnoVozilo());
        for (int i = 0; i < BROJ_AUTOBUSA; i++)
            red.add(new Autobus());
        for (int i = 0; i < BROJ_KAMIONA; i++)
            red.add(new Kamion());

        Collections.shuffle(red);

        generisiNeispravneDokumente();

        generisiNedozvoljeneKofere();

        generisiStvarnuMasuTereta();

        generisiPozicije();

    }

    private static void generisiNeispravneDokumente() {
        int sum = red.stream()
                .mapToInt(Vozilo::getKapacitet)
                .sum();

        int br = sum * 3 / 100;

        for (int k = 0; k < br; k++) {
            System.out.println(k);
            int vozilo = rand.nextInt(red.size());
            int putnik = rand.nextInt(red.get(vozilo).getPutnici().size());

            if (red.get(vozilo).getPutnici().get(putnik).isIspravniDokmnti()) {
                red.get(vozilo).getPutnici().get(putnik).setIspravniDokmnti(false);
            } else {
                k--;
            }
        }
    }

    private static void generisiNedozvoljeneKofere() {
        long count = red.stream()
                .flatMap(list -> list.getPutnici().stream())
                .filter(p -> p.imaKofer())
                .count();

        int br = (int) count * 10 / 100;

        for (int i = 0; i < br; i++) {
            int vozilo = rand.nextInt(red.size());
            int putnik = rand.nextInt(red.get(vozilo).getPutnici().size());

            if (red.get(vozilo).getPutnici().get(putnik).imaKofer() && !red.get(vozilo).getPutnici().get(putnik).getKofer().isNedozvoljeneStvari()) {
                red.get(vozilo).getPutnici().get(putnik).getKofer().setNedozvoljeneStvari(true);
            } else {
                i--;
            }
        }
    }

    private static void generisiStvarnuMasuTereta() {
        int brojUvecanih = BROJ_KAMIONA * 20 / 100;
        for (Vozilo v : red) {
            if (v instanceof Kamion) {
                if (brojUvecanih > 0) {
                    ((Kamion) v).uvecajTeret();
                    brojUvecanih--;
                }
            }
        }
    }

    private static void generisiPozicije()
    {
        for(int i=0; i<Simulacija.red.size(); i++)
        {
            red.get(i).setPozicija(i);
        }
    }

    public static boolean aktivnaVozila()
    {
        for(Vozilo v:noviRed)
        {
            if (!v.kraj)
            {
             //   System.out.println("zivo" + v.getIdVozilo() + " na " + v.getPozicija() + v.presaoNaPolicijski + v.presaoNaCarinski + v.isAlive() + " " + v.kraj);
                return true;
            }
        }
        SimulacijaController.removeImageFromGui(4);
        SimulacijaController.removeImageFromGui(3);
        SimulacijaController.removeImageFromGui(2);
        SimulacijaController.removeImageFromGui(1);
        SimulacijaController.removeImageFromGui(0);
        for(int i=1; i<=5; i++)
        SimulacijaController.obrisiVoziloNaTerminalu(i);

        return false;
    }

    public void pokreniSimulaciju()
    {
        for (int i=0; i<GranicniPrelaz.getTerminali().size(); i++) {
            GranicniPrelaz.getTerminali().get(i).start();
        }
        for(Vozilo v: noviRed)
        {
            v.start();
        }
        k.kontrola();

    }

}
