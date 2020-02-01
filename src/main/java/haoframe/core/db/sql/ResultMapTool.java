package haoframe.core.db.sql;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import haoframe.core.db.model.Column;
import haoframe.core.db.model.Table;
import haoframe.core.exception.HaoException;
import haoframe.core.utils.ClassUtils;


/**
 * 结果映射工具
 * @author chianghao
 *
 */
public class ResultMapTool {

	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(ResultSet rs, Class<?> clazz) {
		Table table = Table.getTable(clazz);
		LinkedList<T> list = new LinkedList<T>();
		try {
			while (rs.next()) {
				T bean = (T) clazz.newInstance();
				for(String fieldName:table.getColumnMap().keySet()) {
					Column column = table.getColumnInfo(fieldName);
					Object value  = column.getValue(rs);
					ClassUtils.setFieldValue(bean, fieldName, value);
				}
				list.addLast(bean);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			throw new HaoException("Mapper_Result_Error",e.getMessage());
		}
	}

}
