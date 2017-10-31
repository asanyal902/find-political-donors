import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.PriorityQueue;

//Maintains 2 heaps (see the readme) and get the median in O(1) time.
//Also stores the total number and amount of transactions.
//BigDecimal used since FEC dictionary specifies Number(14,2)
//as type of field.
public class MedianGetter {
	
	//max heap
	private PriorityQueue<BigDecimal> lower = new PriorityQueue<>(new Comparator<BigDecimal>(){
        @Override
        public int compare(BigDecimal b1, BigDecimal b2) {
            return b2.compareTo(b1);
        }
	});
	
	//min heap
	private PriorityQueue<BigDecimal> upper = new PriorityQueue<>();

	private BigDecimal totalContrib = new BigDecimal(0);
	
	public void addDonation(BigDecimal donation){
		if(upper.size() == 0 || donation.compareTo(upper.peek()) >= 0){
			upper.offer(donation);
		}
		else{
			lower.offer(donation);
		}
		
		//rebalance
		if(upper.size() > lower.size() + 1){
			lower.offer(upper.poll());
		}
		else if(lower.size() > upper.size() + 1){
			upper.offer(lower.poll());
		}
		
		totalContrib = totalContrib.add(donation);
	}
	
	public BigDecimal getMedian(){
		if(upper.size() == 0 && lower.size() == 0) return null;
		
		if(upper.size() > lower.size()){
			 return upper.peek().stripTrailingZeros();
		}
		else if(lower.size() > upper.size()){
			return lower.peek().stripTrailingZeros();
		}
		else {//avg
			return (lower.peek().add(upper.peek())).divide(new BigDecimal(2), RoundingMode.HALF_UP).stripTrailingZeros();
		}		
	}
	
	public BigDecimal getTotalContrib(){
		return totalContrib.stripTrailingZeros();
	}
	
	public int getTotalTrans(){
		return lower.size() + upper.size();
	}
	
}
