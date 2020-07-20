<%@ page language="java" contentType="text/html; charset=utf-8" %>
<%
String _contexPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
%>

<ul>
	<li>浏览器安装与设置</li>
	<li>常见问题</li>
</ul>
<div class="tab-content">
<!--  
<h1>浏览器安装与设置</h1>
-->

<h2>下载浏览器</h2>
<div>
	<p id="browserDownload"></p>
</div>

<h2>下载并安装数字证书客户端（如老客户已经安装过，请跳过此步）</h2>
<div>
	<p>点击页面右下角的“驱动下载”链接，下载并安装数字证书客户端</p>
	<p><a href="template/login/ish02/file/Handbook.zip" target="_blank">点击这里</a>下载详细安装文档。</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/1.png">
	<p>打开压缩包，双击” 证书应用环境安装程序V2.3M4.exe”:</p>
	<img src="<%=_contexPath%>/template/login/ish02/image/help/2.png">
	<p>确认是否可以重启之后，点击“完成”按钮：</p>
	<img src="<%=_contexPath%>/template/login/ish02/image/help/3.png"> --%>
</div>

<h2>设置站点安全级别</h2>
<div>
	<p>将苏州市住房公积金网上业务大厅加入可信站点：</p>
	<p>① 输入地址<a href="https://dw.gjj.suzhou.gov.cn/corpor/" target="_blank">https://dw.gjj.suzhou.gov.cn/corpor/</a>，访问苏州市住房公积金网上业务大厅；</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/4.png"> --%>
	<p>② 打开Internet选项；</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/5.png"> --%>
	<p>③ 选择“安全”页签，选择“收信任的站点”，点击“站点”按钮；</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/6.png"> --%>
	<p>④ 点击“添加“按钮，将地址加入可信站点；</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/7.png"> --%>
	<p>⑤ 添加后，点击“关闭”按钮。</p>
	<p><a href="template/login/ish02/file/Handbook.zip" target="_blank">点击这里</a>下载详细设置文档。</p>
</div>

<h2>设置允许加载控件</h2>
<div>
	<p>① 在Internet选项的“安全”页签中点击“自定义级别…”按钮；</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/8.png"> --%>
	<p>② 在弹出窗口中，将所有ActiveX的选项设置成“启用”；</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/9.png">
	<img src="<%=_contexPath%>/template/login/ish02/image/help/10.png">
	<img src="<%=_contexPath%>/template/login/ish02/image/help/11.png">
	<img src="<%=_contexPath%>/template/login/ish02/image/help/12.png">
	<img src="<%=_contexPath%>/template/login/ish02/image/help/13.png"> --%>
	<p>③ 点击“确定”按钮，并点击Internet选项窗口的“确定”按钮；</p>
	<p>③ 设置之后，就可以访问<a href="https://dw.gjj.suzhou.gov.cn/corpor/" target="_blank">https://dw.gjj.suzhou.gov.cn/corpor/</a>了，选择单位登录->CA证书登录，选择单位名称并输入密码；</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/14.png"> --%>
	<p><a href="template/login/ish02/file/Handbook.zip" target="_blank">点击这里</a>下载详细设置文档。</p>
</div>

</div>

<div class="tab-content">
<!--  
<h1>常见问题</h1>
-->

<h2>我明明是IE11，为什么还是无法登录呢？</h2>
<div>
	<p>① 需再次确认浏览器版本，请查看“2. 怎么查看我的浏览器版本呢？”。</p>
	<p>② 如版本无误，很可能是浏览器设置不准确，请查看“一. 浏览器兼容情况与设置”。</p>
	<p>③ 如果也设置好了，还是不行，可能是IE安装时缺少安装文件，可使用登录页提供的火狐浏览器访问网厅。</p>
	<p><a href="template/login/ish02/file/Handbook.zip" target="_blank">点击这里</a>下载详细设置文档。</p>
</div>

<h2>怎么查看我的浏览器版本呢？</h2>
<div>
	<p>① 首先，IE的图片是长成这样的哦……<img src="<%=_contexPath%>/template/login/ish02/image/help/15.png">
	这种<img src="<%=_contexPath%>/template/login/ish02/image/help/16.png">，这种<img src="<%=_contexPath%>/template/login/ish02/image/help/17.png">，这种<img src="<%=_contexPath%>/template/login/ish02/image/help/18.png">……都不是哦~~</p>
	<p>② 然后，点击浏览器右上角齿轮形状的的“设置”按钮；</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/19.png"> --%>
	<p>③ 选择“关于Internet explorer”选项，弹出窗口中会显示IE的版本；</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/20.png"> --%>
	<p>④ IE打开之后，访问<a href="https://dw.gjj.suzhou.gov.cn/corpor/" target="_blank">公积金网上业务大厅</a>，按F12键：</p>
	<p>⑤ 弹出窗口中右上角，要显示“11”才可以，显示‘edge’等内容的都不对哦。</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/21.png"> --%>
	<p><a href="template/login/ish02/file/Handbook.zip" target="_blank">点击这里</a>下载详细设置文档。</p>
</div>

<h2>怎么设置我的浏览器？</h2>
<div>
	<p>如果浏览器版本真的是IE11，还是无法登陆，那就是对浏览器没有设置好了，请查看“一. 浏览器兼容情况与设置”按文档中步骤操作。</p>
</div>

<h2>我要怎么安装IE11呢？</h2>
<div>
	<p>
		① 先查看我的操作系统版本，在桌面找到“我的电脑”图标<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/22.png"> --%>，右键点击图片，选择“属性”，
		这里可以看到操作系统的版本，win10 64位或者win7 32位或64位。
	</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/23.png"> --%>
	<p>或者win7，32位或64位。</p>
	<p>
		② 访问微软官网<a href="https://support.microsoft.com/zh-cn/help/18520/download-internet-explorer-11-offline-installer" target="_blank">https://support.microsoft.com/zh-cn/help/18520/download-internet-explorer-11-offline-installer</a>打开之后，找到“中文简体”，根据我操作系统的版本点击对应的链接；
	</p>
	<%-- <img src="<%=_contexPath%>/template/login/ish02/image/help/24.png"> --%>
	<p>③ 下载后，双机下载文件进行安装，安装好后需要重启。</p>
	<p>
		④ 重启后打开IE，按照“一. 浏览器兼容情况与设置”进行设置使用即可。
	</p>
	<p><a href="template/login/ish02/file/Handbook.zip" target="_blank">点击这里</a>下载详细设置文档。</p>
</div>

<h2>我是XP系统+火狐浏览器，在登录页还是无法显示单位名称</h2>
<div>
	<p>
		多数是因为使用的证书客户端工具版本问题，可以在本机将原来的客户端工具卸载，重新安装。
	</p>
	<p>登录页右下角，点击“驱动下载”链接，重新下载并安装证书客户端。</p>
</div>

<h2>火狐浏览器要如何设置？</h2>
<div>
	<p>① 初次访问网厅时，会在页面最上方提示用户是否允许加载插件，点击“允许”按钮。</p>
	<p>② 一定在之后的弹出框中，点击“长期允许”按钮。</p>
	<p>如果访问网厅时未弹出“允许”按钮，请将本机的证书客户端工具和火狐卸载，点击页面下方的“驱动下载”和“火狐下载”重新下载并安装。</p>
	<p><a href="template/login/ish02/file/Handbook.zip" target="_blank">点击这里</a>下载详细设置文档。</p>
</div>

</div>
