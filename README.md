# Bachelor Semester Project 2
## Sporadic Tasks Scheduling

## Project
We are implementing a job scheduling simulator for a very specific class of tasks.
The scheduler is (1,1)-restricted (full migration, static priorities) and all tasks
have unique priorities.

## Repository Structure
- `notes`: weekly study notes for understanding and later reuse for writing
- `spu11sched`: Java implementation (as a Maven project)

## Compiling and Running
Enter the `spu11sched` directory and run `mvn install` to compile.

Then, run `java -ea -jar target/spu11sched-1.0-SNAPSHOT.jar test.spc`


## License
See LICENSE
