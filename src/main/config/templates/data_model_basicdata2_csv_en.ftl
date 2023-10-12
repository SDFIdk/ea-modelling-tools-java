GUID,UML NAME,NAMESPACE,TYPE,prefLabel (en),definition (en),comment (en),applicationNote (en),example (en),altLabel (en),deprecatedLabel (en),source,legalSource,URI
<#if strictnessMode = StrictnessMode.STRICT><#-- Fail if a tagged value is missing in the UML model. -->
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${modelElement.taggedValues["prefLabel (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["definition (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["comment (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["applicationNote (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["example (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["altLabel (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["deprecatedLabel (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["source"]?replace("\"", "\"\"")}","${modelElement.taggedValues["legalSource"]?replace("\"", "\"\"")}","${modelElement.taggedValues["URI"]?replace("\"", "\"\"")}"
</#list>
<#else>
<#if strictnessMode = StrictnessMode.MODERATE><#-- Output '_MISSING TAG_' if the requested tag is missing in the UML model. -->
<#assign defaultValue = "_MISSING TAG_" >
<#elseif strictnessMode = StrictnessMode.LENIENT><#-- Output empty string if the requested tag is missing in the UML model. -->
<#assign defaultValue = "" >
</#if>
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${modelElement.taggedValues["prefLabel (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["definition (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["comment (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["applicationNote (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["example (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["altLabel (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["deprecatedLabel (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["source"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["legalSource"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["URI"]!defaultValue?replace("\"", "\"\"")}"
</#list>
</#if>