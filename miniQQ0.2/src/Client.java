import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * 
 * author Corey 1.6,
 * date 2011/3/14
 * ����ͼ�ν���
 *����һЩ�߳�����,�Լ���̬���ڱ���
 */
public class Client 
{
	static QQchatframe qq;
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				// TODO Auto-generated method stub
				qq=new QQchatframe();
				qq.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				//qq.setUndecorated(true);						// ȥ�����ڵ�װ�� 
				qq.setVisible(true);
				
			}
		});

		/*SwingUtilities.invokeLater(new Runnable() 
		{
			//���ô��ڶ�̬����
			@Override
			public void run() 
			{
				while(true)
				{
					String str = "QQchat";
					qq.setTitle(str);
					str=str.substring(1);
					try {
						TimeUnit.SECONDS.sleep(1);
						str=str.substring(1);
						qq.setTitle(str);
						TimeUnit.SECONDS.sleep(1);
						str=str.substring(1);
						qq.setTitle(str);
						TimeUnit.SECONDS.sleep(1);
						str=str.substring(1);
						qq.setTitle(str);
						TimeUnit.SECONDS.sleep(1);
						str=str.substring(1);
						qq.setTitle(str);
						TimeUnit.SECONDS.sleep(1);
						}
					catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});*/
				
	}
}

class QQchatframe extends JFrame
{
	public static final int DEFAULT_WIDTH=580;
	public static final int DEFAUL_HEIGHT=500;
	
	public QQchatframe()
	{
		setTitle("QQchat");								//���ô�������
		setLayout(null);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize =kit.getScreenSize();
		//Image ime = kit.getImage("QQ.png");				//���ô���ͼ��
		this.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});
		//setIconImage(ime);
		add(new Inputbox());
		setSize(DEFAULT_WIDTH,DEFAUL_HEIGHT);			//���ô��ڴ�С
		setLocationByPlatform(true);					//��ϵͳ���ƴ���λ��

	}
	
}
class Inputbox extends JPanel
{
	public static final int PANEL_WIDTH=390;
	public static final int PANEL_HEIGHT=150;
	private JButton
		send =new JButton("����"),
		close = new JButton("�ر�");
	private JTextField
		input = new JTextField();
	
	public Inputbox()
	{
		setLayout(null);
		input.setBounds(0, 20, 390, 100);
		setBackground(Color.blue);
		//setBackground(Color.black);
		add(input);
		setSize(PANEL_WIDTH, PANEL_HEIGHT);
		setLocation(0, 340);
		
	}
}
