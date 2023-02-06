package dk.gov.data.modellingtools.constants;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Constants related to the Basic Data UML profile v1.
 */
public final class BasicData1Constants {

  public static final String FQ_STEREOTYPE_CODE_LIST = "Grunddata::DKKodeliste";
  public static final String FQ_STEREOTYPE_DATA_TYPE = "Grunddata::DKDatatype";
  public static final String FQ_STEREOTYPE_DOMAIN_MODEL = "Grunddata::DKDomænemodel";
  public static final String FQ_STEREOTYPE_ENUMERATION = "Grunddata::DKEnumeration";
  public static final String FQ_STEREOTYPE_FEATURE_TYPE = "Geodata::DKFeaturetype";
  public static final String FQ_STEREOTYPE_OBJECT_TYPE = "Grunddata::DKObjekttype";
  public static final String FQ_STEREOTYPE_PROPERTY = "Grunddata::DKEgenskab";

  public static final String TAG_ALTERNATIVT_NAVN = "alternativtNavn";
  public static final String TAG_DEFINITION = "definition";
  public static final String TAG_EXAMPLE = "eksempel";
  public static final String TAG_LOVGRUNDLAG = "lovgrundlag";
  public static final String TAG_NOTE = "note";

  public static final String TAG_VOCABULARY = "vokabularium";

  public static final String TAG_FORVALTNINGSOPGAVE = "forvaltingsopgave";
  public static final String TAG_MODELDOMÆNE = "modeldomæne";
  public static final String TAG_REGISTERMYNDIGHED = "registermyndighed";
  public static final String TAG_STATUS = "status";
  public static final String TAG_VERSION = "version";

  private static Set<String> modelElementTags = new HashSet<>();
  private static Set<String> modelTags = new HashSet<>();

  static {
    modelElementTags.add(TAG_ALTERNATIVT_NAVN);
    modelElementTags.add(TAG_DEFINITION);
    modelElementTags.add(TAG_EXAMPLE);
    modelElementTags.add(TAG_LOVGRUNDLAG);
    modelElementTags.add(TAG_NOTE);
  }

  static {
    modelTags.add(TAG_FORVALTNINGSOPGAVE);
    modelTags.add(TAG_MODELDOMÆNE);
    modelTags.add(TAG_REGISTERMYNDIGHED);
    modelTags.add(TAG_STATUS);
    modelTags.add(TAG_VERSION);
  }

  public static Collection<String> getAllModelElementTags() {
    return Collections.unmodifiableCollection(modelElementTags);
  }

  public static Collection<String> getAllModelTags() {
    return Collections.unmodifiableCollection(modelTags);
  }

  private BasicData1Constants() {
    super();
  }

}
