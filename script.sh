#!/bin/bash
#java -cp /submission/target/bond-spread-calc-jar-with-dependencies.jar org.bondspread.App /submission/src/test/resource/input_multiple_bonds.json /submission/out.json
java -cp /submission/target/bond-spread-calc-jar-with-dependencies.jar org.bondspread.App $1 $2