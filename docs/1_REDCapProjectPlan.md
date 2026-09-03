# REDCap Project Plan

## **1. Introduzione**
###### [Nell'introduzione al piano di progetto, vengono forniti il background e la storia del progetto, insieme ai suoi obiettivi, i risultati del progetto, i nomi delle persone responsabili e una sintesi del progetto.]

![Badge](https://img.shields.io/badge/Paolo-rev-red)
### *Background*  
<p style="font-family:calibri; font-size: 12pt;">La Fondazione IRCCS "Istituto Nazionale dei Tumori" (INT) ha una sua procedura di qualità interna PRO-G-14 in cui vengono dettagliate le modalità di gestione per la creazione di una eCRF (electronic Case Report Form) per la raccolta dati di studi clinici no profit promossi dall'ente stesso. L'applicativo open source utilizzato da INT per creare queste eCRF è Redcap (https://project-redcap.org/).</p>
<p style="font-family:calibri; font-size: 12pt;">All'interno di tale procedura vengono dettagliati gli step che il principal investigator (PI) dello studio deve seguire per chiedere la creazione della eCRF e viene allegato uno schema base in word per la personalizzazione della propria eCRF in base alle caratteristiche dello studio clinico. Poiché alcune delle informazioni richieste sono comuni tra i vari studi clinici, la procedura definisce già le modalità standard di raccolta di tali informazioni e in RedCap sono già disponibili dei moduli da utilizzare.</p>

### *Obiettivi del progetto*
<p style="font-family:calibri; font-size: 12pt;">L'obiettivo di tale progetto è costruire un programma Java che acquisendo le informazioni necessarie per costruire la eCRF di uno studio clinico specifico restituisca un formato dati compatibile da importare in Redcap per la creazione vera e propria della eCRF.</p>

### *Risultati attesi*
<p style="font-family:calibri; font-size: 12pt;">Automazione del processo di creazione della eCRF di studi clinici no
profit dell'INT con una riduzione del 50-70% dei tempi necessari per
tale creazione.</p>

### *Stakeholder e Responsabili di progetto*
+ Paolo Baili - supervisore
+ Ilaria Cavallo - requisiti funzionali
+ Gianfranco Zanotti - sviluppo 

### *Sintesi del progetto*
Attraverso l'utilizzo del linguaggio di programmazione (Object Oriented) Java e della tecnologia Eclipse, Github, UML, REDCap si vuole automatizzare gran parte della procedura di creazione di una eCRF; arrivando a fornire in tempi brevi una interfaccia per la raccolta dei dati clinici ai  medici - ricercatori.

## **2. Modello di processo**
![Badge](https://img.shields.io/badge/Gian-rev_07.01.2026-red)
###### [Nel Capitolo 1 è stato introdotto un semplice modello del ciclo di vita per discutere le varie attività da affrontare in un progetto di sviluppo software. Esistono molte varianti di questo modello di processo, alcune delle quali sono discusse nel Capitolo 3. Per ogni progetto si deve decidere l'esatto modello di processo da seguire: quali attività intraprendere, quali pietre miliari possono essere identificate, come si accerta se tali pietre miliari vengono raggiunte e quali sono i percorsi critici. Diversi tipi di progetti hanno caratteristiche diverse e quindi richiedono modelli di processo diversi.]
Data la natura modulare e diversificata della costruzione delle eCRFs si cercherà di applicare un modello "agile" a **sviluppo incrementale** coinvolgendo continuamente gli stakeholder (utenti/responsabili). In alcune fasi, soprattutto quelle iniziali, si potrebbero anche inframmezzare tecniche di sviluppo a **prototipazione** usando un linguaggio ad alto livello (python) per visualizzare e discutere con gli stakeholder alcune caratteristiche e definire meglio i requisiti. Si cercherà di specializzare il progetto in ottica **SCRUM** caratterizzandolo con:

- iterazioni regolari (**Sprint**) di durata di 10-20 giorni
- incrementi funzionali possibilmente da distribuire al termine di ciascuno Sprint
- feedback continuo con gli stakeholder, integrato nelle procedure SCRUM (Sprint Review, Refinement, etc.)

Questo approccio iterativo-incrementale aiuterà a gestire la complessità e la difficoltà nel definire one-shot tutte le specifiche che in realtà riguardano processi di natura clinica differenti, ridurre i rischi di "andare fuori tema" e ottenere feedback continui con gli stakeholder clinici e tecnici.  
Si identificano le seguenti ***milestone***:
- **M1**: definizione del metamodello eCRF (basato sulla struttura degli oggetti in REDCap)
- **M2**: primo incremento funzionale con generazione di un Instrument tipico, quello degli esami di laboratorio, che è anche quello di maggiore complessità e di cui si esegue un prototipo in python
- **M3**: particolare attenzione alla gestione dei template ed alla riusabilità per gli instruments Standard
- **M4**: validazione dell'attività e reportistica tenuta con l'ausilio di un sistema di controllo delle versioni (github)
- **M5**: versione pilota da prototipo utilizzabile in un progetto reale (Reprint)

Gli elementi e/o i percorsi critici che si possono intuire, oltre alla definizione del metamodello, sono:
- integrazione con le API di REDCap,
- aderenza ai requisiti regolatori (GCP, audit trail, GDPR),
- validazione delle configurazioni generate.


## **3. Organizzazione del progetto**
###### [Il rapporto del progetto con altri enti e l'organizzazione del progetto stesso sono trattati in questa rubrica. Il progetto avrà una relazione con l\'organizzazione utente, l\'organizzazione madre ed eventualmente con altre organizzazioni.  I potenziali utenti saranno, di volta in volta, coinvolti nel progetto. Il piano del progetto deve indicare quali informazioni, servizi, risorse e strutture devono essere fornite dagli utenti e quando devono essere forniti. All\'interno del team di progetto possono essere identificati vari ruoli: project manager, tester, programmatore, analista, ecc. È necessario delineare chiaramente questi ruoli e identificare le responsabilità di ciascuno di essi. Se ci sono lacune nelle conoscenze richieste per ricoprire uno di questi ruoli, è necessario identificare la formazione e l\'istruzione necessarie per colmare queste lacune. Diverse forme di organizzazione del team sono discusse nel Capitolo 5.]

![Badge](https://img.shields.io/badge/Paolo-rev-red)
L'INT agisce come ente appaltatore del progetto Java e il personale dell'INT che attualmente crea le eCRF in Redcap per gli studi clinici è strettamente coinvolto nella ideazione e validazione del progetto stesso.
Nell'analisi della procedura PRO-G-14 dell'INT fin dal principio si è riconosciuto nell'utilizzo del file Word per la stesura della eCRF uno strumento non standardizzato che poco facilita la definizione precisa di quali sono le informazioni necessarie in input per il progetto Java.
Per questo motivo si è deciso di abbandonare Word e di sostituirlo con una survey sempre in Redcap con domande e risposte predefinite che deve servire come base per la definizione dei parametri in ingresso del progetto Java. La survey verrà costruita dal personale INT parallelamente al progetto Java a suo uso e consumo.
L'organizzazione dei lavori avverrà per moduli separati in base alle singole sezioni necessarie per la creazione della eCRF. Ogni modulo sarà uno strumento da creare in RedCap tra questi:

- Registration and criteria (inclusion/exclusion)
- Randomization form
- Medical History
- Concomitant medications
- Clinical Laboratory data
- Radiological assessment
- Quality of Life QoL (among a list of already available questionnaires)
- Adverse Events and SAE
- End of study
- Monitor

## **4. Standard, linee guida, procedure**
###### [I progetti software sono grandi progetti. Di solito, molte persone sono coinvolte. Occorre quindi una forte disciplina di lavoro, in cui ogni persona coinvolta segua gli standard, le linee guida e le procedure concordate. Oltre ad essere dichiarati sulla carta, molti di questi possono essere supportati o applicati da strumenti. Di estrema importanza sono gli accordi chiari sulla documentazione: quando consegnare la documentazione, come valutare la qualità della documentazione, come garantire l\'aggiornamento della documentazione? In larga misura, questi standard e procedure saranno descritti in documenti separati, come il Piano di controllo della configurazione o il Piano di garanzia della qualità.]

![Badge](https://img.shields.io/badge/Paolo-rev-red)
L'intero progetto deve seguire le regole imposte da Redcap per la preparazione dell'xml della eCRF da importare. In estrema sintesi le linee generali su come funziona Redcap sono queste:

- Per accedere ai dati inseriti in Redcap (della survey con le informazioni necessarie per la eCRF) si può procedere accedendo direttamente al database oppure da API autorizzate dall'amministratore di Redcap
- Si possono dividere le raccolte dati in sezioni diverse chiamati "Strumenti". Nel nostro caso saranno i moduli che verranno creati
- All'interno di ogni Strumento si possono raccogliere dati in modi diversi: campi numerici, campi data, campi testuali, menù a tendina, menù a risposta multipla, campi calcolati, campi frutto di query sul database interno, ecc...
- All'interno di ogni Strumento si possono visualizzare i campi in modo diverso usando anche tag html e si può inoltre condizionare la visualizzazione di talune variabili in funzione a valori di variabili raccolte precedentemente (anche in strumenti diversi)

## **5. Attività di gestione**
###### [Sono guidate da obiettivi e priorità fissati per il progetto. Ad esempio, la direzione dovrà presentare relazioni periodiche sullo stato e sullo stato di avanzamento del progetto. Dovrà anche seguire alcune priorità nel bilanciamento di requisiti, tempi e costi.]

![Badge](https://img.shields.io/badge/Paolo-rev-red)
Il progetto Java acquisirà via API i dati raccolti dalla survey di Redcap con le domande base necessarie a identificare cosa deve raccogliere la eCRF. La survey tendenzialmente sarà formata da una serie di domande a risposta fissa Si/No in base alla quale verranno inseriti (o non inseriti) specifici strumenti o specifiche parti di uno strumento. Ad esempio:
- La domanda della survey "Nella eCRF deve essere presente la raccolta delle concomitant medications?" con risposta affermativa inserirà nell'xml un intero strumento standard già disponibile in precedenti eCRF preparate
- La domanda della survey "Nella eCRF deve essere presente la raccolta dell'effettuazione del test di gravidanza?" con risposta affermativa comporterà l'inserimento di una domanda specifica dello strumento "Clinical laboratory data"

## **6. Rischi**
###### [I potenziali rischi devono essere identificati il prima possibile. Ci saranno sempre dei rischi: l\'hardware potrebbe non essere consegnato in tempo, il personale qualificato potrebbe non essere disponibile quando necessario, le informazioni critiche potrebbero mancare quando è necessario e così via. È piuttosto ingenuo supporre che un progetto di sviluppo software funzioni sempre senza intoppi. Anche in campi consolidati, come quello delle costruzioni, c'è sempre qualcosa che non va. Si dovrebbe diagnosticare precocemente i rischi di un progetto software e fornire misure per affrontarli; vedere anche il capitolo 8. Più incerti sono i vari aspetti del progetto, maggiori sono i rischi.]

![Badge](https://img.shields.io/badge/Paolo-rev-red)
I rischi sono limitati in quanto il personale INT coinvolto nel progetto è amministratore del Redcap e perciò non sono previsti problemi di accesso al database, alle API e alla creazione della survey. Inoltre la survey sarà creata per rendere più semplice la compilazione da parte dei principal investigator ma soprattutto la creazione del progetto Java la cui gestione poi passerà direttamente a INT.

## **7. Personale**
###### [In momenti diversi, il progetto richiederà diverse quantità di personale, con competenze diverse. L'inizio, la durata, l'importo e la competenza delle categorie di personale sono elencati in questa voce.]

Oltre allo studente che preparerà il progetto Java, in INT sono coinvolte due persone amministratori di Redcap e attualmente coinvolti nelle creazione delle eCRF degli studi clinici.

## **8. Metodi e tecniche**
![Badge](https://img.shields.io/badge/Gian-OK-green)
###### [In questa sezione vengono forniti i metodi e le tecniche da utilizzare durante l'ingegneria dei requisiti, la progettazione, l'implementazione e le prove. In genere, qui viene descritto anche il modo in cui viene gestito il controllo della versione e della configurazione per i componenti software. Gran parte della documentazione tecnica sarà prodotta durante queste fasi. Si deve quindi precisare come sarà curata questa documentazione.<br> Viene descritto l'ambiente di prova e le apparecchiature di prova necessari. Durante il test, viene normalmente esercitata una pressione considerevole sull'apparecchiatura di test. Pertanto, questa attività deve essere pianificata con attenzione. L'ordine in cui i componenti vengono integrati e testati deve essere indicato esplicitamente. Inoltre, devono essere indicate le procedure da seguire durante i test di accettazione, ovvero i test sotto la supervisione dell'utente. I test sono discussi nel Capitolo 13.]

### Ingegneria dei Requisiti
Lo scopo è definire i requisiti per lo sviluppo di un applicativo software in grado di interfacciarsi con le API di REDCap, acquisire dati da survey relative a preferenze e necessità cliniche e generare automaticamente gli ***instrument*** di un progetto REDCap.<br> L’obiettivo è automatizzare la costruzione degli studi di ricerca che si formalizzano in eCRF [electronic Case Report Form] di progetti di REDCap, riducendo errori manuali e migliorando la creazione dei campi e la tracciabilità dei dati. <br> La modalità di raccolta dei requisiti , della definizione e organizzazione degli stessi, deelle figure coinvolte e dei vincoli di dominoi sono specificari nel documento [**"Requisiti del Progetto Software: Generazione Automatica di Instrument da Survey REDCap"**](./3_Requisiti.md)  
### Progettazione
Durante la fase di progettazione si è cercato di definire una sorta di meta-modello di partenza della struttura dati da replicare e/o costruire. Si è preso ad esempio la struttura dati utilizzata dall'applicativo REDCap che ha una gerarchia simile:
- attributi (18 elementi fissi per tutti i field)
- fields (numerosità e composizione variabile a seconda degli instrument)
- instruments (numerosità e composizione variabile a seconda del tipo di progetto)
- project.
Una descrizione più articolata è definita nel documento: [**"Documento di Gestione del Progetto – eCRF Generator con REDCap"**](./2_GestioneDelProgetto.md) 

### Implementazione
Nell'implementazione del progetto ci si propone di utilizzare una tecnica *AGILE (Scrum)* con ciclo di vita a sviluppo incrementale.<br>Le tecnologie utilizzate sono :
- Eclipse e Maven per la gestione del progetto java
- Github per la gestione del versioning
- KanBan (Github) per la gestione delle fasi Scrum, per organizzare e tener traccia delle decisioni e delle attività
- applicativo REDCap e relatice API per la gestione dei flussi dati e controllo delle strutture dati
- un editor di testo generico come Notepad++ per la verifica dei file ".csv" di interscambio dei dati.

Altre informazioni complementari sull'implementazione si posso reperire nel documento: [**"Documento di Gestione del Progetto – eCRF Generator con REDCap"**](./2_GestioneDelProgetto.md) 
### Prove
Le prove di **Verifica** e **Validazione** sono descritte nel dettaglio nel documento: [**"Piano di Test (IEEE 829:2008)"**](./5_PianoDiTest_IEEE829.md) 

## **9. Garanzia di qualità**
###### [Quale organizzazione e procedure verranno utilizzate per garantire che il software in fase di sviluppo soddisfi i requisiti di qualità dichiarati? I molti aspetti di un Piano di Assicurazione della Qualità possono anche essere trattati in un documento separato. Il tema della garanzia della qualità è discusso nel Capitolo 6.]

La garanzia di qualità ha l’obiettivo di assicurare che il software sviluppato soddisfi in modo verificabile i requisiti funzionali e non funzionali stabiliti. L’approccio adottato si basa sui principi descritti da Van Vliet, secondo cui la qualità deriva dall’applicazione controllata di processi strutturati, dalla prevenzione dei difetti e dalla verifica sistematica dei risultati lungo tutto il ciclo di vita del progetto.

### Organizzazione della qualità

La responsabilità della qualità è distribuita tra più ruoli:
- il responsabile di progetto supervisiona l’applicazione delle procedure di qualità e garantisce la conformità agli standard stabiliti;
- il team di sviluppo applica le linee guida tecniche, partecipa alle revisioni e produce codice conforme agli standard;
- il Product Owner contribuisce alla validazione dei requisiti e alla verifica dell’aderenza del prodotto agli obiettivi.

La collaborazione tra i ruoli avviene secondo un processo iterativo, con momenti strutturati di revisione e verifica

#### Procedure di Quality Assurance (QA)
Le attività di QA sono orientate alla prevenzione dei difetti e alla definizione di processi ripetibili e controllabili. Le principali procedure adottate includono:
- definizione e applicazione di standard di codifica, convenzioni architetturali e linee guida progettuali;
- revisione formale dei requisiti e dei documenti di progetto per garantirne completezza, coerenza e verificabilità;
- revisioni tecniche periodiche del design e del codice (peer review);
- controllo della configurazione e gestione delle versioni per assicurare tracciabilità e integrità degli artefatti;
- audit interni per verificare l’aderenza ai processi stabiliti

#### Procedure di Quality Control (QC)
Le attività di QC sono orientate all’individuazione dei difetti nel prodotto e alla verifica della sua conformità ai requisiti. Le principali attività comprendono:
- test unitari, di integrazione, di sistema e di regressione;
- test funzionali e non funzionali (prestazioni, sicurezza, usabilità);
- test di accettazione basati sui criteri concordati con il committente;
- misurazione della qualità tramite metriche quali copertura dei test, densità dei difetti, tasso di risoluzione e stabilità delle build.

#### Strategie di testing
Il processo di testing è integrato nel ciclo di sviluppo e prevede:
- automazione dei test ove possibile, per garantire ripetibilità e rapidità di esecuzione;
- integrazione continua, con esecuzione automatica dei test a ogni modifica significativa;
- utilizzo di ambienti di test dedicati e dati controllati;
- eventuale adozione di pratiche come Test-Driven Development o Behavior-Driven Development.

#### Gestione dei difetti
I difetti rilevati vengono registrati, classificati e gestiti secondo un processo strutturato che prevede:
- identificazione e descrizione del problema;
- assegnazione di priorità e severità;
- pianificazione e verifica della correzione;
- monitoraggio delle metriche di difettosità e analisi dei trend.

#### Documentazione della qualità
Le attività di qualità sono supportate da documentazione specifica, che può includere:
- piano di assicurazione della qualità (separato o integrato nel presente documento);
- report di test e risultati delle verifiche;
- registro dei difetti e stato delle correzioni;
- checklist di revisione e verbali delle attività di audit.

#### Miglioramento continuo
Il processo di qualità prevede un ciclo di miglioramento continuo basato su:
- analisi delle cause radice dei difetti significativi;
- aggiornamento periodico degli standard e delle procedure;
- raccolta delle lezioni apprese e loro integrazione nei processi successivi;
- momenti di revisione e retrospettiva, in particolare nei contesti iterativi e agili.
Queste attività garantiscono che la qualità non sia un risultato finale, ma un processo continuo che accompagna l’intero sviluppo del software.

## **10. Pacchetti di lavoro (workpackages)**
![Badge](https://img.shields.io/badge/Gian-si_può_saltare-green)
###### [I progetti più grandi devono essere suddivisi in attività, parti gestibili che possono essere allocate ai singoli membri del team. Ciascuna di queste attività deve essere identificata nel piano di progetto. La scomposizione gerarchica del progetto è rappresentata in una struttura di scomposizione del lavoro (vedi anche Sezione 8.4).]

## **11. Risorse**
###### [Durante il progetto sono necessarie molte risorse. L'hardware, i cicli della CPU e gli strumenti necessari per supportare il progetto sono elencati in questa voce. Occorre inoltre indicare il personale necessario per le varie fasi del processo. ]

### Risorse Software
- Java Development Kit (JDK 17+)
- Eclipse IDE
- Maven per build automation
- GitHub per version control
- REDCap 13+ con API abilitate

### Risorse Hardware
- Workstation di sviluppo (Windows/Linux)
  - i requisiti minimi sono abbastanza limitati poiche l'applicazione non ha grosse necessita di memoria sia persistente che volatile
  - anche la **cpu** non è rilevante, qualsiasi processore relativamente recente è più che sufficiente 
- Accesso al server REDCap dell'INT
- Connessione internet stabile privata e sicura (VPN)

### Formazione Richiesta
- Conoscenza delle API REDCap
- Familiarità con standard clinici e GCP
- Competenze in Java e JavaFX
- Conoscenze di base su GDPR e normative sulla privacy

## **12. Budget e programma**
######  [Il budget totale per il progetto deve essere assegnato alle varie attività come indicato nella struttura di ripartizione del lavoro. Anche le attività devono essere programmate in tempo, ad esempio utilizzando un grafico PERT (vedi Sezione 8.4). In questa rubrica è indicato anche il modo in cui vengono tracciate le risorse e le altre spese. Il tema della stima dei costi e dei tempi è trattato ampiamente nel Capitolo 7.]

Il progetto è sviluppato come parte di un'attività di lavoro e studio universitario, pertanto non sono previsti costi diretti per lo sviluppo. I costi indiretti includono:
- Ore dedicate dal personale INT: 10 ore/mese per 3 mesi
- Utilizzo infrastruttura esistente (server REDCap)
- Formazione specifica sulle API REDCap (da parte dello sviluppatore)

### Programma temporale (ipotesi con diagramma di Gantt)
<table>
  <tr>
    <th>Attività</th>
    <th>1–4</th>
    <th>5–8</th>
    <th>9–12</th>
    <th>13–16</th>
    <th>17–20</th>
  </tr>

  <tr>
    <td>Definizione requisiti e progettazione</td>
    <td><span style="color:#4CAF50;">██████</span></td>
    <td></td><td></td><td></td><td></td>
  </tr>

  <tr>
    <td>Sviluppo metamodello e API integration</td>
    <td></td>
    <td><span style="color:#2196F3;">████████</span></td>
    <td></td><td></td><td></td>
  </tr>

  <tr>
    <td>Sviluppo strumenti standard</td>
    <td></td><td></td>
    <td><span style="color:#9C27B0;">██████████</span></td>
    <td></td><td></td>
  </tr>

  <tr>
    <td>Sviluppo strumento laboratorio</td>
    <td></td><td></td><td></td>
    <td><span style="color:#FF9800;">██████████</span></td>
    <td></td>
  </tr>

  <tr>
    <td>Sviluppo interfaccia utente</td>
    <td></td><td></td><td></td><td></td>
    <td><span style="color:#795548;">████████</span></td>
  </tr>

  <tr>
    <td>Testing e validazione</td>
    <td></td><td></td><td></td>
    <td><span style="color:#F44336;">██</span></td>
    <td><span style="color:#F44336;">██</span></td>
  </tr>

  <tr>
    <td>Deployment e documentazione</td>
    <td></td><td></td><td></td>
    <td></td>
    <td><span style="color:#607D8B;">███</span></td>
  </tr>

  <!-- Sovrapposizioni Scrum -->
  <tr>
    <td><i>Refinement continuo</i></td>
    <td><span style="color:#8BC34A;">██</span></td>
    <td><span style="color:#8BC34A;">██</span></td>
    <td><span style="color:#8BC34A;">██</span></td>
    <td><span style="color:#8BC34A;">██</span></td>
    <td><span style="color:#8BC34A;">██</span></td>
  </tr>
  <tr>
    <td><i>QA anticipato</i></td>
    <td></td>
    <td></td>
    <td><span style="color:#E91E63;">██</span></td>
    <td><span style="color:#E91E63;">██</span></td>
    <td></td>
  </tr>
</table>

## **13. Cambiamenti**
![Badge](https://img.shields.io/badge/Gian-OK-green)
###### [È stato affermato in precedenza che i cambiamenti sono inevitabili. Bisogna garantire che questi cambiamenti siano gestiti in modo ordinato. Sono quindi necessarie procedure chiare su come verranno gestite le modifiche proposte. Se il processo è agile, ogni iterazione comporta modifiche e queste vengono gestite in modo leggero. In realtà, non sono visti come cambiamenti. Nei processi più pesanti, ogni modifica proposta deve essere registrata e rivista. Quando una richiesta di modifica è stata approvata, è necessario stimarne l'impatto (costo). Infine, la modifica deve essere incorporata nel progetto. Le modifiche che vengono immesse tramite la porta sul retro portano a codice strutturato male, documentazione inadeguata e superamento di costi e tempi. Poiché le modifiche portano a versioni diverse sia della documentazione che del codice, le procedure da seguire per gestire tali modifiche vengono spesso gestite nel contesto di un piano di controllo della configurazione.]
Data la inevitabilità dei cambiamenti all'interno di progetti software evoluti, si cerca di definirne la natura e garantire una gestione ordinata e controllata delle modifiche. Il progetto REDCap adotta un processo strutturato definito nel [**"Piano di Manutenzione del Software (REDCap-MNT-PLAN-1.0)"**](./6_MaintenancePlan.md), che integra procedure sia per la gestione agile delle iterazioni che per il controllo formale delle modifiche sostanziali.<br> Il piano definisce quattro tipologie di manutenzione, ciascuna con un processo specifico:
1. **Manutenzione Correttiva (Corrective)**: Per risolvere difetti (bug) rilevati in produzione. Viene gestita tramite ticketing e prioritarizzata in base a Gravità e Impatto.
2. **Manutenzione Adattiva (Adaptive)**: Per adattare il sistema a cambiamenti esterni (es. aggiornamenti del sistema operativo, nuove normative GDPR). Richiede analisi d'impatto e test estesi.
3. **Manutenzione Perfettiva (Perfective)**: Per miglioramenti interni (performance, usabilità, manutenibilità) senza alterare funzionalità esterne (es. refactoring, ottimizzazione).
4. **Manutenzione Evolutiva (Enhancement)**: Per l'aggiunta di nuove funzionalità o modifiche sostanziali. Questa tipologia viene *gestita come un mini-progetto*, richiedendo una formale *Richiesta di Modifica (RFC)* che documenti descrizione, impatto, costi, benefici e rischi.<br>
Per evitare che le modifiche portino a "codice strutturato male, documentazione inadeguata e superamento di costi e tempi", si propone l'utilizzo di una procedura rigorosa:
1. **Identificazione e Registrazione**: Tutte le richieste vengono registrate in un sistema di ticketing (es. GitHub Issues) e collegate alla baseline dei requisiti.
2. **Classificazione e Prioritarizzazione**: Il Responsabile della Manutenzione classifica la tipologia e assegna una priorità.
3. **Analisi e Approvazione**: Per le modifiche evolutive, una RFC formale viene sottoposta all'approvazione del *Comitato di Governo*.
4. **Pianificazione e Sviluppo**: Le modifiche approvate vengono inserite in sprint di sviluppo e implementate su branch separati del repository Git.
5. **Test**: Ogni modifica viene sottoposta a test di unità, integrazione e sistema, con particolare attenzione ai test di regressione.
6. **Rilascio in Staging e Produzione**: Dopo la validazione in un ambiente di staging speculare alla produzione, il rilascio avviene in una finestra pianificata, accompagnato da un *piano di rollback*.

## **14. Consegna**
![Badge](https://img.shields.io/badge/Gian-OK-green)
###### [Devono essere indicate le procedure da seguire per la consegna dell'impianto al cliente.
###### Il piano del progetto mira a fornire un quadro chiaro del progetto sia al cliente che al team di progetto. Se gli obiettivi non sono chiari, non saranno raggiunti.
###### Nonostante un'attenta pianificazione, le sorprese continueranno a sorgere durante il progetto. Tuttavia, un'attenta pianificazione all'inizio porta a meno sorprese e rende il progetto meno vulnerabile a queste sorprese. Il piano del progetto affronta una serie di domande che anticipano possibili eventi futuri. Fornisce procedure ordinate per affrontare tali eventi, in modo che possano essere raggiunte decisioni giustificate.]
Inizialmente si prevede una distribuzione dell'applcativo in formato .JAR come applicazione standalone di java. L'esecuzione si avvarrà della libreria **JavaFX** per simulare una *applicazione desktop* e sarà accompagnata da un file di script per settare l'ambiente di avvio: path della libreria JavaFx e dell'eseguibile javaw.  
In sostanza all'atto di consegna verrà fornito agli stakeholder e/o utenti:
- file .jar dell'applicazione java-desktop,
- file .bat di configurazione dell'ambiente di esecuzione,
- specifica obbligatoria da controllare o comunicare è che nell'host dell'utente deve essere presente una versione di java jre o jdk 17 o superiore.  

Si prevede la distribuzione di una nuova release dell'applicativo al termine di ogni ciclo **Sprint** del framework Scrum.  
In una sezione apposita dell'applicativo desktop definita **Help** verranno implementate le informazioni sulla struttura e utilizzo dell'applicativo; inizialmente si metteranno le informazioni rintracciabili attraverso *javadoc* dei commenti sulle rispettive parti di codice.