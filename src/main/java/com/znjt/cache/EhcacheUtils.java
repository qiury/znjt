package com.znjt.cache;

import com.znjt.exs.ExceptionInfoUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Created by qiuzx on 2019-03-19
 * Company BTT
 * Depart Tech
 *
 * @author qiuzx
 */
public class EhcacheUtils {
    private static Logger logger = LoggerFactory.getLogger(EhcacheUtils.class);
    private static CacheManager manager = null;
    private Cache cache = null;
    private static String configfile = "ehcache.xml";

    //private Timer timer;
    private interface Helper {
        Map<String, EhcacheUtils> INSTANCES = new HashMap<>();
    }

    private EhcacheUtils() {

    }

    //EHCache初始化
    private static void init() {
        try {
            if (manager == null) {
                ClassLoader clas = EhcacheUtils.class.getClassLoader();
                manager = CacheManager.create(clas.getResourceAsStream(configfile));
            }
        } catch (CacheException e) {
            logger.error(ExceptionInfoUtils.getExceptionCauseInfo(e));
        }
    }

    /**
     * 获取实例
     *
     * @return
     * @category 获取实例
     */
    public static EhcacheUtils getInstance(String cachename) {
        init();
        EhcacheUtils instance = Helper.INSTANCES.get(cachename);
        if (instance != null) {
            return instance;
        }
        instance = new EhcacheUtils();
        instance.cache = manager.getCache(cachename);
//        instance.timer = new Timer();
//        EhcacheUtils _instance = instance;
//        //默认时不会自动触发监听过期失效  只有访问才能触发   ，这里是为了让程序1分钟监听一次过期失效
//        instance.timer.schedule(new TimerTask() {//1分钟监听一次过期失效
//            @Override
//            public void run() {
//                _instance.cache.evictExpiredElements();
//            }
//        }, 500,1000);
        return instance;
    }


    /**
     * 将数据存入Cache
     *
     * @param seconds ttl
     * @param key     类似redis的Key
     * @param value   类似redis的value，value可以是任何对象、数据类型，比如person,map,list等
     */
    public void put(String key, Object value, int seconds) {
        Element ele = new Element(key, value);
        if (seconds != 0) {//失效时的间隔时间 和最后一次操作时间间隔
            ele.setTimeToLive(seconds);
            ele.setTimeToIdle(seconds);
        }
        cache.put(ele);
    }

    /**
     * 获取缓存cachename中key对应的value
     *
     * @param key
     * @return
     */
    public Element get(String key) {
        try {
            Element e = cache.get(key);
            if (e == null) {
                return null;
            }
            return e;
        } catch (IllegalStateException e) {
            logger.error(ExceptionInfoUtils.getExceptionCauseInfo(e));
        } catch (CacheException e) {
            logger.error(ExceptionInfoUtils.getExceptionCauseInfo(e));
        }
        return null;
    }

    /**
     * 清除缓存名称为cachename的缓存
     */
    public void clearCache() {
        try {
            if (cache != null) {
                cache.removeAll();
            }
        } catch (IllegalStateException e) {
            logger.error(ExceptionInfoUtils.getExceptionCauseInfo(e));
        }
    }

    /**
     * 判断缓存是否存在
     *
     * @throws
     */
    public boolean exist(String key) {
        if (get(key) != null) {
            return true;
        }
        return false;
    }

    /**
     * 移除缓存cachename中key对应的value
     *
     * @param key
     * @return
     */
    public boolean remove(String key) {
        boolean flag = false;
        if (cache != null) {
            flag = cache.remove(key);
            cache.flush();
        }
        return flag;
    }

    /**
     * 使缓存在某个时间过期
     *
     * @throws
     */
    public boolean expire(String key, int timeout) {
        boolean boo = false;
        Element el = get(key);
        if (el != null) {
            Object value = el.getObjectValue();
            put(key, value, timeout);
            boo = true;
        }
        return boo;
    }

    /**
     * 使缓存在某个时间过期
     *
     * @throws
     */
    public boolean expireAt(String key, int timeout) {
        return expire(key, timeout);
    }

    public void evictExpiredElements() {
        if (cache != null) {
            cache.evictExpiredElements();
        }
    }
}
