

## UML - Work in progress :construction:

``` mermaid

classDiagram

DataDistributor "1" *-- SharedDataListener
DataDistributor <|.. StreamRabbitDataDistributor
SharedDataListener <|.. Controller
Controller "1" *-- DataDistributor
Controller *-- GUI

class DataDistributor {
  <<interface>>
  + init(UpdateListener controller) void
  + joinBoard(String nickname, String boardName)
  + leaveBoard() void
  + shareUpdate(String jsonData) void
  + updateCursor(String jsonData) void
  + existingBoards() String
}

class SharedDataListener {
  <<interface>>

  + joined(String jsonInitBoard) void
  + boardUpdate(String jsonEdits) void 
  + cursorsUpdate(String jsonCursor) void
  + notifyErrors(String errMsg, Exception exc) void
  + boardLeft(Boolean hasLeft) void
  + newBoardCreated(String jsonBoardInfo) void
}

class Controller {
  <<interface>>
  + setCellValue(Pos cellPos, int value)
  + getPublishedBoards() List~BoardInfo~
  + createNewBoard(String name, int size)
  + selectCell(Pos cellPos)
  + leaveBoard()
  + joinToBoard(String boardName)
}

```
---

**NOTE** Instead `String` type for jsonData i could create a wrapper or interface like:

``` mermaid

---
title: Alternative type instead String as parameter in DataDistributor methods
---
classDiagram
class JsonData {
  <<interface>>
  + getJsonString() String
}
```

# Note-Appunti
   
## RabbitDataDistributor  
- [X] Tutti i metodi prendono in input una ~~stringa~~ `JsonData` json del Messaggio da inviare —> quindi è chi chiama il metodo che deve preparare i dati e convertirli in json  
- [X] Gli handler dei messaggi ricevuti vengono definiti fuori ( da messaggio json string poi fuori questa classe uni si arrangia a convertire e gestire i dati)  
- [ ] Aggiungere metodo alla api così da sapere se esiste una coda o no (aggiungere una nuova coda stream che contiene tutte le board create con lo stato iniziale)  
  
  
1. Chiedi se esiste già la board  
2. Fai join (internamente fa subscribe a stream della board e collegamento consume handler mag ricevuti)  
3. Se non esisteva (al esito del punto 1) allora dichaira/ pubblica nel registro delle Baird lo stato della board e il nome (su board registry)  
  
  
Alla fine quindi avrei tre stream:  
  
BoardRegistry 🟫🟫🟫🟫🟫🟫🟫🟫  
{boardName}-users🟩🟩🟩🟩🟩🟩🟩🟩  
{boardName}-edits🟪🟪🟪🟪🟪🟪🟪🟪  
  
Il 🟩 avrebbe una retention dei messaggi bassa = dopo X minuti/secondi cancella i messaggi pubblicati  
  
**Dinamica di collegamento base:**  
1. Da 🟫 ottengo le board attive (e il loro schema iniziale)  
2. Mi collego ad una board  
    1. Una esistente tra quelle registrate  
        1. Carico lo schema iniziale della board (presente in 🟫, in locale ho tutte le board iniziali dal momento in cui viene pubblicata una)  
        2. Mi sottoscrivo a 🟪 e 🟩  
    2. Creo una nuova (se non esistente)  
        1. Registro/pubblico board in 🟫  
        2. Sottoscrizione a 🟪 e 🟩  
3. Ogni movimento del mouse/ cella selezionata viene pubblicata su 🟩  
4. Ogni modifica di cella pubblicata su 🟪  
  
* Se voglio **cambiare board** faccio leave board (discrivo dalle code 🟪🟩 )  
* Nel mentre mi salvo ogni tanto il tag di riferimento di un messaggio recente  così se per sbaglio ce un errore di connessione e mi voglio ricollegare non ricevo tutti i messaggi ma solo dall’ultimo ricevuto prima della sconnessione (dovrei salvare ad ogni messaggio che arriva il suo tag —> capire come si comporta il consume handler che si ritrova con un rif di partenza diverso , poi in realtà lo definisco una volta da dove partire a ricevere i msg) —> posso fare che l’app in caso di riconnessione si ricorda da che punto è non ricostruisce da zero la gui  
  