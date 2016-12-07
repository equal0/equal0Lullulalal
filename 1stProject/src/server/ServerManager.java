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

import manager.Interface;
import vo.Address;
import vo.Category;
import vo.Evaluation;
import vo.Member;
import vo.Restaurant;

public class ServerManager implements Interface{

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
			ps.setInt(4, Member.USER); //�ű�ȸ�������ڴ� ��� user ���Ѹ��� ������.
			ps.setString(5, member.getBirth());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} finally {
			ConnectionManager.close(connection);
		}
		return true;
	}

	@Override
	public boolean login(Member member) {
		Connection connection = ConnectionManager.getConnection();
		try(Statement st = connection.createStatement();) {
			
			String sql = "select id, password from users where id = '" + member.getId() 
							+ "' and password = '" + member.getPassword() + "'";
			try(ResultSet rs = st.executeQuery(sql);){
				if(rs.next()){
					userList.put(member.getId(), oos);
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			ConnectionManager.close(connection);
		}
		return false;
	}

	@Override
	public boolean logout(Member member) {
		userList.remove(member.getId());
		return true;
	}

	@Override
	public ArrayList<Restaurant> showList(Category category, int num) {
		ArrayList<Restaurant> showList = new ArrayList<>();
		Connection connection = ConnectionManager.getConnection();
		try {
			Statement st = connection.createStatement();
			String sql = "select * from restaurants where location like '%" + category.getLocation().toString() + "%' "
					+ "and food_type = " + category.getType() + "" + " and average_score = "
					+ category.getEvaluation().getAverage() + "";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				System.out.println("while");
				String restaurantName = rs.getString("restaurant_name");
				String location = rs.getString("location");
				Address address = new Address(location);
				int foodType = rs.getInt("food_type");
				String price = rs.getString("price_range");
				String operationHour = rs.getString("operation_Hour");

				String id = rs.getString("id");
				Statement st2 = connection.createStatement();
				String sql2 = "select * from users where id = '" + id + "'";
				ResultSet rs2 = st2.executeQuery(sql2);
				Member evaluator = null;
				if (rs2.next()) {
					String password = rs2.getString("password");
					String name = rs2.getString("name");
					int permission = rs2.getInt("permission");
					String birth = rs2.getString("birth");
					evaluator = new Member(id, password, name, permission, birth);
				}
				st2.close();
				rs2.close();

				String evaluatorComments = rs.getString("comments");
				double taste_score = rs.getDouble("taste_score ");
				double service_score = rs.getDouble("service_score");
				double hygiene_score = rs.getDouble("hygiene_score");
				double average_score = rs.getDouble("average_score");
				Evaluation evaluation = new Evaluation(taste_score, service_score, hygiene_score, average_score,
						evaluatorComments, evaluator);
				Category categories = new Category(address, foodType, evaluation);

				String image_path = rs.getString("image_path");
				String[] splitedImage = image_path.split(";");
				ImageIcon icon = null;
				ArrayList<ImageIcon> imageList = new ArrayList<>();
				for (int i = 0; i < splitedImage.length; i++) {
					icon = new ImageIcon(this.getClass().getResource(splitedImage[i]));
					imageList.add(icon);
				}
				String menus = rs.getString("menus");
				String[] splitedMenus = menus.split(";");
				ArrayList<String> menuList = new ArrayList<>();
				for (int i = 0; i < splitedMenus.length; i++) {
					menuList.add(splitedMenus[i]);
				}
				int recommendNum = rs.getInt("Recommend");

				Statement st3 = connection.createStatement();
				String sql3 = "select eval.id, eval.score, eval.comments from evaluations eval, restaurants rest "
						+ "where eval.location = rest.location";
				ResultSet rs3 = st3.executeQuery(sql3);
				ArrayList<Evaluation> userEvaluation = new ArrayList<>();
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
					userEvaluation.add(evaluate);
				}
				Restaurant restaurant = new Restaurant(restaurantName, price, operationHour, categories, imageList,
						userEvaluation, menuList, recommendNum);
				showList.add(restaurant);
				st3.close();
				rs3.close();
			}
			st.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.close(connection);
		}
		if (num == 0) {
			num = showList.size();
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
				
				sql = "insert into evaluations values(?, ?, ?, ?)";
				try(PreparedStatement pstmt = conn.prepareCall(sql)){
					pstmt.setString(1, evaluation.getUser().getId());
					pstmt.setString(2, restaurant.getCategory().getLocation().toString());
					pstmt.setDouble(3, evaluation.getAverage());
					pstmt.setString(4, evaluation.getComment());
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
		
		String sql = "insert into stanby values(?, ?, ?, ?, ?,"
											+ " ?, ?, ?, ?, ?,"
											+ " ?, ?, ?, ?, ?)";
		
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
			
			pstmt.executeUpdate();
			
		} catch (SQLException e){
			e.printStackTrace();
			
			for(String imgPath : tempImagePaths){
				File file = new File(imgPath);
				file.delete();
			}
			
			return false;
		} finally{
			ConnectionManager.close(conn);
		}
		
		return true;
	}

	@Override
	public boolean recommendRestaurant(Restaurant restaurant) {
		Connection conn = ConnectionManager.getConnection();
		String sql = "update stanby set recommend=? where location=?";
		
		try {
			try(PreparedStatement pstmt = conn.prepareCall(sql)){
				System.out.println(restaurant.getRecommendNum());
				pstmt.setInt(1, restaurant.getRecommendNum());
				pstmt.setString(2, restaurant.getCategory().getLocation().toString());
				
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
		if (isRandom == true){

			ArrayList<Restaurant> restaurants = showList(category, 0);
			
			int randomNum = (int)(Math.random() * restaurants.size());
			
			Restaurant restaurant = restaurants.get(randomNum);

			Object[] data = new Object[4];
			
			data[0] = "replyRestaurant";
			data[1] = restaurant;
			data[2] = member;
			Member from = new Member();
			from.setId("�ܰ��� �߷�׻�");
			data[3] = from; 
			
			ObjectOutputStream toOos = userList.get(member.getId());
			
			try {
				toOos.writeObject(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			Object[] data = new Object[3];
			data[0] = "askRestaurant";
			data[1] = category;
			data[2] = member;
			
			for(Map.Entry<String, ObjectOutputStream> entry : userList.entrySet()) {
			    //String key = entry.getKey();
			    ObjectOutputStream oos = entry.getValue();
			    try {
					oos.writeObject(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void replyRestaurant( Restaurant restaurant, Member to, Member from) {
		Object[] data = new Object[4];
		
		data[0] = "replyRestaurant";
		data[1] = restaurant;
		data[2] = to;
		data[3] = from;
		
		ObjectOutputStream toOos = userList.get(to.getId());
		
		try {
			toOos.writeObject(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

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
		ServerManager testManager = new ServerManager();
//findAddress test
		/*Address address = new Address();
		
		address.setSido("����Ư����");
		address.setSigungu("���α�");
		address.setStreetName("���Ϲ���16��");
		address.setBuildPrimaryNo("4");
		//address.setBuildSecondaryNo("0");
		
		ArrayList<Address> addresses = testManager.findAddresses(address);
		
		for(Address a : addresses){
			System.out.println(a);
		}*/
		//-----------------------------------------------------
//insert test
		Restaurant restaurant = new Restaurant();

		restaurant.setRestaurantName("��ũ���");
		restaurant.setPrice("4500");
		restaurant.setOperationHour("09:00~17:00");
		ArrayList<String> menus = new ArrayList<>();
		menus.add("������");
		menus.add("����");
		menus.add("�����");
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
			sourceimage = new File("d:/test/4.jpg");
			image = ImageIO.read(sourceimage);
			images.add(new ImageIcon(image));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		restaurant.setImages(images);
		Category category = new Category();
		category.setType(Category.KOREAN);
		Address address = new Address("(110034) ����Ư���� ���α� ���Ϲ���17�� 4 (â����);02-1111-1111");
		category.setLocation(address);
		Evaluation evaluation= new Evaluation();
		evaluation.setComment("�����̵�����");
		evaluation.setHygiene(2);
		evaluation.setService(1);
		evaluation.setTaste(2);
		evaluation.setAverage(5);
		Member user = new Member();
		user.setBirth("1988/11/25");
		user.setId("lullulalal");
		user.setPassword("hahah");
		user.setName("���μ�");
		user.setPermission(Member.USER);
		evaluation.setUser(user);
		
		category.setEvaluation(evaluation);
		restaurant.setCategory(category);
		
		boolean rst = testManager.insertRestaurant(restaurant);
		System.out.println(rst); 
		
		//--------------------------------------------------------------
//recommend test
		//restaurant.plusRecommend();
		//restaurant.plusRecommend();
		//restaurant.plusRecommend();
		//boolean rst = testManager.recommendRestaurant(restaurant);
		//System.out.println(rst); 
		
		//-------------------------------------------------------------
//evaluateResataurant test
		/*Evaluation e = new Evaluation();
		e.setAverage(1);
		e.setComment("������׿�");
		e.setHygiene(2);
		e.setService(3); 
		e.setTaste(4);
		Member commentor = new Member();
		commentor.setId("equal0");
		commentor.setPermission(2);
		e.setUser(commentor);
		testManager.evaluateRestaurant(e, restaurant);*/
	}
}
