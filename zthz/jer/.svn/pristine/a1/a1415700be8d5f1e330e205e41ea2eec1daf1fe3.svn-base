package qileke.jer;

import java.io.IOException;
import java.sql.Timestamp;

import org.junit.Test;

import qileke.jer.utils.ClassUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JettyTest {
	
	@Test
	public void testHello(){
	}
	
	@Test
	public void testPackage() throws IOException{
		
	}
	
	@Test
	public void testJson(){
		Person person = new Person("jim" , new Timestamp(System.currentTimeMillis()));
		Group group = new Group(1, 3, "tom");
		group.getPersons().add(person);
		
		ParserConfig config = new ParserConfig();
		String jPerson = JSON.toJSONString(person);
		String jgroup = JSON.toJSONString(group );
		System.out.println(jPerson);
		System.out.println(jgroup);
		Group g = JSON.parseObject(jgroup, Group.class);
		System.out.println(g.getPersons().get(0).getName());
		
	}
	
	

}
