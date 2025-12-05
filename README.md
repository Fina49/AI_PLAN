## compile
javac -d classes -cp lib/pddl4j-4.0.0.jar src/fr/uga/pddl4j/examples/asp/ASP.java

## run
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP --help

## example
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/domain.pddl src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/p01.pddl -e FAST_FORWARD -w 1.2 -t 1000