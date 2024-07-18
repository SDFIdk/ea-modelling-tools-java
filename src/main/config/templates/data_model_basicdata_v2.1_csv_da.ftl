GUID,UML-NAVN,NAMESPACE,TYPE,STEREOTYPE,Grunddata2::ModelElement::prefLabel (da),Grunddata2::ModelElement::definition (da),Grunddata2::ModelElement::comment (da),Grunddata2::ModelElement::applicationNote (da),Grunddata2::ModelElement::example (da),Grunddata2::ModelElement::altLabel (da),Grunddata2::ModelElement::deprecatedLabel (da),Grunddata2::ModelElement::source,Grunddata2::ModelElement::legalSource,Grunddata2::ModelElement::URI,Grunddata2::DKObjekttype::historikmodel,Grunddata2::DKKodeliste::vokabularium
<#if strictnessMode = StrictnessMode.STRICT><#-- Fail if a tagged value is missing in the UML model. -->
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${modelElement.fqStereotype}","${modelElement.taggedValues["Grunddata2::ModelElement::prefLabel (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["Grunddata2::ModelElement::definition (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["Grunddata2::ModelElement::comment (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["Grunddata2::ModelElement::applicationNote (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["Grunddata2::ModelElement::example (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["Grunddata2::ModelElement::altLabel (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["Grunddata2::ModelElement::deprecatedLabel (da)"]?replace("\"", "\"\"")}","${modelElement.taggedValues["Grunddata2::ModelElement::source"]?replace("\"", "\"\"")}","${modelElement.taggedValues["Grunddata2::ModelElement::legalSource"]?replace("\"", "\"\"")}","${modelElement.taggedValues["Grunddata2::ModelElement::URI"]?replace("\"", "\"\"")}","<#if modelElement.fqStereotype = "Grunddata2::DKObjekttype">${modelElement.taggedValues["Grunddata2::DKObjekttype::historikmodel"]?replace("\"", "\"\"")}</#if>","<#if modelElement.fqStereotype = "Grunddata2::DKKodeliste">${modelElement.taggedValues["Grunddata2::DKKodeliste::vokabularium"]?replace("\"", "\"\"")}</#if>"
</#list>
<#else>
<#if strictnessMode = StrictnessMode.MODERATE><#-- Output '_MISSING TAG_' if the requested tag is missing in the UML model. -->
<#assign defaultValue = "_MISSING TAG_" >
<#elseif strictnessMode = StrictnessMode.LENIENT><#-- Output empty string if the requested tag is missing in the UML model. -->
<#assign defaultValue = "" >
</#if>
<#list modelElements as modelElement>
"${modelElement.eaGuid}","${modelElement.umlName}","${modelElement.namespaceName}","${modelElement.umlModelElementType.name()}","${modelElement.fqStereotype}","${(modelElement.taggedValues["Grunddata2::ModelElement::prefLabel (da)"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["Grunddata2::ModelElement::definition (da)"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["Grunddata2::ModelElement::comment (da)"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["Grunddata2::ModelElement::applicationNote (da)"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["Grunddata2::ModelElement::example (da)"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["Grunddata2::ModelElement::altLabel (da)"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["Grunddata2::ModelElement::deprecatedLabel (da)"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["Grunddata2::ModelElement::source"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["Grunddata2::ModelElement::legalSource"]!defaultValue)?replace('"', '""')}","${(modelElement.taggedValues["Grunddata2::ModelElement::URI"]!defaultValue)?replace('"', '""')}","<#if modelElement.fqStereotype = "Grunddata2::DKObjekttype">${(modelElement.taggedValues["Grunddata2::DKObjekttype::historikmodel"]!defaultValue)?replace('"', '""')}</#if>","<#if modelElement.fqStereotype = "Grunddata2::DKKodeliste">${(modelElement.taggedValues["Grunddata2::DKKodeliste::vokabularium"]!defaultValue)?replace('"', '""')}</#if>"
</#list>
</#if>