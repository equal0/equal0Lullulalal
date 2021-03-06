package manager;

import java.util.ArrayList;

import vo.Address;
import vo.Category;
import vo.Evaluation;
import vo.Member;
import vo.Restaurant;

public interface Interface {
	public boolean insertRestaurant(Restaurant restaurant);
	public Member login(Member member);
	public boolean join(Member member);
	public boolean evaluateRestaurant(Evaluation evalauation, Restaurant restaurant);
	public boolean recommendRestaurant(Restaurant restaurant, Member valuer);
	public ArrayList<Restaurant> showList(Category category, int num, String table);
	public boolean logout(Member member);
	public void askRestaurant(Category category, Member member, boolean isRandom);
	public void replyRestaurant(Restaurant restaurant, Member to, Member from);
	public ArrayList<Address> findAddresses(Address address);
	//public void checkStatement(int guiId);
}
