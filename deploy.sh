#!/bin/bash
ant deploy || exit 1
mv dist/Stradoku.jar Stradoku/
rm Stradoku/verlauf.log
rm Stradoku/stradoku.lst
rm -r Stradoku/Aufgaben/
rm -r Stradoku/PNGs/
cp ../"Stradoku Original/letztes.str" Stradoku/
cp ../"Stradoku Original/Arche.zip" Stradoku/
zip -q -r -9 -X Stradoku.zip Stradoku/ -x "*DS_STORE"
