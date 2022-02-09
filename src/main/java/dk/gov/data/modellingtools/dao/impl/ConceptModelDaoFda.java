package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.constants.FdaConstants;
import dk.gov.data.modellingtools.dao.ConceptModelDao;
import dk.gov.data.modellingtools.ea.EnterpriseArchitectWrapper;
import dk.gov.data.modellingtools.ea.utils.TaggedValueUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.ConceptModel;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;
import org.sparx.Element;

/**
 * Implementation based on the FDA rules for concept and data modelling.
 */
public class ConceptModelDaoFda implements ConceptModelDao {

  // TODO make configurable
  private static final char SPLIT_CHARACTER = ';';

  private EnterpriseArchitectWrapper eaWrapper;

  @SuppressFBWarnings("EI_EXPOSE_REP")
  public ConceptModelDaoFda(EnterpriseArchitectWrapper eaWrapper) {
    super();
    this.eaWrapper = eaWrapper;
  }

  @Override
  public ConceptModel findByPackageGuid(String packageGuid) throws ModellingToolsException {
    // TODO extract all tags to constants and move to FdaConstants
    // TODO find all tags at once
    Element packageElement = this.eaWrapper.getPackageByGuid(packageGuid).GetElement();
    String fqStereotype = packageElement.GetFQStereotype();
    Validate.isTrue(FdaConstants.FQ_STEREOTYPE_CONCEPT_MODEL.equals(fqStereotype),
        "Stereotype must be " + FdaConstants.FQ_STEREOTYPE_CONCEPT_MODEL + " but is "
            + fqStereotype);
    ConceptModel conceptModel = new ConceptModel();
    try {
      conceptModel.setIdentifier(TaggedValueUtils.getTaggedValueValueAsUri(packageElement, "URI"));
    } catch (URISyntaxException e) {
      throw new ModellingToolsException("Could not create a valid URI", e);
    }

    Map<String, String> titles = new HashMap<>();
    titles.put("da", TaggedValueUtils.getTaggedValueValue(packageElement, "title (da)"));
    titles.put("en", TaggedValueUtils.getTaggedValueValue(packageElement, "title (en)"));
    conceptModel.setTitles(titles);

    Map<String, String> descriptions = new HashMap<>();
    descriptions.put("da",
        TaggedValueUtils.getTaggedValueValue(packageElement, "description (da)"));
    descriptions.put("en",
        TaggedValueUtils.getTaggedValueValue(packageElement, "description (en)"));
    conceptModel.setDescriptions(descriptions);

    // TODO move logic to split tagged value value to TaggedValueUtils
    String[] languagesAsStrings = StringUtils.stripAll(
        StringUtils.split(TaggedValueUtils.getTaggedValueValueFullyQualifiedName(packageElement,
            FdaConstants.QUALIFIER_TAGS + "language"), SPLIT_CHARACTER));
    URI[] languages = new URI[languagesAsStrings.length];
    for (int i = 0; i < languagesAsStrings.length; i++) {
      try {
        languages[i] = new URI(languagesAsStrings[i]);
      } catch (URISyntaxException e) {
        throw new ModellingToolsException("Could not convert string " + languagesAsStrings[i]
            + ", a part of tagged value " + FdaConstants.QUALIFIER_TAGS + "language" + " "
            + " on element " + packageElement.GetName() + " to a valid URI", e);
      }
    }
    conceptModel.setLanguages(languages);

    conceptModel.setResponsibleEntity(
        TaggedValueUtils.getTaggedValueValue(packageElement, "responsibleEntity"));
    try {
      conceptModel
          .setPublisher(TaggedValueUtils.getTaggedValueValueAsUri(packageElement, "publisher"));
    } catch (URISyntaxException e) {
      throw new ModellingToolsException("Could not create a valid URI", e);
    }
    conceptModel.setVersion(TaggedValueUtils.getTaggedValueValue(packageElement, "versionInfo"));
    String modifiedDateAsString = TaggedValueUtils.getTaggedValueValueFullyQualifiedName(
        packageElement, FdaConstants.QUALIFIER_TAGS + "modified");
    if (StringUtils.isNotBlank(modifiedDateAsString)) {
      try {
        // TODO dependent on Locale?
        conceptModel
            .setLastModifiedDate(DateUtils.parseDateStrictly(modifiedDateAsString, "dd-MM-yyyy"));
      } catch (ParseException e1) {
        throw new ModellingToolsException("Could not parse " + modifiedDateAsString, e1);
      }
    }

    conceptModel
        .setModelStatus(TaggedValueUtils.getTaggedValueValue(packageElement, "modelStatus"));
    conceptModel
        .setApprovalStatus(TaggedValueUtils.getTaggedValueValue(packageElement, "approvalStatus"));
    conceptModel
        .setApprovingAuthority(TaggedValueUtils.getTaggedValueValue(packageElement, "approvedBy"));
    conceptModel.setModelScope(TaggedValueUtils.getTaggedValueValue(packageElement, "modelScope"));

    String taggedValueName = "theme";
    String[] themesAsStrings = StringUtils.stripAll(StringUtils.split(
        TaggedValueUtils.getTaggedValueValue(packageElement, taggedValueName), SPLIT_CHARACTER));
    URI[] themes = new URI[themesAsStrings.length];
    for (int i = 0; i < themesAsStrings.length; i++) {
      try {
        themes[i] = new URI(themesAsStrings[i]);
      } catch (URISyntaxException e) {
        throw new ModellingToolsException("Could not convert string " + themesAsStrings[i]
            + ", a part of tagged value " + taggedValueName + " " + " on element "
            + packageElement.GetName() + " to a valid URI", e);
      }
    }
    conceptModel.setThemes(themes);

    Map<String, String> versionNotes = new HashMap<>();
    versionNotes.put("da",
        TaggedValueUtils.getTaggedValueValue(packageElement, "versionNotes (da)"));
    versionNotes.put("en",
        TaggedValueUtils.getTaggedValueValue(packageElement, "versionNotes (en)"));
    conceptModel.setVersionNotes(versionNotes);

    return conceptModel;
  }

}
