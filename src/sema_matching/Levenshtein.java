package sema_matching;

import java.util.Arrays;

public class Levenshtein {
	 
    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
    //early termination technique 1
    public static int optdistance(String a, String b, int thres) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int[][] costs = new int[a.length()+1][b.length()+1];
        for (int j = 0; j <= b.length(); j++)
            costs[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[i][0] = i;
            boolean early=true;
            for (int j = 1; j <= b.length(); j++) {
                costs[i][j] = Math.min(1 + Math.min(costs[i-1][j], costs[i][j-1]), a.charAt(i - 1) == b.charAt(j - 1) ? costs[i-1][j-1] : costs[i-1][j-1] + 1);
                if(costs[i][j]>thres && j>i) break;
                if(costs[i][j]+Math.abs((a.length()-i)-(b.length()-j))<=thres) early=false;
            }
            if(early) return -1;
        }
       
        return costs[a.length()][b.length()];
	}
    //early termination technique //everything
    public static boolean optdist(String a, String b, int thres, int delta) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        if(a.length()<=thres-1 || b.length()<=thres-1){
        	if(Levenshtein.distance(a,b)<thres) return true;
        	else return false;
        }
        // i == 0
        int[][] costs = new int[a.length()+1][b.length()+1];
        for (int j = 0; j <= b.length(); j++)
            costs[0][j] = j+1;
        for (int i = 1; i <= a.length(); i++) {
            costs[i][0] = i+1;
            boolean early=true;
            for (int j = i-thres- Math.floorDiv((thres-delta),2); j <= i + Math.floorDiv((thres-1+delta),2); j++) { //   
            	if(j>0 && j<=b.length()){
            		if(costs[i-1][j]==0) costs[i-1][j]=100;
            		if(costs[i][j-1]==0) costs[i][j-1]=100;
            		//if(costs[i-1][j-1]==0) costs[i-1][j-1]=100;
            		costs[i][j] = Math.min(1 + Math.min(costs[i-1][j], costs[i][j-1]), a.charAt(i - 1) == b.charAt(j - 1) ? costs[i-1][j-1] : costs[i-1][j-1] + 1);
            		if(costs[i][j]+Math.abs((a.length()-i)-(b.length()-j))<=thres) early=false; //
            	}
            }
            if(early) {
/*               for (int k = 0; k <= a.length(); k++){
                	for (int j = 0; j <= b.length(); j++){
                		System.out.print(costs[k][j] +"  ");
                	}
                	System.out.println();	
            	}
                System.out.println("break  "+i);*/
            	return false;
            }
        }
        
/*       for (int i = 0; i <= a.length(); i++){
        	for (int j = 0; j <= b.length(); j++){
        		System.out.print(costs[i][j] +"  ");
        	}
        	System.out.println();	
    	}*/
        if(costs[a.length()][b.length()]<= thres){
        	//System.out.print(costs[a.length()][b.length()] +"  ");
        	return true;}
        else 
        	return false;
	}


    
    //https://github.com/jpmml/jpmml-evaluator/blob/master/pmml-evaluator/src/main/java/org/jpmml/evaluator/StringUtil.java
    	
    
    
    public static int getLevenshteinDistance(CharSequence s, CharSequence t, final int threshold) {
        /*if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }*/

        /*
        This implementation only computes the distance if it's less than or equal to the
        threshold value, returning -1 if it's greater.  The advantage is performance: unbounded
        distance is O(nm), but a bound of k allows us to reduce it to O(km) time by only
        computing a diagonal stripe of width 2k + 1 of the cost table.
        It is also possible to use this to compute the unbounded Levenshtein distance by starting
        the threshold at 1 and doubling each time until the distance is found; this is O(dm), where
        d is the distance.

        One subtlety comes from needing to ignore entries on the border of our stripe
        eg.
        p[] = |#|#|#|*
        d[] =  *|#|#|#|
        We must ignore the entry to the left of the leftmost member
        We must ignore the entry above the rightmost member

        Another subtlety comes from our stripe running off the matrix if the strings aren't
        of the same size.  Since string s is always swapped to be the shorter of the two,
        the stripe will always run off to the upper right instead of the lower left of the matrix.

        As a concrete example, suppose s is of length 5, t is of length 7, and our threshold is 1.
        In this case we're going to walk a stripe of length 3.  The matrix would look like so:

           1 2 3 4 5
        1 |#|#| | | |
        2 |#|#|#| | |
        3 | |#|#|#| |
        4 | | |#|#|#|
        5 | | | |#|#|
        6 | | | | |#|
        7 | | | | | |

        Note how the stripe leads off the table as there is no possible way to turn a string of length 5
        into one of length 7 in edit distance of 1.

        Additionally, this implementation decreases memory usage by using two
        single-dimensional arrays and swapping them back and forth instead of allocating
        an entire n by m matrix.  This requires a few minor changes, such as immediately returning
        when it's detected that the stripe has run off the matrix and initially filling the arrays with
        large values so that entries we don't compute are ignored.

        See Algorithms on Strings, Trees and Sequences by Dan Gusfield for some discussion.
         */
    	 s = ((String) s).toLowerCase();
         t = ((String) t).toLowerCase();
        int n = s.length(); // length of s
        int m = t.length(); // length of t

        // if one string is empty, the edit distance is necessarily the length of the other
        if (n == 0) {
            return m <= threshold ? m : -1;
        } else if (m == 0) {
            return n <= threshold ? n : -1;
        }

        if (n > m) {
            // swap the two strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n + 1]; // 'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; // placeholder to assist in swapping p and d

        // fill in starting table values
        final int boundary = Math.min(n, threshold) + 1;
        for (int i = 0; i < boundary; i++) {
            p[i] = i;
        }
        // these fills ensure that the value above the rightmost entry of our
        // stripe will be ignored in following loop iterations
        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
        Arrays.fill(d, Integer.MAX_VALUE);

        // iterates through t
        for (int j = 1; j <= m; j++) {
        	boolean early=true;
            final char t_j = t.charAt(j - 1); // jth character of t
            d[0] = j;

            // compute stripe indices, constrain to array size
            final int min = Math.max(1, j - threshold);
            final int max = (j > Integer.MAX_VALUE - threshold) ? n : Math.min(n, j + threshold);

            // the stripe may lead off of the table if s and t are of different sizes
            if (min > max) {
                return -1;
            }

            // ignore entry left of leftmost
            if (min > 1) {
                d[min - 1] = Integer.MAX_VALUE;
            }

            // iterates through [min, max] in s
            for (int i = min; i <= max; i++) {
                if (s.charAt(i - 1) == t_j) {
                    // diagonally left and up
                    d[i] = p[i - 1];
                } else {
                    // 1 + minimum of cell to the left, to the top, diagonally left and up
                    d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
                }
                if(d[i]<=threshold) early=false;
            }
            if(early) return -1;
            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // if p[n] is greater than the threshold, there's no guarantee on it being the correct
        // distance
        if (p[n] <= threshold) {
            return p[n];
        }
        return -1;
    }
 
}