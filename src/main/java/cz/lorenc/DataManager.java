package cz.lorenc;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    public DataManager() {
    }

    /**
     * method that reads the table from the disk
     *
     * @param file loaded from disk
     * @return workBook containing Excel table
     * @throws IOException if an error occurs during file reading or manipulation
     */
    public XSSFWorkbook readExcel(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        return new XSSFWorkbook(fis);
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
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = sheet.getRow(i);
            XSSFCell cell = row.getCell(columnNumber);
            Long number = getCellValue(cell);
            if (number != null && isPrime(number))
                numbers.add(number);
        }
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
     * converts the Excel sheet cell value to an integer number if possible
     *
     * @param cell one cell of the Excel sheet
     * @return an integer or null
     */
    public Long getCellValue(XSSFCell cell) {
        switch (cell.getCellType()) {
            case NUMERIC -> {
                double numberD = cell.getNumericCellValue();
                long numberL = (long) numberD;
                if (numberD != numberL)
                    return null;                // it was a decimal number...
                return numberL;
            }
            case STRING -> {
                try {
                    return Long.valueOf(cell.getStringCellValue().trim()); // it removes decimal numbers...
                } catch (Exception e) {
                    return null;                // text that can't be converted to a number...
                }
            }
            default -> {
                return null;
            }
        }
    }
}
