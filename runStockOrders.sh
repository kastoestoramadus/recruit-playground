#!/usr/bin/env bash

cat orders.txt | sbt "runMain walidus.stock.StockConsole"