import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.BorderLayout;


public class client extends JFrame {

    Socket socket;
    BufferedReader br; // for reading
    PrintWriter out; // for writing

    // Declare component
    private JLabel heading=new JLabel("Client Area");
    private JTextArea messageArea=new JTextArea();
    private JTextField messageInput=new JTextField();
    private Font font=new Font("Roboto",Font.PLAIN,20);

    // Constructor
    public client()
    {
        try {
            System.out.println("sending to request");
            socket=new Socket("127.0.0.1",7771);
            System.out.println("connection done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            createGUI();
            handleEvents();
            startReading();
            //startWriting();
        }
        catch (Exception e)
        {
           // e.printStackTrace();
        }

    }

    private void handleEvents()
    {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e)
            {

            }

            @Override
            public void keyPressed(KeyEvent e)
            {

            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                //System.out.println("key released" +e.getKeyCode());
                if (e.getKeyCode() ==10)
                {
                    String contentToSend=messageInput.getText();
                    messageArea.append("Me : " +contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    private void createGUI()
    {
        this.setTitle("Client Messager[END]");
        this.setSize(500,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //coding for component
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("chaticon.png"));

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        //
        messageArea.setEditable(false);
        this.setLayout(new BorderLayout());
        //
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);


        this.setVisible(true);
    }


    // Reading method
    public void startReading(){
        //thread data ko read karke deta rhega
        Runnable  r1=()->{
            System.out.println("reader started");
            try {
            while (true){

                    String msg=br.readLine();
                    if(msg.equals("exit"))
                    {
                        System.out.println("server terminated the chat");
                        JOptionPane.showMessageDialog(this,"Server terminated the socket");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    //System.out.println("Server:-" +msg);
                    messageArea.append("Server : " + msg + "\n");
                }

            }
             catch (Exception e)
            {
               // e.printStackTrace();
                System.out.println("connection is closed");
            }
        };
        new Thread(r1).start();
    }
    // Writing method
    public void startWriting()
    {
        // thread data user se lega or send krega clientr tak
        Runnable r2=()->{
            System.out.println("writer started");
            try {
            while (true && !socket.isClosed())
            {

                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content=br1.readLine();
                    out.println(content);
                    out.flush();

                    if (content.equals("exit")){
                        socket.close();
                        break;
                    }
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args)
    {
        System.out.println("thi is client site");
        new client();
    }
}
