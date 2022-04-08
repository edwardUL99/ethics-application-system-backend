package ie.ul.ethics.scieng.common.zip;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A utility class for working with zip files through the Zippable API
 */
public final class Zip {
    /**
     * Zip the existing file into a ZIP
     * @param file the file to ZIP
     * @return the file object representing the zipped file
     * @throws IOException if an error occurs
     */
    public static File zipFile(File file) throws IOException {
        ZIPFile zip = new ZIPFile(file);
        zip.zip();

        return zip.getFile();
    }

    /**
     * Zip the given file into a ZIP with the given name
     * @param name the name of the ZIP
     * @param file the file. Can be directory/file
     * @return the file object representing the zipped file
     */
    public static File zipFile(String name, File file) throws IOException {
        ZIPFile zip = new ZIPFile(name, file);
        zip.zip();

        return zip.getFile();
    }

    /**
     * Zips the array of files into a ZIP with the given name. The zippables can be created using either {@link #createZippable(String, File, boolean)}
     * or {@link #createZippable(File)}
     * @param name the name of the ZIP file
     * @param files the array of zippables to zip
     * @return the file object representing the zipped file
     * @throws IOException if an error occurs
     */
    public static File zipFiles(String name, Zippable...files) throws IOException {
        return zipFiles(name, List.of(files));
    }

    /**
     * Zips the list of files into a ZIP with the given name. The zippables can be created using either {@link #createZippable(String, File, boolean)}
     * or {@link #createZippable(File)}
     * @param name the name of the ZIP file
     * @param files the list of zippables to zip
     * @return the file object representing the zipped file
     * @throws IOException if an error occurs
     */
    public static File zipFiles(String name, List<Zippable> files) throws IOException {
        ZIPFile zip = new ZIPFile(name);
        files.forEach(zip::add);
        zip.zip();

        return zip.getFile();
    }

    /**
     * A factory method to create a Zippable object
     * @param name the name of the object, this is optional if the file is not a nested zip
     * @param file the file to create the object from
     * @param zip true to zip the given file. If true, name will be used, else, it will be ignored
     * @return the zippable object
     */
    public static Zippable createZippable(String name, File file, boolean zip) {
        if (zip) {
            return new ZIPFile(name, file);
        } else {
            return new ZippableFile(file);
        }
    }

    /**
     * Create a file that can be added to a zip
     * @param file the file to convert to Zippable
     * @return the Zippable object
     */
    public static Zippable createZippable(File file) {
        return createZippable(null, file, false);
    }
}
