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
	 * Query node list.
	 * 
	 * @return
	 */
	Set<String> nodes();

	/**
	 * Query application list.
	 * 
	 * @param node
	 *            Support the * query.
	 * @return
	 */
	Set<String> apps(String node);

	/**
	 * Query configuration list.
	 * 
	 * @param node
	 *            Support the * query.
	 * @param app
	 *            Support the * query.
	 * @return
	 */
	Set<String> confs(String node, String app);

	/**
	 * Query configuration center data structure.
	 * 
	 * @return
	 */
	Map<String, Map<String, Set<String>>> structures();

}
