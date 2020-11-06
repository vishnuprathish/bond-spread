##How to run
`script.sh full_path_input_file.json full_path_output_file.json`

OR

equivalent docker run command. entry point setup to script.sh (which in turn contains the jar run command)

OR 

Attach to the container and:
 `java -cp target/bond-spread-calc-jar-with-dependencies.jar full_path_input_file.json full_path_output_file.json`

Notes:
- If No matching govt bond is found, it will result in an error in execution.
- a test has not been run after mounting an external folder to docker and using docker run. script.py has details. 