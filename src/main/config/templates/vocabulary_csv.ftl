<#ftl strip_whitespace=true>
<#if hasHeader>
"Model","Navn i model","Type","Foretrukken term","Definition","Kommentar","Eksempel","Accepterede termer","Kilde","Frarådede termer","Translitereret navn i model"
</#if>
<#assign umlTypeAbbreviations = {"CLASS":"kl", "ATTRIBUTE":"at", "ASSOCIATION_END":"ae", "ENUMERATION":"en", "ENUMERATION_LITERAL":"ev", "DATA_TYPE":"da"}>
<#list modelElements as modelElement>
"<#if hasMetadata>${metadataUrl}[</#if>${model.name} v${model.version}<#if hasMetadata>,title=Se metadata for ${model.name}]</#if>","${modelElement.umlName}","${umlTypeAbbreviations[modelElement.umlModelElementType.name()]}","${modelElement.concept.preferredTerms[language]?replace("\"", "\"\"")}","${modelElement.concept.definitions[language]?replace("\"", "\"\"")}","${modelElement.concept.notes[language]?replace("\"", "\"\"")}","${modelElement.concept.examples[language]?replace("\"", "\"\"")}","${modelElement.concept.acceptedTerms[language]?join("; ")?replace("\"", "\"\"")}","<#if modelElement.concept.source??>${modelElement.concept.source}[🡭,title=Gå til kilden]<#else>${modelElement.concept.sourceTextualReference!""}</#if>","${modelElement.concept.deprecatedTerms[language]?join("; ")?replace("\"", "\"\"")}","${modelElement.transliteratedUmlName}"
</#list>