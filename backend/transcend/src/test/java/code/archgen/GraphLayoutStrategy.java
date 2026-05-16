package code.archgen;

public interface GraphLayoutStrategy {
  String getLayoutDirectives();

  String getSkinparamSettings();

  class HybridDecoupledLayout implements GraphLayoutStrategy {

    @Override
    public String getLayoutDirectives() {
      return "left to right direction";
    }

    @Override
    public String getSkinparamSettings() {
      return """
            ' Use polyline to prevent the "merging" effect of ortho
            skinparam linetype polyline
            """;
    }
    /*
           ' High nodesep forces horizontal distance between nodes, preventing arrow overlap
           skinparam nodesep 100
           ' High ranksep forces vertical distance between layers
           skinparam ranksep 150

           ' Visual clarity tweaks
           skinparam shadowing false
           skinparam packageStyle rectangle
           skinparam roundcorner 5

           ' Prevents the engine from grouping arrows too aggressively
           skinparam searchDistance 2000
    */
  }
}
