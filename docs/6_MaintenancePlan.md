# **Documento "Piano di Manutenzione del Software" - Progetto REDCap**

**ID Documento:** `REDCap-MNT-PLAN-1.0`  
**Progetto:** REDCap (Research Electronic Data Capture)  
**Versione:** 1.0  
**Data:** 30.12.2025  
**Stato:** Bozza  

---

## **1.0 Introduzione e Scopo**

Questo documento definisce la strategia, i processi, le responsabilità e la pianificazione per la manutenzione del software della piattaforma REDCap implementata. Esso si appoggia e fa riferimento ai documenti fondanti del progetto, in particolare:
*   `1_REDCapProjectPlan.md`: Per gli obiettivi generali, la governance e gli stakeholder.
*   `2_GestioneDelProgetto.md`: Per i ruoli, le responsabilità e i processi di comunicazione.
*   `3_Requisiti.md`: Per la baseline funzionale contro cui valutare modifiche e correzioni.
*   `5_PianoDiTest_IEEE829.md`: Per i criteri di accettazione e le procedure di validazione delle modifiche.

Lo scopo è garantire che il sistema operi in modo sicuro, stabile ed efficiente, e che possa evolversi in risposta a nuove esigenze degli utenti, cambiamenti normativi o aggiornamenti tecnologici, minimizzando i tempi di fermo e i rischi.

## **2.0 Tipi di Manutenzione Previste**

### **2.1 Manutenzione Correttiva (Corrective)**
*   **Descrizione:** Interventi per risolvere difetti (bug) e malfunzionamenti rilevati in produzione.
*   **Origine:** Segnalazioni degli utenti (ticket), monitoraggio di sistema, log errori.
*   **Processo:** Prioritarizzazione basata su Gravità e Impatto (Critico, Alto, Medio, Basso).

### **2.2 Manutenzione Adattiva (Adaptive)**
*   **Descrizione:** Interventi necessari per adattare il sistema a cambiamenti nell'ambiente esterno.
*   **Esempi:** Aggiornamenti del sistema operativo del server, aggiornamenti major/minor della versione di REDCap fornita dal consorzio, cambiamenti nelle normative sulla privacy (es. GDPR).
*   **Nota:** Richiede spesso analisi d'impatto e test estesi.

### **2.3 Manutenzione Perfettiva (Perfective)**
*   **Descrizione:** Miglioramenti delle prestazioni, dell'usabilità o della manutenibilità del codice, senza alterare le funzionalità esterne.
*   **Esempi:** Ottimizzazione di query lente, refactoring di modulo complesso, miglioramento della documentazione interna.

### **2.4 Manutenzione Evolutiva (Enhancement)**
*   **Descrizione:** Aggiunta di nuove funzionalità o modifiche sostanziali a quelle esistenti, derivanti da nuovi requisiti.
*   **Processo:** Deve essere gestita come un mini-progetto, con riferimento al documento `3_Requisiti.md` per la tracciabilità. Richiede una formale **Richiesta di Modifica (RFC)**.

## **3.0 Organizzazione e Responsabilità**

| Ruolo | Responsabilità Principali | Riferimento |
| :--- | :--- | :--- |
| **Responsabile della Manutenzione** | Approva piani di rilascio, RFC e alloca le risorse. | `2_GestioneDelProgetto.md` |
| **Team di Sviluppo/Manutenzione** | Sviluppa, testa e implementa correzioni e aggiornamenti. Supporto tecnico di 3° livello (livelli di supporto IT da 0 a 4 <i>fonte ServiceNow</i>). | - |
| **Amministratori di Sistema** | Esegue aggiornamenti in produzione, gestisce backup/ripristini, monitora le prestazioni. | - |
| **Amministratori REDCap (Super-User)** | Filtra segnalazioni utente, gestisce configurazioni, svolge test di accettazione base (UAT). | - |
| **Comitato di Governo** | Approva modifiche evolutive di ampio impatto e i budget correlati. | `1_REDCapProjectPlan.md` |

## **4.0 Processo di Gestione delle Richieste di Modifica (RFC) e dei Difetti**

1.  **Identificazione e Registrazione**
    *   Registrazione nel sistema di ticketing (es. ServiceNow, GitHub, GitLab Issues).
    *   Riferimento a requisiti (`3_Requisiti.md`) o casi di test (`5_PianoDiTest_IEEE829.md`) se applicabile.

2.  **Classificazione e Prioritarizzazione**
    *   Il Responsabile Manutenzione classifica il tipo (Correttiva, Adattiva, ecc.) e assegna priorità.

3.  **Analisi e Approvazione (per le Evolutive)**
    *   Redazione di una RFC (descrizione impatto, costi, benefici, rischi).
    *   Approvazione da parte del Comitato di Governo.

4.  **Pianificazione e Sviluppo**
    *   Inserimento in sprint/cycle di sviluppo.
    *   Sviluppo su branch separato del repository.

5.  **Test**
    *   Esecuzione test secondo `5_PianoDiTest_IEEE829.md` (Unità, Integrazione, Sistema).
    *   Verifica regressione per le correzioni.

6.  **Rilascio in ambiente di Test (Staging)**
    *   Deployment in ambiente di Test (identico a produzione).
    *   Validazione finale da parte Amministratori REDCap/utenti chiave.

7.  **Pianificazione del Rilascio in Produzione**
    *   Definizione finestra di intervento a basso impatto (notturna/weekend).
    *   Preparazione piano di rollback.

8.  **Implementazione e Verifica**
    *   Esecuzione deployment in produzione da parte Amministratore di Sistema.
    *   Verifica corretto avvio e funzionamento da parte del team.

9.  **Chiusura e Documentazione**
    *   Chiusura ticket.
    *   Aggiornamento documenti (`3_Requisiti.md`, manuali utente).

## **5.0 Gestione degli Ambienti e Controllo di Versione**

### **Ambienti**
*   **Sviluppo (DEV):** Sviluppo e test iniziale.
*   **Test/Integrazione (TEST):** Test integrati e di sistema automatizzati.
*   **Staging:** Copia speculare della produzione, per validazione finale.
*   **Produzione (PROD):** Ambiente live.

### **Controllo di Versione**
*   Tutto il codice custom, script di configurazione e deployment devono essere versionati (es. Git).
*   Strategia di branching (es. Git Flow) per supportare sviluppo parallelo di feature, hotfix e rilasci.

### **Documentazione delle Versioni**
*   Ogni rilascio in PROD deve essere accompagnato da **Release Notes** (modifiche, bug fix, istruzioni aggiornamento).

## **6.0 Piano delle Attività di Manutenzione Ricorrenti**

| Attività | Descrizione | Frequenza | Responsabile |
| :--- | :--- | :--- | :--- |
| **Applicazione di Patch di Sicurezza** | Revisione e applicazione patch critiche per OS, middleware e REDCap. | Immediata (Critiche) / Mensile (Altre) | Amm. Sistema & Team Dev |
| **Backup e Test di Ripristino** | Verifica integrità backup dati e codice. | Settimanale (Log) / Trimestrale (Full Restore Test) | Amm. Sistema |
| **Review dei Log e Monitoraggio** | Analisi log errori applicativi e di sistema, monitoraggio performance. | Giornaliera | Amm. Sistema |
| **Aggiornamento Versione REDCap** | Pianificazione e applicazione aggiornamenti minori/major del core REDCap. | Semestrale o in base a criticità | Team Dev & Amm. Sistema |
| **Audit degli Accessi** | Revisione log di accesso e privilegi utente. | Trimestrale | Amm. REDCap |
| **Pulizia e Ottimizzazione DB** | Manutenzione database (ottimizzazione tabelle). | Mensile | Amm. Sistema |

## **7.0 Gestione dei Rischi nella Manutenzione**

| Rischio | Strategia di Mitigazione |
| :--- | :--- |
| **Regressione** | Suite di test automatizzati e ambiente di staging. |
| **Data Loss** | Backup verificati e piano di rollback. |
| **Tempi di Fermo (Downtime)** | Finestre di manutenzione pianificate e comunicate con anticipo. |
| **Accumulo di Debito Tecnico** | Dedicare percentuale sforzo (es. 15-20%) ad attività perfettive/refactoring. |

## **8.0 Metriche e Reporting**

*   Numero di ticket aperti/chiusi per tipo e priorità.
*   Tempo medio di risoluzione (Mean Time To Resolution).
*   Tempo di uptime del sistema (disponibilità).
*   Numero di rilasci in produzione per periodo.
*   Soddisfazione degli utenti (raccolta post-intervento).

**Nota:** *Un report di sintesi su queste metriche sarà presentato al Comitato di Governo con cadenza trimestrale.*

---
## **Approvazioni:**

| Nome | Ruolo | Firma | Data |
| :--- | :--- | :--- | :--- |
| *[Nome Responsabile Manutenzione]* | Responsabile Manutenzione | | |
| *[Nome Project Sponsor]* | Project Sponsor / Steering Committee | | |

---
## **Riferimenti ai Documenti del Progetto:**
1.  `1_REDCapProjectPlan.md`
2.  `2_GestioneDelProgetto.md`
3.  `3_Requisiti.md`
4.  `5_PianoDiTest_IEEE829.md`
