import java.awt.*;
import java.awt.event.*;
import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;

public class Learnword extends WindowAdapter implements ActionListener 
{
	static final int WIDTH = 30*10;
	static final int HEIGHT = 40*10;
	JLabel label2;
	JLabel label1;
	Connection conn;
	Statement showworddetail;
	Statement update;
	Statement recordlastremembered;
	String currentwordid;
	int currentid = 1;
	int previousid = 1;
	String[] currentworddetail;
	String learnedbook = Frame.choosedbook;
	public Learnword() 
	{
		JFrame learnframe = new JFrame("学习单词");	
		learnframe.setSize(WIDTH,HEIGHT);
		learnframe.setVisible(true);
		learnframe.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		JButton Correct = new JButton("Correct");
		Correct.addActionListener(this);
		JButton Wrong = new JButton("Wrong");
		Wrong.addActionListener(this);
		label1 = new JLabel();
		label2 = new JLabel();
		label2.setVisible(false);
		JButton Reveal = new JButton("Reveal");
		Reveal.addActionListener(this);
		
		//Correct button & Wrong button location
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.WEST;
		learnframe.add(Correct, c);
		c.anchor = GridBagConstraints.EAST;
		learnframe.add(Wrong, c);
		c.anchor = GridBagConstraints.CENTER;
		learnframe.add(Reveal, c);
		
		//单词释义的布局
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 100;
		//如果组件没有填充整个区域,可以通过设置anchor域指定其位置
		c.fill = GridBagConstraints.BOTH;
		learnframe.add(label1, c);
		c.gridy = 1;
		learnframe.add(label2, c);
		
		
		learnframe.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.out.println("wo yao tuichu le ");
				try
				{
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
					if(!conn.isClosed())
						System.out.println("Succeeded connecting to the database(Learnwordbord)");
					recordlastremembered = conn.createStatement();
					//在mysql语句总引用java变量
					System.out.println("The book you learn is:" + learnedbook + "; The current id is:" + currentid);
					recordlastremembered.execute("update booklist set lastremember =" + currentid + " where bookname like '" + learnedbook + "'");
					recordlastremembered.close();
					conn.close();
					Browseword.Begin.setEnabled(true);
					Browseword.BrowseList.setEnabled(true);
				}
				catch (Exception ex)
				{
					System.out.println("I am Error, too: " + ex.toString());
				}
				
			}
		});
		
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
			if(!conn.isClosed())
				System.out.println("Succeeded connecting to the database(Learnwordbord)");
			Statement check = conn.createStatement();
			showworddetail = conn.createStatement();
			//在mysql语句总引用java变量
			//System.out.println("The book you learn is:" + learnedbook + "; The current id is:" + currentid);
			ResultSet result = check.executeQuery("select lastremember from booklist where bookname like '" + learnedbook + "'");
			result.next();
			System.out.println(result.getInt(1));
			if (result.getInt(1) >= 1)
			{
				currentid = result.getInt(1);
			}
			ResultSet rs = showworddetail.executeQuery("select * from " + learnedbook + " where id=" + currentid + "");
			ResultSetMetaData metaData = rs.getMetaData();
			int numberOfColumns = metaData.getColumnCount();
			currentworddetail = new String[1];
			int i = 0;
			while(rs.next())
			{
				for (int z =1; z <= numberOfColumns; z++)
				{
					System.out.printf("%-8s\t", rs.getObject(z));
				}
	/****************************************排列问题,存储在数组中***********************************************************************************/				
				currentworddetail[i] =  rs.getString(1)+ "          " + rs.getString(2) + "          " + rs.getString(3);
				label1.setText(rs.getString(1)+ "\n" + rs.getString(2) + "\n");
				label2.setText(rs.getString(3));
				i++;
			}	
			rs.close();
			result.close();
			check.close();
			showworddetail.close();
			conn.close();
		}
		catch (Exception ex)
		{
			System.out.println("I am Error, too: " + ex.toString());
		}
	}
	
	
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand().equals("Reveal"))
		{
			System.out.println("I had clicked the Reveal button");
			label2.setVisible(true);
		}
		
		if (e.getActionCommand().equals("Correct"))
		{
			if (currentid >= 9999)
			{
				
			}
			else
			{
				label1.setText("");
				label2.setText("");
				previousid = currentid;
				currentid++;
				try
				{
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
					if(!conn.isClosed())
						System.out.println("Succeeded connecting to the database(learnword-Correct)");
					showworddetail = conn.createStatement();
					System.out.println("current id: "+currentid);
					System.out.println("previous id: "+previousid);
					ResultSet rs = showworddetail.executeQuery("select * from " + learnedbook + " where  id = "+currentid+"");
					//update.executeUpdate("update " + learnedbook + " set remembered = 1 where id = "+previousid+"");
					while(rs.next())
					{		
						label1.setText(rs.getString(1)+ "\n"+"\n" +"\n" + rs.getString(2));
						label2.setText(rs.getString(3)+"\n"+"\n");
						label2.setVisible(false);
					}
					rs.close();
					showworddetail.close();
					update = conn.createStatement();
					update.executeUpdate("update " + learnedbook + " set remembered = 1 where id = "+previousid+"");
					update.close();
					conn.close();
				}
				catch (Exception ex)
				{
					System.out.println("Error: " + ex.toString());
				}
			}
		}
		
		if (e.getActionCommand().equals("Wrong"))
		{
			if (currentid >= 9999)
			{
				
			}
			else
			{
				label1.setText("");
				label2.setText("");
				previousid = currentid;
				currentid++;
				try
				{
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
					if(!conn.isClosed())
						System.out.println("Succeeded connecting to the database(learnword-Wrong)");
					showworddetail = conn.createStatement();
					System.out.println(currentid);		
					ResultSet rs = showworddetail.executeQuery("select * from " + learnedbook + " where  id = "+currentid+"");
					
					while(rs.next())
					{		
						label1.setText(rs.getString(1)+ "\n"+"\n" +"\n" + rs.getString(2));
						label2.setText(rs.getString(3)+"\n"+"\n");
						label2.setVisible(false);
					}
					rs.close();
					showworddetail.close();
					update = conn.createStatement();
					update.executeUpdate("update " + learnedbook + " set remembered = 0 where id = "+previousid+"");
					update.close();
					conn.close();
				}
				catch (Exception ex)
				{
					System.out.println("I am a Error: " + ex.toString());
				}
			}
		}
	}
	

}
