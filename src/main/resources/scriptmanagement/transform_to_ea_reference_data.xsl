<?xml version="1.0"?>
<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8" />
  <xsl:template match="/">
    <xsl:copy>
      <xsl:apply-templates select="EADATA" />
    </xsl:copy>
  </xsl:template>
  <xsl:template match="EADATA">
    <RefData version="1.0" exporter="Geodata Tools">
      <DataSet name="Automation Scripts" table="t_script" filter="ScriptName='#ScriptName#' and ScriptCategory='#ScriptCategory#'" stoplist=";ScriptID;">
        <xsl:apply-templates select="Dataset_0/Data/Row" />
      </DataSet>
    </RefData>
  </xsl:template>
  <xsl:template match="Row">
    <DataRow>
      <xsl:apply-templates select="*" />
    </DataRow>
  </xsl:template>
  <xsl:template match="*">
    <xsl:if test="string-length(text()) != 0">
      <xsl:element name="Column">
        <xsl:attribute name="name"><xsl:value-of select="local-name()" /></xsl:attribute>
        <xsl:attribute name="value"><xsl:value-of select="text()" /></xsl:attribute>
      </xsl:element>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>