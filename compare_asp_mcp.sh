#!/bin/bash

# Script pour comparer ASP et MCP sur les benchmarks IPC2000
# Ce script exécute les deux algorithmes sur chaque problème et compare les résultats

OUTPUT_FILE="comparison_results_$(date +%Y%m%d_%H%M%S).txt"
TIMEOUT=1000
BASE_PATH="src/test/resources/benchmarks/pddl/ipc2000"

echo "========================================" > "$OUTPUT_FILE"
echo "COMPARAISON ASP vs MCP" >> "$OUTPUT_FILE"
echo "Date: $(date)" >> "$OUTPUT_FILE"
echo "Timeout: ${TIMEOUT}s" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Fonction pour exécuter un test et extraire les résultats
run_planner() {
    local algorithm=$1
    local domain=$2
    local problem=$3
    local temp_file=$4
    
    if [ "$algorithm" = "ASP" ]; then
        java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.asp.ASP \
            "$domain" "$problem" -t "$TIMEOUT" > "$temp_file" 2>&1
    else
        java -cp classes:lib/pddl4j-4.0.0.jar fr.uga.pddl4j.examples.mcp.MCP \
            "$domain" "$problem" -t "$TIMEOUT" > "$temp_file" 2>&1
    fi
}

# Fonction pour comparer deux planificateurs sur un problème
compare_planners() {
    local domain=$1
    local problem=$2
    local test_name=$3
    
    echo "Testing: $test_name"
    
    local temp_asp="/tmp/asp_$$.txt"
    local temp_mcp="/tmp/mcp_$$.txt"
    
    # Exécuter ASP
    run_planner "ASP" "$domain" "$problem" "$temp_asp"
    
    # Exécuter MCP
    run_planner "MCP" "$domain" "$problem" "$temp_mcp"
    
    # Écrire les résultats dans le fichier de comparaison
    echo "" >> "$OUTPUT_FILE"
    echo "========================================" >> "$OUTPUT_FILE"
    echo "TEST: $test_name" >> "$OUTPUT_FILE"
    echo "Domain: $domain" >> "$OUTPUT_FILE"
    echo "Problem: $problem" >> "$OUTPUT_FILE"
    echo "========================================" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    
    echo "--- ASP Results ---" >> "$OUTPUT_FILE"
    cat "$temp_asp" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    
    echo "--- MCP Results ---" >> "$OUTPUT_FILE"
    cat "$temp_mcp" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    
    # Extraction et comparaison des métriques
    echo "--- Comparison Summary ---" >> "$OUTPUT_FILE"
    
    # Extraire le nombre d'actions du plan (compter les lignes numérotées du plan)
    local asp_plan_length=$(grep -E "^[0-9]+:" "$temp_asp" | wc -l | tr -d ' ')
    local mcp_plan_length=$(grep -E "^[0-9]+:" "$temp_mcp" | wc -l | tr -d ' ')
    
    # Extraire le temps total
    local asp_total=$(grep "seconds total time" "$temp_asp" | awk '{print $1}')
    local mcp_total=$(grep "seconds total time" "$temp_mcp" | awk '{print $1}')
    
    # Extraire le temps de recherche
    local asp_search=$(grep "seconds searching" "$temp_asp" | awk '{print $1}')
    local mcp_search=$(grep "seconds searching" "$temp_mcp" | awk '{print $1}')
    
    # Vérifier si un plan a été trouvé
    local asp_success=$(grep -c "search succeeded" "$temp_asp")
    local mcp_success=$(grep -c "search succeeded" "$temp_mcp")
    
    echo "ASP:" >> "$OUTPUT_FILE"
    if [ "$asp_success" -gt 0 ]; then
        echo "  - Status: SUCCESS" >> "$OUTPUT_FILE"
        echo "  - Plan length: $asp_plan_length actions" >> "$OUTPUT_FILE"
    else
        echo "  - Status: FAILED" >> "$OUTPUT_FILE"
    fi
    [ -n "$asp_total" ] && echo "  - Total time: ${asp_total}s" >> "$OUTPUT_FILE"
    [ -n "$asp_search" ] && echo "  - Search time: ${asp_search}s" >> "$OUTPUT_FILE"
    
    echo "" >> "$OUTPUT_FILE"
    echo "MCP:" >> "$OUTPUT_FILE"
    if [ "$mcp_success" -gt 0 ]; then
        echo "  - Status: SUCCESS" >> "$OUTPUT_FILE"
        echo "  - Plan length: $mcp_plan_length actions" >> "$OUTPUT_FILE"
    else
        echo "  - Status: FAILED" >> "$OUTPUT_FILE"
    fi
    [ -n "$mcp_total" ] && echo "  - Total time: ${mcp_total}s" >> "$OUTPUT_FILE"
    [ -n "$mcp_search" ] && echo "  - Search time: ${mcp_search}s" >> "$OUTPUT_FILE"
    
    echo "" >> "$OUTPUT_FILE"
    if [ "$asp_success" -gt 0 ] && [ "$mcp_success" -gt 0 ]; then
        if [ "$asp_plan_length" -lt "$mcp_plan_length" ]; then
            local diff=$((mcp_plan_length - asp_plan_length))
            echo "  => ASP found a SHORTER plan by $diff actions" >> "$OUTPUT_FILE"
        elif [ "$mcp_plan_length" -lt "$asp_plan_length" ]; then
            local diff=$((asp_plan_length - mcp_plan_length))
            echo "  => MCP found a SHORTER plan by $diff actions" >> "$OUTPUT_FILE"
        else
            echo "  => Both found plans of EQUAL length" >> "$OUTPUT_FILE"
        fi
    fi
    
    echo "" >> "$OUTPUT_FILE"
    echo "----------------------------------------" >> "$OUTPUT_FILE"
    
    # Nettoyer les fichiers temporaires
    rm -f "$temp_asp" "$temp_mcp"
}

# Fonction pour tester tous les problèmes d'un domaine
test_domain() {
    local domain_path=$1
    local domain_name=$2
    local variant=$3
    local num_problems=$4
    
    echo ""
    echo "========================================="
    echo "Testing domain: $domain_name ($variant)"
    echo "========================================="
    
    local domain_file="$domain_path/domain.pddl"
    
    if [ ! -f "$domain_file" ]; then
        echo "Warning: Domain file not found: $domain_file"
        return
    fi
    
    # Tester les N premiers problèmes
    for i in $(seq 1 $num_problems); do
        local problem_num=$(printf "%03d" $i)
        local problem_file="$domain_path/p${problem_num}.pddl"
        
        # Pour freecell et logistics, le format peut être différent
        if [ ! -f "$problem_file" ]; then
            problem_num=$(printf "%02d" $i)
            problem_file="$domain_path/p${problem_num}.pddl"
        fi
        
        if [ -f "$problem_file" ]; then
            compare_planners "$domain_file" "$problem_file" "${domain_name}-${variant}-P${problem_num}"
        fi
    done
}

# ========================================
# BLOCKS DOMAIN
# ========================================
echo ""
echo "Starting Blocks domain tests..."

# Blocks - STRIPS Typed (tester 15 problèmes)
test_domain "$BASE_PATH/blocks/strips-typed" "Blocks" "strips-typed" 15

# Blocks - STRIPS Untyped (tester 10 problèmes)
test_domain "$BASE_PATH/blocks/strips-untyped" "Blocks" "strips-untyped" 10

# ========================================
# ELEVATOR DOMAIN
# ========================================
echo ""
echo "Starting Elevator domain tests..."

# Elevator - STRIPS Simple Typed (tester 15 problèmes)
test_domain "$BASE_PATH/elevator/strips-simple-typed" "Elevator" "strips-simple-typed" 15

# Elevator - STRIPS Simple Untyped (tester 10 problèmes)
test_domain "$BASE_PATH/elevator/strips-simple-untyped" "Elevator" "strips-simple-untyped" 10

# ========================================
# FREECELL DOMAIN
# ========================================
echo ""
echo "Starting Freecell domain tests..."

# Freecell - STRIPS Typed (tester 15 problèmes)
test_domain "$BASE_PATH/freecell/strips-typed" "Freecell" "strips-typed" 15

# Freecell - STRIPS Untyped (tester 10 problèmes)
test_domain "$BASE_PATH/freecell/strips-untyped" "Freecell" "strips-untyped" 10

# ========================================
# LOGISTICS DOMAIN
# ========================================
echo ""
echo "Starting Logistics domain tests..."

# Logistics - STRIPS Typed (tester 15 problèmes)
test_domain "$BASE_PATH/logistics/strips-typed" "Logistics" "strips-typed" 15

# Logistics - STRIPS Untyped (tester 10 problèmes)
test_domain "$BASE_PATH/logistics/strips-untyped" "Logistics" "strips-untyped" 10

# ========================================
# SCHEDULE DOMAIN (ADL - peut ne pas fonctionner avec ASP/MCP)
# ========================================
echo ""
echo "Starting Schedule domain tests (ADL)..."

# Schedule - ADL Typed (tester 5 problèmes)
test_domain "$BASE_PATH/schedule/adl-typed" "Schedule" "adl-typed" 5

# Schedule - ADL Untyped (tester 5 problèmes)
test_domain "$BASE_PATH/schedule/adl-untyped" "Schedule" "adl-untyped" 5

# ========================================
# Résumé final
# ========================================
echo "" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"
echo "TESTS TERMINÉS" >> "$OUTPUT_FILE"
echo "Date: $(date)" >> "$OUTPUT_FILE"
echo "========================================" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"
echo "Pour analyser les résultats:" >> "$OUTPUT_FILE"
echo "  - Rechercher 'total time' pour les temps d'exécution" >> "$OUTPUT_FILE"
echo "  - Rechercher 'plan length' pour la longueur des plans" >> "$OUTPUT_FILE"
echo "  - Rechercher 'fail' ou 'error' pour les échecs" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

echo ""
echo "========================================="
echo "Tous les tests sont terminés!"
echo "Résultats sauvegardés dans: $OUTPUT_FILE"
echo "========================================="
echo ""
