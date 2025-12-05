package fr.uga.pddl4j.examples.mcp;

import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.RequireKey;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.plan.SequentialPlan;
import fr.uga.pddl4j.planners.AbstractPlanner;
import fr.uga.pddl4j.planners.ProblemNotSupportedException;
import fr.uga.pddl4j.problem.DefaultProblem;
import fr.uga.pddl4j.problem.Problem;
import fr.uga.pddl4j.problem.State;
import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.operator.ConditionalEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Monte Carlo Planner using random walks to find solutions.
 *
 * @author Your Name
 * @version 1.0
 */
@CommandLine.Command(name = "MCP",
    version = "MCP 1.0",
    description = "Solves a specified planning problem using Monte Carlo random walks.",
    sortOptions = false,
    mixinStandardHelpOptions = true,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n")
public class MCP extends AbstractPlanner {

    private static final Logger LOGGER = LogManager.getLogger(MCP.class.getName());
    
    // Paramètres Monte Carlo
    private static final int MAX_ITERATIONS = 10000;
    private static final int MAX_WALK_LENGTH = 1000;
    
    private Random random;

    public MCP() {
        super();
        this.random = new Random();
    }

    @Override
    public Problem instantiate(DefaultParsedProblem problem) {
        final Problem pb = new DefaultProblem(problem);
        pb.instantiate();
        return pb;
    }

    /**
     * Vérifie si le problème est supporté par le planificateur.
     *
     * @param problem le problème à vérifier
     * @return true si supporté, false sinon
     */
    @Override
    public boolean isSupported(Problem problem) {
        if (!problem.getRequirements().isEmpty()) {
            for (RequireKey requirement : problem.getRequirements()) {
                if (requirement.equals(RequireKey.ACTION_COSTS)
                    || requirement.equals(RequireKey.CONSTRAINTS)
                    || requirement.equals(RequireKey.CONTINOUS_EFFECTS)
                    || requirement.equals(RequireKey.DERIVED_PREDICATES)
                    || requirement.equals(RequireKey.DURATIVE_ACTIONS)
                    || requirement.equals(RequireKey.DURATION_INEQUALITIES)
                    || requirement.equals(RequireKey.FLUENTS)
                    || requirement.equals(RequireKey.TIMED_INITIAL_LITERALS)
                    || requirement.equals(RequireKey.PREFERENCES)
                    || requirement.equals(RequireKey.HIERARCHY)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Plan solve(final Problem problem) throws ProblemNotSupportedException {
        // Vérifier si le problème est supporté
        if (!isSupported(problem)) {
            throw new ProblemNotSupportedException("Problem not supported");
        }
        
        LOGGER.info("* Starting Monte Carlo planning *");
        
        Plan bestPlan = null;
        int bestPlanLength = Integer.MAX_VALUE;
        
        // Effectuer plusieurs marches aléatoires
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            Plan candidatePlan = randomWalk(problem);
            
            if (candidatePlan != null) {
                int planLength = candidatePlan.size();
                if (planLength < bestPlanLength) {
                    bestPlan = candidatePlan;
                    bestPlanLength = planLength;
                    LOGGER.info("New best plan found at iteration " + iteration + " with length: " + planLength);
                }
            }
        }
        
        return bestPlan;
    }

    /**
     * Effectue une marche aléatoire depuis l'état initial.
     *
     * @param problem le problème à résoudre
     * @return un plan si l'objectif est atteint, null sinon
     */
    private Plan randomWalk(Problem problem) {
        State currentState = new State(problem.getInitialState());
        Plan plan = new SequentialPlan();
        
        for (int step = 0; step < MAX_WALK_LENGTH; step++) {
            // Vérifier si l'objectif est atteint
            if (currentState.satisfy(problem.getGoal())) {
                return plan;
            }
            
            // Obtenir les actions applicables
            List<Action> applicableActions = getApplicableActions(problem, currentState);
            
            if (applicableActions.isEmpty()) {
                return null; // Impasse
            }
            
            // Choisir une action aléatoire
            Action randomAction = applicableActions.get(random.nextInt(applicableActions.size()));
            
            // Appliquer l'action
            List<ConditionalEffect> effects = randomAction.getConditionalEffects();
            for (ConditionalEffect effect : effects) {
                if (currentState.satisfy(effect.getCondition())) {
                    currentState.apply(effect.getEffect());
                }
            }
            
            // Ajouter l'action au plan
            plan.add(step, randomAction);
        }
        
        return null; // Longueur maximale atteinte sans succès
    }

    /**
     * Obtient toutes les actions applicables dans l'état actuel.
     *
     * @param problem le problème
     * @param state l'état actuel
     * @return liste des actions applicables
     */
    private List<Action> getApplicableActions(Problem problem, State state) {
        List<Action> applicableActions = new ArrayList<>();
        
        for (Action action : problem.getActions()) {
            if (state.satisfy(action.getPrecondition())) {
                applicableActions.add(action);
            }
        }
        
        return applicableActions;
    }

    public static void main(String[] args) {
        try {
            final MCP planner = new MCP();
            CommandLine cmd = new CommandLine(planner);
            cmd.execute(args);
        } catch (IllegalArgumentException e) {
            LOGGER.fatal(e.getMessage());
        }
    }
}
