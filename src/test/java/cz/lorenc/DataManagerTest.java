package cz.lorenc;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataManagerTest {

    private DataManager dataManager;
    private int rowNumber = 0;
    static private XSSFWorkbook workbook;
    static private XSSFSheet sheet;
    static private XSSFRow row;
    static private XSSFCell cell;

    @BeforeAll
    static void setupBeforeAllTests() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("List1");
        row = sheet.createRow(0);
        cell = row.createCell(0);
    }

    @BeforeEach
    void setupBeforeEachTest() {
        dataManager = new DataManager();
    }

    /**
     * tests a method that verifies whether a given numbers is a prime number
     */
    @Test
    void testIsPrimeForPrimeNumbers() {
        assertTrue(dataManager.isPrime(2));
        assertTrue(dataManager.isPrime(3));
        assertTrue(dataManager.isPrime(5));
    }

    @Test
    void testIsPrimeForNonPrimeNumbers() {
        assertFalse(dataManager.isPrime(1));
        assertFalse(dataManager.isPrime(6));
        assertFalse(dataManager.isPrime(10));
    }

    @Test
    void testIsPrimeForOthersNumbers() {
        assertFalse(dataManager.isPrime(0));
        assertFalse(dataManager.isPrime(-1));
    }

    /**
     * tests the method for getting numbers from the Excel table
     * and converting them to Long values
     */
    @Test
    void testGetCellValue() {
        // an integer value is expected (Cell Type: NUMERIC)
        cell.setCellType(CellType.NUMERIC);
        cell.setCellValue(-7);
        assertEquals(dataManager.getCellValue(cell), -7);
        cell.setCellValue(Long.MAX_VALUE);
        assertEquals(dataManager.getCellValue(cell), Long.MAX_VALUE);
        cell.setCellValue(Long.MIN_VALUE);
        assertEquals(dataManager.getCellValue(cell), Long.MIN_VALUE);

        // a null value is expected (Cell Type: NUMERIC)
        cell.setCellValue(51.2);
        assertNull(dataManager.getCellValue(cell));

        // an integer value is expected (Cell Type: STRING)
        cell.setCellType(CellType.STRING);
        cell.setCellValue(Long.MAX_VALUE);
        assertEquals(dataManager.getCellValue(cell), Long.MAX_VALUE);
        cell.setCellValue(Long.MIN_VALUE);
        assertEquals(dataManager.getCellValue(cell), Long.MIN_VALUE);
        cell.setCellValue(-10);
        assertEquals(dataManager.getCellValue(cell), -10);
        cell.setCellValue("  64 ");
        assertEquals(dataManager.getCellValue(cell), 64);
        cell.setCellValue(" 128  ");
        assertEquals(dataManager.getCellValue(cell), 128);

        // a null value is expected (Cell Type: STRING)
        cell.setCellValue(1.5);
        assertNull(dataManager.getCellValue(cell));
        cell.setCellValue("data");
        assertNull(dataManager.getCellValue(cell));
        cell.setCellValue("163,84");
        assertNull(dataManager.getCellValue(cell));
        cell.setCellValue("25.6");
        assertNull(dataManager.getCellValue(cell));
        cell.setCellValue("");
        assertNull(dataManager.getCellValue(cell));
        cell.setCellValue(" ");
        assertNull(dataManager.getCellValue(cell));

        // an unexpected types...
        cell.setCellType(CellType.BLANK);
        assertNull(dataManager.getCellValue(cell));
        cell.setCellType(CellType.BOOLEAN);
        cell.setCellValue(true);
        assertNull(dataManager.getCellValue(cell));
    }

    /**
     * tests a range of different possible values
     * this method can return only List of prime numbers
     */
    @Test
    void testGetListOfPrimes() {
        ArrayList<String> testStrings = new ArrayList<>(
                Arrays.asList("Data", "", " ", "2,5", "5.2", " 7", "20 ", "11", "x")
        );
        ArrayList<Double> testNumbers = new ArrayList<>(Arrays.asList(-7.0, 0.0, 1.0, 2.0, 17.0, 13.3));
        List<Long> correctNumbers = new ArrayList<>(Arrays.asList(7L, 11L, 2L, 17L));

        CellType typeString = CellType.STRING;
        for (String testString : testStrings) {
            Cell cell = setCell(sheet, typeString, rowNumber);
            cell.setCellValue(testString);
            rowNumber++;
        }
        CellType typeNumeric = CellType.NUMERIC;
        for (Double testNumber : testNumbers) {
            Cell cell = setCell(sheet, typeNumeric, rowNumber);
            cell.setCellValue(testNumber);
            rowNumber++;
        }
        assertEquals(dataManager.getListOfPrimes(sheet, 1), correctNumbers);
    }

    // region: Private methods

    /**
     * presets one cell of the Excel table
     *
     * @param sheet     list of Excel table
     * @param type      cell type
     * @param rowNumber current row number
     * @return preset cell
     */
    private Cell setCell(XSSFSheet sheet, CellType type, int rowNumber) {
        row = sheet.createRow(rowNumber);
        cell = row.createCell(1);
        cell.setCellType(type);
        return cell;
    }
    // end region

    @AfterAll
    static void tearDown() {
        if (workbook != null) {
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}