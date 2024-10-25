import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ThreadLocalRandom;

public class RaftNode extends Node {

    public enum RaftState {
        FOLLOWER,
        CANDIDATE,
        LEADER
    }
    public RaftState getState() {
        return state;
    }

    private RaftState state;
    private int currentTerm;
    private int votedFor;

    public RaftNode(int idx) throws RemoteException {
        super(idx);
        this.state = RaftState.FOLLOWER;
        this.currentTerm = 0;
        this.votedFor = -1;
    }

    public void startElection() {
        state = RaftState.CANDIDATE;
        currentTerm++;
        votedFor = idx();

        int votesReceived = 1;  // Vote for itself
        int votesNeeded = (n / 2) + 1;  // Majority of nodes

        // Request votes from other nodes
        for (int i = 0; i < n; i++) {
            if (i != idx()) {
                try {
                    Registry reg = LocateRegistry.getRegistry(ports[i]);
                    NodeI e = (NodeI) reg.lookup(services[i]);
                    if (e.requestVote(currentTerm, idx())) {
                        votesReceived++;
                        if (votesReceived >= votesNeeded) {
                            becomeLeader();
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // If not enough votes received, transition back to FOLLOWER
        state = RaftState.FOLLOWER;
    }

    public boolean requestVote(int term, int candidateId) throws RemoteException {
        // Respond to a vote request
        if (term > currentTerm) {
            state = RaftState.FOLLOWER;
            currentTerm = term;
            votedFor = candidateId;
            return true;
        } else if (term == currentTerm && (votedFor == -1 || votedFor == candidateId)) {
            // If the requesting candidate has the same or higher term and hasn't voted for another node
            return true;
        }

        return false;
    }

    private void becomeLeader() {
        state = RaftState.LEADER;

        System.out.println("Node " + idx() + " became LEADER for term " + currentTerm);

    }


}