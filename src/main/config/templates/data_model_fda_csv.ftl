GUID,UML-NAVN,NAMESPACE,TYPE,prefLabel (da),prefLabel (en),definition (da),definition (en),comment (da),comment (en),applicationNote (da),applicationNote (en),example (da),example (en),altLabel (da),altLabel (en),deprecatedLabel (da),deprecatedLabel (en),label (da),label (en),source,legalSource,URI,isDefinedBy,wasDerivedFrom
<#if strictnessMode = StrictnessMode.STRICT><#-- Fail if a tagged value is missing in the UML model. -->
<#list modelElements as modelElement>
${modelElement.eaGuid},${modelElement.umlName},${modelElement.namespaceName},${modelElement.umlModelElementType.name()},"${modelElement.taggedValues["prefLabel (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["prefLabel (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["definition (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["definition (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["comment (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["comment (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["applicationNote (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["applicationNote (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["example (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["example (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["altLabel (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["altLabel (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["deprecatedLabel (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["deprecatedLabel (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["label (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["label (en)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["source"]?replace("\"", "\"\"")}","${modelElement.taggedValues["legalSource"]?replace("\"", "\"\"")}","${modelElement.taggedValues["URI"]?replace("\"", "\"\"")}","${modelElement.taggedValues["isDefinedBy"]?replace("\"", "\"\"")}","${modelElement.taggedValues["wasDerivedFrom"]?replace("\"", "\"\"")}"
</#list>
<#else>
<#if strictnessMode = StrictnessMode.MODERATE><#-- Output '_MISSING TAG_' if the requested tag is missing in the UML model. -->
<#assign defaultValue = "_MISSING TAG_" >
<#elseif strictnessMode = StrictnessMode.LENIENT><#-- Output empty string if the requested tag is missing in the UML model. -->
<#assign defaultValue = "" >
</#if>
<#list modelElements as modelElement>
${modelElement.eaGuid},${modelElement.umlName},${modelElement.namespaceName},${modelElement.umlModelElementType.name()},"${modelElement.taggedValues["prefLabel (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["prefLabel (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["definition (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["definition (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["comment (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["comment (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["applicationNote (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["applicationNote (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["example (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["example (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["altLabel (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["altLabel (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["deprecatedLabel (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["deprecatedLabel (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["label (da)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["label (en)"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["source"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["legalSource"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["URI"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["isDefinedBy"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["wasDerivedFrom"]!defaultValue?replace("\"", "\"\"")}"
</#list>
</#if>