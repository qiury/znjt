package com.znjt.datasource.enhance;

/**
 * Created by qiuzx on 2019-03-12
 * Company BTT
 * Depart Tech
 *
 * usage:
 *   1. def Mapper sub class
 *   2. get sqlsession by datasource
 *
 * public interface ChannelDao extends Mapper {
 *
 *     Long checkRelationship(@Param("fromId") String fromId,
 *                            @Param("userId") Long userId);
 * }
 *
 * ChannelDao channelDao = MapperFactory.createMapper(ChannelDao.class, "datasourcename");
 * channelID = channelDao.checkRelationship("XXXXX", 123456);
 */
public interface Mapper {

}
