package cz.lorenc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileManager {
    private final Logger logger;

    public FileManager() {
        this.logger = LogManager.getLogger(FileManager.class);
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
     * writes a list of primes using the logger
     *
     * @param primes list of primes
     * @throws IOException if an error occurs during file reading or manipulation
     */
    public void createLogFile(List<Long> primes) throws IOException {
        for (Long prime : primes) {
            logger.info(prime);
        }
    }
}
