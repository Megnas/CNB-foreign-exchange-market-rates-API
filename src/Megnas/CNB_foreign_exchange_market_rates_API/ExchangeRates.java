package Megnas.CNB_foreign_exchange_market_rates_API;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ExchangeRates {
    private static final String WEB_ADDRESS = "https://www.cnb.cz/cs/financni-trhy/devizovy-trh/kurzy-devizoveho-trhu/kurzy-devizoveho-trhu/denni_kurz.txt";

    private HashMap<String, CurrencyRecord> data;
    private ExchangeDate date;
    private int serialNumber;

    public static ExchangeRates GetExchangeRates(String path) {
        File f = new File(path);

        if(f == null || !f.exists() || !f.canRead()) {
            System.out.println("File do not exist or can not be read!");
            return null;
        }

        ArrayList<String> list = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String radek; //Epic Tomas Milka reference
            while ((radek = reader.readLine()) != null) {
                list.add(radek);
            }
        }
        catch (Exception ex) {
            System.out.println("Error while reading file: " + ex);
            return null;
        }
        return new ExchangeRates(list);
    }

    public static ExchangeRates GetExchangeRates() {
        return new ExchangeRates(GetTextDataFromWeb(WEB_ADDRESS));
    }

    public static ExchangeRates GetExchangeRates(ExchangeDate date) {
        return new ExchangeRates(GetTextDataFromWeb(WEB_ADDRESS + date.GetAsParameters()));
    }

    public boolean SaveExchangeRatesToFile(String path) {
        try {
            File f = new File(path);
            if (f.createNewFile()) {
                FileWriter writer = new FileWriter(f, true);
                for(String radek : ToSaveData()) {
                    writer.write(radek + "\n");
                }
                writer.close();
            } else {
                System.out.println("Error: File already exists!");
            }
        } catch (Exception ex) {
            System.out.println("Error while creating new file: " + ex);
            return false;
        }
        return true;
    }

    private static ArrayList<String> GetTextDataFromWeb(String path) {
        ArrayList<String> list = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader((new URL(path)).openStream()));
            String radek; //Epic Tomas Milka reference
            while ((radek = reader.readLine()) != null) {
                list.add(radek);
            }
        }
        catch (Exception ex) {
            System.out.println("Error occurred while reading file from web: " + ex);
        }
        finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch (Exception ex) {
                    System.out.println("Error occurred while trying to close BufferedReader: " + ex);
                }
            }
        }
        return list;
    }

    private ExchangeRates(ArrayList<String> textData) {
        data = new HashMap<>();
        if(textData.size() == 0) return;
        String[] header = textData.get(0).split(" #");
        if(header.length != 2) {
            System.out.println("Error while parsing head: Cannot split correctly!");
        }
        date = ParseDate(header[0]);
        serialNumber = ParseSerialNumber(header[1]);
        for (int i = 2; i < textData.size(); i++) {
            CurrencyRecord record = ParseCurrency(textData.get(i));
            if(record != null) {
                data.put(record.code(), record);
            }
        }
    }

    private ExchangeDate ParseDate(String text) {
        try {
            String[] date = text.split("\\.");
            return new ExchangeDate(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
        }
        catch (Exception ex) {
            System.out.println("Error while parsing date!");
            return new ExchangeDate(0, 0, 0);
        }
    }

    private int ParseSerialNumber(String text) {
        try {
            return Integer.parseInt(text);
        }
        catch (Exception ex) {
            System.out.println("Error while parsing serialnumber!");
            return -1;
        }
    }

    private CurrencyRecord ParseCurrency(String text) {
        try {
            String[] data = text.split("\\|");
            return new CurrencyRecord(data[0], data[1], Double.parseDouble(data[2].replace(",", ".")), data[3], Double.parseDouble(data[4].replace(",", ".")));
        }
        catch (Exception ex) {
            System.out.println("Error while parsing currency!");
            return null;
        }
    }

    private ArrayList<String> ToSaveData() {
        ArrayList<String> list = new ArrayList<>();
        list.add(date.GetTextFormat() + " #" + serialNumber);
        list.add("země|měna|množství|kód|kurz");
        for (String code : data.keySet()) {
            list.add(data.get(code).GetTextFormat());
        }
        return list;
    }

    public Set<String> GetAllCodes() {
        return data.keySet();
    }

    public boolean Contains(String code) {
        return data.containsKey(code);
    }

    public CurrencyRecord GetCurrencyRecord(String code) {
        return data.get(code);
    }

    public CurrencyRecord GetCurrencyRecordSafe(String code) {
        if(data.containsKey(code)) {
            return data.get(code);
        }
        else {
            return null;
        }
    }

    public ExchangeDate GetDate() {
        return date;
    }

    public int GetSerialNumber() {
        return serialNumber;
    }
}
