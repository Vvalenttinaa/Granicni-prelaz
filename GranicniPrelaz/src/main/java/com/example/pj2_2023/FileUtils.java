package com.example.pj2_2023;
import com.example.pj2_2023.models.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class FileUtils
{
    public static synchronized void writeToFile(String fileName, String text) {
        if (!new File(fileName).exists()) {
            try {
                new File(fileName).createNewFile();
            } catch (IOException e) {
                GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }
            PrintWriter out = null;
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
                out.println(text);
            } catch (IOException e) {
                GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
            } finally {
                if (out != null) {
                    out.close();
                }
            }
    }

    public static synchronized void serijalizuj(File f, Putnik putnik)
    {
        if(!f.exists())
        {
            try {
                f.createNewFile();
            } catch (IOException e) {
                GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
            }
        }
        List<Putnik>putnici=deserijalizuj();
        putnici.add(putnik);
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            for(Putnik p:putnici) {
                oos.writeObject(p);
            }
            oos.close();
        } catch (IOException e)
        {
            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
        }
    }

    public static synchronized List<Putnik> deserijalizuj() {
        System.out.println("DESER");
        List<Putnik> putnici = new ArrayList<>();
        try
        {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Simulacija.sankcionisaniPutniciFile));
           try{
               while (true) {
                   Object o = ois.readObject();
                   if (o instanceof Putnik) {
                       Putnik p = (Putnik) o;
                       putnici.add(p);
                   }
               }
           }catch(EOFException e){
               GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
           }
            ois.close();
        } catch (Exception e) {
            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
        }
        return putnici;
    }

    public static List<String> readFromFile(File f)
    {
        if(!f.exists())
        {
            List<String> l = new ArrayList<>();
            l.add("PRAZNO");
            return l;
        }
        try {
            return Files.readAllLines(f.toPath());
        } catch (Exception e) {
            GranicniPrelazApplication.logger.log(Level.SEVERE, e.fillInStackTrace().toString());
        }
        return null;
    }
}
