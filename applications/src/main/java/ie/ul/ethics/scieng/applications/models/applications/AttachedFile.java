package ie.ul.ethics.scieng.applications.models.applications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

/**
 * This class represents a file that has been attached to an application. It consists of the filename and the directory the file
 * has been saved with so that the /api/files/download/filename?directory=dirname path can be built
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AttachedFile {
    /**
     * The ID of the attached file instance in the DB
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The name of the file
     */
    private String filename;
    /**
     * The directory the file is stored in
     */
    private String directory;
    /**
     * The username of the user that owns the file
     */
    private String username;

    /**
     * Copy the attached file instance
     * @return the copy
     */
    public AttachedFile copy() {
        return new AttachedFile(null, filename, directory, username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AttachedFile that = (AttachedFile) o;
        return Objects.equals(id, that.id) && Objects.equals(filename, that.filename) && Objects.equals(directory, that.directory)
                && Objects.equals(username, that.username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, filename, directory, username);
    }
}
