# find-political-donors
Insight Data Engineering challenge

Run instructions - 

This was compiled and built using Java 8/JRE 1.8 on a Mac.
To run, simply execute run.sh present in the top directory.
Nothing other than standard Java libraries were used.


Algorithm:

Preprocessing:
Validate against all input conditions and trim unwanted space.

Median:
The median value was calculated using two heaps. A max-heap stores the larger half while a min-heap the lower half of the data. The median can be calculated in O(1) time by either picking the min/max from one of them (if there are an odd number of elements) or the average of the two (if even number of elements). This is used both for the running median for zip codes as well as for the dates. A hashmap keeps track of every zipcode-recipient combination and it's corresponding Median heap object. Similarly, a TreeMap keeps track of every date-recipient comnination and it's corresponding Median heap object. The TreeMap provides a sorted map of entries in addition to O(1) retrieval of corresponding metrics. 
