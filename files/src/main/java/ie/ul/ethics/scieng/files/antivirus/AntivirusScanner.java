package ie.ul.ethics.scieng.files.antivirus;

import java.io.InputStream;

/**
 * This interface represents an interface for providing antivirus scanning that can be enabled or disabled based on certain configuration.
 * It is a simple implementation where it only scans for the presence or no presence of viruses, not the viruses found
 * within the file
 */
public interface AntivirusScanner {
    /**
     * Determines whether the scanner is enabled/disabled
     * @return true if enabled, false if disabled. If returns
     */
    boolean isEnabled();

    /**
     * Determines if the file represented by the input stream is safe (virus-free).
     * If {@link #isEnabled()} returns false, this should return true
     * @param inputStream the stream representing the file
     * @return true if the file is virus-free, false if it contains viruses
     * @throws AntivirusException if an error occurs that prevents scanning for viruses
     */
    boolean isFileSafe(InputStream inputStream) throws AntivirusException;
}
