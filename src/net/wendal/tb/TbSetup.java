package net.wendal.tb;

import java.util.concurrent.atomic.AtomicLong;

import net.wendal.tb.bean.BaseBean;
import net.wendal.tb.bean.SystemInfo;
import net.wendal.tb.tool.Tbs;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.random.R;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.resource.Scans;

public class TbSetup implements Setup {

	public void init(NutConfig config) {
		Dao dao = config.getIoc().get(Dao.class);
		for (Class<?> klass : Scans.me().scanPackage("net.wendal.tb.bean"))
			if (klass.getAnnotation(Table.class) != null) {
				dao.create(klass, false);
				Sql sql = Sqls.fetchLong("select MAX(ID) from " + dao.getEntity(klass).getTableName());
				dao.execute(sql);
				Long maxID = sql.getObject(Long.class);
				if (maxID == null)
					BaseBean.ids.put(klass, new AtomicLong());
				else
					BaseBean.ids.put(klass, new AtomicLong(maxID + 1));
			}
		SystemInfo info = dao.fetch(SystemInfo.class, "md5_key");
		if (info == null) {
			info = new SystemInfo();
			info.setName("md5_key");
			info.setData(R.UU64());
			dao.insert(info);
		}
		Tbs.md5_key = info.getData();
	}

	public void destroy(NutConfig config) {
	}

}
