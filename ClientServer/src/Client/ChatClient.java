package Client;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import common.Network;
import common.Network.ChatMessage;
import common.Network.RegisterName;
import common.Network.UpdateNames;

public class ChatClient {
  ChatFrame chatFrame;
  Client client;
  String name;

  public ChatClient () {
    client = new Client();
    client.start();

    // For consistency, the classes to be sent over the network are
    // registered by the same method for both the client and server.
    Network.register(client);

    client.addListener(new Listener() {
      @Override
      public void connected (final Connection connection) {
        final RegisterName registerName = new RegisterName();
        registerName.name = name;
        client.sendTCP(registerName);
      }

      @Override
      public void received (final Connection connection, final Object object) {
        if (object instanceof UpdateNames) {
          final UpdateNames updateNames = (UpdateNames)object;
          chatFrame.setNames(updateNames.names);
          return;
        }

        if (object instanceof ChatMessage) {
          final ChatMessage chatMessage = (ChatMessage)object;
          chatFrame.addMessage(chatMessage.text);
          return;
        }
      }

      @Override
      public void disconnected (final Connection connection) {
        EventQueue.invokeLater(new Runnable() {
          @Override
          public void run () {
            // Closing the frame calls the close listener which will stop the client's update thread.
            chatFrame.dispose();
          }
        });
      }
    });

    // Request the host from the user.
    String input = (String)JOptionPane.showInputDialog(null, "Host:", "Connect to chat server", JOptionPane.QUESTION_MESSAGE,
        null, null, "localhost");
    if ((input == null) || (input.trim().length() == 0)) {
      System.exit(1);
    }
    final String host = input.trim();

    // Request the user's name.
    input = (String)JOptionPane.showInputDialog(null, "Name:", "Connect to chat server", JOptionPane.QUESTION_MESSAGE, null,
        null, "Test");
    if ((input == null) || (input.trim().length() == 0)) {
      System.exit(1);
    }
    name = input.trim();

    // All the ugly Swing stuff is hidden in ChatFrame so it doesn't clutter the KryoNet example code.
    chatFrame = new ChatFrame(host);
    // This listener is called when the send button is clicked.
    chatFrame.setSendListener(new Runnable() {
      @Override
      public void run () {
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.text = chatFrame.getSendText();
        client.sendTCP(chatMessage);
      }
    });
    // This listener is called when the chat window is closed.
    chatFrame.setCloseListener(new Runnable() {
      @Override
      public void run () {
        client.stop();
      }
    });
    chatFrame.setVisible(true);

    // We'll do the connect on a new thread so the ChatFrame can show a progress bar.
    // Connecting to localhost is usually so fast you won't see the progress bar.
    new Thread("Connect") {
      @Override
      public void run () {
        try {
          client.connect(5000, host, Network.port);
          // Server communication after connection can go here, or in Listener#connected().
        } catch (final IOException ex) {
          ex.printStackTrace();
          System.exit(1);
        }
      }
    }.start();
  }

  static private class ChatFrame extends JFrame {
    CardLayout cardLayout;
    JProgressBar progressBar;
    JList messageList;
    JTextField sendText;
    JButton sendButton;
    JList nameList;

    public ChatFrame (final String host) {
      super("Chat Client");
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setSize(640, 200);
      setLocationRelativeTo(null);

      final Container contentPane = getContentPane();
      cardLayout = new CardLayout();
      contentPane.setLayout(cardLayout);
      {
        final JPanel panel = new JPanel(new BorderLayout());
        contentPane.add(panel, "progress");
        panel.add(new JLabel("Connecting to " + host + "..."));
        {
          panel.add(progressBar = new JProgressBar(), BorderLayout.SOUTH);
          progressBar.setIndeterminate(true);
        }
      }
      {
        final JPanel panel = new JPanel(new BorderLayout());
        contentPane.add(panel, "chat");
        {
          final JPanel topPanel = new JPanel(new GridLayout(1, 2));
          panel.add(topPanel);
          {
            topPanel.add(new JScrollPane(messageList = new JList()));
            messageList.setModel(new DefaultListModel());
          }
          {
            topPanel.add(new JScrollPane(nameList = new JList()));
            nameList.setModel(new DefaultListModel());
          }
          final DefaultListSelectionModel disableSelections = new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval (final int index0, final int index1) {
            }
          };
          messageList.setSelectionModel(disableSelections);
          nameList.setSelectionModel(disableSelections);
        }
        {
          final JPanel bottomPanel = new JPanel(new GridBagLayout());
          panel.add(bottomPanel, BorderLayout.SOUTH);
          bottomPanel.add(sendText = new JTextField(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
              GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
          bottomPanel.add(sendButton = new JButton("Send"), new GridBagConstraints(1, 0, 1, 1, 0, 0,
              GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));
        }
      }

      sendText.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed (final ActionEvent e) {
          sendButton.doClick();
        }
      });
    }

    public void setSendListener (final Runnable listener) {
      sendButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed (final ActionEvent evt) {
          if (getSendText().length() == 0) {
            return;
          }
          listener.run();
          sendText.setText("");
          sendText.requestFocus();
        }
      });
    }

    public void setCloseListener (final Runnable listener) {
      addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed (final WindowEvent evt) {
          listener.run();
        }

        @Override
        public void windowActivated (final WindowEvent evt) {
          sendText.requestFocus();
        }
      });
    }

    public String getSendText () {
      return sendText.getText().trim();
    }

    public void setNames (final String[] names) {
      // This listener is run on the client's update thread, which was started by client.start().
      // We must be careful to only interact with Swing components on the Swing event thread.
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run () {
          cardLayout.show(getContentPane(), "chat");
          final DefaultListModel model = (DefaultListModel)nameList.getModel();
          model.removeAllElements();
          for (final String name : names) {
            model.addElement(name);
          }
        }
      });
    }

    public void addMessage (final String message) {
      EventQueue.invokeLater(new Runnable() {
        @Override
        public void run () {
          final DefaultListModel model = (DefaultListModel)messageList.getModel();
          model.addElement(message);
          messageList.ensureIndexIsVisible(model.size() - 1);
        }
      });
    }
  }

  public static void main (final String[] args) {
    Log.set(Log.LEVEL_DEBUG);
    new ChatClient();
  }
}
