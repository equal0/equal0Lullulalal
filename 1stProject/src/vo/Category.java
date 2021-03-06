package vo;

import java.io.Serializable;

public class Category implements Serializable{
	
	private Address location;
	private int type;
	private Evaluation evaluation;
	
	public static final int KOREAN = 1;
	public static final int CHINA = 2;
	public static final int JAPAN = 3;
	public static final int WESTERN = 4;
	
	public static String S_KOREAN = "한  식";
	public static String S_WESTERN = "양  식";
	public static String S_JAPAN = "일  식";
	public static String S_CHINA = "중  식";
	
	public static String getStringFoodType(int foodType){
		String rtn = null;
		if (foodType == KOREAN) rtn = S_KOREAN;
		else if (foodType == CHINA) rtn = S_CHINA;
		else if (foodType == JAPAN) rtn = S_JAPAN;
		else if (foodType == WESTERN) rtn = S_WESTERN;
		
		return rtn;
	}
	
	public static int getIntFoodType(String foodType){
		int rtn = 0;
		
		if (S_KOREAN.equals(foodType)) rtn = KOREAN;
		else if (S_CHINA.equals(foodType)) rtn = CHINA;
		else if (S_JAPAN.equals(foodType)) rtn = JAPAN;
		else if (S_WESTERN.equals(foodType)) rtn = WESTERN;
		
		return rtn;
	}
	
	public Category(Address location, int type, Evaluation evaluation) {
		this.location = location;
		this.type = type;
		this.evaluation = evaluation;
	}
	
	public Category() {
		
	}
	
	public Address getLocation() {
		return location;
	}
	public void setLocation(Address location) {
		this.location = location;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Evaluation getEvaluation() {
		return evaluation;
	}
	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}
	
}
