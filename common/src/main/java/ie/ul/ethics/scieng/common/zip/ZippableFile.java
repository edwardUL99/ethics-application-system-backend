package ie.ul.ethics.scieng.common.zip;

import java.io.File;

/**
 * This class represents a file that can be zipped by being added to a {@link ZIPFile}. They can't be inherently
 * zipped themselves, so the zip method is a no-op
 */
public class ZippableFile implements Zippable {
    /**
     * The file to add to the zip
     */
    private final File file;

    /**
     * Create the ZippableFile instance
     * @param file the zippable file instance
     */
    public ZippableFile(File file) {
        this.file = file;
    }

    /**
     * Create a ZippableFile with the given file path
     * @param file the file to zip
     */
    public ZippableFile(String file) {
        this(new File(file));
    }

    /**
     * Determines if this object is a zip file.
     *
     * @return true if a zip file, false if not
     */
    @Override
    public boolean isZipFile() {
        return false;
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
        return false;
    }

    /**
     * Get the name of the zippable object
     *
     * @return the object's name
     */
    @Override
    public String getName() {
        return file.getName();
    }

    /**
     * Get the file object representing the Zippable object on the file system
     *
     * @return filesystem file object
     */
    @Override
    public File getFile() {
        return file;
    }

    /**
     * No-op
     */
    @Override
    public void zip() {
        // no-op in ZippableFile
    }
}
