package cn.ms.mconf;

import java.util.Map;
import java.util.Set;

/**
 * The Mconf Node Governor.
 * 
 * @author lry
 */
public interface Node {

	/**
	 * Query configuration center data structure.
	 * <br>
	 * Map< node, Map< app, Map< env, Map< conf, Map< group, Set< version>>>>>>
	 * <br>
	 * @return
	 */
	Map<String, Map<String, Map<String, Map<String, Map<String, Set<String>>>>>> structures();

}
