package dk.gov.data.modellingtools.model;

import java.util.Map;
import java.util.Objects;
import org.apache.commons.exec.util.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Model element from the UML specification.
 */
public class ModelElement {

  private String eaGuid;

  private String umlName;

  private String transliteratedUmlName;

  private String uniqueName;

  private String namespaceName;

  private ModelElementType umlModelElementType;

  private String fqStereotype;

  private Map<String, String> taggedValues;

  public String getEaGuid() {
    return eaGuid;
  }

  public void setEaGuid(String eaGuid) {
    this.eaGuid = eaGuid;
  }

  public String getUmlName() {
    return umlName;
  }

  public void setUmlName(String umlName) {
    this.umlName = umlName;
  }

  /**
   * Replaces the special Danish characters with their basic Latin Ascii versions.
   */
  public String getTransliteratedUmlName() {
    // this method is used in a Freemarker template
    if (transliteratedUmlName == null) {
      transliteratedUmlName =
          StringUtils.replaceEach(umlName, new String[] {"Æ", "Ø", "Å", "É", "æ", "ø", "å", "é"},
              new String[] {"Ae", "Oe", "Aa", "E", "ae", "oe", "aa", "e"});
    }
    return transliteratedUmlName;
  }

  public String getUniqueName() {
    return uniqueName;
  }

  public void setUniqueName(String uniqueName) {
    this.uniqueName = uniqueName;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public void setNamespaceName(String namespaceName) {
    this.namespaceName = namespaceName;
  }

  public ModelElementType getUmlModelElementType() {
    return umlModelElementType;
  }

  public void setUmlModelElementType(ModelElementType umlModelElementType) {
    this.umlModelElementType = umlModelElementType;
  }

  public String getFqStereotype() {
    return fqStereotype;
  }

  public void setFqStereotype(String fqStereotype) {
    this.fqStereotype = fqStereotype;
  }

  public Map<String, String> getTaggedValues() {
    return MapUtils.copy(taggedValues);
  }

  public void setTaggedValues(Map<String, String> taggedValues) {
    this.taggedValues = MapUtils.copy(taggedValues);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("GUID", this.eaGuid).append("UML-name", this.umlName)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(eaGuid);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ModelElement other = (ModelElement) obj;
    return Objects.equals(eaGuid, other.eaGuid);
  }

  /**
   * Type of model element.
   */
  public enum ModelElementType {
    CLASS("class", "Class"), ATTRIBUTE("attribute"), ASSOCIATION("association"), ASSOCIATION_END(
        "association end"), ENUMERATION_LITERAL("enumeration literal"), ENUMERATION("enumeration",
            "Enumeration"), DATA_TYPE("data type", "DataType"), INTERFACE("interface", "Interface");

    private String name;
    private String eaType;

    ModelElementType(String name) {
      this(name, null);
    }

    ModelElementType(String name, String eaType) {
      this.name = name;
      this.eaType = eaType;
    }

    public String getName() {
      return name;
    }

    /**
     * Returns the type of element in EA.
     *
     * @see org.sparx.Element#GetType()
     * @throws UnsupportedOperationException When the EA type is not a type that in EA is treated as
     *         an Element (table t_object).
     */
    public String getEaType() {
      if (this.equals(CLASS) || this.equals(DATA_TYPE) || this.equals(ENUMERATION)
          || this.equals(INTERFACE)) {
        return eaType;
      } else {
        throw new UnsupportedOperationException("This method is not supported for " + this);
      }
    }

  }

}
