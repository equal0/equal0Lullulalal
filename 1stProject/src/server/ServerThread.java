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
	private ServerManager manager; 
	public ServerThread(ObjectInputStream ois, ObjectOutputStream oos) {
		this.ois = ois;
		this.oos = oos;
		manager = new ServerManager(oos);
	}

	@Override
	public void run() {
		boolean flag = true;
		while (flag) {
			try {

				Object[] protocol = (Object[]) ois.readObject();
				String proto = (String)protocol[1];
				switch (proto) {
				case "test":
					Object[] testResponse = {protocol[0], proto};
					oos.writeObject(testResponse);
					break;
					
				case "check":
					Object[] checkResponse = {protocol[0], proto, 
							ServerManager.RESTAURANTS_UPDATE_COUNTER,
							ServerManager.STANBY_UPDATE_COUNTER};
					oos.writeObject(checkResponse);
					break;	
					
				case "join":
					Member member1 = (Member) protocol[2];
					boolean rstJoin = manager.join(member1);
					System.out.println(rstJoin);
					Object[] joinResponse = {protocol[0], proto, rstJoin};
					System.out.println("여기서");
					oos.writeObject(joinResponse);
					System.out.println("요기서");
					break;

				case "login":
					Member member2 = (Member) protocol[2];
					member2 = manager.login(member2);
					
					Object[] loginResponse = {protocol[0], proto, member2};
					oos.writeObject(loginResponse);
					break;

				case "logout":
					Member member3 = (Member) protocol[2];
					boolean rstLogout = manager.logout(member3);
					
					Object[] logoutResponse = {protocol[0], proto, rstLogout};
					oos.writeObject(logoutResponse);
					break;
					
				case "showList":
					Category category1 = (Category) protocol[2];
					int showNum = (int) protocol[3];
					String table = (String) protocol[4];
					ArrayList<Restaurant> rList = manager.showList(category1, showNum, table);
			
					Object[] showListResponse = {protocol[0], proto, rList,  protocol[5]};
					oos.writeObject(showListResponse);
					break;
				
				case "insert":
					Restaurant restaurant = (Restaurant) protocol[2];
					boolean rstInsert = manager.insertRestaurant(restaurant);
					
					Object[] insertResponse = {protocol[0], proto, rstInsert};
					oos.writeObject(insertResponse);
					break;

				case "evaluate":
					Evaluation evaluation = (Evaluation) protocol[2];
					restaurant = (Restaurant) protocol[3];
					boolean rstEvaluation = manager.evaluateRestaurant(evaluation, restaurant);
					
					Object[] evaluationResponse = {protocol[0], proto, rstEvaluation, restaurant, evaluation};
					oos.writeObject(evaluationResponse);
					break;
					
				case "recommend" :
					restaurant = (Restaurant) protocol[2];
					Member member = (Member) protocol[3];
					boolean rstRecommend = manager.recommendRestaurant(restaurant, member);
					
					Object[] recommendResponse = {protocol[0], proto, rstRecommend, restaurant, member};
					oos.writeObject(recommendResponse);
					break;
						
				case "findAddress" :
					System.out.println("why");
					Address address = (Address) protocol[2];
					ArrayList<Address> addresses = manager.findAddresses(address);
					
					Object[] findAddressResponse = {protocol[0], proto, addresses};
					oos.writeObject(findAddressResponse);
					break;

				case "askRestaurant":
					Category category2 = (Category) protocol[2];
					Member member4 = (Member) protocol[3];
					boolean isRandom = (boolean) protocol[4];
					
					manager.askRestaurant(category2, member4, isRandom);
					
					break;

				case "replyRestaurant":
					Restaurant restaurant2 = (Restaurant) protocol[2];
					Member memberTo = (Member) protocol[3];
					Member memberFrom = (Member) protocol[4];
					
					manager.replyRestaurant(restaurant2, memberTo, memberFrom);
					
					break;

				default:
					break;
				}
				oos.reset();
				//ois.reset();
			} catch (ClassNotFoundException e) {
				System.out.println("ClassNotFoundException");
				e.printStackTrace();
			} catch (IOException e) {
				flag = false;
				System.out.println("IOException");
				//e.printStackTrace();
			}
		}
	}

}
