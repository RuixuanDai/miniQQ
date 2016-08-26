import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import javax.swing.*;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * 
 * �������Ҫ�����빦��
 * ���ߣ������
 */


public class QQchatframe extends JFrame {
	/**
	 * 
	 * qq��������������
	 * ���ߣ������  
	 */
	private static final long serialVersionUID = 1L;
	public static final int DEFAULT_WIDTH = 580;
	public static final int DEFAUL_HEIGHT = 500;
	public Showbox show = new Showbox();
	public Inputbox input =new Inputbox();
	public Downbtn Dbtn = new Downbtn(this);
	public JLabel myqq,frdqq;
	
	private DataOutputStream out;
	private DataInputStream in;
	private DatagramPacket sPack = null;
	private DatagramSocket sSocket = null;
	private InetAddress sIP;
	private int sPort;
	
	private Calendar ca = null;
	

	private User user = null,frd = null;
	
	public QQchatframe(User user,User frd,int sPort
			,InetAddress sIP,DatagramSocket sSocket ,DataOutputStream out,DataInputStream in) 
	{
		this.user=user;
		this.frd = frd;
		this.sPort=sPort;
		this.sIP=sIP;
		this.sSocket=sSocket;
		this.out=out;
		this.in=in;
		
		
		setTitle("��"+frd.getUserName()+"������"); // ���ô�������
		setLayout(null);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image ime = kit.getImage(this.getClass().getResource(
				frd.getPic().substring(0, 10) +"-2.gif")); //���ô���ͼ��
		setIconImage(ime);
		setBackground(Color.DARK_GRAY);

		
		
		myqq = new JLabel(new ImageIcon(this.getClass().getResource("pic/qqshow/"+user.getShow())));
		frdqq = new JLabel(new ImageIcon(this.getClass().getResource("pic/qqshow/"+this.frd.getShow())));
		
		JPanel myqp = new JPanel(),frdqp = new JPanel();
		myqp.add(myqq);
		frdqp.add(frdqq);
		frdqp.setBounds(390,10,180,210);
		myqp.setBounds(390,225,180,210);
		frdqp.setBackground(Color.CYAN);
		myqp.setBackground(Color.CYAN);
		
		
		
		add(myqp);
		add(frdqp);
		add(Dbtn);
		add(show);
		add(input);
		
		setSize(DEFAULT_WIDTH, DEFAUL_HEIGHT); // ���ô��ڴ�С
		setBackground(Color.DARK_GRAY);
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				QQchatframe.this.setVisible(false);
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		setResizable(false);
		setLocationByPlatform(true); // ��ϵͳ���ƴ���λ��
		
		setVisible(true);
	

	}
	
	/*
	 * ��İ�������� (δʵ��)
	 */
	
	public QQchatframe(User user,String recID,int sPort
			,InetAddress sIP,DatagramSocket sSocket)
	{
		
		
		setTitle("��İ����"+recID+"�ĶԻ�"); // ���ô�������
		
		setLayout(null);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image ime = kit.getImage(this.getClass().getResource(
				frd.getPic().substring(0, 10) + "-2.gif")); // ���ô���ͼ��
		setIconImage(ime);
		
					
		add(Dbtn);
		add(show);
		add(input);
		setSize(DEFAULT_WIDTH, DEFAUL_HEIGHT); // ���ô��ڴ�С
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				QQchatframe.this.dispose();
				
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		setResizable(false);
		setLocationByPlatform(true); // ��ϵͳ���ƴ���λ��
		
		setVisible(true);
	
	}
	
	/*
	 * ������Ϣ,��mainframe����
	 */
	public void recmsg(String str)
	{
		
		ca = Calendar.getInstance();
		int min=ca.get(Calendar.MINUTE);//��   
		int hour=ca.get(Calendar.HOUR_OF_DAY);//Сʱ   
		int second=ca.get(Calendar.SECOND);//�� 
     
		str = frd.getUserName()+" "+hour+":"+min+":"+second + "\n" + str +"\n";//Ϊ�����������ʱ��
		
		show.append(str);
	}
	/*
	 * ��дsetvisible����
	 */
	public void setVisible(boolean flag)
	{
		super.setVisible(flag);
		if(!flag)
		{
			String str=show.getText();
			
			try {
				out.writeUTF("history");
				out.writeUTF(user.getUserID());
				out.writeUTF(frd.getUserID());
				out.writeUTF(str);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	class Downbtn extends JPanel implements ActionListener
	{
		/**
		 * ������������ť,�Լ����ݽ����뷢��
		 *  ���ߣ������  
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 390;
		public static final int PANEL_HEIGHT = 50;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 430;
			
		
		private JButton send = new JButton("����");
		private JButton quit = new JButton("�˳�");
		private JButton his = new JButton("��ʷ��¼");
		private Inputbox sendout = null;
		private Showbox show = null;
		private String messg = null;
		private JFrame history = null;

		
		
		public Downbtn(QQchatframe it)
		{
			sendout = it.input;
			show = it.show;
			
			setBounds(PANEL_X, PANEL_Y, PANEL_WIDTH, PANEL_HEIGHT);
			setLayout(new FlowLayout(FlowLayout.RIGHT));
			
			send.addActionListener(this);
			quit.addActionListener(this);
			his.addActionListener(this);
					
			add(send);
			add(quit);
			setLayout(new FlowLayout(FlowLayout.LEFT));
			add(his);
		}
		/*
		 * ������ť�Ķ�������
		 */
		
		
		public void actionPerformed(ActionEvent e) 
		{
			String str = e.getActionCommand();
			byte[] smessg = null;
			
			if(str.equals("����"))
			{
				messg = sendout.getText();
				String messgsend = user.getUserID()+"*"+"0"+messg;
				if(messg.isEmpty())
				{
					JOptionPane.showMessageDialog(null,"��������������");
				}
				else
				{
					ca = Calendar.getInstance();
					int min=ca.get(Calendar.MINUTE);//��   
					int hour=ca.get(Calendar.HOUR_OF_DAY);//Сʱ   
					int second=ca.get(Calendar.SECOND);//�� 
		         /*
		          * Ϊ��Ϣ�����ʱ��
		          */
					messg = user.getUserName()+" "+hour+":"+min+":"+second + "\n" + messg +"\n";
					sendout.setText(null);
					show.append(messg);
					
					try {
						smessg = messgsend.getBytes();
						// ��װҪ���͵����ݰ�
						sPack = new DatagramPacket(smessg, smessg.length, sIP, sPort);
						// ��������
						sSocket.send(sPack);
						
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null,"��������ʧ��");
					}
				}
			}
			if(str.equals("�˳�"))
			{
				QQchatframe.this.setVisible(false);
			}
			if(e.getSource()==his){
				showhistory();
			}
			
		}
		/*
		 * �鿴��ʷ��¼
		 */
		private void showhistory() {
			String histo = null;
			history = new JFrame("��ʷ��¼");
			try {//���ʷ�����
				out.writeUTF("readChatRecord");
				out.writeUTF(user.getUserID());
				out.writeUTF(frd.getUserID());
				
				histo = in.readUTF();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			input hist = new input(34,5);
			hist.setText(histo);
			hist.setEditable(false);
			hist.setBackground(Color.green);
			history.add(new JScrollPane(hist));
			history.setSize(350, 350);
			history.setVisible(true);
		
		}
		

		
		
	}



	class Inputbox extends JPanel {
		/**
		 * �����,�����Ҽ������˵�,�Լ�������
		 *  ���ߣ������  
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 390;
		public static final int PANEL_HEIGHT = 150;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 330;
		public static final int Area_ROW=5;
		public static final int Area_COLUMN=34;

		
		private input inputok = new input(Area_ROW,Area_COLUMN);
		
		

		public Inputbox() {
			setBackground(Color.blue);
			setSize(PANEL_WIDTH, PANEL_HEIGHT);
			setLocation(PANEL_X, PANEL_Y);
			add(new JScrollPane(inputok));
		}


		
		public void setText(String str) {
			inputok.setText(str);	
		}
		



		public String getText() {
			return inputok.getText();
		}


	}

	class Showbox extends JPanel
	{
		/**
		 * ��Ϣ��ʾ������Ҽ������˵�,�Լ�������
		 *  ���ߣ������
		 */
		private static final long serialVersionUID = 1L;
		public static final int PANEL_WIDTH = 390;
		public static final int PANEL_HEIGHT = 250;
		public static final int PANEL_X = 0;
		public static final int PANEL_Y = 80;
		public static final int Area_ROW=13;
		public static final int Area_COLUMN=35;

		private input show = new input(Area_ROW,Area_COLUMN);
		
		public Showbox()
		{
			
			setBounds(PANEL_X,PANEL_Y,PANEL_WIDTH,PANEL_HEIGHT);
			show.setEditable(false);
			add(new JScrollPane(show));
		}

		public void append(String messg) 
		{
			show.append(messg);
			show.setSelectionStart(show.getText().length());
			
		}
		public String getText()
		{	String str =show.getText();
			show.setText("");
			return str;
		}
	
	}


	class input extends JTextArea implements MouseListener, ActionListener 
	{
		/**
		 * ���Ҽ��˵��������,��������ճ��,ȫѡ,����.
		 * ����:����
		 * ��Դ����
		 */
		private static final long serialVersionUID = 1L;


		private JPopupMenu popupMenu = null; 			//�Ҽ��˵�����
		private JMenuItem cutMenu = null, copyMenu = null, pasteMenu = null,
				selectAllMenu = null;

		public input(int row,int column) {

			super(row, column);
			setLineWrap(true);  									 //�����Զ�����
			popupMenu = new JPopupMenu();

			cutMenu = new JMenuItem("Cut");
			copyMenu = new JMenuItem("Copy");
			pasteMenu = new JMenuItem("Paste");
			selectAllMenu = new JMenuItem("Select All");

			cutMenu.setAccelerator(KeyStroke.getKeyStroke('X',    			//���ü��̿�ݼ�
					InputEvent.CTRL_MASK));
			copyMenu.setAccelerator(KeyStroke.getKeyStroke('C',				//���ü��̿�ݼ�
					InputEvent.CTRL_MASK));
			pasteMenu.setAccelerator(KeyStroke.getKeyStroke('V',
					InputEvent.CTRL_MASK));
			selectAllMenu.setAccelerator(KeyStroke.getKeyStroke('A',
					InputEvent.CTRL_MASK));

			cutMenu.addActionListener(this);
			copyMenu.addActionListener(this);
			pasteMenu.addActionListener(this);
			selectAllMenu.addActionListener(this);

			popupMenu.add(cutMenu);
			popupMenu.add(copyMenu);
			popupMenu.add(pasteMenu);
			popupMenu.add(new JSeparator());
			popupMenu.add(selectAllMenu);

			this.add(popupMenu);
			this.addMouseListener(this);				//����������

		}

		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == copyMenu) {
				this.copy();
			}
			if (e.getSource() == pasteMenu) {
				this.paste();
			}
			if (e.getSource() == cutMenu) {
				this.cut();
			}
			if (e.getSource() == selectAllMenu) {
				this.selectAll();
			}

		}

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

			if(e.isMetaDown())  		//�ж��Ƿ�Ϊ�Ҽ����
			{

				this.requestFocusInWindow();     //ʹ����ƶ�����ǰ����

				cutMenu.setEnabled(isAbleToCopyAndCut());    //�ж��Ƿ�����Щ����
				copyMenu.setEnabled(isAbleToCopyAndCut());
				pasteMenu.setEnabled(isAbleToPaste());
				selectAllMenu.setEnabled(isAbleToSelectAll());

				popupMenu.show(this, e.getX() + 3, e.getY() + 3);
			}
		}

		private boolean isAbleToSelectAll() {

			return !("".equalsIgnoreCase(this.getText()) || (null == this
					.getText()));
		}

		private boolean isAbleToCopyAndCut() {

			return (this.getSelectionStart() != this.getSelectionEnd());
		}

		private boolean isAbleToPaste() {

			Transferable content = this.getToolkit().getSystemClipboard()
					.getContents(this);
			try {
				return (content.getTransferData(DataFlavor.stringFlavor) instanceof String);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

	}


}

