<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">  
<html>  
  <head>  
    <title>重置密码</title>  
      
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <script>
    	function check(){
    		var p1 = document.getElementById("p1");
    		var p2 = document.getElementById("p2");
    		var f = document.getElementById("mf");
    		if("" == p1.value){
    			alert("密码不能为空！");
    			return false;
    		}
    		if(p1.value!=p2.value){
    			alert("两次密码输入不一致");
    			return 	;
    		}else{
    			f.submit();
    		}
    	}
    </script>
  
  </head>  
    
  <body>  
    <h3>重置密码</h3>
    <form method="post" action="/user/resetPassword.json" id="mf" onsubmit="check()">
    <table>
    	<tr><td>新密码：</td><td><input type="password" name="password" id="p1"/></td></tr>
    	<tr><td>确认新密码：</td><td><input  type="password" name="repassword" id="p2"/></td><td id="tip"></td></tr>
    	<tr><td></td><td><input type="button" value="提交" onclick="check()"/></td></tr>
    </table>
    <input type="hidden" name="userId" value="${userId}"/>
    <input type="hidden" name="resetToken" value="${resetToken}"/>
    </form>
  </body>  
</html>  