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
	 * ����ϵͳ���û��ͷ���������
	 * ���ߣ������
	 */
public class MainFrame extends JFrame {

	/**
	 * �����洰��Ԫ��
	 * ���ߣ������
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
	private int Port; // �洢���Ͷ˿ںͽ��ն˿�
	private byte inBuf[];
	public static final int BUFSIZE = 1024;
	private String recv = null;

	private Friends frdpane = null;
	JLabel pic;
	private JButton change = new JButton("�޸ĸ�����Ϣ");

	private ArrayList<User> friendlist = new ArrayList<User>();  //���ڴ���ȫ�������б�
	private ArrayList<User> frdtalk = new ArrayList<User>();	//���ڴ洢���������еĺ���
	private ArrayList<String> groupname = new ArrayList<String>();//���ڴ洢���ѷ���
	private User user;
	private int groupnum = 0;

	public MainFrame(User user, final DataInputStream in,
			final DataOutputStream out) {
		this.user = user;
		this.in = in;
		this.out = out;

		StringTokenizer stk = new StringTokenizer(user.getGroup()); 
		groupnum = stk.countTokens(); // ��¼�û��м�������
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
			rPack = new DatagramPacket(inBuf, BUFSIZE); // �����������ݰ�
			rSocket = new DatagramSocket(Port); // ָ���������ݵĶ˿�
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
				user.getPic().substring(0, 10) + "-2.gif")); // ���ô���ͼ��
		setIconImage(ime);

		/*
		 * �жϵ�½״̬������ͼ��
		 */
		if (user.getLOGINSTATE().equals("1")) // �жϵ�½״̬
		{
			setTitle(user.getUserName() + "(����)");
			pic = new JLabel(new ImageIcon(this.getClass().getResource(
					user.getPic())));
		} 
		else {
			setTitle(user.getUserName() + "(����)");
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
				int j = JOptionPane.showConfirmDialog(null, "ȷ���˳���", "�˳�",
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
	 *  ��ȡ�ҵ�IP�Ų�д�����ݿ�
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
	 *  ��ȡ�ҵĶ˿ں�
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
	 * 		�����º���
	 */
	class Search  implements ActionListener
	{
		/**
		 * ���Һ��Ѵ���Ԫ���Լ������
		 * ���ߣ������
		 */
		private static final long serialVersionUID = 1L;
		
		public void ShowSearchUI(){
			JFrame window = new JFrame("����/��Ӻ���");
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
			la2.setText("������ϵͳ���ҵ��û�,ѡ�е������Ӻ��ѣ�");
			north.add(la2);
			
			north.setVisible(true);
			
			
			JPanel frdpanel = new JPanel(new GridLayout(10, 1));
			
			/*
			 * ��ѯ����������
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
			 * ��ʾ���ѵİ�ť����Ҫ��Ϊ�˱���ID��
			 * ����:�����
			 */
			private static final long serialVersionUID = 1L;
			public String ID = null;
			public jfriend(String ID,String name,String face,String place,String login)
			{
				this.ID=ID;
				if (login.equals("1")) // �жϺ����Ƿ�����
				{
					setIcon(new ImageIcon(this.getClass().getResource(
							face)));
				} else
					setIcon(new ImageIcon(this.getClass().getResource(
							face.substring(0, 10) + "-0.gif")));
				
				setText("  "+"�ǳƣ�"+name+"   ������"+place);
			}
		}

		/*
		 * ������ť
		 */
		public void actionPerformed(ActionEvent e) {
			int i = JOptionPane.showConfirmDialog(null, "��Ϊ����?",
					"��Ϊ���ѣ�", JOptionPane.YES_NO_OPTION);
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
	 * ��̨��������ͺ�������
	 */
	class listenchat extends thread implements Runnable {

		/**
		 * ��̨ͨ�����̼߳�������ͺ�������
		 * ���ߣ������
		 */
		String recID, recAct, recMsg;

		public void run() {
			while (true) {
				try {
					rSocket.receive(rPack);
					recv = new String(rPack.getData(), 0, rPack.getLength());

					int num_Idex = recv.indexOf("*");
					
					/*
					 * �������յ����ݰ�,recActΪ0Ϊ��Ϣ,recActΪ1Ϊ��������
					 */
					recID = recv.substring(0, num_Idex);
					recAct = recv.substring(num_Idex + 1, num_Idex + 2);
					recMsg = recv.substring(num_Idex + 2);

					if (recAct.equals("0")) {//���յ���Ϣ
						revmess(recID, recMsg);
					}
					if (recAct.equals("1")) {//���յ���������
						frdrequest();
					}
				} catch (Exception e) {

				}
			}
		}
		/*
		 * ���յ���Ϣ�ķ�Ӧ
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
				if(!istalk)frdtalk.add(frd);  //������������б��У����������б�
				if(frd.getTalk()!=null)
				{
					if(frd.getTalk().isVisible())	
						frd.getTalk().recmsg(str); 
					else 
					{int i = JOptionPane.showConfirmDialog(null, frd.getUserName()
							+ "������������", "��������",
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
						+ "������������", "��������",
						JOptionPane.YES_NO_OPTION);
					if (i == JOptionPane.YES_OPTION)
					{
						int sPort = 0;
						InetAddress sIP = null;
						try {			//��ȡ���ѵ�Ip�Ͷ˿ں�
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
						+ "������������", "��������",
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
		 * ���յ���������ķ�Ӧ
		 */
		public void frdrequest(){
			int i = JOptionPane.showConfirmDialog(null, recID + "�������Ϊ����",
					"��Ϊ���ѣ�", JOptionPane.YES_NO_OPTION);
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
		 * �޸ĸ�����Ϣ����Ԫ���Լ������
		 * ���ߣ������
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
		String qqstr = "��.gif";
		JLabel show = new JLabel();
		JLabel qqshows = new JLabel();

		public changeinfo() {
			setSize(PANEL_WIDTH, PANEL_HEIGHT);
			setBackground(Color.red);
			setResizable(false);
			setDefaultCloseOperation(2);
			setLocationRelativeTo(null);
			setLayout(new FlowLayout(0, 20, 20));



			JLabel jNickname = new JLabel("��         ��:");
			final JTextField nickname = new JTextField(12);
			nickname.setText(user.getUserName());
			JLabel content4 = new JLabel("�����ǳ�                                     ");
			content4.setForeground(Color.red);

			add(jNickname);
			add(nickname);
			add(content4);

			final JLabel age = new JLabel("��         ��:");
			final JTextField ageIn = new JTextField(3);
			ageIn.setText(user.getAge());
			add(age);
			add(ageIn);

			String[] da = { "��", "Ů" };
			JLabel sex = new JLabel("��         ��:");
			final JComboBox cb = new JComboBox(da);// ������
			cb.setSelectedItem(new MenuItem(user.getSexstr()));
			add(sex);
			add(cb);
			add(new JLabel("                                               "));// ���û���

			JLabel place = new JLabel("��          ��:");
			final JTextField placeIn = new JTextField(6);
			placeIn.setText(user.getPlacestr());
			add(place);
			add(placeIn);
			add(new JLabel("                                                "
					+ "                       "));// ���û���

			/*
			 * ����Ϊ����ͼ��
			 */
			JLabel jPhoto = new JLabel("ͷ         ��:");
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
			add(new JLabel("         Ԥ��:"));
			add(show);
			add(new JLabel("                                      "));// ���û���
			
			
			/*
			 * ����Ϊqqshow
			 */
			JLabel jshow = new JLabel("QQ��:");
			String[] shows = {"��","��1","��2","��3","��4","��5","Ů","Ů1","Ů2","Ů3","Ů4","Ů5"};
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
			add(new JLabel("   Ԥ��:"));
			add(qqshows);
			add(new JLabel("                   "));// ���û���

			
			JLabel jSign = new JLabel("����ǩ��:");
			final JTextArea sign = new JTextArea(5, 20);
			sign.setLineWrap(true);
			sign.setBackground(Color.LIGHT_GRAY);
			sign.setText(user.getSignature());
			JLabel content5 = new JLabel("����ǩ��������50�����ĺ���֮��,���ಿ" +
					"��ϵͳ���Զ�ɾ��...                              ");
			content5.setForeground(Color.red);

			
			
			
			add(jSign);
			add(sign); 
			add(content5);
			add(new JLabel("                                              "));// ���û���
			
			
			
			

			final JButton regsit = new JButton("ȷ���޸�");
			add(regsit);
			
			
			setVisible(true);
			
			/*
			 * ���ȷ���޸ĺ�ķ�Ӧ,�����ڲ���
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
		 * �޸ĵ�¼״̬����Ԫ���Լ������
		 * ���ߣ������
		 */

		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 52;
		public static final int PANEL_HEIGHT = 25;
		public static final int PANEL_X = 180;
		public static final int PANEL_Y = 0;

		public state() {

			addItem("����");
			addItem("����");
			setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
			if (user.getLOGINSTATE().equals("0")) // �����½��ʱ������ʹ����ѡ�С�
			{
				setSelectedIndex(1);
			}

			addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (this.getSelectedItem().toString().equals("����")) {
				//����ͼ��
				pic.setIcon(new ImageIcon(this.getClass().getResource(
						user.getPic().substring(0, 10) + "-0.gif")));
				//���ô�������
				MainFrame.this.setTitle(user.getUserName() + "(����)");

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
				MainFrame.this.setTitle(user.getUserName() + "(����)");
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
		 * ������Ϣ����Ԫ���Լ������
		 * ���ߣ������
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 240;
		public static final int PANEL_HEIGHT = 20;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 454;

		public info() {
			setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);

			add(new JLabel("�ҵ���Ϣ"));
		}
	}

	class Category extends JPanel implements ActionListener {
		/**
		 * ���ѷ��鴰��Ԫ���Լ������
		 * ���ߣ������
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 240;
		public static final int PANEL_HEIGHT = 70;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 555;

		private JButton all = new JButton("ȫ������");
		private JButton online = new JButton("���ߺ���");
		private JMenu cate = null;
		private JButton talking = new JButton("������");
		private JButton newfrd = new JButton("�����º���");

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

			cate = new JMenu("���ѷ����");
			/*
			 * ɨ��ӵ�м�������
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
			
			JMenuItem newsort = new JMenuItem("����·���:");
			
			/*
			 * ����·��鹦�ܼ���
			 */
			newsort.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String str = JOptionPane.showInputDialog("�·��飺");
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
		 * ����·��鹦�ܼ���
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
		 * ������ť�Ĺ���,����Ӧ�ķ�Ӧ
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
		 * ��ʾ������Ϣ����ع���
		 * ����:�����
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

			j1.add(new JLabel("�ǳ�"));
			j1.add(new JLabel(user.getUserName()));
			j2.add(new JLabel("�Ա�"));
			j2.add(new JLabel(user.getSexstr()));
			j3.add(new JLabel("����"));
			j3.add(new JLabel(user.getAge()));
			j4.add(new JLabel("����"));
			j4.add(new JLabel(user.getPlacestr()));

			add(j1);
			add(j2);
			add(j3);
			add(j4);

		}
	}

	class Friends extends JPanel {
		/**
		 * �����б���ع���
		 * �����ڲ��������ʾfrditm
		 * ����:�����
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 240;
		public static final int PANEL_HEIGHT = 400;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 60;

		/*
		 * ������,�ڳ��ι�����,��ʾȫ������
		 */
		public Friends() {
			setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
			setBackground(Color.DARK_GRAY);
			setLayout(new GridLayout(10, 1));

			
			showfriend();//��ʾȫ������

		}
		/*
		 * ��ʾ�����еĺ���
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
		 * ��ʾ���ߵĺ���
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
		 * ��ʾ�������
		 */
		public void showfriend(String str) {
			this.removeAll();

			/*
			 * ɨ������Ƿ��ڴ˷���
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
		 * ��ʾȫ������
		 */
		public void showfriend() {

			this.removeAll(); // ���panel����ʾ�ĺ���
			friendlist.clear(); // ��պ�������

			int nn = 0;
			/*
			 * ��ȡ������
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
			 * ����һ������ʾ
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
			 * �������ѵ���ʾ,����jbuttonʵ��,
			 * ������ѯ��������ʾȫ������,�Լ���friendlist����ʾ�ض��ĺ���
			 * ����:�����
			 */
			private static final long serialVersionUID = 1L;
			private JPopupMenu popupMenu = null; // �Ҽ��˵�����
			private JMenuItem delete = null, info = null;
			private JMenu sort = null;

			private User frd = null;//û��button����һ���û�����

			/*
			 * ��friendlist����ʾ�ض��ĺ���
			 */
			public frditm(User frds) {
				super(frds.getUserName());

				frd = frds;

				if (frds.getLOGINSTATE().equals("1")) // �жϺ����Ƿ�����
				{
					setIcon(new ImageIcon(this.getClass().getResource(
							frds.getPic())));
				} else
					setIcon(new ImageIcon(this.getClass().getResource(
							frds.getPic().substring(0, 10) + "-0.gif")));

				popupMenu = new JPopupMenu();
				delete = new JMenuItem("ɾ������");
				info = new JMenuItem("������Ϣ");
				sort = new JMenu("��ӷ����");

				for (int j = 0; j < groupnum; j++) // ���ѷ�����
				{
					JMenuItem temp = new JMenuItem(groupname.get(j));
					temp.addActionListener(new ActionListener() {

						/*
						 * �˴�ʵ�ֽ�������ӵ��ض��ķ���,ͨ��������
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
				 * �����ť�������������
				 */
				
				addActionListener(new ActionListener() // �����ť�������������
				{
					public void actionPerformed(ActionEvent e) {
						int sPort = 0;
						InetAddress sIP = null;
						/*
						 * ���Ȼ�ȡ�õ�IP�Լ��˿�
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
						boolean newit = true;//�����ж��Ƿ�Ҫ�½��������
						for(User u :frdtalk )//ɨ������ǲ����Ѿ�������������
						{
							if(frd.getUserID().equals(u.getUserID())) {newit= false;break;}
						}
						
						if(newit)//�����ж��Ƿ�Ҫ�½��������,��Ҫ�½�
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
			 * ��Ҫ������ʾȫ������ʱ,�������ӷ�������ѯ���ѵĸ�����Ϣ
			 */
			public frditm(int i) {
				

				popupMenu = new JPopupMenu();
				delete = new JMenuItem("ɾ������");
				info = new JMenuItem("������Ϣ");
				sort = new JMenu("��ӷ����");

				for (int j = 0; j < groupnum; j++) // ���������������
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
				 * �����ť�������������
				 */
				
				addActionListener(new ActionListener() // �����ť�������������
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
				 * �������ݿ⣬��ȡ������Ϣ
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
				if (LOGINSTATE.equals("1")) // �жϺ����Ƿ�����
				{
					setIcon(new ImageIcon(this.getClass().getResource(pic)));
				} else
					setIcon(new ImageIcon(this.getClass().getResource(
							pic.substring(0, 10) + "-0.gif")));

				setPreferredSize(new Dimension(240, 37));
				setBackground(Color.WHITE);
			}

			/*
			 * �Ҽ��������ڵķ�Ӧ
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

				if (e.isMetaDown()) // �ж��Ƿ�Ϊ�Ҽ����
				{

					this.requestFocusInWindow(); // ʹ����ƶ�����ǰ����

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
			 * ��ȡ������Ϣ,���´�������ʾ,ʹ���Ѿ�����ĺ�����Ϣ,����Ҫ������
			 */
			private void getFrdInfo() {
				JFrame infoP = new JFrame();
				JLabel userID, UserName, sex, age, place, face, qqshow, signature;

				userID = new JLabel("�û��˺ţ�" + this.frd.getUserID());
				UserName = new JLabel("�û��ǳƣ�" + this.frd.getUserName());
				sex = new JLabel("�Ա�" + this.frd.getSexstr());
				age = new JLabel("���䣺" + this.frd.getAge());
				place = new JLabel("������" + this.frd.getPlacestr());
				face = new JLabel("ͷ��", new ImageIcon(this.getClass()
						.getResource(this.frd.getPic())), SwingConstants.LEFT);
				qqshow = face = new JLabel("QQ��", new ImageIcon(this.getClass()
						.getResource("/pic/qqshow/" + this.frd.getShow())),
						SwingConstants.LEFT);
				signature = new JLabel("����ǩ����" + this.frd.getSignature());

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
