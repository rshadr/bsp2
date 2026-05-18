# Bachelor Semester Project 2
## Sporadic Tasks Scheduling

## Project
We are implementing a job scheduling simulator for a very specific class of tasks.
The scheduler is (1,1)-restricted (full migration, static priorities) and all tasks
have unique priorities.

## Repository Structure
- `notes`: weekly study notes for understanding and later reuse for writing
- `spu11sched`: Java implementation (as a Maven project)
- `testgen`: C program to generate test cases
- `testprog`: C uber-program to test `spu11sched`
- `report`: Final report written in LaTeX (ACM article template)

## Compiling and Running

### Do everything magically
XXX magic script

### testgen
Enter the `testgen` directory and run

`$ make`

Now, you can generate test cases using the following family of commands:

`$ ./testgen <options>`

Options are of the form `--option-name=value`.
The following options are supported and required (all as decimal int32):

- `max-duration`
- `num-processors`
- `max-sporadic-delay`
- `num-tasks`
- `max-initial-time`
- `max-wcet`
- `max-min-period`
- `random-seed`


### testprog
Enter the `testprog` directory and run

`$ make`

XXX todo


### spu11sched
Enter the `spu11sched` directory and run

`$ mvn package assembly:single` to compile.

Then, run

`$ java -jar target/spu11sched-1.0-SNAPSHOT-jar-with-dependencies.jar test.spc`


## License
See LICENSE

