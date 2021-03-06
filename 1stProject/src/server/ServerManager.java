package server;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import client.Config;
import manager.Interface;
import vo.Address;
import vo.Category;
import vo.Evaluation;
import vo.Member;
import vo.Restaurant;

public class ServerManager implements Interface{

	public static int RESTAURANTS_UPDATE_COUNTER = 0;
	public static int STANBY_UPDATE_COUNTER = 0;
	
	private ObjectOutputStream oos;
	private static ConcurrentHashMap<String, ObjectOutputStream> userList = new ConcurrentHashMap<>();
	
	public ServerManager(ObjectOutputStream oos) {
		this.oos = oos;
	}
	
	public ServerManager() {

	}
	
	
	@Override
	public boolean join(Member member) {
		Connection connection = ConnectionManager.getConnection();
		String sql = "insert into users(id, password, name, permission, birth) values(?,?,?,?,?)";
		try(PreparedStatement ps = connection.prepareStatement(sql); ) {
			ps.setString(1, member.getId());
			ps.setString(2, member.getPassword());
			ps.setString(3, member.getName());
			ps.setInt(4, Member.USER); //신규회원가입자는 모두 user 권한만을 가진다.
			ps.setString(5, member.getBirth());
			ps.executeUpdate();
		} catch (SQLException e) {
			return false;
		} finally {
			ConnectionManager.close(connection);
		}
		return true;
	}

	@Override
	public Member login(Member member) {
		Connection connection = ConnectionManager.getConnection();
		try(Statement st = connection.createStatement();) {
			
			String sql = "select * from users where id = '" + member.getId() 
							+ "' and password = '" + member.getPassword() + "'";
			try(ResultSet rs = st.executeQuery(sql);){
				if(rs.next()){
					String id = member.getId();
					System.out.println(id + " " + oos);
					userList.put(member.getId(), oos);
					member.setName(rs.getString("name"));
					member.setBirth(rs.getString("birth"));
					member.setPermission(rs.getInt("permission"));
					return member;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		}
		finally{
			ConnectionManager.close(connection);
		}
		return null;
	}

	@Override
	public boolean logout(Member member) {
		System.out.println(member.getId());
		userList.remove(member.getId());
		return true;
	}

	@Override
	public ArrayList<Restaurant> showList(Category category, int num, String table) {

		ArrayList<Restaurant> showList = new ArrayList<>();

		Connection conn = ConnectionManager.getConnection();
		//String sql = "select * from restaurants where location like ? and food_type=? and average_score>=?";
		StringBuilder stb = null;
		if (category.getEvaluation() == null && 
				category.getLocation() == null && 
				category.getType() == 0){
			stb = new StringBuilder("select * from ");
			stb.append(table);
		}
		else {
			stb = new StringBuilder("select * from ");
			stb.append(table);
			stb.append(" where");
		}

		
		int[] t = {0, 0, 0};
		int test = 0;
		if (category.getLocation() != null){
			test++;
			t[0] = test;
			stb.append(" location like ?");
		}
		System.out.println(category.getType());
		if (category.getType() != 0){
			if(test > 0)
				stb.append(" and");
			
			test++;
			t[1] = test;
			
			stb.append(" food_type=?");
		}
		if (category.getEvaluation() != null){
			
			if(test > 0)
				stb.append(" and");
			
			test++;
			t[2] = test;
			
			stb.append(" average_score>=?");
		}
		
		stb.append(" order by insert_date desc");
		
		try (PreparedStatement pstmt = conn.prepareStatement(stb.toString())) {
			if (category.getLocation() != null) pstmt.setString(t[0], "%" + category.getLocation().toString() + "%");
			if (category.getType() != 0) pstmt.setInt(t[1], category.getType());
			if (category.getEvaluation() != null) pstmt.setDouble(t[2], category.getEvaluation().getAverage());
			
			try (ResultSet rs = pstmt.executeQuery()) {
				if (num == 0) {
					num = 2147483647;
				}
				for(int j = 0; j < num; ++j){
					if (rs.next() == false)
						break;
				//while (rs.next()) {
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
					
					String recommenders = rs.getString("recommender");
					String[] splitedRecommenders = recommenders.split(";");
					ArrayList<String> recommenderList = new ArrayList<>();
					for (String s : splitedRecommenders ){
						recommenderList.add(s);
					}

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
							+ "where eval.location = rest.location and eval.location = ? order by eval.insert_date desc";
					ArrayList<Evaluation> userEvaluations = new ArrayList<>();
					try (PreparedStatement pstmt3 = conn.prepareStatement(sql3)) {
						pstmt3.setString(1, location);
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
						Evaluation evaluations = new Evaluation(taste_score, service_score, hygiene_score,
								average_score, evaluatorComments, evaluator);
						Category categories = new Category(address, foodType, evaluations);
						Restaurant restaurants = new Restaurant(restaurantName, price, operationHour, categories,
								imageList, userEvaluations, menuList, recommendNum, recommenderList);
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
	
	@Override
	public boolean evaluateRestaurant(Evaluation evaluation, Restaurant restaurant) {
		
		boolean rtn = false;
		Connection conn = ConnectionManager.getConnection();
		
		try {
				String sql = "select * from evaluations where id=? and location=?";
				try(PreparedStatement pstmt = conn.prepareCall(sql)){
					pstmt.setString(1, evaluation.getUser().getId());
					pstmt.setString(2, restaurant.getCategory().getLocation().toString());
					
					try(ResultSet rs = pstmt.executeQuery();) {
						while(rs.next()){
							return rtn;
						}
					}
				}catch (SQLException e) {
					e.printStackTrace();
					return rtn;
				}
				
				sql = "insert into evaluations values(?, ?, ?, ?, ?)";
				try(PreparedStatement pstmt = conn.prepareCall(sql)){
					pstmt.setString(1, evaluation.getUser().getId());
					pstmt.setString(2, restaurant.getCategory().getLocation().toString());
					pstmt.setDouble(3, evaluation.getAverage());
					pstmt.setString(4, evaluation.getComment());
					pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
					pstmt.executeUpdate();
					rtn = true;
				} catch (SQLException e) {
					e.printStackTrace();
				}

		}  finally {
			ConnectionManager.close(conn);
		}
		
		return rtn;
	}
	
	@Override
	public boolean insertRestaurant(Restaurant restaurant) {
		Connection conn = ConnectionManager.getConnection();
		
		String sql = "select * from restaurants where location=?";
		try(PreparedStatement pstmt = conn.prepareCall(sql);){
			pstmt.setString(1, restaurant.getCategory().getLocation().toString());
			try( ResultSet rs = pstmt.executeQuery();){
				while(rs.next())
					return false;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		sql = "select * from stanby where location=?";
		try(PreparedStatement pstmt = conn.prepareCall(sql);){
			pstmt.setString(1, restaurant.getCategory().getLocation().toString());
			try( ResultSet rs = pstmt.executeQuery();){
				while(rs.next())
					return false;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		sql = "insert into stanby values(?, ?, ?, ?, ?,"
											+ " ?, ?, ?, ?, ?,"
											+ " ?, ?, ?, ?, ?,"
											+ " ?)";
		
		ArrayList<String> tempImagePaths = new ArrayList<>();
		
		try(PreparedStatement pstmt = conn.prepareCall(sql);){
			pstmt.setString(1, restaurant.getRestaurantName());
			pstmt.setString(2, restaurant.getCategory().getLocation().toString());
			pstmt.setInt(3, restaurant.getCategory().getType());
			pstmt.setString(4, restaurant.getPrice());
			pstmt.setString(5, restaurant.getOperationHour());
			pstmt.setString(6, restaurant.getCategory().getEvaluation().getUser().getId());
			pstmt.setString(7, restaurant.getCategory().getEvaluation().getComment());
			pstmt.setDouble(8, restaurant.getCategory().getEvaluation().getTaste());
			pstmt.setDouble(9, restaurant.getCategory().getEvaluation().getService());
			pstmt.setDouble(10, restaurant.getCategory().getEvaluation().getHygiene());
			pstmt.setDouble(11, restaurant.getCategory().getEvaluation().getAverage());
			
			StringBuilder strbImagePath = new StringBuilder();
			for(ImageIcon icon : restaurant.getImages()){
		
				try {
					Image img = icon.getImage();

					BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);

					Graphics2D g2 = bi.createGraphics();
					g2.drawImage(img, 0, 0, null);
					g2.dispose();
			
					String imagePath = "d:/" + Long.toString( System.currentTimeMillis() ) + ".jpg";
					ImageIO.write(bi, "jpg", new File(imagePath));
					tempImagePaths.add(imagePath);
					strbImagePath.append(imagePath);
					strbImagePath.append(";");
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			pstmt.setString(12, strbImagePath.toString());
			
			StringBuilder strbMenu = new StringBuilder();
			for(String s : restaurant.getMenu()) {
				strbMenu.append(s);
				strbMenu.append(";");
			}

			pstmt.setString(13, strbMenu.toString());
			
			pstmt.setInt(14, restaurant.getRecommendNum());
			pstmt.setTimestamp(15, new Timestamp(new Date().getTime()));
			pstmt.setString(16, restaurant.getCategory().getEvaluation().getUser().getId());
			pstmt.executeUpdate();
			
		} catch (SQLException e){
			e.printStackTrace();
			
			for(String imgPath : tempImagePaths){
				File file = new File(imgPath);
				if(file.exists())
					file.delete();
			}
			
			return false;
		} finally{
			ConnectionManager.close(conn);
		}
		
		ServerManager.STANBY_UPDATE_COUNTER++;
		return true;
	}

	@Override
	public boolean recommendRestaurant(Restaurant restaurant, Member valuer) {
		
		ArrayList<String> recommender = restaurant.getRecommender();
		for(String s :recommender){
			if ( valuer.getId().equals(s) )
				return false;
		}
		
		Connection conn = ConnectionManager.getConnection();
		String sql = "update stanby set recommend=?, recommender=? where location=?";
		
		try {
			try(PreparedStatement pstmt = conn.prepareCall(sql)){
				System.out.println(restaurant.getRecommendNum());
				pstmt.setInt(1, restaurant.getRecommendNum());
				
				StringBuilder sb = new StringBuilder();
				for(String id : recommender){
					sb.append(id);
					sb.append(";");
				}
				sb.append(valuer.getId());
				
				pstmt.setString(2, sb.toString());	
				
				pstmt.setString(3, restaurant.getCategory().getLocation().toString());
				
				pstmt.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
			
			if (restaurant.getRecommendNum() >= Restaurant.BOUNDARY_OF_ASCEND ){
				sql = "insert into Restaurants select * from stanby where location=?";
				try(PreparedStatement pstmt = conn.prepareCall(sql)){
					pstmt.setString(1, restaurant.getCategory().getLocation().toString());
					pstmt.executeUpdate();
					RESTAURANTS_UPDATE_COUNTER++;
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
				
				sql = "delete stanby where location=?";
				try(PreparedStatement pstmt = conn.prepareCall(sql)){
					pstmt.setString(1, restaurant.getCategory().getLocation().toString());
					pstmt.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
					return false;
				}
				
			}
		} finally {
			ConnectionManager.close(conn);
		}
		
		return true;
	}
	
	@Override
	public void askRestaurant(Category category, Member member, boolean isRandom) {
		if (isRandom == true) {

			ArrayList<Restaurant> restaurants = showList(category, 0, Config.RESTAURANT_TABLE);
			
			if(restaurants.size() != 0){
				int randomNum = (int) (Math.random() * restaurants.size());
				System.out.println("맛집 수는 " + restaurants.size());
				Restaurant restaurant = restaurants.get(randomNum);
				
				Object[] data = new Object[5];
				data[0] = 0;
				data[1] = "replyRestaurant";
				data[2] = restaurant;
				data[3] = member;
					Member from = new Member();
					from.setId("외계인 삐루뿡뿡");
					data[4] = from;
	
					ObjectOutputStream toOos = userList.get(member.getId());
	
					try {
						toOos.writeObject(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		} else {
			Object[] data = new Object[4];
			data[0] = 0;
			data[1] = "askRestaurant";
			data[2] = category;
			data[3] = member;

			for (Map.Entry<String, ObjectOutputStream> entry : userList.entrySet()) {
				String key = entry.getKey();
				if(key.equals(member.getId())){
					continue;
				}
				ObjectOutputStream toOos = entry.getValue();
				try {
					toOos.writeObject(data);
					toOos.reset();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void replyRestaurant(Restaurant restaurant, Member to, Member from) {
		Object[] data = new Object[5];
		data[0] = 0;
		data[1] = "replyRestaurant";
		data[2] = restaurant;
		data[3] = to;
		data[4] = from;

		ObjectOutputStream toOos = userList.get(to.getId());

		try {
			toOos.writeObject(data);
			toOos.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//주의 사항
	//si_do, si_gun_gu, streetName, primBuildNum, secBuildNum
	//si_do 는 무조건 넣어야됨.
	//뒤에는 넣어도 되고 안넣어도 되지만 중간에 빈정보가 있으면 안됨.
	@Override
	public ArrayList<Address> findAddresses(Address address) {
		ArrayList<Address> rtnList = new ArrayList<>();
		
		Connection conn = ConnectionManager.getConnection();
		try {
			String si_do = address.getSido();
			String si_gun_gu = address.getSigungu();
			String streetName = address.getStreetName();
			String primBuildNum = address.getBuildPrimaryNo();
			String secBuildNum = address.getBuildSecondaryNo();
			
			StringBuilder stb = new StringBuilder("select * from addresses where");
			
			if (si_do != null) stb.append(" si_do=?");
			if (si_gun_gu != null) stb.append(" and si_gun_gu=?");
			if (streetName != null) stb.append(" and street_name=?");
			if (primBuildNum != null) stb.append(" and building_primary_no=?");
			if (secBuildNum != null) stb.append(" and building_secondary_no=?");
			
			try(PreparedStatement pstmt = conn.prepareCall(stb.toString())){
				
				if (si_do != null) pstmt.setString(1, si_do);
				if (si_gun_gu != null) pstmt.setString(2, si_gun_gu);
				if (streetName != null) pstmt.setString(3, streetName);
				if (primBuildNum != null) pstmt.setString(4, primBuildNum);
				if (secBuildNum != null) pstmt.setString(5, secBuildNum);
				
				try(ResultSet rs = pstmt.executeQuery()){
				
					while(rs.next()){
						Address addr = new Address();
						addr.setSido(rs.getString("si_do"));
						addr.setSigungu(rs.getString("si_gun_gu"));
						addr.setStreetName(rs.getString("street_name"));
						addr.setBuildingName(rs.getString("building_name"));
						addr.setPostCode(rs.getString("zip_code"));
						addr.setBuildPrimaryNo(rs.getString("building_primary_no"));
						String bsn = rs.getString("building_secondary_no");
						if(!("0".equals(bsn)))
							addr.setBuildSecondaryNo(bsn);
						addr.setDong(rs.getString("dong"));
						addr.setRi(rs.getString("ri"));
						rtnList.add(addr);
					}
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}finally{
			ConnectionManager.close(conn);
		}
		
		return rtnList;
	}
	
	public static void main(String[] args){
		//ServerManager testManager = new ServerManager();
//findAddress test
		/*Address address = new Address();
		
		address.setSido("서울특별시");
		address.setSigungu("종로구");
		address.setStreetName("자하문로16길");
		address.setBuildPrimaryNo("4");
		//address.setBuildSecondaryNo("0");
		
		ArrayList<Address> addresses = testManager.findAddresses(address);
		
		for(Address a : addresses){
			System.out.println(a);
		}*/
		//-----------------------------------------------------
//insert test
		Restaurant restaurant = new Restaurant();

		restaurant.setRestaurantName("카페테리아");
		restaurant.setPrice("4500");
		restaurant.setOperationHour("09:00~17:00");
		ArrayList<String> menus = new ArrayList<>();
		menus.add("돈가스");
		menus.add("피자");
		menus.add("자장면");
		restaurant.setMenu(menus);
		ArrayList<ImageIcon> images = new ArrayList<>();
		
		try {
			File sourceimage = new File("d:/test/1.jpg");
			Image image = ImageIO.read(sourceimage);
			images.add(new ImageIcon(image));
			sourceimage = new File("d:/test/2.jpg");
			image = ImageIO.read(sourceimage);
			images.add(new ImageIcon(image));
			sourceimage = new File("d:/test/3.jpg");
			image = ImageIO.read(sourceimage);
			images.add(new ImageIcon(image));
			sourceimage = new File("d:/test/3.jpg");
			image = ImageIO.read(sourceimage);
			images.add(new ImageIcon(image));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		restaurant.setImages(images);
		Category category = new Category();
		category.setType(Category.KOREAN);
		Address address = new Address("(110035) 서울특별시 종로구 자하문로16길 4 (창성동) 02-1111-1111");
		category.setLocation(address);
		Evaluation evaluation= new Evaluation();
		evaluation.setComment("스고이데스네");
		evaluation.setHygiene(2);
		evaluation.setService(1);
		evaluation.setTaste(2);
		evaluation.setAverage(5);
		Member user = new Member();
		user.setBirth("1988/11/25");
		user.setId("lullulalal");
		user.setPassword("hahah");
		user.setName("서민수");
		user.setPermission(Member.USER);
		evaluation.setUser(user);
		
		category.setEvaluation(evaluation);
		restaurant.setCategory(category);
		ArrayList<String> recommender = new ArrayList<>();
		recommender.add("lullulalal");
		recommender.add("equal0");
		recommender.add("lullulalall");
		recommender.add("lullulalalll");
		//recommender.add("lullulalallll");
		restaurant.setRecommender(recommender);
		//boolean rst = testManager.insertRestaurant(restaurant);
		//System.out.println(rst); 
		
		//--------------------------------------------------------------
//recommend test
		restaurant.plusRecommend();
		restaurant.plusRecommend();
		restaurant.plusRecommend();
		Member commentor = new Member();
		commentor.setId("lullulalallll");
		commentor.setPermission(2);
		//boolean rst = testManager.recommendRestaurant(restaurant, commentor);
	//	System.out.println(rst); 
		
		//-------------------------------------------------------------
//evaluateResataurant test
		/*Evaluation e = new Evaluation();
		e.setAverage(1);
		e.setComment("쓰레기네요");
		e.setHygiene(2);
		e.setService(3); 
		e.setTaste(4);
		Member commentor = new Member();
		commentor.setId("lullulalallll");
		commentor.setPermission(2);
		e.setUser(commentor);
		System.out.println(testManager.evaluateRestaurant(e, restaurant));*/

		//-------------------------------------------------------------	
//showList test
	/*	Category category = new Category();
		category.setType(Category.KOREAN);
		//
		Evaluation evaluation = new Evaluation();
		evaluation.setAverage(2);
		//category.setEvaluation(evaluation);
		Address location = new Address();
		//location.setSido("서울특별시");
		//location.setSigungu("종로구");
		location.setPhone("02-1111-1111");
		category.setLocation(location);
		//System.out.println(location);
		ArrayList<Restaurant> t = testManager.showList(category, 1);
		for(Restaurant r : t) {
			System.out.println(r.getRestaurantName());
			System.out.println(r.getOperationHour());
			System.out.println(r.getPrice());
			System.out.println(r.getRecommendNum());
			
			Category category2 = r.getCategory();
			Evaluation e = category2.getEvaluation();
			System.out.println(e.getAverage());
			System.out.println(e.getHygiene());
			System.out.println(e.getService());
			System.out.println(e.getTaste());
			System.out.println(e.getUser().getBirth());
			System.out.println(e.getUser().getId());
			System.out.println(e.getUser().getName());
			System.out.println(e.getUser().getPassword());
			System.out.println(e.getUser().getPermission());
			System.out.println(e.getComment());
			System.out.println(category2.getLocation().toString());
			System.out.println(category2.getType());
			
			for (String id : r.getRecommender()){
				System.out.println(id);
			}
			for(ImageIcon ii: r.getImages()){
				Image img = ii.getImage();

				BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);

				Graphics2D g2 = bi.createGraphics();
				g2.drawImage(img, 0, 0, null);
				g2.dispose();
		
				String imagePath = "d:/tteesstt/" + Long.toString( System.currentTimeMillis() ) + ".jpg";
				try {
					ImageIO.write(bi, "jpg", new File(imagePath));
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
			//r.getImages();
			
			ArrayList<Evaluation> es = r.getUserEvaluations();
			for(Evaluation e1 : es){
				System.out.println("============================");
				System.out.println(e1.getAverage());
				//System.out.println(e.getHygiene());
				//System.out.println(e.getService());
				//System.out.println(e.getTaste());
				System.out.println(e1.getComment());
				System.out.println(e1.getUser().getId());
				System.out.println("============================");
			}
		}*/
	}
}
