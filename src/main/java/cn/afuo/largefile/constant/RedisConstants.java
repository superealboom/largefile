package cn.afuo.largefile.constant;


public class RedisConstants {

    /**
     * 大文件模块 分布式锁前缀
     */
    public static final String LARGEFILE_LOCK_PREFIX = "largefile:lock:";

    /**
     * 合并大文件锁时间
     */
    public static final long MERGE_LARGEFILE_LOCK_TIME = 10000;




}
