package hu.akusius.palenque.layout.data.export;

import hu.akusius.palenque.layout.data.Item;
import hu.akusius.palenque.layout.data.Layout;
import java.io.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A sablon alapú {@link Exporter}-ek ősosztálya.
 * @author Bujdosó Ákos
 */
public abstract class TemplatedExporter implements Exporter {

  public static final String itemsStart = "^^";

  public static final String itemsEnd = "$$";

  private final String format;

  private final String extension;

  private final Map<Section, List<String>> sectionLines;

  public TemplatedExporter(String format) {
    if (format == null || format.length() == 0) {
      throw new IllegalArgumentException();
    }
    this.format = format.toUpperCase();
    this.extension = this.format.toLowerCase();

    sectionLines = new EnumMap<>(Section.class);
    for (Section section : Section.values()) {
      sectionLines.put(section, new ArrayList<String>(50));
    }

    Section section = Section.Header;

    try (InputStream is = TemplatedExporter.class.getResourceAsStream("template." + extension);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {

      String line;
      while ((line = reader.readLine()) != null) {
        if (line.contains(itemsStart)) {
          // Items rész kezdődik
          if (section != Section.Header) {
            throw new TemplateException(this);
          }

          section = Section.Items;

          if (line.contains(itemsEnd)) {
            // Egysoros Items rész
            if (!line.startsWith(itemsStart) || !line.endsWith(itemsEnd)) {
              throw new TemplateException(this);
            }
            sectionLines.get(section).add(line.substring(itemsStart.length(), line.length() - itemsEnd.length()));
            section = Section.Footer;
          } else {
            // Többsoros Items rész -> figyelmen kívül hagyjuk a sort
          }
        } else if (line.contains(itemsEnd)) {
          // Többsoros Items rész vége
          if (section != Section.Items) {
            throw new TemplateException(this);
          }
          section = Section.Footer;
        } else {
          // Sima sor
          sectionLines.get(section).add(line);
        }
      }
    } catch (IOException ex) {
      throw new TemplateException(this, ex);
    }
  }

  @Override
  public final String getFormat() {
    return format;
  }

  @Override
  public final String getFileExtension() {
    return extension;
  }

  @Override
  public final String export(Layout layout) {
    StringWriter sw = new StringWriter(10000);

    try (PrintWriter pw = new PrintWriter(sw)) {
      processLines(pw, sectionLines.get(Section.Header), null, layout);
      for (Item item : layout.getItems()) {
        processLines(pw, sectionLines.get(Section.Items), item, layout);
      }
      processLines(pw, sectionLines.get(Section.Footer), null, layout);
    }

    return sw.toString();
  }

  private static final Pattern variable = Pattern.compile("\\$([a-zA-Z]+)");

  private void processLines(PrintWriter pw, Iterable<String> lines, Item item, Layout layout) {
    for (String line : lines) {
      String processedLine = processLine(line, item, layout);
      if (processedLine != null) {
        pw.println(processedLine);
      }
    }
  }

  private String processLine(String line, Item item, Layout layout) {
    StringBuffer sb = new StringBuffer(line.length());

    Matcher m = variable.matcher(line);
    while (m.find()) {
      m.appendReplacement(sb, processVariable(m.group(1), item, layout));
    }
    m.appendTail(sb);

    return sb.toString();
  }

  private String processVariable(String var, Item item, Layout layout) {
    Object value = item == null ? getVariableValue(var, layout) : getItemVariableValue(var, item, layout);
    return value != null ? formatValue(value, var, item, layout) : "";
  }

  protected Object getVariableValue(String var, Layout layout) {
    switch (var.toLowerCase()) {
      case "layoutserialized":
        return layout.serialize();
      default:
        return null;
    }
  }

  protected Object getItemVariableValue(String var, Item item, Layout layout) {
    switch (var.toLowerCase()) {
      case "num":
        return layout.getIndex(item) + 1;
      case "type":
        return item.getType().getShortName();
      case "typedesc":
        switch (item.getType()) {
          case Triplet:
            return "Triplet";
          case Sun:
            return "Sun";
          case Star:
            return "Star";
          default:
            throw new AssertionError();
        }
      case "x":
        return item.getCenter().getX();
      case "y":
        return item.getCenter().getY();
      default:
        return null;
    }
  }

  protected String formatValue(Object value, String var, Item item, Layout layout) {
    return value.toString();
  }

  @SuppressWarnings("PublicInnerClass")
  public static class TemplateException extends RuntimeException {

    public TemplateException(TemplatedExporter exporter) {
      this(exporter, null);
    }

    public TemplateException(TemplatedExporter exporter, Throwable cause) {
      super("Wrong or missing template: " + exporter.format, cause);
    }
  }

  private enum Section {

    Header,
    Items,
    Footer
  }
}
