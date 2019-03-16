import java.util.Arrays;
import java.util.Iterator;

/**
 * 
 * David Mualem, 308375872, user: davidmualem
 * Matan Roet, 205660574, user: matanroet
 * 
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over non-negative integers.
 */
public class FibonacciHeap
{
	
	private LinkedList heapsList = new LinkedList();  
	private int totalSize;
	private HeapNode minHeap;
	private int markedCount;
	
	public static int totalLinksCount;
	private static int totalCutsCount;
	
	
	
   /**
    * public boolean empty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean empty()
    {
    	return heapsList.isEmpty(); // should be replaced by student code
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode newNode = new HeapNode(key);
    	totalSize++;
    	if (this.empty() || key < minHeap.getKey()) {
    		minHeap = newNode;
    	}
    	heapsList.insertAtEnd(newNode);
    	return newNode;
    }

   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
    	if (this.empty()) {
    		return;
    	}
    	
    	this.totalSize -- ;
    	deleteAndDetach(); //deletes the min heap and detaches its children from it
    	
    	if (totalSize == 0) {
    		this.minHeap = null;
    		this.markedCount = 0;
    		return;
    	}
    	
    	this.heapsList.mergeLists(this.minHeap.childrenList);
    	HeapNode[] buckets = new HeapNode[3*(1 +(int)Math.ceil(Math.log(this.totalSize)))]; //potential bug - not initiliazied as null array 
    	
    	for (HeapNode node: this.heapsList) {
    		insertToBucket(node, buckets);
    	}
    	
    	LinkedList newHeapList = bucketToHeapList(buckets);
    	this.heapsList = newHeapList;
    	updateNewMinHeap();
    	makeMarkless(); //makes the roots of trees unmarked 
     	
    }

   private void makeMarkless() {
	for (HeapNode node: this.heapsList) {
		if (node.marked) {
			this.markedCount --;
			node.marked = false;
		}
		
	}
	
}

private void updateNewMinHeap() {
	int currMin = this.heapsList.start.getKey();
	HeapNode minHeap = this.heapsList.start;
	for (HeapNode node : this.heapsList) {
		int currentKey= node.getKey();
		if (currentKey < currMin) {
			minHeap = node;
			currMin = currentKey;
		}
		
	this.minHeap = minHeap;
	}
}

private LinkedList bucketToHeapList(HeapNode[] buckets) {
	LinkedList result = new LinkedList();
	for (HeapNode node: buckets) {
		if (node != null) {
			result.insertAtStart(node);
	}
	}
	return result;
}

private void insertToBucket(HeapNode node, HeapNode[] buckets) { // potential complication - marked roots become children
	int rank = node.getRank();
	HeapNode insideArr  = buckets[rank];
	if (insideArr == null) {
		buckets[rank] = node;
		return;
	}
	else {
		buckets[rank] = null;
		HeapNode toInsert = linkNodes(node, insideArr);
		insertToBucket(toInsert, buckets);
	}
	
	
}


private static HeapNode linkNodes(HeapNode node1, HeapNode node2) {
	totalLinksCount ++;
	
	int key1 = node1.getKey();
	int key2 = node2.getKey();
	if(key1 < key2) {
		node2.parent = node1;
		node1.childrenList.insertAtStart(node2);
		return node1;
	}
	else {
		node1.parent = node2;
		node2.childrenList.insertAtStart(node1);
		return node2;
	}
	
}

/**
 * public void deleteAndDetach()
 *
 * Delete the node containing the minimum key, and detach its childen from it
 *
 */
private void deleteAndDetach() {
	this.heapsList.deleteNode(this.minHeap);
	for (HeapNode node : this.minHeap.childrenList) {
		node.parent = null;
	}
}

/**
    * public HeapNode findMin()
    *
    *\
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return minHeap;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Meld the heap with heap2
    *
    */
    public void meld (FibonacciHeap heap2)
    {
    	if (heap2.empty()) {
    		return;
    	}
    	if (this.empty() ) {
    		this.minHeap = heap2.minHeap;
    		this.totalSize = heap2.totalSize;
    		this.markedCount = heap2.markedCount;
    		return;
    	}
    	 totalSize += heap2.totalSize;
    	 markedCount += heap2.markedCount;
    	 if (heap2.minHeap.key < minHeap.key) {
    		minHeap = heap2.minHeap; 
    	 }
    	 heapsList.mergeLists(heap2.heapsList);   		
    }

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return totalSize;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	
	int[] arr = new int[3*(1 + (int) Math.ceil(Math.log(totalSize)))];
	int rank;
	for (HeapNode fibTree : heapsList) {
		rank = fibTree.childrenList.getSize();
		arr[rank]++;
	}
	arr = removeZero(arr);
    return arr; 
    }
	
   private int[] removeZero(int[] arr) {
		int i = arr.length - 1;
		while (i >= 0 && arr[i] == 0) {
			i--;
		}
		int[] newArray = Arrays.copyOfRange(arr, 0, i+1);
		return newArray;
	}

/**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	if (this.empty()) {
    		return;
    	}
    	decreaseKey(x, x.getKey()+1);
    	deleteMin();

    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey() - delta);
    	if (x.getKey() < minHeap.getKey()) {
    		minHeap = x;
    	}
    	if (x.parent == null) {
    		return;
    	}
    	if (x.getKey() < x.parent.getKey()) {
    		HeapNode parent = x.parent;
    		cut(x);
    		cascadingCuts(parent);
    	}
    }

   private void cascadingCuts(HeapNode node) {
	   if (node.parent == null) {
		   return;
	   }
	   if (!node.marked) {
		   node.marked = true;
		   markedCount++;
		   return;
	   }
	   HeapNode parent = node.parent;
	   cut(node);
	   cascadingCuts(parent);
}

private void cut(HeapNode x) {
	totalCutsCount++;
	x.parent.childrenList.deleteNode(x);
	heapsList.insertAtStart(x);
	if (x.marked) {
		x.marked = false;
		markedCount--;
	}
	x.parent = null;
}

/**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.heapsList.getSize() + 2*markedCount; // should be replaced by student code
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return totalLinksCount;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCutsCount;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key;
	private HeapNode parent = null;
	private LinkedList childrenList = new LinkedList();;
	protected HeapNode next = null;
	protected HeapNode prev = null;
	protected boolean marked = false;

  	public HeapNode(int key) {
	    this.key = key;
	    next = null;
	    prev = null;
      }

  	public int getRank() {
  		return this.childrenList.getSize();
	}

	public int getKey() {
	    return this.key;
      }
  
  	
  	/* Function to set link to next node */
  	public void setNext(HeapNode n){
  		next = n;
  	}
  	
  	/* Function to set link to previous node */
  	public void setPrev(HeapNode p){
  		prev = p;
  	}    
  	
  	/* Funtion to get link to next node */
  	public HeapNode getNext(){
  		return next;
  	}
  	
  	/* Function to get link to previous node */
  	public HeapNode getPrev(){
  		return prev;
  	}
  	
  	/* Function to set data to node */
  	public void setKey(int newKey){
  		key = newKey;
  	}


    }
    
    public class LinkedList implements Iterable<HeapNode>
    {
    	
        protected HeapNode start;
        protected HeapNode end ;
        public int listSize;
     
        
        /* Constructor */
        public LinkedList(){
            start = null;
            end = null;
            listSize = 0;
        }
        
        
        /* Function to check if list is empty */
        public boolean isEmpty(){
            return start == null;
        }
        
        
        /* Function to get size of list */
        public int getSize(){
            return listSize;
        }
        
        
        /* Function to insert element at begining */
        public void insertAtStart(HeapNode newNode){
            if (start == null)
            {            
                newNode.setNext(newNode);
                newNode.setPrev(newNode);
                start = newNode;
                end = start;            
            }
            else
            {
            	newNode.setPrev(end);
                end.setNext(newNode);
                start.setPrev(newNode);
                newNode.setNext(start);
                start = newNode;        
            }
            listSize++ ;
        }
        
        
        /*Function to insert element at end */
        public void insertAtEnd(HeapNode newNode)
        {    
            if (start == null)
            {
            	newNode.setNext(newNode);
            	newNode.setPrev(newNode);
                start = newNode;
                end = start;
            }
            else
            {
            	newNode.setPrev(end);
                end.setNext(newNode);
                start.setPrev(newNode);
                newNode.setNext(start);
                end = newNode;    
            }
            listSize++;
        }
        
        
        public void deleteNode(HeapNode node) {
        	if (listSize == 0) {
        	}
        	
        	else if (listSize == 1) {
        		listSize = 0;
        		start = null;
        		end = null;
        	}
        	
        	else {
        		if (node == start) {
                    start = start.getNext();
        		}
        		else if (node == end) {
        			end = end.getPrev();
        		}
        		listSize--;
        		node.getPrev().setNext(node.getNext());
        		node.getNext().setPrev(node.getPrev());
        		node.setPrev(null);
        		node.setNext(null);        			
        	}        	
        }
        
        
        public void mergeLists(LinkedList otherList) {
        	if (otherList.isEmpty()) {
        		return;
        	}
        	if (this.isEmpty()) {
        		this.start = otherList.start;
        		this.end = otherList.end;
        		this.listSize = otherList.listSize;
        		return;
        	}
        	listSize += otherList.getSize();
        	this.end.setNext(otherList.start);
        	otherList.start.setPrev(this.end);
        	otherList.end.setNext(this.start);
        	this.start.setPrev(otherList.end);
        	this.end = otherList.end;
        }
        
        /* Function to delete node at position  */
        public void deleteAtPos(int pos)
        {        
            if (pos == 1) 
            {
                if (listSize == 1)
                {
                    start = null;
                    end = null;
                    listSize = 0;
                    return; 
                }
                start = start.getNext();
                start.setPrev(end);
                end.setNext(start);
                listSize--; 
                return ;
            }
            if (pos == listSize)
            {
                end = end.getPrev();
                end.setNext(start);
                start.setPrev(end);
                listSize-- ;
            }
            
            HeapNode ptr = start.getNext();
            for (int i = 2; i <= listSize; i++)
            {
                if (i == pos)
                {
                    HeapNode p = ptr.getPrev();
                    HeapNode n = ptr.getNext();
     
                    p.setNext(n);
                    n.setPrev(p);
                    listSize-- ;
                    return;
                }
                ptr = ptr.getNext();
            }        
        }    
        /* Function to display status of list */
        public void display()
        {
            System.out.print("\nHeapNode Linked List = ");
            HeapNode ptr = start;
            if (listSize == 0) 
            {
                System.out.print("empty\n");
                return;
            }
            if (start.getNext() == start) 
            {
                System.out.print(start.getKey()+ " <-> "+ptr.getKey()+ "\n");
                return;
            }
            System.out.print(start.getKey()+ " <-> ");
            ptr = start.getNext();
            while (ptr.getNext() != start) 
            {
                System.out.print(ptr.getKey()+ " <-> ");
                ptr = ptr.getNext();
            }
            System.out.print(ptr.getKey()+ " <-> ");
            ptr = ptr.getNext();
            System.out.print(ptr.getKey()+ "\n");
        }


		@Override
		public Iterator<HeapNode> iterator() {
			return new LinkedListIterator(this);
		}
		
		public class LinkedListIterator implements Iterator<HeapNode>{
			
			private LinkedList iteratedList;
			private HeapNode curr;

			public LinkedListIterator(LinkedList linkedList) {
				iteratedList = linkedList;
				curr = iteratedList.start;
			}

			@Override
			public boolean hasNext() {
				if (iteratedList.getSize() == 0) {
					return false;
				}
				if (curr == null) {
					return false;
				}
				return true;
				
			}

			@Override
			public HeapNode next() {
				if (curr == iteratedList.end) {
					HeapNode node = curr;
					curr = null;
					return node;
				}
				HeapNode node = curr;
				curr = curr.getNext();
				return node;
			
			}
			
			
			}
			
		}
    
}

