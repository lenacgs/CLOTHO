package com.github.kiarahmani.replayer;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class Client {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public Client(int id) {
		this.id = id;
		p = new Properties();
		p.setProperty("id", String.valueOf(this.id));
		Object o;
		try {
			o = Class.forName("MyDriver").newInstance();
			DriverManager.registerDriver((Driver) o);
			Driver driver = DriverManager.getDriver("jdbc:mydriver://");
			connect = driver.connect("", p);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void one_increment_lost_update(Long key, Long amount) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setLong(1, key);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		Long read_val = rs.getLong("VALUE");
		System.out.println(read_val);

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt2.setLong(1, read_val + amount);
		stmt2.setLong(2, key);
		stmt2.executeUpdate();
	}

	

	public void two_writes(Long key) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt1.setLong(1, 50);
		stmt1.setLong(2, key);
		stmt1.executeUpdate();

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt2.setLong(1, 100);
		stmt2.setLong(2, key);
		stmt2.executeUpdate();
	}

	public void two_reads(Long key) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setLong(1, key);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		Long read_val1 = rs.getLong("VALUE");
		System.out.println(read_val1);

		PreparedStatement stmt2 = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt2.setLong(1, key);
		ResultSet rs2 = stmt2.executeQuery();
		rs2.next();
		Long read_val2 = rs2.getLong("VALUE");
		System.out.println(read_val2);
		assert (read_val1 == read_val2);
	}

	



}