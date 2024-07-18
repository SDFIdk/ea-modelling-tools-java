GUID,UML-NAVN,NAMESPACE,TYPE,definition,note,eksempel,alternativtNavn,lovgrundlag
<#if strictnessMode = StrictnessMode.STRICT><#-- Fail if a tagged value is missing in the UML model. -->
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${modelElement.taggedValues["definition"]?replace("\"", "\"\"")}","${modelElement.taggedValues["note"]?replace("\"", "\"\"")}","${modelElement.taggedValues["eksempel"]?replace("\"", "\"\"")}","${modelElement.taggedValues["alternativtNavn"]?replace("\"", "\"\"")}","${modelElement.taggedValues["lovgrundlag"]?replace("\"", "\"\"")}"
</#list>
<#else>
<#if strictnessMode = StrictnessMode.MODERATE><#-- Output '_MISSING TAG_' if the requested tag is missing in the UML model. -->
<#assign defaultValue = "_MISSING TAG_" >
<#elseif strictnessMode = StrictnessMode.LENIENT><#-- Output empty string if the requested tag is missing in the UML model. -->
<#assign defaultValue = "" >
</#if>
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${(modelElement.taggedValues["definition"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["note"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["eksempel"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["alternativtNavn"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["lovgrundlag"]!defaultValue)?replace('"', '""')}"
</#list>
</#if>