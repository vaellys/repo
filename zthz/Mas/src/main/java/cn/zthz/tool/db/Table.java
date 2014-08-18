package cn.zthz.tool.db;

@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
public @interface Table {
	String name() default "";
}
