package chat_v6;


import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;





public class severGUI extends JFrame {
	
	
	public severGUI(){
		
		JPanel jp = new JPanel();
		add(jp);
		jp.setLayout(new GridLayout());
	
	
		 ShowServer jpmyfirstgui = new ShowServer();
	     	jp.add(jpmyfirstgui);
	
	
	
	
	setTitle("SERVER");
	setSize(500,400);
	setVisible(true);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
}
	

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				
			///	severGUI gui = new severGUI();
			}

		});
	}

}
