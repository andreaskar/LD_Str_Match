package sema_matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cluster {
	private HashMap<String, List<Integer>> shash;
	
	public Cluster(){
		this.shash = new HashMap<String, List<Integer>>();
	}
	
	public List<Integer> getShashList(String a){
		if(shash.get(a)!=null){
			//System.out.println("not null case" + a );
			return shash.get(a);
		}
		else {
			//System.out.println("lets see: " + a );
			return null;
		}
	}
	
	public void putSubStrings(int point, List<String> l){
        for(int i=0;i<l.size();++i){
        	String s= l.get(i);
        	List<Integer> m = shash.get(s);
        	if(m == null)
               	shash.put(s, m=new ArrayList<Integer>());
            m.add(point);
        }
	}

}
