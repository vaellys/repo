package cn.zthz.tool.picture;

import java.util.ArrayList;
import java.util.List;

import cn.zthz.actor.assemble.GlobalConfig;
import cn.zthz.tool.common.Tuple;

public class IconSizes {
	
	public final static List<Tuple<Integer, Integer>> USER_ICON_SIZES= new ArrayList<>(4);
	public final static List<Tuple<Integer, Integer>> REQUIREMENT_ICON_SIZES= new ArrayList<>(4);
	static {
		String userIconSizesString = GlobalConfig.get("user.icon.sizes");
		String requirementIconSizesString = GlobalConfig.get("requirement.icon.sizes");
		String[] usizes = userIconSizesString.split(",");
		String[] rsizes = requirementIconSizesString.split(",");
		String[] wd = null;
		for (String size : usizes) {
			wd = size.split("x");
			USER_ICON_SIZES.add(new Tuple<>(Integer.valueOf(wd[0].trim()),Integer.valueOf(wd[1].trim())));
		}
		for (String size : rsizes) {
			wd = size.split("x");
			REQUIREMENT_ICON_SIZES.add(new Tuple<>(Integer.valueOf(wd[0].trim()),Integer.valueOf(wd[1].trim())));
		}
	}

}
