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
# Tests IPC2002 - Depots Domain (strips-automatic)
# ========================================
echo "Testing Depots domain (IPC2002)..."

run_test \
    "src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/p01.pddl" \
    "Depots-P01"



# ========================================
# Tests IPC2002 - Driverlog Domain
# ========================================
echo "Testing Driverlog domain (IPC2002)..."

run_test \
    "src/test/resources/benchmarks/pddl/ipc2002/driverlog/strips-automatic/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2002/driverlog/strips-automatic/p02.pddl" \
    "Driverlog-P02"

# ========================================
# Tests IPC2006 - Openstacks Domain
# ========================================
echo "Testing Openstacks domain (IPC2006)..."

run_test \
    "src/test/resources/benchmarks/pddl/ipc2006/openstacks/propositional/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2006/openstacks/propositional/p03.pddl" \
    "Openstacks-P03"

# ========================================
# Tests IPC2002 - Zenotravel Domain
# ========================================
echo "Testing Zenotravel domain (IPC2002)..."

run_test \
    "src/test/resources/benchmarks/pddl/ipc2002/zenotravel/strips-automatic/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2002/zenotravel/strips-automatic/p04.pddl" \
    "Zenotravel-P04"

# ========================================
# Tests IPC2002 - Freecell Domain
# ========================================
echo "Testing Freecell domain (IPC2002)..."

run_test \
    "src/test/resources/benchmarks/pddl/ipc2002/freecell/strips-automatic/domain.pddl" \
    "src/test/resources/benchmarks/pddl/ipc2002/freecell/strips-automatic/p05.pddl" \
    "Freecell-P05"

echo "" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"
echo "TESTS TERMINÉS" >> "$OUTPUT_FILE"
echo "Date: $(date)" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"

echo ""
echo "Tous les tests sont terminés!"
echo "Résultats sauvegardés dans: $OUTPUT_FILE"
echo ""
