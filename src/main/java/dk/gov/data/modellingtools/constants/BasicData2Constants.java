package dk.gov.data.modellingtools.constants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Constants related to the Basic Data UML profile v2.
 */
public final class BasicData2Constants {

  public static final String FQ_STEREOTYPE_CLASSIFICATION_MODEL =
      "Grunddata2::DKKlassifikationsmodel";
  public static final String FQ_STEREOTYPE_CODE_LIST = "Grunddata2::DKKodeliste";
  public static final String FQ_STEREOTYPE_DATA_TYPE = "Grunddata2::DKDatatype";
  public static final String FQ_STEREOTYPE_DOMAIN_MODEL = "Grunddata2::DKDomænemodel";
  public static final String FQ_STEREOTYPE_ENUMERATION = "Grunddata2::DKEnumeration";
  public static final String FQ_STEREOTYPE_ENUMERATION_LITERAL = "Grunddata2::DKEnumværdi";
  public static final String FQ_STEREOTYPE_OBJECT_TYPE = "Grunddata2::DKObjekttype";
  public static final String FQ_STEREOTYPE_PROPERTY = "Grunddata2::DKEgenskab";

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
  public static final String TAG_PREFERRED_LABEL_DA = "prefLabel (da)";
  public static final String TAG_PREFERRED_LABEL_EN = "prefLabel (en)";
  public static final String TAG_SOURCE = "source";
  public static final String TAG_VOCABULARY = "vokabularium";
  public static final String TAG_URI = "URI";

  // model tags
  public static final String TAG_APPROVAL_STATUS = "approvalStatus";
  public static final String TAG_APPROVER = "approvedBy";
  public static final String TAG_DESCRIPTION_DA = "description (da)";
  public static final String TAG_DESCRIPTION_EN = "description (en)";
  public static final String TAG_LANGUAGE = "language";
  public static final String TAG_MODEL_SCOPE = "modelScope";
  public static final String TAG_MODEL_STATUS = "modelStatus";
  public static final String TAG_MODIFIED = "modified";
  public static final String TAG_NAMESPACE = "namespace";
  public static final String TAG_NAMESPACE_PREFIX = "namespacePrefix";
  public static final String TAG_RESPONSIBLE_ENTITY = "responsibleEntity";
  public static final String TAG_THEME = "theme";
  public static final String TAG_TITLE_DA = "title (da)";
  public static final String TAG_TITLE_EN = "title (en)";
  public static final String TAG_VERSION = "versionInfo";
  public static final String TAG_VERSION_NOTES = "versionNotes";

  // tags on models and model elements
  public static final String TAG_LEGALSOURCE = "legalSource";

  private static Set<String> modelElementTags = new HashSet<>();
  private static Set<String> modelTags = new HashSet<>();

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
    modelElementTags.add(TAG_LEGALSOURCE);
    modelElementTags.add(TAG_PREFERRED_LABEL_DA);
    modelElementTags.add(TAG_PREFERRED_LABEL_EN);
    modelElementTags.add(TAG_SOURCE);
    modelElementTags.add(TAG_URI);
  }

  static {
    modelTags.add(TAG_APPROVAL_STATUS);
    modelTags.add(TAG_APPROVER);
    modelTags.add(TAG_DESCRIPTION_DA);
    modelTags.add(TAG_DESCRIPTION_EN);
    modelTags.add(TAG_LANGUAGE);
    modelTags.add(TAG_MODEL_SCOPE);
    modelTags.add(TAG_MODEL_STATUS);
    modelTags.add(TAG_MODIFIED);
    modelTags.add(TAG_NAMESPACE);
    modelTags.add(TAG_NAMESPACE_PREFIX);
    modelTags.add(TAG_RESPONSIBLE_ENTITY);
    modelTags.add(TAG_THEME);
    modelTags.add(TAG_TITLE_DA);
    modelTags.add(TAG_TITLE_EN);
    modelTags.add(TAG_VERSION);
    modelTags.add(TAG_VERSION_NOTES);
  }

  public static Collection<String> getAllModelElementTags() {
    return Collections.unmodifiableCollection(modelElementTags);
  }

  public static Collection<String> getAllModelTags() {
    return Collections.unmodifiableCollection(modelTags);
  }

  private BasicData2Constants() {
    super();
  }

}
