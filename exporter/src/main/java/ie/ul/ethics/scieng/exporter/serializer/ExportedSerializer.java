package ie.ul.ethics.scieng.exporter.serializer;

import ie.ul.ethics.scieng.exporter.ExportedApplication;

import java.io.File;

/**
 * This interface represents an object that can serialize one or more extracted applications onto the file system
 */
public interface ExportedSerializer {
    /**
     * Save the exported application under the given name
     * @param exported the application to export
     * @param name the name to export the application under. If null, the application ID is used
     * @param directory a directory to store the folder under. If null, it's not stored under that directory, instead stored
     *                  in the root of the upload directory
     * @return the file representing the directory the application is stored in (or directory if specified)
     */
    File saveToDisk(ExportedApplication exported, String name, String directory);
}
