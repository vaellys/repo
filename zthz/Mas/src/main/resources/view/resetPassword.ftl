<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>密码重置</title>
<style>
body{ background-color:#ededed; font-family:"微软雅黑";}
.logo{ width:310px; margin:auto; margin-top:50px;}
.password{ width:310px; margin:auto; margin-top:20px; border:1px solid #4ac5dc; background-color:#ffffff; box-shadow: 0px 2px 5px  #ccc;}
.tit{ line-height:35px;text-align:center; font-size:20px; color:#09C; font-weight:bold; border-bottom:1px dotted #0CF; padding:10px;}
.table{ font-size:14px; line-height:50px;  padding:1px; border-bottom:1px solid #ededed; color:#666666; height:50px;}
input{ line-height:20px; color:#333333; font-size:14px; width:200px; margin:0px; padding:0px; font-family:"微软雅黑";}
.sure{ height:40px; }
.btm{text-align:center; line-height:35px; background:#09C; color:#ffffff; font-weight:bold; height:35px; width:100%;  border:1px solid #fff; font-size:16px;}
.footer{ font-size:8px; text-align:right; color:#666666; width:310px; margin:auto; line-height:30px; text-shadow:0px 1px 0px #ffffff;-webkit-text-shadow:0px 1px 1px #ffffff ; margin-top:10px;}
</style>
    <script>
    //阻止浏览器的默认行为 
	function stopDefault( e ) { 
		if ( e && e.preventDefault ) 
		e.preventDefault(); 
		else 
		window.event.returnValue = false; 
		return false; 
	} 
    	function check(obj,e){
    		var p1 = document.getElementById("p1");
    		var p2 = document.getElementById("p2");
    		var f = document.getElementById("mf");
    		if("" == p1.value){
    			alert("密码不能为空！");
    			stopDefault(e);
    			return false;
    		}
    		if(p1.value!=p2.value){
    			alert("两次密码输入不一致");
    			stopDefault(e);
    			return false;
    		}else{
    			f.submit();
    		}
    	}
    </script>
</head>

<body>
<form id="mf" method="post" action="/user/resetPassword.json" onsubmit="check()">
<div class="logo"><img src="/logo_password.png" /></div>
<!----------密码修改-->
<div class="password">
   <div class="tit">密码重置</div>
   <div style="padding:0 5px; margin:5px 0px;">
   <div class="table">新密码：&nbsp;&nbsp;&nbsp;&nbsp;<input type="password" name="password" id="p1"></div>
    <div  class="table">确认密码：<input type="password" name="repassword" id="p2" /></div>
    <div class="sure"><input type="submit" class="btm" value="确 认"  /></div>
    <input type="hidden" name="userId" value="${userId}"/>
    <input type="hidden" name="resetToken" value="${resetToken}"/>
  </div>

</div>
</form>

<!----------footer-->
<div class="footer">北京中天华智科技有限公司</div>


</body>
</html>
