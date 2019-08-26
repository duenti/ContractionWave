package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Groups implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Group> groups;
	
	public Groups(){
		groups = new ArrayList<Group>();
	}
	
	public void add(Group g){
		groups.add(g);
	}
	
	public void add(Groups G) {
		for (int i = 0; i < G.size(); i++) {
			groups.add(G.get(i));
		}
	}
	
	public void remove(String g_name) {
		for (Group group : groups) {
			if (group.getName().equals(g_name) == true) {
				groups.remove(group);
				break;
			}
		}
	}
	
	public void clear(){
		groups.clear();
	}
	
	public Group get(int i){
		return groups.get(i);
	}
	
	public int size(){
		return groups.size();
	}
	
	public ObservableList<Group> getObservableList(){
		return FXCollections.observableArrayList(groups);
	}
	
	public void merge(int i, int j){
		if(i < groups.size() && j < groups.size() && i != j){
			Group g1 = groups.get(i);
			Group g2 = groups.get(j);
			if(!g1.equals(g2)) return;
			if(g1.getType() != g2.getType()) return;
			String new_name = g1.getName() + "_" + g2.getName();
			List<String> newPaths = new ArrayList<String>();
			
			for(String path : g1.getPaths()){
				newPaths.add(path);
			}
			for(String path : g2.getPaths()){
				newPaths.add(path);
			}
			
			Group newGroup = null;
			
			if(g1.getType() == 0){
				//newGroup = new VideoGroup(new_name,newPaths);
			}else{
				newGroup = new ImageGroup(new_name,newPaths);
			}
			
			if(i > j){
				groups.remove(i);
				groups.remove(j);
			}else{
				groups.remove(j);
				groups.remove(i);
			}
			
			groups.add(newGroup);
		}
	}
}