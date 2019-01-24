

/*Swing Imports*/
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/*Abstract Window ToolKit imports */
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;



public class SimpleMiniWebBrowser extends JFrame implements HyperlinkListener {
  //Create instances for...
  private JButton buttonBack = new JButton("<"), buttonForward = new JButton(">");//...the Back button

  private JTextField locationTextField = new JTextField(35);//...the text field (35 chars)

  private JEditorPane displayEditorPane = new JEditorPane();//...the display panel

  private ArrayList pageList = new ArrayList(); //...and an ArrayList for pageList



  public SimpleMiniWebBrowser() {
    setSize(640, 480);//window size to 640px by 480px
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//what to do on close (default)
    JPanel bttnPanel = new JPanel();//Create a JPanel instance with refVar bttnPanel

    //back button's ActionListener
    buttonBack.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        backActn();
      }
    });

    //forward Button's ActionListener
    buttonBack.setEnabled(false);
    bttnPanel.add(buttonBack);
    buttonForward.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        forwardActn();
      }
    });

    //Setup The text Field
    buttonForward.setEnabled(false);
    bttnPanel.add(buttonForward);
    locationTextField.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          actionGo();
        }
      }
    });

    //bttnGo
    bttnPanel.add(locationTextField);
    JButton bttnGo = new JButton("GO");
    bttnGo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        actionGo();
      }
    });

    //Set Display Panel
    bttnPanel.add(bttnGo);
    displayEditorPane.setContentType("text/html");
    displayEditorPane.setEditable(false);
    displayEditorPane.addHyperlinkListener(this);

    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(bttnPanel, BorderLayout.NORTH);
    getContentPane().add(new JScrollPane(displayEditorPane), BorderLayout.CENTER);
  }//close SimpleMiniWebBrowser()



  //Navigate back action
  private void backActn() {
    URL currentUrl = displayEditorPane.getPage();
    int pageIndex = pageList.indexOf(currentUrl.toString());
    try {
      showPage(new URL((String) pageList.get(pageIndex - 1)), false);
    } catch (Exception e) {
    }
  }

  //Navigate forward action
  private void forwardActn() {
    URL currentUrl = displayEditorPane.getPage();
    int pageIndex = pageList.indexOf(currentUrl.toString());
    try {
      showPage(new URL((String) pageList.get(pageIndex + 1)), false);
    } catch (Exception e) {
    }
  }

  //Go action
  private void actionGo() {
    URL verifiedUrl = verifyUrl(locationTextField.getText());
    if (verifiedUrl != null) {
      showPage(verifiedUrl, true);
    } else {
      System.out.println("Invalid URL");
    }
  }


  //verify the URL
  private URL verifyUrl(String url) {
    if (!url.toLowerCase().startsWith("http://"))
      return null;

    URL verifiedUrl = null;
    try {
      verifiedUrl = new URL(url);
    } catch (Exception e) {
      return null;
    }

    return verifiedUrl;
  }

  //Display the page
  private void showPage(URL pageUrl, boolean addToList) {
    try {
      URL currentUrl = displayEditorPane.getPage();
      displayEditorPane.setPage(pageUrl);
      URL newUrl = displayEditorPane.getPage();
      if (addToList) {
        int listSize = pageList.size();
        if (listSize <= 0) {
          return;
        }
        int pageIndex = pageList.indexOf(currentUrl.toString());
        if (pageIndex >= listSize - 1) {
          return;
        }
        for (int i = listSize - 1; i > pageIndex; i--) {
          pageList.remove(i);
        }
        pageList.add(newUrl.toString());
      }
      locationTextField.setText(newUrl.toString());
      updateBttns();
    } catch (Exception e) {
      System.out.println("Unable to load page");
    }
  }//close showPage()




  //Update the Buttons
  private void updateBttns() {
    if (pageList.size() < 2) {
      buttonBack.setEnabled(false);
      buttonForward.setEnabled(false);
    } else {
      URL currentUrl = displayEditorPane.getPage();
      int pageIndex = pageList.indexOf(currentUrl.toString());
      buttonBack.setEnabled(pageIndex > 0);
      buttonForward.setEnabled(pageIndex < (pageList.size() - 1));
    }
  }

  //Update the links
  public void hyperlinkUpdate(HyperlinkEvent event) {
    HyperlinkEvent.EventType eventType = event.getEventType();
    if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
      if (event instanceof HTMLFrameHyperlinkEvent) {
        HTMLFrameHyperlinkEvent linkEvent = (HTMLFrameHyperlinkEvent) event;
        HTMLDocument document = (HTMLDocument) displayEditorPane.getDocument();
        document.processHTMLFrameHyperlinkEvent(linkEvent);
      } else {
        showPage(event.getURL(), true);
      }
    }
  }



  /*MAIN METHOD*/
  public static void main(String[] args) {
    SimpleMiniWebBrowser browser = new SimpleMiniWebBrowser();
    browser.setVisible(true);
  }//main()

}//Close Class--SimpleMiniWebBrowser
