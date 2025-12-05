#!/bin/bash

# Script pour exécuter les tests de planification sur différents domaines
# et collecter les résultats

OUTPUT_FILE="results_$(date +%Y%m%d_%H%M%S).txt"
TIMEOUT=1000
WEIGHT=1.2

echo "========================================" > "$OUTPUT_FILE"
echo "RÉSULTATS DE PLANIFICATION" >> "$OUTPUT_FILE"
echo "Algorithm: ASP" >> "$OUTPUT_FILE"
echo "Date: $(date)" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Fonction pour exécuter un test
run_test() {
    local domain=$1
    local problem=$2
    local name=$3
    
    echo "" >> "$OUTPUT_FILE"
    echo "========================================" >> "$OUTPUT_FILE"
    echo "TEST: $name" >> "$OUTPUT_FILE"
    echo "Domain: $domain" >> "$OUTPUT_FILE"
    echo "Problem: $problem" >> "$OUTPUT_FILE"
    echo "========================================" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    
    echo "Exécution: $name..."
    
    # Exécuter le planificateur et capturer la sortie
    java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP \
        "$domain" "$problem" \
        -e FAST_FORWARD -w "$WEIGHT" -t "$TIMEOUT" >> "$OUTPUT_FILE" 2>&1
    
    echo "" >> "$OUTPUT_FILE"
    echo "----------------------------------------" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
}

# BlocksWorld
if [ -f "src/test/resources/benchmarks/pddl/blocksworld/blocksworld_domain.pddl" ]; then
    run_test \
        "src/test/resources/benchmarks/pddl/blocksworld/blocksworld_domain.pddl" \
        "src/test/resources/benchmarks/pddl/blocksworld/blocksworld.pddl" \
        "BlocksWorld"
fi

# Hanoi
if [ -f "src/test/resources/benchmarks/pddl/hanoi/hanoi_domain.pddl" ]; then
    run_test \
        "src/test/resources/benchmarks/pddl/hanoi/hanoi_domain.pddl" \
        "src/test/resources/benchmarks/pddl/hanoi/hanoi.pddl" \
        "Hanoi"
fi

echo "" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"
echo "TESTS TERMINÉS" >> "$OUTPUT_FILE"
echo "Date: $(date)" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"

echo ""
echo "Tous les tests sont terminés!"
echo "Résultats sauvegardés dans: $OUTPUT_FILE"
echo ""
