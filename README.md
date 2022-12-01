# QRank Map

A map where OpenStreetMap labels are weighted according to their [QRank](https://qrank.wmcloud.org).

OpenStreetMap entries often have a `wikidata=Q*` tag which links the entry to Wikidata of Wikipedia. QRank is a system which measures how often a Wikipedia page associated with a Wikidata entry was loaded in the last year, thereby indicating how important something is.

## Demo

<img src="demo.png" width=450>

<i>Screenshot of the QRank Map in Switzerland shows the label names and QRanks. Geneva seems to be the most important OpenStreetMap entry for Switzerland...</i>

## Generating the Tiles

See [`./planetiler`](planetiler).
