import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 *  连接数据库
 *  作者:戴睿轩
 */
public class database {

	String driver = "com.mysql.jdbc.Driver";
	String url = "jdbc:mysql://localhost/miniQQ";
	String user = "root";
	String pwd = "";

	String s = "";
	String sss;
	String stg;
	int k = 0;

	// 从数据库中读出数据
	public String read(String str1, String str2, String str3, String str4) {
		Connection con = null;
		Statement st;
		ResultSet re;

		String s1 = "SELECT * FROM " + str4 + " WHERE " + str1 + "='" + str2
				+ "' ";

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			re = st.executeQuery(s1);// 执行SQL语句
			while (re.next()) {
				s = re.getString(str3);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	// 写入数据到数据库
	public void write(String qqnum, String str1, String str2, String c2) {
		Connection con = null;
		Statement st;

		String s2 = "UPDATE " + c2 + " SET " + str1 + "='" + str2
				+ "' WHERE userID='" + qqnum + "' ";
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			st.executeUpdate(s2);// 执行SQL语句
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 记录符合条件的项数

	public int count(String qqnum, String c1) {
		Connection con = null;
		Statement st;
		ResultSet re;
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/miniQQ";
		String user = "root";
		String pwd = "";
		String s1 = "SELECT * FROM " + c1 + " WHERE userID='" + qqnum + "' ";

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			re = st.executeQuery(s1);// 执行SQL语句
			while (re.next()) {
				k++;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return k;
	}

	// 逐条第t条符合的项,找出表C1中，第t条str栏目中的项目
	public String readOne(String qqnum, String str, String c1, int t) {
		Connection con = null;
		Statement st;
		ResultSet re;
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/miniQQ";
		String user = "root";
		String pwd = "";
		String s1 = "SELECT * FROM " + c1 + " WHERE userID='" + qqnum + "' ";
		int i = 0;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			re = st.executeQuery(s1);// 执行SQL语句
			while (re.next()) {
				i++;
				if (t == i) {
					stg = re.getString(str);
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return stg;
	}
}
