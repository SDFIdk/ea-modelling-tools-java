package dk.gov.data.modellingtools.dao.impl;

import dk.gov.data.modellingtools.dao.ConceptDao;
import dk.gov.data.modellingtools.ea.utils.EaModelUtils;
import dk.gov.data.modellingtools.exception.ModellingToolsException;
import dk.gov.data.modellingtools.model.Concept;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sparx.Element;

/**
 * Abstract implementation of ConceptDao with helper methods for converting the tagged values for
 * source and legal source to URIs, if possible.
 */
public abstract class AbstractEaConceptDao implements ConceptDao {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEaConceptDao.class);

  public Concept findByElement(Element element) throws ModellingToolsException {
    LOGGER.debug("Finding concept for {}", EaModelUtils.toString(element));
    return findByObject(element);
  }

  protected abstract Concept findByObject(Object object) throws ModellingToolsException;

  protected void setSourceOrSourceTextualReference(Object object, Concept concept,
      String sourceTaggedValue) {
    if (StringUtils.isNotBlank(sourceTaggedValue)) {
      try {
        URI sourceUri = new URI(sourceTaggedValue);
        if ("https".equals(sourceUri.getScheme()) || "http".equals(sourceUri.getScheme())) {
          concept.setSource(sourceUri);
        } else {
          LOGGER.info(
              "Could not convert {} on {} to an absolute HTTP-URI, treating it as a textual reference",
              sourceTaggedValue, EaModelUtils.toString(object));
          concept.setSourceTextualReference(sourceTaggedValue);
        }
      } catch (URISyntaxException e) {
        LOGGER.info(
            "Could not convert {} on {} to a valid URI, ignoring message {}, treating it as a textual reference",
            sourceTaggedValue, EaModelUtils.toString(object), e.getMessage());
        concept.setSourceTextualReference(sourceTaggedValue);
      }
    }
  }

  protected void setLegalSource(Object object, Concept concept, String legalSourceTaggedValue) {
    if (StringUtils.isNotBlank(legalSourceTaggedValue)) {
      try {
        URI legalSourceUri = new URI(legalSourceTaggedValue);
        if ("https".equals(legalSourceUri.getScheme())
            || "http".equals(legalSourceUri.getScheme())) {
          concept.setLegalSource(legalSourceUri);
        } else {
          LOGGER.warn("Could not convert {} on {} to an absolute HTTP-URI, ignoring it",
              legalSourceTaggedValue, EaModelUtils.toString(object));
        }
      } catch (URISyntaxException e) {
        LOGGER.warn("Could not convert {} on {} to a valid URI, ignoring message {}",
            legalSourceTaggedValue, EaModelUtils.toString(object), e.getMessage());
      }
    }
  }

  protected void setDefiningConceptModel(Object object, Concept concept,
      String vocabularyTaggedValue) {
    if (StringUtils.isNotBlank(vocabularyTaggedValue)) {
      try {
        URI vocabularyUri = new URI(vocabularyTaggedValue);
        /*
         * Consider the vocabulary as the concept model (concept scheme) that defines the code list
         * itself and the values in that code list.
         */
        concept.setDefiningConceptModel(vocabularyUri);
      } catch (URISyntaxException e) {
        LOGGER.warn("Could not convert {} on {} to a valid URI, ignoring message {}",
            vocabularyTaggedValue, EaModelUtils.toString(object), e.getMessage());
      }
    }
  }

}
