package com.znjt.thrift;

import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportFactory;

/**
 * Created by qiuzx on 2019-03-27
 * Company BTT
 * Depart Tech
 */
public class ExplainFactory extends TTransportFactory {
    private int maxLength_;

    public ExplainFactory() {
        maxLength_ = Integer.MAX_VALUE;
    }

    public ExplainFactory(int maxLength) {
        maxLength_ = maxLength;
    }

    @Override
    public TTransport getTransport(TTransport base) {
        return new TFramedTransport(base, maxLength_);
    }
}
