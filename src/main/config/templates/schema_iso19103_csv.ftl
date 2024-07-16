GUID,UML-NAVN,NAMESPACE,TYPE,STEREOTYPE,ISO19103::GI_Element::designation,ISO19103::GI_Element::definition,ISO19103::GI_Element::description,ISO19103::GI_Element::IRI
<#if strictnessMode = StrictnessMode.STRICT><#-- Fail if a tagged value is missing in the UML model. -->
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${modelElement.fqStereotype}","${modelElement.taggedValues["ISO19103::GI_Element::designation"]?replace("\"", "\"\"")}","${modelElement.taggedValues["ISO19103::GI_Element::definition"]?replace("\"", "\"\"")}","${modelElement.taggedValues["ISO19103::GI_Element::description"]?replace("\"", "\"\"")}","${modelElement.taggedValues["ISO19103::GI_Element::description"]?replace("\"", "\"\"")}","${modelElement.taggedValues["ISO19103::GI_Element::IRI"]?replace("\"", "\"\"")}"
</#list>
<#else>
<#if strictnessMode = StrictnessMode.MODERATE><#-- Output '_MISSING TAG_' if the requested tag is missing in the UML model. -->
<#assign defaultValue = "_MISSING TAG_" >
<#elseif strictnessMode = StrictnessMode.LENIENT><#-- Output empty string if the requested tag is missing in the UML model. -->
<#assign defaultValue = "" >
</#if>
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${modelElement.fqStereotype}","${modelElement.taggedValues["ISO19103::GI_Element::designation"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["ISO19103::GI_Element::definition"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["ISO19103::GI_Element::description"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["ISO19103::GI_Element::description"]!defaultValue?replace("\"", "\"\"")}","${modelElement.taggedValues["ISO19103::GI_Element::IRI"]!defaultValue?replace("\"", "\"\"")}"
</#list>
</#if>