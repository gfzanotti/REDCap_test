# Requisiti del Progetto Software: Generazione Automatica di Instrument da Survey REDCap
![Badge](https://img.shields.io/badge/Ilaria_Gian-rev_0.1-red)

## 1. ***Introduzione***    ![Badge](https://img.shields.io/badge/Gian-OK!-green)

Il presente documento definisce i requisiti per lo sviluppo di un applicativo software in grado di interfacciarsi con le API di REDCap, acquisire dati da survey relative a preferenze e necessità cliniche e generare automaticamente gli **instrument** di un progetto REDCap, come ad esempio: strumenti da personalizzare in base alle risposte della survey per la **registrazione** di un paziente nello studio, la **visita** del paziente, il **laboratorio**, i **questionari di qualità di vita**  e strumenti standard predefiniti nel caso di trial clinici per **storia medica**, **farmaci concomitanti**, **fine trattamento**, **fine studio**, **eventi avversi**.

L’obiettivo è automatizzare la costruzione degli studi di ricerca che si formalizzano in eCRF [electronic Case Report Form] di progetti di REDCap, riducendo errori manuali e migliorando la creazione dei campi e la tracciabilità dei dati.  

Le tecniche utilizzate per la raccolta dei requisiti sono state:
- <u>*intervista*</u> direttamente agli stakeholder (REDCap manager / product owner) e invito agli stessi a inserire "storie" e "scenari" all'interno del kanban di github per organizzare il product backlog;
- <u>*Etnografia*</u>, osservando le attività svolte "AS-IS". In realtà è da questo tipo di osservazione che è partita l'idea di questo progetto.  

Praticamente il REDCap manager riceveva una descrizione in formato "word" di come doveva essere impostato uno studio clinico ed all'interno di essa c'era un lungo elenco di esami di laboratorio, per ogni singolo esame veniva codificata una variabile in una tabella excel, ripetuta 5 volte con desinenze diverse e su ognuna di esse venivano eseguite alcune formule che creavano codice SQL dopodichè ognuna di queste celle veniva copiata-incollata come valore in uno specifico attributo di un campo di un oggetto (Strumento) in REDCap. In sostanza si producevano centinaia di copia-incolla da celle excel a campi di un progetto REDCap con la logica conseguenza di avere errori posizionali, disattenzione per il lavoro estremamente ripetitivo, errore procedurale (non selezionare bene i campi da copiare), etc...  .
L'attività sopra descritta in realta pareva adattarsi bene ad essere automatizzata con notevole risparmio di tempo ma soprattutto di efficacia.  
<ul>
	<li><a href="./UML/UseCase_eCRT_AS_IS.svg">Diagramma Use-Case della procedure AS-IS.</a>
	<li><a href="./UML/UseCase_eCRT_TO_BE.svg">Diagramma Use-Case della procedure TO-BE.</a>
</ul>
---

## 2. ***Obiettivi del Progetto***    ![Badge](https://img.shields.io/badge/Gian-OK!-green)

* Integrare le API di REDCap per l’importazione dei dati delle survey.
* Interpretare le liste di preferenze e regole cliniche definite dagli utenti.
* Generare automaticamente i vari instrument di REDCap secondo logiche predefinite:

  * Registrazione
  * Visita
  * Laboratorio
  * Questionari di qualità di vita
   
* Gestire la configurazione dei ruoli e delle figure coinvolte nel progetto.
* Fornire report e log sulle azioni eseguite e sugli strumenti generati.

---

## 3. ***Requisiti Funzionali (RF)***

### RF1 – Connessione a REDCap

* L’applicativo deve autenticarsi alle API di REDCap tramite **token sicuro**.

* Deve supportare la lettura di **tipi di survey differenti**, con strutture variabili di campi.

### RF2 – Importazione dei dati

* Deve estrarre le **liste di preferenze** dai dati delle survey tramite chiamate REST alle API REDCap.
* Deve validare la correttezza dei dati ricevuti (es. campi obbligatori presenti, valori coerenti). Ad esempio nel caso dell'oggetto "INSTRUMENT" di laboratorio, gli acronimi che identificano le variabili degli esami devono già essere configurati in un database aziendale, altrimenti devono essere valutati ed eventualmente inseriti manualmente.
* Il sistema deve gestire formati JSON e CSV restituendo messaggi di errore in caso di struttura inattesa.

### RF3 – Configurazione delle regole di costruzione

* Deve essere possibile definire regole di mappatura (es. variabile -> "Field" di REDCap oppure "First Row").
* Le regole devono essere salvabili come template per progetti futuri.
* L'utente deve poter scegliere il tipo di strumento da generare (Registrazione, Laboratorio, Visita, etc...).
* Il sistema deve consentire di impostare **condizioni logiche** (es. se x < y esegui/scegli z) negli strumenti da personalizzare in base alle risposte della survey
  * **Instrument di laboratorio**
  * **Instrument della visita**
  * **Instrument dei questionari**
* La logica di generazione deve supportare scenari/tipologie di campi diversi basati sulle specifiche e necessità dei Responsabili di ricerca.
* Al contempo ci saranno anche strumenti standard che devono essere obbligatoriamente inseriti nel caso di eCRF per trial clinici:
  * **Instrument della storia medica** 
  * **Instrument dei farmaci concomitanti** 
  * **Instrument del fine trattamento** 
  * **Instrument di fine studio**
  * **Instrument degli eventi avversi**
* Deve poter aggiornare strumenti esistenti se necessario, senza duplicare dati.


### RF4 – Generazione automatica di instrument

* Deve creare i diversi instrument del progetto REDCap in base ai dati importati:
  * **Instrument di laboratorio**  ![Badge](https://img.shields.io/badge/LabData(Survey)-Must-blue)
  * **Instrument della visita**   ![Badge](https://img.shields.io/badge/LabData(Survey)-Could-yellow)
  * **Instrument dei questionari**   ![Badge](https://img.shields.io/badge/LabData(Survey)-Could-yellow)
  * **Instrument della storia medica** (obbligatorio e standard quando la eCRF è per un trial clinico)   ![Badge](https://img.shields.io/badge/Introduzione(Survey)-Must-blue)
  * **Instrument dei farmaci concomitanti** (obbligatorio e standard quando la eCRF è per un trial clinico)   ![Badge](https://img.shields.io/badge/Introduzione(Survey)-Must-blue)
  * **Instrument del fine trattamento** (obbligatorio e standard quando la eCRF è per un trial clinico)   ![Badge](https://img.shields.io/badge/Introduzione(Survey)-Must-blue)
  * **Instrument di fine studio** (obbligatorio e standard quando la eCRF è per un trial clinico)   ![Badge](https://img.shields.io/badge/Introduzione(Survey)-Must-blue)
  * **Instrument degli eventi avversi** (obbligatorio e standard quando la eCRF è per un trial clinico)   ![Badge](https://img.shields.io/badge/Introduzione(Survey)-Must-blue)
* La logica di generazione deve supportare scenari/tipologie di campi diversi basati sulle specifiche e necessità dei Responsabili di ricerca.
* Per gli strumenti standard prevedere facili aggiornamenti degli standard.
* Deve poter aggiornare strumenti esistenti se necessario, senza duplicare dati.

### RF5 – Gestione delle figure coinvolte

* Deve prevedere un sistema di configurazione dei ruoli (data_admin, data_manager, data_export) e delle responsabilità.

### RF6 – Report e log

* Deve generare report sull’esito delle operazioni di creazione/aggiornamento degli instrument.
* Deve salvare log dettagliati di eventuali errori o incongruenze.

### RF7 – Gestione di nuovi esami di laboratorio

* Confrontare gli esami richiesti dalla survey con quelli presenti nel Dizionario e aggiungere gli esami non presenti ad oggi nel Dizionario.
  
---

## 4. ***Requisiti Non Funzionali (RNF)***

* **Sicurezza**: gestione sicura del token REDCap, cifratura dei dati sensibili.
* **Affidabilità**: l’applicativo deve essere in grado di gestire errori di rete o dati incompleti.
* **Scalabilità**: supporto per progetti con un alto numero di survey o strumenti.
* **Manutenibilità**: il codice deve essere documentato e modulare.
* **Portabilità**: l’applicativo deve poter girare su sistemi Windows e Linux.
* **Usabilità**: l’interfaccia deve essere intuitiva, con wizard per guidare la generazione degli INSTRUMENTS.

---

## 5. ***Utenti e Ruoli***

* **Referente REDCap (REDCap Manager)**: configura token API, autorizzazioni e progetti; definisce regole di mapping tra survey e instrument, controlla la correttezza dei dati generati.
* **Tecnico / sviluppatore**: manutenzione e aggiornamenti dell’applicativo.
* **Principal investigator** o **Study coordinator**: utilizza gli instrument generati per inserire o consultare dati clinici.
* **Data scientist**: estrai i dati clinici inseriti negli instrument generati.

---

## 6. ***Vincoli***

* L’applicativo deve utilizzare **solo API ufficiali di REDCap**.
* Tutti i dati sensibili devono essere trattati secondo le normative **GDPR** o regolamenti locali.
* Deve essere possibile eseguire l’applicativo sia **in batch** sia tramite **interfaccia grafica minimale**.
* **Dati finali** per la creazione automatica degli INSTRUMENTS devono essere resi disponibili in formato CSV, XML o caricati sul progetto REDCap direttamente tramite API.
* Rendere disponibili anche i **log finali** che documentano lo sviluppo degli INSTRUMENTS
* **VERIFICARE** che la struttura sia conforme ai requisiti REDCap (nomi campo, codifica, titpi di dati, ...).
* **VALIDARE** con il PI o il Responsabile REDCap le strutture dati generate ed importate nella eCRF. 





