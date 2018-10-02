package Generic;
import java.util.ArrayList;

public abstract class GenericSearchProblem
{
	ArrayList<Node> queue;
	ArrayList<Operator> operators;
	QueuingFunction queuingFunction;
	
	public void initValues(ArrayList<Node> queue, ArrayList<Operator> operators) //, QueuingFunction qf)
	{
		this.queue = queue;
		this.operators = operators;
		//this.queuingFunction = qf;
	}
	
	public abstract Node stateSpace(Node node, Operator operator);
	public abstract boolean goalTest(Node node);
	public abstract int pathCostFunction(Node node, Operator operator);
//	public abstract int estimateHeuristic(Node node);
	public void expand(ArrayList<Node> queue, ArrayList<Operator> operators, QueuingFunction strategy)
	{
		for(int i = 0; i < operators.size(); ++i)
		{
			switch(strategy)
			{
				case BREADTH_FIRST_SEARCH: breadthFirstSearch(queue, operators); break;
				case DEPTH_FIRST_SEARCH: depthFirstSearch(queue, operators); break;
				case UNIFORM_COST_SEARCH: uniformCostSearch(queue, operators); break;
				case GREEDY_SEARCH: greedySearch(queue, operators); break;
				case ITERATIVE_DEEPENING: depthFirstSearch(queue, operators); break;
				case A_STAR: aStarSearch(queue, operators); break;
			}
		}
	}
	
	private void breadthFirstSearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		Node firstNode = queue.remove(0);
		
		for(int i = 0; i < operators.size(); i++)
		{
			Node child = stateSpace(firstNode, operators.get(i));
//			child.heuristic = estimateHeuristic(child);
			if(child != null)
				queue.add(child);
		}
	}
	
	private void depthFirstSearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		Node firstNode = queue.remove(0);

		for(int i = 0; i < operators.size(); i++)
		{
			Node child = stateSpace(firstNode, operators.get(i));
			if(child != null)
				queue.add(0,child);
		}
	}
	
	private void uniformCostSearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		Node firstNode = queue.remove(0);
		
		for(int i = 0; i < operators.size(); ++i)
		{
			Node child = stateSpace(firstNode, operators.get(i));
			if(child != null)
			{
				if(child.parent != null)
					child.totalCost = child.parent.totalCost;
				child.totalCost += this.pathCostFunction(child, operators.get(i));
				queue.add(child);
			}
		}
		queue.sort((o1, o2) -> o1.totalCost < o2.totalCost ? -1 : 1);
	}
	
	private void greedySearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		Node firstNode = queue.remove(0); System.out.println(firstNode.operator);
		
		for(int i = 0; i < operators.size(); ++i)
		{
			Node child = stateSpace(firstNode, operators.get(i));
			if(child != null)
				queue.add(child);
		}
		queue.sort((o1, o2) -> o1.heuristic < o2.heuristic ? -1 : 1);
	}
	
	private void aStarSearch(ArrayList<Node> queue, ArrayList<Operator> operators)
	{
		
	}
	
	public ResultObject search(QueuingFunction strategy, boolean visualize)
	{
		int iterativeDeepeningLevel = 0;
		Node root = queue.get(0);
		while(true)
		{
			if(queue.size() == 0)
			{
				System.out.println("No solution");
				break;
			}
			if(this.goalTest(queue.get(0)))
				break;
			if(strategy == QueuingFunction.ITERATIVE_DEEPENING)
			{
				if(queue.get(0).depth < iterativeDeepeningLevel)
					expand(this.queue, this.operators, strategy);
				else
				{
					queue.remove(0);
					if(queue.size() == 0)
					{
						queue.add(root);
						++iterativeDeepeningLevel;
					}
				}
			}
			else
				expand(this.queue, this.operators, strategy);
		}
		
		if(queue.size() == 0)
			return new ResultObject(false);

		ResultObject result = new ResultObject(true);
		
		ArrayList<Operator> sequence = new ArrayList<Operator>();
		int[] numberOfNodes = {0};
		sequence = this.backtrack(queue.get(0), sequence, numberOfNodes);
		
		result.operators = sequence;
		result.numberOfNodes = numberOfNodes[0];
		result.cost = queue.get(0).totalCost;
		
		System.out.println(result);
		return result;
	}
	
	public ArrayList<Operator> backtrack(Node node, ArrayList<Operator> sequence, int[] numberOfNodes)
	{
		if(node != null)
		{
			sequence.add(0, node.operator);
			numberOfNodes[0]++;
			this.backtrack(node.parent, sequence, numberOfNodes);
			System.out.println(node.totalCost);
		}
		return sequence;
	}
	
	public GenericSearchProblem() {}
	
	public GenericSearchProblem
	(
		Node initial,
		ArrayList<Operator> operators
	)
	{
		this.queue = new ArrayList<Node>();
		this.queue.add(initial);
		this.operators = operators;
	}
}