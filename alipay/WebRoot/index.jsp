<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<!-- saved from url=(0066)https://getregisterednow.com/FTFChina/Register/Login.asp -->
<!DOCTYPE html PUBLIC "" ""><HTML><HEAD><META content="IE=11.0000" 
http-equiv="X-UA-Compatible">
		 
<META http-equiv="Content-Type" content="text/html; charset=utf-8">
		 <TITLE>Freescale Technology Forum 2014</TITLE>		 <LINK href="Freescale%20Technology%20Forum%20Beijing%202014_files/toplevel.css" 
rel="stylesheet" type="text/css">		 <LINK href="Freescale%20Technology%20Forum%20Beijing%202014_files/StyleSheet.css" 
rel="stylesheet" type="text/css">				 

 
<META name="GENERATOR" content="MSHTML 11.00.9600.17041"></HEAD> 
<BODY>
<script type="text/javascript">
function submitForm(){
	if(document.getElementById('company').checked){
		if(document.alipayment.invoiceTitle.value == ''){
			alert('请填写发票抬头！');
			document.alipayment.invoiceTitle.focus();
			return false;
		}
		var surveyForm = document.getElementById('alipayment');
		surveyForm.submit();
	}else{
		var surveyForm = document.getElementById('alipayment');
		surveyForm.submit();
	}
}
</script>
<DIV class="noscriptmsg">	Please enable JavaScript and reload the page to 
proceed. </DIV>
<DIV class="wrapper"><!-- ~~~~~~~~~~~~~~~~~~~~~~ Heading ~~~~~~~~~~~~~~~~~~~~~~~~~~~ --> <!-- menu --> 
<DIV class="navContainer">
<DIV class="navTopRow">
<DIV class="navLogo"><A href="http://www.freescale.com/" target="_blank"><IMG 
src="Freescale%20Technology%20Forum%20Beijing%202014_files/freescale_logo.gif"></A></DIV>

<DIV class="clear"></DIV></DIV>
<DIV class="navBottomRow">

<DIV class="clear"></DIV></DIV><!-- graphic -->	 
<DIV class="navGraphic" id="div_freescalegraphic" style="background: rgb(252, 252, 252);"><A 
title="Freescale" href="http://www.freescale.com/FTFChina" target="_blank"><IMG 
alt="Freescale" src="Freescale%20Technology%20Forum%20Beijing%202014_files/HeaderChi.jpg"></A>
	 </DIV></DIV><!-- End Freescale Global Header --> 
<DIV class="titlediv">2014年飞思卡尔技术论坛（中国）在线购票</DIV>
<DIV class="contentdiv">
<DIV><A href="http://payment.ccidnet.com/index_english.jsp?confirmationnumber=<%=request.getParameter("confirmationnumber")==null?"":request.getParameter("confirmationnumber")%>&firstname=<%=request.getParameter("firstname")==null?"":request.getParameter("firstname")%>&lastname=<%=request.getParameter("lastname")==null?"":request.getParameter("lastname")%>">English</A>&nbsp;&nbsp;<A 
href="http://payment.ccidnet.com/index.jsp?confirmationnumber=<%=request.getParameter("confirmationnumber")==null?"":request.getParameter("confirmationnumber")%>&firstname=<%=request.getParameter("firstname")==null?"":request.getParameter("firstname")%>&lastname=<%=request.getParameter("lastname")==null?"":request.getParameter("lastname")%>">简体字</A></DIV>
<DIV>感谢您注册飞思卡尔技术论坛2014！
<P><B>2014年5月20日至21日<BR>2014年飞思卡尔技术论坛 
<BR></B>深圳华侨城洲际大酒店<BR>中国深圳市华侨城深南大道9009号<BR>邮编：518053 <BR></P></DIV></DIV><!-- ~~~~~~~~~~~~~~~~~~~~~~ Welcome Text ~~~~~~~~~~~~~~~~~~~~~~ --> <!-- ~~~~~~~~~~~~~~~~~~~~~~ New Registration ~~~~~~~~~~~~~~~~~~ --> 
<DIV class="headerdiv" style=" margin-top:10px">现在支付</DIV>
<DIV class="contentdiv">

<form name="alipayment" id="alipayment" action="alipayServlet" method="post" target="_blank">
<input type="hidden" name="firstname" value="<%=request.getParameter("firstname")==null?"":request.getParameter("firstname")%>"/>        	
<input type="hidden" name="lastname" value="<%=request.getParameter("lastname")==null?"":request.getParameter("lastname")%>"/>
<input type="hidden" name="confirmationNumber" value="<%=request.getParameter("confirmationnumber")==null?"":request.getParameter("confirmationnumber")%>"/>
<input size="30" name="WIDtotal_fee" value="0.01" type="hidden" />
<DIV>发票抬头 <INPUT name="alumni" id="gr" type="radio" value="" checked="checked" > 个人 <INPUT id="company" name="alumni" type="radio" 
value=""> 单位 <input type="text" name="invoiceTitle" id="invoiceTitle" maxlength="50">		 
</DIV>
<DIV>
<span style="color: rgb(210, 14, 0);">发票配送：发票在大会报到处统一领取。</span><br/>
<span>如您没有支付宝账户，请在下一步的支付页面中点击“有卡就能付”，直接通过银行卡或信用卡来进行支付。</span>
</DIV>
<DIV><!-- Register Now -->			
<DIV style=" text-align:left; font-size:14px; padding-left:10px; margin-top:10px; margin-bottom:10px; background:#eaeaea; color:#5b5b5b">支付金额：<span class="font1">200</span>元</DIV>
<DIV style=" text-align:left"><INPUT name="PageAction" type="button" class="input1" value="确认并提交表单" onclick="submitForm()"></DIV>
</form>
 </DIV></DIV>
 <!-- ~~~~~~~~~~~~~~~~~~~~~~  New Registration ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  --> <!-- ~~~~~~~~~~~~~~~~~~~~~~  Update Your Registration ~~~~~~~~~~~~~~~~~~~~~~  --><!-- ~~~~~~~~~~~~~~~~~~~~~~ Update Your Registration ~~~~~~~~~~~~~~~~~~~~~~  --> <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Contact Us ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --> 
<DIV class="headerdiv"  style=" margin-top:10px">联络我们</DIV>
<DIV class="contentdiv">
<DIV>如果您在付款过程中有疑问或者付款不成功，请联系：<BR><B>电子邮件</B>: <A href="mailto:jinyan@staff.ccidnet.com"> jinyan@staff.ccidnet.com</A><BR><B>联系电话</B>: 
8610 8855 8948 或 8610 8855 8926</DIV></DIV>
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Contact Us ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --><!-- Begin Freescale Global Footer --><!-- End Freescale Global Footer --> </DIV><!--
<a href="login.asp?s=0">&nbsp;Style&nbsp; Gray/Tan</a><br>
<a href="login.asp?s=6">&nbsp;Style&nbsp; Gray Black/Tan</a><br>
<a href="login.asp?s=7">&nbsp;Style&nbsp; Lt Blue</a><br>

urlReferral=

--> 

</BODY></HTML>
