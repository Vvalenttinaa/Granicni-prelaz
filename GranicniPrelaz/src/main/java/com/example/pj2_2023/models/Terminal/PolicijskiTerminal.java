package com.example.pj2_2023.models.Terminal;

import com.example.pj2_2023.*;
import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.vozila.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;

public class PolicijskiTerminal extends Terminal {

    public Object cekamVozilo = new Object();

    @Override
    public void run() {
        while (Simulacija.aktivnaVozila() || this.vozilo!=null) {
            synchronized (terminalLock) {
                while (vozilo == null) {
                    synchronized (cekamVozilo) {
                        try {
                            System.out.println("Policijski ceka ");
                            cekamVozilo.wait();
                            System.out.println("Policijski probudjen sa " + vozilo);
                        } catch (InterruptedException e) {
                            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                    }
                }
                synchronized (this){
                if (this.vozilo != null && !this.vozilo.zavrsioNaPolicijskom) {
                    System.out.println("NA POLICIJSKOM " + idTerminal + vozilo);
                  //  azurirajGui();
                    for (int i = 0; vozilo != null && i < vozilo.getPutnici().size(); i++) {
                        provjeraPauze();
                        System.out.println("Procesiranje putnika " + i + 1 + "na terminalu " + this.idTerminal);
                        try {
                            Thread.sleep(vozilo.getVrijemePolicija());
                        } catch (InterruptedException e) {
                            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                        Putnik p = null;
                        try {
                            p = vozilo.getPutnici().get(i);
                        } catch (Exception e) {
                            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                        }
                        if (p != null && p.isVozac() && !p.isIspravniDokmnti()) {
                            FileUtils.writeToFile(Simulacija.vozilaKojaNisuPreslaFile.getName(), "VOZAC SA NEISPRAVNIM DOKUMENTIMA\n" + this.vozilo);
                            for (int j = 0; j < vozilo.getPutnici().size(); j++) {
                                Putnik putnik = vozilo.getPutnici().get(j);
                                provjeraPauze();
                                sankcionisaniPutnici.add(putnik);
                                vozilo.getPutnici().remove(putnik);
                                vozilo.setKapacitet(vozilo.getKapacitet() - 1);
                                FileUtils.serijalizuj(Simulacija.sankcionisaniPutniciFile, p);
                                SimulacijaController.ispisiSpecijalniDogadjajNaGui("Na policijskom je sankcionisan vozac u " + vozilo.getIdVozilo() +", "+ putnik);
                            }
                            sankcionisanaVozila.add(vozilo);
                            synchronized (vozilo) {
                                vozilo.kraj = true;
                                vozilo.zavrsioNaPolicijskom = true;
                                Simulacija.noviRed.remove(this.vozilo);
                                System.out.println("KRAJ ZA  " + vozilo + " A JOS IMA " + Simulacija.noviRed.size());
                                vozilo.notify();
                                this.vozilo = null;
                                for (Vozilo v : Simulacija.noviRed) {
                                    if (v.getPozicija() == 0 && v.cekamSlobodanPolicijski && v.voziloUslov(this)) {
                                        System.out.println("Policijski je slobodan i ja obavijestim iz terminala nakon sankcionisanja" + v);
                                        synchronized (v) {
                                            v.cekamSlobodanPolicijski = false;
                                        }
                                        synchronized (v.lockCekamSlobodanPolicijski) {
                                            v.lockCekamSlobodanPolicijski.notify();
                                        }
                                        break;
                                    }
                                }
                            }
                        } else if (p != null && !p.isIspravniDokmnti()) {
                            FileUtils.writeToFile(Simulacija.vozilaKojaSuPreslaFile.getName(),"PUTNIK SA NEISPRAVNIM DOKUMENTIMA\n" + this.vozilo);
                            sankcionisaniPutnici.add(p);
                            vozilo.getPutnici().remove(p);
                            System.out.println("Na policijskom je sankcionisan putnik " + p + " iz " + this.vozilo.getIdVozilo());
                            FileUtils.serijalizuj(Simulacija.sankcionisaniPutniciFile, p);
                            SimulacijaController.ispisiSpecijalniDogadjajNaGui("Terminal " + idTerminal + ": Sankcionisan putnik " + p + ", Vozilo: " + vozilo.getIdVozilo());
                            System.out.println("Terminal " + idTerminal + ": Sankcionisan putnik " + p + ", Vozilo: " + vozilo.getIdVozilo());
                        }
                    }
                    if (vozilo != null) {
                        synchronized (vozilo) {
                            vozilo.zavrsioNaPolicijskom = true;
                            System.out.println("Saljem notify da je policijski zavrsio sa " + vozilo);
                            if (vozilo.cekamNaKrajPolicijskog) {
                                vozilo.notify();
                            }
                        }
                    }
                    azurirajKraj();
                }
            }
            }
        }
        System.out.println("kraj za policijski");
    }

    @Override
    protected void provjeraPauze()
    {
        provjeraPauzeSimulacije();
        provjeraPauzeFajl();
    }

    protected void provjeraPauzeFajl() {
        if (pauza || Simulacija.pauzaSimulacije) {
            System.out.println("PAUZA POLICIJSKI");
            synchronized (terminalLock) {
                try {
                    terminalLock.wait();
                } catch (InterruptedException e) {
                    GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                }
                System.out.println("P00000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
            }
        }
    }

    protected void provjeraPauzeSimulacije()
    {
        if (Simulacija.pauzaSimulacije) {
            System.out.println("PAUZA CARINSKI OD SIMULACIJE");
            synchronized (this.terminalLockPauzaSimulacije) {
                try {
                    this.terminalLockPauzaSimulacije.wait();
                } catch (InterruptedException e) {
                    GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
                }
            }
        }
    }


    private void nacrtajVoziloNaTerminall(Vozilo v) {
        if (this.idTerminal == 1)
            SimulacijaController.nacrtajVoziloNaTerminal(v.getSlika(), v.getIdVozilo(), this.idTerminal);
        else if (this.idTerminal == 2)
            SimulacijaController.nacrtajVoziloNaTerminal(v.getSlika(), v.getIdVozilo(), this.idTerminal);
        else if (this.idTerminal == 3)
            SimulacijaController.nacrtajVoziloNaTerminal(v.getSlika(), v.getIdVozilo(), this.idTerminal);
    }

    private void obrisiVoziloSaTerminala() {
        SimulacijaController.obrisiVoziloNaTerminalu(idTerminal);
    }

    public void azurirajGui() {
        if (idTerminal == 1) {
            nacrtajVoziloNaTerminall(this.vozilo);
            SimulacijaController.ispisiOpisTerminalaNaGui("Obradjuje se na terminalu 1 vozilo " + vozilo, SimulacijaController.opisPol1);
        } else if (idTerminal == 2) {
            nacrtajVoziloNaTerminall(this.vozilo);
            SimulacijaController.ispisiOpisTerminalaNaGui("Obradjuje se na terminalu 2 vozilo " + vozilo, SimulacijaController.opisPol2);
        }
        if (idTerminal == 3) {
            nacrtajVoziloNaTerminall(this.vozilo);
            SimulacijaController.ispisiOpisTerminalaNaGui("Obradjuje se na terminalu 3 vozilo " + vozilo, SimulacijaController.opisPol3);
        }
    }

    private void azurirajKraj() {
        if (idTerminal == 1) {
            SimulacijaController.ispisiOpisTerminalaNaGui("Terminal je slobodan", SimulacijaController.opisPol1);
            FileUtils.writeToFile(System.getProperty("user.dir") + File.separator + "terminal1.TXT", "Kraj " + new Date() + " jos u redu " + Simulacija.noviRed.size());
        } else if (idTerminal == 2) {
            SimulacijaController.ispisiOpisTerminalaNaGui("Terminal je slobodan", SimulacijaController.opisPol1);
            FileUtils.writeToFile(System.getProperty("user.dir") + File.separator + "terminal2.TXT", "Kraj " + new Date() + " jos u redu " + Simulacija.noviRed.size());
        }
        if (idTerminal == 3) {
            SimulacijaController.ispisiOpisTerminalaNaGui("Terminal je slobodan", SimulacijaController.opisPol1);
            FileUtils.writeToFile(System.getProperty("user.dir") + File.separator + "terminal3.TXT", "Kraj " + new Date() + " jos u redu " + Simulacija.noviRed.size());
        }
    }

    @Override
    public String toString() {
        return "PolicijskiTerminal{" +
                "zaKamione=" + zaKamione +
                ", idTerminal=" + idTerminal +
                ", vozilo=" + vozilo +
                '}';
    }
}
