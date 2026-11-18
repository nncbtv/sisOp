# Guida alla Contribuzione

Per garantire la stabilità del progetto, il ramo principale (`main`) è protetto.

Se hai trovato un errore, vuoi migliorare un'implementazione, aggiungere un esercizio o proporre una correzione, puoi farlo tranquillamente seguendo i passaggi di questo file e rispettando le linee guida a fine pagina

---

## Come proporre una modifica

### 1. Crea un Fork della repository

Dalla pagina GitHub della repository, clicca su **Fork** per creare una copia del progetto sul tuo account GitHub.

Successivamente puoi clonare il tuo fork sul computer:

```bash
git clone https://github.com/TUO-USERNAME/sisOp.git

cd algoritmieStruttureDati
```

### 2. Crea un nuovo branch

Le modifiche devono essere effettuate su un branch dedicato e non direttamente su `main`.

```bash
git checkout -b nomeDellaModifica
```

Ad esempio:

```bash
git checkout -b giostraGalleggiante
```

### 3. Apporta le modifiche

Modifica il codice o aggiungi il materiale necessario rispettando la struttura della repository.

In particolare:

* `CodiciEsercitazioni/` → materiale che riguarda le esercitazioni viste a lezione.
* `EsempiEsercizio1/` → materiale che riguarda il primo esercizio del compito.
* `RiassuntiTeorici/` → Materiale utile per comprendere la teoria.
* `Tracce/` → Materiale riguardante una traccia (Si preferisce una traccia e il relativo svolgimento per intero).

### 4. Salva le modifiche e pubblica il branch

Una volta terminato il lavoro:

```bash
git add .
git commit -m "Descrizione chiara della modifica"
git push origin nomeDellaModifica
```

Ad esempio:

```bash
git add .
git commit -m "Aggiunta Traccia: Giostra Galleggiante"
git push origin giostraGalleggiante
```

### 5. Apri una Pull Request

Dopo aver effettuato il `push`, vai sul tuo fork su GitHub.

Crea una **Pull Request** verso la repository originale.

Nella Pull Request descrivi brevemente cosa hai fatto e perché.

Infine clicca su **Create pull request**.

## Cosa succede dopo?

La Pull Request verrà esaminata, se correttamente in linea con la Repository verrà aggiunta.

## Linee guida

Quando contribuisci, cerca di:

* mantenere il codice semplice e leggibile.
* seguire lo stile già utilizzato nella repository.
* spiegare eventuali modifiche non immediate.

L'obiettivo della repository è principalmente **didattico**, quindi la chiarezza e la comprensibilità del codice/materiale sono importanti quanto la sua correttezza.