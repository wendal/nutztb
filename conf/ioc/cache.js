var ioc = {
	cacheManager : {
		type : "net.sf.ehcache.CacheManager",
		events : {
			depose : "shutdown"
		}
	},
	daoCache : {
		type : "net.wendal.tb.tool.MethodInvokeCacheInterceptor",
		fields : {
			defaultCacheName : 'daoCache',
			cacheService : {refer : "cacheService"},
			enableCache : {java : "$conf.get('use_cache_dao')"}
		}
	}
};
        