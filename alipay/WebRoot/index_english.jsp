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
			alert('Please fill out the invoice looked up！');
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
<DIV class="titlediv">Freescale Technology Forum China 2014 Online Payment</DIV>
<DIV class="contentdiv">
<DIV><A href="http://payment.ccidnet.com/index_english.jsp?confirmationnumber=<%=request.getParameter("confirmationnumber")==null?"":request.getParameter("confirmationnumber")%>&firstname=<%=request.getParameter("firstname")==null?"":request.getParameter("firstname")%>&lastname=<%=request.getParameter("lastname")==null?"":request.getParameter("lastname")%>">English</A>&nbsp;&nbsp;<A 
href="http://payment.ccidnet.com/index.jsp?confirmationnumber=<%=request.getParameter("confirmationnumber")==null?"":request.getParameter("confirmationnumber")%>&firstname=<%=request.getParameter("firstname")==null?"":request.getParameter("firstname")%>&lastname=<%=request.getParameter("lastname")==null?"":request.getParameter("lastname")%>">简体字</A></DIV>
<DIV>Thank you for registering for FTF China 2014.
<P><B>May 20-21, 2014<BR>Freescale Technology Forum China 2014 
<BR></B>InterContinental Shenzhen Hotel, Shenzhen<BR>Shennan Road 9009, Overseas Chinese Town, Shenzhen 518053, China</P></DIV></DIV><!-- ~~~~~~~~~~~~~~~~~~~~~~ Welcome Text ~~~~~~~~~~~~~~~~~~~~~~ --> <!-- ~~~~~~~~~~~~~~~~~~~~~~ New Registration ~~~~~~~~~~~~~~~~~~ --> 
<DIV class="headerdiv" style=" margin-top:10px">Payment Now</DIV>
<DIV class="contentdiv">

<form name="alipayment" id="alipayment" action="alipayServlet" method="post" target="_blank">
<input type="hidden" name="firstname" value="<%=request.getParameter("firstname")==null?"":request.getParameter("firstname")%>"/>        	
<input type="hidden" name="lastname" value="<%=request.getParameter("lastname")==null?"":request.getParameter("lastname")%>"/>
<input type="hidden" name="confirmationNumber" value="<%=request.getParameter("confirmationnumber")==null?"":request.getParameter("confirmationnumber")%>"/>
<input size="30" name="WIDtotal_fee" value="200" type="hidden" />
<DIV>Invoice’s Title<INPUT name="alumni" id="gr" type="radio" value="" checked="checked" > Personal <INPUT id="company" name="alumni" type="radio" 
value=""> Business <input type="text" name="invoiceTitle" id="invoiceTitle" maxlength="50">		 
</DIV>
<DIV>
<span style="color: rgb(210, 14, 0);">Please collect your invoice (fapiao) at the FTF China registration counters.
</span><br/>
<span>If you don't have "支付宝账户", please click "有卡就能付" at next payment page, you can pay the fare through bank card or credit card.</span>
</DIV>
<DIV><!-- Register Now -->			
<DIV style=" text-align:left; font-size:14px; padding-left:10px; margin-top:10px; margin-bottom:10px; background:#eaeaea; color:#5b5b5b">Payment amount:<span class="font1">RMB 200</span></DIV>
<DIV style=" text-align:left"><INPUT name="PageAction" type="button" class="input1" value="Confirm and Submit" onclick="submitForm()"></DIV>
</form>
 </DIV></DIV>
 <!-- ~~~~~~~~~~~~~~~~~~~~~~  New Registration ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  --> <!-- ~~~~~~~~~~~~~~~~~~~~~~  Update Your Registration ~~~~~~~~~~~~~~~~~~~~~~  --><!-- ~~~~~~~~~~~~~~~~~~~~~~ Update Your Registration ~~~~~~~~~~~~~~~~~~~~~~  --> <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Contact Us ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --> 
<DIV class="headerdiv"  style=" margin-top:10px">Contact us</DIV>
<DIV class="contentdiv">
<DIV>If you have any questions in the payment process, or payment is not successful, please contact:<BR><B>Email</B>: <A href="mailto:jinyan@staff.ccidnet.com"> jinyan@staff.ccidnet.com</A><BR><B>Phone</B>: 
8610 8855 8948 或 8610 8855 8926</DIV></DIV>
<!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Contact Us ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ --><!-- Begin Freescale Global Footer --><!-- End Freescale Global Footer --> </DIV><!--
<a href="login.asp?s=0">&nbsp;Style&nbsp; Gray/Tan</a><br>
<a href="login.asp?s=6">&nbsp;Style&nbsp; Gray Black/Tan</a><br>
<a href="login.asp?s=7">&nbsp;Style&nbsp; Lt Blue</a><br>

urlReferral=

--> 

</BODY></HTML>
