GUID,UML-NAVN,NAMESPACE,TYPE,prefLabel (da),definition (da),comment (da),applicationNote (da),example (da),altLabel (da),deprecatedLabel (da),source,legalSource,URI
<#if strictnessMode = StrictnessMode.STRICT><#-- Fail if a tagged value is missing in the UML model. -->
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${modelElement.taggedValues["prefLabel (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["definition (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["comment (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["applicationNote (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["example (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["altLabel (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["deprecatedLabel (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["source"]?replace("\"", "\"\"")}","${modelElement.taggedValues["legalSource"]?replace("\"", "\"\"")}","${modelElement.taggedValues["URI"]?replace("\"", "\"\"")}"
</#list>
<#else>
<#if strictnessMode = StrictnessMode.MODERATE><#-- Output '_MISSING TAG_' if the requested tag is missing in the UML model. -->
<#assign defaultValue = "_MISSING TAG_" >
<#elseif strictnessMode = StrictnessMode.LENIENT><#-- Output empty string if the requested tag is missing in the UML model. -->
<#assign defaultValue = "" >
</#if>
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${modelElement.taggedValues["prefLabel (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["definition (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["comment (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["applicationNote (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["example (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["altLabel (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["deprecatedLabel (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["source"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["legalSource"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["URI"]!defaultValue?replace("\"", "\"\"")}"
</#list>
</#if>