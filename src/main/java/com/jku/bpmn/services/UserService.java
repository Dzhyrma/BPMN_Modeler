package com.jku.bpmn.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.GsonBuilder;
import com.jku.bpmn.model.Diagram;
import com.jku.bpmn.model.Pair;
import com.jku.bpmn.model.User;
import com.jku.bpmn.model.json.JSONDiagram;

@Service("userService")
@Transactional
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private SimpleJdbcTemplate jdbcTemplate;
	private SimpleJdbcInsert jdbcInsert;
	private RowMapper<User> userMapper = new RowMapper<User>() {

		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setUserID(rs.getInt("user_id"));
			user.setUserName(rs.getString("username"));
			user.setPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setEnabled(rs.getInt("enabled"));
			return user;
		}
	};
	private RowMapper<Diagram> diagramMapper = new RowMapper<Diagram>() {

		public Diagram mapRow(ResultSet rs, int rowNum) throws SQLException {
			Diagram diagram = Diagram.convertFromJSONDiagram(new GsonBuilder().create().fromJson(rs.getString("diagram"), JSONDiagram.class));
			diagram.setId(rs.getInt("user_diagram_id"));
			return diagram;
		}
	};
	
	private RowMapper<Pair<Integer, String>> diagramListMapper = new RowMapper<Pair<Integer, String>>() {

		@Override
		public Pair<Integer, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
			Pair<Integer, String> result = new Pair<Integer, String>(rs.getInt("user_diagram_id"), new GsonBuilder().create()
				.fromJson(rs.getString("diagram"), JSONDiagram.class).getName());
			return result;
		}

	};

	private static String encryptPassword(String password) {
		return new ShaPasswordEncoder().encodePassword(password, null);
	}

	@Resource(name = "dataSource")
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
		this.jdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("user_diagrams").usingGeneratedKeyColumns("user_diagram_id");
	}

	public List<User> getAll() {
		logger.debug("Retrieving all persons");

		// Prepare our SQL statement
		String sql = "select user_id, username, password, email, enabled from users";

		// Retrieve all
		return jdbcTemplate.query(sql, userMapper);
	}

	public User getUser(String userName) {
		logger.debug("Retrieving a user with a user name: " + userName);

		// Prepare our SQL statement
		String sql = "select user_id, username, password, email, enabled from users where username = :userName";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userName", userName);

		List<User> users = jdbcTemplate.query(sql, userMapper, parameters);
		return (users.size() > 0) ? users.get(0) : null;
	}
	
	public List<Pair<Integer, String>> getDiagrams(String userName) {
		logger.debug("Retrieving a diagram from the user with name: " + userName);

		// Prepare our SQL statement
		String sql = "select user_diagram_id, diagram from user_diagrams where user_id = (select user_id from users where username = :userName)";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userName", userName);

		List<Pair<Integer, String>> diagramList = jdbcTemplate.query(sql, diagramListMapper, parameters);
		return diagramList;
	}


	public void saveDiagram(String userName, String diagram, String id) {
		logger.debug("Saving a diagram for the user with name: " + userName);

		String sqlUploadNewDiagram = "update user_diagrams set diagram=:diagram where user_id=(select user_id from users where username = :userName) and user_diagram_id = :id";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userName", userName);
		parameters.put("diagram", diagram);
		parameters.put("id", id);

		jdbcTemplate.update(sqlUploadNewDiagram, parameters);
	}
	
	public String saveDiagram(String userName, String diagram) {
		logger.debug("Saving a diagram for the user with name: " + userName);

		// String sqlUploadNewDiagram =
		// "insert into user_diagrams(user_id, diagram) values ((select user_id from users where username = :userName), :diagram)";
		User user = getUser(userName);
		if (user == null)
			return null;

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("user_id", user.getUserID());
		parameters.put("diagram", diagram);

		Number id = jdbcInsert.executeAndReturnKey(parameters);
		return (id == null) ? null : id.toString();
	}

	public void add(User user) {
		logger.debug("Adding new user");

		// Prepare our SQL statement using Named Parameters style
		String sqlUser = "insert into users(username, password, email, enabled) values (:userName, :password, :email, 1)";
		String sqlUserRole = "insert into user_roles(user_id, authority) values ((SELECT user_id FROM users WHERE username=:userName), :authority)";

		// Assign values to parameters
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userName", user.getUserName());
		parameters.put("password", encryptPassword(user.getPassword()));
		parameters.put("email", user.getEmail());
		parameters.put("authority", user.getAuthority());

		// Save
		jdbcTemplate.update(sqlUser, parameters);
		jdbcTemplate.update(sqlUserRole, parameters);

	}

	public void delete(int id) {
		logger.debug("Deleting existing user");

		// Prepare our SQL statement using Unnamed Parameters style
		String sqlUser = "delete from users where user_id = ?";
		String sqlUserRole = "delete from user_roles where user_id = ?";

		// Assign values to parameters
		Object[] parameters = new Object[] { id };

		// Delete
		jdbcTemplate.update(sqlUser, parameters);
		jdbcTemplate.update(sqlUserRole, parameters);
	}
	
	public Diagram getDiagram(String userName, String id) {
		logger.debug("Retrieving a diagram from the user with name: " + userName);

		// Prepare our SQL statement
		String sql = "select user_diagram_id, diagram from user_diagrams where user_id = (select user_id from users where username = :userName) and user_diagram_id = :id";

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userName", userName);
		parameters.put("id", id);

		List<Diagram> diagrams = jdbcTemplate.query(sql, diagramMapper, parameters);
		return (diagrams == null || diagrams.size() == 0) ? null : diagrams.get(0);
	}

	public void deleteDiagram(String userName, String id) {
		logger.debug("Deleting diagram");

		// Prepare our SQL statement using Unnamed Parameters style
		String sqlUser = "delete from user_diagrams where user_id = (select user_id from users where username = :user_name) and user_diagram_id = :id";
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("user_name", userName);
		parameters.put("id", id);
		
		jdbcTemplate.update(sqlUser, parameters);
	}

	/*
	 * public void edit(User user) { logger.debug("Editing existing person");
	 * 
	 * // Prepare our SQL statement String sql =
	 * "update person set first_name = :firstName, " +
	 * "last_name = :lastName, money = :money where id = :id";
	 * 
	 * // Assign values to parameters Map<String, Object> parameters = new
	 * HashMap<String, Object>(); parameters.put("id", id);
	 * parameters.put("firstName", firstName); parameters.put("lastName",
	 * lastName); parameters.put("money", money);
	 * 
	 * // Edit jdbcTemplate.update(sql, parameters);
	 * 
	 * }
	 */
}
