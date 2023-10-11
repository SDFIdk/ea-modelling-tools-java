package dk.gov.data.modellingtools.constants;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
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

  private static Set<String> modelElementTags;
  private static Set<String> modelTagsV20;
  private static Set<String> modelTagsV21;

  static {
    modelTagsV20 = Set.of(TAG_APPROVAL_STATUS, TAG_APPROVER, TAG_COMMENT_DA, TAG_COMMENT_EN,
        TAG_LANGUAGE, TAG_MODEL_SCOPE, TAG_MODEL_STATUS, TAG_MODIFIED, TAG_NAMESPACE,
        TAG_NAMESPACE_PREFIX, TAG_RESPONSIBLE_ENTITY, TAG_LABEL_DA, TAG_LABEL_EN, TAG_VERSION);

    modelTagsV21 = Set.of(TAG_APPROVAL_STATUS, TAG_APPROVER, TAG_DESCRIPTION_DA, TAG_DESCRIPTION_EN,
        TAG_LANGUAGE, TAG_MODEL_SCOPE, TAG_MODEL_STATUS, TAG_MODIFIED, TAG_NAMESPACE,
        TAG_NAMESPACE_PREFIX, TAG_RESPONSIBLE_ENTITY, TAG_TITLE_DA, TAG_TITLE_EN, TAG_VERSION,
        TAG_VERSION_NOTES_DA, TAG_VERSION_NOTES_EN);

    modelElementTags = Set.of(TAG_ALTERNATIVE_LABEL_DA, TAG_ALTERNATIVE_LABEL_EN,
        TAG_APPLICATION_NOTE_DA, TAG_APPLICATION_NOTE_EN, TAG_COMMENT_DA, TAG_COMMENT_EN,
        TAG_DEFINITION_DA, TAG_DEFINITION_EN, TAG_DEPRECATED_LABEL_DA, TAG_DEPRECATED_LABEL_EN,
        TAG_EXAMPLE_DA, TAG_EXAMPLE_EN, TAG_IS_DEFINED_BY, TAG_LABEL_DA, TAG_LABEL_EN,
        TAG_LEGALSOURCE, TAG_PREFERRED_LABEL_DA, TAG_PREFERRED_LABEL_EN, TAG_SOURCE, TAG_URI);
  }

  private FdaConstants() {
    super();
  }


  // Set.of returns an unmodifiable Set, therefore suppress warning MS_EXPOSE_REP.
  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Collection<String> getAllModelElementTags() {
    return modelElementTags;
  }

  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Collection<String> getAllModelTagsV20() {
    return modelTagsV20;
  }

  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Collection<String> getAllModelTagsV21() {
    return modelTagsV21;
  }

}
