import java.util.*;

public class DFSvisit<T> {
    /**
     * TLS - Thread-Local Storage
     */
    Stack<Node<T>> workingStack; // stack for discovered nodes
    Set<Node<T>> finished;       // set for finished nodes

    public DFSvisit(){
        workingStack = new Stack<>();
        finished = new HashSet<>();
        // {3,1,2}
    }
    /*
Push to stack the starting node of our graph V
While stack is not empty: // there are nodes to handle V
    removed = pop operation V
    insert to finish set V
    invoke getReachableNodes on the removed node V
    For each reachable node:
        if the current reachable node is not in finished set && working stack
        push to stack
 */
    public Collection<T> traverse(Index dest,Traversable<T> partOfGraph){
        workingStack.push(partOfGraph.getOrigin());
        boolean hello =false;
        while(!workingStack.isEmpty()){
            Node<T> poppedNode = workingStack.pop();
            finished.add(poppedNode);
            Collection<Node<T>> reachableNodes = partOfGraph.getReachableNodes2(poppedNode);
            for (Node<T> singleReachableNode: reachableNodes){
                if (!finished.contains(singleReachableNode) &&
                        !workingStack.contains(singleReachableNode)){
//                    we need to save the dist of each node from source
//                    Index curr = new index();
//                    curr.set
//                singleReachableNode.getData()).setdist2source(previews +1);
                   if(dest.equals(singleReachableNode.getData())){
                        hello = true;}
                   if(hello){
                       HashSet<T> recursepaths = new HashSet<>();
                       Matrix matrix = new Matrix
                       recursepaths.add(traverse(dest, partOfGraph)
                   }
                    workingStack.push(singleReachableNode);
                }
            }
        }

        HashSet<T> blackList = new HashSet<>();
        for (Node<T> node: finished){
            blackList.add(node.getData());
        }
        HashSet<HashSet<T>> allofthem = new HashSet<>();
        allofthem.add(blackList);
        allofthem.add()
        return blackList;
    }
   /*
   [1,0,1,1]
   [1,1,0,1]

   Reachables

   WorkingStack

   FinishedSet
   (0,0)
   (1,0)
   (1,1)
    */



}
