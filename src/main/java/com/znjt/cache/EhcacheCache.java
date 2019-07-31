package com.znjt.cache;

public class EhcacheCache implements Cache {

	private EhcacheUtils kit() {
		return EhcacheUtils.getInstance(this.getClass().getSimpleName());
	}

	;

	@Override
	public <T> T get(String key) {
		return (T) kit().get(key);
	}

	@Override
	public <T> void set(String key, T value, long timeout) {
		kit().put(key, value, (int) timeout);
	}

	@Override
	public boolean expire(String key, long timeout) {
		return kit().expire(key, (int) timeout);
	}

	@Override
	public boolean exist(String key) {
		return kit().exist(key);
	}

	@Override
	public <T> void setExpireAt(String key, T value, long expireAt) {
		kit().put(key, value, (int) expireAt);
	}

	@Override
	public boolean expireAt(String key, long expireAt) {
		return kit().expireAt(key, (int) expireAt);
	}

	@Override
	public boolean del(String key) {
		return kit().remove(key);
	}

	@Override
	public <T> void ladd(String key, T value) {

	}

	/**
	 * 清除该缓存配置方案的所有内容
	 *
	 * @throws
	 */
	public void delAll() {
		kit().clearCache();
	}
}
