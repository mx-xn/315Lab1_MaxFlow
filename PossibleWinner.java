import java.util.*;

public class PossibleWinner {
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    //read input
    int numTeam = in.nextInt();
    int[] currentWins = new int[numTeam];
    int[][] remainGames = new int[numTeam][numTeam]; 

    for (int i = 0; i < numTeam; i++) {
      currentWins[i] = in.nextInt();
    }
    for (int i = 0; i < numTeam; i++) {
      for (int j = 0; j < numTeam; j++) {
        remainGames[i][j] = in.nextInt();
      }
    }
    if (isPossibleWinner(currentWins, remainGames)) 
      System.out.println("yes");
    else
      System.out.println("no");
  }

  private static boolean isPossibleWinner(int[] currentWins, 
      int[][] remainGames) {
    final int INF = Integer.MAX_VALUE;
    
    int numTeam = currentWins.length;
    int candidateInd = 0;

    int maxWins = 0;
    //calculate maximum wins of first team
    for (int i = 0; i < numTeam; i++) 
      maxWins += remainGames[candidateInd][i];

    maxWins += currentWins[candidateInd];

    if (!preCheck(maxWins, currentWins))
      return false;

    //if pass pre-check
    
    int[] enemyMaxWins = new int[numTeam];
    for (int i = 0; i < numTeam; i++) {
      if (i != candidateInd)
        enemyMaxWins[i] = maxWins - currentWins[i];
      else
        enemyMaxWins[i] = -1;   //if is processing itself
    }

    //calculate numVertices
    int numMatches = (numTeam - 1) * (numTeam - 2) / 2;

    //1 source, 1 sink, numEnemy choose 2, numEnemy
    int numV = 2 + numTeam - 1 + numMatches;
    int minGames = 0;
    //construct edges
    ArrayList<FlowEdge> edges = new ArrayList<FlowEdge>();
    int matchTrack = 1;
    for (int i = 0; i < numTeam; i++) {
      if (i != candidateInd) {
        for (int j = i + 1; j < numTeam; j++) {
          int capacity = remainGames[i][j];
          minGames += capacity;
          FlowEdge fe = new FlowEdge(0, matchTrack, capacity);
          edges.add(fe);

          FlowEdge fe1 = new FlowEdge(matchTrack, numMatches + i, INF);
          FlowEdge fe2 = new FlowEdge(matchTrack, numMatches + j, INF);
          edges.add(fe1);
          edges.add(fe2);

          matchTrack++;
        }
        FlowEdge fe3 = new FlowEdge(numMatches + i, numV - 1, enemyMaxWins[i]);
        edges.add(fe3);
      }
    }

    //construct graph
    FlowNetwork graph = new FlowNetwork(numV);
    for (FlowEdge e : edges) 
      graph.addEdge(e);

    //run MaxFLow
    FordFulkerson maxFlow = new FordFulkerson(graph, 0, numV - 1);
    double maxFlowValue = maxFlow.value();

    if (maxFlowValue < minGames)
      return false;
    return true;
  }

  private static boolean preCheck(int maxWins, int[] currentWins) {
    for (int i = 0; i < currentWins.length; i++) {
      if (currentWins[i] > maxWins)
        return false;
    }
    return true;
  }
}
