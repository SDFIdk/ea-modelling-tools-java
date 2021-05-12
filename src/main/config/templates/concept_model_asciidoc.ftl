:icons: font
:imagesdir: billeder
:title: ${conceptModel.name}
:titletext: {Title}
:encoding: utf-8
:toc:
:toc-placement: left
:toclevels: 2
:numbered:
:sectanchors:
= Begrebsmodel: ${conceptModel.name}

.Læsevejledning
****
Dette dokument består af tre dele. Den første del introducerer 
modellen. Den anden del forklarer begreberne ved hjælp af diagrammer 
og en beskrivende tekst per diagram. Den sidste del er en liste over 
modellens begreber samt begrebernes termer, definition og eventuelle 
kommentarer og eksempler.

Modellens begreber er relateret til hinanden, og pilene på 
diagrammerne angiver disse relationer. Diagrammerne bør læses som små 
sætninger, der er sammensat af term + tekst på pilen + term. 
Diagrammerne fortæller de fortællinger der er vigtige i forhold til 
emnet. Dvs. at der kan være mange flere sammenhænge end der nævnes 
her. Diagrammerne har understøttet arbejdet med at lave 
begrebsmodellen, og de skulle gerne øge læserens forståelse for 
begreberne og deres indbyrdes relationer. Diagrammerne er suppleret 
med en beskrivende tekst. Der er ikke noget naturligt eller 
foretrukket sted for at begynde at "læse" et begrebsdiagram, men 
teksten foreslår dog en slags læserækkefølge, dvs. et forsøg på at 
guide læseren gennem diagrammet.

Indlånte begreber angives på diagrammer med følgende symbol: TODO. 
Indlånte begrebers beskrivelse er af princip taget ordret fra kilden. 
De eneste modifikationer er tilpasninger der gør at definitioner 
følger anbefalingerne om lille begyndelsesbogstav, ingen punktum til 
sidst, osv., samt oversættelser til dansk eller engelsk. Nogle 
begreber kan være forbundet til en relation med en stiplet linje 
fordi de er modelleret som 
https://www.uml-diagrams.org/association.html[UML-associeringsklasser].
 Der er tale om 
"https://www.brcommunity.com/articles.php?id=c065[objektiviseringer]", 
hvilket i denne kontekst betyder at et verbum-begreb (f.eks. 
"registreres i") som helhed bliver til et substantiv-begreb (f.eks. 
"registrering"). Det giver igen mulighed for at tilføje relationer 
til andre begreber.
****

== Introduktion

${conceptModel.description}

== Diagrammer

<#list diagrams as diagram>
=== ${diagram.name}

${diagram.notesAsAsciidoc}

</#list>

== Begrebsliste

<#list concepts as concept>
[#${concept.uuid}]
=== ${concept.preferredTerms.da}

[cols="1,3,3"]
|===
|_Foretrukken term_
|*${concept.preferredTerms.da}*
|*${concept.preferredTerms.en}*

|_Definition_
|${concept.definitions.da}
|${concept.definitions.en}
|===

</#list>

