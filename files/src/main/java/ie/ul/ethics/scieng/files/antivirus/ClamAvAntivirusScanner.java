package ie.ul.ethics.scieng.files.antivirus;

import lombok.Getter;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.ClamavException;
import xyz.capybara.clamav.Platform;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.InputStream;

/**
 * This class provides antivirus scanning
 */
public class ClamAvAntivirusScanner implements AntivirusScanner {
    /**
     * Determines if the scanner should be enabled/disabled. If disabled, it will always return true for fileSafe
     */
    private final boolean enabled;
    /**
     * The ClamAV client
     */
    @Getter
    private final ClamavClient client;

    /**
     * Construct an Antivirus scanner with the provided parameters
     * @param enabled true if it should be enabled or disabled
     * @param host the hostname of the clamAV daemon
     * @param port the port of the daemon
     * @param platform the platform the daemon is running on
     */
    public ClamAvAntivirusScanner(boolean enabled, String host, int port, String platform) {
        this.enabled = enabled;
        this.client = (enabled) ? new ClamavClient(host, port, Platform.valueOf(platform)):null;
    }

    /**
     * Determines whether the scanner is enabled/disabled
     *
     * @return true if enabled, false if disabled. If returns
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Determines if the file represented by the input stream is safe (virus-free).
     * If {@link #isEnabled()} returns false, this should return true
     * @param inputStream the stream representing the file
     * @return true if the file is virus-free, false if it contains viruses
     * @throws AntivirusException if an error occurs that prevents scanning for viruses
     */
    public boolean isFileSafe(InputStream inputStream) throws AntivirusException {
        if (enabled) {
            try {
                ScanResult result = client.scan(inputStream);

                return result instanceof ScanResult.OK;
            } catch (ClamavException ex) {
                throw new AntivirusException("An error occurred while scanning for viruses", ex);
            }
        } else {
            return true;
        }
    }
}
