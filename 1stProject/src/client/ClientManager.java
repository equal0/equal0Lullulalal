package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import manager.Interface;
import vo.Address;
import vo.Category;
import vo.Evaluation;
import vo.Member;
import vo.Restaurant;

public class ClientManager implements Interface {

	ObjectOutputStream oos;
	
	public void setOos(ObjectOutputStream oos){
		this.oos = oos;
	}

	@Override
	public boolean insertRestaurant(Restaurant restaurant) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean login(Member member) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean join(Member member) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean evaluateRestaurant(Evaluation evalauation, Restaurant restaurant) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean recommendRestaurant(Restaurant restaurant, Member valuer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayList<Restaurant> showList(Category category, int num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean logout(Member member) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void askRestaurant(Category category, Member member, boolean isRandom) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void replyRestaurant(Restaurant restaurant, Member to, Member from) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Address> findAddresses(Address address) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void test(){
		Object[] data = {"test"};
		request(data);
	}
	
	private synchronized void request(Object data){
		try {
			oos.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}