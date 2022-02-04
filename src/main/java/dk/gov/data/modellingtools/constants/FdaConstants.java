package dk.gov.data.modellingtools.constants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Constants related to the FDA UML profile.
 */
public final class FdaConstants {

  public static final String QUALIFIER_TAGS = "FDAprofil::Model::";
  public static final String FQ_STEREOTYPE_CONCEPT = "FDAprofil::Concept";
  public static final String FQ_STEREOTYPE_CONCEPT_MODEL = "FDAprofil::ConceptModel";
  /**
   * Use {@link #STEREOTYPE_LOGICAL_DATA_MODEL} instead when checking a package for a stereotype.
   */
  public static final String FQ_STEREOTYPE_LOGICAL_DATA_MODEL = "FDAprofil::LogicalDataModel";
  public static final String STEREOTYPE_LOGICAL_DATA_MODEL = "LogicalDataModel";
  public static final String FQ_STEREOTYPE_MODEL_ELEMENT = "FDAprofil::ModelElement";

  // model element tags
  public static final String TAG_ALTERNATIVE_LABEL_DA = "altLabel (da)";
  public static final String TAG_ALTERNATIVE_LABEL_EN = "altLabel (en)";
  public static final String TAG_APPLICATION_NOTE_DA = "applicationNote (da)";
  public static final String TAG_APPLICATION_NOTE_EN = "applicationNote (en)";
  public static final String TAG_COMMENT_DA = "comment (da)";
  public static final String TAG_COMMENT_EN = "comment (en)";
  public static final String TAG_DEFINITION_DA = "definition (da)";
  public static final String TAG_DEFINITION_EN = "definition (en)";
  public static final String TAG_DEPRECATED_LABEL_DA = "deprecatedLabel (da)";
  public static final String TAG_DEPRECATED_LABEL_EN = "deprecatedLabel (en)";
  public static final String TAG_EXAMPLE_DA = "example (da)";
  public static final String TAG_EXAMPLE_EN = "example (en)";
  public static final String TAG_IS_DEFINED_BY = "isDefinedBy";
  public static final String TAG_PREFERRED_LABEL_DA = "prefLabel (da)";
  public static final String TAG_PREFERRED_LABEL_EN = "prefLabel (en)";
  public static final String TAG_SOURCE = "source";
  public static final String TAG_URI = "URI";

  // model tags
  public static final String TAG_VERSION = "versionInfo";
  public static final String TAG_TITLE_DA = "title (da)";
  public static final String TAG_TITLE_EN = "title (en)";

  // tags on several stereotypes
  public static final String TAG_LABEL_DA = "label (da)";
  public static final String TAG_LABEL_EN = "label (en)";

  private static Set<String> modelElementTags = new HashSet<>();

  static {
    modelElementTags.add(TAG_ALTERNATIVE_LABEL_DA);
    modelElementTags.add(TAG_ALTERNATIVE_LABEL_EN);
    modelElementTags.add(TAG_APPLICATION_NOTE_DA);
    modelElementTags.add(TAG_APPLICATION_NOTE_EN);
    modelElementTags.add(TAG_COMMENT_DA);
    modelElementTags.add(TAG_COMMENT_EN);
    modelElementTags.add(TAG_DEFINITION_DA);
    modelElementTags.add(TAG_DEFINITION_EN);
    modelElementTags.add(TAG_DEPRECATED_LABEL_DA);
    modelElementTags.add(TAG_DEPRECATED_LABEL_EN);
    modelElementTags.add(TAG_EXAMPLE_DA);
    modelElementTags.add(TAG_EXAMPLE_EN);
    modelElementTags.add(TAG_IS_DEFINED_BY);
    modelElementTags.add(TAG_PREFERRED_LABEL_DA);
    modelElementTags.add(TAG_PREFERRED_LABEL_EN);
    modelElementTags.add(TAG_SOURCE);
    modelElementTags.add(TAG_URI);
  }

  private FdaConstants() {
    super();
  }

  public static Collection<String> getAllModelElementTags() {
    return Collections.unmodifiableCollection(modelElementTags);
  }

}
