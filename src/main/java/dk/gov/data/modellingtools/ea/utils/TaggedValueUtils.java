package dk.gov.data.modellingtools.ea.utils;

import dk.gov.data.modellingtools.exception.ModellingToolsException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Attribute;
import org.sparx.AttributeTag;
import org.sparx.Collection;
import org.sparx.Connector;
import org.sparx.ConnectorEnd;
import org.sparx.ConnectorTag;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(TaggedValueUtils.class);

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
    } else if (object instanceof Connector) {
      return getTaggedValues((Connector) object);
    } else { // TODO add more types
      throw new UnsupportedOperationException(
          "Cannot call this method for object of type " + object.getClass());
    }
  }

  /**
   * Gets the tagged values of the given package. A tagged value's name is fully qualified if the
   * tag is connected to a profile and is unqualified if the tag is not connected to a profile.
   */
  public static Map<String, String> getTaggedValues(Package umlPackage) {
    return getTaggedValues(umlPackage.GetElement());
  }

  /**
   * Gets the tagged values of the given element. A tagged value's name is fully qualified if the
   * tag is connected to a profile and is unqualified if the tag is not connected to a profile.
   */
  public static Map<String, String> getTaggedValues(Element element) {
    Map<String, String> taggedValues = new HashMap<>();
    for (TaggedValue taggedValue : element.GetTaggedValues()) {
      taggedValues.put(getNameOfTaggedValue(taggedValue), getTextOfTaggedValue(taggedValue));
    }
    return taggedValues;
  }

  /**
   * Gets the tagged values of the given attribute. A tagged value's name is fully qualified if the
   * tag is connected to a profile and is unqualified if the tag is not connected to a profile.
   */
  public static Map<String, String> getTaggedValues(Attribute attribute) {
    Map<String, String> taggedValues = new HashMap<>();
    for (AttributeTag taggedValue : attribute.GetTaggedValues()) {
      taggedValues.put(getNameOfTaggedValue(taggedValue), getTextOfTaggedValue(taggedValue));
    }
    return taggedValues;
  }

  /**
   * Gets the tagged values of the given connector end. A tagged value's name is fully qualified if
   * the tag is connected to a profile and is unqualified if the tag is not connected to a profile.
   */
  public static Map<String, String> getTaggedValues(ConnectorEnd connectorEnd) {
    Map<String, String> taggedValues = new HashMap<>();
    for (RoleTag taggedValue : connectorEnd.GetTaggedValues()) {
      taggedValues.put(getNameOfTaggedValue(taggedValue), getTextOfTaggedValue(taggedValue));
    }
    return taggedValues;
  }

  /**
   * Gets the tagged values of the given connector. A tagged value's name is fully qualified if the
   * tag is connected to a profile and is unqualified if the tag is not connected to a profile.
   */
  public static Map<String, String> getTaggedValues(Connector connector) {
    Map<String, String> taggedValues = new HashMap<>();
    for (ConnectorTag taggedValue : connector.GetTaggedValues()) {
      taggedValues.put(getNameOfTaggedValue(taggedValue), getTextOfTaggedValue(taggedValue));
    }
    return taggedValues;
  }

  /**
   * Sets tagged values of the given element as specified.
   */
  public static void setTaggedValues(Element element, Map<String, String> taggedValuesToSet,
      boolean createIfNotPresentAndDeleteIfEmptyString) {
    Objects.requireNonNull(element);
    Objects.requireNonNull(taggedValuesToSet);
    for (Map.Entry<String, String> entry : taggedValuesToSet.entrySet()) {
      String taggedValueName = entry.getKey();
      String taggedValueValue = entry.getValue();
      TaggedValue taggedValueToUpdate =
          IterableUtils.find(element.GetTaggedValues(), new Predicate<TaggedValue>() {

            @Override
            public boolean evaluate(TaggedValue taggedValue) {
              return getNameOfTaggedValue(taggedValue).equals(taggedValueName);
            }
          });
      if (taggedValueToUpdate == null) {
        if (createIfNotPresentAndDeleteIfEmptyString) {
          addTaggedValueToElement(element, taggedValueName, taggedValueValue);
        } else {
          LOGGER.warn("No tag found with name {} on {}", taggedValueName,
              EaModelUtils.toString(element));
        }
      } else {
        if (createIfNotPresentAndDeleteIfEmptyString && StringUtils.isEmpty(taggedValueValue)) {
          deleteTaggedValueElement(element, taggedValueName);
        } else {
          if ("<memo>".equals(taggedValueToUpdate.GetValue())) {
            taggedValueToUpdate.SetNotes(taggedValueValue);
          } else {
            taggedValueToUpdate.SetValue(taggedValueValue);
          }
          taggedValueToUpdate.Update();
        }
      }
    }
  }

  /**
   * Sets tagged values of the given attribute as specified in the map.
   */
  public static void setTaggedValues(Attribute attribute, Map<String, String> taggedValuesToSet,
      boolean createIfNotPresentAndDeleteIfEmptyString) {
    Objects.requireNonNull(attribute);
    Objects.requireNonNull(taggedValuesToSet);
    for (Map.Entry<String, String> entry : taggedValuesToSet.entrySet()) {
      String taggedValueName = entry.getKey();
      String taggedValueValue = entry.getValue();
      AttributeTag taggedValueToUpdate =
          IterableUtils.find(attribute.GetTaggedValues(), new Predicate<AttributeTag>() {

            @Override
            public boolean evaluate(AttributeTag taggedValue) {
              return getNameOfTaggedValue(taggedValue).equals(taggedValueName);
            }
          });
      if (taggedValueToUpdate == null) {
        if (createIfNotPresentAndDeleteIfEmptyString) {
          addTaggedValueToAttribute(attribute, taggedValueName, taggedValueValue);
        } else {
          LOGGER.warn("No tag found with name {} on {}", taggedValueName,
              EaModelUtils.toString(attribute));
        }
      } else {
        if (createIfNotPresentAndDeleteIfEmptyString && StringUtils.isEmpty(taggedValueValue)) {
          deleteTaggedValueAttribute(attribute, taggedValueName);
        } else {
          if ("<memo>".equals(taggedValueToUpdate.GetValue())) {
            taggedValueToUpdate.SetNotes(taggedValueValue);
          } else {
            taggedValueToUpdate.SetValue(taggedValueValue);
          }
          taggedValueToUpdate.Update();
        }
      }
    }
  }


  /**
   * Sets tagged values of the given connector end as specified in the map.
   */
  public static void setTaggedValues(ConnectorEnd connectorEnd,
      Map<String, String> taggedValuesToSet, boolean createIfNotPresentAndDeleteIfEmptyString) {
    Objects.requireNonNull(connectorEnd);
    Objects.requireNonNull(taggedValuesToSet);
    for (Map.Entry<String, String> entry : taggedValuesToSet.entrySet()) {
      String taggedValueName = entry.getKey();
      String taggedValueValue = entry.getValue();
      RoleTag taggedValueToUpdate =
          IterableUtils.find(connectorEnd.GetTaggedValues(), new Predicate<RoleTag>() {

            @Override
            public boolean evaluate(RoleTag taggedValue) {
              return getNameOfTaggedValue(taggedValue).equals(taggedValueName);
            }
          });
      if (taggedValueToUpdate == null) {
        if (createIfNotPresentAndDeleteIfEmptyString) {
          addTaggedValueToConnectorEnd(connectorEnd, taggedValueName, taggedValueValue);
        } else {
          LOGGER.warn("No tag found with name {} on {}", taggedValueName,
              EaModelUtils.toString(connectorEnd));
        }
      } else {
        if (createIfNotPresentAndDeleteIfEmptyString && StringUtils.isEmpty(taggedValueValue)) {
          deleteTaggedValueConnectorEnd(connectorEnd, taggedValueName);
        } else {
          if (taggedValueToUpdate.GetValue().length() >= 6
              && "<memo>".equals(taggedValueToUpdate.GetValue().substring(0, 6))) {
            taggedValueToUpdate.SetValue("<memo>$ea_notes=" + taggedValueValue);
          } else {
            taggedValueToUpdate.SetValue(taggedValueValue);
          }
          taggedValueToUpdate.Update();
        }
      }
    }
  }

  /**
   * Sets tagged values of the given connector as specified in the map.
   */
  public static void setTaggedValues(Connector connector, Map<String, String> taggedValuesToSet,
      boolean createIfNotPresentAndDeleteIfEmptyString) {
    Objects.requireNonNull(connector);
    Objects.requireNonNull(taggedValuesToSet);
    for (Map.Entry<String, String> entry : taggedValuesToSet.entrySet()) {
      String taggedValueName = entry.getKey();
      String taggedValueValue = entry.getValue();
      ConnectorTag taggedValueToUpdate =
          IterableUtils.find(connector.GetTaggedValues(), new Predicate<ConnectorTag>() {

            @Override
            public boolean evaluate(ConnectorTag taggedValue) {
              return getNameOfTaggedValue(taggedValue).equals(taggedValueName);
            }
          });
      if (taggedValueToUpdate == null) {
        if (createIfNotPresentAndDeleteIfEmptyString) {
          addTaggedValueToConnector(connector, taggedValueName, taggedValueValue);
        } else {
          LOGGER.warn("No tag found with name {} on {}", taggedValueName,
              EaModelUtils.toString(connector));
        }
      } else {
        if (createIfNotPresentAndDeleteIfEmptyString && StringUtils.isEmpty(taggedValueValue)) {
          deleteTaggedValueConnector(connector, taggedValueName);
        } else {
          if ("<memo>".equals(taggedValueToUpdate.GetValue())) {
            taggedValueToUpdate.SetNotes(taggedValueValue);
          } else {
            taggedValueToUpdate.SetValue(taggedValueValue);
          }
          taggedValueToUpdate.Update();
        }
      }
    }
  }

  /**
   * Sets tagged values of the given object as specified in the map.
   */
  public static void setTaggedValues(Object object, Map<String, String> taggedValuesToSet,
      boolean createIfNotPresentAndDeleteIfEmptyString) {
    if (object instanceof Element) {
      setTaggedValues((Element) object, taggedValuesToSet,
          createIfNotPresentAndDeleteIfEmptyString);
    } else if (object instanceof Attribute) {
      setTaggedValues((Attribute) object, taggedValuesToSet,
          createIfNotPresentAndDeleteIfEmptyString);
    } else if (object instanceof ConnectorEnd) {
      setTaggedValues((ConnectorEnd) object, taggedValuesToSet,
          createIfNotPresentAndDeleteIfEmptyString);
    } else if (object instanceof Connector) {
      setTaggedValues((Connector) object, taggedValuesToSet,
          createIfNotPresentAndDeleteIfEmptyString);
    } else { // TODO add more types
      throw new UnsupportedOperationException(
          "Cannot call this method for object of type " + object.getClass());
    }
  }

  /**
   * Adds a new tagged value to the element. The new tag is not a memo tag.
   */
  public static void addTaggedValueToElement(Element element, String taggedValueName,
      String taggedValueValue) {
    Objects.requireNonNull(element);
    Objects.requireNonNull(taggedValueName);
    Objects.requireNonNull(taggedValueValue);
    TaggedValue newTaggedValue = element.GetTaggedValues()
        .AddNew(convertToUnqualifiedName(taggedValueName), taggedValueValue);
    newTaggedValue.Update();
    element.GetTaggedValues().Refresh();
  }

  /**
   * Deletes the tag with the given name from the element.
   */
  public static void deleteTaggedValueElement(Element element, String taggedValueName) {
    Objects.requireNonNull(element);
    Objects.requireNonNull(taggedValueName);
    Collection<TaggedValue> taggedValues = element.GetTaggedValues();
    for (short i = 0; i < taggedValues.GetCount(); i++) {
      if (taggedValues.GetAt(i).GetName().equals(convertToUnqualifiedName(taggedValueName))) {
        taggedValues.Delete(i);
        break;
      }
    }
    element.GetTaggedValues().Refresh();
  }

  /**
   * Adds a new tagged value to the attribute. The new tag is not a memo tag.
   */
  public static void addTaggedValueToAttribute(Attribute attribute, String taggedValueName,
      String taggedValueValue) {
    Objects.requireNonNull(attribute);
    Objects.requireNonNull(taggedValueName);
    Objects.requireNonNull(taggedValueValue);
    AttributeTag newTaggedValue = attribute.GetTaggedValues()
        .AddNew(convertToUnqualifiedName(taggedValueName), taggedValueValue);
    newTaggedValue.Update();
    attribute.GetTaggedValues().Refresh();
  }

  /**
   * Deletes the tag with the given name from the attribute.
   */
  public static void deleteTaggedValueAttribute(Attribute attribute, String taggedValueName) {
    Objects.requireNonNull(attribute);
    Objects.requireNonNull(taggedValueName);
    Collection<AttributeTag> taggedValues = attribute.GetTaggedValues();
    for (short i = 0; i < taggedValues.GetCount(); i++) {
      if (taggedValues.GetAt(i).GetName().equals(convertToUnqualifiedName(taggedValueName))) {
        taggedValues.Delete(i);
        break;
      }
    }
    attribute.GetTaggedValues().Refresh();
  }

  /**
   * Adds a new tagged value to the connector end. The new tag is not a memo tag.
   */
  public static void addTaggedValueToConnectorEnd(ConnectorEnd connectorEnd, String taggedValueName,
      String taggedValueValue) {
    Objects.requireNonNull(connectorEnd);
    Objects.requireNonNull(taggedValueName);
    Objects.requireNonNull(taggedValueValue);
    RoleTag newTaggedValue = connectorEnd.GetTaggedValues()
        .AddNew(convertToUnqualifiedName(taggedValueName), taggedValueValue);
    newTaggedValue.Update();
    connectorEnd.GetTaggedValues().Refresh();
  }

  /**
   * Deletes the tag with the given name from the connector end.
   */
  public static void deleteTaggedValueConnectorEnd(ConnectorEnd connectorEnd,
      String taggedValueName) {
    Objects.requireNonNull(connectorEnd);
    Objects.requireNonNull(taggedValueName);
    Collection<RoleTag> taggedValues = connectorEnd.GetTaggedValues();
    for (short i = 0; i < taggedValues.GetCount(); i++) {
      if (taggedValues.GetAt(i).GetTag().equals(convertToUnqualifiedName(taggedValueName))) {
        taggedValues.Delete(i);
        break;
      }
    }
    connectorEnd.GetTaggedValues().Refresh();
  }

  /**
   * Adds a new tagged value to the connector. The new tag is not a memo tag.
   */
  public static void addTaggedValueToConnector(Connector connector, String taggedValueName,
      String taggedValueValue) {
    Objects.requireNonNull(connector);
    Objects.requireNonNull(taggedValueName);
    Objects.requireNonNull(taggedValueValue);
    ConnectorTag newTaggedValue = connector.GetTaggedValues()
        .AddNew(convertToUnqualifiedName(taggedValueName), taggedValueValue);
    newTaggedValue.Update();
    connector.GetTaggedValues().Refresh();
  }

  /**
   * Deletes the tag with the given name from the connector.
   */
  public static void deleteTaggedValueConnector(Connector connector, String taggedValueName) {
    Objects.requireNonNull(connector);
    Objects.requireNonNull(taggedValueName);
    Collection<ConnectorTag> taggedValues = connector.GetTaggedValues();
    for (short i = 0; i < taggedValues.GetCount(); i++) {
      if (taggedValues.GetAt(i).GetName().equals(convertToUnqualifiedName(taggedValueName))) {
        taggedValues.Delete(i);
        break;
      }
    }
    connector.GetTaggedValues().Refresh();
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
    Objects.requireNonNull(element);
    Objects.requireNonNull(taggedValueName);
    String taggedValueValue = "";
    String convertedTaggedValueName = convertToUnqualifiedName(taggedValueName);
    for (TaggedValue taggedValue : element.GetTaggedValues()) {
      if (convertedTaggedValueName.equals(taggedValue.GetName())) {
        taggedValueValue = getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }


  /**
   * Gets the value of the tagged value with the given name for the given attribute, or the empty
   * string if it does not exist (so never null).
   */
  public static String getTaggedValueValue(Attribute attribute, String taggedValueName) {
    Objects.requireNonNull(attribute);
    Objects.requireNonNull(taggedValueName);
    String taggedValueValue = "";
    String convertedTaggedValueName = convertToUnqualifiedName(taggedValueName);
    for (AttributeTag taggedValue : attribute.GetTaggedValues()) {
      if (convertedTaggedValueName.equals(taggedValue.GetName())) {
        taggedValueValue = getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }

  /**
   * Gets the value of the tagged value with the given name for the given connector end, or the
   * empty string if it does not exist (so never null).
   */
  public static String getTaggedValueValue(ConnectorEnd connectorEnd, String taggedValueName) {
    Objects.requireNonNull(connectorEnd);
    Objects.requireNonNull(taggedValueName);
    String taggedValueValue = "";
    String convertedTaggedValueName = convertToUnqualifiedName(taggedValueName);
    for (RoleTag taggedValue : connectorEnd.GetTaggedValues()) {
      if (convertedTaggedValueName.equals(taggedValue.GetTag())) {
        taggedValueValue = getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }

  /**
   * Gets the value of the tagged value with the given name for the given connector, or the empty
   * string if it does not exist (so never null).
   */
  public static String getTaggedValueValue(Connector connector, String taggedValueName) {
    Objects.requireNonNull(connector);
    Objects.requireNonNull(taggedValueName);
    String taggedValueValue = "";
    String convertedTaggedValueName = convertToUnqualifiedName(taggedValueName);
    for (ConnectorTag taggedValue : connector.GetTaggedValues()) {
      if (convertedTaggedValueName.equals(taggedValue.GetName())) {
        taggedValueValue = getTextOfTaggedValue(taggedValue);
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
        taggedValueValue = getTextOfTaggedValue(taggedValue);
      }
    }
    return taggedValueValue;
  }

  /**
   * Return the actual content of the tagged value, as a {@link URI}.
   *
   * @throws ModellingToolsException If a valid URI could not be created.
   */
  public static URI getTaggedValueValueAsUri(Object object, String taggedValueName)
      throws ModellingToolsException {
    String taggedValueValue = getTaggedValueValue(object, taggedValueName);
    try {
      return new URI(taggedValueValue);
    } catch (URISyntaxException e) {
      throw new ModellingToolsException(
          "Could not create a valid URI from string " + taggedValueValue, e);
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

  private static String getTextOfTaggedValue(ConnectorTag taggedValue) {
    String taggedValueValue;
    if ("<memo>".equals(taggedValue.GetValue())) {
      taggedValueValue = taggedValue.GetNotes();
    } else {
      taggedValueValue = taggedValue.GetValue();
    }
    return taggedValueValue;
  }

  /**
   * Finds the value of the given tag and splits the value of the given tag in parts according to
   * the given separator. If a tag with the given name is not present, an empty string array is
   * returned.
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

  private static String convertToUnqualifiedName(String name) {
    String unqualifiedName;
    if (StringUtils.contains(name, "::")) {
      unqualifiedName = StringUtils.substringAfterLast(name, "::");
    } else {
      unqualifiedName = name;
    }
    return unqualifiedName;
  }

  /**
   * Returns the fully qualified name if the tag is connected to a profile, or the unqualified name
   * if the tag is not connected to a profile.
   */
  private static String getNameOfTaggedValue(TaggedValue taggedValue) {
    final String name;
    if (StringUtils.isEmpty(taggedValue.GetFQName())) {
      name = taggedValue.GetName();
    } else {
      name = taggedValue.GetFQName();
    }
    return name;
  }

  /**
   * Returns the fully qualified name if the tag is connected to a profile, or the unqualified name
   * if the tag is not connected to a profile.
   */
  private static String getNameOfTaggedValue(AttributeTag taggedValue) {
    final String name;
    if (StringUtils.isEmpty(taggedValue.GetFQName())) {
      name = taggedValue.GetName();
    } else {
      name = taggedValue.GetFQName();
    }
    return name;
  }

  /**
   * Returns the fully qualified name if the tag is connected to a profile, or the unqualified name
   * if the tag is not connected to a profile.
   */
  private static String getNameOfTaggedValue(RoleTag taggedValue) {
    final String name;
    if (StringUtils.isEmpty(taggedValue.GetFQName())) {
      name = taggedValue.GetTag();
    } else {
      name = taggedValue.GetFQName();
    }
    return name;
  }

  /**
   * Returns the fully qualified name if the tag is connected to a profile, or the unqualified name
   * if the tag is not connected to a profile.
   */
  private static String getNameOfTaggedValue(ConnectorTag taggedValue) {
    final String name;
    if (StringUtils.isEmpty(taggedValue.GetFQName())) {
      name = taggedValue.GetName();
    } else {
      name = taggedValue.GetFQName();
    }
    return name;
  }

}
