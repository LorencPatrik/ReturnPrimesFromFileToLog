package cz.lorenc;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        FileManager fileManager = new FileManager();
        DataManager dataManager = new DataManager();
        int sheetNumber = 0;    // sheet number 1 (List1)
        int columnNumber = 1;   // column 2 (B)
        int rowCount;
        List<Long> primes;
        XSSFWorkbook workBook;
        XSSFSheet sheet;

        System.out.println("Aplikace pro práci se souborem *.xlsx, zadaným jako parametr. \n");

        // check if the address was entered as a parameter
        if (args.length == 0) {
            displayMessages("Nezadaná adresa *.xlsx souboru jako parametr aplikace...");
            return;
        }

        // loads the *.xlsx file if it was found
        String filePath = args[0];
        File file = fileManager.getFile(filePath);
        if (!file.exists()) {
            displayMessages("Soubor na zadané adrese nenalezen: " + filePath);
            return;
        }

        // loads the Excel table from *.xlsx file if possible
        try {
            workBook = dataManager.readExcel(file);
        } catch (Exception e) {
            displayMessages("Data ze souboru nelze načíst...");
            return;
        }

        // reads the Excel table sheet and check if it isn't empty
        sheet = workBook.getSheetAt(sheetNumber);
        rowCount = sheet.getPhysicalNumberOfRows();
        if (rowCount == 0) {
            displayMessages("Tabulka je prázdná...");
            closeWorkBook(workBook);
            return;
        }

        // reads the entire Excel table column and returns a list of primes, check if it isn't empty
        primes = dataManager.getListOfPrimes(sheet, columnNumber);
        if (primes.isEmpty()) {
            displayMessages("Tabulka neobsahovala celá čísla...");
            closeWorkBook(workBook);
            return;
        }

        // creates an output log
        try {
            fileManager.createLogFile(primes);
        } catch (IOException e) {
            displayMessages("Nastala chyba při vytváření logovacího souboru...");
        }
        System.out.println("Aplikace našla v *.xlsx souboru celkem: " + primes.size() +
                "x prvočíslo z celkového počtu položek: " + rowCount + "x\n");
        System.out.println("Aplikace úspěšně skončla. Výsledek je ve složce aplikace v souboru log.txt");
    }

    // region: Private methods

    static private void displayMessages(String message) {
        System.out.println(message);
        System.out.println("Aplikace ukončena");
    }

    static private void closeWorkBook(XSSFWorkbook workBook) {
        if (workBook != null) {
            try {
                workBook.close();
            } catch (Exception e) {
                System.out.println("Nepodařilo se ukončit workBook...");
            }
        }
    }

    //end region
}
