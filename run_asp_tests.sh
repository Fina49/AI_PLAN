#!/bin/bash

# Script pour exécuter les tests de planification sur différents domaines
# et collecter les résultats

OUTPUT_FILE="results_$(date +%Y%m%d_%H%M%S).txt"
TIMEOUT=1000

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
        -t "$TIMEOUT" >> "$OUTPUT_FILE" 2>&1
    
    echo "" >> "$OUTPUT_FILE"
    echo "----------------------------------------" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
}

# ========================================
# Tests IPC2000 - Blocks Domain (strips-typed)
# ========================================
echo "Testing Blocks domain..."

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/blocks/strips-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/blocks/strips-typed/p001.pddl" \
    "Blocks-P001"

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/blocks/strips-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/blocks/strips-typed/p005.pddl" \
    "Blocks-P005"

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/blocks/strips-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/blocks/strips-typed/p010.pddl" \
    "Blocks-P010"

# ========================================
# Tests IPC2000 - Elevator Domain (strips-simple-typed)
# ========================================
echo "Testing Elevator domain..."

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/elevator/strips-simple-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/elevator/strips-simple-typed/p001.pddl" \
    "Elevator-P001"

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/elevator/strips-simple-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/elevator/strips-simple-typed/p005.pddl" \
    "Elevator-P005"

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/elevator/strips-simple-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/elevator/strips-simple-typed/p010.pddl" \
    "Elevator-P010"

# ========================================
# Tests IPC2000 - Freecell Domain (strips-typed)
# ========================================
echo "Testing Freecell domain..."

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/freecell/strips-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/freecell/strips-typed/p01.pddl" \
    "Freecell-P01"

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/freecell/strips-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/freecell/strips-typed/p05.pddl" \
    "Freecell-P05"

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/freecell/strips-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/freecell/strips-typed/p10.pddl" \
    "Freecell-P10"

# ========================================
# Tests IPC2000 - Logistics Domain (strips-typed)
# ========================================
echo "Testing Logistics domain..."

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/logistics/strips-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/logistics/strips-typed/p01.pddl" \
    "Logistics-P01"

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/logistics/strips-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/logistics/strips-typed/p05.pddl" \
    "Logistics-P05"

run_test \
    "src/test/resources/benchmarks/pddl/ipc2000/logistics/strips-typed/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2000/logistics/strips-typed/p10.pddl" \
    "Logistics-P10"

echo "" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"
echo "TESTS TERMINÉS" >> "$OUTPUT_FILE"
echo "Date: $(date)" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"

echo ""
echo "Tous les tests sont terminés!"
echo "Résultats sauvegardés dans: $OUTPUT_FILE"
echo ""
