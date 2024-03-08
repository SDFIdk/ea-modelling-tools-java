<xsl:stylesheet
    version="3.0"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <xsl:output
        method="text"
        encoding="UTF-8" />
        
    <xsl:param name="parseHtml" select="false" as="xsd:boolean" />
    
    <xsl:template match="/">
        <xsl:copy>
            <xsl:apply-templates select="EADATA" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="EADATA">
        <xsl:call-template name="header" />
        <xsl:apply-templates select="Dataset_0/Data/Row" />
    </xsl:template>
    <xsl:template name="header">
        <xsl:for-each select="Dataset_0/Data/Row[1]/*">
            <xsl:text>"</xsl:text>
            <xsl:value-of select="local-name()" />
            <xsl:text>"</xsl:text>
            <xsl:if test="not(position()=last())">
                <xsl:text>,</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>&#13;&#10;</xsl:text>
    </xsl:template>
    
    <xsl:template match="Row">
        <xsl:apply-templates select="*" />
        <xsl:text>&#13;&#10;</xsl:text>
    </xsl:template>
    
    <xsl:template match="*">
        <xsl:text>"</xsl:text>
        <xsl:choose>
            <xsl:when test="$parseHtml">
                <!-- EA saves strings in the Notes fields using a subset of the HTML tags 
                and using HTML entities for certain characters. For example, "æ" is saved as "&#230";.
                Therefore, parse the value as XML and extract the text again to get the actual characters. 
                For more about entities, see e.g. https://developer.mozilla.org/en-US/docs/Glossary/Entity -->
                <xsl:value-of select="replace(parse-xml-fragment(.)//text(), '&quot;', '&quot;&quot;')" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="replace(text(), '&quot;', '&quot;&quot;')" />
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>"</xsl:text>
        <xsl:if test="not(position()=last())">
            <xsl:text>,</xsl:text>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>