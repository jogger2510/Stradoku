#!/bin/bash
ant deploy || exit 1
mv -v dist/Stradoku.jar Stradoku/
rm -v Stradoku/verlauf.log
rm -v Stradoku/out.ps
rm -v Stradoku/out.pdf
rm -v Stradoku/stradoku.lst
rm -rv Stradoku/Aufgaben/
rm -rv Stradoku/PNGs/
cp -v ../"Stradoku Original/letztes.str" Stradoku/
cp -v ../"Stradoku Original/Arche.zip" Stradoku/
zip -q -r -9 -X Stradoku.zip Stradoku/ -x "*DS_STORE"
