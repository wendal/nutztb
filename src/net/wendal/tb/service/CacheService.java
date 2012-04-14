package net.wendal.tb.service;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class CacheService {

	@Inject
	private CacheManager cacheManager;
	
	public void put(String cacheName, Object key, Object val) {
		Element element = new Element(key, val);
		cacheManager.getCache(cacheName).put(element);
	}
	
	public Object get(String cacheName, Object key) {
		Element element = cacheManager.getCache(cacheName).get(key);
		if (element != null)
			return element.getValue();
		return null;
	}
	
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}
}
