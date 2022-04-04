package ie.ul.ethics.scieng.common.zip;

import java.io.File;
import java.io.IOException;

/**
 * This interface marks an object as being able to be zipped into a zip-file or an object that can be added to a zip
 * file.
 *
 * The API should be interacted with through the {@link Zip} utility
 */
public interface Zippable {
    /**
     * Determines if this object is a zip file.
     * @return true if a zip file, false if not
     */
    boolean isZipFile();

    /**
     * Add a zippable file to this object. Only makes sense if {@link #isZipFile()} returns true. Add may also fail
     * even if {@link #isZipFile()} returns true as a ZIPFile can also be set to explicitly set an existing file and not
     * to be created on the fly
     * @param zippable the zippable object to add
     * @return true if successful, false if not
     */
    boolean add(Zippable zippable);

    /**
     * Get the name of the zippable object
     * @return the object's name
     */
    String getName();

    /**
     * Get the file object representing the Zippable object on the file system
     * @return filesystem file object
     */
    File getFile();

    /**
     * Zip the zippable object into a zip file. No-op if {@link #isZipFile()} returns false
     * @throws IOException if the zip process fails
     */
    void zip() throws IOException;
}
