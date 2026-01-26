package com.dify.ai.service;

import com.dify.ai.domain.model.Action;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ActionDependencyResolver creates action dependency resolution for execution ordering.
 * It determines the correct order to execute actions based on their dependencies.
 */
@Service
public class ActionDependencyResolver {

    /**
     * Orders actions based on their dependencies.
     * Uses a topological sort algorithm to determine the execution order.
     *
     * @param availableActions the list of actions that are available to execute
     * @return a list of actions ordered by their dependencies
     */
    public List<Action> orderActionsByDependencies(List<Action> availableActions) {
        if (availableActions == null || availableActions.isEmpty()) {
            return new ArrayList<>();
        }

        // Create a map of actionId to Action for quick lookup
        Map<String, Action> actionMap = new HashMap<>();
        for (Action action : availableActions) {
            actionMap.put(action.getActionId(), action);
        }

        // Build dependency graph: actionId -> set of actionIds that depend on it
        Map<String, Set<String>> dependencyGraph = buildDependencyGraph(availableActions);

        // Perform topological sort
        return topologicalSort(actionMap, dependencyGraph);
    }

    /**
     * Builds a dependency graph where each key is an actionId and the value is a set of actionIds
     * that depend on the key action (i.e., the key action is a prerequisite for the value actions).
     *
     * @param actions the list of actions
     * @return the dependency graph
     */
    private Map<String, Set<String>> buildDependencyGraph(List<Action> actions) {
        Map<String, Set<String>> graph = new HashMap<>();

        for (Action action : actions) {
            // For each action, find other actions that require facts this action produces
            for (Action otherAction : actions) {
                if (!action.getActionId().equals(otherAction.getActionId())) {
                    // Check if otherAction requires any facts that this action produces
                    boolean hasDependency = action.getProduces().stream()
                            .anyMatch(otherAction.getRequires()::contains);

                    if (hasDependency) {
                        // otherAction depends on action (action must come before otherAction)
                        graph.computeIfAbsent(action.getActionId(), k -> new HashSet<>())
                                .add(otherAction.getActionId());
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Performs topological sort on the actions based on their dependencies.
     *
     * @param actionMap map of actionId to Action
     * @param dependencyGraph the dependency graph
     * @return the sorted list of actions
     */
    private List<Action> topologicalSort(Map<String, Action> actionMap, Map<String, Set<String>> dependencyGraph) {
        // Calculate in-degrees (number of prerequisites for each action)
        Map<String, Integer> inDegrees = new HashMap<>();
        Set<String> allActionIds = new HashSet<>(actionMap.keySet());

        for (String actionId : allActionIds) {
            inDegrees.put(actionId, 0);
        }

        for (Set<String> dependentActions : dependencyGraph.values()) {
            for (String dependentActionId : dependentActions) {
                inDegrees.put(dependentActionId, inDegrees.get(dependentActionId) + 1);
            }
        }

        // Find actions with no prerequisites (in-degree = 0)
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegrees.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
            }
        }

        List<Action> sortedActions = new ArrayList<>();
        while (!queue.isEmpty()) {
            String currentActionId = queue.poll();
            sortedActions.add(actionMap.get(currentActionId));

            // For each action that depends on the current action, reduce its in-degree
            Set<String> dependents = dependencyGraph.get(currentActionId);
            if (dependents != null) {
                for (String dependentId : dependents) {
                    inDegrees.put(dependentId, inDegrees.get(dependentId) - 1);
                    if (inDegrees.get(dependentId) == 0) {
                        queue.offer(dependentId);
                    }
                }
            }
        }

        // Check for cycles (if not all actions were processed)
        if (sortedActions.size() != allActionIds.size()) {
            throw new IllegalStateException("Cycle detected in action dependencies");
        }

        return sortedActions;
    }

    /**
     * Checks if there are circular dependencies among the actions.
     *
     * @param actions the list of actions to check
     * @return true if there are circular dependencies, false otherwise
     */
    public boolean hasCircularDependencies(List<Action> actions) {
        try {
            orderActionsByDependencies(actions);
            return false; // If we can order them, there are no circular dependencies
        } catch (IllegalStateException e) {
            return e.getMessage().contains("Cycle detected");
        }
    }

    /**
     * Gets the immediate dependencies of an action (actions that must be executed before this one).
     *
     * @param action the action to check
     * @param allActions all available actions
     * @return a set of actions that are dependencies of the given action
     */
    public Set<Action> getImmediateDependencies(Action action, List<Action> allActions) {
        Set<Action> dependencies = new HashSet<>();

        if (action == null || allActions == null) {
            return dependencies;
        }

        // Find actions that produce facts required by the given action
        for (Action candidateDependency : allActions) {
            if (!candidateDependency.getActionId().equals(action.getActionId())) {
                boolean producesRequiredFact = candidateDependency.getProduces().stream()
                        .anyMatch(action.getRequires()::contains);

                if (producesRequiredFact) {
                    dependencies.add(candidateDependency);
                }
            }
        }

        return dependencies;
    }
}