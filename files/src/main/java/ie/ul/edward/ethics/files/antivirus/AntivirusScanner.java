package ie.ul.edward.ethics.files.antivirus;

import lombok.Getter;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.Platform;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.InputStream;

/**
 * This class provides antivirus scanning
 */
@Getter
public class AntivirusScanner {
    /**
     * Determines if the scanner should be enabled/disabled. If disabled, it will always return true for fileSafe
     */
    private final boolean enabled;
    /**
     * The ClamAV client
     */
    private final ClamavClient client;

    /**
     * Construct an Antivirus scanner with the provided parameters
     * @param enabled true if it should be enabled or disabled
     * @param host the hostname of the clamAV daemon
     * @param port the port of the daemon
     * @param platform the platform the daemon is running on
     */
    public AntivirusScanner(boolean enabled, String host, int port, String platform) {
        this.enabled = enabled;
        this.client = (enabled) ? new ClamavClient(host, port, Platform.valueOf(platform)):null;
    }

    /**
     * Check if the provided inputstream is safe.
     * @param inputStream the inputstream to scan
     * @return true if virus free, false if not
     */
    public boolean fileSafe(InputStream inputStream) {
        if (enabled) {
            ScanResult result = client.scan(inputStream);

            return result instanceof ScanResult.OK;
        } else {
            return true;
        }
    }
}
