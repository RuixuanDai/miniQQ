import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

/**
 * 
 * author Corey 1.6,
 * date 2011/3/14
 * 测试图形界面
 *加入一些线程运行,以及动态窗口标题
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
				//qq.setUndecorated(true);						// 去掉窗口的装饰 
				qq.setVisible(true);
				
			}
		});

		/*SwingUtilities.invokeLater(new Runnable() 
		{
			//设置窗口动态标题
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
		setTitle("QQchat");								//设置窗口名称
		setLayout(null);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screensize =kit.getScreenSize();
		//Image ime = kit.getImage("QQ.png");				//设置窗口图标
		this.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});
		//setIconImage(ime);
		add(new Inputbox());
		setSize(DEFAULT_WIDTH,DEFAUL_HEIGHT);			//设置窗口大小
		setLocationByPlatform(true);					//由系统控制窗口位置

	}
	
}
class Inputbox extends JPanel
{
	public static final int PANEL_WIDTH=390;
	public static final int PANEL_HEIGHT=150;
	private JButton
		send =new JButton("发送"),
		close = new JButton("关闭");
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
