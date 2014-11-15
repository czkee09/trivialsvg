package svg;

/**
 * Created by Administrator on 14-10-22.
 * @see http://xmlgraphics.apache.org/batik/using/swing.html
 */

import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;


public class SVGApplication {

    public static void main(String[] args) {
        // Create a new JFrame.
        JFrame f = new JFrame("Batik");
        SVGApplication app = new SVGApplication(f);

        // Add components to the frame.
        f.getContentPane().add(app.createComponents());
        // Display the frame.
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setSize(400, 400);
        f.setVisible(true);
    }

    // The frame.
    protected JFrame frame;

    // The "Load" button, which displays up a file chooser upon clicking.
    protected JButton button = new JButton("Load...");

    // The status label.
    protected JLabel label = new JLabel();

    // add user agent
    // 注：
    // 1. SVGUserAgentAdapter中openLink方法没有执行任何操作
    // 2. SVGUserAgentGUIAdapter继承了SVGUserAgentAdapter类，且没有重写openLink方法
    // 3. 故在此处用一个新的SVGUserAgentGUIAdapter实例作为参数，构造出一个JSVGCanvass，
    //    该JSVGCanvas不会对SVG图的链接作出相应，可以避免用户误操作，屏蔽因外链无效产生的错误信息
    protected SVGUserAgent svgUserAgent = new SVGUserAgentGUIAdapter(this.frame);

    // The SVG canvas.
    // SVGCanvas是在原有的JSVGCanvas的基础上增加遍历文档节点的方法后得到的新类
    protected SVGCanvas svgCanvas = new SVGCanvas();  // here we use MySvgCanvas instead of JSVGCanvas
//    protected MySvgCanvas svgCanvas = new MySvgCanvas(svgUserAgent, true, true);


    public SVGApplication(JFrame f) {
        frame = f;
    }

    public JComponent createComponents() {
        // Create a panel and add the button, status label and the SVG canvas.
        final JPanel panel = new JPanel(new BorderLayout());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(button);
        p.add(label);

        panel.add("North", p);
        panel.add("Center", svgCanvas);

        // Set the button action.
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fc = new JFileChooser(".");
                int choice = fc.showOpenDialog(panel);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    try {
                        svgCanvas.setURI(f.toURL().toString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Set the JSVGCanvas listeners.
        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            public void documentLoadingStarted(SVGDocumentLoaderEvent e) {
                label.setText("Document Loading...");
            }
            // 此处加入了psgraph.app.MyViewerApp类中documentLoadingCompleted方法的代码

            /**
             * Called when the loading of a document was completed.
//             * @see psgraph.app.MyViewerApp
             */
            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
//                if (debug) {
//                    System.out.print(resources.getString("Message.documentLoadTime"));
//                    System.out.println((System.currentTimeMillis() - time) + " ms");
//                }
//                setSVGDocument(e.getSVGDocument(), e.getSVGDocument().getURL(), e.getSVGDocument().getTitle());
                SVGDocument doc = e.getSVGDocument();
                Element svg = doc.getDocumentElement();

                // Make the text look nice.
                svg.setAttributeNS(null, "text-rendering", "geometricPrecision");

                // Remove the xml-stylesheet PI.
                for (Node n = svg.getPreviousSibling();
                     n != null;
                     n = n.getPreviousSibling()) {
                    if (n.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                        doc.removeChild(n);
                        break;
                    }
                }
                svgCanvas.traverseNode(svg);
            }
            //加入的代码结束，注释的部分是原例程的代码
//            public void documentLoadingCompleted(SVGDocumentLoaderEvent e) {
//                label.setText("Document Loaded.");
//            }
        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            public void gvtBuildStarted(GVTTreeBuilderEvent e) {
                label.setText("Build Started...");
            }

            public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
                label.setText("Build Done.");

                frame.pack();
            }
        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            public void gvtRenderingPrepare(GVTTreeRendererEvent e) {
                label.setText("Rendering Started...");
            }

            public void gvtRenderingCompleted(GVTTreeRendererEvent e) {
                label.setText("");
            }
        });

        return panel;
    }
}
