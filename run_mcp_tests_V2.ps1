# Script pour exécuter les tests de planification sur différents domaines
# et collecter les résultats

$OUTPUT_FILE = "results_$(Get-Date -Format 'yyyyMMdd_HHmmss').txt"
$TIMEOUT = 1000

"========================================" | Out-File -FilePath $OUTPUT_FILE -Encoding UTF8
"RÉSULTATS DE PLANIFICATION" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
"Algorithm: MCP" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
"Date: $(Get-Date)" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
"========================================" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
"" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8

# Fonction pour exécuter un test
function Run-Test {
    param(
        [string]$domain,
        [string]$problem,
        [string]$name
    )
    
    "" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    "========================================" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    "TEST: $name" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    "Domain: $domain" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    "Problem: $problem" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    "========================================" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    "" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    
    Write-Host "Exécution: $name..."
    
    # Exécuter le planificateur et capturer la sortie
    $output = java -cp "classes;lib/pddl4j-4.0.0.jar" fr.uga.pddl4j.examples.mcp.MCP `
        $domain $problem `
        -t $TIMEOUT 2>&1
    
    $output | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    
    "" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    "----------------------------------------" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
    "" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
}

# ========================================
# Tests IPC2002 - Depots Domain (strips-automatic)
# ========================================
Write-Host "Testing Depots domain (IPC2002)..."

Run-Test `
    -domain "src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/domain.pddl" `
    -problem "src/test/resources/benchmarks/pddl/ipc2002/depots/strips-automatic/p01.pddl" `
    -name "Depots-P01"


# ========================================
# Tests IPC2002 - Driverlog Domain
# ========================================
Write-Host "Testing Driverlog domain (IPC2002)..."

Run-Test `
    -domain "src/test/resources/benchmarks/pddl/ipc2002/driverlog/strips-automatic/domain.pddl" `
    -problem "src/test/resources/benchmarks/pddl/ipc2002/driverlog/strips-automatic/p02.pddl" `
    -name "Driverlog-P02"

# ========================================
# Tests IPC2006 - Openstacks Domain
# ========================================
Write-Host "Testing Openstacks domain (IPC2006)..."

Run-Test `
    -domain "src/test/resources/benchmarks/pddl/ipc2006/openstacks/propositional/domain.pddl" `
    -problem "src/test/resources/benchmarks/pddl/ipc2006/openstacks/propositional/p03.pddl" `
    -name "Openstacks-P03"

# ========================================
# Tests IPC2002 - Zenotravel Domain
# ========================================
Write-Host "Testing Zenotravel domain (IPC2002)..."

Run-Test `
    -domain "src/test/resources/benchmarks/pddl/ipc2002/zenotravel/strips-automatic/domain.pddl" `
    -problem "src/test/resources/benchmarks/pddl/ipc2002/zenotravel/strips-automatic/p04.pddl" `
    -name "Zenotravel-P04"

# ========================================
# Tests IPC2002 - Freecell Domain
# ========================================
Write-Host "Testing Freecell domain (IPC2002)..."

Run-Test `
    -domain "src/test/resources/benchmarks/pddl/ipc2002/freecell/strips-automatic/domain.pddl" `
    -problem "src/test/resources/benchmarks/pddl/ipc2002/freecell/strips-automatic/p05.pddl" `
    -name "Freecell-P05"

"" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
"========================================" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
"TESTS TERMINÉS" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
"Date: $(Get-Date)" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8
"========================================" | Out-File -FilePath $OUTPUT_FILE -Append -Encoding UTF8

Write-Host ""
Write-Host "Tous les tests sont terminés!"
Write-Host "Résultats sauvegardés dans: $OUTPUT_FILE"
Write-Host ""
