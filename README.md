# MyCurrency-Unipr

Questa applicazione e' stata progettata seguendo la consegna per l'esame di Programmazione di Sistemi Mobili dell'Universita' di Parma.

- Sviluppare un’applicazione che consenta di applicare un tasso di cambio tra valute
- L’utente deve poter inserire una quantità (di default 1), una valuta di partenza ed una in cui convertire e l’applicazione deve calcolare il risultato. Ad esempio: EUR 100,00 = USD 105,89
- L’utente deve anche poter visualizzare il tasso di cambio negli ultimi 7 giorni
- L’utente può decidere di salvare come “preferita” la coppia di valute per un accesso più rapido
- Le informazioni sui tassi di cambio possono essere recuperate da API pubbliche e gratuite online

<table>
<caption id="multi_row">Contributers summarize</caption>
<tr>    <th>Name      <th>Role               <th>Year
<tr><td>Alessandro Vascelli   <td>Author  <td>2023
</table>

## Architettura

Il programma si divide in 5 classi:
1. Main Activity: al suo interno, oltre a variabili, oggetti e listener necessari al funzionamento, troviamo le funzioni per popolare gli spinner con le valute, per invertire la selezione delle valute, per fare il calcolo della conversione e visualizzare i tassi di cambio dei giorni passati. Queste informazioni vengono visualizzate su un unica homepage (vedi immagine sottostante) realizzata nel layout 'activity_main' che si trova nella cartella 'res'.
   
<div align="center"> <a href="https://ibb.co/NZ1hDLc"><img src="https://i.ibb.co/r4bP8ZD/Screenshot-2023-08-09-alle-14-44-05.png" alt="Screenshot-2023-08-09-alle-14-44-05" border="0" width="30%" align="center"></a> </div>



