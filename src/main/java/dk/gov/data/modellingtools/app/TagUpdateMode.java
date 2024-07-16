package dk.gov.data.modellingtools.app;

/**
 * Mode that defines how tags are updated.
 */
public enum TagUpdateMode {
  /**
   * Existing tags are updated, no tags are added or deleted from the model.
   */
  UPDATE_ONLY,
  /**
   * Existing tags are updated if the new value is not empty and are deleted if the new value is
   * empty; tags are added if the new value is a completely new tag.
   */
  UPDATE_ADD_DELETE;
}
