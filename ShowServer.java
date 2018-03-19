package chat_v6;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ShowServer   extends JPanel  implements ActionListener{
	
	JButton jb1, jb2, jb3, jb4;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextArea textarea;

	
	 public ShowServer(){
	
	//setLayout(new GridLayout(3,1));  /// for # are TO THE row  and the other the coloms
	
	
	jb1 = new JButton("sendMsg");
	jb1.setBounds(10, 220, 72, 23);
	jb1.addActionListener(this);
    setLayout(null);
    add(jb1);
	
	jb2 = new JButton("privateMsg");
	jb2.setBounds(10, 277, 72, 23);
	jb2.addActionListener(this);
	add(jb2);
	
	jb3 = new JButton("Private_ENCR");
	jb3.setBounds(229, 220, 77, 23);
	jb3.addActionListener(this);
	add(jb3);
	
	
	jb4 = new JButton("Disconnect");
	jb4.setBounds(229, 277, 77, 23);
	jb4.addActionListener(this);
	add(jb4);
	
	 JTextArea displayArea = new JTextArea(10,20);
	 displayArea.setBounds(27, 11, 430, 184);
		add(displayArea);
		
		textField = new JTextField();
		textField.setBounds(89, 221, 130, 22);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(89, 278, 130, 22);
		add(textField_1);
		textField_1.setColumns(10);
		
		textField_2 = new JTextField();
		textField_2.setBounds(316, 221, 131, 22);
		add(textField_2);
		textField_2.setColumns(10);
		
		textField_3 = new JTextField();
		textField_3.setBounds(316, 278, 132, 22);
		add(textField_3);
		textField_3.setColumns(10);

}
	 public void writeToShowServer(String text){
		 textarea.append(text);
	 }


	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}



