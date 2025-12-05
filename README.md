## Version
Gradle 9.1.0</br>
java version "17.0.9" 2023-10-17 LTS</br>

## Compile A* and MCP
```bash
javac -d classes -cp lib/pddl4j-4.0.0.jar src/fr/uga/pddl4j/examples/asp/ASP.java src/fr/uga/pddl4j/examples/mcp/MCP.java src/fr/uga/pddl4j/examples/Node.java 
```

## A* help
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP --help
```
## MCP help
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.mcp.MCP --help
```
## A* examples
### Blocksworld
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/blocksworld-domain.pddl src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/blocksworld.pddl -e FAST_FORWARD -w 1.2 -t 1000
```
### Hanoi
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/hanoi-domain.pddl src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/hanoi.pddl -e FAST_FORWARD -w 1.2 -t 1000
```
## MCP examples
### Blocksworld
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.mcp.MCP src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/blocksworld-domain.pddl src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/blocksworld.pddl -t 1000
```
### Hanoi
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.mcp.MCP src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/hanoi-domain.pddl src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/hanoi.pddl -t 1000
```