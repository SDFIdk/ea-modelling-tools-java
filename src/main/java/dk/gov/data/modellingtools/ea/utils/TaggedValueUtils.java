package dk.gov.data.modellingtools.ea.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.sparx.Attribute;
import org.sparx.AttributeTag;
import org.sparx.ConnectorEnd;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.RoleTag;
import org.sparx.TaggedValue;

/**
 * See also the documentation for the TaggedValue Class in the EA User Guide.
 * 
 * <p>Use the getTaggedValues methods instead of the getTaggedValueValue methods when you need to
 * retrieve multiple tagged values for the same model element.</p>
 * 
 */
public class TaggedValueUtils {

  /**
   * Generic method to retrieve the tagged values of an EA object.
   */
  public static Map<String, String> getTaggedValues(Object object) {
    if (object instanceof Element) {
      return getTaggedValues((Element) object);
    } else if (object instanceof Package) {
      return getTaggedValues((Package) object);
    } else if (object instanceof Attribute) {
      return getTaggedValues((Attribute) object);
    } else if (object instanceof ConnectorEnd) {
      return getTaggedValues((ConnectorEnd) object);
    } else { // TODO add more types
      throw new UnsupportedOperationException(
          "Cannot call this method for object of type " + object.getClass());
    }
  }

  /**
   * Gets the tagged values of the given package.
   */
  public static Map<String, String> getTaggedValues(Package umlPackage) {
    return getTaggedValues(umlPackage.GetElement());
  }

  /**
   * Gets the tagged values of the given element.
   */
  public static Map<String, String> getTaggedValues(Element element) {
    Map<String, String> taggedValues = new HashMap<>();
    for (TaggedValue taggedValue : element.GetTaggedValues()) {
      taggedValues.put(taggedValue.GetName(), TaggedValueUtils.getTextOfTaggedValue(taggedValue));
    }
    return taggedValues;
  }

  /**
   * Gets the tagged values of the given attribute.
   */
  public static Map<String, String> getTaggedValues(Attribute attribute) {
    Map<String, String> taggedValues = new HashMap<>();
    for (AttributeTag taggedValue : attribute.GetTaggedValues()) {
      taggedValues.put(taggedValue.GetName(), TaggedValueUtils.getTextOfTaggedValue(taggedValue));
    }
    return taggedValues;
  }

  /**
   * Gets the tagged values of the given connector end.
   */
  public static Map<String, String> getTaggedValues(ConnectorEnd connectorEnd) {
    Map<String, String> taggedValues = new HashMap<>();
    for (RoleTag taggedValue : connectorEnd.GetTaggedValues()) {
      taggedValues.put(taggedValue.GetTag(), TaggedValueUtils.getTextOfTaggedValue(taggedValue));
    }
    return taggedValues;
  }

  /**
   * Gets the value of the tagged value with the given name for the given EA object, or the empty
   * string if it does not exist (so never null).
   */
  public static String getTaggedValueValue(Object object, String taggedValueName) {
    if (object instanceof Element) {
      return getTaggedValueValue((Element) object, taggedValueName);
    } else if (object instanceof Package) {
      return getTaggedValueValue((Package) object, taggedValueName);
    } else if (object instanceof Attribute) {
      return getTaggedValueValue((Attribute) object, taggedValueName);
    } else if (object instanceof ConnectorEnd) {
      return getTaggedValueValue((ConnectorEnd) object, taggedValueName);
    } else { // TODO add more types
      throw new UnsupportedOperationException(
          "Cannot call this method for object of type " + object.getClass());
    }
  }

  /**
   * Gets the value of the tagged value with the given name for the given package, or the empty
   * string if it does not exist (so never null).
   */
  public static String getTaggedValueValue(Package umlPackage, String taggedValueName) {
    return getTaggedValueValue(umlPackage.GetElement(), taggedValueName);
  }

  /**
   * Gets the value of the tagged value with the given name for the given element, or the empty
   * string if it does not exist (so never null).
   */
  public static String getTaggedValueValue(Element element, String taggedValueName) {
    Validate.notNull(taggedValueName, "The name of the tag to retrieve must not be null");
    String taggedValueValue = "";
    for (TaggedValue taggedValue : element.GetTaggedValues()) {
      if (taggedValueName.equals(taggedValue.GetName())) {
        taggedValueValue = TaggedValueUtils.getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }

  /**
   * Gets the value of the tagged value with the given name for the given attribute, or the empty
   * string if it does not exist (so never null).
   */
  public static String getTaggedValueValue(Attribute attribute, String taggedValueName) {
    Validate.notNull(taggedValueName, "The name of the tag to retrieve must not be null");
    String taggedValueValue = "";
    for (AttributeTag taggedValue : attribute.GetTaggedValues()) {
      if (taggedValueName.equals(taggedValue.GetName())) {
        taggedValueValue = TaggedValueUtils.getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }

  /**
   * Gets the value of the tagged value with the given name for the given connector end, or the
   * empty string if it does not exist (so never null).
   */
  public static String getTaggedValueValue(ConnectorEnd connectorEnd, String taggedValueName) {
    Validate.notNull(taggedValueName, "The name of the tag to retrieve must not be null");
    String taggedValueValue = "";
    for (RoleTag taggedValue : connectorEnd.GetTaggedValues()) {
      if (taggedValueName.equals(taggedValue.GetTag())) {
        taggedValueValue = TaggedValueUtils.getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }

  /**
   * Gets the value of the tagged value with the given fully qualified name for the given element,
   * or the empty string if it does not exist (so never null).
   */
  public static String getTaggedValueValueFullyQualifiedName(Element element,
      String taggedValueFqName) {
    Validate.notNull(taggedValueFqName, "The name of the tag to retrieve must not be null");
    Validate.isTrue(taggedValueFqName.matches("([^:]*)::([^:]*)::([^:]*)"),
        taggedValueFqName + " must be a fully qualified name");
    String taggedValueValue = "";
    for (TaggedValue taggedValue : element.GetTaggedValues()) {
      if (taggedValueFqName.equals(taggedValue.GetFQName())) {
        taggedValueValue = TaggedValueUtils.getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }

  public static URI getTaggedValueValueAsUri(Object object, String taggedValueName)
      throws URISyntaxException {
    String taggedValueValue = TaggedValueUtils.getTaggedValueValue(object, taggedValueName);
    return new URI(taggedValueValue);
  }

  /**
   * Return the actual content of the tagged value.
   */
  private static String getTextOfTaggedValue(TaggedValue taggedValue) {
    String taggedValueValue;
    if ("<memo>".equals(taggedValue.GetValue())) {
      taggedValueValue = taggedValue.GetNotes();
    } else {
      taggedValueValue = taggedValue.GetValue();
    }
    return taggedValueValue;
  }

  /**
   * Return the actual content of the tagged value.
   */
  private static String getTextOfTaggedValue(AttributeTag taggedValue) {
    String taggedValueValue;
    if ("<memo>".equals(taggedValue.GetValue())) {
      taggedValueValue = taggedValue.GetNotes();
    } else {
      taggedValueValue = taggedValue.GetValue();
    }
    return taggedValueValue;
  }

  /**
   * Return the actual content of the tagged value.
   */
  private static String getTextOfTaggedValue(RoleTag taggedValue) {
    /*
     * Values are stored in t_taggedvalue.
     */
    final String taggedValueValue;
    String prefixMemoWithNotes = "<memo>$ea_notes=";
    String prefixMemoWithoutNotes = "<memo>";
    String notesSeparator = "$ea_notes=";
    String value = taggedValue.GetValue();
    if (StringUtils.startsWith(value, prefixMemoWithNotes)) {
      /*
       * the following is removed from the start of the value: <memo>$ea_notes=
       */
      taggedValueValue = value.substring(prefixMemoWithNotes.length());
    } else if (StringUtils.startsWith(value, prefixMemoWithoutNotes)) {
      taggedValueValue = value.substring(prefixMemoWithoutNotes.length());
    } else if (StringUtils.startsWith(value, notesSeparator)) {
      taggedValueValue = "";
    } else if (StringUtils.contains(value, notesSeparator)) {
      int index = StringUtils.indexOf(value, notesSeparator);
      taggedValueValue = StringUtils.substring(value, 0, index);
    } else {
      taggedValueValue = value;
    }
    return taggedValueValue;
  }

  /**
   * Finds the value of the given tag and splits the value of the given tag in parts according to
   * the given separator.
   */
  public static String[] retrieveAndSplitTaggedValueValue(Map<String, String> taggedValues,
      String tag, char splitCharacter) {
    String[] splittedValues;
    if (taggedValues.containsKey(tag)) {
      splittedValues =
          StringUtils.stripAll(StringUtils.split(taggedValues.get(tag), splitCharacter));
    } else {
      splittedValues = ArrayUtils.EMPTY_STRING_ARRAY;
    }
    return splittedValues;
  }

}
