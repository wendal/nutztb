var ioc = {
        "$aop" : {
                type : 'org.nutz.ioc.aop.config.impl.JsonAopConfigration',
                fields : {
                        itemList : [
                                ['org.nutz.dao.impl.NutDao','^fetch$','ioc:daoCache']
                        ]
                }
        }
};