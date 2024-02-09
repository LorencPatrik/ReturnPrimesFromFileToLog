package cz.lorenc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Main {

    private static final FileManager fileManager = new FileManager();
    private static final DataManager dataManager = new DataManager();
    private static final Logger logFile = LogManager.getLogger("FileLogger");

    public static void main(String[] args) {

        int sheetNumber = 0;    // sheet number 1 (List1)
        int columnNumber = 1;   // column 2 (B)

        logFile.info("Aplikace pro práci s xlsx souborem, zadaným jako parametr. \n");

        File xlsxFile = getXlsxFile(args);
        if (Objects.isNull(xlsxFile))
            return;     // unavailable file...

        XSSFWorkbook workBook = getWorkBook(xlsxFile);
        if (Objects.isNull(workBook))
            return;     // unavailable Excel data...

        List<Long> primes = processExcelData(workBook, sheetNumber, columnNumber);
        if (Objects.isNull(primes) || primes.isEmpty())
            return;     // empty Excel table or not containing numbers...

        if (!createOutputLog(primes, xlsxFile.getName()))
            return;     // an error occurred while creating a logger file...

        logFile.info("Aplikace úspěšně skončla.");
    }

    // region: Private methods

    /**
     * check if the address was entered as a parameter and loads the *.xlsx file if it was found
     *
     * @param args xlsx file address specified as a parameter when the application is started
     * @return xlsx file or null if not available
     */
    static private File getXlsxFile(String[] args) {
        if (args.length == 0) {
            sendLogToFile("Nezadaná adresa *.xlsx souboru jako parametr aplikace...");
            return null;
        }
        File xlsxFile = fileManager.getFile(args[0]);
        if (!xlsxFile.exists()) {
            sendLogToFile("Soubor na zadané adrese nenalezen: " + args[0]);
            return null;
        }
        return xlsxFile;
    }

    /**
     * loads the Excel table from *.xlsx file if possible
     *
     * @param xlsxFile file for processing
     * @return workBook with Excel table or null if it cannot be read
     */
    static private XSSFWorkbook getWorkBook(File xlsxFile) {
        XSSFWorkbook workBook = null;
        try {
            workBook = dataManager.readExcel(xlsxFile);
        } catch (Exception e) {
            sendLogToFile("Data ze souboru nelze načíst...");
        }
        return workBook;
    }

    /**
     * reads the Excel table sheet and check if it isn't empty
     * reads the entire Excel table column and returns a list of primes, check if it isn't empty
     *
     * @param workBook containing excel table
     * @param sheetNumber table sheet number
     * @param columnNumber sheet column number
     * @return list of prime numbers or null if empty
     */
    static private List<Long> processExcelData(
            XSSFWorkbook workBook, int sheetNumber, int columnNumber
    ) {

        XSSFSheet sheet = workBook.getSheetAt(sheetNumber);
        if (sheet.getPhysicalNumberOfRows() == 0) {
            sendLogToFile("Tabulka je prázdná...");
            closeWorkBook(workBook);
            return null;
        }
        List<Long> primes = dataManager.getListOfPrimes(sheet, columnNumber);
        if (primes.isEmpty()) {
            sendLogToFile("Tabulka neobsahovala celá čísla...");
            closeWorkBook(workBook);
        }
        return primes;
    }

    /**
     * creates an output log to file.txt and writes the result to the console
     *
     * @param primes list of prime numbers
     * @return true if no error occurred
     */
    static private boolean createOutputLog(List<Long> primes, String fileName) {
        try {
            fileManager.createLogFile(primes);
        } catch (IOException e) {
            sendLogToFile("Nastala chyba při vytváření logovacího souboru...");
            return false;
        }
        if (!primes.isEmpty())
            logFile.info("Aplikace našla v souboru: " + fileName + " " + primes.size() + "x prvočíslo.\n");
        return true;
    }

    static private void sendLogToFile(String message) {
        logFile.warn(message);
        logFile.warn("Aplikace předčasně ukončena!");
    }


    static private void closeWorkBook(XSSFWorkbook workBook) {
        if (workBook != null) {
            try {
                workBook.close();
            } catch (Exception e) {
                logFile.warn("Nepodařilo se ukončit workBook...");
            }
        }
    }

    //end region
}
