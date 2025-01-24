#!/bin/bash
# Argument validation check
if [ "$#" -lt 1 ]; then
    echo "Usage: $0 doc|full"
    exit 1
fi
if [ "$1" = "doc" ]; then
    wd=$(pwd)
    cd ../dobudish
    ./generator.sh Stradokuhelp javahelp || exit 1
    cd $wd
    mv ../dobudish/documents/Stradokuhelp/output/javahelp/*.jar Stradoku/lib/
fi
ant deploy || exit 1
mv dist/Stradoku.jar Stradoku/
test "$1" = "full" || exit 0
rm Stradoku/verlauf.log
rm Stradoku/stradoku.lst
rm -r Stradoku/Aufgaben/
rm -r Stradoku/PNGs/
zip -q -r -9 -X Stradoku.zip Stradoku/ -x "*DS_STORE"
