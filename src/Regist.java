import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * 
 *       ע�����
 *       ����:�����
 */
public class Regist extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int PANEL_WIDTH = 465;
	public static final int PANEL_HEIGHT = 550;
	public static final int PANEL_X = 0;
	public static final int PANEL_Y = 430;

	private String userID;
	private String userName;
	private String passWord;
	private String rPassWord;
	private String sexstr;
	private String agenum;
	private String placestr;
	private String qqshow;
	private String signature;

	String str = "pic/face/1.gif";//Ĭ��ͼ��
	JLabel show = new JLabel();

	public Regist(final DataInputStream in, final DataOutputStream out) {
		super("QQע��");

		setSize(PANEL_WIDTH, PANEL_HEIGHT);
		setBackground(Color.red);
		setResizable(false);
		setDefaultCloseOperation(2);
		setLocationRelativeTo(null);
		setLayout(new FlowLayout(0, 20, 20));

		JLabel username = new JLabel("��  ��  ��:");
		final JLabel password = new JLabel("��         ��:");
		final JTextField ju = new JTextField(10);
		final JPasswordField jp = new JPasswordField(12);

		add(username);
		add(ju);
		JLabel content = new JLabel("��������ɣ������ظ�ע��      ");
		content.setForeground(Color.red);

		add(content);
		add(password);
		add(jp);

		JLabel content2 = new JLabel("��6~16λ�ַ����                              ");
		content2.setForeground(Color.red);
		add(content2);

		JLabel rPassword = new JLabel("�ظ�����:");
		add(rPassword);

		final JPasswordField jrp = new JPasswordField(12);
		add(jrp);

		JLabel content3 = new JLabel("���������һ��                              ");
		content3.setForeground(Color.red);
		add(content3);

		JLabel jNickname = new JLabel("��         ��:");
		final JTextField nickname = new JTextField(12);
		JLabel content4 = new JLabel("�����ǳ� ,20Ӣ���ַ�����  ");
		content4.setForeground(Color.red);

		add(jNickname);
		add(nickname);
		add(content4);

		final JLabel age = new JLabel("��     ��:");
		final JTextField ageIn = new JTextField(3);
		add(age);
		add(ageIn);

		String[] da = { "��", "Ů" };
		JLabel sex = new JLabel("��    ��:");
		final JComboBox cb = new JComboBox(da);// ������
		add(sex);
		add(cb);
		add(new JLabel("                                               "));// ���û���

		JLabel place = new JLabel("��    ��:");
		final JTextField placeIn = new JTextField(6);
		add(place);
		add(placeIn);
		add(new JLabel("                                                "
				+ "               "));// ���û���

		/*
		 * ����Ϊ����ͼ��
		 */
		JLabel jPhoto = new JLabel("ͷ         ��:");
		String[] pics = new String[9];
		for (int i = 1; i <= 9; i++) {
			pics[i - 1] = "" + i + ".gif";
		}
		final JComboBox choosePhoto = new JComboBox(pics);
		show.setIcon(new ImageIcon(this.getClass().getResource(str)));
		add(show);
		choosePhoto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				str = "pic/face/" + choosePhoto.getSelectedItem().toString();
				System.out.println(str);
				show.setIcon(new ImageIcon(this.getClass().getResource(str)));
				show.repaint();
			}
		});

		add(new JLabel("            "));// ���û���

		JLabel jSign = new JLabel("����ǩ��:");
		final JTextArea sign = new JTextArea(5, 20);
		sign.setLineWrap(true);
		JLabel content5 = new JLabel(
				"����ǩ��������50�����ĺ���֮��,���ಿ��ϵͳ���Զ�ɾ��...                              ");
		content5.setForeground(Color.red);

		add(jPhoto);
		add(choosePhoto);
		add(new JLabel("         Ԥ��:"));
		add(show);
		add(jSign);
		add(sign); // ����ǩ��
		add(content5);
		add(new JLabel("                                              "));// ���û���

		final JButton regsit = new JButton("ȷ��ע��");
		add(regsit);

		setVisible(true);

		regsit.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				dispose();
				userID = ju.getText();
				passWord = jp.getText();
				rPassWord = jrp.getText();
				agenum = (ageIn.getText() == null || "".equals(ageIn.getText()) ? " "
						: ageIn.getText());
				sexstr = cb.getSelectedItem().toString();
				qqshow = cb.getSelectedItem().toString() + ".gif";
				placestr = (placeIn.getText() == null
						|| "".equals(placeIn.getText()) ? " " : placeIn
						.getText());
				userName = (nickname.getText() == null
						|| "".equals(nickname.getText()) ? " " : nickname
						.getText());
				signature = (sign.getText() == null
						|| "".equals(sign.getText()) ? " " : sign.getText());

				jp.setText("");
				jrp.setText("");

				if ("".equals(passWord) || null == passWord) {
					javax.swing.JOptionPane.showMessageDialog(null, "���벻��Ϊ�գ�");
				} else if ("".equals(rPassWord) || null == rPassWord) {
					JOptionPane.showMessageDialog(null, "�ظ����벻��Ϊ�գ�");
				} else if (!passWord.equals(rPassWord)) {
					JOptionPane.showMessageDialog(null, "�ظ����벻��ȷ��");
				} else 
				{

					new User(userID, passWord, userName, sexstr, agenum,
							placestr, str, qqshow, signature);
					int result = 3;
					try {
						out.writeUTF("register");
						out.writeUTF(userID);
						out.writeUTF(passWord);
						out.writeUTF(userName);
						out.writeUTF(sexstr);
						out.writeUTF(agenum);
						out.writeUTF(placestr);
						out.writeUTF(str);
						out.writeUTF(qqshow);
						out.writeUTF(signature);

						result = in.readInt();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (result == 0)
						JOptionPane.showMessageDialog(null, "ע��ɹ�");
					else
						JOptionPane.showMessageDialog(null, "ע��ʧ�ܣ��˺��Ѵ���");
				}
			}

		});

	}

}
