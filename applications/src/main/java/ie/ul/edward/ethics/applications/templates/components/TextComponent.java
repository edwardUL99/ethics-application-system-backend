package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This component represents a simple text item
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class TextComponent extends SimpleComponent {
    /**
     * The text content to display
     */
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
        super(ComponentTypes.TEXT, title);
        this.content = content;
    }
}
