package fr.uga.pddl4j.examples.mcp;

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

import java.util.*;

/**
 * Monte Carlo Tree Search planner for solving planning problems.
 * Uses MCTS with UCB1 for node selection and random rollouts.
 */
@CommandLine.Command(name = "MCP",
    version = "MCP 1.0",
    description = "Solves a specified planning problem using Monte Carlo Tree Search.",
    sortOptions = false,
    mixinStandardHelpOptions = true,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n")
public class MCP extends AbstractPlanner {

    private static final Logger LOGGER = LogManager.getLogger(MCP.class.getName());

    /**
     * Exploration constant for UCB1 formula
     */
    private double explorationConstant;

    /**
     * Maximum depth for rollout simulations
     */
    private int maxRolloutDepth;

    /**
     * Number of MCTS iterations
     */
    private int numIterations;

    public static final String EXPLORATION_CONSTANT_SETTING = "EXPLORATION_CONSTANT";
    public static final double DEFAULT_EXPLORATION_CONSTANT = Math.sqrt(2);

    public static final String MAX_ROLLOUT_DEPTH_SETTING = "MAX_ROLLOUT_DEPTH";
    public static final int DEFAULT_MAX_ROLLOUT_DEPTH = 100;

    public static final String NUM_ITERATIONS_SETTING = "NUM_ITERATIONS";
    public static final int DEFAULT_NUM_ITERATIONS = 10000;

    /**
     * Inner class representing MCTS tree nodes
     */
    private class MCTSNode {
        State state;
        MCTSNode parent;
        List<MCTSNode> children;
        int actionIndex;
        int visits;
        double totalReward;
        List<Integer> untriedActions;

        MCTSNode(State state, MCTSNode parent, int actionIndex, Problem problem) {
            this.state = new State(state);
            this.parent = parent;
            this.children = new ArrayList<>();
            this.actionIndex = actionIndex;
            this.visits = 0;
            this.totalReward = 0.0;
            this.untriedActions = new ArrayList<>();
            
            // Initialize untried actions with applicable actions
            for (int i = 0; i < problem.getActions().size(); i++) {
                if (problem.getActions().get(i).isApplicable(state)) {
                    untriedActions.add(i);
                }
            }
        }

        /**
         * Calculate UCB1 value for this node
         */
        double ucb1(double explorationParam) {
            if (visits == 0) return Double.POSITIVE_INFINITY;
            double exploitation = totalReward / visits;
            double exploration = explorationParam * Math.sqrt(Math.log(parent.visits) / visits);
            return exploitation + exploration;
        }

        boolean isFullyExpanded() {
            return untriedActions.isEmpty();
        }

        boolean isTerminal(Problem problem) {
            return state.satisfy(problem.getGoal());
        }
    }

    public MCP() {
        this(MCP.getDefaultConfiguration());
    }

    public MCP(final PlannerConfiguration configuration) {
        super();
        this.setConfiguration(configuration);
    }

    @CommandLine.Option(names = {"-c", "--exploration"}, defaultValue = "1.414",
        paramLabel = "<constant>", description = "Set UCB1 exploration constant (default: sqrt(2))")
    public void setExplorationConstant(final double constant) {
        if (constant <= 0) {
            throw new IllegalArgumentException("Exploration constant must be > 0");
        }
        this.explorationConstant = constant;
    }

    @CommandLine.Option(names = {"-d", "--depth"}, defaultValue = "100",
        paramLabel = "<depth>", description = "Set maximum rollout depth (default: 100)")
    public void setMaxRolloutDepth(final int depth) {
        if (depth <= 0) {
            throw new IllegalArgumentException("Max rollout depth must be > 0");
        }
        this.maxRolloutDepth = depth;
    }

    @CommandLine.Option(names = {"-i", "--iterations"}, defaultValue = "10000",
        paramLabel = "<iterations>", description = "Set number of MCTS iterations (default: 10000)")
    public void setNumIterations(final int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("Number of iterations must be > 0");
        }
        this.numIterations = iterations;
    }

    public double getExplorationConstant() {
        return this.explorationConstant;
    }

    public int getMaxRolloutDepth() {
        return this.maxRolloutDepth;
    }

    public int getNumIterations() {
        return this.numIterations;
    }

    public static PlannerConfiguration getDefaultConfiguration() {
        PlannerConfiguration config = Planner.getDefaultConfiguration();
        config.setProperty(MCP.EXPLORATION_CONSTANT_SETTING, 
            Double.toString(MCP.DEFAULT_EXPLORATION_CONSTANT));
        config.setProperty(MCP.MAX_ROLLOUT_DEPTH_SETTING, 
            Integer.toString(MCP.DEFAULT_MAX_ROLLOUT_DEPTH));
        config.setProperty(MCP.NUM_ITERATIONS_SETTING, 
            Integer.toString(MCP.DEFAULT_NUM_ITERATIONS));
        return config;
    }

    @Override
    public void setConfiguration(final PlannerConfiguration configuration) {
        super.setConfiguration(configuration);
        if (configuration.getProperty(EXPLORATION_CONSTANT_SETTING) == null) {
            this.setExplorationConstant(DEFAULT_EXPLORATION_CONSTANT);
        } else {
            this.setExplorationConstant(Double.parseDouble(
                configuration.getProperty(EXPLORATION_CONSTANT_SETTING)));
        }
        if (configuration.getProperty(MAX_ROLLOUT_DEPTH_SETTING) == null) {
            this.setMaxRolloutDepth(DEFAULT_MAX_ROLLOUT_DEPTH);
        } else {
            this.setMaxRolloutDepth(Integer.parseInt(
                configuration.getProperty(MAX_ROLLOUT_DEPTH_SETTING)));
        }
        if (configuration.getProperty(NUM_ITERATIONS_SETTING) == null) {
            this.setNumIterations(DEFAULT_NUM_ITERATIONS);
        } else {
            this.setNumIterations(Integer.parseInt(
                configuration.getProperty(NUM_ITERATIONS_SETTING)));
        }
    }

    @Override
    public boolean hasValidConfiguration() {
        return super.hasValidConfiguration()
            && this.explorationConstant > 0
            && this.maxRolloutDepth > 0
            && this.numIterations > 0;
    }

    @Override
    public Problem instantiate(DefaultParsedProblem problem) {
        final Problem pb = new DefaultProblem(problem);
        pb.instantiate();
        return pb;
    }

    @Override
    public boolean isSupported(Problem problem) {
        return !problem.getRequirements().contains(RequireKey.ACTION_COSTS)
            && !problem.getRequirements().contains(RequireKey.CONSTRAINTS)
            && !problem.getRequirements().contains(RequireKey.CONTINOUS_EFFECTS)
            && !problem.getRequirements().contains(RequireKey.DERIVED_PREDICATES)
            && !problem.getRequirements().contains(RequireKey.DURATIVE_ACTIONS)
            && !problem.getRequirements().contains(RequireKey.DURATION_INEQUALITIES)
            && !problem.getRequirements().contains(RequireKey.FLUENTS)
            && !problem.getRequirements().contains(RequireKey.GOAL_UTILITIES)
            && !problem.getRequirements().contains(RequireKey.METHOD_CONSTRAINTS)
            && !problem.getRequirements().contains(RequireKey.NUMERIC_FLUENTS)
            && !problem.getRequirements().contains(RequireKey.OBJECT_FLUENTS)
            && !problem.getRequirements().contains(RequireKey.PREFERENCES)
            && !problem.getRequirements().contains(RequireKey.TIMED_INITIAL_LITERALS)
            && !problem.getRequirements().contains(RequireKey.HIERARCHY);
    }

    /**
     * Phase 1: Selection - Select most promising node using UCB1
     */
    private MCTSNode select(MCTSNode node, Problem problem) {
        while (!node.isTerminal(problem) && node.isFullyExpanded()) {
            node = Collections.max(node.children, 
                Comparator.comparingDouble(n -> n.ucb1(explorationConstant)));
        }
        return node;
    }

    /**
     * Phase 2: Expansion - Add new child node
     */
    private MCTSNode expand(MCTSNode node, Problem problem) {
        if (node.untriedActions.isEmpty()) {
            return node;
        }
        
        // Select random untried action
        int actionIdx = node.untriedActions.remove(
            new Random().nextInt(node.untriedActions.size()));
        
        Action action = problem.getActions().get(actionIdx);
        State newState = new State(node.state);
        
        // Apply action effects
        final List<ConditionalEffect> effects = action.getConditionalEffects();
        for (ConditionalEffect ce : effects) {
            if (newState.satisfy(ce.getCondition())) {
                newState.apply(ce.getEffect());
            }
        }
        
        MCTSNode child = new MCTSNode(newState, node, actionIdx, problem);
        node.children.add(child);
        return child;
    }

    /**
     * Phase 3: Simulation - Random rollout from node
     */
    private double simulate(MCTSNode node, Problem problem) {
        State currentState = new State(node.state);
        Random random = new Random();
        int depth = 0;
        
        while (!currentState.satisfy(problem.getGoal()) && depth < maxRolloutDepth) {
            List<Integer> applicableActions = new ArrayList<>();
            for (int i = 0; i < problem.getActions().size(); i++) {
                if (problem.getActions().get(i).isApplicable(currentState)) {
                    applicableActions.add(i);
                }
            }
            
            if (applicableActions.isEmpty()) {
                break; // Dead end
            }
            
            // Select random applicable action
            int actionIdx = applicableActions.get(random.nextInt(applicableActions.size()));
            Action action = problem.getActions().get(actionIdx);
            
            // Apply action
            final List<ConditionalEffect> effects = action.getConditionalEffects();
            for (ConditionalEffect ce : effects) {
                if (currentState.satisfy(ce.getCondition())) {
                    currentState.apply(ce.getEffect());
                }
            }
            depth++;
        }
        
        // Reward: 1 if goal reached, 0 otherwise; penalize by depth
        return currentState.satisfy(problem.getGoal()) ? (1.0 - depth / (double)maxRolloutDepth) : 0.0;
    }

    /**
     * Phase 4: Backpropagation - Update nodes with simulation result
     */
    private void backpropagate(MCTSNode node, double reward) {
        while (node != null) {
            node.visits++;
            node.totalReward += reward;
            node = node.parent;
        }
    }

    /**
     * Extract plan from root to best child
     */
    private Plan extractPlan(MCTSNode root, Problem problem) {
        Plan plan = new SequentialPlan();
        MCTSNode current = root;
        
        // Follow most visited path
        while (!current.children.isEmpty()) {
            current = Collections.max(current.children, 
                Comparator.comparingInt(n -> n.visits));
            
            if (current.actionIndex != -1) {
                plan.add(problem.getActions().get(current.actionIndex));
            }
            
            if (current.isTerminal(problem)) {
                break;
            }
        }
        
        return plan.size() > 0 && current.isTerminal(problem) ? plan : null;
    }

    /**
     * Main MCTS algorithm
     */
    public Plan mcts(Problem problem) throws ProblemNotSupportedException {
        if (!this.isSupported(problem)) {
            throw new ProblemNotSupportedException("Problem not supported");
        }

        final State init = new State(problem.getInitialState());
        MCTSNode root = new MCTSNode(init, null, -1, problem);

        final int timeout = this.getTimeout() * 1000;
        final long startTime = System.currentTimeMillis();
        int iteration = 0;

        while (iteration < numIterations && 
               (System.currentTimeMillis() - startTime) < timeout) {
            
            // Phase 1: Selection
            MCTSNode node = select(root, problem);
            
            // Phase 2: Expansion
            if (!node.isTerminal(problem) && !node.untriedActions.isEmpty()) {
                node = expand(node, problem);
            }
            
            // Phase 3: Simulation
            double reward = simulate(node, problem);
            
            // Phase 4: Backpropagation
            backpropagate(node, reward);
            
            iteration++;
        }

        LOGGER.info("MCTS completed " + iteration + " iterations");
        return extractPlan(root, problem);
    }

    @Override
    public Plan solve(final Problem problem) {
        LOGGER.info("* Starting MCTS search \n");
        final long begin = System.currentTimeMillis();
        Plan plan = null;
        try {
            plan = this.mcts(problem);
        } catch (ProblemNotSupportedException e) {
            LOGGER.fatal(e.getMessage());
        }
        final long end = System.currentTimeMillis();
        
        if (plan != null) {
            LOGGER.info("* MCTS search succeeded\n");
            this.getStatistics().setTimeToSearch(end - begin);
        } else {
            LOGGER.info("* MCTS search failed\n");
        }
        return plan;
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
