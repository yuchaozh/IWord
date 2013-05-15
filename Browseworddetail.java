import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import java.sql.*;
public class Browseworddetail extends JFrame implements ActionListener
{
	static final int WIDTH = 30*10;
	static final int HEIGHT = 40*10;
	String browsedword = Browseword.choosedword;
	String browsedbook = Frame.choosedbook;
	String browsedwordid = "";
	Connection conn;
	Statement showworddetail;
	int currentid;
	JTextArea ta;
	public Browseworddetail()
	{
		JFrame browseframe = new JFrame("浏览单词");	
		browseframe.setSize(WIDTH,HEIGHT);
		browseframe.setVisible(true);
		browseframe.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		ta = new JTextArea();
		ta.setEditable(false);
		JButton previous = new JButton("Previous");
		previous.addActionListener(this);
		JButton next = new JButton("Next");
		next.addActionListener(this);
		
		//previous button & next button location
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.WEST;
		browseframe.add(previous, c);
		c.anchor = GridBagConstraints.EAST;
		browseframe.add(next, c);
		
		//单词释义的布局
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 100;
		c.weighty = 100;
		//如果组件没有填充整个区域,可以通过设置anchor域指定其位置
		c.fill = GridBagConstraints.BOTH;
		browseframe.add(ta, c);
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
			if(!conn.isClosed())
				System.out.println("Succeeded connecting to the database(browsewordbord)");
			showworddetail = conn.createStatement();
			System.out.println("You are browsing: ");
			String[] splitvalue = browsedword.split("   ");
			for (int i = 0; i < splitvalue.length; i++)
			{
				System.out.println(splitvalue[i]);
				ta.append(splitvalue[i]);
				ta.append("\n");
			}
			browsedwordid = splitvalue[0];
			currentid = 0;
			//在mysql语句总引用java变量
			System.out.println("the book is:" + browsedbook + " the word is:" + browsedwordid);
					
			ResultSet rs = showworddetail.executeQuery("select id from " + browsedbook + " where  wordid like '"+browsedwordid+"'");
			//ResultSet rs = showworddetail.executeQuery("select id from " + browsedbook + " where  wordid like 'abate'");
			while(rs.next())
			{
				currentid = rs.getInt(1);
			}
			System.out.println("~~~~~~~~~~~~~~~~~~~~The current id is:  ");
			System.out.println(currentid);
			
			rs.close();
			showworddetail.close();
			conn.close();
		}
		catch (Exception ex)
		{
			System.out.println("Error: " + ex.toString());
		}
	}
	
	
	
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand().equals("Previous"))
		{
			if (currentid <= 1)
			{
				
			}
			else
			{
				ta.setText("");
				currentid--;
				try
				{
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
					if(!conn.isClosed())
						System.out.println("Succeeded connecting to the database(browsewordbord-previous)");
					showworddetail = conn.createStatement();
					System.out.println(currentid);		
					//ResultSet rs = showworddetail.executeQuery("select * from " + browsedbook + " where  id = '"+currentid+"'");
					ResultSet rs = showworddetail.executeQuery("select * from " + browsedbook + " where  id = "+currentid+"");

					while(rs.next())
					{		
						ta.append(rs.getString(1)+ "\n"+"\n" +"\n"+ rs.getString(2) + "\n"+"\n" +"\n"+ rs.getString(3)+"\n"+"\n");
					}
					rs.close();
					showworddetail.close();
					conn.close();
				}
				catch (Exception ex)
				{
					System.out.println("Error: " + ex.toString());
				}
			}
		}
		
		if (e.getActionCommand().equals("Next"))
		{
			if (currentid >= 9999)
			{
				
			}
			else
			{
				ta.setText("");
				currentid++;
				try
				{
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/iword","root","123456");
					if(!conn.isClosed())
						System.out.println("Succeeded connecting to the database(browsewordbord-next)");
					showworddetail = conn.createStatement();
					System.out.println(currentid);		
					ResultSet rs = showworddetail.executeQuery("select * from " + browsedbook + " where  id = "+currentid+"");
					while(rs.next())
					{		
						ta.append(rs.getString(1)+ "\n"+"\n" +"\n"+ rs.getString(2) + "\n"+"\n" +"\n"+ rs.getString(3)+"\n"+"\n");
					}
					rs.close();
					showworddetail.close();
					conn.close();
				}
				catch (Exception ex)
				{
					System.out.println("Error: " + ex.toString());
				}
			}
		}
	}

}
