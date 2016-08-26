import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * 
 * 
 * �������ˣ�����ͼ�ν���,�����̴߳���ÿ���û�������
 * ����һ�����ݿ���ٴ�����
 * ����:�����
 * 
 */
public class ServerQQ extends WindowAdapter implements ActionListener {
	private static final int WIDTH = 350;
	private static final int HEIGHT = 180;
	public static final int WIDTH1 = 250;
	public static final int HEIGHT1 = 100;

	private static JFrame jf, jm;
	private static JPanel jpl1, jpl2, jpl3;
	private static JLabel jimage;
	private static JButton jbt1, jbt2;
	private static JButton cancel, exit;
	static JLabel jla;
	
	/*
	 * ���÷���������
	 */
	public void showServer() {
		jf = new JFrame();
		jpl1 = new JPanel();
		jpl2 = new JPanel();
		jpl3 = new JPanel();
		jf.setTitle("QQ������"); // ���ñ���
		jf.setSize(WIDTH, HEIGHT); // ���ý����С
		jf.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);// ���ùرմ���ʱ�Ķ���
		jf.addWindowListener(this); // ���봰�ڼ���
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int w = screenSize.width;
		int h = screenSize.height;
		int x = (w - WIDTH) / 2;
		int y = (h - HEIGHT) / 2;
		jf.setLocation(x, y); // ���ý���λ�ã��˴�����Ϊ��Ļ����
		ImageIcon icon = new ImageIcon(this.getClass().getResource(
				"pic/server/top.jpg")); // ����ͼ��
		jimage = new JLabel(icon);
		jla = new JLabel();
		jbt1 = new JButton("����������");
		jbt1.addActionListener(this);// ��ť�������
		jbt2 = new JButton("�Ͽ�������");
		jbt2.addActionListener(this);// ��ť�������
		jpl3.add(jla);
		jpl1.add(jimage);
		jpl2.add(jbt1);
		jpl2.add(jbt2);

		jpl1.setBackground(Color.CYAN);
		jpl2.setBackground(Color.CYAN);
		jpl3.setBackground(Color.CYAN);
		jf.add(jpl1, BorderLayout.NORTH);
		jf.add(jpl2, BorderLayout.SOUTH);
		jf.add(jpl3, BorderLayout.CENTER);
		jf.setVisible(true);
	}

	/*
	 * �رմ��ڼ���
	 */
	public void windowClosing(WindowEvent e) {
		jm = new JFrame();
		jm.setSize(WIDTH1, HEIGHT1);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int w = screenSize.width;
		int h = screenSize.height;
		int x = (w - WIDTH1) / 2;
		int y = (h - HEIGHT1) / 2;
		jm.setLocation(x, y); // ���ý���λ�ã�����Ļ����
		Container cont = jm.getContentPane();
		cont.setBackground(Color.CYAN);
		JLabel msg = new JLabel("Ҫ�Ͽ���������");
		cont.add(msg);
		JPanel Panel = new JPanel();
		Panel.setBackground(Color.CYAN);
		Panel.setLayout(new FlowLayout());
		exit = new JButton("��");
		exit.addActionListener(this);
		Panel.add(exit);
		cancel = new JButton("��");
		cancel.addActionListener(this);
		Panel.add(cancel);
		cont.add(Panel, BorderLayout.SOUTH);
		jm.setVisible(true);
	}

	/*
	 * ��׽��ActionEvent��Ĵ����߼�
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbt1) {
			service();
			jla.setText("��������������");

		} else if (e.getSource() == jbt2) {
			System.exit(0);
		} else if (e.getSource() == exit) {
			System.exit(0);
		} else if (e.getSource() == cancel) {
			jm.dispose();
		}
	}

	/*
	 * ����������
	 */
	public void service() {
		new thread().start();
	}

	public static void main(String[] args) {
		new ServerQQ().showServer();
	}
}

/*
 * �����߳�����������
 */
class thread extends Thread implements Runnable {

	private Socket socket = null;
	private ServerSocket serverSocket;

	private boolean flag = true;
	private int Port = 5000;

	public void run() {
		try
		{
			serverSocket = new ServerSocket(Port);
			while (flag) 
			{
				socket = serverSocket.accept();
				new Thread(new manage(socket)).start();
			}
		} catch (IOException ex)
		{
			ServerQQ.jla.setText("����������ʧ�ܣ�");
		}
	}

}

/*
 * ����͑��˵�����¼�
 */
class manage implements Runnable {
	private String chatRecord;
	private String ip;
	private String qqnum, qqnum1;
	private boolean ok = true;
	private String initOK;
	private String userID,psword,name,sex,age,place,face,qqshow,signature,LOGINSTATE,group;
	private DataInputStream in = null; // ����������
	private DataOutputStream out = null; // ���������
	private database da;

	public manage(Socket socket)
	{
		try {
			
			in = new DataInputStream(socket.getInputStream());  // ����������
			
			out = new DataOutputStream(socket.getOutputStream()); // ���������
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		while (ok) {
			try {
				String str = in.readUTF();
				System.out.println(str);
				if (str.equals("stop"))
				{
					break;
				} 
				else if (str.equals("login"))//�û���¼
				{
					login();
				}
				else if (str.equals("showfriend"))//�鿴�����б�
				{
					showfriend();
				}
				else if (str.equals("addgroup"))//��Ӻ��ѷ���
				{
					addgroup();
				} 
				else if (str.equals("count")) //ͳ�ƺ�����Ŀ
				{
					count();
				}
				else if (str.equals("setState"))//�������������
				{
					setState();
				} 
				else if (str.equals("register")) //ע��
				{
					register();
				} 
				else if (str.equals("exit")) //�û��˳�
				{
					exit();
				} 
				else if (str.equals("getMyIP"))//��ȡ�ҵ�IP��д�����ݿ�
				{
					getMyIP();
				} 
				else if (str.equals("getIP"))//��ȡ���ѵ�IP
				{
					getIP();
				}
				else if (str.equals("readPORT")) //��ȡ���ѵ�����˿ں�
				{
					readPORT();
				} 
				else if (str.equals("readChatRecord")) //��ȡ�����¼
				{
					readChatRecord();
				} 
				else if (str.equals("addFriend")) //��Ӻ���
				{
					addFriend();
				} 
				else if (str.equals("sendRequest")) 
				{
					sendRequest();
				} 
				else if (str.equals("recordMyInfo"))//�޸ĸ�����Ϣ
				{
					recordMyInfo();
				} 
				else if (str.equals("addingroup")) //��Ӻ���������
				{
					 addingroup();
				}
				else if (str.equals("history"))//�����¼
				{
					 history();
				}
				 else if(str.equals("allfriend"))//������Ӻ���
				 {
					 allfriend();
				 }
			} catch (Exception e) {
				//System.out.println("Listening");
			}
			finally
			{
				/*try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			}
		}
	}


	/**
	 * �����Ǹ���ܵ�ʵ��
	 */


	/*
	 * �û���¼
	 */
	public void login() {
		// �������ݿⲢ���в���
		da = new database();
		Connection con = null;
		Statement st;
		ResultSet re;
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/miniQQ";
		String user = "root";
		String pwd = "";
		String sl = "select * from user_info";
		try {
			String num = in.readUTF();
			String pd = in.readUTF();
			String state = in.readUTF();
			System.out.println(num);
			System.out.println(pd);
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			re = st.executeQuery(sl);// ִ��SQL���
			// ��֤�û����
			while (re.next()) {
				if (re.getString("userID").equals(num)&& re.getString("password").equals(pd)) 
				{
					//n = num;
					out.writeUTF("true");
					initOK = "true";
					
					da.write(num, "LOGINSTATE", state, "user_info");
					
					userID = da.read("userID", num, "userID", "user_info");
					name = da.read("userID", num, "name", "user_info");
					sex = da.read("userID", num, "sex", "user_info");
					age = da.read("userID", num, "age", "user_info");
					place = da.read("userID", num, "place", "user_info");
					face = da.read("userID", num, "face", "user_info");
					qqshow = da.read("userID", num, "qqshow", "user_info");
					signature = da.read("userID", num, "signature", "user_info");
					LOGINSTATE = da.read("userID", num, "LOGINSTATE", "user_info");
					group=da.read("userID", num, "groups", "user_info");
					
					out.writeUTF(name);
					out.writeUTF(sex);
					out.writeUTF(age);
					out.writeUTF(place);
					out.writeUTF(face);
					out.writeUTF(qqshow);
					out.writeUTF(signature);
					out.writeUTF(LOGINSTATE);
					out.writeUTF(group);
					break;
				}
			}
			if (initOK != "true") {
				out.writeUTF("false");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}

	/*
	 * �û�ע��
	 */
	public void register() {
		int result = 0;
		try {
			userID = in.readUTF();
			psword = in.readUTF();
			name = in.readUTF();
			sex = in.readUTF();
			age = in.readUTF();
			place = in.readUTF();
			face = in.readUTF();
			qqshow = in.readUTF();
			signature = in.readUTF();
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		Connection con = null;
		PreparedStatement ps;
		Statement sta;
		ResultSet re;
		String url = "jdbc:mysql://localhost/miniQQ";
		String user = "root";
		String pwd = "";
		String ad = "insert into user_info values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		String sl = "select * from user_info";
		String myport = null;
		ArrayList<String> ports = new ArrayList<String>();
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, user, pwd);
			sta = con.createStatement();
			re = sta.executeQuery(sl);// ִ��SQL���
			Random ra = new Random();
			int q = ra.nextInt(60536);
			myport = String.valueOf(q + 5000);
			
			while (re.next()) 
			{
				
				if(userID.equals(re.getString("userID"))) result = 1;
				ports.add(re.getString("myport"));
					
			}
			
			while(ports.contains(myport))
			{
				q = ra.nextInt(60536);
				myport = String.valueOf(q + 5000);
			}
			
			if(result == 0)   //�˺�û������
			{
				
				
				
				
				ps = con.prepareStatement(ad);
				ps.setString(1, userID);
				ps.setString(2, psword);
				ps.setString(3, name);
				ps.setString(4, sex);
				ps.setString(5, age);
				ps.setString(6, place);
				ps.setString(7, face);
				ps.setString(8, qqshow);
				ps.setString(9, signature);
				ps.setString(10, " ");
				ps.setString(11, "0");
				ps.setString(12, "0");
				ps.setString(13, null);
				ps.setString(14, myport);
				ps.setString(15, InetAddress.getLocalHost().getHostAddress());//Ĭ������IP
				ps.executeUpdate();// ִ��SQL���
			}
			out.writeInt(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * ��ʾȫ������
	 */
	public void showfriend()
	{
		da = new database();
		Connection con = null;
		Statement st;
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/miniQQ";
		String user = "root";
		String pwd = "";
		String sl = "select * from user_info";
		try {
			String userID = in.readUTF();
			int n =in.read();
			System.out.println(userID+"showfriend");
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			st.executeQuery(sl);
			
			String friendID = da.readOne(userID, "FRIEND", "friend", n);
			String groupin = da.readOne(userID, "groupin", "friend",n);


			String name = da.read("userID", friendID, "name", "user_info");
			String sex = da.read("userID", friendID, "sex", "user_info");
			String age = da.read("userID", friendID, "age", "user_info");
			String place = da.read("userID", friendID, "place", "user_info");
			String face = da.read("userID", friendID, "face", "user_info");
			String qqshow = da.read("userID", friendID, "qqshow", "user_info");
			String signature = da.read("userID", friendID, "signature", "user_info");
			String LOGINSTATE = da.read("userID", friendID, "LOGINSTATE", "user_info");
			
			out.writeUTF(friendID);
			out.writeUTF(name);
			out.writeUTF(sex);
			out.writeUTF(age);
			out.writeUTF(place);
			out.writeUTF(face);
			out.writeUTF(qqshow);
			out.writeUTF(signature);
			out.writeUTF(LOGINSTATE);
			out.writeUTF(groupin);
			
			}catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	}
	
	
	
	

	/*
	 * ������		OK
	 */
	public void addgroup() {
		String newgrp = null;
		da = new database();
		try {
			qqnum = in.readUTF();
			 newgrp = in.readUTF();
			System.out.println(newgrp);
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		String group = da.read("userID", qqnum, "groups", "user_info");
			da.write(qqnum, "groups", group + " "+newgrp, "user_info");
	
	}

	/*
	 * ���������������   ok
	 */
	
	public void addingroup()
	{
		Connection con = null;
		Statement st;
		ResultSet re;
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/miniQQ";
		String user = "root";
		String pwd = "";
		
		String frdID = null,userID = null,adgrp = null,grpin=null;
		ArrayList<String> grp = null;
		
		try
		{
			userID = in.readUTF();
			frdID = in.readUTF();
			adgrp = in.readUTF();
			
			System.out.println("addingroup");
			
			String s1 = "SELECT * FROM friend WHERE userID ='" + userID 
			+ "' AND friend ='"+ frdID +"'";

			
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			
			re = st.executeQuery(s1);// ִ��SQL���
			while (re.next()) {
				grpin = re.getString("groupin");
				StringTokenizer stk = new StringTokenizer(grpin);
				grp = new ArrayList<String>();
				while(stk.hasMoreTokens())									//�ж��û��Ƿ����ڷ���
				{
					grp.add(stk.nextToken(" "));
				}
			}
			
			if(!grp.contains(adgrp))
			{
				adgrp = grpin +" "+adgrp+" ";
				String s2 = "UPDATE friend SET groupin = '" + adgrp
							+ "' WHERE userID = '" + userID 
							+ "' AND friend = '"+ frdID +"' ";
			
				st.executeUpdate(s2);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	
	}


	/*
	 * ��ȡ���ݿ���friend�ض�������� ok
	 */
	public void count()
	{
		da = new database();
		try {
			qqnum = in.readUTF();
			int t = da.count(qqnum, "friend");
			System.out.println(t);
			out.write(t);
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	

	/*
	 * ���õ�¼״̬
	 */
	public void setState() {
		da = new database();
		try {
			qqnum = in.readUTF();
			int state = in.readInt();
			if (state==0) //����
			{
				da.write(qqnum, "LOGINSTATE", "0", "user_info");
				
			} else //����
			{
				da.write(qqnum, "LOGINSTATE", "1", "user_info");
				
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/*
	 * ���ҵ���Ϣд�����ݿ�  ok
	 */
	public void recordMyInfo() {
		da = new database();
		try {
			qqnum = in.readUTF();
			String name = in.readUTF();
			String sex = in.readUTF();
			String age = in.readUTF();
			String place = in.readUTF();
			String face = in.readUTF();
			String qqshow = in.readUTF();
			String signature = in.readUTF();
			
		
			da.write(qqnum, "name", name, "user_info");
			da.write(qqnum, "sex", sex, "user_info");
			da.write(qqnum, "age", age, "user_info");
			da.write(qqnum, "place", place, "user_info");
			da.write(qqnum, "face", face, "user_info");
			da.write(qqnum, "qqshow", qqshow, "user_info");
			da.write(qqnum, "signature", signature, "user_info");
			
			
			
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/*
	 *  �û��˳�   ok
	 */
	public void exit() 
	{
		da = new database();
		try {
			qqnum = in.readUTF();
			da.write(qqnum, "STATE", "0", "user_info");
			da.write(qqnum, "LOGINSTATE", "0", "user_info");
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		ok = false;
	}

	

	/*
	 * ���û�IP��д�����ݿ� OK
	 */
	public void getMyIP() {
		da = new database();
		try {
			qqnum = in.readUTF();
			ip = in.readUTF();
			da.write(qqnum, "myip", ip, "user_info");
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/*
	 * ��ȡ���ѵ�IP��
	 */
	public void getIP() {
		da = new database();
		try {
			qqnum = in.readUTF();
			String p = da.read("userID", qqnum, "myip", "user_info");
			System.out.println(p);
			out.writeUTF(p);
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/*
	 * ��Ӻ���  OK
	 */
	public void addFriend() {
		try {
			qqnum = in.readUTF();
			qqnum1 = in.readUTF();
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		Connection con = null;
		PreparedStatement ps;
		String url = "jdbc:mysql://localhost/miniQQ";
		String user = "root";
		String pwd = "";
		String ad = "insert into friend values(?,?,?,?,?)";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, user, pwd);
			con.createStatement();
			// д������
			ps = con.prepareStatement(ad);
			ps.setString(1, qqnum);
			ps.setString(2, qqnum1);
			ps.setString(3, " ");
			ps.setString(4, " ");
			ps.setString(5, "0");
			ps.executeUpdate();// ִ��SQL���
			// д������
			ps = con.prepareStatement(ad);
			ps.setString(1, qqnum1);
			ps.setString(2, qqnum);
			ps.setString(3, " ");
			ps.setString(4, " ");
			ps.setString(5, " ");
			ps.executeUpdate();// ִ��SQL���
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * ������Ӻ�������
	 */
	public void sendRequest() {
		da = new database();
		try {
			String qqnum1 = in.readUTF();
			String qqnum2 = in.readUTF();
			da.write(qqnum1, "NEWFRIEND", qqnum2, "user_info");
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	
	/*
	 * �����ݿ��ж�ȡ�˿ں�
	 */
	public void readPORT() 
	{
		database data = new database();
		String gettport;
		
		try {
			qqnum = in.readUTF();
			
			gettport = data.read("userID", qqnum, "myport", "user_info");
			out.writeUTF(gettport);


		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	

	/*
	 * ��ȡ�����¼
	 */
	public void readChatRecord() {
		try {
			qqnum = in.readUTF();
			qqnum1 = in.readUTF();

			Connection con = null;
			Statement st;
			ResultSet re;
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost/miniQQ";
			String user = "root";
			String pwd = "";
			String s1 = "SELECT * FROM friend WHERE userID='" + qqnum
					+ "' AND friend='" + qqnum1 + "'";
			try {

				Class.forName(driver);
				con = DriverManager.getConnection(url, user, pwd);
				st = con.createStatement();
				re = st.executeQuery(s1);// ִ��SQL���
				while (re.next()) {
					chatRecord = re.getString("history");
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
			if (chatRecord != null)
				out.writeUTF(chatRecord);
			else
				out.writeUTF("");
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
	/*
	 * ����������¼
	 */
	private void history() {
		Connection con = null;
		Statement st;
		ResultSet re;
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/miniQQ";
		String user = "root";
		String pwd = "";
		
		String frdID = null,userID = null,newhis = null,history=null;
		try
		{
			userID = in.readUTF();
			frdID = in.readUTF();
			newhis = in.readUTF();
			
			System.out.println("history");
			
			String s1 = "SELECT * FROM friend WHERE userID ='" + userID 
			+ "' AND friend ='"+ frdID +"'";

			
			
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			
			re = st.executeQuery(s1);// ִ��SQL���
			while (re.next()) {
				history = re.getString("history");
				
			}
			
	
				history = history+"\n"+newhis;
				String s2 = "UPDATE friend SET history = '" + history
							+ "' WHERE userID = '" + userID 
							+ "' AND friend = '"+ frdID +"' ";
			
				st.executeUpdate(s2);
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * ���Ҳ���Ӻ���
	 */
	private void allfriend() {

		Connection con = null;
		Statement st;
		ResultSet rs = null;
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/miniQQ";
		String user = "root";
		String pwd = "";
		String q = "select * from user_info";
		int frdok = 0;
		System.out.println("allfriend");
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
			st = con.createStatement();
			
			rs = st.executeQuery(q);// ִ��SQL��� 
			while (rs.next()) {
				frdok++;
			}
			out.writeInt(frdok);
			
			rs = st.executeQuery(q);// ִ��SQL��� 
			while (rs.next()) {
				out.writeUTF(rs.getString("userID"));
				out.writeUTF(rs.getString("name"));
				out.writeUTF(rs.getString("face"));
				out.writeUTF(rs.getString("place"));
				out.writeUTF(rs.getString("LOGINSTATE"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
					e.printStackTrace();
			}
		}
		
	}
}




/**
 * 
 *  �������ݿ�
 *  ����:�����
 */
 class database {

	 /**
	  * 
	  *  �������ݿ�
	  *  ����:�����
	  */
	String driver = "com.mysql.jdbc.Driver";
	String url = "jdbc:mysql://localhost/miniQQ";
	String user = "root";
	String pwd = "";

	String s = "";
	String sss;
	String stg;
	int k = 0;

	/*
	 * �����ݿ��ж�������
	 */
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

	/*
	 *  д�����ݵ����ݿ�
	 */
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

	/*
	 *  ��¼��������������
	 */

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

	/*
	 *  ������t�����ϵ���,�ҳ���C1�У���t��str��Ŀ�е���Ŀ
	 */
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

