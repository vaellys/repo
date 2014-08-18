package cn.zthz.tool.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.zthz.actor.rest.ErrorCodes;

public class XmlUtils {

	public static void putList(Map<String, List<String>> result, String key, String value) {
		if (result.containsKey(key)) {
			result.get(key).add(value);
		} else {
			List<String> values = new LinkedList<>();
			values.add(value);
			result.put(key, values);
		}
	}

	public static Map<String, List<String>> parseXml(String xml) throws HzException{
		try {

			final Map<String, List<String>> result = new HashMap<String, List<String>>();
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(xml.getBytes());

			final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(arrayInputStream);
			walk(document, new NodeHandler() {
				@Override
				public void handler(Node node) {
					// result.put(getLeafPath(document, node),
					// node.getTextContent());
					putList(result, getLeafPath(document, node), node.getTextContent());
				}
			}, new NodeAttributeHandler() {

				@Override
				public void handler(Node node, NamedNodeMap attributes) {
					Node aNode = null;
					for (int i = 0; i < attributes.getLength(); i++) {
						aNode = attributes.item(i);
						// result.put(getPath(document,
						// node)+"$"+aNode.getNodeName(), aNode.getNodeValue());
						putList(result, getPath(document, node) + "$" + aNode.getNodeName(), aNode.getNodeValue());
					}
				}
			});
			arrayInputStream.close();
			return result;
		} catch (Exception e) {
			throw new HzException(ErrorCodes.XML_PARSE_ERROR,"failed to parse xml");
		}
	}

	public static interface NodeHandler {
		void handler(Node node);
	}

	public static interface NodeAttributeHandler {
		void handler(Node node, NamedNodeMap attributes);
	}

	public static void walk(Node node, NodeHandler handler, NodeAttributeHandler attributeHandler) {
		if (node.hasAttributes()) {
			attributeHandler.handler(node, node.getAttributes());
		}
		if (!node.hasChildNodes()) {
			handler.handler(node);
			return;
		}
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			walk(nodeList.item(i), handler, attributeHandler);
		}
	}

	public static String getLeafPath(Node root, Node node) {
		Node parent = node.getParentNode();
		String result = "";
		while (root != parent) {
			result = parent.getNodeName() + "." + result;
			parent = parent.getParentNode();
		}
		return result.substring(0, result.length() - 1);

	}

	public static String getPath(Node root, Node node) {
		Node parent = node;
		String result = "";
		while (root != parent) {
			result = parent.getNodeName() + "." + result;
			parent = parent.getParentNode();
		}
		return result.substring(0, result.length() - 1);

	}

	public static void main(String[] args) throws Exception {
		String xml = "<root h=\"1\"><a>1<a1>aa<a2 at='12'>no</a2><a2 at='13' bb='45'>no</a2>bb</a1></a><b>2</b></root>";
		System.out.println(JsonUtils.toJsonString(parseXml(xml)));
	}

}
