import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 *  �������ݿ�
 *  ����:�����
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

	// �����ݿ��ж�������
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
			re = st.executeQuery(s1);// ִ��SQL���
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

	// д�����ݵ����ݿ�
	public void write(String qqnum, String str1, String str2, String c2) {
		Connection con = null;
		Statement st;

		String s2 = "UPDATE " + c2 + " SET " + str1 + "='" + str2
				+ "' WHERE userID='" + qqnum + "' ";
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			st.executeUpdate(s2);// ִ��SQL���
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

	// ��¼��������������

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
			re = st.executeQuery(s1);// ִ��SQL���
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

	// ������t�����ϵ���,�ҳ���C1�У���t��str��Ŀ�е���Ŀ
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
			re = st.executeQuery(s1);// ִ��SQL���
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
