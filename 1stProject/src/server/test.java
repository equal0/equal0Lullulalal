package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import vo.Address;
import vo.Category;
import vo.Evaluation;
import vo.Member;
import vo.Restaurant;

public class test {

	public void showList() {
		ArrayList<Restaurant> showList = new ArrayList<>();
		Connection conn = ConnectionManager.getConnection();
		String sql = "select * from restaurants where location like ? and food_type=? and average_score=?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, null);
			pstmt.setInt(2, 1);
			pstmt.setDouble(3, 0);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getString(1));
				String restaurantName = rs.getString("restaurant_name");
				String location = rs.getString("location");
				Address address = new Address(location);
				int foodType = rs.getInt("food_type");
				String price = rs.getString("price_range");
				String operationHour = rs.getString("operation_Hour");
				String evaluatorComments = rs.getString("comments");
				double taste_score = rs.getDouble("taste_score");
				double service_score = rs.getDouble("service_score");
				double hygiene_score = rs.getDouble("hygiene_score");
				double average_score = rs.getDouble("average_score");
				String id = rs.getString("id");
				String image_path = rs.getString("image_path");
				String[] splitedImage = image_path.split(";");
				ImageIcon icon = null;
				ArrayList<ImageIcon> imageList = new ArrayList<>();
				for (int i = 0; i < splitedImage.length; i++) {
					icon = new ImageIcon((splitedImage[i]));
					imageList.add(icon);
				}
				String menus = rs.getString("menus");
				String[] splitedMenus = menus.split(";");
				ArrayList<String> menuList = new ArrayList<>();
				for (int i = 0; i < splitedMenus.length; i++) {
					menuList.add(splitedMenus[i]);
				}
				int recommendNum = rs.getInt("Recommend");

				String sql2 = "select * from users where id=?";
				Member evaluator = null;
				try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
					pstmt2.setString(1, "equal0");
					try (ResultSet rs2 = pstmt2.executeQuery()) {
						while (rs2.next()) {
							String password = rs2.getString("password");
							String name = rs2.getString("name");
							int permission = rs2.getInt("permission");
							String birth = rs2.getString("birth");
							evaluator = new Member(id, password, name, permission, birth);
						}
					}
				}
				String sql3 = "select eval.id, eval.score, eval.comments from evaluations eval, restaurants rest "
						+ "where eval.location = rest.location";
				ArrayList<Evaluation> userEvaluations = new ArrayList<>();
				try (PreparedStatement pstmt3 = conn.prepareStatement(sql3)) {
					try (ResultSet rs3 = pstmt3.executeQuery()) {
						while (rs3.next()) {
							String userId = rs3.getString("ID");
							double score = rs3.getDouble("SCORE");
							String comment = rs3.getString("COMMENTS");
							Member member = new Member();
							member.setId(userId);
							Evaluation evaluate = new Evaluation();
							evaluate.setAverage(score);
							evaluate.setUser(member);
							evaluate.setComment(comment);
							userEvaluations.add(evaluate);
						}
					}
				}
				if (evaluator != null) {
					Evaluation evaluations = new Evaluation(taste_score, service_score, hygiene_score, average_score,
							evaluatorComments, evaluator);
					Category categories = new Category(address, foodType, evaluations);
					Restaurant restaurants = new Restaurant(restaurantName, price, operationHour, categories, imageList,
							userEvaluations, menuList, recommendNum);
					showList.add(restaurants);
					System.out.println(showList.toString());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn);
		}
	}

	public ArrayList<Restaurant> showList(Category category, int num) {

		ArrayList<Restaurant> showList = new ArrayList<>();

		Connection conn = ConnectionManager.getConnection();
		String sql = "select * from restaurants where location like ? and food_type=? and average_score=?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, "%" + category.getLocation().toString() + "%");
			pstmt.setInt(2, category.getType());
			pstmt.setDouble(3, category.getEvaluation().getAverage());
			try (ResultSet rs = pstmt.executeQuery()) {
				while(rs.next()){
					String restaurantName = rs.getString("restaurant_name");
					String location = rs.getString("location");
					Address address = new Address(location);
					int foodType = rs.getInt("food_type");
					String price = rs.getString("price_range");
					String operationHour = rs.getString("operation_Hour");
					String evaluatorComments = rs.getString("comments");
					double taste_score = rs.getDouble("taste_score");
					double service_score = rs.getDouble("service_score");
					double hygiene_score = rs.getDouble("hygiene_score");
					double average_score = rs.getDouble("average_score");
					String id = rs.getString("id");
					String image_path = rs.getString("image_path");
					String[] splitedImage = image_path.split(";");
					ImageIcon icon = null;
					ArrayList<ImageIcon> imageList = new ArrayList<>();
					for (int i = 0; i < splitedImage.length; i++) {
						icon = new ImageIcon(splitedImage[i]);
						imageList.add(icon);
					}
					String menus = rs.getString("menus");
					String[] splitedMenus = menus.split(";");
					ArrayList<String> menuList = new ArrayList<>();
					for (int i = 0; i < splitedMenus.length; i++) {
						menuList.add(splitedMenus[i]);
					}
					int recommendNum = rs.getInt("Recommend");
					
					String sql2 = "select * from users where id=?";
					Member evaluator = null;
					try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
						pstmt2.setString(1, id);
						try (ResultSet rs2 = pstmt2.executeQuery()) {
							while (rs2.next()) {
								String password = rs2.getString("password");
								String name = rs2.getString("name");
								int permission = rs2.getInt("permission");
								String birth = rs2.getString("birth");
								evaluator = new Member(id, password, name, permission, birth);
							}
						}
					}

					String sql3 = "select eval.id, eval.score, eval.comments from evaluations eval, restaurants rest "
							+ "where eval.location = rest.location";
					ArrayList<Evaluation> userEvaluations = new ArrayList<>();
					try (PreparedStatement pstmt3 = conn.prepareStatement(sql3)) {
						try (ResultSet rs3 = pstmt3.executeQuery()) {
							while (rs3.next()) {
								String userId = rs3.getString("ID");
								double score = rs3.getDouble("SCORE");
								String comment = rs3.getString("COMMENTS");
								Member member = new Member();
								member.setId(userId);
								Evaluation evaluate = new Evaluation();
								evaluate.setAverage(score);
								evaluate.setUser(member);
								evaluate.setComment(comment);
								userEvaluations.add(evaluate);
							}
						}
					}
					if (evaluator != null) {
						Evaluation evaluations = new Evaluation(taste_score, service_score, hygiene_score, average_score,
								evaluatorComments, evaluator);
						Category categories = new Category(address, foodType, evaluations);
						Restaurant restaurants = new Restaurant(restaurantName, price, operationHour, categories, imageList,
								userEvaluations, menuList, recommendNum);
						showList.add(restaurants);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(conn);
		}
		return showList;
	}

	public static void main(String[] args) {
		/*
		 * test t = new test();
		 * 
		 * Address address = new Address("서울특별시 종로구 자하문로16길 4"); //
		 * address.setSido("서울특별시"); // address.set //
		 * address.setStreetName("자하문로16길"); // address.setBuildPrimaryNo("4");
		 * 
		 * // Member m = new Member();
		 * 
		 * Evaluation evaluation = new Evaluation(); evaluation.setAverage(4);
		 * 
		 * Category category = new Category(address, 2, evaluation);
		 * 
		 * int num = 0;
		 * 
		 * ArrayList<Restaurant> list = t.showList(category, num);
		 * 
		 * System.out.println(list);
		 */

		new test().showList();
	}
}
