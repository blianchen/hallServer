package top.yxgu.hall.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Pool;

@Service
public class RedisPool {
	public static final int T_READ = 1;
	public static final int T_WRITE = 2;

    @Value("${redis.slave.on}")
    private boolean isSlaveOn;
    
	@Value("${redis.master.host}")
	private String masterHost;
	@Value("${redis.master.port}")
	private int masterPort;
    @Value("${redis.master.pool.max-idle}")  
    private int masterMaxIdle;  
    @Value("${redis.master.pool.max-wait}")  
    private long masterMaxWaitMillis;
    
	@Value("${redis.slave.host}")
	private String slaveHost;
	@Value("${redis.slave.port}")
	private int slavePort;
    @Value("${redis.slave.pool.max-idle}")  
    private int slaveMaxIdle;  
    @Value("${redis.slave.pool.max-wait}")  
    private long slaveMaxWaitMillis;
    
    private Pool<Jedis> slavePool = null;
    private Pool<Jedis> masterPool = null;
    
    public Jedis getResource() {
    	return getResource(T_READ);
    }
    
    public Jedis getResource(int type) {
    	switch (type) {
    		case T_READ: {
        		return isSlaveOn ? jedisSlavePool().getResource() : jedisMasterPool().getResource();
    		}
    		case T_WRITE: {
        		return jedisMasterPool().getResource();
    		}
    		default:
    			return null;
    	}
    }
    
    private  Pool<Jedis> jedisMasterPool() {
    	if (masterPool == null) {
	    	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
	        jedisPoolConfig.setMaxIdle(masterMaxIdle);
	        jedisPoolConfig.setMaxWaitMillis(masterMaxWaitMillis);
	        masterPool = new JedisPool(jedisPoolConfig, masterHost, masterPort);
    	}
    	return masterPool;
//        JedisSentinelPool jedisPool = new JedisSentinelPool(masterName, sentinels, jedisPoolConfig);
    }

    private Pool<Jedis> jedisSlavePool() {
    	if (slavePool == null) {
        	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(slaveMaxIdle);
            jedisPoolConfig.setMaxWaitMillis(slaveMaxWaitMillis);
            slavePool = new JedisPool(jedisPoolConfig, slaveHost, slavePort);
    	}
//        JedisSentinelPool jedisPool = new JedisSentinelPool(masterName, sentinels, jedisPoolConfig);
        return slavePool;
    }
}
