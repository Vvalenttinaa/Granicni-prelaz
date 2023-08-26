package com.example.pj2_2023.models.vozila;

import com.example.pj2_2023.*;
import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.Terminal.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public abstract class Vozilo extends Thread {
    protected List<Putnik> putnici = new ArrayList<>();
    protected int kapacitet;
    private long vrijemePolicija;
    private long vrijemeCarina;
    protected File slika;

    protected int idVozilo;
    private static int id = 0;
    protected int pozicija;
    public boolean kraj = false;

    protected Random rand = new Random();

    public static Object lock = new Object();
    public boolean cekanje = true;

    public boolean presaoNaPolicijski = false;
    public boolean cekamNaKrajPolicijskog = true;

    public boolean zavrsioNaPolicijskom = false;
    public boolean presaoNaCarinski = false;
    public boolean cekamNaKrajCarinskog = false;

    public boolean cekamSlobodanPolicijski = true;
    public static Object lockCekamSlobodanPolicijski = new Object();

    public boolean cekamSlobodanCarinski;
    public Object lockCekamSlobodanCarinski = new Object();

    PolicijskiTerminal pt;
    CarinskiTerminal ct;

  //  CountDownLatch myLatch;

    boolean cekaoSamNekada = false;
    private static Object redSinhr = new Object();
    private static Object biranjePolicijskog = new Object();

    public Vozilo() {
        this.idVozilo = id++;
    }

    public void run() {
        {
            while (!kraj) {
                if (pozicija == 0) {
                    System.out.println("na 0 " + this);
                    synchronized (biranjePolicijskog) {
                        do {
                            izaberiPolicijski();
                        } while (!this.presaoNaPolicijski);
                    }

                    synchronized (this) {
                        while(this.cekamNaKrajPolicijskog) {
                            try {
                                System.out.println("cekam kraj obrade na policijskom " + this.idVozilo);
                                this.wait();
                            } catch (Exception e) {
                                GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                            }
                            this.cekamNaKrajPolicijskog=false;
                            System.out.println(this.idVozilo + " zavrsio na pol " + pt);
                        }
                    }
                    //bilo ovdje notify policijskog

                } else if (this.zavrsioNaPolicijskom && !this.kraj) {
                    do {
                        izaberiCarinski();
                    } while (!this.presaoNaCarinski);

                    synchronized (this.pt) {
                        System.out.println(this.idVozilo + " bukvalno oslobodi " + this.pt.getIdentifikator());
                        this.pt.vozilo = null;
                        System.out.println("oslobodjeni " + this.pt.getIdentifikator() + " od " + this.idVozilo + " je " + this.pt.getIdentifikator());
                    }

                    synchronized (ct.cekamVoziloNaCarinskom) {
                        System.out.println("NA CARINSKOM " + ct.vozilo);
                        ct.cekamVoziloNaCarinskom.notify();
                    }
                    SimulacijaController.obrisiVoziloNaTerminalu(pt.getIdentifikator());

                    for (Vozilo v : Simulacija.noviRed) {
                        if (v.pozicija == 0 && v.cekamSlobodanPolicijski && voziloUslov(this.pt)) {
                            System.out.println("Policijski je slobodan i ja " + this.idVozilo + "obavijestim " + v);
                            synchronized (v) {
                                if(v.cekamSlobodanPolicijski)
                                {
                                    synchronized (v.lockCekamSlobodanPolicijski) {
                                        v.lockCekamSlobodanPolicijski.notify();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    synchronized (this) {
                        presaoNaCarinski = true;
                        cekamNaKrajCarinskog = true;
                        while (cekamNaKrajCarinskog) {
                            try {
                                this.wait();
                            } catch (Exception e) {
                                GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                            }
                        }
                        kraj = true;
                        System.out.println(this);
                        System.out.println("Zavrsio!!!!!!!!!!!!!!!!!!!" + this.idVozilo + " jos ima " + Simulacija.noviRed.size());
                        if(Simulacija.noviRed.size()==1)
                        {
                            System.out.println("Ostalo jos " + Simulacija.noviRed.peek());
                        }

                        SimulacijaController.obrisiVoziloNaTerminalu(ct.getIdentifikator());
                        Simulacija.noviRed.remove(this);
                    }
                } else if (pozicija > 0) {
                    synchronized (lock) {
                        if (this.cekanje) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                            }
                        }
                        synchronized (this) {
                            this.pozicija--;
                            this.cekanje = true;
                        //    myLatch.countDown();
                        //    System.out.println("Pomjerio" + this.idVozilo + " " + myLatch.getCount());

                        }
                        if (pozicija < 5) {
                            SimulacijaController.addToGui(pozicija, slika, idVozilo);
                        }
                    }
                }
            }
        }
    }

    public boolean pomjeriRed(Vozilo voz) {
        System.out.println("pozvao pomjeri red " + voz.idVozilo);
        synchronized (Simulacija.noviRed) {
         //   CountDownLatch latch = new CountDownLatch(brojPomjeraja--);
         //   System.out.println("Kreiran za " + latch.getCount());
            for (Vozilo v : Simulacija.noviRed) {
                if (v.pozicija == 1) {
                    System.out.println("Mene " + v.idVozilo + " pomijera sa " + v.pozicija + "na 0 vozilo " + voz.idVozilo);
                }

                synchronized (v) {
              //      v.myLatch = latch;
                    v.cekanje = false;
                }
            }
            synchronized (lock) {
                lock.notifyAll();
            }
            try
            {
                Thread.sleep(500);
            }catch (InterruptedException e){

            }
        }

        System.out.println("zavrsio sa pomijeranjem reda " + voz.idVozilo);

        return true;
    }

    public void izaberiPolicijski() {
        for (Terminal t : GranicniPrelaz.getTerminali()) {
                System.out.println("Provjeravam pol za " + this.idVozilo + t);
                if (t.vozilo == null && voziloUslov(t) && t instanceof PolicijskiTerminal && !this.presaoNaPolicijski) {

                    synchronized (this) {
                        this.presaoNaPolicijski = true;
                        this.pozicija = -1;
                        this.cekamSlobodanPolicijski = false;
                        System.out.println("Nasao policijski " + this.idVozilo);
                        this.pt = (PolicijskiTerminal) t;
                    }
                    synchronized (t) {
                        t.vozilo = this;
                        System.out.println("postavi na terminal " + this.idVozilo + t);
                        ((PolicijskiTerminal) t).azurirajGui();
                    }
                    synchronized (redSinhr) {
                        pomjeriRed(this);
                    }
                    if(this.pt.vozilo==this && !this.zavrsioNaPolicijskom) {
                        synchronized (pt.cekamVozilo) {
                            System.out.println("Saljem notify policijskom da krene " + this.idVozilo);
                            this.pt.cekamVozilo.notify();
                        }
                    }
                    break;
                }
        }

        if (pt == null) {
            System.out.println("+++++++++++Zaglavio cekajuci policijski " + this);
            synchronized (this) {
                cekamSlobodanPolicijski = true;
                cekaoSamNekada = true;
            }
            while (cekamSlobodanPolicijski) {
                synchronized (lockCekamSlobodanPolicijski) {
                    try {
                        lockCekamSlobodanPolicijski.wait();
                    } catch (InterruptedException e) {
                        GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                    }
                    this.cekamSlobodanPolicijski=false;
                }
            }
            System.out.println("+++++++++++OSLOBODJEN OD CEKANJA POLICIJSKOG " + this);
        }
    }

    public void izaberiCarinski() {
        for (Terminal t : GranicniPrelaz.getTerminali()) {
                System.out.println("Provjeravam carinski za " + this.idVozilo + t);
                if (t.vozilo == null && voziloUslov(t) && t instanceof CarinskiTerminal && !this.presaoNaCarinski) {
                    synchronized (t) {
                        t.vozilo = this;
                        ((CarinskiTerminal) t).azuruirajGui();
                    }
                    this.ct = (CarinskiTerminal) t;
                    synchronized (this) {
                        this.presaoNaCarinski = true;
                    }
                    System.out.println("PRELAZAAAAAAAAAAK " + idVozilo + " na " + ct.getIdentifikator());
                    break;
                }
     //       }
        }
        if (ct == null) {
            System.out.println("+++++++++++Zaglavio cekajuci carinski " + this);
            cekamSlobodanCarinski = true;
            while (cekamSlobodanCarinski) {
                synchronized (lockCekamSlobodanCarinski) {
                    try {
                        lockCekamSlobodanCarinski.wait();
                    } catch (InterruptedException e) {
                        GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                    }
                    cekamSlobodanCarinski = false;
                }
            }
            System.out.println("+++++++++++OSLOBODJEN OD CEKANJA CARINSKGOG " + this);
            //           izaberiCarinski();
        }
    }

    public abstract boolean voziloUslov(Terminal t);

    public int getKapacitet() {
        return kapacitet;
    }

    public void setKapacitet(int kapacitet) {
        this.kapacitet = kapacitet;
    }

    public long getVrijemePolicija() {
        return vrijemePolicija;
    }

    public void setVrijemePolicija(long vrijemeProcesiranja) {
        this.vrijemePolicija = vrijemeProcesiranja;
    }

    protected void generisiPutnike() {
        for (int i = 0; i < this.kapacitet; i++) {
            Putnik putnik = new Putnik();
            if (i == 0) {
                putnik.setVozac(true);
            }
            putnik.setImaKofer();
            this.putnici.add(putnik);
        }
    }

    public List<Putnik> getPutnici() {
        return putnici;
    }

    public long getVrijemeCarina() {
        return vrijemeCarina;
    }

    public void setVrijemeCarina(long vrijemeCarina) {
        this.vrijemeCarina = vrijemeCarina;
    }

    public File getSlika() {
        return slika;
    }

    public int getIdVozilo() {
        return idVozilo;
    }

    public void setIdVozilo(int idVozilo) {
        this.idVozilo = idVozilo;
    }

    public int getPozicija() {
        return pozicija;
    }

    public void setPozicija(int pozicija) {
        this.pozicija = pozicija;
    }


}
