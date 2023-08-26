package com.example.pj2_2023.models.Terminal;

import com.example.pj2_2023.*;
import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.vozila.*;

import java.util.logging.*;

public class CarinskiTerminal extends Terminal {
    public Object cekamVoziloNaCarinskom = new Object();

    @Override
    public void run() {
        while (Simulacija.aktivnaVozila()) {
            synchronized (terminalLock) {
                while (this.vozilo == null) {
                    synchronized (cekamVoziloNaCarinskom) {
                        try {
                            cekamVoziloNaCarinskom.wait();
                        } catch (InterruptedException e) {
                            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                    }
                }
                if (this.vozilo != null) {
                    System.out.println("NA CARINSKOM " + vozilo);
                   // azuruirajGui();
                    SimulacijaController.nacrtajVoziloNaTerminal(vozilo.getSlika(), vozilo.getIdVozilo(), idTerminal);
                    //   nacrtajVoziloNaTerminall(vozilo);
                    provjeraPauze();
                    if (vozilo instanceof LicnoVozilo) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                    } else if (vozilo instanceof Autobus) {
                        for (int i = 0; i < vozilo.getPutnici().size(); i++) {
                            provjeraPauze();
                            Putnik putnik = vozilo.getPutnici().get(i);
                            if (putnik.imaKofer()) {
                                if (putnik.getKofer().isNedozvoljeneStvari()) {
                                    sankcionisaniPutnici.add(putnik);
                                    FileUtils.writeToFile(Simulacija.vozilaKojaSuPreslaFile.getName(),"PUTNIK SA NEDOZVOLJENIM STVARIMA\n" + this.vozilo);
                                    String s = "Sankcionisan putnik " + putnik.getIdentifikacioniDokument() + " iz vozila " + vozilo.getIdVozilo() + " zbog nedozvoljenih stvari.";
                                    System.out.println(s);
                                    SimulacijaController.ispisiSpecijalniDogadjajNaGui(s);
                                    FileUtils.serijalizuj(Simulacija.sankcionisaniPutniciFile, putnik);
                                    FileUtils.writeToFile(Simulacija.evidencijaCarinskogFile.toString(), s);
                                    vozilo.getPutnici().remove(putnik);
                                    vozilo.setKapacitet(vozilo.getKapacitet()-1);
                                    i++;
                                }
                            }
                        }
                    } else if (vozilo instanceof Kamion) {
                        if (((Kamion) vozilo).carinskaDokumentacija()) {
                            String s="Generisanje carinske dokumenacije za " + this.vozilo.getIdVozilo();
                            System.out.println(s);
                            SimulacijaController.ispisiSpecijalniDogadjajNaGui(s);
                        }
                        if (((Kamion) vozilo).getTeret().getStvarnaMasa() > ((Kamion) vozilo).getTeret().getDeklarisanaMasa()) {
                            String s = "Sankcionisanje kamiona " + this.vozilo.getIdVozilo()+ " zbog nedozvoljenog tereta.";
                            FileUtils.writeToFile(Simulacija.vozilaKojaNisuPreslaFile.getName(), "KAMION SA NEDOZVOLJENIM TERETOM\n" + this.vozilo);
                            SimulacijaController.ispisiSpecijalniDogadjajNaGui(s);
                            FileUtils.writeToFile(Simulacija.evidencijaCarinskogFile.toString(), s);

                            sankcionisanaVozila.add(vozilo);
                            for (Putnik p : vozilo.getPutnici()) {
                                provjeraPauze();
                                String str = "Sankcionisanje putnika " + p.getIdentifikacioniDokument() + " iz kamiona " + vozilo.getIdVozilo() + " zbog nedozvoljenog tereta.";
                                FileUtils.writeToFile(Simulacija.evidencijaCarinskogFile.toString(), str);
                                FileUtils.serijalizuj(Simulacija.sankcionisaniPutniciFile, p);
                                SimulacijaController.ispisiSpecijalniDogadjajNaGui(str);
                                sankcionisaniPutnici.add(p);
                            }
                            String izb = "Izbaceno iz reda za carinu " + vozilo.getIdVozilo();
                            System.out.println(izb);
                            SimulacijaController.ispisiSpecijalniDogadjajNaGui(izb);
                            SimulacijaController.obrisiVoziloNaTerminalu(this.idTerminal);

                        } else {
                         //   System.out.println("Proslo granicu " + vozilo.getIdVozilo());
                         //   SimulacijaController.ispisiSpecijalniDogadjajNaGui("Proslo granicu " + vozilo.getIdVozilo());
                        }
                    }
                    synchronized (vozilo) {
                        System.out.println("***********************************Brisem vozilo na terminalu " + idTerminal + " " + vozilo.getIdVozilo());
                        SimulacijaController.obrisiVoziloNaTerminalu(this.idTerminal);
                        vozilo.cekamNaKrajCarinskog = false;
                        System.out.println("Saljem notify da je carinski zavrsio sa " + vozilo);
                        Simulacija.noviRed.remove(this.vozilo);
                        System.out.println("KRAJ ZA  " + vozilo + " A JOS IMA " + Simulacija.noviRed.size());
                        vozilo.notify();
                    }
                    this.vozilo.cekamNaKrajCarinskog = false;
                    synchronized (this) {
                        this.vozilo = null;
                        azuruirajGuiPrazan();

                    }
                    SimulacijaController.obrisiVoziloNaTerminalu(this.idTerminal);
                    for (Vozilo v : Simulacija.noviRed) {
                        if (v.cekamSlobodanCarinski) {
                            System.out.println("Carinski je slobodan i ja obavijestim " + v.getIdVozilo());
                            v.cekamSlobodanCarinski = false;
                            synchronized (v.lockCekamSlobodanCarinski) {
                                v.lockCekamSlobodanCarinski.notify();
                            }
                            break;
                        }
                    }
                }
            }
        }
        System.out.println("kraj za kontrolu");
    }

    private void nacrtajVoziloNaTerminall(Vozilo v) {
        if (this.idTerminal == 4)
            SimulacijaController.nacrtajVoziloNaTerminal(v.getSlika(), v.getIdVozilo(), idTerminal);
        else if (this.idTerminal == 5)
            SimulacijaController.nacrtajVoziloNaTerminal(v.getSlika(), v.getIdVozilo(), idTerminal);
    }

    @Override
    protected void provjeraPauze() {
        provjeraPauzeSimulacije();
        provjeraPauzeFajl();
    }


    protected void provjeraPauzeFajl() {
        if (pauza) {
            System.out.println("PAUZA CARINSKI");
            synchronized (this.terminalLock) {
                try {
                    this.terminalLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void provjeraPauzeSimulacije() {
        if (Simulacija.pauzaSimulacije) {
            System.out.println("PAUZA CARINSKI OD SIMULACIJE");
            synchronized (this.terminalLockPauzaSimulacije) {
                try {
                    this.terminalLockPauzaSimulacije.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void azuruirajGui() {
        if (idTerminal == 4) {
            SimulacijaController.ispisiOpisTerminalaNaGui(vozilo.toString(), SimulacijaController.opisCar1);
        } else if (idTerminal == 5) {
            SimulacijaController.ispisiOpisTerminalaNaGui(vozilo.toString(), SimulacijaController.opisCar2);
        }
    }

    private void azuruirajGuiPrazan() {
        if (idTerminal == 4) {
            SimulacijaController.ispisiOpisTerminalaNaGui("Terminal je slobodan ", SimulacijaController.opisCar1);
        } else if (idTerminal == 5) {
            SimulacijaController.ispisiOpisTerminalaNaGui("Terminal je slobodan ", SimulacijaController.opisCar2);
        }
    }

    @Override
    public String toString() {
        return "CarinskiTerminal{" +
                "zaKamione=" + zaKamione +
                ", idTerminal=" + idTerminal +
                ", vozilo=" + vozilo +
                '}';
    }
}
