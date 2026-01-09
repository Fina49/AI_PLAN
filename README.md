GitHub : https://github.com/Fina49/AI_PLAN<br>
author: LUGINBÜHL Valentin at valentin.luginbuhl@etu.univ-grenoble-alpes.fr, BEYELER Elie at elie.beyeler@etu.univ-grenoble-alpes.fr<br>

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

## Results
```bash
./compare_asp_mcp.sh
```

## Comparison ASP vs MCP

### ASP (A* Search Planner)
- **Algorithm**: A* search avec heuristique.
- **Optimality**: Trouve des plans **optimaux** (nombre minimal d'actions).
- **Performance**: Temps de recherche généralement rapide pour les problèmes simples.
- **Use case**: Idéal quand la qualité du plan est critique.

### MCP (Monte Carlo Planner)
- **Algorithm**: Monte Carlo Random Walk.
- **Optimality**: Plans **non optimaux** (souvent beaucoup plus longs).
- **Performance**: Génère des plans avec beaucoup d'actions redondantes.
- **Use case**: Utile pour des problèmes où l'optimalité n'est pas critique.

### Résultats typiques (IPC2000 Blocks domain)
| Problem | ASP Actions | MCP Actions | Winner (Quality) |
|---------|-------------|-------------|------------------|
| P001    | 6           | 30          | ASP (5x better) |
| P002    | 10          | 68          | ASP (6.8x better) |
| P003    | 6           | 100         | ASP (16x better) |
| P004    | 12          | 12          | Equal |
| P005    | 10          | 16          | ASP (1.6x better) |
| P006    | 16          | 4           | MCP (4x better) |

### Conclusion
- **ASP** est recommandé pour la plupart des cas où l'on cherche des plans de qualité optimale.
- **MCP** peut occasionnellement trouver des solutions plus courtes par chance, mais produit généralement des plans beaucoup plus longs avec de nombreuses actions redondantes.