package net.wendal.tb.tool;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.wendal.tb.annotation.Cache;
import net.wendal.tb.service.CacheService;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.dao.Cnd;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class MethodInvokeCacheInterceptor implements MethodInterceptor {
	
	private static final Log log = Logs.get();
	
	private CacheService cacheService;
	
	private ConcurrentMap<Method, CacheInfo> cacheInfoMap = new ConcurrentHashMap<Method, CacheInfo>();
	
	private String defaultCacheName;
	
	private boolean enableCache;

	public void filter(InterceptorChain chain) throws Throwable {
		if (!enableCache) {
			chain.doChain();
			return;
		}
		
		CacheInfo ci = cacheInfoMap.get(chain.getCallingMethod());
		if (ci == null) {
			ci = new CacheInfo();
			Method method = chain.getCallingMethod();
			Cache cache = method.getAnnotation(Cache.class);
			if (cache != null)
				ci.cacheName = cache.name();
			else
				ci.cacheName = defaultCacheName;
			if (ci.cacheName != null && !method.getReturnType().equals(Void.class)) { 
				ci.canCache = true;
				for (Class<?> klass : method.getParameterTypes()) {
					if (!klass.isPrimitive() && !Serializable.class.isAssignableFrom(klass)
							&& !Cnd.class.equals(klass)) { //特别处理一下Cnd类
						ci.canCache = false;
						break;
					}
				}
			}
			cacheInfoMap.putIfAbsent(method, ci);
		}
		if (!ci.canCache || ci.cacheName == null)
			chain.doChain();
		else {
			List<Object> key = new ArrayList<Object>();
			key.add(chain.getCallingMethod().toGenericString());
			for (Object arg : chain.getArgs())
				key.add(arg);
			Object obj = cacheService.get(ci.cacheName, key);
			if (obj != null) {
				if (log.isDebugEnabled())
					log.debugf("Load method invoke result from cache --> %s(%d)" , obj.getClass(), obj.hashCode());
				chain.setReturnValue(obj);
				return;
			}
			chain.doChain();
			Object re = chain.getReturn();
			if (re != null && re instanceof Serializable) {
				cacheService.put(ci.cacheName, key, re);
				if (log.isDebugEnabled())
					log.debug("Save result to cache --> " + re);
			} else {
				if (log.isDebugEnabled())
					log.debug("ingore result to cache --> " + re);
			}
		}
	}

	public void setDefaultCacheName(String defaultCacheName) {
		this.defaultCacheName = defaultCacheName;
	}
}

class CacheInfo {
	String cacheName;
	boolean canCache;
}
