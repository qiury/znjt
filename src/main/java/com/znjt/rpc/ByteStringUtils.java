package com.znjt.rpc;

import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by qiuzx on 2019-03-15
 * Company BTT
 * Depart Tech
 */
public class ByteStringUtils {
    private ByteStringUtils(){}

    /**
     * Java字节数组转换为ByteString
     * @param datas
     * @return
     */
    public static ByteString changeBytes2ByteString(byte[] datas){
        if(datas==null){
            return null;
        }
        return ByteString.copyFrom(datas);
    }

    public static List<ByteString> changeBytesIter2ByteString(List<byte[]> datas){
        if(datas==null){
            return null;
        }
        List<ByteString> bss = new ArrayList<>();
        datas.forEach(item->{
            Optional.ofNullable(item).ifPresent(x->{
                ByteString bs = changeBytes2ByteString(item);
                bss.add(bs);
            });
        });
        return bss;
    }
}
