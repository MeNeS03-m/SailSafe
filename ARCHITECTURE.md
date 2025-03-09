<h1> Arkitektur </h1>

**SailSafe** sitt hovedmål er å tilby brukere presise vær- og havprognoser. For å oppnå en godt strukturert, vedlikeholdbar og skalerbar kodebase, har vi implementert prinsipper innen objektorientert programmering samt design patterns som **MVVM** (Model-View-ViewModel). Dette sikrer at alle aspekter av løsningen er enkle å forstå, vedlikeholde og videreutvikle.

Det første vi gjorde i utviklingsfasen var å velge oss en apparkitektur. Teammedlemmene var på forhånd kjent med MVVM arkitekturen, og da falt det naturlig å velge denne. Denne arkitekturmodellen skiller datapresentasjonslaget (Data layer) fra kjernevirksomhetslogikken i applikasjonen.

<h3>De ulike kodelagene er følgende:</h3> 

**Model:** Håndterer applikasjonens data og forretningslogikk. Modellen henter og lagrer data fra de ulike APIene som vi bruker. Dette laget er også kjent som Datalaget (Data Layer).

**View:** Representerer brukergrensesnittet og fanger brukerens handlinger. View er ansvarlig for å presentere dataen fra Model til brukeren. Dette laget er også kjent som UI laget (UI Layer).

**ViewModel:** Ansvarlig for å håndtere logikk som binder data fra Model til View. Komponenten sikrer at data- og forretningslogikk er adskilt fra brukergrensesnittet. Eksponerer observerbare data som UI kan lytte til og oppdatere seg automatisk.

<h3>Integrering av OOP-prinsipper og MVVM-arkitektur</h3>
For å ivareta MVVM-arkitekturen sørget vi for at hver komponent i MVVM-mønsteret (Model, View, ViewModel) håndterte sitt spesifikke ansvarsområde. Dette ser man i inndelingen/strukturen av prosjektet vårt.

**«Data»** mappen er bygd opp av flere mapper, et til hvert API. Innad i mappene finner du datakilden til hvert API, samt et Repository knyttet til API-et. 

**Repository** er ansvarlig for å eksponere data til appen, abstraherer bort datakilden fra resten av appen og håndterer businesslogikk/forretningslogikk. Dette er mappen som representerer Model delen av MVVM.

Mappen **«Model»** følger samme struktur som **«Data»** mappen. Denne benyttes til å lage dataklasser fra endepunktet/APIet ved bruk av tjenesten «Kotlin data class file from JSON».  Disse har vært essensielle i prosjektet for å representere, lagre, manipulere og formatere data. En strukturert og type-sikker måte å håndtere data på.  

Aspektet **«View»** av MVVM forekommer i mappen **«UI»**. Det er denne mappen som beskriver brukergrensesnittet. I denne mappen forekommer flere mapper, som inneholder hver sin fil. Disse filene er spesifikke til hver skjerm i appen vår. Det er her vi håndterer UI logikken i appen som f.eks. knappetrykk, navigasjoner og andre UI-hendelser. 

I tillegg trengs en **ViewModel**. Dette er limet mellom View og Model i MVVM. Vi har valgt å benytte oss av en felles ViewModel i prosjektet. En av hovedgrunnene til dette valget var på grunn av mye gjenbruk av logikk og data i prosjektet. Å ha en felles ViewModel som håndterte dette gjorde at vi unngikk å duplisere kode, som resulterte i en mer vedlikeholdbar kodebase. Denne tilnærmingen av en ViewModel har også ført til en effektiv ressursbruk i form av antallet API kall. Denne sørger for å laste dataene en gang og cache disse, i stedet for å gjøre flere nettverksforespørsler for hver skjerm. 

Vi var nøye med å følge flere OOP (Objekt-orientert programmering) prinsipper. To i hovedfokus var å sikre høy kohesjon og lav kobling. MVVM-arkitekturen har oppmuntret til dette ved å definere et klart skille og ansvarsområde.

<h3>Høy kohesjon:</h3>

 Prinsippet om at elementer i en komponent skal ha et moderat ansvar og utføre begrenset antall oppgaver. 

Model delen håndterer kun data og forretningslogikken. Denne har ingen kjennskap til brukergrensesnittet. View fokuserer kun på brukergrensesnitt og presentasjon. Her håndteres ikke forretningslogikk eller direkte databehandling. ViewModel blir bindeleddet mellom disse. Denne håndterer presentasjonslogikken. På denne måten vil hver komponent i arkitekturen ha sitt ansvarsområde.

<h3>Lav kobling:</h3>

Beskriver et design hvor hver del av systemet skal ha minimalt med avhengigheter til andre systemer. 

View vil ikke direkte hente data fra Model, men må gjennom ViewModel. Dette reduserer avhengigheter mellom View og Model, som fører til at vi enkelt kan gjøre endringer i View, uten å påvirke hverken Model eller ViewModel. 

<h2>API Nivå</h2>

SailSafe er utviklet i Kotlin, og bruker Jetpack Compose som UI-rammeverk. Applikasjonen er utviklet for API-nivå 34 (Upside Down Cake). De praktiske betydningene for dette valget er at SailSafe kun er kompatibel med Android-enheter som har Android-versjon 14, som har en kumulativ bruk på 16.3% *"https://apilevels.com"* Bakgrunnen for dette valget er at nye applikasjoner som publiseres på Google Play må være kompatible med Android-versjon 13 eller høyere *"https://developer.android.com/google/play/requirements/target-sdk"*. For å sikre at brukskvaliteten er lik på tvers av Android-enheter er det sørget for at skjermutformingen av de ulike komponentene er relativ til enheten den kjører på. 

<h2>Vedlikehold og videreutvikling</h2>

Utenom det som står ovenfor mtp. API nivå og MVVM-arkitekturen har vi kommentert kodebasen vår grundig. Dette for at nye utviklere skal enkelt kan sette seg inn i funksjonaliteten. Vi har i tillegg sørget for at konseptet "separation of concerns" er tungt vektlagt, for å bevare MVVM og apparkitekturen vår. Dette har bidratt til enkel vedlikehold, og blir et viktig punkt i videreutvikling. I filen UtilityComponents.kt vil du finne kode som blir gjenbrukt samtlige steder. Dette er også en kodeskikk som har resultert i bedre og enklere vedlikehold. Det er derfor anbefalt å bruke denne utviklingstanken hyppig, for å ha en effektiv gjenbruk av kode og logikk. Ved å følge disse få punktene vil SailSafe forbli funksjonell, lett å forbedre, utvide og vedlikeholde.
