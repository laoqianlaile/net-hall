/************************************************************************************************
 * 华信永道住房公积金平台公共脚本库
 * ydl.xml.js 跨浏览器的XML文档操作库
 * @modified 修改人 修改时间 修改内容描述
 ************************************************************************************************/

if (!ydl) ydl = {};

(function(ydl, $, undefined) {

	ydl.xml = {};

	/**
	 * 从字符串创建xml文档对象
	 * @param {String} xmlString xml字符串
	 * @returns {Object} xml文档对象
	 */
	function createDocumentFromString(xmlString) {
		var xmlDoc = null;
		try {
			//Firefox, Mozilla, Opera, ...
			if (window.DOMParser) {
				xmlDoc = new DOMParser().parseFromString(xmlString, 'text/xml');
			}
			//IE
			else {
				xmlDoc = new ActiveXObject('Microsoft.XMLDOM');
				xmlDoc.async = false;
				xmlDoc.loadXML(xmlString);
				xmlDoc.setProperty('SelectionLanguage', 'XPath');
			}
		}
		catch(e) {
			alert('创建XMLDoc出错：' + e.message);
		}
		return xmlDoc;
	}

	/**
	 * 从url创建xml文档
	 * @param {String} xmlUrl xml源地址
	 * @returns {Object} xml文档对象
	 */
	function createDocumentFromUrl(xmlUrl) {
		var xmlDoc = null;
		ydl.ajax(xmlUrl, null, function(data) {
			xmlDoc = data;
		}, {
			async: false,
			dataType: 'xml'
		});
		if ($.browser.msie) xmlDoc.setProperty('SelectionLanguage', 'XPath');
		return xmlDoc;
	}

	/**
	 * 根据文档内容字符串或url创建xml文档对象
	 * 如果字符串以小于号开头，将作为文档内容处理，否则作为url处理
	 * @param {String} inputString xml字符串或url
	 * @returns {Object} xml文档对象
	 */
	ydl.xml.createDocument = function(inputString) {
		if (inputString.charAt(0) == '<') return createDocumentFromString(inputString);
		else return createDocumentFromUrl(inputString);
	};

	/**
	 * 使用xpath获取单个xml节点
	 * @param {Object} contextNode 上下文节点
	 * @param {String} xpath xpath字符串
	 * @return {Object} xpath匹配的节点，如果匹配多个节点，只返回第一个
	 */
	ydl.xml.findNode = function(contextNode, xpath) {
		if (!contextNode) return null;
		if (!xpath) xpath = '//';
		if ($.browser.msie) {
			return contextNode.selectSingleNode(xpath);
		}
		else {
			var xPath = new XPathEvaluator();
			var nodes = xPath.evaluate(xpath, contextNode, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
			if (nodes) return nodes.iterateNext();
			else return null;
		}
	};

	/**
	 * 使用xpath获取xml节点列表
	 * @param {Object} contextNode 上下文节点
	 * @param {String} xpath xpath字符串
	 * @return {Array} xpath匹配的节点列表
	 */
	ydl.xml.findNodes = function(contextNode, xpath) {
		if (!contextNode) return null;
		if (!xpath) xpath = '//';
		var nodes = [];
		if ($.browser.msie) {
			var nodeList = contextNode.selectNodes(xpath);
			if (nodeList.length > 0) nodes = nodeList;
		}
		else {
			var xPath = new XPathEvaluator();
			var nodeList = xPath.evaluate(xpath, contextNode, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
			if (nodeList) {
				var node;
				while (node = nodeList.iterateNext()) nodes.push(node);
			}
		}
		return nodes;
	};

	/**
	 * 读写xml属性
	 */
	ydl.xml.attr = function() {
		//TODO...
	};

	/**
	 * 序列化xml节点
	 * @param {Object} xmlNode xml节点对象
	 * @returns {String} 节点的xml字符串
	 */
	ydl.xml.serialize = function(xmlNode) {
		try {	//Firefox, Chrome, Opera, ...
			return new XMLSerializer().serializeToString(xmlNode);
		}
		catch (e) {
			try {	//IE
				return xmlNode.xml;
			}
			catch (e) {
				alert('获取xml文本出错' + e.message);
				return '';
			}
		}
	};

	/**
	 * 设置xml元素节点的文本
	 * @param {Object} xmlDoc xml文档对象
	 * @param {Object} xmlNode 要操作的xml节点对象
	 * @param {String} text 要设置的节点文本
	 * @returns undefined
	 */
	ydl.xml.setText = function(xmlDoc, xmlNode, text) {
		if (xmlNode.text) xmlNode.text = text;
		else if (xmlNode.childNodes.length > 0) {
			xmlNode.childNodes[0].nodeValue = text;
		}
		else xmlNode.appendChild(xmlDoc.createTextNode(text));
	};

	/**
	 * 获取xml节点文本（包含所有子节点）
	 * @param {Object} xml节点对象
	 * @returns {String} 节点文本
	 */
	ydl.xml.getText = function(xmlNode) {
		var text = '';
		for (var i = 0; i < xmlNode.childNodes.length; i++) {
			if (xmlNode.childNodes[i].hasChildNodes()) text += arguments.calee(xmlNode.childNodes[i]);
			else text += xmlNode.childNodes[i].nodeValue;
		}
		return text;
	};

	/**
	 * 根据需要添加CDATA，在构造xml字符串时使用
	 * @param {String} data 字符串
	 * @returns {String} 如果字符串中包含>、<、&符号，在字符串两端添加cdata标签
	 */
	ydl.xml.cdata = function(data) {
		if (data.indexOf('>') >= 0 || data.indexOf('<') >= 0 || data.indexOf('&') >= 0) return '<![CDATA[' + data + ']]>';
		else return data;
	};


})(ydl, jQuery);