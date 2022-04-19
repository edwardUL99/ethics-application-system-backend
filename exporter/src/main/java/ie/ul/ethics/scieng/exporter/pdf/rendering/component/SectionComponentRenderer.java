package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.SectionComponent;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

import java.util.Map;
import java.util.Objects;

/**
 * Renders a section. The render options supports a parameter called subsection which takes a Section object which represents
 * a parent section. It is also expected to contain the chapter the section belongs to in the chapter property
 */
public class SectionComponentRenderer extends DefaultComponentRenderer {
    /**
     * Instantiate the renderer
     *
     * @param application the application to render
     * @param component   the component being rendered
     * @param context     for rendering
     */
    public SectionComponentRenderer(Application application, ApplicationComponent component, PDFContext context) {
        super(application, component, context);
    }

    /**
     * Renders the given section
     * @param chapter the chapter containing the sections
     * @param section the section to add child elements to
     * @param sectionComponent the section component being rendered
     */
    private void renderSection(Chapter chapter, Section section, SectionComponent sectionComponent) {
        String description = sectionComponent.getDescription();

        if (description != null)
            section.add(new Chunk(description, FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.LIGHT_GRAY)));

        for (ApplicationComponent sub : ((SectionComponent) component).getComponents()) {
            ComponentType componentType = sub.getType();

            if (componentType == ComponentType.SECTION) {
                new SectionComponentRenderer(application, sub, context).renderToElement(Map.of(
                        "chapter", chapter,
                        "parent", section
                )); // the render adds the section to the chapter/parent automatically
            } else {
                ComponentRenderer renderer = ComponentRenderers.getRenderer(application, context, sub);
                boolean add = renderer.addReturnedElements();

                Element element = renderer.renderToElement(Map.of("chapyer", chapter));

                if (add) {
                    section.add(element);
                    section.add(Chunk.NEWLINE);
                }
            }
        }
    }

    /**
     * Render the component into a PDF element
     *
     * @param renderOptions a map of key/value render options. The supported options depend on the implementation
     * @return the rendered element
     */
    @Override
    public Element renderToElement(Map<String, Object> renderOptions) {
        if (renderOptions == null) {
            throw new IllegalArgumentException("The renderOptions map is expected by the SectionComponentRenderer");
        } else {
            Chapter chapter = (Chapter) renderOptions.get("chapter");

            if (chapter == null) {
                throw new IllegalArgumentException("You must provide a Chapter object in the renderOptions map");
            } else {
                Section parent = (Section) renderOptions.get("parent");
                Paragraph title = new Paragraph();
                title.add(new Chunk(component.getTitle(), FontFactory.getFont(FontFactory.COURIER_BOLD, 18, BaseColor.BLACK)));
                Section section = Objects.requireNonNullElse(parent, chapter).addSection(title);
                SectionComponent sectionComponent = (SectionComponent) component;
                renderSection(chapter, section, sectionComponent);

                return section;
            }
        }
    }

    /**
     * Determines if elements returned from {@link #renderToElement(Map)} should be added or if they are automatically added
     *
     * @return true to add, false to not add
     */
    @Override
    public boolean addReturnedElements() {
        return false;
    }
}
