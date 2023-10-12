package dk.gov.data.modellingtools.constants;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
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
  public static final String TAG_VERSION_NOTES_DA = "versionNotes (da)";
  public static final String TAG_VERSION_NOTES_EN = "versionNotes (en)";

  // tags on models and model elements
  public static final String TAG_LEGALSOURCE = "legalSource";

  // tag on DKObjekttype
  public static final String TAG_HISTORYMODEL = "historikmodel";

  // tag on DKKodeliste
  public static final String TAG_VOCABULARY = "vokabularium";

  private static Set<String> commonModelElementTags;
  private static final Set<String> commonModelTags;

  private static final Map<String, Collection<String>> tagsPerStereotype;

  private BasicData2Constants() {
    super();
  }

  static {
    commonModelTags = Set.of(TAG_APPROVAL_STATUS, TAG_APPROVER, TAG_DESCRIPTION_DA,
        TAG_DESCRIPTION_EN, TAG_LANGUAGE, TAG_LEGALSOURCE, TAG_MODEL_SCOPE, TAG_MODEL_STATUS,
        TAG_MODIFIED, TAG_NAMESPACE, TAG_NAMESPACE_PREFIX, TAG_RESPONSIBLE_ENTITY, TAG_THEME,
        TAG_TITLE_DA, TAG_TITLE_EN, TAG_VERSION, TAG_VERSION_NOTES_DA, TAG_VERSION_NOTES_EN);

    commonModelElementTags = Set.of(TAG_ALTERNATIVE_LABEL_DA, TAG_ALTERNATIVE_LABEL_EN,
        TAG_APPLICATION_NOTE_DA, TAG_APPLICATION_NOTE_EN, TAG_COMMENT_DA, TAG_COMMENT_EN,
        TAG_DEFINITION_DA, TAG_DEFINITION_EN, TAG_DEPRECATED_LABEL_DA, TAG_DEPRECATED_LABEL_EN,
        TAG_EXAMPLE_DA, TAG_EXAMPLE_EN, TAG_LEGALSOURCE, TAG_PREFERRED_LABEL_DA,
        TAG_PREFERRED_LABEL_EN, TAG_SOURCE, TAG_URI);

    Set<String> objectTypeTags = new HashSet<>(commonModelElementTags);
    objectTypeTags.add(TAG_HISTORYMODEL);
    objectTypeTags = Set.copyOf(objectTypeTags);
    Set<String> codeListTags = new HashSet<>(commonModelElementTags);
    codeListTags.add(TAG_VOCABULARY);
    codeListTags = Set.copyOf(codeListTags);
    tagsPerStereotype =
        Map.ofEntries(Map.entry(FQ_STEREOTYPE_CLASSIFICATION_MODEL, commonModelTags),
            Map.entry(FQ_STEREOTYPE_CODE_LIST, codeListTags),
            Map.entry(FQ_STEREOTYPE_DATA_TYPE, commonModelElementTags),
            Map.entry(FQ_STEREOTYPE_DOMAIN_MODEL, commonModelTags),
            Map.entry(FQ_STEREOTYPE_ENUMERATION, commonModelElementTags),
            Map.entry(FQ_STEREOTYPE_ENUMERATION_LITERAL, commonModelElementTags),
            Map.entry(FQ_STEREOTYPE_OBJECT_TYPE, objectTypeTags),
            Map.entry(FQ_STEREOTYPE_PROPERTY, commonModelElementTags));
  }

  // Set.of returns an unmodifiable Set, therefore suppress warning MS_EXPOSE_REP.
  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Collection<String> getCommonModelElementTags() {
    return commonModelElementTags;
  }

  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Collection<String> getCommonModelTags() {
    return commonModelTags;
  }

  @SuppressFBWarnings("MS_EXPOSE_REP")
  public static Map<String, Collection<String>> getTagsPerStereotype() {
    return tagsPerStereotype;
  }



}
