package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import vo.Address;
import vo.Category;
import vo.Evaluation;
import vo.Member;
import vo.Restaurant;

public class ServerThread implements Runnable {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ServerManager manager = new ServerManager();

	public ServerThread(ObjectInputStream ois, ObjectOutputStream oos) {
		this.ois = ois;
		this.oos = oos;
		
	}

	@Override
	public void run() {
		boolean flag = true;
		while (flag) {
			try {
				Object[] protocol = (Object[]) ois.readObject();
				String proto = (String)protocol[0];
				switch (proto) {
				case "test":
					Object[] testResponse = {proto};
					oos.writeObject(testResponse);
					break;
					
				case "join":
					Member member1 = (Member) protocol[1];
					boolean rstJoin = manager.join(member1);
					
					Object[] joinResponse = {proto, rstJoin};
					oos.writeObject(joinResponse);
					break;

				case "login":
					Member member2 = (Member) protocol[1];
					boolean rstLogin = manager.login(member2);
					
					Object[] loginResponse = {proto, rstLogin};
					oos.writeObject(loginResponse);
					break;

				case "logout":
					Member member3 = (Member) protocol[1];
					boolean rstLogout = manager.logout(member3);
					
					Object[] logoutResponse = {proto, rstLogout};
					oos.writeObject(logoutResponse);
					break;
					
				case "showList":
					Category category1 = (Category) protocol[1];
					int showNum = (int) protocol[2];
					ArrayList<Restaurant> rList = manager.showList(category1, showNum);
					
					Object[] showListResponse = {proto, rList};
					oos.writeObject(showListResponse);
					break;
				
				case "insert":
					Restaurant restaurant = (Restaurant) protocol[1];
					boolean rstInsert = manager.insertRestaurant(restaurant);
					
					Object[] insertResponse = {proto, rstInsert};
					oos.writeObject(insertResponse);
					break;

				case "evaluate":
					Evaluation evaluation = (Evaluation) protocol[1];
					restaurant = (Restaurant) protocol[2];
					boolean rstEvaluation = manager.evaluateRestaurant(evaluation, restaurant);
					
					Object[] evaluationResponse = {proto, rstEvaluation};
					oos.writeObject(evaluationResponse);
					break;
					
				case "recommend" :
					restaurant = (Restaurant) protocol[1];
					Member member = (Member) protocol[2];
					boolean rstRecommend = manager.recommendRestaurant(restaurant, member);
					
					Object[] recommendResponse = {proto, rstRecommend};
					oos.writeObject(recommendResponse);
					break;
						
				case "findAddress" :
					Address address = (Address) protocol[1];
					ArrayList<Address> addresses = manager.findAddresses(address);
					
					Object[] findAddressResponse = {proto, addresses};
					oos.writeObject(findAddressResponse);
					break;

				case "askRestaurant":
					Category category2 = (Category) protocol[1];
					Member member4 = (Member) protocol[2];
					boolean isRandom = (boolean) protocol[3];
					
					manager.askRestaurant(category2, member4, isRandom);
					
					break;

				case "replyRestaurant":
					Restaurant restaurant2 = (Restaurant) protocol[1];
					Member memberTo = (Member) protocol[2];
					Member memberFrom = (Member) protocol[3];
					
					manager.replyRestaurant(restaurant2, memberTo, memberFrom);
					
					break;

				default:
					break;
				}
				oos.reset();
			} catch (ClassNotFoundException e) {
				System.out.println("ClassNotFoundException");
				e.printStackTrace();
			} catch (IOException e) {
				flag = false;
				System.out.println("IOException");
				e.printStackTrace();
			}
		}
	}

}
