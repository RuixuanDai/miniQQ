

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


/** 
 *		�û���¼����
 *		����:�����
 */
public class Login{

		private static final long serialVersionUID = 1L;
		final JTextField name_text = new JTextField(15);
		final JPasswordField pwd_text = new JPasswordField(15);
		private JComboBox stateC;
		final JFrame frame = new JFrame();
		
		private String host="localhost";
		private int port=5000;//������5000�˿�
		private Socket socket;
		private DataOutputStream out;
		private DataInputStream in;
		private String flag;
		private String UserName,sex,age,place,face,qqshow,signature,loginstate,group;
		
		public User user;
		
		public static final int DEFAULT_WIDTH = 330;
		public static final int DEFAUL_HEIGHT = 235;
		
		public static void main(String[] args) {
			new Login().showGUI();
		}
		public void showGUI() {
			
			try {
				socket=new Socket(host,port);
				in = new DataInputStream(socket.getInputStream());
				out =new DataOutputStream(socket.getOutputStream());
			} catch (UnknownHostException e1) {
				
				e1.printStackTrace();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			
			
			
			String[] stateStr = { "����", "����" };
			frame.setLayout(new FlowLayout(1, 0, 0));
			frame.setTitle("QQ�û���½");
			frame.setSize(DEFAULT_WIDTH,DEFAUL_HEIGHT );
			frame.setResizable(false);
			
			
			Image img = frame.getToolkit().createImage(this.getClass().getResource("pic/login/top.gif"));
			frame.setIconImage(img); // �����������ϵ�ͼ��
			
			
			try
			{
				ImageIcon icon = new ImageIcon(this.getClass().getResource("pic/login/top.jpg"));
				JLabel la = new JLabel(icon);
				la.setIcon(icon);
				frame.add(la); // �ӵ�½�����ϲ���ͼƬ	
			}catch (NullPointerException e)
			{
				System.out.println("ͼƬδ�ҵ�");
			}
			
			
			// ����һ��JPanel������
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(320, 115)); // ���ô�С
			// ����JPanel�ı�����ɫ
			panel.setBackground(new Color(159, 238, 253));

			// ����JPanel�ı߿�
			panel.setBorder(new javax.swing.border.LineBorder(new Color(
					144, 185, 213)));

			JLabel name_label = new JLabel("QQ�ʺ�");
			JLabel pwd_label = new JLabel("QQ���� ");
			 
			final JLabel user = new JLabel("ע���û�");

			JLabel mima = new JLabel("         ");
			
			mima.setFont(new Font("����", 0, 12));
			mima.setForeground(new java.awt.Color(0, 50, 255));
			JLabel zt = new JLabel("״̬��");
			stateC = new JComboBox(stateStr);// ������
			JLabel jll = new JLabel("      ");
			JLabel ck = new JLabel("         ");

			panel.add(name_label);
			panel.add(name_text);
		 	panel.add(user);
			panel.add(pwd_label);
			panel.add(pwd_text);
			panel.add(mima);
			panel.add(zt);
			panel.add(stateC);
			panel.add(jll);
			panel.add(ck);

			frame.add(panel);
			loginM(); // ���÷���

			
			frame.setLocation(300, 300); 
			frame.setVisible(true);
			frame.setDefaultCloseOperation(3);
			
			/*
			 * ������ע�ᰴť,û�в���button,����jlabel,��������������
			 */
			user.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent e) {
					new Regist(in,out);
					
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					user.setForeground(Color.green);
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					user.setForeground(Color.black);
				
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}
				
			});
		}
		
		/*
		 * 
		 * ��½��ť,������
		 */

		public  void loginM() {
			JPanel ppp = new JPanel();

			JButton init = new JButton();// д�ͻ��˵Ľ���

			init.setPreferredSize(new java.awt.Dimension(80, 22));
			init.add(new JLabel(new ImageIcon(this.getClass().
					getResource("pic/login/loginok.jpg"))));
			
			
		
			ppp.add(new JLabel("                        "));
			ppp.add(init);
			frame.add(ppp);
			
			
			init.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					
					String userID = name_text.getText();
					@SuppressWarnings("deprecation")
					String password = pwd_text.getText();
					String state;String states = stateC.getSelectedItem().toString();
					if(states.equals("����")) state="0";
					else state = "1";
					
					
					if(userID==null||"".equals(userID)||password==null||"".equals(password)){
						javax.swing.JOptionPane.showMessageDialog(null, "�û��������벻��Ϊ��");
					}
					else
					{	frame.dispose();
					
						
						try 
						{
							out.writeUTF("login");
							out.writeUTF(userID);
							out.writeUTF(password);
							out.writeUTF(state);
							
							flag = in.readUTF();
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}	
						
					
					
					if(flag.equals("true"))//��½�ɹ�
					{
							JOptionPane.showMessageDialog(null, "��½�ɹ�");
							try {
								 UserName = in.readUTF();
								 sex = in.readUTF();
								 age = in.readUTF();
								 place = in.readUTF();
								 face = in.readUTF();
								 qqshow = in.readUTF();
								 signature = in.readUTF();
								loginstate=in.readUTF();
								group=in.readUTF();
							} catch (IOException e1) {
								
								e1.printStackTrace();
							}
							
							user = new User(userID, password, UserName, sex, age, place, face, 
									qqshow, signature,loginstate,group);
							
							new MainFrame(user,in,out);
					}
					else //�û��������벻ƥ��
						{
							javax.swing.JOptionPane.showMessageDialog(null, "�û��������벻ƥ��");
							System.exit(0);
						}
						
					
					
					
				}
				
			});
		}

		
	}
