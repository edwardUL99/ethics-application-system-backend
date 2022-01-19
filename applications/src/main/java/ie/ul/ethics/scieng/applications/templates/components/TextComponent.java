package ie.ul.ethics.scieng.applications.templates.components;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Lob;
import java.util.Objects;

/**
 * This component represents a simple text item
 */
@Getter
@Setter
@Entity
public class TextComponent extends SimpleComponent {
    /**
     * The text content to display
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    /**
     * Create a default TextComponent object
     */
    public TextComponent() {
        this(null, null);
    }

    /**
     * Create a TextComponent object
     * @param title the title of the text component
     * @param content the text content to display
     */
    public TextComponent(String title, String content) {
        super(ComponentType.TEXT, title);
        this.content = content;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TextComponent that = (TextComponent) o;
        return databaseId != null && Objects.equals(databaseId, that.databaseId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
