package vo;

import java.io.Serializable;

public class Evaluation implements Serializable{
	
	private double taste;
	private double service;
	private double hygiene;
	private double average;
	private String comment;
	private Member user;
	
	public static final double ZERO = 0;
	public static final double ONE = 1;
	public static final double TWO = 2;
	public static final double THREE = 3;
	public static final double FOUR = 4;
	public static final double FIVE = 5;
	
	public Evaluation(double taste, double service, double hygiene, double average, String comment, Member user) {
		super();
		this.taste = taste;
		this.service = service;
		this.hygiene = hygiene;
		this.average = average;
		this.comment = comment;
		this.user = user;
	}
	
	public Evaluation(){
		
	}
	
	public static double getScoreFromStarRate(String star){
		double score = 0;
	
		switch(star){
		case "�١١١١�":
			score = ZERO;
			break;
		case "�ڡ١١١�":
			score = ONE;
			break;
		case "�ڡڡ١١�":
			score = TWO;
			break;
		case "�ڡڡڡ١�":
			score = THREE;
			break;
		case "�ڡڡڡڡ�":
			score = FOUR;
			break;
		case "�ڡڡڡڡ�":
			score = FIVE;
			break;
		}
		
		return score;
	}
	
	public static String getStarRateFromScore(double score){
		String s_avg = null;
		switch( (int)Math.round(score) ){
		case 0:
			s_avg = "�١١١١�";
			break;
		case 1:
			s_avg = "�ڡ١١١�";
			break;
		case 2:
			s_avg = "�ڡڡ١١�";
			break;
		case 3:
			s_avg = "�ڡڡڡ١�";
			break;
		case 4:
			s_avg = "�ڡڡڡڡ�";
			break;
		case 5:
			s_avg = "�ڡڡڡڡ�";
			break;
		}
		return s_avg;
	}
	
	public double getTaste() {
		return taste;
	}
	public void setTaste(double taste) {
		this.taste = taste;
	}
	public double getService() {
		return service;
	}
	public void setService(double service) {
		this.service = service;
	}
	public double getHygiene() {
		return hygiene;
	}
	public void setHygiene(double hygiene) {
		this.hygiene = hygiene;
	}
	public double getAverage() {
		return average;
	}
	public void setAverage(double average) {
		this.average = average;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Member getUser() {
		return user;
	}
	public void setUser(Member user) {
		this.user = user;
	}
}
