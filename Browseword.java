import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;

public class Browseword extends JFrame implements ActionListener
{
	public static final String BASIC_ADDRESS ="http://192.168.1.102/word/CGI/";
	public static final String APPID="&appID=MEMORYCGI";
	private static  final String REG_INTERFACE="register_CGI.php";
	private static  final String LOGIN_INTERFACE="login_CGI.php";
	private static  final String SYNC_INTERFACE="sync_CGI.php";
	JTabbedPane browsetp = new JTabbedPane();
	//从数据库中取出wordlist
	String[] Wordlist;
	static JList BrowseList;
	static String choosedword = "";
	Connection conn;
	Statement showwordlist;
	Statement saveUnit;
	Statement Synicate;
	String word;
	String[] wordarray;
	JPanel learnword2;
	JPanel Setting2;
	JTextField UnitParameter;
	String textFieldValue;
	String password1;
	String username;
	JPasswordField pwdinput;
	JTextField nameinput;
	int bookid;
	int lastremember;
	static JButton Begin;
	public Browseword()
	{
		String choosed = Frame.choosedbook;

		//创建一个选项卡容器,将之添加到顶层容器内
		learnword2 = new JPanel();
		Setting2 = new JPanel();
		browsetp.addTab("Learn", learnword2);
		browsetp.addTab("Setting", Frame.Setting);
		browsetp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		
		//Learn选项卡界面,使用GridBagLayout布局
		learnword2.setLayout(new GridBagLayout());
		learnword2.setOpaque(true);
		GridBagConstraints c = new GridBagConstraints();
		//Back按钮布局
		JButton Back = new JButton("Back");
		Back.addActionListener(this);
		Begin = new JButton("Begin");
		Begin.addActionListener(this);
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		//如果组件没有填充整个区域,可以通过设置anchor域指定其位置
		c.anchor = GridBagConstraints.WEST;
		//c.anchor = GridBagConstraints.NORTH;
		learnword2.add(Back, c);
		c.anchor = GridBagConstraints.EAST;
		learnword2.add(Begin, c);
		//词典List布局
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 100;
		//组件拉伸至整个区域
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
			if(!conn.isClosed())
				System.out.println("Succeeded connecting to the database(browsebord)");
			showwordlist = conn.createStatement();
			//System.out.println("You choosed: " + choosed);
			//在mysql语句总引用java变量
			ResultSet rs = showwordlist.executeQuery("select * from `" + choosed + "`");
			
			ResultSetMetaData metaData = rs.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			//System.out.println(numberOfColumns);
			wordarray = new String[4103];
			int i = 0;
			while(rs.next())
			{
				for (int z =1; z <= numberOfColumns; z++)
				{
					//System.out.printf("%-8s\t", rs.getObject(z));
				}
/****************************************排列问题,存储在数组中***********************************************************************************/				
				wordarray[i] =  rs.getString(1)+ "          " + rs.getString(2) + "          " + rs.getString(3);
				i++;
			}
			for(int j = 0; j < wordarray.length; j++)
			{
				//System.out.println(wordarray[j]);
			}
			rs.close();
			showwordlist.close();
			conn.close();
		}
		catch (Exception ex)
		{
			System.out.println("Error: " + ex.toString());
		}
		//把数据库中的wordarray数组放到BrowseList中
		BrowseList = new JList(wordarray);
		BrowseList.addListSelectionListener(new ListSelectionListener()
		{
/**************************选中高亮问题****************************************************************/			
			public void valueChanged(ListSelectionEvent e)
			{
				choosedword = (String) BrowseList.getSelectedValue();
				//System.out.println(choosedword+"in Browseword");
				//Browseworddetail browseworddetail = new Browseworddetail();
				new Browseworddetail();
				//刷新界面
				//repaint();
			}
		});
		
		//一次只能选择一个列表索引
		BrowseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		learnword2.add(new JScrollPane (BrowseList),c);
		
		
		//Setting选项卡界面
		JLabel Unit = new JLabel("Unit: ");
		UnitParameter = new JTextField(10);
		JButton Save = new JButton("Save");
		JButton Syn= new JButton("Syn");
		JButton Initialize= new JButton("Initialize");
		JLabel name = new JLabel("name :");
		JLabel password = new JLabel("password :");
		nameinput = new JTextField(15);
		pwdinput = new JPasswordField(15);

		JButton login = new JButton("login");
		JButton register = new JButton("register");
		Setting2.setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		Setting2.add(name, c);
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		Setting2.add(nameinput, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		Setting2.add(password, c);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		Setting2.add(pwdinput, c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		Setting2.add(login, c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		Setting2.add(register, c);
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		Setting2.add(Unit, c);
		c.gridx = 1;
		c.gridy = 6;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		Setting2.add(UnitParameter, c);
		c.gridx = 2;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		Setting2.add(Save, c);
		c.gridy = 7;
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		Setting2.add(Syn, c);
		c.gridy = 7;
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		Setting2.add(Initialize, c);
		register.addActionListener(this);
		login.addActionListener(this);
		Save.addActionListener(this);
		Syn.addActionListener(this);
		Initialize.addActionListener(this);
	}
	
	
	
/****************************返回问题*****************************************************/	
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand().equals("Back"))
		{
			System.out.println("here to back");
			//Frame frame2 = new Frame();
			//learnword2.setVisible(false);
			//Setting2.setVisible(false);
			Frame frame = new Frame();
			setContentPane(frame.tp);
			repaint();
		}
		
		if (e.getActionCommand().equals("Begin"))
		{
			System.out.println("Begin to learn word");
			new Learnword();
			Begin.setEnabled(false);
			BrowseList.setEnabled(false);
			repaint();
		}
		
		if (e.getActionCommand().equals("Save"))
		{
			textFieldValue = UnitParameter.getText();
			System.out.println("You input:" + textFieldValue);
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
				if(!conn.isClosed())
					System.out.println("Succeeded connecting to the database(frame-Setting)");
				saveUnit = conn.createStatement();
				ResultSet result = saveUnit.executeQuery("select amount from configuration");
				while(result.next())
				{
					int number = (int) result.getInt(1);
					System.out.println(number);
				}
				saveUnit.executeUpdate("update configuration set amount = " + textFieldValue + " where lastbookid = 1");
				//saveUnit.executeUpdate("insert into configuration(amount) values (`" + textFieldValue + "`)");
				saveUnit.close();
				conn.close();
			}
			catch (Exception ex)
			{
				System.out.println("Error: " + ex.toString());
			}
			repaint();
		}
		
		if (e.getActionCommand().equals("Syn"))
		{
			
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
				if(!conn.isClosed())
					System.out.println("Succeeded connecting to the database(frame-Setting)");
				Synicate = conn.createStatement();
				ResultSet rs = Synicate.executeQuery("select bookid, lastremember from booklist where bookname like '" + Frame.choosedbook + "'");
				rs.next();
				System.out.println(rs.getInt(1) + rs.getInt(2));
				bookid = rs.getInt(1);
				lastremember = rs.getInt(2);
				rs.close();
				conn.close();
			}
			catch (Exception ex)
			{
				System.out.println("I am a Error: " + ex.toString());
			}
			try 
			{
				URL u = new URL(combineSync(username, password1, bookid, lastremember));
				InputStream in = u.openStream();
				BufferedReader bin = new BufferedReader(new InputStreamReader(in, "GB2312"));
				String result="";
				String s;
				while((s=bin.readLine())!=null){
					result+=s;
				}
				
				if(result.charAt(0)=='1')
				{
					JOptionPane.showMessageDialog(getContentPane(),"同步成功" ,"确认",JOptionPane.WARNING_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(getContentPane(),"同步失败" ,"确认",JOptionPane.WARNING_MESSAGE);
				}
				bin.close();
			}
			catch (MalformedURLException ex)
			{
				System.err.println(ex);
			}
			catch (IOException ex)
			{
				System.err.println(ex);
			}
			
			
			
			/*//从数据库导出csv文件
			System.out.println("you clicked Syn");
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
				if(!conn.isClosed())
					System.out.println("Succeeded connecting to the database(frame-Setting)");
				Synicate = conn.createStatement();
				for (int i = 0; i <= Frame.Wordbook.length; i++)
				{
*//****************************************已经存在文件问题*************************************************************************//*					
					Synicate.execute("select * from " + Frame.Wordbook[i] + " into outfile 'F:/" + Frame.Wordbook[i] + ".csv' fields terminated by ','");

				}
				Synicate.close();
				conn.close();
			}
			catch (Exception ex)
			{
				System.out.println("Error: " + ex.toString());
			}*/
		}
		
		
		if (e.getActionCommand().equals("Initialize"))
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
				if(!conn.isClosed())
					System.out.println("Succeeded connecting to the database(frame-Setting)");
				Statement delete = conn.createStatement();
				for (int i = 0; i <= Frame.Wordbook.length; i++)
				{
/****************************************已经存在文件问题*************************************************************************/					
					delete.execute("update booklist set lastremember = 0");
				}
				delete.close();
				conn.close();
			}
			catch (Exception ex)
			{
				System.out.println("Error: " + ex.toString());
			}
		}
		
		if (e.getActionCommand().equals("register"))
		{
			username = nameinput.getText();
			password1 = pwdinput.getText();
			System.out.println("you click register!");
			System.out.println(username + password1);
			
		}
		
		
		if (e.getActionCommand().equals("login"))
		{
			username = nameinput.getText();
			password1 = pwdinput.getText();
			System.out.println("you click login!");
			System.out.println(username + password1);
			
		}
		
		
		
	}
	public  String combineSync(String username, String psw, int listID, int wordID)
	{
		String combine = this.BASIC_ADDRESS + SYNC_INTERFACE + "?username=" + username + "&password=" +psw+APPID + "&bookID=" + listID + "&wordID=" + wordID;
		return combine;	
	}
	
	
	
}
