package cz.lorenc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private final Logger logFile;

    public DataManager() {
        this.logFile = LogManager.getLogger("FileLogger");
    }

    /**
     * method that reads the table from the disk
     *
     * @param file loaded from disk
     * @return workBook containing Excel table
     * @throws IOException if an error occurs during file reading or manipulation
     */
    public XSSFWorkbook readExcel(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        XSSFWorkbook workBook =  new XSSFWorkbook(fileInputStream);
        try {
            fileInputStream.close();
        } catch (IOException e) {
            logFile.warn("Nepodařilo se uzavřít fileInputStream...");
        }
        return workBook;
    }

    /**
     * this method return a List of primes from the Excel table column
     *
     * @param sheet        list of the Excel table
     * @param columnNumber number of the column which, we need to read
     * @return all primes, that the column contained
     */
    public List<Long> getListOfPrimes(XSSFSheet sheet, int columnNumber) {
        List<Long> numbers = new ArrayList<>();
        int countNullCells = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row;
            XSSFCell cell;
            try {
                row = sheet.getRow(i);
                cell = row.getCell(columnNumber);
            } catch (Exception e) {
                countNullCells++;
                continue;   // when the row or the cell contains a null silently continues the next iteration...
            }
            Long number = getCellValue(cell);
            if (number != null && isPrimeJavaMath(number))  // you can try this less efficient method: isPrime(number)
                numbers.add(number);
        }
        if (countNullCells > 0)
            logFile.warn("Sloupec tabulky obsahoval: " + countNullCells + "x nezadanou hodnotu.\n");
        return numbers;
    }

    // the following methods should be private, but I also wanted to test them...

    /**
     * method that verifies whether a given numbers is a prime number
     *
     * @param number an integer number
     * @return true for the prime number
     */
    public boolean isPrime(long number) {
        if (number == 2)
            return true;
        if (number < 2 || number % 2 == 0)
            return false;
        for (int i = 3; i <= Math.sqrt(number); i += 1) {
            if (number % i == 0)
                return false;
        }
        return true;
    }

    /**
     * Miller-Rabin's algorithm using a more efficient method of calculating a prime number
     *
     * @param number an integer number
     * @return true for the prime number
     */
    public boolean isPrimeJavaMath(Long number) {
        if (number < 2)
            return false;
        BigInteger numberCheck = BigInteger.valueOf(number);
        return numberCheck.isProbablePrime(10);     // certainty = rate of probability...
    }

    /**
     * converts the Excel sheet cell value to an integer number if possible
     *
     * @param cell one cell of the Excel sheet
     * @return an integer or null
     */
    public Long getCellValue(XSSFCell cell) {
        return switch (cell.getCellType()) {
            case NUMERIC -> {
                double numberD = cell.getNumericCellValue();
                if (numberD > Long.MAX_VALUE || numberD < Long.MIN_VALUE)
                    yield null;
                long numberL = (long) numberD;
                if (numberD != numberL)
                    yield null;                 // it was a decimal number...
                yield numberL;
            }
            case STRING -> {
                try {
                    yield Long.valueOf(cell.getStringCellValue().trim());   // it removes decimal numbers...
                } catch (Exception e) {
                    yield null;                 // text that can't be converted to a number...
                }
            }
            default -> null;
        };
    }
}
