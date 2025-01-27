#!/bin/bash
wd=$(pwd)
cd ../dobudish
./generator.sh Stradokuhelp javahelp || exit 1
cd $wd
mv ../dobudish/documents/Stradokuhelp/output/javahelp/*.jar Stradoku/lib/
