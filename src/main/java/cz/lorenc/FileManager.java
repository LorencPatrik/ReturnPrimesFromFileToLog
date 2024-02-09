package cz.lorenc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileManager {

    private final Logger logConsole;
    private final Logger logFile;

    public FileManager() {
        this.logFile = LogManager.getLogger("FileLogger");
        this.logConsole = LogManager.getLogger("ConsoleLogger");
    }

    /**
     * method that attempts to read the file from disk
     *
     * @param filePath address of the file on the disk
     * @return loaded file
     */
    public File getFile(String filePath) {
        return new File(filePath);
    }

    /**
     * writes a list of primes to the console and to the file using the logger
     *
     * @param primes list of primes
     * @throws IOException if an error occurs during file reading or manipulation
     */
    public void createLogFile(List<Long> primes) throws IOException {
        for (int i = 0; i < primes.size(); i++) {
            logConsole.info(primes.get(i));
            logFile.info(i + 1 + ": " + primes.get(i) + ((i == primes.size() - 1) ? "\n" : ""));
        }   // adds a blank line under the last number in the log file
    }
}
