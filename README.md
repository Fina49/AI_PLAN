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
```bash
./run_asp_tests.sh
```

## MCP run tests
```bash
./run_mcp_tests.sh
```