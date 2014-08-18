#!/usr/bin/env expect 

set cwd  /home/uzoice/workspace/ztwork/Mas
set hosts(uzoice@hz1) mylinux2012
#set hosts(uzoice@java3) mylinux2012

set hostKeys [array names hosts]
foreach host $hostKeys {
	set password $hosts($host)
 	set cmd "$cwd/upload.sh $host"
	spawn sh upload.sh $host
	expect "*(yes/no)?" {
			send "yes\n"
			expect "password:" {send "$password\n"}
		} "password:" { send "$password\n" 
		expect eof {puts "over" }
	 } eof {
		puts "eof"
	}
}


