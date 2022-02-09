package benchmarks.example;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class example {
	private Connection connect = null;
	private int _ISOLATION = Connection.TRANSACTION_READ_COMMITTED;
	private int id;
	Properties p;

	public example(int id) {
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

	/*private void close() {
		try {
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void one_increment_write_skew(int key1 , int key2 , int amount2) throws SQLException {
		// read account 1
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setInt(1, key1);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
		System.out.println(read_val);
		// update account 2
		if (read_val + amount2 < 1000 ) {
			PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
			stmt1.setInt(1, amount2);
			stmt1.setInt(2, key2);
			stmt1.executeUpdate();
		}
	}*/

	public void cluster_c1_one_increment_lost_update(int key, int amount) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setInt(1, key);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
		System.out.println(read_val);

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt2.setInt(1, read_val + amount);
		stmt2.setInt(2, key);
		stmt2.executeUpdate();
	}

	/*public void one_read(int key) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setInt(1, key);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val = rs.getInt("VALUE");
		assert (read_val == 100);
		System.out.println(read_val);
	}*/

	public void cluster_c2_two_writes(int key) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt1.setInt(1, 50);
		stmt1.setInt(2, key);
		stmt1.executeUpdate();

		PreparedStatement stmt2 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt2.setInt(1, 100);
		stmt2.setInt(2, key);
		stmt2.executeUpdate();
	}
	

	public void cluster_c1_two_reads(int key) throws SQLException {
		PreparedStatement stmt = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt.setInt(1, key);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		int read_val1 = rs.getInt("VALUE");
		System.out.println(read_val1);

		PreparedStatement stmt2 = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt2.setInt(1, key);
		ResultSet rs2 = stmt2.executeQuery();
		rs2.next();
		int read_val2 = rs2.getInt("VALUE");
		System.out.println(read_val2);
		assert (read_val1 == read_val2);
	}

	/*public void one_write(int key, int new_val) throws SQLException {
		PreparedStatement stmt1 = connect.prepareStatement("UPDATE ACCOUNTS SET value = ?" + " WHERE id = ?");
		stmt1.setInt(1, new_val);
		stmt1.setInt(2, key);
		stmt1.executeUpdate();
	}

	public void two_reads_long_fork(int key1, int key2) throws SQLException {
		// read account 1
		PreparedStatement stmt1 = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt1.setInt(1, key1);
		ResultSet rs1 = stmt1.executeQuery();
		rs1.next();
		int read_val1 = rs1.getInt("VALUE");
		System.out.println(read_val1);

		// read account 2
		PreparedStatement stmt2 = connect.prepareStatement("SELECT value " + "FROM " + "ACCOUNTS" + " WHERE id = ?");
		stmt2.setInt(1, key2);
		ResultSet rs2 = stmt2.executeQuery();
		rs2.next();
		int read_val2 = rs2.getInt("VALUE");
		System.out.println(read_val2);
	}*/
}