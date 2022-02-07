package dk.gov.data.modellingtools.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link UuidUtils}.
 */
public class UuidUtilsTests {

  @Test
  public void testStandardizeUuidNotation() {
    assertEquals("1cbfa485-acf5-4428-882a-a44831bcdf76",
        UuidUtils.standardizeUuidNotation("{1CBFA485-ACF5-4428-882A-A44831BCDF76}"));
  }

}
