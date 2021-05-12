package dk.gov.data.geo.datamodellingtools.ea.utils;

import java.util.Iterator;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.TaggedValue;

/**
 * See also the documentation for the TaggedValue Class in the EA User Guide.
 * 
 */
public class TaggedValueUtils {

  public static String getTaggedValueValue(Package umlPackage, String taggedValueName) {
    String taggedValueValue = "";
    Element element = umlPackage.GetElement();
    org.sparx.Collection<TaggedValue> taggedValues = element.GetTaggedValues();
    for (Iterator<TaggedValue> iterator = taggedValues.iterator(); iterator.hasNext();) {
      TaggedValue taggedValue = iterator.next();
      if (taggedValue.GetName().equals(taggedValueName)) {
        taggedValueValue = TaggedValueUtils.getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }

  public static String getTaggedValueValue(Element element, String taggedValueName) {
    String taggedValueValue = "";
    org.sparx.Collection<TaggedValue> taggedValues = element.GetTaggedValues();
    for (Iterator<TaggedValue> iterator = taggedValues.iterator(); iterator.hasNext();) {
      TaggedValue taggedValue = iterator.next();
      if (taggedValue.GetName().equals(taggedValueName)) {
        taggedValueValue = TaggedValueUtils.getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }

  /**
   * Return the actual content of the tagged value.
   */
  public static String getTextOfTaggedValue(TaggedValue taggedValue) {
    String taggedValueValue;
    if ("<memo>".equals(taggedValue.GetValue())) {
      taggedValueValue = taggedValue.GetNotes();
    } else {
      taggedValueValue = taggedValue.GetValue();
    }
    return taggedValueValue;
  }

}
