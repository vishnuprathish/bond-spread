##How to run
Description: 
A simple tool to compute spread (in bps) of coprorate bonds in comparison with the government bond of nearest tenor.

Easiest:

`script.sh full_path_input_file.json full_path_output_file.json`

OR

equivalent docker run command. entry point setup to script.sh (which in turn contains the jar run command)

OR 

Attach to the container and run:
 `java -cp target/bond-spread-calc-jar-with-dependencies.jar full_path_input_file.json full_path_output_file.json`
 
 
 Sample input
 JSON format specifying an array of mixed bonds containing both corporate and govt bonds. 
 - Tenor should be specfied in years
 - Yield should be specified in percent with a `%` at the end. 
 - There should be at least one corporate and one govt bond. 
 ```{
   "data": [
     {
       "id": "c1",
       "type": "corporate",
       "tenor": "10.3 years",
       "yield": "5.30%",
       "amount_outstanding": 1200000
     },
     {
       "id": "g1",
       "type": "government",
       "tenor": "9.4 years",
       "yield": "3.70%",
       "amount_outstanding": 2500000
     },
     {
       "id": "c2",
       "type": "corporate",
       "tenor": "13.5 years",
       "yield": null,
       "amount_outstanding": 1100000
     },
     {
       "id": "g2",
       "type": "government",
       "tenor": "12.0 years",
       "yield": "4.80%",
       "amount_outstanding": 1750000
     }
   ]
 }
```
 Sample output
 - Outputs an array of coprate and govt bonds identified by their ID fields. Spread is specfied in bps.
 ```
 {
   "data": [
     {
       "corporate_bond_id": "c1",
       "government_bond_id": "g1",
       "spread_to_benchmark": "160 bps"
     }
   ]
 }
```

Design thoughts: 
- Componenets of standard persitence, Business logic and view layer is used although this program isn't strong in the view and persistence layer. 
- Domain package encapsulate all domain object. In this case key domain objects are Bond and Spread. 
- Service package encaspulates all business logic. 
- documentation available in key functions and as comments on complex statments. 
- A lookupMap is maintained as a one time build to allow for O(logN) lookup time later on. This is enabled by Treemap data structure which maintains a Map of ordered keys. 

Complexity of key operations:
Treemap insert: Log(N)
Treemap iteration N: N
Total complexity of build: NLog(N)


Treemap lookup: Log(N)
Iteration of list: N
Total complexity of list iteration: NLog(N)

Overall system complexity: NLog(N)

A binary lookup needs to be performed to find the nearest bond in a sorted array. So a complexity lower than NLog(N) can not be achieved. 

Other Notes:
- If No matching govt bond is found, it will result in an error in execution.
- a test has not been run after mounting an external folder to docker and using docker run. script.py has details.

 