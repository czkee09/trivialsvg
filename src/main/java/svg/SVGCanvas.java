package svg;

import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javax.swing.*;

/**
 * Created by czk on 2014/11/15.
 *
 */
public class SVGCanvas extends JSVGCanvas {

    protected static String namespaceURI = "http://www.w3.org/1999/xlink";

    public SVGCanvas() {
        super();
    }

    public void traverseNode(Node node) {
        if (node != null) {
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) child;
                    if (element.hasAttributeNS(namespaceURI, "href")) {
                        String ref = element.getAttributeNS(namespaceURI, "href");
                        if (ref.startsWith("#rect_")) {
                            JOptionPane.showMessageDialog(null, ref);
                            ((EventTarget) element).addEventListener(SVGConstants.SVG_MOUSEEVENTS_EVENT_TYPE, new EventListener() {
                                @Override
                                public void handleEvent(Event evt) {
                                    JOptionPane.showMessageDialog(null, "You clicked the rectangular.");
                                }
                            }, false);

                        }
                    }
                }
                traverseNode(child);
            }
        }


    }
}
