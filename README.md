## Version
Gradle 9.1.0</br>
java version "17.0.9" 2023-10-17 LTS</br>

## Compile A* and MCP
```bash
javac -d classes -cp lib/pddl4j-4.0.0.jar src/fr/uga/pddl4j/examples/asp/ASP.java src/fr/uga/pddl4j/examples/mcp/MCP.java src/fr/uga/pddl4j/examples/Node.java 
```

## ASP help
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP --help
```
## MCP help
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.mcp.MCP --help
```

## ASP run tests
```bashbash
./run_asp_tests.sh
```

## MCP run tests
```bashbash
./run_mcp_tests.sh
```

## ASP examples
### Blocksworld
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP src/test/resources/benchmarks/pddl/blocksworld/blocksworld_domain.pddl src/test/resources/benchmarks/pddl/blocksworld/blocksworld.pddl -e FAST_FORWARD -w 1.2 -t 1000
```
### Hanoi
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP src/test/resources/benchmarks/pddl/hanoi/hanoi_domain.pddl src/test/resources/benchmarks/pddl/hanoi/hanoi.pddl -e FAST_FORWARD -w 1.2 -t 1000
```

## MCP examples
### Blocksworld
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.mcp.MCP src/test/resources/benchmarks/pddl/blocksworld/blocksworld_domain.pddl src/test/resources/benchmarks/pddl/blocksworld/blocksworld.pddl -t 1000
```
### Hanoi
```bash
java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.mcp.MCP src/test/resources/benchmarks/pddl/hanoi/hanoi_domain.pddl src/test/resources/benchmarks/pddl/hanoi/hanoi.pddl -t 1000
```