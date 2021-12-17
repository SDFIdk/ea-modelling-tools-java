package dk.gov.data.modellingtools.ea.utils;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.Validate;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.TaggedValue;

/**
 * See also the documentation for the TaggedValue Class in the EA User Guide.
 * 
 */
public class TaggedValueUtils {

  /**
   * @return value of the tagged value with the given name for the given package, or the empty
   *         string if it does not exist (so never null)
   */
  public static String getTaggedValueValue(Package umlPackage, String taggedValueName) {
    return getTaggedValueValue(umlPackage.GetElement(), taggedValueName);
  }

  /**
   * @return value of the tagged value with the given name for the given element, or the empty
   *         string if it does not exist (so never null)
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
   * @return value of the tagged value with the given fully qualified name for the given element, or
   *         the empty string if it does not exist (so never null)
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

  public static URI getTaggedValueValueAsUri(Element element, String taggedValueName)
      throws ModellingToolsException {
    String taggedValueValue = TaggedValueUtils.getTaggedValueValue(element, taggedValueName);
    URI uri;
    try {
      uri = new URI(taggedValueValue);
    } catch (URISyntaxException e) {
      throw new ModellingToolsException("Could not convert tagged value " + taggedValueName + " "
          + taggedValueValue + " on element " + element.GetName() + " to a valid URI", e);
    }
    return uri;
  }

  public static URI getTaggedValueValueFullyQualifiedNameAsUri(Element element,
      String taggedValueName) throws ModellingToolsException {
    String taggedValueValue =
        TaggedValueUtils.getTaggedValueValueFullyQualifiedName(element, taggedValueName);
    try {
      return new URI(taggedValueValue);
    } catch (URISyntaxException e) {
      throw new ModellingToolsException("Could not convert tagged value " + taggedValueName + " "
          + taggedValueValue + " on element " + element.GetName() + " to a valid URI", e);
    }
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

}
