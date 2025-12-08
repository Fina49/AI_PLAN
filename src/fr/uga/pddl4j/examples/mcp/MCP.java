package fr.uga.pddl4j.examples.mcp;

import fr.uga.pddl4j.examples.Node;
import fr.uga.pddl4j.heuristics.state.StateHeuristic;
import fr.uga.pddl4j.parser.DefaultParsedProblem;
import fr.uga.pddl4j.parser.RequireKey;
import fr.uga.pddl4j.plan.Plan;
import fr.uga.pddl4j.plan.SequentialPlan;
import fr.uga.pddl4j.planners.AbstractPlanner;
import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.PlannerConfiguration;
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
 * The class implements a Monte Carlo Random Walk planner for solving planning problems.
 * It uses local search with random walks to explore the state space.
 *
 * @author D. Pellier (adapted)
 * @version 1.0 - 08.12.2024
 */
@CommandLine.Command(name = "MCP",
    version = "MCP 1.0",
    description = "Solves a specified planning problem using Monte Carlo Random Walk strategy.",
    sortOptions = false,
    mixinStandardHelpOptions = true,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n")

public class MCP extends AbstractPlanner {

    /**
     * The class logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(MCP.class.getName());

    /**
     * The name of the heuristic used by the planner.
     */
    private StateHeuristic.Name heuristic;

    /**
     * Maximum number of steps before restart.
     */
    private int maxSteps;

    /**
     * Number of random walks per iteration.
     */
    private int numWalk;

    /**
     * Length of each random walk.
     */
    private int lengthWalk;

    /**
     * Random number generator.
     */
    private Random random;

    /**
     * The HEURISTIC property used for planner configuration.
     */
    public static final String HEURISTIC_SETTING = "HEURISTIC";

    /**
     * The default value of the HEURISTIC property.
     */
    public static final StateHeuristic.Name DEFAULT_HEURISTIC = StateHeuristic.Name.FAST_FORWARD;

    /**
     * The MAX_STEPS property used for planner configuration.
     */
    public static final String MAX_STEPS_SETTING = "MAX_STEPS";

    /**
     * The default value of the MAX_STEPS property.
     */
    public static final int DEFAULT_MAX_STEPS = 1000;

    /**
     * The NUM_WALK property used for planner configuration.
     */
    public static final String NUM_WALK_SETTING = "NUM_WALK";

    /**
     * The default value of the NUM_WALK property.
     */
    public static final int DEFAULT_NUM_WALK = 10;

    /**
     * The LENGTH_WALK property used for planner configuration.
     */
    public static final String LENGTH_WALK_SETTING = "LENGTH_WALK";

    /**
     * The default value of the LENGTH_WALK property.
     */
    public static final int DEFAULT_LENGTH_WALK = 100;

    /**
     * Creates a new MCP planner with the default configuration.
     */
    public MCP() {
        this(MCP.getDefaultConfiguration());
    }

    /**
     * Creates a new MCP planner with a specified configuration.
     *
     * @param configuration the configuration of the planner.
     */
    public MCP(final PlannerConfiguration configuration) {
        super();
        this.setConfiguration(configuration);
        this.random = new Random();
    }

    /**
     * Set the name of heuristic used by the planner to solve a planning problem.
     *
     * @param heuristic the name of the heuristic.
     */
    @CommandLine.Option(names = {"-e", "--heuristic"}, defaultValue = "FAST_FORWARD",
        description = "Set the heuristic : AJUSTED_SUM, AJUSTED_SUM2, AJUSTED_SUM2M, COMBO, "
            + "MAX, FAST_FORWARD SET_LEVEL, SUM, SUM_MUTEX (preset: FAST_FORWARD)")
    public void setHeuristic(StateHeuristic.Name heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * Returns the name of the heuristic used by the planner.
     *
     * @return the name of the heuristic.
     */
    public final StateHeuristic.Name getHeuristic() {
        return this.heuristic;
    }

    /**
     * Sets the maximum number of steps before restart.
     *
     * @param maxSteps the maximum number of steps.
     */
    @CommandLine.Option(names = {"-m", "--max-steps"}, defaultValue = "1000",
        paramLabel = "<steps>", description = "Set the maximum steps before restart (preset 1000).")
    public void setMaxSteps(final int maxSteps) {
        if (maxSteps <= 0) {
            throw new IllegalArgumentException("Max steps must be > 0");
        }
        this.maxSteps = maxSteps;
    }

    /**
     * Returns the maximum number of steps before restart.
     *
     * @return the maximum number of steps.
     */
    public final int getMaxSteps() {
        return this.maxSteps;
    }

    /**
     * Sets the number of random walks per iteration.
     *
     * @param numWalk the number of walks.
     */
    @CommandLine.Option(names = {"-nw", "--num-walk"}, defaultValue = "10",
        paramLabel = "<walks>", description = "Set the number of random walks (preset 10).")
    public void setNumWalk(final int numWalk) {
        if (numWalk <= 0) {
            throw new IllegalArgumentException("Number of walks must be > 0");
        }
        this.numWalk = numWalk;
    }

    /**
     * Returns the number of random walks per iteration.
     *
     * @return the number of walks.
     */
    public final int getNumWalk() {
        return this.numWalk;
    }

    /**
     * Sets the length of each random walk.
     *
     * @param lengthWalk the length of walk.
     */
    @CommandLine.Option(names = {"-lw", "--length-walk"}, defaultValue = "100",
        paramLabel = "<length>", description = "Set the length of each random walk (preset 100).")
    public void setLengthWalk(final int lengthWalk) {
        if (lengthWalk <= 0) {
            throw new IllegalArgumentException("Length of walk must be > 0");
        }
        this.lengthWalk = lengthWalk;
    }

    /**
     * Returns the length of each random walk.
     *
     * @return the length of walk.
     */
    public final int getLengthWalk() {
        return this.lengthWalk;
    }

    /**
     * Returns the default configuration of the planner.
     *
     * @return the default configuration of the planner.
     * @see PlannerConfiguration
     */
    public static PlannerConfiguration getDefaultConfiguration() {
        PlannerConfiguration config = Planner.getDefaultConfiguration();
        config.setProperty(MCP.HEURISTIC_SETTING, MCP.DEFAULT_HEURISTIC.toString());
        config.setProperty(MCP.MAX_STEPS_SETTING, Integer.toString(MCP.DEFAULT_MAX_STEPS));
        config.setProperty(MCP.NUM_WALK_SETTING, Integer.toString(MCP.DEFAULT_NUM_WALK));
        config.setProperty(MCP.LENGTH_WALK_SETTING, Integer.toString(MCP.DEFAULT_LENGTH_WALK));
        return config;
    }

    /**
     * Checks the planner configuration and returns if the configuration is valid.
     *
     * @return <code>true</code> if the configuration is valid <code>false</code> otherwise.
     */
    public boolean hasValidConfiguration() {
        return super.hasValidConfiguration()
            && this.getHeuristic() != null
            && this.getMaxSteps() > 0
            && this.getNumWalk() > 0
            && this.getLengthWalk() > 0;
    }

    /**
     * Sets the configuration of the planner.
     *
     * @param configuration the configuration to set.
     */
    @Override
    public void setConfiguration(final PlannerConfiguration configuration) {
        super.setConfiguration(configuration);
        if (configuration.getProperty(MCP.HEURISTIC_SETTING) == null) {
            this.setHeuristic(MCP.DEFAULT_HEURISTIC);
        } else {
            this.setHeuristic(StateHeuristic.Name.valueOf(configuration.getProperty(
                MCP.HEURISTIC_SETTING)));
        }
        if (configuration.getProperty(MCP.MAX_STEPS_SETTING) == null) {
            this.setMaxSteps(MCP.DEFAULT_MAX_STEPS);
        } else {
            this.setMaxSteps(Integer.parseInt(configuration.getProperty(MCP.MAX_STEPS_SETTING)));
        }
        if (configuration.getProperty(MCP.NUM_WALK_SETTING) == null) {
            this.setNumWalk(MCP.DEFAULT_NUM_WALK);
        } else {
            this.setNumWalk(Integer.parseInt(configuration.getProperty(MCP.NUM_WALK_SETTING)));
        }
        if (configuration.getProperty(MCP.LENGTH_WALK_SETTING) == null) {
            this.setLengthWalk(MCP.DEFAULT_LENGTH_WALK);
        } else {
            this.setLengthWalk(Integer.parseInt(configuration.getProperty(MCP.LENGTH_WALK_SETTING)));
        }
    }

    /**
     * Instantiates the planning problem from a parsed problem.
     *
     * @param problem the problem to instantiate.
     * @return the instantiated planning problem or null if the problem cannot be instantiated.
     */
    @Override
    public Problem instantiate(DefaultParsedProblem problem) {
        final Problem pb = new DefaultProblem(problem);
        pb.instantiate();
        return pb;
    }

    /**
     * Returns if a specified problem is supported by the planner.
     *
     * @param problem the problem to test.
     * @return <code>true</code> if the problem is supported <code>false</code> otherwise.
     */
    @Override
    public boolean isSupported(Problem problem) {
        return (problem.getRequirements().contains(RequireKey.ACTION_COSTS)
            || problem.getRequirements().contains(RequireKey.CONSTRAINTS)
            || problem.getRequirements().contains(RequireKey.CONTINOUS_EFFECTS)
            || problem.getRequirements().contains(RequireKey.DERIVED_PREDICATES)
            || problem.getRequirements().contains(RequireKey.DURATIVE_ACTIONS)
            || problem.getRequirements().contains(RequireKey.DURATION_INEQUALITIES)
            || problem.getRequirements().contains(RequireKey.FLUENTS)
            || problem.getRequirements().contains(RequireKey.GOAL_UTILITIES)
            || problem.getRequirements().contains(RequireKey.METHOD_CONSTRAINTS)
            || problem.getRequirements().contains(RequireKey.NUMERIC_FLUENTS)
            || problem.getRequirements().contains(RequireKey.OBJECT_FLUENTS)
            || problem.getRequirements().contains(RequireKey.PREFERENCES)
            || problem.getRequirements().contains(RequireKey.TIMED_INITIAL_LITERALS)
            || problem.getRequirements().contains(RequireKey.HIERARCHY))
            ? false : true;
    }

    /**
     * Checks if a state is a dead-end (no applicable actions).
     *
     * @param state   the state to check.
     * @param problem the problem.
     * @return true if the state is a dead-end, false otherwise.
     */
    private boolean isDeadEnd(final State state, final Problem problem) {
        for (Action action : problem.getActions()) {
            if (action.isApplicable(state)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets all applicable actions for a given state.
     *
     * @param state   the current state.
     * @param problem the problem.
     * @return a list of applicable action indices.
     */
    private List<Integer> getApplicableActions(final State state, final Problem problem) {
        List<Integer> applicableActions = new ArrayList<>();
        for (int i = 0; i < problem.getActions().size(); i++) {
            if (problem.getActions().get(i).isApplicable(state)) {
                applicableActions.add(i);
            }
        }
        return applicableActions;
    }

    /**
     * Applies an action to a state and returns the resulting state.
     *
     * @param state      the current state.
     * @param actionIdx  the action index to apply.
     * @param problem    the problem.
     * @return the resulting state after applying the action.
     */
    private State applyAction(final State state, final int actionIdx, final Problem problem) {
        State nextState = new State(state);
        Action action = problem.getActions().get(actionIdx);
        final List<ConditionalEffect> effects = action.getConditionalEffects();
        for (ConditionalEffect ce : effects) {
            if (state.satisfy(ce.getCondition())) {
                nextState.apply(ce.getEffect());
            }
        }
        return nextState;
    }

    /**
     * Algorithm 2: Pure Random Walk
     * Performs multiple random walks from the current state and returns the state
     * with the minimum heuristic value found.
     *
     * @param current    the current state.
     * @param problem    the problem.
     * @param heuristic  the heuristic function.
     * @return the state with minimum heuristic found, or current state if no better state found.
     */
    private Node pureRandomWalk(final Node current, final Problem problem, 
                                final StateHeuristic heuristic) {
        double hmin = Double.POSITIVE_INFINITY;
        Node smin = null;

        // Perform NUM_WALK random walks
        for (int i = 0; i < this.numWalk; i++) {
            State s = new State(current);
            Node walkPath = new Node(s, current, -1, current.getCost(), 
                                     heuristic.estimate(s, problem.getGoal()));

            // Perform LENGTH_WALK steps in this walk
            for (int j = 0; j < this.lengthWalk; j++) {
                List<Integer> applicableActions = getApplicableActions(s, problem);
                
                // If no applicable actions, break this walk
                if (applicableActions.isEmpty()) {
                    break;
                }

                // Select a random action uniformly
                int randomIdx = random.nextInt(applicableActions.size());
                int actionIdx = applicableActions.get(randomIdx);

                // Apply the action
                State nextState = applyAction(s, actionIdx, problem);
                
                // Create node for tracking
                Node nextNode = new Node(nextState, walkPath, actionIdx, 
                                        walkPath.getCost() + 1,
                                        heuristic.estimate(nextState, problem.getGoal()));
                
                // Check if goal is satisfied
                if (nextState.satisfy(problem.getGoal())) {
                    return nextNode;
                }

                s = nextState;
                walkPath = nextNode;
            }

            // Check if this walk found a better state
            double h = heuristic.estimate(s, problem.getGoal());
            if (h < hmin) {
                smin = walkPath;
                hmin = h;
            }
        }

        // Return the best state found, or current state if none better
        if (smin == null) {
            return current;
        } else {
            return smin;
        }
    }

    /**
     * Extracts a plan from a specified node.
     *
     * @param node    the node.
     * @param problem the problem.
     * @return the plan extracted from the specified node.
     */
    private Plan extractPlan(final Node node, final Problem problem) {
        Node n = node;
        final Plan plan = new SequentialPlan();
        while (n.getAction() != -1) {
            final Action a = problem.getActions().get(n.getAction());
            plan.add(0, a);
            n = n.getParent();
        }
        return plan;
    }

    /**
     * Algorithm 1: Local Search Using Monte Carlo Random Walks
     * Search a solution plan for a planning problem using Monte Carlo random walks.
     *
     * @param problem the problem to solve.
     * @return a plan solution for the problem or null if there is no solution.
     * @throws ProblemNotSupportedException if the problem to solve is not supported by the planner.
     */
    public Plan monteCarloSearch(Problem problem) throws ProblemNotSupportedException {
        // Check if the problem is supported by the planner
        if (!this.isSupported(problem)) {
            throw new ProblemNotSupportedException("Problem not supported");
        }

        // Create an instance of the heuristic to use
        final StateHeuristic heuristic = StateHeuristic.getInstance(this.getHeuristic(), problem);

        // Get the initial state from the planning problem
        final State init = new State(problem.getInitialState());
        Node s = new Node(init, null, -1, 0, heuristic.estimate(init, problem.getGoal()));
        
        // Store the initial state for restart
        final Node s0 = s;

        double hmin = s.getHeuristic();
        int counter = 0;

        // Set the timeout in ms allocated to the search
        final int timeout = this.getTimeout() * 1000;
        final long startTime = System.currentTimeMillis();

        // Main search loop
        while (!s.satisfy(problem.getGoal())) {
            // Check timeout
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime >= timeout) {
                LOGGER.info("* Timeout reached");
                return null;
            }

            // Restart if counter exceeds MAX_STEPS or dead-end is reached
            if (counter > this.maxSteps || isDeadEnd(s, problem)) {
                LOGGER.info("* Restarting from initial state (counter: " + counter + ")");
                s = new Node(s0, null, -1, 0, heuristic.estimate(s0, problem.getGoal()));
                hmin = s.getHeuristic();
                counter = 0;
            }

            // Perform Monte Carlo random walk
            Node nextS = pureRandomWalk(s, problem, heuristic);

            // Check if goal was found during random walk
            if (nextS.satisfy(problem.getGoal())) {
                return extractPlan(nextS, problem);
            }

            // Update state and counter based on heuristic improvement
            if (nextS.getHeuristic() < hmin) {
                hmin = nextS.getHeuristic();
                counter = 0;
                LOGGER.info("* New minimum heuristic: " + hmin);
            } else {
                counter++;
            }

            s = nextS;
        }

        // Goal satisfied, extract and return the plan
        return extractPlan(s, problem);
    }

    /**
     * Search a solution plan to a specified domain and problem using Monte Carlo Random Walk.
     *
     * @param problem the problem to solve.
     * @return the plan found or null if no plan was found.
     */
    @Override
    public Plan solve(final Problem problem) {
        LOGGER.info("* Starting Monte Carlo Random Walk search \n");
        // Search a solution
        final long begin = System.currentTimeMillis();
        Plan plan = null;
        try {
            plan = this.monteCarloSearch(problem);
        } catch (ProblemNotSupportedException e) {
            LOGGER.fatal(e.getMessage());
        }
        final long end = System.currentTimeMillis();
        // If a plan is found update the statistics of the planner
        // and log search information
        if (plan != null) {
            LOGGER.info("* Monte Carlo search succeeded\n");
            this.getStatistics().setTimeToSearch(end - begin);
        } else {
            LOGGER.info("* Monte Carlo search failed\n");
        }
        // Return the plan found or null if the search fails.
        return plan;
    }

    /**
     * The main method of the <code>MCP</code> planner.
     *
     * @param args the arguments of the command line.
     */
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
