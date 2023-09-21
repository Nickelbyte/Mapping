/**
* This program uses Dijkstra's algorithm to find the shortest path
* between real world locations in Washington State. Both the distance
* and the driving route are printed to the console. 
*
* @version 1.0
* @since   2023-06-09 
*/

import java.util.*;

public class Mapping {
    public static void main(String[] args) {
        /*  Weights are equal to real world mileage from Google Maps
         *  Weights of 10000 indicate infinite distance (not connected).
         *  These indicate to the algorithm that it needs to figure out
         *  a better path between the nodes. The graph is undirected which creates
         *  slight differences to real world route distances in some cases. */
        int[][] graph = {
            {0, 17, 12, 10000, 10000, 16},
            {17, 0, 10, 15, 10000, 10000},
            {12, 10, 0, 17, 10000, 10000},
            {10000, 15, 17, 0, 22, 10000},
            {10000, 10000, 10000, 22, 0, 10000},
            {16, 10000, 10000, 10000, 10000, 0}
        };

        // Assign a number to each city for the algorithm and
        // for the user to choose from
        HashMap<Integer, String> locations = new HashMap<>();
        locations.put(0, "Bothell");
        locations.put(1, "Seattle");
        locations.put(2, "Bellevue");
        locations.put(3, "Seatac");
        locations.put(4, "Tacoma");
        locations.put(5, "Monroe");

        // Path list that will be passed to and filled by dijkstra()
        ArrayList<Integer> path = new ArrayList<>(6);

        // Map of corresponding roads to real world names
        Map<Road, String> roads = new HashMap<>();
        roads.put(new Road(0, 1), "SR 522 West");
        roads.put(new Road(1, 0), "SR 522 East");
        roads.put(new Road(1, 2), "SR 520 East");
        roads.put(new Road(2, 1), "SR 520 West");
        roads.put(new Road(2, 3), "I-405 South");
        roads.put(new Road(3, 2), "I-405 North");
        roads.put(new Road(3, 4), "I-5 South");
        roads.put(new Road(4, 3), "I-5 North");
        roads.put(new Road(0, 5), "SR 522 East");
        roads.put(new Road(5, 0), "SR 522 West");
        roads.put(new Road(0, 2), "I-405 South");
        roads.put(new Road(2, 0), "I-405 North");
        roads.put(new Road(1, 3), "I-5 South");
        roads.put(new Road(3, 1), "I-5 North");

        // Gather user input for start and end locations
        int[] input = getInput(locations);

        // Print the path and distance
        printPath(path, locations, input, graph, roads);
    }

    /**
    * Calculates the shortest distance and route between the two specified
    * nodes in the graph
    * @param graph - Graph representing all roads that connect the locations
    * @param startNode - The starting node
    * @param targetNode - The ending node
    * @param path - The list that will be filled with the path
    * @return int - the shortest distance between the start and end nodes in miles
    */
    public static int dijkstra(int[][] graph, int startNode, int targetNode, List<Integer> path) {
        // Instantiate visited and distance arrays for the shortest distance
        // calculation and the previous array for the path calculation
        boolean[] visited = new boolean[graph.length];
        int[] distances = new int[graph.length];
        Integer[] previous = new Integer[graph.length];
        Arrays.fill(visited, false);
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(previous, null);

        // Implements a linkedlist queue rather than a priority queue due
        // to the nodes being represented by integers rather than objects
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startNode);
        distances[startNode] = 0;
        previous[startNode] = null;

        // While the queue is not empty, poll the node mark it as visited
        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            visited[currentNode] = true;

            // For each adjacent node that is not visited, calculate the
            // distance from the startNode and update the distance array
            // while keeping track of previous nodes for the path
            for (int i = 0; i < graph.length; i++) {
                if (graph[currentNode][i] < 10000 && !visited[i]) {
                    int newDistance = distances[currentNode] + graph[currentNode][i];
                    if (newDistance < distances[i]) {
                        distances[i] = newDistance;
                        previous[i] = currentNode;

                        // Add the adjacent node to the queue such that
                        // its adjacent nodes can be iterated through later
                        queue.add(i);
                        previous[i] = currentNode;
                    }
                }
            }
        }

        // Iterate through the previous array to create the path
        Integer pathNode = targetNode;
        while (pathNode != null) {
            path.add(pathNode);
            pathNode = previous[pathNode];
        }

        // The reversed path is the shortest path between the start and end nodes
        Collections.reverse(path);
        return distances[targetNode];
    }

    /**
    * Gathers and filters user input for the start and end locations
    * @param locations - HashMap of locations and their corresponding numbers
    * @return int[] - Array of the start and end nodes
    */
    public static int[] getInput(HashMap<Integer, String> locations) {
        // Gather user input for start and end locations after prompting
        Scanner input = new Scanner(System.in);
        System.out.println("Available locations: ");
        locations.forEach((key, value) -> System.out.printf("%d. %s\n", key, value));
        System.out.println("Enter the starting location number: ");
        int startNode = input.nextInt();
        System.out.println("Enter the destination location number: ");
        int targetNode = input.nextInt();
        input.close();

        // Screen for invalid input
        if (startNode < 0 || startNode >= locations.size() || targetNode < 0 || 
            targetNode >= locations.size()) {
            System.out.println("Invalid input");
            System.exit(0);
        }

        return new int[] {startNode, targetNode};
    }

    /**
    * Handles console output for the path and distance
    * @param path - The list that will be filled with the path between nodes
    * @param locations - HashMap of locations and their corresponding numbers
    * @param input - Array of the start and end nodes
    * @param graph - Graph representing all roads that connect the locations
    * @param roads - Map of corresponding roads to real world names
    * @return void
    */
    public static void printPath(List<Integer> path, Map<Integer, String> locations,
        int[] input, int[][] graph, Map<Road, String> roads) {
        // Print the distance from dijkstra() which also sets up the path
        System.out.printf(
            "\nThe route from %s to %s is approximately %d miles long\nThe Route: ",
            locations.get(input[0]), locations.get(input[1]), 
            dijkstra(graph, input[0], input[1], path)); 
        
        // From the finished path created in dijkstra(), iterate through each
        // node and print the corresponding road to traverse between each city
        path.forEach((n) -> {
        int index = path.indexOf(n);
        if (index != path.size() - 1)
            System.out.printf("%s; through %s -> ", locations.get(n), 
                roads.get(new Road(n, path.get(index + 1))));
        else
            System.out.printf("%s\n", locations.get(n));
        });
    }
}