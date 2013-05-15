import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;
import com.alibaba.fastjson.*;
public class Frame extends JFrame implements ActionListener
{
	public class SyncObject 
	{  
	    public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public int getBookID() {
			return bookID;
		}
		public void setBookID(int bookID) {
			this.bookID = bookID;
		}
		public int getWordID() {
			return wordID;
		}
		public void setWordID(int wordID) {
			this.wordID = wordID;
		}
		public String getError() {
			return error;
		}
		public void setError(String error) {
			this.error = error;
		}
		private int status;  
	    private int bookID;  
	    private int wordID; 
	    private String error;
	    
	}  
	
	
	
	
	
	
	
	
	public static final String BASIC_ADDRESS ="http://192.168.1.103/word/CGI/";
	public static final String APPID="&appID=MEMORYCGI";
	private static  final String REG_INTERFACE="register_CGI.php";
	private static  final String LOGIN_INTERFACE="login_CGI.php";
	private static  final String SYNC_INTERFACE="sync_CGI.php";
	private static int bookID = 0;
	private static int wordID = 0;
	private static final int DEFAULT_WIDTH = 65*10;
	private static final int DEFAULT_HEIGHT = 70*10;
	//从数据库中取出wordbook
	static String[] Wordbook;
	JList List;
	static JTabbedPane tp = new JTabbedPane();
	//使用者在List中选择他要学习的书
	static String choosedbook;
	Connection conn;
	Statement showbooklist;
	Statement saveUnit;
	Statement Synicate;
	Statement Synicate1;
	JTextField UnitParameter;
	String textFieldValue;
	String password1;
	String username;
	JPasswordField pwdinput;
	JTextField nameinput;
	int bookid;
	int lastremember;
	JButton Syn;
	static JPanel learnword;
	static JPanel Setting;
	
	private String combineReg(String username,String psw)
	{
		String combine = this.BASIC_ADDRESS + REG_INTERFACE + "?username=" + username + "&password=" +psw+APPID;
		return combine;	
	}
	
	private String combineLogin(String username, String psw)
	{
		String combine = this.BASIC_ADDRESS + LOGIN_INTERFACE + "?username=" + username + "&password=" +psw+APPID;
		return combine;	
	}
	
	public  String combineSync(String username, String psw, int listID, int wordID)
	{
		String combine = this.BASIC_ADDRESS + SYNC_INTERFACE + "?username=" + username + "&password=" +psw+APPID + "&bookID=" + listID + "&wordID=" + wordID;
		return combine;	
	}
	
	public Frame()
	{
		setTitle("IWord");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setResizable(true);
		//创建一个选项卡容器,将之添加到顶层容器内
		
		setContentPane(tp);
		learnword = new JPanel();
		Setting = new JPanel();
		tp.addTab("Learn", learnword);
		tp.addTab("Setting", Setting);
		tp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		
		//Learn选项卡界面,使用GridBagLayout布局
		learnword.setLayout(new GridBagLayout());
		learnword.setOpaque(true);
		GridBagConstraints c = new GridBagConstraints();
		//Refresh按钮布局
		JButton Refresh = new JButton("Refresh");
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		//如果组件没有填充整个区域,可以通过设置anchor域指定其位置
		c.anchor = GridBagConstraints.WEST;
		//c.anchor = GridBagConstraints.NORTH;
		learnword.add(Refresh, c);
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
		
		
		//连接数据库
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
			if(!conn.isClosed())
				System.out.println("Succeeded connecting to the database(frame)");
			showbooklist = conn.createStatement();
			ResultSet rs = showbooklist.executeQuery("select bookname from booklist");
/**********************************最好改为动态创建********************************************************************/			
			Wordbook = new String[3];
			int i = 0;
			while(rs.next())
			{
				Wordbook[i] = rs.getString(1);
				i++;
			}
			rs.close();
			showbooklist.close();
			conn.close();
		}
		catch (Exception ex)
		{
			System.out.println("Error: " + ex.toString());
		}
		
		
		//把wordbook添加到list中
		List = new JList(Wordbook);
		List.setBorder(BorderFactory.createTitledBorder("请选择一本书: "));
		List.addListSelectionListener(new ListSelectionListener()
		{
/**************************选中高亮问题****************************************************************/			
			public void valueChanged(ListSelectionEvent e)
			{
				choosedbook = (String) List.getSelectedValue();
				System.out.println(choosedbook);
				Browseword browseword = new Browseword();
				setContentPane(browseword.browsetp);
				repaint();
			}
		});
		
		//一次只能选择一个列表索引
		List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		learnword.add(new JScrollPane (List),c);
		
		
		
		//Setting选项卡界面
		JLabel Unit = new JLabel("Unit: ");
		UnitParameter = new JTextField(10);
		JButton Save = new JButton("Save");
		Syn= new JButton("Syn");
		Syn.setVisible(false);
		JButton Initialize= new JButton("Initialize");
		JLabel name = new JLabel("name :");
		JLabel password = new JLabel("password :");
		nameinput = new JTextField(15);
		pwdinput = new JPasswordField(15);
		
		
		
		
		JButton login = new JButton("login");
		JButton register = new JButton("register");
		Setting.setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		Setting.add(name, c);
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		Setting.add(nameinput, c);
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		Setting.add(password, c);
		c.gridx = 1;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		Setting.add(pwdinput, c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		Setting.add(login, c);
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		Setting.add(register, c);
		c.gridx = 0;
		c.gridy = 6;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		Setting.add(Unit, c);
		c.gridx = 1;
		c.gridy = 6;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.CENTER;
		Setting.add(UnitParameter, c);
		c.gridx = 2;
		c.gridy = 6;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		Setting.add(Save, c);
		c.gridy = 7;
		c.gridx = 1;
		c.anchor = GridBagConstraints.EAST;
		Setting.add(Syn, c);
		c.gridy = 7;
		c.gridx = 1;
		c.anchor = GridBagConstraints.WEST;
		Setting.add(Initialize, c);
		register.addActionListener(this);
		login.addActionListener(this);
		Save.addActionListener(this);
		Syn.addActionListener(this);
		Initialize.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) 
	{
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
				
				ResultSet rs = Synicate.executeQuery("select bookid, lastremember from booklist where bookname like '" + choosedbook + "'");
				rs.next();
				System.out.println("dasdsa"+rs.getInt(1) + rs.getInt(2));
				bookid = rs.getInt(1);
				lastremember = rs.getInt(2);
				rs.close();
				Synicate.close();
				conn.close();
			}
			catch (Exception ex)
			{
				System.out.println("I am a Error11111: " + ex.toString());
			}
			try 
			{
				
				URL u = new URL(this.combineSync(username, password1, bookid, lastremember));
				InputStream in = u.openStream();
				BufferedReader bin = new BufferedReader(new InputStreamReader(in, "GB2312"));
				String result="";
				String s;
				while((s=bin.readLine())!=null){
					result+=s;
				}
				bin.close();
				
				System.out.println(result);
			
				//SyncObject so = 
				JSONObject jo = JSON.parseObject(result);
			
				System.out.println(jo.getIntValue("status"));
				System.out.println(jo.getIntValue("bookID"));
				System.out.println(jo.getIntValue("wordID"));
				System.out.println(jo.getIntValue("error"));
				
				if(jo.getIntValue("status")==1)
				{
					//so.getBookID();
					//so.getWordID();
					try
					{
						Class.forName("com.mysql.jdbc.Driver");
						conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
						if(!conn.isClosed())
							System.out.println("Succeeded connecting to the database(frame-Setting)");
						Synicate1 = conn.createStatement();
						
						//choosedbook = "gre";
						//String sqls="Replace into booklist  values("+so.getBookID()+","+""+",";
						 //Synicate.executeQuery("Replace into booklist ");
						//recordlastremembered.execute("update booklist set lastremember =" + currentid + " where bookname like '" + learnedbook + "'");
						Synicate1.execute("update booklist set lastremember = " + jo.getIntValue("wordID") + " where bookid = " +jo.getIntValue("bookID") + "");
						//saveUnit.executeUpdate("update configuration set amount = " + textFieldValue + " where lastbookid = 1");
						/*rs.next();
						System.out.println("dasdsa"+rs.getInt(1) + rs.getInt(2));
						bookid = rs.getInt(1);
						lastremember = rs.getInt(2);*/
						//rs.close();
						Synicate1.close();
						conn.close();
					}
					catch (Exception ex)
					{
						System.out.println("I am a Error: " + ex.toString());
					}
					
					JOptionPane.showMessageDialog(getContentPane(),"同步成功" ,"确认",JOptionPane.WARNING_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(getContentPane(),"同步失败!\n"+jo.getIntValue("error") ,"确认",JOptionPane.WARNING_MESSAGE);
				}
				
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
				for (int i = 0; i <= Wordbook.length; i++)
				{
*//****************************************已经存在文件问题*************************************************************************//*					
					Synicate.execute("select * from " + Wordbook[i] + " into outfile 'F:/" + Wordbook[i] + ".csv' fields terminated by ','");

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
				for (int i = 0; i <= Wordbook.length; i++)
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
			password1 = new String(pwdinput.getPassword());
			System.out.println("you click register!");
			System.out.println(username + password1);
			try 
			{
				
				if (username.equals("")||password1.equals(""))
				{
					JOptionPane.showMessageDialog(getContentPane(),"不能为空" ,"确认",JOptionPane.WARNING_MESSAGE);
				}else{
					URL u = new URL(this.combineReg(username, password1));
					InputStream in = u.openStream();
					BufferedReader bin = new BufferedReader(new InputStreamReader(in, "GB2312"));
					String result="";
					String s;
					while((s=bin.readLine())!=null){
						result+=s;
					}
					
					if(result.length()!=0||result.charAt(0)=='1')
					{
						JOptionPane.showMessageDialog(getContentPane(),"注册成功" ,"确认",JOptionPane.WARNING_MESSAGE);
					}else{
						JOptionPane.showMessageDialog(getContentPane(),"注册失败" ,"确认",JOptionPane.WARNING_MESSAGE);
					}
					bin.close();
				}
			}
			catch (MalformedURLException ex)
			{
				System.err.println(ex);
			}
			catch (IOException ex)
			{
				System.err.println(ex);
			}
		}
		
		if (e.getActionCommand().equals("logout"))
		{
			JOptionPane.showMessageDialog(getContentPane(),"退出成功" ,"确认",JOptionPane.WARNING_MESSAGE);
			nameinput.setEditable(true);
			pwdinput.setEditable(true);
			((JButton)e.getSource()).setText("login");
			
		}
		
		
		
		if (e.getActionCommand().equals("login"))
		{
			username = nameinput.getText();
			password1 = new String(pwdinput.getPassword());
			System.out.println("you click register!");
			System.out.println(username + password1);
			try 
			{
				if (username.equals("")||password1.equals(""))
				{
					JOptionPane.showMessageDialog(getContentPane(),"不能为空" ,"确认",JOptionPane.WARNING_MESSAGE);
				}else{
					URL u = new URL(this.combineLogin(username, password1));
					InputStream in = u.openStream();
					BufferedReader bin = new BufferedReader(new InputStreamReader(in, "GB2312"));
					String result="";
					String s;
					while((s=bin.readLine())!=null){
						result+=s;
					}
					System.out.println(result);
					if(result.charAt(0)=='1')
					{
						JOptionPane.showMessageDialog(getContentPane(),"登入成功" ,"确认",JOptionPane.WARNING_MESSAGE);
						Syn.setVisible(true);
						((JButton)e.getSource()).setText("logout");
						nameinput.setEditable(false);
						pwdinput.setEditable(false);
					}else{
						JOptionPane.showMessageDialog(getContentPane(),"登入失败" ,"确认",JOptionPane.WARNING_MESSAGE);
					}
					bin.close();
				}
			}
			catch (MalformedURLException ex)
			{
				System.err.println(ex);
			}
			catch (IOException ex)
			{
				System.err.println(ex);
			}
		}
		
	}
}
