import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

	/**
	 * 聊天系统的用户客服端主界面
	 * 作者：戴睿轩
	 */
public class MainFrame extends JFrame {

	/**
	 * 主界面窗口元素
	 * 作者：戴睿轩
	 */
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_WIDTH = 240;
	public static final int DEFAUL_HEIGHT = 650;
	public static int Frame_X;
	public static int Frame_Y;
	public static final double Frame_RadioX = 0.66666;
	public static final double Frame_RadioY = 0.11111;

	private DataOutputStream out;
	private DataInputStream in;
	private String ip;
	private DatagramPacket rPack = null;
	private DatagramSocket sSocket = null, rSocket = null;
	private int Port; // 存储发送端口和接收端口
	private byte inBuf[];
	public static final int BUFSIZE = 1024;
	private String recv = null;

	private Friends frdpane = null;
	JLabel pic;
	private JButton change = new JButton("修改个人信息");

	private ArrayList<User> friendlist = new ArrayList<User>();  //用于储存全部好友列表
	private ArrayList<User> frdtalk = new ArrayList<User>();	//用于存储正在聊天中的好友
	private ArrayList<String> groupname = new ArrayList<String>();//用于存储好友分组
	private User user;
	private int groupnum = 0;

	public MainFrame(User user, final DataInputStream in,
			final DataOutputStream out) {
		this.user = user;
		this.in = in;
		this.out = out;

		StringTokenizer stk = new StringTokenizer(user.getGroup()); 
		groupnum = stk.countTokens(); // 纪录用户有几个分组
		while (stk.hasMoreTokens()) {
			groupname.add(stk.nextToken(" "));
		}

		getMyIP(this.user.getUserID());
		readport();
		System.out.println(Port);
		new listenchat().start();

		inBuf = new byte[BUFSIZE];
		try {
			sSocket = new DatagramSocket();
			rPack = new DatagramPacket(inBuf, BUFSIZE); // 创建接收数据包
			rSocket = new DatagramSocket(Port); // 指定接收数据的端口
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		Frame_X = (int) (screenSize.width * Frame_RadioX);
		Frame_Y = (int) (screenSize.height * Frame_RadioY);

		setBounds(Frame_X, Frame_Y, DEFAULT_WIDTH, DEFAUL_HEIGHT);
		setLayout(null);
		Image ime = kit.getImage(this.getClass().getResource(
				user.getPic().substring(0, 10) + "-2.gif")); // 设置窗口图标
		setIconImage(ime);

		/*
		 * 判断登陆状态并设置图像
		 */
		if (user.getLOGINSTATE().equals("1")) // 判断登陆状态
		{
			setTitle(user.getUserName() + "(在线)");
			pic = new JLabel(new ImageIcon(this.getClass().getResource(
					user.getPic())));
		} 
		else {
			setTitle(user.getUserName() + "(隐身)");
			pic = new JLabel(new ImageIcon(this.getClass().getResource(
					user.getPic().substring(0, 10) + "-0.gif")));
		}

		pic.setBounds(0, 0, 60, 60);
		change.setBackground(Color.lightGray);
		change.setBounds(80,0,100,25);
		change.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent arg0) {
				new changeinfo();
				
			}
		});

		add(pic);
		add(change);

		frdpane = new Friends();

		add(new state());
		add(new info());
		add(new Category());
		add(frdpane);
		add(new MyInfo());

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				int j = JOptionPane.showConfirmDialog(null, "确认退出吗？", "退出",
						JOptionPane.YES_NO_OPTION);
				if (j == JOptionPane.YES_OPTION) 
				{
					try {
						
						out.writeUTF("exit");
						out.writeUTF(MainFrame.this.user.getUserID());
						System.exit(0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}

		});

		setLocationByPlatform(true);
		setResizable(false);
		setVisible(true);

	}

	/*
	 *  获取我的IP号并写入数据库
	 */
	public void getMyIP(String qqnum) {
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		try {
			out.writeUTF("getMyIP");
			out.writeUTF(qqnum);
			out.writeUTF(ip);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/*
	 *  读取我的端口号
	 */
	public void readport()
	{
		try {
			out.writeUTF("readPORT");
			out.writeUTF(user.getUserID());

			Port = Integer.parseInt(in.readUTF());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/*	
	 * 		查找新好友
	 */
	class Search  implements ActionListener
	{
		/**
		 * 查找好友窗口元素以及各项功能
		 * 作者：戴睿轩
		 */
		private static final long serialVersionUID = 1L;
		
		public void ShowSearchUI(){
			JFrame window = new JFrame("查找/添加好友");
			window.getContentPane().setBackground(Color.white);
			window.setSize(325,530);
			window.setResizable(false);
			window.setDefaultCloseOperation(2);
			
			JPanel north = new JPanel();
			north.setPreferredSize(new Dimension(320,25));
			north.setBackground(Color.gray);
			JLabel la = new JLabel();
			la.setVisible(true);
			north.add(la,BorderLayout.WEST);
			
			JLabel la2 = new JLabel();
			la2.setText("以下是系统查找的用户,选中点击击添加好友！");
			north.add(la2);
			
			north.setVisible(true);
			
			
			JPanel frdpanel = new JPanel(new GridLayout(10, 1));
			
			/*
			 * 查询服务器好友
			 */
			try {
				out.writeUTF("allfriend");
				
				int frdok = in.readInt();
				
				
				for(int k = 0;k<frdok;k++)
				{
					
					String ID = in.readUTF();
					String name = in.readUTF();
					String face = in.readUTF();
					String place = in.readUTF();
					String login = in.readUTF();
					
					jfriend temp = new jfriend(ID,name,face,place,login);
					
					temp.addActionListener(this);
					frdpanel.add(temp);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
			window.setLocationRelativeTo(null);
			
			
			
			frdpanel.setBackground(Color.cyan);
			window.add(north,BorderLayout.NORTH);
			window.add(frdpanel,BorderLayout.CENTER);
			
			window.setVisible(true);
			
			
		}
		
		class jfriend extends JButton
		{
			/**
			 * 显示好友的按钮，主要是为了保存ID；
			 * 作者:戴睿轩
			 */
			private static final long serialVersionUID = 1L;
			public String ID = null;
			public jfriend(String ID,String name,String face,String place,String login)
			{
				this.ID=ID;
				if (login.equals("1")) // 判断好友是否隐身
				{
					setIcon(new ImageIcon(this.getClass().getResource(
							face)));
				} else
					setIcon(new ImageIcon(this.getClass().getResource(
							face.substring(0, 10) + "-0.gif")));
				
				setText("  "+"昵称："+name+"   地区："+place);
			}
		}

		/*
		 * 监听按钮
		 */
		public void actionPerformed(ActionEvent e) {
			int i = JOptionPane.showConfirmDialog(null, "加为好友?",
					"加为好友？", JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION)
			{
				
				try {
					out.writeUTF("addFriend");
					out.writeUTF(user.getUserID());
					out.writeUTF(((jfriend)e.getSource()).ID);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		}
		

	}
	
	
	
	/*
	 * 后台监听聊天和好友请求
	 */
	class listenchat extends thread implements Runnable {

		/**
		 * 后台通过新线程监听聊天和好友请求
		 * 作者：戴睿轩
		 */
		String recID, recAct, recMsg;

		public void run() {
			while (true) {
				try {
					rSocket.receive(rPack);
					recv = new String(rPack.getData(), 0, rPack.getLength());

					int num_Idex = recv.indexOf("*");
					
					/*
					 * 分析接收的数据包,recAct为0为消息,recAct为1为好友请求
					 */
					recID = recv.substring(0, num_Idex);
					recAct = recv.substring(num_Idex + 1, num_Idex + 2);
					recMsg = recv.substring(num_Idex + 2);

					if (recAct.equals("0")) {//接收到消息
						revmess(recID, recMsg);
					}
					if (recAct.equals("1")) {//接收到好友请求
						frdrequest();
					}
				} catch (Exception e) {

				}
			}
		}
		/*
		 * 接收到消息的反应
		 */
		public void revmess(String frdID,String str)
		{
			boolean myfrd = false;
			User frd = null;
			for (User talk : friendlist)
			{
				if(talk.getUserID().equals(frdID)){myfrd = true;frd = talk;break;}
			}
			
			if (myfrd) 
			{	
				boolean istalk = false;
				for(User u :frdtalk)
				{
					if(u.getUserID().equals(frdID)){istalk=true;break;}
				}
				if(!istalk)frdtalk.add(frd);  //如果不在聊天列表中，加入聊天列表
				if(frd.getTalk()!=null)
				{
					if(frd.getTalk().isVisible())	
						frd.getTalk().recmsg(str); 
					else 
					{int i = JOptionPane.showConfirmDialog(null, frd.getUserName()
							+ "请求与你聊天", "聊天请求？",
							JOptionPane.YES_NO_OPTION);
						if (i == JOptionPane.YES_OPTION)
						{
							frd.getTalk().setVisible(true);frd.getTalk().recmsg(str); 
						}
						else	frd.setRecv(frd.getRecv()+" "+str);
					}
				}
					
				else
				{
					int i = JOptionPane.showConfirmDialog(null, frd.getUserName()
						+ "请求与你聊天", "聊天请求？",
						JOptionPane.YES_NO_OPTION);
					if (i == JOptionPane.YES_OPTION)
					{
						int sPort = 0;
						InetAddress sIP = null;
						try {			//读取好友的Ip和端口号
							out.writeUTF("readPORT");
							out.writeUTF(frd.getUserID());
							
							sPort = Integer.parseInt(in.readUTF());
							System.out.println(sPort);
							
							out.writeUTF("getIP");
							out.writeUTF(frd.getUserID());
							sIP =InetAddress.getByName(in.readUTF()); 
							System.out.println(sIP);
							} 
						catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						frd.setTalk(new QQchatframe(user, frd,sPort,sIP,sSocket,out,in));
						frd.getTalk().recmsg(str);
					}
					else  frd.setRecv(frd.getRecv()+" "+str);
				}
				
			}
			if (!myfrd) {
				int i = JOptionPane.showConfirmDialog(null, frdID
						+ "请求与你聊天", "聊天请求？",
						JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.YES_OPTION)
				{
					int sPort = 0;
					InetAddress sIP = null;
					try {
						out.writeUTF("readPORT");
						out.writeUTF(frdID);
						
						sPort = Integer.parseInt(in.readUTF());
						System.out.println(sPort);
						
						out.writeUTF("getIP");
						out.writeUTF(frdID);
						sIP =InetAddress.getByName(in.readUTF()); 
						System.out.println(sIP);
						} 
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					new QQchatframe(user,frdID,sPort,sIP,sSocket);
				}
			}
		}
		
		/*
		 * 接收到好友请求的反应
		 */
		public void frdrequest(){
			int i = JOptionPane.showConfirmDialog(null, recID + "请求加你为好友",
					"加为好友？", JOptionPane.YES_NO_OPTION);
			if (i == JOptionPane.YES_OPTION) {

				try {
					out.writeUTF("addFriend");
					out.writeUTF(user.getUserID());
					out.writeUTF(recID);
				} catch (IOException e) {
						// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
		}
	}
	
	
	class changeinfo extends JFrame {
		/**
		 * 修改个人信息窗口元素以及各项功能
		 * 作者：戴睿轩
		 */
		private static final long serialVersionUID = 1L;

		public static final int PANEL_WIDTH = 460;
		public static final int PANEL_HEIGHT = 650;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 430;

		private String userName;
		private String sexstr;
		private String agenum;
		private String placestr;
		private String qqshow;
		private String signature;

		String str = "pic/face/1.gif";
		String qqstr = "男.gif";
		JLabel show = new JLabel();
		JLabel qqshows = new JLabel();

		public changeinfo() {
			setSize(PANEL_WIDTH, PANEL_HEIGHT);
			setBackground(Color.red);
			setResizable(false);
			setDefaultCloseOperation(2);
			setLocationRelativeTo(null);
			setLayout(new FlowLayout(0, 20, 20));



			JLabel jNickname = new JLabel("昵         称:");
			final JTextField nickname = new JTextField(12);
			nickname.setText(user.getUserName());
			JLabel content4 = new JLabel("聊天昵称                                     ");
			content4.setForeground(Color.red);

			add(jNickname);
			add(nickname);
			add(content4);

			final JLabel age = new JLabel("年         龄:");
			final JTextField ageIn = new JTextField(3);
			ageIn.setText(user.getAge());
			add(age);
			add(ageIn);

			String[] da = { "男", "女" };
			JLabel sex = new JLabel("性         别:");
			final JComboBox cb = new JComboBox(da);// 下拉框
			cb.setSelectedItem(new MenuItem(user.getSexstr()));
			add(sex);
			add(cb);
			add(new JLabel("                                               "));// 设置换行

			JLabel place = new JLabel("地          区:");
			final JTextField placeIn = new JTextField(6);
			placeIn.setText(user.getPlacestr());
			add(place);
			add(placeIn);
			add(new JLabel("                                                "
					+ "                       "));// 设置换行

			/*
			 * 以下为设置图像
			 */
			JLabel jPhoto = new JLabel("头         像:");
			String[] pics = new String[9];
			for (int i = 1; i <= 9; i++) {
				pics[i - 1] = "" + i + ".gif";
			}
			final JComboBox choosePhoto = new JComboBox(pics);
			show.setIcon(new ImageIcon(this.getClass().getResource(user.getPic())));
			choosePhoto.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					str = "pic/face/" + choosePhoto.getSelectedItem().toString();
					System.out.println(str);
					show.setIcon(new ImageIcon(this.getClass().getResource(str)));
					show.repaint();
				}
			});
			
			
			add(jPhoto);
			add(choosePhoto);
			add(new JLabel("         预览:"));
			add(show);
			add(new JLabel("                                      "));// 设置换行
			
			
			/*
			 * 以下为qqshow
			 */
			JLabel jshow = new JLabel("QQ秀:");
			String[] shows = {"男","男1","男2","男3","男4","男5","女","女1","女2","女3","女4","女5"};
			final JComboBox showPhoto = new JComboBox(shows);
			qqshows.setIcon(new ImageIcon(this.getClass().getResource("pic/qqshow/"+user.getShow())));
			showPhoto.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					qqstr = showPhoto.getSelectedItem().toString();
					System.out.println( "pic/qqshow/" + qqstr+".gif");
					qqshows.setIcon(new ImageIcon(this.getClass().
							getResource( "pic/qqshow/" + qqstr+".gif")));
					qqshows.repaint();
				}
			});


			add(jshow);
			add(showPhoto);
			add(new JLabel("   预览:"));
			add(qqshows);
			add(new JLabel("                   "));// 设置换行

			
			JLabel jSign = new JLabel("个性签名:");
			final JTextArea sign = new JTextArea(5, 20);
			sign.setLineWrap(true);
			sign.setBackground(Color.LIGHT_GRAY);
			sign.setText(user.getSignature());
			JLabel content5 = new JLabel("个性签名，请在50个中文汉字之内,多余部" +
					"分系统会自动删除...                              ");
			content5.setForeground(Color.red);

			
			
			
			add(jSign);
			add(sign); 
			add(content5);
			add(new JLabel("                                              "));// 设置换行
			
			
			
			

			final JButton regsit = new JButton("确认修改");
			add(regsit);
			
			
			setVisible(true);
			
			/*
			 * 点击确认修改后的反应,采用内部类
			 */
			regsit.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					dispose();
					agenum = (ageIn.getText() == null || "".equals(ageIn.getText()) ? " "
							: ageIn.getText());
					sexstr = cb.getSelectedItem().toString();
					qqshow = showPhoto.getSelectedItem().toString() + ".gif";
					placestr = (placeIn.getText() == null
							|| "".equals(placeIn.getText()) ? " " : placeIn
							.getText());
					userName = (nickname.getText() == null
							|| "".equals(nickname.getText()) ? " " : nickname
							.getText());
					signature = (sign.getText() == null
							|| "".equals(sign.getText()) ? " " : sign.getText());

					
					
						try {
							out.writeUTF("recordMyInfo");
							
							out.writeUTF(user.getUserID());
							out.writeUTF(userName);
							out.writeUTF(sexstr);
							out.writeUTF(agenum);
							out.writeUTF(placestr);
							out.writeUTF(str);
							out.writeUTF(qqshow);
							out.writeUTF(signature);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					
					
				}

			});

		}
		

	}

	

	class state extends JComboBox implements ActionListener {
		/**
		 * 修改登录状态窗口元素以及各项功能
		 * 作者：戴睿轩
		 */

		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 52;
		public static final int PANEL_HEIGHT = 25;
		public static final int PANEL_X = 180;
		public static final int PANEL_Y = 0;

		public state() {

			addItem("在线");
			addItem("隐身");
			setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
			if (user.getLOGINSTATE().equals("0")) // 如果登陆的时候隐身，使隐身被选中。
			{
				setSelectedIndex(1);
			}

			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (this.getSelectedItem().toString().equals("隐身")) {
				//设置图像
				pic.setIcon(new ImageIcon(this.getClass().getResource(
						user.getPic().substring(0, 10) + "-0.gif")));
				//设置窗口名称
				MainFrame.this.setTitle(user.getUserName() + "(隐身)");

				try {
					out.writeUTF("setState");
					out.writeUTF(user.getUserID());
					out.writeInt(0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else {
				pic.setIcon(new ImageIcon(this.getClass().getResource(
						user.getPic())));
				MainFrame.this.setTitle(user.getUserName() + "(在线)");
				try {
					out.writeUTF("setState");
					out.writeUTF(user.getUserID());
					out.writeInt(1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	}

	class info extends JPanel {
		/**
		 * 个人信息窗口元素以及各项功能
		 * 作者：戴睿轩
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 240;
		public static final int PANEL_HEIGHT = 20;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 454;

		public info() {
			setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);

			add(new JLabel("我的信息"));
		}
	}

	class Category extends JPanel implements ActionListener {
		/**
		 * 好友分组窗口元素以及各项功能
		 * 作者：戴睿轩
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 240;
		public static final int PANEL_HEIGHT = 70;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 555;

		private JButton all = new JButton("全部好友");
		private JButton online = new JButton("在线好友");
		private JMenu cate = null;
		private JButton talking = new JButton("聊天中");
		private JButton newfrd = new JButton("查找新好友");

		public Category() {
			setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);

			setBackground(Color.cyan);

			all.addActionListener(this);
			online.addActionListener(this);
			talking.addActionListener(this);
			newfrd.addActionListener(this);

			add(talking);
			add(all);
			add(online);
			add(newfrd);

			cate = new JMenu("好友分组↓");
			/*
			 * 扫描拥有几个分组
			 */
			for (String s : groupname) {
				JMenuItem temp = new JMenuItem(s);
				temp.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						MainFrame.this.frdpane.showfriend(e.getActionCommand());

					}
				});
				temp.setEnabled(true);
				cate.add(temp);
				cate.setEnabled(true);
			}
			cate.addActionListener(this);
			
			JMenuItem newsort = new JMenuItem("添加新分组:");
			
			/*
			 * 添加新分组功能监听
			 */
			newsort.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String str = JOptionPane.showInputDialog("新分组：");
					if(str!=null&&!str.isEmpty())
					newcate(str);
					
				}
			});
			
			cate.add(newsort);
			JPanel catepn = new JPanel();
			JMenuBar catebr = new JMenuBar();
			catebr.add(cate);
			catepn.add(catebr);
			catepn.setBounds(140, 30, 100, 25);

			MainFrame.this.add(catepn);

		}

		/*
		 * 添加新分组功能监听
		 */
		public void newcate(String str) 
		{
			try {
				out.writeUTF("addgroup");
				out.writeUTF(user.getUserID());
				out.writeUTF(str);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		 * 各个按钮的功能,及相应的反应
		 */
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == all) {
				frdpane.showfriend();
				System.out.println("showall");
			}
			if (e.getSource() == online) {
				frdpane.showonlinefrd();
				System.out.println("online");
			}
			if (e.getSource() == talking) {
				frdpane.showtalking();
			}
			if(e.getSource()==newfrd){
				new MainFrame.Search().ShowSearchUI();
			}
		}

	}

	class MyInfo extends JPanel {
		/**
		 * 显示个人信息及相关功能
		 * 作者:戴睿轩
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 240;
		public static final int PANEL_HEIGHT = 75;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 480;

		JLabel nickname = null;
		JLabel age = null;
		JLabel place = null;
		JLabel fm = null;

		public MyInfo() {
			setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);

			setBackground(Color.lightGray);
			// setLayout(null);

			// info.setLocation(85,5);
			JPanel j1 = new JPanel();
			JPanel j2 = new JPanel();
			JPanel j3 = new JPanel();
			JPanel j4 = new JPanel();

			j1.setBackground(Color.lightGray);
			j2.setBackground(Color.lightGray);
			j3.setBackground(Color.lightGray);
			j4.setBackground(Color.lightGray);

			j1.add(new JLabel("昵称"));
			j1.add(new JLabel(user.getUserName()));
			j2.add(new JLabel("性别"));
			j2.add(new JLabel(user.getSexstr()));
			j3.add(new JLabel("年龄"));
			j3.add(new JLabel(user.getAge()));
			j4.add(new JLabel("地区"));
			j4.add(new JLabel(user.getPlacestr()));

			add(j1);
			add(j2);
			add(j3);
			add(j4);

		}
	}

	class Friends extends JPanel {
		/**
		 * 好友列表及相关功能
		 * 包括内部类好友显示frditm
		 * 作者:戴睿轩
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 240;
		public static final int PANEL_HEIGHT = 400;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 60;

		/*
		 * 构造器,在初次构造是,显示全部好友
		 */
		public Friends() {
			setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
			setBackground(Color.DARK_GRAY);
			setLayout(new GridLayout(10, 1));

			
			showfriend();//显示全部好友

		}
		/*
		 * 显示聊天中的好友
		 */
		public void showtalking() {
			this.removeAll();
			for (User s : frdtalk) {
				this.add(new frditm(s));
			}

			this.repaint();
			this.doLayout();

		}

		/*
		 * 显示在线的好友
		 */
		public void showonlinefrd() {
			this.removeAll();
			for (User s : friendlist) {
				if (s.getLOGINSTATE().equals("1")) {
					System.out.println(s);
					this.add(new frditm(s));
				}
			}
			this.repaint();
			this.doLayout();
		}

		/*
		 * 显示分组好友
		 */
		public void showfriend(String str) {
			this.removeAll();

			/*
			 * 扫描好友是否在此分组
			 */
			for (User s : friendlist) {
				StringTokenizer stk = new StringTokenizer(s.getGroupin());
				ArrayList<String> grp = new ArrayList<String>();
				while (stk.hasMoreTokens()) {
					grp.add(stk.nextToken(" "));
				}

				if (grp.contains(str)) {
					this.add(new frditm(s));
				}
			}

			this.repaint();
			this.doLayout();
		}

		/*
		 * 显示全部好友
		 */
		public void showfriend() {

			this.removeAll(); // 清空panel中显示的好友
			friendlist.clear(); // 清空好友数组

			int nn = 0;
			/*
			 * 读取好友数
			 */
			try {
				out.writeUTF("count");
				out.writeUTF(user.getUserID());

				nn = in.read();
				System.out.println("haoyoushu"+nn);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * 好友一个个显示
			 */
			for (int i = 1; i <= nn; i++) {
				frditm temp = new frditm(i);
				add(temp);
				temp.setVisible(true);
			}

			this.repaint();
			this.doLayout();

		}

		class frditm extends JButton implements ActionListener, MouseListener,
				ItemListener {
			/**
			 * 单个好友的显示,采用jbutton实现,
			 * 包括查询服务器显示全部好友,以及从friendlist中显示特定的好友
			 * 作者:戴睿轩
			 */
			private static final long serialVersionUID = 1L;
			private JPopupMenu popupMenu = null; // 右键菜单对象
			private JMenuItem delete = null, info = null;
			private JMenu sort = null;

			private User frd = null;//没个button包括一个用户对象

			/*
			 * 从friendlist中显示特定的好友
			 */
			public frditm(User frds) {
				super(frds.getUserName());

				frd = frds;

				if (frds.getLOGINSTATE().equals("1")) // 判断好友是否隐身
				{
					setIcon(new ImageIcon(this.getClass().getResource(
							frds.getPic())));
				} else
					setIcon(new ImageIcon(this.getClass().getResource(
							frds.getPic().substring(0, 10) + "-0.gif")));

				popupMenu = new JPopupMenu();
				delete = new JMenuItem("删除好友");
				info = new JMenuItem("好友信息");
				sort = new JMenu("添加分组→");

				for (int j = 0; j < groupnum; j++) // 好友分组名
				{
					JMenuItem temp = new JMenuItem(groupname.get(j));
					temp.addActionListener(new ActionListener() {

						/*
						 * 此处实现将好友添加到特定的分组,通过服务器
						 */
						public void actionPerformed(ActionEvent e) {
							System.out.println(e.getActionCommand());
							try {
								out.writeUTF("addingroup");
								out.writeUTF(user.getUserID());
								out.writeUTF(frd.getUserID());
								out.writeUTF(e.getActionCommand());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

						}
					});
					sort.add(temp);
				}

				info.addActionListener(this);
				delete.addActionListener(this);
				sort.addActionListener(this);
				addMouseListener(this);

				/*
				 * 点击按钮，出现聊天界面
				 */
				
				addActionListener(new ActionListener() // 点击按钮，出现聊天界面
				{
					public void actionPerformed(ActionEvent e) {
						int sPort = 0;
						InetAddress sIP = null;
						/*
						 * 首先获取好的IP以及端口
						 */
						try {
							out.writeUTF("readPORT");
							out.writeUTF(frd.getUserID());

							sPort = Integer.parseInt(in.readUTF());
							System.out.println(sPort);

							out.writeUTF("getIP");
							out.writeUTF(frd.getUserID());
							sIP = InetAddress.getByName(in.readUTF());
							System.out.println(sIP);
							
							
							

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						boolean newit = true;//用于判断是否要新建聊天界面
						for(User u :frdtalk )//扫描好友是不是已经在正在聊天中
						{
							if(frd.getUserID().equals(u.getUserID())) {newit= false;break;}
						}
						
						if(newit)//用于判断是否要新建聊天界面,需要新建
						{
							frd.setTalk(new QQchatframe(user, frditm.this.frd, sPort, sIP,
									sSocket,out,in));
							frdtalk.add(frd);
							System.out.println("newit");
						}
						else if(frd.getTalk()==null)
						{
							frd.setTalk(new QQchatframe(user, frditm.this.frd, sPort, sIP,
									sSocket,out,in));
							System.out.println("show");
						}
						else {frd.getTalk().setVisible(true);System.out.println("pack");}

					}
				});

				popupMenu.add(info);
				popupMenu.add(delete);
				popupMenu.add(sort);
				popupMenu.pack();

				setBackground(Color.WHITE);

			}

			/*
			 * 主要用于显示全部好友时,包括连接服务器查询好友的各项信息
			 */
			public frditm(int i) {
				

				popupMenu = new JPopupMenu();
				delete = new JMenuItem("删除好友");
				info = new JMenuItem("好友信息");
				sort = new JMenu("添加分组→");

				for (int j = 0; j < groupnum; j++) // 将好友添加至分组
				{
					JMenuItem temp = new JMenuItem(groupname.get(j));
					temp.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							System.out.println(e.getActionCommand());
							try {
								out.writeUTF("addingroup");
								out.writeUTF(user.getUserID());
								out.writeUTF(frditm.this.frd.getUserID());
								out.writeUTF(e.getActionCommand());
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					});
					sort.add(temp);
				}

				info.addActionListener(this);
				delete.addActionListener(this);
				sort.addActionListener(this);
				addMouseListener(this);
				
				/*
				 * 点击按钮，出现聊天界面
				 */
				
				addActionListener(new ActionListener() // 点击按钮，出现聊天界面
				{
					public void actionPerformed(ActionEvent e) {
						int sPort = 0;
						InetAddress sIP = null;
						try {
							out.writeUTF("readPORT");
							out.writeUTF(frd.getUserID());

							sPort = Integer.parseInt(in.readUTF());
							System.out.println(sPort);

							out.writeUTF("getIP");
							out.writeUTF(frd.getUserID());
							sIP = InetAddress.getByName(in.readUTF());
							System.out.println(sIP);
							
							
							

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						boolean newit = true;
						for(User u :frdtalk )
						{
							if(frd.getUserID().equals(u.getUserID())) {newit= false;break;}
						}
						if(newit)
						{
							frd.setTalk(new QQchatframe(user, frditm.this.frd, sPort, sIP,
									sSocket,out,in));
							frdtalk.add(frd);
							System.out.println("newit");
						}
						else if(frd.getTalk()==null)
						{
							frd.setTalk(new QQchatframe(user, frditm.this.frd, sPort, sIP,
									sSocket,out,in));
							System.out.println("show");
						}
						else {frd.getTalk().setVisible(true);System.out.println("pack");}

					}
				});

				popupMenu.add(info);
				popupMenu.add(delete);
				popupMenu.add(sort);
				popupMenu.pack();

				String userID = null, userName = null, sexstr = null, age = null, placestr = null, pic = null, show = null, signature = null, LOGINSTATE = null, groupin = null;

				/*
				 * 连接数据库，读取好友信息
				 */
				try {
					out.writeUTF("showfriend");
					out.writeUTF(user.getUserID());
					out.write(i);

					userID = in.readUTF();
					userName = in.readUTF();
					sexstr = in.readUTF();
					age = in.readUTF();
					placestr = in.readUTF();
					pic = in.readUTF();
					show = in.readUTF();
					signature = in.readUTF();
					LOGINSTATE = in.readUTF();
					groupin = in.readUTF();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				frd = new User(userID, userName, sexstr, age, placestr, pic,
						show, signature, LOGINSTATE, groupin);

				friendlist.add(frd);

				System.out.println(friendlist.get(i - 1));

				setText(userName);
				if (LOGINSTATE.equals("1")) // 判断好友是否隐身
				{
					setIcon(new ImageIcon(this.getClass().getResource(pic)));
				} else
					setIcon(new ImageIcon(this.getClass().getResource(
							pic.substring(0, 10) + "-0.gif")));

				setPreferredSize(new Dimension(240, 37));
				setBackground(Color.WHITE);
			}

			/*
			 * 右键弹出窗口的反应
			 */
			public void mousePressed(MouseEvent e) {

				popupMenuTrigger(e);
			}

			public void mouseReleased(MouseEvent e) {

				popupMenuTrigger(e);
			}

			public void mouseClicked(MouseEvent e) {
				popupMenuTrigger(e);
			}

			public void mouseEntered(MouseEvent e) {
				popupMenuTrigger(e);
			}

			public void mouseExited(MouseEvent e) {
				popupMenuTrigger(e);
			}

			private void popupMenuTrigger(MouseEvent e) {

				if (e.isMetaDown()) // 判断是否为右键点击
				{

					this.requestFocusInWindow(); // 使光标移动到当前窗口

					delete.setEnabled(true);
					info.setEnabled(true);

					popupMenu.show(this, e.getX() + 3, e.getY() + 3);
				}
			}

			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == delete) {
					this.delete();
				}
				if (e.getSource() == info) {
					this.getFrdInfo();
				}

			}
			/*
			 * 获取好友信息,在新窗口中显示,使用已经保存的好友信息,不需要服务器
			 */
			private void getFrdInfo() {
				JFrame infoP = new JFrame();
				JLabel userID, UserName, sex, age, place, face, qqshow, signature;

				userID = new JLabel("用户账号：" + this.frd.getUserID());
				UserName = new JLabel("用户昵称：" + this.frd.getUserName());
				sex = new JLabel("性别：" + this.frd.getSexstr());
				age = new JLabel("年龄：" + this.frd.getAge());
				place = new JLabel("地区：" + this.frd.getPlacestr());
				face = new JLabel("头像", new ImageIcon(this.getClass()
						.getResource(this.frd.getPic())), SwingConstants.LEFT);
				qqshow = face = new JLabel("QQ秀", new ImageIcon(this.getClass()
						.getResource("/pic/qqshow/" + this.frd.getShow())),
						SwingConstants.LEFT);
				signature = new JLabel("个性签名：" + this.frd.getSignature());

				JPanel infopanel = new JPanel(new GridLayout(7, 1));
				infoP.setLayout(new GridLayout(2, 1));
				infoP.setBackground(Color.cyan);

				infopanel.add(userID);
				infopanel.add(UserName);
				infopanel.add(sex);
				infopanel.add(age);
				infopanel.add(place);
				infopanel.add(face);
				infopanel.add(signature);

				infoP.add(infopanel);
				infoP.add(qqshow);
				infoP.pack();
				infoP.setVisible(true);

			}

			private void delete() {
				// TODO Auto-generated method stub

			}

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub

			}

		}
	}


}
