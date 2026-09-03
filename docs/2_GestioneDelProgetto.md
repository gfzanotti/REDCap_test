# Documento di Gestione del Progetto – eCRF Generator con REDCap

## Introduzione

Questo documento descrive in modo dettagliato la **gestione del progetto di ingegneria del software** relativo allo sviluppo di un sistema Java per la generazione automatica di eCRF (electronic Case Report Form) compatibili con REDCap. Il contenuto è un'estensione e un approfondimento delle informazioni presenti nel piano di progetto originale REDCap Project Plan, con particolare attenzione a tre aspetti fondamentali della gestione di progetto:

* Software Life Cycle
* Configuration Management
* People Management and Team Organization

L'obiettivo è fornire una visione strutturata, chiara e coerente con le best practice dell'ingegneria del software, adattata al contesto clinico-regolatorio dell'INT.

---

## 1. Software Life Cycle

### 1.1 Modello di ciclo di vita adottato

Il progetto adotta un **modello di ciclo di vita agile a sviluppo incrementale**, con elementi di prototipazione rapida, ispirato al framework **SCRUM**, in quanto:

* i requisiti non sono completamente definibili all'inizio;
* è necessario un continuo confronto con stakeholder clinici;
* le eCRF presentano una forte variabilità strutturale.

Il ciclo di vita è suddiviso in **iterazioni (Sprint)** di durata compresa tra 10 e 20 giorni, ciascuna delle quali produce un incremento funzionante del sistema.

### 1.2 Fasi del ciclo di vita

#### a) Ingegneria dei requisiti

* Analisi della procedura PRO-G-14 dell'INT;
* Analisi dei moduli standard REDCap già in uso;
* Definizione dei requisiti funzionali tramite survey REDCap strutturata (sostituisce il precedente processo con modelli "Word");
* Raccolta dei requisiti non funzionali (performance, sicurezza, compliance GDPR e GCP).

I requisiti vengono raffinati progressivamente attraverso backlog di prodotto e backlog di sprint.

#### b) Progettazione

* Definizione del **metamodello eCRF**, basato sugli oggetti REDCap;
* Progettazione architetturale modulare;
* Utilizzo di diagrammi UML (classi, sequenze). Soprattutto ho progettatto il package *mi.istitutotumori.e_crf* e realizzato automaticamente la struttura del codice relativo con **Papyrus** che ho però tenuto su una configurazione eclipse a parte dati i continui problemi sull error.log e di funzionalità dovuti alla compatibilità con versioni troppo aggiornate di eclipse e relativi Plug-ins.
* Progettazione orientata alla riusabilità dei moduli standard.

#### c) Implementazione
* ###### c.1 Prototipazione Rapida (Python)
  * Sviluppo di un prototipo dello strumento “Clinical Laboratory Data” in Python per validare l’approccio con gli stakeholder.
  * Feedback early-stage per affinare requisiti e interfaccia.
  
* ###### c.2 Sviluppo Incrementale (Java + SCRUM)
	* Sviluppo in linguaggio Java con paradigma Object-Oriented;
	* Adozione di standard di codifica condivisi;
	* Sviluppo incrementale dei moduli eCRF;  
Si è cercato di rispettare quanto scritto nel documento *1_REDCapProjectPlan.md* applicando una metodologia *agile* **SRUM** facendo cicli di sviluppo.  
Non si è riusciti a tenere un documento di *BACKLOG* ordinato ma sono riuscito a fare diversi cicli di **SPRINT** applicando il più possibile tecniche di rifattorizzazione alla fine di ogni ciclo usando anche gli strumenti automatici sul controllo della qualità del codice come *UCDetector*, *PMD* e *SonarLint*. Si è cercato di tenere la traccia di tutta l'attività con lo strumento *kanban project* in github, col tempo sono diventato anche più attento a dettagliare le richieste (non è stato facile coinvolgere gli stakeholder a tenere un BACKLOG aggiornato le **"user story"**).  
Solitamente facevo un aggiornamento una volta a settimana o ogni 2 e tendenzialmente ogni 2/3 settimane rilasciavo una nuova versione dell'applicativo. In tutto ho rilasciato 5 versioni dalla 0.1.0 alla 0.1.5.


#### d) Verifica e validazione

* Test di unità sui singoli moduli;
* Test di integrazione progressivi;
* Verifica della correttezza dei file .csv generati o degli import/export tramite API di REDCap;
* Test di accettazione con il personale INT.

#### e) Rilascio e manutenzione

* Rilascio di versioni incrementali;
* Utilizzo in uno studio reale (es. “Reprint”) come caso pilota.
* Monitoraggio delle performance e raccolta feedback utente.
* Manutenzione correttiva ed evolutiva gestita tramite backlog.
* Supporto post-deploy e integrazione di nuovi strumenti standard.
* Aggiornamenti in risposta a cambiamenti normativi o esigenze cliniche.

### 1.3 Milestone di progetto

* M1: Definizione del metamodello eCRF
* M2: Primo incremento funzionale (Clinical Laboratory Data)
* M3: Gestione template e strumenti standard
* M4: Validazione e reportistica tramite versioning
* M5: Versione pilota operativa

---

## 2. Configuration Management

### 2.1 Obiettivi del Configuration Management

Il Configuration Management ha l'obiettivo di:

* garantire tracciabilità e controllo delle modifiche;
* mantenere la coerenza tra codice, documentazione e configurazioni;
* supportare la collaborazione tra i membri del team;
* soddisfare i requisiti di audit e compliance.

### 2.2 Elementi di configurazione (Configuration Items)

Sono considerati elementi di configurazione:

* Codice sorgente Java;
* Script di prototipazione Python;
* File csv/json REDCap generati;
* Documentazione tecnica e di progetto;
* Diagrammi UML;
* File di configurazione e template eCRF.

### 2.3 Sistema di controllo di versione

* Utilizzo di **Git** con repository centralizzato (*GitHub*);
* Adozione di branching model semplificato:

  * main: versione stabile
  * develop: integrazione continua
  * feature/*: sviluppo funzionalità  
  
Ogni commit dovrebbe essere commentato in modo chiaro e riferibile a uno specifico requisito o task.  


In più di un occasione si sono creati branch secondari più che altro per provare a separare attività nettamente differenti e imparare l'uso della tecnologia. Nella attività quotidiana, essendo lo sviluppatore del progetto uno solo, utilizzare troppo il branching portava a confusione in quanto ci si doveva ricordare, su eclipse, di selezionare ogni volta il branch corretto per quelle specifiche modifiche su un particolare package o classe!  

L'attività di *Configuration Management* svolta su github ha portato alla creazione e gestione di 15 items circa sul *project plan "kanban"*, una quarantina di commits (in alcuni casi di sequenza di commits semplici si è proceduta alla attività di merging), sono state effettuate e gestite circa 9 pull requests ed in 4 occasioni sono stati attivati degli issues.

### 2.4 Gestione delle modifiche

* Le modifiche sono introdotte tramite backlog SCRUM;
* Ogni modifica viene valutata in termini di impatto;
* Le modifiche approvate sono pianificate nello sprint successivo;
* Si cerca di agevolare la tracciabilità tra requisiti, codice e versioni.
* Utilizzo di project board su github.

### 2.5 Gestione delle versioni

* Versionamento semantico (major.minor.patch);
* Ogni release è accompagnata da note di rilascio;
* Archiviazione delle versioni consegnate.

---

## 3. People Management and Team Organization

### 3.1 Struttura organizzativa

Il progetto adotta una **struttura di team snella**, adatta a un contesto accademico-clinico:

* Supervisore di progetto
* Analista dei requisiti clinici
* Sviluppatore software
* Stakeholder clinici (personale INT)

### 3.2 Ruoli e responsabilità

| Ruolo                     | Nome             | Responsabilità                                                                 |
|---------------------------|------------------|--------------------------------------------------------------------------------|
| Supervisore               | Paolo Baili      | Coordinamento generale, approvazione delle milestone, gestione dei rischi.     |
| Responsabile Funzionale   | Ilaria Cavallo   | Definizione requisiti, validazione funzionale, interfaccia con utenti clinici. |
| Sviluppatore              | Gianfranco Zanotti | Sviluppo Java, integrazione REDCap, testing tecnico.                           |
| Amministratore REDCap     | Personale INT    | Creazione survey, gestione API, supporto tecnico REDCap.                       |
| Tester/Validatore         | Team misto INT   | Test di accettazione, validazione regolatoria, feedback utente.                |

---

### 3.3 Comunicazione e coordinamento

* Riunioni periodiche di sprint review;
* Comunicazione diretta e informale;
* Documentazione condivisa nel repository;
* Feedback continuo integrato nel processo agile.

### 3.4 Gestione delle competenze

* Formazione su REDCap e standard clinici;
* Apprendimento progressivo delle API REDCap;
* Condivisione della conoscenza tramite documentazione.

---

## Conclusioni

La gestione del progetto è impostata per garantire **flessibilità, qualità e tracciabilità**, elementi fondamentali in un contesto clinico regolato. L'integrazione di un ciclo di vita agile, un solido configuration management e una chiara organizzazione del team consente di ridurre i rischi e massimizzare il valore prodotto per l'INT.

*Documento redatto in accordo con la procedura PRO-G-14 di INT e i principi di ingegneria del software agile-incrementale.*
Revisione: Gianfranco Zanotti, Paolo Baili
Data: 22.01.2026
