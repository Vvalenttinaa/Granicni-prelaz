package com.example.pj2_2023;

import com.example.pj2_2023.models.*;
import com.example.pj2_2023.models.Terminal.*;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.logging.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;


public class KontrolaTerminala {

    private static final String fajlPauze = System.getProperty("user.dir");
    private static final String fajlKontrola = "kontrola_terminala.TXT";

    public void kontrola()
    {
        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = Paths.get(fajlPauze);
            dir.register(watcher, ENTRY_MODIFY);
     //       System.out.println("Watch Service registered for dir: " + dir.getFileName());
            while (Simulacija.aktivnaVozila()) {
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }

                for (WatchEvent<?> event : key.pollEvents())
                {
                    WatchEvent.Kind<?> kind = event.kind();
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path fileName = ev.context();
                    System.out.println(kind.name() + ": " + fileName);
                    if(fileName.toString().trim().equals(fajlKontrola) && kind.equals(ENTRY_MODIFY)) {
                        List<String> content = Files.readAllLines(dir.resolve(fileName));
                        for(String s: content)
                        {
                            {
                                String[] args = s.split(":");
                                if("true".equals(args[1]))
                                {
                                    zaustaviTerminal(args[0]);
                                }
                                else if("false".equals(args[1]))
                                {
                                    pokreniTerminal(args[0]);
                                }
                            }
                        }
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
            System.out.println("kraj za kontrolu");
        } catch (IOException ex) {
            GranicniPrelazApplication.logger.log(Level.SEVERE, ex.fillInStackTrace().toString());
        }
    }

    private void zaustaviTerminal(String id)
    {
        for(Terminal t: GranicniPrelaz.getTerminali())
        {
            System.out.println("trazim terminal sa id " + id);
            if(t.getIdentifikator() == Integer.valueOf(id))
            {
                System.out.println("nasao terminal sa id " + id);
                if(!t.isPauza()) {
                    t.setPauza(true);
                    FileUtils.writeToFile("pom.TXT",id + " : " + t.isPauza());
                }
            }
        }
    }

    private void pokreniTerminal(String id)
    {
        for(Terminal t: GranicniPrelaz.getTerminali())
        {
            if(t.getIdentifikator() == Integer.valueOf(id))
            {
                if(t.isPauza()) {
                    synchronized (t.terminalLock) {
                        t.setPauza(false);
                        FileUtils.writeToFile("pom.TXT",id + " : " + (t.isPauza()));
                        t.terminalLock.notify();
                    }
                }
            }
        }
    }
}
