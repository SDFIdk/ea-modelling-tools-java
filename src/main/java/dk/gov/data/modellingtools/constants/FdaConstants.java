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
  public static final String FQ_STEREOTYPE_LOGICAL_DATA_MODEL = "FDAprofil::LogicalDataModel";
  public static final String FQ_STEREOTYPE_MODEL_ELEMENT = "FDAprofil::ModelElement";

  // model element tags
  public static final String TAG_ALTERNATIVE_LABEL_DA = "altLabel (da)";
  public static final String TAG_ALTERNATIVE_LABEL_EN = "altLabel (en)";
  public static final String TAG_APPLICATION_NOTE_DA = "applicationNote (da)";
  public static final String TAG_APPLICATION_NOTE_EN = "applicationNote (en)";
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

  // model tags
  public static final String TAG_APPROVAL_STATUS = "approvalStatus";
  public static final String TAG_APPROVER = "approvedBy";
  public static final String TAG_DESCRIPTION_DA = "description (da)";
  public static final String TAG_DESCRIPTION_EN = "description (en)";
  public static final String TAG_LANGUAGE = "language";
  public static final String TAG_MODEL_SCOPE = "modelScope";
  public static final String TAG_MODEL_STATUS = "modelStatus";
  public static final String TAG_MODIFIED = "modified";
  public static final String TAG_PUBLISHER = "publisher";
  public static final String TAG_NAMESPACE = "namespace";
  public static final String TAG_NAMESPACE_PREFIX = "namespacePrefix";
  public static final String TAG_RESPONSIBLE_ENTITY = "responsibleEntity";
  public static final String TAG_TITLE_DA = "title (da)";
  public static final String TAG_TITLE_EN = "title (en)";
  public static final String TAG_VERSION = "versionInfo";
  public static final String TAG_VERSION_NOTES_DA = "versionNotes (da)";
  public static final String TAG_VERSION_NOTES_EN = "versionNotes (en)";

  // tags on several stereotypes
  public static final String TAG_COMMENT_DA = "comment (da)";
  public static final String TAG_COMMENT_EN = "comment (en)";
  public static final String TAG_LABEL_DA = "label (da)";
  public static final String TAG_LABEL_EN = "label (en)";
  public static final String TAG_LEGALSOURCE = "legalSource";
  public static final String TAG_URI = "URI";

  private static Set<String> modelElementTags = new HashSet<>();
  private static Set<String> modelTagsV20 = new HashSet<>();
  private static Set<String> modelTagsV21 = new HashSet<>();

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
    modelElementTags.add(TAG_LEGALSOURCE);
    modelElementTags.add(TAG_PREFERRED_LABEL_DA);
    modelElementTags.add(TAG_PREFERRED_LABEL_EN);
    modelElementTags.add(TAG_SOURCE);
    modelElementTags.add(TAG_URI);
  }

  static {
    modelTagsV20.add(TAG_APPROVAL_STATUS);
    modelTagsV20.add(TAG_APPROVER);
    modelTagsV20.add(TAG_DESCRIPTION_DA);
    modelTagsV20.add(TAG_DESCRIPTION_EN);
    modelTagsV20.add(TAG_LANGUAGE);
    modelTagsV20.add(TAG_MODEL_SCOPE);
    modelTagsV20.add(TAG_MODEL_STATUS);
    modelTagsV20.add(TAG_MODIFIED);
    modelTagsV20.add(TAG_NAMESPACE);
    modelTagsV20.add(TAG_NAMESPACE_PREFIX);
    modelTagsV20.add(TAG_RESPONSIBLE_ENTITY);
    modelTagsV20.add(TAG_TITLE_DA);
    modelTagsV20.add(TAG_TITLE_EN);
    modelTagsV20.add(TAG_VERSION);
    modelTagsV20.add(TAG_VERSION_NOTES_DA);
    modelTagsV20.add(TAG_VERSION_NOTES_EN);
  }

  static {
    modelTagsV21.add(TAG_APPROVAL_STATUS);
    modelTagsV21.add(TAG_APPROVER);
    modelTagsV21.add(TAG_DESCRIPTION_DA);
    modelTagsV21.add(TAG_DESCRIPTION_EN);
    modelTagsV21.add(TAG_LANGUAGE);
    modelTagsV21.add(TAG_MODEL_SCOPE);
    modelTagsV21.add(TAG_MODEL_STATUS);
    modelTagsV21.add(TAG_MODIFIED);
    modelTagsV21.add(TAG_NAMESPACE);
    modelTagsV21.add(TAG_NAMESPACE_PREFIX);
    modelTagsV21.add(TAG_RESPONSIBLE_ENTITY);
    modelTagsV21.add(TAG_TITLE_DA);
    modelTagsV21.add(TAG_TITLE_EN);
    modelTagsV21.add(TAG_VERSION);
    modelTagsV21.add(TAG_VERSION_NOTES_DA);
    modelTagsV21.add(TAG_VERSION_NOTES_EN);
  }

  private FdaConstants() {
    super();
  }

  public static Collection<String> getAllModelElementTags() {
    return Collections.unmodifiableCollection(modelElementTags);
  }

  public static Collection<String> getAllModelTagsV20() {
    return Collections.unmodifiableCollection(modelTagsV20);
  }

  public static Collection<String> getAllModelTagsV21() {
    return Collections.unmodifiableCollection(modelTagsV21);
  }

}
