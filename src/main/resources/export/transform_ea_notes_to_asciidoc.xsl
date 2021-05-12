<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" indent="no" encoding="UTF-8" />
  
  <xsl:template match="/">
    <xsl:copy>
      <xsl:apply-templates select="note" />
    </xsl:copy>
  </xsl:template>
  
  <!-- <a href="$element://{14A1B4F4-781D-47db-87B9-3F08578D0CE3}"><font color="#0000ff"><u>hyperlink text</u></font></a>
  is transformed to
  <<14a1b4f4-781d-47db-87b9-3f08578d0ce3,hyperlink text>> -->
  <xsl:template match="a">
      <xsl:text>&lt;&lt;</xsl:text>
      <xsl:value-of select="lower-case(substring(@href,13,36))" />
      <xsl:text>,</xsl:text>
      <xsl:apply-templates select="node()" />
      <xsl:text>&gt;&gt;</xsl:text>
  </xsl:template>
  
  <xsl:template match="b">
      <xsl:text>*</xsl:text>
      <xsl:value-of select="text()" />
      <xsl:text>*</xsl:text>
  </xsl:template>
  
  <xsl:template match="ul">
      <!-- Add a blank line in case no whitespace is present before the unordered list (Asciidoc: blank line needed before a list) -->
      <xsl:text>&#xD;&#xA;</xsl:text>
      <xsl:apply-templates select="li" />
  </xsl:template>
  
  <xsl:template match="li" >
      <xsl:text>* </xsl:text>
      <xsl:apply-templates select="node()" />
      <xsl:text>&#xD;&#xA;</xsl:text>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()" />
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>