package ie.ul.ethics.scieng.common.zip;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a file that will result in a ZIPFile of other files
 */
public class ZIPFile implements Zippable {
    /**
     * The file to turn into a zip
     */
    private final File fileToZip;
    /**
     * The name of the zip
     */
    private final String name;
    /**
     * The list of zippable objects to zip
     */
    private final List<Zippable> toZip;

    /**
     * Create a ZIPFile explicitly setting the file to be zipped
     * @param name the name of the zip file
     * @param fileToZip the explicit file to zip
     */
    public ZIPFile(String name, File fileToZip) {
        this.name = name;
        this.fileToZip = fileToZip;
        this.toZip = null;
    }

    /**
     * Create a ZIPFile explicitly zipping the provided fileToZip instance
     * @param fileToZip the fileToZip instance
     */
    public ZIPFile(File fileToZip) {
        this(fileToZip.getName() + ".zip", fileToZip);
    }

    /**
     * Create a ZIPFile that will zip files through files added by {@link #add(Zippable)}
     * @param name the name of the zip file
     */
    public ZIPFile(String name) {
        this.fileToZip = null;
        this.name = name;
        this.toZip = new ArrayList<>();
    }

    /**
     * Determines if this object is a zip file.
     *
     * @return true if a zip file, false if not
     */
    @Override
    public boolean isZipFile() {
        return true;
    }

    /**
     * Add a zippable file to this object. Only makes sense if {@link #isZipFile()} returns true. Add may also fail
     * even if {@link #isZipFile()} returns true as a ZIPFile can also be set to explicitly set an existing file and not
     * to be created on the fly
     *
     * @param zippable the zippable object to add
     * @return true if successful, false if not
     */
    @Override
    public boolean add(Zippable zippable) {
        if (this.fileToZip == null) {
            this.toZip.add(zippable);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the name of the zippable object
     *
     * @return the object's name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the file object representing the Zippable object on the file system
     *
     * @return filesystem file object
     */
    @Override
    public File getFile() {
        return new File(name);
    }

    /**
     * Add the file to the zip file
     * @param zipFile the file to add the ZIP to
     * @param file the file to add to the ZipFile
     * @throws ZipException if an error occurs
     */
    private void addFile(ZipFile zipFile, File file) throws ZipException {
        if (file.isDirectory()) {
            zipFile.addFolder(file);
        } else {
            zipFile.addFile(file);
        }
    }

    /**
     * Zip the file into the zip4j zip file
     * @param zipFile the zip to add zip files to
     * @param toZip the list of files to zip
     */
    private void _zip(ZipFile zipFile, List<Zippable> toZip) throws ZipException {
        for (Zippable zippable : toZip) {
            if (zippable.isZipFile()) {
                ZIPFile child = (ZIPFile) zippable;
                child.doZip();

                if (child.fileToZip != null || !(child.toZip == null || child.toZip.size() > 0)) {
                    addFile(zipFile, child.getFile());
                }
            } else {
                addFile(zipFile, zippable.getFile());
            }
        }
    }

    /**
     * Create the zip4j zip file
     * @return the Zip4j zip file
     */
    private ZipFile createZipFile() {
        return new ZipFile(name);
    }

    /**
     * Initiate the zipping process
     */
    private void doZip() throws ZipException {
        ZipFile zipFile = createZipFile();

        if (this.fileToZip != null) {
            addFile(zipFile, this.fileToZip);
        } else {
            this._zip(zipFile, this.toZip);
        }
    }

    /**
     * Zip the zippable object into a zip file. No-op if {@link #isZipFile()} returns false
     *
     * @throws IOException if the zip process fails
     */
    @Override
    public void zip() throws IOException {
        doZip();
    }
}
